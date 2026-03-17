package org.dizzymii.millenaire2.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Millénaire's core position class — wraps a 3D integer coordinate.
 * Used extensively throughout the mod for building positions, villager locations, etc.
 * Provides conversion to/from Minecraft's BlockPos and serialization.
 */
public class Point {
    public int x;
    public int y;
    public int z;

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
        tag.putInt(prefix + "X", x);
        tag.putInt(prefix + "Y", y);
        tag.putInt(prefix + "Z", z);
    }

    public static Point readFromNBT(CompoundTag tag, String prefix) {
        if (!tag.contains(prefix + "X")) return null;
        return new Point(
                tag.getInt(prefix + "X"),
                tag.getInt(prefix + "Y"),
                tag.getInt(prefix + "Z")
        );
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public static Point readFromBuf(FriendlyByteBuf buf) {
        return new Point(buf.readInt(), buf.readInt(), buf.readInt());
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
