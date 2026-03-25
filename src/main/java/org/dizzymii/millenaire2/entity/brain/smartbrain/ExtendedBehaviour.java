package org.dizzymii.millenaire2.entity.brain.smartbrain;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;

import java.util.Map;

/**
 * Simplified base class for SmartBrain-compatible behaviours.
 *
 * <p>Subclasses implement {@link #checkExtraStartConditions}, {@link #start},
 * and optionally {@link #tick} / {@link #stop}.  The status machine follows the
 * same contract as vanilla {@link Behavior}.
 *
 * <p><b>SmartBrainLib migration note:</b> when {@code net.tslat.smartbrainlib}
 * is added as a dependency, change {@code extends ExtendedBehaviour<E>} to
 * {@code extends net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour<E>}
 * and remove this file.  Method signatures are identical.
 */
public abstract class ExtendedBehaviour<E extends LivingEntity> implements BehaviorControl<E> {

    private Behavior.Status status = Behavior.Status.STOPPED;

    // ========== Abstract / overrideable hooks ==========

    /** @return {@code true} if this behaviour can start this tick. */
    protected abstract boolean checkExtraStartConditions(ServerLevel level, E entity);

    /** Called once when the behaviour transitions to RUNNING. */
    protected abstract void start(ServerLevel level, E entity);

    /** @return {@code true} to keep the behaviour running after {@link #start}. */
    protected boolean shouldKeepRunning(ServerLevel level, E entity) {
        return false;
    }

    /** Called every tick while RUNNING. Default is a no-op. */
    protected void tick(ServerLevel level, E entity) {}

    /** Called once when the behaviour transitions to STOPPED. Default is a no-op. */
    protected void stop(ServerLevel level, E entity) {}

    // ========== BehaviorControl implementation ==========

    @Override
    public final Behavior.Status getStatus() {
        return status;
    }

    @Override
    public final boolean tryStart(ServerLevel level, E entity, long gameTime) {
        if (status != Behavior.Status.STOPPED) return false;
        if (!checkExtraStartConditions(level, entity)) return false;
        status = Behavior.Status.RUNNING;
        start(level, entity);
        return true;
    }

    @Override
    public final void tickOrStop(ServerLevel level, E entity, long gameTime) {
        if (status != Behavior.Status.RUNNING) return;
        if (!shouldKeepRunning(level, entity)) {
            status = Behavior.Status.STOPPED;
            stop(level, entity);
            return;
        }
        tick(level, entity);
    }

    @Override
    public final void doStop(ServerLevel level, E entity, long gameTime) {
        if (status == Behavior.Status.RUNNING) {
            status = Behavior.Status.STOPPED;
            stop(level, entity);
        }
    }

    @Override
    public String debugString() {
        return getClass().getSimpleName();
    }

    // ========== Shared goal-execution helper ==========

    /**
     * Executes one tick of a villager's active goal: proximity check → action duration
     * check → {@link org.dizzymii.millenaire2.goal.Goal#performAction performAction}.
     *
     * <p>Calls {@link org.dizzymii.millenaire2.entity.VillagerDebugger#checkStuck} during
     * travel and clears the goal on completion or exception.
     *
     * @return {@code true} if the goal completed (and was cleared) this tick
     */
    protected final boolean tickCurrentGoal(net.minecraft.server.level.ServerLevel level,
                                             org.dizzymii.millenaire2.entity.MillVillager villager) {
        org.dizzymii.millenaire2.goal.Goal current = villager.getCurrentGoal();
        org.dizzymii.millenaire2.goal.GoalInformation info = villager.getGoalInformation();
        if (current == null || info == null) return false;

        // Proximity gate
        if (info.targetPoint != null) {
            double distSq = villager.distanceToSqr(
                    info.targetPoint.x + 0.5, info.targetPoint.y, info.targetPoint.z + 0.5);
            int range = current.range(villager);
            if (distSq > (double) (range * range + 1)) {
                org.dizzymii.millenaire2.entity.VillagerDebugger.checkStuck(villager);
                return false;
            }
        }

        try {
            int duration = current.actionDuration(villager);
            if ((level.getGameTime() - villager.actionStart) < duration) return false;

            boolean finished = current.performAction(villager);
            if (finished) {
                org.dizzymii.millenaire2.entity.VillagerDebugger.recordGoalEnd(
                        villager, villager.goalKey, true);
                villager.getGoalController().clearGoal();
                return true;
            } else {
                villager.actionStart = level.getGameTime();
                return false;
            }
        } catch (Exception e) {
            org.dizzymii.millenaire2.util.MillLog.error(
                    villager, "[" + debugString() + "] performAction threw: " + villager.goalKey, e);
            org.dizzymii.millenaire2.entity.VillagerDebugger.recordGoalEnd(
                    villager, villager.goalKey, false);
            villager.getGoalController().clearGoal();
            return true;
        }
    }

    /**
     * Entry-condition memory requirements — empty by default (no memory preconditions).
     * Override to require specific Brain memories to be present before start.
     */
    protected Map<net.minecraft.world.entity.ai.memory.MemoryModuleType<?>,
            net.minecraft.world.entity.ai.memory.MemoryStatus> entryConditions() {
        return Map.of();
    }
}
