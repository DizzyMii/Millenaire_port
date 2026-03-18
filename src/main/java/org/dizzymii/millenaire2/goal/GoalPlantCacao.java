package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Mayan villager plants cocoa beans on jungle log blocks.
 */
public class GoalPlantCacao extends Goal {

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
            for (int dy = -1; dy <= 3; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos check = pos.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).isAir() && hasAdjacentJungleLog(v, check)) {
                        v.level().setBlockAndUpdate(check, Blocks.COCOA.defaultBlockState());
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 20; }

    private boolean hasAdjacentJungleLog(MillVillager v, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (v.level().getBlockState(pos.relative(dir)).is(Blocks.JUNGLE_LOG)) {
                return true;
            }
        }
        return false;
    }

    private BlockPos findPlantSpot(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -10; dx <= 10; dx += 2) {
            for (int dy = 0; dy <= 5; dy++) {
                for (int dz = -10; dz <= 10; dz += 2) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).isAir() && hasAdjacentJungleLog(v, check)) {
                        return check;
                    }
                }
            }
        }
        return null;
    }
}
