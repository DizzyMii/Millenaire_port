/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockFenceGate
 *  net.minecraft.block.BlockPane
 *  net.minecraft.block.BlockWall
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.pathfinding.PathNodeType
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockWall;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;

public class BlockMillWall
extends Block {
    public static final PropertyBool UP = PropertyBool.func_177716_a((String)"up");
    public static final PropertyBool NORTH = PropertyBool.func_177716_a((String)"north");
    public static final PropertyBool EAST = PropertyBool.func_177716_a((String)"east");
    public static final PropertyBool SOUTH = PropertyBool.func_177716_a((String)"south");
    public static final PropertyBool WEST = PropertyBool.func_177716_a((String)"west");
    protected static final AxisAlignedBB[] AABB_BY_INDEX = new AxisAlignedBB[]{new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.0, 0.75), new AxisAlignedBB(0.25, 0.0, 0.25, 0.75, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.25, 0.75, 1.0, 0.75), new AxisAlignedBB(0.0, 0.0, 0.25, 0.75, 1.0, 1.0), new AxisAlignedBB(0.25, 0.0, 0.0, 0.75, 1.0, 0.75), new AxisAlignedBB(0.3125, 0.0, 0.0, 0.6875, 0.875, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 0.75, 1.0, 0.75), new AxisAlignedBB(0.0, 0.0, 0.0, 0.75, 1.0, 1.0), new AxisAlignedBB(0.25, 0.0, 0.25, 1.0, 1.0, 0.75), new AxisAlignedBB(0.25, 0.0, 0.25, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.3125, 1.0, 0.875, 0.6875), new AxisAlignedBB(0.0, 0.0, 0.25, 1.0, 1.0, 1.0), new AxisAlignedBB(0.25, 0.0, 0.0, 1.0, 1.0, 0.75), new AxisAlignedBB(0.25, 0.0, 0.0, 1.0, 1.0, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.75), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)};
    protected static final AxisAlignedBB[] CLIP_AABB_BY_INDEX = new AxisAlignedBB[]{AABB_BY_INDEX[0].func_186666_e(1.5), AABB_BY_INDEX[1].func_186666_e(1.5), AABB_BY_INDEX[2].func_186666_e(1.5), AABB_BY_INDEX[3].func_186666_e(1.5), AABB_BY_INDEX[4].func_186666_e(1.5), AABB_BY_INDEX[5].func_186666_e(1.5), AABB_BY_INDEX[6].func_186666_e(1.5), AABB_BY_INDEX[7].func_186666_e(1.5), AABB_BY_INDEX[8].func_186666_e(1.5), AABB_BY_INDEX[9].func_186666_e(1.5), AABB_BY_INDEX[10].func_186666_e(1.5), AABB_BY_INDEX[11].func_186666_e(1.5), AABB_BY_INDEX[12].func_186666_e(1.5), AABB_BY_INDEX[13].func_186666_e(1.5), AABB_BY_INDEX[14].func_186666_e(1.5), AABB_BY_INDEX[15].func_186666_e(1.5)};
    private final Block baseBlock;

    private static int getAABBIndex(IBlockState state) {
        int i = 0;
        if (((Boolean)state.func_177229_b((IProperty)NORTH)).booleanValue()) {
            i |= 1 << EnumFacing.NORTH.func_176736_b();
        }
        if (((Boolean)state.func_177229_b((IProperty)EAST)).booleanValue()) {
            i |= 1 << EnumFacing.EAST.func_176736_b();
        }
        if (((Boolean)state.func_177229_b((IProperty)SOUTH)).booleanValue()) {
            i |= 1 << EnumFacing.SOUTH.func_176736_b();
        }
        if (((Boolean)state.func_177229_b((IProperty)WEST)).booleanValue()) {
            i |= 1 << EnumFacing.WEST.func_176736_b();
        }
        return i;
    }

    protected static boolean isExcepBlockForAttachWithPiston(Block p_194143_0_) {
        return Block.func_193382_c((Block)p_194143_0_) || p_194143_0_ == Blocks.field_180401_cv || p_194143_0_ == Blocks.field_150440_ba || p_194143_0_ == Blocks.field_150423_aK || p_194143_0_ == Blocks.field_150428_aP;
    }

    public BlockMillWall(String blockName, Block baseBlock) {
        super(baseBlock.func_149688_o(null));
        this.baseBlock = baseBlock;
        this.func_149647_a(MillBlocks.tabMillenaire);
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a((IProperty)UP, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)NORTH, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)EAST, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)SOUTH, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)WEST, (Comparable)Boolean.valueOf(false)));
        this.func_149711_c(baseBlock.func_176195_g(null, null, null));
        this.func_149752_b(baseBlock.func_149638_a(null) * 5.0f / 3.0f);
        this.func_149672_a(baseBlock.func_185467_w());
    }

    public void func_185477_a(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        if (!isActualState) {
            state = this.func_176221_a(state, (IBlockAccess)worldIn, pos);
        }
        BlockMillWall.func_185492_a((BlockPos)pos, (AxisAlignedBB)entityBox, collidingBoxes, (AxisAlignedBB)CLIP_AABB_BY_INDEX[BlockMillWall.getAABBIndex(state)]);
    }

    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        Block connector = world.func_180495_p(pos.func_177972_a(facing)).func_177230_c();
        return connector instanceof BlockWall || connector instanceof BlockFenceGate || connector instanceof BlockMillWall;
    }

    private boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing p_176253_3_) {
        IBlockState iblockstate = worldIn.func_180495_p(pos);
        Block block = iblockstate.func_177230_c();
        BlockFaceShape blockfaceshape = iblockstate.func_193401_d(worldIn, pos, p_176253_3_);
        boolean flag = blockfaceshape == BlockFaceShape.MIDDLE_POLE_THICK || blockfaceshape == BlockFaceShape.MIDDLE_POLE && block instanceof BlockFenceGate;
        return !BlockMillWall.isExcepBlockForAttachWithPiston(block) && blockfaceshape == BlockFaceShape.SOLID || flag || block instanceof BlockPane;
    }

    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    private boolean canWallConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
        BlockPos other = pos.func_177972_a(facing);
        Block block = world.func_180495_p(other).func_177230_c();
        return block.canBeConnectedTo(world, other, facing.func_176734_d()) || this.canConnectTo(world, other, facing.func_176734_d());
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{UP, NORTH, EAST, WEST, SOUTH});
    }

    public IBlockState func_176221_a(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        boolean flag = this.canWallConnectTo(worldIn, pos, EnumFacing.NORTH);
        boolean flag1 = this.canWallConnectTo(worldIn, pos, EnumFacing.EAST);
        boolean flag2 = this.canWallConnectTo(worldIn, pos, EnumFacing.SOUTH);
        boolean flag3 = this.canWallConnectTo(worldIn, pos, EnumFacing.WEST);
        boolean flag4 = flag && !flag1 && flag2 && !flag3 || !flag && flag1 && !flag2 && flag3;
        return state.func_177226_a((IProperty)UP, (Comparable)Boolean.valueOf(!flag4 || !worldIn.func_175623_d(pos.func_177984_a()))).func_177226_a((IProperty)NORTH, (Comparable)Boolean.valueOf(flag)).func_177226_a((IProperty)EAST, (Comparable)Boolean.valueOf(flag1)).func_177226_a((IProperty)SOUTH, (Comparable)Boolean.valueOf(flag2)).func_177226_a((IProperty)WEST, (Comparable)Boolean.valueOf(flag3));
    }

    public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos) {
        return PathNodeType.FENCE;
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THICK : BlockFaceShape.CENTER_BIG;
    }

    public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
        state = this.func_176221_a(state, source, pos);
        return AABB_BY_INDEX[BlockMillWall.getAABBIndex(state)];
    }

    @Nullable
    public AxisAlignedBB func_180646_a(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        blockState = this.func_176221_a(blockState, worldIn, pos);
        return CLIP_AABB_BY_INDEX[BlockMillWall.getAABBIndex(blockState)];
    }

    public int func_176201_c(IBlockState state) {
        return 0;
    }

    public IBlockState func_176203_a(int meta) {
        return this.func_176223_P();
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), ""));
    }

    public boolean func_149686_d(IBlockState state) {
        return false;
    }

    public boolean func_149662_c(IBlockState state) {
        return false;
    }

    public boolean func_176205_b(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

    public boolean func_180639_a(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return this.baseBlock.func_180639_a(worldIn, pos, this.baseBlock.func_176223_P(), playerIn, hand, EnumFacing.DOWN, 0.0f, 0.0f, 0.0f);
    }

    @SideOnly(value=Side.CLIENT)
    public boolean func_176225_a(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.DOWN ? super.func_176225_a(blockState, blockAccess, pos, side) : true;
    }
}

