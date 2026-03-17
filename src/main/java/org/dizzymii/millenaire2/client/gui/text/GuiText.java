package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Base class for Millenaire text-based GUI screens (parchment-style).
 * Ported from org.millenaire.client.gui.text.GuiText (Forge 1.12.2).
 */
public class GuiText extends Screen {

    protected GuiText(Component title) {
        super(title);
    }

    // TODO: Implement parchment background rendering, text layout, button handling
}
