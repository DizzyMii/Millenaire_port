package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.network.ClientPacketSender;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;

/**
 * Negation wand screen — confirms village removal within the wand's area.
 */
public class GuiNegationWand extends GuiText {

    public GuiNegationWand() {
        super(Component.literal("Wand of Negation"));
    }

    @Override
    protected void init() {
        super.init();
        lines.clear();
        addLine("Wand of Negation");
        addLine("");
        addLine("This will remove all Millenaire");
        addLine("structures and villagers in the");
        addLine("targeted area.");
        addLine("");
        addLine("This action cannot be undone!");

        int btnY = guiTop + BG_HEIGHT - 55;
        addRenderableWidget(Button.builder(Component.literal("Confirm"), btn -> confirmNegation())
                .bounds(guiLeft + MARGIN, btnY, 75, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), btn -> onClose())
                .bounds(guiLeft + MARGIN + 80, btnY, 75, 20).build());
    }

    private void confirmNegation() {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        var player = Minecraft.getInstance().player;
        if (player != null) {
            w.writeInt(player.blockPosition().getX());
            w.writeInt(player.blockPosition().getY());
            w.writeInt(player.blockPosition().getZ());
        }
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_NEGATION_WAND, w.toByteArray());
        onClose();
    }
}
