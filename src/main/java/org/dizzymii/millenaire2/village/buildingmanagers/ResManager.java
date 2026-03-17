package org.dizzymii.millenaire2.village.buildingmanagers;

import org.dizzymii.millenaire2.village.Building;

/**
 * Extended resource manager for building-specific resource operations (farming, trees, etc.).
 * Ported from org.millenaire.common.village.buildingmanagers.ResManager (Forge 1.12.2).
 */
public class ResManager {

    private final Building building;

    public ResManager(Building building) {
        this.building = building;
    }

    // TODO: Implement farming plots, tree management, silk worm/snail soil, cocoa, sapling tracking, NBT save/load
}
