package org.dizzymii.millenaire2.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.buildingplan.BuildingBlock;
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
        townhall.setName(generateVillageName(culture, random));
        townhall.setPos(villagePos);
        townhall.setTownHallPos(villagePos);
        townhall.location = location;
        townhall.mw = worldData;

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
        // Filter to only auto-generable types (not player-controlled, weight > 0, not lone buildings)
        List<VillageType> eligible = new ArrayList<>();
        for (VillageType vt : culture.listVillageTypes) {
            if (vt.playerControlled) continue;
            if (vt.lonebuilding) continue;
            if (vt.weight <= 0) continue;
            eligible.add(vt);
        }
        if (eligible.isEmpty()) return null;

        int totalWeight = 0;
        for (VillageType vt : eligible) {
            totalWeight += vt.weight;
        }
        int roll = random.nextInt(Math.max(totalWeight, 1));
        int cumulative = 0;
        for (VillageType vt : eligible) {
            cumulative += vt.weight;
            if (roll < cumulative) return vt;
        }
        return eligible.get(0);
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
    private static List<BuildingBlock> loadPlanBlocks(Culture culture, BuildingPlanSet planSet,
                                                       BuildingPlan plan) {
        // Locate the PNG file
        File contentDir = MillCommonUtilities.getMillenaireContentDir();
        File cultureDir = new File(contentDir, "cultures/" + culture.key);
        VirtualDir buildingsDir = new VirtualDir(new File(cultureDir, "buildings"));

        String fileName = plan.pngFileName;
        if (fileName == null) {
            if ("initial".equals(plan.upgradeKey)) {
                fileName = planSet.key + ".png";
            } else {
                fileName = planSet.key + "_" + plan.upgradeKey + ".png";
            }
        }

        File pngFile = buildingsDir.getChildFileRecursive(fileName);
        if (pngFile == null) pngFile = buildingsDir.getChildFile(fileName);
        if (pngFile == null || !pngFile.exists()) {
            MillLog.warn("WorldGenVillage", "PNG plan file not found: " + fileName);
            return new ArrayList<>();
        }

        Map<String, List<int[]>> specialPositions = new HashMap<>();
        return PngPlanLoader.loadPlan(pngFile, plan.width, plan.altitudeOffset, specialPositions);
    }

    /**
     * Create initial VillagerRecords for a new village from the plan's villager definitions.
     */
    private static void createInitialVillagers(Building townhall, Culture culture,
                                                BuildingPlan plan, Point villagePos,
                                                RandomSource random) {
        boolean added = false;

        // Primary: use plan.male / plan.female fields (original Millénaire format)
        if (plan.male != null && !plan.male.isEmpty()) {
            VillagerRecord vr = VillagerRecord.create(
                    culture.key, plan.male,
                    getRandomName(culture, "male_first", random),
                    getRandomName(culture, "family", random),
                    MillVillager.MALE);
            vr.setHousePos(villagePos);
            vr.setTownHallPos(villagePos);
            townhall.addVillagerRecord(vr);
            added = true;
        }
        if (plan.female != null && !plan.female.isEmpty()) {
            VillagerRecord vr = VillagerRecord.create(
                    culture.key, plan.female,
                    getRandomName(culture, "female_first", random),
                    getRandomName(culture, "family", random),
                    MillVillager.FEMALE);
            vr.setHousePos(villagePos);
            vr.setTownHallPos(villagePos);
            townhall.addVillagerRecord(vr);
            added = true;
        }

        // Secondary: use plan.villagers list (per-level villager entries)
        for (String vtKey : plan.villagers) {
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
            added = true;
        }

        // Fallback: find a leader-like type from the culture's registered villager types
        if (!added) {
            String leaderKey = null;
            for (VillagerType vt : culture.listVillagerTypes) {
                if (vt.key.contains("leader") || vt.key.contains("chief")) {
                    leaderKey = vt.key;
                    break;
                }
            }
            if (leaderKey == null && !culture.listVillagerTypes.isEmpty()) {
                leaderKey = culture.listVillagerTypes.get(0).key;
            }
            if (leaderKey != null) {
                VillagerRecord leader = VillagerRecord.create(
                        culture.key, leaderKey,
                        getRandomName(culture, "male_first", random),
                        getRandomName(culture, "family", random),
                        MillVillager.MALE);
                leader.setHousePos(villagePos);
                leader.setTownHallPos(villagePos);
                townhall.addVillagerRecord(leader);
            }
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
