package org.dizzymii.millenaire2.entity.brain.behaviour;

import net.minecraft.server.level.ServerLevel;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerDebugger;
import org.dizzymii.millenaire2.entity.brain.smartbrain.ExtendedBehaviour;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.MillLog;

/**
 * Brain behaviour for the WORK activity (daytime productive goals).
 *
 * <p>Iterates the villager type's goal list in order, selects the first goal
 * that is not a leisure goal, is not on cooldown, has valid time restrictions,
 * and can produce a destination; then delegates execution every tick to the
 * goal's {@link Goal#performAction} method via the {@link
 * org.dizzymii.millenaire2.entity.VillagerGoalController}.
 */
public class MillWorkBehaviour extends ExtendedBehaviour<MillVillager> {

    /** Maximum ticks a single work goal may run before it is force-cleared. */
    private static final int GOAL_TIMEOUT_TICKS = 1200; // 60 s

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MillVillager villager) {
        if (Goal.goals == null || Goal.goals.isEmpty()) return false;
        if (villager.vtype == null || villager.vtype.goals.isEmpty()) return false;
        // Already have an active goal — keep ticking it rather than starting a new one
        if (villager.getCurrentGoal() != null) return true;
        // Try to find a valid work goal
        return findAndStartGoal(level, villager);
    }

    @Override
    protected boolean shouldKeepRunning(ServerLevel level, MillVillager villager) {
        Goal current = villager.getCurrentGoal();
        if (current == null) return false;

        // Timeout guard: a goal that has run too long is cleared and we stop
        long elapsed = level.getGameTime() - villager.goalStarted;
        if (elapsed > GOAL_TIMEOUT_TICKS) {
            MillLog.minor(villager, "[Work] Goal timed out after " + elapsed + " ticks: " + villager.goalKey);
            VillagerDebugger.recordTimeout(villager, villager.goalKey);
            villager.getGoalController().clearGoal();
            return false;
        }

        try {
            return current.isStillValid(villager);
        } catch (Exception e) {
            MillLog.error(villager, "[Work] isStillValid threw for " + villager.goalKey, e);
            villager.getGoalController().clearGoal();
            return false;
        }
    }

    @Override
    protected void start(ServerLevel level, MillVillager villager) {
        // findAndStartGoal was already called in checkExtraStartConditions;
        // the goal is now active.  Nothing else to do here.
    }

    @Override
    protected void tick(ServerLevel level, MillVillager villager) {
        if (villager.getCurrentGoal() == null) {
            // Current goal finished; try selecting the next one immediately
            findAndStartGoal(level, villager);
            return;
        }
        tickCurrentGoal(level, villager);
    }

    @Override
    protected void stop(ServerLevel level, MillVillager villager) {
        villager.getGoalController().clearGoal();
    }

    // ========== Helpers ==========

    /** Scans the villager's goal list and activates the first suitable work goal. */
    private boolean findAndStartGoal(ServerLevel level, MillVillager villager) {
        if (villager.vtype == null) return false;

        for (String gKey : villager.vtype.goals) {
            Goal g = Goal.goals.get(gKey);
            if (g == null || g.leasure) continue;
            if (!g.canBeDoneInDayTime()) continue;
            if (!checkTimeWindow(g, level)) continue;
            if (isOnCooldown(g, villager, level)) continue;

            try {
                GoalInformation info = g.getDestination(villager);
                if (info != null && info.hasTarget()) {
                    villager.getGoalController().setActiveGoal(gKey, g, info);
                    VillagerDebugger.recordGoalStart(villager, gKey);
                    return true;
                }
            } catch (Exception e) {
                MillLog.error(villager, "[Work] getDestination threw for " + gKey, e);
            }
        }
        return false;
    }

    private boolean checkTimeWindow(Goal g, ServerLevel level) {
        if (g.minimumHour < 0 && g.maximumHour < 0) return true;
        long dayTime = level.getDayTime() % 24000;
        int hour = (int) (dayTime / 1000 + 6) % 24;
        if (g.minimumHour >= 0 && hour < g.minimumHour) return false;
        if (g.maximumHour >= 0 && hour > g.maximumHour) return false;
        return true;
    }

    private boolean isOnCooldown(Goal g, MillVillager villager, ServerLevel level) {
        Long last = villager.lastGoalTime.get(g);
        return last != null && (level.getGameTime() - last) < Goal.STANDARD_DELAY / 50L;
    }
}
