package org.dizzymii.millenaire2.pathing.atomicstryker;

import net.minecraft.world.level.Level;

/**
 * Jump Point Search (JPS) variant of A* pathfinding worker.
 * Ported from org.millenaire.common.pathing.atomicstryker.AStarWorkerJPS (Forge 1.12.2).
 */
public class AStarWorkerJPS implements Runnable {

    private final Level level;
    private final IAStarPathedEntity entity;
    private final AStarNode startNode;
    private final AStarNode[] targetNodes;
    private final AStarConfig config;
    private final int maxIterations;

    public AStarWorkerJPS(Level level, IAStarPathedEntity entity, AStarNode startNode,
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
        // TODO: Implement JPS variant of A* — jump, prune, expand
        //       Call entity.onFoundPath() or entity.onNoPathAvailable()
        entity.onNoPathAvailable();
    }
}
