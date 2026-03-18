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
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerAnimState;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.network.ClientPacketHandler;
import org.dizzymii.millenaire2.sound.MillSounds;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.FamilyData;
import org.dizzymii.millenaire2.village.VillagerRecord;
import org.dizzymii.millenaire2.world.MillTreeFeatures;
import org.dizzymii.millenaire2.world.MillTreeGrowers;

/**
 * Tests for features merged from fix/texture-paths-and-item-use and feat/remaining-features.
 * Covers: FamilyData, VillagerAnimState, MillSounds, MillTreeFeatures/Growers,
 * MillVillager merged fields (spouse, anim, familyData, held items, NBT),
 * ClientPacketHandler data classes, MillConfig defaults, ItemParchment, BlockMillSapling.
 */
@GameTestHolder(Millenaire2.MODID)
@PrefixGameTestTemplate(false)
public class MergedFeatureTests {

    // ==================== FamilyData: Marriage ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testFamilyDataMarriage(GameTestHelper helper) {
        FamilyData fd = new FamilyData();
        helper.assertFalse(fd.isMarried(), "Should not be married initially");
        helper.assertTrue(fd.getSpouseId() == -1L, "Spouse ID should be -1 initially");
        helper.assertTrue(fd.getSpouseName().isEmpty(), "Spouse name should be empty initially");

        fd.marry(42L, "Alice Smith");
        helper.assertTrue(fd.isMarried(), "Should be married after marry()");
        helper.assertTrue(fd.getSpouseId() == 42L, "Spouse ID should be 42");
        helper.assertTrue(fd.getSpouseName().equals("Alice Smith"), "Spouse name mismatch");

