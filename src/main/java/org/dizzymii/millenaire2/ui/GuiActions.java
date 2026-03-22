package org.dizzymii.millenaire2.ui;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Constants and server-side GUI action handlers.
 * Ported from org.millenaire.common.ui.GuiActions (Forge 1.12.2).
 *
 * Full action logic (trade, quests, culture control, etc.) will be
 * implemented once Building/Village runtime is complete.
 */
public final class GuiActions {
    private static final Logger LOGGER = LogUtils.getLogger();

    private GuiActions() {}

    public static final int VILLAGE_SCROLL_PRICE = 128;
    public static final int VILLAGE_SCROLL_REPUTATION = 8192;
    public static final int CROP_REPUTATION = 8192;
    public static final int CROP_PRICE = 512;
    public static final int CULTURE_CONTROL_REPUTATION = 131072;

    /**
     * Activate a locked chest for a player (village ownership check).
     */
    public static boolean activateMillChest(net.minecraft.server.level.ServerPlayer player, net.minecraft.core.BlockPos pos) {
        LOGGER.debug("Activating mill chest at " + pos + " for " + player.getName().getString());
        return true;
    }

    /**
     * Process a trade transaction between player and villager.
     */
    public static void handleTrade(net.minecraft.server.level.ServerPlayer player, int villagerId, int tradeIndex) {
        LOGGER.debug("Trade action: player=" + player.getName().getString()
                + " villager=" + villagerId + " trade=" + tradeIndex);
    }

    /**
     * Process a building purchase request.
     */
    public static void handleBuyBuilding(net.minecraft.server.level.ServerPlayer player, String buildingKey) {
        LOGGER.debug("Buy building: " + buildingKey + " by " + player.getName().getString());
    }

    /**
     * Process a hire request for a villager.
     */
    public static void handleHire(net.minecraft.server.level.ServerPlayer player, int villagerId) {
        LOGGER.debug("Hire action: villager=" + villagerId + " by " + player.getName().getString());
    }

    /**
     * Process a quest-related action (accept, complete, abandon).
     */
    public static void handleQuestAction(net.minecraft.server.level.ServerPlayer player, int questId, int actionType) {
        LOGGER.debug("Quest action: quest=" + questId
                + " action=" + actionType + " by " + player.getName().getString());
    }

    /**
     * Process a culture control action (change crops, building priorities, etc.).
     */
    public static void handleCultureControl(net.minecraft.server.level.ServerPlayer player, int controlType, int value) {
        LOGGER.debug("Culture control: type=" + controlType
                + " value=" + value + " by " + player.getName().getString());
    }
}
