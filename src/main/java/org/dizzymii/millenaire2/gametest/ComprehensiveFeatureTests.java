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
import org.dizzymii.millenaire2.block.MillBlocks;
import org.dizzymii.millenaire2.culture.BuildingPlanSet;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.item.MillItems;
import org.dizzymii.millenaire2.item.TradeGood;
import org.dizzymii.millenaire2.quest.Quest;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.BuildingLocation;
import org.dizzymii.millenaire2.village.DiplomacyManager;
import org.dizzymii.millenaire2.village.VillageEconomyLoader;
import org.dizzymii.millenaire2.village.VillagerRecord;
import org.dizzymii.millenaire2.world.UserProfile;

import java.util.UUID;

/**
 * Comprehensive feature tests covering every testable subsystem in Millenaire2.
 * Organized by feature area: utilities, items, goals, diplomacy, economy,
 * building relations, villager records, user profiles, entity features,
 * registry verification, and quest constants.
 */
@GameTestHolder(Millenaire2.MODID)
@PrefixGameTestTemplate(false)
public class ComprehensiveFeatureTests {

    // ==================== Point Utility ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPointConstructorsAndEquals(GameTestHelper helper) {
        Point a = new Point(10, 64, -20);
        Point b = new Point(10, 64, -20);
        Point c = new Point(a);

