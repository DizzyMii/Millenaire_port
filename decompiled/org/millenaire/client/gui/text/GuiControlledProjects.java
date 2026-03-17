/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 */
package org.millenaire.client.gui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.village.VillagerRecord;

public class GuiControlledProjects
extends GuiText {
    private final Building townHall;
    private List<BuildingProject> projects;
    private final EntityPlayer player;
    ResourceLocation background = new ResourceLocation("millenaire", "textures/gui/panel.png");

    public GuiControlledProjects(EntityPlayer player, Building th) {
        this.townHall = th;
        this.projects = this.townHall.getFlatProjectList();
        this.player = player;
        this.bookManager = new BookManager(204, 220, 190, 195, new GuiText.FontRendererGUIWrapper(this));
    }

    @Override
    protected void func_146284_a(GuiButton guibutton) throws IOException {
        if (!guibutton.field_146124_l) {
            return;
        }
        if (guibutton instanceof GuiButtonProject) {
            GuiButtonProject gbp = (GuiButtonProject)guibutton;
            if (gbp.field_146127_k == 1) {
                ClientSender.controlledBuildingsToggleUpgrades(this.player, this.townHall, gbp.project, true);
            } else if (gbp.field_146127_k == 2) {
                ClientSender.controlledBuildingsToggleUpgrades(this.player, this.townHall, gbp.project, false);
            } else if (gbp.field_146127_k == 3) {
                ClientSender.controlledBuildingsForgetBuilding(this.player, this.townHall, gbp.project);
                this.projects = this.townHall.getFlatProjectList();
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
        ArrayList<TextLine> text = new ArrayList<TextLine>();
        text.add(new TextLine(this.townHall.getVillageQualifiedName(), "\u00a71", new GuiText.GuiButtonReference(this.townHall.villageType)));
        text.add(new TextLine(false));
        text.add(new TextLine(LanguageUtilities.string("ui.controlbuildingprojects")));
        text.add(new TextLine());
        for (int i = 0; i < this.projects.size(); ++i) {
            BuildingProject project = this.projects.get(i);
            boolean obsolete = project.planSet != null && project.location.version != project.planSet.plans.get((int)project.location.getVariation())[0].version;
            String status = null;
            if (project.planSet != null) {
                status = project.location.level < 0 ? LanguageUtilities.string("ui.notyetbuilt") : (obsolete ? LanguageUtilities.string("ui.obsoleteplan") : LanguageUtilities.string("ui.level") + ": " + (project.location.level + 1) + "/" + project.planSet.plans.get(project.location.getVariation()).length);
            }
            if (!project.location.isCustomBuilding) {
                text.add(new TextLine(project.getFullName() + " (" + (char)(65 + project.location.getVariation()) + "):", new GuiText.GuiButtonReference(project.planSet)));
            } else {
                text.add(new TextLine(project.getFullName() + " (" + (char)(65 + project.location.getVariation()) + "):"));
            }
            ((TextLine)text.get((int)(text.size() - 1))).canCutAfter = false;
            if (status != null) {
                text.add(new TextLine(status + ", " + this.townHall.getPos().distanceDirectionShort(project.location.pos), false));
            } else {
                text.add(new TextLine(this.townHall.getPos().distanceDirectionShort(project.location.pos), false));
            }
            int nbInhabitants = 0;
            if (project.location != null && project.location.chestPos != null) {
                for (VillagerRecord vr : this.townHall.getAllVillagerRecords()) {
                    if (!project.location.chestPos.equals(vr.getHousePos())) continue;
                    ++nbInhabitants;
                }
            }
            text.add(new TextLine(LanguageUtilities.string("ui.nbinhabitants", "" + nbInhabitants)));
            GuiButtonProject firstButton = null;
            GuiButtonProject secondButton = null;
            if (!obsolete && project.planSet != null && project.location.level < project.planSet.plans.get(project.location.getVariation()).length - 1 && project.planSet.plans.get(project.location.getVariation()).length > 1) {
                if (project.location.upgradesAllowed) {
                    firstButton = new GuiButtonProject(project, 2, LanguageUtilities.string("ui.forbidupgrades"));
                } else {
                    firstButton = new GuiButtonProject(project, 1, LanguageUtilities.string("ui.allowupgrades"));
                    firstButton.itemStackIconLeft = new ItemStack(Items.field_151037_a, 1);
                }
            }
            boolean canForget = true;
            if (project.location == null || project.location.getBuilding(this.townHall.world) != null && project.location.getBuilding((World)this.townHall.world).isTownhall) {
                canForget = false;
            }
            if (canForget) {
                secondButton = new GuiButtonProject(project, 3, LanguageUtilities.string("ui.cancelbuilding"));
            }
            text.add(new TextLine(firstButton, secondButton));
            text.add(new TextLine());
        }
        ArrayList<List<TextLine>> pages = new ArrayList<List<TextLine>>();
        pages.add(text);
        this.textBook = this.bookManager.convertAndAdjustLines(pages);
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

    public static class GuiButtonProject
    extends GuiText.MillGuiButton {
        public static final int ALLOW_UPGRADES = 1;
        public static final int FORBID_UPGRADES = 2;
        public static final int CANCEL_BUILDING = 3;
        public BuildingProject project;

        public GuiButtonProject(BuildingProject project, int i, String s) {
            super(i, 0, 0, 0, 0, s);
            this.project = project;
        }
    }
}

