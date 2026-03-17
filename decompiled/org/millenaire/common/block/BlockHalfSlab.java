/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSlab$EnumBlockHalf
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
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

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;

public abstract class BlockHalfSlab
extends Block {
    public static final PropertyEnum<BlockSlab.EnumBlockHalf> HALF = PropertyEnum.func_177709_a((String)"half", BlockSlab.EnumBlockHalf.class);
    protected static final AxisAlignedBB AABB_BOTTOM_HALF = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
    protected static final AxisAlignedBB AABB_TOP_HALF = new AxisAlignedBB(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);
    private final Block baseBlock;

    public BlockHalfSlab(Block fullBlock) {
        super(fullBlock.func_176223_P().func_185904_a(), fullBlock.func_176223_P().func_185909_g(null, null));
        this.func_149647_a(MillBlocks.tabMillenaire);
        this.baseBlock = fullBlock;
        this.field_149787_q = false;
        this.func_149713_g(255);
        this.field_149783_u = true;
    }

    protected boolean func_149700_E() {
        return false;
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{HALF});
    }

    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        if (ForgeModContainer.disableStairSlabCulling) {
            return super.doesSideBlockRendering(state, world, pos, face);
        }
        if (state.func_185914_p()) {
            return true;
        }
        BlockSlab.EnumBlockHalf side = (BlockSlab.EnumBlockHalf)state.func_177229_b(HALF);
        return side == BlockSlab.EnumBlockHalf.TOP && face == EnumFacing.UP || side == BlockSlab.EnumBlockHalf.BOTTOM && face == EnumFacing.DOWN;
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        if (face == EnumFacing.UP && state.func_177229_b(HALF) == BlockSlab.EnumBlockHalf.TOP) {
            return BlockFaceShape.SOLID;
        }
        return face == EnumFacing.DOWN && state.func_177229_b(HALF) == BlockSlab.EnumBlockHalf.BOTTOM ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.func_177229_b(HALF) == BlockSlab.EnumBlockHalf.TOP ? AABB_TOP_HALF : AABB_BOTTOM_HALF;
    }

    public int func_176201_c(IBlockState state) {
        int i = 0;
        if (state.func_177229_b(HALF) == BlockSlab.EnumBlockHalf.TOP) {
            i |= 8;
        }
        return i;
    }

    public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState iblockstate = super.func_180642_a(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).func_177226_a(HALF, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM);
        return facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double)hitY <= 0.5) ? iblockstate : iblockstate.func_177226_a(HALF, (Comparable)BlockSlab.EnumBlockHalf.TOP);
    }

    public IBlockState func_176203_a(int meta) {
        IBlockState iblockstate = this.func_176223_P();
        iblockstate = iblockstate.func_177226_a(HALF, (Comparable)((meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP));
        return iblockstate;
    }

    public String getUnlocalizedName(int meta) {
        return super.func_149739_a();
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "half=bottom"));
    }

    public boolean func_149686_d(IBlockState state) {
        return false;
    }

    public boolean func_149662_c(IBlockState state) {
        return false;
    }

    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        IBlockState state = this.func_176221_a(base_state, world, pos);
        return state.func_177229_b(HALF) == BlockSlab.EnumBlockHalf.TOP && side == EnumFacing.UP || state.func_177229_b(HALF) == BlockSlab.EnumBlockHalf.BOTTOM && side == EnumFacing.DOWN;
    }

    public boolean func_185481_k(IBlockState state) {
        return state.func_177229_b(HALF) == BlockSlab.EnumBlockHalf.TOP;
    }

    public boolean func_180639_a(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return this.baseBlock.func_180639_a(worldIn, pos, this.baseBlock.func_176223_P(), playerIn, hand, EnumFacing.DOWN, 0.0f, 0.0f, 0.0f);
    }
}

