package org.dizzymii.millenaire2.village;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Maintains a 2D terrain grid around a village center for building placement decisions.
 * Ported from org.millenaire.VillageGeography (MoonCutter2B).
 *
 * Tracks per-cell:
 * - ground height (topGround) and construction height
 * - buildability (canBuild), danger, water, occupied by building
 * - space above ground for villager navigation
 */
public class VillageGeography {

    private static final int MAP_MARGIN = 10;
    private static final int BUILDING_MARGIN = 5;
    private static final int VALID_HEIGHT_DIFF = 10;

    public int length = 0;
    public int width = 0;
    public int mapStartX = 0;
    public int mapStartZ = 0;
    private int yBaseline = 0;

    private short[][] topGround;
    public short[][] constructionHeight;
    private short[][] spaceAbove;

    public boolean[][] danger;
    public boolean[][] buildingForbidden;
    public boolean[][] canBuild;
    public boolean[][] buildTested;
    public boolean[][] buildingLoc;
    private boolean[][] water;
    public boolean[][] path;

    private final List<BuildingLocation> buildingLocations = new ArrayList<>();

    @Nullable private ServerLevel world;

    // ========== Initialisation ==========

    /**
     * Create or recreate the terrain grid covering all registered buildings
     * plus the search radius around the village center.
     */
    public void update(ServerLevel level, List<BuildingLocation> locations,
                       Point center, int radius) {
        this.world = level;
        this.yBaseline = center.y;

        int startX = center.x;
        int startZ = center.z;
        int endX = center.x;
        int endZ = center.z;

        for (BuildingLocation bl : locations) {
            if (bl == null) continue;
            Point blPos = bl.pos;
            if (blPos == null) continue;
            int halfLen = bl.length / 2;
            int halfWid = bl.width / 2;
            startX = Math.min(startX, blPos.x - halfLen);
            endX = Math.max(endX, blPos.x + halfLen);
            startZ = Math.min(startZ, blPos.z - halfWid);
            endZ = Math.max(endZ, blPos.z + halfWid);
        }

        startX = Math.min(startX - BUILDING_MARGIN, center.x - radius - MAP_MARGIN);
        startZ = Math.min(startZ - BUILDING_MARGIN, center.z - radius - MAP_MARGIN);
        endX = Math.max(endX + BUILDING_MARGIN, center.x + radius + MAP_MARGIN);
        endZ = Math.max(endZ + BUILDING_MARGIN, center.z + radius + MAP_MARGIN);

        // Align to chunk boundaries
        int chunkStartX = startX >> 4;
        int chunkStartZ = startZ >> 4;
        mapStartX = chunkStartX << 4;
        mapStartZ = chunkStartZ << 4;
        length = ((endX >> 4) + 1 << 4) - mapStartX;
        width = ((endZ >> 4) + 1 << 4) - mapStartZ;

        if (length <= 0 || width <= 0) {
            MillLog.warn("VillageGeography", "Invalid grid dimensions: " + length + "x" + width);
            return;
        }

        allocateArrays();

        buildingLocations.clear();
        for (BuildingLocation bl : locations) {
            registerBuildingLocation(bl);
        }

        // Scan all chunks in the grid
        for (int cx = 0; cx < length; cx += 16) {
            for (int cz = 0; cz < width; cz += 16) {
                updateChunk(cx, cz);
            }
        }
    }

    private void allocateArrays() {
        topGround = new short[length][width];
        constructionHeight = new short[length][width];
        spaceAbove = new short[length][width];
        danger = new boolean[length][width];
        buildingLoc = new boolean[length][width];
        buildingForbidden = new boolean[length][width];
        canBuild = new boolean[length][width];
        buildTested = new boolean[length][width];
        water = new boolean[length][width];
        path = new boolean[length][width];
    }

    // ========== Building registration ==========

    private void registerBuildingLocation(BuildingLocation bl) {
        if (bl == null || bl.pos == null) return;
        buildingLocations.add(bl);

        int sx = Math.max(bl.minxMargin - mapStartX, 0);
        int sz = Math.max(bl.minzMargin - mapStartZ, 0);
        int ex = Math.min(bl.maxxMargin - mapStartX, length);
        int ez = Math.min(bl.maxzMargin - mapStartZ, width);

        for (int i = sx; i < ex; i++) {
            for (int j = sz; j < ez; j++) {
                buildingLoc[i][j] = true;
            }
        }
    }

