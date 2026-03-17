package org.dizzymii.millenaire2.pathing.atomicstryker;

/**
 * A node in the A* pathfinding graph.
 * Ported from org.millenaire.common.pathing.atomicstryker.AStarNode (Forge 1.12.2).
 */
public class AStarNode implements Comparable<AStarNode> {

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

    @Override
    public AStarNode clone() {
        return new AStarNode(this.x, this.y, this.z, this.g, this.parent);
    }

    @Override
    public int compareTo(AStarNode other) {
        return Double.compare(this.getF(), other.getF());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AStarNode) {
            AStarNode check = (AStarNode) obj;
            return check.x == this.x && check.y == this.y && check.z == this.z;
        }
        return false;
    }

    public double getF() {
        return (double) this.g + this.h;
    }

    public int getG() {
        return this.g;
    }

    @Override
    public int hashCode() {
        return this.x << 16 ^ this.z ^ this.y << 24;
    }

    @Override
    public String toString() {
        if (this.parent == null) {
            return String.format("[%d|%d|%d], dist %d, F: %f", x, y, z, g, getF());
        }
        return String.format("[%d|%d|%d], dist %d, parent [%d|%d|%d], F: %f",
                x, y, z, g, parent.x, parent.y, parent.z, getF());
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
        this.h = this.target != null
                ? (double) this.g + AStarStatic.getDistanceBetweenNodes(this, this.target) * 10.0
                : 0.0;
    }
}
