package org.dizzymii.millenaire2.event;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.dizzymii.millenaire2.menu.FirePitMenu;
import org.dizzymii.millenaire2.network.MillPacketIds;

import javax.annotation.Nullable;

/**
 * Handles server-side GUI open requests (container menus).
 * Ported from org.millenaire.common.forge.ServerGuiHandler (Forge 1.12.2).
 */
public class ServerGuiHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Opens the appropriate GUI for the given ID on the server side.
     * Uses NeoForge's MenuProvider system for container-based GUIs.
     */
    public static void openGui(ServerPlayer player, int guiId, int entityId) {
        switch (guiId) {
            case MillPacketIds.GUI_TRADE:
                LOGGER.debug("Opening trade GUI for " + player.getName().getString());
                // Trade GUI will be opened via MenuProvider when ContainerTrade is fully implemented
                break;
            case MillPacketIds.GUI_MILLCHEST:
                LOGGER.debug("Opening locked chest GUI for " + player.getName().getString());
                // Locked chest container opened when ContainerLockedChest is fully implemented
                break;
            case MillPacketIds.GUI_HIRE:
                LOGGER.debug("Opening hire GUI for " + player.getName().getString());
                break;
            case MillPacketIds.GUI_QUEST:
                LOGGER.debug("Opening quest GUI for " + player.getName().getString());
                break;
            case MillPacketIds.GUI_VILLAGE_BOOK:
                LOGGER.debug("Opening village book for " + player.getName().getString());
                break;
            case MillPacketIds.GUI_PANEL:
                LOGGER.debug("Opening panel for " + player.getName().getString());
                break;
            case MillPacketIds.GUI_IMPORT_TABLE:
                LOGGER.debug("Opening import table for " + player.getName().getString());
                break;
            default:
                LOGGER.warn("Unknown GUI ID: " + guiId);
                break;
        }
    }

    /**
     * Opens the fire pit GUI for a player at a specific block position.
     */
    public static void openFirePitGui(ServerPlayer player, net.minecraft.core.BlockPos pos) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.millenaire2.fire_pit");
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player p) {
                return new FirePitMenu(containerId, inv,
                        new net.minecraft.world.SimpleContainer(3),
                        new net.minecraft.world.inventory.SimpleContainerData(4));
            }
        });
    }
}
