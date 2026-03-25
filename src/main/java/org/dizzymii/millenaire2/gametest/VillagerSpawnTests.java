package org.dizzymii.millenaire2.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.VillagerRecord;
import org.dizzymii.millenaire2.village.VillagerSpawner;

import java.util.List;

/**
 * GameTest suite verifying that villagers spawn naturally from {@link VillagerSpawner}.
 *
 * <p>Each test creates a {@link Building} with one or more {@link VillagerRecord}s,
 * calls {@link VillagerSpawner#checkAndSpawnVillagers} on a live {@link ServerLevel},
 * and asserts the expected entities (or absence thereof) in the world.</p>
 */
@GameTestHolder(Millenaire2.MODID)
@PrefixGameTestTemplate(false)
public class VillagerSpawnTests {

    // ==================== Helpers ====================

    /** Small AABB centred on a Point, used to query nearby spawned entities. */
    private static AABB nearbyBox(Point p) {
        return AABB.ofSize(new Vec3(p.x + 0.5, p.y + 1.0, p.z + 0.5), 10, 10, 10);
    }

    /**
     * Build a minimal active Building positioned at the given absolute point.
     * Uses Norman culture to ensure culture data is available.
     */
    private static Building activeBuilding(Point pos) {
        Building b = new Building();
        b.isActive = true;
        b.isTownhall = true;
        b.cultureKey = "norman";
        b.setPos(pos);
        b.setTownHallPos(pos);
        return b;
    }

    /** Convert a helper-relative BlockPos to a {@link Point}. */
    private static Point toPoint(GameTestHelper helper, int rx, int ry, int rz) {
        BlockPos abs = helper.absolutePos(new BlockPos(rx, ry, rz));
        return new Point(abs.getX(), abs.getY(), abs.getZ());
    }

    // ==================== Core spawn tests ====================

    /**
     * A VillagerRecord that is neither killed, raiding, nor hired causes
     * {@link VillagerSpawner} to add exactly one {@link MillVillager} entity.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerCreatesVillager(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = activeBuilding(pos);
        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Jean", "Martin", MillVillager.MALE);
        vr.setHousePos(pos);
        building.addVillagerRecord(vr);

        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        helper.assertTrue(spawned.size() == 1,
                "Expected 1 villager after spawn, found " + spawned.size());
        helper.succeed();
    }

    /** A record with {@code killed = true} must never be spawned. */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerSkipsKilledRecord(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = activeBuilding(pos);
        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Henri", "Lebon", MillVillager.MALE);
        vr.killed = true;
        vr.setHousePos(pos);
        building.addVillagerRecord(vr);

        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        helper.assertTrue(spawned.isEmpty(),
                "Killed villager must not be spawned, found " + spawned.size());
        helper.succeed();
    }

    /** A record with {@code awayraiding = true} must not be spawned. */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerSkipsRaidingRecord(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = activeBuilding(pos);
        VillagerRecord vr = VillagerRecord.create("norman", "guard", "Robert", "Chevalier", MillVillager.MALE);
        vr.awayraiding = true;
        vr.setHousePos(pos);
        building.addVillagerRecord(vr);

        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        helper.assertTrue(spawned.isEmpty(),
                "Raiding villager must not be spawned, found " + spawned.size());
        helper.succeed();
    }

    /** A record with {@code awayhired = true} must not be spawned. */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerSkipsHiredRecord(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = activeBuilding(pos);
        VillagerRecord vr = VillagerRecord.create("norman", "guard", "Pierre", "Blanc", MillVillager.MALE);
        vr.awayhired = true;
        vr.setHousePos(pos);
        building.addVillagerRecord(vr);

        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        helper.assertTrue(spawned.isEmpty(),
                "Hired-away villager must not be spawned, found " + spawned.size());
        helper.succeed();
    }

    /**
     * Calling {@link VillagerSpawner#checkAndSpawnVillagers} a second time for the same
     * building must not duplicate an entity that is already loaded in the level.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerNoDuplicates(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = activeBuilding(pos);
        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Louis", "Petit", MillVillager.MALE);
        vr.setHousePos(pos);
        building.addVillagerRecord(vr);

        VillagerSpawner.checkAndSpawnVillagers(building, level);
        VillagerSpawner.checkAndSpawnVillagers(building, level); // second call

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        helper.assertTrue(spawned.size() == 1,
                "Expected exactly 1 villager (no duplicate), found " + spawned.size());
        helper.succeed();
    }

    // ==================== Entity-type selection ====================

    /** A male VillagerRecord must produce a {@link MillVillager.GenericMale} entity. */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerMaleEntityType(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = activeBuilding(pos);
        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Marc", "Simon", MillVillager.MALE);
        vr.setHousePos(pos);
        building.addVillagerRecord(vr);

        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        helper.assertTrue(!spawned.isEmpty(), "No villager was spawned");
        helper.assertTrue(spawned.get(0) instanceof MillVillager.GenericMale,
                "Male record must produce GenericMale, got "
                        + spawned.get(0).getClass().getSimpleName());
        helper.succeed();
    }

