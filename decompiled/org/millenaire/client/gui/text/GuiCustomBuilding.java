/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ResourceLocation
 */
package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.gui.DisplayActions;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class GuiCustomBuilding
extends GuiText {
    private static final int BUTTON_CONFIRM = 1;
    private final Building townHall;
    private final Building existingBuilding;
    private final Point pos;
    private final VillageType villageType;
    private final BuildingCustomPlan customBuilding;
    private final EntityPlayer player;
    private final Map<BuildingCustomPlan.TypeRes, List<Point>> resources;
    ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/panel.png");

    public GuiCustomBuilding(EntityPlayer player, Building building) {
        this.townHall = building.getTownHall();
        this.existingBuilding = building;
        this.villageType = null;
        this.pos = building.getPos();
        this.player = player;
        this.customBuilding = building.location.getCustomPlan();
        this.resources = this.customBuilding.findResources(this.townHall.world, this.pos, this.townHall, building.location);
        this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
    }

    public GuiCustomBuilding(EntityPlayer player, Building th, Point p, BuildingCustomPlan customBuilding) {
        this.townHall = th;
        this.villageType = null;
        this.existingBuilding = null;
        this.pos = p;
        this.player = player;
        this.customBuilding = customBuilding;
        this.resources = customBuilding.findResources(th.world, this.pos, th, null);
        this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
    }

    public GuiCustomBuilding(EntityPlayer player, Point p, VillageType villageType) {
        this.townHall = null;
        this.existingBuilding = null;
        this.villageType = villageType;
        this.pos = p;
        this.player = player;
        this.customBuilding = villageType.customCentre;
        this.resources = this.customBuilding.findResources(player.field_70170_p, this.pos, null, null);
        this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
    }

    @Override
    protected void func_146284_a(GuiButton guibutton) throws IOException {
        if (!guibutton.field_146124_l) {
            return;
        }
        if (guibutton instanceof GuiText.MillGuiButton) {
            if (guibutton.field_146127_k == 1) {
                if (this.townHall != null) {
                    if (this.existingBuilding == null) {
                        ClientSender.newCustomBuilding(this.player, this.townHall, this.pos, this.customBuilding.buildingKey);
                    } else {
                        ClientSender.updateCustomBuilding(this.player, this.existingBuilding);
                    }
                } else {
                    ClientSender.newVillageCreation(this.player, this.pos, this.villageType.culture.key, this.villageType.key);
                }
                this.closeWindow();
            } else {
                this.closeWindow();
                if (this.townHall != null) {
                    DisplayActions.displayNewBuildingProjectGUI(this.player, this.townHall, this.pos);
                } else {
                    DisplayActions.displayNewVillageGUI(this.player, this.pos);
                }
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
    public void decrementPage() {
        super.decrementPage();
        this.buttonPagination();
    }

    @Override
    public ResourceLocation getPNGPath() {
        return this.background;
    }

    @Override
    public void incrementPage() {
        super.incrementPage();
        this.buttonPagination();
    }

    @Override
    public void initData() {
        boolean validBuild = true;
        for (BuildingCustomPlan.TypeRes res : this.customBuilding.minResources.keySet()) {
            if (this.resources.containsKey((Object)res) && this.resources.get((Object)res).size() >= this.customBuilding.minResources.get((Object)res)) continue;
            validBuild = false;
        }
        ArrayList<List<TextLine>> pages = new ArrayList<List<TextLine>>();
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        if (this.townHall != null) {
            text.add(new TextLine(this.townHall.getVillageQualifiedName(), "\u00a71", new GuiText.GuiButtonReference(this.townHall.villageType)));
        } else {
            text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_newvillage"), "\u00a71"));
        }
        text.add(new TextLine());
        if (this.existingBuilding != null) {
            text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_edit", this.customBuilding.getFullDisplayName())));
        } else if (validBuild) {
            text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_confirm", this.customBuilding.getFullDisplayName())));
        } else {
            text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_cantconfirm", this.customBuilding.getFullDisplayName())));
        }
        text.add(new TextLine());
        text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_radius", "" + this.customBuilding.radius, "" + this.customBuilding.heightRadius)));
        if (this.resources.containsKey((Object)BuildingCustomPlan.TypeRes.SIGN) && this.resources.get((Object)BuildingCustomPlan.TypeRes.SIGN).size() > 1) {
            text.add(new TextLine());
            text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_signnumber", "" + this.resources.get((Object)BuildingCustomPlan.TypeRes.SIGN).size())));
        }
        text.add(new TextLine());
        text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_resneededintro")));
        text.add(new TextLine());
        for (BuildingCustomPlan.TypeRes res : this.customBuilding.minResources.keySet()) {
            int resFound = 0;
            if (this.resources.containsKey((Object)res)) {
                resFound = this.resources.get((Object)res).size();
            }
            text.add(new TextLine(LanguageUtilities.string("ui.custombuilding_resneeded", LanguageUtilities.string("custombuilding." + res.key), "" + resFound, "" + this.customBuilding.minResources.get((Object)res), "" + this.customBuilding.maxResources.get((Object)res))));
        }
        text.add(new TextLine());
        if (validBuild) {
            text.add(new TextLine(new GuiText.MillGuiButton(LanguageUtilities.string("ui.close"), 0), new GuiText.MillGuiButton(LanguageUtilities.string("ui.confirm"), 1)));
        } else {
            text.add(new TextLine(new GuiText.MillGuiButton(LanguageUtilities.string("ui.close"), 0)));
        }
        pages.add(text);
        this.textBook = this.bookManager.convertAndAdjustLines(pages);
    }
}

