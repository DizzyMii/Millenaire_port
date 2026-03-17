package org.dizzymii.millenaire2.village.buildingmanagers;

import org.dizzymii.millenaire2.village.Building;

/**
 * Manages village information panels (signs) displaying building status, villager info, etc.
 * Ported from org.millenaire.common.village.buildingmanagers.PanelManager (Forge 1.12.2).
 */
public class PanelManager {

    public static final int MAX_LINE_NB = 8;
    public long lastSignUpdate = 0L;
    private final Building building;
    private final Building townHall;

    public PanelManager(Building building, Building townHall) {
        this.building = building;
        this.townHall = townHall;
    }

    // TODO: Implement sign content generation, panel updates, flower/item decoration logic
}
