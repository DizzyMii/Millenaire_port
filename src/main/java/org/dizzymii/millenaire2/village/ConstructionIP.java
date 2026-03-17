package org.dizzymii.millenaire2.village;

/**
 * Represents a construction in progress for a building.
 * Ported from org.millenaire.common.village.ConstructionIP (Forge 1.12.2).
 */
public class ConstructionIP {

    public BuildingLocation location;
    public int nbBlocksDone = 0;
    public int nbBlocksTotal = 0;

    public ConstructionIP() {}

    public ConstructionIP(BuildingLocation location) {
        this.location = location;
    }

    public float getCompletionPercent() {
        if (nbBlocksTotal <= 0) return 0f;
        return (float) nbBlocksDone / (float) nbBlocksTotal;
    }

    // TODO: Implement NBT save/load, block tracking, construction step logic
}
