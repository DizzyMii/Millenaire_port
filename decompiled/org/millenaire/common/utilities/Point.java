/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityBrewingStand
 *  net.minecraft.tileentity.TileEntityDispenser
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.tileentity.TileEntitySign
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package org.millenaire.common.utilities;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.entity.TileEntityImportTable;
import org.millenaire.common.entity.TileEntityLockedChest;
import org.millenaire.common.entity.TileEntityPanel;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.pathing.atomicstryker.RegionMapper;
import org.millenaire.common.utilities.IntPoint;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.WorldUtilities;

public class Point {
    public final double x;
    public final double y;
    public final double z;

    public static final Point read(NBTTagCompound nbttagcompound, String label) {
        double x = nbttagcompound.func_74769_h(label + "_xCoord");
        double y = nbttagcompound.func_74769_h(label + "_yCoord");
        double z = nbttagcompound.func_74769_h(label + "_zCoord");
        if (x == 0.0 && y == 0.0 && z == 0.0) {
            return null;
        }
        return new Point(x, y, z);
    }

    public Point(AStarNode node) {
        this.x = node.x;
        this.y = node.y;
        this.z = node.z;
    }

    public Point(BlockPos pos) {
        this.x = pos.func_177958_n();
        this.y = pos.func_177956_o();
        this.z = pos.func_177952_p();
    }

    public Point(double i, double j, double k) {
        this.x = i;
        this.y = j;
        this.z = k;
    }

    public Point(Entity ent) {
        this.x = ent.field_70165_t;
        this.y = ent.field_70163_u;
        this.z = ent.field_70161_v;
    }

    public Point(PathPoint pp) {
        this.x = pp.field_75839_a;
        this.y = pp.field_75837_b;
        this.z = pp.field_75838_c;
    }

    public Point(String s) {
        String[] scoord = s.split("/");
        this.x = Double.parseDouble(scoord[0]);
        this.y = Double.parseDouble(scoord[1]);
        this.z = Double.parseDouble(scoord[2]);
    }

    public String approximateDistanceLongString(Point p) {
        int dist = (int)this.distanceTo(p);
        if (dist < 950) {
            return dist / 100 * 100 + " " + LanguageUtilities.string("other.metre");
        }
        if ((dist = Math.round(dist / 500)) % 2 == 0) {
            return dist / 2 + " " + LanguageUtilities.string("other.kilometre");
        }
        return (dist - 1) / 2 + LanguageUtilities.string("other.andhalf") + " " + LanguageUtilities.string("other.kilometre");
    }

    public String approximateDistanceShortString(Point p) {
        int dist = (int)this.distanceTo(p);
        if (dist < 950) {
            return dist / 100 * 100 + "m";
        }
        if ((dist /= 500) % 2 == 0) {
            return dist / 2 + "km";
        }
        return (dist - 1) / 2 + ".5 km";
    }

    public String directionTo(Point p) {
        return this.directionTo(p, false);
    }

    public String directionTo(Point p, boolean prefixed) {
        String direction;
        String prefix = prefixed ? "other.tothe" : "other.";
        int xdist = MathHelper.func_76128_c((double)(p.x - this.x));
        int zdist = MathHelper.func_76128_c((double)(p.z - this.z));
        if ((double)Math.abs(xdist) > (double)Math.abs(zdist) * 0.6 && (double)Math.abs(xdist) < (double)Math.abs(zdist) * 1.4 || (double)Math.abs(zdist) > (double)Math.abs(xdist) * 0.6 && (double)Math.abs(zdist) < (double)Math.abs(xdist) * 1.4) {
            direction = zdist > 0 ? prefix + "south-" : prefix + "north-";
            direction = xdist > 0 ? direction + "east" : direction + "west";
        } else {
            direction = Math.abs(xdist) > Math.abs(zdist) ? (xdist > 0 ? prefix + "east" : prefix + "west") : (zdist > 0 ? prefix + "south" : prefix + "north");
        }
        return direction;
    }

    public String directionToShort(Point p) {
        String direction;
        int xdist = MathHelper.func_76128_c((double)(p.x - this.x));
        int zdist = MathHelper.func_76128_c((double)(p.z - this.z));
        if ((double)Math.abs(xdist) > (double)Math.abs(zdist) * 0.6 && (double)Math.abs(xdist) < (double)Math.abs(zdist) * 1.4 || (double)Math.abs(zdist) > (double)Math.abs(xdist) * 0.6 && (double)Math.abs(zdist) < (double)Math.abs(xdist) * 1.4) {
            direction = zdist > 0 ? LanguageUtilities.string("other.south_short") : LanguageUtilities.string("other.north_short");
            direction = xdist > 0 ? direction + LanguageUtilities.string("other.east_short") : direction + LanguageUtilities.string("other.west_short");
        } else {
            direction = Math.abs(xdist) > Math.abs(zdist) ? (xdist > 0 ? LanguageUtilities.string("other.east_short") : LanguageUtilities.string("other.west_short")) : (zdist > 0 ? LanguageUtilities.string("other.south_short") : LanguageUtilities.string("other.north_short"));
        }
        return direction;
    }

