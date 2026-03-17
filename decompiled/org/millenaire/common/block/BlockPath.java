/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.MapColor
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.common.ForgeModContainer
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.BlockPathSlab;
import org.millenaire.common.block.IBlockPath;
import org.millenaire.common.block.MillBlocks;

public class BlockPath
extends Block
implements IBlockPath {
    protected static final AxisAlignedBB AABB_FULL = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);
    private final String singleSlabBlockName;
    private final String doubleSlabName;

    public BlockPath(String blockName, MapColor color, SoundType soundType) {
        super(Material.field_151578_c, color);
        this.func_149672_a(soundType);
        this.singleSlabBlockName = blockName + "_slab";
        this.doubleSlabName = blockName;
        this.func_149663_c("millenaire." + this.doubleSlabName);
        this.setRegistryName(this.doubleSlabName);
        this.field_149783_u = true;
        IBlockState iblockstate = this.field_176227_L.func_177621_b();
        iblockstate = iblockstate.func_177226_a((IProperty)STABLE, (Comparable)Boolean.valueOf(false));
        this.func_180632_j(iblockstate);
        this.setHarvestLevel("shovel", 0);
        this.func_149711_c(0.8f);
        this.func_149647_a(MillBlocks.tabMillenaire);
        this.field_149787_q = false;
        this.func_149713_g(255);
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{STABLE});
    }

    public int func_180651_a(IBlockState state) {
        return 0;
    }

    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        if (ForgeModContainer.disableStairSlabCulling) {
            return super.doesSideBlockRendering(state, world, pos, face);
        }
        return face == EnumFacing.DOWN;
    }

    public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    public BlockRenderLayer func_180664_k() {
        return BlockRenderLayer.CUTOUT;
    }

    public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB_FULL;
    }

    @Override
    public BlockPath getDoubleSlab() {
        return (BlockPath)Block.func_149684_b((String)("millenaire:" + this.doubleSlabName));
    }

    public ItemStack func_185473_a(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(Block.func_149684_b((String)("millenaire:" + this.doubleSlabName)), 1, 0);
    }

    public Item func_180660_a(IBlockState state, Random rand, int fortune) {
        return Item.func_150898_a((Block)Block.func_149684_b((String)("millenaire:" + this.doubleSlabName)));
    }

    public int func_176201_c(IBlockState state) {
        int i = 0;
        if (((Boolean)state.func_177229_b((IProperty)STABLE)).booleanValue()) {
            i |= 1;
        }
        return i;
    }

    @Override
    public BlockPathSlab getSingleSlab() {
        return (BlockPathSlab)Block.func_149684_b((String)("millenaire:" + this.singleSlabBlockName));
    }

    public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.func_176203_a(meta).func_177226_a((IProperty)STABLE, (Comparable)Boolean.valueOf(true));
    }

    public IBlockState func_176203_a(int meta) {
        IBlockState iblockstate = this.func_176223_P();
        if ((meta & 1) == 1) {
            iblockstate = iblockstate.func_177226_a((IProperty)STABLE, (Comparable)Boolean.valueOf(true));
        }
        return iblockstate;
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "stable=false"));
    }

    public boolean func_149686_d(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullPath() {
        return true;
    }

    public boolean func_149662_c(IBlockState state) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean func_176225_a(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        switch (side) {
            case UP: {
                return true;
            }
            case NORTH: 
            case SOUTH: 
            case WEST: 
            case EAST: {
                IBlockState iblockstate = blockAccess.func_180495_p(pos.func_177972_a(side));
                Block block = iblockstate.func_177230_c();
                return !iblockstate.func_185914_p() && block != Blocks.field_150458_ak && block != Blocks.field_185774_da && !(block instanceof BlockPath);
            }
        }
        return super.func_176225_a(blockState, blockAccess, pos, side);
    }
}

