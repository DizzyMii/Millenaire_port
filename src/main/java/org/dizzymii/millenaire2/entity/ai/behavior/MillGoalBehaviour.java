package org.dizzymii.millenaire2.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.ai.MillMemoryTypes;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.MillLog;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Generic Brain behaviour wrapper that delegates to the existing Millénaire
 * {@link Goal} system. This is the key bridge enabling 1:1 functional parity
 * with the original mod's AI while using the modern Brain architecture.
 *
 * <p>Each tick this behaviour evaluates the highest-priority Goal from the
 * villager's type, walks toward the destination, and performs the action
 * exactly as the original {@code checkGoals()} did.</p>
 */
public class MillGoalBehaviour extends ExtendedBehaviour<MillVillager> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED)
            );

    @Nullable private Goal activeGoal;
    @Nullable private GoalInformation activeInfo;
    @Nullable private String activeGoalKey;
    private long actionStart = 0;
    private long goalStarted = 0;
    private boolean arrivedAtTarget = false;

    public MillGoalBehaviour() {
        runFor(entity -> entity.getRandom().nextIntBetweenInclusive(200, 600));
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MillVillager villager) {
        if (villager.vtype == null) return false;
        if (Goal.goals == null || Goal.goals.isEmpty()) return false;
        if (villager.vtype.goals == null || villager.vtype.goals.isEmpty()) return false;

        // Try to select best goal by priority (matching original setNextGoal)
        Goal best = selectBestGoal(villager);
        if (best != null) return true;

        // Fallback goals that don't depend on vtype.goals list
        boolean isNight = (level.getDayTime() % 24000) >= 12000;
        if (isNight && Goal.sleep != null) {
            try {
                GoalInformation info = Goal.sleep.getDestination(villager);
                if (info != null && info.hasTarget()) return true;
            } catch (Exception ignored) {}
        }
        if (!isNight && Goal.gosocialise != null) {
            try {
                GoalInformation info = Goal.gosocialise.getDestination(villager);
                if (info != null && info.hasTarget()) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    @Override
    protected void start(ServerLevel level, MillVillager villager, long gameTime) {
        arrivedAtTarget = false;
        actionStart = 0;
        goalStarted = gameTime;

        Goal best = selectBestGoal(villager);
        if (best == null) {
            // Try fallback goals
            boolean isNight = (level.getDayTime() % 24000) >= 12000;
            if (isNight && Goal.sleep != null) {
                try {
                    GoalInformation info = Goal.sleep.getDestination(villager);
                    if (info != null && info.hasTarget()) best = Goal.sleep;
                } catch (Exception ignored) {}
            }
            if (best == null && !isNight && Goal.gosocialise != null) {
                try {
                    GoalInformation info = Goal.gosocialise.getDestination(villager);
                    if (info != null && info.hasTarget()) best = Goal.gosocialise;
                } catch (Exception ignored) {}
            }
        }
        if (best == null) {
            doStop(level, villager, gameTime);
            return;
        }

        activeGoal = best;
        activeGoalKey = best.key;

        try {
            activeInfo = best.getDestination(villager);
        } catch (Exception e) {
            MillLog.error(villager, "Error getting goal destination: " + best.key, e);
            activeGoal = null;
            return;
        }

        // Update brain memories
        villager.getBrain().setMemory(MillMemoryTypes.ACTIVE_GOAL_KEY.get(), activeGoalKey);

        // Set walk target if destination exists
        if (activeInfo != null && activeInfo.hasTarget()) {
            BlockPos target = activeInfo.targetPoint.toBlockPos();
            float speed = best.sprint ? 1.0f : 0.6f;
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                    new WalkTarget(target, speed, best.range(villager)));
        }
    }

    @Override
    protected boolean shouldKeepRunning(MillVillager villager) {
        if (activeGoal == null) return false;
        try {
            return activeGoal.isStillValid(villager);
        } catch (Exception e) {
            MillLog.error(villager, "Error checking goal validity: " + activeGoalKey, e);
            return false;
        }
    }

    @Override
    protected void tick(ServerLevel level, MillVillager villager, long gameTime) {
        if (activeGoal == null || activeInfo == null) return;

        // Check if we've arrived at the target
        if (activeInfo.hasTarget() && !arrivedAtTarget) {
            double dist = villager.distanceToSqr(
                    activeInfo.targetPoint.x + 0.5,
                    activeInfo.targetPoint.y,
                    activeInfo.targetPoint.z + 0.5
            );
            int range = activeGoal.range(villager);
            if (dist > (range * range + 1)) {
                // Still travelling — re-issue walk target if nav stopped
                if (villager.getNavigation().isDone()) {
                    BlockPos target = activeInfo.targetPoint.toBlockPos();
                    float speed = activeGoal.sprint ? 1.0f : 0.6f;
                    villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                            new WalkTarget(target, speed, range));
                }
                return;
            }
            arrivedAtTarget = true;
            actionStart = gameTime; // Start action timer when ARRIVING, not when goal is set
        }

        // If no target, treat as immediate action
        if (!activeInfo.hasTarget() && actionStart == 0) {
            actionStart = gameTime;
            arrivedAtTarget = true;
        }

        // Check action duration
        try {
            int duration = activeGoal.actionDuration(villager);
            if ((gameTime - actionStart) < duration) {
                return; // Still waiting for action to complete
            }

            boolean finished = activeGoal.performAction(villager);
            if (finished) {
                // Check for chained goal (nextGoal)
                String nextGoalKey = activeGoal.nextGoal(villager);
                if (nextGoalKey != null && Goal.goals.containsKey(nextGoalKey)) {
                    // Chain to next goal
                    activeGoal = Goal.goals.get(nextGoalKey);
                    activeGoalKey = nextGoalKey;
                    arrivedAtTarget = false;
                    actionStart = 0;
                    goalStarted = gameTime;

                    try {
                        activeInfo = activeGoal.getDestination(villager);
                    } catch (Exception e) {
                        MillLog.error(villager, "Error getting chained goal destination: " + nextGoalKey, e);
                        doStop(level, villager, gameTime);
                        return;
                    }

                    villager.getBrain().setMemory(MillMemoryTypes.ACTIVE_GOAL_KEY.get(), activeGoalKey);

                    if (activeInfo != null && activeInfo.hasTarget()) {
                        BlockPos target = activeInfo.targetPoint.toBlockPos();
                        float speed = activeGoal.sprint ? 1.0f : 0.6f;
                        villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                                new WalkTarget(target, speed, activeGoal.range(villager)));
                    }
                } else {
                    doStop(level, villager, gameTime);
                }
            } else {
                // Action not finished, reset timer for next cycle
                actionStart = gameTime;
            }
        } catch (Exception e) {
            MillLog.error(villager, "Error executing goal: " + activeGoalKey, e);
            doStop(level, villager, gameTime);
        }
    }

    @Override
    protected void stop(ServerLevel level, MillVillager villager, long gameTime) {
        if (activeGoal != null) {
            try {
                activeGoal.onComplete(villager);
            } catch (Exception e) {
                MillLog.error(villager, "Error in goal onComplete: " + activeGoalKey, e);
            }
        }
        activeGoal = null;
        activeInfo = null;
        activeGoalKey = null;
        actionStart = 0;
        arrivedAtTarget = false;
        villager.getBrain().eraseMemory(MillMemoryTypes.ACTIVE_GOAL_KEY.get());
    }

    /**
     * Select the highest-priority goal from the villager's type goals.
     * Mirrors the original {@code setNextGoal()} priority-based selection.
     */
    @Nullable
    private Goal selectBestGoal(MillVillager villager) {
        if (villager.vtype == null || villager.vtype.goals == null) return null;

        Goal bestGoal = null;
        int bestPriority = Integer.MIN_VALUE;
        long worldTime = villager.level().getDayTime() % 24000;
        boolean isDay = worldTime >= 0 && worldTime < 12000;

        for (String goalKey : villager.vtype.goals) {
            Goal goal = Goal.goals.get(goalKey);
            if (goal == null) continue;

            // Time-of-day check
            if (isDay && !goal.canBeDoneInDayTime()) continue;
            if (!isDay && !goal.canBeDoneAtNight()) continue;

            // Check if goal is possible (has valid destination)
            try {
                GoalInformation info = goal.getDestination(villager);
                if (info == null || !info.hasTarget()) continue;
            } catch (Exception e) {
                continue;
            }

            // Priority check — use goal.priority() if available, else use order
            try {
                int priority = goal.priority(villager);
                if (priority > bestPriority) {
                    bestPriority = priority;
                    bestGoal = goal;
                }
            } catch (Exception e) {
                // Fallback: use order position as priority
                if (bestGoal == null) {
                    bestGoal = goal;
                }
            }
        }

        return bestGoal;
    }
}
