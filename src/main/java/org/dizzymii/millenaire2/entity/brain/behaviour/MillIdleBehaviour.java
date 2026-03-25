package org.dizzymii.millenaire2.entity.brain.behaviour;

import net.minecraft.server.level.ServerLevel;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerDebugger;
import org.dizzymii.millenaire2.entity.brain.smartbrain.ExtendedBehaviour;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;

/**
 * Brain behaviour for the IDLE activity (socialise, then wander near home).
 *
 * <p>First tries the {@link Goal#gosocialise} goal; if that produces no target
 * the villager wanders randomly near their home or town-hall position.
 */
public class MillIdleBehaviour extends ExtendedBehaviour<MillVillager> {

    /** Re-pick wander target at most every 60 ticks to avoid jitter. */
    private static final int WANDER_INTERVAL = 60;

    private int wanderCooldown = 0;

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MillVillager villager) {
        return true; // IDLE is the catch-all fallback; it can always start
    }

    @Override
    protected boolean shouldKeepRunning(ServerLevel level, MillVillager villager) {
        return true; // IDLE always keeps running until a higher-priority activity interrupts
    }

    @Override
    protected void start(ServerLevel level, MillVillager villager) {
        trySocialise(level, villager);
    }

    @Override
    protected void tick(ServerLevel level, MillVillager villager) {
        wanderCooldown--;

        // If socialise goal finished or was never active, try again every interval
        if (villager.getCurrentGoal() == null && wanderCooldown <= 0) {
            if (!trySocialise(level, villager)) {
                tryWander(villager);
                wanderCooldown = WANDER_INTERVAL;
            }
        }

        // Execute socialise goal if active
        if (villager.getCurrentGoal() == Goal.gosocialise) {
            executeSocialiseGoal(level, villager);
        }
    }

    @Override
    protected void stop(ServerLevel level, MillVillager villager) {
        if (villager.getCurrentGoal() == Goal.gosocialise) {
            villager.getGoalController().clearGoal();
        }
    }

    // ========== Helpers ==========

    private boolean trySocialise(ServerLevel level, MillVillager villager) {
        if (Goal.gosocialise == null) return false;
        try {
            GoalInformation info = Goal.gosocialise.getDestination(villager);
            if (info != null && info.hasTarget()) {
                villager.getGoalController().setActiveGoal("gosocialise", Goal.gosocialise, info);
                VillagerDebugger.recordGoalStart(villager, "gosocialise");
                return true;
            }
        } catch (Exception e) {
            MillLog.error(villager, "[Idle] Error in gosocialise getDestination", e);
        }
        return false;
    }

    private void tryWander(MillVillager villager) {
        Point base = villager.housePoint != null ? villager.housePoint : villager.townHallPoint;
        if (base == null) return;
        int dx = villager.getRandom().nextInt(11) - 5;
        int dz = villager.getRandom().nextInt(11) - 5;
        villager.getNavigation().moveTo(base.x + dx + 0.5, base.y, base.z + dz + 0.5, 0.5);
        VillagerDebugger.recordGoalStart(villager, "wander");
    }

    private void executeSocialiseGoal(ServerLevel level, MillVillager villager) {
        tickCurrentGoal(level, villager);
    }
}
