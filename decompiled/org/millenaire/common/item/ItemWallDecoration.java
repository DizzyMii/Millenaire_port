/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package org.millenaire.common.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.entity.EntityWallDecoration;
import org.millenaire.common.item.ItemMill;

public class ItemWallDecoration
extends ItemMill {
    public int type;

    public ItemWallDecoration(String itemName, int type) {
        super(itemName);
        this.type = type;
    }

    public EnumActionResult func_180614_a(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.func_184586_b(hand);
        BlockPos blockpos = pos.func_177972_a(facing);
        if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && player.func_175151_a(blockpos, facing, itemstack)) {
            EntityWallDecoration entityhanging = new EntityWallDecoration(worldIn, blockpos, facing, this.type, false);
            if (entityhanging != null && entityhanging.func_70518_d()) {
                if (!worldIn.field_72995_K) {
                    entityhanging.func_184523_o();
                    worldIn.func_72838_d((Entity)entityhanging);
                }
                itemstack.func_190918_g(1);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }
}

