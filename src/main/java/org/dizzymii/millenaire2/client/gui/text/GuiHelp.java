package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.network.chat.Component;

/**
 * Help screen — displays basic mod information and controls.
 */
public class GuiHelp extends GuiText {

    public GuiHelp() {
        super(Component.literal("Millenaire Help"));
    }

    @Override
    protected void init() {
        super.init();
        lines.clear();
        addLine("Welcome to Millenaire!");
        addLine("");
        addLine("Explore the world to discover villages");
        addLine("from many different cultures.");
        addLine("");
        addLine("Trade with villagers to gain reputation");
        addLine("and unlock new buildings and quests.");
        addLine("");
        addLine("Use the Wand of Summoning to create");
        addLine("new villages in suitable locations.");
        addLine("");
        addLine("Right-click villagers to interact.");
        addLine("Right-click the village scroll to see");
        addLine("village information.");
    }
}
