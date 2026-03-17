/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.pathing.atomicstryker;

import java.util.List;
import org.millenaire.common.pathing.atomicstryker.AStarNode;

public interface IAStarPathedEntity {
    public void onFoundPath(List<AStarNode> var1);

    public void onNoPathAvailable();
}

