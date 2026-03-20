package org.dizzymii.sblpoc.ai.goap;

import net.minecraft.world.item.Items;
import org.dizzymii.sblpoc.ai.world.BlockCategory;
import org.dizzymii.sblpoc.ai.world.InventoryModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registry of all GOAP actions available to the NPC.
 * Each action defines preconditions and effects for the planner,
 * and maps to a real SBL behaviour for execution.
 *
 * Actions are registered once at entity construction and reused across
 * all planning cycles.
 */
public class GoapActionRegistry {

    private static List<GoapAction> ACTIONS;

    public static List<GoapAction> getActions() {
        if (ACTIONS == null) {
            ACTIONS = Collections.unmodifiableList(buildActions());
        }
        return ACTIONS;
    }

    private static List<GoapAction> buildActions() {
        List<GoapAction> actions = new ArrayList<>();

        // ===== WOOD GATHERING =====

        actions.add(GoapAction.builder("chop_tree")
                .type(GoapAction.ActionType.GATHER)
                .cost(3f)
                .estimatedTicks(60)
                .produceItem(Items.OAK_LOG, 4)
                .build());

        // ===== BASIC CRAFTING (no station needed — 2x2 grid) =====

        actions.add(GoapAction.builder("craft_planks")
                .type(GoapAction.ActionType.CRAFT)
                .cost(1f)
                .estimatedTicks(10)
                .consumeItem(Items.OAK_LOG, 1)
                .produceItem(Items.OAK_PLANKS, 4)
                .build());

        actions.add(GoapAction.builder("craft_sticks")
                .type(GoapAction.ActionType.CRAFT)
                .cost(1f)
                .estimatedTicks(10)
                .consumeItem(Items.OAK_PLANKS, 2)
                .produceItem(Items.STICK, 4)
                .build());

        actions.add(GoapAction.builder("craft_crafting_table")
                .type(GoapAction.ActionType.CRAFT)
                .cost(1f)
                .estimatedTicks(10)
                .consumeItem(Items.OAK_PLANKS, 4)
                .produceItem(Items.CRAFTING_TABLE, 1)
                .build());

        // ===== PLACE STATIONS =====

        actions.add(GoapAction.builder("place_crafting_table")
                .type(GoapAction.ActionType.BUILD)
                .cost(2f)
                .estimatedTicks(20)
                .consumeItem(Items.CRAFTING_TABLE, 1)
                .setFlag("has_crafting_table")
                .customEffect(s -> s.nearbyStations.add(BlockCategory.CRAFTING_TABLE))
                .build());

        actions.add(GoapAction.builder("place_furnace")
                .type(GoapAction.ActionType.BUILD)
                .cost(2f)
                .estimatedTicks(20)
                .consumeItem(Items.FURNACE, 1)
                .setFlag("has_furnace")
                .customEffect(s -> s.nearbyStations.add(BlockCategory.FURNACE))
                .build());

        // ===== TOOL CRAFTING (requires crafting table) =====

        // Wooden pickaxe
        actions.add(GoapAction.builder("craft_wooden_pickaxe")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.OAK_PLANKS, 3)
                .consumeItem(Items.STICK, 2)
                .produceItem(Items.WOODEN_PICKAXE, 1)
                .upgradeToolTier(InventoryModel.ToolTier.WOOD)
                .build());

