package org.dizzymii.millenaire2.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Millénaire's core position class — wraps a 3D integer coordinate.
 * Used extensively throughout the mod for building positions, villager locations, etc.
 * Provides conversion to/from Minecraft's BlockPos and serialization.
 */
public class Point {
    public final int x;
    public final int y;
    public final int z;

    public Point() {
        this(0, 0, 0);
    }

    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point(BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public Point(Point other) {
        this(other.x, other.y, other.z);
    }

    public BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }

    public double distanceTo(Point other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double horizontalDistanceTo(Point other) {
        double dx = this.x - other.x;
        double dz = this.z - other.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    public int horizontalDistanceToSquared(Point other) {
        int dx = this.x - other.x;
        int dz = this.z - other.z;
        return dx * dx + dz * dz;
    }

    public Point getAbove() {
        return new Point(x, y + 1, z);
    }

    public Point getBelow() {
        return new Point(x, y - 1, z);
    }

    public Point getRelative(int dx, int dy, int dz) {
        return new Point(x + dx, y + dy, z + dz);
    }

    // --- Serialization ---

    public void write(CompoundTag tag, String prefix) {
        writeToNBT(tag, prefix);
    }

    public static Point read(CompoundTag tag, String prefix) {
        return readFromNBT(tag, prefix);
    }

    public void writeToNBT(CompoundTag tag, String prefix) {
        tag.put(prefix, NbtUtils.writeBlockPos(toBlockPos()));
    }

    public static Point readFromNBT(CompoundTag tag, String prefix) {
        if (!tag.contains(prefix)) return null;
        BlockPos pos = NbtUtils.readBlockPos(tag, prefix).orElse(null);
        return pos != null ? new Point(pos) : null;
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        buf.writeBlockPos(toBlockPos());
    }

    public static Point readFromBuf(FriendlyByteBuf buf) {
        return new Point(buf.readBlockPos());
    }

    // --- Object overrides ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point other)) return false;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
