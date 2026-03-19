package org.dizzymii.millenaire2.village;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Plans and stores path nodes between village buildings.
 * Uses Bresenham-like line rasterisation between building entrances,
 * with Y adjustment to follow terrain via VillageGeography.
 *
 * Ported from the original Millenaire path construction system.
 */
public class VillagePathPlanner {

    private static final int PATH_WIDTH = 2;

    /** Pending path blocks that villagers should place. */
    private final Deque<Point> pendingPathBlocks = new ArrayDeque<>();

    /** Already-placed path block positions (avoid re-queuing). */
    private final java.util.Set<Point> placedPaths = new java.util.LinkedHashSet<>();

    /**
     * Plan paths from the townhall to every other building in the village.
     * Uses VillageGeography for ground height lookup.
     */
    public void planPaths(Building townhall, @Nullable VillageGeography geography, ServerLevel level) {
        if (townhall.mw == null || townhall.getPos() == null) return;
        pendingPathBlocks.clear();

        Point thPos = townhall.getPos();
        List<Point> buildingCenters = new ArrayList<>();

        for (Building b : townhall.mw.getBuildingsMap().values()) {
            if (b == townhall) continue;
            if (!townhall.isSameVillage(b)) continue;
            Point bPos = b.getPos();
            if (bPos == null) continue;
            buildingCenters.add(bPos);
        }

        for (Point target : buildingCenters) {
            List<Point> pathLine = rasterizePath(thPos, target, geography, level);
            for (Point p : pathLine) {
                if (!placedPaths.contains(p)) {
                    pendingPathBlocks.add(p);
                }
            }
        }

        if (!pendingPathBlocks.isEmpty()) {
            MillLog.minor("PathPlanner", "Queued " + pendingPathBlocks.size()
                    + " path blocks for village at " + thPos);
        }
    }

    /**
     * Get the next path block to place, or null if none pending.
     */
    @Nullable
    public Point getNextPathBlock() {
        return pendingPathBlocks.pollFirst();
    }

    /**
     * Check if there are pending path blocks to place.
     */
    public boolean hasPendingPaths() {
        return !pendingPathBlocks.isEmpty();
    }

    /**
     * Mark a path block as placed.
     */
    public void markPlaced(Point p) {
        placedPaths.add(p);
    }

    /**
     * Get total pending path blocks count.
     */
    public int getPendingCount() {
        return pendingPathBlocks.size();
    }

    /**
     * Rasterize a line from start to end at ground level, producing path block positions.
     * Uses Bresenham's line algorithm in 2D (XZ plane), with Y from geography or world query.
     */
    private List<Point> rasterizePath(Point start, Point end,
                                       @Nullable VillageGeography geography,
                                       ServerLevel level) {
        List<Point> path = new ArrayList<>();

        int x0 = start.x;
        int z0 = start.z;
        int x1 = end.x;
        int z1 = end.z;

        int dx = Math.abs(x1 - x0);
        int dz = Math.abs(z1 - z0);
        int sx = x0 < x1 ? 1 : -1;
        int sz = z0 < z1 ? 1 : -1;
        int err = dx - dz;

        while (true) {
            // Get ground Y at this position
            int y = getGroundY(x0, z0, geography, level);
            if (y > 0) {
                // Add path block at ground level (replaces top block)
                addPathWidth(path, x0, y, z0, dx > dz);
            }

            if (x0 == x1 && z0 == z1) break;

            int e2 = 2 * err;
            if (e2 > -dz) {
                err -= dz;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                z0 += sz;
            }
        }

        return path;
    }

    /**
     * Add path blocks for a given center point, with optional width.
     */
    private void addPathWidth(List<Point> path, int cx, int y, int cz, boolean wideOnZ) {
        path.add(new Point(cx, y, cz));
        if (PATH_WIDTH >= 2) {
            if (wideOnZ) {
                path.add(new Point(cx, y, cz + 1));
            } else {
                path.add(new Point(cx + 1, y, cz));
            }
        }
    }

    /**
     * Get ground Y at a world position. Uses geography if available, else world query.
     */
    private int getGroundY(int worldX, int worldZ,
                           @Nullable VillageGeography geography, ServerLevel level) {
        if (geography != null && geography.isInGrid(worldX, worldZ)) {
            int h = geography.getGroundHeight(worldX, worldZ);
            if (h > 0) return h;
        }
        // Fallback: scan world
        return findGroundLevel(level, worldX, worldZ);
    }

    /**
     * Find ground level by scanning downward from build height.
     */
    private static int findGroundLevel(ServerLevel level, int x, int z) {
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos(x, 0, z);
        int maxY = Math.min(level.getMaxBuildHeight() - 1, 320);
        for (int y = maxY; y > level.getMinBuildHeight(); y--) {
            mpos.setY(y);
            BlockState state = level.getBlockState(mpos);
            if (!state.isAir() && state.getFluidState().isEmpty()) {
                // Found solid — path goes on top
                return y + 1;
            }
        }
        return -1;
    }

    /**
     * Place a single path block in the world.
     * Replaces only replaceable blocks (grass, dirt, etc.).
     * Returns true if placed.
     */
    public static boolean placePathBlock(ServerLevel level, Point p) {
        BlockPos pos = new BlockPos(p.x, p.y - 1, p.z);
        BlockState existing = level.getBlockState(pos);

        // Only replace natural surface blocks
        if (existing.is(Blocks.GRASS_BLOCK) || existing.is(Blocks.DIRT)
                || existing.is(Blocks.PODZOL) || existing.is(Blocks.MYCELIUM)
                || existing.is(Blocks.SAND) || existing.is(Blocks.RED_SAND)
                || existing.is(Blocks.GRAVEL) || existing.is(Blocks.COARSE_DIRT)) {
            level.setBlock(pos, Blocks.DIRT_PATH.defaultBlockState(), 3);
            return true;
        }

        return false;
    }
}
