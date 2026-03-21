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
 * Project control screen — shows active building projects and allows
 * the player to prioritize or cancel them.
 */
public class GuiControlledProjects extends GuiText {

    @Nullable private final Building building;
    private final List<String> projectKeys = new ArrayList<>();
    private int selectedProject = 0;

    public GuiControlledProjects(@Nullable Building building) {
        super(Component.literal("Building Projects"));
        this.building = building;
    }

    public GuiControlledProjects() { this(null); }

    @Override
    protected void init() {
        super.init();
        lines.clear();
        projectKeys.clear();

        if (building != null) {
            projectKeys.addAll(building.buildingsBought);
            if (selectedProject >= projectKeys.size()) {
                selectedProject = Math.max(0, projectKeys.size() - 1);
            }

            addLine("Village: " + (building.getName() != null ? building.getName() : "Unknown"));
            addLine("");
            addLine("Active Projects:");
            if (projectKeys.isEmpty()) {
                addLine("  (none)");
            } else {
                String active = projectKeys.get(selectedProject);
                addLine("Selected: " + active);
                addLine("");
                for (int i = 0; i < projectKeys.size(); i++) {
                    String marker = i == selectedProject ? "> " : "  ";
                    addLine(marker + projectKeys.get(i));
                }
            }

            int btnY = guiTop + BG_HEIGHT - 55;
            addRenderableWidget(Button.builder(Component.literal("◀"), btn -> selectPrev())
                    .bounds(guiLeft + MARGIN, btnY, 20, 20).build());
            addRenderableWidget(Button.builder(Component.literal("▶"), btn -> selectNext())
                    .bounds(guiLeft + MARGIN + 24, btnY, 20, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Allow Upg"), btn -> toggleUpgrades(true))
                    .bounds(guiLeft + MARGIN + 50, btnY, 70, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Forbid"), btn -> toggleUpgrades(false))
                    .bounds(guiLeft + MARGIN + 124, btnY, 58, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Forget"), btn -> forgetProject())
                    .bounds(guiLeft + MARGIN + 186, btnY, 50, 20).build());
        } else {
            addLine("No village data available.");
        }
    }

    private void selectPrev() {
        if (projectKeys.isEmpty()) return;
        selectedProject = (selectedProject - 1 + projectKeys.size()) % projectKeys.size();
        clearWidgets();
        init();
    }

    private void selectNext() {
        if (projectKeys.isEmpty()) return;
        selectedProject = (selectedProject + 1) % projectKeys.size();
        clearWidgets();
        init();
    }

    private void toggleUpgrades(boolean allow) {
        if (building == null || projectKeys.isEmpty()) return;
        Point thPos = getTownHallPos();
        Point projectPos = building.getPos();
        if (thPos == null || projectPos == null) return;

        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeBlockPos(thPos.x, thPos.y, thPos.z);
        w.writeString(projectKeys.get(selectedProject));
        w.writeBlockPos(projectPos.x, projectPos.y, projectPos.z);
        w.writeBoolean(allow);
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_CONTROLLEDBUILDING_TOGGLEALLOWED, w);
    }

    private void forgetProject() {
        if (building == null || projectKeys.isEmpty()) return;
        Point thPos = getTownHallPos();
        Point projectPos = building.getPos();
        if (thPos == null || projectPos == null) return;

        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeBlockPos(thPos.x, thPos.y, thPos.z);
        w.writeString(projectKeys.get(selectedProject));
        w.writeBlockPos(projectPos.x, projectPos.y, projectPos.z);
        ClientPacketSender.sendGuiAction(MillPacketIds.GUIACTION_CONTROLLEDBUILDING_FORGET, w);
    }

    @Nullable
    private Point getTownHallPos() {
        if (building == null) return null;
        if (building.isTownhall) return building.getPos();
        Point th = building.getTownHallPos();
        return th != null ? th : building.getPos();
    }
}
