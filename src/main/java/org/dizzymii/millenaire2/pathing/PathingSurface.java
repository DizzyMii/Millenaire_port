package org.dizzymii.millenaire2.pathing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a walkable surface derived from a 3D region of PathingPathCalcTiles.
 * Used for village path planning and building placement.
 * Ported from org.millenaire.common.pathing.PathingSurface (Forge 1.12.2).
 */
public class PathingSurface {

    public LinkedList<ExtendedPathTile> alltiles;

    public PathingSurface(PathingPathCalcTile[][][] region, PathingPathCalcTile ct) {
        this.alltiles = new LinkedList<>();
        // TODO: Build walkable surface graph from 3D tile region
        //       - Filter tiles with open space above
        //       - Flood-fill neighbours from central tile
        //       - Calculate distance from centre for each reachable tile
    }

    /**
     * Extended path tile with distance and neighbour information.
     */
    public static class ExtendedPathTile {
        public final short[] position;
        public short distance = Short.MAX_VALUE;
        public boolean ladder;
        public final List<ExtendedPathTile> neighbors = new ArrayList<>();

        public ExtendedPathTile(PathingPathCalcTile tile) {
            this.position = tile.position.clone();
            this.ladder = tile.ladder;
        }
    }
}
