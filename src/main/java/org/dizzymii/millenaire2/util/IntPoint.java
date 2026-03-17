package org.dizzymii.millenaire2.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;

/**
 * 3D integer point used for pathfinding, building plan grids, and other coordinate references.
 * Ported from org.millenaire.common.utilities.IntPoint (Forge 1.12.2).
 */
public class IntPoint implements Comparable<IntPoint> {

    public final int x;
    public final int y;
    public final int z;

    public IntPoint(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public IntPoint(Point p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
    }

    @Override
    public int compareTo(IntPoint p) {
        return p.hashCode() - this.hashCode();
    }

    public int distanceToSquared(int px, int py, int pz) {
        int dx = px - x, dy = py - y, dz = pz - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public int distanceToSquared(IntPoint p) {
        return distanceToSquared(p.x, p.y, p.z);
    }

    public IntPoint getAbove() { return new IntPoint(x, y + 1, z); }
    public IntPoint getBelow() { return new IntPoint(x, y - 1, z); }
    public IntPoint getNorth() { return new IntPoint(x, y, z - 1); }
    public IntPoint getSouth() { return new IntPoint(x, y, z + 1); }
    public IntPoint getEast()  { return new IntPoint(x + 1, y, z); }
    public IntPoint getWest()  { return new IntPoint(x - 1, y, z); }
    public IntPoint getRelative(int dx, int dy, int dz) { return new IntPoint(x + dx, y + dy, z + dz); }

    public List<IntPoint> getAllNeighbours() {
        return Arrays.asList(
                getAbove(), getBelow(), getNorth(), getEast(), getSouth(), getWest(),
                getRelative(1,1,0), getRelative(1,-1,0), getRelative(-1,1,0), getRelative(-1,-1,0),
                getRelative(1,0,1), getRelative(1,0,-1), getRelative(-1,0,1), getRelative(-1,0,-1),
                getRelative(0,1,1), getRelative(0,-1,1), getRelative(0,1,-1), getRelative(0,-1,-1),
                getRelative(1,1,1), getRelative(1,1,-1), getRelative(1,-1,1), getRelative(1,-1,-1),
                getRelative(-1,1,1), getRelative(-1,1,-1), getRelative(-1,-1,1), getRelative(-1,-1,-1)
        );
    }

    public BlockPos getBlockPos() { return new BlockPos(x, y, z); }

    public Block getBlock(Level level) {
        return level.getBlockState(getBlockPos()).getBlock();
    }

    public BlockState getBlockState(Level level) {
        return level.getBlockState(getBlockPos());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntPoint p)) return false;
        return x == p.x && y == p.y && z == p.z;
    }

    @Override
    public int hashCode() {
        return (x * 31 + y) * 31 + z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
