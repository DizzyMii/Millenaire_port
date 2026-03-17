package org.dizzymii.millenaire2.util;

/**
 * Simple 2D integer point, used for building plan grid coordinates and other 2D references.
 */
public class IntPoint {
    public final int x;
    public final int z;

    public IntPoint(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntPoint other)) return false;
        return x == other.x && z == other.z;
    }

    @Override
    public int hashCode() {
        return 31 * x + z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + z + ")";
    }
}
