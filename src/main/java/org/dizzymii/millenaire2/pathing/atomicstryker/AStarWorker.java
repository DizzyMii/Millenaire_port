package org.dizzymii.millenaire2.pathing.atomicstryker;

import net.minecraft.world.level.Level;

/**
 * Standard A* pathfinding worker (runs on thread pool).
 * Ported from org.millenaire.common.pathing.atomicstryker.AStarWorker (Forge 1.12.2).
 */
public class AStarWorker implements Runnable {

    private final Level level;
    private final IAStarPathedEntity entity;
    private final AStarNode startNode;
    private final AStarNode[] targetNodes;
    private final AStarConfig config;
    private final int maxIterations;

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
        // TODO: Implement standard A* search algorithm
        //       - Open/closed sets, neighbour expansion, cost calculation
        //       - Call entity.onFoundPath() or entity.onNoPathAvailable()
        entity.onNoPathAvailable();
    }
}
