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
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.Mirror
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.Rotation
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
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.item.IMetaBlockName;

public class BlockMillStainedGlass
extends BlockPane
implements IMetaBlockName {
    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.func_177709_a((String)"variant", EnumType.class);
    private final String blockName;

    public BlockMillStainedGlass(String blockName) {
        super(Material.field_151592_s, true);
        this.blockName = blockName;
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149647_a(MillBlocks.tabMillenaire);
        this.func_149672_a(SoundType.field_185853_f);
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a((IProperty)field_176241_b, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)field_176242_M, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)field_176243_N, (Comparable)Boolean.valueOf(false)).func_177226_a((IProperty)field_176244_O, (Comparable)Boolean.valueOf(false)).func_177226_a(VARIANT, (Comparable)((Object)EnumType.WHITE)));
    }

    public void func_180663_b(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.field_72995_K) {
            BlockBeacon.func_176450_d((World)worldIn, (BlockPos)pos);
        }
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{field_176241_b, field_176242_M, field_176244_O, field_176243_N, VARIANT});
    }

    public int func_180651_a(IBlockState state) {
        return ((EnumType)((Object)state.func_177229_b(VARIANT))).getMetadata();
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer func_180664_k() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    public MapColor func_180659_g(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return MapColor.field_151670_w;
    }

    public int func_176201_c(IBlockState state) {
        return ((EnumType)((Object)state.func_177229_b(VARIANT))).getMetadata();
    }

    @Override
    public String getSpecialName(ItemStack stack) {
        return "tile.millenaire." + this.blockName + "." + ((EnumType)((Object)this.func_176203_a(stack.func_77960_j()).func_177229_b(VARIANT))).func_176610_l();
    }

    public IBlockState func_176203_a(int meta) {
        return this.func_176223_P().func_177226_a(VARIANT, (Comparable)((Object)EnumType.byMetadata(meta)));
    }

    public void func_149666_a(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (int i = 0; i < EnumType.values().length; ++i) {
            items.add((Object)new ItemStack((Block)this, 1, i));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        for (EnumType enumtype : EnumType.values()) {
            ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)enumtype.getMetadata(), (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName() + "_" + enumtype.name, "variant=inventory"));
        }
    }

    public void func_176213_c(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.field_72995_K) {
            BlockBeacon.func_176450_d((World)worldIn, (BlockPos)pos);
        }
    }

    public IBlockState func_185471_a(IBlockState state, Mirror mirrorIn) {
        switch (mirrorIn) {
            case LEFT_RIGHT: {
                return state.func_177226_a((IProperty)field_176241_b, state.func_177229_b((IProperty)field_176243_N)).func_177226_a((IProperty)field_176243_N, state.func_177229_b((IProperty)field_176241_b));
            }
            case FRONT_BACK: {
                return state.func_177226_a((IProperty)field_176242_M, state.func_177229_b((IProperty)field_176244_O)).func_177226_a((IProperty)field_176244_O, state.func_177229_b((IProperty)field_176242_M));
            }
        }
        return super.func_185471_a(state, mirrorIn);
    }

    public IBlockState func_185499_a(IBlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180: {
                return state.func_177226_a((IProperty)field_176241_b, state.func_177229_b((IProperty)field_176243_N)).func_177226_a((IProperty)field_176242_M, state.func_177229_b((IProperty)field_176244_O)).func_177226_a((IProperty)field_176243_N, state.func_177229_b((IProperty)field_176241_b)).func_177226_a((IProperty)field_176244_O, state.func_177229_b((IProperty)field_176242_M));
            }
            case COUNTERCLOCKWISE_90: {
                return state.func_177226_a((IProperty)field_176241_b, state.func_177229_b((IProperty)field_176242_M)).func_177226_a((IProperty)field_176242_M, state.func_177229_b((IProperty)field_176243_N)).func_177226_a((IProperty)field_176243_N, state.func_177229_b((IProperty)field_176244_O)).func_177226_a((IProperty)field_176244_O, state.func_177229_b((IProperty)field_176241_b));
            }
            case CLOCKWISE_90: {
                return state.func_177226_a((IProperty)field_176241_b, state.func_177229_b((IProperty)field_176244_O)).func_177226_a((IProperty)field_176242_M, state.func_177229_b((IProperty)field_176241_b)).func_177226_a((IProperty)field_176243_N, state.func_177229_b((IProperty)field_176242_M)).func_177226_a((IProperty)field_176244_O, state.func_177229_b((IProperty)field_176243_N));
            }
        }
        return state;
    }

    public static final class EnumType
    extends Enum<EnumType>
    implements IStringSerializable {
        public static final /* enum */ EnumType WHITE;
        public static final /* enum */ EnumType YELLOW;
        public static final /* enum */ EnumType YELLOW_RED;
        public static final /* enum */ EnumType RED_BLUE;
        public static final /* enum */ EnumType GREEN_BLUE;
        private static final EnumType[] META_LOOKUP;
        private final int meta;
        private final String name;
        private static final /* synthetic */ EnumType[] $VALUES;

        public static EnumType[] values() {
            return (EnumType[])$VALUES.clone();
        }

        public static EnumType valueOf(String name) {
            return Enum.valueOf(EnumType.class, name);
        }

        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }
            return META_LOOKUP[meta];
        }

        private EnumType(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public int getMetadata() {
            return this.meta;
        }

        public String func_176610_l() {
            return this.name;
        }

        public String getUnlocalizedName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }

        static {
            EnumType[] var0;
            WHITE = new EnumType(0, "white");
            YELLOW = new EnumType(1, "yellow");
            YELLOW_RED = new EnumType(2, "yellow_red");
            RED_BLUE = new EnumType(3, "red_blue");
            GREEN_BLUE = new EnumType(4, "green_blue");
            $VALUES = new EnumType[]{WHITE, YELLOW, YELLOW_RED, RED_BLUE, GREEN_BLUE};
            META_LOOKUP = new EnumType[EnumType.values().length];
            EnumType[] enumTypeArray = var0 = EnumType.values();
            int n = enumTypeArray.length;
            for (int i = 0; i < n; ++i) {
                EnumType var3;
                EnumType.META_LOOKUP[var3.getMetadata()] = var3 = enumTypeArray[i];
            }
        }
    }
}