    public String distanceDirectionShort(Point p) {
        return LanguageUtilities.string("other.directionshort", this.directionToShort(p), "" + (int)this.horizontalDistanceTo(p) + "m");
    }

    public double distanceTo(double px, double py, double pz) {
        double d = px - this.x;
        double d1 = py - this.y;
        double d2 = pz - this.z;
        return MathHelper.func_76133_a((double)(d * d + d1 * d1 + d2 * d2));
    }

    public double distanceTo(Entity e) {
        return this.distanceTo(e.field_70165_t, e.field_70163_u, e.field_70161_v);
    }

    public double distanceTo(Point p) {
        if (p == null) {
            return -1.0;
        }
        return this.distanceTo(p.x, p.y, p.z);
    }

    public double distanceToSquared(double px, double py, double pz) {
        double d = px - this.x;
        double d1 = py - this.y;
        double d2 = pz - this.z;
        return d * d + d1 * d1 + d2 * d2;
    }

    public double distanceToSquared(Entity e) {
        return this.distanceToSquared(e.field_70165_t, e.field_70163_u, e.field_70161_v);
    }

    public double distanceToSquared(PathPoint pp) {
        return this.distanceToSquared(pp.field_75839_a, pp.field_75837_b, pp.field_75838_c);
    }

    public double distanceToSquared(Point p) {
        return this.distanceToSquared(p.x, p.y, p.z);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Point)) {
            return false;
        }
        Point p = (Point)o;
        return p.x == this.x && p.y == this.y && p.z == this.z;
    }

    public Point getAbove() {
        return new Point(this.x, this.y + 1.0, this.z);
    }

    public Point getBelow() {
        return new Point(this.x, this.y - 1.0, this.z);
    }

    public Block getBlock(World world) {
        return world.func_180495_p(this.getBlockPos()).func_177230_c();
    }

    public IBlockState getBlockActualState(World world) {
        Block block = this.getBlock(world);
        BlockPos pos = this.getBlockPos();
        IBlockState state = world.func_180495_p(pos);
        return block.func_176221_a(state, (IBlockAccess)world, pos);
    }

    public BlockPos getBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public TileEntityBrewingStand getBrewingStand(World world) {
        TileEntity ent = world.func_175625_s(this.getBlockPos());
        if (ent != null && ent instanceof TileEntityBrewingStand) {
            return (TileEntityBrewingStand)ent;
        }
        return null;
    }

    public String getChunkString() {
        return this.getChunkX() + "/" + this.getChunkZ();
    }

    public int getChunkX() {
        return this.getiX() >> 4;
    }

    public int getChunkZ() {
        return this.getiZ() >> 4;
    }

    public TileEntityDispenser getDispenser(World world) {
        TileEntity ent = world.func_175625_s(this.getBlockPos());
        if (ent != null && ent instanceof TileEntityDispenser) {
            return (TileEntityDispenser)ent;
        }
        return null;
    }

    public Point getEast() {
        return new Point(this.x + 1.0, this.y, this.z);
    }

    public TileEntityFirePit getFirePit(World world) {
        TileEntity ent = world.func_175625_s(this.getBlockPos());
        if (ent != null && ent instanceof TileEntityFirePit) {
            return (TileEntityFirePit)ent;
        }
        return null;
    }

    public TileEntityFurnace getFurnace(World world) {
        TileEntity ent = world.func_175625_s(this.getBlockPos());
        if (ent != null && ent instanceof TileEntityFurnace) {
            return (TileEntityFurnace)ent;
        }
        return null;
    }

    public TileEntityImportTable getImportTable(World world) {
        TileEntity ent = world.func_175625_s(this.getBlockPos());
        if (ent != null && ent instanceof TileEntityImportTable) {
            return (TileEntityImportTable)ent;
        }
        return null;
    }

    public IntPoint getIntPoint() {
        return new IntPoint(this);
    }

    public String getIntString() {
        return this.getiX() + "/" + this.getiY() + "/" + this.getiZ();
    }

    public int getiX() {
        return MathHelper.func_76128_c((double)this.x);
    }

    public int getiY() {
        return MathHelper.func_76128_c((double)this.y);
    }

    public int getiZ() {
        return MathHelper.func_76128_c((double)this.z);
    }

    public int getMeta(World world) {
        IBlockState state = world.func_180495_p(this.getBlockPos());
        return state.func_177230_c().func_176201_c(state);
    }

    public TileEntityLockedChest getMillChest(World world) {
        TileEntity ent = world.func_175625_s(this.getBlockPos());
        if (ent != null && ent instanceof TileEntityLockedChest) {
            return (TileEntityLockedChest)ent;
        }
        return null;
    }

    public List<Point> getNeightbours() {
        return Arrays.asList(this.getAbove(), this.getBelow(), this.getNorth(), this.getEast(), this.getSouth(), this.getWest());
    }

    public Point getNorth() {
        return new Point(this.x, this.y, this.z - 1.0);
    }

    public RegionMapper.Point2D getP2D() {
        return new RegionMapper.Point2D(this.getiX(), this.getiZ());
    }

    public TileEntityPanel getPanel(World world) {
        TileEntity ent = world.func_175625_s(this.getBlockPos());
        if (ent != null && ent instanceof TileEntityPanel) {
            return (TileEntityPanel)ent;
        }
        return null;
    }

    public PathPoint getPathPoint() {
        return new PathPoint((int)this.x, (int)this.y, (int)this.z);
    }

    public String getPathString() {
        return this.getiX() + "_" + this.getiY() + "_" + this.getiZ();
    }

    public Point getRelative(double dx, double dy, double dz) {
        return new Point(this.x + dx, this.y + dy, this.z + dz);
    }

    public TileEntitySign getSign(World world) {
        TileEntity ent = world.func_175625_s(this.getBlockPos());
        if (ent != null && ent instanceof TileEntitySign) {
            return (TileEntitySign)ent;
        }
        return null;
    }

    public Point getSouth() {
        return new Point(this.x, this.y, this.z + 1.0);
    }

    public TileEntity getTileEntity(World world) {
        return world.func_175625_s(this.getBlockPos());
    }

    public Point getWest() {
        return new Point(this.x - 1.0, this.y, this.z);
    }

    public int hashCode() {
        return (int)(this.x + (double)((int)this.y << 8) + (double)((int)this.z << 16));
    }

    public double horizontalDistanceTo(BlockPos bp) {
        return this.horizontalDistanceTo(bp.func_177958_n(), bp.func_177952_p());
    }

    public double horizontalDistanceTo(double px, double pz) {
        double d = px - this.x;
        double d2 = pz - this.z;
        return MathHelper.func_76133_a((double)(d * d + d2 * d2));
    }

    public double horizontalDistanceTo(Entity e) {
        return this.horizontalDistanceTo(e.field_70165_t, e.field_70161_v);
    }

    public double horizontalDistanceTo(PathPoint p) {
        if (p == null) {
            return 0.0;
        }
        return this.horizontalDistanceTo(p.field_75839_a, p.field_75838_c);
    }

    public double horizontalDistanceTo(Point p) {
        if (p == null) {
            return 0.0;
        }
        return this.horizontalDistanceTo(p.x, p.z);
    }

    public double horizontalDistanceToSquared(double px, double pz) {
        double d = px - this.x;
        double d2 = pz - this.z;
        return d * d + d2 * d2;
    }

    public double horizontalDistanceToSquared(Entity e) {
        return this.horizontalDistanceToSquared(e.field_70165_t, e.field_70161_v);
    }

    public double horizontalDistanceToSquared(Point p) {
        return this.horizontalDistanceToSquared(p.x, p.z);
    }

    public boolean isBlockPassable(World world) {
        return !this.getBlock(world).func_176223_P().func_185904_a().func_76220_a();
    }

    public boolean sameBlock(PathPoint p) {
        if (p == null) {
            return false;
        }
        return this.getiX() == p.field_75839_a && this.getiY() == p.field_75837_b && this.getiZ() == p.field_75838_c;
    }

    public boolean sameBlock(Point p) {
        if (p == null) {
            return false;
        }
        return this.getiX() == p.getiX() && this.getiY() == p.getiY() && this.getiZ() == p.getiZ();
    }

    public void setBlock(World world, Block block, int meta, boolean notify, boolean sound) {
        WorldUtilities.setBlockAndMetadata(world, this, block, meta, notify, sound);
    }

    public void setBlockState(World world, IBlockState state) {
        world.func_175656_a(this.getBlockPos(), state);
    }

    public int squareRadiusDistance(Point p) {
        return (int)Math.max(Math.abs(this.x - p.x), Math.abs(this.z - p.z));
    }

    public String toString() {
        return Math.round(this.x * 100.0) / 100L + "/" + Math.round(this.y * 100.0) / 100L + "/" + Math.round(this.z * 100.0) / 100L;
    }

    public void write(NBTTagCompound nbttagcompound, String label) {
        nbttagcompound.func_74780_a(label + "_xCoord", this.x);
        nbttagcompound.func_74780_a(label + "_yCoord", this.y);
        nbttagcompound.func_74780_a(label + "_zCoord", this.z);
    }
}