        // Wooden axe
        actions.add(GoapAction.builder("craft_wooden_axe")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.OAK_PLANKS, 3)
                .consumeItem(Items.STICK, 2)
                .produceItem(Items.WOODEN_AXE, 1)
                .build());

        // Wooden sword
        actions.add(GoapAction.builder("craft_wooden_sword")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.OAK_PLANKS, 2)
                .consumeItem(Items.STICK, 1)
                .produceItem(Items.WOODEN_SWORD, 1)
                .build());

        // ===== STONE GATHERING & TOOLS =====

        actions.add(GoapAction.builder("mine_stone")
                .type(GoapAction.ActionType.GATHER)
                .cost(4f)
                .estimatedTicks(80)
                .requireToolTier(InventoryModel.ToolTier.WOOD)
                .produceItem(Items.COBBLESTONE, 8)
                .build());

        actions.add(GoapAction.builder("craft_stone_pickaxe")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.COBBLESTONE, 3)
                .consumeItem(Items.STICK, 2)
                .produceItem(Items.STONE_PICKAXE, 1)
                .upgradeToolTier(InventoryModel.ToolTier.STONE)
                .build());

        actions.add(GoapAction.builder("craft_stone_sword")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.COBBLESTONE, 2)
                .consumeItem(Items.STICK, 1)
                .produceItem(Items.STONE_SWORD, 1)
                .build());

        actions.add(GoapAction.builder("craft_stone_axe")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.COBBLESTONE, 3)
                .consumeItem(Items.STICK, 2)
                .produceItem(Items.STONE_AXE, 1)
                .build());

        actions.add(GoapAction.builder("craft_furnace")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.COBBLESTONE, 8)
                .produceItem(Items.FURNACE, 1)
                .build());

        // ===== IRON =====

        actions.add(GoapAction.builder("mine_iron_ore")
                .type(GoapAction.ActionType.GATHER)
                .cost(6f)
                .estimatedTicks(120)
                .requireToolTier(InventoryModel.ToolTier.STONE)
                .requireFlag("knows_iron_location")
                .produceItem(Items.RAW_IRON, 3)
                .build());

        actions.add(GoapAction.builder("mine_coal")
                .type(GoapAction.ActionType.GATHER)
                .cost(4f)
                .estimatedTicks(60)
                .requireToolTier(InventoryModel.ToolTier.WOOD)
                .produceItem(Items.COAL, 4)
                .build());

        actions.add(GoapAction.builder("smelt_iron")
                .type(GoapAction.ActionType.CRAFT)
                .cost(8f)
                .estimatedTicks(200)
                .requireNearbyStation(BlockCategory.FURNACE)
                .consumeItem(Items.RAW_IRON, 3)
                .consumeItem(Items.COAL, 1)
                .produceItem(Items.IRON_INGOT, 3)
                .build());

        actions.add(GoapAction.builder("craft_iron_pickaxe")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.IRON_INGOT, 3)
                .consumeItem(Items.STICK, 2)
                .produceItem(Items.IRON_PICKAXE, 1)
                .upgradeToolTier(InventoryModel.ToolTier.IRON)
                .build());

        actions.add(GoapAction.builder("craft_iron_sword")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.IRON_INGOT, 2)
                .consumeItem(Items.STICK, 1)
                .produceItem(Items.IRON_SWORD, 1)
                .build());

        actions.add(GoapAction.builder("craft_shield")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.IRON_INGOT, 1)
                .consumeItem(Items.OAK_PLANKS, 6)
                .produceItem(Items.SHIELD, 1)
                .build());

        // ===== DIAMOND =====

        actions.add(GoapAction.builder("mine_diamond_ore")
                .type(GoapAction.ActionType.GATHER)
                .cost(10f)
                .estimatedTicks(200)
                .requireToolTier(InventoryModel.ToolTier.IRON)
                .requireFlag("knows_diamond_location")
                .produceItem(Items.DIAMOND, 2)
                .build());

        actions.add(GoapAction.builder("craft_diamond_pickaxe")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.DIAMOND, 3)
                .consumeItem(Items.STICK, 2)
                .produceItem(Items.DIAMOND_PICKAXE, 1)
                .upgradeToolTier(InventoryModel.ToolTier.DIAMOND)
                .build());

        actions.add(GoapAction.builder("craft_diamond_sword")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.DIAMOND, 2)
                .consumeItem(Items.STICK, 1)
                .produceItem(Items.DIAMOND_SWORD, 1)
                .build());

        // ===== FOOD =====

        actions.add(GoapAction.builder("hunt_animal")
                .type(GoapAction.ActionType.COMBAT)
                .cost(6f)
                .estimatedTicks(100)
                .produceItem(Items.BEEF, 2)
                .customEffect(s -> s.foodSupply += 6)
                .build());

        actions.add(GoapAction.builder("cook_food")
                .type(GoapAction.ActionType.CRAFT)
                .cost(4f)
                .estimatedTicks(100)
                .requireNearbyStation(BlockCategory.FURNACE)
                .consumeItem(Items.BEEF, 2)
                .consumeItem(Items.COAL, 1)
                .produceItem(Items.COOKED_BEEF, 2)
                .customEffect(s -> s.foodSupply += 10)
                .build());

        // ===== SHELTER =====

        actions.add(GoapAction.builder("build_shelter")
                .type(GoapAction.ActionType.BUILD)
                .cost(30f)
                .estimatedTicks(600)
                .requireItem(Items.OAK_PLANKS, 20)
                .setFlag("has_home")
                .build());

        // ===== EXPLORATION =====

        actions.add(GoapAction.builder("explore_area")
                .type(GoapAction.ActionType.NAVIGATE)
                .cost(5f)
                .estimatedTicks(200)
                .setFlag("explored_enough")
                .build());

        // ===== ARMOR =====

        actions.add(GoapAction.builder("craft_iron_helmet")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.IRON_INGOT, 5)
                .produceItem(Items.IRON_HELMET, 1)
                .build());

        actions.add(GoapAction.builder("craft_iron_chestplate")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.IRON_INGOT, 8)
                .produceItem(Items.IRON_CHESTPLATE, 1)
                .build());

        actions.add(GoapAction.builder("craft_iron_leggings")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.IRON_INGOT, 7)
                .produceItem(Items.IRON_LEGGINGS, 1)
                .build());

        actions.add(GoapAction.builder("craft_iron_boots")
                .type(GoapAction.ActionType.CRAFT)
                .cost(2f)
                .estimatedTicks(20)
                .requireNearbyStation(BlockCategory.CRAFTING_TABLE)
                .consumeItem(Items.IRON_INGOT, 4)
                .produceItem(Items.IRON_BOOTS, 1)
                .build());

        actions.add(GoapAction.builder("equip_iron_armor")
                .type(GoapAction.ActionType.SURVIVE)
                .cost(1f)
                .estimatedTicks(5)
                .requireItem(Items.IRON_HELMET, 1)
                .requireItem(Items.IRON_CHESTPLATE, 1)
                .requireItem(Items.IRON_LEGGINGS, 1)
                .requireItem(Items.IRON_BOOTS, 1)
                .customEffect(s -> s.armorTier = WorldState.ArmorTier.IRON)
                .build());

        return actions;
    }
}
