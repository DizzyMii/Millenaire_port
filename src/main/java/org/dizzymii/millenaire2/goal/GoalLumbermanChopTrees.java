package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Lumberman searches for nearby trees, walks to them, and chops logs.
 */
public class GoalLumbermanChopTrees extends Goal {

    private static final int SEARCH_RADIUS = 24;

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        BlockPos tree = findNearestLog(v);
        if (tree != null) {
            return new GoalInformation(new Point(tree), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        if (!(v.level() instanceof ServerLevel serverLevel)) return true;

        BlockPos pos = v.blockPosition();
        // Search for log block near current position
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -1; dy <= 3; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    BlockPos check = pos.offset(dx, dy, dz);
                    BlockState state = serverLevel.getBlockState(check);
                    if (state.is(BlockTags.LOGS)) {
                        serverLevel.destroyBlock(check, true, v);
                        return false; // Keep chopping
                    }
                }
            }
        }
        return true; // No more logs nearby
    }

    @Override
    public int actionDuration(MillVillager v) { return 15; }

    private BlockPos findNearestLog(MillVillager v) {
        BlockPos center = v.blockPosition();
        BlockPos nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx += 2) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz += 2) {
                for (int dy = -2; dy <= 8; dy++) {
                    BlockPos check = center.offset(dx, dy, dz);
                    if (v.level().getBlockState(check).is(BlockTags.LOGS)) {
                        double dist = center.distSqr(check);
                        if (dist < nearestDist) {
                            nearestDist = dist;
                            nearest = check;
                        }
                        break; // Found log at this column, skip higher
                    }
                }
            }
        }
        return nearest;
    }
}
