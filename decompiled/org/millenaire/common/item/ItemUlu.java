/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDirt
 *  net.minecraft.block.BlockDirt$DirtType
 *  net.minecraft.block.BlockPlanks
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.BlockSnow
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.BlockSod;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.item.ItemMill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

public class ItemUlu
extends ItemMill {
    private static final IBlockState COARSE_DIRT = Blocks.field_150346_d.func_176223_P().func_177226_a((IProperty)BlockDirt.field_176386_a, (Comparable)BlockDirt.DirtType.COARSE_DIRT);

    public ItemUlu(String itemName) {
        super(itemName);
    }

    private EnumActionResult attemptSodPlanks(EntityPlayer player, World world, BlockPos pos, EnumFacing side, EnumHand hand) {
        int resUseCount;
        if (world.func_180495_p(pos).func_177230_c() == Blocks.field_150431_aC) {
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
        if (world.func_180495_p(pos).func_177230_c() != Blocks.field_150350_a) {
            return EnumActionResult.PASS;
        }
        ItemStack is = player.func_184586_b(hand);
        BlockPlanks.EnumType chosenPlankType = null;
        for (BlockPlanks.EnumType plankType : BlockPlanks.EnumType.values()) {
            if (chosenPlankType != null || MillCommonUtilities.countChestItems((IInventory)player.field_71071_by, Blocks.field_150344_f.func_176223_P().func_177226_a((IProperty)BlockPlanks.field_176383_a, (Comparable)plankType)) <= 0) continue;
            chosenPlankType = plankType;
        }
        if (chosenPlankType == null) {
            if (!world.field_72995_K) {
                ServerSender.sendTranslatedSentence(player, 'f', "ui.uluexplanations", new String[0]);
                ServerSender.sendTranslatedSentence(player, '6', "ui.ulunoplanks", new String[0]);
            }
            return EnumActionResult.PASS;
        }
        if (!is.func_77942_o()) {
            is.func_77982_d(new NBTTagCompound());
        }
        if ((resUseCount = is.func_77978_p().func_74762_e("resUseCount")) == 0) {
            if (MillCommonUtilities.countChestItems((IInventory)player.field_71071_by, COARSE_DIRT) == 0) {
                if (!world.field_72995_K) {
                    ServerSender.sendTranslatedSentence(player, '6', "ui.ulunodirt", new String[0]);
                }
                return EnumActionResult.PASS;
            }
            WorldUtilities.getItemsFromChest((IInventory)player.field_71071_by, COARSE_DIRT, 1);
            WorldUtilities.getItemsFromChest((IInventory)player.field_71071_by, Blocks.field_150344_f.func_176223_P().func_177226_a((IProperty)BlockPlanks.field_176383_a, (Comparable)chosenPlankType), 1);
            resUseCount = 3;
        } else {
            --resUseCount;
        }
        is.func_77978_p().func_74768_a("resUseCount", resUseCount);
        WorldUtilities.setBlockstate(world, new Point(pos), MillBlocks.SOD.func_176223_P().func_177226_a(BlockSod.VARIANT, (Comparable)chosenPlankType), true, true);
        is.func_77972_a(1, (EntityLivingBase)player);
        return EnumActionResult.SUCCESS;
    }

    @SideOnly(value=Side.CLIENT)
    public String func_77653_i(ItemStack stack) {
        if (stack.func_77978_p() != null) {
            int resUseCount = stack.func_77978_p().func_74762_e("resUseCount");
            return super.func_77653_i(stack) + " - " + LanguageUtilities.string("ui.ulusodplanksleft", "" + resUseCount);
        }
        return super.func_77653_i(stack);
    }

    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack uluIS = player.func_184586_b(hand);
        if (world.func_180495_p(pos).func_177230_c() == Blocks.field_150433_aE) {
            world.func_175656_a(pos, Blocks.field_150350_a.func_176223_P());
            MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Block)MillBlocks.SNOW_BRICK, 0, 4);
            uluIS.func_77972_a(1, (EntityLivingBase)player);
            return EnumActionResult.SUCCESS;
        }
        if (world.func_180495_p(pos).func_177230_c() == Blocks.field_150431_aC) {
            int snowDepth = (Integer)world.func_180495_p(pos).func_177229_b((IProperty)BlockSnow.field_176315_a);
            world.func_175656_a(pos, Blocks.field_150350_a.func_176223_P());
            MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, (Block)MillBlocks.SNOW_BRICK, 0, (snowDepth + 1) / 2);
            uluIS.func_77972_a(1, (EntityLivingBase)player);
            return EnumActionResult.SUCCESS;
        }
        if (world.func_180495_p(pos).func_177230_c() == Blocks.field_150432_aD) {
            MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, MillBlocks.ICE_BRICK, 0, 4);
            world.func_175656_a(pos, Blocks.field_150350_a.func_176223_P());
            uluIS.func_77972_a(1, (EntityLivingBase)player);
            return EnumActionResult.SUCCESS;
        }
        return this.attemptSodPlanks(player, world, pos, side, hand);
    }
}