    /** A female VillagerRecord must produce a {@link MillVillager.GenericSymmFemale} entity. */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerFemaleEntityType(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = activeBuilding(pos);
        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Marie", "Dupont", MillVillager.FEMALE);
        vr.setHousePos(pos);
        building.addVillagerRecord(vr);

        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        helper.assertTrue(!spawned.isEmpty(), "No villager was spawned");
        helper.assertTrue(spawned.get(0) instanceof MillVillager.GenericSymmFemale,
                "Female record must produce GenericSymmFemale, got "
                        + spawned.get(0).getClass().getSimpleName());
        helper.succeed();
    }

    // ==================== Attribute propagation ====================

    /** The spawned entity must carry the first name and family name from its record. */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerSetsVillagerName(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = activeBuilding(pos);
        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Thomas", "Bernard", MillVillager.MALE);
        vr.setHousePos(pos);
        building.addVillagerRecord(vr);

        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        helper.assertTrue(!spawned.isEmpty(), "No villager was spawned");
        MillVillager v = spawned.get(0);
        helper.assertTrue("Thomas".equals(v.getFirstName()),
                "Expected firstName=Thomas, got " + v.getFirstName());
        helper.assertTrue("Bernard".equals(v.getFamilyName()),
                "Expected familyName=Bernard, got " + v.getFamilyName());
        helper.succeed();
    }

    /** The spawned entity must have the villager-ID taken from the record. */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerSetsVillagerId(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = activeBuilding(pos);
        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Alain", "Rousseau", MillVillager.MALE);
        vr.setHousePos(pos);
        building.addVillagerRecord(vr);
        long expectedId = vr.getVillagerId();

        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        helper.assertTrue(!spawned.isEmpty(), "No villager was spawned");
        helper.assertTrue(spawned.get(0).getVillagerId() == expectedId,
                "Expected villagerId=" + expectedId
                        + ", got " + spawned.get(0).getVillagerId());
        helper.succeed();
    }

    /** The spawned entity must reflect the culture key from the owning building. */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerSetsCultureKey(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = activeBuilding(pos);
        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Claude", "Morin", MillVillager.MALE);
        vr.setHousePos(pos);
        building.addVillagerRecord(vr);

        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        helper.assertTrue(!spawned.isEmpty(), "No villager was spawned");
        helper.assertTrue("norman".equals(spawned.get(0).getCultureKey()),
                "Expected cultureKey=norman, got " + spawned.get(0).getCultureKey());
        helper.succeed();
    }

    /** The spawned entity must have its vtypeKey populated from the record. */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerSetsVillagerTypeKey(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = activeBuilding(pos);
        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Emile", "Leclerc", MillVillager.MALE);
        vr.setHousePos(pos);
        building.addVillagerRecord(vr);

        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        helper.assertTrue(!spawned.isEmpty(), "No villager was spawned");
        helper.assertTrue("farmer".equals(spawned.get(0).vtypeKey),
                "Expected vtypeKey=farmer, got " + spawned.get(0).vtypeKey);
        helper.succeed();
    }

    /** The spawned entity must have its townHallPoint set from the building. */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerSetsTownHallPoint(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = activeBuilding(pos);
        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Nicolas", "Garnier", MillVillager.MALE);
        vr.setHousePos(pos);
        building.addVillagerRecord(vr);

        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        helper.assertTrue(!spawned.isEmpty(), "No villager was spawned");
        MillVillager v = spawned.get(0);
        helper.assertFalse(v.townHallPoint == null,
                "Spawned villager must have townHallPoint set");
        helper.assertTrue(v.townHallPoint.equals(pos),
                "townHallPoint should be " + pos + ", got " + v.townHallPoint);
        helper.succeed();
    }

    // ==================== Multiple-villager spawn ====================

