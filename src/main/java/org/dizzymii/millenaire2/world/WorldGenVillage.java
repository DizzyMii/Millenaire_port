package org.dizzymii.millenaire2.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

import javax.annotation.Nullable;
import java.util.HashSet;

/**
 * Handles village generation in the world — placing town halls, hamlets, lone buildings.
 * In 1.21.1 NeoForge this integrates via server tick checks on explored chunks.
 * Ported from org.millenaire.common.world.WorldGenVillage (Forge 1.12.2).
 */
public class WorldGenVillage {

    private static final int HAMLET_ATTEMPT_ANGLE_STEPS = 36;
    private static final int CHUNK_DISTANCE_LOAD_TEST = 8;
    private static final int HAMLET_MAX_DISTANCE = 350;
    private static final int HAMLET_MIN_DISTANCE = 250;
    private static final double MINIMUM_USABLE_BLOCK_PERC = 0.7;
    private static final int MIN_DISTANCE_FROM_SPAWN = 200;
    private static final int MIN_DISTANCE_BETWEEN_VILLAGES = 500;

    private static final HashSet<Long> chunkCoordsTried = new HashSet<>();

    /**
     * Attempts to generate a new village near the given chunk coordinates.
     * Called from server tick when a player explores new terrain.
     */
    public static boolean attemptVillageGeneration(ServerLevel level, int chunkX, int chunkZ,
                                                     RandomSource random, MillWorldData worldData) {
        long chunkKey = chunkKey(chunkX, chunkZ);
        if (chunkCoordsTried.contains(chunkKey)) return false;
        chunkCoordsTried.add(chunkKey);

        BlockPos center = new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8);

        // Check spawn protection radius
        BlockPos spawn = level.getSharedSpawnPos();
        if (center.closerThan(spawn, MIN_DISTANCE_FROM_SPAWN)) return false;

        // Check distance from existing villages
        Point centerPoint = new Point(center.getX(), 0, center.getZ());
        for (Building b : worldData.allBuildings()) {
            if (b.isTownhall && b.getPos() != null) {
                double dist = centerPoint.distanceTo(b.getPos());
                if (dist < MIN_DISTANCE_BETWEEN_VILLAGES) return false;
            }
        }

        // Find suitable ground level
        int groundY = findGroundLevel(level, center);
        if (groundY < 0) return false;

        BlockPos groundPos = new BlockPos(center.getX(), groundY, center.getZ());

        // Evaluate terrain suitability
        double usable = evaluateTerrainFlat(level, groundPos, 16);
        if (usable < MINIMUM_USABLE_BLOCK_PERC) return false;

        // Pick a random culture
        if (Culture.LIST_CULTURES.isEmpty()) return false;
        Culture culture = Culture.LIST_CULTURES.get(random.nextInt(Culture.LIST_CULTURES.size()));

