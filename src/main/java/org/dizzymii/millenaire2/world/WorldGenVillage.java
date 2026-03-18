package org.dizzymii.millenaire2.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.culture.BuildingPlan;
import org.dizzymii.millenaire2.culture.BuildingPlanSet;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.culture.VillageType;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.BuildingLocation;
import org.dizzymii.millenaire2.village.ConstructionIP;
import org.dizzymii.millenaire2.village.VillagerRecord;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

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

        // Pick a culture based on biome mapping (data-driven)
        Culture culture = BiomeCultureMapper.selectCulture(level, groundPos, random);
        if (culture == null) return false;

        return generateNewVillage(level, groundPos, culture, worldData, random);
    }

    /**
     * Places a new village townhall building at the given position.
     * Selects a VillageType, resolves the centre BuildingPlanSet, loads the PNG plan,
     * creates a ConstructionIP, and populates initial VillagerRecords.
     */
    public static boolean generateNewVillage(ServerLevel level, BlockPos pos, Culture culture,
                                              MillWorldData worldData, RandomSource random) {
        // Pick a village type from this culture
        VillageType villageType = pickVillageType(culture, random);
        if (villageType == null) {
            MillLog.warn("WorldGenVillage", "No village types for culture: " + culture.key);
            return false;
        }

        // Resolve centre building plan set
        BuildingPlanSet planSet = resolveCentrePlanSet(culture, villageType);
        if (planSet == null) {
            MillLog.warn("WorldGenVillage", "No centre building plan for village type: " + villageType.key);
            return false;
        }

        BuildingPlan initialPlan = planSet.getInitialPlan();
        if (initialPlan == null || !initialPlan.hasImage()) {
            MillLog.warn("WorldGenVillage", "No initial plan image for: " + planSet.key);
            return false;
        }

        Point villagePos = new Point(pos.getX(), pos.getY(), pos.getZ());

        // Create a BuildingLocation
        BuildingLocation location = new BuildingLocation();
        location.planKey = planSet.key;
        location.cultureKey = culture.key;
        location.pos = villagePos;
        location.orientation = random.nextInt(4);
        location.width = initialPlan.width;
        location.length = initialPlan.length;
        location.level = 0;

        // Create a new Building as the townhall
        Building townhall = new Building();
        townhall.isTownhall = true;
        townhall.isActive = true;
        townhall.cultureKey = culture.key;
        townhall.planSetKey = planSet.key;
        townhall.villageTypeKey = villageType.key;
        townhall.buildingLevel = 0;
        townhall.setName(generateVillageName(culture, random));
        townhall.setPos(villagePos);
        townhall.setTownHallPos(villagePos);
        townhall.location = location;
        townhall.mw = worldData;
        townhall.world = level;

        // Prepare terrain and queue gradual construction (original behavior)
        prepareTerrain(level, villagePos, initialPlan.width, initialPlan.length);
        ConstructionIP cip = ConstructionIP.fromBuildingPlan(initialPlan, villagePos, level);
        if (cip != null) {
            cip.orientation = location.orientation;
            townhall.currentConstruction = cip;
            MillLog.minor("WorldGenVillage", "Queued construction (" + cip.nbBlocksTotal + " blocks) for " + planSet.key);
        } else {
            MillLog.warn("WorldGenVillage", "No constructable blocks from plan: " + planSet.key);
        }

        // Create initial VillagerRecords from the plan's villager list
        createInitialVillagers(townhall, culture, initialPlan, villagePos, random);

        worldData.addBuilding(townhall, villagePos);

        MillLog.minor("WorldGenVillage", "Generated new " + culture.key + " " + villageType.key
                + " village at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()
                + " (" + townhall.getVillagerRecords().size() + " villagers)");
        return true;
    }

    /**
     * Pick a VillageType from the culture's list, weighted by generation weight.
     */
    @Nullable
    private static VillageType pickVillageType(Culture culture, RandomSource random) {
        List<VillageType> types = culture.listVillageTypes;
        if (types.isEmpty()) return null;

        int totalWeight = 0;
        for (VillageType vt : types) {
            totalWeight += Math.max(vt.weight, 1);
        }
        int roll = random.nextInt(Math.max(totalWeight, 1));
        int cumulative = 0;
        for (VillageType vt : types) {
            cumulative += Math.max(vt.weight, 1);
            if (roll < cumulative) return vt;
        }
        return types.get(0);
    }

    /**
     * Resolve the centre BuildingPlanSet for a VillageType.
     */
    @Nullable
    private static BuildingPlanSet resolveCentrePlanSet(Culture culture, VillageType villageType) {
        if (villageType.centreBuilding != null) {
            BuildingPlanSet set = culture.planSets.get(villageType.centreBuilding);
            if (set != null) return set;
        }
        // Fallback: try to find any plan set tagged as "townhall"
        for (BuildingPlanSet bps : culture.listPlanSets) {
            if (bps.tags.contains("townhall") || bps.tags.contains("centre")) {
                return bps;
            }
        }
        // Last resort: first plan set
        return culture.listPlanSets.isEmpty() ? null : culture.listPlanSets.get(0);
    }

    /**
     * Create initial VillagerRecords for a new village from the plan's villager definitions.
     */
    private static void createInitialVillagers(Building townhall, Culture culture,
                                                BuildingPlan plan, Point villagePos,
                                                RandomSource random) {
        List<String> villagerTypes = plan.villagers;
        if (villagerTypes.isEmpty()) {
            // Fallback: create a default leader villager
            VillagerRecord leader = VillagerRecord.create(
                    culture.key, "leader",
                    getRandomFirstName(culture, MillVillager.MALE, random),
                    getRandomFamilyName(culture, random),
                    MillVillager.MALE);
            leader.setHousePos(villagePos);
            leader.setTownHallPos(villagePos);
            townhall.addVillagerRecord(leader);
            return;
        }

        for (String vtKey : villagerTypes) {
            VillagerType vtype = culture.getVillagerType(vtKey);
            int gender = MillVillager.MALE;
            if (vtype != null && "female".equalsIgnoreCase(vtype.gender)) gender = MillVillager.FEMALE;

            VillagerRecord vr = VillagerRecord.create(
                    culture.key, vtKey,
                    getRandomFirstName(culture, gender, random),
                    getRandomFamilyName(culture, random),
                    gender);
            vr.setHousePos(villagePos);
            vr.setTownHallPos(villagePos);
            townhall.addVillagerRecord(vr);
        }
    }

    /**
     * Get a random first name from a culture's name lists.
     * Tries multiple common name list key patterns used across cultures.
     */
    private static String getRandomFirstName(Culture culture, int gender, RandomSource random) {
        String[] keys = gender == MillVillager.FEMALE
                ? new String[]{"women_names", "female_first", "low_caste_women_names", "high_caste_women_names"}
                : new String[]{"men_names", "male_first"};
        for (String k : keys) {
            List<String> names = culture.nameLists.get(k);
            if (names != null && !names.isEmpty()) return names.get(random.nextInt(names.size()));
        }
        // Last resort: try any name list containing "name"
        for (var entry : culture.nameLists.entrySet()) {
            if (entry.getKey().contains("name") && !entry.getValue().isEmpty()) {
                return entry.getValue().get(random.nextInt(entry.getValue().size()));
            }
        }
        return "Villager";
    }

    /**
     * Get a random family name from a culture's name lists.
     */
    private static String getRandomFamilyName(Culture culture, RandomSource random) {
        String[] keys = {"family_names", "low_caste_family_names", "high_caste_family_names", "noble_family_names"};
        for (String k : keys) {
            List<String> names = culture.nameLists.get(k);
            if (names != null && !names.isEmpty()) return names.get(random.nextInt(names.size()));
        }
        return "";
    }

    /**
     * Generate a village name from the culture's name lists.
     */
    private static String generateVillageName(Culture culture, RandomSource random) {
        String[] keys = {"villages", "village"};
        for (String k : keys) {
            List<String> names = culture.nameLists.get(k);
            if (names != null && !names.isEmpty()) return names.get(random.nextInt(names.size()));
        }
        return culture.key + "_village_" + random.nextInt(10000);
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

    public static int findGroundLevel(ServerLevel level, BlockPos pos) {
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
    public static double evaluateTerrainFlat(ServerLevel level, BlockPos center, int radius) {
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

    /**
     * Prepare terrain for building construction: level the ground, clear vegetation,
     * fill gaps, and create a flat foundation at the building's Y level.
     */
    public static void prepareTerrain(ServerLevel level, Point origin, int width, int length) {
        if (origin == null) return;
        int baseY = origin.y;
        int halfW = width / 2 + 1;
        int halfL = length / 2 + 1;

        for (int dx = -halfW; dx <= halfW; dx++) {
            for (int dz = -halfL; dz <= halfL; dz++) {
                int x = origin.x + dx;
                int z = origin.z + dz;

                // Clear blocks above the building level (trees, tall grass, etc.)
                for (int dy = 0; dy < 12; dy++) {
                    BlockPos above = new BlockPos(x, baseY + dy, z);
                    BlockState aboveState = level.getBlockState(above);
                    if (!aboveState.isAir() && aboveState.canBeReplaced()) {
                        level.setBlock(above, Blocks.AIR.defaultBlockState(), 3);
                    } else if (aboveState.is(Blocks.OAK_LOG) || aboveState.is(Blocks.BIRCH_LOG)
                            || aboveState.is(Blocks.SPRUCE_LOG) || aboveState.is(Blocks.JUNGLE_LOG)
                            || aboveState.is(Blocks.ACACIA_LOG) || aboveState.is(Blocks.DARK_OAK_LOG)
                            || aboveState.is(Blocks.OAK_LEAVES) || aboveState.is(Blocks.BIRCH_LEAVES)
                            || aboveState.is(Blocks.SPRUCE_LEAVES) || aboveState.is(Blocks.JUNGLE_LEAVES)
                            || aboveState.is(Blocks.ACACIA_LEAVES) || aboveState.is(Blocks.DARK_OAK_LEAVES)) {
                        level.setBlock(above, Blocks.AIR.defaultBlockState(), 3);
                    }
                }

                // Fill gaps below the building level to create a flat foundation
                BlockPos groundPos = new BlockPos(x, baseY - 1, z);
                BlockState groundState = level.getBlockState(groundPos);
                if (groundState.isAir() || groundState.is(Blocks.WATER)) {
                    level.setBlock(groundPos, Blocks.DIRT.defaultBlockState(), 3);
                }

                // Clear the block at building level if it's not air
                BlockPos buildPos = new BlockPos(x, baseY, z);
                BlockState buildState = level.getBlockState(buildPos);
                if (!buildState.isAir()) {
                    level.setBlock(buildPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    private static long chunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }

    public static void resetTriedChunks() {
        chunkCoordsTried.clear();
    }
}
