package org.dizzymii.sblpoc.personality;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

/**
 * Defines an NPC's personality as a set of trait axes.
 * Each trait is a float in [-1.0, 1.0] representing a spectrum.
 *
 * Traits affect:
 * - GOAP utility scores (what goals feel important)
 * - Reaction speed (via ReactionTimer modifier)
 * - Combat style preferences
 * - Risk tolerance for exploration/mining
 * - Social behaviour (trading eagerness)
 *
 * Generated randomly at NPC spawn and persisted via NBT.
 */
public class PersonalityProfile {

    // Trait axes: each [-1.0, 1.0]
    private float bravery;       // -1 = cowardly (flee early), +1 = fearless (fight to death)
    private float aggression;    // -1 = pacifist (avoid combat), +1 = aggressive (seek fights)
    private float industriousness; // -1 = lazy (idle often), +1 = workaholic (always busy)
    private float curiosity;     // -1 = homebody (stay near base), +1 = explorer (roam far)
    private float sociability;   // -1 = loner, +1 = social (trades eagerly, seeks allies)
    private float caution;       // -1 = reckless (ignores danger), +1 = cautious (avoid hazards)

    public PersonalityProfile() {
        // Default: neutral on all axes
    }

    /**
     * Generate a random personality.
     */
    public static PersonalityProfile randomize(RandomSource random) {
        PersonalityProfile p = new PersonalityProfile();
        p.bravery = randomTrait(random);
        p.aggression = randomTrait(random);
        p.industriousness = randomTrait(random);
        p.curiosity = randomTrait(random);
        p.sociability = randomTrait(random);
        p.caution = randomTrait(random);
        return p;
    }

    /**
     * Generate a trait value with a normal-ish distribution centered at 0.
     * Most NPCs will be moderate; extreme values are rarer.
     */
    private static float randomTrait(RandomSource random) {
        // Average of two uniform randoms → roughly bell-shaped
        float a = random.nextFloat() * 2.0f - 1.0f;
        float b = random.nextFloat() * 2.0f - 1.0f;
        return (a + b) * 0.5f;
    }

    // ========== Derived Modifiers ==========

    /**
     * How much this NPC prefers combat goals. Range: [0.5, 1.5]
     */
    public float getCombatWeight() {
        return 1.0f + aggression * 0.3f + bravery * 0.2f;
    }

    /**
     * How much this NPC values gathering/building. Range: [0.5, 1.5]
     */
    public float getWorkWeight() {
        return 1.0f + industriousness * 0.4f;
    }

    /**
     * How much this NPC wants to explore. Range: [0.5, 1.5]
     */
    public float getExplorationWeight() {
        return 1.0f + curiosity * 0.4f;
    }

    /**
     * Health percentage at which the NPC considers fleeing.
     * Cowardly NPCs flee at 50% HP; brave ones at 15%.
     */
    public float getFleeHealthThreshold() {
        return 0.3f - bravery * 0.15f;
    }

    /**
     * Modifier for ReactionTimer: cautious = slower, aggressive = faster.
     */
    public int getReactionModifier() {
        return Math.round(caution * 2.0f - aggression * 1.0f);
    }

    /**
     * How eagerly this NPC trades. Range: [0.5, 1.5]
     */
    public float getTradeEagerness() {
        return 1.0f + sociability * 0.4f;
    }

    /**
     * Whether this NPC avoids dangerous areas (lava, cliffs).
     * Cautious NPCs have higher avoidance radius.
     */
    public float getDangerAvoidanceMultiplier() {
        return 1.0f + caution * 0.5f;
    }

    // ========== NBT ==========

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Bravery", bravery);
        tag.putFloat("Aggression", aggression);
        tag.putFloat("Industriousness", industriousness);
        tag.putFloat("Curiosity", curiosity);
        tag.putFloat("Sociability", sociability);
        tag.putFloat("Caution", caution);
        return tag;
    }

    public void load(CompoundTag tag) {
        bravery = tag.getFloat("Bravery");
        aggression = tag.getFloat("Aggression");
        industriousness = tag.getFloat("Industriousness");
        curiosity = tag.getFloat("Curiosity");
        sociability = tag.getFloat("Sociability");
        caution = tag.getFloat("Caution");
    }

    // ========== Getters ==========

    public float getBravery() { return bravery; }
    public float getAggression() { return aggression; }
    public float getIndustriousness() { return industriousness; }
    public float getCuriosity() { return curiosity; }
    public float getSociability() { return sociability; }
    public float getCaution() { return caution; }

    @Override
    public String toString() {
        return String.format("Personality[brave=%.2f aggr=%.2f work=%.2f curious=%.2f social=%.2f cautious=%.2f]",
                bravery, aggression, industriousness, curiosity, sociability, caution);
    }
}
