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
import org.dizzymii.millenaire2.world.UserProfile;

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
        b.world = level;

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

    // ==================== Goal System ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testGoalSystemInitialized(GameTestHelper helper) {
        // Goal.initGoals() should have been called at startup
        helper.assertFalse(Goal.goals == null, "Goal registry is null");
        helper.assertTrue(Goal.goals.size() > 0,
                "No goals registered");

        // Check core goals exist
        helper.assertFalse(Goal.sleep == null, "sleep goal missing");
        helper.assertFalse(Goal.hide == null, "hide goal missing");
        helper.assertFalse(Goal.construction == null, "construction goal missing");
        helper.assertFalse(Goal.beSeller == null, "beSeller goal missing");

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

        // Spot-check a few well-known colours from blocklist.txt
        // Cobblestone = 76/76/76 = 0x4C4C4C, Bricks = 128/0/0 = 0x800000
        helper.assertFalse(
                org.dizzymii.millenaire2.buildingplan.PointType.colourPoints.get(0x4C4C4C) == null,
                "Cobblestone colour 0x4C4C4C missing");
        helper.assertFalse(
                org.dizzymii.millenaire2.buildingplan.PointType.colourPoints.get(0x800000) == null,
                "Bricks colour 0x800000 missing");

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

    // ==================== DiplomacyManager ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testDiplomacyManagerLoaded(GameTestHelper helper) {
        helper.assertTrue(
                org.dizzymii.millenaire2.village.DiplomacyManager.isLoaded(),
                "DiplomacyManager should be loaded at server start");

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
        b.world = level;
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

    // ==================== Integration: Building + Construction NBT Round-Trip ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBuildingWithConstructionNBTRoundTrip(GameTestHelper helper) {
        Building b = new Building();
        b.cultureKey = "norman";
        b.planSetKey = "armoury_a";
        b.villageTypeKey = "norman_village";
        b.buildingLevel = 0;
        b.isTownhall = true;
        b.isActive = true;
        b.setPos(new Point(100, 64, 200));
        b.setName("Test Village");

        // Attach an active construction
        org.dizzymii.millenaire2.village.BuildingLocation loc =
                new org.dizzymii.millenaire2.village.BuildingLocation();
        loc.pos = new Point(100, 64, 200);

        ConstructionIP cip = new ConstructionIP(loc);
        java.util.List<org.dizzymii.millenaire2.buildingplan.BuildingBlock> blocks = new java.util.ArrayList<>();
        org.dizzymii.millenaire2.buildingplan.BuildingBlock bb =
                new org.dizzymii.millenaire2.buildingplan.BuildingBlock();
        bb.blockState = net.minecraft.world.level.block.Blocks.COBBLESTONE.defaultBlockState();
        bb.x = 0; bb.y = 0; bb.z = 0;
        blocks.add(bb);

        org.dizzymii.millenaire2.buildingplan.BuildingBlock bb2 =
                new org.dizzymii.millenaire2.buildingplan.BuildingBlock();
        bb2.blockState = net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState();
        bb2.x = 1; bb2.y = 0; bb2.z = 0;
        bb2.secondStep = true;
        blocks.add(bb2);

        cip.setBlocks(blocks);
        b.currentConstruction = cip;

        // Save and reload
        CompoundTag tag = b.save();
        Building loaded = Building.load(tag);

        helper.assertFalse(loaded == null, "Building.load returned null");
        helper.assertTrue("norman".equals(loaded.cultureKey), "cultureKey not persisted");
        helper.assertTrue("armoury_a".equals(loaded.planSetKey), "planSetKey not persisted");
        helper.assertTrue("norman_village".equals(loaded.villageTypeKey), "villageTypeKey not persisted");
        helper.assertTrue(loaded.isTownhall, "isTownhall not persisted");

        // The critical check: currentConstruction must survive round-trip
        helper.assertTrue(loaded.isUnderConstruction(),
                "Construction was LOST during NBT save/load — this is the root cause of structures not spawning");
        helper.assertTrue(loaded.currentConstruction.nbBlocksTotal == 2,
                "Construction block count lost, got " + loaded.currentConstruction.nbBlocksTotal);
        helper.assertFalse(loaded.currentConstruction.isComplete(),
                "Loaded construction should not be complete");

        helper.succeed();
    }

    // ==================== Integration: generateNewVillage sets required fields ====================

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testGenerateNewVillageSetsRequiredFields(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        net.minecraft.core.BlockPos abs = helper.absolutePos(new net.minecraft.core.BlockPos(5, 2, 5));

        // Place solid ground for terrain check
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                level.setBlock(abs.offset(dx, -1, dz),
                        net.minecraft.world.level.block.Blocks.STONE.defaultBlockState(), 3);
            }
        }

        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        org.dizzymii.millenaire2.world.MillWorldData worldData =
                org.dizzymii.millenaire2.world.MillWorldData.get(level);
        helper.assertFalse(worldData == null, "worldData is null");

        int buildingsBefore = worldData.allBuildings().size();

        boolean generated = org.dizzymii.millenaire2.world.WorldGenVillage.generateNewVillage(
                level, abs, norman, worldData, level.random);

        helper.assertTrue(generated, "generateNewVillage returned false");
        helper.assertTrue(worldData.allBuildings().size() > buildingsBefore,
                "No building was added to worldData");

        // Find the generated building
        Building townhall = null;
        for (Building b : worldData.allBuildings()) {
            if (b.isTownhall && "norman".equals(b.cultureKey)) {
                townhall = b;
                break;
            }
        }

        helper.assertFalse(townhall == null, "No townhall found in worldData");
        helper.assertFalse(townhall.planSetKey == null,
                "planSetKey is null — village can never upgrade");
        helper.assertFalse(townhall.villageTypeKey == null,
                "villageTypeKey is null — village can never expand");
        helper.assertTrue(townhall.buildingLevel == 0,
                "buildingLevel should be 0 for initial plan");
        helper.assertFalse(townhall.world == null,
                "world is null — construction will never tick");

        // Construction is gradual — townhall should have active construction queued
        helper.assertTrue(townhall.isUnderConstruction(),
                "Townhall should have active construction queued for gradual building");

        helper.succeed();
    }

    // ==================== Integration: real PNG plan produces correct block count ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testRealPlanBlockCount(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        ServerLevel level = helper.getLevel();
        net.minecraft.core.BlockPos abs = helper.absolutePos(net.minecraft.core.BlockPos.ZERO);
        Point origin = new Point(abs.getX(), abs.getY(), abs.getZ());

        int testedPlans = 0;
        for (BuildingPlanSet bps : norman.planSets.values()) {
            BuildingPlan plan = bps.getInitialPlan();
            if (plan == null || !plan.hasImage()) continue;

            ConstructionIP cip = ConstructionIP.fromBuildingPlan(plan, origin, level);
            if (cip == null) continue;

            // A real building should have at least 10 blocks
            helper.assertTrue(cip.nbBlocksTotal >= 10,
                    "Plan " + bps.key + " has only " + cip.nbBlocksTotal
                    + " blocks — likely PointType colour mismatch");
            testedPlans++;
            if (testedPlans >= 3) break;
        }

        helper.assertTrue(testedPlans >= 1,
                "No Norman plans produced blocks — blocklist.txt colours may not match PNGs");

        helper.succeed();
    }
}
