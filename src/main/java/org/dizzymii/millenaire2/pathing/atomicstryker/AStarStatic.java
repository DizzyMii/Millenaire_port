package org.dizzymii.millenaire2.pathing.atomicstryker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Static utility methods for A* pathfinding — node viability, distance, candidate generation.
 * Ported from org.millenaire.common.pathing.atomicstryker.AStarStatic (Forge 1.12.2).
 */
public class AStarStatic {

    static final int[][] candidates = {
            {0, 0, -1, 1}, {0, 0, 1, 1}, {0, 1, 0, 1}, {1, 0, 0, 1}, {-1, 0, 0, 1},
            {1, 1, 0, 2}, {-1, 1, 0, 2}, {0, 1, 1, 2}, {0, 1, -1, 2},
            {1, -1, 0, 1}, {-1, -1, 0, 1}, {0, -1, 1, 1}, {0, -1, -1, 1}
    };

    static final int[][] candidates_allowdrops = {
            {0, 0, -1, 1}, {0, 0, 1, 1}, {1, 0, 0, 1}, {-1, 0, 0, 1},
            {1, 1, 0, 2}, {-1, 1, 0, 2}, {0, 1, 1, 2}, {0, 1, -1, 2},
            {1, -1, 0, 1}, {-1, -1, 0, 1}, {0, -1, 1, 1}, {0, -1, -1, 1},
            {1, -2, 0, 1}, {-1, -2, 0, 1}, {0, -2, 1, 1}, {0, -2, -1, 1}
    };

    public static double getDistanceBetweenNodes(AStarNode a, AStarNode b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double dz = a.z - b.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static boolean isViable(Level level, AStarNode node, int entityHeight, AStarConfig config) {
        BlockPos below = new BlockPos(node.x, node.y - 1, node.z);
        // Basic viability: solid ground below, empty space at node and above
        if (!level.getBlockState(below).isSolid()) return false;
        for (int h = 0; h < entityHeight; h++) {
            BlockPos check = new BlockPos(node.x, node.y + h, node.z);
            if (level.getBlockState(check).isSolid()) return false;
        }
        return true;
    }

    public static AStarNode[] getAccessNodesSorted(Level level, int workerX, int workerY, int workerZ,
                                                    int posX, int posY, int posZ, AStarConfig config) {
        List<AStarNode> resultList = new ArrayList<>();
        for (int xIter = -2; xIter <= 2; xIter++) {
            for (int zIter = -2; zIter <= 2; zIter++) {
                for (int yIter = -3; yIter <= 2; yIter++) {
                    AStarNode check = new AStarNode(posX + xIter, posY + yIter, posZ + zIter,
                            Math.abs(xIter) + Math.abs(yIter), null);
                    if (isViable(level, check, 1, config)) {
                        resultList.add(check);
                    }
                }
            }
        }
        Collections.sort(resultList);
        return resultList.toArray(new AStarNode[0]);
    }

    // TODO: getNeighbours, isBlockPassable, isBlockClimbable, convertToPathEntity
}
