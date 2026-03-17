package org.dizzymii.millenaire2.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.block.MillBlocks;
import org.dizzymii.millenaire2.item.MillItems;

public class MillItemModelProvider extends ItemModelProvider {

    public MillItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Millenaire2.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Currency
        basicItem(MillItems.DENIER);
        basicItem(MillItems.DENIER_ARGENT);
        basicItem(MillItems.DENIER_OR);

        // Wands
        basicItem(MillItems.SUMMONING_WAND);
        basicItem(MillItems.NEGATION_WAND);

        // Purse
        basicItem(MillItems.PURSE);

        // Food - Norman
        basicItem(MillItems.CIDER_APPLE);
        basicItem(MillItems.CIDER);
        basicItem(MillItems.CALVA);
        basicItem(MillItems.BOUDIN);
        basicItem(MillItems.TRIPES);

        // Food - Indian
        basicItem(MillItems.VEGETABLE_CURRY);
        basicItem(MillItems.CHICKEN_CURRY);
        basicItem(MillItems.RASGULLA);

        // Food - Mayan
        basicItem(MillItems.MASA);
        basicItem(MillItems.WAH);
        basicItem(MillItems.BALCHE);
        basicItem(MillItems.SIKILPAH);
        basicItem(MillItems.CACAUHAA);

        // Food - Japanese
        basicItem(MillItems.UDON);
        basicItem(MillItems.SAKE);
        basicItem(MillItems.IKAYAKI);

        // Food - Byzantine
        basicItem(MillItems.OLIVES);
        basicItem(MillItems.OLIVE_OIL);
        basicItem(MillItems.FETA);
        basicItem(MillItems.SOUVLAKI);
        basicItem(MillItems.WINE_BASIC);
        basicItem(MillItems.WINE_FANCY);

        // Food - Seljuk
        basicItem(MillItems.AYRAN);
        basicItem(MillItems.YOGURT);
        basicItem(MillItems.PIDE);
        basicItem(MillItems.LOKUM);
        basicItem(MillItems.HELVA);
        basicItem(MillItems.PISTACHIOS);

        // Food - Inuit
        basicItem(MillItems.BEARMEAT_RAW);
        basicItem(MillItems.BEARMEAT_COOKED);
        basicItem(MillItems.WOLFMEAT_RAW);
        basicItem(MillItems.WOLFMEAT_COOKED);
        basicItem(MillItems.SEAFOOD_RAW);
        basicItem(MillItems.SEAFOOD_COOKED);
        basicItem(MillItems.INUIT_BEAR_STEW);
        basicItem(MillItems.INUIT_MEATY_STEW);
        basicItem(MillItems.INUIT_POTATO_STEW);

        // Food - Misc
        basicItem(MillItems.CHERRIES);
        basicItem(MillItems.CHERRY_BLOSSOM);

        // Seeds / Crops
        basicItem(MillItems.RICE);
        basicItem(MillItems.TURMERIC);
        basicItem(MillItems.MAIZE);
        basicItem(MillItems.GRAPES);
        basicItem(MillItems.COTTON);

        // Misc Goods
        basicItem(MillItems.SILK);
        basicItem(MillItems.OBSIDIAN_FLAKE);
        basicItem(MillItems.UNKNOWN_POWDER);
        basicItem(MillItems.TANNED_HIDE);
        basicItem(MillItems.BRICK_MOULD);
        basicItem(MillItems.ULU);
        basicItem(MillItems.BANNER_PATTERN);

        // Wall Decorations
        basicItem(MillItems.TAPESTRY);
        basicItem(MillItems.INDIAN_STATUE);
        basicItem(MillItems.MAYAN_STATUE);
        basicItem(MillItems.BYZANTINE_ICON_SMALL);
        basicItem(MillItems.BYZANTINE_ICON_MEDIUM);
        basicItem(MillItems.BYZANTINE_ICON_LARGE);
        basicItem(MillItems.HIDE_HANGING);
        basicItem(MillItems.WALL_CARPET_SMALL);
        basicItem(MillItems.WALL_CARPET_MEDIUM);
        basicItem(MillItems.WALL_CARPET_LARGE);

        // Clothes
        basicItem(MillItems.CLOTHES_BYZ_WOOL);
        basicItem(MillItems.CLOTHES_BYZ_SILK);
        basicItem(MillItems.CLOTHES_SELJUK_WOOL);
        basicItem(MillItems.CLOTHES_SELJUK_COTTON);

        // Banners
        basicItem(MillItems.VILLAGE_BANNER);
        basicItem(MillItems.CULTURE_BANNER);

        // Amulets
        basicItem(MillItems.AMULET_VISHNU);
        basicItem(MillItems.AMULET_ALCHEMIST);
        basicItem(MillItems.AMULET_YGGDRASIL);
        basicItem(MillItems.AMULET_SKOLL_HATI);

        // Tools - handheld model
        handheldItem(MillItems.NORMAN_BROADSWORD);
        handheldItem(MillItems.NORMAN_AXE);
        handheldItem(MillItems.NORMAN_PICKAXE);
        handheldItem(MillItems.NORMAN_SHOVEL);
        handheldItem(MillItems.NORMAN_HOE);

