package org.dizzymii.millenaire2.pathing.atomicstryker;


import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Thread pool executor that logs uncaught exceptions from pathfinding workers.
 * Ported from org.millenaire.common.pathing.atomicstryker.LoggedThreadPoolExecutor (Forge 1.12.2).
 */
public class LoggedThreadPoolExecutor extends ThreadPoolExecutor {
    private static final Logger LOGGER = LogUtils.getLogger();

    public LoggedThreadPoolExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime) {
        super(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t != null) {
            LOGGER.error("Pathfinding worker threw exception: " + t.getMessage());
        }
    }
}
