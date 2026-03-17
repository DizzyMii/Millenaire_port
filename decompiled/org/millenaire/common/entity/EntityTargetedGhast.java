/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.monster.EntityGhast
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 */
package org.millenaire.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.millenaire.common.utilities.Point;

public class EntityTargetedGhast
extends EntityGhast {
    public Point target = null;

    public EntityTargetedGhast(World par1World) {
        super(par1World);
    }

    protected boolean func_70692_ba() {
        return false;
    }

    public void func_70071_h_() {
        if (this.target != null) {
            if (this.target.distanceTo((Entity)this) > 20.0) {
                this.func_70605_aq().func_75642_a(this.target.x, this.target.y, this.target.z, (double)this.func_70689_ay());
            } else if (this.target.distanceTo((Entity)this) < 10.0) {
                this.func_70605_aq().func_75642_a(this.target.x + (double)((this.field_70146_Z.nextFloat() * 2.0f - 1.0f) * 16.0f), this.target.y + (double)((this.field_70146_Z.nextFloat() * 2.0f - 1.0f) * 16.0f), this.target.z + (double)((this.field_70146_Z.nextFloat() * 2.0f - 1.0f) * 16.0f), (double)this.func_70689_ay());
            }
        }
        super.func_70071_h_();
    }

    public void func_70020_e(NBTTagCompound par1nbtTagCompound) {
        super.func_70020_e(par1nbtTagCompound);
        this.target = Point.read(par1nbtTagCompound, "targetPoint");
    }

    public NBTTagCompound func_189511_e(NBTTagCompound par1nbtTagCompound) {
        super.func_189511_e(par1nbtTagCompound);
        if (this.target != null) {
            this.target.write(par1nbtTagCompound, "targetPoint");
        }
        return par1nbtTagCompound;
    }
}

