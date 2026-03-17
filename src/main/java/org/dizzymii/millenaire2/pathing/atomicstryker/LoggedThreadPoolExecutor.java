package org.dizzymii.millenaire2.pathing.atomicstryker;

import org.dizzymii.millenaire2.util.MillLog;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Thread pool executor that logs uncaught exceptions from pathfinding workers.
 * Ported from org.millenaire.common.pathing.atomicstryker.LoggedThreadPoolExecutor (Forge 1.12.2).
 */
public class LoggedThreadPoolExecutor extends ThreadPoolExecutor {

    public LoggedThreadPoolExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime) {
        super(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t != null) {
            MillLog.error(null, "Pathfinding worker threw exception: " + t.getMessage());
        }
    }
}
