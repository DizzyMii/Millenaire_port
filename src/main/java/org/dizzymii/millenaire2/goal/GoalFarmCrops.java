package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Generic crop farming: finds farmland near the village, harvests mature crops,
 * and replants seeds. Handles wheat, carrots, potatoes, and beetroot.
 */
public class GoalFarmCrops extends Goal {

    { this.tags.add(TAG_AGRICULTURE); }

    private static final int SEARCH_RADIUS = 16;
    private static final int HARVEST_RADIUS = 3;

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos target = findMatureCrop(v, SEARCH_RADIUS);
        if (target != null) {
            return new GoalInformation(new Point(target), 2);
        }
        // If no mature crops, look for empty farmland to plant
        BlockPos farmland = findEmptyFarmland(v, SEARCH_RADIUS);
        if (farmland != null) {
            return new GoalInformation(new Point(farmland), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        if (!(v.level() instanceof ServerLevel serverLevel)) return true;

        // First try to harvest a nearby mature crop
        BlockPos mature = findMatureCrop(v, HARVEST_RADIUS);
        if (mature != null) {
            harvestCrop(v, serverLevel, mature);
            return false; // May have more to do
        }

        // Then try to plant on nearby empty farmland
        BlockPos farmland = findEmptyFarmland(v, HARVEST_RADIUS);
        if (farmland != null) {
            plantCrop(v, serverLevel, farmland);
            return false;
        }

        // Nothing within arm's reach
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 20; }

    private void harvestCrop(MillVillager v, ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        // Break the crop block, drops items naturally
        level.destroyBlock(pos, true, v);

        // Collect nearby drops
        collectDrops(v, pos);

        // Replant if we have seeds
        BlockPos below = pos.below();
        if (level.getBlockState(below).getBlock() instanceof FarmBlock) {
            BlockState replant = getSeedState(block);
            if (replant != null) {
                level.setBlock(pos, replant, 3);
            }
        }
    }

    private void plantCrop(MillVillager v, ServerLevel level, BlockPos farmlandPos) {
        BlockPos above = farmlandPos.above();
        if (!level.getBlockState(above).isAir()) return;

        // Plant wheat seeds by default (most common)
        level.setBlock(above, Blocks.WHEAT.defaultBlockState(), 3);
    }

    private void collectDrops(MillVillager v, BlockPos pos) {
        AABB area = new AABB(pos).inflate(2.0);
        List<ItemEntity> drops = v.level().getEntitiesOfClass(ItemEntity.class, area);
        for (ItemEntity drop : drops) {
            ItemStack stack = drop.getItem();
            if (!stack.isEmpty()) {
                int remaining = v.inventory.add(stack);
                if (remaining <= 0) {
                    drop.discard();
                } else {
                    drop.setItem(stack.copyWithCount(remaining));
                }
            }
        }
    }

    @Nullable
    private BlockPos findMatureCrop(MillVillager v, int radius) {
        BlockPos center = v.blockPosition();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos check = center.offset(dx, dy, dz);
                    BlockState state = v.level().getBlockState(check);
                    if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
                        double d = center.distSqr(check);
                        if (d < bestDist) {
                            bestDist = d;
                            best = check;
                        }
                    }
                }
            }
        }
        return best;
    }

    @Nullable
    private BlockPos findEmptyFarmland(MillVillager v, int radius) {
        BlockPos center = v.blockPosition();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).getBlock() instanceof FarmBlock) {
                        BlockPos above = check.above();
                        if (v.level().getBlockState(above).isAir()) {
                            return check;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private BlockState getSeedState(Block harvested) {
        if (harvested == Blocks.WHEAT) return Blocks.WHEAT.defaultBlockState();
        if (harvested == Blocks.CARROTS) return Blocks.CARROTS.defaultBlockState();
        if (harvested == Blocks.POTATOES) return Blocks.POTATOES.defaultBlockState();
        if (harvested == Blocks.BEETROOTS) return Blocks.BEETROOTS.defaultBlockState();
        return null;
    }
}
