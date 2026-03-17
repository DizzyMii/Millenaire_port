/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockContainer
 *  net.minecraft.block.BlockHorizontal
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyDirection
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.Mirror
 *  net.minecraft.util.Rotation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.entity.TileEntityPanel;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class BlockPanel
extends BlockContainer {
    public static final PropertyDirection FACING = BlockHorizontal.field_185512_D;
    protected static final AxisAlignedBB SIGN_EAST_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 0.125, 1.0, 1.0);
    protected static final AxisAlignedBB SIGN_WEST_AABB = new AxisAlignedBB(0.875, 0.0, 0.0, 1.0, 1.0, 1.0);
    protected static final AxisAlignedBB SIGN_SOUTH_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.125);
    protected static final AxisAlignedBB SIGN_NORTH_AABB = new AxisAlignedBB(0.0, 0.0, 0.875, 1.0, 1.0, 1.0);

    public BlockPanel(String blockName) {
        super(Material.field_151575_d);
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a((IProperty)FACING, (Comparable)EnumFacing.NORTH));
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149711_c(1.0f);
    }

    public boolean func_176196_c(World worldIn, BlockPos pos) {
        return !this.func_181087_e(worldIn, pos) && super.func_176196_c(worldIn, pos);
    }

    public boolean func_181623_g() {
        return true;
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{FACING});
    }

    public TileEntity func_149915_a(World worldIn, int meta) {
        return new TileEntityPanel();
    }

    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityPanel();
    }

    public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch ((EnumFacing)state.func_177229_b((IProperty)FACING)) {
            default: {
                return SIGN_NORTH_AABB;
            }
            case SOUTH: {
                return SIGN_SOUTH_AABB;
            }
            case WEST: {
                return SIGN_WEST_AABB;
            }
            case EAST: 
        }
        return SIGN_EAST_AABB;
    }

    @Nullable
    public AxisAlignedBB func_180646_a(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return field_185506_k;
    }

    public ItemStack func_185473_a(World worldIn, BlockPos pos, IBlockState state) {
        return ItemStack.field_190927_a;
    }

    public Item func_180660_a(IBlockState state, Random rand, int fortune) {
        return null;
    }

    public int func_176201_c(IBlockState state) {
        return ((EnumFacing)state.func_177229_b((IProperty)FACING)).func_176745_a();
    }

    public IBlockState func_176203_a(int meta) {
        EnumFacing enumfacing = EnumFacing.func_82600_a((int)meta);
        if (enumfacing.func_176740_k() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        return this.func_176223_P().func_177226_a((IProperty)FACING, (Comparable)enumfacing);
    }

    @SideOnly(value=Side.CLIENT)
    public boolean func_190946_v(IBlockState state) {
        return true;
    }

    public boolean func_149686_d(IBlockState state) {
        return false;
    }

    public boolean func_149662_c(IBlockState state) {
        return false;
    }

    public boolean func_176205_b(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    public void func_189540_a(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        EnumFacing enumfacing = (EnumFacing)state.func_177229_b((IProperty)FACING);
        if (!worldIn.func_180495_p(pos.func_177972_a(enumfacing.func_176734_d())).func_185904_a().func_76220_a()) {
            this.func_176226_b(worldIn, pos, state, 0);
            worldIn.func_175698_g(pos);
        }
        super.func_189540_a(state, worldIn, pos, blockIn, fromPos);
    }

    public boolean func_180639_a(World world, BlockPos pos, IBlockState state, EntityPlayer entityplayer, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world.field_72995_K) {
            return true;
        }
        TileEntityPanel panel = (TileEntityPanel)world.func_175625_s(pos);
        if (panel == null || panel.panelType == 0) {
            return false;
        }
        Building building = Mill.getMillWorld(world).getBuilding(panel.buildingPos);
        if (building == null) {
            return false;
        }
        if (panel.panelType == 4 && building.controlledBy(entityplayer)) {
            ServerSender.displayControlledProjectGUI(entityplayer, building);
            return true;
        }
        if (panel.panelType == 13 && building.controlledBy(entityplayer)) {
            ServerSender.displayControlledMilitaryGUI(entityplayer, building);
            return true;
        }
        ServerSender.displayPanel(entityplayer, new Point(pos));
        return true;
    }

    public int func_149745_a(Random random) {
        return 0;
    }

    public IBlockState func_185471_a(IBlockState state, Mirror mirrorIn) {
        return state.func_185907_a(mirrorIn.func_185800_a((EnumFacing)state.func_177229_b((IProperty)FACING)));
    }

    public IBlockState func_185499_a(IBlockState state, Rotation rot) {
        return state.func_177226_a((IProperty)FACING, (Comparable)rot.func_185831_a((EnumFacing)state.func_177229_b((IProperty)FACING)));
    }
}

