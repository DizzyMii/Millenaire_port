package org.dizzymii.sblpoc.behaviour;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;

import java.util.List;

/**
 * Drinks potions from inventory when conditions are met.
 *
 * - Healing potion: When HP < 50% and not in immediate melee danger
 * - Strength potion: When engaging multiple enemies or a tough fight
 *
 * Saves the main hand weapon, equips potion, drinks for 32 ticks, restores weapon.
 * Cooldown prevents spam.
 */
public class DrinkPotionBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final int DRINK_TICKS = 32;

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.REGISTERED)
            );

    private int drinkTimer = 0;
    private ItemStack savedMainHand = ItemStack.EMPTY;
    private int potionSlot = -1;
    private long lastDrinkTime = 0;

    public DrinkPotionBehaviour() {
        runFor(entity -> DRINK_TICKS + 10);
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        // Don't drink if already using an item
        if (npc.isUsingItem()) return false;

        // Cooldown — 10 seconds between potions
        if (level.getGameTime() - lastDrinkTime < 200) return false;

        // Check if we have an appropriate potion and the conditions warrant drinking it
        float hpPercent = npc.getHealth() / npc.getMaxHealth();

        // Healing: HP < 50% and no enemy within 3 blocks (safe to drink)
        if (hpPercent < 0.5f) {
            LivingEntity target = BrainUtils.getMemory(npc, MemoryModuleType.ATTACK_TARGET);
            double nearestEnemyDistSq = target != null ? npc.distanceToSqr(target) : Double.MAX_VALUE;
            if (nearestEnemyDistSq > 9.0) { // 3 blocks
                int slot = findHealingPotion(npc);
                if (slot >= 0) {
                    potionSlot = slot;
                    return true;
                }
            }
        }

        // Strength: When fighting 2+ enemies and not already buffed
        Integer hostileCount = BrainUtils.getMemory(npc, SblPocSetup.NEARBY_HOSTILE_COUNT.get());
        if (hostileCount != null && hostileCount >= 2 && !npc.hasEffect(net.minecraft.world.effect.MobEffects.DAMAGE_BOOST)) {
            int slot = findStrengthPotion(npc);
            if (slot >= 0) {
                potionSlot = slot;
                return true;
            }
        }

        return false;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        drinkTimer = DRINK_TICKS;

        // Save current main hand and equip the potion
        savedMainHand = npc.getMainHandItem().copy();
        ItemStack potion = npc.getInventory().getItem(potionSlot).copy();
        npc.setItemInHand(InteractionHand.MAIN_HAND, potion);
        npc.getInventory().setItem(potionSlot, savedMainHand);

        // Start drinking
        npc.startUsingItem(InteractionHand.MAIN_HAND);
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return drinkTimer > 0 && npc.isUsingItem();
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        drinkTimer--;
        // Don't call stopUsingItem() here — let vanilla's LivingEntity.tick()
        // handle completeUsingItem() which actually applies the potion effect.
        // When the item use finishes naturally, isUsingItem() becomes false,
        // shouldKeepRunning() returns false, and the behaviour stops cleanly.
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        npc.stopUsingItem();

        ItemStack currentMain = npc.getMainHandItem();

        if (currentMain.is(Items.GLASS_BOTTLE)) {
            // Potion was consumed normally — stash the bottle
            boolean stashed = false;
            for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
                if (npc.getInventory().getItem(i).isEmpty()) {
                    npc.getInventory().setItem(i, currentMain.copy());
                    stashed = true;
                    break;
                }
            }
            if (!stashed) {
                npc.spawnAtLocation(currentMain);
            }
        } else if (currentMain.is(Items.POTION)) {
            // Drink was interrupted — put unconsumed potion back in inventory
            for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
                if (npc.getInventory().getItem(i).isEmpty()) {
                    npc.getInventory().setItem(i, currentMain.copy());
                    break;
                }
            }
        }

        // Restore original weapon from inventory
        boolean restored = false;
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            ItemStack stack = npc.getInventory().getItem(i);
            if (ItemStack.isSameItemSameComponents(stack, savedMainHand)) {
                npc.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
                npc.getInventory().setItem(i, ItemStack.EMPTY);
                restored = true;
                break;
            }
        }
        // Fallback: if savedMainHand wasn't found (e.g., inventory full), clear hand
        if (!restored && !savedMainHand.isEmpty()) {
            npc.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }

        savedMainHand = ItemStack.EMPTY;
        potionSlot = -1;
        lastDrinkTime = gameTime;
        drinkTimer = 0;
    }

    private int findHealingPotion(PocNpc npc) {
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            ItemStack stack = npc.getInventory().getItem(i);
            if (stack.is(Items.POTION)) {
                PotionContents contents = stack.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
                if (contents != null && contents.is(Potions.HEALING)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int findStrengthPotion(PocNpc npc) {
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            ItemStack stack = npc.getInventory().getItem(i);
            if (stack.is(Items.POTION)) {
                PotionContents contents = stack.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
                if (contents != null && contents.is(Potions.STRENGTH)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
