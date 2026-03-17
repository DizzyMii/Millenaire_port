/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.monster.EntityBlaze
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.world.World
 */
package org.millenaire.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.millenaire.common.utilities.Point;

public class EntityTargetedBlaze
extends EntityBlaze {
    public Point target = null;

    public EntityTargetedBlaze(World par1World) {
        super(par1World);
    }

    protected boolean func_70692_ba() {
        return false;
    }

    private boolean isCourseTraversable(double par1, double par3, double par5, double par7) {
        double d4 = (this.target.x - this.field_70165_t) / par7;
        double d5 = (this.target.y - this.field_70163_u) / par7;
        double d6 = (this.target.z - this.field_70161_v) / par7;
        AxisAlignedBB axisalignedbb = this.func_70046_E().func_72321_a(0.0, 0.0, 0.0);
        int i = 1;
        while ((double)i < par7) {
            axisalignedbb.func_72317_d(d4, d5, d6);
            if (!this.field_70170_p.func_184144_a((Entity)this, axisalignedbb).isEmpty()) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public boolean func_70026_G() {
        return false;
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

