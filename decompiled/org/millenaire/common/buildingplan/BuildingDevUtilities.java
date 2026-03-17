/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 */
package org.millenaire.common.buildingplan;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.buildingplan.PointType;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.LanguageData;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;

public class BuildingDevUtilities {
    private static final String EOL = "\n";

    public static void exportMissingTravelBookDesc() {
        int translationLogLevel = MillConfigValues.LogTranslation;
        MillConfigValues.LogTranslation = 0;
        for (Culture culture : Culture.ListCultures) {
            File dir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "travelbook");
            dir.mkdirs();
            File file = new File(dir, culture.key + "_travelbook.txt");
            try {
                String key;
                BufferedWriter writer = MillCommonUtilities.getWriter(file);
                writer.write("//Elements without travel book descriptions\n\n");
                for (VillagerType villagerType : culture.listVillagerTypes.stream().filter(p -> p.travelBookDisplay).sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
                    key = "travelbook.villager." + villagerType.key + ".desc";
                    if (culture.hasCultureString(key)) continue;
                    writer.write(key + "=" + EOL);
                }
                writer.write(EOL);
                for (VillageType villageType : culture.listVillageTypes.stream().filter(p -> p.travelBookDisplay).sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
                    key = "travelbook.village." + villageType.key + ".desc";
                    if (culture.hasCultureString(key)) continue;
                    writer.write(key + "=" + EOL);
                }
                writer.write(EOL);
                for (BuildingPlanSet planSet : culture.ListPlanSets.stream().filter(p -> p.getFirstStartingPlan().travelBookDisplay).sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
                    key = "travelbook.building." + planSet.key + ".desc";
                    if (culture.hasCultureString(key)) continue;
                    writer.write(key + "=" + EOL);
                }
                writer.write(EOL);
                for (TradeGood tradeGood : culture.goodsList.stream().filter(p -> p.travelBookDisplay).sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
                    key = "travelbook.trade_good." + tradeGood.key + ".desc";
                    if (culture.hasCultureString(key)) continue;
                    writer.write(key + "=" + EOL);
                }
                writer.close();
            }
            catch (Exception e) {
                MillLog.printException(e);
            }
        }
        MillConfigValues.LogTranslation = translationLogLevel;
    }

    public static void exportTravelBookDescCSV() {
        int translationLogLevel = MillConfigValues.LogTranslation;
        MillConfigValues.LogTranslation = 0;
        HashMap<String, String> tradeGoodsDesc = new HashMap<String, String>();
        HashMap<String, String> tradeGoodsCultures = new HashMap<String, String>();
        for (Culture culture : Culture.ListCultures) {
            File dir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "travelbook");
            dir.mkdirs();
            File file = new File(dir, culture.key + "_travelbook.csv");
            try {
                String key;
                BufferedWriter writer = MillCommonUtilities.getWriter(file);
                for (VillagerType villagerType : culture.listVillagerTypes.stream().sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
                    key = "travelbook.villager." + villagerType.key + ".desc";
                    if (!culture.hasCultureString(key)) {
                        writer.write(key + ";" + EOL);
                        continue;
                    }
                    writer.write(key + ";" + culture.getCultureString(key) + EOL);
                }
                for (VillageType villageType : culture.listVillageTypes.stream().sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
                    key = "travelbook.village." + villageType.key + ".desc";
                    if (!culture.hasCultureString(key)) {
                        writer.write(key + ";" + EOL);
                        continue;
                    }
                    writer.write(key + ";" + culture.getCultureString(key) + EOL);
                }
                for (BuildingPlanSet planSet : culture.ListPlanSets.stream().sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
                    key = "travelbook.building." + planSet.key + ".desc";
                    if (!culture.hasCultureString(key)) {
                        writer.write(key + ";" + EOL);
                        continue;
                    }
                    writer.write(key + ";" + culture.getCultureString(key) + EOL);
                }
                for (TradeGood tradeGood : culture.goodsList.stream().sorted((p1, p2) -> p1.key.compareTo(p2.key)).collect(Collectors.toList())) {
                    key = "travelbook.trade_good." + tradeGood.key + ".desc";
                    if (!culture.hasCultureString(key)) {
                        writer.write(key + ";" + EOL);
                        if (!tradeGoodsDesc.containsKey(key)) {
                            tradeGoodsDesc.put(key, null);
                        }
                    } else {
                        writer.write(key + ";" + culture.getCultureString(key) + EOL);
                        tradeGoodsDesc.put(key, culture.getCultureString(key));
                    }
                    String treadeGoodCulture = culture.key + " (" + tradeGood.travelBookCategory + ")";
                    if (tradeGoodsCultures.containsKey(key)) {
                        tradeGoodsCultures.put(key, (String)tradeGoodsCultures.get(key) + " " + treadeGoodCulture);
                        continue;
                    }
                    tradeGoodsCultures.put(key, treadeGoodCulture);
                }
                writer.close();
            }
            catch (Exception e) {
                MillLog.printException(e);
            }
        }
        File dir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "travelbook");
        dir.mkdirs();
        File file = new File(dir, "tradegoods.csv");
        try {
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            for (String key : tradeGoodsDesc.keySet().stream().sorted().collect(Collectors.toList())) {
                if (tradeGoodsDesc.get(key) == null) {
                    writer.write(key + ";;;;" + (String)tradeGoodsCultures.get(key) + EOL);
                    continue;
                }
                writer.write(key + ";" + (String)tradeGoodsDesc.get(key) + ";;;" + (String)tradeGoodsCultures.get(key) + EOL);
            }
            writer.close();
        }
        catch (Exception e) {
            MillLog.printException(e);
        }
        MillConfigValues.LogTranslation = translationLogLevel;
    }

    public static void generateBuildingRes() {
        File file = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "resources used.txt");
        try {
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            if (MillConfigValues.DEV) {
                BuildingDevUtilities.generateSignBuildings(writer);
            }
            for (Culture culture : Culture.ListCultures) {
                writer.write(culture.key + ": " + EOL);
                BuildingDevUtilities.generateVillageTypeListing(writer, culture.listVillageTypes);
                writer.write(EOL);
                BuildingDevUtilities.generateVillageTypeListing(writer, culture.listLoneBuildingTypes);
            }
            writer.write(EOL);
            writer.write(EOL);
            for (Culture culture : Culture.ListCultures) {
                for (BuildingPlanSet set : culture.ListPlanSets) {
                    BuildingDevUtilities.writePlanCostWikiStyle(set, writer);
                }
            }
            writer.close();
        }
        catch (Exception e) {
            MillLog.printException(e);
        }
        if (MillConfigValues.LogBuildingPlan >= 1) {
            MillLog.major(null, "Wrote resources used.txt");
        }
        for (Culture culture : Culture.ListCultures) {
            BuildingDevUtilities.generateCultureBuildingRes(culture);
        }
    }

    public static void generateColourSheet() {
        try {
            if (MillConfigValues.LogBuildingPlan >= 1) {
                MillLog.major(null, "Generating colour sheet.");
            }
            BufferedImage pict = new BufferedImage(200, PointType.colourPoints.size() * 20 + 25, 1);
            Graphics2D graphics = pict.createGraphics();
            graphics.setColor(new Color(0xFFFFFF));
            graphics.fillRect(0, 0, pict.getWidth(), pict.getHeight());
            graphics.setColor(new Color(0));
            graphics.drawString("Generated colour sheet.", 5, 20);
            int pos = 1;
            for (File loadDir : Mill.loadingDirs) {
                File mainList = new File(loadDir, "blocklist.txt");
                if (!mainList.exists()) continue;
                pos = BuildingDevUtilities.generateColourSheetHandleFile(graphics, pos, mainList);
            }
            try {
                ImageIO.write((RenderedImage)pict, "png", new File(MillCommonUtilities.getMillenaireContentDir(), "Colour Sheet.png"));
            }
            catch (Exception e) {
                MillLog.printException(e);
            }
            if (MillConfigValues.LogBuildingPlan >= 1) {
                MillLog.major(null, "Finished generating colour sheet.");
            }
        }
        catch (Exception e) {
            MillLog.printException("Exception when trying to generate Colour Sheet:", e);
        }
    }

    private static int generateColourSheetHandleFile(Graphics2D graphics, int pos, File file) {
        try {
            String line;
            BufferedReader reader = MillCommonUtilities.getReader(file);
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() <= 0 || line.startsWith("//")) continue;
                String[] params = line.split(";", -1);
                String[] rgb = params[4].split("/", -1);
                int colour = (Integer.parseInt(rgb[0]) << 16) + (Integer.parseInt(rgb[1]) << 8) + (Integer.parseInt(rgb[2]) << 0);
                graphics.setColor(new Color(0));
                graphics.drawString(params[0], 20, 17 + 20 * pos);
                graphics.setColor(new Color(colour));
                graphics.fillRect(0, 5 + 20 * pos, 15, 15);
                ++pos;
            }
        }
        catch (Exception e) {
            MillLog.printException(e);
        }
        return pos;
    }

    private static void generateCultureBuildingRes(Culture culture) {
        File file = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "resources used " + culture.key + ".txt");
        try {
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            for (BuildingPlanSet set : culture.ListPlanSets) {
                BuildingDevUtilities.writePlanCostTextStyle(set, writer);
            }
            writer.close();
        }
        catch (Exception e) {
            MillLog.printException(e);
        }
    }

    private static void generateSignBuildings(BufferedWriter writer) throws Exception {
        writer.write("\n\n\nBuildings with signs (not panels):\n\n\n");
        for (Culture culture : Culture.ListCultures) {
            for (BuildingPlanSet set : culture.ListPlanSets) {
                for (BuildingPlan[] plans : set.plans) {
                    for (BuildingPlan plan : plans) {
                        if (plan.containsTags("hof")) continue;
                        PointType[][][] pointTypeArray = plan.plan;
                        int n = pointTypeArray.length;
                        for (int i = 0; i < n; ++i) {
                            PointType[][] level;
                            PointType[][] pointTypeArray2 = level = pointTypeArray[i];
                            int n2 = pointTypeArray2.length;
                            for (int j = 0; j < n2; ++j) {
                                PointType[] row;
                                for (PointType pt : row = pointTypeArray2[j]) {
                                    if (pt == null || pt.specialType == null || !pt.specialType.startsWith("plainSignGuess")) continue;
                                    writer.write("Sign in " + plan.toString() + EOL);
                                }
                            }
                        }
                    }
                }
            }
        }
        writer.write("\n\n\n");
    }

    public static void generateTranslatedHoFData(LanguageData language) {
        List<String> hofData = LanguageUtilities.getHoFData();
        File file = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "hof_" + language.language + ".txt");
        try {
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            boolean isFirstLine = true;
            for (String line : hofData) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] lines = line.split(";");
                String output = lines[0];
                int roleStart = 1;
                if (lines[1].length() > 0 && !lines[1].startsWith("hof.")) {
                    output = output + " " + lines[1];
                    roleStart = 2;
                }
                for (int i = roleStart; i < lines.length; ++i) {
                    if (lines[i].length() <= 0) continue;
                    output = lines[i].endsWith("2") ? output + " " : output + ";";
                    if (language.strings.containsKey(lines[i].toLowerCase())) {
                        output = output + language.strings.get(lines[i].toLowerCase());
                        continue;
                    }
                    MillLog.error(null, "Unknown HoF translation: " + lines[i] + " in language " + language.language);
                }
                writer.write(output);
                writer.newLine();
            }
            writer.close();
        }
        catch (Exception e) {
            MillLog.printException(e);
        }
    }

    private static void generateVillageTypeListing(BufferedWriter writer, List<VillageType> villages) throws IOException {
        for (VillageType villageType : villages) {
            Map<InvItem, Integer> villageCost = villageType.computeVillageTypeCost();
            writer.write(villageType.key + " resource use: " + EOL);
            for (InvItem key : villageCost.keySet()) {
                writer.write(key.getName() + ": " + villageCost.get(key) + EOL);
            }
            writer.write(EOL);
        }
    }

    /*
     * WARNING - void declaration
     */
    public static void generateWikiTable() throws MillLog.MillenaireException {
        HashMap<InvItem, String> picts = new HashMap<InvItem, String>();
        HashMap<InvItem, String> links = new HashMap<InvItem, String>();
        picts.put(InvItem.createInvItem(Blocks.field_150364_r, -1), "Wood_Any.gif");
        picts.put(InvItem.createInvItem(Blocks.field_150364_r, 0), "Wood.png");
        picts.put(InvItem.createInvItem(Blocks.field_150364_r, 1), "Wood_Pine.png");
        picts.put(InvItem.createInvItem(Blocks.field_150364_r, 2), "Wood_Birch.png");
        picts.put(InvItem.createInvItem(Blocks.field_150364_r, 3), "Wood_Jungle.png");
        picts.put(InvItem.createInvItem(Blocks.field_150363_s, 0), "Wood_Acacia.png");
        picts.put(InvItem.createInvItem(Blocks.field_150363_s, 1), "Wood_DarkOak.png");
        picts.put(InvItem.createInvItem(Blocks.field_150348_b, 0), "Stone.png");
        picts.put(InvItem.createInvItem(Blocks.field_150359_w, 0), "Glass.png");
        picts.put(InvItem.createInvItem(Blocks.field_150325_L, 0), "White_Wool.png");
        picts.put(InvItem.createInvItem(Blocks.field_150322_A, 0), "Sandstone.png");
        picts.put(InvItem.createInvItem(Blocks.field_150347_e, 0), "Cobblestone.png");
        picts.put(InvItem.createInvItem(Blocks.field_150336_V, 0), "Brick.png");
        picts.put(InvItem.createInvItem((Block)Blocks.field_150354_m, 0), "Sand.png");
        picts.put(InvItem.createInvItem(Blocks.field_150426_aN, 0), "Glowstone_(Block).png");
        picts.put(InvItem.createInvItem(Blocks.field_150342_X, 0), "Bookshelf.png");
        picts.put(InvItem.createInvItem(Blocks.field_150351_n, 0), "Gravel.png");
        picts.put(InvItem.createInvItem(Blocks.field_150322_A, 2), "SmoothSandstone.png");
        picts.put(InvItem.createInvItem(Blocks.field_150417_aV, 3), "ChiselledStoneBricks.png");
        picts.put(InvItem.createInvItem(Blocks.field_150417_aV, 2), "CrackedStoneBricks.png");
        picts.put(InvItem.createInvItem((Block)Blocks.field_150329_H, 1), "TallGrass.png");
        picts.put(InvItem.createInvItem((Block)Blocks.field_150329_H, 2), "Fern.png");
        picts.put(InvItem.createInvItem(Blocks.field_150341_Y, 0), "MossyCobblestone.png");
        picts.put(InvItem.createInvItem(Blocks.field_150417_aV, 1), "MossyStoneBricks.png");
        picts.put(InvItem.createInvItem(Blocks.field_150366_p, 0), "Ore_Iron.png");
        picts.put(InvItem.createInvItem(Blocks.field_150365_q, 0), "Ore_Coal.png");
        picts.put(InvItem.createInvItem(Blocks.field_150352_o, 0), "Ore_Gold.png");
        picts.put(InvItem.createInvItem(Blocks.field_150450_ax, 0), "Ore_Redstone.png");
        picts.put(InvItem.createInvItem(Blocks.field_150369_x, 0), "Ore_Lapis_Lazuli.png");
        picts.put(InvItem.createInvItem(Blocks.field_150482_ag, 0), "Ore_Diamond.png");
        picts.put(InvItem.createInvItem(Blocks.field_150428_aP, 0), "Jack-O-Lantern.png");
        picts.put(InvItem.createInvItem(Blocks.field_150440_ba, 0), "Melon (Block).png");
        picts.put(InvItem.createInvItem(Blocks.field_150368_y, 0), "Lapis_Lazuli_(Block).png");
        picts.put(InvItem.createInvItem(Blocks.field_150437_az, 0), "Redstone_Torch.png");
        picts.put(InvItem.createInvItem(Blocks.field_150357_h, 0), "Bedrock.png");
        picts.put(InvItem.createInvItem(Blocks.field_150388_bm, 0), "Nether_Wart.png");
        picts.put(InvItem.createInvItem((Block)Blocks.field_150353_l, 0), "Lava.png");
        picts.put(InvItem.createInvItem((Block)Blocks.field_150356_k, 0), "Lava.png");
        picts.put(InvItem.createInvItem(Blocks.field_150430_aB, 0), "Stone_Button.png");
        picts.put(InvItem.createInvItem((Block)Blocks.field_150488_af, 0), "Redstone_Dust.png");
        picts.put(InvItem.createInvItem(Blocks.field_150348_b, 0), "Stone.png");
        picts.put(InvItem.createInvItem(Items.field_151042_j, 0), "Ironitm.png");
        picts.put(InvItem.createInvItem(Items.field_151043_k, 0), "Golditm.png");
        picts.put(InvItem.createInvItem((Block)MillBlocks.WOOD_DECORATION, 0), "ML_colombages_plain.png");
        links.put(InvItem.createInvItem((Block)MillBlocks.WOOD_DECORATION, 0), "|link=Norman:Colombages");
        picts.put(InvItem.createInvItem((Block)MillBlocks.WOOD_DECORATION, 1), "ML_colombages_cross.png");
        links.put(InvItem.createInvItem((Block)MillBlocks.WOOD_DECORATION, 1), "|link=Norman:Colombages");
        picts.put(InvItem.createInvItem((Block)MillBlocks.WOOD_DECORATION, 2), "ML_Thatch.png");
        links.put(InvItem.createInvItem((Block)MillBlocks.WOOD_DECORATION, 2), "|link=Japanese:Thatch");
        picts.put(InvItem.createInvItem((Block)MillBlocks.STONE_DECORATION, 1), "ML_whitewashedbricks.png");
        links.put(InvItem.createInvItem((Block)MillBlocks.STONE_DECORATION, 1), "|link=Hindi:Cooked brick");
        picts.put(InvItem.createInvItem((Block)MillBlocks.STONE_DECORATION, 0), "ML_mudbrick.png");
        links.put(InvItem.createInvItem((Block)MillBlocks.STONE_DECORATION, 0), "|link=Hindi:Mud brick");
        picts.put(InvItem.createInvItem((Block)MillBlocks.STONE_DECORATION, 2), "ML_Mayan_Gold.png");
        links.put(InvItem.createInvItem((Block)MillBlocks.STONE_DECORATION, 2), "|link=Maya:Gold Ornament");
        picts.put(InvItem.createInvItem((Block)MillBlocks.PAPER_WALL, 0), "ML_paperwall.png");
        links.put(InvItem.createInvItem((Block)MillBlocks.PAPER_WALL, 0), "|link=Japanese:Paper Wall");
        picts.put(InvItem.createInvItem(MillItems.TAPESTRY, 0), "ML_tapestry.png");
        links.put(InvItem.createInvItem(MillItems.TAPESTRY, 0), "|link=Norman:Tapisserie");
        picts.put(InvItem.createInvItem(MillItems.INDIAN_STATUE, 0), "ML_IndianStatue.png");
        links.put(InvItem.createInvItem(MillItems.INDIAN_STATUE, 0), "|link=Hindi:Statue");
        picts.put(InvItem.createInvItem(MillItems.MAYAN_STATUE, 0), "ML_MayanStatue.png");
        links.put(InvItem.createInvItem(MillItems.MAYAN_STATUE, 0), "|link=Maya:Carving");
        picts.put(InvItem.createInvItem(MillItems.BYZANTINE_ICON_SMALL, 0), "ML_ByzantineIconSmall.png");
        links.put(InvItem.createInvItem(MillItems.BYZANTINE_ICON_SMALL, 0), "|link=Byzantine:IIcon");
        picts.put(InvItem.createInvItem(MillItems.BYZANTINE_ICON_MEDIUM, 0), "ML_ByzantineIconMedium.png");
        links.put(InvItem.createInvItem(MillItems.BYZANTINE_ICON_MEDIUM, 0), "|link=Byzantine:IIcon");
        picts.put(InvItem.createInvItem(MillItems.BYZANTINE_ICON_LARGE, 0), "ML_ByzantineIconLarge.png");
        links.put(InvItem.createInvItem(MillItems.BYZANTINE_ICON_LARGE, 0), "|link=Byzantine:IIcon");
        picts.put(InvItem.createInvItem((Block)MillBlocks.BYZANTINE_TILES, 0), "ML_byzSlab.png");
        links.put(InvItem.createInvItem((Block)MillBlocks.BYZANTINE_TILES, 0), "|link=Blocks#Byzantine");
        try {
            HashMap<String, Integer> nameCount = new HashMap<String, Integer>();
            HashMap<BuildingPlanSet, String> uniqueNames = new HashMap<BuildingPlanSet, String>();
            for (Culture culture : Culture.ListCultures) {
                for (BuildingPlanSet set : culture.ListPlanSets) {
                    String name = set.plans.get((int)0)[0].nativeName;
                    if (!nameCount.containsKey(name)) {
                        nameCount.put(name, 1);
                        continue;
                    }
                    nameCount.put(name, (Integer)nameCount.get(name) + 1);
                }
            }
            for (Culture culture : Culture.ListCultures) {
                for (BuildingPlanSet set : culture.ListPlanSets) {
                    if ((Integer)nameCount.get(set.plans.get((int)0)[0].nativeName) > 1) {
                        uniqueNames.put(set, set.plans.get((int)0)[0].nativeName + "~" + set.key);
                        continue;
                    }
                    uniqueNames.put(set, set.plans.get((int)0)[0].nativeName);
                }
            }
            File file = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "resources used wiki building list.txt");
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            writer.write("{| class=\"wikitable\"\n");
            writer.write("|-\n");
            writer.write("! Requirements Template Building Name\n");
            writer.write("|-\n");
            for (Culture culture : Culture.ListCultures) {
                for (BuildingPlanSet set : culture.ListPlanSets) {
                    writer.write("! " + (String)uniqueNames.get(set) + EOL);
                    writer.write("|-\n");
                }
            }
            writer.write("|}");
            writer.close();
            file = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "resources used wiki.txt");
            writer = MillCommonUtilities.getWriter(file);
            writer.write("{{#switch: {{{1|{{BASEPAGENAME}}}}}\n");
            for (Culture culture : Culture.ListCultures) {
                for (BuildingPlanSet set : culture.ListPlanSets) {
                    writer.write("|" + (String)uniqueNames.get(set) + " = <table><tr><td style=\"vertical-align:top;\">" + EOL);
                    for (BuildingPlan[] plans : set.plans) {
                        if (set.plans.size() > 1) {
                            writer.write("<table class=\"reqirements\"><tr><th scope=\"col\" style=\"text-align:center;\">Variation " + (char)(65 + plans[0].variation) + "</th>");
                        } else {
                            writer.write("<table class=\"reqirements\"><tr><th scope=\"col\" style=\"text-align:center;\"></th>");
                        }
                        ArrayList<InvItem> items = new ArrayList<InvItem>();
                        for (BuildingPlan buildingPlan : plans) {
                            for (InvItem key : buildingPlan.resCost.keySet()) {
                                if (items.contains(key)) continue;
                                items.add(key);
                            }
                        }
                        Collections.sort(items);
                        for (InvItem key : items) {
                            void var16_23;
                            String pict = "Unknown Pict:" + key.item + "/" + key.meta;
                            String string = "";
                            if (picts.containsKey(key)) {
                                pict = (String)picts.get(key);
                            }
                            if (links.containsKey(key)) {
                                String string2 = (String)links.get(key);
                            }
                            writer.write("<td>[[File:" + pict + "|32px" + (String)var16_23 + "|" + key.getName() + "]]</td>");
                        }
                        writer.write("</tr>\n");
                        for (BuildingPlan buildingPlan : plans) {
                            if (buildingPlan.level == 0) {
                                writer.write("<tr><th scope=\"row\">Construction</th>");
                            } else {
                                writer.write("<tr><th scope=\"row\">Upgrade " + buildingPlan.level + "</th>");
                            }
                            for (InvItem key : items) {
                                if (buildingPlan.resCost.containsKey(key)) {
                                    writer.write("<td>" + buildingPlan.resCost.get(key) + "</td>");
                                    continue;
                                }
                                writer.write("<td></td>");
                            }
                            writer.write("</tr>\n");
                        }
                        writer.write("</table>\n");
                    }
                    writer.write("</table>\n\n");
                }
            }
            writer.write("| #default = {{msgbox | title = Requirements not found| text = The requirements template couldn't find the upgrade table of the building you were looking for.Please consult the building list at [[Template:Requirements|this page]] to find the correct name.}}}}<noinclude>[[Category:Templates formatting|{{PAGENAME}}]]{{documentation}}</noinclude>");
            writer.close();
        }
        catch (Exception e) {
            MillLog.printException(e);
        }
        if (MillConfigValues.LogBuildingPlan >= 1) {
            MillLog.major(null, "Wrote resources used wiki.txt");
        }
    }

    public static void writePlanCostTextStyle(BuildingPlanSet set, BufferedWriter writer) throws IOException {
        writer.write(set.plans.get((int)0)[0].nativeName + EOL + set.plans.get((int)0)[0].buildingKey + EOL + EOL);
        for (BuildingPlan[] plans : set.plans) {
            if (set.plans.size() > 1) {
                writer.write("===Variation " + (char)(65 + plans[0].variation) + "===" + EOL);
            }
            writer.write("\nTotal Cost\n");
            HashMap<InvItem, Integer> totalCost = new HashMap<InvItem, Integer>();
            for (BuildingPlan buildingPlan : plans) {
                for (InvItem key : buildingPlan.resCost.keySet()) {
                    if (totalCost.containsKey(key)) {
                        totalCost.put(key, (Integer)totalCost.get(key) + buildingPlan.resCost.get(key));
                        continue;
                    }
                    totalCost.put(key, buildingPlan.resCost.get(key));
                }
            }
            for (InvItem key : totalCost.keySet()) {
                writer.write(key.getName() + "(" + key.item.getRegistryName() + "/" + key.meta + "): " + totalCost.get(key) + EOL);
            }
            for (BuildingPlan buildingPlan : plans) {
                if (buildingPlan.level == 0) {
                    writer.write("\nInitial Construction\n");
                } else {
                    writer.write("\nUpgrade " + buildingPlan.level + EOL);
                }
                for (InvItem key : buildingPlan.resCost.keySet()) {
                    writer.write(key.getName() + "(" + key.item.getRegistryName() + "/" + key.meta + "): " + buildingPlan.resCost.get(key) + EOL);
                }
            }
        }
        writer.write(EOL);
    }

    public static void writePlanCostWikiStyle(BuildingPlanSet set, BufferedWriter writer) throws IOException {
        writer.write(set.plans.get((int)0)[0].nativeName + EOL + set.plans.get((int)0)[0].buildingKey + EOL + EOL);
        writer.write("==Requirements==\n");
        for (BuildingPlan[] plans : set.plans) {
            if (set.plans.size() > 1) {
                writer.write("===Variation " + (char)(65 + plans[0].variation) + "===" + EOL);
            }
            for (BuildingPlan plan : plans) {
                if (plan.level == 0) {
                    writer.write("Initial Construction\n\n");
                } else {
                    writer.write("Upgrade " + plan.level + EOL + EOL);
                }
                writer.write("{| border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width: 300px;\"\n");
                writer.write("! scope=\"col\"|Resource\n");
                writer.write("! scope=\"col\"|Quantity\n");
                for (InvItem key : plan.resCost.keySet()) {
                    writer.write("|-\n");
                    writer.write("| style=\"text-align: center; \"|" + key.getName() + EOL);
                    writer.write("| style=\"text-align: center; \"|" + plan.resCost.get(key) + EOL);
                }
                writer.write("|}\n\n\n");
            }
        }
    }
}

