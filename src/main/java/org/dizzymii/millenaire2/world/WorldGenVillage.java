package org.dizzymii.millenaire2.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.buildingplan.BuildingBlock;
import org.dizzymii.millenaire2.buildingplan.SpecialPointTypeList;
import org.dizzymii.millenaire2.buildingplan.PngPlanLoader;
import org.dizzymii.millenaire2.culture.BuildingPlan;
import org.dizzymii.millenaire2.culture.BuildingPlanSet;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.culture.VillageType;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.MillCommonUtilities;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.util.VirtualDir;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.BuildingLocation;
import org.dizzymii.millenaire2.village.ConstructionIP;
import org.dizzymii.millenaire2.village.VillagerRecord;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

        // Load blocks from the PNG plan
        List<BuildingBlock> blocks = loadPlanBlocks(culture, planSet, initialPlan);

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
        location.computeMargins();
        townhall.location = location;
        townhall.mw = worldData;
        townhall.world = level;
        townhall.applyPlanMetadata(planSet, initialPlan);
        applyPlanSpecialPositions(townhall, initialPlan);

        // Set up construction if we have blocks
        if (!blocks.isEmpty()) {
            ConstructionIP cip = new ConstructionIP(location);
            cip.orientation = location.orientation;
            cip.setBlocks(blocks);
            townhall.currentConstruction = cip;
            MillLog.minor("WorldGenVillage", "Construction queued: " + blocks.size() + " blocks for " + planSet.key);
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
     * Load BuildingBlock list from a plan's PNG file via PngPlanLoader.
     */
    public static List<BuildingBlock> loadPlanBlocks(Culture culture, BuildingPlanSet planSet,
                                                       BuildingPlan plan) {
        // Locate the PNG file
        File contentDir = MillCommonUtilities.getMillenaireContentDir();
        File cultureDir = new File(contentDir, "cultures/" + culture.key);
        VirtualDir buildingsDir = new VirtualDir(new File(cultureDir, "buildings"));

        String fileName = plan.pngFileName;
        if (fileName == null) {
            fileName = planSet.key + plan.planIndex + ".png";
        }

        File pngFile = buildingsDir.getChildFileRecursive(fileName);
        if (pngFile == null) pngFile = buildingsDir.getChildFile(fileName);
        if (pngFile == null || !pngFile.exists()) {
            MillLog.warn("WorldGenVillage", "PNG plan file not found: " + fileName);
            return new ArrayList<>();
        }

        Map<String, List<int[]>> specialPositions = new HashMap<>();
        List<BuildingBlock> blocks = PngPlanLoader.loadPlan(pngFile, plan.width, plan.altitudeOffset, specialPositions);
        plan.specialPositions.clear();
        plan.specialPositions.putAll(specialPositions);
        return blocks;
    }

    public static void applyPlanSpecialPositions(Building building, BuildingPlan plan) {
        if (building.location == null || building.location.pos == null || plan == null) {
            return;
        }
        Point origin = building.location.pos;
        int orientation = building.location.orientation;
        building.location.chestPos = firstWorldPoint(plan, origin, orientation,
                SpecialPointTypeList.bmainchestGuess,
                SpecialPointTypeList.bmainchestTop,
                SpecialPointTypeList.bmainchestBottom,
                SpecialPointTypeList.bmainchestLeft,
                SpecialPointTypeList.bmainchestRight,
                SpecialPointTypeList.blockedchestGuess,
                SpecialPointTypeList.blockedchestTop,
                SpecialPointTypeList.blockedchestBottom,
                SpecialPointTypeList.blockedchestLeft,
                SpecialPointTypeList.blockedchestRight);
        building.location.sleepingPos = firstWorldPoint(plan, origin, orientation, SpecialPointTypeList.bsleepingPos);
        building.location.sellingPos = firstWorldPoint(plan, origin, orientation, SpecialPointTypeList.bsellingPos);
        building.location.craftingPos = firstWorldPoint(plan, origin, orientation, SpecialPointTypeList.bcraftingPos);
        building.location.shelterPos = firstWorldPoint(plan, origin, orientation, SpecialPointTypeList.bshelterPos);
        building.location.defendingPos = firstWorldPoint(plan, origin, orientation, SpecialPointTypeList.bdefendingPos);
        building.location.leisurePos = firstWorldPoint(plan, origin, orientation, SpecialPointTypeList.bleasurePos);
        building.resManager.sleepingPositions.clear();
        if (building.location.sleepingPos != null) {
            building.resManager.sleepingPositions.add(building.location.sleepingPos);
        }
        building.resManager.stalls.clear();
        List<int[]> stalls = plan.specialPositions.get(SpecialPointTypeList.bstall);
        if (stalls != null) {
            for (int[] coords : stalls) {
                Point stall = toWorldPoint(building.location.pos, building.location.orientation, coords);
                if (stall != null) {
                    building.resManager.stalls.add(stall);
                }
            }
        }
    }

    @Nullable
    private static Point firstWorldPoint(BuildingPlan plan, @Nullable Point origin, int orientation, String... keys) {
        if (origin == null) {
            return null;
        }
        for (String key : keys) {
            List<int[]> positions = plan.specialPositions.get(key);
            if (positions == null || positions.isEmpty()) {
                continue;
            }
            Point point = toWorldPoint(origin, orientation, positions.get(0));
            if (point != null) {
                return point;
            }
        }
        return null;
    }

    @Nullable
    private static Point toWorldPoint(@Nullable Point origin, int orientation, int[] coords) {
        if (origin == null || coords == null || coords.length < 3) {
            return null;
        }
        int x = coords[0];
        int y = coords[1];
        int z = coords[2];
        int rx;
        int rz;
        switch (orientation) {
            case 1 -> {
                rx = z;
                rz = -x;
            }
            case 2 -> {
                rx = -x;
                rz = -z;
            }
            case 3 -> {
                rx = -z;
                rz = x;
            }
            default -> {
                rx = x;
                rz = z;
            }
        }
        return origin.getRelative(rx, y, rz);
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
                    getRandomName(culture, "male_first", random),
                    getRandomName(culture, "family", random),
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

            String nameListKey = gender == MillVillager.FEMALE ? "female_first" : "male_first";
            VillagerRecord vr = VillagerRecord.create(
                    culture.key, vtKey,
                    getRandomName(culture, nameListKey, random),
                    getRandomName(culture, "family", random),
                    gender);
            vr.setHousePos(villagePos);
            vr.setTownHallPos(villagePos);
            townhall.addVillagerRecord(vr);
        }
    }

    /**
     * Get a random name from a culture's name list.
     */
    private static String getRandomName(Culture culture, String listKey, RandomSource random) {
        List<String> names = culture.nameLists.get(listKey);
        if (names != null && !names.isEmpty()) {
            return names.get(random.nextInt(names.size()));
        }
        return "Villager";
    }

    /**
     * Generate a village name from the culture's name lists.
     */
    private static String generateVillageName(Culture culture, RandomSource random) {
        List<String> villageNames = culture.nameLists.get("village");
        if (villageNames != null && !villageNames.isEmpty()) {
            return villageNames.get(random.nextInt(villageNames.size()));
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
