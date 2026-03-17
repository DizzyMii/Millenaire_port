/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.pathing.atomicstryker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.millenaire.common.utilities.MillLog;

public class LoggedThreadPoolExecutor
extends ThreadPoolExecutor {
    public LoggedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t != null) {
            MillLog.printException("Exception occured in worker thread:", t);
        }
    }
}

