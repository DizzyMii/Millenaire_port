package org.dizzymii.millenaire2.pathing.atomicstryker;

import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.pathing.PathingPathCalcTile;

/**
 * Generates a 3D region map for pathfinding around a given point.
 * Ported from org.millenaire.common.pathing.atomicstryker.RegionMapper (Forge 1.12.2).
 */
public final class RegionMapper {

    private RegionMapper() {}

    public static PathingPathCalcTile[][][] generateRegion(Level level, int centreX, int centreY, int centreZ,
                                                            int radiusH, int radiusV) {
        int sizeX = radiusH * 2 + 1;
        int sizeY = radiusV * 2 + 1;
        int sizeZ = radiusH * 2 + 1;
        PathingPathCalcTile[][][] region = new PathingPathCalcTile[sizeX][sizeY][sizeZ];
        // TODO: Populate region with walkable/ladder/empty tiles by scanning world blocks
        return region;
    }
}
