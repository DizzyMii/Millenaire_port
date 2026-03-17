package org.dizzymii.millenaire2.village.buildingmanagers;

import org.dizzymii.millenaire2.village.Building;

/**
 * Manages visitor and foreign merchant spawning for markets and inns.
 * Ported from org.millenaire.common.village.buildingmanagers.VisitorManager (Forge 1.12.2).
 */
public class VisitorManager {

    private final Building building;
    private boolean nightActionPerformed = false;

    public VisitorManager(Building building) {
        this.building = building;
    }

    public void update(boolean forceAttempt) {
        if (this.building.isMarket) {
            this.updateMarket(forceAttempt);
        } else {
            this.updateVisitors(forceAttempt);
        }
    }

    private void updateMarket(boolean forceAttempt) {
        // TODO: Implement foreign merchant spawning logic
    }

    private void updateVisitors(boolean forceAttempt) {
        // TODO: Implement visitor spawning for inns/taverns
    }
}
