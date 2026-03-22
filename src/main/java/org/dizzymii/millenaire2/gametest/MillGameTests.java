package org.dizzymii.millenaire2.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.culture.BuildingPlan;
import org.dizzymii.millenaire2.culture.BuildingPlanSet;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.culture.VillageType;
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.ConstructionIP;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.UserProfile;
import org.dizzymii.millenaire2.world.WorldGenVillage;

/**
 * GameTest suite for Millenaire2.
 * Covers culture loading, PNG plan resolution, village generation,
 * villager AI, trade system, and village expansion.
 */
@GameTestHolder(Millenaire2.MODID)
@PrefixGameTestTemplate(false)
public class MillGameTests {

    // ==================== Culture Loading ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testCulturesLoaded(GameTestHelper helper) {
        // Cultures should already be loaded by server start
        helper.assertTrue(Culture.LIST_CULTURES.size() >= 7,
                "Expected at least 7 cultures, got " + Culture.LIST_CULTURES.size());

        // Verify specific cultures exist
        helper.assertFalse(Culture.getCultureByName("norman") == null, "Norman culture missing");
        helper.assertFalse(Culture.getCultureByName("indian") == null, "Indian culture missing");
        helper.assertFalse(Culture.getCultureByName("japanese") == null, "Japanese culture missing");
        helper.assertFalse(Culture.getCultureByName("mayan") == null, "Mayan culture missing");
        helper.assertFalse(Culture.getCultureByName("byzantines") == null, "Byzantine culture missing");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testMillWorldDataLoadDefaultsEnableGeneration(GameTestHelper helper) {
        net.minecraft.nbt.CompoundTag root = new net.minecraft.nbt.CompoundTag();
        MillWorldData loaded = MillWorldData.load(root, helper.getLevel().registryAccess());

        helper.assertTrue(loaded.millenaireEnabled,
                "MillWorldData should default millenaireEnabled=true when key is missing");
        helper.assertTrue(loaded.generateVillages,
                "MillWorldData should default generateVillages=true when key is missing");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void testGenerateNewVillageSetsFieldsAndConstruction(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        MillWorldData mw = new MillWorldData();
        mw.world = level; // MillWorldData.world is fine - it's the data store's own field

        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        BlockPos origin = helper.absolutePos(new BlockPos(0, 1, 0));
        boolean generated = WorldGenVillage.generateNewVillage(level, origin, norman, mw, level.random);
        helper.assertTrue(generated, "Expected village generation to succeed");

        Building townhall = mw.getBuilding(new Point(origin.getX(), origin.getY(), origin.getZ()));
        helper.assertFalse(townhall == null, "Townhall should be registered in world data");
        helper.assertTrue(townhall.isTownhall, "Generated building should be townhall");
        helper.assertTrue(townhall.planSetKey != null && !townhall.planSetKey.isEmpty(),
                "Townhall planSetKey must be set for expansion");
        helper.assertTrue(townhall.villageTypeKey != null && !townhall.villageTypeKey.isEmpty(),
                "Townhall villageTypeKey must be set for expansion");
        helper.assertTrue(townhall.currentConstruction != null,
                "Townhall should have queued construction from plan data");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBuildingPlanSetsLoaded(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        // Norman should have building plan sets
        helper.assertTrue(norman.planSets.size() > 0,
                "Norman has no building plan sets");

        // Check a known plan set exists
        boolean hasArmoury = norman.planSets.containsKey("armoury_a");
        helper.assertTrue(hasArmoury, "Norman missing armoury_a plan set");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBuildingPlanSetParamsLoaded(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        BuildingPlanSet armoury = norman.planSets.get("armoury_a");
        helper.assertFalse(armoury == null, "armoury_a plan set missing");

        // Verify building-level params were loaded (the paramName prefix bug fix)
        helper.assertTrue(armoury.width > 0,
                "armoury_a width not loaded, got: " + armoury.width);
        helper.assertTrue(armoury.length > 0,
                "armoury_a length not loaded, got: " + armoury.length);

        helper.succeed();
    }

    // ==================== PNG Plan Loading ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testPngPlansLoaded(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        // Find at least one plan set where the initial plan has an image
        boolean foundImage = false;
        for (BuildingPlanSet bps : norman.planSets.values()) {
            BuildingPlan initial = bps.getInitialPlan();
            if (initial != null && initial.hasImage()) {
                foundImage = true;
                break;
            }
        }
        helper.assertTrue(foundImage,
                "No Norman building plan has a loaded PNG image");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testPngPlanBlockData(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        BuildingPlanSet armoury = norman.planSets.get("armoury_a");
        helper.assertFalse(armoury == null, "armoury_a missing");

        BuildingPlan initial = armoury.getInitialPlan();
        helper.assertFalse(initial == null, "armoury_a initial plan missing");
        helper.assertTrue(initial.hasImage(),
                "armoury_a initial plan PNG not loaded");

        // Verify block data dimensions match declared width/length
        helper.assertTrue(initial.width > 0, "Plan width is 0");
        helper.assertTrue(initial.length > 0, "Plan length is 0");
        helper.assertTrue(initial.nbFloors > 0, "Plan has 0 floors");

        helper.succeed();
    }

    // ==================== Village Types ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillageTypesLoaded(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        helper.assertTrue(norman.villageTypes.size() > 0,
                "Norman has no village types");
        helper.assertTrue(norman.loneBuildingTypes.size() > 0,
                "Norman has no lone building types");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillageTypeHasCentreBuilding(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        boolean hasCentre = false;
        for (VillageType vt : norman.villageTypes.values()) {
            if (vt.centreBuilding != null && !vt.centreBuilding.isEmpty()) {
                hasCentre = true;
                break;
            }
        }
        helper.assertTrue(hasCentre,
                "No Norman village type has a centre building defined");

        helper.succeed();
    }

    // ==================== ConstructionIP ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testConstructionIPFromBuildingPlan(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        // Try all plans until we find one that produces blocks via fromBuildingPlan
        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(BlockPos.ZERO);
        Point origin = new Point(abs.getX(), abs.getY(), abs.getZ());

        ConstructionIP cip = null;
        String testedKey = null;
        for (BuildingPlanSet bps : norman.planSets.values()) {
            for (BuildingPlan plan : bps.plans) {
                if (plan.hasImage()) {
                    cip = ConstructionIP.fromBuildingPlan(plan, origin, level);
                    if (cip != null) {
                        testedKey = bps.key + "/" + plan.upgradeKey;
                        break;
                    }
                }
            }
            if (cip != null) break;
        }

        helper.assertFalse(cip == null,
                "No Norman building plan produced blocks via fromBuildingPlan. "
                + "Check that PointType colour mappings match the PNG pixel colours.");
        helper.assertTrue(!cip.isComplete(),
                "New ConstructionIP for " + testedKey + " should not be complete");

        helper.succeed();
    }

    // ==================== Building Upgrade ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBuildingCanUpgrade(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        // Find a plan set with at least 2 levels
        BuildingPlanSet multiLevel = null;
        for (BuildingPlanSet bps : norman.planSets.values()) {
            if (bps.plans.size() >= 2
                    && bps.getInitialPlan() != null
                    && bps.getInitialPlan().hasImage()) {
                multiLevel = bps;
                break;
            }
        }
        helper.assertFalse(multiLevel == null,
                "No multi-level plan set with images found");

        // Create a building at level 0
        Building b = new Building();
        b.cultureKey = "norman";
        b.planSetKey = multiLevel.key;
        b.buildingLevel = 0;
        b.isActive = true;

        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(BlockPos.ZERO);
        b.setPos(new Point(abs.getX(), abs.getY(), abs.getZ()));
        b.setLevelContext(level, null);

        helper.assertTrue(b.canUpgrade(),
                "Building should be able to upgrade from level 0 to 1");

        helper.succeed();
    }

