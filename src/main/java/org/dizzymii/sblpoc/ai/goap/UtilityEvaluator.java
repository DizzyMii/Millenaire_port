package org.dizzymii.sblpoc.ai.goap;

import javax.annotation.Nullable;

/**
 * Scores all NpcGoals against the current WorldState and picks the best one.
 * Uses hysteresis to prevent rapid goal flip-flopping.
 */
public class UtilityEvaluator {

    private static final float HYSTERESIS_BONUS = 5.0f;
    private static final float MIN_SCORE_THRESHOLD = 1.0f;

    @Nullable
    private NpcGoal currentGoal = null;

    /**
     * Evaluate all goals and return the highest-scoring one.
     * The current goal gets a hysteresis bonus to prevent flip-flopping.
     *
     * @param state Current world state snapshot
     * @return The best goal, or null if no goal scores above threshold
     */
    @Nullable
    public NpcGoal evaluate(WorldState state) {
        NpcGoal bestGoal = null;
        float bestScore = MIN_SCORE_THRESHOLD;

        for (NpcGoal goal : NpcGoal.values()) {
            float score = goal.score(state);

            // Hysteresis: current goal gets a bonus to prevent flip-flopping
            if (goal == currentGoal) {
                score += HYSTERESIS_BONUS;
            }

            if (score > bestScore) {
                bestScore = score;
                bestGoal = goal;
            }
        }

        currentGoal = bestGoal;
        return bestGoal;
    }

    /**
     * Force-set the current goal (e.g., for SURVIVE_IMMEDIATE interrupt).
     */
    public void setCurrentGoal(@Nullable NpcGoal goal) {
        this.currentGoal = goal;
    }

    @Nullable
    public NpcGoal getCurrentGoal() {
        return currentGoal;
    }

    /**
     * Check if the current goal has changed from the provided one.
     */
    public boolean hasGoalChanged(@Nullable NpcGoal previousGoal) {
        return currentGoal != previousGoal;
    }
}
