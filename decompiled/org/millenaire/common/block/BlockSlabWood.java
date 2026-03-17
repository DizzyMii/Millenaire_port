/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.BlockSlab$EnumBlockHalf
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.MapColor
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.BlockDecorativeWood;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.item.IMetaBlockName;

public class BlockSlabWood
extends BlockSlab
implements IMetaBlockName {
    public static final PropertyBool SEAMLESS = PropertyBool.func_177716_a((String)"seamless");
    public static final PropertyEnum<BlockDecorativeWood.EnumType> VARIANT = PropertyEnum.func_177709_a((String)"variant", BlockDecorativeWood.EnumType.class);

    public BlockSlabWood(String blockName) {
        super(Material.field_151575_d);
        IBlockState iblockstate = this.field_176227_L.func_177621_b();
        iblockstate = this.func_176552_j() ? iblockstate.func_177226_a((IProperty)SEAMLESS, (Comparable)Boolean.valueOf(false)) : iblockstate.func_177226_a((IProperty)field_176554_a, (Comparable)BlockSlab.EnumBlockHalf.BOTTOM);
        this.func_180632_j(iblockstate.func_177226_a(VARIANT, (Comparable)((Object)BlockDecorativeWood.EnumType.TIMBERFRAMEPLAIN)));
        this.setRegistryName(blockName);
        this.func_149647_a(MillBlocks.tabMillenaire);
        this.func_149711_c(2.0f);
        this.func_149752_b(5.0f);
        this.func_149672_a(SoundType.field_185848_a);
        this.field_149783_u = true;
    }

    protected BlockStateContainer func_180661_e() {
        return this.func_176552_j() ? new BlockStateContainer((Block)this, new IProperty[]{SEAMLESS, VARIANT}) : new BlockStateContainer((Block)this, new IProperty[]{field_176554_a, VARIANT});
    }

    public int func_180651_a(IBlockState state) {
        return ((BlockDecorativeWood.EnumType)((Object)state.func_177229_b(VARIANT))).getMetadata();
    }

    public MapColor func_180659_g(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return ((BlockDecorativeWood.EnumType)((Object)state.func_177229_b(VARIANT))).getMapColor();
    }

    public int func_176201_c(IBlockState state) {
        int i = 0;
        i |= ((BlockDecorativeWood.EnumType)((Object)state.func_177229_b(VARIANT))).getMetadata();
        if (this.func_176552_j()) {
            if (((Boolean)state.func_177229_b((IProperty)SEAMLESS)).booleanValue()) {
                i |= 8;
            }
        } else if (state.func_177229_b((IProperty)field_176554_a) == BlockSlab.EnumBlockHalf.TOP) {
            i |= 8;
        }
        return i;
    }

    @Override
    public String getSpecialName(ItemStack stack) {
        return this.func_150002_b(stack.func_77960_j());
    }

    public IBlockState func_176203_a(int meta) {
        IBlockState iblockstate = this.func_176223_P().func_177226_a(VARIANT, (Comparable)((Object)BlockDecorativeWood.EnumType.byMetadata(meta & 7)));
        iblockstate = this.func_176552_j() ? iblockstate.func_177226_a((IProperty)SEAMLESS, (Comparable)Boolean.valueOf((meta & 8) != 0)) : iblockstate.func_177226_a((IProperty)field_176554_a, (Comparable)((meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP));
        return iblockstate;
    }

    public void func_149666_a(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (BlockDecorativeWood.EnumType enumtype : BlockDecorativeWood.EnumType.values()) {
            if (!enumtype.hasSlab()) continue;
            items.add((Object)new ItemStack((Block)this, 1, enumtype.getMetadata()));
        }
    }

    public Comparable<?> func_185674_a(ItemStack stack) {
        return BlockDecorativeWood.EnumType.byMetadata(stack.func_77960_j() & 7);
    }

    public String func_150002_b(int meta) {
        return "tile.millenaire.slabs_" + BlockDecorativeWood.EnumType.byMetadata(meta).getUnlocalizedName();
    }

    public IProperty<?> func_176551_l() {
        return VARIANT;
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        for (BlockDecorativeWood.EnumType enumtype : BlockDecorativeWood.EnumType.values()) {
            if (!enumtype.hasSlab()) continue;
            ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)enumtype.getMetadata(), (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "half=bottom,variant=" + enumtype.func_176610_l()));
        }
    }

    public boolean func_176552_j() {
        return false;
    }
}

