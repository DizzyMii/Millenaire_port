package org.dizzymii.millenaire2.pathing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Represents a walkable surface derived from a 3D region of PathingPathCalcTiles.
 * Used for village path planning and building placement.
 * Ported from org.millenaire.common.pathing.PathingSurface (Forge 1.12.2).
 */
public class PathingSurface {

    public LinkedList<ExtendedPathTile> alltiles;

    public PathingSurface(PathingPathCalcTile[][][] region, PathingPathCalcTile ct) {
        this.alltiles = new LinkedList<>();

        int sizeX = region.length;
        int sizeY = region[0].length;
        int sizeZ = region[0][0].length;

        // Build lookup map: position key -> ExtendedPathTile
        Map<Long, ExtendedPathTile> tileMap = new HashMap<>();

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    PathingPathCalcTile tile = region[x][y][z];
                    if (tile == null) continue;
                    if (tile.isWalkable || tile.ladder) {
                        ExtendedPathTile et = new ExtendedPathTile(tile);
                        tileMap.put(posKey(x, y, z), et);
                    }
                }
            }
        }

        // Connect neighbours (6-connected: +/-x, +/-y, +/-z)
        int[][] offsets = {{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1},
                           {1,1,0},{-1,1,0},{0,1,1},{0,1,-1},
                           {1,-1,0},{-1,-1,0},{0,-1,1},{0,-1,-1}};
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    ExtendedPathTile current = tileMap.get(posKey(x, y, z));
                    if (current == null) continue;
                    for (int[] off : offsets) {
                        int nx = x + off[0], ny = y + off[1], nz = z + off[2];
                        if (nx < 0 || nx >= sizeX || ny < 0 || ny >= sizeY || nz < 0 || nz >= sizeZ) continue;
                        ExtendedPathTile neighbour = tileMap.get(posKey(nx, ny, nz));
                        if (neighbour != null) {
                            current.neighbors.add(neighbour);
                        }
                    }
                }
            }
        }

        // Find centre tile and BFS to calculate distances
        ExtendedPathTile centreTile = null;
        if (ct != null) {
            // ct.position is relative offset; find matching index in region
            int halfX = sizeX / 2, halfY = sizeY / 2, halfZ = sizeZ / 2;
            int cx = ct.position[0] + halfX;
            int cy = ct.position[1] + halfY;
            int cz = ct.position[2] + halfZ;
            centreTile = tileMap.get(posKey(cx, cy, cz));
        }

        if (centreTile != null) {
            // BFS from centre
            centreTile.distance = 0;
            Queue<ExtendedPathTile> queue = new LinkedList<>();
            queue.add(centreTile);

            while (!queue.isEmpty()) {
                ExtendedPathTile current = queue.poll();
                for (ExtendedPathTile n : current.neighbors) {
                    short newDist = (short) (current.distance + 1);
                    if (newDist < n.distance) {
                        n.distance = newDist;
                        queue.add(n);
                    }
                }
            }
        }

        // Collect all reachable tiles
        for (ExtendedPathTile et : tileMap.values()) {
            if (et.distance < Short.MAX_VALUE) {
                alltiles.add(et);
            }
        }
    }

    private static long posKey(int x, int y, int z) {
        return ((long) x & 0xFFFFF) | (((long) z & 0xFFFFF) << 20) | (((long) y & 0xFFF) << 40);
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