        helper.assertTrue(a.equals(b), "Equal points should be equal");
        helper.assertTrue(a.equals(c), "Copy constructor should produce equal point");
        helper.assertTrue(a.hashCode() == b.hashCode(), "Equal points must have same hashCode");
        helper.assertFalse(a.equals(new Point(10, 65, -20)), "Different Y should not be equal");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPointDistanceTo(GameTestHelper helper) {
        Point a = new Point(0, 0, 0);
        Point b = new Point(3, 4, 0);

        double dist = a.distanceTo(b);
        helper.assertTrue(Math.abs(dist - 5.0) < 0.001, "Distance 3-4-5 triangle should be 5.0, got " + dist);

        double hDist = a.horizontalDistanceTo(new Point(3, 100, 4));
        helper.assertTrue(Math.abs(hDist - 5.0) < 0.001, "Horizontal distance ignores Y, got " + hDist);

        int hDistSq = a.horizontalDistanceToSquared(new Point(3, 100, 4));
        helper.assertTrue(hDistSq == 25, "Horizontal distance squared should be 25, got " + hDistSq);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPointRelativeMethods(GameTestHelper helper) {
        Point p = new Point(10, 64, 20);

        Point above = p.getAbove();
        helper.assertTrue(above.y == 65, "getAbove should be y+1");
        helper.assertTrue(above.x == 10 && above.z == 20, "getAbove should not change x/z");

        Point below = p.getBelow();
        helper.assertTrue(below.y == 63, "getBelow should be y-1");

        Point rel = p.getRelative(5, -3, 7);
        helper.assertTrue(rel.x == 15 && rel.y == 61 && rel.z == 27,
                "getRelative(5,-3,7) failed: " + rel);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPointToBlockPos(GameTestHelper helper) {
        Point p = new Point(100, 64, -200);
        BlockPos bp = p.toBlockPos();
        helper.assertTrue(bp.getX() == 100 && bp.getY() == 64 && bp.getZ() == -200,
                "toBlockPos mismatch");

        Point fromBp = new Point(bp);
        helper.assertTrue(p.equals(fromBp), "Point(BlockPos) round-trip failed");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPointNBTRoundTrip(GameTestHelper helper) {
        Point original = new Point(42, -11, 999);
        CompoundTag tag = new CompoundTag();
        original.writeToNBT(tag, "test");

        Point loaded = Point.readFromNBT(tag, "test");
        helper.assertFalse(loaded == null, "Point.readFromNBT returned null");
        helper.assertTrue(original.equals(loaded), "Point NBT round-trip failed: " + loaded);

        // Missing prefix should return null
        Point missing = Point.readFromNBT(tag, "nonexistent");
        helper.assertTrue(missing == null, "Should return null for missing prefix");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPointToString(GameTestHelper helper) {
        Point p = new Point(1, 2, 3);
        String s = p.toString();
        helper.assertTrue(s.contains("1") && s.contains("2") && s.contains("3"),
                "toString should contain coordinates: " + s);
        helper.succeed();
    }

    // ==================== InvItem ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testInvItemRegisterAndGet(GameTestHelper helper) {
        InvItem item = InvItem.registerDirect("__test_wheat", "minecraft:wheat");
        helper.assertFalse(item == null, "registerDirect returned null");

        InvItem fetched = InvItem.get("__test_wheat");
        helper.assertFalse(fetched == null, "get returned null for registered item");
        helper.assertTrue(fetched.key.equals("__test_wheat"), "Key mismatch");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testInvItemResolveAndItemStack(GameTestHelper helper) {
        InvItem item = InvItem.registerDirect("__test_stone", "minecraft:stone");
        ItemStack stack = item.getItemStack(5);
        helper.assertFalse(stack.isEmpty(), "ItemStack should not be empty");
        helper.assertTrue(stack.getCount() == 5, "ItemStack count should be 5, got " + stack.getCount());
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testInvItemEqualsAndHashCode(GameTestHelper helper) {
        InvItem a = InvItem.registerDirect("__test_eq_a", "minecraft:dirt");
        InvItem b = InvItem.get("__test_eq_a");
        helper.assertTrue(a.equals(b), "Same key InvItems should be equal");
        helper.assertTrue(a.hashCode() == b.hashCode(), "Same key InvItems should have same hashCode");

        InvItem c = InvItem.registerDirect("__test_eq_c", "minecraft:gravel");
        helper.assertFalse(a.equals(c), "Different key InvItems should not be equal");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testInvItemExistsAndGetAll(GameTestHelper helper) {
        InvItem.registerDirect("__test_exists", "minecraft:sand");
        helper.assertTrue(InvItem.exists("__test_exists"), "exists() should return true");
        helper.assertFalse(InvItem.exists("__nonexistent_item_xyz"), "exists() should return false for unknown");
        helper.assertTrue(InvItem.getAll().size() > 0, "getAll() should not be empty");
        helper.succeed();
    }

    // ==================== TradeGood ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testTradeGoodCreation(GameTestHelper helper) {
        ItemStack wheat = new ItemStack(Items.WHEAT, 1);
        TradeGood good = new TradeGood(wheat, 10, 5);

        helper.assertTrue(good.buyPrice == 10, "buyPrice should be 10");
        helper.assertTrue(good.sellPrice == 5, "sellPrice should be 5");
        helper.assertFalse(good.item.isEmpty(), "item should not be empty");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testTradeGoodMatches(GameTestHelper helper) {
        ItemStack wheat = new ItemStack(Items.WHEAT, 1);
        TradeGood good = new TradeGood(wheat, 10, 5);

        helper.assertTrue(good.matches(new ItemStack(Items.WHEAT, 3)), "Should match same item type");
        helper.assertFalse(good.matches(new ItemStack(Items.IRON_INGOT, 1)), "Should not match different item");
        helper.assertFalse(good.matches(ItemStack.EMPTY), "Should not match empty stack");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testTradeGoodPriceAdjustment(GameTestHelper helper) {
        ItemStack wheat = new ItemStack(Items.WHEAT, 1);
        TradeGood good = new TradeGood(wheat, 100, 50);

        // 0 reputation = no discount
        int buy0 = good.getAdjustedBuyPrice(0);
        helper.assertTrue(buy0 == 100, "0 rep buy price should be 100, got " + buy0);

        int sell0 = good.getAdjustedSellPrice(0);
        helper.assertTrue(sell0 == 50, "0 rep sell price should be 50, got " + sell0);

        // Max reputation (1000) = 20% discount on buy, 20% bonus on sell
        int buy1000 = good.getAdjustedBuyPrice(1000);
        helper.assertTrue(buy1000 == 80, "1000 rep buy price should be 80, got " + buy1000);

        int sell1000 = good.getAdjustedSellPrice(1000);
        helper.assertTrue(sell1000 == 60, "1000 rep sell price should be 60, got " + sell1000);

        // Mid reputation
        int buy500 = good.getAdjustedBuyPrice(500);
        helper.assertTrue(buy500 == 90, "500 rep buy price should be 90, got " + buy500);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testTradeGoodEmptyItem(GameTestHelper helper) {
        TradeGood empty = new TradeGood();
        helper.assertTrue(empty.item.isEmpty(), "Default TradeGood item should be empty");
        helper.assertFalse(empty.matches(new ItemStack(Items.WHEAT, 1)), "Empty good should not match anything");
        helper.succeed();
    }

    // ==================== Goal System Details ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testGoalRegistryCount(GameTestHelper helper) {
        helper.assertFalse(Goal.goals == null, "Goal registry is null");
        // At least 30 goals should be registered based on initGoals()
        helper.assertTrue(Goal.goals.size() >= 30,
                "Expected at least 30 goals, got " + Goal.goals.size());
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testGoalCoreGoalsPresent(GameTestHelper helper) {
        helper.assertFalse(Goal.sleep == null, "sleep goal missing");
        helper.assertFalse(Goal.hide == null, "hide goal missing");
        helper.assertFalse(Goal.defendVillage == null, "defendVillage goal missing");
        helper.assertFalse(Goal.beSeller == null, "beSeller goal missing");
        helper.assertFalse(Goal.construction == null, "construction goal missing");
        helper.assertFalse(Goal.gettool == null, "gettool goal missing");
        helper.assertFalse(Goal.gosocialise == null, "gosocialise goal missing");
        helper.assertFalse(Goal.raidVillage == null, "raidVillage goal missing");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testGoalKeyLookups(GameTestHelper helper) {
        String[] expectedKeys = {
            "sleep", "hide", "defendvillage", "beseller", "construction",
            "gettool", "gosocialise", "gathergoods", "bringbackresourceshome",
            "lumbermanchoptrees", "lumbermanplantsaplings", "fish",
            "breedanimals", "indiandrybrick", "byzantinegathersilk",
            "huntmonster", "raidvillage", "buildpath", "clearoldpath",
            "foreignmerchantkeepstall", "brewpotions", "childbecomeadult",
            "gochat", "gorest"
        };
        for (String key : expectedKeys) {
            helper.assertFalse(Goal.goals.get(key) == null,
                    "Goal key '" + key + "' not found in registry");
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testGoalProperties(GameTestHelper helper) {
        // Sleep can be done at night
        helper.assertTrue(Goal.sleep.canBeDoneAtNight(), "sleep should be doable at night");

        // Defend village is a fighting goal
        helper.assertTrue(Goal.defendVillage.isFightingGoal(), "defendVillage should be a fighting goal");

        // Construction should not be a fighting goal
        helper.assertFalse(Goal.construction.isFightingGoal(), "construction should not be fighting");

        // Raid village is a fighting goal
        Goal raid = Goal.goals.get("raidvillage");
        helper.assertFalse(raid == null, "raidvillage goal missing");
        helper.assertTrue(raid.isFightingGoal(), "raidvillage should be a fighting goal");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testGoalToStringAndGameName(GameTestHelper helper) {
        helper.assertTrue("sleep".equals(Goal.sleep.toString()), "sleep.toString() should be 'sleep'");
        helper.assertTrue(Goal.sleep.gameName().contains("sleep"), "gameName should contain key");
        helper.succeed();
    }

    // ==================== DiplomacyManager Logic ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testDiplomacyDefaultValues(GameTestHelper helper) {
        // These are static defaults or loaded from JSON
        helper.assertTrue(DiplomacyManager.tradeRelationGain > 0, "tradeRelationGain should be positive");
        helper.assertTrue(DiplomacyManager.raidRelationLoss < 0, "raidRelationLoss should be negative");
        helper.assertTrue(DiplomacyManager.maxRaiders > 0, "maxRaiders should be positive");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testDiplomacyCultureAffinity(GameTestHelper helper) {
        // Unknown cultures should return 0
        int aff = DiplomacyManager.getCultureAffinity("fakecultureA", "fakecultureB");
        helper.assertTrue(aff == 0, "Unknown cultures affinity should be 0, got " + aff);

        // Null safety
        int nullAff = DiplomacyManager.getCultureAffinity(null, "norman");
        helper.assertTrue(nullAff == 0, "Null culture affinity should be 0");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testDiplomacyInitRelation(GameTestHelper helper) {
        Building thA = new Building();
        thA.isTownhall = true;
        thA.cultureKey = "norman";
        thA.setPos(new Point(0, 64, 0));

        Building thB = new Building();
        thB.isTownhall = true;
        thB.cultureKey = "indian";
        thB.setPos(new Point(500, 64, 500));

        DiplomacyManager.initRelation(thA, thB);

        // Both should now know each other
        helper.assertTrue(thA.getKnownVillages().contains(thB.getPos()),
                "thA should know thB after initRelation");
        helper.assertTrue(thB.getKnownVillages().contains(thA.getPos()),
                "thB should know thA after initRelation");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testDiplomacyOnTrade(GameTestHelper helper) {
        Building b = new Building();
        b.setPos(new Point(0, 64, 0));
        Point otherVillage = new Point(500, 64, 500);
        b.setRelation(otherVillage, 0);

        DiplomacyManager.onTrade(b, otherVillage);
        int after = b.getRelation(otherVillage);
        helper.assertTrue(after == DiplomacyManager.tradeRelationGain,
                "After trade, relation should be " + DiplomacyManager.tradeRelationGain + ", got " + after);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testDiplomacyRelationDecay(GameTestHelper helper) {
        Building th = new Building();
        th.isTownhall = true;
        th.setPos(new Point(0, 64, 0));
        Point other = new Point(100, 64, 100);
        th.setRelation(other, 50);

        DiplomacyManager.tickRelationDecay(th);
        int after = th.getRelation(other);
        // decayPerHour is -1, so positive relations decay by adding -1
        helper.assertTrue(after < 50, "Relation should decay from 50, got " + after);
        helper.succeed();
    }

    // ==================== VillageEconomyLoader Logic ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testEconomyExpansionDefaults(GameTestHelper helper) {
        helper.assertTrue(VillageEconomyLoader.upgradeCheckIntervalTicks > 0,
                "upgradeCheckIntervalTicks should be positive");
        helper.assertTrue(VillageEconomyLoader.maxConcurrentConstructions > 0,
                "maxConcurrentConstructions should be positive");
        helper.assertTrue(VillageEconomyLoader.constructionBlocksPerVillagerTick > 0,
                "constructionBlocksPerVillagerTick should be positive");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testEconomyGetCapacity(GameTestHelper helper) {
        int cap = VillageEconomyLoader.getCapacity("nonexistent_plan_set_xyz");
        // Should return default capacity (64)
        helper.assertTrue(cap > 0, "Default capacity should be positive, got " + cap);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testEconomyGetProductionConsumption(GameTestHelper helper) {
        // For unknown plan set, should return empty list (not null)
        var prod = VillageEconomyLoader.getProduction("nonexistent_xyz");
        helper.assertFalse(prod == null, "getProduction should never return null");
        helper.assertTrue(prod.isEmpty(), "Unknown plan set should have empty production");

        var cons = VillageEconomyLoader.getConsumption("nonexistent_xyz");
        helper.assertFalse(cons == null, "getConsumption should never return null");
        helper.assertTrue(cons.isEmpty(), "Unknown plan set should have empty consumption");
        helper.succeed();
    }

    // ==================== Building Relations ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingRelationDefaults(GameTestHelper helper) {
        Building b = new Building();
        Point unknown = new Point(999, 64, 999);
        helper.assertTrue(b.getRelation(unknown) == Building.RELATION_NEUTRAL,
                "Unknown village relation should be NEUTRAL (0)");
        helper.assertTrue(b.getKnownVillages().isEmpty(), "New building should have no known villages");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingRelationSetAndGet(GameTestHelper helper) {
        Building b = new Building();
        Point v1 = new Point(100, 64, 100);
        Point v2 = new Point(200, 64, 200);

        b.setRelation(v1, 50);
        b.setRelation(v2, -30);

        helper.assertTrue(b.getRelation(v1) == 50, "v1 relation should be 50");
        helper.assertTrue(b.getRelation(v2) == -30, "v2 relation should be -30");
        helper.assertTrue(b.getKnownVillages().size() == 2, "Should know 2 villages");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingRelationClamping(GameTestHelper helper) {
        Building b = new Building();
        Point v = new Point(100, 64, 100);

        b.setRelation(v, 999);
        helper.assertTrue(b.getRelation(v) == Building.RELATION_MAX,
                "Relation should be clamped to MAX (100), got " + b.getRelation(v));

        b.setRelation(v, -999);
        helper.assertTrue(b.getRelation(v) == Building.RELATION_MIN,
                "Relation should be clamped to MIN (-100), got " + b.getRelation(v));
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingIsSameVillage(GameTestHelper helper) {
        Building thA = new Building();
        thA.isTownhall = true;
        thA.setPos(new Point(0, 64, 0));

        Building childA = new Building();
        childA.setPos(new Point(10, 64, 10));
        childA.setTownHallPos(new Point(0, 64, 0));

        Building thB = new Building();
        thB.isTownhall = true;
        thB.setPos(new Point(500, 64, 500));

        // isSameVillage is package-private; verify village membership via townHallPos matching
        helper.assertTrue(thA.getPos().equals(childA.getTownHallPos()),
                "Child's townHallPos should match townhall's pos");
        helper.assertFalse(thA.getPos().equals(thB.getPos()),
                "Different townhalls should have different positions");
        helper.succeed();
    }

    // ==================== Building Villager Records ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingVillagerRecordManagement(GameTestHelper helper) {
        Building b = new Building();

        VillagerRecord vr1 = new VillagerRecord();
        vr1.setVillagerId(100L);
        vr1.firstName = "Jean";
        vr1.familyName = "DuBois";

        VillagerRecord vr2 = new VillagerRecord();
        vr2.setVillagerId(200L);
        vr2.firstName = "Marie";

        b.addVillagerRecord(vr1);
        b.addVillagerRecord(vr2);

        helper.assertTrue(b.getVillagerRecords().size() == 2, "Should have 2 records");
        helper.assertFalse(b.getVillagerRecord(100L) == null, "Should find vr1 by ID");
        helper.assertTrue("Jean".equals(b.getVillagerRecord(100L).firstName), "vr1 firstName should be Jean");

        b.removeVillagerRecord(100L);
        helper.assertTrue(b.getVillagerRecords().size() == 1, "Should have 1 record after removal");
        helper.assertTrue(b.getVillagerRecord(100L) == null, "vr1 should be removed");
        helper.succeed();
    }

    // ==================== Building NBT with Relations & VillagerRecords ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingFullNBTRoundTrip(GameTestHelper helper) {
        Building b = new Building();
        b.cultureKey = "norman";
        b.planSetKey = "armoury_a";
        b.villageTypeKey = "artisans";
        b.buildingLevel = 2;
        b.isActive = true;
        b.isTownhall = true;
        b.isInn = false;
        b.isMarket = true;
        b.chestLocked = true;
        b.underAttack = true;
        b.setPos(new Point(100, 64, 200));
        b.setTownHallPos(new Point(100, 64, 200));
        b.setName("Forge Royale");
        b.setQualifier("grand");
        b.controlledBy = UUID.randomUUID();
        b.controlledByName = "TestPlayer";

        // Add villager records
        VillagerRecord vr = new VillagerRecord();
        vr.setVillagerId(42L);
        vr.gender = 0;
        vr.firstName = "Guillaume";
        vr.familyName = "LeFer";
        vr.type = "blacksmith";
        vr.setCultureKey("norman");
        vr.killed = false;
        vr.setHousePos(new Point(110, 64, 210));
        vr.setTownHallPos(new Point(100, 64, 200));
        b.addVillagerRecord(vr);

        // Add relations
        b.setRelation(new Point(500, 64, 500), 75);
        b.setRelation(new Point(600, 64, 600), -40);

        CompoundTag tag = b.save();
        Building loaded = Building.load(tag);

        helper.assertTrue("norman".equals(loaded.cultureKey), "cultureKey not persisted");
        helper.assertTrue("armoury_a".equals(loaded.planSetKey), "planSetKey not persisted");
        helper.assertTrue("artisans".equals(loaded.villageTypeKey), "villageTypeKey not persisted");
        helper.assertTrue(loaded.buildingLevel == 2, "buildingLevel not persisted");
        helper.assertTrue(loaded.isActive, "isActive not persisted");
        helper.assertTrue(loaded.isTownhall, "isTownhall not persisted");
        helper.assertTrue(loaded.isMarket, "isMarket not persisted");
        helper.assertTrue(loaded.chestLocked, "chestLocked not persisted");
        helper.assertTrue(loaded.underAttack, "underAttack not persisted");
        helper.assertTrue("Forge Royale".equals(loaded.getName()), "name not persisted");
        helper.assertTrue("grand".equals(loaded.getQualifier()), "qualifier not persisted");
        helper.assertFalse(loaded.controlledBy == null, "controlledBy not persisted");
        helper.assertTrue("TestPlayer".equals(loaded.controlledByName), "controlledByName not persisted");

        // Check villager record
        helper.assertTrue(loaded.getVillagerRecords().size() == 1, "Should have 1 villager record");
        VillagerRecord lvr = loaded.getVillagerRecord(42L);
        helper.assertFalse(lvr == null, "VillagerRecord 42 not found");
        helper.assertTrue("Guillaume".equals(lvr.firstName), "VR firstName not persisted");
        helper.assertTrue("blacksmith".equals(lvr.type), "VR type not persisted");

        // Check relations
        helper.assertTrue(loaded.getRelation(new Point(500, 64, 500)) == 75, "Relation to 500 not persisted");
        helper.assertTrue(loaded.getRelation(new Point(600, 64, 600)) == -40, "Relation to 600 not persisted");
        helper.succeed();
    }

    // ==================== BuildingLocation NBT ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingLocationNBTRoundTrip(GameTestHelper helper) {
        BuildingLocation loc = new BuildingLocation();
        loc.planKey = "armoury_a";
        loc.pos = new Point(100, 64, 200);
        loc.cultureKey = "norman";
        loc.orientation = 2;
        loc.length = 15;
        loc.width = 12;
        loc.level = 1;
        loc.reputation = 50;
        loc.price = 1000;
        loc.version = 3;
        loc.priorityMoveIn = 5;
        loc.shop = "blacksmith";
        loc.upgradesAllowed = true;
        loc.bedrocklevel = false;
        loc.showTownHallSigns = true;
        loc.isSubBuildingLocation = false;
        loc.isCustomBuilding = true;
        loc.chestPos = new Point(105, 64, 205);
        loc.sleepingPos = new Point(107, 64, 207);
        loc.minx = 90; loc.maxx = 110;
        loc.miny = 60; loc.maxy = 80;
        loc.minz = 190; loc.maxz = 210;
        loc.minxMargin = 2; loc.maxxMargin = 2;
        loc.minyMargin = 1; loc.maxyMargin = 1;
        loc.minzMargin = 2; loc.maxzMargin = 2;

        CompoundTag tag = new CompoundTag();
        loc.save(tag, "bl");

        BuildingLocation loaded = BuildingLocation.read(tag, "bl");
        helper.assertFalse(loaded == null, "BuildingLocation.read returned null");
        helper.assertTrue("armoury_a".equals(loaded.planKey), "planKey not persisted");
        helper.assertTrue("norman".equals(loaded.cultureKey), "cultureKey not persisted");
        helper.assertTrue(loaded.orientation == 2, "orientation not persisted");
        helper.assertTrue(loaded.length == 15, "length not persisted");
        helper.assertTrue(loaded.width == 12, "width not persisted");
        helper.assertTrue(loaded.level == 1, "level not persisted");
        helper.assertTrue(loaded.reputation == 50, "reputation not persisted");
        helper.assertTrue(loaded.price == 1000, "price not persisted");
        helper.assertTrue(loaded.version == 3, "version not persisted");
        helper.assertTrue(loaded.priorityMoveIn == 5, "priorityMoveIn not persisted");
        helper.assertTrue("blacksmith".equals(loaded.shop), "shop not persisted");
        helper.assertTrue(loaded.upgradesAllowed, "upgradesAllowed not persisted");
        helper.assertTrue(loaded.showTownHallSigns, "showTownHallSigns not persisted");
        helper.assertTrue(loaded.isCustomBuilding, "isCustomBuilding not persisted");
        helper.assertFalse(loaded.pos == null, "pos not persisted");
        helper.assertTrue(loaded.pos.x == 100, "pos.x mismatch");
        helper.assertFalse(loaded.chestPos == null, "chestPos not persisted");
        helper.assertTrue(loaded.chestPos.x == 105, "chestPos.x mismatch");
        helper.assertTrue(loaded.minx == 90, "minx not persisted");
        helper.assertTrue(loaded.maxx == 110, "maxx not persisted");
        helper.succeed();
    }

    // ==================== BuildingResManager NBT ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingResManagerNBTRoundTrip(GameTestHelper helper) {
        Building b = new Building();

        InvItem testItem = InvItem.registerDirect("__test_res_wood", "minecraft:oak_log");
        b.resManager.storeGoods(testItem, 25);
        b.resManager.sleepingPositions.add(new Point(10, 64, 20));
        b.resManager.stalls.add(new Point(15, 64, 25));

        CompoundTag tag = new CompoundTag();
        b.resManager.save(tag, "rm_");

        Building b2 = new Building();
        b2.resManager.load(tag, "rm_");

        helper.assertTrue(b2.resManager.countGoods(testItem) == 25,
                "Resources not persisted, got " + b2.resManager.countGoods(testItem));
        helper.assertTrue(b2.resManager.sleepingPositions.size() == 1, "Sleeping positions not persisted");
        helper.assertTrue(b2.resManager.stalls.size() == 1, "Stalls not persisted");
        helper.succeed();
    }

    // ==================== VillagerRecord Full NBT ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testVillagerRecordCreate(GameTestHelper helper) {
        VillagerRecord vr = VillagerRecord.create("norman", "blacksmith", "Jean", "DuBois", 0);
        vr.setHousePos(new Point(10, 64, 20));
        vr.setTownHallPos(new Point(0, 64, 0));
        helper.assertTrue("norman".equals(vr.getCultureKey()), "Culture key mismatch");
        helper.assertTrue("blacksmith".equals(vr.type), "Type mismatch");
        helper.assertTrue("Jean".equals(vr.firstName), "First name mismatch");
        helper.assertTrue("DuBois".equals(vr.familyName), "Family name mismatch");
        helper.assertTrue(vr.gender == 0, "Gender mismatch");
        helper.assertTrue(vr.getVillagerId() != 0, "VillagerId should be assigned");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testVillagerRecordNBTRoundTrip(GameTestHelper helper) {
        VillagerRecord original = new VillagerRecord();
        original.setVillagerId(123L);
        original.gender = 1;
        original.firstName = "Marie";
        original.familyName = "Blanc";
        original.type = "farmer";
        original.setCultureKey("norman");
        original.killed = false;
        original.awayraiding = true;
        original.awayhired = false;
        original.scale = 0.9f;
        original.fathersName = "Pierre";
        original.mothersName = "Anne";
        original.setHousePos(new Point(50, 64, 50));
        original.setTownHallPos(new Point(0, 64, 0));
        original.addQuestTag("quest_started");

        CompoundTag tag = original.save();
        VillagerRecord loaded = VillagerRecord.load(tag);

        helper.assertFalse(loaded == null, "VillagerRecord.load returned null");
        helper.assertTrue(loaded.getVillagerId() == 123L, "villagerId not persisted");
        helper.assertTrue(loaded.gender == 1, "gender not persisted");
        helper.assertTrue("Marie".equals(loaded.firstName), "firstName not persisted");
        helper.assertTrue("Blanc".equals(loaded.familyName), "familyName not persisted");
        helper.assertTrue("farmer".equals(loaded.type), "type not persisted");
        helper.assertTrue("norman".equals(loaded.getCultureKey()), "cultureKey not persisted");
        helper.assertTrue(loaded.awayraiding, "awayraiding not persisted");
        helper.assertTrue(Math.abs(loaded.scale - 0.9f) < 0.01, "scale not persisted");
        helper.assertTrue("Pierre".equals(loaded.fathersName), "fathersName not persisted");
        helper.assertTrue("Anne".equals(loaded.mothersName), "mothersName not persisted");
        helper.assertFalse(loaded.getHousePos() == null, "housePos not persisted");
        helper.assertTrue(loaded.getHousePos().x == 50, "housePos.x mismatch");
        helper.assertTrue(loaded.hasQuestTag("quest_started"), "questTag not persisted");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testVillagerRecordClone(GameTestHelper helper) {
        VillagerRecord original = VillagerRecord.create("seljuk", "warrior", "Ali", "Khan", 0);
        original.setHousePos(new Point(10, 64, 20));
        original.setTownHallPos(new Point(0, 64, 0));
        VillagerRecord cloned = original.clone();

        helper.assertTrue(original.getVillagerId() == cloned.getVillagerId(), "Clone should have same ID");
        helper.assertTrue("Ali".equals(cloned.firstName), "Clone firstName mismatch");

        // Mutating clone should not affect original
        cloned.firstName = "Mehmet";
        helper.assertTrue("Ali".equals(original.firstName), "Clone mutation should not affect original");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testVillagerRecordQuestTags(GameTestHelper helper) {
        VillagerRecord vr = new VillagerRecord();
        helper.assertFalse(vr.hasQuestTag("test"), "Should not have tag initially");

        vr.addQuestTag("test");
        helper.assertTrue(vr.hasQuestTag("test"), "Should have tag after add");

        vr.addQuestTag("test"); // duplicate add
        helper.assertTrue(vr.questTags.size() == 1, "Should not duplicate tags");

        vr.removeQuestTag("test");
        helper.assertFalse(vr.hasQuestTag("test"), "Should not have tag after remove");
        helper.succeed();
    }

    // ==================== UserProfile Full Features ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testUserProfileCultureReputation(GameTestHelper helper) {
        UserProfile p = new UserProfile();
        helper.assertTrue(p.getCultureReputation("norman") == 0, "Default culture rep should be 0");

        p.setCultureReputation("norman", 100);
        helper.assertTrue(p.getCultureReputation("norman") == 100, "Norman rep should be 100");
        helper.assertTrue(p.getCultureReputation("indian") == 0, "Indian rep should still be 0");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testUserProfileCultureLanguage(GameTestHelper helper) {
        UserProfile p = new UserProfile();
        helper.assertTrue(p.getCultureLanguage("norman") == 0, "Default language should be 0");

        p.setCultureLanguage("norman", 5);
        helper.assertTrue(p.getCultureLanguage("norman") == 5, "Norman language should be 5");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testUserProfileUnlockedContent(GameTestHelper helper) {
        UserProfile p = new UserProfile();

        helper.assertFalse(p.hasUnlockedVillager("blacksmith"), "Should not be unlocked initially");
        p.unlockVillager("blacksmith");
        helper.assertTrue(p.hasUnlockedVillager("blacksmith"), "Should be unlocked after unlock");

        p.unlockVillage("norman_village");
        helper.assertTrue(p.hasUnlockedVillage("norman_village"), "Village should be unlocked");

        p.unlockBuilding("armoury_a");
        helper.assertTrue(p.hasUnlockedBuilding("armoury_a"), "Building should be unlocked");

        p.unlockTradeGood("wheat");
        helper.assertTrue(p.hasUnlockedTradeGood("wheat"), "TradeGood should be unlocked");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testUserProfileTags(GameTestHelper helper) {
        UserProfile p = new UserProfile();
        helper.assertFalse(p.hasTag("quest_done"), "Should not have tag initially");

        p.addTag("quest_done");
        helper.assertTrue(p.hasTag("quest_done"), "Should have tag after add");

        p.addTag("quest_done"); // duplicate
        helper.assertTrue(p.getProfileTags().size() == 1, "Should not duplicate tags");

        p.removeTag("quest_done");
        helper.assertFalse(p.hasTag("quest_done"), "Should not have tag after remove");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testUserProfileFullNBTRoundTrip(GameTestHelper helper) {
        UserProfile original = new UserProfile();
        original.uuid = UUID.randomUUID();
        original.playerName = "TestPlayer";
        original.deniers = 500;
        original.donationActivated = true;
        original.adjustVillageReputation(new Point(100, 64, 200), 75);
        original.setCultureReputation("norman", 200);
        original.setCultureLanguage("indian", 3);
        original.addTag("started_quest");
        original.unlockVillager("blacksmith");
        original.unlockVillage("norman_village");
        original.unlockBuilding("armoury_a");
        original.unlockTradeGood("wheat");

        CompoundTag tag = original.save();
        UserProfile loaded = UserProfile.load(tag);

        helper.assertFalse(loaded.uuid == null, "UUID not persisted");
        helper.assertTrue(original.uuid.equals(loaded.uuid), "UUID mismatch");
        helper.assertTrue("TestPlayer".equals(loaded.playerName), "playerName not persisted");
        helper.assertTrue(loaded.deniers == 500, "deniers not persisted");
        helper.assertTrue(loaded.donationActivated, "donationActivated not persisted");
        helper.assertTrue(loaded.getVillageReputation(new Point(100, 64, 200)) == 75, "village rep not persisted");
        helper.assertTrue(loaded.getCultureReputation("norman") == 200, "culture rep not persisted");
        helper.assertTrue(loaded.getCultureLanguage("indian") == 3, "culture language not persisted");
        helper.assertTrue(loaded.hasTag("started_quest"), "tag not persisted");
        helper.assertTrue(loaded.hasUnlockedVillager("blacksmith"), "unlocked villager not persisted");
        helper.assertTrue(loaded.hasUnlockedVillage("norman_village"), "unlocked village not persisted");
        helper.assertTrue(loaded.hasUnlockedBuilding("armoury_a"), "unlocked building not persisted");
        helper.assertTrue(loaded.hasUnlockedTradeGood("wheat"), "unlocked trade good not persisted");
        helper.succeed();
    }

    // ==================== MillVillager Entity Features ====================

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testVillagerSynchedData(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager");

        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        villager.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);

        villager.setFirstName("Jean");
        villager.setFamilyName("DuBois");
        villager.setGender(MillVillager.MALE);
        villager.setCultureKey("norman");

        helper.assertTrue("Jean".equals(villager.getFirstName()), "firstName synched data failed");
        helper.assertTrue("DuBois".equals(villager.getFamilyName()), "familyName synched data failed");
        helper.assertTrue(villager.getGender() == MillVillager.MALE, "gender synched data failed");
        helper.assertTrue("norman".equals(villager.getCultureKey()), "cultureKey synched data failed");
        helper.assertTrue(villager.isMale(), "isMale() should be true");
        helper.assertFalse(villager.isFemale(), "isFemale() should be false");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testVillagerDisplayName(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager");

        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        villager.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);

        villager.setFirstName("Guillaume");
        villager.setFamilyName("LeFer");

        String display = villager.getDisplayName().getString();
        helper.assertTrue(display.contains("Guillaume") && display.contains("LeFer"),
                "Display name should contain full name, got: " + display);
        helper.assertTrue(villager.hasCustomName(), "Should have custom name when firstName set");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testVillagerInventory(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager");

        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        villager.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);

        InvItem testItem = InvItem.registerDirect("__test_inv_item", "minecraft:iron_ingot");
        helper.assertTrue(villager.countInv(testItem) == 0, "Should have 0 initially");

        villager.addToInv(testItem, 10);
        helper.assertTrue(villager.countInv(testItem) == 10, "Should have 10 after adding");

        villager.removeFromInv(testItem, 3);
        helper.assertTrue(villager.countInv(testItem) == 7, "Should have 7 after removing 3");

        villager.removeFromInv(testItem, 7);
        helper.assertTrue(villager.countInv(testItem) == 0, "Should have 0 after removing all");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testVillagerVillagerIdAndType(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager");

        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        villager.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(villager);

        villager.setVillagerId(42L);
        helper.assertTrue(villager.getVillagerId() == 42L, "villagerId should be 42");

        villager.setCultureKey("norman");
        villager.setVillagerTypeKey("blacksmith");
        // After setting type key and culture, vtype should be resolved (or null if culture not loaded yet)
        // We just verify no crash
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testVillagerFemaleEntities(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();

        MillVillager symm = MillEntities.GENERIC_SYMM_FEMALE.get().create(level);
        helper.assertFalse(symm == null, "Failed to create GenericSymmFemale");

        MillVillager asymm = MillEntities.GENERIC_ASYMM_FEMALE.get().create(level);
        helper.assertFalse(asymm == null, "Failed to create GenericAsymmFemale");

        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
        symm.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        asymm.moveTo(pos.getX() + 2.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(symm);
        level.addFreshEntity(asymm);

        symm.setGender(MillVillager.FEMALE);
        helper.assertTrue(symm.isFemale(), "Symm female should be female");
        helper.assertFalse(symm.isMale(), "Symm female should not be male");
        helper.succeed();
    }

    // ==================== Entity Registration ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testEntityTypesRegistered(GameTestHelper helper) {
        helper.assertFalse(MillEntities.GENERIC_MALE.get() == null, "GENERIC_MALE not registered");
        helper.assertFalse(MillEntities.GENERIC_SYMM_FEMALE.get() == null, "GENERIC_SYMM_FEMALE not registered");
        helper.assertFalse(MillEntities.GENERIC_ASYMM_FEMALE.get() == null, "GENERIC_ASYMM_FEMALE not registered");
        helper.assertFalse(MillEntities.WALL_DECORATION.get() == null, "WALL_DECORATION not registered");
        helper.assertFalse(MillEntities.TARGETED_BLAZE.get() == null, "TARGETED_BLAZE not registered");
        helper.assertFalse(MillEntities.TARGETED_WITHER_SKELETON.get() == null, "TARGETED_WITHER_SKELETON not registered");
        helper.assertFalse(MillEntities.TARGETED_GHAST.get() == null, "TARGETED_GHAST not registered");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBlockEntityTypesRegistered(GameTestHelper helper) {
        helper.assertFalse(MillEntities.FIRE_PIT_BE.get() == null, "FIRE_PIT_BE not registered");
        helper.assertFalse(MillEntities.LOCKED_CHEST_BE.get() == null, "LOCKED_CHEST_BE not registered");
        helper.assertFalse(MillEntities.PANEL_BE.get() == null, "PANEL_BE not registered");
        helper.assertFalse(MillEntities.IMPORT_TABLE_BE.get() == null, "IMPORT_TABLE_BE not registered");
        helper.succeed();
    }

    // ==================== Block Registration ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testDecorativeBlocksRegistered(GameTestHelper helper) {
        helper.assertFalse(MillBlocks.STONE_DECORATION.get() == null, "STONE_DECORATION not registered");
        helper.assertFalse(MillBlocks.COOKED_BRICK.get() == null, "COOKED_BRICK not registered");
        helper.assertFalse(MillBlocks.TIMBER_FRAME_PLAIN.get() == null, "TIMBER_FRAME_PLAIN not registered");
        helper.assertFalse(MillBlocks.TIMBER_FRAME_CROSS.get() == null, "TIMBER_FRAME_CROSS not registered");
        helper.assertFalse(MillBlocks.THATCH.get() == null, "THATCH not registered");
        helper.assertFalse(MillBlocks.MUD_BRICK.get() == null, "MUD_BRICK not registered");
        helper.assertFalse(MillBlocks.SANDSTONE_CARVED.get() == null, "SANDSTONE_CARVED not registered");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testFunctionalBlocksRegistered(GameTestHelper helper) {
        helper.assertFalse(MillBlocks.FIRE_PIT.get() == null, "FIRE_PIT not registered");
        helper.assertFalse(MillBlocks.LOCKED_CHEST.get() == null, "LOCKED_CHEST not registered");
        helper.assertFalse(MillBlocks.PANEL.get() == null, "PANEL not registered");
        helper.assertFalse(MillBlocks.IMPORT_TABLE.get() == null, "IMPORT_TABLE not registered");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testPaintedBricksRegistered(GameTestHelper helper) {
        helper.assertFalse(MillBlocks.PAINTED_BRICK_WHITE.get() == null, "PAINTED_BRICK_WHITE not registered");
        helper.assertFalse(MillBlocks.PAINTED_BRICK_RED.get() == null, "PAINTED_BRICK_RED not registered");
        helper.assertFalse(MillBlocks.PAINTED_BRICK_BLUE.get() == null, "PAINTED_BRICK_BLUE not registered");
        helper.assertFalse(MillBlocks.PAINTED_BRICK_BLACK.get() == null, "PAINTED_BRICK_BLACK not registered");
        helper.assertFalse(MillBlocks.PAINTED_BRICK_DECO_WHITE.get() == null, "PAINTED_BRICK_DECO_WHITE not registered");
        helper.assertFalse(MillBlocks.PAINTED_BRICK_DECO_BLACK.get() == null, "PAINTED_BRICK_DECO_BLACK not registered");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testCultureSpecificBlocksRegistered(GameTestHelper helper) {
        helper.assertFalse(MillBlocks.BYZANTINE_STONE_ORNAMENT.get() == null, "BYZANTINE_STONE_ORNAMENT not registered");
        helper.assertFalse(MillBlocks.BYZANTINE_TILES.get() == null, "BYZANTINE_TILES not registered");
        helper.succeed();
    }

    // ==================== Item Registration ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testCurrencyItemsRegistered(GameTestHelper helper) {
        helper.assertFalse(MillItems.DENIER.get() == null, "DENIER not registered");
        helper.assertFalse(MillItems.DENIER_ARGENT.get() == null, "DENIER_ARGENT not registered");
        helper.assertFalse(MillItems.DENIER_OR.get() == null, "DENIER_OR not registered");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testWandItemsRegistered(GameTestHelper helper) {
        helper.assertFalse(MillItems.SUMMONING_WAND.get() == null, "SUMMONING_WAND not registered");
        helper.assertFalse(MillItems.NEGATION_WAND.get() == null, "NEGATION_WAND not registered");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testWeaponItemsRegistered(GameTestHelper helper) {
        helper.assertFalse(MillItems.NORMAN_BROADSWORD.get() == null, "NORMAN_BROADSWORD not registered");
        helper.assertFalse(MillItems.MAYAN_MACE.get() == null, "MAYAN_MACE not registered");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testAllItemsListPopulated(GameTestHelper helper) {
        helper.assertTrue(MillItems.ALL_ITEMS.size() > 50,
                "ALL_ITEMS should contain 50+ items, got " + MillItems.ALL_ITEMS.size());
        helper.succeed();
    }

    // ==================== Quest Constants ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testQuestWorldMissionKeys(GameTestHelper helper) {
        helper.assertTrue("sadhu".equals(Quest.INDIAN_WQ), "Indian world quest key mismatch");
        helper.assertTrue("alchemist".equals(Quest.NORMAN_WQ), "Norman world quest key mismatch");
        helper.assertTrue("fallenking".equals(Quest.MAYAN_WQ), "Mayan world quest key mismatch");

        helper.assertTrue(Quest.WORLD_MISSION_KEYS.length == 3, "Should have 3 world mission keys");
        helper.assertTrue(Quest.WORLD_MISSION_NB.get(Quest.INDIAN_WQ) == 15, "Sadhu mission count should be 15");
        helper.assertTrue(Quest.WORLD_MISSION_NB.get(Quest.NORMAN_WQ) == 13, "Alchemist mission count should be 13");
        helper.assertTrue(Quest.WORLD_MISSION_NB.get(Quest.MAYAN_WQ) == 10, "FallenKing mission count should be 10");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testQuestDefaultFields(GameTestHelper helper) {
        Quest q = new Quest();
        helper.assertTrue(q.steps.isEmpty(), "New quest should have empty steps");
        helper.assertTrue(q.villagers.isEmpty(), "New quest should have empty villagers");
        helper.assertTrue(q.maxsimultaneous == 5, "Default maxsimultaneous should be 5");
        helper.assertTrue(q.minreputation == 0, "Default minreputation should be 0");
        helper.succeed();
    }

    // ==================== VillagerType Loading ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillagerTypesLoadedForCultures(GameTestHelper helper) {
        String[] cultures = {"norman", "indian", "japanese", "mayan", "byzantines", "seljuk", "inuits"};
        for (String ckey : cultures) {
            Culture c = Culture.getCultureByName(ckey);
            helper.assertFalse(c == null, ckey + " culture missing");
            helper.assertTrue(c.villagerTypes.size() > 0,
                    ckey + " has no villager types, got " + c.villagerTypes.size());
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillagerTypeFields(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        boolean foundWithGoals = false;
        for (VillagerType vt : norman.villagerTypes.values()) {
            helper.assertFalse(vt.key == null || vt.key.isEmpty(), "VillagerType key should not be empty");
            if (vt.goals != null && !vt.goals.isEmpty()) {
                foundWithGoals = true;
            }
        }
        helper.assertTrue(foundWithGoals, "At least one Norman villager type should have goals defined");
        helper.succeed();
    }

    // ==================== BuildingPlanSet Per-Culture ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testAllCulturesHavePlanSets(GameTestHelper helper) {
        String[] cultures = {"norman", "indian", "japanese", "mayan", "byzantines", "seljuk", "inuits"};
        for (String ckey : cultures) {
            Culture c = Culture.getCultureByName(ckey);
            helper.assertFalse(c == null, ckey + " culture missing");
            helper.assertTrue(c.planSets.size() > 0,
                    ckey + " has no plan sets, got " + c.planSets.size());
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testPlanSetsHavePlans(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        int withPlans = 0;
        for (BuildingPlanSet bps : norman.planSets.values()) {
            if (!bps.plans.isEmpty()) withPlans++;
        }
        helper.assertTrue(withPlans > 0, "No Norman plan sets have plans loaded");
        helper.succeed();
    }

    // ==================== Building Constants ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingRelationConstants(GameTestHelper helper) {
        helper.assertTrue(Building.RELATION_NEUTRAL == 0, "NEUTRAL should be 0");
        helper.assertTrue(Building.RELATION_MAX == 100, "MAX should be 100");
        helper.assertTrue(Building.RELATION_MIN == -100, "MIN should be -100");
        helper.assertTrue(Building.RELATION_GOOD > Building.RELATION_FAIR, "GOOD > FAIR");
        helper.assertTrue(Building.RELATION_BAD < Building.RELATION_CHILLY, "BAD < CHILLY");
        helper.succeed();
    }

    // ==================== Building Trade Goods ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingTradeGoodsList(GameTestHelper helper) {
        Building b = new Building();
        helper.assertTrue(b.tradeGoods.isEmpty(), "New building should have empty trade goods");

        ItemStack wheat = new ItemStack(Items.WHEAT, 1);
        b.tradeGoods.add(new TradeGood(wheat, 5, 3));
        helper.assertTrue(b.getTradeGoods().size() == 1, "Should have 1 trade good");
        helper.succeed();
    }

    // ==================== Building ResManager Extended ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingResManagerSleepingPos(GameTestHelper helper) {
        Building b = new Building();
        helper.assertTrue(b.resManager.getSleepingPos() == null, "No sleeping pos initially");

        b.resManager.sleepingPositions.add(new Point(10, 64, 20));
        Point sp = b.resManager.getSleepingPos();
        helper.assertFalse(sp == null, "Should have sleeping pos");
        helper.assertTrue(sp.x == 10 && sp.z == 20, "Sleeping pos coordinates mismatch");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingResManagerMultipleItems(GameTestHelper helper) {
        Building b = new Building();
        InvItem wood = InvItem.registerDirect("__test_rm_wood", "minecraft:oak_log");
        InvItem stone = InvItem.registerDirect("__test_rm_stone", "minecraft:cobblestone");

        b.resManager.storeGoods(wood, 10);
        b.resManager.storeGoods(stone, 20);
        b.resManager.storeGoods(wood, 5); // merge

        helper.assertTrue(b.resManager.countGoods(wood) == 15, "Wood should be 15, got " + b.resManager.countGoods(wood));
        helper.assertTrue(b.resManager.countGoods(stone) == 20, "Stone should be 20");
        helper.assertTrue(b.resManager.resources.size() == 2, "Should have 2 resource types");
        helper.succeed();
    }
}
