package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.network.ClientPacketSender;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;

/**
 * Hiring screen — lists available mercenaries/soldiers for hire and their costs.
 */
public class GuiHire extends GuiText {

    public GuiHire() {
        super(Component.literal("Hire Soldiers"));
    }

    @Override
    protected void init() {
        super.init();
        lines.clear();
        addLine("Available for hire:");
        addLine("");
        addLine("  Soldier - 64 Deniers");
        addLine("  Archer  - 96 Deniers");
        addLine("  Knight  - 128 Deniers");
        addLine("");
        addLine("Select a unit type to hire.");

        int btnY = guiTop + BG_HEIGHT - 55;
        addRenderableWidget(Button.builder(Component.literal("Soldier"), btn -> hire("soldier"))
                .bounds(guiLeft + MARGIN, btnY, 65, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Archer"), btn -> hire("archer"))
                .bounds(guiLeft + MARGIN + 70, btnY, 65, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Knight"), btn -> hire("knight"))
                .bounds(guiLeft + MARGIN + 140, btnY, 65, 20).build());
    }

    private void hire(String unitType) {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeString(unitType);
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_HIRE_HIRE, w);
        onClose();
    }
}
