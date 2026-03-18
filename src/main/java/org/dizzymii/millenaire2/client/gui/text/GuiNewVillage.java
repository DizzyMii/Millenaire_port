package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.network.ClientPacketSender;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;

/**
 * New village creation screen — used with the Wand of Summoning to pick
 * a culture and village type to spawn.
 */
public class GuiNewVillage extends GuiText {

    public GuiNewVillage() {
        super(Component.literal("Summon Village"));
    }

    @Override
    protected void init() {
        super.init();
        lines.clear();
        addLine("Wand of Summoning");
        addLine("");
        addLine("Select a culture to summon a village:");
        addLine("");
        addLine("  Norman");
        addLine("  Indian");
        addLine("  Mayan");
        addLine("  Japanese");
        addLine("  Byzantine");

        int btnY = guiTop + BG_HEIGHT - 55;
        addRenderableWidget(Button.builder(Component.literal("Summon"), btn -> summonVillage())
                .bounds(guiLeft + MARGIN, btnY, 75, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), btn -> onClose())
                .bounds(guiLeft + MARGIN + 80, btnY, 75, 20).build());
    }

    private void summonVillage() {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeString("norman"); // default culture; full impl would use selected culture
        w.writeString("village");
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_NEWVILLAGE, w.toByteArray());
        onClose();
    }
}
