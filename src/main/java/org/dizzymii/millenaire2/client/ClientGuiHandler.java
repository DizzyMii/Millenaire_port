package org.dizzymii.millenaire2.client;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.dizzymii.millenaire2.client.gui.GuiLockedChest;
import org.dizzymii.millenaire2.client.gui.GuiTrade;
import org.dizzymii.millenaire2.client.gui.text.*;
import org.dizzymii.millenaire2.network.MillPacketIds;

import javax.annotation.Nullable;

/**
 * Routes GUI open requests to the correct Screen implementation.
 * Maps integer GUI IDs (sent from server via MillPacketIds) to concrete Screen classes.
 * Ported from org.millenaire.client.ClientGuiHandler (Forge 1.12.2).
 */
public class ClientGuiHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Opens a GUI screen based on the given ID. Must be called on the client thread.
     */
    public static void openGui(int guiId) {
        Screen screen = createScreen(guiId);
        if (screen != null) {
            Minecraft.getInstance().execute(() ->
                    Minecraft.getInstance().setScreen(screen));
        } else {
            LOGGER.warn("Unknown GUI id: " + guiId);
        }
    }

    @Nullable
    private static Screen createScreen(int guiId) {
        return switch (guiId) {
            case MillPacketIds.GUI_TRADE -> new GuiTrade();
            case MillPacketIds.GUI_MILLCHEST -> new GuiLockedChest();
            case MillPacketIds.GUI_QUEST -> new GuiQuest();
            case MillPacketIds.GUI_PANEL -> new GuiPanelParchment();
            case MillPacketIds.GUI_NEGATION -> new GuiNegationWand();
            case MillPacketIds.GUI_NEW_BUILDING -> new GuiNewBuildingProject();
            case MillPacketIds.GUI_CONTROLLED_PROJECT -> new GuiControlledProjects();
            case MillPacketIds.GUI_HIRE -> new GuiHire();
            case MillPacketIds.GUI_NEW_VILLAGE -> new GuiNewVillage();
            case MillPacketIds.GUI_CONTROLLED_MILITARY -> new GuiControlledMilitary();
            case MillPacketIds.GUI_IMPORT_TABLE -> new GuiImportTable();
            case MillPacketIds.GUI_VILLAGE_BOOK -> new GuiTravelBook();
            default -> null;
        };
    }
}
