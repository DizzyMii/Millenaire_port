package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.network.ClientPacketSender;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;

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
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeString("custom_plan");
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_NEW_CUSTOM_BUILDING_PROJECT, w);
        onClose();
    }
}
