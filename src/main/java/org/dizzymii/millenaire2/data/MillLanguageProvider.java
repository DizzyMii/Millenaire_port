package org.dizzymii.millenaire2.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.init.ModBlocks;
import org.dizzymii.millenaire2.init.ModItems;

public class MillLanguageProvider extends LanguageProvider {

    public MillLanguageProvider(PackOutput output) {
        super(output, Millenaire2.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // Creative tab
        add("itemGroup.millenaire2", "Millénaire");

        // ===== Blocks =====
        addBlock(ModBlocks.STONE_DECORATION, "Stone Decoration");
        addBlock(ModBlocks.COOKED_BRICK, "Cooked Brick");

        // Painted bricks
        addBlock(ModBlocks.PAINTED_BRICK_WHITE, "White Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_ORANGE, "Orange Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_MAGENTA, "Magenta Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_LIGHT_BLUE, "Light Blue Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_YELLOW, "Yellow Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_LIME, "Lime Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_PINK, "Pink Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_GRAY, "Gray Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_LIGHT_GRAY, "Light Gray Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_CYAN, "Cyan Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_PURPLE, "Purple Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_BLUE, "Blue Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_BROWN, "Brown Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_GREEN, "Green Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_RED, "Red Painted Brick");
        addBlock(ModBlocks.PAINTED_BRICK_BLACK, "Black Painted Brick");

        // Decorated painted bricks
        addBlock(ModBlocks.PAINTED_BRICK_DECO_WHITE, "White Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_ORANGE, "Orange Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_MAGENTA, "Magenta Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_LIGHT_BLUE, "Light Blue Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_YELLOW, "Yellow Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_LIME, "Lime Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_PINK, "Pink Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_GRAY, "Gray Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_LIGHT_GRAY, "Light Gray Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_CYAN, "Cyan Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_PURPLE, "Purple Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_BLUE, "Blue Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_BROWN, "Brown Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_GREEN, "Green Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_RED, "Red Decorated Brick");
        addBlock(ModBlocks.PAINTED_BRICK_DECO_BLACK, "Black Decorated Brick");

        // Wood
        addBlock(ModBlocks.TIMBER_FRAME_PLAIN, "Timber Frame");
        addBlock(ModBlocks.TIMBER_FRAME_CROSS, "Timber Frame (Cross)");
        addBlock(ModBlocks.THATCH, "Thatch");

        // Earth
        addBlock(ModBlocks.MUD_BRICK, "Mud Brick");
        addBlock(ModBlocks.MUD_BRICK_EXTENDED, "Extended Mud Brick");

        // Sandstone
        addBlock(ModBlocks.SANDSTONE_CARVED, "Carved Sandstone");
        addBlock(ModBlocks.SANDSTONE_RED_CARVED, "Carved Red Sandstone");
        addBlock(ModBlocks.SANDSTONE_OCHRE_CARVED, "Carved Ochre Sandstone");
        addBlock(ModBlocks.SANDSTONE_DECORATED, "Decorated Sandstone");
        addBlock(ModBlocks.BYZANTINE_STONE_ORNAMENT, "Byzantine Stone Ornament");
        addBlock(ModBlocks.BYZANTINE_SANDSTONE_ORNAMENT, "Byzantine Sandstone Ornament");

        // Tiles
        addBlock(ModBlocks.BYZANTINE_TILES, "Byzantine Tiles");
        addBlock(ModBlocks.BYZANTINE_STONE_TILES, "Byzantine Stone Tiles");
        addBlock(ModBlocks.BYZANTINE_SANDSTONE_TILES, "Byzantine Sandstone Tiles");
        addBlock(ModBlocks.GRAY_TILES, "Gray Tiles");
        addBlock(ModBlocks.GREEN_TILES, "Green Tiles");
        addBlock(ModBlocks.RED_TILES, "Red Tiles");

        // Stairs
        addBlock(ModBlocks.STAIRS_TIMBERFRAME, "Timber Frame Stairs");
        addBlock(ModBlocks.STAIRS_MUDBRICK, "Mud Brick Stairs");
        addBlock(ModBlocks.STAIRS_COOKEDBRICK, "Cooked Brick Stairs");
        addBlock(ModBlocks.STAIRS_THATCH, "Thatch Stairs");
        addBlock(ModBlocks.STAIRS_SANDSTONE_CARVED, "Carved Sandstone Stairs");
        addBlock(ModBlocks.STAIRS_SANDSTONE_RED_CARVED, "Carved Red Sandstone Stairs");
        addBlock(ModBlocks.STAIRS_SANDSTONE_OCHRE_CARVED, "Carved Ochre Sandstone Stairs");
        addBlock(ModBlocks.STAIRS_BYZANTINE_TILES, "Byzantine Tile Stairs");
        addBlock(ModBlocks.STAIRS_GRAY_TILES, "Gray Tile Stairs");
        addBlock(ModBlocks.STAIRS_GREEN_TILES, "Green Tile Stairs");
        addBlock(ModBlocks.STAIRS_RED_TILES, "Red Tile Stairs");

        // Slabs
        addBlock(ModBlocks.SLAB_WOOD_DECO, "Decorative Wood Slab");
        addBlock(ModBlocks.SLAB_STONE_DECO, "Decorative Stone Slab");
        addBlock(ModBlocks.SLAB_SANDSTONE_CARVED, "Carved Sandstone Slab");
        addBlock(ModBlocks.SLAB_SANDSTONE_RED_CARVED, "Carved Red Sandstone Slab");
        addBlock(ModBlocks.SLAB_SANDSTONE_OCHRE_CARVED, "Carved Ochre Sandstone Slab");
        addBlock(ModBlocks.SLAB_BYZANTINE_TILES, "Byzantine Tile Slab");
        addBlock(ModBlocks.SLAB_GRAY_TILES, "Gray Tile Slab");
        addBlock(ModBlocks.SLAB_GREEN_TILES, "Green Tile Slab");
        addBlock(ModBlocks.SLAB_RED_TILES, "Red Tile Slab");
        addBlock(ModBlocks.SLAB_PATH_DIRT, "Dirt Path Slab");
        addBlock(ModBlocks.SLAB_PATH_GRAVEL, "Gravel Path Slab");
        addBlock(ModBlocks.SLAB_PATH_SLABS, "Stone Path Slab");
        addBlock(ModBlocks.SLAB_PATH_SANDSTONE, "Sandstone Path Slab");
        addBlock(ModBlocks.SLAB_PATH_GRAVEL_SLABS, "Gravel Stone Path Slab");
        addBlock(ModBlocks.SLAB_PATH_OCHRE_TILES, "Ochre Tile Path Slab");
        addBlock(ModBlocks.SLAB_PATH_SNOW, "Snow Path Slab");

        // Walls
        addBlock(ModBlocks.WALL_MUD_BRICK, "Mud Brick Wall");
        addBlock(ModBlocks.WALL_SANDSTONE_CARVED, "Carved Sandstone Wall");
        addBlock(ModBlocks.WALL_SANDSTONE_RED_CARVED, "Carved Red Sandstone Wall");
        addBlock(ModBlocks.WALL_SANDSTONE_OCHRE_CARVED, "Carved Ochre Sandstone Wall");
        addBlock(ModBlocks.WALL_SNOW, "Snow Wall");

        // Panes / Bars
        addBlock(ModBlocks.PAPER_WALL, "Paper Wall");
        addBlock(ModBlocks.WOODEN_BARS, "Wooden Bars");
        addBlock(ModBlocks.WOODEN_BARS_INDIAN, "Indian Wooden Bars");
        addBlock(ModBlocks.WOODEN_BARS_ROSETTE, "Rosette Wooden Bars");
        addBlock(ModBlocks.WOODEN_BARS_DARK, "Dark Wooden Bars");

        // Functional
        addBlock(ModBlocks.WET_BRICK, "Wet Brick");
        addBlock(ModBlocks.SILK_WORM_BLOCK, "Silk Worm");
        addBlock(ModBlocks.SNAIL_SOIL, "Snail Soil");
        addBlock(ModBlocks.SOD, "Sod");
        addBlock(ModBlocks.ALCHEMIST_EXPLOSIVE, "Alchemist's Explosive");
        addBlock(ModBlocks.ROSETTE, "Rosette");
        addBlock(ModBlocks.STAINED_GLASS, "Stained Glass");
        addBlock(ModBlocks.MILL_STATUE, "Statue");
        addBlock(ModBlocks.ICE_BRICK, "Ice Brick");
        addBlock(ModBlocks.SNOW_BRICK, "Snow Brick");

        // Paths
        addBlock(ModBlocks.PATH_DIRT, "Dirt Path");
        addBlock(ModBlocks.PATH_GRAVEL, "Gravel Path");
        addBlock(ModBlocks.PATH_SLABS, "Stone Path");
        addBlock(ModBlocks.PATH_SANDSTONE, "Sandstone Path");
        addBlock(ModBlocks.PATH_GRAVEL_SLABS, "Gravel Stone Path");
        addBlock(ModBlocks.PATH_OCHRE_TILES, "Ochre Tile Path");
        addBlock(ModBlocks.PATH_SNOW, "Snow Path");

        // Crops
        addBlock(ModBlocks.CROP_RICE, "Rice");
        addBlock(ModBlocks.CROP_TURMERIC, "Turmeric");
        addBlock(ModBlocks.CROP_MAIZE, "Maize");
        addBlock(ModBlocks.CROP_COTTON, "Cotton");
        addBlock(ModBlocks.CROP_VINE, "Vine");

        // Saplings & Leaves
        addBlock(ModBlocks.SAPLING_APPLE, "Apple Tree Sapling");
        addBlock(ModBlocks.SAPLING_OLIVE, "Olive Tree Sapling");
        addBlock(ModBlocks.SAPLING_PISTACHIO, "Pistachio Sapling");
        addBlock(ModBlocks.SAPLING_CHERRY, "Cherry Sapling");
        addBlock(ModBlocks.SAPLING_SAKURA, "Sakura Sapling");
        addBlock(ModBlocks.LEAVES_APPLE, "Apple Tree Leaves");
        addBlock(ModBlocks.LEAVES_OLIVE, "Olive Tree Leaves");
        addBlock(ModBlocks.LEAVES_PISTACHIO, "Pistachio Leaves");
        addBlock(ModBlocks.LEAVES_CHERRY, "Cherry Leaves");
        addBlock(ModBlocks.LEAVES_SAKURA, "Sakura Leaves");
        addBlock(ModBlocks.FRUIT_LEAVES, "Fruit Leaves");

        // Special
        addBlock(ModBlocks.LOCKED_CHEST, "Locked Chest");
        addBlock(ModBlocks.FIRE_PIT, "Fire Pit");
        addBlock(ModBlocks.PANEL, "Village Panel");
        addBlock(ModBlocks.IMPORT_TABLE, "Import Table");
        addBlock(ModBlocks.BED_STRAW, "Straw Bed");
        addBlock(ModBlocks.BED_CHARPOY, "Charpoy Bed");

        // Mock blocks
        addBlock(ModBlocks.MARKER_BLOCK, "Marker Block");
        addBlock(ModBlocks.MAIN_CHEST, "Main Chest Marker");
        addBlock(ModBlocks.ANIMAL_SPAWN, "Animal Spawn Marker");
        addBlock(ModBlocks.SOURCE, "Source Marker");
        addBlock(ModBlocks.FREE_BLOCK, "Free Block Marker");
        addBlock(ModBlocks.TREE_SPAWN, "Tree Spawn Marker");
        addBlock(ModBlocks.SOIL_BLOCK, "Soil Marker");
        addBlock(ModBlocks.DECOR_BLOCK, "Decoration Marker");

        // ===== Items =====
        // Currency
        addItem(ModItems.DENIER, "Denier");
        addItem(ModItems.DENIER_ARGENT, "Denier Argent");
        addItem(ModItems.DENIER_OR, "Denier Or");

        // Wands
        addItem(ModItems.SUMMONING_WAND, "Summoning Wand");
        addItem(ModItems.NEGATION_WAND, "Negation Wand");

        // Purse
        addItem(ModItems.PURSE, "Purse");

        // Food - Norman
        addItem(ModItems.CIDER_APPLE, "Cider Apple");
        addItem(ModItems.CIDER, "Cider");
        addItem(ModItems.CALVA, "Calva");
        addItem(ModItems.BOUDIN, "Boudin");
        addItem(ModItems.TRIPES, "Tripes");

        // Food - Indian
        addItem(ModItems.VEGETABLE_CURRY, "Vegetable Curry");
        addItem(ModItems.CHICKEN_CURRY, "Chicken Curry");
        addItem(ModItems.RASGULLA, "Rasgulla");

        // Food - Mayan
        addItem(ModItems.MASA, "Masa");
        addItem(ModItems.WAH, "Wah");
        addItem(ModItems.BALCHE, "Balché");
        addItem(ModItems.SIKILPAH, "Sikilpah");
        addItem(ModItems.CACAUHAA, "Cacauhaa");

        // Food - Japanese
        addItem(ModItems.UDON, "Udon");
        addItem(ModItems.SAKE, "Saké");
        addItem(ModItems.IKAYAKI, "Ikayaki");

        // Food - Byzantine
        addItem(ModItems.OLIVES, "Olives");
        addItem(ModItems.OLIVE_OIL, "Olive Oil");
        addItem(ModItems.FETA, "Feta");
        addItem(ModItems.SOUVLAKI, "Souvlaki");
        addItem(ModItems.WINE_BASIC, "Wine");
        addItem(ModItems.WINE_FANCY, "Fine Wine");

        // Food - Seljuk
        addItem(ModItems.AYRAN, "Ayran");
        addItem(ModItems.YOGURT, "Yogurt");
        addItem(ModItems.PIDE, "Pide");
        addItem(ModItems.LOKUM, "Lokum");
        addItem(ModItems.HELVA, "Helva");
        addItem(ModItems.PISTACHIOS, "Pistachios");

        // Food - Inuit
        addItem(ModItems.BEARMEAT_RAW, "Raw Bear Meat");
        addItem(ModItems.BEARMEAT_COOKED, "Cooked Bear Meat");
        addItem(ModItems.WOLFMEAT_RAW, "Raw Wolf Meat");
        addItem(ModItems.WOLFMEAT_COOKED, "Cooked Wolf Meat");
        addItem(ModItems.SEAFOOD_RAW, "Raw Seafood");
        addItem(ModItems.SEAFOOD_COOKED, "Cooked Seafood");
        addItem(ModItems.INUIT_BEAR_STEW, "Bear Stew");
        addItem(ModItems.INUIT_MEATY_STEW, "Meaty Stew");
        addItem(ModItems.INUIT_POTATO_STEW, "Potato Stew");

        // Food - Misc
        addItem(ModItems.CHERRIES, "Cherries");
        addItem(ModItems.CHERRY_BLOSSOM, "Cherry Blossom");

        // Seeds / Crops
        addItem(ModItems.RICE, "Rice");
        addItem(ModItems.TURMERIC, "Turmeric");
        addItem(ModItems.MAIZE, "Maize");
        addItem(ModItems.GRAPES, "Grapes");
        addItem(ModItems.COTTON, "Cotton");

        // Misc Goods
        addItem(ModItems.SILK, "Silk");
        addItem(ModItems.OBSIDIAN_FLAKE, "Obsidian Flake");
        addItem(ModItems.UNKNOWN_POWDER, "Unknown Powder");
        addItem(ModItems.TANNED_HIDE, "Tanned Hide");
        addItem(ModItems.BRICK_MOULD, "Brick Mould");
        addItem(ModItems.ULU, "Ulu");
        addItem(ModItems.BANNER_PATTERN, "Banner Pattern");

        // Wall Decorations
        addItem(ModItems.TAPESTRY, "Tapestry");
        addItem(ModItems.INDIAN_STATUE, "Indian Statue");
        addItem(ModItems.MAYAN_STATUE, "Mayan Statue");
        addItem(ModItems.BYZANTINE_ICON_SMALL, "Small Byzantine Icon");
        addItem(ModItems.BYZANTINE_ICON_MEDIUM, "Medium Byzantine Icon");
        addItem(ModItems.BYZANTINE_ICON_LARGE, "Large Byzantine Icon");
        addItem(ModItems.HIDE_HANGING, "Hide Hanging");
        addItem(ModItems.WALL_CARPET_SMALL, "Small Wall Carpet");
        addItem(ModItems.WALL_CARPET_MEDIUM, "Medium Wall Carpet");
        addItem(ModItems.WALL_CARPET_LARGE, "Large Wall Carpet");

        // Clothes
        addItem(ModItems.CLOTHES_BYZ_WOOL, "Byzantine Wool Clothes");
        addItem(ModItems.CLOTHES_BYZ_SILK, "Byzantine Silk Clothes");
        addItem(ModItems.CLOTHES_SELJUK_WOOL, "Seljuk Wool Clothes");
        addItem(ModItems.CLOTHES_SELJUK_COTTON, "Seljuk Cotton Clothes");

        // Banners
        addItem(ModItems.VILLAGE_BANNER, "Village Banner");
        addItem(ModItems.CULTURE_BANNER, "Culture Banner");

        // Amulets
        addItem(ModItems.AMULET_VISHNU, "Amulet of Vishnu");
        addItem(ModItems.AMULET_ALCHEMIST, "Alchemist's Amulet");
        addItem(ModItems.AMULET_YGGDRASIL, "Amulet of Yggdrasil");
        addItem(ModItems.AMULET_SKOLL_HATI, "Amulet of Sköll and Hati");

        // Norman Tools
        addItem(ModItems.NORMAN_BROADSWORD, "Norman Broadsword");
        addItem(ModItems.NORMAN_AXE, "Norman Axe");
        addItem(ModItems.NORMAN_PICKAXE, "Norman Pickaxe");
        addItem(ModItems.NORMAN_SHOVEL, "Norman Shovel");
        addItem(ModItems.NORMAN_HOE, "Norman Hoe");

        // Mayan Tools
        addItem(ModItems.MAYAN_MACE, "Mayan Mace");
        addItem(ModItems.MAYAN_AXE, "Mayan Axe");
        addItem(ModItems.MAYAN_PICKAXE, "Mayan Pickaxe");
        addItem(ModItems.MAYAN_SHOVEL, "Mayan Shovel");
        addItem(ModItems.MAYAN_HOE, "Mayan Hoe");

        // Byzantine Tools
        addItem(ModItems.BYZANTINE_MACE, "Byzantine Mace");
        addItem(ModItems.BYZANTINE_AXE, "Byzantine Axe");
        addItem(ModItems.BYZANTINE_PICKAXE, "Byzantine Pickaxe");
        addItem(ModItems.BYZANTINE_SHOVEL, "Byzantine Shovel");
        addItem(ModItems.BYZANTINE_HOE, "Byzantine Hoe");

        // Japanese / Seljuk / Inuit
        addItem(ModItems.TACHI_SWORD, "Tachi");
        addItem(ModItems.SELJUK_SCIMITAR, "Seljuk Scimitar");
        addItem(ModItems.INUIT_TRIDENT, "Inuit Trident");

        // Bows
        addItem(ModItems.YUMI_BOW, "Yumi");
        addItem(ModItems.INUIT_BOW, "Inuit Bow");
        addItem(ModItems.SELJUK_BOW, "Seljuk Bow");

        // Armor - Norman
        addItem(ModItems.NORMAN_HELMET, "Norman Helmet");
        addItem(ModItems.NORMAN_CHESTPLATE, "Norman Chestplate");
        addItem(ModItems.NORMAN_LEGGINGS, "Norman Leggings");
        addItem(ModItems.NORMAN_BOOTS, "Norman Boots");

        // Armor - Japanese Red
        addItem(ModItems.JAPANESE_RED_HELMET, "Japanese Red Helmet");
        addItem(ModItems.JAPANESE_RED_CHESTPLATE, "Japanese Red Chestplate");
        addItem(ModItems.JAPANESE_RED_LEGGINGS, "Japanese Red Leggings");
        addItem(ModItems.JAPANESE_RED_BOOTS, "Japanese Red Boots");

        // Armor - Japanese Blue
        addItem(ModItems.JAPANESE_BLUE_HELMET, "Japanese Blue Helmet");
        addItem(ModItems.JAPANESE_BLUE_CHESTPLATE, "Japanese Blue Chestplate");
        addItem(ModItems.JAPANESE_BLUE_LEGGINGS, "Japanese Blue Leggings");
        addItem(ModItems.JAPANESE_BLUE_BOOTS, "Japanese Blue Boots");

        // Armor - Japanese Guard
        addItem(ModItems.JAPANESE_GUARD_HELMET, "Japanese Guard Helmet");
        addItem(ModItems.JAPANESE_GUARD_CHESTPLATE, "Japanese Guard Chestplate");
        addItem(ModItems.JAPANESE_GUARD_LEGGINGS, "Japanese Guard Leggings");
        addItem(ModItems.JAPANESE_GUARD_BOOTS, "Japanese Guard Boots");

        // Armor - Byzantine
        addItem(ModItems.BYZANTINE_HELMET, "Byzantine Helmet");
        addItem(ModItems.BYZANTINE_CHESTPLATE, "Byzantine Chestplate");
        addItem(ModItems.BYZANTINE_LEGGINGS, "Byzantine Leggings");
        addItem(ModItems.BYZANTINE_BOOTS, "Byzantine Boots");

        // Armor - Fur
        addItem(ModItems.FUR_HELMET, "Fur Hat");
        addItem(ModItems.FUR_CHESTPLATE, "Fur Coat");
        addItem(ModItems.FUR_LEGGINGS, "Fur Leggings");
        addItem(ModItems.FUR_BOOTS, "Fur Boots");

        // Armor - Seljuk
        addItem(ModItems.SELJUK_TURBAN, "Seljuk Turban");
        addItem(ModItems.SELJUK_HELMET, "Seljuk Helmet");
        addItem(ModItems.SELJUK_CHESTPLATE, "Seljuk Chestplate");
        addItem(ModItems.SELJUK_LEGGINGS, "Seljuk Leggings");
        addItem(ModItems.SELJUK_BOOTS, "Seljuk Boots");

        // Mayan Crown
        addItem(ModItems.MAYAN_QUEST_CROWN, "Mayan Quest Crown");

        // Parchments
        addItem(ModItems.PARCHMENT_NORMAN_VILLAGERS, "Norman Villagers Parchment");
        addItem(ModItems.PARCHMENT_NORMAN_BUILDINGS, "Norman Buildings Parchment");
        addItem(ModItems.PARCHMENT_NORMAN_ITEMS, "Norman Items Parchment");
        addItem(ModItems.PARCHMENT_NORMAN_COMPLETE, "Complete Norman Parchment");
        addItem(ModItems.PARCHMENT_INDIAN_VILLAGERS, "Indian Villagers Parchment");
        addItem(ModItems.PARCHMENT_INDIAN_BUILDINGS, "Indian Buildings Parchment");
        addItem(ModItems.PARCHMENT_INDIAN_ITEMS, "Indian Items Parchment");
        addItem(ModItems.PARCHMENT_INDIAN_COMPLETE, "Complete Indian Parchment");
        addItem(ModItems.PARCHMENT_MAYAN_VILLAGERS, "Mayan Villagers Parchment");
        addItem(ModItems.PARCHMENT_MAYAN_BUILDINGS, "Mayan Buildings Parchment");
        addItem(ModItems.PARCHMENT_MAYAN_ITEMS, "Mayan Items Parchment");
        addItem(ModItems.PARCHMENT_MAYAN_COMPLETE, "Complete Mayan Parchment");
        addItem(ModItems.PARCHMENT_JAPANESE_VILLAGERS, "Japanese Villagers Parchment");
        addItem(ModItems.PARCHMENT_JAPANESE_BUILDINGS, "Japanese Buildings Parchment");
        addItem(ModItems.PARCHMENT_JAPANESE_ITEMS, "Japanese Items Parchment");
        addItem(ModItems.PARCHMENT_JAPANESE_COMPLETE, "Complete Japanese Parchment");
        addItem(ModItems.PARCHMENT_VILLAGE_SCROLL, "Village Scroll");
        addItem(ModItems.PARCHMENT_SADHU, "Sadhu Parchment");

        // ===== Commands =====
        add("millenaire2.command.error.player_only", "This command can only be run by a player.");
        add("millenaire2.command.error.village_not_found", "Village '%s' not found.");
        add("millenaire2.command.error.culture_not_found", "Culture '%s' not found.");
        add("millenaire2.command.error.culture_not_found_reload", "Culture '%s' not found after reload.");
        add("millenaire2.command.listvillages.none", "No active villages found.");
        add("millenaire2.command.listvillages.header", "Active villages (%s):");
        add("millenaire2.command.listvillages.entry", "  %s (%s) at %s \u2014 %s villagers");
        add("millenaire2.command.tp.success", "Teleported to %s.");
        add("millenaire2.command.reputation.success", "Reputation with %s set to %s.");
        add("millenaire2.command.spawn.success", "Spawned %s village at %s.");
        add("millenaire2.command.rename.success", "Renamed '%s' to '%s'.");
        add("millenaire2.command.control.released", "Released control of %s.");
        add("millenaire2.command.control.taken", "Now controlling %s.");
        add("millenaire2.command.importculture.success", "Reloaded culture '%s' successfully.");
        add("millenaire2.command.debug.resetvillagers.success", "Reset %s killed villager records. They will respawn.");
        add("millenaire2.command.debug.resendprofiles.success", "Marked %s profiles for resync.");

        // ===== Misc UI =====
        add("millenaire2.chest.locked", "This chest is locked. You need a better reputation to access it.");

        // ===== Container Titles =====
        add("container.millenaire2.fire_pit", "Fire Pit");
        add("container.millenaire2.locked_chest", "Locked Chest");
        add("container.millenaire2.import_table", "Import Table");

        // ===== Entity =====
        add("entity.millenaire2.mill_villager", "Villager");
        add("entity.millenaire2.targeted_blaze", "Targeted Blaze");
        add("entity.millenaire2.targeted_ghast", "Targeted Ghast");
        add("entity.millenaire2.targeted_wither_skeleton", "Targeted Wither Skeleton");
        add("entity.millenaire2.wall_decoration", "Wall Decoration");
    }
}

