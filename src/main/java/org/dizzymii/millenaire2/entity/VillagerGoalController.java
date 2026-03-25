package org.dizzymii.millenaire2.entity;

import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;

/**
 * Encapsulates low-level goal lifecycle for a {@link MillVillager}:
 * activating, executing, and clearing goals.
 *
 * <p>Goal <em>selection</em> logic now lives in the Brain behaviour classes
 * ({@link org.dizzymii.millenaire2.entity.brain.behaviour.MillWorkBehaviour},
 * {@link org.dizzymii.millenaire2.entity.brain.behaviour.MillRestBehaviour}, etc.)
 * so this controller is intentionally lean.  It owns only the transitions that
 * are shared across multiple behaviours.</p>
 *
 * <p>Accessibility note: {@link #setActiveGoal} is package-private — Brain
 * behaviours in the {@code entity.brain.behaviour} package call it via the
 * public {@link MillVillager#getGoalController()} getter.</p>
 */
public final class VillagerGoalController {

    private final MillVillager villager;

    public VillagerGoalController(MillVillager villager) {
        this.villager = villager;
    }

    // ========== Kept for backward-compat / transition ==========

    /**
     * Legacy entry-point retained so any code still calling the old polling
     * loop continues to work during transition.  New code should go through
     * the Brain behaviour classes instead.
     *
     * @deprecated Goal selection is handled by Brain behaviours; this method
     *             is a no-op bridge kept only for backward compatibility.
     */
    @Deprecated
    public void tickGoalSelection() {
        if (Goal.goals == null || Goal.goals.isEmpty()) return;

        // Ensure VillagerType is resolved (needed after NBT load)
        if (villager.vtype == null && villager.vtypeKey != null) {
            villager.resolveVillagerType();
        }

        // Validate current goal
        if (villager.getCurrentGoal() != null && villager.goalKey != null) {
            try {
                if (!villager.getCurrentGoal().isStillValid(villager)) {
                    MillLog.minor(villager, "[GoalController] Goal no longer valid: " + villager.goalKey);
                    clearGoal();
                }
            } catch (Exception e) {
                MillLog.error(villager, "[GoalController] Error checking goal validity: " + villager.goalKey, e);
                clearGoal();
            }
        }
    }

    // ========== Core API used by Brain behaviours ==========

    /**
     * Activates a goal for the villager, setting navigation towards the target
     * and syncing the goal key to clients.
     *
     * <p>Accessible from Brain behaviours via
     * {@link MillVillager#getGoalController()}.
     */
    public void setActiveGoal(String key, Goal goal, GoalInformation info) {
        String previous = villager.goalKey;
        villager.goalKey = key;
        villager.setCurrentGoalInternal(goal, info);
        villager.setSynchedGoalKey(key);
        villager.goalStarted = villager.level().getGameTime();
        villager.actionStart = villager.level().getGameTime();

        if (villager.extraLog && !key.equals(previous)) {
            MillLog.major(villager, "[GoalController] Goal → " + key
                    + (previous != null ? " (was " + previous + ")" : ""));
        }

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
     * Clears the active goal, recording the cooldown timestamp so the same
     * goal is not immediately re-selected.
     */
    public void clearGoal() {
        if (villager.getCurrentGoal() != null) {
            if (villager.extraLog) {
                MillLog.major(villager, "[GoalController] Cleared goal: " + villager.goalKey);
            }
            villager.lastGoalTime.put(villager.getCurrentGoal(), villager.level().getGameTime());
        }
        villager.goalKey = null;
        villager.setCurrentGoalInternal(null, null);
        villager.setSynchedGoalKey("");
        villager.setPathDestPoint(null);
        villager.actionStart = 0;
    }
}
