package org.dizzymii.millenaire2.village;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.culture.WallType;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Computes wall segment positions around a village perimeter.
 * Walls form a rectangle around the outermost buildings, with gates at path exits.
 */
public class VillageWallPlanner {

    /**
     * A single wall segment to be built.
     */
    public static class WallSegment {
        public final Point start;
        public final Point end;
        public final WallType wallType;
        public boolean built = false;

        public WallSegment(Point start, Point end, WallType wallType) {
            this.start = start;
            this.end = end;
            this.wallType = wallType;
        }
    }

    /**
     * Plan wall segments for a village. Creates a rectangular perimeter
     * around all buildings with a configurable margin.
     *
     * @param townhall The village's townhall building
     * @param buildings All buildings in the village
     * @param margin Extra blocks of space between outermost buildings and wall
     * @return List of wall segments to build, or empty if no wall type available
     */
    public static List<WallSegment> planWall(Building townhall, List<Building> buildings, int margin) {
        List<WallSegment> segments = new ArrayList<>();

        // Determine wall type from culture
        WallType wallType = selectWallType(townhall);
        if (wallType == null) {
            MillLog.minor("WallPlanner", "No wall type available for village at " + townhall.getPos());
            return segments;
        }

        // Compute bounding box of all buildings
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;
        int baseY = townhall.getPos() != null ? townhall.getPos().y : 64;

        for (Building b : buildings) {
            Point pos = b.getPos();
            if (pos == null) continue;
            BuildingLocation loc = b.location;
            int halfW = loc != null ? Math.max(loc.length / 2, 4) : 4;
            int halfL = loc != null ? Math.max(loc.width / 2, 4) : 4;
            minX = Math.min(minX, pos.x - halfW);
            maxX = Math.max(maxX, pos.x + halfW);
            minZ = Math.min(minZ, pos.z - halfL);
            maxZ = Math.max(maxZ, pos.z + halfL);
        }

        if (minX == Integer.MAX_VALUE) return segments;

        // Add margin
        minX -= margin;
        maxX += margin;
        minZ -= margin;
        maxZ += margin;

        // Create four wall segments (N, S, E, W)
        int segmentLength = 8; // Each segment covers 8 blocks

        // North wall (minZ side, running E-W)
        for (int x = minX; x < maxX; x += segmentLength) {
            int endX = Math.min(x + segmentLength, maxX);
            segments.add(new WallSegment(
                    new Point(x, baseY, minZ),
                    new Point(endX, baseY, minZ),
                    wallType));
        }

        // South wall (maxZ side, running E-W)
        for (int x = minX; x < maxX; x += segmentLength) {
            int endX = Math.min(x + segmentLength, maxX);
            segments.add(new WallSegment(
                    new Point(x, baseY, maxZ),
                    new Point(endX, baseY, maxZ),
                    wallType));
        }

        // West wall (minX side, running N-S)
        for (int z = minZ; z < maxZ; z += segmentLength) {
            int endZ = Math.min(z + segmentLength, maxZ);
            segments.add(new WallSegment(
                    new Point(minX, baseY, z),
                    new Point(minX, baseY, endZ),
                    wallType));
        }

        // East wall (maxX side, running N-S)
        for (int z = minZ; z < maxZ; z += segmentLength) {
            int endZ = Math.min(z + segmentLength, maxZ);
            segments.add(new WallSegment(
                    new Point(maxX, baseY, z),
                    new Point(maxX, baseY, endZ),
                    wallType));
        }

        MillLog.minor("WallPlanner", "Planned " + segments.size() + " wall segments for village at " + townhall.getPos());
        return segments;
    }

    /**
     * Build a single wall segment in the world.
     * Places wall blocks from start to end at the configured height.
     */
    public static void buildSegment(Level level, WallSegment segment) {
        if (segment.built) return;

        WallType wt = segment.wallType;
        BlockState wallBlock = resolveBlock(wt.wallBlocks);
        BlockState topBlock = resolveBlock(wt.topBlocks);
        if (wallBlock == null) wallBlock = Blocks.COBBLESTONE.defaultBlockState();
        if (topBlock == null) topBlock = Blocks.STONE_BRICK_SLAB.defaultBlockState();

        int height = wt.height;

        // Determine direction
        int dx = Integer.signum(segment.end.x - segment.start.x);
        int dz = Integer.signum(segment.end.z - segment.start.z);
        int length = Math.max(Math.abs(segment.end.x - segment.start.x),
                Math.abs(segment.end.z - segment.start.z));

        for (int i = 0; i <= length; i++) {
            int x = segment.start.x + dx * i;
            int z = segment.start.z + dz * i;

            // Find ground level
            int groundY = findGroundY(level, x, z, segment.start.y);

            // Place wall column
            for (int h = 0; h < height; h++) {
                BlockPos pos = new BlockPos(x, groundY + h, z);
                if (level.getBlockState(pos).isAir() || level.getBlockState(pos).canBeReplaced()) {
                    level.setBlock(pos, wallBlock, 3);
                }
            }
            // Place top block
            BlockPos topPos = new BlockPos(x, groundY + height, z);
            if (level.getBlockState(topPos).isAir() || level.getBlockState(topPos).canBeReplaced()) {
                level.setBlock(topPos, topBlock, 3);
            }
        }

        segment.built = true;
    }

    @Nullable
    private static WallType selectWallType(Building townhall) {
        if (townhall.cultureKey == null) return null;
        Culture culture = Culture.getCultureByName(townhall.cultureKey);
        if (culture == null || culture.wallTypes.isEmpty()) return null;

        // Pick highest-weight wall type, or first available
        WallType best = null;
        for (WallType wt : culture.wallTypes.values()) {
            if (best == null || wt.weight > best.weight) {
                best = wt;
            }
        }
        return best;
    }

    @Nullable
    private static BlockState resolveBlock(List<String> blockIds) {
        if (blockIds == null || blockIds.isEmpty()) return null;
        String blockId = blockIds.get(0);
        try {
            net.minecraft.resources.ResourceLocation rl = net.minecraft.resources.ResourceLocation.parse(blockId);
            net.minecraft.world.level.block.Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(rl);
            if (block != Blocks.AIR) {
                return block.defaultBlockState();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static int findGroundY(Level level, int x, int z, int hintY) {
        // Search down from hint for solid ground
        for (int y = hintY + 5; y > hintY - 10; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockPos above = pos.above();
            if (!level.getBlockState(pos).isAir() && level.getBlockState(above).isAir()) {
                return y + 1;
            }
        }
        return hintY;
    }
}
