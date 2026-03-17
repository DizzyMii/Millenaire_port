package org.dizzymii.millenaire2.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * GUI screen for trading with villagers.
 * Ported from org.millenaire.client.gui.GuiTrade (Forge 1.12.2).
 */
public class GuiTrade extends Screen {
    public GuiTrade() { super(Component.literal("Trade")); }
    // TODO: Implement trade UI with buy/sell slots and reputation display
}
