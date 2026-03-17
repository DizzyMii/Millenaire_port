/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockContainer
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumBlockRenderType
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.SoundCategory
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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.forge.Mill;

public class BlockFirePit
extends BlockContainer {
    public static final PropertyBool LIT = PropertyBool.func_177716_a((String)"lit");
    public static final PropertyEnum<EnumAlignment> ALIGNMENT = PropertyEnum.func_177709_a((String)"alignment", EnumAlignment.class);
    public static final AxisAlignedBB FIRE_PIT_BOX = new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 0.5, 0.8125);
    private ItemStack stack;

    public BlockFirePit(String name) {
        super(Material.field_151575_d);
        this.func_149672_a(SoundType.field_185848_a);
        this.func_149711_c(0.2f);
        this.func_180632_j(this.func_176223_P().func_177226_a((IProperty)LIT, (Comparable)Boolean.valueOf(false)));
        this.setRegistryName(name);
        this.func_149663_c("millenaire." + name);
        this.func_149647_a(MillBlocks.tabMillenaire);
    }

    public void func_180663_b(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity te = worldIn.func_175625_s(pos);
        if (te instanceof TileEntityFirePit) {
            ((TileEntityFirePit)te).dropAll();
        }
        super.func_180663_b(worldIn, pos, state);
    }

    @Nonnull
    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{LIT, ALIGNMENT});
    }

    @Nullable
    public TileEntity func_149915_a(@Nonnull World worldIn, int meta) {
        return new TileEntityFirePit();
    }

    @Nonnull
    public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Nonnull
    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer func_180664_k() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nonnull
    public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FIRE_PIT_BOX;
    }

    @Nullable
    public AxisAlignedBB func_180646_a(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
        return field_185506_k;
    }

    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return (Boolean)state.func_177229_b((IProperty)LIT) != false ? 15 : 0;
    }

    public int func_176201_c(IBlockState state) {
        return ((Boolean)state.func_177229_b((IProperty)LIT) != false ? 1 : 0) | ((EnumAlignment)((Object)state.func_177229_b(ALIGNMENT))).meta << 1;
    }

    public String getName() {
        if (this.stack == null) {
            this.stack = new ItemStack((Block)this);
        }
        return this.stack.func_82833_r();
    }

    @Nonnull
    @SideOnly(value=Side.CLIENT)
    public EnumBlockRenderType func_149645_b(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Nonnull
    public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.func_176223_P().func_177226_a(ALIGNMENT, (Comparable)((Object)EnumAlignment.fromAxis(placer.func_174811_aO().func_176740_k())));
    }

    @Nonnull
    public IBlockState func_176203_a(int meta) {
        boolean lit = (meta & 1) != 0;
        EnumAlignment alignment = EnumAlignment.fromMeta(meta & 2);
        return this.func_176223_P().func_177226_a((IProperty)LIT, (Comparable)Boolean.valueOf(lit)).func_177226_a(ALIGNMENT, (Comparable)((Object)alignment));
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    public boolean func_149686_d(IBlockState state) {
        return false;
    }

    public boolean func_149662_c(IBlockState state) {
        return false;
    }

    public boolean func_180639_a(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.field_72995_K) {
            playerIn.openGui((Object)Mill.instance, 16, worldIn, pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p());
        }
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public void func_180655_c(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (((Boolean)stateIn.func_177229_b((IProperty)LIT)).booleanValue()) {
            worldIn.func_184134_a((double)pos.func_177958_n() + 0.5, (double)pos.func_177956_o() + this.func_185496_a((IBlockState)stateIn, (IBlockAccess)worldIn, (BlockPos)pos).field_72338_b, (double)pos.func_177952_p() + 0.5, SoundEvents.field_187643_bs, SoundCategory.BLOCKS, 1.0f + rand.nextFloat(), rand.nextFloat() * 0.7f + 0.3f, false);
            if (rand.nextInt(24) == 0) {
                for (int i = 0; i < 3; ++i) {
                    double x = (double)pos.func_177958_n() + rand.nextDouble();
                    double y = (double)pos.func_177956_o() + rand.nextDouble() * 0.5 + 0.5;
                    double z = (double)pos.func_177952_p() + rand.nextDouble();
                    worldIn.func_175688_a(EnumParticleTypes.SMOKE_LARGE, x, y, z, 0.0, 0.0, 0.0, new int[0]);
                }
            }
        }
    }

    public static enum EnumAlignment implements IStringSerializable
    {
        X("x", 0, 90.0),
        Z("z", 1, 0.0);

        private final String name;
        private final int meta;
        public final double angle;

        public static EnumAlignment fromAxis(EnumFacing.Axis axis) {
            if (axis == EnumFacing.Axis.X) {
                return Z;
            }
            if (axis == EnumFacing.Axis.Z) {
                return X;
            }
            throw new UnsupportedOperationException("Y isn't horizontal!");
        }

        public static EnumAlignment fromMeta(int flag) {
            return flag != 0 ? X : Z;
        }

        private EnumAlignment(String name, int meta, double angle) {
            this.name = name;
            this.meta = meta;
            this.angle = angle;
        }

        public int getMeta() {
            return this.meta;
        }

        @Nonnull
        public String func_176610_l() {
            return this.name;
        }
    }
}

