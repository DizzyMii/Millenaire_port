/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.OpenGlHelper
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 */
package org.millenaire.client.gui.text;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.millenaire.client.book.BookManagerTravelBook;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.world.UserProfile;

public class GuiTravelBook
extends GuiText {
    public static final int BUTTON_CLOSE = 0;
    public static final int BUTTON_BACK = 1;
    private final List<ScreenState> previousScreenStates = new ArrayList<ScreenState>();
    private GUIScreen currentScreen = GUIScreen.HOME;
    private Culture currentCulture = null;
    private String currentItemKey = null;
    private String currentCategory = null;
    ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/quest.png");
    private final UserProfile profile;
    private MillVillager mockVillager = null;
    long timeElapsed = 0L;
    private final BookManagerTravelBook travelBookManager;

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, MillVillager villager) {
        GlStateManager.func_179142_g();
        GlStateManager.func_179094_E();
        GlStateManager.func_179109_b((float)posX, (float)posY, (float)50.0f);
        GlStateManager.func_179152_a((float)(-scale), (float)scale, (float)scale);
        GlStateManager.func_179114_b((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.func_179114_b((float)-20.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        float renderYawOffset = villager.field_70761_aq;
        float rotationYaw = villager.field_70177_z;
        float rotationPitch = villager.field_70125_A;
        float prevRotationYawHead = villager.field_70758_at;
        float rotationYawHead = villager.field_70759_as;
        GlStateManager.func_179114_b((float)0.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        RenderHelper.func_74519_b();
        GlStateManager.func_179114_b((float)(-((float)Math.atan(mouseY / 40.0f)) * 20.0f), (float)1.0f, (float)0.0f, (float)0.0f);
        villager.field_70761_aq = (float)Math.atan(mouseX / 40.0f) * 20.0f;
        villager.field_70177_z = (float)Math.atan(mouseX / 40.0f) * -30.0f;
        villager.field_70125_A = -((float)Math.atan(mouseY / 40.0f)) * 20.0f;
        villager.field_70759_as = villager.field_70177_z;
        villager.field_70758_at = villager.field_70177_z;
        GlStateManager.func_179109_b((float)0.0f, (float)0.0f, (float)0.0f);
        RenderManager rendermanager = Minecraft.func_71410_x().func_175598_ae();
        rendermanager.func_178631_a(180.0f);
        rendermanager.func_178633_a(false);
        rendermanager.func_188391_a((Entity)villager, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        rendermanager.func_178633_a(true);
        villager.field_70761_aq = renderYawOffset;
        villager.field_70177_z = rotationYaw;
        villager.field_70125_A = rotationPitch;
        villager.field_70758_at = prevRotationYawHead;
        villager.field_70759_as = rotationYawHead;
        GlStateManager.func_179121_F();
        RenderHelper.func_74518_a();
        GlStateManager.func_179101_C();
        GlStateManager.func_179138_g((int)OpenGlHelper.field_77476_b);
        GlStateManager.func_179090_x();
        GlStateManager.func_179138_g((int)OpenGlHelper.field_77478_a);
    }

    public GuiTravelBook(EntityPlayer player) {
        this.profile = Mill.getMillWorld(player.field_70170_p).getProfile(player);
        this.travelBookManager = new BookManagerTravelBook(256, 220, 175, 240, new GuiText.FontRendererGUIWrapper(this));
        this.bookManager = this.travelBookManager;
    }

    @Override
    protected void func_146284_a(GuiButton guibutton) {
        if (!guibutton.field_146124_l) {
            return;
        }
        try {
            if (guibutton instanceof GuiText.GuiButtonReference) {
                GuiText.GuiButtonReference refButton = (GuiText.GuiButtonReference)guibutton;
                this.jumpToDetails(refButton.culture, refButton.type, refButton.key, true);
            } else if (guibutton instanceof GuiButtonTravelBook) {
                GuiButtonTravelBook gb = (GuiButtonTravelBook)guibutton;
                boolean close = false;
                if (gb.key == ButtonTypes.CHOOSE_CULTURE) {
                    this.currentCulture = Culture.getCultureByName(gb.value);
                    this.storePreviousState();
                    this.currentScreen = GUIScreen.CULTURE;
                } else if (gb.key == ButtonTypes.VIEW_BUILDINGS) {
                    this.storePreviousState();
                    this.currentCategory = gb.value;
                    this.currentScreen = GUIScreen.BUILDINGS_LIST;
                } else if (gb.key == ButtonTypes.VIEW_VILLAGERS) {
                    this.storePreviousState();
                    this.currentCategory = gb.value;
                    this.currentScreen = GUIScreen.VILLAGERS_LIST;
                } else if (gb.key == ButtonTypes.VIEW_VILLAGES) {
                    this.storePreviousState();
                    this.currentScreen = GUIScreen.VILLAGES_LIST;
                } else if (gb.key == ButtonTypes.VIEW_TRADE_GOODS) {
                    this.storePreviousState();
                    this.currentCategory = gb.value;
                    this.currentScreen = GUIScreen.TRADE_GOODS_LIST;
                } else if (gb.key == ButtonTypes.BUILDING_DETAIL) {
                    this.storePreviousState();
                    this.currentScreen = GUIScreen.BUILDING_DETAIL;
                    this.currentItemKey = gb.value;
                    this.currentCategory = this.currentCulture.getBuildingPlanSet((String)this.currentItemKey).getFirstStartingPlan().travelBookCategory;
                } else if (gb.key == ButtonTypes.VILLAGER_DETAIL) {
                    this.storePreviousState();
                    this.currentScreen = GUIScreen.VILLAGER_DETAIL;
                    this.currentItemKey = gb.value;
                    this.currentCategory = this.currentCulture.getVillagerType((String)this.currentItemKey).travelBookCategory;
                } else if (gb.key == ButtonTypes.VILLAGE_DETAIL) {
                    this.storePreviousState();
                    this.currentScreen = GUIScreen.VILLAGE_DETAIL;
                    this.currentItemKey = gb.value;
                } else if (gb.key == ButtonTypes.TRADE_GOODS_DETAILS) {
                    this.storePreviousState();
                    this.currentScreen = GUIScreen.TRADE_GOOD_DETAIL;
                    this.currentItemKey = gb.value;
                } else if (gb.key == ButtonTypes.BACK) {
                    if (this.currentScreen == GUIScreen.BUILDING_DETAIL) {
                        List<BuildingPlanSet> planSets = this.travelBookManager.getCurrentBuildingList(this.currentCulture, this.currentCategory);
                        String prevKey = null;
                        for (BuildingPlanSet planSet : planSets) {
                            if (planSet.key.equals(this.currentItemKey)) break;
                            prevKey = planSet.key;
                        }
                        this.currentItemKey = prevKey;
                    } else if (this.currentScreen == GUIScreen.VILLAGER_DETAIL) {
                        List<VillagerType> villagerTypes = this.travelBookManager.getCurrentVillagerList(this.currentCulture, this.currentCategory);
                        String prevKey = null;
                        for (VillagerType villagerType : villagerTypes) {
                            if (villagerType.key.equals(this.currentItemKey)) break;
                            prevKey = villagerType.key;
                        }
                        this.currentItemKey = prevKey;
                    } else if (this.currentScreen == GUIScreen.TRADE_GOOD_DETAIL) {
                        List<TradeGood> tradeGood = this.travelBookManager.getCurrentTradeGoodList(this.currentCulture, this.currentCategory);
                        String prevKey = null;
                        for (TradeGood villagerType : tradeGood) {
                            if (villagerType.key.equals(this.currentItemKey)) break;
                            prevKey = villagerType.key;
                        }
                        this.currentItemKey = prevKey;
                    } else if (this.currentScreen == GUIScreen.VILLAGE_DETAIL) {
                        List<VillageType> villageTypes = this.travelBookManager.getCurrentVillageList(this.currentCulture);
                        String prevKey = null;
                        for (VillageType villageType : villageTypes) {
                            if (villageType.key.equals(this.currentItemKey)) break;
                            prevKey = villageType.key;
                        }
                        this.currentItemKey = prevKey;
                    }
                } else if (gb.key == ButtonTypes.NEXT) {
                    if (this.currentScreen == GUIScreen.BUILDING_DETAIL) {
                        List<BuildingPlanSet> planSets = this.travelBookManager.getCurrentBuildingList(this.currentCulture, this.currentCategory);
                        String nextKey = null;
                        int i = 0;
                        while (i + 1 < planSets.size()) {
                            if (planSets.get((int)i).key.equals(this.currentItemKey)) {
                                nextKey = planSets.get((int)(i + 1)).key;
                            }
                            ++i;
                        }
                        this.currentItemKey = nextKey;
                    } else if (this.currentScreen == GUIScreen.VILLAGER_DETAIL) {
                        List<VillagerType> villagerTypes = this.travelBookManager.getCurrentVillagerList(this.currentCulture, this.currentCategory);
                        String nextKey = null;
                        int i = 0;
                        while (i + 1 < villagerTypes.size()) {
                            if (villagerTypes.get((int)i).key.equals(this.currentItemKey)) {
                                nextKey = villagerTypes.get((int)(i + 1)).key;
                            }
                            ++i;
                        }
                        this.currentItemKey = nextKey;
                    } else if (this.currentScreen == GUIScreen.TRADE_GOOD_DETAIL) {
                        List<TradeGood> tradeGood = this.travelBookManager.getCurrentTradeGoodList(this.currentCulture, this.currentCategory);
                        String nextKey = null;
                        int i = 0;
                        while (i + 1 < tradeGood.size()) {
                            if (tradeGood.get((int)i).key.equals(this.currentItemKey)) {
                                nextKey = tradeGood.get((int)(i + 1)).key;
                            }
                            ++i;
                        }
                        this.currentItemKey = nextKey;
                    } else if (this.currentScreen == GUIScreen.VILLAGE_DETAIL) {
                        List<VillageType> villageTypes = this.travelBookManager.getCurrentVillageList(this.currentCulture);
                        String nextKey = null;
                        int i = 0;
                        while (i + 1 < villageTypes.size()) {
                            if (villageTypes.get((int)i).key.equals(this.currentItemKey)) {
                                nextKey = villageTypes.get((int)(i + 1)).key;
                            }
                            ++i;
                        }
                        this.currentItemKey = nextKey;
                    }
                }
                this.pageNum = 0;
                this.textBook = this.getBook();
                this.buttonPagination();
            } else if (guibutton.field_146127_k == 0) {
                this.closeGui();
            } else if (guibutton.field_146127_k == 1) {
                ScreenState previousState = this.previousScreenStates.get(this.previousScreenStates.size() - 1);
                this.currentScreen = previousState.screen;
                this.currentItemKey = previousState.currentItemKey;
                this.currentCategory = previousState.categoryKey;
                this.pageNum = previousState.pageNum;
                this.previousScreenStates.remove(this.previousScreenStates.size() - 1);
                this.textBook = null;
            }
        }
        catch (Exception e) {
            MillLog.printException("Exception while handling button pressed:", e);
        }
    }

    @Override
    public void buttonPagination() {
        try {
            super.buttonPagination();
            int xStart = (this.field_146294_l - this.getXSize()) / 2;
            int yStart = (this.field_146295_m - this.getYSize()) / 2;
            this.field_146292_n.add(new GuiButton(0, xStart + this.getXSize() / 2 + 5, yStart + this.getYSize() - 40, 95, 20, LanguageUtilities.string("hire.close")));
            if (this.currentScreen != GUIScreen.HOME && !this.previousScreenStates.isEmpty()) {
                this.field_146292_n.add(new GuiButton(1, xStart + this.getXSize() / 2 - 100, yStart + this.getYSize() - 40, 95, 20, LanguageUtilities.string("importtable.back")));
            }
            if (this.currentScreen == GUIScreen.BUILDING_DETAIL) {
                List<BuildingPlanSet> buildings = this.travelBookManager.getCurrentBuildingList(this.currentCulture, this.currentCategory);
                if (buildings.size() == 0) {
                    MillLog.warning((Object)this, "Empty buildings list for culture " + this.currentCulture + " and category " + this.currentCategory + "!");
                } else {
                    boolean isFirstItem = buildings.get((int)0).key.equals(this.currentItemKey);
                    boolean isLastItem = buildings.get((int)(buildings.size() - 1)).key.equals(this.currentItemKey);
                    GuiButtonTravelBook backButton = new GuiButtonTravelBook(ButtonTypes.BACK, "<", xStart + 1, yStart + 1, 15, 20);
                    backButton.field_146124_l = !isFirstItem;
                    GuiButtonTravelBook nextButton = new GuiButtonTravelBook(ButtonTypes.NEXT, ">", xStart + this.getXSize() - 15, yStart + 1, 15, 20);
                    nextButton.field_146124_l = !isLastItem;
                    this.field_146292_n.add(backButton);
                    this.field_146292_n.add(nextButton);
                }
            } else if (this.currentScreen == GUIScreen.VILLAGER_DETAIL) {
                List<VillagerType> villagerTypes = this.travelBookManager.getCurrentVillagerList(this.currentCulture, this.currentCategory);
                if (villagerTypes.size() == 0) {
                    MillLog.warning((Object)this, "Empty villagerTypes list for culture " + this.currentCulture + " and category " + this.currentCategory + "!");
                } else {
                    boolean isFirstItem = villagerTypes.get((int)0).key.equals(this.currentItemKey);
                    boolean isLastItem = villagerTypes.get((int)(villagerTypes.size() - 1)).key.equals(this.currentItemKey);
                    GuiButtonTravelBook backButton = new GuiButtonTravelBook(ButtonTypes.BACK, "<", xStart + 1, yStart + 1, 15, 20);
                    backButton.field_146124_l = !isFirstItem;
                    GuiButtonTravelBook nextButton = new GuiButtonTravelBook(ButtonTypes.NEXT, ">", xStart + this.getXSize() - 15, yStart + 1, 15, 20);
                    nextButton.field_146124_l = !isLastItem;
                    this.field_146292_n.add(backButton);
                    this.field_146292_n.add(nextButton);
                }
            } else if (this.currentScreen == GUIScreen.TRADE_GOOD_DETAIL) {
                List<TradeGood> tradeGoods = this.travelBookManager.getCurrentTradeGoodList(this.currentCulture, this.currentCategory);
                if (tradeGoods.size() == 0) {
                    MillLog.warning((Object)this, "Empty tradeGoods list for culture " + this.currentCulture + " and category " + this.currentCategory + "!");
                } else {
                    boolean isFirstItem = tradeGoods.get((int)0).key.equals(this.currentItemKey);
                    boolean isLastItem = tradeGoods.get((int)(tradeGoods.size() - 1)).key.equals(this.currentItemKey);
                    GuiButtonTravelBook backButton = new GuiButtonTravelBook(ButtonTypes.BACK, "<", xStart + 1, yStart + 1, 15, 20);
                    backButton.field_146124_l = !isFirstItem;
                    GuiButtonTravelBook nextButton = new GuiButtonTravelBook(ButtonTypes.NEXT, ">", xStart + this.getXSize() - 15, yStart + 1, 15, 20);
                    nextButton.field_146124_l = !isLastItem;
                    this.field_146292_n.add(backButton);
                    this.field_146292_n.add(nextButton);
                }
            } else if (this.currentScreen == GUIScreen.VILLAGE_DETAIL) {
                List<VillageType> villageTypes = this.travelBookManager.getCurrentVillageList(this.currentCulture);
                if (villageTypes.size() == 0) {
                    MillLog.warning((Object)this, "Empty villageTypes list for culture " + this.currentCulture + " and category " + this.currentCategory + "!");
                } else {
                    boolean isFirstItem = villageTypes.get((int)0).key.equals(this.currentItemKey);
                    boolean isLastItem = villageTypes.get((int)(villageTypes.size() - 1)).key.equals(this.currentItemKey);
                    GuiButtonTravelBook backButton = new GuiButtonTravelBook(ButtonTypes.BACK, "<", xStart + 1, yStart + 1, 15, 20);
                    backButton.field_146124_l = !isFirstItem;
                    GuiButtonTravelBook nextButton = new GuiButtonTravelBook(ButtonTypes.NEXT, ">", xStart + this.getXSize() - 15, yStart + 1, 15, 20);
                    nextButton.field_146124_l = !isLastItem;
                    this.field_146292_n.add(backButton);
                    this.field_146292_n.add(nextButton);
                }
            }
        }
        catch (Exception e) {
            MillLog.printException("Exception in buttonPagination:", e);
        }
    }

    @Override
    protected void customDrawBackground(int i, int j, float f) {
        if (this.currentScreen == GUIScreen.VILLAGER_DETAIL && this.mockVillager != null) {
            int xStart = (this.field_146294_l - this.getXSize()) / 2;
            int yStart = (this.field_146295_m - this.getYSize()) / 2;
            GuiTravelBook.drawEntityOnScreen(xStart + this.getXSize() - 40, yStart + 150, 50, 20.0f, 0.0f, this.mockVillager);
        }
    }

    @Override
    protected void customDrawScreen(int i, int j, float f) {
        ++this.timeElapsed;
        if (this.timeElapsed % 100L == 0L) {
            this.refreshContent();
        }
    }

    @Override
    public boolean func_73868_f() {
        return true;
    }

    private TextBook getBook() {
        TextBook book = null;
        try {
            if (this.currentScreen == GUIScreen.HOME) {
                book = this.travelBookManager.getBookHome(this.profile);
            } else if (this.currentScreen == GUIScreen.CULTURE) {
                book = this.travelBookManager.getBookCulture(this.currentCulture, this.profile);
            } else if (this.currentScreen == GUIScreen.BUILDINGS_LIST) {
                book = this.travelBookManager.getBookBuildingsList(this.currentCulture, this.currentCategory, this.profile);
            } else if (this.currentScreen == GUIScreen.BUILDING_DETAIL) {
                book = this.travelBookManager.getBookBuildingDetail(this.currentCulture, this.currentItemKey, this.profile);
            } else if (this.currentScreen == GUIScreen.VILLAGERS_LIST) {
                book = this.travelBookManager.getBookVillagersList(this.currentCulture, this.currentCategory, this.profile);
            } else if (this.currentScreen == GUIScreen.TRADE_GOODS_LIST) {
                book = this.travelBookManager.getBookTradeGoodsList(this.currentCulture, this.currentCategory, this.profile);
            } else if (this.currentScreen == GUIScreen.VILLAGER_DETAIL) {
                book = this.travelBookManager.getBookVillagerDetail(this.currentCulture, this.currentItemKey, this.profile);
                this.updateMockVillager();
            } else if (this.currentScreen == GUIScreen.VILLAGES_LIST) {
                book = this.travelBookManager.getBookVillagesList(this.currentCulture, this.profile);
            } else if (this.currentScreen == GUIScreen.VILLAGE_DETAIL) {
                book = this.travelBookManager.getBookVillageDetail(this.currentCulture, this.currentItemKey, this.profile);
            } else if (this.currentScreen == GUIScreen.TRADE_GOOD_DETAIL) {
                book = this.travelBookManager.getBookTradeGoodDetail(this.currentCulture, this.currentItemKey, this.profile);
            }
            book = this.bookManager.adjustTextBookLineLength(book);
        }
        catch (Exception e) {
            MillLog.printException("Error when computing Travel Book", e);
            book = new TextBook();
        }
        return book;
    }

    @Override
    public ResourceLocation getPNGPath() {
        return this.background;
    }

    @Override
    public void initData() {
        this.refreshContent();
    }

    public void jumpToDetails(Culture culture, GuiText.GuiButtonReference.RefType type, String key, boolean withinTravelBook) {
        if (withinTravelBook) {
            this.storePreviousState();
            this.currentCulture = culture;
        } else {
            this.currentCulture = culture;
            this.previousScreenStates.add(new ScreenState(GUIScreen.HOME, null, null, 0));
            if (type != GuiText.GuiButtonReference.RefType.CULTURE) {
                this.previousScreenStates.add(new ScreenState(GUIScreen.CULTURE, null, null, 0));
            }
        }
        this.pageNum = 0;
        this.currentItemKey = key;
        this.currentCategory = null;
        if (type == GuiText.GuiButtonReference.RefType.BUILDING_DETAIL) {
            this.currentCategory = this.currentCulture.getBuildingPlanSet((String)this.currentItemKey).getFirstStartingPlan().travelBookCategory;
            this.currentScreen = GUIScreen.BUILDING_DETAIL;
        } else if (type == GuiText.GuiButtonReference.RefType.VILLAGER_DETAIL) {
            this.currentCategory = this.currentCulture.getVillagerType((String)this.currentItemKey).travelBookCategory;
            this.currentScreen = GUIScreen.VILLAGER_DETAIL;
        } else if (type == GuiText.GuiButtonReference.RefType.VILLAGE_DETAIL) {
            this.currentScreen = GUIScreen.VILLAGE_DETAIL;
        } else if (type == GuiText.GuiButtonReference.RefType.TRADE_GOOD_DETAIL) {
            this.currentCategory = this.currentCulture.getTradeGood((String)this.currentItemKey).travelBookCategory;
            this.currentScreen = GUIScreen.TRADE_GOOD_DETAIL;
        } else if (type == GuiText.GuiButtonReference.RefType.CULTURE) {
            this.currentScreen = GUIScreen.CULTURE;
        }
        this.textBook = this.getBook();
        this.buttonPagination();
    }

    private void refreshContent() {
        this.textBook = this.getBook();
        this.buttonPagination();
    }

    private void storePreviousState() {
        this.previousScreenStates.add(new ScreenState(this.currentScreen, this.currentItemKey, this.currentCategory, this.pageNum));
    }

    private void updateMockVillager() {
        VillagerType villagerType = this.currentCulture.getVillagerType(this.currentItemKey);
        boolean knownVillager = this.profile.isVillagerUnlocked(this.currentCulture, villagerType);
        if (knownVillager || !MillConfigValues.TRAVEL_BOOK_LEARNING) {
            VillagerRecord villagerRecord = VillagerRecord.createVillagerRecord(this.currentCulture, villagerType.key, Mill.getMillWorld((World)Minecraft.func_71410_x().field_71441_e), null, null, null, null, -1L, true);
            this.mockVillager = MillVillager.createMockVillager(villagerRecord, (World)Minecraft.func_71410_x().field_71441_e);
            this.mockVillager.heldItem = villagerType.getTravelBookHeldItem();
            this.mockVillager.heldItemOffHand = villagerType.getTravelBookHeldItemOffHand();
            this.mockVillager.travelBookMockVillager = true;
        } else {
            this.mockVillager = null;
        }
    }

    private static class ScreenState {
        GUIScreen screen;
        String currentItemKey;
        String categoryKey;
        int pageNum;

        public ScreenState(GUIScreen screen, String objectKey, String categoryKey, int pageNum) {
            this.screen = screen;
            this.currentItemKey = objectKey;
            this.categoryKey = categoryKey;
            this.pageNum = pageNum;
        }
    }

    static enum GUIScreen {
        HOME,
        CULTURE,
        BUILDINGS_LIST,
        BUILDING_DETAIL,
        VILLAGERS_LIST,
        VILLAGER_DETAIL,
        VILLAGES_LIST,
        VILLAGE_DETAIL,
        TRADE_GOODS_LIST,
        TRADE_GOOD_DETAIL;

    }

    public static class GuiButtonTravelBook
    extends GuiText.MillGuiButton {
        private String value;
        private final ButtonTypes key;

        public GuiButtonTravelBook(ButtonTypes key, String label) {
            super(0, 0, 0, 0, 0, label);
            this.key = key;
        }

        public GuiButtonTravelBook(ButtonTypes key, String label, int x, int y, int width, int height) {
            super(0, x, y, width, height, label);
            this.key = key;
        }

        public GuiButtonTravelBook(ButtonTypes key, String label, ItemStack icon) {
            super(label, 0, icon);
            this.key = key;
        }

        public GuiButtonTravelBook(ButtonTypes key, String label, String value) {
            super(0, 0, 0, 0, 0, label);
            this.key = key;
            this.value = value;
        }

        public GuiButtonTravelBook(ButtonTypes key, String label, String value, ItemStack icon) {
            super(label, 0, icon);
            this.key = key;
            this.value = value;
        }

        public GuiButtonTravelBook(ButtonTypes key, String label, String value, GuiText.SpecialIcon icon) {
            super(label, 0, icon);
            this.key = key;
            this.value = value;
        }
    }

    public static enum ButtonTypes {
        CHOOSE_CULTURE,
        VIEW_BUILDINGS,
        VIEW_VILLAGERS,
        VIEW_VILLAGES,
        VIEW_TRADE_GOODS,
        BUILDING_DETAIL,
        VILLAGER_DETAIL,
        VILLAGE_DETAIL,
        TRADE_GOODS_DETAILS,
        BACK,
        NEXT;

    }
}

