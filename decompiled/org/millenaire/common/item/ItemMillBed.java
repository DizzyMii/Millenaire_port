/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.CriteriaTriggers
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBed
 *  net.minecraft.block.BlockBed$EnumPartType
 *  net.minecraft.block.BlockHorizontal
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package org.millenaire.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.millenaire.common.entity.TileEntityMillBed;

public class ItemMillBed
extends ItemBlock {
    public ItemMillBed(Block bed) {
        super(bed);
    }

    public void func_150895_a(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.func_194125_a(tab)) {
            items.add((Object)new ItemStack((Item)this, 1, 0));
        }
    }

    public String func_77667_c(ItemStack stack) {
        return super.func_77658_a();
    }

    public EnumActionResult func_180614_a(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.field_72995_K) {
            return EnumActionResult.SUCCESS;
        }
        if (facing != EnumFacing.UP) {
            return EnumActionResult.FAIL;
        }
        IBlockState iblockstate = worldIn.func_180495_p(pos);
        Block placedBlock = iblockstate.func_177230_c();
        boolean flag = placedBlock.func_176200_f((IBlockAccess)worldIn, pos);
        if (!flag) {
            pos = pos.func_177984_a();
        }
        int i = MathHelper.func_76128_c((double)((double)(player.field_70177_z * 4.0f / 360.0f) + 0.5)) & 3;
        EnumFacing enumfacing = EnumFacing.func_176731_b((int)i);
        BlockPos blockpos = pos.func_177972_a(enumfacing);
        ItemStack itemstack = player.func_184586_b(hand);
        if (player.func_175151_a(pos, facing, itemstack) && player.func_175151_a(blockpos, facing, itemstack)) {
            boolean flag3;
            IBlockState iblockstate1 = worldIn.func_180495_p(blockpos);
            boolean flag1 = iblockstate1.func_177230_c().func_176200_f((IBlockAccess)worldIn, blockpos);
            boolean flag2 = flag || worldIn.func_175623_d(pos);
            boolean bl = flag3 = flag1 || worldIn.func_175623_d(blockpos);
            if (flag2 && flag3 && worldIn.func_180495_p(pos.func_177977_b()).func_185896_q() && worldIn.func_180495_p(blockpos.func_177977_b()).func_185896_q()) {
                TileEntity tileentity1;
                IBlockState iblockstate2 = this.field_150939_a.func_176223_P().func_177226_a((IProperty)BlockBed.field_176471_b, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)BlockHorizontal.field_185512_D, (Comparable)enumfacing).func_177226_a((IProperty)BlockBed.field_176472_a, (Comparable)BlockBed.EnumPartType.FOOT);
                worldIn.func_180501_a(pos, iblockstate2, 10);
                worldIn.func_180501_a(blockpos, iblockstate2.func_177226_a((IProperty)BlockBed.field_176472_a, (Comparable)BlockBed.EnumPartType.HEAD), 10);
                SoundType soundtype = iblockstate2.func_177230_c().getSoundType(iblockstate2, worldIn, pos, (Entity)player);
                worldIn.func_184133_a((EntityPlayer)null, pos, soundtype.func_185841_e(), SoundCategory.BLOCKS, (soundtype.func_185843_a() + 1.0f) / 2.0f, soundtype.func_185847_b() * 0.8f);
                TileEntity tileentity = worldIn.func_175625_s(blockpos);
                if (tileentity instanceof TileEntityMillBed) {
                    ((TileEntityMillBed)tileentity).func_193051_a(itemstack);
                }
                if ((tileentity1 = worldIn.func_175625_s(pos)) instanceof TileEntityMillBed) {
                    ((TileEntityMillBed)tileentity1).func_193051_a(itemstack);
                }
                worldIn.func_175722_b(pos, placedBlock, false);
                worldIn.func_175722_b(blockpos, iblockstate1.func_177230_c(), false);
                if (player instanceof EntityPlayerMP) {
                    CriteriaTriggers.field_193137_x.func_193173_a((EntityPlayerMP)player, pos, itemstack);
                }
                itemstack.func_190918_g(1);
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.FAIL;
        }
        return EnumActionResult.FAIL;
    }
}

