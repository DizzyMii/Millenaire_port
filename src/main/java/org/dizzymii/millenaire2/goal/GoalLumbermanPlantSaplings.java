package org.dizzymii.millenaire2.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Lumberman plants saplings on suitable ground near the village.
 */
public class GoalLumbermanPlantSaplings extends Goal {

    private static final int SEARCH_RADIUS = 16;

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
        if (!(v.level() instanceof ServerLevel serverLevel)) return true;

        BlockPos pos = v.blockPosition();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                BlockPos ground = pos.offset(dx, -1, dz);
                BlockPos above = pos.offset(dx, 0, dz);
                BlockState groundState = serverLevel.getBlockState(ground);
                boolean isDirt = groundState.is(Blocks.GRASS_BLOCK) || groundState.is(Blocks.DIRT);
                if (isDirt && serverLevel.getBlockState(above).isAir()) {
                    serverLevel.setBlockAndUpdate(above, Blocks.OAK_SAPLING.defaultBlockState());
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 20; }

    private BlockPos findPlantSpot(MillVillager v) {
        BlockPos center = v.blockPosition();
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx += 3) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz += 3) {
                BlockPos ground = center.offset(dx, -1, dz);
                BlockPos above = center.offset(dx, 0, dz);
                BlockState gs = v.level().getBlockState(ground);
                boolean isDirt = gs.is(Blocks.GRASS_BLOCK) || gs.is(Blocks.DIRT);
                if (isDirt && v.level().getBlockState(above).isAir()) {
                    return above;
                }
            }
        }
        return null;
    }
}
