package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * New building project screen — lists available building types the village can construct.
 */
public class GuiNewBuildingProject extends GuiText {

    public GuiNewBuildingProject() {
        super(Component.literal("New Building Project"));
    }

    @Override
    protected void init() {
        super.init();
        lines.clear();
        addLine("Available Building Projects:");
        addLine("");
        addLine("Select a project to start construction.");
        addLine("Resources will be gathered by villagers.");
        addLine("");
        addLine("(Project list depends on village level");
        addLine(" and available resources.)");

        int btnY = guiTop + BG_HEIGHT - 55;
        addRenderableWidget(Button.builder(Component.literal("Start"), btn -> startProject())
                .bounds(guiLeft + MARGIN, btnY, 65, 20).build());
    }

    private void startProject() {
        // Send start project packet to server
        onClose();
    }
}
