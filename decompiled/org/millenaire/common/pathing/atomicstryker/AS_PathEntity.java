/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.util.math.Vec3d
 */
package org.millenaire.common.pathing.atomicstryker;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.Vec3d;

public class AS_PathEntity
extends Path {
    private long timeLastPathIncrement = System.currentTimeMillis();
    public final PathPoint[] pointsCopy;
    private int pathIndexCopy;

    public AS_PathEntity(PathPoint[] points) {
        super(points);
        this.pointsCopy = points;
        this.pathIndexCopy = 0;
    }

    public void advancePathIndex() {
        this.timeLastPathIncrement = System.currentTimeMillis();
        ++this.pathIndexCopy;
        this.func_75872_c(this.pathIndexCopy);
    }

    public PathPoint getCurrentTargetPathPoint() {
        if (this.func_75879_b()) {
            return null;
        }
        return this.pointsCopy[this.func_75873_e()];
    }

    public PathPoint getFuturePathPoint(int jump) {
        if (this.func_75873_e() >= this.pointsCopy.length - jump) {
            return null;
        }
        return this.pointsCopy[this.func_75873_e() + jump];
    }

    public PathPoint getNextTargetPathPoint() {
        if (this.func_75873_e() >= this.pointsCopy.length - 1) {
            return null;
        }
        return this.pointsCopy[this.func_75873_e() + 1];
    }

    public PathPoint getPastTargetPathPoint(int jump) {
        if (this.func_75873_e() < jump || this.pointsCopy.length == 0) {
            return null;
        }
        return this.pointsCopy[this.func_75873_e() - jump];
    }

    public Vec3d func_75878_a(Entity var1) {
        if (super.func_75879_b()) {
            return null;
        }
        return super.func_75878_a(var1);
    }

    public PathPoint getPreviousTargetPathPoint() {
        if (this.func_75873_e() < 1 || this.pointsCopy.length == 0) {
            return null;
        }
        return this.pointsCopy[this.func_75873_e() - 1];
    }

    public long getTimeSinceLastPathIncrement() {
        return System.currentTimeMillis() - this.timeLastPathIncrement;
    }

    public void func_75872_c(int par1) {
        this.timeLastPathIncrement = System.currentTimeMillis();
        this.pathIndexCopy = par1;
        super.func_75872_c(par1);
    }
}

