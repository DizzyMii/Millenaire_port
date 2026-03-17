/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockPane
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.item.Item
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.BlockMillWall;
import org.millenaire.common.block.MillBlocks;

public class BlockBars
extends BlockPane {
    protected BlockBars(String blockName) {
        super(Material.field_151575_d, true);
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149711_c(5.0f);
        this.func_149752_b(10.0f);
        this.func_149672_a(SoundType.field_185848_a);
        this.func_149647_a(MillBlocks.tabMillenaire);
    }

    public boolean canPaneConnectTo(IBlockAccess world, BlockPos pos, EnumFacing dir) {
        BlockPos other = pos.func_177972_a(dir);
        IBlockState state = world.func_180495_p(other);
        return state.func_177230_c().canBeConnectedTo(world, other, dir.func_176734_d()) || this.func_193393_b(world, state, other, dir.func_176734_d()) || state.func_177230_c() instanceof BlockMillWall;
    }

    public IBlockState func_176221_a(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.func_177226_a((IProperty)field_176241_b, (Comparable)Boolean.valueOf(this.canPaneConnectTo(worldIn, pos, EnumFacing.NORTH) || this.canPaneConnectTo(worldIn, pos, EnumFacing.SOUTH))).func_177226_a((IProperty)field_176243_N, (Comparable)Boolean.valueOf(this.canPaneConnectTo(worldIn, pos, EnumFacing.SOUTH) || this.canPaneConnectTo(worldIn, pos, EnumFacing.NORTH))).func_177226_a((IProperty)field_176244_O, (Comparable)Boolean.valueOf(this.canPaneConnectTo(worldIn, pos, EnumFacing.WEST) || this.canPaneConnectTo(worldIn, pos, EnumFacing.EAST))).func_177226_a((IProperty)field_176242_M, (Comparable)Boolean.valueOf(this.canPaneConnectTo(worldIn, pos, EnumFacing.EAST) || this.canPaneConnectTo(worldIn, pos, EnumFacing.WEST)));
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), ""));
    }
}

