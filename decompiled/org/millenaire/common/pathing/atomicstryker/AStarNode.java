/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.pathing.atomicstryker;

import org.millenaire.common.pathing.atomicstryker.AStarStatic;

public class AStarNode
implements Comparable {
    public final int x;
    public final int y;
    public final int z;
    final AStarNode target;
    public AStarNode parent;
    private int g;
    private double h;

    public AStarNode(int ix, int iy, int iz) {
        this.x = ix;
        this.y = iy;
        this.z = iz;
        this.g = 0;
        this.parent = null;
        this.target = null;
    }

    public AStarNode(int ix, int iy, int iz, int dist, AStarNode p) {
        this.x = ix;
        this.y = iy;
        this.z = iz;
        this.g = dist;
        this.parent = p;
        this.target = null;
    }

    public AStarNode(int ix, int iy, int iz, int dist, AStarNode p, AStarNode t) {
        this.x = ix;
        this.y = iy;
        this.z = iz;
        this.g = dist;
        this.parent = p;
        this.target = t;
        this.updateTargetCostEstimate();
    }

    public AStarNode clone() {
        return new AStarNode(this.x, this.y, this.z, this.g, this.parent);
    }

    public int compareTo(Object o) {
        if (o instanceof AStarNode) {
            AStarNode other = (AStarNode)o;
            if (this.getF() < other.getF()) {
                return -1;
            }
            if (this.getF() > other.getF()) {
                return 1;
            }
        }
        return 0;
    }

    public boolean equals(Object checkagainst) {
        if (checkagainst instanceof AStarNode) {
            AStarNode check = (AStarNode)checkagainst;
            if (check.x == this.x && check.y == this.y && check.z == this.z) {
                return true;
            }
        }
        return false;
    }

    public double getF() {
        return (double)this.g + this.h;
    }

    public int getG() {
        return this.g;
    }

    public int hashCode() {
        return this.x << 16 ^ this.z ^ this.y << 24;
    }

    public String toString() {
        if (this.parent == null) {
            return String.format("[%d|%d|%d], dist %d, F: %f", this.x, this.y, this.z, this.g, this.getF());
        }
        return String.format("[%d|%d|%d], dist %d, parent [%d|%d|%d], F: %f", this.x, this.y, this.z, this.g, this.parent.x, this.parent.y, this.parent.z, this.getF());
    }

    public boolean updateDistance(int checkingDistance, AStarNode parentOtherNode) {
        if (checkingDistance < this.g) {
            this.g = checkingDistance;
            this.parent = parentOtherNode;
            this.updateTargetCostEstimate();
            return true;
        }
        return false;
    }

    private void updateTargetCostEstimate() {
        this.h = this.target != null ? (double)this.g + AStarStatic.getDistanceBetweenNodes(this, this.target) * 10.0 : 0.0;
    }
}

