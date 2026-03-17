/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package org.millenaire.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.item.ItemMill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

public class ItemBrickMould
extends ItemMill {
    public ItemBrickMould(String itemName) {
        super(itemName);
    }

    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.func_180495_p(pos).func_177230_c() == Blocks.field_150433_aE) {
            side = EnumFacing.DOWN;
        } else {
            if (side == EnumFacing.DOWN) {
                pos = pos.func_177977_b();
            }
            if (side == EnumFacing.UP) {
                pos = pos.func_177984_a();
            }
            if (side == EnumFacing.EAST) {
                pos = pos.func_177974_f();
            }
            if (side == EnumFacing.WEST) {
                pos = pos.func_177976_e();
            }
            if (side == EnumFacing.SOUTH) {
                pos = pos.func_177968_d();
            }
            if (side == EnumFacing.NORTH) {
                pos = pos.func_177978_c();
            }
        }
        if (!world.func_190527_a((Block)MillBlocks.WET_BRICK, pos, false, side, (Entity)null)) {
            return EnumActionResult.PASS;
        }
        if (world.func_180495_p(pos).func_177230_c() != Blocks.field_150350_a) {
            return EnumActionResult.PASS;
        }
        ItemStack is = player.func_184586_b(hand);
        if (is.func_77952_i() % 4 == 0) {
            if (MillCommonUtilities.countChestItems((IInventory)player.field_71071_by, Blocks.field_150346_d, 0) == 0 || MillCommonUtilities.countChestItems((IInventory)player.field_71071_by, (Block)Blocks.field_150354_m, 0) == 0) {
                if (!world.field_72995_K) {
                    ServerSender.sendTranslatedSentence(player, 'f', "ui.brickinstructions", new String[0]);
                }
                return EnumActionResult.PASS;
            }
            WorldUtilities.getItemsFromChest((IInventory)player.field_71071_by, Blocks.field_150346_d, 0, 1);
            WorldUtilities.getItemsFromChest((IInventory)player.field_71071_by, (Block)Blocks.field_150354_m, 0, 1);
        }
        WorldUtilities.setBlockstate(world, new Point(pos), MillBlocks.BS_WET_BRICK, true, false);
        is.func_77972_a(1, (EntityLivingBase)player);
        return EnumActionResult.SUCCESS;
    }
}

