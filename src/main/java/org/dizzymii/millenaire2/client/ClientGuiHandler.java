package org.dizzymii.millenaire2.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.dizzymii.millenaire2.client.gui.GuiLockedChest;
import org.dizzymii.millenaire2.client.gui.GuiPujas;
import org.dizzymii.millenaire2.client.gui.GuiTrade;
import org.dizzymii.millenaire2.client.gui.text.*;
import org.dizzymii.millenaire2.util.MillLog;

import javax.annotation.Nullable;

/**
 * Routes GUI open requests to the correct Screen implementation.
 * Maps integer GUI IDs (sent from server) to concrete Screen classes.
 * Ported from org.millenaire.client.ClientGuiHandler (Forge 1.12.2).
 */
public class ClientGuiHandler {

    public static final int GUI_TRADE = 1;
    public static final int GUI_LOCKED_CHEST = 2;
    public static final int GUI_PUJAS = 3;
    public static final int GUI_VILLAGE_HEAD = 10;
    public static final int GUI_QUEST = 11;
    public static final int GUI_HIRE = 12;
    public static final int GUI_HELP = 13;
    public static final int GUI_CONFIG = 14;
    public static final int GUI_TRAVEL_BOOK = 15;
    public static final int GUI_CONTROLLED_PROJECTS = 16;
    public static final int GUI_CONTROLLED_MILITARY = 17;
    public static final int GUI_CUSTOM_BUILDING = 18;
    public static final int GUI_IMPORT_TABLE = 19;
    public static final int GUI_NEGATION_WAND = 20;
    public static final int GUI_NEW_BUILDING_PROJECT = 21;
    public static final int GUI_NEW_VILLAGE = 22;
    public static final int GUI_PANEL_PARCHMENT = 23;

    /**
     * Opens a GUI screen based on the given ID. Must be called on the client thread.
     */
    public static void openGui(int guiId) {
        Screen screen = createScreen(guiId);
        if (screen != null) {
            Minecraft.getInstance().execute(() ->
                    Minecraft.getInstance().setScreen(screen));
        } else {
            MillLog.warn("ClientGuiHandler", "Unknown GUI id: " + guiId);
        }
    }

    @Nullable
    private static Screen createScreen(int guiId) {
        return switch (guiId) {
            case GUI_TRADE -> new GuiTrade();
            case GUI_LOCKED_CHEST -> new GuiLockedChest();
            case GUI_PUJAS -> new GuiPujas();
            case GUI_VILLAGE_HEAD -> new GuiVillageHead();
            case GUI_QUEST -> new GuiQuest();
            case GUI_HIRE -> new GuiHire();
            case GUI_HELP -> new GuiHelp();
            case GUI_CONFIG -> new GuiConfig();
            case GUI_TRAVEL_BOOK -> new GuiTravelBook();
            case GUI_CONTROLLED_PROJECTS -> new GuiControlledProjects();
            case GUI_CONTROLLED_MILITARY -> new GuiControlledMilitary();
            case GUI_CUSTOM_BUILDING -> new GuiCustomBuilding();
            case GUI_IMPORT_TABLE -> new GuiImportTable();
            case GUI_NEGATION_WAND -> new GuiNegationWand();
            case GUI_NEW_BUILDING_PROJECT -> new GuiNewBuildingProject();
            case GUI_NEW_VILLAGE -> new GuiNewVillage();
            case GUI_PANEL_PARCHMENT -> new GuiPanelParchment();
            default -> null;
        };
    }
}
