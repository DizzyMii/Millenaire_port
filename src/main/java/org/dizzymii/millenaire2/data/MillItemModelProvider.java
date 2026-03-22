package org.dizzymii.millenaire2.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.init.ModBlocks;
import org.dizzymii.millenaire2.init.ModItems;

public class MillItemModelProvider extends ItemModelProvider {

    public MillItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Millenaire2.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Currency
        basicItem(ModItems.DENIER);
        basicItem(ModItems.DENIER_ARGENT);
        basicItem(ModItems.DENIER_OR);

        // Wands
        basicItem(ModItems.SUMMONING_WAND);
        basicItem(ModItems.NEGATION_WAND);

        // Purse
        basicItem(ModItems.PURSE);

        // Food - Norman
        basicItem(ModItems.CIDER_APPLE);
        basicItem(ModItems.CIDER);
        basicItem(ModItems.CALVA);
        basicItem(ModItems.BOUDIN);
        basicItem(ModItems.TRIPES);

        // Food - Indian
        basicItem(ModItems.VEGETABLE_CURRY);
        basicItem(ModItems.CHICKEN_CURRY);
        basicItem(ModItems.RASGULLA);

        // Food - Mayan
        basicItem(ModItems.MASA);
        basicItem(ModItems.WAH);
        basicItem(ModItems.BALCHE);
        basicItem(ModItems.SIKILPAH);
        basicItem(ModItems.CACAUHAA);

        // Food - Japanese
        basicItem(ModItems.UDON);
        basicItem(ModItems.SAKE);
        basicItem(ModItems.IKAYAKI);

        // Food - Byzantine
        basicItem(ModItems.OLIVES);
        basicItem(ModItems.OLIVE_OIL);
        basicItem(ModItems.FETA);
        basicItem(ModItems.SOUVLAKI);
        basicItem(ModItems.WINE_BASIC);
        basicItem(ModItems.WINE_FANCY);

        // Food - Seljuk
        basicItem(ModItems.AYRAN);
        basicItem(ModItems.YOGURT);
        basicItem(ModItems.PIDE);
        basicItem(ModItems.LOKUM);
        basicItem(ModItems.HELVA);
        basicItem(ModItems.PISTACHIOS);

        // Food - Inuit
        basicItem(ModItems.BEARMEAT_RAW);
        basicItem(ModItems.BEARMEAT_COOKED);
        basicItem(ModItems.WOLFMEAT_RAW);
        basicItem(ModItems.WOLFMEAT_COOKED);
        basicItem(ModItems.SEAFOOD_RAW);
        basicItem(ModItems.SEAFOOD_COOKED);
        basicItem(ModItems.INUIT_BEAR_STEW);
        basicItem(ModItems.INUIT_MEATY_STEW);
        basicItem(ModItems.INUIT_POTATO_STEW);

        // Food - Misc
        basicItem(ModItems.CHERRIES);
        basicItem(ModItems.CHERRY_BLOSSOM);

        // Seeds / Crops
        basicItem(ModItems.RICE);
        basicItem(ModItems.TURMERIC);
        basicItem(ModItems.MAIZE);
        basicItem(ModItems.GRAPES);
        basicItem(ModItems.COTTON);

        // Misc Goods
        basicItem(ModItems.SILK);
        basicItem(ModItems.OBSIDIAN_FLAKE);
        basicItem(ModItems.UNKNOWN_POWDER);
        basicItem(ModItems.TANNED_HIDE);
        basicItem(ModItems.BRICK_MOULD);
        basicItem(ModItems.ULU);
        basicItem(ModItems.BANNER_PATTERN);

        // Wall Decorations
        basicItem(ModItems.TAPESTRY);
        basicItem(ModItems.INDIAN_STATUE);
        basicItem(ModItems.MAYAN_STATUE);
        basicItem(ModItems.BYZANTINE_ICON_SMALL);
        basicItem(ModItems.BYZANTINE_ICON_MEDIUM);
        basicItem(ModItems.BYZANTINE_ICON_LARGE);
        basicItem(ModItems.HIDE_HANGING);
        basicItem(ModItems.WALL_CARPET_SMALL);
        basicItem(ModItems.WALL_CARPET_MEDIUM);
        basicItem(ModItems.WALL_CARPET_LARGE);

        // Clothes
        basicItem(ModItems.CLOTHES_BYZ_WOOL);
        basicItem(ModItems.CLOTHES_BYZ_SILK);
        basicItem(ModItems.CLOTHES_SELJUK_WOOL);
        basicItem(ModItems.CLOTHES_SELJUK_COTTON);

        // Banners
        basicItem(ModItems.VILLAGE_BANNER);
        basicItem(ModItems.CULTURE_BANNER);

        // Amulets
        basicItem(ModItems.AMULET_VISHNU);
        basicItem(ModItems.AMULET_ALCHEMIST);
        basicItem(ModItems.AMULET_YGGDRASIL);
        basicItem(ModItems.AMULET_SKOLL_HATI);

        // Tools - handheld model
        handheldItem(ModItems.NORMAN_BROADSWORD);
        handheldItem(ModItems.NORMAN_AXE);
        handheldItem(ModItems.NORMAN_PICKAXE);
        handheldItem(ModItems.NORMAN_SHOVEL);
        handheldItem(ModItems.NORMAN_HOE);

        handheldItem(ModItems.MAYAN_MACE);
        handheldItem(ModItems.MAYAN_AXE);
        handheldItem(ModItems.MAYAN_PICKAXE);
        handheldItem(ModItems.MAYAN_SHOVEL);
        handheldItem(ModItems.MAYAN_HOE);

