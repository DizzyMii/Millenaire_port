package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.network.chat.Component;

/**
 * Config screen — displays mod configuration options.
 */
public class GuiConfig extends GuiText {

    public GuiConfig() {
        super(Component.literal("Millenaire Config"));
    }

    @Override
    protected void init() {
        super.init();
        lines.clear();
        addLine("Millenaire Configuration");
        addLine("");
        addLine("Village generation: Enabled");
        addLine("Generation distance: 500 blocks");
        addLine("Show village names: On");
        addLine("Language: English");
        addLine("");
        addLine("(Configuration editing will be available");
        addLine(" in a future update.)");
    }
}
