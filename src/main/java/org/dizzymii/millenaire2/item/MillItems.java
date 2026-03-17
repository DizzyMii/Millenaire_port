package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;
import org.dizzymii.millenaire2.Millenaire2;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Central registry for all Millénaire items.
 * Items are registered via DeferredRegister in Millenaire2.ITEMS.
 */
public class MillItems {

    /** Tracks all registered items for creative tab population. */
    public static final List<DeferredItem<? extends Item>> ALL_ITEMS = new ArrayList<>();

    // ===== Currency =====
    public static final DeferredItem<Item> DENIER = registerItem("denier",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> DENIER_ARGENT = registerItem("denierargent",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final DeferredItem<Item> DENIER_OR = registerItem("denieror",
            () -> new Item(new Item.Properties().stacksTo(64)));

    // ===== Wands =====
    public static final DeferredItem<Item> SUMMONING_WAND = registerItem("summoningwand",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> NEGATION_WAND = registerItem("negationwand",
            () -> new Item(new Item.Properties().stacksTo(1)));

    // ===== Food — Raw =====
    public static final DeferredItem<Item> CIDER_APPLE = registerItem("ciderapple",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(4).saturationModifier(0.3f).build())));

    public static final DeferredItem<Item> CIDER = registerItem("cider",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(6).saturationModifier(0.6f).build())));

    public static final DeferredItem<Item> CALVA = registerItem("calva",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(2).saturationModifier(0.1f).build())));

    public static final DeferredItem<Item> TRIPES = registerItem("tripes",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(8).saturationModifier(0.8f).build())));

    public static final DeferredItem<Item> BOUDIN = registerItem("boudin",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(6).saturationModifier(0.6f).build())));

    public static final DeferredItem<Item> RASGULLA = registerItem("rasgulla",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(4).saturationModifier(0.4f).build())));

    public static final DeferredItem<Item> VEGETABLE_CURRY = registerItem("vegetablecurry",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(8).saturationModifier(0.8f).build())));

    public static final DeferredItem<Item> CHICKEN_CURRY = registerItem("chickencurry",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(10).saturationModifier(1.0f).build())));

    public static final DeferredItem<Item> RICE = registerItem("rice",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(2).saturationModifier(0.2f).build())));

    public static final DeferredItem<Item> TURMERIC = registerItem("turmeric",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> MAIZE = registerItem("maize",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(3).saturationModifier(0.3f).build())));

    public static final DeferredItem<Item> MASA = registerItem("masa",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(5).saturationModifier(0.5f).build())));

    public static final DeferredItem<Item> WAHI = registerItem("wah",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(7).saturationModifier(0.6f).build())));

    public static final DeferredItem<Item> CACAUHAA = registerItem("cacauhaa",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(2).saturationModifier(0.1f).build())));

    public static final DeferredItem<Item> BEARMEAT_RAW = registerItem("bearmeatraw",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(3).saturationModifier(0.3f).build())));

    public static final DeferredItem<Item> BEARMEAT_COOKED = registerItem("bearmeatcooked",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(8).saturationModifier(0.8f).build())));

    public static final DeferredItem<Item> WOLFMEAT_RAW = registerItem("wolfmeatraw",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(2).saturationModifier(0.2f).build())));

    public static final DeferredItem<Item> WOLFMEAT_COOKED = registerItem("wolfmeatcooked",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(6).saturationModifier(0.6f).build())));

    public static final DeferredItem<Item> SEAFOOD_RAW = registerItem("seafoodraw",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(2).saturationModifier(0.1f).build())));

    public static final DeferredItem<Item> SEAFOOD_COOKED = registerItem("seafoodcooked",
            () -> new Item(new Item.Properties()
                    .food(new net.minecraft.world.food.FoodProperties.Builder()
                            .nutrition(5).saturationModifier(0.5f).build())));

    // ===== Misc Goods =====
    public static final DeferredItem<Item> SILK = registerItem("silk",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SILK_DYED = registerItem("silkdyed",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> COTTON = registerItem("cotton",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> WOOL_COLOURED = registerItem("woolcoloured",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TAPESTRY = registerItem("tapestry",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> PARCHMENT_VILLAGE = registerItem("parchmentvillage",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> PARCHMENT_LONE = registerItem("parchmentlone",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> PURSE = registerItem("purse",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> BRICK_MOULD = registerItem("brickmould",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> PAINT_BUCKET = registerItem("paintbucket",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> BANNER_PATTERN = registerItem("bannerpattern",
            () -> new Item(new Item.Properties()));

    // ===== Norman Tools =====
    public static final DeferredItem<Item> NORMAN_BROADSWORD = registerItem("normanbroadsword",
            () -> new SwordItem(Tiers.IRON, new Item.Properties()
                    .attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4f))));

    public static final DeferredItem<Item> NORMAN_AXE = registerItem("normanaxe",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> NORMAN_PICKAXE = registerItem("normanpickaxe",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> NORMAN_SHOVEL = registerItem("normanshovel",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> NORMAN_HOE = registerItem("normanhoe",
            () -> new Item(new Item.Properties()));

    // ===== Amulets =====
    public static final DeferredItem<Item> AMULET_ALCHEMIST = registerItem("amuletalchemist",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> AMULET_SKOLL_HATI = registerItem("amuletskollhati",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> AMULET_VISHNU = registerItem("amuletvishnu",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> AMULET_YGGDRASIL = registerItem("amuletyggdrasil",
            () -> new Item(new Item.Properties().stacksTo(1)));

    // ===== Seeds =====
    public static final DeferredItem<Item> RICE_SEEDS = registerItem("riceseeds",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TURMERIC_SEEDS = registerItem("turmericseeds",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> MAIZE_SEEDS = registerItem("maizeseeds",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> COTTON_SEEDS = registerItem("cottonseeds",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> VINE_SEEDS = registerItem("vineseeds",
            () -> new Item(new Item.Properties()));

    // ===== Saplings (item form) =====
    public static final DeferredItem<Item> SAPLING_APPLE_TREE = registerItem("sapling_appletree",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SAPLING_CHERRY = registerItem("sapling_cherry",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SAPLING_OLIVE = registerItem("sapling_olive",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SAPLING_PISTACHIO = registerItem("sapling_pistachio",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SAPLING_SAKURA = registerItem("sapling_sakura",
            () -> new Item(new Item.Properties()));

    // TODO: Phase 3 will flesh out these items with proper custom Item subclasses:
    // - Norman/Byzantine/Japanese/Seljuk/Obsidian tool tiers with custom stats
    // - Armor sets per culture (Norman, Japanese Red/Blue/Guard, Byzantine, Seljuk, Fur, Mayan Crown)
    // - Custom bow (ItemMillenaireBow)
    // - Ulu (Inuit knife/hoe)
    // - ItemClothes (villager clothing)
    // - ItemWallDecoration
    // - ItemMockBanner
    // - ItemFoodMultiple variants with potion effects
    // - Summoning/Negation wand custom behavior
    // - Purse with currency UI
    // - Parchment with village creation logic
    // - Seeds that actually plant crops (BlockItem for crop blocks)
    // - Block items for all MillBlocks

    // ===== Block Items =====
    // Auto-register BlockItems for all simple blocks in MillBlocks
    static {
        registerBlockItems();
    }

    private static void registerBlockItems() {
        // Register BlockItems for all blocks defined in MillBlocks
        Millenaire2.ITEMS.registerSimpleBlockItem("stone_decoration", org.dizzymii.millenaire2.block.MillBlocks.STONE_DECORATION);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_white", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_WHITE);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_orange", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_ORANGE);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_magenta", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_MAGENTA);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_light_blue", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_LIGHT_BLUE);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_yellow", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_YELLOW);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_lime", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_LIME);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_pink", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_PINK);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_gray", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_GRAY);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_light_gray", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_LIGHT_GRAY);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_cyan", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_CYAN);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_purple", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_PURPLE);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_blue", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_BLUE);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_brown", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_BROWN);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_green", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_GREEN);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_red", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_RED);
        Millenaire2.ITEMS.registerSimpleBlockItem("painted_brick_black", org.dizzymii.millenaire2.block.MillBlocks.PAINTED_BRICK_BLACK);
        Millenaire2.ITEMS.registerSimpleBlockItem("timber_frame_plain", org.dizzymii.millenaire2.block.MillBlocks.TIMBER_FRAME_PLAIN);
        Millenaire2.ITEMS.registerSimpleBlockItem("timber_frame_cross", org.dizzymii.millenaire2.block.MillBlocks.TIMBER_FRAME_CROSS);
        Millenaire2.ITEMS.registerSimpleBlockItem("mud_brick", org.dizzymii.millenaire2.block.MillBlocks.MUD_BRICK);
        Millenaire2.ITEMS.registerSimpleBlockItem("mud_brick_extended", org.dizzymii.millenaire2.block.MillBlocks.MUD_BRICK_EXTENDED);
        Millenaire2.ITEMS.registerSimpleBlockItem("wet_brick", org.dizzymii.millenaire2.block.MillBlocks.WET_BRICK);
        Millenaire2.ITEMS.registerSimpleBlockItem("silk_worm", org.dizzymii.millenaire2.block.MillBlocks.SILK_WORM_BLOCK);
        Millenaire2.ITEMS.registerSimpleBlockItem("snail_soil", org.dizzymii.millenaire2.block.MillBlocks.SNAIL_SOIL);
        Millenaire2.ITEMS.registerSimpleBlockItem("sod", org.dizzymii.millenaire2.block.MillBlocks.SOD);
        Millenaire2.ITEMS.registerSimpleBlockItem("path_dirt", org.dizzymii.millenaire2.block.MillBlocks.PATH_DIRT);
        Millenaire2.ITEMS.registerSimpleBlockItem("path_gravel", org.dizzymii.millenaire2.block.MillBlocks.PATH_GRAVEL);
        Millenaire2.ITEMS.registerSimpleBlockItem("path_slabs", org.dizzymii.millenaire2.block.MillBlocks.PATH_SLABS);
        Millenaire2.ITEMS.registerSimpleBlockItem("path_sandstone", org.dizzymii.millenaire2.block.MillBlocks.PATH_SANDSTONE);
        Millenaire2.ITEMS.registerSimpleBlockItem("rosette", org.dizzymii.millenaire2.block.MillBlocks.ROSETTE);
        Millenaire2.ITEMS.registerSimpleBlockItem("alchemist_explosive", org.dizzymii.millenaire2.block.MillBlocks.ALCHEMIST_EXPLOSIVE);
        Millenaire2.ITEMS.registerSimpleBlockItem("sandstone_carved", org.dizzymii.millenaire2.block.MillBlocks.SANDSTONE_CARVED);
        Millenaire2.ITEMS.registerSimpleBlockItem("sandstone_decorated", org.dizzymii.millenaire2.block.MillBlocks.SANDSTONE_DECORATED);
        Millenaire2.ITEMS.registerSimpleBlockItem("mill_statue", org.dizzymii.millenaire2.block.MillBlocks.MILL_STATUE);
    }

    /**
     * Helper to register an item and also add it to the ALL_ITEMS list for creative tab.
     */
    private static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> supplier) {
        DeferredItem<T> item = Millenaire2.ITEMS.register(name, supplier);
        ALL_ITEMS.add(item);
        return item;
    }

    /**
     * Called from Millenaire2 constructor to force class loading and register all items.
     */
    public static void init() {
        // Class loading triggers all static final fields above
    }
}
