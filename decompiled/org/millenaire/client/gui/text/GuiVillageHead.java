/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 */
package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.VillageUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.world.UserProfile;

public class GuiVillageHead
extends GuiText {
    private final MillVillager chief;
    private final EntityPlayer player;
    ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/village_chief.png");

    public GuiVillageHead(EntityPlayer player, MillVillager chief) {
        this.chief = chief;
        this.player = player;
        this.bookManager = new BookManager(256, 200, 160, 240, new GuiText.FontRendererGUIWrapper(this));
    }

    @Override
    protected void func_146284_a(GuiButton guibutton) throws IOException {
        if (guibutton instanceof GuiButtonChief) {
            GuiButtonChief gb = (GuiButtonChief)guibutton;
            boolean close = false;
            if (gb.key == "PRAISE") {
                ClientSender.villageChiefPerformDiplomacy(this.player, this.chief, gb.village, true);
            } else if (gb.key == "SLANDER") {
                ClientSender.villageChiefPerformDiplomacy(this.player, this.chief, gb.village, false);
            } else if (gb.key == "VILLAGE_SCROLL") {
                ClientSender.villageChiefPerformVillageScroll(this.player, this.chief);
                close = true;
            } else if (gb.key == "CULTURE_CONTROL") {
                ClientSender.villageChiefPerformCultureControl(this.player, this.chief);
                close = true;
            } else if (gb.key == "BUILDING") {
                ClientSender.villageChiefPerformBuilding(this.player, this.chief, gb.value);
                close = true;
            } else if (gb.key == "CROP") {
                ClientSender.villageChiefPerformCrop(this.player, this.chief, gb.value);
                close = true;
            } else if (gb.key == "HUNTING_DROP") {
                ClientSender.villageChiefPerformHuntingDrop(this.player, this.chief, gb.value);
                close = true;
            }
            if (close) {
                this.closeWindow();
            } else {
                this.textBook = this.getData();
                this.buttonPagination();
            }
        }
        super.func_146284_a(guibutton);
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
        Building b;
        GuiButtonChief button;
        String localizedName;
        Object button2;
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        String game = "";
        if (this.chief.getGameOccupationName(this.player.func_70005_c_()).length() > 0) {
            game = " (" + this.chief.getGameOccupationName(this.player.func_70005_c_()) + ")";
        }
        text.add(new TextLine(this.chief.func_70005_c_() + ", " + this.chief.getNativeOccupationName() + game, "\u00a71", new GuiText.GuiButtonReference(this.chief.vtype)));
        text.add(new TextLine(LanguageUtilities.string("ui.villagechief", this.chief.getTownHall().getVillageQualifiedName())));
        text.add(new TextLine());
        String col = "";
        if (this.chief.getTownHall().getReputation(this.player) >= 32768) {
            col = "\u00a72";
        } else if (this.chief.getTownHall().getReputation(this.player) >= 4096) {
            col = "\u00a71";
        } else if (this.chief.getTownHall().getReputation(this.player) < -256) {
            col = "\u00a74";
        } else if (this.chief.getTownHall().getReputation(this.player) < 0) {
            col = "\u00a7c";
        }
        text.add(new TextLine(LanguageUtilities.string("ui.yourstatus") + ": " + this.chief.getTownHall().getReputationLevelLabel(this.player), col));
        text.add(new TextLine(this.chief.getTownHall().getReputationLevelDesc(this.player).replaceAll("\\$name", this.player.func_70005_c_()), col));
        text.add(new TextLine());
        text.add(new TextLine(LanguageUtilities.string("ui.possiblehousing") + ":", "\u00a71"));
        text.add(new TextLine());
        UserProfile profile = Mill.proxy.getClientProfile();
        int reputation = this.chief.getTownHall().getReputation(this.player);
        for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
            if (!this.chief.getTownHall().buildingProjects.containsKey((Object)ep)) continue;
            List projectsLevel = this.chief.getTownHall().buildingProjects.get((Object)ep);
            for (BuildingProject project : projectsLevel) {
                GuiButtonChief button3;
                boolean buyButton;
                String status;
                if (project.planSet == null) continue;
                BuildingPlan plan = project.planSet.getFirstStartingPlan();
                if (plan != null && plan.price > 0 && !plan.isgift) {
                    status = "";
                    buyButton = false;
                    if (project.location != null) {
                        status = LanguageUtilities.string("ui.alreadybuilt") + ".";
                    } else if (this.chief.getTownHall().buildingsBought.contains(project.key)) {
                        status = LanguageUtilities.string("ui.alreadyrequested") + ".";
                    } else if (plan.reputation > reputation) {
                        status = LanguageUtilities.string("ui.notavailableyet") + ".";
                    } else if (plan.price > MillCommonUtilities.countMoney((IInventory)this.player.field_71071_by)) {
                        status = LanguageUtilities.string("ui.youaremissing", "" + MillCommonUtilities.getShortPrice(plan.price - MillCommonUtilities.countMoney((IInventory)this.player.field_71071_by)));
                    } else {
                        status = LanguageUtilities.string("ui.available") + ".";
                        buyButton = true;
                    }
                    text.add(new TextLine(plan.nativeName + ": " + status, false));
                    if (!buyButton) continue;
                    button3 = new GuiButtonChief("BUILDING", LanguageUtilities.string("ui.buybuilding", plan.nativeName, MillCommonUtilities.getShortPrice(plan.price)), plan.buildingKey);
                    button3.itemStackIconLeft = plan.getIcon();
                    button3.itemStackIconRight = new ItemStack((Item)MillItems.PURSE, 1);
                    text.add(new TextLine(button3));
                    continue;
                }
                if (!plan.isgift || !MillConfigValues.bonusEnabled || Mill.isDistantClient()) continue;
                status = "";
                buyButton = false;
                if (project.location != null) {
                    status = LanguageUtilities.string("ui.alreadybuilt") + ".";
                } else if (this.chief.getTownHall().buildingsBought.contains(project.key)) {
                    status = LanguageUtilities.string("ui.alreadyrequested") + ".";
                } else {
                    status = LanguageUtilities.string("ui.bonusavailable") + ".";
                    buyButton = true;
                }
                text.add(new TextLine(plan.nativeName + ": " + status, false));
                if (!buyButton) continue;
                button3 = new GuiButtonChief("BUILDING", LanguageUtilities.string("ui.buybonusbuilding", plan.nativeName), plan.buildingKey);
                button3.itemStackIconLeft = plan.getIcon();
                text.add(new TextLine(button3));
            }
        }
        if (8192 > reputation) {
            text.add(new TextLine(LanguageUtilities.string("ui.scrollsnoreputation")));
        } else if (128 > MillCommonUtilities.countMoney((IInventory)this.player.field_71071_by)) {
            text.add(new TextLine(LanguageUtilities.string("ui.scrollsnotenoughmoney", "" + MillCommonUtilities.getShortPrice(128 - MillCommonUtilities.countMoney((IInventory)this.player.field_71071_by)))));
        } else {
            text.add(new TextLine(LanguageUtilities.string("ui.scrollsok"), false));
            button2 = new GuiButtonChief("VILLAGE_SCROLL", LanguageUtilities.string("ui.buyscroll"), MillCommonUtilities.getShortPrice(128));
            button2.itemStackIconLeft = new ItemStack((Item)MillItems.PARCHMENT_VILLAGE_SCROLL, 1);
            button2.itemStackIconRight = new ItemStack((Item)MillItems.PURSE, 1);
            text.add(new TextLine((GuiText.MillGuiButton)((Object)button2)));
        }
        if (this.chief.getCulture().knownCrops.size() > 0) {
            text.add(new TextLine(LanguageUtilities.string("ui.cropsknown")));
            text.add(new TextLine());
            for (String crop : this.chief.getCulture().knownCrops) {
                Item itemCrop = Item.func_111206_d((String)("millenaire:" + crop));
                localizedName = I18n.func_135052_a((String)(itemCrop.func_77658_a() + ".name"), (Object[])new Object[0]);
                if (profile.isTagSet("cropplanting_" + crop)) {
                    text.add(new TextLine(LanguageUtilities.string("ui.cropknown", localizedName)));
                    continue;
                }
                if (8192 > reputation) {
                    text.add(new TextLine(LanguageUtilities.string("ui.cropinsufficientreputation", localizedName)));
                    continue;
                }
                if (512 > MillCommonUtilities.countMoney((IInventory)this.player.field_71071_by)) {
                    text.add(new TextLine(LanguageUtilities.string("ui.cropnotenoughmoney", localizedName, "" + MillCommonUtilities.getShortPrice(512 - MillCommonUtilities.countMoney((IInventory)this.player.field_71071_by)))));
                    continue;
                }
                text.add(new TextLine(LanguageUtilities.string("ui.cropoktolearn", localizedName), false));
                button = new GuiButtonChief("CROP", LanguageUtilities.string("ui.croplearn", "" + MillCommonUtilities.getShortPrice(512)), crop);
                button.itemStackIconLeft = new ItemStack(itemCrop, 1);
                button.itemStackIconRight = new ItemStack((Item)MillItems.PURSE, 1);
                text.add(new TextLine(button));
            }
            text.add(new TextLine());
        }
        if (this.chief.getCulture().knownHuntingDrops.size() > 0) {
            text.add(new TextLine(LanguageUtilities.string("ui.huntingdropsknown")));
            text.add(new TextLine());
            for (String crop : this.chief.getCulture().knownHuntingDrops) {
                Item itemCrop = Item.func_111206_d((String)("millenaire:" + crop));
                localizedName = I18n.func_135052_a((String)(itemCrop.func_77658_a() + ".name"), (Object[])new Object[0]);
                if (profile.isTagSet("huntingdrop_" + crop)) {
                    text.add(new TextLine(LanguageUtilities.string("ui.huntingdropknown", localizedName)));
                    continue;
                }
                if (8192 > reputation) {
                    text.add(new TextLine(LanguageUtilities.string("ui.huntingdropinsufficientreputation", localizedName)));
                    continue;
                }
                if (512 > MillCommonUtilities.countMoney((IInventory)this.player.field_71071_by)) {
                    text.add(new TextLine(LanguageUtilities.string("ui.huntingdropnotenoughmoney", localizedName, "" + MillCommonUtilities.getShortPrice(512 - MillCommonUtilities.countMoney((IInventory)this.player.field_71071_by)))));
                    continue;
                }
                text.add(new TextLine(LanguageUtilities.string("ui.huntingdropoktolearn", localizedName), false));
                button = new GuiButtonChief("HUNTING_DROP", LanguageUtilities.string("ui.huntingdroplearn", "" + MillCommonUtilities.getShortPrice(512)), crop);
                button.itemStackIconLeft = new ItemStack(itemCrop, 1);
                button.itemStackIconRight = new ItemStack((Item)MillItems.PURSE, 1);
                text.add(new TextLine(button));
            }
            text.add(new TextLine());
        }
        if (profile.isTagSet("culturecontrol_" + this.chief.getCulture().key)) {
            text.add(new TextLine(LanguageUtilities.string("ui.control_alreadydone", this.chief.getCulture().getAdjectiveTranslated())));
        } else if (131072 > reputation) {
            text.add(new TextLine(LanguageUtilities.string("ui.control_noreputation", this.chief.getCulture().getAdjectiveTranslated())));
        } else {
            text.add(new TextLine(LanguageUtilities.string("ui.control_ok", this.chief.getCulture().getAdjectiveTranslated()), false));
            button2 = new GuiButtonChief("CULTURE_CONTROL", LanguageUtilities.string("ui.control_get"));
            button2.itemStackIconLeft = this.chief.getCulture().getIcon();
            button2.itemStackIconRight = new ItemStack((Item)Items.field_151028_Y, 1);
            text.add(new TextLine((GuiText.MillGuiButton)((Object)button2)));
        }
        ArrayList<List<TextLine>> pages = new ArrayList<List<TextLine>>();
        pages.add(text);
        text = new ArrayList();
        text.add(new TextLine(LanguageUtilities.string("ui.relationlist"), "\u00a71"));
        text.add(new TextLine());
        text.add(new TextLine(LanguageUtilities.string("ui.relationpoints", "" + profile.getDiplomacyPoints(this.chief.getTownHall()))));
        text.add(new TextLine());
        ArrayList<VillageRelation> relations = new ArrayList<VillageRelation>();
        for (Point p : this.chief.getTownHall().getKnownVillages()) {
            b = this.chief.getTownHall().mw.getBuilding(p);
            if (b == null) continue;
            relations.add(new VillageRelation(p, this.chief.getTownHall().getRelationWithVillage(p), b.getVillageQualifiedName()));
        }
        Collections.sort(relations);
        for (VillageRelation vr : relations) {
            b = this.chief.getTownHall().mw.getBuilding(vr.pos);
            if (b == null) continue;
            col = "";
            if (vr.relation > 70) {
                col = "<darkgreen>";
            } else if (vr.relation > 30) {
                col = "<darkblue>";
            } else if (vr.relation <= -90) {
                col = "<darkred>";
            } else if (vr.relation <= -30) {
                col = "<lightred>";
            }
            text.add(new TextLine(col + LanguageUtilities.string("ui.villagerelations", b.getVillageQualifiedName(), b.villageType.name, b.culture.getAdjectiveTranslated(), LanguageUtilities.string(VillageUtilities.getRelationName(vr.relation)) + " (" + vr.relation + ")"), false));
            GuiButtonChief praise = null;
            GuiButtonChief slander = null;
            if (profile.getDiplomacyPoints(this.chief.getTownHall()) > 0 && reputation > 0) {
                if (vr.relation < 100) {
                    praise = new GuiButtonChief("PRAISE", LanguageUtilities.string("ui.relationpraise"), vr.pos);
                    praise.itemStackIconLeft = new ItemStack((Block)Blocks.field_150328_O, 1);
                }
                if (vr.relation > -100) {
                    slander = new GuiButtonChief("SLANDER", LanguageUtilities.string("ui.relationslander"), vr.pos);
                    slander.itemStackIconLeft = new ItemStack(Items.field_151040_l, 1);
                }
                text.add(new TextLine(praise, slander));
                text.add(new TextLine());
                continue;
            }
            text.add(new TextLine("<darkred>" + LanguageUtilities.string("ui.villagerelationsnobutton")));
            text.add(new TextLine());
        }
        pages.add(text);
        text = new ArrayList();
        text.add(new TextLine(LanguageUtilities.string("ui.relationhelp")));
        pages.add(text);
        return this.bookManager.convertAndAdjustLines(pages);
    }

    @Override
    public ResourceLocation getPNGPath() {
        return this.background;
    }

    @Override
    public void initData() {
        this.textBook = this.getData();
    }

    private class VillageRelation
    implements Comparable<VillageRelation> {
        int relation;
        Point pos;
        String name;

        VillageRelation(Point p, int r, String name) {
            this.relation = r;
            this.pos = p;
            this.name = name;
        }

        @Override
        public int compareTo(VillageRelation arg0) {
            return this.name.compareTo(arg0.name);
        }

        public boolean equals(Object o) {
            if (o == null || !(o instanceof VillageRelation)) {
                return false;
            }
            return this.pos.equals(((VillageRelation)o).pos);
        }

        public int hashCode() {
            return this.pos.hashCode();
        }
    }

    private static class GuiButtonChief
    extends GuiText.MillGuiButton {
        private static final String PRAISE = "PRAISE";
        private static final String SLANDER = "SLANDER";
        private static final String BUILDING = "BUILDING";
        private static final String VILLAGE_SCROLL = "VILLAGE_SCROLL";
        private static final String CULTURE_CONTROL = "CULTURE_CONTROL";
        private static final String CROP = "CROP";
        private static final String HUNTING_DROP = "HUNTING_DROP";
        private Point village;
        private String value;
        private final String key;

        private GuiButtonChief(String key, String label) {
            super(0, 0, 0, 0, 0, label);
            this.key = key;
        }

        private GuiButtonChief(String key, String label, Point v) {
            super(0, 0, 0, 0, 0, label);
            this.village = v;
            this.key = key;
        }

        public GuiButtonChief(String key, String label, String plan) {
            super(0, 0, 0, 0, 0, label);
            this.key = key;
            this.value = plan;
        }
    }
}

