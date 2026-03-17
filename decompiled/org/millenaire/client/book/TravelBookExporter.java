/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.texture.TextureUtil
 *  net.minecraft.client.shader.Framebuffer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL30
 */
package org.millenaire.client.book;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.BookManagerTravelBook;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.book.TextPage;
import org.millenaire.client.gui.text.GuiTravelBook;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.VillagerRecord;

public class TravelBookExporter {
    private static Map<String, ItemStack> itemsToRender = new HashMap<String, ItemStack>();
    private static final String EOL = "\n";

    private static String escapeQuotes(String label) {
        label = label.replaceAll("'", "''");
        label = label.replaceAll("\"", "\\\\\"");
        return label;
    }

    private static void exportAllBuildings(BookManagerTravelBook travelBookManager, BufferedWriter writer, Gson gson, String language) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        boolean firstValues = true;
        writer.write("DELETE from encyclopediadata WHERE type='buildings' AND language='" + language + "';" + EOL);
        for (Culture culture : Culture.ListCultures) {
            for (BuildingPlanSet planSet : culture.ListPlanSets) {
                if (!planSet.getFirstStartingPlan().travelBookDisplay) continue;
                if (firstValues) {
                    firstValues = false;
                }
                TextBook book = travelBookManager.getBookBuildingDetail(culture, planSet.key, null);
                String json = TravelBookExporter.escapeQuotes(gson.toJson((Object)new ExportBook(book)));
                String label = planSet.getNameNativeAndTranslated();
                label = TravelBookExporter.escapeQuotes(label);
                String categoryLabel = TravelBookExporter.escapeQuotes(culture.getCultureString("travelbook_category." + planSet.getFirstStartingPlan().travelBookCategory));
                String itemref = culture.key + "-buildings-" + planSet.key + "-" + language;
                writer.write("INSERT INTO encyclopediadata (itemref,type,language,culture,category,categorylabel,itemkey,label,iconkey,data) VALUES \n");
                writer.write("('" + itemref + "','buildings','" + language + "','" + culture.key + "','" + planSet.getFirstStartingPlan().travelBookCategory + "','" + categoryLabel + "','" + planSet.key + "','" + label + "','" + TravelBookExporter.getIconKey(planSet.getIcon()) + "','" + json + "');" + EOL);
            }
        }
        writer.write(";\n");
    }

    private static void exportAllCultures(BookManagerTravelBook travelBookManager, BufferedWriter writer, Gson gson, String language) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        boolean firstValues = true;
        writer.write("DELETE from encyclopediadata WHERE type='cultures' AND language='" + language + "';" + EOL);
        for (Culture culture : Culture.ListCultures) {
            if (firstValues) {
                firstValues = false;
            }
            TextBook book = travelBookManager.getBookCultureForJSONExport(culture, null);
            String json = TravelBookExporter.escapeQuotes(gson.toJson((Object)new ExportBook(book)));
            String label = culture.getNameTranslated();
            label = TravelBookExporter.escapeQuotes(label);
            String itemref = culture.key + "-cultures-" + culture.key + "-" + language;
            writer.write("INSERT INTO encyclopediadata (itemref,type,language,culture,category,itemkey,label,iconkey,data) VALUES \n");
            writer.write("('" + itemref + "','cultures','" + language + "','" + culture.key + "',NULL,'" + culture.key + "','" + label + "','" + TravelBookExporter.getIconKey(culture.getIcon()) + "','" + json + "');" + EOL);
        }
        writer.write(";\n");
    }

    private static void exportAllTradeGoods(BookManagerTravelBook travelBookManager, BufferedWriter writer, Gson gson, String language) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        boolean firstValues = true;
        writer.write("DELETE from encyclopediadata WHERE type='tradegoods' AND language='" + language + "';" + EOL);
        for (Culture culture : Culture.ListCultures) {
            for (TradeGood tradeGood : culture.goodsList) {
                if (!tradeGood.travelBookDisplay || tradeGood.travelBookCategory.equals("foreigntrade")) continue;
                if (firstValues) {
                    firstValues = false;
                }
                TextBook book = travelBookManager.getBookTradeGoodDetail(culture, tradeGood.key, null);
                String json = TravelBookExporter.escapeQuotes(gson.toJson((Object)new ExportBook(book)));
                String label = tradeGood.getName();
                label = TravelBookExporter.escapeQuotes(label);
                String categoryLabel = TravelBookExporter.escapeQuotes(culture.getCultureString("travelbook_category." + tradeGood.travelBookCategory));
                String itemref = culture.key + "-tradegoods-" + tradeGood.key + "-" + language;
                writer.write("INSERT INTO encyclopediadata (itemref,type,language,culture,category,categorylabel,itemkey,label,iconkey,data) VALUES \n");
                writer.write("('" + itemref + "','tradegoods','" + language + "','" + culture.key + "','" + tradeGood.travelBookCategory + "','" + categoryLabel + "','" + tradeGood.key + "','" + label + "','" + TravelBookExporter.getIconKey(tradeGood.getIcon()) + "','" + json + "');" + EOL);
            }
        }
        writer.write(";\n");
    }

    private static void exportAllVillagers(BookManagerTravelBook travelBookManager, BufferedWriter writer, Gson gson, String language) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        writer.write("DELETE from encyclopediadata WHERE type='villagers' AND language='" + language + "';" + EOL);
        boolean firstValues = true;
        for (Culture culture : Culture.ListCultures) {
            for (VillagerType vtype : culture.listVillagerTypes) {
                if (!vtype.travelBookDisplay) continue;
                if (firstValues) {
                    firstValues = false;
                }
                TextBook book = travelBookManager.getBookVillagerDetail(culture, vtype.key, null);
                String json = TravelBookExporter.escapeQuotes(gson.toJson((Object)new ExportBook(book)));
                String label = vtype.getNameNativeAndTranslated();
                label = TravelBookExporter.escapeQuotes(label);
                String categoryLabel = TravelBookExporter.escapeQuotes(culture.getCultureString("travelbook_category." + vtype.travelBookCategory));
                String itemref = culture.key + "-villagers-" + vtype.key + "-" + language;
                writer.write("INSERT INTO encyclopediadata (itemref,type,language,culture,category,categorylabel,itemkey,label,iconkey,data) VALUES \n");
                writer.write("('" + itemref + "','villagers','" + language + "','" + culture.key + "','" + vtype.travelBookCategory + "','" + categoryLabel + "','" + vtype.key + "','" + label + "','" + TravelBookExporter.getIconKey(vtype.getIcon()) + "','" + json + "');" + EOL);
            }
        }
        writer.write(";\n");
    }

    private static void exportAllVillages(BookManagerTravelBook travelBookManager, BufferedWriter writer, Gson gson, String language) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        boolean firstValues = true;
        writer.write("DELETE from encyclopediadata WHERE type='villages' AND language='" + language + "';" + EOL);
        for (Culture culture : Culture.ListCultures) {
            for (VillageType vtype : culture.listVillageTypes) {
                if (!vtype.travelBookDisplay) continue;
                if (firstValues) {
                    firstValues = false;
                }
                TextBook book = travelBookManager.getBookVillageDetail(culture, vtype.key, null);
                String json = TravelBookExporter.escapeQuotes(gson.toJson((Object)new ExportBook(book)));
                String label = vtype.getNameNativeAndTranslated();
                label = TravelBookExporter.escapeQuotes(label);
                String itemref = culture.key + "-villages-" + vtype.key + "-" + language;
                writer.write("INSERT INTO encyclopediadata (itemref,type,language,culture,category,itemkey,label,iconkey,data) VALUES \n");
                writer.write("('" + itemref + "','villages','" + language + "','" + culture.key + "',NULL,'" + vtype.key + "','" + label + "','" + TravelBookExporter.getIconKey(vtype.getIcon()) + "','" + json + "');" + EOL);
            }
        }
        writer.write(";\n");
    }

    private static void exportItemStack(ItemStack stack) throws IOException {
        int width = Minecraft.func_71410_x().func_147110_a().field_147622_a;
        int height = Minecraft.func_71410_x().func_147110_a().field_147620_b;
        Framebuffer fbo = Minecraft.func_71410_x().func_147110_a();
        Framebuffer framebuffer = new Framebuffer(width, height, true);
        framebuffer.func_147610_a(true);
        GlStateManager.func_179082_a((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        GlStateManager.func_179086_m((int)16384);
        RenderHelper.func_74520_c();
        GlStateManager.func_179140_f();
        GlStateManager.func_179091_B();
        GlStateManager.func_179142_g();
        GlStateManager.func_179145_e();
        Minecraft.func_71410_x().func_175599_af().field_77023_b = 100.0f;
        GL11.glEnable((int)2929);
        Minecraft.func_71410_x().func_175599_af().func_180450_b(stack, 0, 0);
        IntBuffer pixels = BufferUtils.createIntBuffer((int)(width * height));
        GlStateManager.func_179144_i((int)framebuffer.field_147617_g);
        GlStateManager.func_187433_a((int)3553, (int)0, (int)32993, (int)33639, (IntBuffer)pixels);
        int[] vals = new int[width * height];
        pixels.get(vals);
        TextureUtil.func_147953_a((int[])vals, (int)width, (int)height);
        BufferedImage bufferedimage = new BufferedImage(width, height, 2);
        bufferedimage.setRGB(0, 0, width, height, vals, 0, width);
        framebuffer.func_147608_a();
        if (fbo != null) {
            fbo.func_147610_a(true);
        } else {
            GL30.glBindFramebuffer((int)36160, (int)0);
            GL11.glViewport((int)0, (int)0, (int)Minecraft.func_71410_x().field_71443_c, (int)Minecraft.func_71410_x().field_71440_d);
        }
        try {
            BufferedImage rightSizeImage = new BufferedImage(32, 32, 6);
            Graphics2D graphics = rightSizeImage.createGraphics();
            graphics.drawImage(bufferedimage, 0, 0, 32, 32, 0, 0, 32, 32, null);
            File f = new File(new File(MillCommonUtilities.getMillenaireCustomContentDir(), "item picts"), TravelBookExporter.getIconKey(stack) + ".png");
            f.mkdirs();
            f.createNewFile();
            ImageIO.write((RenderedImage)rightSizeImage, "png", f);
        }
        catch (IOException e) {
            MillLog.printException(e);
            return;
        }
    }

    public static void exportItemStacks() {
        for (ItemStack stack : itemsToRender.values()) {
            try {
                TravelBookExporter.exportItemStack(stack);
            }
            catch (IOException e) {
                MillLog.printException(e);
            }
        }
        MillLog.major(null, "Exported " + itemsToRender.size() + " icons.");
    }

    public static void exportTravelBookData() {
        BookManagerTravelBook travelBookManager = new BookManagerTravelBook(50000, 50000, 50000, 50000, new BookManager.FontRendererMock());
        File dir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "jsonexports");
        dir.mkdirs();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        try {
            String language = MillConfigValues.effective_language;
            if (language.contains("_")) {
                language = language.split("_")[0];
            }
            File file = new File(dir, "travelbook_" + language + ".sql");
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            TravelBookExporter.exportAllCultures(travelBookManager, writer, gson, language);
            TravelBookExporter.exportAllVillagers(travelBookManager, writer, gson, language);
            TravelBookExporter.exportAllBuildings(travelBookManager, writer, gson, language);
            TravelBookExporter.exportAllVillages(travelBookManager, writer, gson, language);
            TravelBookExporter.exportAllTradeGoods(travelBookManager, writer, gson, language);
            writer.close();
            MillLog.warning(null, "Exported travel book data to SQL for language: " + language);
        }
        catch (IOException e) {
            MillLog.printException(e);
        }
    }

    public static void exportVillagerPicture(VillagerType villagerType, boolean mainPageExport) throws IOException {
        MillCommonUtilities.initRandom(villagerType.key.hashCode());
        VillagerRecord villagerRecord = VillagerRecord.createVillagerRecord(villagerType.culture, villagerType.key, Mill.getMillWorld((World)Minecraft.func_71410_x().field_71441_e), null, null, null, null, -1L, true);
        MillVillager mockVillager = MillVillager.createMockVillager(villagerRecord, (World)Minecraft.func_71410_x().field_71441_e);
        if (!mainPageExport) {
            mockVillager.heldItem = villagerType.getTravelBookHeldItem();
            mockVillager.heldItemOffHand = villagerType.getTravelBookHeldItemOffHand();
            mockVillager.travelBookMockVillager = true;
        }
        int width = Minecraft.func_71410_x().func_147110_a().field_147622_a;
        int height = Minecraft.func_71410_x().func_147110_a().field_147620_b;
        Framebuffer fbo = Minecraft.func_71410_x().func_147110_a();
        Framebuffer framebuffer = new Framebuffer(width, height, true);
        framebuffer.func_147610_a(true);
        GlStateManager.func_179082_a((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        GlStateManager.func_179086_m((int)16384);
        GuiTravelBook.drawEntityOnScreen(200, 200, 100, 20.0f, 0.0f, mockVillager);
        IntBuffer pixels = BufferUtils.createIntBuffer((int)(width * height));
        GlStateManager.func_179144_i((int)framebuffer.field_147617_g);
        GlStateManager.func_187433_a((int)3553, (int)0, (int)32993, (int)33639, (IntBuffer)pixels);
        int[] vals = new int[width * height];
        pixels.get(vals);
        TextureUtil.func_147953_a((int[])vals, (int)width, (int)height);
        BufferedImage bufferedimage = new BufferedImage(width, height, 2);
        bufferedimage.setRGB(0, 0, width, height, vals, 0, width);
        File f = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "villagers");
        f.mkdirs();
        if (mainPageExport) {
            f = new File(f, mockVillager.vtype.culture.key + "_" + mockVillager.vtype.key + "_main.png");
            f.createNewFile();
            BufferedImage finalImage = new BufferedImage(280, 420, 6);
            Graphics2D graphics = finalImage.createGraphics();
            graphics.drawImage(bufferedimage, 0, 0, 280, 420, 256, 0, 536, 420, null);
            ImageIO.write((RenderedImage)finalImage, "png", f);
        } else {
            f = new File(f, mockVillager.vtype.culture.key + "_" + mockVillager.vtype.key + ".png");
            f.createNewFile();
            BufferedImage finalImage = new BufferedImage(320, 420, 6);
            Graphics2D graphics = finalImage.createGraphics();
            graphics.drawImage(bufferedimage, 0, 0, 320, 420, 195, 0, 515, 420, null);
            ImageIO.write((RenderedImage)finalImage, "png", f);
        }
        framebuffer.func_147608_a();
        if (fbo != null) {
            fbo.func_147610_a(true);
        } else {
            GL30.glBindFramebuffer((int)36160, (int)0);
            GL11.glViewport((int)0, (int)0, (int)Minecraft.func_71410_x().field_71443_c, (int)Minecraft.func_71410_x().field_71440_d);
        }
    }

    public static void exportVillagerPictures(World world) {
        int nb = 0;
        for (Culture culture : Culture.ListCultures) {
            for (VillagerType villagerType : culture.listVillagerTypes) {
                if (villagerType.travelBookDisplay) {
                    try {
                        TravelBookExporter.exportVillagerPicture(villagerType, false);
                        ++nb;
                    }
                    catch (Exception e) {
                        MillLog.printException(e);
                    }
                }
                if (!villagerType.travelBookMainCultureVillager) continue;
                try {
                    TravelBookExporter.exportVillagerPicture(villagerType, true);
                    ++nb;
                }
                catch (Exception e) {
                    MillLog.printException(e);
                }
            }
        }
        MillLog.major(null, "Exported " + nb + " villager pictures.");
    }

    private static String getIconKey(ItemStack stack) {
        if (stack == null) {
            return "";
        }
        String key = stack.func_77952_i() > 0 ? stack.func_77973_b().getRegistryName().toString().replaceAll(":", "_") + "_" + stack.func_77952_i() : stack.func_77973_b().getRegistryName().toString().replaceAll(":", "_");
        if (!itemsToRender.containsKey(key)) {
            itemsToRender.put(key, stack);
        }
        return key;
    }

    private static class ExportPage {
        public List<ExportLine> lines = new ArrayList<ExportLine>();

        public ExportPage(TextPage page) {
            for (TextLine line : page.getLines()) {
                if (line.columns != null && this.lines.size() > 0 && this.lines.get((int)(this.lines.size() - 1)).columns != null && !line.exportTwoColumns) {
                    ExportLine previousLine = this.lines.get(this.lines.size() - 1);
                    ExportLine newLine = new ExportLine(line);
                    previousLine.columns.addAll(newLine.columns);
                    continue;
                }
                if (line.columns == null && (line.text == null || line.text.trim().length() <= 0)) continue;
                this.lines.add(new ExportLine(line));
            }
        }
    }

    private static class ExportLine {
        public String style = "";
        public String text = "";
        public String referenceButtonCulture = null;
        public String referenceButtonType = null;
        public String referenceButtonKey = null;
        public String referenceButtonLabel = null;
        public String referenceButtonIconKey = null;
        public String iconKey = null;
        public String iconLabel = null;
        public List<ExportLine> columns = null;
        public Boolean exportTwoColumns = null;
        public String specialTag = null;

        public ExportLine(TextLine textLine) {
            this.text = textLine.text;
            this.specialTag = textLine.exportSpecialTag;
            if (textLine.style != null) {
                this.style = textLine.style.equals(TextLine.ITALIC) ? "subheader" : (textLine.style.equals("\u00a71") ? "header" : textLine.style);
            }
            if (textLine.referenceButton != null) {
                this.referenceButtonCulture = textLine.referenceButton.culture.key;
                switch (textLine.referenceButton.type) {
                    case BUILDING_DETAIL: {
                        this.referenceButtonType = "buildings";
                        break;
                    }
                    case CULTURE: {
                        this.referenceButtonType = "cultures";
                        break;
                    }
                    case TRADE_GOOD_DETAIL: {
                        this.referenceButtonType = "tradegoods";
                        break;
                    }
                    case VILLAGE_DETAIL: {
                        this.referenceButtonType = "villages";
                        break;
                    }
                    case VILLAGER_DETAIL: {
                        this.referenceButtonType = "villagers";
                    }
                }
                this.referenceButtonKey = textLine.referenceButton.key;
                this.referenceButtonIconKey = TravelBookExporter.getIconKey(textLine.referenceButton.getIcon());
                this.referenceButtonLabel = textLine.referenceButton.getIconFullLegendExport();
            }
            if (textLine.icons != null && textLine.icons.size() > 0) {
                this.iconKey = TravelBookExporter.getIconKey(textLine.icons.get(0));
                if (textLine.iconExtraLegends != null && textLine.iconExtraLegends.size() > 0 && textLine.iconExtraLegends.get(0) != null) {
                    this.iconLabel = textLine.iconExtraLegends.get(0);
                } else if (textLine.displayItemLegend()) {
                    this.iconLabel = textLine.icons.get(0).func_82833_r();
                }
            }
            if (textLine.columns != null) {
                this.columns = new ArrayList<ExportLine>();
                for (TextLine col : textLine.columns) {
                    this.columns.add(new ExportLine(col));
                }
                if (textLine.exportTwoColumns) {
                    this.exportTwoColumns = Boolean.TRUE;
                }
            }
        }
    }

    private static class ExportBook {
        public List<ExportPage> pages = new ArrayList<ExportPage>();

        public ExportBook(TextBook book) {
            for (TextPage page : book.getPages()) {
                this.pages.add(new ExportPage(page));
            }
        }
    }
}

