/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.NonNullList
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.item.IMetaBlockName;

public class BlockSod
extends Block
implements IMetaBlockName {
    public static final PropertyEnum<BlockPlanks.EnumType> VARIANT = PropertyEnum.func_177709_a((String)"variant", BlockPlanks.EnumType.class);

    public BlockSod(String blockName) {
        super(Material.field_151575_d);
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(VARIANT, (Comparable)BlockPlanks.EnumType.OAK));
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149647_a(MillBlocks.tabMillenaire);
        this.setHarvestLevel("axe", 0);
        this.func_149711_c(2.0f);
        this.func_149752_b(5.0f);
        this.func_149672_a(SoundType.field_185848_a);
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{VARIANT});
    }

    public int func_180651_a(IBlockState state) {
        return ((BlockPlanks.EnumType)state.func_177229_b(VARIANT)).func_176839_a();
    }

    public BlockRenderLayer func_180664_k() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    public int func_176201_c(IBlockState state) {
        return ((BlockPlanks.EnumType)state.func_177229_b(VARIANT)).func_176839_a();
    }

    @Override
    public String getSpecialName(ItemStack stack) {
        return "tile.millenaire." + this.getRegistryName().func_110623_a() + "_" + ((BlockPlanks.EnumType)this.func_176203_a(stack.func_77960_j()).func_177229_b(VARIANT)).func_176610_l();
    }

    public IBlockState func_176203_a(int meta) {
        return this.func_176223_P().func_177226_a(VARIANT, (Comparable)BlockPlanks.EnumType.func_176837_a((int)meta));
    }

    @SideOnly(value=Side.CLIENT)
    public void func_149666_a(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (BlockPlanks.EnumType enumtype : BlockPlanks.EnumType.values()) {
            items.add((Object)new ItemStack((Block)this, 1, enumtype.func_176839_a()));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        for (BlockPlanks.EnumType enumtype : BlockPlanks.EnumType.values()) {
            ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)enumtype.func_176839_a(), (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "variant=" + enumtype.func_176610_l()));
        }
    }
}

