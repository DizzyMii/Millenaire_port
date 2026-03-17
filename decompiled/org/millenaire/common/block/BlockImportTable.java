/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.ITileEntityProvider
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.entity.TileEntityImportTable;

public class BlockImportTable
extends Block
implements ITileEntityProvider {
    public BlockImportTable(String blockName) {
        super(Material.field_151575_d);
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149711_c(1.0f);
        this.func_149647_a(MillBlocks.tabMillenaireContentCreator);
        this.func_149672_a(SoundType.field_185848_a);
    }

    public TileEntity func_149915_a(World worldIn, int meta) {
        return new TileEntityImportTable();
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), ""));
    }

    public boolean func_180639_a(World world, BlockPos pos, IBlockState state, EntityPlayer entityplayer, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntityImportTable importTable = (TileEntityImportTable)world.func_175625_s(pos);
        if (importTable == null) {
            return false;
        }
        importTable.activate(entityplayer);
        return true;
    }
}

