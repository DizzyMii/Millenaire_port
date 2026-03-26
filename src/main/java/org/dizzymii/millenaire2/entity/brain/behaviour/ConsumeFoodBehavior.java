package org.dizzymii.millenaire2.entity.brain.behaviour;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.dizzymii.millenaire2.entity.HumanoidNPC;
import org.dizzymii.millenaire2.entity.brain.ModMemoryTypes;
import org.dizzymii.millenaire2.entity.brain.smartbrain.ExtendedBehaviour;

import java.util.List;

/**
 * Survival behaviour that consumes the best edible inventory item when healing is needed
 * or hunger is low.
 */
public class ConsumeFoodBehavior extends ExtendedBehaviour<HumanoidNPC> {
    private static final float MAX_HEAL_FROM_CONSUMPTION = 4.0f;
    private static final float HEAL_PER_NUTRITION = 0.5f;

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, HumanoidNPC entity) {
        if (entity.isUsingItem()) return false;
        if (!shouldEat(entity)) return false;
        return findBestFoodIndex(entity.getCarriedInventory()) >= 0;
    }

    @Override
    protected void start(ServerLevel level, HumanoidNPC entity) {
        List<ItemStack> inventory = entity.getCarriedInventory();
        int bestIndex = findBestFoodIndex(inventory);
        if (bestIndex < 0) return;

        ItemStack previousMainHand = entity.getItemInHand(InteractionHand.MAIN_HAND).copy();
        ItemStack consumed = entity.removeCarriedInventorySlot(bestIndex);
        if (consumed.isEmpty()) return;
        FoodProperties food = consumed.get(DataComponents.FOOD);
        if (food == null) {
            entity.addToCarriedInventory(consumed);
            return;
        }

        entity.setItemInHand(InteractionHand.MAIN_HAND, consumed.copyWithCount(1));

        int nutrition = food.nutrition();
        entity.setNpcFoodLevel(entity.getNpcFoodLevel() + nutrition);
        if (entity.getHealth() < entity.getMaxHealth()) {
            entity.heal(Math.min(MAX_HEAL_FROM_CONSUMPTION, nutrition * HEAL_PER_NUTRITION));
        }

        consumed.shrink(1);
        if (!consumed.isEmpty()) {
            entity.addToCarriedInventory(consumed);
        }
        entity.setItemInHand(InteractionHand.MAIN_HAND, previousMainHand);
    }

    private boolean shouldEat(HumanoidNPC entity) {
        boolean needsHealing = entity.getBrain().getMemory(ModMemoryTypes.NEEDS_HEALING.get()).orElse(false);
        return needsHealing || entity.getNpcFoodLevel() <= HumanoidNPC.LOW_HUNGER_FOOD_LEVEL;
    }

    private int findBestFoodIndex(List<ItemStack> inventory) {
        int bestIndex = -1;
        int bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (stack.isEmpty()) continue;
            FoodProperties food = stack.get(DataComponents.FOOD);
            if (food == null) continue;
            int nutrition = food.nutrition();
            if (nutrition > bestScore) {
                bestScore = nutrition;
                bestIndex = i;
            }
        }
        return bestIndex;
    }
}
