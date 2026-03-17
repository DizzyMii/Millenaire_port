/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 */
package org.millenaire.client.gui.text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.buildingplan.BuildingFileFiler;
import org.millenaire.common.buildingplan.BuildingImportExport;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.entity.TileEntityImportTable;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.virtualdir.VirtualDir;

public class GuiImportTable
extends GuiText {
    private static final String SETTINGS_CLEARGROUND = "clearground";
    private static final String SETTINGS_WIDTH_MINUS = "width_minus";
    private static final String SETTINGS_WIDTH_PLUS = "width_plus";
    private static final String SETTINGS_LENGTH_PLUS = "length_plus";
    private static final String SETTINGS_LENGTH_MINUS = "length_minus";
    private static final String IMPORT_ALL = "all";
    private static final String SETTINGS_IMPORTMOCKBLOCKS = "importmockblocks";
    private static final String SETTINGS_CONVERTTOPRESERVEGROUND = "onverttopreserveground";
    private static final String SETTINGS_EXPORTSNOW = "exportsnow";
    private static final String SETTINGS_EXPORTREGULARCHESTS = "exportregularchests";
    private static final String SETTINGS_STARTINGLEVEL_PLUS = "startinglevel_plus";
    private static final String SETTINGS_STARTINGLEVEL_MINUS = "startinglevel_minus";
    private static final String SETTINGS_ORIENTATION = "orientation";
    public static final int BUTTON_CLOSE = 0;
    public static final int BUTTON_BACK = 1;
    private GUIScreen currentScreen = GUIScreen.HOME;
    private final List<GUIScreen> previousScreens = new ArrayList<GUIScreen>();
    private Culture currentCulture = null;
    private String currentBuildingKey = null;
    private String currentSubDirectory = null;
    private int newBuildingLength;
    private int newBuildingWidth;
    private int newBuildingStartLevel;
    private boolean newBuildingClearGround;
    private final EntityPlayer player;
    private final Point tablePos;
    private final TileEntityImportTable importTable;
    ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/quest.png");

    public GuiImportTable(EntityPlayer player, Point tablePos) {
        this.player = player;
        this.tablePos = tablePos;
        this.importTable = tablePos.getImportTable(player.field_70170_p);
        this.currentBuildingKey = this.importTable.getBuildingKey();
        this.bookManager = new BookManager(256, 220, 175, 240, new GuiText.FontRendererGUIWrapper(this));
    }

    @Override
    protected void func_146284_a(GuiButton guibutton) throws IOException {
        if (!guibutton.field_146124_l) {
            return;
        }
        if (guibutton instanceof GuiButtonImportTable) {
            GuiButtonImportTable gb = (GuiButtonImportTable)guibutton;
            boolean close = false;
            if (gb.key == ButtonTypes.REIMPORT) {
                this.importBuildingPlan(BuildingImportExport.EXPORT_DIR, this.importTable.getBuildingKey(), this.importTable.getVariation(), this.importTable.getUpgradeLevel());
                close = true;
            } else if (gb.key == ButtonTypes.REIMPORTALL) {
                this.importBuildingPlanAll(BuildingImportExport.EXPORT_DIR, this.importTable.getBuildingKey());
                close = true;
            } else if (gb.key == ButtonTypes.EXPORT) {
                BuildingImportExport.importTableExportBuildingPlan(this.player.field_70170_p, this.importTable, this.importTable.getUpgradeLevel());
                close = true;
            } else if (gb.key == ButtonTypes.EXPORTNEWLEVEL) {
                BuildingImportExport.importTableExportBuildingPlan(this.player.field_70170_p, this.importTable, -1);
                close = true;
            } else if (gb.key == ButtonTypes.CHANGE_PLAN) {
                this.currentCulture = null;
                this.currentBuildingKey = null;
            } else if (gb.key == ButtonTypes.EXPORT_COST) {
                BuildingImportExport.importTableExportPlanCost(this.importTable.getBuildingKey());
                close = true;
            } else if (gb.key == ButtonTypes.SETTINGS) {
                this.previousScreens.add(this.currentScreen);
                this.currentScreen = GUIScreen.SETTINGS;
                this.pageNum = 0;
            } else if (gb.key == ButtonTypes.SETTINGS_CHANGE) {
                this.adjustSetting(gb.value);
            } else if (gb.key == ButtonTypes.NEWBUILDING) {
                this.newBuildingLength = 10;
                this.newBuildingWidth = 10;
                this.newBuildingStartLevel = -1;
                this.newBuildingClearGround = true;
                this.previousScreens.add(this.currentScreen);
                this.currentScreen = GUIScreen.NEWBUILDING;
                this.pageNum = 0;
            } else if (gb.key == ButtonTypes.NEWBUILDING_SETTING) {
                this.adjustNewBuildingSetting(gb.value);
            } else if (gb.key == ButtonTypes.NEWBUILDING_CREATE) {
                this.createNewBuilding();
                close = true;
            } else if (gb.key == ButtonTypes.IMPORT_CULTURE) {
                this.currentCulture = Culture.getCultureByName(gb.value);
                this.previousScreens.add(this.currentScreen);
                this.currentScreen = GUIScreen.IMPORT_CULTURE;
                this.pageNum = 0;
            } else if (gb.key == ButtonTypes.IMPORT_CULTURE_BUILDING_SUBDIR) {
                this.currentSubDirectory = gb.value;
                this.previousScreens.add(this.currentScreen);
                this.currentScreen = GUIScreen.IMPORT_CULTURE_SUBDIR;
                this.pageNum = 0;
            } else if (gb.key == ButtonTypes.IMPORT_CULTURE_BUILDING) {
                this.previousScreens.add(this.currentScreen);
                this.currentScreen = GUIScreen.IMPORT_CULTURE_BUILDING;
                this.currentBuildingKey = gb.value;
                this.pageNum = 0;
            } else if (gb.key == ButtonTypes.IMPORT_EXPORT_DIR) {
                this.previousScreens.add(this.currentScreen);
                this.currentScreen = GUIScreen.IMPORT_EXPORT_DIR;
                this.pageNum = 0;
            } else if (gb.key == ButtonTypes.IMPORT_EXPORT_DIR_BUILDING) {
                this.previousScreens.add(this.currentScreen);
                this.currentScreen = GUIScreen.IMPORT_EXPORT_DIR_BUILDING;
                this.currentBuildingKey = gb.value;
                this.pageNum = 0;
            } else if (gb.key == ButtonTypes.IMPORT_EXPORT_DIR_BUILDING_IMPORT) {
                if (gb.value.equals(IMPORT_ALL)) {
                    this.importBuildingPlanAll(BuildingImportExport.EXPORT_DIR, this.currentBuildingKey);
                } else {
                    int variation = Integer.parseInt(gb.value.split("_")[0]);
                    int upgradeLevel = Integer.parseInt(gb.value.split("_")[1]);
                    this.importBuildingPlan(BuildingImportExport.EXPORT_DIR, this.currentBuildingKey, variation, upgradeLevel);
                }
                close = true;
            } else if (gb.key == ButtonTypes.IMPORT_CULTURE_BUILDING_IMPORT) {
                if (gb.value.equals(IMPORT_ALL)) {
                    this.importBuildingPlanAll(this.currentCulture.key, this.currentBuildingKey);
                } else {
                    int variation = Integer.parseInt(gb.value.split("_")[0]);
                    int upgradeLevel = Integer.parseInt(gb.value.split("_")[1]);
                    this.importBuildingPlan(this.currentCulture.key, this.currentBuildingKey, variation, upgradeLevel);
                }
                close = true;
            }
            if (close) {
                this.closeWindow();
            } else {
                this.textBook = this.getData();
                this.buttonPagination();
            }
        } else if (guibutton.field_146127_k == 0) {
            this.field_146297_k.func_147108_a(null);
            this.field_146297_k.func_71381_h();
        } else if (guibutton.field_146127_k == 1) {
            this.currentBuildingKey = null;
            this.currentScreen = this.previousScreens.get(this.previousScreens.size() - 1);
            this.previousScreens.remove(this.previousScreens.size() - 1);
            this.pageNum = 0;
            this.textBook = this.getData();
            this.buttonPagination();
        }
        super.func_146284_a(guibutton);
    }

    private void adjustNewBuildingSetting(String value) {
        if (value.equals(SETTINGS_CLEARGROUND)) {
            this.newBuildingClearGround = !this.newBuildingClearGround;
        } else if (value.equals(SETTINGS_LENGTH_MINUS)) {
            this.newBuildingLength = Math.max(this.newBuildingLength - 1, 1);
        } else if (value.equals(SETTINGS_WIDTH_MINUS)) {
            this.newBuildingWidth = Math.max(this.newBuildingWidth - 1, 1);
        } else if (value.equals(SETTINGS_LENGTH_PLUS)) {
            ++this.newBuildingLength;
        } else if (value.equals(SETTINGS_WIDTH_PLUS)) {
            ++this.newBuildingWidth;
        } else if (value.equals(SETTINGS_STARTINGLEVEL_MINUS)) {
            --this.newBuildingStartLevel;
        } else if (value.equals(SETTINGS_STARTINGLEVEL_PLUS)) {
            ++this.newBuildingStartLevel;
        }
    }

    private void adjustSetting(String value) {
        if (value.equals(SETTINGS_EXPORTSNOW)) {
            this.importTable.setExportSnow(!this.importTable.exportSnow());
        } else if (value.equals(SETTINGS_IMPORTMOCKBLOCKS)) {
            this.importTable.setImportMockBlocks(!this.importTable.importMockBlocks());
        } else if (value.equals(SETTINGS_CONVERTTOPRESERVEGROUND)) {
            this.importTable.setAutoconvertToPreserveGround(!this.importTable.autoconvertToPreserveGround());
        } else if (value.equals(SETTINGS_EXPORTREGULARCHESTS)) {
            this.importTable.setExportRegularChests(!this.importTable.exportRegularChests());
        } else if (value.equals(SETTINGS_ORIENTATION)) {
            int orientation = this.importTable.getOrientation() + 1;
            this.importTable.setOrientation(orientation %= 4);
        } else if (value.equals(SETTINGS_STARTINGLEVEL_MINUS)) {
            this.importTable.setStartingLevel(this.importTable.getStartingLevel() - 1);
        } else if (value.equals(SETTINGS_STARTINGLEVEL_PLUS)) {
            this.importTable.setStartingLevel(this.importTable.getStartingLevel() + 1);
        }
        ClientSender.importTableUpdateSettings(this.tablePos, this.importTable.getUpgradeLevel(), this.importTable.getOrientation(), this.importTable.getStartingLevel(), this.importTable.exportSnow(), this.importTable.importMockBlocks(), this.importTable.autoconvertToPreserveGround(), this.importTable.exportRegularChests());
    }

    @Override
    public void buttonPagination() {
        super.buttonPagination();
        int xStart = (this.field_146294_l - this.getXSize()) / 2;
        int yStart = (this.field_146295_m - this.getYSize()) / 2;
        this.field_146292_n.add(new GuiButton(0, xStart + this.getXSize() / 2 + 5, yStart + this.getYSize() - 40, 95, 20, LanguageUtilities.string("hire.close")));
        if (this.currentScreen != GUIScreen.HOME && !this.previousScreens.isEmpty()) {
            this.field_146292_n.add(new GuiButton(1, xStart + this.getXSize() / 2 - 100, yStart + this.getYSize() - 40, 95, 20, LanguageUtilities.string("importtable.back")));
        }
    }

    private void createNewBuilding() {
        ClientSender.importTableCreateNewBuilding(this.tablePos, this.newBuildingLength, this.newBuildingWidth, this.newBuildingStartLevel, this.newBuildingClearGround);
    }

    @Override
    protected void customDrawBackground(int i, int j, float f) {
    }

    @Override
    protected void customDrawScreen(int i, int j, float f) {
    }

    @Override
    public boolean func_73868_f() {
        return false;
    }

    private TextBook getData() {
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        text.add(new TextLine(LanguageUtilities.string("importtable.title"), "\u00a71"));
        text.add(new TextLine());
        if (this.currentScreen == GUIScreen.HOME) {
            if (this.currentBuildingKey == null || this.currentBuildingKey.length() == 0) {
                text.addAll(this.getDataNoPlan());
            } else {
                text.addAll(this.getDataPlan());
            }
        } else if (this.currentScreen == GUIScreen.SETTINGS) {
            text.addAll(this.getDataSettings());
        } else if (this.currentScreen == GUIScreen.NEWBUILDING) {
            text.addAll(this.getDataNewBuilding());
        } else if (this.currentScreen == GUIScreen.IMPORT_EXPORT_DIR) {
            text.addAll(this.getDataImportExportDirData());
        } else if (this.currentScreen == GUIScreen.IMPORT_EXPORT_DIR_BUILDING) {
            text.addAll(this.getDataImportExportDirBuilding());
        } else if (this.currentScreen == GUIScreen.IMPORT_CULTURE) {
            text.addAll(this.getDataImportCulture());
        } else if (this.currentScreen == GUIScreen.IMPORT_CULTURE_SUBDIR) {
            text.addAll(this.getDataImportCultureSubDirectories());
        } else if (this.currentScreen == GUIScreen.IMPORT_CULTURE_BUILDING) {
            text.addAll(this.getDataImportCultureBuilding());
        }
        ArrayList<List<TextLine>> ftext = new ArrayList<List<TextLine>>();
        ftext.add(text);
        return this.bookManager.convertAndAdjustLines(ftext);
    }

    private List<TextLine> getDataImportCulture() {
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        ArrayList<String> subDirectories = new ArrayList<String>();
        for (BuildingPlanSet planSet : this.currentCulture.ListPlanSets) {
            String subDirName = planSet.getFirstStartingPlan().getLoadedFromFile().getParentFile().getName();
            if (subDirectories.contains(subDirName)) continue;
            subDirectories.add(subDirName);
        }
        for (String subDir : subDirectories) {
            text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE_BUILDING_SUBDIR, subDir, subDir)));
        }
        return text;
    }

    private List<TextLine> getDataImportCultureBuilding() {
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE_BUILDING_IMPORT, LanguageUtilities.string("importtable.all"), IMPORT_ALL)));
        BuildingPlanSet set = this.currentCulture.getBuildingPlanSet(this.currentBuildingKey);
        for (int variation = 0; variation < set.plans.size(); ++variation) {
            for (int level = 0; level < set.plans.get(variation).length; ++level) {
                BuildingPlan plan = set.plans.get(variation)[level];
                String planName = plan.planName;
                text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE_BUILDING_IMPORT, planName, variation + "_" + level, plan.getIcon())));
            }
        }
        return text;
    }

    private List<TextLine> getDataImportCultureSubDirectories() {
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        ArrayList<BuildingPlanSet> sortedPlans = new ArrayList<BuildingPlanSet>(this.currentCulture.ListPlanSets);
        Collections.sort(sortedPlans, (p1, p2) -> p1.key.compareTo(p2.key));
        for (BuildingPlanSet planSet : sortedPlans) {
            if (!planSet.getFirstStartingPlan().getLoadedFromFile().getParentFile().getName().equals(this.currentSubDirectory)) continue;
            text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE_BUILDING, planSet.key, planSet.key, planSet.getIcon())));
        }
        return text;
    }

    private List<TextLine> getDataImportExportDirBuilding() {
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        File exportDir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "exports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        VirtualDir exportVirtualDir = new VirtualDir(exportDir);
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_EXPORT_DIR_BUILDING_IMPORT, LanguageUtilities.string("importtable.all"), IMPORT_ALL)));
        for (File file : exportVirtualDir.listFiles()) {
            if (!file.getName().matches(this.currentBuildingKey + "_\\w(\\d\\d?)\\.png")) continue;
            String planKey = file.getName().substring(0, file.getName().length() - 4);
            String suffix = planKey.split("_")[planKey.split("_").length - 1].toUpperCase();
            int variation = suffix.charAt(0) - 65;
            int level = Integer.parseInt(suffix.substring(1, suffix.length()));
            text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_EXPORT_DIR_BUILDING_IMPORT, planKey, variation + "_" + level)));
        }
        return text;
    }

    private List<TextLine> getDataImportExportDirData() {
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        File exportDir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "exports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        VirtualDir exportVirtualDir = new VirtualDir(exportDir);
        for (File file : exportVirtualDir.listFiles(new BuildingFileFiler("_A.txt"))) {
            String buildingKey = file.getName().substring(0, file.getName().length() - 6);
            text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_EXPORT_DIR_BUILDING, buildingKey, buildingKey)));
        }
        return text;
    }

    private List<TextLine> getDataNewBuilding() {
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        text.add(new TextLine(LanguageUtilities.string("importtable.newbuilding")));
        text.add(new TextLine());
        text.add(new TextLine(LanguageUtilities.string("importtable.length", "" + this.newBuildingLength)));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, LanguageUtilities.string("importtable.minus"), SETTINGS_LENGTH_MINUS, GuiText.SpecialIcon.MINUS), new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, LanguageUtilities.string("importtable.plus"), SETTINGS_LENGTH_PLUS, GuiText.SpecialIcon.PLUS)));
        text.add(new TextLine(LanguageUtilities.string("importtable.width", "" + this.newBuildingWidth)));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, LanguageUtilities.string("importtable.minus"), SETTINGS_WIDTH_MINUS, GuiText.SpecialIcon.MINUS), new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, LanguageUtilities.string("importtable.plus"), SETTINGS_WIDTH_PLUS, GuiText.SpecialIcon.PLUS)));
        text.add(new TextLine(LanguageUtilities.string("importtable.startinglevel", "" + this.newBuildingStartLevel)));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, LanguageUtilities.string("importtable.minus"), SETTINGS_STARTINGLEVEL_MINUS, GuiText.SpecialIcon.MINUS), new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, LanguageUtilities.string("importtable.plus"), SETTINGS_STARTINGLEVEL_PLUS, GuiText.SpecialIcon.PLUS)));
        text.add(new TextLine(LanguageUtilities.string("importtable.clearground", "" + this.newBuildingClearGround)));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.NEWBUILDING_SETTING, "" + this.newBuildingClearGround, SETTINGS_CLEARGROUND, new ItemStack(Items.field_151037_a, 1))));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.NEWBUILDING_CREATE, LanguageUtilities.string("importtable.create"), new ItemStack((Item)MillItems.SUMMONING_WAND, 1))));
        return text;
    }

    private List<TextLine> getDataNoPlan() {
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        text.add(new TextLine(LanguageUtilities.string("importtable.nocurrentplan"), TextLine.ITALIC));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.NEWBUILDING, LanguageUtilities.string("importtable.newbuilding"), new ItemStack(Items.field_151037_a, 1))));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_EXPORT_DIR, LanguageUtilities.string("importtable.importexportdir"), new ItemStack((Item)MillItems.SUMMONING_WAND, 1))));
        text.add(new TextLine(LanguageUtilities.string("importtable.importfromculture"), TextLine.ITALIC));
        for (int i = 0; i < Culture.ListCultures.size(); i += 2) {
            Culture culture1;
            if (i + 1 < Culture.ListCultures.size()) {
                culture1 = Culture.ListCultures.get(i);
                Culture culture2 = Culture.ListCultures.get(i + 1);
                text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE, culture1.getAdjectiveTranslated(), culture1.key, culture1.getIcon()), new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE, culture2.getAdjectiveTranslated(), culture2.key, culture2.getIcon())));
                continue;
            }
            culture1 = Culture.ListCultures.get(i);
            text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.IMPORT_CULTURE, culture1.getAdjectiveTranslated(), culture1.key, culture1.getIcon()), null));
        }
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS, LanguageUtilities.string("importtable.settings"), new ItemStack(Items.field_151107_aW, 1)), null));
        return text;
    }

    private List<TextLine> getDataPlan() {
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        text.add(new TextLine(LanguageUtilities.string("importtable.currentplan", this.importTable.getBuildingKey() + "_" + (char)(65 + this.importTable.getVariation()) + this.importTable.getUpgradeLevel())));
        text.add(new TextLine(LanguageUtilities.string("importtable.currentsize", "" + this.importTable.getLength(), "" + this.importTable.getWidth(), "" + this.importTable.getStartingLevel())));
        text.add(new TextLine());
        text.add(new TextLine());
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.REIMPORT, LanguageUtilities.string("importtable.reimport"), new ItemStack((Item)MillItems.SUMMONING_WAND, 1)), new GuiButtonImportTable(ButtonTypes.REIMPORTALL, LanguageUtilities.string("importtable.reimportall"), new ItemStack((Item)MillItems.SUMMONING_WAND, 1))));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.EXPORT, LanguageUtilities.string("importtable.export"), new ItemStack((Item)MillItems.NEGATION_WAND, 1)), new GuiButtonImportTable(ButtonTypes.EXPORTNEWLEVEL, LanguageUtilities.string("importtable.exportnewlevel"), new ItemStack((Item)MillItems.NEGATION_WAND, 1))));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.CHANGE_PLAN, LanguageUtilities.string("importtable.changeplan"), new ItemStack((Item)Items.field_151148_bJ, 1)), new GuiButtonImportTable(ButtonTypes.SETTINGS, LanguageUtilities.string("importtable.settings"), new ItemStack(Items.field_151107_aW, 1))));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.EXPORT_COST, LanguageUtilities.string("importtable.exportcost"), new ItemStack((Item)MillItems.PURSE, 1)), null));
        return text;
    }

    private List<TextLine> getDataSettings() {
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        text.add(new TextLine(LanguageUtilities.string("importtable.settings")));
        text.add(new TextLine());
        text.add(new TextLine(LanguageUtilities.string("importtable.orientation", BuildingPlan.FACING_KEYS[this.importTable.getOrientation()])));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, BuildingPlan.FACING_KEYS[this.importTable.getOrientation()], SETTINGS_ORIENTATION)));
        text.add(new TextLine(LanguageUtilities.string("importtable.startinglevel", "" + this.importTable.getStartingLevel())));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, LanguageUtilities.string("importtable.minus"), SETTINGS_STARTINGLEVEL_MINUS), new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, LanguageUtilities.string("importtable.plus"), SETTINGS_STARTINGLEVEL_PLUS)));
        text.add(new TextLine(LanguageUtilities.string("importtable.exportsnow", "" + this.importTable.exportSnow())));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, "" + this.importTable.exportSnow(), SETTINGS_EXPORTSNOW)));
        text.add(new TextLine(LanguageUtilities.string("importtable.exportregularchests", "" + this.importTable.exportRegularChests())));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, "" + this.importTable.exportRegularChests(), SETTINGS_EXPORTREGULARCHESTS)));
        text.add(new TextLine(LanguageUtilities.string("importtable.importmockblocks", "" + this.importTable.importMockBlocks())));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, "" + this.importTable.importMockBlocks(), SETTINGS_IMPORTMOCKBLOCKS)));
        text.add(new TextLine(LanguageUtilities.string("importtable.converttopreserveground", "" + this.importTable.autoconvertToPreserveGround())));
        text.add(new TextLine(new GuiButtonImportTable(ButtonTypes.SETTINGS_CHANGE, "" + this.importTable.autoconvertToPreserveGround(), SETTINGS_CONVERTTOPRESERVEGROUND)));
        return text;
    }

    @Override
    public ResourceLocation getPNGPath() {
        return this.background;
    }

    private void importBuildingPlan(String source, String buildingKey, int variation, int targetLevel) {
        ClientSender.importTableImportBuildingPlan(this.player, this.tablePos, source, buildingKey, false, variation, targetLevel, this.importTable.getOrientation(), this.importTable.importMockBlocks());
    }

    private void importBuildingPlanAll(String source, String buildingKey) {
        Point parentTablePos = this.importTable.getParentTablePos() != null ? this.importTable.getParentTablePos() : this.tablePos;
        ClientSender.importTableImportBuildingPlan(this.player, parentTablePos, source, buildingKey, true, 0, 0, this.importTable.getOrientation(), this.importTable.importMockBlocks());
    }

    @Override
    public void initData() {
        this.refreshContent();
    }

    private void refreshContent() {
        this.textBook = this.getData();
        this.buttonPagination();
    }

    private static enum GUIScreen {
        HOME,
        SETTINGS,
        NEWBUILDING,
        IMPORT_CULTURE,
        IMPORT_CULTURE_SUBDIR,
        IMPORT_CULTURE_BUILDING,
        IMPORT_EXPORT_DIR,
        IMPORT_EXPORT_DIR_BUILDING;

    }

    private static class GuiButtonImportTable
    extends GuiText.MillGuiButton {
        private String value;
        private final ButtonTypes key;

        public GuiButtonImportTable(ButtonTypes key, String label) {
            super(0, 0, 0, 0, 0, label);
            this.key = key;
        }

        public GuiButtonImportTable(ButtonTypes key, String label, ItemStack icon) {
            super(label, 0, icon);
            this.key = key;
        }

        public GuiButtonImportTable(ButtonTypes key, String label, String value) {
            super(0, 0, 0, 0, 0, label);
            this.key = key;
            this.value = value;
        }

        public GuiButtonImportTable(ButtonTypes key, String label, String value, ItemStack icon) {
            super(label, 0, icon);
            this.key = key;
            this.value = value;
        }

        public GuiButtonImportTable(ButtonTypes key, String label, String value, GuiText.SpecialIcon icon) {
            super(label, 0, icon);
            this.key = key;
            this.value = value;
        }
    }

    private static enum ButtonTypes {
        BACK,
        IMPORT_EXPORT_DIR,
        IMPORT_EXPORT_DIR_BUILDING,
        IMPORT_EXPORT_DIR_BUILDING_IMPORT,
        IMPORT_CULTURE,
        IMPORT_CULTURE_BUILDING,
        IMPORT_CULTURE_BUILDING_SUBDIR,
        IMPORT_CULTURE_BUILDING_IMPORT,
        REIMPORT,
        REIMPORTALL,
        EXPORT,
        EXPORTNEWLEVEL,
        CHANGE_PLAN,
        SETTINGS,
        NEWBUILDING,
        SETTINGS_CHANGE,
        NEWBUILDING_SETTING,
        NEWBUILDING_CREATE,
        EXPORT_COST;

    }
}

