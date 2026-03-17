/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block.mock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.IMetaBlockName;
import org.millenaire.common.item.MillItems;

public class MockBlockFree
extends Block
implements IMetaBlockName {
    public static final PropertyEnum<Resource> RESOURCE = PropertyEnum.func_177709_a((String)"resource", Resource.class);

    public MockBlockFree(String blockName) {
        super(Material.field_151576_e);
        this.func_149649_H();
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149722_s();
        this.func_149647_a(MillBlocks.tabMillenaireContentCreator);
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{RESOURCE});
    }

    public int func_180651_a(IBlockState state) {
        return ((Resource)((Object)state.func_177229_b(RESOURCE))).getMetadata();
    }

    public BlockRenderLayer func_180664_k() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    public int func_176201_c(IBlockState state) {
        return ((Resource)((Object)state.func_177229_b(MockBlockFree.RESOURCE))).meta;
    }

    @Override
    public String getSpecialName(ItemStack stack) {
        return "tile.millenaire." + this.getRegistryName().func_110623_a() + "." + ((Resource)((Object)this.func_176203_a(stack.func_77960_j()).func_177229_b(RESOURCE))).func_176610_l();
    }

    public IBlockState func_176203_a(int meta) {
        return this.func_176223_P().func_177226_a(RESOURCE, (Comparable)((Object)Resource.fromMeta(meta)));
    }

    @SideOnly(value=Side.CLIENT)
    public void func_149666_a(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (Resource enumtype : Resource.values()) {
            items.add((Object)new ItemStack((Block)this, 1, enumtype.getMetadata()));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        for (Resource enumtype : Resource.values()) {
            ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)enumtype.getMetadata(), (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "resource=" + enumtype.func_176610_l()));
        }
    }

    public boolean func_180639_a(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.field_72995_K) {
            return true;
        }
        if (playerIn.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() == MillItems.NEGATION_WAND) {
            int meta = state.func_177230_c().func_176201_c(state) + 1;
            if (Resource.fromMeta(meta) == null) {
                meta = 0;
            }
            worldIn.func_180501_a(pos, state.func_177226_a(RESOURCE, (Comparable)((Object)Resource.fromMeta(meta))), 3);
            Mill.proxy.sendLocalChat(playerIn, 'a', Resource.fromMeta((int)meta).name);
            return true;
        }
        return false;
    }

    public static enum Resource implements IStringSerializable
    {
        STONE(0, "stone"),
        SAND(1, "sand"),
        GRAVEL(2, "gravel"),
        SANDSTONE(3, "sandstone"),
        WOOL(4, "wool"),
        COBBLESTONE(5, "cobblestone"),
        STONEBRICK(6, "stonebrick"),
        PAINTEDBRICK(7, "paintedbrick"),
        GRASS_BLOCK(8, "grass_block");

        public final int meta;
        public final String name;

        public static Resource fromMeta(int meta) {
            for (Resource t : Resource.values()) {
                if (t.meta != meta) continue;
                return t;
            }
            return null;
        }

        private Resource(int m, String n2) {
            this.meta = m;
            this.name = n2;
        }

        public int getMetadata() {
            return this.meta;
        }

        public String func_176610_l() {
            return this.name;
        }

        public String toString() {
            return "Free Block (" + this.name + ")";
        }
    }
}