        handheldItem(MillItems.MAYAN_MACE);
        handheldItem(MillItems.MAYAN_AXE);
        handheldItem(MillItems.MAYAN_PICKAXE);
        handheldItem(MillItems.MAYAN_SHOVEL);
        handheldItem(MillItems.MAYAN_HOE);

        handheldItem(MillItems.BYZANTINE_MACE);
        handheldItem(MillItems.BYZANTINE_AXE);
        handheldItem(MillItems.BYZANTINE_PICKAXE);
        handheldItem(MillItems.BYZANTINE_SHOVEL);
        handheldItem(MillItems.BYZANTINE_HOE);

        handheldItem(MillItems.TACHI_SWORD);
        handheldItem(MillItems.SELJUK_SCIMITAR);
        handheldItem(MillItems.INUIT_TRIDENT);

        // Bows
        basicItem(MillItems.YUMI_BOW);
        basicItem(MillItems.INUIT_BOW);
        basicItem(MillItems.SELJUK_BOW);

        // Armor
        basicItem(MillItems.NORMAN_HELMET);
        basicItem(MillItems.NORMAN_CHESTPLATE);
        basicItem(MillItems.NORMAN_LEGGINGS);
        basicItem(MillItems.NORMAN_BOOTS);

        basicItem(MillItems.JAPANESE_RED_HELMET);
        basicItem(MillItems.JAPANESE_RED_CHESTPLATE);
        basicItem(MillItems.JAPANESE_RED_LEGGINGS);
        basicItem(MillItems.JAPANESE_RED_BOOTS);

        basicItem(MillItems.JAPANESE_BLUE_HELMET);
        basicItem(MillItems.JAPANESE_BLUE_CHESTPLATE);
        basicItem(MillItems.JAPANESE_BLUE_LEGGINGS);
        basicItem(MillItems.JAPANESE_BLUE_BOOTS);

        basicItem(MillItems.JAPANESE_GUARD_HELMET);
        basicItem(MillItems.JAPANESE_GUARD_CHESTPLATE);
        basicItem(MillItems.JAPANESE_GUARD_LEGGINGS);
        basicItem(MillItems.JAPANESE_GUARD_BOOTS);

        basicItem(MillItems.BYZANTINE_HELMET);
        basicItem(MillItems.BYZANTINE_CHESTPLATE);
        basicItem(MillItems.BYZANTINE_LEGGINGS);
        basicItem(MillItems.BYZANTINE_BOOTS);

        basicItem(MillItems.FUR_HELMET);
        basicItem(MillItems.FUR_CHESTPLATE);
        basicItem(MillItems.FUR_LEGGINGS);
        basicItem(MillItems.FUR_BOOTS);

        basicItem(MillItems.SELJUK_TURBAN);
        basicItem(MillItems.SELJUK_HELMET);
        basicItem(MillItems.SELJUK_CHESTPLATE);
        basicItem(MillItems.SELJUK_LEGGINGS);
        basicItem(MillItems.SELJUK_BOOTS);

        basicItem(MillItems.MAYAN_QUEST_CROWN);

        // Parchments
        basicItem(MillItems.PARCHMENT_NORMAN_VILLAGERS);
        basicItem(MillItems.PARCHMENT_NORMAN_BUILDINGS);
        basicItem(MillItems.PARCHMENT_NORMAN_ITEMS);
        basicItem(MillItems.PARCHMENT_NORMAN_COMPLETE);
        basicItem(MillItems.PARCHMENT_INDIAN_VILLAGERS);
        basicItem(MillItems.PARCHMENT_INDIAN_BUILDINGS);
        basicItem(MillItems.PARCHMENT_INDIAN_ITEMS);
        basicItem(MillItems.PARCHMENT_INDIAN_COMPLETE);
        basicItem(MillItems.PARCHMENT_MAYAN_VILLAGERS);
        basicItem(MillItems.PARCHMENT_MAYAN_BUILDINGS);
        basicItem(MillItems.PARCHMENT_MAYAN_ITEMS);
        basicItem(MillItems.PARCHMENT_MAYAN_COMPLETE);
        basicItem(MillItems.PARCHMENT_JAPANESE_VILLAGERS);
        basicItem(MillItems.PARCHMENT_JAPANESE_BUILDINGS);
        basicItem(MillItems.PARCHMENT_JAPANESE_ITEMS);
        basicItem(MillItems.PARCHMENT_JAPANESE_COMPLETE);
        basicItem(MillItems.PARCHMENT_VILLAGE_SCROLL);
        basicItem(MillItems.PARCHMENT_SADHU);

        // Sapling items (flat texture, not block model)
        basicItem(MillBlocks.SAPLING_APPLE.getId().getPath(),
                modLoc("block/sapling_appletree"));
        basicItem(MillBlocks.SAPLING_OLIVE.getId().getPath(),
                modLoc("block/sapling_olivetree"));
        basicItem(MillBlocks.SAPLING_PISTACHIO.getId().getPath(),
                modLoc("block/sapling_pistachio"));
        basicItem(MillBlocks.SAPLING_CHERRY.getId().getPath(),
                modLoc("block/sapling_cherry"));
        basicItem(MillBlocks.SAPLING_SAKURA.getId().getPath(),
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
