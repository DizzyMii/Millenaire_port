package org.dizzymii.millenaire2.pathing.atomicstryker;

/**
 * Configuration for A* pathfinding behaviour.
 * Ported from org.millenaire.common.pathing.atomicstryker.AStarConfig (Forge 1.12.2).
 */
public class AStarConfig {

    public boolean canUseDoors = false;
    public boolean canTakeDiagonals = false;
    public boolean allowDropping = false;
    public boolean canSwim = false;
    public boolean canClearLeaves = true;
    public boolean tolerance = false;
    public int toleranceHorizontal = 0;
    public int toleranceVertical = 0;

    public AStarConfig(boolean canUseDoors, boolean makePathDiagonals, boolean allowDropping,
                       boolean canSwim, boolean canClearLeaves) {
        this.canUseDoors = canUseDoors;
        this.canTakeDiagonals = makePathDiagonals;
        this.allowDropping = allowDropping;
        this.canSwim = canSwim;
        this.canClearLeaves = canClearLeaves;
    }

    public AStarConfig(boolean canUseDoors, boolean makePathDiagonals, boolean allowDropping,
                       boolean canSwim, boolean canClearLeaves,
                       int toleranceHorizontal, int toleranceVertical) {
        this(canUseDoors, makePathDiagonals, allowDropping, canSwim, canClearLeaves);
        this.toleranceHorizontal = toleranceHorizontal;
        this.toleranceVertical = toleranceVertical;
        this.tolerance = true;
    }
}
