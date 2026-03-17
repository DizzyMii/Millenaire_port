/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBed
 *  net.minecraft.block.BlockBed$EnumPartType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityBed
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumBlockRenderType
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.entity.TileEntityMillBed;

public class BlockMillBed
extends BlockBed {
    protected final AxisAlignedBB field_185513_c;
    private final int bedHeight;

    public BlockMillBed(String blockName, int height) {
        this.bedHeight = height;
        this.field_185513_c = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, (double)height / 16.0, 1.0);
        this.func_149647_a(MillBlocks.tabMillenaire);
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
    }

    public TileEntity func_149915_a(World worldIn, int meta) {
        return new TileEntityMillBed();
    }

    public void func_180653_a(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (state.func_177229_b((IProperty)field_176472_a) == BlockBed.EnumPartType.HEAD) {
            ItemStack itemstack = this.func_185473_a(worldIn, pos, state);
            BlockMillBed.func_180635_a((World)worldIn, (BlockPos)pos, (ItemStack)itemstack);
        }
    }

    public int getBedHeight() {
        return this.bedHeight;
    }

    public BlockRenderLayer func_180664_k() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
        return this.field_185513_c;
    }

    @Deprecated
    public ItemStack func_185473_a(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(Item.func_150898_a((Block)this), 1, this.func_180651_a(state));
    }

    public Item func_180660_a(IBlockState state, Random rand, int fortune) {
        return state.func_177229_b((IProperty)field_176472_a) == BlockBed.EnumPartType.FOOT ? Items.field_190931_a : Item.func_150898_a((Block)this);
    }

    public EnumBlockRenderType func_149645_b(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public void func_180657_a(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
        if (state.func_177229_b((IProperty)field_176472_a) == BlockBed.EnumPartType.HEAD && te instanceof TileEntityBed) {
            ItemStack itemstack = this.func_185473_a(worldIn, pos, state);
            BlockMillBed.func_180635_a((World)worldIn, (BlockPos)pos, (ItemStack)itemstack);
        } else {
            super.func_180657_a(worldIn, player, pos, state, (TileEntity)null, stack);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), ""));
    }

    public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity player) {
        return true;
    }
}

