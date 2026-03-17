package org.dizzymii.millenaire2.pathing;

/**
 * Tile data for pathfinding surface calculations.
 * Ported from org.millenaire.common.pathing.PathingPathCalcTile (Forge 1.12.2).
 */
public class PathingPathCalcTile {

    public boolean ladder;
    public boolean isWalkable;
    public short[] position;

    public PathingPathCalcTile(boolean walkable, boolean lad, short[] pos) {
        this.ladder = lad;
        if (this.ladder) {
            this.isWalkable = false;
        } else if (!this.ladder & walkable) {
            this.isWalkable = true;
        }
        this.position = pos.clone();
    }

    public PathingPathCalcTile(PathingPathCalcTile c) {
        this.ladder = c.ladder;
        this.isWalkable = c.isWalkable;
        this.position = c.position.clone();
    }
}
