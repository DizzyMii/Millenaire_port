package org.dizzymii.millenaire2.village;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.util.BlockItemUtilities;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Maintains a 2D map of the village area for building placement, pathfinding, and terrain analysis.
 * Ported from org.millenaire.common.village.VillageMapInfo (Forge 1.12.2).
 */
public class VillageMapInfo implements Cloneable {

    private static final int MAP_MARGIN = 5;
    private static final int BUILDING_MARGIN = 5;
    private static final int VALID_HEIGHT_DIFF = 10;
    public static final int UPDATE_FREQUENCY = 1000;

    public int length = 0;
    public int width = 0;
    public int chunkStartX = 0;
    public int chunkStartZ = 0;
    public int mapStartX = 0;
    public int mapStartZ = 0;
    public int yBaseline = 0;

    public short[][] topGround;
    public short[][] spaceAbove;
    public boolean[][] danger;
    public BuildingLocation[][] buildingLocRef;
    public boolean[][] canBuild;
    public boolean[][] buildingForbidden;
    public boolean[][] water;
    public boolean[][] tree;
    public boolean[][] buildTested = null;
    public boolean[][] topAdjusted;
    public boolean[][] path;

    public int frequency = 10;
    private List<BuildingLocation> buildingLocations = new ArrayList<>();
    public Level world;
    public int lastUpdatedX;
    public int lastUpdatedZ;
    private int updateCounter;

    // ========== Array deep clone helpers ==========

    public static BuildingLocation[][] blArrayDeepClone(BuildingLocation[][] source) {
        BuildingLocation[][] target = new BuildingLocation[source.length][];
        for (int i = 0; i < source.length; ++i) {
            target[i] = source[i].clone();
        }
        return target;
    }

    public static boolean[][] booleanArrayDeepClone(boolean[][] source) {
        boolean[][] target = new boolean[source.length][];
        for (int i = 0; i < source.length; ++i) {
            target[i] = source[i].clone();
        }
        return target;
    }

    public static short[][] shortArrayDeepClone(short[][] source) {
        short[][] target = new short[source.length][];
        for (int i = 0; i < source.length; ++i) {
            target[i] = source[i].clone();
        }
        return target;
    }

    // ========== Initialization ==========

