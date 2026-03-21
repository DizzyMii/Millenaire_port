package org.dizzymii.millenaire2.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

/**
 * Lightweight village wall generator used during townhall creation.
 * Builds a simple perimeter ring to provide immediate defensive structure.
 */
public final class VillageWallGenerator {

    private static final int DEFAULT_WALL_RADIUS = 16;

    private VillageWallGenerator() {}

    public static void generateForTownhall(ServerLevel level, Building townhall) {
        if (townhall == null || !townhall.isTownhall) return;
        Point center = townhall.getPos();
        if (center == null) return;

        BlockState wall = Blocks.COBBLESTONE_WALL.defaultBlockState();

        int minX = center.x - DEFAULT_WALL_RADIUS;
        int maxX = center.x + DEFAULT_WALL_RADIUS;
        int minZ = center.z - DEFAULT_WALL_RADIUS;
        int maxZ = center.z + DEFAULT_WALL_RADIUS;

        for (int x = minX; x <= maxX; x++) {
            placeWallColumn(level, x, center.y, minZ, wall);
            placeWallColumn(level, x, center.y, maxZ, wall);
        }
        for (int z = minZ + 1; z < maxZ; z++) {
            placeWallColumn(level, minX, center.y, z, wall);
            placeWallColumn(level, maxX, center.y, z, wall);
        }
    }

    private static void placeWallColumn(ServerLevel level, int x, int startY, int z, BlockState wall) {
        BlockPos top = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                new BlockPos(x, startY, z));
        BlockPos placePos = top.below();
        if (placePos.getY() < level.getMinBuildHeight()) return;

        if (level.getBlockState(placePos).canBeReplaced()) {
            return;
        }

        BlockPos wallPos = placePos.above();
        if (level.getBlockState(wallPos).canBeReplaced()) {
            level.setBlock(wallPos, wall, 3);
        }
    }
}