        return generateNewVillage(level, groundPos, culture, worldData, random);
    }

    /**
     * Places a new village townhall building at the given position.
     */
    public static boolean generateNewVillage(ServerLevel level, BlockPos pos, Culture culture,
                                              MillWorldData worldData, RandomSource random) {
        Point villagePos = new Point(pos.getX(), pos.getY(), pos.getZ());

        // Create a new Building as the townhall
        Building townhall = new Building();
        townhall.isTownhall = true;
        townhall.isActive = true;
        townhall.cultureKey = culture.key;
        townhall.setName(culture.key + "_village_" + random.nextInt(10000));
        townhall.setPos(villagePos);

        worldData.addBuilding(townhall, villagePos);

        MillLog.minor("WorldGenVillage", "Generated new " + culture.key + " village at " +
                pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
        return true;
    }

    /**
     * Attempts to generate a hamlet (satellite village) near an existing village.
     */
    @Nullable
    public static Building generateHamlet(ServerLevel level, Building parentVillage,
                                           MillWorldData worldData, RandomSource random) {
        if (parentVillage.getPos() == null) return null;
        Point parentPos = parentVillage.getPos();

        for (int step = 0; step < HAMLET_ATTEMPT_ANGLE_STEPS; step++) {
            double angle = (2 * Math.PI * step) / HAMLET_ATTEMPT_ANGLE_STEPS;
            int dist = HAMLET_MIN_DISTANCE + random.nextInt(HAMLET_MAX_DISTANCE - HAMLET_MIN_DISTANCE);
            int hx = parentPos.x + (int) (Math.cos(angle) * dist);
            int hz = parentPos.z + (int) (Math.sin(angle) * dist);

            BlockPos hamletCenter = new BlockPos(hx, 0, hz);
            int groundY = findGroundLevel(level, hamletCenter);
            if (groundY < 0) continue;

            BlockPos groundPos = new BlockPos(hx, groundY, hz);
            double usable = evaluateTerrainFlat(level, groundPos, 8);
            if (usable < MINIMUM_USABLE_BLOCK_PERC) continue;

            Point hamletPoint = new Point(hx, groundY, hz);

            Building hamlet = new Building();
            hamlet.isTownhall = false;
            hamlet.isActive = true;
            hamlet.cultureKey = parentVillage.cultureKey;
            hamlet.setName((parentVillage.getName() != null ? parentVillage.getName() : "village") + "_hamlet");
            hamlet.setPos(hamletPoint);

            worldData.addBuilding(hamlet, hamletPoint);

            MillLog.minor("WorldGenVillage", "Generated hamlet at " + hx + ", " + groundY + ", " + hz);
            return hamlet;
        }
        return null;
    }

    /**
     * Generates a lone building (e.g. bedrock-spawned structure like a lone farm).
     */
    public static boolean generateBedrockLoneBuilding(ServerLevel level, BlockPos pos,
                                                       Culture culture, MillWorldData worldData) {
        int groundY = findGroundLevel(level, pos);
        if (groundY < 0) return false;

        Point bldgPos = new Point(pos.getX(), groundY, pos.getZ());

        Building lone = new Building();
        lone.isTownhall = false;
        lone.isActive = true;
        lone.cultureKey = culture.key;
        lone.setName(culture.key + "_lone_" + pos.getX() + "_" + pos.getZ());
        lone.setPos(bldgPos);

        worldData.addBuilding(lone, bldgPos);

        MillLog.minor("WorldGenVillage", "Generated lone building at " +
                pos.getX() + ", " + groundY + ", " + pos.getZ());
        return true;
    }

    // ========== Terrain utilities ==========

    private static int findGroundLevel(ServerLevel level, BlockPos pos) {
        // Scan down from world height to find the first solid block
        for (int y = level.getMaxBuildHeight() - 1; y > level.getMinBuildHeight(); y--) {
            BlockPos check = new BlockPos(pos.getX(), y, pos.getZ());
            BlockState state = level.getBlockState(check);
            if (!state.isAir() && !state.is(Blocks.WATER) && !state.canBeReplaced()) {
                return y + 1;
            }
        }
        return -1;
    }

    /**
     * Evaluates how flat/buildable the terrain is around a position.
     * Returns a 0.0-1.0 value indicating % of positions within radius that are suitable.
     */
    private static double evaluateTerrainFlat(ServerLevel level, BlockPos center, int radius) {
        int total = 0;
        int usable = 0;
        int baseY = center.getY();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                total++;
                BlockPos check = new BlockPos(center.getX() + dx, 0, center.getZ() + dz);
                int groundY = findGroundLevel(level, check);
                if (groundY >= 0 && Math.abs(groundY - baseY) <= 3) {
                    usable++;
                }
            }
        }
        return total > 0 ? (double) usable / total : 0.0;
    }

    private static long chunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }

    public static void resetTriedChunks() {
        chunkCoordsTried.clear();
    }
}
