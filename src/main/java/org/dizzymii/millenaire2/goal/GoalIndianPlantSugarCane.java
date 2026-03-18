package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Indian villager plants sugar cane on dirt/sand blocks adjacent to water.
 */
public class GoalIndianPlantSugarCane extends Goal {

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos spot = findPlantSpot(v);
        if (spot != null) {
            return new GoalInformation(new Point(spot), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        BlockPos pos = v.blockPosition();
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                BlockPos check = pos.offset(dx, 0, dz);
                if (isValidPlantSpot(v, check)) {
                    v.level().setBlockAndUpdate(check, Blocks.SUGAR_CANE.defaultBlockState());
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 20; }

    private boolean isValidPlantSpot(MillVillager v, BlockPos pos) {
        if (!v.level().getBlockState(pos).isAir()) return false;
        BlockPos below = pos.below();
        if (!v.level().getBlockState(below).is(BlockTags.DIRT) && !v.level().getBlockState(below).is(Blocks.SAND)) {
            return false;
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (v.level().getBlockState(below.relative(dir)).is(Blocks.WATER)) {
                return true;
            }
        }
        return false;
    }

    private BlockPos findPlantSpot(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -10; dx <= 10; dx += 2) {
            for (int dz = -10; dz <= 10; dz += 2) {
                BlockPos check = center.offset(dx, 0, dz);
                if (isValidPlantSpot(v, check)) {
                    return check;
                }
            }
        }
        return null;
    }
}
