package org.dizzymii.sblpoc.behaviour;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;

import java.util.List;

/**
 * Eats food from inventory when health is low and it's safe to do so.
 * 
 * Conditions:
 * - HP < 60% of max
 * - Has food in inventory
 * - Not on food cooldown (200 ticks between eating)
 * - NOT currently using shield
 * - Target is > 6 blocks away OR no target (safe to eat)
 */
public class EatFoodBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final int EAT_DURATION = 32; // ticks to eat
    private static final int FOOD_COOLDOWN_TICKS = 200;
    private static final double SAFE_DISTANCE_SQ = 36.0; // 6 blocks squared

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.REGISTERED)
            );

    private int foodSlot = -1;
    private int eatTimer = 0;

    public EatFoodBehaviour() {
        runFor(entity -> EAT_DURATION + 5); // Small buffer
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        // Must be below 60% HP
        float hpPercent = npc.getHealth() / npc.getMaxHealth();
        if (hpPercent >= 0.6f) return false;

        // Must not be on cooldown
        Long cooldownEnd = BrainUtils.getMemory(npc, SblPocSetup.FOOD_COOLDOWN.get());
        if (cooldownEnd != null && level.getGameTime() < cooldownEnd) return false;

        // Must not be using shield
        if (npc.isUsingItem()) return false;

        // Must be safe — target far away or absent
        LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
        if (target != null && target.isAlive()) {
            double distSq = npc.distanceToSqr(target);
            if (distSq < SAFE_DISTANCE_SQ) return false;
        }

        // Must have food
        foodSlot = findBestFoodSlot(npc);
        return foodSlot >= 0;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        if (foodSlot < 0) return;

        ItemStack foodStack = npc.getInventory().getItem(foodSlot);
        // Move food to main hand temporarily for eating animation
        ItemStack currentMainHand = npc.getMainHandItem().copy();
        npc.setItemInHand(InteractionHand.MAIN_HAND, foodStack.split(1));

        // Store the original main hand item back in inventory
        if (!currentMainHand.isEmpty()) {
            npc.getInventory().setItem(foodSlot, currentMainHand);
        }

        npc.startUsingItem(InteractionHand.MAIN_HAND);
        eatTimer = 0;
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return npc.isAlive() && npc.isUsingItem() && eatTimer < EAT_DURATION;
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        eatTimer++;

        if (eatTimer >= EAT_DURATION) {
            // Finish eating — apply food heal
            ItemStack eating = npc.getMainHandItem();
            FoodProperties food = eating.getFoodProperties(npc);
            if (food != null) {
                float healAmount = food.nutrition() * 0.5f; // Heal based on nutrition
                npc.heal(healAmount);
            }

            // Remove the food item
            eating.shrink(1);
            npc.stopUsingItem();

            // Restore sword to main hand
            restoreSword(npc);
        }
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        npc.stopUsingItem();
        restoreSword(npc);

        // Set food cooldown
        BrainUtils.setMemory(npc, SblPocSetup.FOOD_COOLDOWN.get(), gameTime + FOOD_COOLDOWN_TICKS);

        foodSlot = -1;
        eatTimer = 0;
    }

    private int findBestFoodSlot(PocNpc npc) {
        int bestSlot = -1;
        int bestNutrition = 0;

        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            ItemStack stack = npc.getInventory().getItem(i);
            if (stack.isEmpty()) continue;

            FoodProperties food = stack.getFoodProperties(npc);
            if (food != null && food.nutrition() > bestNutrition) {
                bestNutrition = food.nutrition();
                bestSlot = i;
            }
        }
        return bestSlot;
    }

    private void restoreSword(PocNpc npc) {
        // Check if main hand already has a sword
        ItemStack mainHand = npc.getMainHandItem();
        if (mainHand.isEmpty() || mainHand.getFoodProperties(npc) != null) {
            // Find sword in inventory and restore it
            for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
                ItemStack stack = npc.getInventory().getItem(i);
                if (stack.getItem() instanceof net.minecraft.world.item.SwordItem) {
                    npc.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
                    npc.getInventory().setItem(i, mainHand.copy());
                    break;
                }
            }
        }
    }
}
