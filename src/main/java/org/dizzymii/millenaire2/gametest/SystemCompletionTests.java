package org.dizzymii.millenaire2.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.entity.EntityTargetedBlaze;
import org.dizzymii.millenaire2.entity.EntityTargetedWitherSkeleton;
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.item.TradeGood;
import org.dizzymii.millenaire2.quest.Quest;
import org.dizzymii.millenaire2.quest.QuestInstance;
import org.dizzymii.millenaire2.quest.QuestStep;
import org.dizzymii.millenaire2.quest.QuestVillager;
import org.dizzymii.millenaire2.quest.SpecialQuestActions;
import org.dizzymii.millenaire2.sound.MillSounds;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.VillagerRecord;
import org.dizzymii.millenaire2.world.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Comprehensive tests for all systems completed during the final release phase.
 * Covers: TradeGood pricing, Quest system, SpecialQuestActions, Point NBT,
 * Targeted entity AI, Sound registration, PointType coverage, and more.
 */
@GameTestHolder(Millenaire2.MODID)
@PrefixGameTestTemplate(false)
public class SystemCompletionTests {

    // ==================== TradeGood: Reputation-Adjusted Pricing ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testTradeGoodBuyPriceNoReputation(GameTestHelper helper) {
        TradeGood good = new TradeGood(new ItemStack(Items.IRON_INGOT), 100, 50);
        int price = good.getAdjustedBuyPrice(0);
        helper.assertTrue(price == 100, "Buy price at 0 rep should be 100, got " + price);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testTradeGoodBuyPriceMaxReputation(GameTestHelper helper) {
        TradeGood good = new TradeGood(new ItemStack(Items.IRON_INGOT), 100, 50);
        int price = good.getAdjustedBuyPrice(1000);
        // 20% discount: 100 * 0.8 = 80
        helper.assertTrue(price == 80, "Buy price at 1000 rep should be 80, got " + price);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testTradeGoodSellPriceNoReputation(GameTestHelper helper) {
        TradeGood good = new TradeGood(new ItemStack(Items.IRON_INGOT), 100, 50);
        int price = good.getAdjustedSellPrice(0);
        helper.assertTrue(price == 50, "Sell price at 0 rep should be 50, got " + price);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testTradeGoodSellPriceMaxReputation(GameTestHelper helper) {
        TradeGood good = new TradeGood(new ItemStack(Items.IRON_INGOT), 100, 50);
        int price = good.getAdjustedSellPrice(1000);
        // 20% bonus: 50 * 1.2 = 60
        helper.assertTrue(price == 60, "Sell price at 1000 rep should be 60, got " + price);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testTradeGoodBuyPriceNeverBelowOne(GameTestHelper helper) {
        TradeGood good = new TradeGood(new ItemStack(Items.IRON_INGOT), 1, 0);
        int price = good.getAdjustedBuyPrice(1000);
        helper.assertTrue(price >= 1, "Buy price should never be below 1, got " + price);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testTradeGoodMatches(GameTestHelper helper) {
        TradeGood good = new TradeGood(new ItemStack(Items.DIAMOND), 500, 200);
        helper.assertTrue(good.matches(new ItemStack(Items.DIAMOND)),
                "Should match same item");
        helper.assertFalse(good.matches(new ItemStack(Items.EMERALD)),
                "Should not match different item");
        helper.assertFalse(good.matches(ItemStack.EMPTY),
                "Should not match empty stack");
        helper.succeed();
    }

    // ==================== Quest System ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testQuestStepRewardParsing(GameTestHelper helper) {
        Quest quest = new Quest();
        quest.key = "test_rewards";
        quest.steps = new ArrayList<>();
        QuestStep step = new QuestStep(quest, 0);
        step.rewardMoney = 100;
        step.rewardReputation = 25;
        quest.steps.add(step);
        helper.assertTrue(step.rewardMoney == 100, "Step reward money should be 100");
        helper.assertTrue(step.rewardReputation == 25, "Step reward reputation should be 25");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testQuestStepDescriptionFallback(GameTestHelper helper) {
        Quest quest = new Quest();
        quest.key = "test_desc";
        quest.steps = new ArrayList<>();
        QuestStep step = new QuestStep(quest, 0);
        quest.steps.add(step);
        // No descriptions set — should return empty string (HashMap default)
        String desc = step.getDescription("en");
        helper.assertTrue(desc == null || desc.isEmpty(),
                "Description should be null/empty when not set");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testQuestInstanceStepLifecycle(GameTestHelper helper) {
        Quest quest = new Quest();
        quest.key = "test_quest";
        quest.steps = new ArrayList<>();
        QuestStep s1 = new QuestStep(quest, 0);
        s1.rewardMoney = 10;
        QuestStep s2 = new QuestStep(quest, 1);
        s2.rewardMoney = 20;
        quest.steps.add(s1);
        quest.steps.add(s2);

        UserProfile profile = new UserProfile();
        QuestInstance qi = new QuestInstance(null, quest, profile, new HashMap<>(), System.currentTimeMillis());

        helper.assertTrue(qi.currentStep == 0, "Should start at step 0");

        // completeStep requires mw/profile to not be null for tag operations,
        // but with empty tag lists it will just advance the step counter
        qi.mw = new org.dizzymii.millenaire2.world.MillWorldData();
        boolean done = qi.completeStep();
        helper.assertFalse(done, "Should not be done after step 0");
        helper.assertTrue(qi.currentStep == 1, "Should be at step 1");

        done = qi.completeStep();
        helper.assertTrue(done, "Should be done after completing all steps");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testQuestVillagerTestMethod(GameTestHelper helper) {
        QuestVillager qv = new QuestVillager();
        qv.types.add("peasant");

        UserProfile profile = new UserProfile();
        VillagerRecord vr = VillagerRecord.create("norman", "peasant", "John", "Smith", 1);
        helper.assertTrue(qv.testVillager(profile, vr), "Peasant should match peasant requirement");

        VillagerRecord vr2 = VillagerRecord.create("norman", "knight", "Jane", "Doe", 2);
        helper.assertFalse(qv.testVillager(profile, vr2), "Knight should not match peasant requirement");
        helper.succeed();
    }

    // ==================== SpecialQuestActions ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testSpecialQuestActionConstants(GameTestHelper helper) {
        helper.assertTrue(SpecialQuestActions.COMPLETE.equals("_complete"),
                "COMPLETE constant mismatch");
        helper.assertTrue(SpecialQuestActions.ENCHANTMENTTABLE.equals("action_build_enchantment_table"),
                "ENCHANTMENTTABLE constant mismatch");
        helper.assertTrue(SpecialQuestActions.TOPOFTHEWORLD.equals("action_topoftheworld"),
                "TOPOFTHEWORLD constant mismatch");
        helper.assertTrue(SpecialQuestActions.THEVOID.equals("action_thevoid"),
                "THEVOID constant mismatch");
        helper.assertTrue(SpecialQuestActions.UNDERWATER_DIVE.equals("action_underwater_dive"),
                "UNDERWATER_DIVE constant mismatch");
        helper.succeed();
    }

    // ==================== Point NBT Round-Trip ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPointNBTRoundTrip(GameTestHelper helper) {
        Point original = new Point(100, 64, -200);
        CompoundTag tag = new CompoundTag();
        original.writeToNBT(tag, "test");

        Point loaded = Point.readFromNBT(tag, "test");
        helper.assertTrue(loaded != null, "Loaded point should not be null");
        helper.assertTrue(loaded.x == 100, "X mismatch: " + loaded.x);
        helper.assertTrue(loaded.y == 64, "Y mismatch: " + loaded.y);
        helper.assertTrue(loaded.z == -200, "Z mismatch: " + loaded.z);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPointNBTMissingReturnsNull(GameTestHelper helper) {
        CompoundTag tag = new CompoundTag();
        Point loaded = Point.readFromNBT(tag, "missing");
        helper.assertTrue(loaded == null, "Should return null for missing NBT data");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPointDistance(GameTestHelper helper) {
        Point a = new Point(0, 0, 0);
        Point b = new Point(3, 4, 0);
        double dist = a.distanceTo(b);
        helper.assertTrue(Math.abs(dist - 5.0) < 0.01, "Distance should be 5.0, got " + dist);
        helper.succeed();
    }

    // ==================== Targeted Entity AI ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testEntityTargetedBlazeSpawnAndTarget(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 1, 1));

        EntityTargetedBlaze blaze = new EntityTargetedBlaze(
                MillEntities.TARGETED_BLAZE.get(), level);
        blaze.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
        blaze.target = new Point(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

        helper.assertTrue(blaze.target != null, "Target should be set");
        helper.assertTrue(!blaze.isOnFire(), "Targeted blaze should not be on fire");

        // Test NBT round-trip
        CompoundTag tag = new CompoundTag();
        blaze.addAdditionalSaveData(tag);
        helper.assertTrue(tag.contains("targetX"), "NBT should contain targetX");

        EntityTargetedBlaze blaze2 = new EntityTargetedBlaze(
                MillEntities.TARGETED_BLAZE.get(), level);
        blaze2.readAdditionalSaveData(tag);
        helper.assertTrue(blaze2.target != null, "Target should be restored from NBT");
        helper.assertTrue(blaze2.target.x == spawnPos.getX(), "Target X mismatch after NBT");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testEntityTargetedWitherSkeletonNBT(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();

        EntityTargetedWitherSkeleton ws = new EntityTargetedWitherSkeleton(
                MillEntities.TARGETED_WITHER_SKELETON.get(), level);
        ws.target = new Point(50, 70, -100);

        CompoundTag tag = new CompoundTag();
        ws.addAdditionalSaveData(tag);

        EntityTargetedWitherSkeleton ws2 = new EntityTargetedWitherSkeleton(
                MillEntities.TARGETED_WITHER_SKELETON.get(), level);
        ws2.readAdditionalSaveData(tag);
        helper.assertTrue(ws2.target != null, "Target should be restored");
        helper.assertTrue(ws2.target.x == 50 && ws2.target.z == -100,
                "Target coordinates mismatch");
        helper.succeed();
    }

    // ==================== Sound Registration Coverage ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testAllSoundEventsRegistered(GameTestHelper helper) {
        helper.assertTrue(MillSounds.NORMAN_BELLS.isBound(), "NORMAN_BELLS not registered");
        helper.assertTrue(MillSounds.VILLAGER_WORKING.isBound(), "VILLAGER_WORKING not registered");
        helper.assertTrue(MillSounds.VILLAGER_EATING.isBound(), "VILLAGER_EATING not registered");
        helper.assertTrue(MillSounds.VILLAGER_SLEEPING.isBound(), "VILLAGER_SLEEPING not registered");
        helper.assertTrue(MillSounds.VILLAGER_TRADING.isBound(), "VILLAGER_TRADING not registered");
        helper.assertTrue(MillSounds.VILLAGER_GREETING.isBound(), "VILLAGER_GREETING not registered");
        helper.assertTrue(MillSounds.VILLAGER_HURT.isBound(), "VILLAGER_HURT not registered");
        helper.assertTrue(MillSounds.VILLAGER_DEATH.isBound(), "VILLAGER_DEATH not registered");
        helper.assertTrue(MillSounds.CONSTRUCTION_HAMMER.isBound(), "CONSTRUCTION_HAMMER not registered");
        helper.assertTrue(MillSounds.CONSTRUCTION_COMPLETE.isBound(), "CONSTRUCTION_COMPLETE not registered");
        helper.assertTrue(MillSounds.VILLAGER_ATTACK.isBound(), "VILLAGER_ATTACK not registered");
        helper.assertTrue(MillSounds.VILLAGER_BOW_SHOOT.isBound(), "VILLAGER_BOW_SHOOT not registered");
        helper.assertTrue(MillSounds.QUEST_ACCEPTED.isBound(), "QUEST_ACCEPTED not registered");
        helper.assertTrue(MillSounds.QUEST_COMPLETED.isBound(), "QUEST_COMPLETED not registered");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testSoundEventIds(GameTestHelper helper) {
        helper.assertTrue(MillSounds.NORMAN_BELLS.get().getLocation().getPath().equals("norman_bells"),
                "NORMAN_BELLS ID mismatch");
        helper.assertTrue(MillSounds.QUEST_COMPLETED.get().getLocation().getPath().equals("quest_completed"),
                "QUEST_COMPLETED ID mismatch");
        helper.assertTrue(MillSounds.VILLAGER_HURT.get().getLocation().getNamespace().equals("millenaire2"),
                "Sound namespace should be millenaire2");
        helper.succeed();
    }

    // ==================== PointType Coverage ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPointTypeCoverageCount(GameTestHelper helper) {
        int count = org.dizzymii.millenaire2.buildingplan.PointType.colourPoints.size();
        // We registered 130+ colour mappings in registerDefaults
        helper.assertTrue(count >= 120,
                "PointType colour count should be >= 120, got " + count);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPointTypeCoreBlockMappings(GameTestHelper helper) {
        var pts = org.dizzymii.millenaire2.buildingplan.PointType.colourPoints;
        // Cobblestone = 0x808080
        helper.assertTrue(pts.containsKey(0x808080), "Missing cobblestone mapping (0x808080)");
        // Oak planks = 0x905020
        helper.assertTrue(pts.containsKey(0x905020), "Missing oak_planks mapping (0x905020)");
        // Bricks = 0xFF0000
        helper.assertTrue(pts.containsKey(0xFF0000), "Missing bricks mapping (0xFF0000)");
        // Glass pane = 0xFFA000
        helper.assertTrue(pts.containsKey(0xFFA000), "Missing glass_pane mapping (0xFFA000)");
        // Torch = 0xFF8080
        helper.assertTrue(pts.containsKey(0xFF8080), "Missing torch mapping (0xFF8080)");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPointTypeSpecialMappings(GameTestHelper helper) {
        var pts = org.dizzymii.millenaire2.buildingplan.PointType.colourPoints;
        // Empty = 0xFFFFFF
        helper.assertTrue(pts.containsKey(0xFFFFFF), "Missing empty mapping (0xFFFFFF)");
        // Sleeping pos = 0x0000C0
        helper.assertTrue(pts.containsKey(0x0000C0), "Missing sleeping pos mapping (0x0000C0)");
        // Soil = 0x00C000
        helper.assertTrue(pts.containsKey(0x00C000), "Missing soil mapping (0x00C000)");
        // Main chest guess = 0xC00080
        helper.assertTrue(pts.containsKey(0xC00080), "Missing main chest mapping (0xC00080)");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPointTypeMillenaireBlocks(GameTestHelper helper) {
        var pts = org.dizzymii.millenaire2.buildingplan.PointType.colourPoints;
        // Timber frame plain = 0xA07848
        helper.assertTrue(pts.containsKey(0xA07848), "Missing timber_frame_plain (0xA07848)");
        // Thatch = 0xC0A040
        helper.assertTrue(pts.containsKey(0xC0A040), "Missing thatch (0xC0A040)");
        // Mud brick = 0x806040
        helper.assertTrue(pts.containsKey(0x806040), "Missing mud_brick (0x806040)");
        // Paper wall = 0xE0C080
        helper.assertTrue(pts.containsKey(0xE0C080), "Missing paper_wall (0xE0C080)");
        helper.succeed();
    }

    // ==================== UserProfile Trade Integration ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testUserProfileTradeFlow(GameTestHelper helper) {
        UserProfile profile = new UserProfile();
        profile.deniers = 500;

        Point villagePos = new Point(100, 64, 200);
        profile.adjustVillageReputation(villagePos, 50);

        TradeGood good = new TradeGood(new ItemStack(Items.IRON_INGOT), 100, 50);
        int rep = profile.getVillageReputation(villagePos);
        int buyPrice = good.getAdjustedBuyPrice(rep);

        // Rep 50 -> discount = 50/5000 = 0.01 -> price = 100 * 0.99 = 99
        helper.assertTrue(buyPrice == 99, "Buy price at rep 50 should be 99, got " + buyPrice);

        // Simulate buy
        profile.deniers -= buyPrice;
        profile.adjustVillageReputation(villagePos, 1);
        helper.assertTrue(profile.deniers == 401, "Deniers after buy should be 401, got " + profile.deniers);
        helper.assertTrue(profile.getVillageReputation(villagePos) == 51,
                "Rep should be 51 after trade");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testUserProfileNBTDeniersPersistence(GameTestHelper helper) {
        UserProfile profile = new UserProfile();
        profile.deniers = 1234;
        Point vp = new Point(10, 20, 30);
        profile.adjustVillageReputation(vp, 42);

        CompoundTag tag = profile.save();

        UserProfile loaded = UserProfile.load(tag);

        helper.assertTrue(loaded.deniers == 1234, "Deniers not persisted: " + loaded.deniers);
        helper.assertTrue(loaded.getVillageReputation(vp) == 42,
                "Reputation not persisted: " + loaded.getVillageReputation(vp));
        helper.succeed();
    }

    // ==================== Quest Instance Serialization ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testQuestInstanceSerialization(GameTestHelper helper) {
        Quest quest = new Quest();
        quest.key = "norman_intro";
        quest.steps = new ArrayList<>();
        quest.steps.add(new QuestStep(quest, 0));
        quest.steps.add(new QuestStep(quest, 1));

        UserProfile profile = new UserProfile();
        QuestInstance qi = new QuestInstance(null, quest, profile, new HashMap<>(), System.currentTimeMillis());
        qi.uniqueid = 999L;

        String serialized = qi.saveToString();
        helper.assertTrue(serialized != null && !serialized.isEmpty(),
                "Serialized quest should not be empty");
        helper.succeed();
    }

    // ==================== Building Trade Goods ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingTradeGoodsListInitialization(GameTestHelper helper) {
        org.dizzymii.millenaire2.village.Building b = new org.dizzymii.millenaire2.village.Building();
        java.util.List<TradeGood> goods = b.getTradeGoods();
        helper.assertTrue(goods != null, "Trade goods list should not be null");
        helper.succeed();
    }

    // ==================== VillagerRecord Full Fields ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testVillagerRecordFullFields(GameTestHelper helper) {
        VillagerRecord vr = VillagerRecord.create("norman", "knight", "Henri", "De Lion", 1);
        helper.assertTrue("norman".equals(vr.getCultureKey()), "Culture key mismatch");
        helper.assertTrue("knight".equals(vr.type), "Type mismatch");
        helper.assertTrue("Henri".equals(vr.firstName), "First name mismatch");
        helper.assertTrue("De Lion".equals(vr.familyName), "Family name mismatch");
        helper.assertTrue(vr.gender == 1, "Gender should be 1");
        helper.assertFalse(vr.killed, "Should not be killed by default");
        helper.assertFalse(vr.awayhired, "Should not be away-hired by default");
        helper.assertFalse(vr.awayraiding, "Should not be away-raiding by default");
        helper.assertTrue(vr.getVillagerId() != 0, "Villager ID should not be 0");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testVillagerRecordNBTRoundTrip(GameTestHelper helper) {
        VillagerRecord vr = VillagerRecord.create("indian", "farmer", "Raj", "Patel", 1);
        vr.killed = false;
        vr.awayhired = true;

        CompoundTag tag = vr.save();

        VillagerRecord loaded = VillagerRecord.load(tag);

        helper.assertTrue("indian".equals(loaded.getCultureKey()), "Culture key not persisted");
        helper.assertTrue("farmer".equals(loaded.type), "Type not persisted");
        helper.assertTrue("Raj".equals(loaded.firstName), "First name not persisted");
        helper.assertTrue("Patel".equals(loaded.familyName), "Family name not persisted");
        helper.assertTrue(loaded.gender == 1, "Gender not persisted");
        helper.assertTrue(loaded.awayhired, "Awayhired not persisted");
        helper.assertTrue(loaded.getVillagerId() == vr.getVillagerId(), "Villager ID not persisted");
        helper.succeed();
    }
}