    /**
     * Register a newly placed building into the grid without a full rescan.
     */
    public void registerNewBuilding(BuildingLocation bl) {
        registerBuildingLocation(bl);
        // Recompute canBuild for affected cells
        if (bl.pos == null) return;
        int sx = Math.max(bl.minxMargin - mapStartX, 0);
        int sz = Math.max(bl.minzMargin - mapStartZ, 0);
        int ex = Math.min(bl.maxxMargin - mapStartX, length);
        int ez = Math.min(bl.maxzMargin - mapStartZ, width);
        for (int i = sx; i < ex; i++) {
            for (int j = sz; j < ez; j++) {
                canBuild[i][j] = false;
            }
        }
    }

    // ========== Terrain scanning ==========

    private void updateChunk(int startX, int startZ) {
        if (world == null) return;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int mx = i + startX;
                int mz = j + startZ;
                if (mx < 0 || mx >= length || mz < 0 || mz >= width) continue;

                int worldX = mx + mapStartX;
                int worldZ = mz + mapStartZ;

                short miny = (short) Math.max(yBaseline - 25, world.getMinBuildHeight() + 1);
                short maxy = (short) Math.min(yBaseline + 25, world.getMaxBuildHeight() - 1);

                canBuild[mx][mz] = false;
                buildingForbidden[mx][mz] = false;
                water[mx][mz] = false;

                // Scan downward from maxy to find ground
                short y = maxy;
                BlockState state = world.getBlockState(new BlockPos(worldX, y, worldZ));
                while (y >= miny && !isGround(state)) {
                    y--;
                    state = world.getBlockState(new BlockPos(worldX, y, worldZ));
                }

                constructionHeight[mx][mz] = y;

                // Walk upward through solid blocks to find actual construction surface
                boolean heightDone = false;
                while (y <= maxy && y > miny) {
                    state = world.getBlockState(new BlockPos(worldX, y, worldZ));

                    if (state.is(BlockTags.LOGS)) {
                        heightDone = true;
                    } else if (!heightDone && isSolid(state)) {
                        constructionHeight[mx][mz] = (short) (y + 1);
                    }

                    if (isForbiddenForConstruction(state)) {
                        buildingForbidden[mx][mz] = true;
                    }

                    y++;
                    state = world.getBlockState(new BlockPos(worldX, y, worldZ));
                    if (!isSolid(state) && state.getFluidState().isEmpty()) break;
                }

                // Find topGround: first position with 2 air blocks above
                y = constructionHeight[mx][mz];
                while (y <= maxy) {
                    BlockState atY = world.getBlockState(new BlockPos(worldX, y, worldZ));
                    BlockState aboveY = world.getBlockState(new BlockPos(worldX, y + 1, worldZ));
                    if (!isSolid(atY) && !isSolid(aboveY)) break;
                    y++;
                }
                topGround[mx][mz] = (short) Math.max(miny, y);

                // Check for water
                BlockState groundBlock = world.getBlockState(new BlockPos(worldX, topGround[mx][mz], worldZ));
                water[mx][mz] = !groundBlock.getFluidState().isEmpty();

                // Compute space above
                spaceAbove[mx][mz] = 0;
                BlockState soilBlock = world.getBlockState(new BlockPos(worldX, topGround[mx][mz] - 1, worldZ));
                if (!(soilBlock.getBlock() instanceof FenceBlock) && !(soilBlock.getBlock() instanceof WallBlock)
                        && !isSolid(groundBlock) && groundBlock.getFluidState().isEmpty()) {
                    for (short s = 0; s < 3; s++) {
                        BlockState above = world.getBlockState(
                                new BlockPos(worldX, topGround[mx][mz] + s, worldZ));
                        if (!isSolid(above)) {
                            spaceAbove[mx][mz]++;
                        } else {
                            break;
                        }
                    }
                }

                // Check for lava/danger
                danger[mx][mz] = groundBlock.is(Blocks.LAVA);

                // Determine buildability
                if (!danger[mx][mz] && !buildingLoc[mx][mz] && !buildingForbidden[mx][mz]) {
                    if (constructionHeight[mx][mz] > yBaseline - VALID_HEIGHT_DIFF
                            && constructionHeight[mx][mz] < yBaseline + VALID_HEIGHT_DIFF) {
                        canBuild[mx][mz] = true;
                    }
                }

                // Check if this is a mill path block
                path[mx][mz] = soilBlock.is(Blocks.DIRT_PATH)
                        || soilBlock.getBlock().getDescriptionId().contains("mill_path");
            }
        }
    }

    // ========== Building placement queries ==========

    /**
     * Test if a building of the given footprint can be placed at map-relative position (mi, mj).
     * Checks that the full footprint is buildable and height variation is acceptable.
     *
     * @param mi map-relative X position (center of the building footprint)
     * @param mj map-relative Z position (center of the building footprint)
     * @param bldgLength building length (X dimension)
     * @param bldgWidth building width (Z dimension)
     * @param margin extra margin around the building
     * @return the average ground Y if valid, or -1 if not buildable
     */
    public int testBuildingPlacement(int mi, int mj, int bldgLength, int bldgWidth, int margin) {
        int halfLen = bldgLength / 2;
        int halfWid = bldgWidth / 2;

        int sx = mi - halfLen - margin;
        int sz = mj - halfWid - margin;
        int ex = mi + halfLen + margin;
        int ez = mj + halfWid + margin;

        if (sx < 0 || sz < 0 || ex >= length || ez >= width) return -1;

        long totalHeight = 0;
        int count = 0;
        int minH = Integer.MAX_VALUE;
        int maxH = Integer.MIN_VALUE;

        for (int i = sx; i <= ex; i++) {
            for (int j = sz; j <= ez; j++) {
                if (!canBuild[i][j]) return -1;
                if (water[i][j]) return -1;
                if (danger[i][j]) return -1;

                int h = constructionHeight[i][j];
                totalHeight += h;
                count++;
                minH = Math.min(minH, h);
                maxH = Math.max(maxH, h);
            }
        }

        // Max height variation across footprint: 5 blocks
        if (maxH - minH > 5) return -1;
        if (count == 0) return -1;

        return (int) (totalHeight / count);
    }

    /**
     * Find a valid location for a building with the given dimensions,
     * searching outward from the center in expanding rings.
     *
     * @param centerX map-relative X of search center
     * @param centerZ map-relative Z of search center
     * @param bldgLength building length
     * @param bldgWidth building width
     * @param minRadius minimum search radius
     * @param maxRadius maximum search radius
     * @param margin building margin (spacing)
     * @return world-space Point if found, null otherwise
     */
    @Nullable
    public Point findBuildingLocation(int centerX, int centerZ,
                                       int bldgLength, int bldgWidth,
                                       int minRadius, int maxRadius, int margin) {
        int ci = centerX - mapStartX;
        int cj = centerZ - mapStartZ;

        // Reset test flags
        if (buildTested != null) {
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < width; j++) {
                    buildTested[i][j] = false;
                }
            }
        }

        for (int radius = minRadius; radius <= maxRadius; radius++) {
            int mini = Math.max(0, ci - radius);
            int maxi = Math.min(length - 1, ci + radius);
            int minj = Math.max(0, cj - radius);
            int maxj = Math.min(width - 1, cj + radius);

            // Scan the ring at this radius
            for (int i = mini; i <= maxi; i++) {
                // Top edge
                if (cj - radius >= 0) {
                    int result = tryPosition(i, minj, bldgLength, bldgWidth, margin);
                    if (result >= 0) {
                        return new Point(i + mapStartX, result, minj + mapStartZ);
                    }
                }
                // Bottom edge
                if (cj + radius < width) {
                    int result = tryPosition(i, maxj, bldgLength, bldgWidth, margin);
                    if (result >= 0) {
                        return new Point(i + mapStartX, result, maxj + mapStartZ);
                    }
                }
            }
            for (int j = minj + 1; j < maxj; j++) {
                // Left edge
                if (ci - radius >= 0) {
                    int result = tryPosition(mini, j, bldgLength, bldgWidth, margin);
                    if (result >= 0) {
                        return new Point(mini + mapStartX, result, j + mapStartZ);
                    }
                }
                // Right edge
                if (ci + radius < length) {
                    int result = tryPosition(maxi, j, bldgLength, bldgWidth, margin);
                    if (result >= 0) {
                        return new Point(maxi + mapStartX, result, j + mapStartZ);
                    }
                }
            }
        }

        return null;
    }

    private int tryPosition(int mi, int mj, int bldgLength, int bldgWidth, int margin) {
        if (mi < 0 || mi >= length || mj < 0 || mj >= width) return -1;
        if (buildTested[mi][mj]) return -1;
        buildTested[mi][mj] = true;
        return testBuildingPlacement(mi, mj, bldgLength, bldgWidth, margin);
    }

    // ========== Query helpers ==========

    /**
     * Get the ground height at a world position, or -1 if outside the grid.
     */
    public int getGroundHeight(int worldX, int worldZ) {
        int mi = worldX - mapStartX;
        int mj = worldZ - mapStartZ;
        if (mi < 0 || mi >= length || mj < 0 || mj >= width) return -1;
        return topGround[mi][mj];
    }

    /**
     * Check if a world position is within the scanned grid.
     */
    public boolean isInGrid(int worldX, int worldZ) {
        int mi = worldX - mapStartX;
        int mj = worldZ - mapStartZ;
        return mi >= 0 && mi < length && mj >= 0 && mj < width;
    }

    /**
     * Check if a world position is buildable.
     */
    public boolean canBuildAt(int worldX, int worldZ) {
        int mi = worldX - mapStartX;
        int mj = worldZ - mapStartZ;
        if (mi < 0 || mi >= length || mj < 0 || mj >= width) return false;
        return canBuild[mi][mj];
    }

    /**
     * Check if a world position is water.
     */
    public boolean isWater(int worldX, int worldZ) {
        int mi = worldX - mapStartX;
        int mj = worldZ - mapStartZ;
        if (mi < 0 || mi >= length || mj < 0 || mj >= width) return false;
        return water[mi][mj];
    }

    /**
     * Check if a world position is dangerous (lava, etc.).
     */
    public boolean isDangerous(int worldX, int worldZ) {
        int mi = worldX - mapStartX;
        int mj = worldZ - mapStartZ;
        if (mi < 0 || mi >= length || mj < 0 || mj >= width) return true;
        return danger[mi][mj];
    }

    /**
     * Get space above ground at a world position. Useful for path validation.
     */
    public int getSpaceAbove(int worldX, int worldZ) {
        int mi = worldX - mapStartX;
        int mj = worldZ - mapStartZ;
        if (mi < 0 || mi >= length || mj < 0 || mj >= width) return 0;
        return spaceAbove[mi][mj];
    }

    // ========== Block classification (1.21.1 API) ==========

    private static boolean isGround(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.BEDROCK || block == Blocks.CLAY || block == Blocks.DIRT
                || block == Blocks.GRASS_BLOCK || block == Blocks.GRAVEL
                || block == Blocks.OBSIDIAN || block == Blocks.SAND
                || block == Blocks.RED_SAND || block == Blocks.FARMLAND
                || block == Blocks.PODZOL || block == Blocks.MYCELIUM
                || block == Blocks.MUD || block == Blocks.MUDDY_MANGROVE_ROOTS
                || state.is(BlockTags.TERRACOTTA);
    }

    private static boolean isSolid(BlockState state) {
        if (state.isAir()) return false;
        Block block = state.getBlock();
        return state.canOcclude()
                || block == Blocks.GLASS || block == Blocks.GLASS_PANE
                || block instanceof SlabBlock || block instanceof StairBlock
                || block instanceof FenceBlock || block instanceof WallBlock;
    }

    private static boolean isForbiddenForConstruction(BlockState state) {
        Block block = state.getBlock();
        return !state.getFluidState().isEmpty()
                || block == Blocks.ICE || block == Blocks.PACKED_ICE
                || state.is(BlockTags.PLANKS) || block == Blocks.COBBLESTONE
                || block == Blocks.BRICKS || block == Blocks.CHEST
                || block == Blocks.GLASS || block == Blocks.STONE_BRICKS
                || block instanceof WallBlock || block instanceof FenceBlock;
    }
}