    // ==================== Villager Spawning ====================

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testVillagerSpawns(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 1, 1));

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create MillVillager");

        villager.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);

        // Verify entity exists in the world
        helper.runAfterDelay(5, () -> {
            helper.assertFalse(villager.isRemoved(), "Villager was removed immediately");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 140)
    public static void testSummonedVillagerHasIdleMovementFallback(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(2, 1, 2));

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager entity");

        villager.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);

        double startX = villager.getX();
        double startZ = villager.getZ();

        helper.runAfterDelay(80, () -> {
            double dx = villager.getX() - startX;
            double dz = villager.getZ() - startZ;
            double movedSq = dx * dx + dz * dz;
            helper.assertTrue(movedSq > 0.04,
                    "Summoned villager should move via idle fallback, movedSq=" + movedSq);
            helper.succeed();
        });
    }

    // ==================== Goal System ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testGoalSystemInitialized(GameTestHelper helper) {
        // Goal.initGoals() should have been called at startup
        helper.assertTrue(Goal.isInitialized(), "Goal registry is null or empty");
        helper.assertTrue(Goal.registeredCount() > 0,
                "No goals registered");

        // Check core goals exist
        helper.assertFalse(Goal.get("sleep") == null, "sleep goal missing");
        helper.assertFalse(Goal.get("hide") == null, "hide goal missing");
        helper.assertFalse(Goal.get("construction") == null, "construction goal missing");
        helper.assertFalse(Goal.get("beseller") == null, "beSeller goal missing");

        helper.succeed();
    }

    // ==================== User Profile / Trade ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testUserProfileDeniers(GameTestHelper helper) {
        UserProfile profile = new UserProfile();
        helper.assertTrue(profile.deniers == 0,
                "New profile should have 0 deniers");

        profile.deniers = 100;
        helper.assertTrue(profile.deniers == 100,
                "Deniers should be 100 after set");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testUserProfileReputation(GameTestHelper helper) {
        UserProfile profile = new UserProfile();
        Point villagePos = new Point(100, 64, 200);

        int rep = profile.getVillageReputation(villagePos);
        helper.assertTrue(rep == 0,
                "New profile should have 0 reputation for unknown village");

        profile.adjustVillageReputation(villagePos, 50);
        rep = profile.getVillageReputation(villagePos);
        helper.assertTrue(rep == 50,
                "Reputation should be 50 after +50 adjustment, got " + rep);

        helper.succeed();
    }

    // ==================== Village Expansion Logic ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBuildingNBTPersistence(GameTestHelper helper) {
        Building b = new Building();
        b.cultureKey = "norman";
        b.planSetKey = "armoury_a";
        b.villageTypeKey = "norman_village";
        b.buildingLevel = 2;
        b.isActive = true;
        b.setPos(new Point(100, 64, 200));
        b.setName("Test Armoury");

        // Save to NBT
        CompoundTag tag = b.save();

        // Load from NBT
        Building loaded = Building.load(tag);
        helper.assertFalse(loaded == null, "Building.load returned null");
        helper.assertTrue("norman".equals(loaded.cultureKey),
                "cultureKey not persisted");
        helper.assertTrue("armoury_a".equals(loaded.planSetKey),
                "planSetKey not persisted");
        helper.assertTrue("norman_village".equals(loaded.villageTypeKey),
                "villageTypeKey not persisted");
        helper.assertTrue(loaded.buildingLevel == 2,
                "buildingLevel not persisted, got " + loaded.buildingLevel);
        helper.assertTrue("Test Armoury".equals(loaded.getName()),
                "name not persisted");

        helper.succeed();
    }

    // ==================== PointType Colour Mapping ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testPointTypeColoursLoaded(GameTestHelper helper) {
        helper.assertTrue(
                org.dizzymii.millenaire2.buildingplan.PointType.colourPoints.size() > 50,
                "Expected >50 PointType colour mappings, got "
                        + org.dizzymii.millenaire2.buildingplan.PointType.colourPoints.size());

        // Spot-check a few well-known colours
        helper.assertFalse(
                org.dizzymii.millenaire2.buildingplan.PointType.colourPoints.get(0x808080) == null,
                "Cobblestone colour 0x808080 missing");
        helper.assertFalse(
                org.dizzymii.millenaire2.buildingplan.PointType.colourPoints.get(0xFF0000) == null,
                "Bricks colour 0xFF0000 missing");

        helper.succeed();
    }

    // ==================== BiomeCultureMapper ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBiomeCultureMapperLoaded(GameTestHelper helper) {
        helper.assertTrue(
                org.dizzymii.millenaire2.world.BiomeCultureMapper.isLoaded(),
                "BiomeCultureMapper should be loaded at server start");

        // selectCulture should return a non-null culture
        ServerLevel level = helper.getLevel();
        net.minecraft.core.BlockPos pos = helper.absolutePos(net.minecraft.core.BlockPos.ZERO);
        Culture culture = org.dizzymii.millenaire2.world.BiomeCultureMapper.selectCulture(
                level, pos, level.random);
        helper.assertFalse(culture == null,
                "BiomeCultureMapper.selectCulture returned null");

        helper.succeed();
    }

    // ==================== VillageEconomyLoader ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillageEconomyLoaderLoaded(GameTestHelper helper) {
        helper.assertTrue(
                org.dizzymii.millenaire2.village.VillageEconomyLoader.isLoaded(),
                "VillageEconomyLoader should be loaded at server start");

        helper.succeed();
    }

    // ==================== Parity Contracts ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testParityContractsAllCriticalPass(GameTestHelper helper) {
        var report = org.dizzymii.millenaire2.parity.ParityContracts.getLastStartupReport();
        helper.assertFalse(report.isEmpty(), "Parity contract report should not be empty after server start");

        for (var result : report) {
            if (result.contract().isCritical()) {
                helper.assertTrue(result.passed(),
                        "Critical parity contract failed: " + result.contract().getId() + " :: " + result.details());
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testParityContractsReportSize(GameTestHelper helper) {
        var report = org.dizzymii.millenaire2.parity.ParityContracts.getLastStartupReport();
        helper.assertTrue(report.size() == org.dizzymii.millenaire2.parity.ParityContract.values().length,
                "Report should have one entry per ParityContract enum value, got " + report.size());
        helper.succeed();
    }

    // ==================== DiplomacyManager ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testDiplomacyManagerLoaded(GameTestHelper helper) {
        helper.assertTrue(
                org.dizzymii.millenaire2.village.DiplomacyManager.isLoaded(),
                "DiplomacyManager should be loaded at server start");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testRaidStartMarksTargetAndRaiders(GameTestHelper helper) {
        org.dizzymii.millenaire2.world.MillWorldData mw = new org.dizzymii.millenaire2.world.MillWorldData();
        ServerLevel level = helper.getLevel();
        mw.world = level; // MillWorldData.world is fine - it's the data store's own field

        Building attacker = new Building();
        attacker.isTownhall = true;
        attacker.isActive = true;
        attacker.setName("AttackerTown");
        attacker.setPos(new Point(0, 64, 0));
        attacker.setTownHallPos(attacker.getPos());
        attacker.setLevelContext(level, mw);

        Building target = new Building();
        target.isTownhall = true;
        target.isActive = true;
        target.setName("TargetTown");
        target.setPos(new Point(40, 64, 0));
        target.setTownHallPos(target.getPos());
        target.setLevelContext(level, mw);

        org.dizzymii.millenaire2.village.VillagerRecord raider = new org.dizzymii.millenaire2.village.VillagerRecord();
        raider.setVillagerId(1L);
        attacker.addVillagerRecord(raider);

        mw.addBuilding(attacker, attacker.getPos());
        mw.addBuilding(target, target.getPos());

        attacker.setRelation(target.getPos(), -100);
        target.setRelation(attacker.getPos(), -100);

        boolean started = org.dizzymii.millenaire2.village.DiplomacyManager.startRaid(attacker, target.getPos(), mw);
        helper.assertTrue(started, "Expected raid to start");
        helper.assertTrue(attacker.raidTarget != null && attacker.raidTarget.equals(target.getPos()),
                "Attacker raidTarget should be set to target townhall");
        helper.assertTrue(target.underAttack, "Target should be underAttack when raid starts");
        helper.assertTrue(raider.awayraiding, "At least one villager should be marked awayraiding");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testRaidUpdateClearsAttackStateAfterDuration(GameTestHelper helper) {
        org.dizzymii.millenaire2.world.MillWorldData mw = new org.dizzymii.millenaire2.world.MillWorldData();
        ServerLevel level = helper.getLevel();
        mw.world = level; // MillWorldData.world is fine - it's the data store's own field

        Building attacker = new Building();
        attacker.isTownhall = true;
        attacker.isActive = true;
        attacker.setPos(new Point(0, 64, 0));
        attacker.setTownHallPos(attacker.getPos());
        attacker.setLevelContext(level, mw);

        Building target = new Building();
        target.isTownhall = true;
        target.isActive = true;
        target.setPos(new Point(40, 64, 0));
        target.setTownHallPos(target.getPos());
        target.setLevelContext(level, mw);

        org.dizzymii.millenaire2.village.VillagerRecord raider = new org.dizzymii.millenaire2.village.VillagerRecord();
        raider.setVillagerId(2L);
        attacker.addVillagerRecord(raider);

        org.dizzymii.millenaire2.village.VillagerRecord defender = new org.dizzymii.millenaire2.village.VillagerRecord();
        defender.setVillagerId(3L);
        target.addVillagerRecord(defender);

        mw.addBuilding(attacker, attacker.getPos());
        mw.addBuilding(target, target.getPos());

        boolean started = org.dizzymii.millenaire2.village.DiplomacyManager.startRaid(attacker, target.getPos(), mw);
        helper.assertTrue(started, "Expected raid to start");

        attacker.activeRaidStartTick = level.getGameTime() - org.dizzymii.millenaire2.village.DiplomacyManager.raidDurationTicks;
        org.dizzymii.millenaire2.village.DiplomacyManager.updateRaidState(attacker, mw);

        helper.assertTrue(attacker.raidTarget == null, "Raid target should be cleared after raid resolution");
        helper.assertFalse(target.underAttack, "Target underAttack should be cleared after raid resolution");
        helper.assertFalse(raider.awayraiding, "Raider should be returned after raid resolution");

        helper.succeed();
    }

    // ==================== BuildingResManager ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBuildingResManagerStoreAndTake(GameTestHelper helper) {
        Building b = new Building();
        org.dizzymii.millenaire2.item.InvItem testItem =
                org.dizzymii.millenaire2.item.InvItem.get("wheat");

        // If InvItem registry is empty, skip gracefully
        if (testItem == null) {
            helper.succeed();
            return;
        }

        b.resManager.storeGoods(testItem, 10);
        helper.assertTrue(b.resManager.countGoods(testItem) == 10,
                "Expected 10 goods stored, got " + b.resManager.countGoods(testItem));

        boolean took = b.resManager.takeGoods(testItem, 3);
        helper.assertTrue(took, "takeGoods should succeed");
        helper.assertTrue(b.resManager.countGoods(testItem) == 7,
                "Expected 7 goods remaining, got " + b.resManager.countGoods(testItem));

        boolean tookTooMany = b.resManager.takeGoods(testItem, 100);
        helper.assertFalse(tookTooMany, "takeGoods should fail when not enough stock");

        helper.succeed();
    }

    // ==================== ConstructionIP Block Placement ====================

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testConstructionIPPlacesBlocks(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        net.minecraft.core.BlockPos origin = helper.absolutePos(new net.minecraft.core.BlockPos(1, 1, 1));

        // Create a small ConstructionIP manually with known blocks
        java.util.List<org.dizzymii.millenaire2.buildingplan.BuildingBlock> blocks = new java.util.ArrayList<>();
        org.dizzymii.millenaire2.buildingplan.BuildingBlock bb1 =
                new org.dizzymii.millenaire2.buildingplan.BuildingBlock();
        bb1.blockState = net.minecraft.world.level.block.Blocks.STONE.defaultBlockState();
        bb1.x = 0; bb1.y = 0; bb1.z = 0;
        blocks.add(bb1);

        org.dizzymii.millenaire2.buildingplan.BuildingBlock bb2 =
                new org.dizzymii.millenaire2.buildingplan.BuildingBlock();
        bb2.blockState = net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState();
        bb2.x = 1; bb2.y = 0; bb2.z = 0;
        blocks.add(bb2);

        org.dizzymii.millenaire2.village.BuildingLocation loc =
                new org.dizzymii.millenaire2.village.BuildingLocation();
        loc.pos = new Point(origin.getX(), origin.getY(), origin.getZ());

        ConstructionIP cip = new ConstructionIP(loc);
        cip.setBlocks(blocks);

        helper.assertFalse(cip.isComplete(), "New CIP should not be complete");
        helper.assertTrue(cip.nbBlocksTotal == 2, "Expected 2 total blocks");

        // Place all blocks
        int placed = cip.placeBlocks(level, 10);
        helper.assertTrue(placed == 2, "Expected 2 blocks placed, got " + placed);
        helper.assertTrue(cip.isComplete(), "CIP should be complete after placing all");

        // Verify blocks exist in the world
        helper.assertBlockPresent(net.minecraft.world.level.block.Blocks.STONE,
                new net.minecraft.core.BlockPos(1, 1, 1));
        helper.assertBlockPresent(net.minecraft.world.level.block.Blocks.OAK_PLANKS,
                new net.minecraft.core.BlockPos(2, 1, 1));

        helper.succeed();
    }

    // ==================== Building Tick Progresses Construction ====================

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testBuildingTickProgressesConstruction(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        net.minecraft.core.BlockPos abs = helper.absolutePos(new net.minecraft.core.BlockPos(1, 1, 1));

        Building b = new Building();
        b.isActive = true;
        b.isTownhall = false;
        b.setLevelContext(level, null);
        b.setPos(new Point(abs.getX(), abs.getY(), abs.getZ()));

        // Set up a small construction
        java.util.List<org.dizzymii.millenaire2.buildingplan.BuildingBlock> blocks = new java.util.ArrayList<>();
        org.dizzymii.millenaire2.buildingplan.BuildingBlock bb =
                new org.dizzymii.millenaire2.buildingplan.BuildingBlock();
        bb.blockState = net.minecraft.world.level.block.Blocks.COBBLESTONE.defaultBlockState();
        bb.x = 0; bb.y = 0; bb.z = 0;
        blocks.add(bb);

        org.dizzymii.millenaire2.village.BuildingLocation loc =
                new org.dizzymii.millenaire2.village.BuildingLocation();
        loc.pos = new Point(abs.getX(), abs.getY(), abs.getZ());

        ConstructionIP cip = new ConstructionIP(loc);
        cip.setBlocks(blocks);
        b.currentConstruction = cip;

        helper.assertTrue(b.isUnderConstruction(),
                "Building should be under construction");

        // Tick 20 times to trigger slowTick
        for (int i = 0; i < 20; i++) {
            b.tick();
        }

        // Construction should be complete (1 block, placed in slowTick)
        helper.assertFalse(b.isUnderConstruction(),
                "Construction should be complete after 20 ticks");
        helper.assertBlockPresent(net.minecraft.world.level.block.Blocks.COBBLESTONE,
                new net.minecraft.core.BlockPos(1, 1, 1));

        helper.succeed();
    }

    // ==================== ConstructionIP NBT Round-Trip ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testConstructionIPNBTRoundTrip(GameTestHelper helper) {
        org.dizzymii.millenaire2.village.BuildingLocation loc =
                new org.dizzymii.millenaire2.village.BuildingLocation();
        loc.pos = new Point(10, 64, 20);

        ConstructionIP original = new ConstructionIP(loc);
        java.util.List<org.dizzymii.millenaire2.buildingplan.BuildingBlock> blocks = new java.util.ArrayList<>();

        org.dizzymii.millenaire2.buildingplan.BuildingBlock bb =
                new org.dizzymii.millenaire2.buildingplan.BuildingBlock();
        bb.blockState = net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState();
        bb.x = 0; bb.y = 0; bb.z = 0;
        blocks.add(bb);

        org.dizzymii.millenaire2.buildingplan.BuildingBlock bb2 =
                new org.dizzymii.millenaire2.buildingplan.BuildingBlock();
        bb2.blockState = net.minecraft.world.level.block.Blocks.OAK_LOG.defaultBlockState();
        bb2.x = 1; bb2.y = 0; bb2.z = 0;
        bb2.secondStep = true;
        blocks.add(bb2);

        original.setBlocks(blocks);
        original.placeNextBlock(helper.getLevel()); // place one block
        helper.assertTrue(original.nbBlocksDone == 1, "Should have placed 1 block");

        // Save and reload
        net.minecraft.nbt.CompoundTag tag = original.save();
        ConstructionIP loaded = ConstructionIP.load(tag);

        helper.assertTrue(loaded.nbBlocksDone == 1, "nbBlocksDone not persisted");
        helper.assertTrue(loaded.nbBlocksTotal == 2, "nbBlocksTotal not persisted");
        helper.assertFalse(loaded.isComplete(), "Loaded CIP should not be complete (1 block remaining)");

        helper.succeed();
    }

    // ==================== MillWorldData NBT Round-Trip ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testMillWorldDataNBTRoundTrip(GameTestHelper helper) {
        MillWorldData original = new MillWorldData();
        original.millenaireEnabled = true;
        original.generateVillages = true;
        original.lastWorldUpdate = 12345L;
        original.addGlobalTag("test_tag_1");

        Building b = new Building();
        b.cultureKey = "norman";
        b.planSetKey = "fort_a";
        b.villageTypeKey = "norman_village";
        b.buildingLevel = 1;
        b.isActive = true;
        b.isTownhall = true;
        b.setPos(new Point(50, 64, 100));
        b.setTownHallPos(new Point(50, 64, 100));
        b.setName("Test Fort");
        original.addBuilding(b, b.getPos());

        CompoundTag tag = original.save(new CompoundTag(), helper.getLevel().registryAccess());
        MillWorldData loaded = MillWorldData.load(tag, helper.getLevel().registryAccess());

        helper.assertTrue(loaded.millenaireEnabled, "millenaireEnabled not persisted");
        helper.assertTrue(loaded.generateVillages, "generateVillages not persisted");
        helper.assertTrue(loaded.lastWorldUpdate == 12345L, "lastWorldUpdate not persisted");
        helper.assertTrue(loaded.hasGlobalTag("test_tag_1"), "globalTag not persisted");
        helper.assertTrue(loaded.allBuildings().size() == 1, "Building count mismatch, got " + loaded.allBuildings().size());

        Building loadedB = loaded.getBuilding(new Point(50, 64, 100));
        helper.assertFalse(loadedB == null, "Building not found at persisted position");
        helper.assertTrue("norman".equals(loadedB.cultureKey), "Building cultureKey not persisted");
        helper.assertTrue("fort_a".equals(loadedB.planSetKey), "Building planSetKey not persisted");
        helper.assertTrue(loadedB.isTownhall, "Building isTownhall not persisted");
        helper.assertTrue("Test Fort".equals(loadedB.getName()), "Building name not persisted");

        helper.succeed();
    }

    // ==================== UserProfile NBT Round-Trip ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testUserProfileNBTRoundTrip(GameTestHelper helper) {
        UserProfile original = new UserProfile();
        original.deniers = 500;
        original.adjustVillageReputation(new Point(100, 64, 200), 75);

        net.minecraft.nbt.CompoundTag tag = original.save();
        UserProfile loaded = UserProfile.load(tag);

        helper.assertTrue(loaded.deniers == 500,
                "Deniers not persisted, got " + loaded.deniers);
        helper.assertTrue(loaded.getVillageReputation(new Point(100, 64, 200)) == 75,
                "Reputation not persisted");

        helper.succeed();
    }
}
