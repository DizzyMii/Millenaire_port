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
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.ai.world.BlockCategory;
import org.dizzymii.sblpoc.ai.world.RecipeKB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Crafts an item by consuming ingredients from inventory.
 * If a crafting table is needed, navigates to one first.
 * Simulates crafting via direct inventory manipulation (mobs can't open GUIs).
 */
public class CraftItemBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final double REACH_DISTANCE_SQ = 4.5 * 4.5;
    private static final int CRAFT_TICKS = 15; // ~0.75 seconds to simulate crafting animation

    @Nullable private RecipeKB.SimpleRecipe recipe;
    @Nullable private BlockPos stationPos;
    private int craftTimer = 0;
    private boolean navigating = false;
    private boolean crafted = false;

    public CraftItemBehaviour() {
        noTimeout();
    }

    public CraftItemBehaviour recipe(RecipeKB.SimpleRecipe recipe) {
        this.recipe = recipe;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        if (recipe == null) return false;
        if (!recipe.canCraftWith(npc.getInventoryModel())) return false;

        // Check if we need a station
        if (recipe.requiredStation != null) {
            stationPos = npc.getSpatialMemory().findNearest(recipe.requiredStation, npc.blockPosition());
            return stationPos != null;
        }
        return true;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        crafted = false;
        craftTimer = 0;
        navigating = false;

        if (stationPos != null) {
            double distSq = npc.blockPosition().distSqr(stationPos);
            if (distSq > REACH_DISTANCE_SQ) {
                BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                        new WalkTarget(Vec3.atCenterOf(stationPos), 1.0f, 1));
                navigating = true;
            }
        }
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (crafted || recipe == null) {
            doStop(level, npc, gameTime);
            return;
        }

        if (navigating && stationPos != null) {
            double distSq = npc.blockPosition().distSqr(stationPos);
            if (distSq <= REACH_DISTANCE_SQ) {
                navigating = false;
            } else {
                return;
            }
        }

        // Look at crafting station or ahead
        if (stationPos != null) {
            npc.getLookControl().setLookAt(stationPos.getX() + 0.5, stationPos.getY() + 0.5, stationPos.getZ() + 0.5);
        }

        craftTimer++;

        // Swing arm for visual feedback at start
        if (craftTimer == 1) {
            npc.swing(InteractionHand.MAIN_HAND);
        }

        if (craftTimer >= CRAFT_TICKS) {
            // Consume inputs
            Map<Item, Integer> toConsume = new HashMap<>();
            for (ItemStack input : recipe.inputs) {
                toConsume.merge(input.getItem(), input.getCount(), Integer::sum);
            }

            for (Map.Entry<Item, Integer> entry : toConsume.entrySet()) {
                int remaining = entry.getValue();
                for (int i = 0; i < npc.getInventory().getContainerSize() && remaining > 0; i++) {
                    ItemStack stack = npc.getInventory().getItem(i);
                    if (stack.getItem() == entry.getKey()) {
                        int take = Math.min(remaining, stack.getCount());
                        npc.getInventory().removeItem(i, take);
                        remaining -= take;
                    }
                }
            }

            // Add output
            ItemStack output = recipe.output.copy();
            for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
                ItemStack slot = npc.getInventory().getItem(i);
                if (slot.isEmpty()) {
                    npc.getInventory().setItem(i, output);
                    output = ItemStack.EMPTY;
                    break;
                } else if (ItemStack.isSameItemSameComponents(slot, output)
                        && slot.getCount() + output.getCount() <= slot.getMaxStackSize()) {
                    slot.grow(output.getCount());
                    output = ItemStack.EMPTY;
                    break;
                }
            }

            // If inventory full, drop on ground
            if (!output.isEmpty()) {
                npc.spawnAtLocation(output);
            }

            npc.swing(InteractionHand.MAIN_HAND);
            npc.getInventoryModel().markDirty();
            crafted = true;
            doStop(level, npc, gameTime);
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return !crafted && recipe != null;
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        recipe = null;
        stationPos = null;
    }
}
