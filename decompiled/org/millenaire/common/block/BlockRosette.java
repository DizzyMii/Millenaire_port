/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBeacon
 *  net.minecraft.block.BlockPane
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.MapColor
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.item.Item
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
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
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.BlockMillWall;
import org.millenaire.common.block.MillBlocks;

public class BlockRosette
extends BlockPane {
    public static final PropertyBool ROSETTE_NORTH = PropertyBool.func_177716_a((String)"ros_n");
    public static final PropertyBool ROSETTE_EAST = PropertyBool.func_177716_a((String)"ros_e");
    public static final PropertyBool ROSETTE_SOUTH = PropertyBool.func_177716_a((String)"ros_s");
    public static final PropertyBool ROSETTE_WEST = PropertyBool.func_177716_a((String)"ros_w");
    public static final PropertyBool ROSETTE_UP = PropertyBool.func_177716_a((String)"ros_u");
    public static final PropertyBool ROSETTE_DOWN = PropertyBool.func_177716_a((String)"ros_d");

    public BlockRosette(String blockName, Material material, SoundType soundType) {
        super(material, true);
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149647_a(MillBlocks.tabMillenaire);
        this.func_149672_a(soundType);
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a((IProperty)field_176241_b, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)field_176242_M, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)field_176243_N, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)field_176244_O, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)ROSETTE_NORTH, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)ROSETTE_EAST, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)ROSETTE_SOUTH, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)ROSETTE_WEST, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)ROSETTE_UP, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)ROSETTE_DOWN, (Comparable)Boolean.valueOf(false)));
    }

    public void func_180663_b(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.field_72995_K) {
            BlockBeacon.func_176450_d((World)worldIn, (BlockPos)pos);
        }
    }

    public boolean canPaneConnectTo(IBlockAccess world, BlockPos pos, EnumFacing dir) {
        BlockPos other = pos.func_177972_a(dir);
        IBlockState state = world.func_180495_p(other);
        return state.func_177230_c().canBeConnectedTo(world, other, dir.func_176734_d()) || this.func_193393_b(world, state, other, dir.func_176734_d()) || state.func_177230_c() instanceof BlockMillWall;
    }

    protected boolean func_149700_E() {
        return false;
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{field_176241_b, field_176242_M, field_176244_O, field_176243_N, ROSETTE_NORTH, ROSETTE_EAST, ROSETTE_WEST, ROSETTE_SOUTH, ROSETTE_UP, ROSETTE_DOWN});
    }

    public IBlockState func_176221_a(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.func_176221_a(state, world, pos).func_177226_a((IProperty)ROSETTE_NORTH, (Comparable)Boolean.valueOf(this.hasRosette(world, pos, EnumFacing.NORTH))).func_177226_a((IProperty)ROSETTE_EAST, (Comparable)Boolean.valueOf(this.hasRosette(world, pos, EnumFacing.EAST))).func_177226_a((IProperty)ROSETTE_SOUTH, (Comparable)Boolean.valueOf(this.hasRosette(world, pos, EnumFacing.SOUTH))).func_177226_a((IProperty)ROSETTE_WEST, (Comparable)Boolean.valueOf(this.hasRosette(world, pos, EnumFacing.WEST))).func_177226_a((IProperty)ROSETTE_UP, (Comparable)Boolean.valueOf(this.hasRosette(world, pos, EnumFacing.UP))).func_177226_a((IProperty)ROSETTE_DOWN, (Comparable)Boolean.valueOf(this.hasRosette(world, pos, EnumFacing.DOWN)));
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer func_180664_k() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    public MapColor func_180659_g(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.field_151670_w;
    }

    private boolean hasRosette(IBlockAccess world, BlockPos pos, EnumFacing direction) {
        return world.func_180495_p(pos.func_177972_a(direction)).func_177230_c() == this;
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "variant=inventory"));
    }

    public void func_176213_c(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.field_72995_K) {
            BlockBeacon.func_176450_d((World)worldIn, (BlockPos)pos);
        }
    }
}

