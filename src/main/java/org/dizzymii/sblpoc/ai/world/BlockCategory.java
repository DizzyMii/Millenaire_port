package org.dizzymii.sblpoc.ai.world;

/**
 * Categories of blocks the NPC remembers. Only "interesting" blocks
 * are stored — dirt, stone, air, etc. are never remembered.
 */
public enum BlockCategory {
    // Ores
    COAL_ORE,
    IRON_ORE,
    GOLD_ORE,
    DIAMOND_ORE,
    EMERALD_ORE,
    LAPIS_ORE,
    REDSTONE_ORE,
    ANCIENT_DEBRIS,

    // Natural resources
    LOG,

    // Crafting stations
    CRAFTING_TABLE,
    FURNACE,
    ANVIL,
    ENCHANTING_TABLE,
    BREWING_STAND,
    SMITHING_TABLE,

    // Storage
    CHEST,

    // Fluids
    WATER,
    LAVA,

    // Misc
    BED,
    CROP
}
