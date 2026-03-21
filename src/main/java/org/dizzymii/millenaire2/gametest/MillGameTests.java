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
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.ConstructionIP;
import org.dizzymii.millenaire2.village.VillagerRecord;
import org.dizzymii.millenaire2.world.MillWorldData;
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

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testTradeGoodSupplyAdjustedPricing(GameTestHelper helper) {
        org.dizzymii.millenaire2.item.TradeGood good = new org.dizzymii.millenaire2.item.TradeGood(
                new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WHEAT),
                100,
                80,
                4,
                "wheat"
        );

        int buyLowStock = good.getSupplyAdjustedBuyPrice(0, 0);
        int buyHighStock = good.getSupplyAdjustedBuyPrice(0, 128);
        helper.assertTrue(buyLowStock > buyHighStock,
                "Buy price should be higher at low stock, got low=" + buyLowStock + ", high=" + buyHighStock);

        int sellLowStock = good.getSupplyAdjustedSellPrice(0, 0);
        int sellHighStock = good.getSupplyAdjustedSellPrice(0, 128);
        helper.assertTrue(sellLowStock > sellHighStock,
                "Sell price should be higher at low stock, got low=" + sellLowStock + ", high=" + sellHighStock);

        helper.assertTrue(good.quantity == 4, "Trade quantity should be 4");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBuildingTradeStockAndExecution(GameTestHelper helper) {
        Building b = new Building();
        org.dizzymii.millenaire2.item.InvItem wheat = org.dizzymii.millenaire2.item.InvItem.get("wheat");
        if (wheat == null) {
            helper.succeed();
            return;
        }

        org.dizzymii.millenaire2.item.TradeGood good = new org.dizzymii.millenaire2.item.TradeGood(
                wheat.getItemStack(1),
                5,
                3,
                4,
                "wheat"
        );
        b.tradeGoods.add(good);

        b.resManager.storeGoods(wheat, 3);
        helper.assertTrue(b.calculateSellingGoods(0).isEmpty(),
                "Building should not sell when stock is below required quantity");

        b.resManager.storeGoods(wheat, 2);
        helper.assertTrue(b.calculateSellingGoods(0).size() == 1,
                "Building should sell once stock reaches required quantity");

        boolean buyOk = b.executeBuyFromBuilding(good, 4);
        helper.assertTrue(buyOk, "executeBuyFromBuilding should succeed with enough stock");
        helper.assertTrue(b.resManager.countGoods(wheat) == 1,
                "Expected stock 1 after selling 4 from stock 5");

        boolean buyFail = b.executeBuyFromBuilding(good, 4);
        helper.assertFalse(buyFail, "executeBuyFromBuilding should fail with insufficient stock");

        boolean sellOk = b.executeSellToBuilding(good, 2);
        helper.assertTrue(sellOk, "executeSellToBuilding should succeed");
        helper.assertTrue(b.resManager.countGoods(wheat) >= 2,
                "Stock should increase after executeSellToBuilding");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testTradeGoodLoaderCultureGoodsFallback(GameTestHelper helper) {
        String villagerTypeKey = "gametest_trade_vtype";
        java.util.List<org.dizzymii.millenaire2.item.TradeGood> cultureGoods = new java.util.ArrayList<>();
        cultureGoods.add(new org.dizzymii.millenaire2.item.TradeGood(
                new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BREAD),
                7,
                0,
                2,
                "bread"
        ));

        org.dizzymii.millenaire2.item.TradeGoodLoader.registerCultureGoods(villagerTypeKey, cultureGoods);
        java.util.List<org.dizzymii.millenaire2.item.TradeGood> resolved =
                org.dizzymii.millenaire2.item.TradeGoodLoader.getTradeGoods(villagerTypeKey);

        helper.assertTrue(!resolved.isEmpty(), "Expected culture goods fallback to return at least one trade good");
        boolean hasBread = false;
        for (org.dizzymii.millenaire2.item.TradeGood tg : resolved) {
            if (!tg.item.isEmpty() && tg.item.is(net.minecraft.world.item.Items.BREAD) && tg.quantity == 2) {
                hasBread = true;
                break;
            }
        }
        helper.assertTrue(hasBread, "Expected registered BREAD trade good with quantity 2");

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

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testConstructionIPOrientationRotation(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        net.minecraft.core.BlockPos origin = helper.absolutePos(new net.minecraft.core.BlockPos(5, 1, 5));

        java.util.List<org.dizzymii.millenaire2.buildingplan.BuildingBlock> blocks = new java.util.ArrayList<>();
        org.dizzymii.millenaire2.buildingplan.BuildingBlock bb =
                new org.dizzymii.millenaire2.buildingplan.BuildingBlock();
        bb.blockState = net.minecraft.world.level.block.Blocks.STONE.defaultBlockState();
        bb.x = 1; bb.y = 0; bb.z = 0;
        blocks.add(bb);

        org.dizzymii.millenaire2.village.BuildingLocation loc =
                new org.dizzymii.millenaire2.village.BuildingLocation();
        loc.pos = new Point(origin.getX(), origin.getY(), origin.getZ());

        ConstructionIP cip = new ConstructionIP(loc);
        cip.orientation = 1;
        cip.setBlocks(blocks);

        int placed = cip.placeBlocks(level, 5);
        helper.assertTrue(placed == 1, "Expected 1 block placed, got " + placed);

        // Orientation 1 rotates local (1,0,0) to world (0,0,-1)
        helper.assertBlockPresent(net.minecraft.world.level.block.Blocks.STONE,
                new net.minecraft.core.BlockPos(5, 1, 4));

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testConstructionIPFactoryPreservesOrientation(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");
        if (norman == null) {
            helper.fail("Norman culture missing");
            return;
        }

        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(BlockPos.ZERO);
        Point origin = new Point(abs.getX(), abs.getY(), abs.getZ());

        ConstructionIP cip = null;
        for (BuildingPlanSet bps : norman.planSets.values()) {
            BuildingPlan initial = bps.getInitialPlan();
            if (initial == null || !initial.hasImage()) {
                continue;
            }
            cip = ConstructionIP.fromBuildingPlan(initial, origin, level, 3, false);
            if (cip != null) {
                break;
            }
        }

        helper.assertFalse(cip == null, "Could not create ConstructionIP from any Norman initial plan");
        if (cip == null) {
            helper.fail("Could not create ConstructionIP from any Norman initial plan");
            return;
        }
        helper.assertTrue(cip.orientation == 3,
                "Expected ConstructionIP orientation 3, got " + cip.orientation);

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

    // ==================== VillagerRecord NBT Round-Trip ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillagerRecordNBTRoundTrip(GameTestHelper helper) {
        VillagerRecord original = VillagerRecord.create("norman", "farmer", "Jean", "Martin", 1);
        original.nb = 2;
        original.size = 17;
        original.scale = 0.95f;
        original.killed = false;
        original.raidingVillage = true;
        original.awayraiding = true;
        original.awayhired = false;
        original.fathersName = "Pierre";
        original.mothersName = "Marie";
        original.spousesName = "Anne";
        original.maidenName = "Dubois";
        original.setHousePos(new Point(100, 65, 200));
        original.setTownHallPos(new Point(96, 65, 196));
        original.originalVillagePos = new Point(32, 70, 32);
        original.setOriginalId(4242L);
        original.addQuestTag("test_quest_tag");

        InvItem wheat = InvItem.get("wheat");
        if (wheat != null) {
            original.addToInv(wheat, 12);
        }

        CompoundTag tag = original.save();
        VillagerRecord loaded = VillagerRecord.load(tag);

        helper.assertTrue(loaded.getVillagerId() == original.getVillagerId(), "Villager id not persisted");
        helper.assertTrue(loaded.gender == original.gender, "Gender not persisted");
        helper.assertTrue(loaded.nb == original.nb, "nb not persisted");
        helper.assertTrue(loaded.size == original.size, "size not persisted");
        helper.assertTrue(Math.abs(loaded.scale - original.scale) < 0.001f, "scale not persisted");
        helper.assertTrue(loaded.raidingVillage == original.raidingVillage, "raidingVillage not persisted");
        helper.assertTrue(loaded.awayraiding == original.awayraiding, "awayraiding not persisted");
        helper.assertTrue(loaded.hasQuestTag("test_quest_tag"), "quest tag not persisted");
        Point loadedHouse = loaded.getHousePos();
        Point originalHouse = original.getHousePos();
        helper.assertTrue(loadedHouse != null && originalHouse != null,
                "house position missing after round-trip");
        if (loadedHouse != null && originalHouse != null) {
            helper.assertTrue(loadedHouse.equals(originalHouse), "house position not persisted");
        }

        Point loadedTownHall = loaded.getTownHallPos();
        Point originalTownHall = original.getTownHallPos();
        helper.assertTrue(loadedTownHall != null && originalTownHall != null,
                "townhall position missing after round-trip");
        if (loadedTownHall != null && originalTownHall != null) {
            helper.assertTrue(loadedTownHall.equals(originalTownHall), "townhall position not persisted");
        }
        helper.assertTrue(loaded.originalVillagePos != null && loaded.originalVillagePos.equals(original.originalVillagePos),
                "original village position not persisted");

        if (wheat != null) {
            helper.assertTrue(loaded.countInv(wheat) == 12, "inventory not persisted for wheat");
        }

        helper.succeed();
    }

    // ==================== MillWorldData Persistence Round-Trip ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testMillWorldDataPersistenceRoundTrip(GameTestHelper helper) {
        MillWorldData original = new MillWorldData();

        Building th = new Building();
        th.isTownhall = true;
        th.isActive = true;
        th.cultureKey = "norman";
        th.villageTypeKey = "village";
        th.setName("Test Village");
        Point thPos = new Point(200, 70, 200);
        th.setPos(thPos);
        original.addBuilding(th, thPos);

        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Louis", "Martin", 1);
        vr.setHousePos(new Point(204, 70, 202));
        vr.setTownHallPos(thPos);
        original.addVillagerRecord(vr);
        original.addGlobalTag("gametest_tag");

        CompoundTag saved = original.save(new CompoundTag(), helper.getLevel().registryAccess());
        MillWorldData loaded = MillWorldData.load(saved, helper.getLevel().registryAccess());

        helper.assertTrue(loaded.hasGlobalTag("gametest_tag"), "Global tag not persisted");
        helper.assertTrue(loaded.getVillagerRecord(vr.getVillagerId()) != null, "Villager record not persisted");
        helper.assertTrue(loaded.getBuilding(thPos) != null, "Building not persisted");
        helper.assertTrue(!loaded.villagesList.pos.isEmpty(), "Village list not persisted");
        helper.assertTrue(loaded.villagesList.pos.get(0).equals(thPos), "Village list position mismatch");

        helper.succeed();
    }
}
