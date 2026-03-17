package org.dizzymii.millenaire2.pathing.atomicstryker;

import java.util.List;

/**
 * Interface for entities that use the A* pathfinding system.
 * Ported from org.millenaire.common.pathing.atomicstryker.IAStarPathedEntity (Forge 1.12.2).
 */
public interface IAStarPathedEntity {
    void onFoundPath(List<AStarNode> path);
    void onNoPathAvailable();
}
