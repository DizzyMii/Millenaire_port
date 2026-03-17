package org.dizzymii.millenaire2.client.gui.text;

import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.village.Building;

import javax.annotation.Nullable;

/**
 * Project control screen — shows active building projects and allows
 * the player to prioritize or cancel them.
 */
public class GuiControlledProjects extends GuiText {

    @Nullable private final Building building;

    public GuiControlledProjects(@Nullable Building building) {
        super(Component.literal("Building Projects"));
        this.building = building;
    }

    public GuiControlledProjects() { this(null); }

    @Override
    protected void init() {
        super.init();
        lines.clear();

        if (building != null) {
            addLine("Village: " + (building.getName() != null ? building.getName() : "Unknown"));
            addLine("");
            addLine("Active Projects:");
            if (building.buildingsBought.isEmpty()) {
                addLine("  (none)");
            } else {
                for (String project : building.buildingsBought) {
                    addLine("  - " + project);
                }
            }
        } else {
            addLine("No village data available.");
        }
    }
}
