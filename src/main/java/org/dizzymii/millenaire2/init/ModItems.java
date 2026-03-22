package org.dizzymii.millenaire2.init;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.init.ModBlocks;
import org.dizzymii.millenaire2.item.MillTiers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Central registry for all Millénaire items.
 * Items are registered via DeferredRegister in ITEMS.
 *
 * Ported from org.millenaire.common.item.MillItems (Forge 1.12.2).
 * Armor items are stubbed as plain Items â€” full ArmorMaterial registration in a later phase.
 */
public class ModItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Millenaire2.MODID);

    /** Tracks all registered items for creative tab population. */
    public static final List<DeferredItem<? extends Item>> ALL_ITEMS = new ArrayList<>();

    // ========== Helpers ==========
    private static Item.Properties props() { return new Item.Properties(); }
    private static Item.Properties props1() { return new Item.Properties().stacksTo(1); }
    private static FoodProperties food(int nutrition, float saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build();
    }

    // ========== Currency ==========
    public static final DeferredItem<Item> DENIER = registerItem("denier", () -> new Item(props()));
    public static final DeferredItem<Item> DENIER_ARGENT = registerItem("denierargent", () -> new Item(props()));
    public static final DeferredItem<Item> DENIER_OR = registerItem("denieror", () -> new Item(props()));

    // ========== Wands ==========
    public static final DeferredItem<Item> SUMMONING_WAND = registerItem("summoningwand", () -> new Item(props1()));
    public static final DeferredItem<Item> NEGATION_WAND = registerItem("negationwand", () -> new Item(props1()));

    // ========== Purse ==========
    public static final DeferredItem<Item> PURSE = registerItem("purse", () -> new Item(props1()));

    // ========== Food â€” Norman ==========
    public static final DeferredItem<Item> CIDER_APPLE = registerItem("ciderapple",
            () -> new Item(props().food(food(1, 0.05f))));
    public static final DeferredItem<Item> CIDER = registerItem("cider",
            () -> new Item(props().food(food(4, 0.6f))));
    public static final DeferredItem<Item> CALVA = registerItem("calva",
            () -> new Item(props().food(food(8, 0.3f))));
    public static final DeferredItem<Item> BOUDIN = registerItem("boudin",
            () -> new Item(props().food(food(8, 1.0f))));
    public static final DeferredItem<Item> TRIPES = registerItem("tripes",
            () -> new Item(props().food(food(10, 1.0f))));

    // ========== Food â€” Indian ==========
    public static final DeferredItem<Item> VEGETABLE_CURRY = registerItem("vegcurry",
            () -> new Item(props().food(food(6, 0.6f))));
    public static final DeferredItem<Item> CHICKEN_CURRY = registerItem("chickencurry",
            () -> new Item(props().food(food(8, 0.8f))));
    public static final DeferredItem<Item> RASGULLA = registerItem("rasgulla",
            () -> new Item(props().food(food(2, 0.3f))));

    // ========== Food â€” Mayan ==========
    public static final DeferredItem<Item> MASA = registerItem("masa",
            () -> new Item(props().food(food(6, 0.6f))));
    public static final DeferredItem<Item> WAH = registerItem("wah",
            () -> new Item(props().food(food(10, 1.0f))));
    public static final DeferredItem<Item> BALCHE = registerItem("balche",
            () -> new Item(props().food(food(6, 0.4f))));
    public static final DeferredItem<Item> SIKILPAH = registerItem("sikilpah",
            () -> new Item(props().food(food(7, 0.7f))));
    public static final DeferredItem<Item> CACAUHAA = registerItem("cacauhaa",
            () -> new Item(props().food(food(6, 0.3f))));

    // ========== Food â€” Japanese ==========
    public static final DeferredItem<Item> UDON = registerItem("udon",
            () -> new Item(props().food(food(8, 0.8f))));
    public static final DeferredItem<Item> SAKE = registerItem("sake",
            () -> new Item(props().food(food(8, 0.3f))));
    public static final DeferredItem<Item> IKAYAKI = registerItem("ikayaki",
            () -> new Item(props().food(food(10, 1.0f))));

    // ========== Food â€” Byzantine ==========
    public static final DeferredItem<Item> OLIVES = registerItem("olives",
            () -> new Item(props().food(food(1, 0.05f))));
    public static final DeferredItem<Item> OLIVE_OIL = registerItem("oliveoil",
            () -> new Item(props().stacksTo(16)));
    public static final DeferredItem<Item> FETA = registerItem("feta",
            () -> new Item(props().food(food(2, 0.3f))));
    public static final DeferredItem<Item> SOUVLAKI = registerItem("souvlaki",
            () -> new Item(props().food(food(10, 1.0f))));
    public static final DeferredItem<Item> WINE_BASIC = registerItem("winebasic",
            () -> new Item(props().food(food(3, 0.3f))));
    public static final DeferredItem<Item> WINE_FANCY = registerItem("winefancy",
            () -> new Item(props().food(food(8, 0.3f))));

    // ========== Food â€” Seljuk ==========
    public static final DeferredItem<Item> AYRAN = registerItem("ayran",
            () -> new Item(props().food(food(3, 0.2f))));
    public static final DeferredItem<Item> YOGURT = registerItem("yogurt",
            () -> new Item(props().food(food(3, 0.4f))));
    public static final DeferredItem<Item> PIDE = registerItem("pide",
            () -> new Item(props().food(food(8, 1.0f))));
    public static final DeferredItem<Item> LOKUM = registerItem("lokum",
            () -> new Item(props().food(food(3, 0.0f))));
    public static final DeferredItem<Item> HELVA = registerItem("helva",
            () -> new Item(props().food(food(5, 0.0f))));
    public static final DeferredItem<Item> PISTACHIOS = registerItem("pistachios",
            () -> new Item(props().food(food(1, 0.1f))));

    // ========== Food â€” Inuit ==========
    public static final DeferredItem<Item> BEARMEAT_RAW = registerItem("bearmeat_raw",
            () -> new Item(props().food(food(4, 0.5f))));
    public static final DeferredItem<Item> BEARMEAT_COOKED = registerItem("bearmeat_cooked",
            () -> new Item(props().food(food(10, 1.0f))));
    public static final DeferredItem<Item> WOLFMEAT_RAW = registerItem("wolfmeat_raw",
            () -> new Item(props().food(food(3, 0.3f))));
    public static final DeferredItem<Item> WOLFMEAT_COOKED = registerItem("wolfmeat_cooked",
            () -> new Item(props().food(food(5, 0.6f))));
    public static final DeferredItem<Item> SEAFOOD_RAW = registerItem("seafood_raw",
            () -> new Item(props().food(food(2, 0.2f))));
    public static final DeferredItem<Item> SEAFOOD_COOKED = registerItem("seafood_cooked",
            () -> new Item(props().food(food(5, 0.5f))));
    public static final DeferredItem<Item> INUIT_BEAR_STEW = registerItem("inuitbearstew",
            () -> new Item(props().food(food(8, 1.0f))));
    public static final DeferredItem<Item> INUIT_MEATY_STEW = registerItem("inuitmeatystew",
            () -> new Item(props().food(food(8, 0.8f))));
    public static final DeferredItem<Item> INUIT_POTATO_STEW = registerItem("inuitpotatostew",
            () -> new Item(props().food(food(6, 0.6f))));

    // ========== Food â€” Misc ==========
    public static final DeferredItem<Item> CHERRIES = registerItem("cherries",
            () -> new Item(props().food(food(1, 0.1f))));
    public static final DeferredItem<Item> CHERRY_BLOSSOM = registerItem("cherry_blossom",
            () -> new Item(props().food(food(1, 0.1f))));

    // ========== Seeds / Crops ==========
    public static final DeferredItem<Item> RICE = registerItem("rice",
            () -> new Item(props().food(food(2, 0.2f))));
    public static final DeferredItem<Item> TURMERIC = registerItem("turmeric", () -> new Item(props()));
    public static final DeferredItem<Item> MAIZE = registerItem("maize",
            () -> new Item(props().food(food(3, 0.3f))));
    public static final DeferredItem<Item> GRAPES = registerItem("grapes",
            () -> new Item(props().food(food(1, 0.1f))));
    public static final DeferredItem<Item> COTTON = registerItem("cotton", () -> new Item(props()));

    // ========== Misc Goods ==========
    public static final DeferredItem<Item> SILK = registerItem("silk", () -> new Item(props()));
    public static final DeferredItem<Item> OBSIDIAN_FLAKE = registerItem("obsidianflake", () -> new Item(props()));
    public static final DeferredItem<Item> UNKNOWN_POWDER = registerItem("unknownpowder", () -> new Item(props()));
    public static final DeferredItem<Item> TANNED_HIDE = registerItem("tannedhide", () -> new Item(props()));
    public static final DeferredItem<Item> BRICK_MOULD = registerItem("brickmould",
            () -> new Item(props1().durability(512)));
    public static final DeferredItem<Item> ULU = registerItem("ulu",
            () -> new Item(props1().durability(512)));
    public static final DeferredItem<Item> BANNER_PATTERN = registerItem("bannerpattern", () -> new Item(props()));

    // ========== Wall Decorations (stubbed as plain items) ==========
    public static final DeferredItem<Item> TAPESTRY = registerItem("tapestry", () -> new Item(props()));
    public static final DeferredItem<Item> INDIAN_STATUE = registerItem("indianstatue", () -> new Item(props()));
    public static final DeferredItem<Item> MAYAN_STATUE = registerItem("mayanstatue", () -> new Item(props()));
    public static final DeferredItem<Item> BYZANTINE_ICON_SMALL = registerItem("byzantineiconsmall", () -> new Item(props()));
    public static final DeferredItem<Item> BYZANTINE_ICON_MEDIUM = registerItem("byzantineiconmedium", () -> new Item(props()));
    public static final DeferredItem<Item> BYZANTINE_ICON_LARGE = registerItem("byzantineiconlarge", () -> new Item(props()));
    public static final DeferredItem<Item> HIDE_HANGING = registerItem("hidehanging", () -> new Item(props()));
    public static final DeferredItem<Item> WALL_CARPET_SMALL = registerItem("wallcarpetsmall", () -> new Item(props()));
    public static final DeferredItem<Item> WALL_CARPET_MEDIUM = registerItem("wallcarpetmedium", () -> new Item(props()));
    public static final DeferredItem<Item> WALL_CARPET_LARGE = registerItem("wallcarpetlarge", () -> new Item(props()));

    // ========== Clothes (stubbed) ==========
    public static final DeferredItem<Item> CLOTHES_BYZ_WOOL = registerItem("clothes_byz_wool", () -> new Item(props()));
    public static final DeferredItem<Item> CLOTHES_BYZ_SILK = registerItem("clothes_byz_silk", () -> new Item(props()));
    public static final DeferredItem<Item> CLOTHES_SELJUK_WOOL = registerItem("clothes_seljuk_wool", () -> new Item(props()));
    public static final DeferredItem<Item> CLOTHES_SELJUK_COTTON = registerItem("clothes_seljuk_cotton", () -> new Item(props()));

    // ========== Banners (stubbed) ==========
    public static final DeferredItem<Item> VILLAGE_BANNER = registerItem("villagebanner", () -> new Item(props()));
    public static final DeferredItem<Item> CULTURE_BANNER = registerItem("culturebanner", () -> new Item(props()));

    // ========== Amulets ==========
    public static final DeferredItem<Item> AMULET_VISHNU = registerItem("vishnu_amulet", () -> new Item(props1()));
    public static final DeferredItem<Item> AMULET_ALCHEMIST = registerItem("alchemist_amulet", () -> new Item(props1()));
    public static final DeferredItem<Item> AMULET_YGGDRASIL = registerItem("yggdrasil_amulet", () -> new Item(props1()));
    public static final DeferredItem<Item> AMULET_SKOLL_HATI = registerItem("skoll_hati_amulet", () -> new Item(props1()));

    // ========== Norman Tools ==========
    public static final DeferredItem<Item> NORMAN_BROADSWORD = registerItem("normanbroadsword",
            () -> new SwordItem(MillTiers.NORMAN, props().attributes(SwordItem.createAttributes(MillTiers.NORMAN, 3, -2.4f))));
    public static final DeferredItem<Item> NORMAN_AXE = registerItem("normanaxe",
            () -> new AxeItem(MillTiers.NORMAN, props().attributes(AxeItem.createAttributes(MillTiers.NORMAN, 8.0f, -3.0f))));
    public static final DeferredItem<Item> NORMAN_PICKAXE = registerItem("normanpickaxe",
            () -> new PickaxeItem(MillTiers.NORMAN, props().attributes(PickaxeItem.createAttributes(MillTiers.NORMAN, 1, -2.8f))));
    public static final DeferredItem<Item> NORMAN_SHOVEL = registerItem("normanshovel",
            () -> new ShovelItem(MillTiers.NORMAN, props().attributes(ShovelItem.createAttributes(MillTiers.NORMAN, 1.5f, -3.0f))));
    public static final DeferredItem<Item> NORMAN_HOE = registerItem("normanhoe",
            () -> new HoeItem(MillTiers.NORMAN, props().attributes(HoeItem.createAttributes(MillTiers.NORMAN, -2, -1.0f))));

    // ========== Mayan Tools (Obsidian tier) ==========
    public static final DeferredItem<Item> MAYAN_MACE = registerItem("mayanmace",
            () -> new SwordItem(Tiers.IRON, props().attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4f))));
    public static final DeferredItem<Item> MAYAN_AXE = registerItem("mayanaxe",
            () -> new AxeItem(MillTiers.OBSIDIAN, props().attributes(AxeItem.createAttributes(MillTiers.OBSIDIAN, 8.0f, -3.0f))));
    public static final DeferredItem<Item> MAYAN_PICKAXE = registerItem("mayanpickaxe",
            () -> new PickaxeItem(MillTiers.OBSIDIAN, props().attributes(PickaxeItem.createAttributes(MillTiers.OBSIDIAN, 1, -2.8f))));
    public static final DeferredItem<Item> MAYAN_SHOVEL = registerItem("mayanshovel",
            () -> new ShovelItem(MillTiers.OBSIDIAN, props().attributes(ShovelItem.createAttributes(MillTiers.OBSIDIAN, 1.5f, -3.0f))));
    public static final DeferredItem<Item> MAYAN_HOE = registerItem("mayanhoe",
            () -> new HoeItem(MillTiers.OBSIDIAN, props().attributes(HoeItem.createAttributes(MillTiers.OBSIDIAN, -2, -1.0f))));

    // ========== Byzantine Tools ==========
    public static final DeferredItem<Item> BYZANTINE_MACE = registerItem("byzantinemace",
            () -> new SwordItem(Tiers.IRON, props().attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4f))));
    public static final DeferredItem<Item> BYZANTINE_AXE = registerItem("byzantineaxe",
            () -> new AxeItem(MillTiers.BYZANTINE, props().attributes(AxeItem.createAttributes(MillTiers.BYZANTINE, 8.0f, -3.0f))));
    public static final DeferredItem<Item> BYZANTINE_PICKAXE = registerItem("byzantinepickaxe",
            () -> new PickaxeItem(MillTiers.BYZANTINE, props().attributes(PickaxeItem.createAttributes(MillTiers.BYZANTINE, 1, -2.8f))));
    public static final DeferredItem<Item> BYZANTINE_SHOVEL = registerItem("byzantineshovel",
            () -> new ShovelItem(MillTiers.BYZANTINE, props().attributes(ShovelItem.createAttributes(MillTiers.BYZANTINE, 1.5f, -3.0f))));
    public static final DeferredItem<Item> BYZANTINE_HOE = registerItem("byzantinehoe",
            () -> new HoeItem(MillTiers.BYZANTINE, props().attributes(HoeItem.createAttributes(MillTiers.BYZANTINE, -2, -1.0f))));

    // ========== Japanese Tools ==========
    public static final DeferredItem<Item> TACHI_SWORD = registerItem("tachisword",
            () -> new SwordItem(MillTiers.OBSIDIAN, props().attributes(SwordItem.createAttributes(MillTiers.OBSIDIAN, 3, -2.4f))));

    // ========== Seljuk Tools ==========
    public static final DeferredItem<Item> SELJUK_SCIMITAR = registerItem("seljukscimitar",
            () -> new SwordItem(MillTiers.BETTER_STEEL, props().attributes(SwordItem.createAttributes(MillTiers.BETTER_STEEL, 3, -2.4f))));

    // ========== Inuit Tools ==========
    public static final DeferredItem<Item> INUIT_TRIDENT = registerItem("inuittrident",
            () -> new SwordItem(Tiers.IRON, props().attributes(SwordItem.createAttributes(Tiers.IRON, 3, -2.4f)).durability(20)));

    // ========== Bows (stubbed as plain items â€” custom BowItem behavior later) ==========
    public static final DeferredItem<Item> YUMI_BOW = registerItem("yumibow", () -> new Item(props1().durability(384)));
    public static final DeferredItem<Item> INUIT_BOW = registerItem("inuitbow", () -> new Item(props1().durability(384)));
    public static final DeferredItem<Item> SELJUK_BOW = registerItem("seljukbow", () -> new Item(props1().durability(384)));

    // ========== Armor â€” Norman (stubbed as plain items, proper ArmorItem later) ==========
    public static final DeferredItem<Item> NORMAN_HELMET = registerItem("normanhelmet", () -> new Item(props1()));
    public static final DeferredItem<Item> NORMAN_CHESTPLATE = registerItem("normanplate", () -> new Item(props1()));
    public static final DeferredItem<Item> NORMAN_LEGGINGS = registerItem("normanlegs", () -> new Item(props1()));
    public static final DeferredItem<Item> NORMAN_BOOTS = registerItem("normanboots", () -> new Item(props1()));

    // ========== Armor â€” Japanese Red ==========
    public static final DeferredItem<Item> JAPANESE_RED_HELMET = registerItem("japaneseredhelmet", () -> new Item(props1()));
    public static final DeferredItem<Item> JAPANESE_RED_CHESTPLATE = registerItem("japaneseredplate", () -> new Item(props1()));
    public static final DeferredItem<Item> JAPANESE_RED_LEGGINGS = registerItem("japaneseredlegs", () -> new Item(props1()));
    public static final DeferredItem<Item> JAPANESE_RED_BOOTS = registerItem("japaneseredboots", () -> new Item(props1()));

    // ========== Armor â€” Japanese Blue ==========
    public static final DeferredItem<Item> JAPANESE_BLUE_HELMET = registerItem("japanesebluehelmet", () -> new Item(props1()));
    public static final DeferredItem<Item> JAPANESE_BLUE_CHESTPLATE = registerItem("japaneseblueplate", () -> new Item(props1()));
    public static final DeferredItem<Item> JAPANESE_BLUE_LEGGINGS = registerItem("japanesebluelegs", () -> new Item(props1()));
    public static final DeferredItem<Item> JAPANESE_BLUE_BOOTS = registerItem("japaneseblueboots", () -> new Item(props1()));

    // ========== Armor â€” Japanese Guard ==========
    public static final DeferredItem<Item> JAPANESE_GUARD_HELMET = registerItem("japaneseguardhelmet", () -> new Item(props1()));
    public static final DeferredItem<Item> JAPANESE_GUARD_CHESTPLATE = registerItem("japaneseguardplate", () -> new Item(props1()));
    public static final DeferredItem<Item> JAPANESE_GUARD_LEGGINGS = registerItem("japaneseguardlegs", () -> new Item(props1()));
    public static final DeferredItem<Item> JAPANESE_GUARD_BOOTS = registerItem("japaneseguardboots", () -> new Item(props1()));

    // ========== Armor â€” Byzantine ==========
    public static final DeferredItem<Item> BYZANTINE_HELMET = registerItem("byzantinehelmet", () -> new Item(props1()));
    public static final DeferredItem<Item> BYZANTINE_CHESTPLATE = registerItem("byzantineplate", () -> new Item(props1()));
    public static final DeferredItem<Item> BYZANTINE_LEGGINGS = registerItem("byzantinelegs", () -> new Item(props1()));
    public static final DeferredItem<Item> BYZANTINE_BOOTS = registerItem("byzantineboots", () -> new Item(props1()));

    // ========== Armor â€” Fur (Inuit) ==========
    public static final DeferredItem<Item> FUR_HELMET = registerItem("furhelmet", () -> new Item(props1()));
    public static final DeferredItem<Item> FUR_CHESTPLATE = registerItem("furplate", () -> new Item(props1()));
    public static final DeferredItem<Item> FUR_LEGGINGS = registerItem("furlegs", () -> new Item(props1()));
    public static final DeferredItem<Item> FUR_BOOTS = registerItem("furboots", () -> new Item(props1()));

    // ========== Armor â€” Seljuk ==========
    public static final DeferredItem<Item> SELJUK_TURBAN = registerItem("seljukturban", () -> new Item(props1()));
    public static final DeferredItem<Item> SELJUK_HELMET = registerItem("seljukhelmet", () -> new Item(props1()));
    public static final DeferredItem<Item> SELJUK_CHESTPLATE = registerItem("seljukplate", () -> new Item(props1()));
    public static final DeferredItem<Item> SELJUK_LEGGINGS = registerItem("seljuklegs", () -> new Item(props1()));
    public static final DeferredItem<Item> SELJUK_BOOTS = registerItem("seljukboots", () -> new Item(props1()));

    // ========== Armor â€” Mayan Crown ==========
    public static final DeferredItem<Item> MAYAN_QUEST_CROWN = registerItem("mayanquestcrown", () -> new Item(props1()));

    // ========== Parchments ==========
    public static final DeferredItem<Item> PARCHMENT_NORMAN_VILLAGERS = registerItem("parchment_normanvillagers", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_NORMAN_BUILDINGS = registerItem("parchment_normanbuildings", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_NORMAN_ITEMS = registerItem("parchment_normanitems", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_NORMAN_COMPLETE = registerItem("parchment_normanfull", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_INDIAN_VILLAGERS = registerItem("parchment_indianvillagers", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_INDIAN_BUILDINGS = registerItem("parchment_indianbuildings", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_INDIAN_ITEMS = registerItem("parchment_indianitems", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_INDIAN_COMPLETE = registerItem("parchment_indianfull", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_MAYAN_VILLAGERS = registerItem("parchment_mayanvillagers", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_MAYAN_BUILDINGS = registerItem("parchment_mayanbuildings", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_MAYAN_ITEMS = registerItem("parchment_mayanitems", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_MAYAN_COMPLETE = registerItem("parchment_mayanfull", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_JAPANESE_VILLAGERS = registerItem("parchment_japanesevillagers", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_JAPANESE_BUILDINGS = registerItem("parchment_japanesebuildings", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_JAPANESE_ITEMS = registerItem("parchment_japaneseitems", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_JAPANESE_COMPLETE = registerItem("parchment_japanesefull", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_VILLAGE_SCROLL = registerItem("parchment_villagescroll", () -> new Item(props1()));
    public static final DeferredItem<Item> PARCHMENT_SADHU = registerItem("parchment_sadhu", () -> new Item(props1()));

    // ========== Block Items ==========
    static {
        registerBlockItems();
    }

    private static void registerBlockItems() {
        // Decorative stone
        ITEMS.registerSimpleBlockItem("stone_decoration", ModBlocks.STONE_DECORATION);
        ITEMS.registerSimpleBlockItem("cooked_brick", ModBlocks.COOKED_BRICK);

        // Painted bricks
        ITEMS.registerSimpleBlockItem("painted_brick_white", ModBlocks.PAINTED_BRICK_WHITE);
        ITEMS.registerSimpleBlockItem("painted_brick_orange", ModBlocks.PAINTED_BRICK_ORANGE);
        ITEMS.registerSimpleBlockItem("painted_brick_magenta", ModBlocks.PAINTED_BRICK_MAGENTA);
        ITEMS.registerSimpleBlockItem("painted_brick_light_blue", ModBlocks.PAINTED_BRICK_LIGHT_BLUE);
        ITEMS.registerSimpleBlockItem("painted_brick_yellow", ModBlocks.PAINTED_BRICK_YELLOW);
        ITEMS.registerSimpleBlockItem("painted_brick_lime", ModBlocks.PAINTED_BRICK_LIME);
        ITEMS.registerSimpleBlockItem("painted_brick_pink", ModBlocks.PAINTED_BRICK_PINK);
        ITEMS.registerSimpleBlockItem("painted_brick_gray", ModBlocks.PAINTED_BRICK_GRAY);
        ITEMS.registerSimpleBlockItem("painted_brick_light_gray", ModBlocks.PAINTED_BRICK_LIGHT_GRAY);
        ITEMS.registerSimpleBlockItem("painted_brick_cyan", ModBlocks.PAINTED_BRICK_CYAN);
        ITEMS.registerSimpleBlockItem("painted_brick_purple", ModBlocks.PAINTED_BRICK_PURPLE);
        ITEMS.registerSimpleBlockItem("painted_brick_blue", ModBlocks.PAINTED_BRICK_BLUE);
        ITEMS.registerSimpleBlockItem("painted_brick_brown", ModBlocks.PAINTED_BRICK_BROWN);
        ITEMS.registerSimpleBlockItem("painted_brick_green", ModBlocks.PAINTED_BRICK_GREEN);
        ITEMS.registerSimpleBlockItem("painted_brick_red", ModBlocks.PAINTED_BRICK_RED);
        ITEMS.registerSimpleBlockItem("painted_brick_black", ModBlocks.PAINTED_BRICK_BLACK);

        // Decorated painted bricks
        ITEMS.registerSimpleBlockItem("painted_brick_deco_white", ModBlocks.PAINTED_BRICK_DECO_WHITE);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_orange", ModBlocks.PAINTED_BRICK_DECO_ORANGE);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_magenta", ModBlocks.PAINTED_BRICK_DECO_MAGENTA);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_light_blue", ModBlocks.PAINTED_BRICK_DECO_LIGHT_BLUE);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_yellow", ModBlocks.PAINTED_BRICK_DECO_YELLOW);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_lime", ModBlocks.PAINTED_BRICK_DECO_LIME);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_pink", ModBlocks.PAINTED_BRICK_DECO_PINK);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_gray", ModBlocks.PAINTED_BRICK_DECO_GRAY);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_light_gray", ModBlocks.PAINTED_BRICK_DECO_LIGHT_GRAY);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_cyan", ModBlocks.PAINTED_BRICK_DECO_CYAN);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_purple", ModBlocks.PAINTED_BRICK_DECO_PURPLE);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_blue", ModBlocks.PAINTED_BRICK_DECO_BLUE);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_brown", ModBlocks.PAINTED_BRICK_DECO_BROWN);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_green", ModBlocks.PAINTED_BRICK_DECO_GREEN);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_red", ModBlocks.PAINTED_BRICK_DECO_RED);
        ITEMS.registerSimpleBlockItem("painted_brick_deco_black", ModBlocks.PAINTED_BRICK_DECO_BLACK);

        // Wood
        ITEMS.registerSimpleBlockItem("timber_frame_plain", ModBlocks.TIMBER_FRAME_PLAIN);
        ITEMS.registerSimpleBlockItem("timber_frame_cross", ModBlocks.TIMBER_FRAME_CROSS);
        ITEMS.registerSimpleBlockItem("thatch", ModBlocks.THATCH);

        // Earth
        ITEMS.registerSimpleBlockItem("mud_brick", ModBlocks.MUD_BRICK);
        ITEMS.registerSimpleBlockItem("mud_brick_extended", ModBlocks.MUD_BRICK_EXTENDED);

        // Sandstone
        ITEMS.registerSimpleBlockItem("sandstone_carved", ModBlocks.SANDSTONE_CARVED);
        ITEMS.registerSimpleBlockItem("sandstone_red_carved", ModBlocks.SANDSTONE_RED_CARVED);
        ITEMS.registerSimpleBlockItem("sandstone_ochre_carved", ModBlocks.SANDSTONE_OCHRE_CARVED);
        ITEMS.registerSimpleBlockItem("sandstone_decorated", ModBlocks.SANDSTONE_DECORATED);
        ITEMS.registerSimpleBlockItem("byzantine_stone_ornament", ModBlocks.BYZANTINE_STONE_ORNAMENT);
        ITEMS.registerSimpleBlockItem("byzantine_sandstone_ornament", ModBlocks.BYZANTINE_SANDSTONE_ORNAMENT);

        // Tiles
        ITEMS.registerSimpleBlockItem("byzantine_tiles", ModBlocks.BYZANTINE_TILES);
        ITEMS.registerSimpleBlockItem("byzantine_stone_tiles", ModBlocks.BYZANTINE_STONE_TILES);
        ITEMS.registerSimpleBlockItem("byzantine_sandstone_tiles", ModBlocks.BYZANTINE_SANDSTONE_TILES);
        ITEMS.registerSimpleBlockItem("gray_tiles", ModBlocks.GRAY_TILES);
        ITEMS.registerSimpleBlockItem("green_tiles", ModBlocks.GREEN_TILES);
        ITEMS.registerSimpleBlockItem("red_tiles", ModBlocks.RED_TILES);

        // Stairs
        ITEMS.registerSimpleBlockItem("stairs_timberframe", ModBlocks.STAIRS_TIMBERFRAME);
        ITEMS.registerSimpleBlockItem("stairs_mudbrick", ModBlocks.STAIRS_MUDBRICK);
        ITEMS.registerSimpleBlockItem("stairs_cookedbrick", ModBlocks.STAIRS_COOKEDBRICK);
        ITEMS.registerSimpleBlockItem("stairs_thatch", ModBlocks.STAIRS_THATCH);
        ITEMS.registerSimpleBlockItem("stairs_sandstone_carved", ModBlocks.STAIRS_SANDSTONE_CARVED);
        ITEMS.registerSimpleBlockItem("stairs_sandstone_red_carved", ModBlocks.STAIRS_SANDSTONE_RED_CARVED);
        ITEMS.registerSimpleBlockItem("stairs_sandstone_ochre_carved", ModBlocks.STAIRS_SANDSTONE_OCHRE_CARVED);
        ITEMS.registerSimpleBlockItem("stairs_byzantine_tiles", ModBlocks.STAIRS_BYZANTINE_TILES);
        ITEMS.registerSimpleBlockItem("stairs_gray_tiles", ModBlocks.STAIRS_GRAY_TILES);
        ITEMS.registerSimpleBlockItem("stairs_green_tiles", ModBlocks.STAIRS_GREEN_TILES);
        ITEMS.registerSimpleBlockItem("stairs_red_tiles", ModBlocks.STAIRS_RED_TILES);

        // Slabs
        ITEMS.registerSimpleBlockItem("slab_wood_deco", ModBlocks.SLAB_WOOD_DECO);
        ITEMS.registerSimpleBlockItem("slab_stone_deco", ModBlocks.SLAB_STONE_DECO);
        ITEMS.registerSimpleBlockItem("slab_sandstone_carved", ModBlocks.SLAB_SANDSTONE_CARVED);
        ITEMS.registerSimpleBlockItem("slab_sandstone_red_carved", ModBlocks.SLAB_SANDSTONE_RED_CARVED);
        ITEMS.registerSimpleBlockItem("slab_sandstone_ochre_carved", ModBlocks.SLAB_SANDSTONE_OCHRE_CARVED);
        ITEMS.registerSimpleBlockItem("slab_byzantine_tiles", ModBlocks.SLAB_BYZANTINE_TILES);
        ITEMS.registerSimpleBlockItem("slab_gray_tiles", ModBlocks.SLAB_GRAY_TILES);
        ITEMS.registerSimpleBlockItem("slab_green_tiles", ModBlocks.SLAB_GREEN_TILES);
        ITEMS.registerSimpleBlockItem("slab_red_tiles", ModBlocks.SLAB_RED_TILES);
        ITEMS.registerSimpleBlockItem("slab_path_dirt", ModBlocks.SLAB_PATH_DIRT);
        ITEMS.registerSimpleBlockItem("slab_path_gravel", ModBlocks.SLAB_PATH_GRAVEL);
        ITEMS.registerSimpleBlockItem("slab_path_slabs", ModBlocks.SLAB_PATH_SLABS);
        ITEMS.registerSimpleBlockItem("slab_path_sandstone", ModBlocks.SLAB_PATH_SANDSTONE);
        ITEMS.registerSimpleBlockItem("slab_path_gravel_slabs", ModBlocks.SLAB_PATH_GRAVEL_SLABS);
        ITEMS.registerSimpleBlockItem("slab_path_ochre_tiles", ModBlocks.SLAB_PATH_OCHRE_TILES);
        ITEMS.registerSimpleBlockItem("slab_path_snow", ModBlocks.SLAB_PATH_SNOW);

        // Walls
        ITEMS.registerSimpleBlockItem("wall_mud_brick", ModBlocks.WALL_MUD_BRICK);
        ITEMS.registerSimpleBlockItem("wall_sandstone_carved", ModBlocks.WALL_SANDSTONE_CARVED);
        ITEMS.registerSimpleBlockItem("wall_sandstone_red_carved", ModBlocks.WALL_SANDSTONE_RED_CARVED);
        ITEMS.registerSimpleBlockItem("wall_sandstone_ochre_carved", ModBlocks.WALL_SANDSTONE_OCHRE_CARVED);
        ITEMS.registerSimpleBlockItem("wall_snow", ModBlocks.WALL_SNOW);

        // Panes / Bars
        ITEMS.registerSimpleBlockItem("paper_wall", ModBlocks.PAPER_WALL);
        ITEMS.registerSimpleBlockItem("wooden_bars", ModBlocks.WOODEN_BARS);
        ITEMS.registerSimpleBlockItem("wooden_bars_indian", ModBlocks.WOODEN_BARS_INDIAN);
        ITEMS.registerSimpleBlockItem("wooden_bars_rosette", ModBlocks.WOODEN_BARS_ROSETTE);
        ITEMS.registerSimpleBlockItem("wooden_bars_dark", ModBlocks.WOODEN_BARS_DARK);

        // Functional
        ITEMS.registerSimpleBlockItem("wet_brick", ModBlocks.WET_BRICK);
        ITEMS.registerSimpleBlockItem("silk_worm", ModBlocks.SILK_WORM_BLOCK);
        ITEMS.registerSimpleBlockItem("snail_soil", ModBlocks.SNAIL_SOIL);
        ITEMS.registerSimpleBlockItem("sod", ModBlocks.SOD);
        ITEMS.registerSimpleBlockItem("alchemist_explosive", ModBlocks.ALCHEMIST_EXPLOSIVE);
        ITEMS.registerSimpleBlockItem("rosette", ModBlocks.ROSETTE);
        ITEMS.registerSimpleBlockItem("stained_glass", ModBlocks.STAINED_GLASS);
        ITEMS.registerSimpleBlockItem("mill_statue", ModBlocks.MILL_STATUE);
        ITEMS.registerSimpleBlockItem("ice_brick", ModBlocks.ICE_BRICK);
        ITEMS.registerSimpleBlockItem("snow_brick", ModBlocks.SNOW_BRICK);

        // Paths
        ITEMS.registerSimpleBlockItem("path_dirt", ModBlocks.PATH_DIRT);
        ITEMS.registerSimpleBlockItem("path_gravel", ModBlocks.PATH_GRAVEL);
        ITEMS.registerSimpleBlockItem("path_slabs", ModBlocks.PATH_SLABS);
        ITEMS.registerSimpleBlockItem("path_sandstone", ModBlocks.PATH_SANDSTONE);
        ITEMS.registerSimpleBlockItem("path_gravel_slabs", ModBlocks.PATH_GRAVEL_SLABS);
        ITEMS.registerSimpleBlockItem("path_ochre_tiles", ModBlocks.PATH_OCHRE_TILES);
        ITEMS.registerSimpleBlockItem("path_snow", ModBlocks.PATH_SNOW);

        // Saplings & Leaves
        ITEMS.registerSimpleBlockItem("sapling_appletree", ModBlocks.SAPLING_APPLE);
        ITEMS.registerSimpleBlockItem("sapling_olivetree", ModBlocks.SAPLING_OLIVE);
        ITEMS.registerSimpleBlockItem("sapling_pistachio", ModBlocks.SAPLING_PISTACHIO);
        ITEMS.registerSimpleBlockItem("sapling_cherry", ModBlocks.SAPLING_CHERRY);
        ITEMS.registerSimpleBlockItem("sapling_sakura", ModBlocks.SAPLING_SAKURA);
        ITEMS.registerSimpleBlockItem("leaves_appletree", ModBlocks.LEAVES_APPLE);
        ITEMS.registerSimpleBlockItem("leaves_olivetree", ModBlocks.LEAVES_OLIVE);
        ITEMS.registerSimpleBlockItem("leaves_pistachio", ModBlocks.LEAVES_PISTACHIO);
        ITEMS.registerSimpleBlockItem("leaves_cherry", ModBlocks.LEAVES_CHERRY);
        ITEMS.registerSimpleBlockItem("leaves_sakura", ModBlocks.LEAVES_SAKURA);

        // Special blocks
        ITEMS.registerSimpleBlockItem("locked_chest", ModBlocks.LOCKED_CHEST);
        ITEMS.registerSimpleBlockItem("fire_pit", ModBlocks.FIRE_PIT);
        ITEMS.registerSimpleBlockItem("panel", ModBlocks.PANEL);
        ITEMS.registerSimpleBlockItem("import_table", ModBlocks.IMPORT_TABLE);
        ITEMS.registerSimpleBlockItem("bed_straw", ModBlocks.BED_STRAW);
        ITEMS.registerSimpleBlockItem("bed_charpoy", ModBlocks.BED_CHARPOY);
    }

    /**
     * Helper to register an item and also add it to the ALL_ITEMS list for creative tab.
     */
    private static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> supplier) {
        DeferredItem<T> item = ITEMS.register(name, supplier);
        ALL_ITEMS.add(item);
        return item;
    }

    /**
     * Called from Millenaire2 constructor to force class loading and register all items.
     */
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    public static void init() {
        // Class loading triggers all static final fields above
    }
}

