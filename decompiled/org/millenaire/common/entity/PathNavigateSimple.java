/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.init.Blocks
 *  net.minecraft.pathfinding.PathFinder
 *  net.minecraft.pathfinding.PathNavigate
 *  net.minecraft.pathfinding.PathNodeType
 *  net.minecraft.pathfinding.WalkNodeProcessor
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package org.millenaire.common.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class PathNavigateSimple
extends PathNavigate {
    public PathNavigateSimple(EntityLiving entityIn, World worldIn) {
        super(entityIn, worldIn);
    }

    protected boolean func_75485_k() {
        return true;
    }

    private boolean getCanSwim() {
        return true;
    }

    protected Vec3d func_75502_i() {
        return new Vec3d(this.field_75515_a.field_70165_t, (double)this.getPathablePosY(), this.field_75515_a.field_70161_v);
    }

    private int getPathablePosY() {
        if (this.field_75515_a.func_70090_H() && this.getCanSwim()) {
            int i = (int)this.field_75515_a.func_174813_aQ().field_72338_b;
            Block block = this.field_75513_b.func_180495_p(new BlockPos(MathHelper.func_76128_c((double)this.field_75515_a.field_70165_t), i, MathHelper.func_76128_c((double)this.field_75515_a.field_70161_v))).func_177230_c();
            int j = 0;
            while (block == Blocks.field_150358_i || block == Blocks.field_150355_j) {
                block = this.field_75513_b.func_180495_p(new BlockPos(MathHelper.func_76128_c((double)this.field_75515_a.field_70165_t), ++i, MathHelper.func_76128_c((double)this.field_75515_a.field_70161_v))).func_177230_c();
                if (++j <= 16) continue;
                return (int)this.field_75515_a.func_174813_aQ().field_72338_b;
            }
            return i;
        }
        return (int)(this.field_75515_a.func_174813_aQ().field_72338_b + 0.5);
    }

    protected PathFinder func_179679_a() {
        this.field_179695_a = new WalkNodeProcessor();
        this.field_179695_a.func_186317_a(true);
        return null;
    }

    protected boolean func_75493_a(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
        int i = MathHelper.func_76128_c((double)posVec31.field_72450_a);
        int j = MathHelper.func_76128_c((double)posVec31.field_72449_c);
        double d0 = posVec32.field_72450_a - posVec31.field_72450_a;
        double d1 = posVec32.field_72449_c - posVec31.field_72449_c;
        double d2 = d0 * d0 + d1 * d1;
        if (d2 < 1.0E-8) {
            return false;
        }
        double d3 = 1.0 / Math.sqrt(d2);
        if (!this.isSafeToStandAt(i, (int)posVec31.field_72448_b, j, sizeX += 2, sizeY, sizeZ += 2, posVec31, d0 *= d3, d1 *= d3)) {
            return false;
        }
        sizeX -= 2;
        sizeZ -= 2;
        double d4 = 1.0 / Math.abs(d0);
        double d5 = 1.0 / Math.abs(d1);
        double d6 = (double)i - posVec31.field_72450_a;
        double d7 = (double)j - posVec31.field_72449_c;
        if (d0 >= 0.0) {
            d6 += 1.0;
        }
        if (d1 >= 0.0) {
            d7 += 1.0;
        }
        d6 /= d0;
        d7 /= d1;
        int k = d0 < 0.0 ? -1 : 1;
        int l = d1 < 0.0 ? -1 : 1;
        int i1 = MathHelper.func_76128_c((double)posVec32.field_72450_a);
        int j1 = MathHelper.func_76128_c((double)posVec32.field_72449_c);
        int k1 = i1 - i;
        int l1 = j1 - j;
        while (k1 * k > 0 || l1 * l > 0) {
            if (d6 < d7) {
                d6 += d4;
                k1 = i1 - (i += k);
            } else {
                d7 += d5;
                l1 = j1 - (j += l);
            }
            if (this.isSafeToStandAt(i, (int)posVec31.field_72448_b, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) continue;
            return false;
        }
        return true;
    }

    private boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d p_179692_7_, double p_179692_8_, double p_179692_10_) {
        for (BlockPos blockpos : BlockPos.func_177980_a((BlockPos)new BlockPos(x, y, z), (BlockPos)new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1))) {
            Block block;
            double d1;
            double d0 = (double)blockpos.func_177958_n() + 0.5 - p_179692_7_.field_72450_a;
            if (!(d0 * p_179692_8_ + (d1 = (double)blockpos.func_177952_p() + 0.5 - p_179692_7_.field_72449_c) * p_179692_10_ >= 0.0) || (block = this.field_75513_b.func_180495_p(blockpos).func_177230_c()).func_176205_b((IBlockAccess)this.field_75513_b, blockpos)) continue;
            return false;
        }
        return true;
    }

    private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d vec31, double p_179683_8_, double p_179683_10_) {
        int i = x - sizeX / 2;
        int j = z - sizeZ / 2;
        if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_)) {
            return false;
        }
        for (int k = i; k < i + sizeX; ++k) {
            for (int l = j; l < j + sizeZ; ++l) {
                double d0 = (double)k + 0.5 - vec31.field_72450_a;
                double d1 = (double)l + 0.5 - vec31.field_72449_c;
                if (!(d0 * p_179683_8_ + d1 * p_179683_10_ >= 0.0)) continue;
                PathNodeType pathnodetype = this.field_179695_a.func_186319_a((IBlockAccess)this.field_75513_b, k, y - 1, l, this.field_75515_a, sizeX, sizeY, sizeZ, true, true);
                if (pathnodetype == PathNodeType.WATER) {
                    return false;
                }
                if (pathnodetype == PathNodeType.LAVA) {
                    return false;
                }
                if (pathnodetype == PathNodeType.OPEN) {
                    return false;
                }
                pathnodetype = this.field_179695_a.func_186319_a((IBlockAccess)this.field_75513_b, k, y, l, this.field_75515_a, sizeX, sizeY, sizeZ, true, true);
                float f = this.field_75515_a.func_184643_a(pathnodetype);
                if (f < 0.0f || f >= 8.0f) {
                    return false;
                }
                if (pathnodetype != PathNodeType.DAMAGE_FIRE && pathnodetype != PathNodeType.DANGER_FIRE && pathnodetype != PathNodeType.DAMAGE_OTHER) continue;
                return false;
            }
        }
        return true;
    }

    protected void func_75508_h() {
        Vec3d vec3d = this.func_75502_i();
        int i = this.field_75514_c.func_75874_d();
        for (int j = this.field_75514_c.func_75873_e(); j < this.field_75514_c.func_75874_d(); ++j) {
            if ((double)this.field_75514_c.func_75877_a((int)j).field_75837_b == Math.floor(vec3d.field_72448_b)) continue;
            i = j;
            break;
        }
        this.field_188561_o = this.field_75515_a.field_70130_N > 0.75f ? this.field_75515_a.field_70130_N / 2.0f : 0.75f - this.field_75515_a.field_70130_N / 2.0f;
        Vec3d vec3d1 = this.field_75514_c.func_186310_f();
        if (MathHelper.func_76135_e((float)((float)(this.field_75515_a.field_70165_t - (vec3d1.field_72450_a + 0.5)))) < this.field_188561_o && MathHelper.func_76135_e((float)((float)(this.field_75515_a.field_70161_v - (vec3d1.field_72449_c + 0.5)))) < this.field_188561_o && Math.abs(this.field_75515_a.field_70163_u - vec3d1.field_72448_b) < 1.0) {
            this.field_75514_c.func_75872_c(this.field_75514_c.func_75873_e() + 1);
        }
        int k = MathHelper.func_76123_f((float)this.field_75515_a.field_70130_N);
        int l = MathHelper.func_76123_f((float)this.field_75515_a.field_70131_O);
        int i1 = k;
        for (int j1 = i - 1; j1 >= this.field_75514_c.func_75873_e(); --j1) {
            if (!this.func_75493_a(vec3d, this.field_75514_c.func_75881_a((Entity)this.field_75515_a, j1), k, l, i1)) continue;
            this.field_75514_c.func_75872_c(j1);
            break;
        }
    }
}

