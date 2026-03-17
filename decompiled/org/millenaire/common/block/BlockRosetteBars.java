/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBeacon
 *  net.minecraft.block.BlockHorizontal
 *  net.minecraft.block.BlockSlab$EnumBlockHalf
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.MapColor
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyDirection
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.Item
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.Mirror
 *  net.minecraft.util.Rotation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.BlockBars;
import org.millenaire.common.block.BlockMillWall;

public class BlockRosetteBars
extends BlockBars {
    public static final PropertyDirection FACING = BlockHorizontal.field_185512_D;
    static final PropertyEnum<BlockSlab.EnumBlockHalf> TOP_BOTTOM = PropertyEnum.func_177709_a((String)"topbottom", BlockSlab.EnumBlockHalf.class);

    public BlockRosetteBars(String blockName, Material material, SoundType soundType) {
        super(blockName);
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a((IProperty)field_176241_b, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)field_176242_M, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)field_176243_N, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)field_176244_O, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)FACING, (Comparable)EnumFacing.SOUTH).func_177226_a(TOP_BOTTOM, (Comparable)BlockSlab.EnumBlockHalf.TOP));
    }

    public void func_180663_b(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.field_72995_K) {
            BlockBeacon.func_176450_d((World)worldIn, (BlockPos)pos);
        }
    }

    @Override
    public boolean canPaneConnectTo(IBlockAccess world, BlockPos pos, EnumFacing dir) {
        BlockPos other = pos.func_177972_a(dir);
        IBlockState state = world.func_180495_p(other);
        return state.func_177230_c().canBeConnectedTo(world, other, dir.func_176734_d()) || this.func_193393_b(world, state, other, dir.func_176734_d()) || state.func_177230_c() instanceof BlockMillWall;
    }

    protected boolean func_149700_E() {
        return false;
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{field_176241_b, field_176242_M, field_176244_O, field_176243_N, FACING, TOP_BOTTOM});
    }

    public int func_180651_a(IBlockState state) {
        return this.func_176201_c(this.func_176223_P());
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer func_180664_k() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    public MapColor func_180659_g(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.field_151670_w;
    }

    public int func_176201_c(IBlockState state) {
        int i = 0;
        if (state.func_177229_b(TOP_BOTTOM) == BlockSlab.EnumBlockHalf.BOTTOM) {
            i |= 4;
        }
        return i |= ((EnumFacing)state.func_177229_b((IProperty)FACING)).func_176736_b();
    }

    public IBlockState func_180642_a(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState iBlockStateAbove = world.func_180495_p(pos.func_177982_a(0, 1, 0));
        if (iBlockStateAbove.func_177230_c() == this && iBlockStateAbove.func_177229_b(TOP_BOTTOM) == BlockSlab.EnumBlockHalf.TOP) {
            return this.func_176223_P().func_177226_a(TOP_BOTTOM, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM).func_177226_a((IProperty)FACING, iBlockStateAbove.func_177229_b((IProperty)FACING));
        }
        IBlockState iBlockStateWest = world.func_180495_p(pos.func_177982_a(-1, 0, 0));
        if (iBlockStateWest.func_177230_c() == this && iBlockStateWest.func_177229_b((IProperty)FACING) == EnumFacing.WEST) {
            return this.func_176223_P().func_177226_a((IProperty)FACING, (Comparable)EnumFacing.EAST).func_177226_a(TOP_BOTTOM, iBlockStateWest.func_177229_b(TOP_BOTTOM));
        }
        IBlockState iBlockStateSouth = world.func_180495_p(pos.func_177982_a(0, 0, 1));
        if (iBlockStateSouth.func_177230_c() == this && iBlockStateSouth.func_177229_b((IProperty)FACING) == EnumFacing.SOUTH) {
            return this.func_176223_P().func_177226_a((IProperty)FACING, (Comparable)EnumFacing.NORTH).func_177226_a(TOP_BOTTOM, iBlockStateSouth.func_177229_b(TOP_BOTTOM));
        }
        IBlockState iBlockStateBelow = world.func_180495_p(pos.func_177982_a(0, -1, 0));
        if (iBlockStateBelow.func_177230_c() == this && iBlockStateBelow.func_177229_b(TOP_BOTTOM) == BlockSlab.EnumBlockHalf.BOTTOM) {
            return this.func_176223_P().func_177226_a(TOP_BOTTOM, (Comparable)BlockSlab.EnumBlockHalf.TOP).func_177226_a((IProperty)FACING, iBlockStateBelow.func_177229_b((IProperty)FACING));
        }
        IBlockState iBlockStateEast = world.func_180495_p(pos.func_177982_a(1, 0, 0));
        if (iBlockStateEast.func_177230_c() == this && iBlockStateEast.func_177229_b((IProperty)FACING) == EnumFacing.EAST) {
            return this.func_176223_P().func_177226_a((IProperty)FACING, (Comparable)EnumFacing.WEST).func_177226_a(TOP_BOTTOM, iBlockStateEast.func_177229_b(TOP_BOTTOM));
        }
        IBlockState iBlockStateNorth = world.func_180495_p(pos.func_177982_a(0, 0, -1));
        if (iBlockStateNorth.func_177230_c() == this && iBlockStateNorth.func_177229_b((IProperty)FACING) == EnumFacing.NORTH) {
            return this.func_176223_P().func_177226_a((IProperty)FACING, (Comparable)EnumFacing.SOUTH).func_177226_a(TOP_BOTTOM, iBlockStateNorth.func_177229_b(TOP_BOTTOM));
        }
        IBlockState basicState = this.func_176223_P();
        if (!iBlockStateAbove.func_185913_b() && iBlockStateBelow.func_185913_b()) {
            basicState = basicState.func_177226_a(TOP_BOTTOM, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM);
        }
        if (!iBlockStateWest.func_185913_b() && iBlockStateEast.func_185913_b()) {
            basicState = basicState.func_177226_a((IProperty)FACING, (Comparable)EnumFacing.EAST);
        } else if (!iBlockStateSouth.func_185913_b() && iBlockStateNorth.func_185913_b()) {
            basicState = basicState.func_177226_a((IProperty)FACING, (Comparable)EnumFacing.NORTH);
        } else if (iBlockStateSouth.func_185913_b() && !iBlockStateNorth.func_185913_b()) {
            basicState = basicState.func_177226_a((IProperty)FACING, (Comparable)EnumFacing.SOUTH);
        } else if (iBlockStateWest.func_185913_b() && !iBlockStateEast.func_185913_b()) {
            basicState = basicState.func_177226_a((IProperty)FACING, (Comparable)EnumFacing.WEST);
        }
        return basicState;
    }

    public IBlockState func_176203_a(int meta) {
        EnumFacing enumfacing;
        IBlockState iblockstate = this.func_176223_P();
        if ((meta & 4) == 4) {
            iblockstate = iblockstate.func_177226_a(TOP_BOTTOM, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM);
        }
        if ((enumfacing = EnumFacing.func_176731_b((int)(meta & 3))).func_176740_k() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        iblockstate = iblockstate.func_177226_a((IProperty)FACING, (Comparable)enumfacing);
        return iblockstate;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "variant=inventory"));
    }

    public void func_176213_c(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.field_72995_K) {
            BlockBeacon.func_176450_d((World)worldIn, (BlockPos)pos);
        }
    }

    public IBlockState func_185471_a(IBlockState state, Mirror mirrorIn) {
        switch (mirrorIn) {
            case LEFT_RIGHT: {
                return state.func_177226_a((IProperty)field_176241_b, state.func_177229_b((IProperty)field_176243_N)).func_177226_a((IProperty)field_176243_N, state.func_177229_b((IProperty)field_176241_b));
            }
            case FRONT_BACK: {
                return state.func_177226_a((IProperty)field_176242_M, state.func_177229_b((IProperty)field_176244_O)).func_177226_a((IProperty)field_176244_O, state.func_177229_b((IProperty)field_176242_M));
            }
        }
        return super.func_185471_a(state, mirrorIn);
    }

    public IBlockState func_185499_a(IBlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180: {
                return state.func_177226_a((IProperty)field_176241_b, state.func_177229_b((IProperty)field_176243_N)).func_177226_a((IProperty)field_176242_M, state.func_177229_b((IProperty)field_176244_O)).func_177226_a((IProperty)field_176243_N, state.func_177229_b((IProperty)field_176241_b)).func_177226_a((IProperty)field_176244_O, state.func_177229_b((IProperty)field_176242_M));
            }
            case COUNTERCLOCKWISE_90: {
                return state.func_177226_a((IProperty)field_176241_b, state.func_177229_b((IProperty)field_176242_M)).func_177226_a((IProperty)field_176242_M, state.func_177229_b((IProperty)field_176243_N)).func_177226_a((IProperty)field_176243_N, state.func_177229_b((IProperty)field_176244_O)).func_177226_a((IProperty)field_176244_O, state.func_177229_b((IProperty)field_176241_b));
            }
            case CLOCKWISE_90: {
                return state.func_177226_a((IProperty)field_176241_b, state.func_177229_b((IProperty)field_176244_O)).func_177226_a((IProperty)field_176242_M, state.func_177229_b((IProperty)field_176241_b)).func_177226_a((IProperty)field_176243_N, state.func_177229_b((IProperty)field_176242_M)).func_177226_a((IProperty)field_176244_O, state.func_177229_b((IProperty)field_176243_N));
            }
        }
        return state;
    }
}

