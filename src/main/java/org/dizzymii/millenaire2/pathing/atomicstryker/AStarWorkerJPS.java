package org.dizzymii.millenaire2.pathing.atomicstryker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Jump Point Search (JPS) variant of A* pathfinding worker.
 * JPS prunes neighbours aggressively, only expanding "jump points" where the
 * path might turn. This dramatically reduces nodes explored on open terrain.
 * Ported from org.millenaire.common.pathing.atomicstryker.AStarWorkerJPS (Forge 1.12.2).
 */
public class AStarWorkerJPS extends AStarWorker {

    private static final int MAX_JUMP_DISTANCE = 32;

    public AStarWorkerJPS(Level level, IAStarPathedEntity entity, AStarNode startNode,
                          AStarNode[] targetNodes, AStarConfig config, int maxIterations) {
        super(level, entity, startNode, targetNodes, config, maxIterations);
    }

    @Override
    protected List<AStarNode> findPath() {
        if (targetNodes == null || targetNodes.length == 0) return null;

        AStarNode primaryTarget = targetNodes[0];

        PriorityQueue<AStarNode> openSet = new PriorityQueue<>();
        Set<Long> closedSet = new HashSet<>();
        Map<Long, AStarNode> openMap = new HashMap<>();

        startNode.parent = null;
        openSet.add(startNode);
        openMap.put(nodeKey(startNode), startNode);

        int iterations = 0;

        while (!openSet.isEmpty() && iterations < maxIterations) {
            iterations++;

            AStarNode current = openSet.poll();
            long currentKey = nodeKey(current);
            openMap.remove(currentKey);

            for (AStarNode target : targetNodes) {
                if (isAtTarget(current, target)) {
                    return AStarStatic.reconstructPath(current);
                }
            }

            closedSet.add(currentKey);

            // JPS: instead of all neighbours, find jump points
            List<AStarNode> jumpPoints = identifySuccessors(current, primaryTarget);
            for (AStarNode jp : jumpPoints) {
                long jpKey = nodeKey(jp);
                if (closedSet.contains(jpKey)) continue;

                AStarNode existing = openMap.get(jpKey);
                if (existing != null) {
                    if (existing.updateDistance(jp.getG(), current)) {
                        openSet.remove(existing);
                        openSet.add(existing);
                    }
                } else {
                    openSet.add(jp);
                    openMap.put(jpKey, jp);
                }
            }
        }

        return null;
    }

    /**
     * For a given node, find all jump point successors in cardinal directions.
     */
    private List<AStarNode> identifySuccessors(AStarNode current, AStarNode target) {
        List<AStarNode> successors = new ArrayList<>();

        // Cardinal directions: N, S, E, W
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : dirs) {
            AStarNode jp = jump(current.x, current.y, current.z, dir[0], dir[1], current, target);
            if (jp != null) {
                successors.add(jp);
            }
        }

        // Vertical jumps: up, down
        AStarNode upJp = jumpVertical(current.x, current.y, current.z, 1, current, target);
        if (upJp != null) successors.add(upJp);
        AStarNode downJp = jumpVertical(current.x, current.y, current.z, -1, current, target);
        if (downJp != null) successors.add(downJp);

        return successors;
    }

    /**
     * Jump in a horizontal direction (dx, dz) until a jump point is found or blocked.
     */
    private AStarNode jump(int x, int y, int z, int dx, int dz, AStarNode parent, AStarNode target) {
        int nx = x + dx;
        int nz = z + dz;

        for (int dist = 0; dist < MAX_JUMP_DISTANCE; dist++) {
            // Check if walkable at this position (try y, y-1, y+1 for steps)
            int ny = findWalkableY(nx, y, nz);
            if (ny == Integer.MIN_VALUE) return null; // blocked

            int cost = parent.getG() + (int) (AStarStatic.getDistanceBetweenNodes(
                    new AStarNode(parent.x, parent.y, parent.z),
                    new AStarNode(nx, ny, nz)) * 10);

            // Reached target
            for (AStarNode t : targetNodes) {
                if (isAtTarget(new AStarNode(nx, ny, nz), t)) {
                    return new AStarNode(nx, ny, nz, cost, parent, target);
                }
            }

            // Check for forced neighbours (blocks that create a turn)
            if (hasForcedNeighbour(nx, ny, nz, dx, dz)) {
                return new AStarNode(nx, ny, nz, cost, parent, target);
            }

            nx += dx;
            nz += dz;
            y = ny;
        }

        return null;
    }

    /**
     * Jump vertically (up/down) until a jump point is found.
     */
    private AStarNode jumpVertical(int x, int y, int z, int dy, AStarNode parent, AStarNode target) {
        int ny = y + dy;
        for (int dist = 0; dist < 4; dist++) {
            AStarNode check = new AStarNode(x, ny, z);
            if (!AStarStatic.isViable(level, check, 2, config)) return null;

            int cost = parent.getG() + Math.abs(ny - parent.y) * 10;

            for (AStarNode t : targetNodes) {
                if (isAtTarget(check, t)) {
                    return new AStarNode(x, ny, z, cost, parent, target);
                }
            }

            // Any horizontal expansion possible = jump point
            int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            for (int[] dir : dirs) {
                int testX = x + dir[0];
                int testZ = z + dir[1];
                AStarNode testNode = new AStarNode(testX, ny, testZ);
                if (AStarStatic.isViable(level, testNode, 2, config)) {
                    return new AStarNode(x, ny, z, cost, parent, target);
                }
            }

            ny += dy;
        }
        return null;
    }

    /**
     * Find a walkable Y coordinate near the given position (handles steps up/down).
     */
    private int findWalkableY(int x, int baseY, int z) {
        // Try same level first
        AStarNode same = new AStarNode(x, baseY, z);
        if (AStarStatic.isViable(level, same, 2, config)) return baseY;

        // Try step up
        AStarNode up = new AStarNode(x, baseY + 1, z);
        if (AStarStatic.isViable(level, up, 2, config)) return baseY + 1;

        // Try step down
        AStarNode down = new AStarNode(x, baseY - 1, z);
        if (AStarStatic.isViable(level, down, 2, config)) return baseY - 1;

        return Integer.MIN_VALUE; // blocked
    }

    /**
     * Check if a position has forced neighbours (obstacles creating a turn).
     */
    private boolean hasForcedNeighbour(int x, int y, int z, int dx, int dz) {
        // If moving along X, check Z neighbours for blockage
        if (dx != 0) {
            BlockPos left = new BlockPos(x, y, z + 1);
            BlockPos right = new BlockPos(x, y, z - 1);
            boolean leftBlocked = !AStarStatic.isBlockPassable(level, left, config);
            boolean rightBlocked = !AStarStatic.isBlockPassable(level, right, config);
            if (leftBlocked || rightBlocked) return true;
        }
        // If moving along Z, check X neighbours
        if (dz != 0) {
            BlockPos left = new BlockPos(x + 1, y, z);
            BlockPos right = new BlockPos(x - 1, y, z);
            boolean leftBlocked = !AStarStatic.isBlockPassable(level, left, config);
            boolean rightBlocked = !AStarStatic.isBlockPassable(level, right, config);
            if (leftBlocked || rightBlocked) return true;
        }
        return false;
    }
}
