package org.dizzymii.millenaire2.entity.ai;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * Manages goal selection, execution, day-phase computation, and stuck detection for a Millénaire villager.
 * Extracted from MillVillager to keep the entity class focused on entity definition, data, and NBT.
 */
public class VillagerAIController {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int GOAL_TICK_INTERVAL = 20;
    private static final int STUCK_THRESHOLD_TICKS = 200;
    private static final double STUCK_DISTANCE_SQ = 0.25;

    private final MillVillager villager;

    @Nullable private Goal currentGoal = null;
    @Nullable private GoalInformation goalInformation = null;
    @Nullable private Point pathDestPoint = null;

    private final HashMap<Goal, Long> lastGoalTime = new HashMap<>();

    private long goalStarted = 0L;
    private int goalTickCounter = 0;
    private int stuckCounter = 0;
    private double lastTickX = 0;
    private double lastTickZ = 0;

    public VillagerAIController(MillVillager villager) {
        this.villager = villager;
    }

    // --- Accessors ---
    @Nullable public Goal getCurrentGoal() { return currentGoal; }
    @Nullable public GoalInformation getGoalInformation() { return goalInformation; }
    @Nullable public Point getPathDestPoint() { return pathDestPoint; }
    public void setPathDestPoint(@Nullable Point p) { this.pathDestPoint = p; }
    public long getGoalStarted() { return goalStarted; }

    // --- Main tick entry point called from MillVillager.serverTick() ---

    public void tick() {
        goalTickCounter++;
        if (goalTickCounter >= GOAL_TICK_INTERVAL) {
            goalTickCounter = 0;
            villager.setCurrentPhase(computeDayPhase());
            tickStuckDetection();
            tickGoalSelection();
        }
        tickGoalExecution();
    }

    // --- Day phase ---

    private MillVillager.DayPhase computeDayPhase() {
        long dayTime = villager.level().getDayTime() % 24000;
        if (dayTime >= 18000 || dayTime < 6000) return MillVillager.DayPhase.NIGHT;
        if (dayTime < 8000) return MillVillager.DayPhase.MORNING;
        if (dayTime < 12000) return MillVillager.DayPhase.WORK;
        if (dayTime < 16000) return MillVillager.DayPhase.AFTERNOON;
        return MillVillager.DayPhase.EVENING;
    }

    // --- Stuck detection ---

    private void tickStuckDetection() {
        double dx = villager.getX() - lastTickX;
        double dz = villager.getZ() - lastTickZ;
        double movedSq = dx * dx + dz * dz;
        lastTickX = villager.getX();
        lastTickZ = villager.getZ();

        if (currentGoal != null && movedSq < STUCK_DISTANCE_SQ) {
            stuckCounter++;
            if (stuckCounter >= STUCK_THRESHOLD_TICKS / GOAL_TICK_INTERVAL) {
                LOGGER.debug("Villager stuck (" + stuckCounter + " checks), clearing goal: " + villager.getGoalKey());
                clearGoal();
                stuckCounter = 0;
                villager.teleportTo(
                        villager.getX() + (villager.getRandom().nextDouble() - 0.5) * 3,
                        villager.getY(),
                        villager.getZ() + (villager.getRandom().nextDouble() - 0.5) * 3);
            }
        } else {
            stuckCounter = 0;
        }
    }

    // --- Goal selection ---

    private void tickGoalSelection() {
        if (!Goal.isInitialized()) {
            idleWanderFallback();
            return;
        }

        if (villager.getVillagerType() == null && villager.getVtypeKey() != null) {
            villager.resolveVillagerType();
        }

        if (currentGoal != null && villager.getGoalKey() != null) {
            try {
                if (!currentGoal.isStillValid(villager)) {
                    clearGoal();
                }
            } catch (Exception e) {
                LOGGER.error("Error checking goal validity: " + villager.getGoalKey(), e);
                clearGoal();
            }
        }

        if (currentGoal == null) {
            selectNewGoal();
        }
    }

    private void selectNewGoal() {
        switch (villager.getCurrentPhase()) {
            case NIGHT -> selectNightGoal();
            case MORNING -> selectMorningGoal();
            case WORK, AFTERNOON -> selectWorkGoal();
            case EVENING -> selectEveningGoal();
        }
    }

    private void selectNightGoal() {
        if (tryGoal("sleep", Goal.get("sleep"))) return;
        if (tryGoal("hide", Goal.get("hide"))) return;
        navigateTowardsHome();
    }

    private void selectMorningGoal() {
        if (tryVtypeGoals(false)) return;
        navigateTowardsHome();
    }

    private void selectWorkGoal() {
        if (tryVtypeGoals(false)) return;
        if (tryGoal("construction", Goal.get("construction"))) return;
        if (tryGoal("gosocialise", Goal.get("gosocialise"))) return;
        idleWander();
    }

