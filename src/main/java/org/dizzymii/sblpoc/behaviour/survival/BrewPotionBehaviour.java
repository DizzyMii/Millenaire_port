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
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.ai.world.BlockCategory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Navigates to a brewing stand and brews a potion.
 * Inserts water bottles + ingredient + blaze powder, waits for brewing, collects result.
 */
public class BrewPotionBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final double REACH_DISTANCE_SQ = 4.5 * 4.5;
    private static final int BREW_TICKS = 400; // 20 seconds vanilla brewing time

    @Nullable private BlockPos standPos;
    @Nullable private Item ingredient;
    private boolean navigating = false;
    private boolean inserted = false;
    private int brewTimer = 0;
    private boolean completed = false;

    public BrewPotionBehaviour() {
        noTimeout();
    }

    public BrewPotionBehaviour ingredient(Item ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        if (ingredient == null) return false;
        if (!npc.getInventoryModel().hasItem(ingredient)) return false;
        if (!npc.getInventoryModel().hasItem(Items.BLAZE_POWDER)) return false;
        if (!npc.getInventoryModel().hasItem(Items.GLASS_BOTTLE)) return false;

        standPos = npc.getSpatialMemory().findNearest(BlockCategory.BREWING_STAND, npc.blockPosition());
        return standPos != null;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        inserted = false;
        completed = false;
        brewTimer = 0;
        navigating = false;

        if (standPos == null) return;

        double distSq = npc.blockPosition().distSqr(standPos);
        if (distSq > REACH_DISTANCE_SQ) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(standPos), 1.0f, 1));
            navigating = true;
        }
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (completed || standPos == null) {
            doStop(level, npc, gameTime);
            return;
        }

        if (navigating) {
            double distSq = npc.blockPosition().distSqr(standPos);
            if (distSq <= REACH_DISTANCE_SQ) {
                navigating = false;
            } else {
                return;
            }
        }

        npc.getLookControl().setLookAt(standPos.getX() + 0.5, standPos.getY() + 0.5, standPos.getZ() + 0.5);

        if (!inserted) {
            BlockEntity be = level.getBlockEntity(standPos);
            if (be instanceof BrewingStandBlockEntity stand) {
                // Insert water bottles in slots 0-2
                for (int i = 0; i < 3; i++) {
                    if (stand.getItem(i).isEmpty()) {
                        int bottleSlot = npc.getInventoryModel().findSlot(s -> s.getItem() == Items.GLASS_BOTTLE);
                        if (bottleSlot >= 0) {
                            ItemStack bottle = npc.getInventory().removeItem(bottleSlot, 1);
                            // Convert glass bottle to water bottle
                            ItemStack waterBottle = PotionContents.createItemStack(Items.POTION, Potions.WATER);
                            stand.setItem(i, waterBottle);
                        }
                    }
                }

                // Insert ingredient in slot 3
                if (ingredient != null) {
                    int ingSlot = npc.getInventoryModel().findSlot(s -> s.getItem() == ingredient);
                    if (ingSlot >= 0) {
                        ItemStack ing = npc.getInventory().removeItem(ingSlot, 1);
                        stand.setItem(3, ing);
                    }
                }

                // Insert blaze powder in slot 4
                int powderSlot = npc.getInventoryModel().findSlot(s -> s.getItem() == Items.BLAZE_POWDER);
                if (powderSlot >= 0) {
                    ItemStack powder = npc.getInventory().removeItem(powderSlot, 1);
                    stand.setItem(4, powder);
                }

                stand.setChanged();
                npc.swing(InteractionHand.MAIN_HAND);
                npc.getInventoryModel().markDirty();
                inserted = true;
            } else {
                doStop(level, npc, gameTime);
                return;
            }
        }

        brewTimer++;

        if (brewTimer >= BREW_TICKS) {
            BlockEntity be = level.getBlockEntity(standPos);
            if (be instanceof BrewingStandBlockEntity stand) {
                // Collect brewed potions from slots 0-2
                for (int i = 0; i < 3; i++) {
                    ItemStack potion = stand.getItem(i);
                    if (!potion.isEmpty()) {
                        for (int j = 0; j < npc.getInventory().getContainerSize(); j++) {
                            ItemStack slot = npc.getInventory().getItem(j);
                            if (slot.isEmpty()) {
                                npc.getInventory().setItem(j, potion.copy());
                                stand.setItem(i, ItemStack.EMPTY);
                                break;
                            }
                        }
                    }
                }
                stand.setChanged();
                npc.swing(InteractionHand.MAIN_HAND);
                npc.getInventoryModel().markDirty();
            }
            completed = true;
            doStop(level, npc, gameTime);
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return !completed && standPos != null;
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        standPos = null;
        ingredient = null;
    }
}
