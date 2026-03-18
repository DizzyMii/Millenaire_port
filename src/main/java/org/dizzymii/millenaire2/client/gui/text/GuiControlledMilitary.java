package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.network.ClientPacketSender;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.village.Building;

import javax.annotation.Nullable;

/**
 * Military control screen — shows village soldiers and allows
 * the player to send patrols or set defense modes.
 */
public class GuiControlledMilitary extends GuiText {

    @Nullable private final Building building;

    public GuiControlledMilitary(@Nullable Building building) {
        super(Component.literal("Military Control"));
        this.building = building;
    }

    public GuiControlledMilitary() { this(null); }

    @Override
    protected void init() {
        super.init();
        lines.clear();

        if (building != null) {
            addLine("Village: " + (building.getName() != null ? building.getName() : "Unknown"));
            addLine("");
            addLine("Military Status:");
            addLine("  Under Attack: " + building.underAttack);
            addLine("");
            addLine("Commands:");
        } else {
            addLine("No village data available.");
        }

        int btnY = guiTop + BG_HEIGHT - 55;
        addRenderableWidget(Button.builder(Component.literal("Patrol"), btn -> sendPatrol())
                .bounds(guiLeft + MARGIN, btnY, 65, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Defend"), btn -> setDefend())
                .bounds(guiLeft + MARGIN + 70, btnY, 65, 20).build());
    }

    private void sendPatrol() {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeInt(-1); // entity ID from context
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_MILITARY_RAID, w.toByteArray());
    }

    private void setDefend() {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeInt(-1); // entity ID from context
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_MILITARY_CANCEL_RAID, w.toByteArray());
    }
}
