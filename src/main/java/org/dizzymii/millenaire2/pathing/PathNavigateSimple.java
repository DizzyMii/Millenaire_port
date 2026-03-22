package org.dizzymii.millenaire2.pathing;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.pathing.atomicstryker.AStarConfig;
import org.dizzymii.millenaire2.pathing.atomicstryker.AStarNode;
import org.dizzymii.millenaire2.pathing.atomicstryker.AStarPathPlannerJPS;
import org.dizzymii.millenaire2.pathing.atomicstryker.AStarStatic;
import org.dizzymii.millenaire2.pathing.atomicstryker.IAStarPathedEntity;
import org.dizzymii.millenaire2.pathing.atomicstryker.LoggedThreadPoolExecutor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Simplified path navigator for Millenaire villagers.
 * Manages path requests via the A* planner and drives entity movement along the result.
 * Ported from org.millenaire.common.entity.PathNavigateSimple (Forge 1.12.2).
 */
public class PathNavigateSimple implements IAStarPathedEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int MAX_ITERATIONS = 5000;
    private static final int PATH_RECALC_TICKS = 40;
    private static final int PATH_PENDING_TIMEOUT_TICKS = 100; // 5 seconds max wait for async path

    private static ExecutorService pathExecutor;
    private static AStarPathPlannerJPS planner;

    private final Mob entity;
    @Nullable private List<AStarNode> currentPath;
    private int pathIndex = 0;
    private int ticksSinceLastPath = 0;
    private boolean pathPending = false;
    private int pendingTicks = 0;
    @Nullable private BlockPos lastRequestedTarget = null;

    // Default config: can use doors, no diagonals, no drops, no swim, can clear leaves
    private AStarConfig config = new AStarConfig(true, false, false, false, true, 1, 1);

    public PathNavigateSimple(Mob entity) {
        this.entity = entity;
        ensureExecutor();
    }

    private static synchronized void ensureExecutor() {
        if (pathExecutor == null) {
            pathExecutor = new LoggedThreadPoolExecutor(2, 4, 60000);
            planner = new AStarPathPlannerJPS(pathExecutor, true);
        }
    }

    /**
     * Request a path to a target position.
     */
    public void navigateTo(BlockPos target) {
        if (pathPending) return;
        lastRequestedTarget = target;
        pendingTicks = 0;

        Level level = entity.level();
        BlockPos entityPos = entity.blockPosition();

        AStarNode startNode = new AStarNode(entityPos.getX(), entityPos.getY(), entityPos.getZ());
        AStarNode[] targets = AStarStatic.getAccessNodesSorted(level,
                entityPos.getX(), entityPos.getY(), entityPos.getZ(),
                target.getX(), target.getY(), target.getZ(), config);

        if (targets.length == 0) {
            // No accessible positions near target — try direct
            targets = new AStarNode[]{new AStarNode(target.getX(), target.getY(), target.getZ())};
        }

        pathPending = true;
        planner.requestPath(level, this, startNode, targets, config, MAX_ITERATIONS);
    }

    /**
     * Tick the navigation — move entity along current path.
     */
    public void tick() {
        ticksSinceLastPath++;

        // Timeout stale pending requests and fall back to vanilla nav
        if (pathPending) {
            pendingTicks++;
            if (pendingTicks > PATH_PENDING_TIMEOUT_TICKS) {
                LOGGER.debug("Path request timed out for {}, falling back to vanilla nav", entity.getName().getString());
                pathPending = false;
                pendingTicks = 0;
                if (lastRequestedTarget != null) {
                    entity.getNavigation().moveTo(
                            lastRequestedTarget.getX() + 0.5,
                            lastRequestedTarget.getY(),
                            lastRequestedTarget.getZ() + 0.5, 1.0);
                }
            }
            return;
        }

        if (currentPath == null || pathIndex >= currentPath.size()) return;

        AStarNode next = currentPath.get(pathIndex);
        double dx = next.x + 0.5 - entity.getX();
        double dz = next.z + 0.5 - entity.getZ();
        double distSq = dx * dx + dz * dz;

        if (distSq < 0.5) {
            // Reached this waypoint
            pathIndex++;
        } else {
            // Move toward waypoint using vanilla navigation
            entity.getNavigation().moveTo(next.x + 0.5, next.y, next.z + 0.5, 1.0);
        }
    }

    public boolean hasPath() {
        return currentPath != null && pathIndex < currentPath.size();
    }

    public boolean isPathPending() {
        return pathPending;
    }

    public void clearPath() {
        currentPath = null;
        pathIndex = 0;
        pathPending = false;
    }

    public void setConfig(AStarConfig config) {
        this.config = config;
    }

    @Override
    public void onFoundPath(List<AStarNode> path) {
        this.currentPath = path;
        this.pathIndex = 0;
        this.pathPending = false;
        this.ticksSinceLastPath = 0;
    }

    @Override
    public void onNoPathAvailable() {
        this.currentPath = null;
        this.pathIndex = 0;
        this.pathPending = false;
        this.pendingTicks = 0;
        // Fallback: use vanilla navigation for short distances
        BlockPos fallbackTarget = lastRequestedTarget;
        if (fallbackTarget != null) {
            double dist = entity.blockPosition().distSqr(fallbackTarget);
            if (dist < 256) { // within 16 blocks
                entity.getNavigation().moveTo(
                        fallbackTarget.getX() + 0.5,
                        fallbackTarget.getY(),
                        fallbackTarget.getZ() + 0.5, 1.0);
            }
        }
    }

    public static void shutdown() {
        if (pathExecutor != null) {
            pathExecutor.shutdownNow();
            pathExecutor = null;
            planner = null;
        }
    }
}
