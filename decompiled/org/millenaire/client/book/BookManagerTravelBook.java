/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockChest
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.BlockSapling
 *  net.minecraft.block.BlockStone
 *  net.minecraft.block.BlockStone$EnumType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemSpade
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.item.ItemTool
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.biome.Biome
 *  org.apache.commons.lang3.text.WordUtils
 */
package org.millenaire.client.book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockStone;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.text.WordUtils;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.book.TextPage;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.gui.text.GuiTravelBook;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.buildingplan.PointType;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.entity.VillagerConfig;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.goal.generic.GoalGenericCrafting;
import org.millenaire.common.goal.generic.GoalGenericHarvestCrop;
import org.millenaire.common.goal.generic.GoalGenericMining;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.ItemFoodMultiple;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.world.UserProfile;

public class BookManagerTravelBook
extends BookManager {
    private static final int VILLAGER_PICTURE_OFFSET = 80;
    private static final String EXPORT_TAG_MAIN_DESC = "MAIN_DESC";
    private static final String[] VILLAGER_TAGS_TO_DISPLAY = new String[]{"chief", "foreignmerchant", "localmerchant", "helpinattacks", "hostile", "raider", "archer"};
    private static final Map<String, ItemStack> VILLAGER_TAGS_ICONS = new HashMap<String, ItemStack>(){
        private static final long serialVersionUID = 1L;
        {
            this.put("chief", new ItemStack((Item)Items.field_151028_Y, 1));
            this.put("foreignmerchant", new ItemStack((Item)MillItems.PURSE, 1));
            this.put("localmerchant", new ItemStack((Block)Blocks.field_150486_ae, 1));
            this.put("helpinattacks", new ItemStack(Items.field_185159_cQ, 1));
            this.put("hostile", new ItemStack(Items.field_179564_cE, 1));
            this.put("raider", new ItemStack((Item)MillItems.NORMAN_AXE, 1));
            this.put("archer", new ItemStack((Item)Items.field_151031_f, 1));
        }
    };
    private static final String[] BUILDING_TAGS_TO_DISPLAY = new String[]{"archives", "hof", "leasure", "pujas", "sacrifices"};
    private static final Map<String, ItemStack> BUILDING_TAGS_ICONS = new HashMap<String, ItemStack>(){
        private static final long serialVersionUID = 1L;
        {
            this.put("archives", new ItemStack(Items.field_151155_ap, 1));
            this.put("hof", new ItemStack(Items.field_151155_ap, 1));
            this.put("leasure", new ItemStack((Item)MillItems.CIDER, 1));
            this.put("pujas", new ItemStack((Item)MillItems.INDIAN_STATUE, 1));
            this.put("sacrifices", new ItemStack((Item)MillItems.MAYAN_STATUE, 1));
        }
    };

    private static <T> void incrementMap(Map<T, Integer> map, T key, int value) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + value);
        } else {
            map.put(key, value);
        }
    }

    public BookManagerTravelBook(int xSize, int ySize, int textHeight, int lineSizeInPx, BookManager.IFontRendererWrapper fontRenderer) {
        super(xSize, ySize, textHeight, lineSizeInPx, fontRenderer);
    }

    public TextBook getBookBuildingDetail(Culture culture, String itemKey, UserProfile profile) {
        boolean displayFullInfos;
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        BuildingPlanSet planSet = culture.getBuildingPlanSet(itemKey);
        TextLine line = new TextLine(planSet.getNameNativeAndTranslated(), "\u00a71", planSet.getIcon(), false);
        page.addLine(line);
        page.getLastLine().canCutAfter = false;
        page.addBlankLine();
        page.getLastLine().canCutAfter = false;
        boolean knownBuilding = profile == null || profile.isBuildingUnlocked(culture, planSet);
        boolean bl = displayFullInfos = knownBuilding || !MillConfigValues.TRAVEL_BOOK_LEARNING;
        if (!knownBuilding) {
            page.addLine(new TextLine(LanguageUtilities.string("travelbook.unknownbuilding"), "\u00a74"));
            page.addBlankLine();
        }
        if (displayFullInfos) {
            if (planSet.getFirstStartingPlan().isSubBuilding) {
                if (planSet.getFirstStartingPlan().parentBuildingPlan != null && culture.getBuildingPlan(planSet.getFirstStartingPlan().parentBuildingPlan) != null) {
                    BuildingPlan parentPlan = culture.getBuildingPlan(planSet.getFirstStartingPlan().parentBuildingPlan);
                    BuildingPlanSet parentSet = culture.getBuildingPlanSet(parentPlan.buildingKey);
                    page.addLine(LanguageUtilities.string("travelbook.subbuildingof", parentPlan.getNameNativeAndTranslated()), "\u00a74", new GuiText.GuiButtonReference(parentSet));
                } else {
                    page.addLine(LanguageUtilities.string("travelbook.subbuilding"), "\u00a74");
                }
                page.addBlankLine();
            }
            if (culture.hasCultureString("travelbook.building." + planSet.key + ".desc")) {
                page.addLine(culture.getCultureString("travelbook.building." + planSet.key + ".desc"));
                page.getLastLine().exportSpecialTag = EXPORT_TAG_MAIN_DESC;
                page.addBlankLine();
            }
            for (int variation = 0; variation < planSet.plans.size(); ++variation) {
                this.getBookBuildingDetail_exportVariation(culture, page, planSet, variation);
            }
        }
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        for (VillageType village : culture.listVillageTypes) {
            if (village.centreBuilding != planSet && !village.startBuildings.contains(planSet) && !village.coreBuildings.contains(planSet) && !village.secondaryBuildings.contains(planSet) && !village.extraBuildings.contains(planSet)) continue;
            infoColumns.add(new TextLine(village.name, new GuiText.GuiButtonReference(village)));
        }
        if (infoColumns.size() > 0) {
            page.addBlankLine();
            page.addLine(LanguageUtilities.string("travelbook.villageswithbuilding"), "\u00a71");
            page.getLastLine().canCutAfter = false;
            List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
            for (TextLine l : linesWithColumns) {
                page.addLine(l);
            }
            page.addBlankLine();
        }
        book.addPage(page);
        book = this.offsetFirstLines(book);
        return book;
    }

    private void getBookBuildingDetail_exportVariation(Culture culture, TextPage page, BuildingPlanSet planSet, int variation) {
        if (planSet.plans.size() > 1) {
            page.addLine(LanguageUtilities.string("travelbook.variation", "" + (char)(65 + variation)), "\u00a71");
            page.getLastLine().canCutAfter = false;
            page.addBlankLine();
            page.getLastLine().canCutAfter = false;
        }
        BuildingPlan plan = planSet.getPlan(variation, 0);
        this.getBookBuildingDetail_exportVariationBasicInfos(culture, page, plan);
        for (int level = 0; level < planSet.plans.get(variation).length; ++level) {
            this.getBookBuildingDetail_exportVariationLevel(culture, page, planSet, variation, level);
        }
        page.addBlankLine();
    }

    private void getBookBuildingDetail_exportVariationBasicInfos(Culture culture, TextPage page, BuildingPlan plan) {
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        infoColumns.add(new TextLine(plan.length + "x" + plan.width, new ItemStack((Block)Blocks.field_150349_c, 1), LanguageUtilities.string("travelbook.building_size"), false));
        if (plan.shop != null) {
            infoColumns.add(new TextLine(culture.getCultureString("shop." + plan.shop), new ItemStack((Item)MillItems.PURSE, 1), LanguageUtilities.string("travelbook.building_shop"), false));
        }
        if (plan.price > 0) {
            String[] priceText = MillCommonUtilities.getShortPrice(plan.price);
            infoColumns.add(new TextLine((String)priceText, new ItemStack((Item)MillItems.DENIER_OR, 1), LanguageUtilities.string("travelbook.building_cost"), false));
        }
        if (plan.reputation > 0) {
            infoColumns.add(new TextLine("" + plan.reputation, new ItemStack((Block)Blocks.field_150328_O, 1), LanguageUtilities.string("travelbook.building_reputation"), false));
        }
        if (plan.isgift) {
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.building_gift"), new ItemStack(Items.field_151105_aU, 1), LanguageUtilities.string("travelbook.building_gift_desc"), false));
        }
        for (String tag : BUILDING_TAGS_TO_DISPLAY) {
            if (!plan.containsTags(tag)) continue;
            TextLine col1 = new TextLine(LanguageUtilities.string("travelbook.specialbuildingtag." + tag), BUILDING_TAGS_ICONS.get(tag), LanguageUtilities.string("travelbook.specialbuildingtag." + tag + ".desc"), false);
            infoColumns.add(col1);
        }
        List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : linesWithColumns) {
            page.addLine(l);
        }
        page.addBlankLine();
        if (plan.maleResident.size() > 0 || plan.femaleResident.size() > 0) {
            VillagerType resident;
            infoColumns = new ArrayList();
            page.addLine(LanguageUtilities.string("travelbook.residents"), TextLine.ITALIC);
            page.getLastLine().canCutAfter = false;
            for (String maleResident : plan.maleResident) {
                resident = culture.getVillagerType(maleResident);
                if (resident == null) continue;
                infoColumns.add(new TextLine(resident.name, new GuiText.GuiButtonReference(resident)));
            }
            for (String femaleResident : plan.femaleResident) {
                resident = culture.getVillagerType(femaleResident);
                if (resident == null) continue;
                infoColumns.add(new TextLine(resident.name, new GuiText.GuiButtonReference(resident)));
            }
            linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
            for (TextLine l : linesWithColumns) {
                page.addLine(l);
            }
            page.addBlankLine();
        }
        if (plan.startingSubBuildings.size() > 0) {
            page.addLine(LanguageUtilities.string("travelbook.startingsubbuildings"), TextLine.ITALIC);
            page.getLastLine().canCutAfter = false;
            infoColumns = new ArrayList();
            for (String subBuildingKey : plan.startingSubBuildings) {
                BuildingPlanSet subBuildingSet = culture.getBuildingPlanSet(subBuildingKey);
                infoColumns.add(new TextLine(subBuildingSet.getNameNative(), new GuiText.GuiButtonReference(subBuildingSet)));
            }
            linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
            for (TextLine l : linesWithColumns) {
                page.addLine(l);
            }
        }
        if (plan.startingGoods.size() > 0) {
            page.addLine(LanguageUtilities.string("travelbook.startinggoods"), TextLine.ITALIC);
            page.getLastLine().canCutAfter = false;
            infoColumns = new ArrayList();
            for (BuildingPlan.StartingGood good : plan.startingGoods) {
                int min = good.probability >= 1.0 ? good.fixedNumber : 0;
                int max = good.fixedNumber + good.randomNumber;
                infoColumns.add(new TextLine(min + "-" + max, good.item.getItemStack(), true));
            }
            linesWithColumns = BookManager.splitInColumns(infoColumns, 4);
            for (TextLine l : linesWithColumns) {
                page.addLine(l);
            }
        }
    }

    private void getBookBuildingDetail_exportVariationLevel(Culture culture, TextPage page, BuildingPlanSet planSet, int variation, int level) {
        List<TextLine> linesWithColumns;
        BuildingPlan plan = planSet.getPlan(variation, level);
        BuildingPlan previousPlan = null;
        if (level > 0) {
            previousPlan = planSet.getPlan(variation, level - 1);
        }
        if (level == 0) {
            page.addLine(LanguageUtilities.string("travelbook.initial"), "\u00a71");
        } else {
            page.addLine(LanguageUtilities.string("travelbook.upgrade", "" + level), "\u00a71");
        }
        page.getLastLine().canCutAfter = false;
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        if (plan.shop != null && level > 0 && previousPlan.shop == null) {
            infoColumns.add(new TextLine(culture.getCultureString("shop." + plan.shop), new ItemStack((Item)MillItems.PURSE, 1), LanguageUtilities.string("travelbook.building_shop"), false));
        }
        if (plan.irrigation > 0 && level > 0 && previousPlan.irrigation != plan.irrigation) {
            infoColumns.add(new TextLine("+" + plan.irrigation + "%", new ItemStack(Items.field_151131_as, 1), LanguageUtilities.string("effect.irrigation", "" + plan.irrigation), false));
        }
        if (plan.extraSimultaneousConstructions > 0 && level > 0 && previousPlan.extraSimultaneousConstructions != plan.extraSimultaneousConstructions) {
            infoColumns.add(new TextLine("+" + plan.extraSimultaneousConstructions, new ItemStack(Items.field_151037_a, 1), LanguageUtilities.string("effect.extraconstructionslot", "" + plan.extraSimultaneousConstructions), false));
        }
        if (plan.extraSimultaneousWallConstructions > 0 && level > 0 && previousPlan.extraSimultaneousWallConstructions != plan.extraSimultaneousWallConstructions) {
            infoColumns.add(new TextLine("+" + plan.extraSimultaneousWallConstructions, new ItemStack(Blocks.field_150463_bK, 1), LanguageUtilities.string("effect.extrawallconstructionslot", "" + plan.extraSimultaneousWallConstructions), false));
        }
        this.getBookBuildingDetail_loadInfosFromBlocks(plan, infoColumns);
        if (infoColumns.size() > 0) {
            page.addLine(LanguageUtilities.string("travelbook.features"), TextLine.ITALIC);
            page.getLastLine().canCutAfter = false;
            linesWithColumns = BookManager.splitInColumns(infoColumns, 6);
            for (TextLine l : linesWithColumns) {
                page.addLine(l);
            }
        }
        if (plan.subBuildings.size() > 0 && (previousPlan == null || previousPlan.subBuildings.size() < plan.subBuildings.size())) {
            infoColumns = new ArrayList();
            for (String subBuildingKey : plan.subBuildings) {
                if (previousPlan != null && previousPlan.subBuildings.contains(subBuildingKey)) continue;
                BuildingPlanSet subBuildingSet = culture.getBuildingPlanSet(subBuildingKey);
                infoColumns.add(new TextLine(subBuildingSet.getNameNative(), new GuiText.GuiButtonReference(subBuildingSet)));
            }
            if (infoColumns.size() > 0) {
                page.addLine(LanguageUtilities.string("travelbook.subbuildings"), TextLine.ITALIC);
                page.getLastLine().canCutAfter = false;
                linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
                for (TextLine l : linesWithColumns) {
                    page.addLine(l);
                }
                page.addBlankLine();
            }
        }
        if (plan.visitors.size() > 0 && (previousPlan == null || previousPlan.visitors.size() < plan.visitors.size())) {
            infoColumns = new ArrayList();
            for (String visitor : plan.visitors) {
                VillagerType visitorType;
                if (previousPlan != null && previousPlan.visitors.contains(visitor) || (visitorType = culture.getVillagerType(visitor)) == null) continue;
                infoColumns.add(new TextLine(visitorType.name, new GuiText.GuiButtonReference(visitorType)));
            }
            if (infoColumns.size() > 0) {
                page.addLine(LanguageUtilities.string("travelbook.visitors"), TextLine.ITALIC);
                page.getLastLine().canCutAfter = false;
                linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
                for (TextLine l : linesWithColumns) {
                    page.addLine(l);
                }
                page.addBlankLine();
            }
        }
        page.addLine(LanguageUtilities.string("travelbook.cost"), TextLine.ITALIC);
        page.getLastLine().canCutAfter = false;
        ArrayList<TextLine> costColumns = new ArrayList<TextLine>();
        List costKeys = plan.resCost.keySet().stream().sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).collect(Collectors.toList());
        for (InvItem key : costKeys) {
            if (culture.getTradeGood(key) != null) {
                costColumns.add(new TextLine("" + plan.resCost.get(key), new GuiText.GuiButtonReference(culture.getTradeGood(key))));
                continue;
            }
            costColumns.add(new TextLine("" + plan.resCost.get(key), key.getItemStack(), true));
        }
        linesWithColumns = BookManager.splitInColumns(costColumns, 5);
        for (TextLine l : linesWithColumns) {
            page.addLine(l);
        }
        page.addBlankLine();
    }

    private void getBookBuildingDetail_loadInfosFromBlocks(BuildingPlan plan, List<TextLine> infoColumns) {
        int nbChests = 0;
        int nbFishingSpot = 0;
        int nbFurnace = 0;
        int nbFirePits = 0;
        HashMap plantingSpot = new HashMap();
        HashMap resourceSpot = new HashMap();
        HashMap spawnSpot = new HashMap();
        for (int y = 0; y < plan.plan.length; ++y) {
            for (int x = 0; x < plan.plan[y].length; ++x) {
                for (int z = 0; z < plan.plan[y][x].length; ++z) {
                    PointType pt = plan.plan[y][x][z];
                    if (pt.getBlock() instanceof BlockChest) {
                        ++nbChests;
                        continue;
                    }
                    if (pt.getBlock() == Blocks.field_150460_al) {
                        ++nbFurnace;
                        continue;
                    }
                    if (pt.getBlock() == MillBlocks.FIRE_PIT) {
                        ++nbFirePits;
                        continue;
                    }
                    if (pt.getSpecialType() == null) continue;
                    if (pt.isSubType("lockedchest") || pt.isSubType("mainchest")) {
                        ++nbChests;
                        continue;
                    }
                    if (pt.isType("fishingspot")) {
                        ++nbFishingSpot;
                        continue;
                    }
                    if (pt.isType("furnaceGuess")) {
                        ++nbFurnace;
                        continue;
                    }
                    if (pt.isType("soil")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(Items.field_151015_O), 1);
                        continue;
                    }
                    if (pt.isType("ricesoil")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(MillItems.RICE), 1);
                        continue;
                    }
                    if (pt.isType("turmericsoil")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(MillItems.TURMERIC), 1);
                        continue;
                    }
                    if (pt.isType("maizesoil")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(MillItems.MAIZE), 1);
                        continue;
                    }
                    if (pt.isType("carrotsoil")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(Items.field_151172_bF), 1);
                        continue;
                    }
                    if (pt.isType("potatosoil")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(Items.field_151174_bG), 1);
                        continue;
                    }
                    if (pt.isType("flowersoil")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem((Block)Blocks.field_150328_O), 1);
                        continue;
                    }
                    if (pt.isType("sugarcanesoil")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(Items.field_151120_aE), 1);
                        continue;
                    }
                    if (pt.isType("netherwartsoil")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(Items.field_151075_bm), 1);
                        continue;
                    }
                    if (pt.isType("vinesoil")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(MillItems.GRAPES), 1);
                        continue;
                    }
                    if (pt.isType("cacaospot")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(Items.field_151100_aR, 3), 1);
                        continue;
                    }
                    if (pt.isType("oakspawn")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(Blocks.field_150345_g.func_176223_P().func_177226_a((IProperty)BlockSapling.field_176480_a, (Comparable)BlockPlanks.EnumType.OAK)), 1);
                        continue;
                    }
                    if (pt.isType("pinespawn")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(Blocks.field_150345_g.func_176223_P().func_177226_a((IProperty)BlockSapling.field_176480_a, (Comparable)BlockPlanks.EnumType.SPRUCE)), 1);
                        continue;
                    }
                    if (pt.isType("birchspawn")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(Blocks.field_150345_g.func_176223_P().func_177226_a((IProperty)BlockSapling.field_176480_a, (Comparable)BlockPlanks.EnumType.BIRCH)), 1);
                        continue;
                    }
                    if (pt.isType("junglespawn")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(Blocks.field_150345_g.func_176223_P().func_177226_a((IProperty)BlockSapling.field_176480_a, (Comparable)BlockPlanks.EnumType.JUNGLE)), 1);
                        continue;
                    }
                    if (pt.isType("acaciaspawn")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(Blocks.field_150345_g.func_176223_P().func_177226_a((IProperty)BlockSapling.field_176480_a, (Comparable)BlockPlanks.EnumType.ACACIA)), 1);
                        continue;
                    }
                    if (pt.isType("darkoakspawn")) {
                        BookManagerTravelBook.incrementMap(plantingSpot, InvItem.createInvItem(Blocks.field_150345_g.func_176223_P().func_177226_a((IProperty)BlockSapling.field_176480_a, (Comparable)BlockPlanks.EnumType.DARK_OAK)), 1);
                        continue;
                    }
                    if (pt.isType("chickenspawn")) {
                        BookManagerTravelBook.incrementMap(spawnSpot, InvItem.createInvItem(Items.field_151110_aK), 1);
                        continue;
                    }
                    if (pt.isType("cowspawn")) {
                        BookManagerTravelBook.incrementMap(spawnSpot, InvItem.createInvItem(Items.field_151082_bd), 1);
                        continue;
                    }
                    if (pt.isType("pigspawn")) {
                        BookManagerTravelBook.incrementMap(spawnSpot, InvItem.createInvItem(Items.field_151147_al), 1);
                        continue;
                    }
                    if (pt.isType("squidspawn")) {
                        BookManagerTravelBook.incrementMap(spawnSpot, InvItem.createInvItem(Items.field_151100_aR, 0), 1);
                        continue;
                    }
                    if (pt.isType("sheepspawn")) {
                        BookManagerTravelBook.incrementMap(spawnSpot, InvItem.createInvItem(Blocks.field_150325_L), 1);
                        continue;
                    }
                    if (pt.isType("wolfspawn")) {
                        BookManagerTravelBook.incrementMap(spawnSpot, InvItem.createInvItem(Items.field_151103_aS), 1);
                        continue;
                    }
                    if (pt.isType("silkwormblock")) {
                        BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem(MillItems.SILK), 1);
                        continue;
                    }
                    if (pt.isType("brickspot")) {
                        BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem(MillBlocks.BS_MUD_BRICK), 1);
                        continue;
                    }
                    if (pt.isType("stonesource")) {
                        BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem(Blocks.field_150348_b), 1);
                        continue;
                    }
                    if (pt.isType("sandsource")) {
                        BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem((Block)Blocks.field_150354_m), 1);
                        continue;
                    }
                    if (pt.isType("sandstonesource")) {
                        BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem(Blocks.field_150322_A), 1);
                        continue;
                    }
                    if (pt.isType("claysource")) {
                        BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem(Items.field_151119_aD), 1);
                        continue;
                    }
                    if (pt.isType("gravelsource")) {
                        BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem(Blocks.field_150351_n), 1);
                        continue;
                    }
                    if (pt.isType("granitesource")) {
                        BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem(Blocks.field_150348_b.func_176223_P().func_177226_a((IProperty)BlockStone.field_176247_a, (Comparable)BlockStone.EnumType.GRANITE)), 1);
                        continue;
                    }
                    if (pt.isType("dioritesource")) {
                        BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem(Blocks.field_150348_b.func_176223_P().func_177226_a((IProperty)BlockStone.field_176247_a, (Comparable)BlockStone.EnumType.DIORITE)), 1);
                        continue;
                    }
                    if (pt.isType("andesitesource")) {
                        BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem(Blocks.field_150348_b.func_176223_P().func_177226_a((IProperty)BlockStone.field_176247_a, (Comparable)BlockStone.EnumType.ANDESITE)), 1);
                        continue;
                    }
                    if (pt.isType("redsandstonesource")) {
                        BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem(Blocks.field_180395_cM), 1);
                        continue;
                    }
                    if (pt.isType("quartzsource")) {
                        BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem(Blocks.field_150449_bY), 1);
                        continue;
                    }
                    if (pt.isType("snowsource")) {
                        BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem(Blocks.field_150433_aE), 1);
                        continue;
                    }
                    if (!pt.isType("icesource")) continue;
                    BookManagerTravelBook.incrementMap(resourceSpot, InvItem.createInvItem(Blocks.field_150432_aD), 1);
                }
            }
        }
        if (nbChests > 0) {
            infoColumns.add(new TextLine("" + nbChests, new ItemStack((Block)Blocks.field_150486_ae, 1), LanguageUtilities.string("travelbook.nbchests"), false));
        }
        if (nbFurnace > 0) {
            infoColumns.add(new TextLine("" + nbFurnace, new ItemStack(Blocks.field_150460_al, 1), LanguageUtilities.string("travelbook.nbfurnaces"), false));
        }
        if (nbFirePits > 0) {
            infoColumns.add(new TextLine("" + nbFirePits, new ItemStack((Block)MillBlocks.FIRE_PIT, 1), LanguageUtilities.string("travelbook.nbfirepits"), false));
        }
        if (nbFishingSpot > 0) {
            infoColumns.add(new TextLine("" + nbFishingSpot, new ItemStack((Item)Items.field_151112_aM, 1), LanguageUtilities.string("travelbook.nbfishingspot"), false));
        }
        for (InvItem key : plantingSpot.keySet()) {
            infoColumns.add(new TextLine("" + plantingSpot.get(key), key.getItemStack(), LanguageUtilities.string("travelbook.plantingspot", key.getName()), false));
        }
        for (InvItem key : resourceSpot.keySet()) {
            infoColumns.add(new TextLine("" + resourceSpot.get(key), key.getItemStack(), LanguageUtilities.string("travelbook.resourcespot", key.getName()), false));
        }
        for (InvItem key : spawnSpot.keySet()) {
            infoColumns.add(new TextLine("" + spawnSpot.get(key), key.getItemStack(), LanguageUtilities.string("travelbook.spawnspot"), false));
        }
    }

    public TextBook getBookBuildingsList(Culture culture, String category, UserProfile profile) {
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        page.addLine(LanguageUtilities.string("travelbook.buildingslist", culture.getAdjectiveTranslated()), "\u00a71", culture.getIcon(), false);
        page.addLine(LanguageUtilities.string("travelbook.buildingslistcategory", culture.getCategoryName(category)), culture.getCategoryIcon(category), false);
        page.addBlankLine();
        List<BuildingPlanSet> sortedPlans = this.getCurrentBuildingList(culture, category);
        int nbKnownBuildings = profile.getNbUnlockedBuildings(culture, category);
        page.addLine(LanguageUtilities.string("travelbook.buildingslistcategory_unlocked", "" + nbKnownBuildings, "" + sortedPlans.size()));
        page.addBlankLine();
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        for (BuildingPlanSet planSet : sortedPlans) {
            String style = "";
            if (!profile.isBuildingUnlocked(culture, planSet)) {
                style = TextLine.ITALIC;
            }
            infoColumns.add(new TextLine(planSet.getNameNativeAndTranslated(), style, new GuiText.GuiButtonReference(planSet)));
        }
        List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : linesWithColumns) {
            page.addLine(l);
        }
        book.addPage(page);
        return book;
    }

    public TextBook getBookCulture(Culture culture, UserProfile profile) {
        GuiTravelBook.GuiButtonTravelBook button1;
        ItemStack icon;
        String category;
        GuiTravelBook.GuiButtonTravelBook button2;
        int i;
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        page.addLine(culture.getNameTranslated(), "\u00a71", culture.getIcon(), false);
        page.addBlankLine();
        if (culture.hasCultureString("travelbook.culture.desc")) {
            page.addLine(culture.getCultureString("travelbook.culture.desc"));
            page.addBlankLine();
        }
        int nbTotal = culture.listVillagerTypes.stream().filter(p -> p.travelBookDisplay).collect(Collectors.toList()).size();
        int nbKnown = profile.getNbUnlockedVillagers(culture);
        page.addLine(LanguageUtilities.string("travelbook.villagers"), "\u00a71");
        page.getLastLine().canCutAfter = false;
        page.addLine(LanguageUtilities.string("travelbook.villagerslist_unlocked", "" + nbKnown, "" + nbTotal, culture.getAdjectiveTranslated()), TextLine.ITALIC);
        page.getLastLine().canCutAfter = false;
        for (i = 0; i < culture.travelBookVillagerCategories.size(); i += 2) {
            button2 = null;
            category = culture.travelBookVillagerCategories.get(i);
            icon = culture.getCategoryIcon(category);
            button1 = new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_VILLAGERS, culture.getCategoryName(category), category, icon);
            if (i + 1 < culture.travelBookVillagerCategories.size()) {
                category = culture.travelBookVillagerCategories.get(i + 1);
                icon = culture.getCategoryIcon(category);
                button2 = new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_VILLAGERS, culture.getCategoryName(category), category, icon);
            }
            page.addLine(new TextLine(button1, button2));
        }
        page.addBlankLine();
        nbTotal = culture.listVillageTypes.stream().filter(p -> p.travelBookDisplay).collect(Collectors.toList()).size();
        nbKnown = profile.getNbUnlockedVillages(culture);
        page.addLine(LanguageUtilities.string("travelbook.villages"), "\u00a71");
        page.getLastLine().canCutAfter = false;
        page.addLine(LanguageUtilities.string("travelbook.villageslist_unlocked", "" + nbKnown, "" + nbTotal, culture.getAdjectiveTranslated()), TextLine.ITALIC);
        page.getLastLine().canCutAfter = false;
        page.addLine(new TextLine(new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_VILLAGES, LanguageUtilities.string("travelbook.villages"), new ItemStack((Item)Items.field_151148_bJ, 1)), null));
        page.addBlankLine();
        nbTotal = culture.ListPlanSets.stream().filter(p -> p.getFirstStartingPlan().travelBookDisplay).collect(Collectors.toList()).size();
        nbKnown = profile.getNbUnlockedBuildings(culture);
        page.addLine(LanguageUtilities.string("travelbook.buildings"), "\u00a71");
        page.getLastLine().canCutAfter = false;
        page.addLine(LanguageUtilities.string("travelbook.buildingslist_unlocked", "" + nbKnown, "" + nbTotal, culture.getAdjectiveTranslated()), TextLine.ITALIC);
        page.getLastLine().canCutAfter = false;
        for (i = 0; i < culture.travelBookBuildingCategories.size(); i += 2) {
            button2 = null;
            category = culture.travelBookBuildingCategories.get(i);
            icon = culture.getCategoryIcon(category);
            button1 = new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_BUILDINGS, culture.getCategoryName(category), category, icon);
            if (i + 1 < culture.travelBookBuildingCategories.size()) {
                category = culture.travelBookBuildingCategories.get(i + 1);
                icon = culture.getCategoryIcon(category);
                button2 = new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_BUILDINGS, culture.getCategoryName(category), category, icon);
            }
            page.addLine(new TextLine(button1, button2));
        }
        page.addBlankLine();
        nbTotal = culture.goodsList.stream().filter(p -> p.travelBookDisplay).collect(Collectors.toList()).size();
        nbKnown = profile.getNbUnlockedTradeGoods(culture);
        page.addLine(LanguageUtilities.string("travelbook.tradegoods"), "\u00a71");
        page.getLastLine().canCutAfter = false;
        page.addLine(LanguageUtilities.string("travelbook.tradegoodslist_unlocked", "" + nbKnown, "" + nbTotal, culture.getAdjectiveTranslated()), TextLine.ITALIC);
        page.getLastLine().canCutAfter = false;
        for (i = 0; i < culture.travelBookTradeGoodCategories.size(); i += 2) {
            button2 = null;
            category = culture.travelBookTradeGoodCategories.get(i);
            icon = culture.getCategoryIcon(category);
            button1 = new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_TRADE_GOODS, culture.getCategoryName(category), category, icon);
            if (i + 1 < culture.travelBookTradeGoodCategories.size()) {
                category = culture.travelBookTradeGoodCategories.get(i + 1);
                icon = culture.getCategoryIcon(category);
                button2 = new GuiTravelBook.GuiButtonTravelBook(GuiTravelBook.ButtonTypes.VIEW_TRADE_GOODS, culture.getCategoryName(category), category, icon);
            }
            page.addLine(new TextLine(button1, button2));
        }
        book.addPage(page);
        return book;
    }

    public TextBook getBookCultureForJSONExport(Culture culture, UserProfile profile) {
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        page.addLine(culture.getNameTranslated(), "\u00a71", culture.getIcon(), false);
        page.addBlankLine();
        if (culture.hasCultureString("travelbook.culture.desc")) {
            page.addLine(culture.getCultureString("travelbook.culture.desc"));
            page.getLastLine().exportSpecialTag = EXPORT_TAG_MAIN_DESC;
            page.addBlankLine();
        }
        book.addPage(page);
        return book;
    }

    public TextBook getBookHome(UserProfile profile) {
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        page.addLine(LanguageUtilities.string("travelbook.title"), "\u00a71");
        page.addBlankLine();
        int nbKnownCultures = profile.getNbUnlockedCultures();
        page.addLine(LanguageUtilities.string("travelbook.culture_unlocked", "" + nbKnownCultures, "" + Culture.ListCultures.size()));
        page.addBlankLine();
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        for (Culture culture : Culture.ListCultures) {
            String style = "";
            if (!profile.isCultureUnlocked(culture)) {
                style = TextLine.ITALIC;
            }
            infoColumns.add(new TextLine(culture.getNameTranslated(), style, new GuiText.GuiButtonReference(culture)));
        }
        List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : linesWithColumns) {
            page.addLine(l);
        }
        if (MillConfigValues.TRAVEL_BOOK_LEARNING) {
            page.addBlankLine();
            page.addLine(LanguageUtilities.string("travelbook.contentlocked"));
            page.addLine(LanguageUtilities.string("travelbook.learningsetting"), "\u00a71");
        } else {
            page.addBlankLine();
            page.addLine(LanguageUtilities.string("travelbook.contentunlocked"));
            page.addLine(LanguageUtilities.string("travelbook.learningsetting_off"), "\u00a71");
        }
        book.addPage(page);
        return book;
    }

    public TextBook getBookTradeGoodDetail(Culture culture, String itemKey, UserProfile profile) {
        boolean displayFullInfos;
        TradeGood tradeGood = culture.getTradeGood(itemKey);
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        TextLine line = new TextLine(tradeGood.getName(), "\u00a71", tradeGood.getIcon(), false);
        page.addLine(line);
        page.getLastLine().canCutAfter = false;
        page.addBlankLine();
        boolean knownTradeGood = profile == null || profile.isTradeGoodUnlocked(culture, tradeGood);
        boolean bl = displayFullInfos = knownTradeGood || !MillConfigValues.TRAVEL_BOOK_LEARNING;
        if (!knownTradeGood) {
            page.addLine(new TextLine(LanguageUtilities.string("travelbook.unknowntradegood"), "\u00a74"));
            page.addBlankLine();
        }
        if (displayFullInfos) {
            if (culture.hasCultureString("travelbook.trade_good." + tradeGood.key + ".desc")) {
                page.addLine(culture.getCultureString("travelbook.trade_good." + tradeGood.key + ".desc"));
                page.getLastLine().exportSpecialTag = EXPORT_TAG_MAIN_DESC;
                page.addBlankLine();
            } else if (LanguageUtilities.hasString("travelbook.trade_good." + tradeGood.key + ".desc")) {
                page.addLine(LanguageUtilities.string("travelbook.trade_good." + tradeGood.key + ".desc"));
                page.getLastLine().exportSpecialTag = EXPORT_TAG_MAIN_DESC;
                page.addBlankLine();
            }
            this.getBookTradeGoodDetail_basicInfos(page, tradeGood);
            this.getBookTradeGoodDetail_shops(culture, page, tradeGood);
            page.addBlankLine();
            this.getBookTradeGoodDetail_goalsInfo(culture, page, tradeGood);
            this.getBookTradeGoodDetail_villageUse(culture, page, tradeGood);
        }
        book.addPage(page);
        book = this.offsetFirstLines(book);
        return book;
    }

    private void getBookTradeGoodDetail_basicInfos(TextPage page, TradeGood tradeGood) {
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        if (tradeGood.getBasicBuyingPrice(null) > 0) {
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_buying_price", "" + MillCommonUtilities.getShortPrice(tradeGood.getBasicBuyingPrice(null))), new ItemStack((Item)MillItems.DENIER, 1), LanguageUtilities.string("travelbook.trade_good_buying_price.desc"), false));
        }
        if (tradeGood.getBasicSellingPrice(null) > 0) {
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_selling_price", MillCommonUtilities.getShortPrice(tradeGood.getBasicSellingPrice(null))), new ItemStack((Item)MillItems.DENIER_ARGENT, 1), LanguageUtilities.string("travelbook.trade_good_selling_price.desc"), false));
        }
        if (tradeGood.foreignMerchantPrice > 0) {
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_market_price", MillCommonUtilities.getShortPrice(tradeGood.foreignMerchantPrice)), new ItemStack((Item)MillItems.DENIER_OR, 1), LanguageUtilities.string("travelbook.trade_good_market_price.desc"), false));
        }
        if (tradeGood.minReputation > 0) {
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_min_reputation", "" + tradeGood.minReputation), new ItemStack((Block)Blocks.field_150328_O, 1), LanguageUtilities.string("travelbook.trade_good_min_reputation.desc"), false));
        }
        if (tradeGood.autoGenerate) {
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_autogenerated"), new ItemStack(Blocks.field_150346_d, 1), LanguageUtilities.string("travelbook.trade_good_autogenerated_desc"), false));
        }
        if (tradeGood.item.item instanceof ItemTool) {
            ItemTool tool = (ItemTool)tradeGood.item.item;
            Block testBlock = null;
            if (tool instanceof ItemSpade) {
                testBlock = Blocks.field_150346_d;
            } else if (tool instanceof ItemPickaxe) {
                testBlock = Blocks.field_150348_b;
            } else if (tool instanceof ItemAxe) {
                testBlock = Blocks.field_150364_r;
            }
            if (testBlock != null) {
                infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_toolefficiency", "" + tool.func_150893_a(tradeGood.item.getItemStack(), testBlock.func_176223_P())), new ItemStack(Items.field_151035_b, 1), LanguageUtilities.string("travelbook.trade_good_toolefficiency.desc"), false));
            }
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_durability", "" + tool.func_77612_l()), new ItemStack(Blocks.field_150467_bQ, 1), LanguageUtilities.string("travelbook.trade_good_durability.desc"), false));
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_enchantability", "" + tool.func_77619_b()), new ItemStack(Blocks.field_150381_bn, 1), LanguageUtilities.string("travelbook.trade_good_enchantability.desc"), false));
        }
        if (tradeGood.item.item instanceof ItemSword) {
            ItemSword sword = (ItemSword)tradeGood.item.item;
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_durability", "" + sword.func_77612_l()), new ItemStack(Blocks.field_150467_bQ, 1), LanguageUtilities.string("travelbook.trade_good_durability.desc"), false));
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_enchantability", "" + sword.func_77619_b()), new ItemStack(Blocks.field_150381_bn, 1), LanguageUtilities.string("travelbook.trade_good_enchantability.desc"), false));
        }
        if (tradeGood.item.item instanceof ItemFood) {
            ItemFood food = (ItemFood)tradeGood.item.item;
            if (food.func_150905_g(tradeGood.item.getItemStack()) > 0) {
                infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_foodhealamount", "" + food.func_150905_g(tradeGood.item.getItemStack())), new ItemStack(Items.field_151034_e, 1), LanguageUtilities.string("travelbook.trade_good_foodhealamount.desc"), false));
            }
            if (food.func_150906_h(tradeGood.item.getItemStack()) > 0.0f) {
                infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_foodsaturation", "" + food.func_150906_h(tradeGood.item.getItemStack())), new ItemStack(Items.field_151083_be, 1), LanguageUtilities.string("travelbook.trade_good_foodsaturation.desc"), false));
            }
            if (food instanceof ItemFoodMultiple) {
                ItemFoodMultiple foodMultiple = (ItemFoodMultiple)food;
                if (foodMultiple.getHealthAmount() > 0) {
                    infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_multiplefoodhealth", "" + foodMultiple.getHealthAmount()), new ItemStack((Item)MillItems.RASGULLA, 1), LanguageUtilities.string("travelbook.trade_good_multiplefoodhealth.desc"), false));
                }
                if (foodMultiple.getRegenerationDuration() > 0) {
                    infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_enchantment", I18n.func_135052_a((String)MobEffects.field_76428_l.func_76393_a(), (Object[])new Object[0]), "" + foodMultiple.getRegenerationDuration()), new ItemStack(Items.field_151153_ao, 1), LanguageUtilities.string("travelbook.trade_good_enchantment.desc"), false));
                }
                if (foodMultiple.getPotionId() != null) {
                    infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_enchantment", I18n.func_135052_a((String)I18n.func_135052_a((String)foodMultiple.getPotionId().func_76453_d(), (Object[])new Object[0]), (Object[])new Object[0]), "" + foodMultiple.getPotionId().func_76459_b() / 20), new ItemStack(Items.field_151153_ao, 1), LanguageUtilities.string("travelbook.trade_good_enchantment.desc"), false));
                }
                if (foodMultiple.getDrunkDuration() > 0) {
                    infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_drunk"), new ItemStack((Item)MillItems.CIDER, 1), LanguageUtilities.string("travelbook.trade_good_drunk.desc"), false));
                }
                if (foodMultiple.func_77612_l() > 1) {
                    infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_nbuse", "" + foodMultiple.func_77612_l()), new ItemStack((Item)MillItems.TRIPES, 1), LanguageUtilities.string("travelbook.trade_good_nbuse.desc"), false));
                }
            }
        } else if (tradeGood.item.item instanceof ItemPickaxe) {
            ItemPickaxe pickaxe = (ItemPickaxe)tradeGood.item.item;
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_harvestlevel", "" + pickaxe.getHarvestLevel(tradeGood.item.getItemStack(), "pickaxe", null, null)), new ItemStack(Blocks.field_150343_Z, 1), LanguageUtilities.string("travelbook.trade_good_harvestlevel.desc"), false));
        }
        if (VillagerConfig.DEFAULT_CONFIG.foodsGrowth.containsKey(tradeGood.item)) {
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_growthfood", "" + VillagerConfig.DEFAULT_CONFIG.foodsGrowth.get(tradeGood.item)), new ItemStack(Items.field_151025_P, 1), LanguageUtilities.string("travelbook.trade_good_growthfood.desc"), false));
        }
        if (VillagerConfig.DEFAULT_CONFIG.foodsConception.containsKey(tradeGood.item)) {
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_conceptionfood", "+" + VillagerConfig.DEFAULT_CONFIG.foodsConception.get(tradeGood.item) * 10 + "%"), new ItemStack((Item)MillItems.CIDER, 1), LanguageUtilities.string("travelbook.trade_good_conceptionfood.desc"), false));
        }
        if (VillagerConfig.DEFAULT_CONFIG.weapons.containsKey(tradeGood.item)) {
            double attackBoost = Math.ceil((float)MillCommonUtilities.getItemWeaponDamage(tradeGood.item.item) / 2.0f);
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_weapon", "" + attackBoost), new ItemStack(Items.field_151040_l, 1), LanguageUtilities.string("travelbook.trade_good_weapon.desc"), false));
        }
        if (VillagerConfig.DEFAULT_CONFIG.armoursBoots.containsKey(tradeGood.item) || VillagerConfig.DEFAULT_CONFIG.armoursChestplate.containsKey(tradeGood.item) || VillagerConfig.DEFAULT_CONFIG.armoursHelmet.containsKey(tradeGood.item) || VillagerConfig.DEFAULT_CONFIG.armoursLeggings.containsKey(tradeGood.item)) {
            int armourValue = ((ItemArmor)tradeGood.item.item).field_77879_b;
            infoColumns.add(new TextLine("" + armourValue, new ItemStack((Item)Items.field_151030_Z, 1), LanguageUtilities.string("travelbook.trade_good_armour.desc"), false));
        }
        List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : linesWithColumns) {
            page.addLine(l);
        }
    }

    private void getBookTradeGoodDetail_goalsInfo(Culture culture, TextPage page, TradeGood tradeGood) {
        boolean hasConsumers;
        ArrayList<GoalGenericCrafting> craftingProducingGoals = new ArrayList<GoalGenericCrafting>();
        ArrayList<Goal> gatheringGoals = new ArrayList<Goal>();
        ArrayList<Goal> harvestingGoals = new ArrayList<Goal>();
        ArrayList<GoalGenericCrafting> consumingGoals = new ArrayList<GoalGenericCrafting>();
        for (Goal goal : culture.getAllUsedGoals()) {
            if (goal instanceof GoalGenericCrafting) {
                GoalGenericCrafting craftingGoal = (GoalGenericCrafting)goal;
                if (craftingGoal.output.containsKey(tradeGood.item)) {
                    craftingProducingGoals.add(craftingGoal);
                }
                if (!craftingGoal.input.containsKey(tradeGood.item)) continue;
                consumingGoals.add(craftingGoal);
                continue;
            }
            if (goal instanceof GoalGenericMining) {
                GoalGenericMining miningGoal = (GoalGenericMining)goal;
                for (InvItem invItem : miningGoal.loots.keySet()) {
                    if (!invItem.equals(tradeGood.item)) continue;
                    gatheringGoals.add(goal);
                }
                continue;
            }
            if (!(goal instanceof GoalGenericHarvestCrop)) continue;
            GoalGenericHarvestCrop harvestGoal = (GoalGenericHarvestCrop)goal;
            for (AnnotedParameter.BonusItem bonusItem : harvestGoal.harvestItem) {
                if (!bonusItem.item.equals(tradeGood.item)) continue;
                harvestingGoals.add(goal);
            }
        }
        if (InvItem.createInvItem(MillBlocks.BS_MUD_BRICK).equals(tradeGood.item)) {
            gatheringGoals.add(Goal.goals.get("gatherbrick"));
        }
        if (InvItem.createInvItem(MillItems.SILK).equals(tradeGood.item)) {
            gatheringGoals.add(Goal.goals.get("gathersilk"));
        }
        if (InvItem.createInvItem(Items.field_151115_aP).equals(tradeGood.item)) {
            gatheringGoals.add(Goal.goals.get("fish"));
            gatheringGoals.add(Goal.goals.get("fishinuits"));
        }
        if (InvItem.createInvItem(Blocks.field_189880_di).equals(tradeGood.item)) {
            gatheringGoals.add(Goal.goals.get("fishinuits"));
        }
        if (InvItem.createInvItem(Items.field_151100_aR, 3).equals(tradeGood.item)) {
            harvestingGoals.add(Goal.goals.get("harvestcocoa"));
        }
        if (InvItem.createInvItem(Items.field_151075_bm).equals(tradeGood.item)) {
            harvestingGoals.add(Goal.goals.get("harvestwarts"));
        }
        if (InvItem.createInvItem(Items.field_151120_aE).equals(tradeGood.item)) {
            harvestingGoals.add(Goal.goals.get("harvestsugarcane"));
        }
        if (InvItem.createInvItem(Blocks.field_150325_L).equals(tradeGood.item)) {
            gatheringGoals.add(Goal.goals.get("shearsheep"));
        }
        if (InvItem.createInvItem(Blocks.field_150364_r).equals(tradeGood.item) || InvItem.createInvItem(Blocks.field_150363_s).equals(tradeGood.item)) {
            gatheringGoals.add(Goal.goals.get("choptrees"));
        }
        ArrayList<VillagerType> craftingVillagers = new ArrayList<VillagerType>();
        ArrayList<VillagerType> harvestingVillagers = new ArrayList<VillagerType>();
        ArrayList<VillagerType> gatheringVillagers = new ArrayList<VillagerType>();
        ArrayList<VillagerType> usingVillagers = new ArrayList<VillagerType>();
        for (VillagerType villagerType : culture.villagerTypes.values()) {
            boolean found = false;
            for (Goal goal : craftingProducingGoals) {
                if (!villagerType.goals.contains(goal)) continue;
                found = true;
            }
            if (found) {
                craftingVillagers.add(villagerType);
            }
            found = false;
            for (Goal goal : harvestingGoals) {
                if (!villagerType.goals.contains(goal)) continue;
                found = true;
            }
            if (found) {
                harvestingVillagers.add(villagerType);
            }
            found = false;
            for (Goal goal : gatheringGoals) {
                if (!villagerType.goals.contains(goal)) continue;
                found = true;
            }
            if (!found) continue;
            gatheringVillagers.add(villagerType);
        }
        for (VillagerType villagerType : culture.villagerTypes.values()) {
            if (villagerType.requiredFoodAndGoods.containsKey(tradeGood.item)) {
                usingVillagers.add(villagerType);
            }
            if (villagerType.itemsNeeded.contains(tradeGood.item)) {
                usingVillagers.add(villagerType);
            }
            for (String toolcategory : villagerType.toolsCategoriesNeeded) {
                if (!villagerType.villagerConfig.categories.get(toolcategory).contains(tradeGood.item)) continue;
                usingVillagers.add(villagerType);
            }
        }
        ArrayList<TextLine> arrayList = new ArrayList<TextLine>();
        boolean hasProducers = !craftingVillagers.isEmpty() || !harvestingVillagers.isEmpty() || !gatheringVillagers.isEmpty();
        boolean bl = hasConsumers = !usingVillagers.isEmpty();
        if (hasProducers && hasConsumers) {
            ArrayList<TextLine> producerColumn = new ArrayList<TextLine>();
            ArrayList<TextLine> arrayList2 = new ArrayList<TextLine>();
            if (craftingVillagers.size() > 0) {
                producerColumn.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_craftingvillagers"), TextLine.ITALIC, false));
                for (VillagerType villagerType : craftingVillagers) {
                    producerColumn.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType)));
                }
                producerColumn.add(new TextLine());
            }
            if (harvestingVillagers.size() > 0) {
                producerColumn.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_harvestingvillagers"), TextLine.ITALIC, false));
                for (VillagerType villagerType : harvestingVillagers) {
                    producerColumn.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType)));
                }
                producerColumn.add(new TextLine());
            }
            if (gatheringVillagers.size() > 0) {
                producerColumn.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_gatheringvillagers"), TextLine.ITALIC, false));
                producerColumn.clear();
                for (VillagerType villagerType : gatheringVillagers) {
                    producerColumn.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType)));
                }
                producerColumn.add(new TextLine());
            }
            if (usingVillagers.size() > 0) {
                arrayList2.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_usingvillagers"), TextLine.ITALIC, false));
                for (VillagerType villagerType : usingVillagers) {
                    arrayList2.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType)));
                }
                arrayList2.add(new TextLine());
            }
            List<TextLine> mergedColumns = BookManagerTravelBook.mergeColumns(producerColumn, arrayList2);
            for (TextLine line : mergedColumns) {
                page.addLine(line);
            }
        } else {
            List<TextLine> linesWithColumns;
            if (craftingVillagers.size() > 0) {
                page.addLine(LanguageUtilities.string("travelbook.trade_good_craftingvillagers"), TextLine.ITALIC);
                page.getLastLine().canCutAfter = false;
                arrayList.clear();
                for (VillagerType villagerType : craftingVillagers) {
                    arrayList.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType)));
                }
                linesWithColumns = BookManager.splitInColumns(arrayList, 2);
                for (TextLine textLine : linesWithColumns) {
                    page.addLine(textLine);
                }
                page.addBlankLine();
            }
            if (harvestingVillagers.size() > 0) {
                page.addLine(LanguageUtilities.string("travelbook.trade_good_harvestingvillagers"), TextLine.ITALIC);
                page.getLastLine().canCutAfter = false;
                arrayList.clear();
                for (VillagerType villagerType : harvestingVillagers) {
                    arrayList.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType)));
                }
                linesWithColumns = BookManager.splitInColumns(arrayList, 2);
                for (TextLine textLine : linesWithColumns) {
                    page.addLine(textLine);
                }
                page.addBlankLine();
            }
            if (gatheringVillagers.size() > 0) {
                page.addLine(LanguageUtilities.string("travelbook.trade_good_gatheringvillagers"), TextLine.ITALIC);
                page.getLastLine().canCutAfter = false;
                arrayList.clear();
                for (VillagerType villagerType : gatheringVillagers) {
                    arrayList.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType)));
                }
                linesWithColumns = BookManager.splitInColumns(arrayList, 2);
                for (TextLine textLine : linesWithColumns) {
                    page.addLine(textLine);
                }
                page.addBlankLine();
            }
            if (usingVillagers.size() > 0) {
                page.addLine(LanguageUtilities.string("travelbook.trade_good_usingvillagers"), TextLine.ITALIC);
                page.getLastLine().canCutAfter = false;
                arrayList.clear();
                for (VillagerType villagerType : usingVillagers) {
                    arrayList.add(new TextLine(villagerType.name, new GuiText.GuiButtonReference(villagerType)));
                }
                linesWithColumns = BookManager.splitInColumns(arrayList, 2);
                for (TextLine textLine : linesWithColumns) {
                    page.addLine(textLine);
                }
                page.addBlankLine();
            }
        }
        if (consumingGoals.size() > 0) {
            page.addLine(LanguageUtilities.string("travelbook.trade_good_consuminggoals"), TextLine.ITALIC);
            page.getLastLine().canCutAfter = false;
            for (Goal goal : consumingGoals) {
                page.addLine(new TextLine(goal.gameName(), goal.getIcon(), false));
            }
            page.addBlankLine();
        }
    }

    private void getBookTradeGoodDetail_shops(Culture culture, TextPage page, TradeGood tradeGood) {
        ArrayList<String> buyingShops = new ArrayList<String>();
        for (String string : culture.shopBuys.keySet()) {
            if (!culture.shopBuys.get(string).contains(tradeGood)) continue;
            buyingShops.add(string);
        }
        for (String string : culture.shopBuysOptional.keySet()) {
            if (!culture.shopBuysOptional.get(string).contains(tradeGood)) continue;
            buyingShops.add(string);
        }
        List<Object> buyingBuildings = new ArrayList();
        for (BuildingPlanSet buildingPlanSet : culture.ListPlanSets) {
            if (!buyingShops.contains(buildingPlanSet.getFirstStartingPlan().shop)) continue;
            buyingBuildings.add(buildingPlanSet);
        }
        buyingBuildings = buyingBuildings.stream().sorted((p1, p2) -> p1.getNameNative().compareTo(p2.getNameNative())).collect(Collectors.toList());
        ArrayList<String> arrayList = new ArrayList<String>();
        for (String string : culture.shopSells.keySet()) {
            if (!culture.shopSells.get(string).contains(tradeGood)) continue;
            arrayList.add(string);
        }
        ArrayList<BuildingPlanSet> arrayList2 = new ArrayList<BuildingPlanSet>();
        for (BuildingPlanSet buildingPlanSet : culture.ListPlanSets) {
            if (!arrayList.contains(buildingPlanSet.getFirstStartingPlan().shop)) continue;
            arrayList2.add(buildingPlanSet);
        }
        List list = arrayList2.stream().sorted((p1, p2) -> p1.getNameNative().compareTo(p2.getNameNative())).collect(Collectors.toList());
        if (list.equals(buyingBuildings)) {
            page.addBlankLine();
            page.addLine(new TextLine(LanguageUtilities.string("travelbook.trade_good_tradingbuildings"), TextLine.ITALIC, false));
            page.getLastLine().canCutAfter = false;
            ArrayList<TextLine> arrayList3 = new ArrayList<TextLine>();
            for (BuildingPlanSet buildingPlanSet : buyingBuildings) {
                arrayList3.add(new TextLine(buildingPlanSet.getNameNative(), new GuiText.GuiButtonReference(buildingPlanSet)));
            }
            List<TextLine> list2 = BookManager.splitInColumns(arrayList3, 2);
            for (TextLine l : list2) {
                page.addLine(l);
            }
        } else if (buyingBuildings.size() > 0 && list.size() > 0) {
            ArrayList<TextLine> arrayList4 = new ArrayList<TextLine>();
            page.addBlankLine();
            arrayList4.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_buyingbuildings"), TextLine.ITALIC, false));
            page.getLastLine().canCutAfter = false;
            for (BuildingPlanSet buildingPlanSet : buyingBuildings) {
                arrayList4.add(new TextLine(buildingPlanSet.getNameNative(), new GuiText.GuiButtonReference(buildingPlanSet)));
            }
            ArrayList<TextLine> arrayList5 = new ArrayList<TextLine>();
            page.addBlankLine();
            arrayList5.add(new TextLine(LanguageUtilities.string("travelbook.trade_good_sellingbuildings"), TextLine.ITALIC, false));
            page.getLastLine().canCutAfter = false;
            for (Object planSet : list) {
                arrayList5.add(new TextLine(((BuildingPlanSet)planSet).getNameNative(), new GuiText.GuiButtonReference((BuildingPlanSet)planSet)));
            }
            List<TextLine> list3 = BookManagerTravelBook.mergeColumns(arrayList4, arrayList5);
            for (TextLine line : list3) {
                page.addLine(line);
            }
        } else if (buyingBuildings.size() > 0) {
            page.addBlankLine();
            page.addLine(new TextLine(LanguageUtilities.string("travelbook.trade_good_buyingbuildings"), TextLine.ITALIC, false));
            page.getLastLine().canCutAfter = false;
            ArrayList<TextLine> arrayList6 = new ArrayList<TextLine>();
            for (BuildingPlanSet buildingPlanSet : buyingBuildings) {
                arrayList6.add(new TextLine(buildingPlanSet.getNameNative(), new GuiText.GuiButtonReference(buildingPlanSet)));
            }
            List<TextLine> list4 = BookManager.splitInColumns(arrayList6, 2);
            for (TextLine l : list4) {
                page.addLine(l);
            }
        } else if (list.size() > 0) {
            page.addBlankLine();
            page.addLine(new TextLine(LanguageUtilities.string("travelbook.trade_good_sellingbuildings"), TextLine.ITALIC, false));
            page.getLastLine().canCutAfter = false;
            ArrayList<TextLine> arrayList7 = new ArrayList<TextLine>();
            for (BuildingPlanSet buildingPlanSet : list) {
                arrayList7.add(new TextLine(buildingPlanSet.getNameNative(), new GuiText.GuiButtonReference(buildingPlanSet)));
            }
            List<TextLine> list5 = BookManager.splitInColumns(arrayList7, 2);
            for (TextLine l : list5) {
                page.addLine(l);
            }
        }
        ArrayList<VillagerType> arrayList8 = new ArrayList<VillagerType>();
        for (VillagerType villagerType : culture.listVillagerTypes) {
            if (!villagerType.isForeignMerchant || !villagerType.foreignMerchantStock.containsKey(tradeGood.item)) continue;
            arrayList8.add(villagerType);
        }
        if (arrayList8.size() > 0) {
            page.addBlankLine();
            page.addLine(new TextLine(LanguageUtilities.string("travelbook.trade_good_marketmerchants"), TextLine.ITALIC, false));
            page.getLastLine().canCutAfter = false;
            ArrayList<TextLine> arrayList9 = new ArrayList<TextLine>();
            for (VillagerType merchant : arrayList8) {
                arrayList9.add(new TextLine(merchant.name, new GuiText.GuiButtonReference(merchant)));
            }
            List<TextLine> list6 = BookManager.splitInColumns(arrayList9, 2);
            for (TextLine l : list6) {
                page.addLine(l);
            }
        }
    }

    private void getBookTradeGoodDetail_villageUse(Culture culture, TextPage page, TradeGood tradeGood) {
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        for (VillageType villageType : culture.listVillageTypes) {
            Integer resUse = villageType.computeVillageTypeCost().get(tradeGood.item);
            if (resUse == null) continue;
            infoColumns.add(new TextLine(villageType.name + ": " + resUse, new GuiText.GuiButtonReference(villageType)));
        }
        if (infoColumns.size() > 0) {
            page.addLine(LanguageUtilities.string("travelbook.trade_good_usebyvillage"), TextLine.ITALIC);
            page.getLastLine().canCutAfter = false;
            for (TextLine l : infoColumns) {
                page.addLine(l);
            }
            page.addBlankLine();
        }
        infoColumns.clear();
        for (BuildingPlanSet planSet : culture.ListPlanSets) {
            if (!planSet.getFirstStartingPlan().travelBookDisplay) continue;
            for (int variation = 0; variation < planSet.plans.size(); ++variation) {
                HashMap<InvItem, Integer> totalCost = new HashMap<InvItem, Integer>();
                for (BuildingPlan plan : planSet.plans.get(variation)) {
                    for (InvItem key : plan.resCost.keySet()) {
                        if (totalCost.containsKey(key)) {
                            totalCost.put(key, (Integer)totalCost.get(key) + plan.resCost.get(key));
                            continue;
                        }
                        totalCost.put(key, plan.resCost.get(key));
                    }
                }
                Integer resUse = (Integer)totalCost.get(tradeGood.item);
                if (resUse == null) continue;
                String buildingName = planSet.getNameNative();
                if (planSet.plans.size() > 1) {
                    buildingName = buildingName + " (" + (char)(65 + variation) + ")";
                }
                infoColumns.add(new TextLine(buildingName + ": " + resUse, new GuiText.GuiButtonReference(planSet)));
            }
        }
        if (infoColumns.size() > 0) {
            page.addLine(LanguageUtilities.string("travelbook.trade_good_usebybuilding"), TextLine.ITALIC);
            page.getLastLine().canCutAfter = false;
            for (TextLine l : infoColumns) {
                page.addLine(l);
            }
            page.addBlankLine();
        }
    }

    public TextBook getBookTradeGoodsList(Culture culture, String category, UserProfile profile) {
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        page.addLine(LanguageUtilities.string("travelbook.tradegoodslist", culture.getAdjectiveTranslated()), "\u00a71", culture.getIcon(), false);
        page.addLine(LanguageUtilities.string("travelbook.tradegoodslistcategory", culture.getCategoryName(category)), culture.getCategoryIcon(category), false);
        page.addBlankLine();
        List<TradeGood> sortedTradeGoods = this.getCurrentTradeGoodList(culture, category);
        int nbKnownTradeGoods = profile.getNbUnlockedTradeGoods(culture, category);
        page.addLine(LanguageUtilities.string("travelbook.tradegoodslistcategory_unlocked", "" + nbKnownTradeGoods, "" + sortedTradeGoods.size()));
        page.addBlankLine();
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        for (TradeGood tradeGood : sortedTradeGoods) {
            String style = "";
            if (!profile.isTradeGoodUnlocked(culture, tradeGood)) {
                style = TextLine.ITALIC;
            }
            infoColumns.add(new TextLine(tradeGood.getName(), style, new GuiText.GuiButtonReference(tradeGood)));
        }
        List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : linesWithColumns) {
            page.addLine(l);
        }
        book.addPage(page);
        return book;
    }

    public TextBook getBookVillageDetail(Culture culture, String itemKey, UserProfile profile) {
        List<TextLine> linesWithColumns;
        boolean displayFullInfos;
        VillageType villageType = culture.getVillageType(itemKey);
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        TextLine line = new TextLine(villageType.name + " (" + culture.getCultureString("village." + itemKey) + ")", "\u00a71", villageType.getIcon(), false);
        page.addLine(line);
        page.getLastLine().canCutAfter = false;
        page.addBlankLine();
        boolean knownVillageType = profile == null || profile.isVillageUnlocked(culture, villageType);
        boolean bl = displayFullInfos = knownVillageType || !MillConfigValues.TRAVEL_BOOK_LEARNING;
        if (!knownVillageType) {
            page.addLine(new TextLine(LanguageUtilities.string("travelbook.unknownvillage"), "\u00a74"));
            page.addBlankLine();
        }
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        if (displayFullInfos) {
            if (culture.hasCultureString("travelbook.village." + villageType.key + ".desc")) {
                page.addLine(culture.getCultureString("travelbook.village." + villageType.key + ".desc"));
                page.getLastLine().exportSpecialTag = EXPORT_TAG_MAIN_DESC;
                page.addBlankLine();
            }
            infoColumns.add(new TextLine("" + villageType.radius, new ItemStack((Item)Items.field_151148_bJ, 1), LanguageUtilities.string("travelbook.village_radius"), false));
            infoColumns.add(new TextLine("" + villageType.weight, new ItemStack(Blocks.field_150467_bQ, 1), LanguageUtilities.string("travelbook.village_weight"), false));
            linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
            for (TextLine textLine : linesWithColumns) {
                page.addLine(textLine);
            }
            page.addBlankLine();
        }
        ArrayList<String> validBiomes = new ArrayList<String>();
        for (String biomeName : villageType.biomes) {
            for (ResourceLocation rl : Biome.field_185377_q.func_148742_b()) {
                if (!((Biome)Biome.field_185377_q.func_82594_a((Object)rl)).func_185359_l().equalsIgnoreCase(biomeName)) continue;
                validBiomes.add(biomeName);
            }
        }
        if (validBiomes.size() > 0) {
            String string2 = validBiomes.stream().collect(Collectors.joining(", "));
            string2 = WordUtils.capitalizeFully((String)string2);
            page.addLine(LanguageUtilities.string("travelbook.village_biomes", string2));
            page.addBlankLine();
        }
        if (displayFullInfos) {
            if (villageType.hamlets.size() > 0) {
                page.addLine(LanguageUtilities.string("travelbook.village_hamlets"), TextLine.ITALIC);
                page.getLastLine().canCutAfter = false;
                for (String hamletKey : villageType.hamlets) {
                    VillageType hamlet = culture.getVillageType(hamletKey);
                    page.addLine(new TextLine(hamlet.name, new GuiText.GuiButtonReference(hamlet)));
                }
                page.addBlankLine();
            }
            page.addLine(LanguageUtilities.string("travelbook.village_townhall"), TextLine.ITALIC);
            if (villageType.centreBuilding != null) {
                page.addLine(new TextLine(villageType.centreBuilding.getNameNative(), new GuiText.GuiButtonReference(villageType.centreBuilding)));
            }
            if (villageType.customCentre != null) {
                page.addLine(LanguageUtilities.string("travelbook.customvillagecentre"));
            }
            page.addBlankLine();
            if (villageType.startBuildings.size() > 0) {
                page.addLine(LanguageUtilities.string("travelbook.village_start"), TextLine.ITALIC);
                page.getLastLine().canCutAfter = false;
                infoColumns.clear();
                for (BuildingPlanSet planSet : villageType.startBuildings) {
                    infoColumns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet)));
                }
                linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
                for (TextLine l : linesWithColumns) {
                    page.addLine(l);
                }
                page.addBlankLine();
            }
            if (villageType.coreBuildings.size() > 0) {
                page.addLine(LanguageUtilities.string("travelbook.village_core"), TextLine.ITALIC);
                page.getLastLine().canCutAfter = false;
                infoColumns.clear();
                for (BuildingPlanSet planSet : villageType.coreBuildings) {
                    infoColumns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet)));
                }
                linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
                for (TextLine l : linesWithColumns) {
                    page.addLine(l);
                }
                page.addBlankLine();
            }
            if (villageType.secondaryBuildings.size() > 0) {
                page.addLine(LanguageUtilities.string("travelbook.village_secondary"), TextLine.ITALIC);
                page.getLastLine().canCutAfter = false;
                infoColumns.clear();
                for (BuildingPlanSet planSet : villageType.secondaryBuildings) {
                    infoColumns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet)));
                }
                linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
                for (TextLine l : linesWithColumns) {
                    page.addLine(l);
                }
                page.addBlankLine();
            }
            if (villageType.extraBuildings.size() > 0) {
                page.addLine(LanguageUtilities.string("travelbook.village_extra"), TextLine.ITALIC);
                page.getLastLine().canCutAfter = false;
                infoColumns.clear();
                for (BuildingPlanSet planSet : villageType.extraBuildings) {
                    infoColumns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet)));
                }
                linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
                for (TextLine l : linesWithColumns) {
                    page.addLine(l);
                }
                page.addBlankLine();
            }
            if (villageType.playerBuildings.size() > 0) {
                page.addLine(LanguageUtilities.string("travelbook.village_player"), TextLine.ITALIC);
                page.getLastLine().canCutAfter = false;
                infoColumns.clear();
                for (BuildingPlanSet planSet : villageType.playerBuildings) {
                    infoColumns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet)));
                }
                linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
                for (TextLine l : linesWithColumns) {
                    page.addLine(l);
                }
                page.addBlankLine();
            }
            page.addBlankLine();
            page.addLine(LanguageUtilities.string("travelbook.village_rescost"), "\u00a71");
            page.getLastLine().canCutAfter = false;
            page.addBlankLine();
            page.getLastLine().canCutAfter = false;
            infoColumns.clear();
            Map<InvItem, Integer> map = villageType.computeVillageTypeCost();
            List resTypes = map.keySet().stream().sorted((r1, r2) -> ((Integer)resCost.get(r2)).compareTo((Integer)resCost.get(r1))).collect(Collectors.toList());
            for (InvItem res : resTypes) {
                TradeGood tradeGood = culture.getTradeGood(res);
                if (tradeGood == null) {
                    infoColumns.add(new TextLine("" + map.get(res), res.getItemStack(), true));
                    continue;
                }
                infoColumns.add(new TextLine("" + map.get(res), new GuiText.GuiButtonReference(tradeGood)));
            }
            linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
            for (TextLine l : linesWithColumns) {
                page.addLine(l);
            }
            page.addBlankLine();
        }
        book.addPage(page);
        book = this.offsetFirstLines(book);
        return book;
    }

    public TextBook getBookVillagerDetail(Culture culture, String itemKey, UserProfile profile) {
        VillagerType villagerType = culture.getVillagerType(itemKey);
        boolean knownVillager = profile == null || profile.isVillagerUnlocked(culture, villagerType);
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        String name = villagerType.getNameNativeAndTranslated();
        TextLine line = new TextLine(name, "\u00a71", villagerType.getIcon(), false);
        page.addLine(line);
        page.getLastLine().canCutAfter = false;
        page.addBlankLine();
        page.getLastLine().canCutAfter = false;
        if (!knownVillager) {
            page.addLine(new TextLine(LanguageUtilities.string("travelbook.unknownvillager"), "\u00a74"));
            page.addBlankLine();
        }
        if (!knownVillager && MillConfigValues.TRAVEL_BOOK_LEARNING) {
            book.addPage(page);
            this.getBookVillagerDetail_residence(culture, villagerType, page, false);
            book = this.offsetFirstLines(book);
            return book;
        }
        this.lineSizeInPx -= 80;
        if (culture.hasCultureString("travelbook.villager." + villagerType.key + ".desc")) {
            page.addLine(culture.getCultureString("travelbook.villager." + villagerType.key + ".desc"));
            page.getLastLine().exportSpecialTag = EXPORT_TAG_MAIN_DESC;
            page.addBlankLine();
        }
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        infoColumns.add(new TextLine("" + villagerType.health, new ItemStack((Item)Items.field_151030_Z, 1), LanguageUtilities.string("travelbook.health"), false));
        infoColumns.add(new TextLine("" + villagerType.baseAttackStrength, new ItemStack(Items.field_151040_l, 1), LanguageUtilities.string("travelbook.attackstrength"), false));
        if (villagerType.hireCost > 0) {
            String string = MillCommonUtilities.getShortPrice(villagerType.hireCost);
            infoColumns.add(new TextLine(string, new ItemStack((Item)MillItems.DENIER, 1), LanguageUtilities.string("travelbook.hirecost"), false));
        }
        for (String tag : VILLAGER_TAGS_TO_DISPLAY) {
            if (!villagerType.containsTags(tag)) continue;
            TextLine col1 = new TextLine(LanguageUtilities.string("travelbook.specialbehaviours." + tag), VILLAGER_TAGS_ICONS.get(tag), LanguageUtilities.string("travelbook.specialbehaviours." + tag + ".desc"), false);
            infoColumns.add(col1);
        }
        List<TextLine> list = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : list) {
            page.addLine(l);
        }
        page.addBlankLine();
        boolean showGoals = false;
        for (Goal goal : villagerType.goals) {
            if (!goal.travelBookShow) continue;
            showGoals = true;
        }
        if (showGoals) {
            page.addLine(culture.getCultureString("travelbook.goals"), "\u00a71");
            page.getLastLine().canCutAfter = false;
            for (Goal goal : villagerType.goals) {
                if (!goal.travelBookShow) continue;
                page.addLine(goal.gameName(null), goal.getIcon(), false);
            }
            page.addBlankLine();
        }
        infoColumns.clear();
        for (InvItem item : villagerType.requiredFoodAndGoods.keySet()) {
            TradeGood tradeGood = culture.getTradeGood(item);
            if (tradeGood == null) continue;
            infoColumns.add(new TextLine(item.getName(), new GuiText.GuiButtonReference(tradeGood)));
        }
        for (String toolCategory : villagerType.toolsCategoriesNeeded) {
            InvItem iconItem = VillagerConfig.DEFAULT_CONFIG.categories.get(toolCategory).get(0);
            infoColumns.add(new TextLine(LanguageUtilities.string("travelbook.toolscategory." + toolCategory), iconItem.getItemStack(), LanguageUtilities.string("travelbook.toolscategory." + toolCategory + ".desc"), false));
        }
        if (infoColumns.size() > 0) {
            page.addLine(LanguageUtilities.string("travelbook.neededitems"), "\u00a71");
            page.getLastLine().canCutAfter = false;
            List<TextLine> list2 = BookManager.splitInColumns(infoColumns, 2);
            for (TextLine l : list2) {
                page.addLine(l);
            }
        }
        this.getBookVillagerDetail_residence(culture, villagerType, page, true);
        book.addPage(page);
        book = this.offsetFirstLines(book);
        this.lineSizeInPx += 80;
        return book;
    }

    private void getBookVillagerDetail_residence(Culture culture, VillagerType villagerType, TextPage page, boolean villagerUnlocked) {
        List<TextLine> linesWithColumns;
        ArrayList<BuildingPlanSet> buildings = new ArrayList<BuildingPlanSet>();
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        for (BuildingPlanSet planSet : culture.ListPlanSets) {
            if (!planSet.getRandomStartingPlan().maleResident.contains(villagerType.key) && !planSet.getRandomStartingPlan().femaleResident.contains(villagerType.key)) continue;
            infoColumns.add(new TextLine(planSet.getNameNative(), new GuiText.GuiButtonReference(planSet)));
            buildings.add(planSet);
        }
        if (villagerUnlocked && infoColumns.size() > 0) {
            page.addBlankLine();
            page.addLine(LanguageUtilities.string("travelbook.residesin"), "\u00a71");
            page.getLastLine().canCutAfter = false;
            linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
            for (TextLine l : linesWithColumns) {
                page.addLine(l);
            }
        }
        infoColumns.clear();
        HashSet<VillageType> villages = new HashSet<VillageType>();
        for (BuildingPlanSet planSet : buildings) {
            for (VillageType village : culture.listVillageTypes) {
                if (village.centreBuilding != planSet && !village.startBuildings.contains(planSet) && !village.coreBuildings.contains(planSet) && !village.secondaryBuildings.contains(planSet) && !village.extraBuildings.contains(planSet)) continue;
                villages.add(village);
            }
        }
        for (VillageType villageType : villages) {
            infoColumns.add(new TextLine(villageType.name, new GuiText.GuiButtonReference(villageType)));
        }
        if (infoColumns.size() > 0) {
            page.addBlankLine();
            page.addLine(LanguageUtilities.string("travelbook.residesinvillage"), "\u00a71");
            page.getLastLine().canCutAfter = false;
            linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
            for (TextLine l : linesWithColumns) {
                page.addLine(l);
            }
        }
    }

    public TextBook getBookVillagersList(Culture culture, String category, UserProfile profile) {
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        page.addLine(LanguageUtilities.string("travelbook.villagerslist", culture.getAdjectiveTranslated()), "\u00a71", culture.getIcon(), false);
        page.addLine(LanguageUtilities.string("travelbook.villagerslistcategory", culture.getCategoryName(category)), culture.getCategoryIcon(category), false);
        page.addBlankLine();
        List<VillagerType> sortedVillagers = this.getCurrentVillagerList(culture, category);
        int nbKnownVillagers = profile.getNbUnlockedVillagers(culture, category);
        page.addLine(LanguageUtilities.string("travelbook.villagerslistcategory_unlocked", "" + nbKnownVillagers, "" + sortedVillagers.size()));
        page.addBlankLine();
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        for (VillagerType villagerType : sortedVillagers) {
            String style = "";
            if (!profile.isVillagerUnlocked(culture, villagerType)) {
                style = TextLine.ITALIC;
            }
            String name = villagerType.getNameNativeAndTranslated();
            infoColumns.add(new TextLine(name, style, new GuiText.GuiButtonReference(villagerType)));
        }
        List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : linesWithColumns) {
            page.addLine(l);
        }
        book.addPage(page);
        return book;
    }

    public TextBook getBookVillagesList(Culture culture, UserProfile profile) {
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        page.addLine(LanguageUtilities.string("travelbook.villageslist", culture.getAdjectiveTranslated()), "\u00a71", culture.getIcon(), false);
        page.addBlankLine();
        List<VillageType> sortedVillages = this.getCurrentVillageList(culture);
        int nbKnownVillages = profile.getNbUnlockedVillages(culture);
        page.addLine(LanguageUtilities.string("travelbook.villageslist_unlocked", "" + nbKnownVillages, "" + sortedVillages.size(), culture.getAdjectiveTranslated()));
        page.addBlankLine();
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        for (VillageType villageType : sortedVillages) {
            String style = "";
            if (!profile.isVillageUnlocked(culture, villageType)) {
                style = TextLine.ITALIC;
            }
            String translatedName = villageType.getNameNativeAndTranslated();
            infoColumns.add(new TextLine(translatedName, style, new GuiText.GuiButtonReference(villageType)));
        }
        List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
        for (TextLine l : linesWithColumns) {
            page.addLine(l);
        }
        book.addPage(page);
        return book;
    }

    public List<BuildingPlanSet> getCurrentBuildingList(Culture culture, String category) {
        List<BuildingPlanSet> sortedPlans = new ArrayList<BuildingPlanSet>(culture.ListPlanSets);
        if (category != null) {
            sortedPlans = sortedPlans.stream().filter(p -> p.getFirstStartingPlan().travelBookDisplay && category.equalsIgnoreCase(p.getFirstStartingPlan().travelBookCategory)).sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList());
        }
        return sortedPlans;
    }

    public List<TradeGood> getCurrentTradeGoodList(Culture culture, String category) {
        List<TradeGood> sortedGoods = new ArrayList<TradeGood>(culture.goodsList);
        if (category != null) {
            sortedGoods = sortedGoods.stream().filter(p -> p.travelBookDisplay && category.equals(p.travelBookCategory)).sorted((p1, p2) -> p1.name.compareTo(p2.name)).collect(Collectors.toList());
        }
        return sortedGoods;
    }

    public List<VillageType> getCurrentVillageList(Culture culture) {
        List<VillageType> sortedVillages = new ArrayList<VillageType>(culture.listVillageTypes);
        sortedVillages = sortedVillages.stream().filter(p -> p.travelBookDisplay).sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList());
        return sortedVillages;
    }

    public List<VillagerType> getCurrentVillagerList(Culture culture, String category) {
        List<VillagerType> sortedVillagers = new ArrayList<VillagerType>(culture.listVillagerTypes);
        if (category != null) {
            sortedVillagers = sortedVillagers.stream().filter(p -> p.travelBookDisplay && category.equals(p.travelBookCategory)).sorted((p1, p2) -> p1.name.compareTo(p2.name)).collect(Collectors.toList());
        }
        return sortedVillagers;
    }

    private TextBook offsetFirstLines(TextBook book) {
        book = this.adjustTextBookLineLength(book);
        for (TextPage apage : book.getPages()) {
            apage.getLine(0).setLineMarginLeft(10);
            apage.getLine(0).setLineMarginRight(10);
            if (apage.getNbLines() <= 1 || apage.getLine(0).getLineHeight() >= 18) continue;
            apage.getLine(1).setLineMarginLeft(10);
            apage.getLine(1).setLineMarginRight(10);
        }
        return book;
    }
}