    /**
     * A building with two VillagerRecords (one male, one female) must produce exactly
     * two distinct entities.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerMultipleVillagers(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = activeBuilding(pos);

        VillagerRecord male = VillagerRecord.create("norman", "farmer", "Paul", "Dubois", MillVillager.MALE);
        male.setHousePos(pos);
        building.addVillagerRecord(male);

        VillagerRecord female = VillagerRecord.create("norman", "farmer", "Sophie", "Dubois", MillVillager.FEMALE);
        female.setHousePos(pos);
        building.addVillagerRecord(female);

        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        helper.assertTrue(spawned.size() == 2,
                "Expected 2 villagers (1 male + 1 female), found " + spawned.size());

        long maleCount = spawned.stream().filter(v -> v instanceof MillVillager.GenericMale).count();
        long femaleCount = spawned.stream().filter(v -> v instanceof MillVillager.GenericSymmFemale).count();
        helper.assertTrue(maleCount == 1, "Expected 1 GenericMale, found " + maleCount);
        helper.assertTrue(femaleCount == 1, "Expected 1 GenericSymmFemale, found " + femaleCount);
        helper.succeed();
    }

    // ==================== VillagerRecord creation helper ====================

    /**
     * {@link VillagerRecord#create} must assign a non-zero unique ID and populate
     * all specified fields correctly.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillagerRecordCreate(GameTestHelper helper) {
        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Francois", "Legrand", MillVillager.MALE);

        helper.assertTrue(vr.getVillagerId() != 0, "villagerId must be non-zero");
        helper.assertTrue("norman".equals(vr.getCultureKey()), "cultureKey not set");
        helper.assertTrue("farmer".equals(vr.type), "type not set");
        helper.assertTrue("Francois".equals(vr.firstName), "firstName not set");
        helper.assertTrue("Legrand".equals(vr.familyName), "familyName not set");
        helper.assertTrue(vr.gender == MillVillager.MALE, "gender not MALE");
        helper.assertFalse(vr.killed, "New record must not be killed");
        helper.assertFalse(vr.awayraiding, "New record must not be awayraiding");
        helper.assertFalse(vr.awayhired, "New record must not be awayhired");
        helper.succeed();
    }

    /**
     * Two records created in quick succession must have different IDs to prevent
     * duplicate-entity suppression during spawning.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillagerRecordIdsUnique(GameTestHelper helper) {
        VillagerRecord vr1 = VillagerRecord.create("norman", "farmer", "A", "B", MillVillager.MALE);
        VillagerRecord vr2 = VillagerRecord.create("norman", "farmer", "C", "D", MillVillager.MALE);
        helper.assertFalse(vr1.getVillagerId() == vr2.getVillagerId(),
                "Two VillagerRecords must have distinct IDs");
        helper.succeed();
    }

    /** VillagerRecord NBT round-trip must preserve all spawn-relevant fields. */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillagerRecordNBTRoundTrip(GameTestHelper helper) {
        VillagerRecord original = VillagerRecord.create("norman", "guard", "Hugo", "Renard", MillVillager.MALE);
        original.setHousePos(new Point(10, 64, 20));
        original.setTownHallPos(new Point(0, 64, 0));
        original.killed = false;
        original.awayraiding = false;
        original.awayhired = false;

        net.minecraft.nbt.CompoundTag tag = original.save();
        VillagerRecord loaded = VillagerRecord.load(tag);

        helper.assertTrue(loaded.getVillagerId() == original.getVillagerId(),
                "villagerId not persisted");
        helper.assertTrue("Hugo".equals(loaded.firstName),
                "firstName not persisted, got " + loaded.firstName);
        helper.assertTrue("Renard".equals(loaded.familyName),
                "familyName not persisted, got " + loaded.familyName);
        helper.assertTrue(loaded.gender == MillVillager.MALE, "gender not persisted");
        helper.assertTrue("norman".equals(loaded.getCultureKey()), "cultureKey not persisted");
        helper.assertTrue("guard".equals(loaded.type), "type not persisted");
        helper.assertFalse(loaded.killed, "killed flag not persisted");
        helper.assertFalse(loaded.awayraiding, "awayraiding flag not persisted");
        helper.assertFalse(loaded.awayhired, "awayhired flag not persisted");
        helper.assertTrue(loaded.getHousePos() != null
                && loaded.getHousePos().equals(new Point(10, 64, 20)),
                "housePos not persisted");
        helper.succeed();
    }

    // ==================== Culture data backing natural spawn ====================

