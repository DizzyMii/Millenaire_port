package org.dizzymii.millenaire2.pathing.atomicstryker;

import net.minecraft.world.level.Level;

import java.util.concurrent.ExecutorService;

/**
 * Manages A* pathfinding requests using a thread pool.
 * Ported from org.millenaire.common.pathing.atomicstryker.AStarPathPlannerJPS (Forge 1.12.2).
 */
public class AStarPathPlannerJPS {

    private final ExecutorService executor;
    private final boolean useJPS;

    public AStarPathPlannerJPS(ExecutorService executor, boolean useJPS) {
        this.executor = executor;
        this.useJPS = useJPS;
    }

    public void requestPath(Level level, IAStarPathedEntity entity, AStarNode startNode,
                            AStarNode[] targetNodes, AStarConfig config, int maxIterations) {
        Runnable worker = useJPS
                ? new AStarWorkerJPS(level, entity, startNode, targetNodes, config, maxIterations)
                : new AStarWorker(level, entity, startNode, targetNodes, config, maxIterations);
        executor.submit(worker);
    }

    public void cancelAllPaths() {
        // No individual tracking — relies on executor queue clearing
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
