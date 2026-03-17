package org.dizzymii.millenaire2.ui;

/**
 * Constants and server-side GUI action handlers.
 * Ported from org.millenaire.common.ui.GuiActions (Forge 1.12.2).
 *
 * Full action logic (trade, quests, culture control, etc.) will be
 * implemented once Building/Village runtime is complete.
 */
public final class GuiActions {

    private GuiActions() {}

    public static final int VILLAGE_SCROLL_PRICE = 128;
    public static final int VILLAGE_SCROLL_REPUTATION = 8192;
    public static final int CROP_REPUTATION = 8192;
    public static final int CROP_PRICE = 512;
    public static final int CULTURE_CONTROL_REPUTATION = 131072;

    // TODO: activateMillChest, handleTrade, handleBuyBuilding, handleHire,
    //       handleQuestAction, handleCultureControl, etc.
}
