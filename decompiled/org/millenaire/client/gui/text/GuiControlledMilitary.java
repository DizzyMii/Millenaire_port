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
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.VillageUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.buildingmanagers.PanelContentGenerator;

public class GuiControlledMilitary
extends GuiText {
    private final Building townHall;
    private final EntityPlayer player;
    ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/panel.png");

    public GuiControlledMilitary(EntityPlayer player, Building th) {
        this.townHall = th;
        this.player = player;
        this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
    }

    @Override
    protected void func_146284_a(GuiButton guibutton) throws IOException {
        if (!guibutton.field_146124_l) {
            return;
        }
        if (guibutton instanceof GuiButtonDiplomacy) {
            GuiButtonDiplomacy gbp = (GuiButtonDiplomacy)guibutton;
            if (gbp.field_146127_k == 0) {
                ClientSender.controlledMilitaryDiplomacy(this.player, this.townHall, gbp.targetVillage, gbp.data);
            } else if (gbp.field_146127_k == 1) {
                ClientSender.controlledMilitaryPlanRaid(this.player, this.townHall, gbp.targetVillage);
            } else if (gbp.field_146127_k == 2) {
                ClientSender.controlledMilitaryCancelRaid(this.player, this.townHall);
            }
            this.fillData();
        }
        super.func_146284_a(guibutton);
    }

    @Override
    protected void customDrawBackground(int i, int j, float f) {
    }

    @Override
    protected void customDrawScreen(int i, int j, float f) {
    }

    private void fillData() {
        Building b;
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        text.add(new TextLine(this.townHall.getVillageQualifiedName(), "\u00a71", new GuiText.GuiButtonReference(this.townHall.villageType)));
        text.add(new TextLine(false));
        text.add(new TextLine(LanguageUtilities.string("ui.controldiplomacy"), "\u00a71"));
        text.add(new TextLine());
        ArrayList<VillageRelation> relations = new ArrayList<VillageRelation>();
        for (Point p : this.townHall.getKnownVillages()) {
            b = this.townHall.mw.getBuilding(p);
            if (b == null) continue;
            relations.add(new VillageRelation(p, this.townHall.getRelationWithVillage(p), b.getVillageQualifiedName()));
        }
        Collections.sort(relations);
        for (VillageRelation vr : relations) {
            GuiButtonDiplomacy raid;
            b = this.townHall.mw.getBuilding(vr.pos);
            if (b == null) continue;
            String col = "";
            if (vr.relation > 70) {
                col = "<darkgreen>";
            } else if (vr.relation > 30) {
                col = "<darkblue>";
            } else if (vr.relation <= -90) {
                col = "<darkred>";
            } else if (vr.relation <= -30) {
                col = "<lightred>";
            }
            text.add(new TextLine(col + LanguageUtilities.string("ui.villagerelations", b.getVillageQualifiedName(), b.villageType.name, b.culture.getAdjectiveTranslated(), LanguageUtilities.string(VillageUtilities.getRelationName(vr.relation)) + " (" + vr.relation + ")"), new GuiText.GuiButtonReference(b.villageType)));
            ((TextLine)text.get((int)(text.size() - 1))).canCutAfter = false;
            GuiButtonDiplomacy relGood = new GuiButtonDiplomacy(vr.pos, 0, 100, LanguageUtilities.string("ui.relgood"));
            GuiButtonDiplomacy relNeutral = new GuiButtonDiplomacy(vr.pos, 0, 0, LanguageUtilities.string("ui.relneutral"));
            GuiButtonDiplomacy relBad = new GuiButtonDiplomacy(vr.pos, 0, -100, LanguageUtilities.string("ui.relbad"));
            text.add(new TextLine(relGood, relNeutral, relBad));
            text.add(new TextLine(false));
            if (this.townHall.raidTarget == null) {
                raid = new GuiButtonDiplomacy(vr.pos, 1, -100, LanguageUtilities.string("ui.raid"));
                raid.itemStackIconLeft = new ItemStack(Items.field_151036_c, 1);
                text.add(new TextLine(raid));
            } else if (this.townHall.raidStart > 0L) {
                if (this.townHall.raidTarget.equals(vr.pos)) {
                    text.add(new TextLine(LanguageUtilities.string("ui.raidinprogress"), "\u00a74"));
                } else {
                    text.add(new TextLine(LanguageUtilities.string("ui.otherraidinprogress"), "\u00a74"));
                }
            } else if (this.townHall.raidTarget.equals(vr.pos)) {
                raid = new GuiButtonDiplomacy(vr.pos, 2, 0, LanguageUtilities.string("ui.raidcancel"));
                raid.itemStackIconLeft = new ItemStack((Item)Items.field_151021_T, 1);
                text.add(new TextLine(raid));
                text.add(new TextLine(LanguageUtilities.string("ui.raidplanned"), "\u00a7c"));
            } else {
                raid = new GuiButtonDiplomacy(vr.pos, 1, -100, LanguageUtilities.string("ui.raid"));
                raid.itemStackIconLeft = new ItemStack(Items.field_151036_c, 1);
                text.add(new TextLine(raid));
                text.add(new TextLine(LanguageUtilities.string("ui.otherraidplanned"), "\u00a7c"));
            }
            text.add(new TextLine());
        }
        ArrayList<List<TextLine>> pages = new ArrayList<List<TextLine>>();
        pages.add(text);
        this.textBook = this.bookManager.convertAndAdjustLines(pages);
        TextBook milBook = PanelContentGenerator.generateMilitary(this.townHall);
        this.textBook.addBook(milBook);
        this.textBook = this.bookManager.adjustTextBookLineLength(this.textBook);
        this.buttonPagination();
    }

    @Override
    public ResourceLocation getPNGPath() {
        return this.background;
    }

    @Override
    public void initData() {
        this.fillData();
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

    public static class GuiButtonDiplomacy
    extends GuiText.MillGuiButton {
        public static final int REL_GOOD = 100;
        public static final int REL_NEUTRAL = 0;
        public static final int REL_BAD = -100;
        public static final int REL = 0;
        public static final int RAID = 1;
        public static final int RAIDCANCEL = 2;
        public Point targetVillage;
        public int data = 0;

        public GuiButtonDiplomacy(Point targetVillage, int id, int data, String s) {
            super(id, 0, 0, 0, 0, s);
            this.targetVillage = targetVillage;
            this.data = data;
        }
    }
}

