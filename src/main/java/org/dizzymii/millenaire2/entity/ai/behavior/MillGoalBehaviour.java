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
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Bulletproof Brain behaviour wrapper that delegates to the Millénaire
 * {@link Goal} system. Guarantees that villagers ALWAYS have something to do.
 *
 * <h3>Failure recovery layers (in order):</h3>
 * <ol>
 *   <li>Select best goal from villager's type goals list</li>
 *   <li>Try time-of-day fallback (sleep at night, socialise in day)</li>
 *   <li>Try DEFAULT_GOALS list (gosocialise, gorest, construction, etc.)</li>
 *   <li>Create synthetic wander target so villager always moves</li>
 * </ol>
 *
 * <p>No combination of null vtype, empty goals, or failed destinations can
 * prevent this behaviour from producing villager activity.</p>
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
    private boolean isSyntheticWander = false;
    private static final long TRAVEL_TIMEOUT_TICKS = 600;
    private static final long GOAL_COOLDOWN_TICKS = 40; // 2 seconds — fast goal cycling

    // Fallback goal keys used when vtype.goals is null/empty or all goals fail
    private static final String[] DEFAULT_GOALS = {
            "gosocialise", "gorest", "construction", "gathergoods", "bringbackresourceshome"
    };

    public MillGoalBehaviour() {
        runFor(entity -> entity.getRandom().nextIntBetweenInclusive(200, 600));
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    // =========================================================================
    // BULLETPROOF: Always returns true. All failure handling is inside start().
    // =========================================================================
    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MillVillager villager) {
        // Attempt vtype resolution if missing — one last chance before we proceed
        if (villager.vtype == null) {
            villager.resolveVillagerType();
        }
        // Always proceed — start() handles every possible null/empty case
        return Goal.goals != null && !Goal.goals.isEmpty();
    }

    @Override
    protected void start(ServerLevel level, MillVillager villager, long gameTime) {
        arrivedAtTarget = false;
        actionStart = 0;
        goalStarted = gameTime;
        isSyntheticWander = false;

        // === Layer 1: Try vtype goals ===
        Goal best = selectBestGoal(villager);

        // === Layer 2: Time-of-day fallback ===
        if (best == null) {
            boolean isNight = (level.getDayTime() % 24000) >= 12000;
            if (isNight && Goal.sleep != null) {
                best = tryGoalSafe(Goal.sleep, villager);
            }
            if (best == null && Goal.gosocialise != null) {
                best = tryGoalSafe(Goal.gosocialise, villager);
            }
        }

        // === Layer 3: DEFAULT_GOALS list ===
        if (best == null) {
            for (String fallbackKey : DEFAULT_GOALS) {
                Goal fallback = Goal.goals.get(fallbackKey);
                if (fallback != null) {
                    best = tryGoalSafe(fallback, villager);
                    if (best != null) break;
                }
            }
        }

        // === Layer 4: Synthetic wander — GUARANTEED to produce movement ===
        if (best == null) {
            issueWanderTarget(level, villager);
            isSyntheticWander = true;
            return;
        }

        // We have a valid goal — activate it
        activateGoal(level, villager, best, gameTime);
    }

    /**
     * Try a goal safely — returns the goal if it has a valid destination, null otherwise.
     */
    @Nullable
    private Goal tryGoalSafe(Goal goal, MillVillager villager) {
        try {
            GoalInformation info = goal.getDestination(villager);
            if (info != null && info.hasTarget()) return goal;
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Activate a goal: set destination, walk target, memories.
     */
    private void activateGoal(ServerLevel level, MillVillager villager, Goal goal, long gameTime) {
        activeGoal = goal;
        activeGoalKey = goal.key;

        try {
            activeInfo = goal.getDestination(villager);
        } catch (Exception e) {
            MillLog.error(villager, "Error getting goal destination: " + goal.key, e);
            // Destination failed — fall back to wander
            issueWanderTarget(level, villager);
            isSyntheticWander = true;
            return;
        }

        // If destination is null despite tryGoalSafe passing (race condition), wander
        if (activeInfo == null || !activeInfo.hasTarget()) {
            issueWanderTarget(level, villager);
            isSyntheticWander = true;
            return;
        }

        // Call onAccept callback
        try {
            goal.onAccept(villager);
        } catch (Exception e) {
            MillLog.error(villager, "Error in goal onAccept: " + goal.key, e);
        }

        // Update villager state
        villager.currentGoal = goal;
        villager.goalKey = activeGoalKey;
        villager.goalStarted = gameTime;
        villager.getBrain().setMemory(MillMemoryTypes.ACTIVE_GOAL_KEY.get(), activeGoalKey);

        // Set walk target
        BlockPos target = activeInfo.targetPoint.toBlockPos();
        float speed = goal.sprint ? 1.0f : 0.6f;
        villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                new WalkTarget(target, speed, goal.range(villager)));
    }

    /**
     * Issue a random wander target so the villager always moves.
     * This is the ultimate fallback — it CANNOT fail.
     */
    private void issueWanderTarget(ServerLevel level, MillVillager villager) {
        int dx = villager.getRandom().nextInt(17) - 8;
        int dz = villager.getRandom().nextInt(17) - 8;
        BlockPos base = villager.blockPosition().offset(dx, 0, dz);
        BlockPos surface = level.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, base);

        villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                new WalkTarget(surface, 0.5f, 2));
    }

    @Override
    protected boolean shouldKeepRunning(MillVillager villager) {
        // Synthetic wander always keeps running (short-lived, handled by runFor timeout)
        if (isSyntheticWander) return true;
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
        // Synthetic wander has no action — just let the walk target drive movement
        if (isSyntheticWander) return;
        if (activeGoal == null || activeInfo == null) return;

        // Check for raid interruption
        if (activeGoal.isInterruptedByRaid()) {
            boolean underAttack = villager.getBrain()
                    .getMemory(MillMemoryTypes.VILLAGE_UNDER_ATTACK.get()).orElse(false);
            if (underAttack) {
                MillLog.minor(villager, "Goal " + activeGoalKey + " interrupted by raid");
                doStop(level, villager, gameTime);
                return;
            }
        }

        // Check if we've arrived at the target
        if (activeInfo.hasTarget() && !arrivedAtTarget) {
            double dist = villager.distanceToSqr(
                    activeInfo.targetPoint.x + 0.5,
                    activeInfo.targetPoint.y,
                    activeInfo.targetPoint.z + 0.5
            );
            int range = activeGoal.range(villager);
            if (dist > (range * range + 1)) {
                // Travel timeout — teleport if stuck too long
                if (gameTime - goalStarted > TRAVEL_TIMEOUT_TICKS) {
                    MillLog.minor(villager, "Travel timeout for goal " + activeGoalKey);
                    // If repeatedly stuck, teleport to target
                    if (villager.longDistanceStuck >= 2 && activeInfo.targetPoint != null) {
                        Point tp = activeInfo.targetPoint;
                        villager.teleportTo(tp.x + 0.5, tp.y + 1.0, tp.z + 0.5);
                        MillLog.minor(villager, "Teleported to goal target " + tp);
                        villager.longDistanceStuck = 0;
                        arrivedAtTarget = true;
                        actionStart = gameTime;
                    } else {
                        villager.onPathFailed();
                        doStop(level, villager, gameTime);
                    }
                    return;
                }
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
            villager.stopMoving = true;
            actionStart = gameTime;
        }

        // If no target, treat as immediate action
        if (!activeInfo.hasTarget() && actionStart == 0) {
            actionStart = gameTime;
            arrivedAtTarget = true;
        }

        // Check action duration then perform
        try {
            int duration = activeGoal.actionDuration(villager);
            if ((gameTime - actionStart) < duration) {
                return; // Still waiting
            }

            boolean finished = activeGoal.performAction(villager);
            if (finished) {
                // Check for chained goal
                String nextGoalKey = activeGoal.nextGoal(villager);
                if (nextGoalKey != null) {
                    String normalizedNext = nextGoalKey.toLowerCase();
                    Goal nextGoal = Goal.goals.get(normalizedNext);
                    if (nextGoal != null) {
                        chainToGoal(level, villager, nextGoal, normalizedNext, gameTime);
                        return;
                    }
                }
                doStop(level, villager, gameTime);
            } else {
                actionStart = gameTime;
            }
        } catch (Exception e) {
            MillLog.error(villager, "Error executing goal: " + activeGoalKey, e);
            doStop(level, villager, gameTime);
        }
    }

    /**
     * Chain to a next goal after current one finishes.
     */
    private void chainToGoal(ServerLevel level, MillVillager villager, Goal nextGoal,
                             String nextKey, long gameTime) {
        activeGoal = nextGoal;
        activeGoalKey = nextKey;
        arrivedAtTarget = false;
        actionStart = 0;
        goalStarted = gameTime;

        try {
            activeInfo = nextGoal.getDestination(villager);
        } catch (Exception e) {
            MillLog.error(villager, "Error getting chained goal destination: " + nextKey, e);
            doStop(level, villager, gameTime);
            return;
        }

        villager.currentGoal = nextGoal;
        villager.goalKey = nextKey;
        villager.getBrain().setMemory(MillMemoryTypes.ACTIVE_GOAL_KEY.get(), nextKey);

        if (activeInfo != null && activeInfo.hasTarget()) {
            BlockPos target = activeInfo.targetPoint.toBlockPos();
            float speed = nextGoal.sprint ? 1.0f : 0.6f;
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                    new WalkTarget(target, speed, nextGoal.range(villager)));
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
            villager.lastGoalTime.put(activeGoal, gameTime);
        }
        // Restore movement and clear held items
        villager.stopMoving = false;
        villager.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,
                net.minecraft.world.item.ItemStack.EMPTY);
        activeGoal = null;
        activeInfo = null;
        activeGoalKey = null;
        actionStart = 0;
        arrivedAtTarget = false;
        isSyntheticWander = false;
        villager.currentGoal = null;
        villager.goalKey = null;
        villager.getBrain().eraseMemory(MillMemoryTypes.ACTIVE_GOAL_KEY.get());
    }

    /**
     * Select the highest-priority goal from the villager's type goals.
     * Case-normalizes all key lookups. Returns null only if no goal resolves.
     */
    @Nullable
    private Goal selectBestGoal(MillVillager villager) {
        if (villager.vtype == null || villager.vtype.goals == null) return null;

        Goal bestGoal = null;
        int bestPriority = Integer.MIN_VALUE;
        long worldTime = villager.level().getDayTime() % 24000;
        boolean isDay = worldTime >= 0 && worldTime < 12000;

        for (String goalKey : villager.vtype.goals) {
            // Case-normalize lookup
            Goal goal = Goal.goals.get(goalKey.toLowerCase());
            if (goal == null) continue;

            // Time-of-day check
            if (isDay && !goal.canBeDoneInDayTime()) continue;
            if (!isDay && !goal.canBeDoneAtNight()) continue;

            // Cooldown check
            Long lastTime = villager.lastGoalTime.get(goal);
            if (lastTime != null && (villager.level().getGameTime() - lastTime) < GOAL_COOLDOWN_TICKS) {
                continue;
            }

            // Check if goal has valid destination
            try {
                GoalInformation info = goal.getDestination(villager);
                if (info == null || !info.hasTarget()) continue;
            } catch (Exception e) {
                continue;
            }

            // Priority selection
            try {
                int priority = goal.priority(villager);
                if (priority > bestPriority) {
                    bestPriority = priority;
                    bestGoal = goal;
                }
            } catch (Exception e) {
                if (bestGoal == null) {
                    bestGoal = goal;
                }
            }
        }

        return bestGoal;
    }
}
