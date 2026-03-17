package org.dizzymii.millenaire2.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps 1.12.2 block/item IDs with metadata to 1.21.1 flattened IDs.
 * This is critical for reading the original data files (blocklist.txt, itemlist.txt, building plans).
 *
 * Format: "minecraft:old_id;meta" → "minecraft:new_id"
 * If meta is 0 or omitted and the ID hasn't changed, no entry is needed.
 */
public class LegacyBlockMapping {

    private static final Map<String, String> BLOCK_MAP = new HashMap<>();
    private static final Map<String, String> ITEM_MAP = new HashMap<>();

    static {
        // ===== Stone variants =====
        BLOCK_MAP.put("minecraft:stone;0", "minecraft:stone");
        BLOCK_MAP.put("minecraft:stone;1", "minecraft:granite");
        BLOCK_MAP.put("minecraft:stone;2", "minecraft:polished_granite");
        BLOCK_MAP.put("minecraft:stone;3", "minecraft:diorite");
        BLOCK_MAP.put("minecraft:stone;4", "minecraft:polished_diorite");
        BLOCK_MAP.put("minecraft:stone;5", "minecraft:andesite");
        BLOCK_MAP.put("minecraft:stone;6", "minecraft:polished_andesite");

        // ===== Dirt variants =====
        BLOCK_MAP.put("minecraft:dirt;0", "minecraft:dirt");
        BLOCK_MAP.put("minecraft:dirt;1", "minecraft:coarse_dirt");
        BLOCK_MAP.put("minecraft:dirt;2", "minecraft:podzol");

        // ===== Planks =====
        BLOCK_MAP.put("minecraft:planks;0", "minecraft:oak_planks");
        BLOCK_MAP.put("minecraft:planks;1", "minecraft:spruce_planks");
        BLOCK_MAP.put("minecraft:planks;2", "minecraft:birch_planks");
        BLOCK_MAP.put("minecraft:planks;3", "minecraft:jungle_planks");
        BLOCK_MAP.put("minecraft:planks;4", "minecraft:acacia_planks");
        BLOCK_MAP.put("minecraft:planks;5", "minecraft:dark_oak_planks");

        // ===== Logs =====
        BLOCK_MAP.put("minecraft:log;0", "minecraft:oak_log");
        BLOCK_MAP.put("minecraft:log;1", "minecraft:spruce_log");
        BLOCK_MAP.put("minecraft:log;2", "minecraft:birch_log");
        BLOCK_MAP.put("minecraft:log;3", "minecraft:jungle_log");
        BLOCK_MAP.put("minecraft:log2;0", "minecraft:acacia_log");
        BLOCK_MAP.put("minecraft:log2;1", "minecraft:dark_oak_log");

        // ===== Sand =====
        BLOCK_MAP.put("minecraft:sand;0", "minecraft:sand");
        BLOCK_MAP.put("minecraft:sand;1", "minecraft:red_sand");

        // ===== Sandstone =====
        BLOCK_MAP.put("minecraft:sandstone;0", "minecraft:sandstone");
        BLOCK_MAP.put("minecraft:sandstone;1", "minecraft:chiseled_sandstone");
        BLOCK_MAP.put("minecraft:sandstone;2", "minecraft:cut_sandstone");
        BLOCK_MAP.put("minecraft:red_sandstone;0", "minecraft:red_sandstone");
        BLOCK_MAP.put("minecraft:red_sandstone;1", "minecraft:chiseled_red_sandstone");
        BLOCK_MAP.put("minecraft:red_sandstone;2", "minecraft:cut_red_sandstone");

        // ===== Wool =====
        BLOCK_MAP.put("minecraft:wool;0", "minecraft:white_wool");
        BLOCK_MAP.put("minecraft:wool;1", "minecraft:orange_wool");
        BLOCK_MAP.put("minecraft:wool;2", "minecraft:magenta_wool");
        BLOCK_MAP.put("minecraft:wool;3", "minecraft:light_blue_wool");
        BLOCK_MAP.put("minecraft:wool;4", "minecraft:yellow_wool");
        BLOCK_MAP.put("minecraft:wool;5", "minecraft:lime_wool");
        BLOCK_MAP.put("minecraft:wool;6", "minecraft:pink_wool");
        BLOCK_MAP.put("minecraft:wool;7", "minecraft:gray_wool");
        BLOCK_MAP.put("minecraft:wool;8", "minecraft:light_gray_wool");
        BLOCK_MAP.put("minecraft:wool;9", "minecraft:cyan_wool");
        BLOCK_MAP.put("minecraft:wool;10", "minecraft:purple_wool");
        BLOCK_MAP.put("minecraft:wool;11", "minecraft:blue_wool");
        BLOCK_MAP.put("minecraft:wool;12", "minecraft:brown_wool");
        BLOCK_MAP.put("minecraft:wool;13", "minecraft:green_wool");
        BLOCK_MAP.put("minecraft:wool;14", "minecraft:red_wool");
        BLOCK_MAP.put("minecraft:wool;15", "minecraft:black_wool");

        // ===== Stained Clay / Terracotta =====
        BLOCK_MAP.put("minecraft:stained_hardened_clay;0", "minecraft:white_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;1", "minecraft:orange_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;2", "minecraft:magenta_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;3", "minecraft:light_blue_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;4", "minecraft:yellow_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;5", "minecraft:lime_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;6", "minecraft:pink_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;7", "minecraft:gray_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;8", "minecraft:light_gray_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;9", "minecraft:cyan_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;10", "minecraft:purple_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;11", "minecraft:blue_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;12", "minecraft:brown_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;13", "minecraft:green_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;14", "minecraft:red_terracotta");
        BLOCK_MAP.put("minecraft:stained_hardened_clay;15", "minecraft:black_terracotta");
        BLOCK_MAP.put("minecraft:hardened_clay", "minecraft:terracotta");

        // ===== Slabs =====
        BLOCK_MAP.put("minecraft:stone_slab;0", "minecraft:smooth_stone_slab");
        BLOCK_MAP.put("minecraft:stone_slab;1", "minecraft:sandstone_slab");
        BLOCK_MAP.put("minecraft:stone_slab;3", "minecraft:cobblestone_slab");
        BLOCK_MAP.put("minecraft:stone_slab;4", "minecraft:brick_slab");
        BLOCK_MAP.put("minecraft:stone_slab;5", "minecraft:stone_brick_slab");
        BLOCK_MAP.put("minecraft:stone_slab;6", "minecraft:nether_brick_slab");
        BLOCK_MAP.put("minecraft:stone_slab;7", "minecraft:quartz_slab");
        BLOCK_MAP.put("minecraft:wooden_slab;0", "minecraft:oak_slab");
        BLOCK_MAP.put("minecraft:wooden_slab;1", "minecraft:spruce_slab");
        BLOCK_MAP.put("minecraft:wooden_slab;2", "minecraft:birch_slab");
        BLOCK_MAP.put("minecraft:wooden_slab;3", "minecraft:jungle_slab");
        BLOCK_MAP.put("minecraft:wooden_slab;4", "minecraft:acacia_slab");
        BLOCK_MAP.put("minecraft:wooden_slab;5", "minecraft:dark_oak_slab");

        // ===== Stairs =====
        BLOCK_MAP.put("minecraft:oak_stairs", "minecraft:oak_stairs");
        BLOCK_MAP.put("minecraft:spruce_stairs", "minecraft:spruce_stairs");
        BLOCK_MAP.put("minecraft:birch_stairs", "minecraft:birch_stairs");
        BLOCK_MAP.put("minecraft:jungle_stairs", "minecraft:jungle_stairs");
        BLOCK_MAP.put("minecraft:acacia_stairs", "minecraft:acacia_stairs");
        BLOCK_MAP.put("minecraft:dark_oak_stairs", "minecraft:dark_oak_stairs");

        // ===== Stained Glass =====
        BLOCK_MAP.put("minecraft:stained_glass;0", "minecraft:white_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;1", "minecraft:orange_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;2", "minecraft:magenta_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;3", "minecraft:light_blue_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;4", "minecraft:yellow_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;5", "minecraft:lime_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;6", "minecraft:pink_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;7", "minecraft:gray_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;8", "minecraft:light_gray_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;9", "minecraft:cyan_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;10", "minecraft:purple_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;11", "minecraft:blue_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;12", "minecraft:brown_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;13", "minecraft:green_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;14", "minecraft:red_stained_glass");
        BLOCK_MAP.put("minecraft:stained_glass;15", "minecraft:black_stained_glass");

        // ===== Stained Glass Panes =====
        BLOCK_MAP.put("minecraft:stained_glass_pane;0", "minecraft:white_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;1", "minecraft:orange_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;2", "minecraft:magenta_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;3", "minecraft:light_blue_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;4", "minecraft:yellow_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;5", "minecraft:lime_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;6", "minecraft:pink_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;7", "minecraft:gray_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;8", "minecraft:light_gray_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;9", "minecraft:cyan_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;10", "minecraft:purple_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;11", "minecraft:blue_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;12", "minecraft:brown_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;13", "minecraft:green_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;14", "minecraft:red_stained_glass_pane");
        BLOCK_MAP.put("minecraft:stained_glass_pane;15", "minecraft:black_stained_glass_pane");

        // ===== Carpet =====
        BLOCK_MAP.put("minecraft:carpet;0", "minecraft:white_carpet");
        BLOCK_MAP.put("minecraft:carpet;1", "minecraft:orange_carpet");
        BLOCK_MAP.put("minecraft:carpet;2", "minecraft:magenta_carpet");
        BLOCK_MAP.put("minecraft:carpet;3", "minecraft:light_blue_carpet");
        BLOCK_MAP.put("minecraft:carpet;4", "minecraft:yellow_carpet");
        BLOCK_MAP.put("minecraft:carpet;5", "minecraft:lime_carpet");
        BLOCK_MAP.put("minecraft:carpet;6", "minecraft:pink_carpet");
        BLOCK_MAP.put("minecraft:carpet;7", "minecraft:gray_carpet");
        BLOCK_MAP.put("minecraft:carpet;8", "minecraft:light_gray_carpet");
        BLOCK_MAP.put("minecraft:carpet;9", "minecraft:cyan_carpet");
        BLOCK_MAP.put("minecraft:carpet;10", "minecraft:purple_carpet");
        BLOCK_MAP.put("minecraft:carpet;11", "minecraft:blue_carpet");
        BLOCK_MAP.put("minecraft:carpet;12", "minecraft:brown_carpet");
        BLOCK_MAP.put("minecraft:carpet;13", "minecraft:green_carpet");
        BLOCK_MAP.put("minecraft:carpet;14", "minecraft:red_carpet");
        BLOCK_MAP.put("minecraft:carpet;15", "minecraft:black_carpet");

        // ===== Leaves =====
        BLOCK_MAP.put("minecraft:leaves;0", "minecraft:oak_leaves");
        BLOCK_MAP.put("minecraft:leaves;1", "minecraft:spruce_leaves");
        BLOCK_MAP.put("minecraft:leaves;2", "minecraft:birch_leaves");
        BLOCK_MAP.put("minecraft:leaves;3", "minecraft:jungle_leaves");
        BLOCK_MAP.put("minecraft:leaves2;0", "minecraft:acacia_leaves");
        BLOCK_MAP.put("minecraft:leaves2;1", "minecraft:dark_oak_leaves");

        // ===== Saplings =====
        BLOCK_MAP.put("minecraft:sapling;0", "minecraft:oak_sapling");
        BLOCK_MAP.put("minecraft:sapling;1", "minecraft:spruce_sapling");
        BLOCK_MAP.put("minecraft:sapling;2", "minecraft:birch_sapling");
        BLOCK_MAP.put("minecraft:sapling;3", "minecraft:jungle_sapling");
        BLOCK_MAP.put("minecraft:sapling;4", "minecraft:acacia_sapling");
        BLOCK_MAP.put("minecraft:sapling;5", "minecraft:dark_oak_sapling");

        // ===== Fences =====
        BLOCK_MAP.put("minecraft:fence", "minecraft:oak_fence");
        BLOCK_MAP.put("minecraft:spruce_fence", "minecraft:spruce_fence");
        BLOCK_MAP.put("minecraft:birch_fence", "minecraft:birch_fence");
        BLOCK_MAP.put("minecraft:jungle_fence", "minecraft:jungle_fence");
        BLOCK_MAP.put("minecraft:acacia_fence", "minecraft:acacia_fence");
        BLOCK_MAP.put("minecraft:dark_oak_fence", "minecraft:dark_oak_fence");
        BLOCK_MAP.put("minecraft:fence_gate", "minecraft:oak_fence_gate");

        // ===== Doors =====
        BLOCK_MAP.put("minecraft:wooden_door", "minecraft:oak_door");

        // ===== Stone Bricks =====
        BLOCK_MAP.put("minecraft:stonebrick;0", "minecraft:stone_bricks");
        BLOCK_MAP.put("minecraft:stonebrick;1", "minecraft:mossy_stone_bricks");
        BLOCK_MAP.put("minecraft:stonebrick;2", "minecraft:cracked_stone_bricks");
        BLOCK_MAP.put("minecraft:stonebrick;3", "minecraft:chiseled_stone_bricks");

        // ===== Quartz =====
        BLOCK_MAP.put("minecraft:quartz_block;0", "minecraft:quartz_block");
        BLOCK_MAP.put("minecraft:quartz_block;1", "minecraft:chiseled_quartz_block");
        BLOCK_MAP.put("minecraft:quartz_block;2", "minecraft:quartz_pillar");

        // ===== Prismarine =====
        BLOCK_MAP.put("minecraft:prismarine;0", "minecraft:prismarine");
        BLOCK_MAP.put("minecraft:prismarine;1", "minecraft:prismarine_bricks");
        BLOCK_MAP.put("minecraft:prismarine;2", "minecraft:dark_prismarine");

        // ===== Miscellaneous renames =====
        BLOCK_MAP.put("minecraft:cobblestone_wall;0", "minecraft:cobblestone_wall");
        BLOCK_MAP.put("minecraft:cobblestone_wall;1", "minecraft:mossy_cobblestone_wall");
        BLOCK_MAP.put("minecraft:grass", "minecraft:grass_block");
        BLOCK_MAP.put("minecraft:snow_layer", "minecraft:snow");
        BLOCK_MAP.put("minecraft:snow", "minecraft:snow_block");
        BLOCK_MAP.put("minecraft:waterlily", "minecraft:lily_pad");
        BLOCK_MAP.put("minecraft:torch", "minecraft:torch");
        BLOCK_MAP.put("minecraft:web", "minecraft:cobweb");
        BLOCK_MAP.put("minecraft:lit_pumpkin", "minecraft:jack_o_lantern");
        BLOCK_MAP.put("minecraft:golden_rail", "minecraft:powered_rail");
        BLOCK_MAP.put("minecraft:noteblock", "minecraft:note_block");
        BLOCK_MAP.put("minecraft:crafting_table", "minecraft:crafting_table");
        BLOCK_MAP.put("minecraft:brick_block", "minecraft:bricks");
        BLOCK_MAP.put("minecraft:nether_brick", "minecraft:nether_bricks");
        BLOCK_MAP.put("minecraft:end_bricks", "minecraft:end_stone_bricks");
        BLOCK_MAP.put("minecraft:magma", "minecraft:magma_block");
        BLOCK_MAP.put("minecraft:red_nether_brick", "minecraft:red_nether_bricks");

        // ===== Flowers =====
        BLOCK_MAP.put("minecraft:red_flower;0", "minecraft:poppy");
        BLOCK_MAP.put("minecraft:red_flower;1", "minecraft:blue_orchid");
        BLOCK_MAP.put("minecraft:red_flower;2", "minecraft:allium");
        BLOCK_MAP.put("minecraft:red_flower;3", "minecraft:azure_bluet");
        BLOCK_MAP.put("minecraft:red_flower;4", "minecraft:red_tulip");
        BLOCK_MAP.put("minecraft:red_flower;5", "minecraft:orange_tulip");
        BLOCK_MAP.put("minecraft:red_flower;6", "minecraft:white_tulip");
        BLOCK_MAP.put("minecraft:red_flower;7", "minecraft:pink_tulip");
        BLOCK_MAP.put("minecraft:red_flower;8", "minecraft:oxeye_daisy");
        BLOCK_MAP.put("minecraft:yellow_flower", "minecraft:dandelion");

        // ===== Tallgrass =====
        BLOCK_MAP.put("minecraft:tallgrass;0", "minecraft:dead_bush");
        BLOCK_MAP.put("minecraft:tallgrass;1", "minecraft:short_grass");
        BLOCK_MAP.put("minecraft:tallgrass;2", "minecraft:fern");

        // ===== Double plants =====
        BLOCK_MAP.put("minecraft:double_plant;0", "minecraft:sunflower");
        BLOCK_MAP.put("minecraft:double_plant;1", "minecraft:lilac");
        BLOCK_MAP.put("minecraft:double_plant;2", "minecraft:tall_grass");
        BLOCK_MAP.put("minecraft:double_plant;3", "minecraft:large_fern");
        BLOCK_MAP.put("minecraft:double_plant;4", "minecraft:rose_bush");
        BLOCK_MAP.put("minecraft:double_plant;5", "minecraft:peony");

        // ===== Item mappings =====
        ITEM_MAP.put("minecraft:dye;0", "minecraft:ink_sac");
        ITEM_MAP.put("minecraft:dye;1", "minecraft:red_dye");
        ITEM_MAP.put("minecraft:dye;2", "minecraft:green_dye");
        ITEM_MAP.put("minecraft:dye;3", "minecraft:cocoa_beans");
        ITEM_MAP.put("minecraft:dye;4", "minecraft:lapis_lazuli");
        ITEM_MAP.put("minecraft:dye;5", "minecraft:purple_dye");
        ITEM_MAP.put("minecraft:dye;6", "minecraft:cyan_dye");
        ITEM_MAP.put("minecraft:dye;7", "minecraft:light_gray_dye");
        ITEM_MAP.put("minecraft:dye;8", "minecraft:gray_dye");
        ITEM_MAP.put("minecraft:dye;9", "minecraft:pink_dye");
        ITEM_MAP.put("minecraft:dye;10", "minecraft:lime_dye");
        ITEM_MAP.put("minecraft:dye;11", "minecraft:yellow_dye");
        ITEM_MAP.put("minecraft:dye;12", "minecraft:light_blue_dye");
        ITEM_MAP.put("minecraft:dye;13", "minecraft:magenta_dye");
        ITEM_MAP.put("minecraft:dye;14", "minecraft:orange_dye");
        ITEM_MAP.put("minecraft:dye;15", "minecraft:bone_meal");
        ITEM_MAP.put("minecraft:coal;0", "minecraft:coal");
        ITEM_MAP.put("minecraft:coal;1", "minecraft:charcoal");
        ITEM_MAP.put("minecraft:cooked_fish;0", "minecraft:cooked_cod");
        ITEM_MAP.put("minecraft:cooked_fish;1", "minecraft:cooked_salmon");
        ITEM_MAP.put("minecraft:fish;0", "minecraft:cod");
        ITEM_MAP.put("minecraft:fish;1", "minecraft:salmon");
        ITEM_MAP.put("minecraft:fish;2", "minecraft:tropical_fish");
        ITEM_MAP.put("minecraft:fish;3", "minecraft:pufferfish");
        ITEM_MAP.put("minecraft:boat", "minecraft:oak_boat");
        ITEM_MAP.put("minecraft:melon", "minecraft:melon_slice");
        ITEM_MAP.put("minecraft:speckled_melon", "minecraft:glistering_melon_slice");
        ITEM_MAP.put("minecraft:reeds", "minecraft:sugar_cane");
        ITEM_MAP.put("minecraft:bed;0", "minecraft:white_bed");
        ITEM_MAP.put("minecraft:sign", "minecraft:oak_sign");
    }

