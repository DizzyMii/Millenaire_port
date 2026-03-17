package org.dizzymii.millenaire2.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * Custom tool tiers for Millénaire cultures.
 * Ported from the original EnumHelper.addToolMaterial calls in MillItems.
 *
 * Parameters: harvestLevel, maxUses, efficiency, attackDamage, enchantability
 * Original values:
 *   norman:    level=2, uses=1561, efficiency=10.0, damage=4.0, enchant=10
 *   betterSteel: level=2, uses=1561, efficiency=5.0, damage=3.0, enchant=10
 *   byzantine: level=2, uses=1561, efficiency=12.0, damage=3.0, enchant=15
 *   obsidian:  level=3, uses=1561, efficiency=6.0, damage=2.0, enchant=25
 */
public enum MillTiers implements Tier {
    NORMAN(BlockTags.INCORRECT_FOR_IRON_TOOL, 1561, 10.0f, 4.0f, 10, () -> Ingredient.EMPTY),
    BETTER_STEEL(BlockTags.INCORRECT_FOR_IRON_TOOL, 1561, 5.0f, 3.0f, 10, () -> Ingredient.EMPTY),
    BYZANTINE(BlockTags.INCORRECT_FOR_IRON_TOOL, 1561, 12.0f, 3.0f, 15, () -> Ingredient.EMPTY),
    OBSIDIAN(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1561, 6.0f, 2.0f, 25, () -> Ingredient.EMPTY);

    private final TagKey<Block> incorrectBlocksForDrops;
    private final int uses;
    private final float speed;
    private final float attackDamageBonus;
    private final int enchantmentValue;
    private final Supplier<Ingredient> repairIngredient;

    MillTiers(TagKey<Block> incorrectBlocksForDrops, int uses, float speed, float attackDamageBonus,
              int enchantmentValue, Supplier<Ingredient> repairIngredient) {
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getUses() { return uses; }

    @Override
    public float getSpeed() { return speed; }

    @Override
    public float getAttackDamageBonus() { return attackDamageBonus; }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() { return incorrectBlocksForDrops; }

    @Override
    public int getEnchantmentValue() { return enchantmentValue; }

    @Override
    public Ingredient getRepairIngredient() { return repairIngredient.get(); }
}
