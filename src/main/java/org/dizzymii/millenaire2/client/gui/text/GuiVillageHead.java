package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.village.Building;

import javax.annotation.Nullable;

/**
 * Village head interaction UI — shows village info, reputation, diplomacy,
 * and buttons for projects/military/quests.
 */
public class GuiVillageHead extends GuiText {

    @Nullable private final Building building;

    public GuiVillageHead(@Nullable Building building) {
        super(Component.literal("Village Head"));
        this.building = building;
    }

    public GuiVillageHead() { this(null); }

    @Override
    protected void init() {
        super.init();
        lines.clear();

        if (building != null) {
            addLine("Village: " + (building.getName() != null ? building.getName() : "Unknown"));
            addLine("Culture: " + (building.cultureKey != null ? building.cultureKey : "Unknown"));
            addLine("Active: " + building.isActive);
            addLine("");
            addLine("What would you like to discuss?");
        } else {
            addLine("No village data available.");
        }

        int btnY = guiTop + BG_HEIGHT - 55;
        addRenderableWidget(Button.builder(Component.literal("Projects"), btn -> openProjects())
                .bounds(guiLeft + MARGIN, btnY, 65, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Military"), btn -> openMilitary())
                .bounds(guiLeft + MARGIN + 70, btnY, 65, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Quest"), btn -> openQuest())
                .bounds(guiLeft + MARGIN + 140, btnY, 65, 20).build());
    }

    private void openProjects() {
        if (minecraft != null) minecraft.setScreen(new GuiControlledProjects(building));
    }

    private void openMilitary() {
        if (minecraft != null) minecraft.setScreen(new GuiControlledMilitary(building));
    }

    private void openQuest() {
        if (minecraft != null) minecraft.setScreen(new GuiQuest());
    }
}