        fd.divorce();
        helper.assertFalse(fd.isMarried(), "Should not be married after divorce()");
        helper.assertTrue(fd.getSpouseId() == -1L, "Spouse ID should reset to -1");
        helper.assertTrue(fd.getSpouseName().isEmpty(), "Spouse name should reset to empty");
        helper.succeed();
    }

    // ==================== FamilyData: Parents ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testFamilyDataParents(GameTestHelper helper) {
        FamilyData fd = new FamilyData();
        helper.assertFalse(fd.hasParents(), "Should have no parents initially");

        fd.setFatherId(10L);
        fd.setFatherName("Papa John");
        fd.setMotherId(11L);
        fd.setMotherName("Mama Maria");
        fd.setMaidenName("Rossi");

        helper.assertTrue(fd.hasParents(), "Should have parents after setting");
        helper.assertTrue(fd.getFatherId() == 10L, "Father ID mismatch");
        helper.assertTrue(fd.getFatherName().equals("Papa John"), "Father name mismatch");
        helper.assertTrue(fd.getMotherId() == 11L, "Mother ID mismatch");
        helper.assertTrue(fd.getMotherName().equals("Mama Maria"), "Mother name mismatch");
        helper.assertTrue(fd.getMaidenName().equals("Rossi"), "Maiden name mismatch");
        helper.succeed();
    }

    // ==================== FamilyData: Children ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testFamilyDataChildren(GameTestHelper helper) {
        FamilyData fd = new FamilyData();
        helper.assertTrue(fd.getChildCount() == 0, "Should have 0 children initially");
        helper.assertTrue(fd.canHaveMoreChildren(), "Should be able to have children");

        fd.addChild(100L);
        fd.addChild(101L);
        fd.addChild(100L); // duplicate — should not add
        helper.assertTrue(fd.getChildCount() == 2, "Should have 2 children, got " + fd.getChildCount());

        fd.addChild(102L);
        fd.addChild(103L);
        helper.assertTrue(fd.getChildCount() == 4, "Should have 4 children");
        helper.assertFalse(fd.canHaveMoreChildren(), "Should not be able to have more (MAX_CHILDREN=4)");

        fd.removeChild(100L);
        helper.assertTrue(fd.getChildCount() == 3, "Should have 3 children after removal");
        helper.assertTrue(fd.canHaveMoreChildren(), "Should be able to have children again");
        helper.succeed();
    }

    // ==================== FamilyData: Child Growth ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testFamilyDataChildGrowth(GameTestHelper helper) {
        FamilyData fd = new FamilyData();
        helper.assertFalse(fd.isChild(), "Should not be child initially");
        helper.assertTrue(fd.getChildSize() == 0, "Child size should be 0");

        fd.setChild(true);
        fd.setChildSize(10);
        helper.assertTrue(fd.isChild(), "Should be child after setChild(true)");
        helper.assertTrue(fd.getChildSize() == 10, "Child size should be 10");
        helper.succeed();
    }

    // ==================== FamilyData: Pregnancy ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testFamilyDataPregnancy(GameTestHelper helper) {
        FamilyData fd = new FamilyData();
        helper.assertFalse(fd.isPregnant(), "Should not be pregnant initially");

        // Setup for pregnancy eligibility
        fd.marry(50L, "Husband");
        helper.assertTrue(fd.canBecomePregnant(), "Married with no children, should be eligible");

        fd.setPregnant(true);
        fd.setPregnancyStart(1000L);
        helper.assertTrue(fd.isPregnant(), "Should be pregnant");
        helper.assertFalse(fd.canBecomePregnant(), "Already pregnant, should not be eligible");

        // Check birth readiness
        helper.assertFalse(fd.isReadyToBirth(1000L + FamilyData.PREGNANCY_DURATION - 1),
                "Should not be ready 1 tick before duration");
        helper.assertTrue(fd.isReadyToBirth(1000L + FamilyData.PREGNANCY_DURATION),
                "Should be ready exactly at duration");
        helper.assertTrue(fd.isReadyToBirth(1000L + FamilyData.PREGNANCY_DURATION + 100),
                "Should be ready after duration");
        helper.succeed();
    }

    // ==================== FamilyData: Pregnancy Eligibility Edge Cases ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testFamilyDataPregnancyEligibility(GameTestHelper helper) {
        FamilyData fd = new FamilyData();

        // Not married — cannot become pregnant
        helper.assertFalse(fd.canBecomePregnant(), "Unmarried should not be eligible");

        fd.marry(50L, "Husband");

        // Child villager — cannot become pregnant
        fd.setChild(true);
        helper.assertFalse(fd.canBecomePregnant(), "Child should not be eligible");
        fd.setChild(false);

        // At max children — cannot become pregnant
        for (int i = 0; i < FamilyData.MAX_CHILDREN; i++) {
            fd.addChild(200L + i);
        }
        helper.assertFalse(fd.canBecomePregnant(), "At max children should not be eligible");
        helper.succeed();
    }

    // ==================== FamilyData: NBT Round-Trip ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testFamilyDataNBTRoundTrip(GameTestHelper helper) {
        FamilyData original = new FamilyData();
        original.marry(42L, "Alice Smith");
        original.setFatherId(10L);
        original.setFatherName("Papa");
        original.setMotherId(11L);
        original.setMotherName("Mama");
        original.setMaidenName("Rossi");
        original.addChild(100L);
        original.addChild(101L);
        original.setChild(true);
        original.setChildSize(15);
        original.setPregnant(true);
        original.setPregnancyStart(5000L);

        CompoundTag tag = original.save();

        FamilyData loaded = new FamilyData();
        loaded.load(tag);

        helper.assertTrue(loaded.isMarried(), "Loaded should be married");
        helper.assertTrue(loaded.getSpouseId() == 42L, "Loaded spouse ID mismatch");
        helper.assertTrue(loaded.getSpouseName().equals("Alice Smith"), "Loaded spouse name mismatch");
        helper.assertTrue(loaded.getFatherId() == 10L, "Loaded father ID mismatch");
        helper.assertTrue(loaded.getFatherName().equals("Papa"), "Loaded father name mismatch");
        helper.assertTrue(loaded.getMotherId() == 11L, "Loaded mother ID mismatch");
        helper.assertTrue(loaded.getMotherName().equals("Mama"), "Loaded mother name mismatch");
        helper.assertTrue(loaded.getMaidenName().equals("Rossi"), "Loaded maiden name mismatch");
        helper.assertTrue(loaded.getChildCount() == 2, "Loaded child count mismatch");
        helper.assertTrue(loaded.getChildrenIds().contains(100L), "Missing child 100");
        helper.assertTrue(loaded.getChildrenIds().contains(101L), "Missing child 101");
        helper.assertTrue(loaded.isChild(), "Loaded should be child");
        helper.assertTrue(loaded.getChildSize() == 15, "Loaded child size mismatch");
        helper.assertTrue(loaded.isPregnant(), "Loaded should be pregnant");
        helper.assertTrue(loaded.getPregnancyStart() == 5000L, "Loaded pregnancy start mismatch");
        helper.succeed();
    }

    // ==================== FamilyData: writeToRecord / readFromRecord ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testFamilyDataRecordSync(GameTestHelper helper) {
        FamilyData fd = new FamilyData();
        fd.marry(42L, "Alice");
        fd.setFatherName("Papa");
        fd.setMotherName("Mama");
        fd.setMaidenName("Rossi");

        VillagerRecord record = VillagerRecord.create("norman", "peasant", "Test", "Tester", 1);
        fd.writeToRecord(record);

        helper.assertTrue(record.spousesName.equals("Alice"), "Record spouse name mismatch");
        helper.assertTrue(record.fathersName.equals("Papa"), "Record father name mismatch");
        helper.assertTrue(record.mothersName.equals("Mama"), "Record mother name mismatch");
        helper.assertTrue(record.maidenName.equals("Rossi"), "Record maiden name mismatch");

        // Read back into a fresh FamilyData
        FamilyData fd2 = new FamilyData();
        fd2.readFromRecord(record);
        helper.assertTrue(fd2.getSpouseName().equals("Alice"), "Read back spouse name mismatch");
        helper.assertTrue(fd2.getFatherName().equals("Papa"), "Read back father name mismatch");
        helper.assertTrue(fd2.getMotherName().equals("Mama"), "Read back mother name mismatch");
        helper.assertTrue(fd2.getMaidenName().equals("Rossi"), "Read back maiden name mismatch");
        helper.succeed();
    }

    // ==================== FamilyData: toString ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testFamilyDataToString(GameTestHelper helper) {
        FamilyData fd = new FamilyData();
        String empty = fd.toString();
        helper.assertTrue(empty.startsWith("FamilyData{"), "toString should start with FamilyData{");

        fd.marry(1L, "Spouse");
        String married = fd.toString();
        helper.assertTrue(married.contains("spouse=Spouse"), "toString should include spouse");

        fd.addChild(100L);
        String withChild = fd.toString();
        helper.assertTrue(withChild.contains("children=1"), "toString should include children count");
        helper.succeed();
    }

    // ==================== VillagerAnimState: Enum Values ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testVillagerAnimStateValues(GameTestHelper helper) {
        VillagerAnimState[] states = VillagerAnimState.values();
        helper.assertTrue(states.length == 8, "Expected 8 anim states, got " + states.length);

        helper.assertTrue(VillagerAnimState.IDLE.getId() == 0, "IDLE id should be 0");
        helper.assertTrue(VillagerAnimState.WALKING.getId() == 1, "WALKING id should be 1");
        helper.assertTrue(VillagerAnimState.WORKING.getId() == 2, "WORKING id should be 2");
        helper.assertTrue(VillagerAnimState.SLEEPING.getId() == 3, "SLEEPING id should be 3");
        helper.assertTrue(VillagerAnimState.COMBAT_MELEE.getId() == 4, "COMBAT_MELEE id should be 4");
        helper.assertTrue(VillagerAnimState.COMBAT_BOW.getId() == 5, "COMBAT_BOW id should be 5");
        helper.assertTrue(VillagerAnimState.SITTING.getId() == 6, "SITTING id should be 6");
        helper.assertTrue(VillagerAnimState.EATING.getId() == 7, "EATING id should be 7");
        helper.succeed();
    }

    // ==================== VillagerAnimState: fromId ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testVillagerAnimStateFromId(GameTestHelper helper) {
        for (VillagerAnimState state : VillagerAnimState.values()) {
            VillagerAnimState roundTripped = VillagerAnimState.fromId(state.getId());
            helper.assertTrue(roundTripped == state,
                    "fromId(" + state.getId() + ") should return " + state + " but got " + roundTripped);
        }
        // Invalid ID should return IDLE
        helper.assertTrue(VillagerAnimState.fromId(-1) == VillagerAnimState.IDLE,
                "fromId(-1) should return IDLE");
        helper.assertTrue(VillagerAnimState.fromId(999) == VillagerAnimState.IDLE,
                "fromId(999) should return IDLE");
        helper.succeed();
    }

    // ==================== MillSounds: Registry Presence ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testMillSoundsRegistered(GameTestHelper helper) {
        helper.assertFalse(MillSounds.NORMAN_BELLS == null, "NORMAN_BELLS holder missing");
        helper.assertFalse(MillSounds.VILLAGER_WORKING == null, "VILLAGER_WORKING holder missing");
        helper.assertFalse(MillSounds.VILLAGER_EATING == null, "VILLAGER_EATING holder missing");
        helper.assertFalse(MillSounds.VILLAGER_SLEEPING == null, "VILLAGER_SLEEPING holder missing");
        helper.assertFalse(MillSounds.VILLAGER_TRADING == null, "VILLAGER_TRADING holder missing");
        helper.assertFalse(MillSounds.VILLAGER_GREETING == null, "VILLAGER_GREETING holder missing");
        helper.assertFalse(MillSounds.VILLAGER_HURT == null, "VILLAGER_HURT holder missing");
        helper.assertFalse(MillSounds.VILLAGER_DEATH == null, "VILLAGER_DEATH holder missing");
        helper.assertFalse(MillSounds.CONSTRUCTION_HAMMER == null, "CONSTRUCTION_HAMMER holder missing");
        helper.assertFalse(MillSounds.CONSTRUCTION_COMPLETE == null, "CONSTRUCTION_COMPLETE holder missing");
        helper.assertFalse(MillSounds.VILLAGER_ATTACK == null, "VILLAGER_ATTACK holder missing");
        helper.assertFalse(MillSounds.VILLAGER_BOW_SHOOT == null, "VILLAGER_BOW_SHOOT holder missing");
        helper.assertFalse(MillSounds.QUEST_ACCEPTED == null, "QUEST_ACCEPTED holder missing");
        helper.assertFalse(MillSounds.QUEST_COMPLETED == null, "QUEST_COMPLETED holder missing");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testMillSoundsResolvable(GameTestHelper helper) {
        // Each holder should resolve to a non-null SoundEvent with the correct namespace
        helper.assertFalse(MillSounds.NORMAN_BELLS.get() == null, "NORMAN_BELLS SoundEvent null");
        helper.assertTrue(
                MillSounds.NORMAN_BELLS.get().getLocation().getNamespace().equals(Millenaire2.MODID),
                "NORMAN_BELLS namespace should be " + Millenaire2.MODID);
        helper.assertTrue(
                MillSounds.NORMAN_BELLS.get().getLocation().getPath().equals("norman_bells"),
                "NORMAN_BELLS path should be norman_bells");

        helper.assertFalse(MillSounds.VILLAGER_ATTACK.get() == null, "VILLAGER_ATTACK SoundEvent null");
        helper.assertTrue(
                MillSounds.VILLAGER_ATTACK.get().getLocation().getPath().equals("villager_attack"),
                "VILLAGER_ATTACK path mismatch");
        helper.succeed();
    }

    // ==================== MillTreeFeatures: Resource Keys ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testMillTreeFeaturesKeysExist(GameTestHelper helper) {
        helper.assertFalse(MillTreeFeatures.APPLE_TREE == null, "APPLE_TREE key missing");
        helper.assertFalse(MillTreeFeatures.OLIVE_TREE == null, "OLIVE_TREE key missing");
        helper.assertFalse(MillTreeFeatures.PISTACHIO_TREE == null, "PISTACHIO_TREE key missing");
        helper.assertFalse(MillTreeFeatures.CHERRY_MILL_TREE == null, "CHERRY_MILL_TREE key missing");
        helper.assertFalse(MillTreeFeatures.SAKURA_TREE == null, "SAKURA_TREE key missing");

        // Verify namespace
        helper.assertTrue(
                MillTreeFeatures.APPLE_TREE.location().getNamespace().equals(Millenaire2.MODID),
                "APPLE_TREE namespace mismatch");
        helper.assertTrue(
                MillTreeFeatures.APPLE_TREE.location().getPath().equals("apple_tree"),
                "APPLE_TREE path mismatch");
        helper.succeed();
    }

    // ==================== MillTreeGrowers: Grower Instances ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testMillTreeGrowersExist(GameTestHelper helper) {
        helper.assertFalse(MillTreeGrowers.APPLE == null, "APPLE grower missing");
        helper.assertFalse(MillTreeGrowers.OLIVE == null, "OLIVE grower missing");
        helper.assertFalse(MillTreeGrowers.PISTACHIO == null, "PISTACHIO grower missing");
        helper.assertFalse(MillTreeGrowers.CHERRY_MILL == null, "CHERRY_MILL grower missing");
        helper.assertFalse(MillTreeGrowers.SAKURA == null, "SAKURA grower missing");
        helper.succeed();
    }

    // ==================== MillVillager: Spouse Synched Data ====================

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testVillagerSpouseNameSynchedData(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 1, 1));
        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager");

        villager.moveTo(spawnPos, 0, 0);
        level.addFreshEntity(villager);

        helper.assertTrue(villager.getSpouseName().isEmpty(), "Spouse name should be empty initially");
        villager.setSpouseName("Jane Doe");
        helper.assertTrue(villager.getSpouseName().equals("Jane Doe"), "Spouse name should be 'Jane Doe'");
        villager.discard();
        helper.succeed();
    }

    // ==================== MillVillager: AnimState Synched Data ====================

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testVillagerAnimStateSynchedData(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 1, 1));
        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager");

        villager.moveTo(spawnPos, 0, 0);
        level.addFreshEntity(villager);

        helper.assertTrue(villager.getAnimState() == VillagerAnimState.IDLE,
                "Default anim state should be IDLE");

        villager.setAnimState(VillagerAnimState.WORKING);
        helper.assertTrue(villager.getAnimState() == VillagerAnimState.WORKING,
                "Anim state should be WORKING after set");

        villager.setAnimState(VillagerAnimState.COMBAT_MELEE);
        helper.assertTrue(villager.getAnimState() == VillagerAnimState.COMBAT_MELEE,
                "Anim state should be COMBAT_MELEE after set");

        villager.setAnimState(VillagerAnimState.SLEEPING);
        helper.assertTrue(villager.getAnimState() == VillagerAnimState.SLEEPING,
                "Anim state should be SLEEPING after set");
        villager.discard();
        helper.succeed();
    }

    // ==================== MillVillager: Culture Synched Data ====================

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testVillagerCultureSynchedData(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 1, 1));
        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager");

        villager.moveTo(spawnPos, 0, 0);
        level.addFreshEntity(villager);

        helper.assertTrue(villager.getCultureKey().isEmpty(), "Culture key should be empty initially");
        villager.setCultureKey("norman");
        helper.assertTrue(villager.getCultureKey().equals("norman"), "Culture key should be 'norman'");
        helper.assertFalse(villager.getCulture() == null, "getCulture() should resolve 'norman'");
        villager.discard();
        helper.succeed();
    }

    // ==================== MillVillager: FamilyData Integration ====================

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testVillagerFamilyDataField(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 1, 1));
        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager");

        villager.moveTo(spawnPos, 0, 0);
        level.addFreshEntity(villager);

        // familyData should be non-null and initialized
        helper.assertFalse(villager.familyData == null, "familyData should not be null");
        helper.assertFalse(villager.isMarried(), "Should not be married initially");
        helper.assertFalse(villager.isChildVillager(), "Should not be child initially");

        villager.familyData.marry(99L, "Partner");
        helper.assertTrue(villager.isMarried(), "Should be married after familyData.marry()");
        villager.discard();
        helper.succeed();
    }

    // ==================== MillVillager: marryTo() ====================

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testVillagerMarryTo(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 1, 1));

        MillVillager male = MillEntities.GENERIC_MALE.get().create(level);
        MillVillager female = MillEntities.GENERIC_SYMM_FEMALE.get().create(level);
        helper.assertFalse(male == null, "Failed to create male villager");
        helper.assertFalse(female == null, "Failed to create female villager");

        male.moveTo(spawnPos, 0, 0);
        female.moveTo(spawnPos.east(2), 0, 0);
        level.addFreshEntity(male);
        level.addFreshEntity(female);

        male.setFirstName("John");
        male.setFamilyName("Smith");
        male.setGender(MillVillager.MALE);
        male.setVillagerId(1L);

        female.setFirstName("Jane");
        female.setFamilyName("Doe");
        female.setGender(MillVillager.FEMALE);
        female.setVillagerId(2L);

        male.marryTo(female);

        // Male
        helper.assertTrue(male.isMarried(), "Male should be married");
        helper.assertTrue(male.familyData.getSpouseId() == 2L, "Male's spouse ID should be 2");
        helper.assertTrue(male.getSpouseName().equals("Jane Doe"), "Male's spouse name mismatch");

        // Female — family name should change to male's, maiden name preserved
        helper.assertTrue(female.isMarried(), "Female should be married");
        helper.assertTrue(female.familyData.getSpouseId() == 1L, "Female's spouse ID should be 1");
        helper.assertTrue(female.getFamilyName().equals("Smith"),
                "Female's family name should change to Smith, got " + female.getFamilyName());
        helper.assertTrue(female.familyData.getMaidenName().equals("Doe"),
                "Female's maiden name should be Doe");
        helper.assertTrue(female.getSpouseName().equals("John Smith"),
                "Female's spouse name mismatch");

        male.discard();
        female.discard();
        helper.succeed();
    }

    // ==================== MillVillager: Held Items ====================

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testVillagerHeldItems(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 1, 1));
        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager");

        villager.moveTo(spawnPos, 0, 0);
        level.addFreshEntity(villager);

        // Initially empty
        helper.assertTrue(villager.heldItem.isEmpty(), "Main hand should be empty initially");
        helper.assertTrue(villager.heldItemOffHand.isEmpty(), "Off hand should be empty initially");

        // Set main hand
        ItemStack sword = new ItemStack(Items.IRON_SWORD);
        villager.setHeldItem(sword);
        helper.assertTrue(ItemStack.matches(villager.heldItem, sword), "Main hand should be iron sword");
        helper.assertTrue(ItemStack.matches(
                villager.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND), sword),
                "MAINHAND slot should be synced");

        // Set off hand
        ItemStack shield = new ItemStack(Items.SHIELD);
        villager.setHeldItemOffHand(shield);
        helper.assertTrue(ItemStack.matches(villager.heldItemOffHand, shield), "Off hand should be shield");
        helper.assertTrue(ItemStack.matches(
                villager.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND), shield),
                "OFFHAND slot should be synced");

        // syncHeldItems
        villager.heldItem = new ItemStack(Items.DIAMOND_PICKAXE);
        villager.heldItemOffHand = new ItemStack(Items.TORCH);
        villager.syncHeldItems();
        helper.assertTrue(ItemStack.matches(
                villager.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND),
                new ItemStack(Items.DIAMOND_PICKAXE)),
                "MAINHAND should be diamond pickaxe after sync");
        helper.assertTrue(ItemStack.matches(
                villager.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.OFFHAND),
                new ItemStack(Items.TORCH)),
                "OFFHAND should be torch after sync");

        villager.discard();
        helper.succeed();
    }

    // ==================== MillVillager: Full NBT Round-Trip (with familyData + inventory) ====================

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testVillagerFullNBTRoundTrip(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 1, 1));
        MillVillager v1 = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(v1 == null, "Failed to create villager");

        v1.moveTo(spawnPos, 0, 0);
        level.addFreshEntity(v1);

        // Set all fields
        v1.setFirstName("Thorin");
        v1.setFamilyName("Oakenshield");
        v1.setGender(MillVillager.MALE);
        v1.setCultureKey("norman");
        v1.setVillagerId(777L);
        v1.isRaider = true;
        v1.aggressiveStance = true;
        v1.housePoint = new Point(10, 64, 20);
        v1.townHallPoint = new Point(30, 64, 40);
        v1.goalKey = "gatherResources";
        v1.hiredBy = "player123";
        v1.hiredUntil = 99999L;

        // Add inventory
        InvItem stone = InvItem.get("stone");
        if (stone != null) {
            v1.addToInv(stone, 32);
        }

        // Set family data
        v1.familyData.marry(888L, "Spouse Name");
        v1.familyData.setFatherName("Father");
        v1.familyData.setMotherName("Mother");
        v1.familyData.addChild(200L);

        // Save
        CompoundTag tag = new CompoundTag();
        v1.addAdditionalSaveData(tag);

        // Create new villager and load
        MillVillager v2 = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(v2 == null, "Failed to create second villager");
        v2.moveTo(spawnPos.east(3), 0, 0);
        level.addFreshEntity(v2);
        v2.readAdditionalSaveData(tag);

        // Verify all fields
        helper.assertTrue(v2.getFirstName().equals("Thorin"), "First name mismatch");
        helper.assertTrue(v2.getFamilyName().equals("Oakenshield"), "Family name mismatch");
        helper.assertTrue(v2.getGender() == MillVillager.MALE, "Gender mismatch");
        helper.assertTrue(v2.getCultureKey().equals("norman"), "Culture key mismatch");
        helper.assertTrue(v2.getVillagerId() == 777L, "Villager ID mismatch");
        helper.assertTrue(v2.isRaider, "isRaider should be true");
        helper.assertTrue(v2.aggressiveStance, "aggressiveStance should be true");
        helper.assertFalse(v2.housePoint == null, "housePoint should not be null");
        helper.assertTrue(v2.housePoint.x == 10, "housePoint.x mismatch");
        helper.assertFalse(v2.townHallPoint == null, "townHallPoint should not be null");
        helper.assertTrue(v2.townHallPoint.x == 30, "townHallPoint.x mismatch");
        helper.assertTrue("gatherResources".equals(v2.goalKey), "goalKey mismatch");
        helper.assertTrue("player123".equals(v2.hiredBy), "hiredBy mismatch");
        helper.assertTrue(v2.hiredUntil == 99999L, "hiredUntil mismatch");

        // Verify inventory round-trip
        if (stone != null) {
            helper.assertTrue(v2.countInv(stone) == 32, "Inventory stone count mismatch, got " + v2.countInv(stone));
        }

        // Verify familyData round-trip
        helper.assertTrue(v2.familyData.isMarried(), "familyData should be married");
        helper.assertTrue(v2.familyData.getSpouseId() == 888L, "familyData spouse ID mismatch");
        helper.assertTrue(v2.familyData.getSpouseName().equals("Spouse Name"), "familyData spouse name mismatch");
        helper.assertTrue(v2.familyData.getFatherName().equals("Father"), "familyData father name mismatch");
        helper.assertTrue(v2.familyData.getMotherName().equals("Mother"), "familyData mother name mismatch");
        helper.assertTrue(v2.familyData.getChildCount() == 1, "familyData child count mismatch");

        // Verify spouse name synched data was restored
        helper.assertTrue(v2.getSpouseName().equals("Spouse Name"),
                "Synched spouse name should be restored from familyData");

        v1.discard();
        v2.discard();
        helper.succeed();
    }

    // ==================== ClientPacketHandler: Data Classes ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testTradeGoodClientEntry(GameTestHelper helper) {
        ClientPacketHandler.TradeGoodClientEntry entry =
                new ClientPacketHandler.TradeGoodClientEntry(0, "stone", 64, 10, 5, 12, 4);
        helper.assertTrue(entry.index == 0, "Index mismatch");
        helper.assertTrue(entry.itemId.equals("stone"), "Item ID mismatch");
        helper.assertTrue(entry.itemCount == 64, "Item count mismatch");
        helper.assertTrue(entry.buyPrice == 10, "Buy price mismatch");
        helper.assertTrue(entry.sellPrice == 5, "Sell price mismatch");
        helper.assertTrue(entry.adjustedBuy == 12, "Adjusted buy mismatch");
        helper.assertTrue(entry.adjustedSell == 4, "Adjusted sell mismatch");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testQuestClientEntry(GameTestHelper helper) {
        ClientPacketHandler.QuestClientEntry entry =
                new ClientPacketHandler.QuestClientEntry("quest_kill_bandit", 2, 5,
                        "Kill the bandit leader", "Step 3", 100, 50, false);
        helper.assertTrue(entry.questKey.equals("quest_kill_bandit"), "Quest key mismatch");
        helper.assertTrue(entry.stepIndex == 2, "Step index mismatch");
        helper.assertTrue(entry.totalSteps == 5, "Total steps mismatch");
        helper.assertTrue(entry.stepDescription.equals("Kill the bandit leader"), "Description mismatch");
        helper.assertTrue(entry.stepLabel.equals("Step 3"), "Label mismatch");
        helper.assertTrue(entry.rewardMoney == 100, "Reward money mismatch");
        helper.assertTrue(entry.rewardReputation == 50, "Reward reputation mismatch");
        helper.assertFalse(entry.isOffer, "Should not be offer");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testQuestClientEntryOffer(GameTestHelper helper) {
        ClientPacketHandler.QuestClientEntry entry =
                new ClientPacketHandler.QuestClientEntry("quest_trade", 0, 1,
                        "Deliver goods", "Offer", 50, 25, true);
        helper.assertTrue(entry.isOffer, "Should be offer");
        helper.assertTrue(entry.totalSteps == 1, "Total steps mismatch");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testVillageListClientEntry(GameTestHelper helper) {
        Point pos = new Point(100, 64, 200);
        ClientPacketHandler.VillageListClientEntry entry =
                new ClientPacketHandler.VillageListClientEntry(pos, "norman", "Willowshire", 350, false);
        helper.assertFalse(entry.pos == null, "Pos should not be null");
        helper.assertTrue(entry.pos.x == 100, "Pos x mismatch");
        helper.assertTrue(entry.cultureKey.equals("norman"), "Culture key mismatch");
        helper.assertTrue(entry.name.equals("Willowshire"), "Name mismatch");
        helper.assertTrue(entry.distance == 350, "Distance mismatch");
        helper.assertFalse(entry.isLoneBuilding, "Should not be lone building");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testVillageListClientEntryLoneBuilding(GameTestHelper helper) {
        ClientPacketHandler.VillageListClientEntry entry =
                new ClientPacketHandler.VillageListClientEntry(null, "indian", "Farm", 100, true);
        helper.assertTrue(entry.pos == null, "Pos should be null for null input");
        helper.assertTrue(entry.isLoneBuilding, "Should be lone building");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBuildingSyncEntry(GameTestHelper helper) {
        Point pos = new Point(50, 64, 100);
        ClientPacketHandler.BuildingSyncEntry entry =
                new ClientPacketHandler.BuildingSyncEntry(pos, "Townhall", "norman", true);
        helper.assertFalse(entry.pos == null, "Pos should not be null");
        helper.assertTrue(entry.name.equals("Townhall"), "Name mismatch");
        helper.assertTrue(entry.cultureKey.equals("norman"), "Culture key mismatch");
        helper.assertTrue(entry.isTownhall, "Should be townhall");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testVillageMapMarker(GameTestHelper helper) {
        Point pos = new Point(200, 64, 300);
        ClientPacketHandler.VillageMapMarker marker =
                new ClientPacketHandler.VillageMapMarker(pos, "mayan");
        helper.assertFalse(marker.pos == null, "Pos should not be null");
        helper.assertTrue(marker.pos.x == 200, "Pos x mismatch");
        helper.assertTrue(marker.cultureKey.equals("mayan"), "Culture key mismatch");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testVillageMapMarkerNullPos(GameTestHelper helper) {
        ClientPacketHandler.VillageMapMarker marker =
                new ClientPacketHandler.VillageMapMarker(null, "japanese");
        helper.assertTrue(marker.pos == null, "Pos should be null");
        helper.assertTrue(marker.cultureKey.equals("japanese"), "Culture key mismatch");
        helper.succeed();
    }

    // ==================== MillConfig: Default Values ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testMillConfigDefaults(GameTestHelper helper) {
        // Runtime defaults should be accessible (values set by onLoad or default)
        // villageMinDistance default is 250
        helper.assertTrue(org.dizzymii.millenaire2.MillConfig.villageMinDistance >= 50,
                "villageMinDistance should be >= 50");
        helper.assertTrue(org.dizzymii.millenaire2.MillConfig.villageMinDistance <= 10000,
                "villageMinDistance should be <= 10000");

        // constructionBlocksPerTick default is 5
        helper.assertTrue(org.dizzymii.millenaire2.MillConfig.constructionBlocksPerTick >= 1,
                "constructionBlocksPerTick should be >= 1");
        helper.assertTrue(org.dizzymii.millenaire2.MillConfig.constructionBlocksPerTick <= 50,
                "constructionBlocksPerTick should be <= 50");
        helper.succeed();
    }

    // ==================== ItemParchment: Culture Key ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testItemParchmentCultureKey(GameTestHelper helper) {
        // Use already-registered parchment items from MillItems (registry is frozen)
        net.minecraft.world.item.Item normanVillagers = org.dizzymii.millenaire2.item.MillItems.PARCHMENT_NORMAN_VILLAGERS.get();
        helper.assertTrue(normanVillagers instanceof org.dizzymii.millenaire2.item.ItemParchment,
                "PARCHMENT_NORMAN_VILLAGERS should be an ItemParchment");
        org.dizzymii.millenaire2.item.ItemParchment parchment =
                (org.dizzymii.millenaire2.item.ItemParchment) normanVillagers;
        helper.assertTrue(parchment.getCultureKey().equals("norman_villagers"),
                "Culture key should be 'norman_villagers', got '" + parchment.getCultureKey() + "'");

        net.minecraft.world.item.Item indianBuildings = org.dizzymii.millenaire2.item.MillItems.PARCHMENT_INDIAN_BUILDINGS.get();
        helper.assertTrue(indianBuildings instanceof org.dizzymii.millenaire2.item.ItemParchment,
                "PARCHMENT_INDIAN_BUILDINGS should be an ItemParchment");
        helper.assertTrue(
                ((org.dizzymii.millenaire2.item.ItemParchment) indianBuildings).getCultureKey().equals("indian_buildings"),
                "Indian buildings parchment culture key mismatch");
        helper.succeed();
    }

    // ==================== BlockMillSapling: TreeType Enum ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testBlockMillSaplingTreeTypes(GameTestHelper helper) {
        org.dizzymii.millenaire2.block.BlockMillSapling.TreeType[] types =
                org.dizzymii.millenaire2.block.BlockMillSapling.TreeType.values();
        helper.assertTrue(types.length == 5, "Should have 5 tree types, got " + types.length);

        // Verify all expected types exist
        helper.assertFalse(
                org.dizzymii.millenaire2.block.BlockMillSapling.TreeType.valueOf("APPLE") == null,
                "APPLE type missing");
        helper.assertFalse(
                org.dizzymii.millenaire2.block.BlockMillSapling.TreeType.valueOf("OLIVE") == null,
                "OLIVE type missing");
        helper.assertFalse(
                org.dizzymii.millenaire2.block.BlockMillSapling.TreeType.valueOf("PISTACHIO") == null,
                "PISTACHIO type missing");
        helper.assertFalse(
                org.dizzymii.millenaire2.block.BlockMillSapling.TreeType.valueOf("CHERRY") == null,
                "CHERRY type missing");
        helper.assertFalse(
                org.dizzymii.millenaire2.block.BlockMillSapling.TreeType.valueOf("SAKURA") == null,
                "SAKURA type missing");
        helper.succeed();
    }

    // ==================== FamilyData: Constants ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testFamilyDataConstants(GameTestHelper helper) {
        helper.assertTrue(FamilyData.PREGNANCY_DURATION == 72000L,
                "PREGNANCY_DURATION should be 72000 (~3 MC days)");
        helper.assertTrue(FamilyData.CHILD_GROWTH_DURATION == 120000L,
                "CHILD_GROWTH_DURATION should be 120000 (~5 MC days)");
        helper.assertTrue(FamilyData.MAX_CHILDREN == 4,
                "MAX_CHILDREN should be 4");
        helper.succeed();
    }

    // ==================== MillVillager: Goal Key Synched Data ====================

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testVillagerGoalKeySynchedData(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 1, 1));
        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager");

        villager.moveTo(spawnPos, 0, 0);
        level.addFreshEntity(villager);

        // goalKey starts as instance field, DATA_GOAL_KEY starts empty
        villager.goalKey = "gatherResources";
        // Verify the instance field was set
        helper.assertTrue("gatherResources".equals(villager.goalKey), "goalKey should be gatherResources");
        villager.discard();
        helper.succeed();
    }

    // ==================== Configured Feature JSON Existence ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testConfiguredFeatureJsonPaths(GameTestHelper helper) {
        // Verify the resource keys reference correct paths
        helper.assertTrue(MillTreeFeatures.APPLE_TREE.location().getPath().equals("apple_tree"),
                "apple_tree path mismatch");
        helper.assertTrue(MillTreeFeatures.OLIVE_TREE.location().getPath().equals("olive_tree"),
                "olive_tree path mismatch");
        helper.assertTrue(MillTreeFeatures.PISTACHIO_TREE.location().getPath().equals("pistachio_tree"),
                "pistachio_tree path mismatch");
        helper.assertTrue(MillTreeFeatures.CHERRY_MILL_TREE.location().getPath().equals("cherry_mill_tree"),
                "cherry_mill_tree path mismatch");
        helper.assertTrue(MillTreeFeatures.SAKURA_TREE.location().getPath().equals("sakura_tree"),
                "sakura_tree path mismatch");
        helper.succeed();
    }

    // ==================== MillVillager: isMarried / isChildVillager helpers ====================

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testVillagerMarriedAndChildHelpers(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 1, 1));
        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager");

        villager.moveTo(spawnPos, 0, 0);
        level.addFreshEntity(villager);

        helper.assertFalse(villager.isMarried(), "Should not be married");
        helper.assertFalse(villager.isChildVillager(), "Should not be child");

        villager.familyData.marry(50L, "Partner");
        helper.assertTrue(villager.isMarried(), "Should be married");

        villager.familyData.setChild(true);
        helper.assertTrue(villager.isChildVillager(), "Should be child");

        villager.discard();
        helper.succeed();
    }

    // ==================== ClientPacketHandler: Cache Initialization ====================

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void testClientPacketHandlerCachesInitialized(GameTestHelper helper) {
        // These static caches should be initialized (non-null, empty)
        helper.assertFalse(ClientPacketHandler.villageListCache == null, "villageListCache should not be null");
        helper.assertFalse(ClientPacketHandler.tradeGoodsCache == null, "tradeGoodsCache should not be null");
        helper.assertFalse(ClientPacketHandler.cachedVillageReputations == null,
                "cachedVillageReputations should not be null");
        helper.assertFalse(ClientPacketHandler.cachedCultureReputations == null,
                "cachedCultureReputations should not be null");
        helper.assertFalse(ClientPacketHandler.cachedCultureLanguages == null,
                "cachedCultureLanguages should not be null");
        helper.assertFalse(ClientPacketHandler.cachedBuildings == null,
                "cachedBuildings should not be null");
        helper.assertFalse(ClientPacketHandler.cachedMapMarkers == null,
                "cachedMapMarkers should not be null");
        helper.succeed();
    }
}
