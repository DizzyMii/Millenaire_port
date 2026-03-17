/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDirectional
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.Item
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
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
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

public class BlockMillStatue
extends BlockDirectional {
    private static final AxisAlignedBB CARVING_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);

    public BlockMillStatue(String blockName, SoundType sound, Material material) {
        super(material);
        this.func_149647_a(MillBlocks.tabMillenaire);
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a((IProperty)field_176387_N, (Comparable)EnumFacing.DOWN));
        this.setHarvestLevel("pickaxe", 0);
        this.func_149711_c(0.5f);
        this.func_149752_b(2.0f);
        this.func_149672_a(sound);
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149713_g(0);
    }

    public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CARVING_AABB;
    }

    public void func_185477_a(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        BlockMillStatue.func_185492_a((BlockPos)pos, (AxisAlignedBB)entityBox, collidingBoxes, (AxisAlignedBB)state.func_185900_c((IBlockAccess)worldIn, pos));
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing f = EnumFacing.func_190914_a((BlockPos)pos, (EntityLivingBase)placer);
        if (f != EnumFacing.DOWN && f != EnumFacing.UP) {
            return this.func_176223_P().func_177226_a((IProperty)field_176387_N, (Comparable)f);
        }
        return this.func_176223_P().func_177226_a((IProperty)field_176387_N, (Comparable)EnumFacing.SOUTH);
    }

    @Nullable
    public static EnumFacing getFacing(int meta) {
        int i = meta & 7;
        return i > 5 ? null : EnumFacing.func_82600_a((int)i);
    }

    public IBlockState func_176203_a(int meta) {
        return this.func_176223_P().func_177226_a((IProperty)field_176387_N, (Comparable)BlockMillStatue.getFacing(meta));
    }

    public int func_176201_c(IBlockState state) {
        int i = 0;
        return i |= ((EnumFacing)state.func_177229_b((IProperty)field_176387_N)).func_176745_a();
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{field_176387_N});
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "facing=down"));
    }

    public boolean func_149686_d(IBlockState state) {
        return false;
    }

    public boolean func_149662_c(IBlockState state) {
        return false;
    }
}

