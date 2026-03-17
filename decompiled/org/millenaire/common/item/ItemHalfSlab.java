/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.CriteriaTriggers
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.BlockSlab$EnumBlockHalf
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.BlockHalfSlab;

public class ItemHalfSlab
extends ItemBlock {
    private final BlockHalfSlab singleSlab;
    private final Block fullBlock;

    public ItemHalfSlab(BlockHalfSlab singleSlab) {
        super((Block)singleSlab);
        this.singleSlab = singleSlab;
        this.fullBlock = singleSlab.getBaseBlock();
        this.func_77656_e(0);
    }

    @SideOnly(value=Side.CLIENT)
    public boolean func_179222_a(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        IBlockState iblockstate1;
        BlockPos blockpos = pos;
        IBlockState iblockstate = worldIn.func_180495_p(pos);
        if (iblockstate.func_177230_c() == this.singleSlab) {
            boolean flag;
            boolean bl = flag = iblockstate.func_177229_b((IProperty)BlockSlab.field_176554_a) == BlockSlab.EnumBlockHalf.TOP;
            if (side == EnumFacing.UP && !flag || side == EnumFacing.DOWN && flag) {
                return true;
            }
        }
        return (iblockstate1 = worldIn.func_180495_p(pos = pos.func_177972_a(side))).func_177230_c() == this.singleSlab ? true : super.func_179222_a(worldIn, blockpos, side, player, stack);
    }

    public int func_77647_b(int damage) {
        return damage;
    }

    public String func_77667_c(ItemStack stack) {
        return this.singleSlab.func_149739_a();
    }

    public EnumActionResult func_180614_a(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack itemstack = player.func_184586_b(hand);
        if (!itemstack.func_190926_b() && player.func_175151_a(pos.func_177972_a(facing), facing, itemstack)) {
            IBlockState iblockstate = worldIn.func_180495_p(pos);
            if (iblockstate.func_177230_c() == this.singleSlab) {
                BlockSlab.EnumBlockHalf blockslab$enumblockhalf = (BlockSlab.EnumBlockHalf)iblockstate.func_177229_b((IProperty)BlockSlab.field_176554_a);
                if (facing == EnumFacing.UP && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.BOTTOM || facing == EnumFacing.DOWN && blockslab$enumblockhalf == BlockSlab.EnumBlockHalf.TOP) {
                    AxisAlignedBB axisalignedbb = this.fullBlock.func_176223_P().func_185890_d((IBlockAccess)worldIn, pos);
                    if (axisalignedbb != Block.field_185506_k && worldIn.func_72855_b(axisalignedbb.func_186670_a(pos)) && worldIn.func_180501_a(pos, this.fullBlock.func_176223_P(), 11)) {
                        SoundType soundtype = this.fullBlock.getSoundType(this.fullBlock.func_176223_P(), worldIn, pos, (Entity)player);
                        worldIn.func_184133_a(player, pos, soundtype.func_185841_e(), SoundCategory.BLOCKS, (soundtype.func_185843_a() + 1.0f) / 2.0f, soundtype.func_185847_b() * 0.8f);
                        itemstack.func_190918_g(1);
                        if (player instanceof EntityPlayerMP) {
                            CriteriaTriggers.field_193137_x.func_193173_a((EntityPlayerMP)player, pos, itemstack);
                        }
                    }
                    return EnumActionResult.SUCCESS;
                }
            }
            return this.tryPlace(player, itemstack, worldIn, pos.func_177972_a(facing)) ? EnumActionResult.SUCCESS : super.func_180614_a(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        }
        return EnumActionResult.FAIL;
    }

    private boolean tryPlace(EntityPlayer player, ItemStack stack, World worldIn, BlockPos pos) {
        IBlockState blockStateInPlace = worldIn.func_180495_p(pos);
        if (blockStateInPlace.func_177230_c() == this.singleSlab) {
            AxisAlignedBB axisalignedbb = this.fullBlock.func_176223_P().func_185890_d((IBlockAccess)worldIn, pos);
            if (axisalignedbb != Block.field_185506_k && worldIn.func_72855_b(axisalignedbb.func_186670_a(pos)) && worldIn.func_180501_a(pos, this.fullBlock.func_176223_P(), 11)) {
                SoundType soundtype = this.fullBlock.getSoundType(this.fullBlock.func_176223_P(), worldIn, pos, (Entity)player);
                worldIn.func_184133_a(player, pos, soundtype.func_185841_e(), SoundCategory.BLOCKS, (soundtype.func_185843_a() + 1.0f) / 2.0f, soundtype.func_185847_b() * 0.8f);
                stack.func_190918_g(1);
            }
            return true;
        }
        return false;
    }
}

