/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.BlockSlab$EnumBlockHalf
 *  net.minecraft.block.material.MapColor
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.item.IMetaBlockName;

public abstract class BlockOrientedSlab
extends BlockSlab
implements IMetaBlockName {
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.func_177709_a((String)"variant", Variant.class);
    public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.func_177709_a((String)"axis", EnumFacing.Axis.class);

    public BlockOrientedSlab(String slabName) {
        super(Material.field_151576_e);
        IBlockState iblockstate = this.field_176227_L.func_177621_b();
        if (!this.func_176552_j()) {
            iblockstate = iblockstate.func_177226_a((IProperty)field_176554_a, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM);
            this.field_149783_u = true;
        }
        iblockstate = iblockstate.func_177226_a(VARIANT, (Comparable)((Object)Variant.DEFAULT));
        this.func_180632_j(iblockstate.func_177226_a(AXIS, (Comparable)EnumFacing.Axis.X));
        this.setHarvestLevel("pickaxe", 0);
        this.func_149711_c(1.5f);
        this.func_149752_b(10.0f);
        this.func_149663_c("millenaire." + slabName);
        this.setRegistryName(slabName);
        this.func_149647_a(MillBlocks.tabMillenaire);
    }

    protected BlockStateContainer func_180661_e() {
        return this.func_176552_j() ? new BlockStateContainer((Block)this, new IProperty[]{VARIANT, AXIS}) : new BlockStateContainer((Block)this, new IProperty[]{VARIANT, field_176554_a, AXIS});
    }

    public int func_180651_a(IBlockState state) {
        return 0;
    }

    public ItemStack func_185473_a(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(state.func_177230_c());
    }

    public Item func_180660_a(IBlockState state, Random rand, int fortune) {
        return Item.func_150898_a((Block)state.func_177230_c());
    }

    public MapColor func_180659_g(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.field_151676_q;
    }

    public int func_176201_c(IBlockState state) {
        int i = 0;
        EnumFacing.Axis enumfacing$axis = (EnumFacing.Axis)state.func_177229_b(AXIS);
        if (enumfacing$axis == EnumFacing.Axis.X) {
            i |= 4;
        } else if (enumfacing$axis == EnumFacing.Axis.Z) {
            i |= 8;
        }
        if (!this.func_176552_j() && state.func_177229_b((IProperty)field_176554_a) == BlockSlab.EnumBlockHalf.TOP) {
            i |= 2;
        }
        return i;
    }

    @Override
    public String getSpecialName(ItemStack stack) {
        return this.func_149739_a();
    }

    public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState iblockstate = this.func_176203_a(meta);
        iblockstate = facing.func_176740_k() == EnumFacing.Axis.Y ? iblockstate.func_177226_a(AXIS, (Comparable)EnumFacing.Axis.X) : iblockstate.func_177226_a(AXIS, (Comparable)facing.func_176740_k());
        if (!this.func_176552_j()) {
            iblockstate = facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double)hitY <= 0.5) ? iblockstate.func_177226_a((IProperty)field_176554_a, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM) : iblockstate.func_177226_a((IProperty)field_176554_a, (Comparable)BlockSlab.EnumBlockHalf.TOP);
        }
        return iblockstate;
    }

    public IBlockState func_176203_a(int meta) {
        EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Y;
        int i = meta & 0xC;
        if (i == 4) {
            enumfacing$axis = EnumFacing.Axis.X;
        } else if (i == 8) {
            enumfacing$axis = EnumFacing.Axis.Z;
        }
        IBlockState iblockstate = this.func_176223_P().func_177226_a(AXIS, (Comparable)enumfacing$axis);
        if (!this.func_176552_j()) {
            iblockstate = iblockstate.func_177226_a((IProperty)field_176554_a, (Comparable)((meta & 2) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP));
        }
        return iblockstate;
    }

    public Comparable<?> func_185674_a(ItemStack stack) {
        return Variant.DEFAULT;
    }

    public String func_150002_b(int meta) {
        return this.func_149739_a();
    }

    public IProperty<?> func_176551_l() {
        return VARIANT;
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        if (this.func_176552_j()) {
            ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "axis=x,variant=default"));
        } else {
            ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "axis=x,half=bottom,variant=default"));
        }
    }

    public int func_149745_a(Random random) {
        return 1;
    }

    public static enum Variant implements IStringSerializable
    {
        DEFAULT;


        public String func_176610_l() {
            return "default";
        }
    }

    public static class BlockOrientedSlabSlab
    extends BlockOrientedSlab {
        public BlockOrientedSlabSlab(String slabName) {
            super(slabName);
        }

        public boolean func_176552_j() {
            return false;
        }
    }

    public static class BlockOrientedSlabDouble
    extends BlockOrientedSlab {
        public BlockOrientedSlabDouble(String slabName) {
            super(slabName);
        }

        public boolean func_176552_j() {
            return true;
        }
    }
}

