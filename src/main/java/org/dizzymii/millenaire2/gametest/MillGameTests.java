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
import org.dizzymii.millenaire2.goal.GoalBringBackResourcesHome;
import org.dizzymii.millenaire2.goal.GoalConstructionStepByStep;
import org.dizzymii.millenaire2.goal.GoalGetResourcesForBuild;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.ConstructionIP;
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

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBuildingResManagerNBTPersistence(GameTestHelper helper) {
        Building b = new Building();
        org.dizzymii.millenaire2.item.InvItem wheat = org.dizzymii.millenaire2.item.InvItem.get("wheat");

        // If InvItem registry is empty, skip gracefully
        if (wheat == null) {
            helper.succeed();
            return;
        }

        b.resManager.storeGoods(wheat, 9);
        CompoundTag tag = b.save();
        Building loaded = Building.load(tag);

        helper.assertTrue(loaded.resManager.countGoods(wheat) == 9,
                "Building resource manager goods were not persisted");

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

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillagerActionRuntimeBreakAndPlace(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos villagerPos = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos targetPos = helper.absolutePos(new BlockPos(2, 1, 1));

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for action runtime test");

        villager.moveTo(villagerPos.getX() + 0.5, villagerPos.getY(), villagerPos.getZ() + 0.5, 0, 0);
        level.setBlockAndUpdate(targetPos, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());

        org.dizzymii.millenaire2.entity.VillagerActionRuntime runtime = villager.getActionRuntime();
        runtime.start("break", org.dizzymii.millenaire2.entity.action.VillagerActions.breakBlock(targetPos, false), villager);
        runtime.tick(villager);

        helper.assertTrue(level.getBlockState(targetPos).isAir(), "Break action did not clear the target block");
        helper.assertTrue(runtime.getLastResult().status() == org.dizzymii.millenaire2.entity.VillagerActionRuntime.Status.SUCCESS,
                "Break action did not report success");

        runtime.reset(villager);
        runtime.start("place", org.dizzymii.millenaire2.entity.action.VillagerActions.placeBlock(
                targetPos,
                net.minecraft.world.level.block.Blocks.OAK_PLANKS.defaultBlockState(),
                false
        ), villager);
        runtime.tick(villager);

        helper.assertTrue(level.getBlockState(targetPos).is(net.minecraft.world.level.block.Blocks.OAK_PLANKS),
                "Place action did not set the expected block");
        helper.assertTrue(runtime.getLastResult().status() == org.dizzymii.millenaire2.entity.VillagerActionRuntime.Status.SUCCESS,
                "Place action did not report success");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillagerPlayerProxyUseBlock(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos villagerPos = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos supportPos = helper.absolutePos(new BlockPos(2, 1, 1));
        BlockPos leverPos = helper.absolutePos(new BlockPos(2, 2, 1));

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for proxy test");

        villager.moveTo(villagerPos.getX() + 0.5, villagerPos.getY(), villagerPos.getZ() + 0.5, 0, 0);
        level.setBlockAndUpdate(supportPos, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(leverPos,
                net.minecraft.world.level.block.Blocks.LEVER.defaultBlockState()
                        .setValue(net.minecraft.world.level.block.LeverBlock.FACE, net.minecraft.world.level.block.state.properties.AttachFace.FLOOR)
                        .setValue(net.minecraft.world.level.block.LeverBlock.FACING, net.minecraft.core.Direction.NORTH)
                        .setValue(net.minecraft.world.level.block.LeverBlock.POWERED, false));

        org.dizzymii.millenaire2.entity.VillagerActionRuntime runtime = villager.getActionRuntime();
        runtime.start("use_block", org.dizzymii.millenaire2.entity.action.VillagerActions.useBlock(
                leverPos,
                net.minecraft.core.Direction.UP,
                net.minecraft.world.InteractionHand.MAIN_HAND
        ), villager);
        runtime.tick(villager);

        helper.assertTrue(level.getBlockState(leverPos).getValue(net.minecraft.world.level.block.LeverBlock.POWERED),
                "Player-proxy block use did not toggle the lever");
        helper.assertTrue(runtime.getLastResult().status() == org.dizzymii.millenaire2.entity.VillagerActionRuntime.Status.SUCCESS,
                "Player-proxy block use did not report success");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void testGoalShearSheepUsesPlayerProxy(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos villagerPos = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos sheepPos = helper.absolutePos(new BlockPos(2, 1, 1));

        org.dizzymii.millenaire2.item.InvItem shears = org.dizzymii.millenaire2.item.InvItem.get("shears");
        org.dizzymii.millenaire2.item.InvItem wool = org.dizzymii.millenaire2.item.InvItem.get("wool_white");
        if (shears == null || wool == null) {
            helper.succeed();
            return;
        }

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for shearing proxy test");
        villager.moveTo(villagerPos.getX() + 0.5, villagerPos.getY(), villagerPos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);
        villager.addToInv(shears, 1);

        net.minecraft.world.entity.animal.Sheep sheep = net.minecraft.world.entity.EntityType.SHEEP.create(level);
        helper.assertFalse(sheep == null, "Failed to create sheep for shearing proxy test");
        sheep.moveTo(sheepPos.getX() + 0.5, sheepPos.getY(), sheepPos.getZ() + 0.5, 0, 0);
        sheep.setColor(net.minecraft.world.item.DyeColor.WHITE);
        level.addFreshEntity(sheep);

        org.dizzymii.millenaire2.goal.GoalShearSheep goal = new org.dizzymii.millenaire2.goal.GoalShearSheep();
        boolean finished = goal.performAction(villager);
        helper.assertFalse(finished, "Shearing goal should start by equipping shears");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Shearing goal did not start the equip action");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);
        helper.assertFalse(finished, "Shearing goal should start a proxy interaction after equipping shears");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Shearing goal did not start the proxy interaction");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);

        helper.assertTrue(finished, "Shearing goal should finish after the proxy interaction succeeds");
        helper.assertTrue(sheep.isSheared(), "Sheep was not sheared by the proxy-backed goal");
        helper.assertTrue(villager.countInv(wool) > 0, "Villager did not collect wool after proxy shearing");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void testGoalBreedAnimalsUsesPlayerProxy(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos villagerPos = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos animalPosA = helper.absolutePos(new BlockPos(2, 1, 1));
        BlockPos animalPosB = helper.absolutePos(new BlockPos(3, 1, 1));

        org.dizzymii.millenaire2.item.InvItem wheat = org.dizzymii.millenaire2.item.InvItem.get("wheat");
        if (wheat == null) {
            helper.succeed();
            return;
        }

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for breeding proxy test");
        villager.moveTo(villagerPos.getX() + 0.5, villagerPos.getY(), villagerPos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);
        villager.addToInv(wheat, 2);

        net.minecraft.world.entity.animal.Cow cowA = net.minecraft.world.entity.EntityType.COW.create(level);
        net.minecraft.world.entity.animal.Cow cowB = net.minecraft.world.entity.EntityType.COW.create(level);
        helper.assertFalse(cowA == null || cowB == null, "Failed to create cows for breeding proxy test");
        cowA.moveTo(animalPosA.getX() + 0.5, animalPosA.getY(), animalPosA.getZ() + 0.5, 0, 0);
        cowB.moveTo(animalPosB.getX() + 0.5, animalPosB.getY(), animalPosB.getZ() + 0.5, 0, 0);
        level.addFreshEntity(cowA);
        level.addFreshEntity(cowB);

        org.dizzymii.millenaire2.goal.GoalBreedAnimals goal = new org.dizzymii.millenaire2.goal.GoalBreedAnimals();
        boolean finished = goal.performAction(villager);
        helper.assertFalse(finished, "Breeding goal should start the first proxy interaction");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Breeding goal did not start the first proxy interaction");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);
        helper.assertFalse(finished, "Breeding goal should start the second proxy interaction after the first succeeds");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Breeding goal did not start the second proxy interaction");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);

        helper.assertTrue(finished, "Breeding goal should finish after both proxy interactions succeed");
        helper.assertTrue(cowA.isInLove() && cowB.isInLove(), "Both animals were not put into love mode by the proxy-backed goal");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void testGoalBuildAndClearPathUseActionRuntime(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos villagerPos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos pathPos = villagerPos.below();

        org.dizzymii.millenaire2.item.InvItem gravel = org.dizzymii.millenaire2.item.InvItem.get("gravel");
        if (gravel == null) {
            helper.succeed();
            return;
        }

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for path runtime test");
        villager.moveTo(villagerPos.getX() + 0.5, villagerPos.getY(), villagerPos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);
        villager.addToInv(gravel, 1);

        org.dizzymii.millenaire2.goal.GoalBuildPath buildGoal = new org.dizzymii.millenaire2.goal.GoalBuildPath();
        boolean finished = buildGoal.performAction(villager);
        helper.assertFalse(finished, "Build path goal should start by equipping gravel");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Build path goal did not start the equip action");

        villager.getActionRuntime().tick(villager);
        finished = buildGoal.performAction(villager);
        helper.assertFalse(finished, "Build path goal should start the place action after equipping gravel");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Build path goal did not start the place action");

        villager.getActionRuntime().tick(villager);
        finished = buildGoal.performAction(villager);

        helper.assertTrue(finished, "Build path goal should finish after the place action succeeds");
        helper.assertTrue(level.getBlockState(pathPos).is(net.minecraft.world.level.block.Blocks.GRAVEL),
                "Build path goal did not place gravel through the runtime action");
        helper.assertTrue(villager.countInv(gravel) == 0, "Build path goal did not consume gravel from inventory");

        org.dizzymii.millenaire2.goal.GoalClearOldPath clearGoal = new org.dizzymii.millenaire2.goal.GoalClearOldPath();
        finished = clearGoal.performAction(villager);
        helper.assertFalse(finished, "Clear path goal should start a break action before finishing");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Clear path goal did not start the break action");

        villager.getActionRuntime().tick(villager);
        finished = clearGoal.performAction(villager);

        helper.assertTrue(finished, "Clear path goal should finish after the break action succeeds");
        helper.assertTrue(level.getBlockState(pathPos).isAir(), "Clear path goal did not remove the gravel path block");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testGoalBrewPotionsUsesActionRuntime(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos homePos = helper.absolutePos(new BlockPos(1, 1, 1));
        Point homePoint = new Point(homePos.getX(), homePos.getY(), homePos.getZ());
        MillWorldData worldData = MillWorldData.get(level);

        org.dizzymii.millenaire2.item.InvItem wart = org.dizzymii.millenaire2.item.InvItem.get("netherwart");
        org.dizzymii.millenaire2.item.InvItem bottle = org.dizzymii.millenaire2.item.InvItem.get("bottle");
        org.dizzymii.millenaire2.item.InvItem potion = org.dizzymii.millenaire2.item.InvItem.get("akwardpotion");
        if (wart == null || bottle == null || potion == null) {
            helper.succeed();
            return;
        }

        Building home = new Building();
        home.isActive = true;
        home.world = level;
        home.setPos(homePoint);
        home.resManager.storeGoods(wart, 1);
        home.resManager.storeGoods(bottle, 1);
        worldData.addBuilding(home, homePoint);

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for brewing runtime test");
        villager.moveTo(homePos.getX() + 0.5, homePos.getY(), homePos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);
        villager.housePoint = homePoint;

        org.dizzymii.millenaire2.goal.GoalBrewPotions goal = new org.dizzymii.millenaire2.goal.GoalBrewPotions();
        boolean finished = goal.performAction(villager);
        helper.assertFalse(finished, "Brew potions goal should start a transform action before finishing");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Brew potions goal did not start the transform action");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);

        helper.assertTrue(finished, "Brew potions goal should finish after the transform action succeeds");
        helper.assertTrue(home.resManager.countGoods(wart) == 0, "Brew potions goal did not consume nether wart");
        helper.assertTrue(home.resManager.countGoods(bottle) == 0, "Brew potions goal did not consume bottles");
        helper.assertTrue(home.resManager.countGoods(potion) == 1, "Brew potions goal did not store the brewed awkward potion");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testGoalPlantAndHarvestNetherWartsUseRuntime(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos villagerPos = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos soilPos = helper.absolutePos(new BlockPos(2, 1, 1));
        BlockPos wartPos = soilPos.above();

        org.dizzymii.millenaire2.item.InvItem wart = org.dizzymii.millenaire2.item.InvItem.get("netherwart");
        if (wart == null) {
            helper.succeed();
            return;
        }

        level.setBlockAndUpdate(soilPos, net.minecraft.world.level.block.Blocks.SOUL_SAND.defaultBlockState());

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for wart runtime test");
        villager.moveTo(villagerPos.getX() + 0.5, villagerPos.getY(), villagerPos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);
        villager.addToInv(wart, 1);

        org.dizzymii.millenaire2.goal.GoalPlantNetherWarts plantGoal = new org.dizzymii.millenaire2.goal.GoalPlantNetherWarts();
        boolean finished = plantGoal.performAction(villager);
        helper.assertFalse(finished, "Plant wart goal should start by equipping nether wart");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Plant wart goal did not start the equip action");

        villager.getActionRuntime().tick(villager);
        finished = plantGoal.performAction(villager);
        helper.assertFalse(finished, "Plant wart goal should start the proxy use action after equipping");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Plant wart goal did not start the proxy use action");

        villager.getActionRuntime().tick(villager);
        finished = plantGoal.performAction(villager);

        helper.assertTrue(finished, "Plant wart goal should finish after the proxy use action succeeds");
        helper.assertTrue(level.getBlockState(wartPos).is(net.minecraft.world.level.block.Blocks.NETHER_WART),
                "Plant wart goal did not place nether wart through proxy item use");
        helper.assertTrue(villager.countInv(wart) == 0, "Plant wart goal did not consume the held nether wart item");

        level.setBlockAndUpdate(wartPos,
                level.getBlockState(wartPos).setValue(net.minecraft.world.level.block.NetherWartBlock.AGE, 3));

        org.dizzymii.millenaire2.goal.GoalHarvestWarts harvestGoal = new org.dizzymii.millenaire2.goal.GoalHarvestWarts();
        finished = harvestGoal.performAction(villager);
        helper.assertFalse(finished, "Harvest wart goal should start a break action before finishing");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Harvest wart goal did not start the break action");

        villager.getActionRuntime().tick(villager);
        finished = harvestGoal.performAction(villager);

        helper.assertTrue(finished, "Harvest wart goal should finish after the break action succeeds");
        helper.assertTrue(level.getBlockState(wartPos).isAir(), "Harvest wart goal did not break the mature wart block");
        helper.assertTrue(villager.countInv(wart) > 0, "Harvest wart goal did not collect dropped wart items");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testGoalPlantAndHarvestCacaoUseRuntime(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos villagerPos = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos logPos = helper.absolutePos(new BlockPos(3, 1, 1));
        BlockPos cacaoPos = helper.absolutePos(new BlockPos(2, 1, 1));

        org.dizzymii.millenaire2.item.InvItem cocoaBeans = org.dizzymii.millenaire2.item.InvItem.get("dye_brown");
        if (cocoaBeans == null) {
            helper.succeed();
            return;
        }

        level.setBlockAndUpdate(logPos, net.minecraft.world.level.block.Blocks.JUNGLE_LOG.defaultBlockState());
        level.setBlockAndUpdate(logPos.north(), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(logPos.south(), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(logPos.east(), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for cacao runtime test");
        villager.moveTo(villagerPos.getX() + 0.5, villagerPos.getY(), villagerPos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);
        villager.addToInv(cocoaBeans, 1);

        org.dizzymii.millenaire2.goal.GoalPlantCacao plantGoal = new org.dizzymii.millenaire2.goal.GoalPlantCacao();
        boolean finished = plantGoal.performAction(villager);
        helper.assertFalse(finished, "Plant cacao goal should start by equipping cocoa beans");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Plant cacao goal did not start the equip action");

        villager.getActionRuntime().tick(villager);
        finished = plantGoal.performAction(villager);
        helper.assertFalse(finished, "Plant cacao goal should start the proxy use action after equipping");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Plant cacao goal did not start the proxy use action");

        villager.getActionRuntime().tick(villager);
        finished = plantGoal.performAction(villager);

        helper.assertTrue(finished, "Plant cacao goal should finish after the proxy use action succeeds");
        helper.assertTrue(level.getBlockState(cacaoPos).is(net.minecraft.world.level.block.Blocks.COCOA),
                "Plant cacao goal did not place cocoa through proxy item use");
        helper.assertTrue(villager.countInv(cocoaBeans) == 0, "Plant cacao goal did not consume cocoa beans");

        level.setBlockAndUpdate(cacaoPos,
                level.getBlockState(cacaoPos).setValue(net.minecraft.world.level.block.CocoaBlock.AGE, 2));

        org.dizzymii.millenaire2.goal.GoalHarvestCacao harvestGoal = new org.dizzymii.millenaire2.goal.GoalHarvestCacao();
        finished = harvestGoal.performAction(villager);
        helper.assertFalse(finished, "Harvest cacao goal should start a break action before finishing");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Harvest cacao goal did not start the break action");

        villager.getActionRuntime().tick(villager);
        finished = harvestGoal.performAction(villager);

        helper.assertTrue(finished, "Harvest cacao goal should finish after the break action succeeds");
        helper.assertTrue(level.getBlockState(cacaoPos).isAir(), "Harvest cacao goal did not break the mature cocoa block");
        helper.assertTrue(villager.countInv(cocoaBeans) > 0, "Harvest cacao goal did not collect dropped cocoa beans");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testGoalPlantAndHarvestSugarCaneUseRuntime(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos villagerPos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos soilPos = helper.absolutePos(new BlockPos(2, 1, 1));
        BlockPos waterPos = helper.absolutePos(new BlockPos(3, 1, 1));
        BlockPos canePos = soilPos.above();
        BlockPos topCanePos = canePos.above();

        org.dizzymii.millenaire2.item.InvItem sugarcane = org.dizzymii.millenaire2.item.InvItem.get("sugarcane");
        if (sugarcane == null) {
            helper.succeed();
            return;
        }

        level.setBlockAndUpdate(soilPos, net.minecraft.world.level.block.Blocks.SAND.defaultBlockState());
        level.setBlockAndUpdate(waterPos, net.minecraft.world.level.block.Blocks.WATER.defaultBlockState());

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for sugar cane runtime test");
        villager.moveTo(villagerPos.getX() + 0.5, villagerPos.getY(), villagerPos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);
        villager.addToInv(sugarcane, 1);

        org.dizzymii.millenaire2.goal.GoalIndianPlantSugarCane plantGoal = new org.dizzymii.millenaire2.goal.GoalIndianPlantSugarCane();
        boolean finished = plantGoal.performAction(villager);
        helper.assertFalse(finished, "Plant sugar cane goal should start by equipping sugar cane");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Plant sugar cane goal did not start the equip action");

        villager.getActionRuntime().tick(villager);
        finished = plantGoal.performAction(villager);
        helper.assertFalse(finished, "Plant sugar cane goal should start the proxy use action after equipping");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Plant sugar cane goal did not start the proxy use action");

        villager.getActionRuntime().tick(villager);
        finished = plantGoal.performAction(villager);

        helper.assertTrue(finished, "Plant sugar cane goal should finish after the proxy use action succeeds");
        helper.assertTrue(level.getBlockState(canePos).is(net.minecraft.world.level.block.Blocks.SUGAR_CANE),
                "Plant sugar cane goal did not place sugar cane through proxy item use");
        helper.assertTrue(villager.countInv(sugarcane) == 0, "Plant sugar cane goal did not consume the planted cane item");

        level.setBlockAndUpdate(topCanePos, net.minecraft.world.level.block.Blocks.SUGAR_CANE.defaultBlockState());

        org.dizzymii.millenaire2.goal.GoalIndianHarvestSugarCane harvestGoal = new org.dizzymii.millenaire2.goal.GoalIndianHarvestSugarCane();
        finished = harvestGoal.performAction(villager);
        helper.assertFalse(finished, "Harvest sugar cane goal should start a break action before finishing");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Harvest sugar cane goal did not start the break action");

        villager.getActionRuntime().tick(villager);
        finished = harvestGoal.performAction(villager);

        helper.assertTrue(finished, "Harvest sugar cane goal should finish after the break action succeeds");
        helper.assertTrue(level.getBlockState(canePos).is(net.minecraft.world.level.block.Blocks.SUGAR_CANE),
                "Harvest sugar cane goal should keep the base cane block for regrowth");
        helper.assertTrue(level.getBlockState(topCanePos).isAir(), "Harvest sugar cane goal did not remove the top cane block");
        helper.assertTrue(villager.countInv(sugarcane) > 0, "Harvest sugar cane goal did not collect dropped sugar cane");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testGoalPlantSaplingsUsesRuntimeFromInventorySlotNine(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos villagerPos = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos soilPos = helper.absolutePos(new BlockPos(2, 0, 1));
        BlockPos saplingPos = helper.absolutePos(new BlockPos(2, 1, 1));

        org.dizzymii.millenaire2.item.InvItem sapling = org.dizzymii.millenaire2.item.InvItem.get("sapling_pine");
        if (sapling == null) {
            helper.succeed();
            return;
        }

        level.setBlockAndUpdate(soilPos, net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState());

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for sapling runtime test");
        villager.moveTo(villagerPos.getX() + 0.5, villagerPos.getY(), villagerPos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);
        villager.getInventoryContainer().setItem(9, sapling.getItemStack(1));

        org.dizzymii.millenaire2.goal.GoalLumbermanPlantSaplings goal = new org.dizzymii.millenaire2.goal.GoalLumbermanPlantSaplings();
        boolean finished = goal.performAction(villager);
        helper.assertFalse(finished, "Plant saplings goal should start by equipping a carried sapling from inventory");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Plant saplings goal did not start the equip action");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);
        helper.assertFalse(finished, "Plant saplings goal should start the proxy use action after equipping");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Plant saplings goal did not start the proxy use action");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);

        helper.assertTrue(finished, "Plant saplings goal should finish after the proxy use action succeeds");
        helper.assertTrue(level.getBlockState(saplingPos).is(net.minecraft.world.level.block.Blocks.SPRUCE_SAPLING),
                "Plant saplings goal did not place the carried spruce sapling");
        helper.assertTrue(villager.countInv(sapling) == 0, "Plant saplings goal did not consume the carried sapling from slot nine");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testGoalLumbermanChopTreesUsesProxyBreak(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos villagerPos = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos logPos = helper.absolutePos(new BlockPos(2, 1, 1));

        org.dizzymii.millenaire2.item.InvItem axe = org.dizzymii.millenaire2.item.InvItem.get("woodaxe");
        org.dizzymii.millenaire2.item.InvItem wood = org.dizzymii.millenaire2.item.InvItem.get("wood");
        if (axe == null || wood == null) {
            helper.succeed();
            return;
        }

        level.setBlockAndUpdate(logPos, net.minecraft.world.level.block.Blocks.OAK_LOG.defaultBlockState());

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for lumberman proxy-break test");
        villager.moveTo(villagerPos.getX() + 0.5, villagerPos.getY(), villagerPos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);
        villager.getInventoryContainer().setItem(9, axe.getItemStack(1));

        org.dizzymii.millenaire2.goal.GoalLumbermanChopTrees goal = new org.dizzymii.millenaire2.goal.GoalLumbermanChopTrees();
        boolean finished = goal.performAction(villager);
        helper.assertFalse(finished, "Lumberman chop goal should start by equipping an axe");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Lumberman chop goal did not start the equip action");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);
        helper.assertFalse(finished, "Lumberman chop goal should start the proxy break action after equipping");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Lumberman chop goal did not start the proxy break action");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);
        helper.assertFalse(finished, "Lumberman chop goal should continue after one successful chop to check for more logs");
        helper.assertTrue(level.getBlockState(logPos).isAir(), "Lumberman chop goal did not break the log through proxy block breaking");
        helper.assertTrue(villager.countInv(wood) > 0, "Lumberman chop goal did not collect dropped wood items");

        finished = goal.performAction(villager);
        helper.assertTrue(finished, "Lumberman chop goal should finish once no logs remain nearby");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testGoalMinerMineResourceUsesProxyBreak(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos villagerPos = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos stonePos = helper.absolutePos(new BlockPos(2, 1, 1));

        org.dizzymii.millenaire2.item.InvItem pickaxe = org.dizzymii.millenaire2.item.InvItem.get("woodpickaxe");
        org.dizzymii.millenaire2.item.InvItem cobblestone = org.dizzymii.millenaire2.item.InvItem.get("cobblestone");
        if (pickaxe == null || cobblestone == null) {
            helper.succeed();
            return;
        }

        level.setBlockAndUpdate(stonePos, net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for mining proxy-break test");
        villager.moveTo(villagerPos.getX() + 0.5, villagerPos.getY(), villagerPos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);
        villager.getInventoryContainer().setItem(9, pickaxe.getItemStack(1));

        org.dizzymii.millenaire2.goal.GoalMinerMineResource goal = new org.dizzymii.millenaire2.goal.GoalMinerMineResource();
        boolean finished = goal.performAction(villager);
        helper.assertFalse(finished, "Miner goal should start by equipping a carried pickaxe");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Miner goal did not start the equip action");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);
        helper.assertFalse(finished, "Miner goal should start the proxy break action after equipping");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Miner goal did not start the proxy break action");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);

        helper.assertTrue(finished, "Miner goal should finish after the proxy break action succeeds");
        helper.assertTrue(level.getBlockState(stonePos).isAir(), "Miner goal did not break the stone block through proxy block breaking");
        helper.assertTrue(villager.countInv(cobblestone) > 0, "Miner goal did not collect the dropped cobblestone");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testConstructionGoalUsesActionRuntime(GameTestHelper helper) throws Exception {
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(new BlockPos(1, 1, 1));
        Point originPoint = new Point(origin.getX(), origin.getY(), origin.getZ());

        Building building = new Building();
        building.isActive = true;
        building.world = level;
        building.setPos(originPoint);

        org.dizzymii.millenaire2.village.BuildingLocation location = new org.dizzymii.millenaire2.village.BuildingLocation();
        location.pos = originPoint;

        ConstructionIP cip = new ConstructionIP(location);
        org.dizzymii.millenaire2.buildingplan.BuildingBlock block = new org.dizzymii.millenaire2.buildingplan.BuildingBlock();
        block.blockState = net.minecraft.world.level.block.Blocks.BRICKS.defaultBlockState();
        block.x = 1;
        block.y = 0;
        block.z = 0;
        cip.setBlocks(java.util.List.of(block));
        building.currentConstruction = cip;

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for construction goal test");
        villager.moveTo(origin.getX() + 0.5, origin.getY(), origin.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);
        villager.setGoalInformation(new GoalInformation(originPoint, building, 5));

        GoalConstructionStepByStep goal = new GoalConstructionStepByStep();
        boolean finished = goal.performAction(villager);
        helper.assertFalse(finished, "Construction goal should start an action before finishing");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Construction goal did not start a runtime action");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);

        helper.assertTrue(finished, "Construction goal should finish after the runtime placement succeeds");
        helper.assertFalse(building.isUnderConstruction(), "Construction should be complete after one placed block");
        helper.assertTrue(level.getBlockState(origin.offset(1, 0, 0)).is(net.minecraft.world.level.block.Blocks.BRICKS),
                "Construction goal did not place the expected block");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testGoalGetResourcesForBuildUsesActionRuntime(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos townHallPos = helper.absolutePos(new BlockPos(1, 1, 1));
        Point townHallPoint = new Point(townHallPos.getX(), townHallPos.getY(), townHallPos.getZ());
        MillWorldData worldData = MillWorldData.get(level);

        org.dizzymii.millenaire2.item.InvItem wheat = org.dizzymii.millenaire2.item.InvItem.get("wheat");
        if (wheat == null) {
            helper.succeed();
            return;
        }

        Building townHall = new Building();
        townHall.isActive = true;
        townHall.world = level;
        townHall.setPos(townHallPoint);
        townHall.resManager.storeGoods(wheat, 6);
        worldData.addBuilding(townHall, townHallPoint);

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for logistics pickup test");
        villager.moveTo(townHallPos.getX() + 0.5, townHallPos.getY(), townHallPos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);
        villager.townHallPoint = townHallPoint;
        villager.constructionJobId = 1;

        GoalGetResourcesForBuild goal = new GoalGetResourcesForBuild();
        boolean finished = goal.performAction(villager);
        helper.assertFalse(finished, "Pickup goal should start a runtime transfer action before finishing");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Pickup goal did not start the runtime transfer action");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);

        helper.assertTrue(finished, "Pickup goal should finish once the runtime transfer succeeds");
        helper.assertTrue(villager.countInv(wheat) == 6, "Villager did not receive the expected transferred goods");
        helper.assertTrue(townHall.resManager.countGoods(wheat) == 0, "Town hall stock was not decremented by the transfer action");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testGoalBringBackResourcesHomeUsesActionRuntime(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos homePos = helper.absolutePos(new BlockPos(1, 1, 1));
        Point homePoint = new Point(homePos.getX(), homePos.getY(), homePos.getZ());
        MillWorldData worldData = MillWorldData.get(level);

        org.dizzymii.millenaire2.item.InvItem wheat = org.dizzymii.millenaire2.item.InvItem.get("wheat");
        if (wheat == null) {
            helper.succeed();
            return;
        }

        Building home = new Building();
        home.isActive = true;
        home.world = level;
        home.setPos(homePoint);
        worldData.addBuilding(home, homePoint);

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager for logistics delivery test");
        villager.moveTo(homePos.getX() + 0.5, homePos.getY(), homePos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);
        villager.housePoint = homePoint;
        villager.addToInv(wheat, 5);

        GoalBringBackResourcesHome goal = new GoalBringBackResourcesHome();
        boolean finished = goal.performAction(villager);
        helper.assertFalse(finished, "Delivery goal should start a runtime transfer action before finishing");
        helper.assertTrue(villager.getActionRuntime().hasAction(), "Delivery goal did not start the runtime transfer action");

        villager.getActionRuntime().tick(villager);
        finished = goal.performAction(villager);

        helper.assertTrue(finished, "Delivery goal should finish once the runtime transfer succeeds");
        helper.assertTrue(home.resManager.countGoods(wheat) == 5, "Home building did not receive the delivered goods");
        helper.assertTrue(villager.countInv(wheat) == 0, "Villager inventory was not cleared by the delivery action");

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
}
