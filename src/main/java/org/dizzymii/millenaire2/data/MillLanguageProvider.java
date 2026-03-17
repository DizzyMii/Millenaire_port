package org.dizzymii.millenaire2.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.block.MillBlocks;
import org.dizzymii.millenaire2.item.MillItems;

public class MillLanguageProvider extends LanguageProvider {

    public MillLanguageProvider(PackOutput output) {
        super(output, Millenaire2.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // Creative tab
        add("itemGroup.millenaire2", "Millénaire");

        // ===== Blocks =====
        addBlock(MillBlocks.STONE_DECORATION, "Stone Decoration");
        addBlock(MillBlocks.COOKED_BRICK, "Cooked Brick");

        // Painted bricks
        addBlock(MillBlocks.PAINTED_BRICK_WHITE, "White Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_ORANGE, "Orange Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_MAGENTA, "Magenta Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_LIGHT_BLUE, "Light Blue Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_YELLOW, "Yellow Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_LIME, "Lime Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_PINK, "Pink Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_GRAY, "Gray Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_LIGHT_GRAY, "Light Gray Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_CYAN, "Cyan Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_PURPLE, "Purple Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_BLUE, "Blue Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_BROWN, "Brown Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_GREEN, "Green Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_RED, "Red Painted Brick");
        addBlock(MillBlocks.PAINTED_BRICK_BLACK, "Black Painted Brick");

        // Decorated painted bricks
        addBlock(MillBlocks.PAINTED_BRICK_DECO_WHITE, "White Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_ORANGE, "Orange Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_MAGENTA, "Magenta Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_LIGHT_BLUE, "Light Blue Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_YELLOW, "Yellow Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_LIME, "Lime Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_PINK, "Pink Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_GRAY, "Gray Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_LIGHT_GRAY, "Light Gray Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_CYAN, "Cyan Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_PURPLE, "Purple Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_BLUE, "Blue Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_BROWN, "Brown Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_GREEN, "Green Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_RED, "Red Decorated Brick");
        addBlock(MillBlocks.PAINTED_BRICK_DECO_BLACK, "Black Decorated Brick");

        // Wood
        addBlock(MillBlocks.TIMBER_FRAME_PLAIN, "Timber Frame");
        addBlock(MillBlocks.TIMBER_FRAME_CROSS, "Timber Frame (Cross)");
        addBlock(MillBlocks.THATCH, "Thatch");

        // Earth
        addBlock(MillBlocks.MUD_BRICK, "Mud Brick");
        addBlock(MillBlocks.MUD_BRICK_EXTENDED, "Extended Mud Brick");

        // Sandstone
        addBlock(MillBlocks.SANDSTONE_CARVED, "Carved Sandstone");
        addBlock(MillBlocks.SANDSTONE_RED_CARVED, "Carved Red Sandstone");
        addBlock(MillBlocks.SANDSTONE_OCHRE_CARVED, "Carved Ochre Sandstone");
        addBlock(MillBlocks.SANDSTONE_DECORATED, "Decorated Sandstone");
        addBlock(MillBlocks.BYZANTINE_STONE_ORNAMENT, "Byzantine Stone Ornament");
        addBlock(MillBlocks.BYZANTINE_SANDSTONE_ORNAMENT, "Byzantine Sandstone Ornament");

        // Tiles
        addBlock(MillBlocks.BYZANTINE_TILES, "Byzantine Tiles");
        addBlock(MillBlocks.BYZANTINE_STONE_TILES, "Byzantine Stone Tiles");
        addBlock(MillBlocks.BYZANTINE_SANDSTONE_TILES, "Byzantine Sandstone Tiles");
        addBlock(MillBlocks.GRAY_TILES, "Gray Tiles");
        addBlock(MillBlocks.GREEN_TILES, "Green Tiles");
        addBlock(MillBlocks.RED_TILES, "Red Tiles");

        // Stairs
        addBlock(MillBlocks.STAIRS_TIMBERFRAME, "Timber Frame Stairs");
        addBlock(MillBlocks.STAIRS_MUDBRICK, "Mud Brick Stairs");
        addBlock(MillBlocks.STAIRS_COOKEDBRICK, "Cooked Brick Stairs");
        addBlock(MillBlocks.STAIRS_THATCH, "Thatch Stairs");
        addBlock(MillBlocks.STAIRS_SANDSTONE_CARVED, "Carved Sandstone Stairs");
        addBlock(MillBlocks.STAIRS_SANDSTONE_RED_CARVED, "Carved Red Sandstone Stairs");
        addBlock(MillBlocks.STAIRS_SANDSTONE_OCHRE_CARVED, "Carved Ochre Sandstone Stairs");
        addBlock(MillBlocks.STAIRS_BYZANTINE_TILES, "Byzantine Tile Stairs");
        addBlock(MillBlocks.STAIRS_GRAY_TILES, "Gray Tile Stairs");
        addBlock(MillBlocks.STAIRS_GREEN_TILES, "Green Tile Stairs");
        addBlock(MillBlocks.STAIRS_RED_TILES, "Red Tile Stairs");

        // Slabs
        addBlock(MillBlocks.SLAB_WOOD_DECO, "Decorative Wood Slab");
        addBlock(MillBlocks.SLAB_STONE_DECO, "Decorative Stone Slab");
        addBlock(MillBlocks.SLAB_SANDSTONE_CARVED, "Carved Sandstone Slab");
        addBlock(MillBlocks.SLAB_SANDSTONE_RED_CARVED, "Carved Red Sandstone Slab");
        addBlock(MillBlocks.SLAB_SANDSTONE_OCHRE_CARVED, "Carved Ochre Sandstone Slab");
        addBlock(MillBlocks.SLAB_BYZANTINE_TILES, "Byzantine Tile Slab");
        addBlock(MillBlocks.SLAB_GRAY_TILES, "Gray Tile Slab");
        addBlock(MillBlocks.SLAB_GREEN_TILES, "Green Tile Slab");
        addBlock(MillBlocks.SLAB_RED_TILES, "Red Tile Slab");
        addBlock(MillBlocks.SLAB_PATH_DIRT, "Dirt Path Slab");
        addBlock(MillBlocks.SLAB_PATH_GRAVEL, "Gravel Path Slab");
        addBlock(MillBlocks.SLAB_PATH_SLABS, "Stone Path Slab");
        addBlock(MillBlocks.SLAB_PATH_SANDSTONE, "Sandstone Path Slab");
        addBlock(MillBlocks.SLAB_PATH_GRAVEL_SLABS, "Gravel Stone Path Slab");
        addBlock(MillBlocks.SLAB_PATH_OCHRE_TILES, "Ochre Tile Path Slab");
        addBlock(MillBlocks.SLAB_PATH_SNOW, "Snow Path Slab");

        // Walls
        addBlock(MillBlocks.WALL_MUD_BRICK, "Mud Brick Wall");
        addBlock(MillBlocks.WALL_SANDSTONE_CARVED, "Carved Sandstone Wall");
        addBlock(MillBlocks.WALL_SANDSTONE_RED_CARVED, "Carved Red Sandstone Wall");
        addBlock(MillBlocks.WALL_SANDSTONE_OCHRE_CARVED, "Carved Ochre Sandstone Wall");
        addBlock(MillBlocks.WALL_SNOW, "Snow Wall");

        // Panes / Bars
        addBlock(MillBlocks.PAPER_WALL, "Paper Wall");
        addBlock(MillBlocks.WOODEN_BARS, "Wooden Bars");
        addBlock(MillBlocks.WOODEN_BARS_INDIAN, "Indian Wooden Bars");
        addBlock(MillBlocks.WOODEN_BARS_ROSETTE, "Rosette Wooden Bars");
        addBlock(MillBlocks.WOODEN_BARS_DARK, "Dark Wooden Bars");

        // Functional
        addBlock(MillBlocks.WET_BRICK, "Wet Brick");
        addBlock(MillBlocks.SILK_WORM_BLOCK, "Silk Worm");
        addBlock(MillBlocks.SNAIL_SOIL, "Snail Soil");
        addBlock(MillBlocks.SOD, "Sod");
        addBlock(MillBlocks.ALCHEMIST_EXPLOSIVE, "Alchemist's Explosive");
        addBlock(MillBlocks.ROSETTE, "Rosette");
        addBlock(MillBlocks.STAINED_GLASS, "Stained Glass");
        addBlock(MillBlocks.MILL_STATUE, "Statue");
        addBlock(MillBlocks.ICE_BRICK, "Ice Brick");
        addBlock(MillBlocks.SNOW_BRICK, "Snow Brick");

        // Paths
        addBlock(MillBlocks.PATH_DIRT, "Dirt Path");
        addBlock(MillBlocks.PATH_GRAVEL, "Gravel Path");
        addBlock(MillBlocks.PATH_SLABS, "Stone Path");
        addBlock(MillBlocks.PATH_SANDSTONE, "Sandstone Path");
        addBlock(MillBlocks.PATH_GRAVEL_SLABS, "Gravel Stone Path");
        addBlock(MillBlocks.PATH_OCHRE_TILES, "Ochre Tile Path");
        addBlock(MillBlocks.PATH_SNOW, "Snow Path");

        // Crops
        addBlock(MillBlocks.CROP_RICE, "Rice");
        addBlock(MillBlocks.CROP_TURMERIC, "Turmeric");
        addBlock(MillBlocks.CROP_MAIZE, "Maize");
        addBlock(MillBlocks.CROP_COTTON, "Cotton");
        addBlock(MillBlocks.CROP_VINE, "Vine");

        // Saplings & Leaves
        addBlock(MillBlocks.SAPLING_APPLE, "Apple Tree Sapling");
        addBlock(MillBlocks.SAPLING_OLIVE, "Olive Tree Sapling");
        addBlock(MillBlocks.SAPLING_PISTACHIO, "Pistachio Sapling");
        addBlock(MillBlocks.SAPLING_CHERRY, "Cherry Sapling");
        addBlock(MillBlocks.SAPLING_SAKURA, "Sakura Sapling");
        addBlock(MillBlocks.LEAVES_APPLE, "Apple Tree Leaves");
        addBlock(MillBlocks.LEAVES_OLIVE, "Olive Tree Leaves");
        addBlock(MillBlocks.LEAVES_PISTACHIO, "Pistachio Leaves");
        addBlock(MillBlocks.LEAVES_CHERRY, "Cherry Leaves");
        addBlock(MillBlocks.LEAVES_SAKURA, "Sakura Leaves");

        // Special
        addBlock(MillBlocks.LOCKED_CHEST, "Locked Chest");
        addBlock(MillBlocks.FIRE_PIT, "Fire Pit");
        addBlock(MillBlocks.PANEL, "Village Panel");
        addBlock(MillBlocks.IMPORT_TABLE, "Import Table");
        addBlock(MillBlocks.BED_STRAW, "Straw Bed");
        addBlock(MillBlocks.BED_CHARPOY, "Charpoy Bed");

        // Mock blocks
        addBlock(MillBlocks.MARKER_BLOCK, "Marker Block");
        addBlock(MillBlocks.MAIN_CHEST, "Main Chest Marker");
        addBlock(MillBlocks.ANIMAL_SPAWN, "Animal Spawn Marker");
        addBlock(MillBlocks.SOURCE, "Source Marker");
        addBlock(MillBlocks.FREE_BLOCK, "Free Block Marker");
        addBlock(MillBlocks.TREE_SPAWN, "Tree Spawn Marker");
        addBlock(MillBlocks.SOIL_BLOCK, "Soil Marker");
        addBlock(MillBlocks.DECOR_BLOCK, "Decoration Marker");

        // ===== Items =====
        // Currency
        addItem(MillItems.DENIER, "Denier");
        addItem(MillItems.DENIER_ARGENT, "Denier Argent");
        addItem(MillItems.DENIER_OR, "Denier Or");

        // Wands
        addItem(MillItems.SUMMONING_WAND, "Summoning Wand");
        addItem(MillItems.NEGATION_WAND, "Negation Wand");

        // Purse
        addItem(MillItems.PURSE, "Purse");

        // Food - Norman
        addItem(MillItems.CIDER_APPLE, "Cider Apple");
        addItem(MillItems.CIDER, "Cider");
        addItem(MillItems.CALVA, "Calva");
        addItem(MillItems.BOUDIN, "Boudin");
        addItem(MillItems.TRIPES, "Tripes");

        // Food - Indian
        addItem(MillItems.VEGETABLE_CURRY, "Vegetable Curry");
        addItem(MillItems.CHICKEN_CURRY, "Chicken Curry");
        addItem(MillItems.RASGULLA, "Rasgulla");

        // Food - Mayan
        addItem(MillItems.MASA, "Masa");
        addItem(MillItems.WAH, "Wah");
        addItem(MillItems.BALCHE, "Balché");
        addItem(MillItems.SIKILPAH, "Sikilpah");
        addItem(MillItems.CACAUHAA, "Cacauhaa");

        // Food - Japanese
        addItem(MillItems.UDON, "Udon");
        addItem(MillItems.SAKE, "Saké");
        addItem(MillItems.IKAYAKI, "Ikayaki");

        // Food - Byzantine
        addItem(MillItems.OLIVES, "Olives");
        addItem(MillItems.OLIVE_OIL, "Olive Oil");
        addItem(MillItems.FETA, "Feta");
        addItem(MillItems.SOUVLAKI, "Souvlaki");
        addItem(MillItems.WINE_BASIC, "Wine");
        addItem(MillItems.WINE_FANCY, "Fine Wine");

        // Food - Seljuk
        addItem(MillItems.AYRAN, "Ayran");
        addItem(MillItems.YOGURT, "Yogurt");
        addItem(MillItems.PIDE, "Pide");
        addItem(MillItems.LOKUM, "Lokum");
        addItem(MillItems.HELVA, "Helva");
        addItem(MillItems.PISTACHIOS, "Pistachios");

        // Food - Inuit
        addItem(MillItems.BEARMEAT_RAW, "Raw Bear Meat");
        addItem(MillItems.BEARMEAT_COOKED, "Cooked Bear Meat");
        addItem(MillItems.WOLFMEAT_RAW, "Raw Wolf Meat");
        addItem(MillItems.WOLFMEAT_COOKED, "Cooked Wolf Meat");
        addItem(MillItems.SEAFOOD_RAW, "Raw Seafood");
        addItem(MillItems.SEAFOOD_COOKED, "Cooked Seafood");
        addItem(MillItems.INUIT_BEAR_STEW, "Bear Stew");
        addItem(MillItems.INUIT_MEATY_STEW, "Meaty Stew");
        addItem(MillItems.INUIT_POTATO_STEW, "Potato Stew");

        // Food - Misc
        addItem(MillItems.CHERRIES, "Cherries");
        addItem(MillItems.CHERRY_BLOSSOM, "Cherry Blossom");

        // Seeds / Crops
        addItem(MillItems.RICE, "Rice");
        addItem(MillItems.TURMERIC, "Turmeric");
        addItem(MillItems.MAIZE, "Maize");
        addItem(MillItems.GRAPES, "Grapes");
        addItem(MillItems.COTTON, "Cotton");

        // Misc Goods
        addItem(MillItems.SILK, "Silk");
        addItem(MillItems.OBSIDIAN_FLAKE, "Obsidian Flake");
        addItem(MillItems.UNKNOWN_POWDER, "Unknown Powder");
        addItem(MillItems.TANNED_HIDE, "Tanned Hide");
        addItem(MillItems.BRICK_MOULD, "Brick Mould");
        addItem(MillItems.ULU, "Ulu");
        addItem(MillItems.BANNER_PATTERN, "Banner Pattern");

        // Wall Decorations
        addItem(MillItems.TAPESTRY, "Tapestry");
        addItem(MillItems.INDIAN_STATUE, "Indian Statue");
        addItem(MillItems.MAYAN_STATUE, "Mayan Statue");
        addItem(MillItems.BYZANTINE_ICON_SMALL, "Small Byzantine Icon");
        addItem(MillItems.BYZANTINE_ICON_MEDIUM, "Medium Byzantine Icon");
        addItem(MillItems.BYZANTINE_ICON_LARGE, "Large Byzantine Icon");
        addItem(MillItems.HIDE_HANGING, "Hide Hanging");
        addItem(MillItems.WALL_CARPET_SMALL, "Small Wall Carpet");
        addItem(MillItems.WALL_CARPET_MEDIUM, "Medium Wall Carpet");
        addItem(MillItems.WALL_CARPET_LARGE, "Large Wall Carpet");

        // Clothes
        addItem(MillItems.CLOTHES_BYZ_WOOL, "Byzantine Wool Clothes");
        addItem(MillItems.CLOTHES_BYZ_SILK, "Byzantine Silk Clothes");
        addItem(MillItems.CLOTHES_SELJUK_WOOL, "Seljuk Wool Clothes");
        addItem(MillItems.CLOTHES_SELJUK_COTTON, "Seljuk Cotton Clothes");

        // Banners
        addItem(MillItems.VILLAGE_BANNER, "Village Banner");
        addItem(MillItems.CULTURE_BANNER, "Culture Banner");

        // Amulets
        addItem(MillItems.AMULET_VISHNU, "Amulet of Vishnu");
        addItem(MillItems.AMULET_ALCHEMIST, "Alchemist's Amulet");
        addItem(MillItems.AMULET_YGGDRASIL, "Amulet of Yggdrasil");
        addItem(MillItems.AMULET_SKOLL_HATI, "Amulet of Sköll and Hati");

        // Norman Tools
        addItem(MillItems.NORMAN_BROADSWORD, "Norman Broadsword");
        addItem(MillItems.NORMAN_AXE, "Norman Axe");
        addItem(MillItems.NORMAN_PICKAXE, "Norman Pickaxe");
        addItem(MillItems.NORMAN_SHOVEL, "Norman Shovel");
        addItem(MillItems.NORMAN_HOE, "Norman Hoe");

        // Mayan Tools
        addItem(MillItems.MAYAN_MACE, "Mayan Mace");
        addItem(MillItems.MAYAN_AXE, "Mayan Axe");
        addItem(MillItems.MAYAN_PICKAXE, "Mayan Pickaxe");
        addItem(MillItems.MAYAN_SHOVEL, "Mayan Shovel");
        addItem(MillItems.MAYAN_HOE, "Mayan Hoe");

        // Byzantine Tools
        addItem(MillItems.BYZANTINE_MACE, "Byzantine Mace");
        addItem(MillItems.BYZANTINE_AXE, "Byzantine Axe");
        addItem(MillItems.BYZANTINE_PICKAXE, "Byzantine Pickaxe");
        addItem(MillItems.BYZANTINE_SHOVEL, "Byzantine Shovel");
        addItem(MillItems.BYZANTINE_HOE, "Byzantine Hoe");

        // Japanese / Seljuk / Inuit
        addItem(MillItems.TACHI_SWORD, "Tachi");
        addItem(MillItems.SELJUK_SCIMITAR, "Seljuk Scimitar");
        addItem(MillItems.INUIT_TRIDENT, "Inuit Trident");

        // Bows
        addItem(MillItems.YUMI_BOW, "Yumi");
        addItem(MillItems.INUIT_BOW, "Inuit Bow");
        addItem(MillItems.SELJUK_BOW, "Seljuk Bow");

        // Armor - Norman
        addItem(MillItems.NORMAN_HELMET, "Norman Helmet");
        addItem(MillItems.NORMAN_CHESTPLATE, "Norman Chestplate");
        addItem(MillItems.NORMAN_LEGGINGS, "Norman Leggings");
        addItem(MillItems.NORMAN_BOOTS, "Norman Boots");

        // Armor - Japanese Red
        addItem(MillItems.JAPANESE_RED_HELMET, "Japanese Red Helmet");
        addItem(MillItems.JAPANESE_RED_CHESTPLATE, "Japanese Red Chestplate");
        addItem(MillItems.JAPANESE_RED_LEGGINGS, "Japanese Red Leggings");
        addItem(MillItems.JAPANESE_RED_BOOTS, "Japanese Red Boots");

        // Armor - Japanese Blue
        addItem(MillItems.JAPANESE_BLUE_HELMET, "Japanese Blue Helmet");
        addItem(MillItems.JAPANESE_BLUE_CHESTPLATE, "Japanese Blue Chestplate");
        addItem(MillItems.JAPANESE_BLUE_LEGGINGS, "Japanese Blue Leggings");
        addItem(MillItems.JAPANESE_BLUE_BOOTS, "Japanese Blue Boots");

        // Armor - Japanese Guard
        addItem(MillItems.JAPANESE_GUARD_HELMET, "Japanese Guard Helmet");
        addItem(MillItems.JAPANESE_GUARD_CHESTPLATE, "Japanese Guard Chestplate");
        addItem(MillItems.JAPANESE_GUARD_LEGGINGS, "Japanese Guard Leggings");
        addItem(MillItems.JAPANESE_GUARD_BOOTS, "Japanese Guard Boots");

        // Armor - Byzantine
        addItem(MillItems.BYZANTINE_HELMET, "Byzantine Helmet");
        addItem(MillItems.BYZANTINE_CHESTPLATE, "Byzantine Chestplate");
        addItem(MillItems.BYZANTINE_LEGGINGS, "Byzantine Leggings");
        addItem(MillItems.BYZANTINE_BOOTS, "Byzantine Boots");

        // Armor - Fur
        addItem(MillItems.FUR_HELMET, "Fur Hat");
        addItem(MillItems.FUR_CHESTPLATE, "Fur Coat");
        addItem(MillItems.FUR_LEGGINGS, "Fur Leggings");
        addItem(MillItems.FUR_BOOTS, "Fur Boots");

        // Armor - Seljuk
        addItem(MillItems.SELJUK_TURBAN, "Seljuk Turban");
        addItem(MillItems.SELJUK_HELMET, "Seljuk Helmet");
        addItem(MillItems.SELJUK_CHESTPLATE, "Seljuk Chestplate");
        addItem(MillItems.SELJUK_LEGGINGS, "Seljuk Leggings");
        addItem(MillItems.SELJUK_BOOTS, "Seljuk Boots");

        // Mayan Crown
        addItem(MillItems.MAYAN_QUEST_CROWN, "Mayan Quest Crown");

        // Parchments
        addItem(MillItems.PARCHMENT_NORMAN_VILLAGERS, "Norman Villagers Parchment");
        addItem(MillItems.PARCHMENT_NORMAN_BUILDINGS, "Norman Buildings Parchment");
        addItem(MillItems.PARCHMENT_NORMAN_ITEMS, "Norman Items Parchment");
        addItem(MillItems.PARCHMENT_NORMAN_COMPLETE, "Complete Norman Parchment");
        addItem(MillItems.PARCHMENT_INDIAN_VILLAGERS, "Indian Villagers Parchment");
        addItem(MillItems.PARCHMENT_INDIAN_BUILDINGS, "Indian Buildings Parchment");
        addItem(MillItems.PARCHMENT_INDIAN_ITEMS, "Indian Items Parchment");
        addItem(MillItems.PARCHMENT_INDIAN_COMPLETE, "Complete Indian Parchment");
        addItem(MillItems.PARCHMENT_MAYAN_VILLAGERS, "Mayan Villagers Parchment");
        addItem(MillItems.PARCHMENT_MAYAN_BUILDINGS, "Mayan Buildings Parchment");
        addItem(MillItems.PARCHMENT_MAYAN_ITEMS, "Mayan Items Parchment");
        addItem(MillItems.PARCHMENT_MAYAN_COMPLETE, "Complete Mayan Parchment");
        addItem(MillItems.PARCHMENT_JAPANESE_VILLAGERS, "Japanese Villagers Parchment");
        addItem(MillItems.PARCHMENT_JAPANESE_BUILDINGS, "Japanese Buildings Parchment");
        addItem(MillItems.PARCHMENT_JAPANESE_ITEMS, "Japanese Items Parchment");
        addItem(MillItems.PARCHMENT_JAPANESE_COMPLETE, "Complete Japanese Parchment");
        addItem(MillItems.PARCHMENT_VILLAGE_SCROLL, "Village Scroll");
        addItem(MillItems.PARCHMENT_SADHU, "Sadhu Parchment");

        // ===== Container Titles =====
        add("container.millenaire2.fire_pit", "Fire Pit");
        add("container.millenaire2.locked_chest", "Locked Chest");
        add("container.millenaire2.import_table", "Import Table");

        // ===== Entity =====
        add("entity.millenaire2.mill_villager_male", "Villager");
        add("entity.millenaire2.mill_villager_female_symm", "Villager");
        add("entity.millenaire2.mill_villager_female_asymm", "Villager");
        add("entity.millenaire2.targeted_blaze", "Targeted Blaze");
        add("entity.millenaire2.targeted_ghast", "Targeted Ghast");
        add("entity.millenaire2.targeted_wither_skeleton", "Targeted Wither Skeleton");
        add("entity.millenaire2.wall_decoration", "Wall Decoration");
    }
}
