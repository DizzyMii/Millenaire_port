/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.client.MillClientUtilities;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.gui.text.GuiConfig;
import org.millenaire.client.gui.text.GuiControlledMilitary;
import org.millenaire.client.gui.text.GuiControlledProjects;
import org.millenaire.client.gui.text.GuiCustomBuilding;
import org.millenaire.client.gui.text.GuiHelp;
import org.millenaire.client.gui.text.GuiHire;
import org.millenaire.client.gui.text.GuiImportTable;
import org.millenaire.client.gui.text.GuiNegationWand;
import org.millenaire.client.gui.text.GuiNewBuildingProject;
import org.millenaire.client.gui.text.GuiNewVillage;
import org.millenaire.client.gui.text.GuiPanelParchment;
import org.millenaire.client.gui.text.GuiQuest;
import org.millenaire.client.gui.text.GuiTravelBook;
import org.millenaire.client.gui.text.GuiVillageHead;
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.UserProfile;

@SideOnly(value=Side.CLIENT)
public class DisplayActions {
    public static void displayChunkGUI(EntityPlayer player, World world) {
        MillClientUtilities.displayChunkPanel(world, player);
    }

    public static void displayConfigGUI() {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiConfig());
    }

    public static void displayControlledMilitaryGUI(EntityPlayer player, Building townHall) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiControlledMilitary(player, townHall));
    }

    public static void displayControlledProjectGUI(EntityPlayer player, Building townHall) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiControlledProjects(player, townHall));
    }

    public static void displayEditCustomBuildingGUI(EntityPlayer player, Building building) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiCustomBuilding(player, building));
    }

    public static void displayHelpGUI() {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiHelp());
    }

    public static void displayHireGUI(EntityPlayer player, MillVillager villager) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiHire(player, villager));
    }

    public static void displayImportTableGUI(EntityPlayer player, Point tablePos) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiImportTable(player, tablePos));
    }

    public static void displayNegationWandGUI(EntityPlayer player, Building townHall) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiNegationWand(player, townHall));
    }

    public static void displayNewBuildingProjectGUI(EntityPlayer player, Building townHall, Point pos) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiNewBuildingProject(player, townHall, pos));
    }

    public static void displayNewCustomBuildingGUI(EntityPlayer player, Building townHall, Point pos, BuildingCustomPlan customBuilding) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiCustomBuilding(player, townHall, pos, customBuilding));
    }

    public static void displayNewCustomBuildingGUI(EntityPlayer player, Point pos, VillageType villageType) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiCustomBuilding(player, pos, villageType));
    }

    public static void displayNewVillageGUI(EntityPlayer player, Point pos) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiNewVillage(player, pos));
    }

    public static void displayParchmentPanelGUI(EntityPlayer player, TextBook book, Building building, int mapType, boolean isParchment) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiPanelParchment(player, book, building, mapType, isParchment));
    }

    public static void displayQuestGUI(EntityPlayer player, MillVillager villager) {
        UserProfile profile = Mill.clientWorld.getProfile(player);
        if (profile.villagersInQuests.containsKey(villager.getVillagerId())) {
            Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiQuest(player, villager));
        }
    }

    public static void displayStartupOrError(EntityPlayer player, boolean error) {
        MillClientUtilities.displayStartupText(error);
    }

    public static void displayTravelBookGUI(EntityPlayer player) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiTravelBook(player));
    }

    public static void displayVillageBookGUI(EntityPlayer player, Point p) {
        MillClientUtilities.displayVillageBook(Mill.clientWorld.world, player, p);
    }

    public static void displayVillageChiefGUI(EntityPlayer player, MillVillager chief) {
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiVillageHead(player, chief));
    }
}