    /**
     * Maps a legacy block ID (potentially with metadata) to its 1.21.1 equivalent.
     * @param legacyId e.g. "minecraft:stone;1" or "minecraft:stone"
     * @return the 1.21.1 block ID, or the input unchanged if no mapping exists
     */
    public static String mapBlock(String legacyId) {
        String mapped = BLOCK_MAP.get(legacyId);
        if (mapped != null) return mapped;

        // Try without metadata (meta 0)
        if (!legacyId.contains(";")) {
            mapped = BLOCK_MAP.get(legacyId + ";0");
            if (mapped != null) return mapped;
        }

        // Strip metadata if no mapping found — the ID itself may still be valid
        if (legacyId.contains(";")) {
            String baseId = legacyId.substring(0, legacyId.indexOf(';'));
            mapped = BLOCK_MAP.get(baseId + ";0");
            if (mapped != null) return mapped;
            return baseId;
        }

        return legacyId;
    }

    /**
     * Maps a legacy item ID (potentially with metadata) to its 1.21.1 equivalent.
     */
    public static String mapItem(String legacyId) {
        String mapped = ITEM_MAP.get(legacyId);
        if (mapped != null) return mapped;

        // Fall back to block map (many items share block IDs)
        mapped = BLOCK_MAP.get(legacyId);
        if (mapped != null) return mapped;

        if (!legacyId.contains(";")) {
            mapped = ITEM_MAP.get(legacyId + ";0");
            if (mapped != null) return mapped;
        }

        if (legacyId.contains(";")) {
            String baseId = legacyId.substring(0, legacyId.indexOf(';'));
            mapped = ITEM_MAP.get(baseId + ";0");
            if (mapped != null) return mapped;
            return baseId;
        }

        return legacyId;
    }
}
