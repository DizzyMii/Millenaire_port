package org.dizzymii.millenaire2.pathing.atomicstryker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.pathing.PathingPathCalcTile;

/**
 * Generates a 3D region map for pathfinding around a given point.
 * Scans world blocks to classify each position as walkable, ladder, or empty.
 * Ported from org.millenaire.common.pathing.atomicstryker.RegionMapper (Forge 1.12.2).
 */
public final class RegionMapper {

    private RegionMapper() {}

    /**
     * Generate a 3D tile region centred on (centreX, centreY, centreZ).
     * Array indices: [x offset + radiusH][y offset + radiusV][z offset + radiusH]
     */
    public static PathingPathCalcTile[][][] generateRegion(Level level, int centreX, int centreY, int centreZ,
                                                            int radiusH, int radiusV) {
        int sizeX = radiusH * 2 + 1;
        int sizeY = radiusV * 2 + 1;
        int sizeZ = radiusH * 2 + 1;
        PathingPathCalcTile[][][] region = new PathingPathCalcTile[sizeX][sizeY][sizeZ];

        for (int dx = -radiusH; dx <= radiusH; dx++) {
            for (int dy = -radiusV; dy <= radiusV; dy++) {
                for (int dz = -radiusH; dz <= radiusH; dz++) {
                    int wx = centreX + dx;
                    int wy = centreY + dy;
                    int wz = centreZ + dz;

                    BlockPos pos = new BlockPos(wx, wy, wz);
                    BlockPos below = new BlockPos(wx, wy - 1, wz);
                    BlockState state = level.getBlockState(pos);
                    BlockState belowState = level.getBlockState(below);
                    Block block = state.getBlock();

                    boolean isLadder = block instanceof LadderBlock || block instanceof VineBlock;
                    boolean isWalkable = !state.isSolid() && belowState.isSolid() && !isLadder;

                    // Also check headroom (need 2 blocks of space for entity)
                    if (isWalkable) {
                        BlockPos above = new BlockPos(wx, wy + 1, wz);
                        BlockState aboveState = level.getBlockState(above);
                        if (aboveState.isSolid()) {
                            isWalkable = false;
                        }
                    }

                    short[] position = new short[]{(short) dx, (short) dy, (short) dz};
                    int ix = dx + radiusH;
                    int iy = dy + radiusV;
                    int iz = dz + radiusH;

                    region[ix][iy][iz] = new PathingPathCalcTile(isWalkable, isLadder, position);
                }
            }
        }

        return region;
    }
}