    /**
     * Initialize map arrays for the given dimensions.
     */
    public void initializeMap(int width, int length, int startX, int startZ, int baseline) {
        this.width = width;
        this.length = length;
        this.mapStartX = startX;
        this.mapStartZ = startZ;
        this.yBaseline = baseline;

        topGround = new short[width][length];
        spaceAbove = new short[width][length];
        danger = new boolean[width][length];
        buildingLocRef = new BuildingLocation[width][length];
        canBuild = new boolean[width][length];
        buildingForbidden = new boolean[width][length];
        water = new boolean[width][length];
        tree = new boolean[width][length];
        topAdjusted = new boolean[width][length];
        path = new boolean[width][length];

        // Default: all positions can be built on
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                canBuild[x][z] = true;
            }
        }
    }

    // ========== Map update ==========

    /**
     * Incrementally update the map, scanning a portion each tick.
     * Call this periodically from the village tick.
     */
    public void updateMap() {
        if (world == null || width <= 0 || length <= 0) return;

        int columnsPerUpdate = Math.max(1, (width * length) / frequency);

        for (int i = 0; i < columnsPerUpdate; i++) {
            int x = lastUpdatedX;
            int z = lastUpdatedZ;

            if (x < width && z < length) {
                updateColumn(x, z);
            }

            // Advance to next column
            lastUpdatedZ++;
            if (lastUpdatedZ >= length) {
                lastUpdatedZ = 0;
                lastUpdatedX++;
                if (lastUpdatedX >= width) {
                    lastUpdatedX = 0;
                    updateCounter++;
                }
            }
        }
    }

    /**
     * Update terrain data for a single map column (x, z).
     */
    private void updateColumn(int mapX, int mapZ) {
        int worldX = mapStartX + mapX;
        int worldZ = mapStartZ + mapZ;

        // Find top solid block
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos(worldX, yBaseline + 30, worldZ);
        int topY = yBaseline;
        int space = 0;

        // Scan down from above to find ground
        for (int y = yBaseline + 30; y >= yBaseline - 10; y--) {
            mpos.setY(y);
            BlockState state = world.getBlockState(mpos);
            if (state.isSolid()) {
                topY = y;
                break;
            }
        }

        // Count air blocks above ground
        for (int y = topY + 1; y <= topY + 10; y++) {
            mpos.setY(y);
            if (world.isEmptyBlock(mpos)) {
                space++;
            } else {
                break;
            }
        }

        topGround[mapX][mapZ] = (short) (topY - yBaseline);
        spaceAbove[mapX][mapZ] = (short) space;

        // Classify block at ground level
        mpos.setY(topY);
        BlockState groundState = world.getBlockState(mpos);
        water[mapX][mapZ] = BlockItemUtilities.isBlockWater(groundState.getBlock());
        tree[mapX][mapZ] = BlockItemUtilities.isTreeBlock(groundState.getBlock());
        danger[mapX][mapZ] = BlockItemUtilities.isBlockDangerous(groundState.getBlock());
    }

    // ========== Building placement checks ==========

    /**
     * Check whether a building of the given dimensions can be placed at the specified map position.
     */
    public boolean canPlaceBuilding(int mapX, int mapZ, int bWidth, int bLength) {
        // Bounds check with margin
        if (mapX - BUILDING_MARGIN < 0 || mapX + bWidth + BUILDING_MARGIN > width) return false;
        if (mapZ - BUILDING_MARGIN < 0 || mapZ + bLength + BUILDING_MARGIN > length) return false;

        // Check all cells in the footprint
        int minHeight = Short.MAX_VALUE;
        int maxHeight = Short.MIN_VALUE;

        for (int x = mapX; x < mapX + bWidth; x++) {
            for (int z = mapZ; z < mapZ + bLength; z++) {
                if (buildingForbidden[x][z]) return false;
                if (buildingLocRef[x][z] != null) return false;
                if (!canBuild[x][z]) return false;
                if (water[x][z]) return false;
                if (danger[x][z]) return false;
                if (spaceAbove[x][z] < 3) return false;

                int h = topGround[x][z];
                if (h < minHeight) minHeight = h;
                if (h > maxHeight) maxHeight = h;
            }
        }

        // Height variance check
        return (maxHeight - minHeight) <= VALID_HEIGHT_DIFF;
    }

    /**
     * Mark a rectangular area as occupied by a building.
     */
    public void markBuildingArea(int mapX, int mapZ, int bWidth, int bLength, BuildingLocation loc) {
        for (int x = mapX; x < mapX + bWidth && x < width; x++) {
            for (int z = mapZ; z < mapZ + bLength && z < length; z++) {
                buildingLocRef[x][z] = loc;
                canBuild[x][z] = false;
            }
        }
        if (!buildingLocations.contains(loc)) {
            buildingLocations.add(loc);
        }
    }

    /**
     * Mark a rectangular area as forbidden for building.
     */
    public void markForbidden(int mapX, int mapZ, int bWidth, int bLength) {
        for (int x = mapX; x < mapX + bWidth && x < width; x++) {
            for (int z = mapZ; z < mapZ + bLength && z < length; z++) {
                buildingForbidden[x][z] = true;
            }
        }
    }

    /**
     * Mark a cell as part of a path.
     */
    public void markPath(int mapX, int mapZ) {
        if (mapX >= 0 && mapX < width && mapZ >= 0 && mapZ < length) {
            path[mapX][mapZ] = true;
        }
    }

    // ========== Coordinate conversion ==========

    /**
     * Convert a world X coordinate to map X.
     */
    public int worldToMapX(int worldX) {
        return worldX - mapStartX;
    }

    /**
     * Convert a world Z coordinate to map Z.
     */
    public int worldToMapZ(int worldZ) {
        return worldZ - mapStartZ;
    }

    /**
     * Check if a map coordinate is within bounds.
     */
    public boolean isInBounds(int mapX, int mapZ) {
        return mapX >= 0 && mapX < width && mapZ >= 0 && mapZ < length;
    }

    /**
     * Get the ground height at a map coordinate (relative to baseline).
     */
    public int getGroundHeight(int mapX, int mapZ) {
        if (!isInBounds(mapX, mapZ)) return 0;
        return topGround[mapX][mapZ];
    }

    /**
     * Get the absolute world Y of the ground at a map coordinate.
     */
    public int getWorldGroundY(int mapX, int mapZ) {
        return yBaseline + getGroundHeight(mapX, mapZ);
    }

    @Nullable
    public BuildingLocation getBuildingAt(int mapX, int mapZ) {
        if (!isInBounds(mapX, mapZ)) return null;
        return buildingLocRef[mapX][mapZ];
    }

    public List<BuildingLocation> getBuildingLocations() {
        return buildingLocations;
    }

    // ========== Clone ==========

    @Override
    public VillageMapInfo clone() {
        try {
            VillageMapInfo copy = (VillageMapInfo) super.clone();
            if (topGround != null) copy.topGround = shortArrayDeepClone(topGround);
            if (spaceAbove != null) copy.spaceAbove = shortArrayDeepClone(spaceAbove);
            if (danger != null) copy.danger = booleanArrayDeepClone(danger);
            if (canBuild != null) copy.canBuild = booleanArrayDeepClone(canBuild);
            if (buildingForbidden != null) copy.buildingForbidden = booleanArrayDeepClone(buildingForbidden);
            if (water != null) copy.water = booleanArrayDeepClone(water);
            if (tree != null) copy.tree = booleanArrayDeepClone(tree);
            if (topAdjusted != null) copy.topAdjusted = booleanArrayDeepClone(topAdjusted);
            if (path != null) copy.path = booleanArrayDeepClone(path);
            if (buildingLocRef != null) copy.buildingLocRef = blArrayDeepClone(buildingLocRef);
            copy.buildingLocations = new ArrayList<>(buildingLocations);
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