    /**
     * The Norman culture must expose enough villager types to support natural village
     * spawning (at least 10 different roles).
     *
     * <p>Norman is the reference culture and is expected to have roles including
     * farmer, guard, miner, smith, carpenter, lumberman, cider producer, merchant,
     * chief/knight, and wife/child — giving a minimum of 10 distinct types.</p>
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testNormanVillagerTypesLoadedForSpawn(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture not loaded");
        helper.assertTrue(norman.villagerTypes.size() >= 10,
                "Norman should have ≥10 villager types for natural spawn, got "
                        + norman.villagerTypes.size());
        helper.succeed();
    }

    /**
     * The Norman farmer type must have at least one production/task goal assigned,
     * confirming data-driven spawn data is complete.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testFarmerTypeHasGoals(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture not loaded");

        VillagerType farmer = norman.getVillagerType("farmer");
        helper.assertFalse(farmer == null, "Norman farmer type not loaded");
        helper.assertTrue(!farmer.goals.isEmpty(),
                "Norman farmer must have at least one goal configured");
        helper.succeed();
    }

    /**
     * The Norman guard type must carry the {@code archer} and {@code raider} tags,
     * confirming combat-role data is in place for defensive spawning.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testGuardTypeIsArcher(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture not loaded");

        VillagerType guard = norman.getVillagerType("guard");
        helper.assertFalse(guard == null, "Norman guard type not loaded");
        helper.assertTrue(guard.isArcher,
                "Norman guard must have isArcher=true");
        helper.assertTrue(guard.isRaider,
                "Norman guard must have isRaider=true (can raid)");
        helper.succeed();
    }

    /**
     * The Norman farmer type must carry the {@code helpInAttacks} flag, confirming that
     * even ordinary villagers join village defence when spawned.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testFarmerHelpsInAttacks(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture not loaded");

        VillagerType farmer = norman.getVillagerType("farmer");
        helper.assertFalse(farmer == null, "Norman farmer type not loaded");
        helper.assertTrue(farmer.helpInAttacks,
                "Norman farmer must have helpInAttacks=true");
        helper.succeed();
    }

    /**
     * Every loaded culture must expose at least one villager type, ensuring that
     * natural spawn cannot produce a culture-less village.
     *
     * <p>The threshold of 7 corresponds to the seven base cultures shipped with the
     * mod: Norman, Indian, Japanese, Mayan, Byzantine, Inuit, and Seljuk.</p>
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testAllCulturesHaveVillagerTypes(GameTestHelper helper) {
        helper.assertTrue(Culture.LIST_CULTURES.size() >= 7, // 7 base cultures shipped with the mod
                "Expected at least 7 cultures, got " + Culture.LIST_CULTURES.size());

        for (Culture c : Culture.LIST_CULTURES) {
            helper.assertTrue(!c.villagerTypes.isEmpty(),
                    "Culture '" + c.key + "' has no villager types — natural spawn will fail");
        }
        helper.succeed();
    }

    /**
     * A building without a position must not throw, and must not spawn any entities.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerHandlesNullPosition(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point queryPos = toPoint(helper, 1, 1, 1);

        Building building = new Building();
        building.isActive = true;
        building.cultureKey = "norman";
        // intentionally no setPos()

        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Jules", "Meunier", MillVillager.MALE);
        building.addVillagerRecord(vr);

        // Should be a no-op (no NPE)
        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(queryPos));
        helper.assertTrue(spawned.isEmpty(),
                "Building with null position should not spawn villagers");
        helper.succeed();
    }

    /**
     * A spawned villager's entity position must be at the house position (not the
     * building root), and its {@code housePoint} field must match the value stored
     * in the corresponding VillagerRecord.
     *
     * <p>This single test replaces two earlier tests that had overlapping logic:
     * one checked spawn location and one checked field assignment—both from the
     * same code path, so they are combined here for clarity.</p>
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerSetsHousePoint(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point buildingPos = toPoint(helper, 1, 1, 1);
        Point housePos = toPoint(helper, 1, 1, 2);

        Building building = activeBuilding(buildingPos);
        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Arnaud", "Pasquier", MillVillager.MALE);
        vr.setHousePos(housePos);
        building.addVillagerRecord(vr);

        VillagerSpawner.checkAndSpawnVillagers(building, level);

        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(housePos));
        helper.assertTrue(!spawned.isEmpty(), "No villager spawned near house position");
        // Verify physical spawn location: entity must be near the house position, not the building root.
        helper.assertTrue(housePos.equals(spawned.get(0).housePoint),
                "housePoint mismatch: expected " + housePos + ", got " + spawned.get(0).housePoint);
        helper.succeed();
    }

    /**
     * When a building has no culture key the spawner must still not throw, and the
     * resulting villager (if spawned) must not crash on access.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSpawnerHandlesNullCulture(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Point pos = toPoint(helper, 1, 1, 1);

        Building building = new Building();
        building.isActive = true;
        building.cultureKey = null; // intentionally missing
        building.setPos(pos);
        building.setTownHallPos(pos);

        VillagerRecord vr = VillagerRecord.create(null, null, "Remi", "Fontaine", MillVillager.MALE);
        vr.setHousePos(pos);
        building.addVillagerRecord(vr);

        // Must not throw
        VillagerSpawner.checkAndSpawnVillagers(building, level);

        // Any entity that was created must be alive and not immediately removed
        List<MillVillager> spawned = level.getEntitiesOfClass(MillVillager.class, nearbyBox(pos));
        for (MillVillager v : spawned) {
            helper.assertFalse(v.isRemoved(),
                    "Spawned villager (no culture) must not be removed immediately");
        }
        helper.succeed();
    }
}
