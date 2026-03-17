/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package org.millenaire.common.utilities;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.utilities.Point;

public class IntPoint
implements Comparable<IntPoint> {
    final int x;
    final int y;
    final int z;

    public IntPoint(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public IntPoint(Point p) {
        this.x = p.getiX();
        this.y = p.getiY();
        this.z = p.getiZ();
    }

    @Override
    public int compareTo(IntPoint p) {
        return p.hashCode() - this.hashCode();
    }

    public int distanceToSquared(int px, int py, int pz) {
        int d = px - this.x;
        int d1 = py - this.y;
        int d2 = pz - this.z;
        return d * d + d1 * d1 + d2 * d2;
    }

    public int distanceToSquared(IntPoint p) {
        return this.distanceToSquared(p.x, p.y, p.z);
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof IntPoint)) {
            return false;
        }
        IntPoint p = (IntPoint)o;
        return this.x == p.x && this.y == p.y && this.z == p.z;
    }

    public IntPoint getAbove() {
        return new IntPoint(this.x, this.y + 1, this.z);
    }

    public List<IntPoint> getAllNeightbours() {
        return Arrays.asList(this.getAbove(), this.getBelow(), this.getNorth(), this.getEast(), this.getSouth(), this.getWest(), this.getRelative(1, 1, 0), this.getRelative(1, -1, 0), this.getRelative(-1, 1, 0), this.getRelative(-1, -1, 0), this.getRelative(1, 0, 1), this.getRelative(1, 0, -1), this.getRelative(-1, 0, 1), this.getRelative(-1, 0, -1), this.getRelative(0, 1, 1), this.getRelative(0, -1, 1), this.getRelative(0, 1, -1), this.getRelative(0, -1, -1), this.getRelative(1, 1, 1), this.getRelative(1, 1, -1), this.getRelative(1, -1, 1), this.getRelative(1, -1, -1), this.getRelative(-1, 1, 1), this.getRelative(-1, 1, -1), this.getRelative(-1, -1, 1), this.getRelative(-1, -1, -1));
    }

    public IntPoint getBelow() {
        return new IntPoint(this.x, this.y - 1, this.z);
    }

    public Block getBlock(World world) {
        return world.func_180495_p(this.getBlockPos()).func_177230_c();
    }

    public BlockPos getBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public IntPoint getEast() {
        return new IntPoint(this.x + 1, this.y, this.z);
    }

    public List<IntPoint> getNeightboursWithDiagonals() {
        return Arrays.asList(this.getAbove(), this.getBelow(), this.getNorth(), this.getEast(), this.getSouth(), this.getWest(), this.getRelative(1, 1, 0), this.getRelative(1, -1, 0), this.getRelative(-1, 1, 0), this.getRelative(-1, -1, 0), this.getRelative(1, 0, 1), this.getRelative(1, 0, -1), this.getRelative(-1, 0, 1), this.getRelative(-1, 0, -1), this.getRelative(0, 1, 0), this.getRelative(0, -1, 1), this.getRelative(0, 1, -1), this.getRelative(0, -1, -1));
    }

    public IntPoint getNorth() {
        return new IntPoint(this.x, this.y, this.z - 1);
    }

    public Point getPoint() {
        return new Point(this.x, this.y, this.z);
    }

    public IntPoint getRelative(int dx, int dy, int dz) {
        return new IntPoint(this.x + dx, this.y + dy, this.z + dz);
    }

    public IntPoint getSouth() {
        return new IntPoint(this.x, this.y, this.z + 1);
    }

    public IntPoint getWest() {
        return new IntPoint(this.x - 1, this.y, this.z);
    }

    public int hashCode() {
        return this.x + (this.y << 8) + (this.z << 16);
    }

    public int horizontalDistanceToSquared(int px, int pz) {
        int d = px - this.x;
        int d2 = pz - this.z;
        return d * d + d2 * d2;
    }

    public int horizontalDistanceToSquared(IntPoint p) {
        return this.horizontalDistanceToSquared(p.x, p.z);
    }

    public void setBlockState(World world, IBlockState state) {
        world.func_175656_a(this.getBlockPos(), state);
    }
}

