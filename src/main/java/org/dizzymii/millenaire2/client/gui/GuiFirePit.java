package org.dizzymii.millenaire2.client.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * GUI screen for the fire pit block (cooking interface).
 * Ported from org.millenaire.client.gui.GuiFirePit (Forge 1.12.2).
 */
public class GuiFirePit extends Screen {
    public GuiFirePit() { super(Component.literal("Fire Pit")); }
    // TODO: Implement fire pit container screen with slot rendering
}