        handheldItem(ModItems.BYZANTINE_MACE);
        handheldItem(ModItems.BYZANTINE_AXE);
        handheldItem(ModItems.BYZANTINE_PICKAXE);
        handheldItem(ModItems.BYZANTINE_SHOVEL);
        handheldItem(ModItems.BYZANTINE_HOE);

        handheldItem(ModItems.TACHI_SWORD);
        handheldItem(ModItems.SELJUK_SCIMITAR);
        handheldItem(ModItems.INUIT_TRIDENT);

        // Bows
        basicItem(ModItems.YUMI_BOW);
        basicItem(ModItems.INUIT_BOW);
        basicItem(ModItems.SELJUK_BOW);

        // Armor
        basicItem(ModItems.NORMAN_HELMET);
        basicItem(ModItems.NORMAN_CHESTPLATE);
        basicItem(ModItems.NORMAN_LEGGINGS);
        basicItem(ModItems.NORMAN_BOOTS);

        basicItem(ModItems.JAPANESE_RED_HELMET);
        basicItem(ModItems.JAPANESE_RED_CHESTPLATE);
        basicItem(ModItems.JAPANESE_RED_LEGGINGS);
        basicItem(ModItems.JAPANESE_RED_BOOTS);

        basicItem(ModItems.JAPANESE_BLUE_HELMET);
        basicItem(ModItems.JAPANESE_BLUE_CHESTPLATE);
        basicItem(ModItems.JAPANESE_BLUE_LEGGINGS);
        basicItem(ModItems.JAPANESE_BLUE_BOOTS);

        basicItem(ModItems.JAPANESE_GUARD_HELMET);
        basicItem(ModItems.JAPANESE_GUARD_CHESTPLATE);
        basicItem(ModItems.JAPANESE_GUARD_LEGGINGS);
        basicItem(ModItems.JAPANESE_GUARD_BOOTS);

        basicItem(ModItems.BYZANTINE_HELMET);
        basicItem(ModItems.BYZANTINE_CHESTPLATE);
        basicItem(ModItems.BYZANTINE_LEGGINGS);
        basicItem(ModItems.BYZANTINE_BOOTS);

        basicItem(ModItems.FUR_HELMET);
        basicItem(ModItems.FUR_CHESTPLATE);
        basicItem(ModItems.FUR_LEGGINGS);
        basicItem(ModItems.FUR_BOOTS);

        basicItem(ModItems.SELJUK_TURBAN);
        basicItem(ModItems.SELJUK_HELMET);
        basicItem(ModItems.SELJUK_CHESTPLATE);
        basicItem(ModItems.SELJUK_LEGGINGS);
        basicItem(ModItems.SELJUK_BOOTS);

        basicItem(ModItems.MAYAN_QUEST_CROWN);

        // Parchments
        basicItem(ModItems.PARCHMENT_NORMAN_VILLAGERS);
        basicItem(ModItems.PARCHMENT_NORMAN_BUILDINGS);
        basicItem(ModItems.PARCHMENT_NORMAN_ITEMS);
        basicItem(ModItems.PARCHMENT_NORMAN_COMPLETE);
        basicItem(ModItems.PARCHMENT_INDIAN_VILLAGERS);
        basicItem(ModItems.PARCHMENT_INDIAN_BUILDINGS);
        basicItem(ModItems.PARCHMENT_INDIAN_ITEMS);
        basicItem(ModItems.PARCHMENT_INDIAN_COMPLETE);
        basicItem(ModItems.PARCHMENT_MAYAN_VILLAGERS);
        basicItem(ModItems.PARCHMENT_MAYAN_BUILDINGS);
        basicItem(ModItems.PARCHMENT_MAYAN_ITEMS);
        basicItem(ModItems.PARCHMENT_MAYAN_COMPLETE);
        basicItem(ModItems.PARCHMENT_JAPANESE_VILLAGERS);
        basicItem(ModItems.PARCHMENT_JAPANESE_BUILDINGS);
        basicItem(ModItems.PARCHMENT_JAPANESE_ITEMS);
        basicItem(ModItems.PARCHMENT_JAPANESE_COMPLETE);
        basicItem(ModItems.PARCHMENT_VILLAGE_SCROLL);
        basicItem(ModItems.PARCHMENT_SADHU);

        // Sapling items (flat texture, not block model)
        basicItem(ModBlocks.SAPLING_APPLE.getId().getPath(),
                modLoc("block/sapling_appletree"));
        basicItem(ModBlocks.SAPLING_OLIVE.getId().getPath(),
                modLoc("block/sapling_olivetree"));
        basicItem(ModBlocks.SAPLING_PISTACHIO.getId().getPath(),
                modLoc("block/sapling_pistachio"));
        basicItem(ModBlocks.SAPLING_CHERRY.getId().getPath(),
                modLoc("block/sapling_cherry"));
        basicItem(ModBlocks.SAPLING_SAKURA.getId().getPath(),
                modLoc("block/sapling_sakura"));
    }

    // ===== Helpers =====

    private void basicItem(DeferredItem<? extends Item> item) {
        basicItem(item.get());
    }

    private void handheldItem(DeferredItem<? extends Item> item) {
        withExistingParent(item.getId().getPath(), mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/" + item.getId().getPath()));
    }

    private void basicItem(String name, net.minecraft.resources.ResourceLocation texture) {
        withExistingParent(name, mcLoc("item/generated"))
                .texture("layer0", texture);
    }
}

