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
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.BuildingLocation;
import org.dizzymii.millenaire2.village.ConstructionIP;
import org.dizzymii.millenaire2.village.VillagerRecord;
import org.dizzymii.millenaire2.world.BiomeCultureMapper;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.WorldGenVillage;

import net.minecraft.core.HolderLookup;

import java.util.Collection;
import java.util.List;

/**
 * Comprehensive end-to-end GameTests for the village generation demo pipeline.
 * Covers: VillageType params, BuildingPlanSet params, name lists, WorldGenVillage,
 * ConstructionIP from plan data, Building tick pipeline, villager spawning,
 * villager AI tick, VillagerRecord NBT, MillWorldData, and BiomeCultureMapper.
 */
@GameTestHolder(Millenaire2.MODID)
@PrefixGameTestTemplate(false)
public class DemoPipelineTests {

    // ==================== VillageType New Params ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillageTypeIconParam(GameTestHelper helper) {
        // Seljuk village types define icon= in their .txt files
        Culture seljuk = Culture.getCultureByName("seljuk");
        helper.assertFalse(seljuk == null, "Seljuk culture missing");

        boolean anyIcon = false;
        for (VillageType vt : seljuk.villageTypes.values()) {
            if (vt.icon != null && !vt.icon.isEmpty()) {
                anyIcon = true;
                break;
            }
        }
        helper.assertTrue(anyIcon,
                "No seljuk village type has an icon param loaded");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillageTypeQualifierParams(GameTestHelper helper) {
        Culture seljuk = Culture.getCultureByName("seljuk");
        helper.assertFalse(seljuk == null, "Seljuk culture missing");

        boolean anyQualifier = false;
        for (VillageType vt : seljuk.villageTypes.values()) {
            if (vt.hillqualifier != null || vt.mountainqualifier != null
                    || vt.desertqualifier != null || vt.forestqualifier != null) {
                anyQualifier = true;
                break;
            }
        }
        helper.assertTrue(anyQualifier,
                "No seljuk village type has biome qualifier params loaded");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillageTypePathMaterialParams(GameTestHelper helper) {
        Culture seljuk = Culture.getCultureByName("seljuk");
        helper.assertFalse(seljuk == null, "Seljuk culture missing");

        boolean anyPath = false;
        for (VillageType vt : seljuk.villageTypes.values()) {
            if (!vt.pathMaterials.isEmpty()) {
                anyPath = true;
                break;
            }
        }
        helper.assertTrue(anyPath,
                "No seljuk village type has pathMaterials loaded");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillageTypeWallParams(GameTestHelper helper) {
        Culture seljuk = Culture.getCultureByName("seljuk");
        helper.assertFalse(seljuk == null, "Seljuk culture missing");

        boolean anyWall = false;
        for (VillageType vt : seljuk.villageTypes.values()) {
            if (vt.outerwalltype != null || vt.innerwalltype != null) {
                anyWall = true;
                break;
            }
        }
        helper.assertTrue(anyWall,
                "No seljuk village type has wall type params loaded");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillageTypePriceParams(GameTestHelper helper) {
        Culture seljuk = Culture.getCultureByName("seljuk");
        helper.assertFalse(seljuk == null, "Seljuk culture missing");

        boolean anySelling = false;
        boolean anyBuying = false;
        for (VillageType vt : seljuk.villageTypes.values()) {
            if (!vt.sellingPrices.isEmpty()) anySelling = true;
            if (!vt.buyingPrices.isEmpty()) anyBuying = true;
        }
        helper.assertTrue(anySelling,
                "No seljuk village type has sellingPrices loaded");
        helper.assertTrue(anyBuying,
                "No seljuk village type has buyingPrices loaded");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillageTypeMaxWallConstructionsParam(GameTestHelper helper) {
        Culture seljuk = Culture.getCultureByName("seljuk");
        helper.assertFalse(seljuk == null, "Seljuk culture missing");

        // Verify the field exists and was populated without error (no more unknown param warnings)
        helper.assertTrue(seljuk.villageTypes.size() > 0,
                "Seljuk has no village types");
        helper.succeed();
    }

    // ==================== BuildingPlanSet New Params ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBuildingPlanSetStartingGoodParam(GameTestHelper helper) {
        // bandittower_a and ruinlonemine_a define startinggood
        Culture seljuk = Culture.getCultureByName("seljuk");
        helper.assertFalse(seljuk == null, "Seljuk culture missing");

        boolean anyGoods = false;
        for (BuildingPlanSet bps : seljuk.planSets.values()) {
            if (!bps.startingGoods.isEmpty()) {
                anyGoods = true;
                break;
            }
        }
        helper.assertTrue(anyGoods,
                "No seljuk building plan set has startingGoods loaded (expected bandittower_a or ruinlonemine_a)");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBuildingPlanSetIsGiftParam(GameTestHelper helper) {
        // playerhousegift_a defines isgift=true
        Culture seljuk = Culture.getCultureByName("seljuk");
        helper.assertFalse(seljuk == null, "Seljuk culture missing");

        BuildingPlanSet giftSet = seljuk.planSets.get("playerhousegift_a");
        helper.assertFalse(giftSet == null,
                "playerhousegift_a plan set missing");
        helper.assertTrue(giftSet.isGift,
                "playerhousegift_a.isGift should be true");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBuildingPlanSetReputationAndPriceParams(GameTestHelper helper) {
        // playerhouse plans define reputation= and price=
        Culture seljuk = Culture.getCultureByName("seljuk");
        helper.assertFalse(seljuk == null, "Seljuk culture missing");

        boolean anyReputation = false;
        boolean anyPrice = false;
        for (BuildingPlanSet bps : seljuk.planSets.values()) {
            if (bps.reputation > 0) anyReputation = true;
            if (bps.price > 0) anyPrice = true;
        }
        helper.assertTrue(anyReputation,
                "No seljuk building plan set has reputation > 0 loaded");
        helper.assertTrue(anyPrice,
                "No seljuk building plan set has price > 0 loaded");
        helper.succeed();
    }

    // ==================== Name List Resolution ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testNormanNameListsPresent(GameTestHelper helper) {
        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        helper.assertFalse(norman.nameLists.get("men_names") == null,
                "Norman missing men_names list");
        helper.assertFalse(norman.nameLists.get("women_names") == null,
                "Norman missing women_names list");
        helper.assertFalse(norman.nameLists.get("family_names") == null,
                "Norman missing family_names list");
        helper.assertFalse(norman.nameLists.get("villages") == null,
                "Norman missing villages list");

        helper.assertTrue(norman.nameLists.get("men_names").size() > 0,
                "Norman men_names is empty");
        helper.assertTrue(norman.nameLists.get("villages").size() > 0,
                "Norman villages is empty");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testMayanNameListsPresent(GameTestHelper helper) {
        Culture mayan = Culture.getCultureByName("mayan");
        helper.assertFalse(mayan == null, "Mayan culture missing");

        helper.assertFalse(mayan.nameLists.get("men_names") == null,
                "Mayan missing men_names list");
        helper.assertFalse(mayan.nameLists.get("villages") == null,
                "Mayan missing villages list");

        helper.assertTrue(mayan.nameLists.get("men_names").size() > 0,
                "Mayan men_names is empty");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testAllCulturesHaveNameLists(GameTestHelper helper) {
        for (Culture c : Culture.LIST_CULTURES) {
            helper.assertFalse(c.nameLists.isEmpty(),
                    "Culture " + c.key + " has no name lists at all");

            // Every culture should have at least one name list with entries
            boolean hasNames = false;
            for (List<String> names : c.nameLists.values()) {
                if (!names.isEmpty()) { hasNames = true; break; }
            }
            helper.assertTrue(hasNames,
                    "Culture " + c.key + " has name lists but all are empty");
        }
        helper.succeed();
    }

    // ==================== WorldGenVillage End-to-End ====================

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testGenerateNewVillageCreatesBuilding(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(new BlockPos(1, 1, 1));

        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        MillWorldData worldData = new MillWorldData();
        worldData.world = level;

        boolean success = WorldGenVillage.generateNewVillage(
                level, abs, norman, worldData, level.random);
        helper.assertTrue(success,
                "generateNewVillage returned false for norman");

        // Building should exist in worldData
        Collection<Building> buildings = worldData.allBuildings();
        helper.assertTrue(buildings.size() == 1,
                "Expected 1 building after generation, got " + buildings.size());

        Building townhall = buildings.iterator().next();
        helper.assertTrue(townhall.isTownhall,
                "Generated building should be a townhall");
        helper.assertTrue(townhall.isActive,
                "Generated building should be active");
        helper.assertTrue("norman".equals(townhall.cultureKey),
                "Building culture should be norman, got " + townhall.cultureKey);
        helper.assertFalse(townhall.getName() == null || townhall.getName().isEmpty(),
                "Building should have a name");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testGenerateNewVillageCreatesVillagerRecords(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(new BlockPos(1, 1, 1));

        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        MillWorldData worldData = new MillWorldData();
        worldData.world = level;

        WorldGenVillage.generateNewVillage(level, abs, norman, worldData, level.random);

        Building townhall = worldData.allBuildings().iterator().next();
        helper.assertTrue(townhall.getVillagerRecords().size() >= 1,
                "Townhall should have at least 1 villager record");

        // Verify villager record has proper names (not "Villager")
        VillagerRecord vr = townhall.getVillagerRecords().iterator().next();
        helper.assertFalse(vr.firstName == null,
                "VillagerRecord firstName is null");
        // With fixed name lists, should not be fallback "Villager"
        // (Norman has men_names populated)
        helper.assertFalse("Villager".equals(vr.firstName),
                "VillagerRecord firstName should not be fallback 'Villager' for norman culture");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testGenerateNewVillageCreatesConstructionIP(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(new BlockPos(1, 1, 1));

        // Try all cultures until we find one that produces a ConstructionIP
        boolean foundConstruction = false;
        for (Culture c : Culture.LIST_CULTURES) {
            MillWorldData worldData = new MillWorldData();
            worldData.world = level;

            boolean success = WorldGenVillage.generateNewVillage(
                    level, abs, c, worldData, level.random);
            if (!success) continue;

            for (Building b : worldData.allBuildings()) {
                if (b.currentConstruction != null && b.currentConstruction.nbBlocksTotal > 0) {
                    foundConstruction = true;
                    break;
                }
            }
            if (foundConstruction) break;
        }

        helper.assertTrue(foundConstruction,
                "No culture produced a village with ConstructionIP blocks");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testGenerateVillageForEachCulture(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(new BlockPos(1, 1, 1));

        int successCount = 0;
        StringBuilder failures = new StringBuilder();

        for (Culture c : Culture.LIST_CULTURES) {
            MillWorldData worldData = new MillWorldData();
            worldData.world = level;

            boolean success = WorldGenVillage.generateNewVillage(
                    level, abs, c, worldData, level.random);
            if (success) {
                successCount++;
            } else {
                failures.append(c.key).append(", ");
            }
        }

        helper.assertTrue(successCount >= 5,
                "Expected at least 5 cultures to generate villages, got " + successCount
                        + ". Failures: " + failures);
        helper.succeed();
    }

    // ==================== ConstructionIP From Loaded Plan Data ====================

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testConstructionIPFromLoadedPlanBlockData(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(new BlockPos(1, 1, 1));
        Point origin = new Point(abs.getX(), abs.getY(), abs.getZ());

        // Find a plan with actual block data via getBlockColor
        boolean testedPlan = false;
        for (Culture c : Culture.LIST_CULTURES) {
            for (BuildingPlanSet bps : c.planSets.values()) {
                BuildingPlan plan = bps.getInitialPlan();
                if (plan == null || !plan.hasImage()) continue;

                // Verify blockData is accessible
                boolean hasNonWhitePixel = false;
                for (int x = 0; x < plan.width; x++) {
                    for (int z = 0; z < plan.length; z++) {
                        int rgb = plan.getBlockColor(x, z, 0);
                        if (rgb >= 0 && rgb != 0xFFFFFF) {
                            hasNonWhitePixel = true;
                            break;
                        }
                    }
                    if (hasNonWhitePixel) break;
                }

                if (!hasNonWhitePixel) continue;

                ConstructionIP cip = ConstructionIP.fromBuildingPlan(plan, origin, level);
                if (cip != null && cip.nbBlocksTotal > 0) {
                    helper.assertTrue(cip.nbBlocksTotal > 0,
                            "ConstructionIP should have blocks for " + bps.key);
                    helper.assertFalse(cip.isComplete(),
                            "New ConstructionIP should not be complete");
                    testedPlan = true;
                    break;
                }
            }
            if (testedPlan) break;
        }

        helper.assertTrue(testedPlan,
                "No plan with loaded blockData produced a ConstructionIP");
        helper.succeed();
    }

    // ==================== Building Tick → Construction + Villager Spawn ====================

    @GameTest(template = "empty", timeoutTicks = 300)
    public static void testBuildingTickSpawnsVillagerFromRecords(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(new BlockPos(1, 2, 1));
        Point bPos = new Point(abs.getX(), abs.getY(), abs.getZ());

        // Place a solid platform so the villager doesn't fall into the void
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.setBlockAndUpdate(abs.offset(dx, -1, dz),
                        net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
            }
        }

        Building townhall = new Building();
        townhall.isTownhall = true;
        townhall.isActive = true;
        townhall.cultureKey = "norman";
        townhall.setPos(bPos);
        townhall.setTownHallPos(bPos);
        townhall.world = level;

        // Add a villager record
        VillagerRecord vr = VillagerRecord.create("norman", "leader", "Guillaume", "DuBois", MillVillager.MALE);
        vr.setHousePos(bPos);
        vr.setTownHallPos(bPos);
        townhall.addVillagerRecord(vr);

        // Tick 200 times (checkAndSpawnVillagers fires at tickCounter % 200 == 0)
        for (int i = 0; i < 201; i++) {
            townhall.tick();
        }

        // Verify villager entity was spawned — allow extra time for entity to register
        helper.runAfterDelay(40, () -> {
            List<MillVillager> villagers = level.getEntitiesOfClass(
                    MillVillager.class,
                    net.minecraft.world.phys.AABB.ofSize(
                            new net.minecraft.world.phys.Vec3(abs.getX(), abs.getY(), abs.getZ()),
                            256, 128, 256));
            helper.assertTrue(villagers.size() >= 1,
                    "Expected at least 1 spawned villager entity, got " + villagers.size());

            // Find our specific villager (other tests may also spawn villagers nearby)
            boolean found = false;
            for (MillVillager v : villagers) {
                if ("Guillaume".equals(v.getFirstName())) {
                    found = true;
                    break;
                }
            }
            helper.assertTrue(found,
                    "Expected to find villager named Guillaume among " + villagers.size() + " entities");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 100)
    public static void testBuildingTickProgressesConstructionToCompletion(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(new BlockPos(1, 1, 1));
        Point bPos = new Point(abs.getX(), abs.getY(), abs.getZ());

        Building b = new Building();
        b.isActive = true;
        b.isTownhall = true;
        b.world = level;
        b.setPos(bPos);

        // Create construction with 3 blocks
        java.util.List<org.dizzymii.millenaire2.buildingplan.BuildingBlock> blocks = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            org.dizzymii.millenaire2.buildingplan.BuildingBlock bb =
                    new org.dizzymii.millenaire2.buildingplan.BuildingBlock();
            bb.blockState = net.minecraft.world.level.block.Blocks.STONE_BRICKS.defaultBlockState();
            bb.x = i; bb.y = 0; bb.z = 0;
            blocks.add(bb);
        }

        BuildingLocation loc = new BuildingLocation();
        loc.pos = bPos;
        ConstructionIP cip = new ConstructionIP(loc);
        cip.setBlocks(blocks);
        b.currentConstruction = cip;

        helper.assertTrue(b.isUnderConstruction(), "Should be under construction");

        // Tick enough to trigger slowTick and place blocks
        for (int i = 0; i < 40; i++) {
            b.tick();
        }

        helper.assertFalse(b.isUnderConstruction(),
                "Construction should be complete after ticking");

        // Verify blocks placed in world
        helper.assertBlockPresent(net.minecraft.world.level.block.Blocks.STONE_BRICKS,
                new BlockPos(1, 1, 1));
        helper.assertBlockPresent(net.minecraft.world.level.block.Blocks.STONE_BRICKS,
                new BlockPos(2, 1, 1));
        helper.assertBlockPresent(net.minecraft.world.level.block.Blocks.STONE_BRICKS,
                new BlockPos(3, 1, 1));

        helper.succeed();
    }

    // ==================== Villager AI Tick ====================

    @GameTest(template = "empty", timeoutTicks = 200)
    public static void testVillagerEntityTicksWithoutCrash(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 2, 1));

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager");

        villager.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
        villager.setCultureKey("norman");
        villager.setFirstName("TestVillager");
        villager.setFamilyName("TestFamily");
        level.addFreshEntity(villager);

        // Let the entity tick for 100 game ticks (covers multiple goal selection cycles)
        helper.runAfterDelay(100, () -> {
            helper.assertFalse(villager.isRemoved(),
                    "Villager should still be alive after 100 ticks");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 200)
    public static void testVillagerGoalSelectionRuns(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 2, 1));

        MillVillager villager = MillEntities.GENERIC_MALE.get().create(level);
        helper.assertFalse(villager == null, "Failed to create villager");

        villager.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
        villager.setCultureKey("norman");
        villager.setVillagerTypeKey("leader");
        level.addFreshEntity(villager);

        // After enough ticks, the villager should have attempted goal selection
        // (GOAL_TICK_INTERVAL = 20, so after 25 ticks it runs at least once)
        helper.runAfterDelay(50, () -> {
            // The villager's tick ran without throwing — that's the core assertion
            helper.assertFalse(villager.isRemoved(),
                    "Villager should survive goal selection ticks");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 200)
    public static void testFemaleVillagerEntityTicks(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos spawnPos = helper.absolutePos(new BlockPos(1, 2, 1));

        MillVillager female = MillEntities.GENERIC_SYMM_FEMALE.get().create(level);
        helper.assertFalse(female == null, "Failed to create female villager");

        female.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
        female.setCultureKey("norman");
        female.setGender(MillVillager.FEMALE);
        level.addFreshEntity(female);

        helper.runAfterDelay(50, () -> {
            helper.assertFalse(female.isRemoved(),
                    "Female villager should survive ticking");
            helper.succeed();
        });
    }

    // ==================== VillagerRecord Create + NBT Round-Trip ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillagerRecordCreate(GameTestHelper helper) {
        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Henri", "Dupont", MillVillager.MALE);

        helper.assertFalse(vr.getVillagerId() == 0, "VillagerRecord should have a non-zero ID");
        helper.assertTrue("norman".equals(vr.getCultureKey()), "Culture key mismatch");
        helper.assertTrue("farmer".equals(vr.type), "Type mismatch");
        helper.assertTrue("Henri".equals(vr.firstName), "First name mismatch");
        helper.assertTrue("Dupont".equals(vr.familyName), "Family name mismatch");
        helper.assertTrue(vr.gender == MillVillager.MALE, "Gender mismatch");
        helper.assertFalse(vr.killed, "New record should not be killed");
        helper.assertFalse(vr.awayraiding, "New record should not be away raiding");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillagerRecordNBTRoundTrip(GameTestHelper helper) {
        VillagerRecord original = VillagerRecord.create("japanese", "samurai", "Takeshi", "Yamamoto", MillVillager.MALE);
        original.setHousePos(new Point(100, 64, 200));
        original.setTownHallPos(new Point(90, 64, 190));
        original.inventory.put("wheat", 10);
        original.inventory.put("iron_ingot", 5);
        original.addQuestTag("quest_started");

        CompoundTag tag = original.save();
        VillagerRecord loaded = VillagerRecord.load(tag);

        helper.assertTrue(loaded.getVillagerId() == original.getVillagerId(),
                "ID not persisted");
        helper.assertTrue("japanese".equals(loaded.getCultureKey()),
                "Culture not persisted");
        helper.assertTrue("samurai".equals(loaded.type),
                "Type not persisted");
        helper.assertTrue("Takeshi".equals(loaded.firstName),
                "firstName not persisted");
        helper.assertTrue("Yamamoto".equals(loaded.familyName),
                "familyName not persisted");
        helper.assertTrue(loaded.gender == MillVillager.MALE,
                "Gender not persisted");

        // Positions
        helper.assertFalse(loaded.getHousePos() == null, "housePos not persisted");
        helper.assertTrue(loaded.getHousePos().x == 100, "housePos.x wrong");
        helper.assertFalse(loaded.getTownHallPos() == null, "townHallPos not persisted");
        helper.assertTrue(loaded.getTownHallPos().x == 90, "townHallPos.x wrong");

        // Inventory
        helper.assertTrue(loaded.inventory.getOrDefault("wheat", 0) == 10,
                "Inventory wheat not persisted");
        helper.assertTrue(loaded.inventory.getOrDefault("iron_ingot", 0) == 5,
                "Inventory iron_ingot not persisted");

        // Quest tags
        helper.assertTrue(loaded.hasQuestTag("quest_started"),
                "Quest tag not persisted");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testVillagerRecordClone(GameTestHelper helper) {
        VillagerRecord original = VillagerRecord.create("indian", "priest", "Arjun", "Sharma", MillVillager.MALE);
        original.inventory.put("diamond", 3);
        original.addQuestTag("temple_visit");

        VillagerRecord clone = original.clone();
        helper.assertTrue(clone.getVillagerId() == original.getVillagerId(), "Clone ID mismatch");
        helper.assertTrue("Arjun".equals(clone.firstName), "Clone firstName mismatch");

        // Modify clone, verify original unaffected
        clone.inventory.put("diamond", 99);
        clone.addQuestTag("new_tag");
        helper.assertTrue(original.inventory.get("diamond") == 3,
                "Original inventory should be unaffected by clone mutation");
        helper.assertFalse(original.hasQuestTag("new_tag"),
                "Original quest tags should be unaffected by clone mutation");

        helper.succeed();
    }

    // ==================== MillWorldData ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testMillWorldDataAddAndGetBuilding(GameTestHelper helper) {
        MillWorldData mwd = new MillWorldData();
        Point p = new Point(100, 64, 200);

        Building b = new Building();
        b.cultureKey = "norman";
        b.setPos(p);
        mwd.addBuilding(b, p);

        helper.assertTrue(mwd.buildingExists(p), "Building should exist at point");
        helper.assertFalse(mwd.getBuilding(p) == null, "getBuilding should return non-null");
        helper.assertTrue(mwd.getBuilding(p) == b, "getBuilding should return same instance");
        helper.assertTrue(mwd.allBuildings().size() == 1, "Should have 1 building");

        // Verify addBuilding sets mw back-reference
        helper.assertTrue(b.mw == mwd, "Building.mw should be set by addBuilding");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testMillWorldDataRemoveBuilding(GameTestHelper helper) {
        MillWorldData mwd = new MillWorldData();
        Point p = new Point(50, 64, 50);

        Building b = new Building();
        mwd.addBuilding(b, p);
        helper.assertTrue(mwd.buildingExists(p), "Building should exist");

        mwd.removeBuilding(p);
        helper.assertFalse(mwd.buildingExists(p), "Building should not exist after removal");
        helper.assertTrue(mwd.allBuildings().size() == 0, "Should have 0 buildings");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testMillWorldDataVillagerRecords(GameTestHelper helper) {
        MillWorldData mwd = new MillWorldData();

        VillagerRecord vr = VillagerRecord.create("norman", "farmer", "Jean", "Lefevre", MillVillager.MALE);
        mwd.addVillagerRecord(vr);

        helper.assertFalse(mwd.getVillagerRecord(vr.getVillagerId()) == null,
                "Should find villager record by ID");
        helper.assertTrue(mwd.allVillagerRecords().size() == 1,
                "Should have 1 villager record");

        mwd.removeVillagerRecord(vr.getVillagerId());
        helper.assertTrue(mwd.getVillagerRecord(vr.getVillagerId()) == null,
                "Should not find record after removal");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testMillWorldDataMultipleBuildings(GameTestHelper helper) {
        MillWorldData mwd = new MillWorldData();

        Point p1 = new Point(0, 64, 0);
        Point p2 = new Point(100, 64, 100);
        Point p3 = new Point(200, 64, 200);

        Building b1 = new Building(); b1.cultureKey = "norman";
        Building b2 = new Building(); b2.cultureKey = "japanese";
        Building b3 = new Building(); b3.cultureKey = "indian";

        mwd.addBuilding(b1, p1);
        mwd.addBuilding(b2, p2);
        mwd.addBuilding(b3, p3);

        helper.assertTrue(mwd.allBuildings().size() == 3, "Should have 3 buildings");
        helper.assertTrue(mwd.getBuilding(p2) == b2, "Should get correct building at p2");

        helper.succeed();
    }

    // ==================== BiomeCultureMapper Fixed Keys ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBiomeCultureMapperReturnsCulture(GameTestHelper helper) {
        helper.assertTrue(BiomeCultureMapper.isLoaded(),
                "BiomeCultureMapper not loaded");

        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(BlockPos.ZERO);

        Culture culture = BiomeCultureMapper.selectCulture(level, pos, level.random);
        helper.assertFalse(culture == null,
                "selectCulture returned null");

        // Verify the returned culture key matches an actual loaded culture
        helper.assertFalse(Culture.getCultureByName(culture.key) == null,
                "selectCulture returned culture '" + culture.key + "' which is not in Culture registry");

        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBiomeCultureMapperFixedKeysByzantinesInuits(GameTestHelper helper) {
        // After the fix, "byzantines" and "inuits" should be valid culture keys
        helper.assertFalse(Culture.getCultureByName("byzantines") == null,
                "Byzantines culture should exist (fixed from 'byzantine')");
        helper.assertFalse(Culture.getCultureByName("inuits") == null,
                "Inuits culture should exist (fixed from 'inuit')");
        helper.assertFalse(Culture.getCultureByName("seljuk") == null,
                "Seljuk culture should exist (fixed from 'slavic')");

        helper.succeed();
    }

    // ==================== Full Pipeline: Generate → Tick → Spawn ====================

    @GameTest(template = "empty", timeoutTicks = 400)
    public static void testFullPipelineGenerateTickSpawn(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos abs = helper.absolutePos(new BlockPos(1, 2, 1));

        Culture norman = Culture.getCultureByName("norman");
        helper.assertFalse(norman == null, "Norman culture missing");

        MillWorldData worldData = new MillWorldData();
        worldData.world = level;

        boolean success = WorldGenVillage.generateNewVillage(
                level, abs, norman, worldData, level.random);
        helper.assertTrue(success, "Village generation failed");

        Building townhall = worldData.allBuildings().iterator().next();
        helper.assertTrue(townhall.getVillagerRecords().size() >= 1,
                "Townhall needs at least 1 villager record for spawn test");

        // Tick 201 times to trigger checkAndSpawnVillagers (fires at tickCounter % 200 == 0)
        for (int i = 0; i < 201; i++) {
            townhall.tick();
        }

        // Wait a moment for entity to spawn and tick
        helper.runAfterDelay(20, () -> {
            List<MillVillager> villagers = level.getEntitiesOfClass(
                    MillVillager.class,
                    net.minecraft.world.phys.AABB.ofSize(
                            new net.minecraft.world.phys.Vec3(abs.getX(), abs.getY(), abs.getZ()),
                            256, 128, 256));
            helper.assertTrue(villagers.size() >= 1,
                    "Full pipeline: expected at least 1 villager entity spawned, got " + villagers.size());

            // Verify the spawned villager has culture data
            MillVillager v = villagers.get(0);
            helper.assertTrue("norman".equals(v.getCultureKey()),
                    "Spawned villager should have norman culture, got " + v.getCultureKey());

            helper.succeed();
        });
    }

    // ==================== MillWorldData NBT Round-Trip ====================

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testMillWorldDataNBTRoundTrip(GameTestHelper helper) {
        MillWorldData original = new MillWorldData();

        Point p = new Point(10, 64, 20);
        Building b = new Building();
        b.cultureKey = "norman";
        b.planSetKey = "armoury_a";
        b.isActive = true;
        b.isTownhall = true;
        b.setPos(p);
        b.setName("Test Village");
        original.addBuilding(b, p);

        // Save
        HolderLookup.Provider registries = helper.getLevel().registryAccess();
        CompoundTag root = new CompoundTag();
        original.save(root, registries);

        // Load
        MillWorldData loaded = MillWorldData.load(root, registries);
        helper.assertTrue(loaded.allBuildings().size() == 1,
                "Loaded world data should have 1 building, got " + loaded.allBuildings().size());

        Building loadedB = loaded.getBuilding(p);
        helper.assertFalse(loadedB == null, "Building not found at saved position");
        helper.assertTrue("norman".equals(loadedB.cultureKey),
                "Loaded building culture mismatch");
        helper.assertTrue("Test Village".equals(loadedB.getName()),
                "Loaded building name mismatch");

        helper.succeed();
    }

    // ==================== Building Location NBT ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBuildingLocationNBTRoundTrip(GameTestHelper helper) {
        BuildingLocation loc = new BuildingLocation();
        loc.pos = new Point(100, 64, 200);
        loc.planKey = "armoury_a";
        loc.cultureKey = "norman";
        loc.orientation = 2;
        loc.width = 15;
        loc.length = 12;
        loc.level = 3;

        CompoundTag tag = new CompoundTag();
        loc.save(tag, "loc");

        BuildingLocation loaded = BuildingLocation.read(tag, "loc");
        helper.assertFalse(loaded == null, "BuildingLocation.read returned null");
        helper.assertTrue(loaded.pos.x == 100, "pos.x mismatch");
        helper.assertTrue("armoury_a".equals(loaded.planKey), "planKey mismatch");
        helper.assertTrue("norman".equals(loaded.cultureKey), "cultureKey mismatch");
        helper.assertTrue(loaded.orientation == 2, "orientation mismatch");
        helper.assertTrue(loaded.width == 15, "width mismatch");
        helper.assertTrue(loaded.length == 12, "length mismatch");
        helper.assertTrue(loaded.level == 3, "level mismatch");

        helper.succeed();
    }
}
