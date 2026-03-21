package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.network.ClientPacketSender;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Military control screen — shows village soldiers and allows
 * the player to send patrols or set defense modes.
 */
public class GuiControlledMilitary extends GuiText {

    @Nullable private final Building building;
    private final List<Point> knownVillages = new ArrayList<>();
    private int selectedTarget = 0;

    public GuiControlledMilitary(@Nullable Building building) {
        super(Component.literal("Military Control"));
        this.building = building;
    }

    public GuiControlledMilitary() { this(null); }

    @Override
    protected void init() {
        super.init();
        lines.clear();
        knownVillages.clear();

        if (building != null) {
            knownVillages.addAll(building.getKnownVillages());
            if (selectedTarget >= knownVillages.size()) {
                selectedTarget = Math.max(0, knownVillages.size() - 1);
            }

            addLine("Village: " + (building.getName() != null ? building.getName() : "Unknown"));
            addLine("");
            addLine("Military Status:");
            addLine("  Under Attack: " + building.underAttack);
            addLine("  Raid Target: " + (building.raidTarget != null ? building.raidTarget.toString() : "None"));
            addLine("");
            if (knownVillages.isEmpty()) {
                addLine("No known villages.");
            } else {
                Point target = knownVillages.get(selectedTarget);
                addLine("Target: " + target);
                addLine("Relation: " + building.getRelation(target));
            }
        } else {
            addLine("No village data available.");
        }

        int btnY = guiTop + BG_HEIGHT - 55;
        addRenderableWidget(Button.builder(Component.literal("◀"), btn -> selectPrev())
                .bounds(guiLeft + MARGIN, btnY, 20, 20).build());
        addRenderableWidget(Button.builder(Component.literal("▶"), btn -> selectNext())
                .bounds(guiLeft + MARGIN + 24, btnY, 20, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Praise"), btn -> sendRelation(10))
                .bounds(guiLeft + MARGIN + 50, btnY, 50, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Slander"), btn -> sendRelation(-10))
                .bounds(guiLeft + MARGIN + 104, btnY, 54, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Raid"), btn -> planRaid())
                .bounds(guiLeft + MARGIN + 162, btnY, 40, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cancel"), btn -> cancelRaid())
                .bounds(guiLeft + MARGIN + 206, btnY, 46, 20).build());
    }

    private void selectPrev() {
        if (knownVillages.isEmpty()) return;
        selectedTarget = (selectedTarget - 1 + knownVillages.size()) % knownVillages.size();
        clearWidgets();
        init();
    }

    private void selectNext() {
        if (knownVillages.isEmpty()) return;
        selectedTarget = (selectedTarget + 1) % knownVillages.size();
        clearWidgets();
        init();
    }

    private void sendRelation(int amount) {
        if (building == null || knownVillages.isEmpty()) return;
        Point thPos = getTownHallPos();
        Point target = knownVillages.get(selectedTarget);
        if (thPos == null || target == null) return;

        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeBlockPos(thPos.x, thPos.y, thPos.z);
        w.writeBlockPos(target.x, target.y, target.z);
        w.writeInt(amount);
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_MILITARY_RELATIONS, w);
    }

    private void planRaid() {
        if (building == null || knownVillages.isEmpty()) return;
        Point thPos = getTownHallPos();
        Point target = knownVillages.get(selectedTarget);
        if (thPos == null || target == null) return;

        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeBlockPos(thPos.x, thPos.y, thPos.z);
        w.writeBlockPos(target.x, target.y, target.z);
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_MILITARY_RAID, w);
    }

    private void cancelRaid() {
        if (building == null) return;
        Point thPos = getTownHallPos();
        if (thPos == null) return;

        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeBlockPos(thPos.x, thPos.y, thPos.z);
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_MILITARY_CANCEL_RAID, w);
    }

    @Nullable
    private Point getTownHallPos() {
        if (building == null) return null;
        if (building.isTownhall) return building.getPos();
        Point th = building.getTownHallPos();
        return th != null ? th : building.getPos();
    }
}
