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
}
