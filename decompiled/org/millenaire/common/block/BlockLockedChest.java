/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockContainer
 *  net.minecraft.block.BlockHorizontal
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyDirection
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.passive.EntityOcelot
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.ISidedInventory
 *  net.minecraft.inventory.InventoryLargeChest
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumBlockRenderType
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.EnumFacing$Plane
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.Mirror
 *  net.minecraft.util.Rotation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.ILockableContainer
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.entity.TileEntityLockedChest;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.ui.ContainerLockedChest;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

public class BlockLockedChest
extends BlockContainer {
    public static final PropertyDirection FACING = BlockHorizontal.field_185512_D;
    protected static final AxisAlignedBB NORTH_CHEST_AABB = new AxisAlignedBB(0.0625, 0.0, 0.0, 0.9375, 0.875, 0.9375);
    protected static final AxisAlignedBB SOUTH_CHEST_AABB = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 1.0);
    protected static final AxisAlignedBB WEST_CHEST_AABB = new AxisAlignedBB(0.0, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
    protected static final AxisAlignedBB EAST_CHEST_AABB = new AxisAlignedBB(0.0625, 0.0, 0.0625, 1.0, 0.875, 0.9375);
    protected static final AxisAlignedBB NOT_CONNECTED_AABB = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);

    public static ContainerLockedChest createContainer(World world, int i, int j, int k, EntityPlayer entityplayer) {
        TileEntityLockedChest lockedchest = (TileEntityLockedChest)world.func_175625_s(new BlockPos(i, j, k));
        IInventory chest = BlockLockedChest.getInventory(lockedchest, world, i, j, k);
        Building building = Mill.getMillWorld(world).getBuilding(lockedchest.buildingPos);
        return new ContainerLockedChest((IInventory)entityplayer.field_71071_by, chest, entityplayer, building, lockedchest.isLockedFor(entityplayer));
    }

    public static IInventory getInventory(TileEntityLockedChest lockedchest, World world, int i, int j, int k) {
        String largename = lockedchest.getInvLargeName();
        TileEntityLockedChest chest = lockedchest;
        Block block = world.func_180495_p(new BlockPos(i, j, k)).func_177230_c();
        if (world.func_180495_p(new BlockPos(i - 1, j, k)).func_177230_c() == block) {
            chest = new InventoryLargeChest(largename, (ILockableContainer)((TileEntityLockedChest)world.func_175625_s(new BlockPos(i - 1, j, k))), (ILockableContainer)chest);
        }
        if (world.func_180495_p(new BlockPos(i + 1, j, k)).func_177230_c() == block) {
            chest = new InventoryLargeChest(largename, (ILockableContainer)chest, (ILockableContainer)((TileEntityLockedChest)world.func_175625_s(new BlockPos(i + 1, j, k))));
        }
        if (world.func_180495_p(new BlockPos(i, j, k - 1)).func_177230_c() == block) {
            chest = new InventoryLargeChest(largename, (ILockableContainer)((TileEntityLockedChest)world.func_175625_s(new BlockPos(i, j, k - 1))), (ILockableContainer)chest);
        }
        if (world.func_180495_p(new BlockPos(i, j, k + 1)).func_177230_c() == block) {
            chest = new InventoryLargeChest(largename, (ILockableContainer)chest, (ILockableContainer)((TileEntityLockedChest)world.func_175625_s(new BlockPos(i, j, k + 1))));
        }
        return chest;
    }

    public BlockLockedChest(String blockName) {
        super(Material.field_151575_d);
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a((IProperty)FACING, (Comparable)EnumFacing.NORTH));
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.setHarvestLevel("axe", 0);
        this.func_149711_c(50.0f);
        this.func_149752_b(2000.0f);
        this.func_149672_a(SoundType.field_185848_a);
    }

    public boolean func_176196_c(World worldIn, BlockPos pos) {
        int i = 0;
        BlockPos blockpos = pos.func_177976_e();
        BlockPos blockpos1 = pos.func_177974_f();
        BlockPos blockpos2 = pos.func_177978_c();
        BlockPos blockpos3 = pos.func_177968_d();
        if (worldIn.func_180495_p(blockpos).func_177230_c() == this) {
            if (this.isDoubleChest(worldIn, blockpos)) {
                return false;
            }
            ++i;
        }
        if (worldIn.func_180495_p(blockpos1).func_177230_c() == this) {
            if (this.isDoubleChest(worldIn, blockpos1)) {
                return false;
            }
            ++i;
        }
        if (worldIn.func_180495_p(blockpos2).func_177230_c() == this) {
            if (this.isDoubleChest(worldIn, blockpos2)) {
                return false;
            }
            ++i;
        }
        if (worldIn.func_180495_p(blockpos3).func_177230_c() == this) {
            if (this.isDoubleChest(worldIn, blockpos3)) {
                return false;
            }
            ++i;
        }
        return i <= 1;
    }

    public boolean func_149744_f(IBlockState state) {
        return false;
    }

    public IBlockState checkForSurroundingChests(World worldIn, BlockPos pos, IBlockState state) {
        if (worldIn.field_72995_K) {
            return state;
        }
        IBlockState iblockstate = worldIn.func_180495_p(pos.func_177978_c());
        IBlockState iblockstate1 = worldIn.func_180495_p(pos.func_177968_d());
        IBlockState iblockstate2 = worldIn.func_180495_p(pos.func_177976_e());
        IBlockState iblockstate3 = worldIn.func_180495_p(pos.func_177974_f());
        EnumFacing enumfacing = (EnumFacing)state.func_177229_b((IProperty)FACING);
        if (iblockstate.func_177230_c() != this && iblockstate1.func_177230_c() != this) {
            boolean flag = iblockstate.func_185913_b();
            boolean flag1 = iblockstate1.func_185913_b();
            if (iblockstate2.func_177230_c() == this || iblockstate3.func_177230_c() == this) {
                BlockPos blockpos1 = iblockstate2.func_177230_c() == this ? pos.func_177976_e() : pos.func_177974_f();
                IBlockState iblockstate7 = worldIn.func_180495_p(blockpos1.func_177978_c());
                IBlockState iblockstate6 = worldIn.func_180495_p(blockpos1.func_177968_d());
                enumfacing = EnumFacing.SOUTH;
                EnumFacing enumfacing2 = iblockstate2.func_177230_c() == this ? (EnumFacing)iblockstate2.func_177229_b((IProperty)FACING) : (EnumFacing)iblockstate3.func_177229_b((IProperty)FACING);
                if (enumfacing2 == EnumFacing.NORTH) {
                    enumfacing = EnumFacing.NORTH;
                }
                if ((flag || iblockstate7.func_185913_b()) && !flag1 && !iblockstate6.func_185913_b()) {
                    enumfacing = EnumFacing.SOUTH;
                }
                if ((flag1 || iblockstate6.func_185913_b()) && !flag && !iblockstate7.func_185913_b()) {
                    enumfacing = EnumFacing.NORTH;
                }
            }
        } else {
            BlockPos blockpos = iblockstate.func_177230_c() == this ? pos.func_177978_c() : pos.func_177968_d();
            IBlockState iblockstate4 = worldIn.func_180495_p(blockpos.func_177976_e());
            IBlockState iblockstate5 = worldIn.func_180495_p(blockpos.func_177974_f());
            enumfacing = EnumFacing.EAST;
            EnumFacing enumfacing1 = iblockstate.func_177230_c() == this ? (EnumFacing)iblockstate.func_177229_b((IProperty)FACING) : (EnumFacing)iblockstate1.func_177229_b((IProperty)FACING);
            if (enumfacing1 == EnumFacing.WEST) {
                enumfacing = EnumFacing.WEST;
            }
            if ((iblockstate2.func_185913_b() || iblockstate4.func_185913_b()) && !iblockstate3.func_185913_b() && !iblockstate5.func_185913_b()) {
                enumfacing = EnumFacing.EAST;
            }
            if ((iblockstate3.func_185913_b() || iblockstate5.func_185913_b()) && !iblockstate2.func_185913_b() && !iblockstate4.func_185913_b()) {
                enumfacing = EnumFacing.WEST;
            }
        }
        state = state.func_177226_a((IProperty)FACING, (Comparable)enumfacing);
        worldIn.func_180501_a(pos, state, 3);
        return state;
    }

    public IBlockState correctFacing(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing enumfacing = null;
        for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
            IBlockState iblockstate = worldIn.func_180495_p(pos.func_177972_a(enumfacing1));
            if (iblockstate.func_177230_c() == this) {
                return state;
            }
            if (!iblockstate.func_185913_b()) continue;
            if (enumfacing != null) {
                enumfacing = null;
                break;
            }
            enumfacing = enumfacing1;
        }
        if (enumfacing != null) {
            return state.func_177226_a((IProperty)FACING, (Comparable)enumfacing.func_176734_d());
        }
        EnumFacing enumfacing2 = (EnumFacing)state.func_177229_b((IProperty)FACING);
        if (worldIn.func_180495_p(pos.func_177972_a(enumfacing2)).func_185913_b()) {
            enumfacing2 = enumfacing2.func_176734_d();
        }
        if (worldIn.func_180495_p(pos.func_177972_a(enumfacing2)).func_185913_b()) {
            enumfacing2 = enumfacing2.func_176746_e();
        }
        if (worldIn.func_180495_p(pos.func_177972_a(enumfacing2)).func_185913_b()) {
            enumfacing2 = enumfacing2.func_176734_d();
        }
        return state.func_177226_a((IProperty)FACING, (Comparable)enumfacing2);
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{FACING});
    }

    public TileEntity func_149915_a(World world, int p_149915_2_) {
        return new TileEntityLockedChest();
    }

    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityLockedChest();
    }

    public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (source.func_180495_p(pos.func_177978_c()).func_177230_c() == this) {
            return NORTH_CHEST_AABB;
        }
        if (source.func_180495_p(pos.func_177968_d()).func_177230_c() == this) {
            return SOUTH_CHEST_AABB;
        }
        if (source.func_180495_p(pos.func_177976_e()).func_177230_c() == this) {
            return WEST_CHEST_AABB;
        }
        return source.func_180495_p(pos.func_177974_f()).func_177230_c() == this ? EAST_CHEST_AABB : NOT_CONNECTED_AABB;
    }

    public int func_180641_l(IBlockState blockState, World worldIn, BlockPos pos) {
        return Container.func_94526_b((IInventory)this.getLockableContainer(worldIn, pos));
    }

    public ILockableContainer getContainer(World worldIn, BlockPos pos, boolean allowBlocking) {
        TileEntity tileentity = worldIn.func_175625_s(pos);
        if (!(tileentity instanceof TileEntityLockedChest)) {
            return null;
        }
        ISidedInventory millChest = (TileEntityLockedChest)tileentity;
        if (this.isBlocked(worldIn, pos)) {
            return null;
        }
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockpos1 = pos.func_177972_a(enumfacing);
            Block block = worldIn.func_180495_p(blockpos1).func_177230_c();
            if (block != this) continue;
            if (this.isBlocked(worldIn, blockpos1)) {
                return null;
            }
            TileEntity tileentity1 = worldIn.func_175625_s(blockpos1);
            if (!(tileentity1 instanceof TileEntityLockedChest)) continue;
            if (enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH) {
                millChest = new TileEntityLockedChest.InventoryLockedLargeChest("container.chestDouble", (TileEntityLockedChest)millChest, (TileEntityLockedChest)tileentity1);
                continue;
            }
            millChest = new TileEntityLockedChest.InventoryLockedLargeChest("container.chestDouble", (TileEntityLockedChest)tileentity1, (TileEntityLockedChest)millChest);
        }
        return millChest;
    }

    @Nullable
    public ILockableContainer getLockableContainer(World worldIn, BlockPos pos) {
        return this.getContainer(worldIn, pos, false);
    }

    public int func_176201_c(IBlockState state) {
        return ((EnumFacing)state.func_177229_b((IProperty)FACING)).func_176745_a();
    }

    public EnumBlockRenderType func_149645_b(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.func_176223_P().func_177226_a((IProperty)FACING, (Comparable)placer.func_174811_aO());
    }

    public IBlockState func_176203_a(int meta) {
        EnumFacing enumfacing = EnumFacing.func_82600_a((int)meta);
        if (enumfacing.func_176740_k() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        return this.func_176223_P().func_177226_a((IProperty)FACING, (Comparable)enumfacing);
    }

    public int func_176211_b(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.UP ? blockState.func_185911_a(blockAccess, pos, side) : 0;
    }

    public int func_180656_a(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        if (!blockState.func_185897_m()) {
            return 0;
        }
        int i = 0;
        TileEntity tileentity = blockAccess.func_175625_s(pos);
        if (tileentity instanceof TileEntityLockedChest) {
            i = ((TileEntityLockedChest)tileentity).numPlayersUsing;
        }
        return MathHelper.func_76125_a((int)i, (int)0, (int)15);
    }

    public boolean func_149740_M(IBlockState state) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean func_190946_v(IBlockState state) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), ""));
    }

    private boolean isBelowSolidBlock(World worldIn, BlockPos pos) {
        return worldIn.func_180495_p(pos.func_177984_a()).doesSideBlockChestOpening((IBlockAccess)worldIn, pos.func_177984_a(), EnumFacing.DOWN);
    }

    private boolean isBlocked(World worldIn, BlockPos pos) {
        return this.isBelowSolidBlock(worldIn, pos) || this.isOcelotSittingOnChest(worldIn, pos);
    }

    private boolean isDoubleChest(World worldIn, BlockPos pos) {
        if (worldIn.func_180495_p(pos).func_177230_c() != this) {
            return false;
        }
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (worldIn.func_180495_p(pos.func_177972_a(enumfacing)).func_177230_c() != this) continue;
            return true;
        }
        return false;
    }

    public boolean func_149686_d(IBlockState state) {
        return false;
    }

    private boolean isOcelotSittingOnChest(World worldIn, BlockPos pos) {
        for (Entity entity : worldIn.func_72872_a(EntityOcelot.class, new AxisAlignedBB((double)pos.func_177958_n(), (double)(pos.func_177956_o() + 1), (double)pos.func_177952_p(), (double)(pos.func_177958_n() + 1), (double)(pos.func_177956_o() + 2), (double)(pos.func_177952_p() + 1)))) {
            EntityOcelot entityocelot = (EntityOcelot)entity;
            if (!entityocelot.func_70906_o()) continue;
            return true;
        }
        return false;
    }

    public boolean func_149662_c(IBlockState state) {
        return false;
    }

    public void func_189540_a(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.func_189540_a(state, worldIn, pos, blockIn, fromPos);
        TileEntity tileentity = worldIn.func_175625_s(pos);
        if (tileentity instanceof TileEntityLockedChest) {
            tileentity.func_145836_u();
        }
    }

    public boolean func_180639_a(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.field_72995_K) {
            ClientSender.activateMillChest(playerIn, new Point(pos));
        }
        return true;
    }

    public void func_176213_c(World worldIn, BlockPos pos, IBlockState state) {
        this.checkForSurroundingChests(worldIn, pos, state);
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.func_177972_a(enumfacing);
            IBlockState iblockstate = worldIn.func_180495_p(blockpos);
            if (iblockstate.func_177230_c() != this) continue;
            this.checkForSurroundingChests(worldIn, blockpos, iblockstate);
        }
    }

    public void func_180649_a(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        super.func_180649_a(worldIn, pos, playerIn);
    }

    public void func_180633_a(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tileentity;
        boolean flag3;
        EnumFacing enumfacing = EnumFacing.func_176731_b((int)(MathHelper.func_76128_c((double)((double)(placer.field_70177_z * 4.0f / 360.0f) + 0.5)) & 3)).func_176734_d();
        state = state.func_177226_a((IProperty)FACING, (Comparable)enumfacing);
        BlockPos blockpos = pos.func_177978_c();
        BlockPos blockpos1 = pos.func_177968_d();
        BlockPos blockpos2 = pos.func_177976_e();
        BlockPos blockpos3 = pos.func_177974_f();
        boolean flag = this == worldIn.func_180495_p(blockpos).func_177230_c();
        boolean flag1 = this == worldIn.func_180495_p(blockpos1).func_177230_c();
        boolean flag2 = this == worldIn.func_180495_p(blockpos2).func_177230_c();
        boolean bl = flag3 = this == worldIn.func_180495_p(blockpos3).func_177230_c();
        if (!(flag || flag1 || flag2 || flag3)) {
            worldIn.func_180501_a(pos, state, 3);
        } else if (enumfacing.func_176740_k() != EnumFacing.Axis.X || !flag && !flag1) {
            if (enumfacing.func_176740_k() == EnumFacing.Axis.Z && (flag2 || flag3)) {
                if (flag2) {
                    worldIn.func_180501_a(blockpos2, state, 3);
                } else {
                    worldIn.func_180501_a(blockpos3, state, 3);
                }
                worldIn.func_180501_a(pos, state, 3);
            }
        } else {
            if (flag) {
                worldIn.func_180501_a(blockpos, state, 3);
            } else {
                worldIn.func_180501_a(blockpos1, state, 3);
            }
            worldIn.func_180501_a(pos, state, 3);
        }
        if (stack.func_82837_s() && (tileentity = worldIn.func_175625_s(pos)) instanceof TileEntityLockedChest) {
            ((TileEntityLockedChest)tileentity).func_190575_a(stack.func_82833_r());
        }
    }

    public int func_149745_a(Random random) {
        return 0;
    }

    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        return !this.isDoubleChest(world, pos) && super.rotateBlock(world, pos, axis);
    }

    public IBlockState func_185471_a(IBlockState state, Mirror mirrorIn) {
        return state.func_185907_a(mirrorIn.func_185800_a((EnumFacing)state.func_177229_b((IProperty)FACING)));
    }

    public IBlockState func_185499_a(IBlockState state, Rotation rot) {
        return state.func_177226_a((IProperty)FACING, (Comparable)rot.func_185831_a((EnumFacing)state.func_177229_b((IProperty)FACING)));
    }
}

