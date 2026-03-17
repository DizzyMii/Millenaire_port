package org.dizzymii.millenaire2.pathing.atomicstryker;

import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Standard A* pathfinding worker (runs on thread pool).
 * Ported from org.millenaire.common.pathing.atomicstryker.AStarWorker (Forge 1.12.2).
 */
public class AStarWorker implements Runnable {

    protected final Level level;
    protected final IAStarPathedEntity entity;
    protected final AStarNode startNode;
    protected final AStarNode[] targetNodes;
    protected final AStarConfig config;
    protected final int maxIterations;

    public AStarWorker(Level level, IAStarPathedEntity entity, AStarNode startNode,
                       AStarNode[] targetNodes, AStarConfig config, int maxIterations) {
        this.level = level;
        this.entity = entity;
        this.startNode = startNode;
        this.targetNodes = targetNodes;
        this.config = config;
        this.maxIterations = maxIterations;
    }

    @Override
    public void run() {
        try {
            List<AStarNode> path = findPath();
            if (path != null && !path.isEmpty()) {
                entity.onFoundPath(path);
            } else {
                entity.onNoPathAvailable();
            }
        } catch (Exception e) {
            entity.onNoPathAvailable();
        }
    }

    protected List<AStarNode> findPath() {
        if (targetNodes == null || targetNodes.length == 0) return null;

        // Use first target for heuristic
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

            // Check if we reached any target
            for (AStarNode target : targetNodes) {
                if (isAtTarget(current, target)) {
                    return AStarStatic.reconstructPath(current);
                }
            }

            closedSet.add(currentKey);

            // Expand neighbours
            List<AStarNode> neighbours = AStarStatic.getNeighbours(level, current, primaryTarget, config);
            for (AStarNode neighbour : neighbours) {
                long nKey = nodeKey(neighbour);

                if (closedSet.contains(nKey)) continue;

                AStarNode existing = openMap.get(nKey);
                if (existing != null) {
                    // Update if cheaper path found
                    if (existing.updateDistance(neighbour.getG(), current)) {
                        openSet.remove(existing);
                        openSet.add(existing);
                    }
                } else {
                    openSet.add(neighbour);
                    openMap.put(nKey, neighbour);
                }
            }
        }

        return null; // No path found
    }

    protected boolean isAtTarget(AStarNode current, AStarNode target) {
        if (current.x == target.x && current.y == target.y && current.z == target.z) {
            return true;
        }
        if (config.tolerance) {
            return Math.abs(current.x - target.x) <= config.toleranceHorizontal
                    && Math.abs(current.z - target.z) <= config.toleranceHorizontal
                    && Math.abs(current.y - target.y) <= config.toleranceVertical;
        }
        return false;
    }

    protected static long nodeKey(AStarNode node) {
        return ((long) node.x & 0xFFFFF) | (((long) node.z & 0xFFFFF) << 20) | (((long) node.y & 0xFFF) << 40);
    }
}
