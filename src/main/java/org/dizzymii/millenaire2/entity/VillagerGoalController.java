package org.dizzymii.millenaire2.entity;

import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;

/**
 * Encapsulates goal AI logic for a {@link MillVillager}: selection, execution, and clearing.
 *
 * <p>This controller is owned by a single villager and delegates goal lifecycle management
 * out of the main entity class, making both easier to read and test independently.</p>
 */
public final class VillagerGoalController {

    private final MillVillager villager;

    public VillagerGoalController(MillVillager villager) {
        this.villager = villager;
    }

    // ========== Tick entry points ==========

    /**
     * Called every {@code GOAL_TICK_INTERVAL} ticks. Validates the current goal and picks a
     * new one if needed.
     */
    public void tickGoalSelection() {
        if (Goal.goals == null || Goal.goals.isEmpty()) return;

        // Ensure VillagerType is resolved (needed after NBT load)
        if (villager.vtype == null && villager.vtypeKey != null) {
            villager.resolveVillagerType();
        }

        // If we have a valid active goal, check if it's still valid
        if (villager.getCurrentGoal() != null && villager.goalKey != null) {
            try {
                if (!villager.getCurrentGoal().isStillValid(villager)) {
                    clearGoal();
                }
            } catch (Exception e) {
                MillLog.error(villager, "Error checking goal validity: " + villager.goalKey, e);
                clearGoal();
            }
        }

        // If no goal, pick a new one
        if (villager.getCurrentGoal() == null) {
            selectNewGoal();
        }
    }

    /**
     * Called every tick. Executes the active goal's action once the villager has arrived at
     * its destination and the action timer has elapsed.
     */
    public void tickGoalExecution() {
        GoalInformation goalInformation = villager.getGoalInformation();
        if (villager.getCurrentGoal() == null || goalInformation == null) return;

        // Check if we're close enough to the target to perform the action
        if (goalInformation.targetPoint != null) {
            double dist = villager.distanceToSqr(
                    goalInformation.targetPoint.x + 0.5,
                    goalInformation.targetPoint.y,
                    goalInformation.targetPoint.z + 0.5
            );
            int range = villager.getCurrentGoal().range(villager);
            if (dist > (range * range + 1)) {
                return; // Still travelling
            }
        }

        // Check action duration
        try {
            int duration = villager.getCurrentGoal().actionDuration(villager);
            if ((villager.level().getGameTime() - villager.actionStart) < duration) {
                return; // Still waiting for action to complete
            }

            boolean finished = villager.getCurrentGoal().performAction(villager);
            if (finished) {
                clearGoal();
            } else {
                villager.actionStart = villager.level().getGameTime();
            }
        } catch (Exception e) {
            MillLog.error(villager, "Error executing goal: " + villager.goalKey, e);
            clearGoal();
        }
    }

    // ========== Internal helpers ==========

    private void selectNewGoal() {
        boolean isNight = !villager.level().isDay();

        if (villager.vtype != null && !villager.vtype.goals.isEmpty()) {
            for (String gKey : villager.vtype.goals) {
                Goal g = Goal.goals.get(gKey);
                if (g == null) continue;
                if (isNight && !g.canBeDoneAtNight()) continue;
                if (!isNight && !g.canBeDoneInDayTime()) continue;

                // Check time-of-day restrictions
                if (g.minimumHour >= 0 || g.maximumHour >= 0) {
                    long dayTime = villager.level().getDayTime() % 24000;
                    int hour = (int) (dayTime / 1000 + 6) % 24;
                    if (g.minimumHour >= 0 && hour < g.minimumHour) continue;
                    if (g.maximumHour >= 0 && hour > g.maximumHour) continue;
                }

                // Check cooldown
                Long lastTime = villager.lastGoalTime.get(g);
                if (lastTime != null && (villager.level().getGameTime() - lastTime) < Goal.STANDARD_DELAY / 50) {
                    continue;
                }

                // Try to get a destination for this goal
                try {
                    GoalInformation info = g.getDestination(villager);
                    if (info != null && info.hasTarget()) {
                        setActiveGoal(gKey, g, info);
                        return;
                    }
                } catch (Exception e) {
                    MillLog.error(villager, "Error getting destination for goal: " + gKey, e);
                }
            }
        }

        // Fallback: sleep at night, idle wander during day
        if (isNight && Goal.sleep != null) {
            try {
                GoalInformation info = Goal.sleep.getDestination(villager);
                if (info != null && info.hasTarget()) {
                    setActiveGoal("sleep", Goal.sleep, info);
                    return;
                }
            } catch (Exception ignored) {}
        }

        if (!isNight) {
            if (Goal.gosocialise != null) {
                try {
                    GoalInformation info = Goal.gosocialise.getDestination(villager);
                    if (info != null && info.hasTarget()) {
                        setActiveGoal("gosocialise", Goal.gosocialise, info);
                        return;
                    }
                } catch (Exception ignored) {}
            }
            // Random wander near home point
            Point wanderTarget = villager.housePoint != null ? villager.housePoint : villager.townHallPoint;
            if (wanderTarget != null) {
                int dx = villager.getRandom().nextInt(11) - 5;
                int dz = villager.getRandom().nextInt(11) - 5;
                Point wander = new Point(wanderTarget.x + dx, wanderTarget.y, wanderTarget.z + dz);
                villager.getNavigation().moveTo(wander.x + 0.5, wander.y, wander.z + 0.5, 0.5);
            }
        }
    }

    /**
     * Activates a goal for the villager.
     * Package-private so {@link MillVillager} can call this for event-driven goal overrides
     * (e.g., switching to defend when attacked).
     */
    void setActiveGoal(String key, Goal goal, GoalInformation info) {
        villager.goalKey = key;
        villager.setCurrentGoalInternal(goal, info);
        villager.setSynchedGoalKey(key);
        villager.goalStarted = villager.level().getGameTime();
        villager.actionStart = villager.level().getGameTime();

        if (info.targetPoint != null) {
            villager.setPathDestPoint(info.targetPoint);
            villager.getNavigation().moveTo(
                    info.targetPoint.x + 0.5,
                    info.targetPoint.y,
                    info.targetPoint.z + 0.5,
                    goal.sprint ? 1.0 : 0.6
            );
        }
    }

    /**
     * Clears the active goal, recording the cooldown timer for the cleared goal.
     */
    public void clearGoal() {
        if (villager.getCurrentGoal() != null) {
            villager.lastGoalTime.put(villager.getCurrentGoal(), villager.level().getGameTime());
        }
        villager.goalKey = null;
        villager.setCurrentGoalInternal(null, null);
        villager.setSynchedGoalKey("");
        villager.setPathDestPoint(null);
        villager.actionStart = 0;
    }
}
