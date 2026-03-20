package org.dizzymii.sblpoc.behaviour.survival;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.ai.world.BlockCategory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Smelts an item using a furnace. Navigates to furnace, inserts ore + fuel,
 * waits for smelting, collects output. Uses direct BlockEntity manipulation.
 */
public class SmeltItemBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final double REACH_DISTANCE_SQ = 4.5 * 4.5;
    private static final int SMELT_TICKS = 200; // 10 seconds vanilla smelting time

    @Nullable private Item inputItem;
    private int inputCount = 1;
    @Nullable private Item expectedOutput;
    @Nullable private BlockPos furnacePos;
    private boolean navigating = false;
    private int smeltTimer = 0;
    private boolean inserted = false;
    private boolean completed = false;

    public SmeltItemBehaviour() {
        noTimeout();
    }

    public SmeltItemBehaviour input(Item item, int count) {
        this.inputItem = item;
        this.inputCount = count;
        return this;
    }

    public SmeltItemBehaviour output(Item expected) {
        this.expectedOutput = expected;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        if (inputItem == null) return false;
        if (!npc.getInventoryModel().hasItem(inputItem, inputCount)) return false;

        // Find furnace
        furnacePos = npc.getSpatialMemory().findNearest(BlockCategory.FURNACE, npc.blockPosition());
        return furnacePos != null;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        inserted = false;
        completed = false;
        smeltTimer = 0;
        navigating = false;

        double distSq = npc.blockPosition().distSqr(furnacePos);
        if (distSq > REACH_DISTANCE_SQ) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(furnacePos), 1.0f, 1));
            navigating = true;
        }
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (completed || furnacePos == null) {
            doStop(level, npc, gameTime);
            return;
        }

        if (navigating) {
            double distSq = npc.blockPosition().distSqr(furnacePos);
            if (distSq <= REACH_DISTANCE_SQ) {
                navigating = false;
            } else {
                return;
            }
        }

        npc.getLookControl().setLookAt(furnacePos.getX() + 0.5, furnacePos.getY() + 0.5, furnacePos.getZ() + 0.5);

        if (!inserted) {
            // Insert items into furnace
            BlockEntity be = level.getBlockEntity(furnacePos);
            if (be instanceof AbstractFurnaceBlockEntity furnace) {
                // Insert input item (slot 0)
                int slot = npc.getInventoryModel().findSlot(s -> s.getItem() == inputItem);
                if (slot >= 0) {
                    ItemStack toInsert = npc.getInventory().removeItem(slot, inputCount);
                    furnace.setItem(0, toInsert);
                }

                // Insert fuel if none present (slot 1)
                if (furnace.getItem(1).isEmpty()) {
                    int fuelSlot = npc.getInventoryModel().findSlot(s ->
                            s.getItem() == Items.COAL || s.getItem() == Items.CHARCOAL
                                    || s.getItem() == Items.OAK_PLANKS || s.getItem() == Items.STICK);
                    if (fuelSlot >= 0) {
                        ItemStack fuel = npc.getInventory().removeItem(fuelSlot, 1);
                        furnace.setItem(1, fuel);
                    }
                }

                furnace.setChanged();
                npc.swing(InteractionHand.MAIN_HAND);
                inserted = true;
                npc.getInventoryModel().markDirty();
            } else {
                // Furnace gone
                doStop(level, npc, gameTime);
                return;
            }
        }

        smeltTimer++;

        // Check if smelting is complete
        if (smeltTimer >= SMELT_TICKS) {
            BlockEntity be = level.getBlockEntity(furnacePos);
            if (be instanceof AbstractFurnaceBlockEntity furnace) {
                // Collect output (slot 2)
                ItemStack output = furnace.getItem(2);
                if (!output.isEmpty()) {
                    // Try to add to inventory
                    for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
                        ItemStack existing = npc.getInventory().getItem(i);
                        if (existing.isEmpty()) {
                            npc.getInventory().setItem(i, output.copy());
                            furnace.setItem(2, ItemStack.EMPTY);
                            break;
                        } else if (ItemStack.isSameItemSameComponents(existing, output)
                                && existing.getCount() + output.getCount() <= existing.getMaxStackSize()) {
                            existing.grow(output.getCount());
                            furnace.setItem(2, ItemStack.EMPTY);
                            break;
                        }
                    }
                    furnace.setChanged();
                    npc.swing(InteractionHand.MAIN_HAND);
                    npc.getInventoryModel().markDirty();
                }
            }
            completed = true;
            doStop(level, npc, gameTime);
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return !completed && furnacePos != null;
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        inputItem = null;
        expectedOutput = null;
        furnacePos = null;
    }
}
