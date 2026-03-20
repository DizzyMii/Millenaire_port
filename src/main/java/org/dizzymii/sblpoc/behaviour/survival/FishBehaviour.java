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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.ai.world.BlockCategory;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Finds water, equips fishing rod, simulates fishing for a random duration,
 * then adds a fish to inventory. Simplified simulation since mobs can't
 * truly use fishing rods.
 */
public class FishBehaviour extends ExtendedBehaviour<PocNpc> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
            );

    private static final double REACH_DISTANCE_SQ = 4.0 * 4.0;
    private static final int MIN_FISH_TIME = 100; // 5 seconds min
    private static final int MAX_FISH_TIME = 400; // 20 seconds max

    @Nullable private BlockPos waterPos;
    private int fishTimer = 0;
    private int fishDuration = 0;
    private boolean navigating = false;
    private boolean fishing = false;
    private boolean caught = false;
    private int fishCount = 0;
    private static final int MAX_FISH = 5;

    public FishBehaviour() {
        noTimeout();
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, PocNpc npc) {
        if (!npc.getInventoryModel().hasItem(Items.FISHING_ROD)) return false;

        waterPos = npc.getSpatialMemory().findNearest(BlockCategory.WATER, npc.blockPosition());
        if (waterPos == null) {
            waterPos = findNearbyWater(level, npc.blockPosition(), 16);
        }
        return waterPos != null;
    }

    @Override
    protected void start(ServerLevel level, PocNpc npc, long gameTime) {
        fishing = false;
        caught = false;
        fishCount = 0;
        navigating = false;

        if (waterPos == null) return;

        double distSq = npc.blockPosition().distSqr(waterPos);
        if (distSq > REACH_DISTANCE_SQ) {
            BrainUtils.setMemory(npc, MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(waterPos), 1.0f, 2));
            navigating = true;
        } else {
            startFishing(npc);
        }
    }

    @Override
    protected void tick(ServerLevel level, PocNpc npc, long gameTime) {
        if (waterPos == null) {
            doStop(level, npc, gameTime);
            return;
        }

        if (navigating) {
            double distSq = npc.blockPosition().distSqr(waterPos);
            if (distSq <= REACH_DISTANCE_SQ) {
                navigating = false;
                startFishing(npc);
            }
            return;
        }

        if (fishing) {
            npc.getLookControl().setLookAt(waterPos.getX() + 0.5, waterPos.getY() + 0.5, waterPos.getZ() + 0.5);
            fishTimer++;

            if (fishTimer >= fishDuration) {
                // Caught a fish!
                npc.swing(InteractionHand.MAIN_HAND);

                // Add random fish to inventory
                ItemStack fish = new ItemStack(
                        npc.getRandom().nextFloat() < 0.7f ? Items.COD : Items.SALMON);
                for (int i = 0; i < npc.getInventory().getContainerSize(); i++) {
                    ItemStack slot = npc.getInventory().getItem(i);
                    if (slot.isEmpty()) {
                        npc.getInventory().setItem(i, fish);
                        fish = ItemStack.EMPTY;
                        break;
                    } else if (ItemStack.isSameItemSameComponents(slot, fish)
                            && slot.getCount() < slot.getMaxStackSize()) {
                        slot.grow(1);
                        fish = ItemStack.EMPTY;
                        break;
                    }
                }
                if (!fish.isEmpty()) {
                    npc.spawnAtLocation(fish);
                }

                npc.getInventoryModel().markDirty();
                fishCount++;

                if (fishCount >= MAX_FISH) {
                    doStop(level, npc, gameTime);
                } else {
                    // Cast again
                    fishTimer = 0;
                    fishDuration = MIN_FISH_TIME + npc.getRandom().nextInt(MAX_FISH_TIME - MIN_FISH_TIME);
                }
            }
        }
    }

    @Override
    protected boolean shouldKeepRunning(PocNpc npc) {
        return fishing && fishCount < MAX_FISH && npc.isAlive();
    }

    @Override
    protected void stop(ServerLevel level, PocNpc npc, long gameTime) {
        waterPos = null;
        fishing = false;
    }

    private void startFishing(PocNpc npc) {
        // Equip fishing rod
        int rodSlot = npc.getInventoryModel().findSlot(s -> s.getItem() == Items.FISHING_ROD);
        if (rodSlot >= 0) {
            ItemStack current = npc.getMainHandItem();
            npc.setItemInHand(InteractionHand.MAIN_HAND, npc.getInventory().getItem(rodSlot));
            npc.getInventory().setItem(rodSlot, current);
        }

        fishing = true;
        fishTimer = 0;
        fishDuration = MIN_FISH_TIME + npc.getRandom().nextInt(MAX_FISH_TIME - MIN_FISH_TIME);
        npc.swing(InteractionHand.MAIN_HAND); // Cast animation
    }

    @Nullable
    private static BlockPos findNearbyWater(ServerLevel level, BlockPos center, int radius) {
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        double bestDist = Double.MAX_VALUE;
        BlockPos best = null;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -radius; z <= radius; z++) {
                    mpos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    if (level.getBlockState(mpos).is(Blocks.WATER)) {
                        double dist = center.distSqr(mpos);
                        if (dist < bestDist) {
                            bestDist = dist;
                            best = mpos.immutable();
                        }
                    }
                }
            }
        }
        return best;
    }
}