    private void selectEveningGoal() {
        if (tryGoal("gosocialise", Goal.get("gosocialise"))) return;
        navigateTowardsHome();
    }

    public boolean tryGoal(String key, Goal goal) {
        if (goal == null) return false;
        try {
            GoalInformation info = goal.getDestination(villager);
            if (info != null && info.hasTarget()) {
                setActiveGoal(key, goal, info);
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("Error in tryGoal(" + key + ")", e);
        }
        return false;
    }

    private boolean tryVtypeGoals(boolean nightOnly) {
        if (villager.getVillagerType() == null || villager.getVillagerType().goals.isEmpty()) return false;
        for (String gKey : villager.getVillagerType().goals) {
            Goal g = Goal.get(gKey);
            if (g == null) continue;
            if (nightOnly && !g.canBeDoneAtNight()) continue;
            if (!nightOnly && !g.canBeDoneInDayTime()) continue;

            if (g.minimumHour >= 0 || g.maximumHour >= 0) {
                long dayTime = villager.level().getDayTime() % 24000;
                int hour = (int) (dayTime / 1000 + 6) % 24;
                if (g.minimumHour >= 0 && hour < g.minimumHour) continue;
                if (g.maximumHour >= 0 && hour > g.maximumHour) continue;
            }

            Long lastTime = lastGoalTime.get(g);
            if (lastTime != null && (villager.level().getGameTime() - lastTime) < Goal.STANDARD_DELAY / 50) continue;

            if (tryGoal(gKey, g)) return true;
        }
        return false;
    }

    // --- Navigation helpers ---

    private void navigateTowardsHome() {
        Point target = villager.getHousePoint() != null ? villager.getHousePoint() : villager.getTownHallPoint();
        if (target != null) {
            villager.getNavigation().moveTo(target.x + 0.5, target.y, target.z + 0.5, 0.5);
        } else {
            idleWander();
        }
    }

    private void idleWander() {
        Point around = villager.getHousePoint() != null ? villager.getHousePoint()
                : (villager.getTownHallPoint() != null ? villager.getTownHallPoint() : new Point(villager.blockPosition()));
        int dx = villager.getRandom().nextInt(11) - 5;
        int dz = villager.getRandom().nextInt(11) - 5;
        villager.getNavigation().moveTo(around.x + dx + 0.5, around.y, around.z + dz + 0.5, 0.4);
    }

    private void idleWanderFallback() {
        idleWander();
    }

    // --- Goal state management ---

    public void setActiveGoal(String key, Goal goal, GoalInformation info) {
        villager.setGoalKey(key);
        this.currentGoal = goal;
        this.goalInformation = info;
        this.goalStarted = villager.level().getGameTime();
        villager.setActionStart(villager.level().getGameTime());
        villager.setGoalKeySync(key);

        if (info.targetPoint != null) {
            this.pathDestPoint = info.targetPoint;
            villager.getNavigation().moveTo(
                    info.targetPoint.x + 0.5,
                    info.targetPoint.y,
                    info.targetPoint.z + 0.5,
                    goal.sprint ? 1.0 : 0.6);
        }
    }

    public void clearGoal() {
        if (currentGoal != null) {
            lastGoalTime.put(currentGoal, villager.level().getGameTime());
        }
        villager.setGoalKey(null);
        this.currentGoal = null;
        this.goalInformation = null;
        this.pathDestPoint = null;
        villager.setActionStart(0);
        villager.setGoalKeySync("");
    }

    // --- Goal execution ---

    private void tickGoalExecution() {
        if (currentGoal == null || goalInformation == null) return;

        if (goalInformation.targetPoint != null) {
            double dist = villager.distanceToSqr(
                    goalInformation.targetPoint.x + 0.5,
                    goalInformation.targetPoint.y,
                    goalInformation.targetPoint.z + 0.5);
            int range = currentGoal.range(villager);
            if (dist > (range * range + 1)) {
                return;
            }
        }

        try {
            int duration = currentGoal.actionDuration(villager);
            if ((villager.level().getGameTime() - villager.getActionStart()) < duration) {
                return;
            }
            boolean finished = currentGoal.performAction(villager);
            if (finished) {
                clearGoal();
            } else {
                villager.setActionStart(villager.level().getGameTime());
            }
        } catch (Exception e) {
            LOGGER.error("Error executing goal: " + villager.getGoalKey(), e);
            clearGoal();
        }
    }

    // --- NBT helpers ---

    /** Called after NBT load to restore the active goal object from a persisted key. */
    public void restoreGoalFromKey(String key) {
        if (key == null || key.isEmpty()) return;
        this.currentGoal = Goal.get(key);
    }
}
