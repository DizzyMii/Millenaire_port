package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Panel parchment screen — displays a parchment document with custom text content.
 */
public class GuiPanelParchment extends GuiText {

    public GuiPanelParchment(String title, List<String> content) {
        super(Component.literal(title));
        lines.addAll(content);
    }

    public GuiPanelParchment() {
        super(Component.literal("Parchment"));
    }
}
