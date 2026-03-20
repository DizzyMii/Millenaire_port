package org.dizzymii.sblpoc.behaviour.survival;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.ai.world.BlockCategory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Navigates to an enchanting table and enchants a tool or armor piece.
 * Simplified simulation: consumes lapis lazuli and XP, applies a random enchantment.
 */
public class EnchantItemBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final double REACH_DISTANCE_SQ = 4.5 * 4.5;
    private static final int ENCHANT_TICKS = 40; // 2 seconds

    @Nullable private BlockPos tablePos;
    private boolean navigating = false;
    private int enchantTimer = 0;
    private boolean completed = false;

    public EnchantItemBehaviour() {
        noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        if (!npc.getInventoryModel().hasItem(Items.LAPIS_LAZULI, 3)) return false;
        if (!hasEnchantableItem(npc)) return false;

        tablePos = npc.getSpatialMemory().findNearest(BlockCategory.ENCHANTING_TABLE, npc.blockPosition());
        return tablePos != null;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        completed = false;
        enchantTimer = 0;
        navigating = false;

        if (tablePos == null) return;

        double distSq = npc.blockPosition().distSqr(tablePos);
        if (distSq > REACH_DISTANCE_SQ) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(tablePos), 1.0f, 1));
            navigating = true;
        }
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (completed || tablePos == null) {
            doStop(level, npc, gameTime);
            return;
        }

        if (navigating) {
            double distSq = npc.blockPosition().distSqr(tablePos);
            if (distSq <= REACH_DISTANCE_SQ) {
                navigating = false;
            } else {
                return;
            }
        }

        npc.getLookControl().setLookAt(tablePos.getX() + 0.5, tablePos.getY() + 0.5, tablePos.getZ() + 0.5);
        enchantTimer++;

        if (enchantTimer == 1) {
            npc.swing(InteractionHand.MAIN_HAND);
        }

        if (enchantTimer >= ENCHANT_TICKS) {
            // Consume lapis
            int lapisSlot = npc.getInventoryModel().findSlot(s -> s.getItem() == Items.LAPIS_LAZULI);
            if (lapisSlot >= 0) {
                npc.getInventory().removeItem(lapisSlot, 3);
            }

            // Enchant the item using vanilla enchanting logic
            int enchantSlot = findEnchantableSlot(npc);
            if (enchantSlot >= 0) {
                ItemStack toEnchant = npc.getInventory().getItem(enchantSlot);
                // Use vanilla helper to enchant at level 15 (mid-tier)
                ItemStack enchanted = EnchantmentHelper.enchantItem(
                        level.random, toEnchant, 15, level.registryAccess(),
                        java.util.Optional.empty());
                npc.getInventory().setItem(enchantSlot, enchanted);
            }

            npc.swing(InteractionHand.MAIN_HAND);
            npc.getInventoryModel().markDirty();
            completed = true;
            doStop(level, npc, gameTime);
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return !completed && tablePos != null;
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        tablePos = null;
    }

    private boolean hasEnchantableItem(PocNpc npc) {
        return findEnchantableSlot(npc) >= 0;
    }

    private int findEnchantableSlot(PocNpc npc) {
        for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
            ItemStack stack = npc.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.isEnchantable()) {
                return i;
            }
        }
        return -1;
    }
}
