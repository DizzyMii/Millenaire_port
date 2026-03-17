package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Custom building placement screen — allows the player to place a custom building plan.
 */
public class GuiCustomBuilding extends GuiText {

    public GuiCustomBuilding() {
        super(Component.literal("Custom Building"));
    }

    @Override
    protected void init() {
        super.init();
        lines.clear();
        addLine("Custom Building Placement");
        addLine("");
        addLine("Select a building plan from the list");
        addLine("below to place at the current location.");
        addLine("");
        addLine("(No custom plans loaded.)");

        int btnY = guiTop + BG_HEIGHT - 55;
        addRenderableWidget(Button.builder(Component.literal("Place"), btn -> placeBuilding())
                .bounds(guiLeft + MARGIN, btnY, 65, 20).build());
    }

    private void placeBuilding() {
        // Send place building packet to server
        onClose();
    }
}
