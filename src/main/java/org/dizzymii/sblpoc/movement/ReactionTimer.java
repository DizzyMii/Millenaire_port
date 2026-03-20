package org.dizzymii.sblpoc.movement;

import net.minecraft.util.RandomSource;

/**
 * Adds human-like reaction time variance to NPC actions.
 *
 * Instead of reacting instantly (0 tick delay), the NPC waits a randomized
 * number of ticks before executing certain actions. This makes combat and
 * survival behaviour feel more natural.
 *
 * Base reaction time: 3-6 ticks (150-300ms) — average human reaction.
 * Modified by:
 * - Personality (cautious NPCs react slightly slower, aggressive faster)
 * - Health (low HP = adrenaline = faster reactions)
 * - Surprise (first hit in combat has longer reaction)
 */
public class ReactionTimer {

    private static final int BASE_MIN_TICKS = 3;  // 150ms
    private static final int BASE_MAX_TICKS = 6;  // 300ms
    private static final int SURPRISE_BONUS = 4;   // Extra delay on first reaction

    private final RandomSource random;
    private int currentDelay = 0;
    private boolean surprised = true; // First reaction in a sequence is slower

    // Personality modifier: negative = faster, positive = slower
    private int personalityModifier = 0;

    public ReactionTimer(RandomSource random) {
        this.random = random;
    }

    /**
     * Start a new reaction delay. Returns the number of ticks to wait.
     */
    public int startReaction(float healthPercent) {
        int min = BASE_MIN_TICKS + personalityModifier;
        int max = BASE_MAX_TICKS + personalityModifier;

        // Adrenaline: faster reactions at low HP
        if (healthPercent < 0.3f) {
            min = Math.max(1, min - 2);
            max = Math.max(2, max - 2);
        } else if (healthPercent < 0.5f) {
            min = Math.max(1, min - 1);
            max = Math.max(2, max - 1);
        }

        // Surprise penalty on first reaction
        if (surprised) {
            max += SURPRISE_BONUS;
            surprised = false;
        }

        min = Math.max(1, min);
        max = Math.max(min + 1, max);

        currentDelay = random.nextIntBetweenInclusive(min, max);
        return currentDelay;
    }

    /**
     * Tick the timer. Returns true when the reaction delay has elapsed.
     */
    public boolean tick() {
        if (currentDelay > 0) {
            currentDelay--;
            return currentDelay <= 0;
        }
        return true; // No active delay
    }

    /**
     * Whether currently waiting for a reaction.
     */
    public boolean isWaiting() {
        return currentDelay > 0;
    }

    /**
     * Reset surprise flag (e.g., when entering combat after a period of peace).
     */
    public void resetSurprise() {
        surprised = true;
    }

    /**
     * Set personality-based reaction modifier.
     * @param modifier negative = faster reactions, positive = slower
     */
    public void setPersonalityModifier(int modifier) {
        this.personalityModifier = modifier;
    }

    public int getPersonalityModifier() {
        return personalityModifier;
    }

    public int getCurrentDelay() {
        return currentDelay;
    }
}
