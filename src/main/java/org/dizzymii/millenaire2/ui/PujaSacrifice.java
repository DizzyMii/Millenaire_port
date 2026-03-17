package org.dizzymii.millenaire2.ui;

/**
 * Defines puja sacrifice recipes and their effects.
 * Ported from org.millenaire.common.ui.PujaSacrifice (Forge 1.12.2).
 */
public class PujaSacrifice {

    public final String name;
    public final net.minecraft.world.item.Item requiredItem;
    public final int requiredCount;
    public final net.minecraft.world.effect.MobEffect effect;
    public final int effectDuration;
    public final int effectAmplifier;

    private static final java.util.List<PujaSacrifice> SACRIFICES = new java.util.ArrayList<>();

    public PujaSacrifice(String name, net.minecraft.world.item.Item requiredItem, int requiredCount,
                         net.minecraft.world.effect.MobEffect effect, int effectDuration, int effectAmplifier) {
        this.name = name;
        this.requiredItem = requiredItem;
        this.requiredCount = requiredCount;
        this.effect = effect;
        this.effectDuration = effectDuration;
        this.effectAmplifier = effectAmplifier;
    }

    public static void registerSacrifice(PujaSacrifice sacrifice) {
        SACRIFICES.add(sacrifice);
    }

    public static java.util.List<PujaSacrifice> getAllSacrifices() {
        return java.util.Collections.unmodifiableList(SACRIFICES);
    }

    /**
     * Check if the given item stack matches this sacrifice requirement.
     */
    public boolean matches(net.minecraft.world.item.ItemStack stack) {
        return stack.is(requiredItem) && stack.getCount() >= requiredCount;
    }

    /**
     * Apply the sacrifice effect to the player.
     */
    public void applyEffect(net.minecraft.world.entity.player.Player player) {
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect),
                effectDuration, effectAmplifier));
    }
}
