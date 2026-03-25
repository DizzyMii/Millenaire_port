package org.dizzymii.millenaire2.entity.brain.behaviour;

import net.minecraft.server.level.ServerLevel;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerDebugger;
import org.dizzymii.millenaire2.entity.brain.smartbrain.ExtendedBehaviour;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.util.MillLog;

/**
 * Brain behaviour for the REST activity (night-time sleep).
 *
 * <p>Activates the {@link Goal#sleep} goal when night falls and the villager
 * has a valid home position to sleep at.
 */
public class MillRestBehaviour extends ExtendedBehaviour<MillVillager> {

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MillVillager villager) {
        if (!level.isNight()) return false;
        if (Goal.sleep == null) return false;

        // Already sleeping — keep running
        if (villager.getCurrentGoal() == Goal.sleep) return true;

        try {
            GoalInformation info = Goal.sleep.getDestination(villager);
            if (info != null && info.hasTarget()) {
                villager.getGoalController().setActiveGoal("sleep", Goal.sleep, info);
                VillagerDebugger.recordGoalStart(villager, "sleep");
                return true;
            }
        } catch (Exception e) {
            MillLog.error(villager, "[Rest] Could not get sleep destination", e);
        }
        return false;
    }

    @Override
    protected boolean shouldKeepRunning(ServerLevel level, MillVillager villager) {
        // Keep sleeping as long as it's night and the goal is still valid
        if (!level.isNight()) return false;
        if (villager.getCurrentGoal() != Goal.sleep) return false;
        try {
            return Goal.sleep.isStillValid(villager);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void start(ServerLevel level, MillVillager villager) {
        // Goal activation was done in checkExtraStartConditions
    }

    @Override
    protected void tick(ServerLevel level, MillVillager villager) {
        if (villager.getCurrentGoal() == null) return;
        tickCurrentGoal(level, villager);
    }

    @Override
    protected void stop(ServerLevel level, MillVillager villager) {
        if (villager.getCurrentGoal() == Goal.sleep) {
            villager.getGoalController().clearGoal();
        }
    }
}
