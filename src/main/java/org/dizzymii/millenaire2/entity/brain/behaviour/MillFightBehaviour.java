package org.dizzymii.millenaire2.entity.brain.behaviour;

import net.minecraft.server.level.ServerLevel;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerDebugger;
import org.dizzymii.millenaire2.entity.brain.smartbrain.ExtendedBehaviour;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.util.MillLog;

/**
 * Brain behaviour for the FIGHT activity (defend the village against hostiles).
 *
 * <p>Activated when the villager's aggro flag is set (e.g. after being hurt).
 * Delegates to the {@link Goal#defendVillage} goal.  Stops once the villager
 * is no longer aggroed.
 */
public class MillFightBehaviour extends ExtendedBehaviour<MillVillager> {

    /** How long the fight activity stays active after the threat disappears (ticks). */
    private static final int FIGHT_LINGER_TICKS = 60;

    private int lingerCounter = 0;

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MillVillager villager) {
        if (!villager.isAggroed()) return false;
        if (Goal.defendVillage == null) return false;
        if (villager.vtype == null || !villager.vtype.helpInAttacks) return false;

        try {
            GoalInformation info = Goal.defendVillage.getDestination(villager);
            if (info != null && info.hasTarget()) {
                villager.getGoalController().setActiveGoal("defendvillage", Goal.defendVillage, info);
                VillagerDebugger.recordGoalStart(villager, "defendvillage");
                lingerCounter = FIGHT_LINGER_TICKS;
                return true;
            }
        } catch (Exception e) {
            MillLog.error(villager, "[Fight] getDestination threw for defendvillage", e);
        }
        return false;
    }

    @Override
    protected boolean shouldKeepRunning(ServerLevel level, MillVillager villager) {
        if (villager.isAggroed()) {
            lingerCounter = FIGHT_LINGER_TICKS;
            return true;
        }
        if (lingerCounter > 0) {
            lingerCounter--;
            return true;
        }
        return false;
    }

    @Override
    protected void start(ServerLevel level, MillVillager villager) {
        // Goal activation already done in checkExtraStartConditions
    }

    @Override
    protected void tick(ServerLevel level, MillVillager villager) {
        if (villager.getCurrentGoal() == null && villager.isAggroed()) {
            // Re-acquire target
            checkExtraStartConditions(level, villager);
        }

        if (villager.getCurrentGoal() != null) {
            executeDefendGoal(level, villager);
        }
    }

    @Override
    protected void stop(ServerLevel level, MillVillager villager) {
        if (villager.getCurrentGoal() == Goal.defendVillage) {
            VillagerDebugger.recordGoalEnd(villager, "defendvillage", true);
            villager.getGoalController().clearGoal();
        }
        lingerCounter = 0;
    }

    // ========== Helpers ==========

    private void executeDefendGoal(ServerLevel level, MillVillager villager) {
        tickCurrentGoal(level, villager);
    }
}
