/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.material.MapColor
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.NonNullList
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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.item.IMetaBlockName;

public class BlockExtendedMudBrick
extends BlockSlab
implements IMetaBlockName {
    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.func_177709_a((String)"variant", EnumType.class);

    public BlockExtendedMudBrick(String blockName) {
        super(Material.field_151576_e);
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149647_a(MillBlocks.tabMillenaire);
        this.setHarvestLevel("pickaxe", 0);
        this.func_149711_c(1.5f);
        this.func_149752_b(10.0f);
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(VARIANT, (Comparable)((Object)EnumType.MUDBRICK_SMOOTH)));
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{VARIANT});
    }

    public int func_180651_a(IBlockState state) {
        return ((EnumType)((Object)state.func_177229_b(VARIANT))).getMetadata();
    }

    public MapColor func_180659_g(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return ((EnumType)((Object)state.func_177229_b(VARIANT))).getMapColor();
    }

    public int func_176201_c(IBlockState state) {
        return ((EnumType)((Object)state.func_177229_b(VARIANT))).getMetadata();
    }

    @Override
    public String getSpecialName(ItemStack stack) {
        return "tile.millenaire." + ((EnumType)((Object)this.func_176203_a(stack.func_77960_j()).func_177229_b(VARIANT))).func_176610_l();
    }

    public IBlockState func_180642_a(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.func_176203_a(meta);
    }

    public IBlockState func_176203_a(int meta) {
        return this.func_176223_P().func_177226_a(VARIANT, (Comparable)((Object)EnumType.byMetadata(meta)));
    }

    @SideOnly(value=Side.CLIENT)
    public void func_149666_a(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (EnumType enumtype : EnumType.values()) {
            items.add((Object)new ItemStack((Block)this, 1, enumtype.getMetadata()));
        }
    }

    public Comparable<?> func_185674_a(ItemStack stack) {
        return EnumType.byMetadata(stack.func_77960_j() & 7);
    }

    public String func_150002_b(int meta) {
        return "tile.millenaire." + ((EnumType)((Object)this.func_176203_a(meta).func_177229_b(VARIANT))).func_176610_l();
    }

    public IProperty<?> func_176551_l() {
        return VARIANT;
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        for (EnumType enumtype : EnumType.values()) {
            ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)enumtype.getMetadata(), (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "variant=" + enumtype.func_176610_l()));
        }
    }

    public boolean func_176552_j() {
        return true;
    }

    public int func_149745_a(Random random) {
        return 1;
    }

    public static final class EnumType
    extends Enum<EnumType>
    implements IStringSerializable {
        public static final /* enum */ EnumType MUDBRICK_SMOOTH;
        public static final /* enum */ EnumType MUDBRICK_SELJUK_DECORATED;
        public static final /* enum */ EnumType MUDBRICK_SELJUK_ORNAMENTED;
        private static final EnumType[] META_LOOKUP;
        private final int meta;
        private final String name;
        private final MapColor mapColor;
        private final boolean hasSlab;
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

        private EnumType(int meta, String name, MapColor mapColor, boolean hasSlab) {
            this.meta = meta;
            this.name = name;
            this.mapColor = mapColor;
            this.hasSlab = hasSlab;
        }

        public MapColor getMapColor() {
            return this.mapColor;
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

        public boolean hasSlab() {
            return this.hasSlab;
        }

        public String toString() {
            return this.name;
        }

        static {
            EnumType[] var0;
            MUDBRICK_SMOOTH = new EnumType(0, "mudbrick_smooth", MapColor.field_151650_B, true);
            MUDBRICK_SELJUK_DECORATED = new EnumType(1, "mudbrick_seljuk_decorated", MapColor.field_151649_A, true);
            MUDBRICK_SELJUK_ORNAMENTED = new EnumType(2, "mudbrick_seljuk_ornamented", MapColor.field_151650_B, true);
            $VALUES = new EnumType[]{MUDBRICK_SMOOTH, MUDBRICK_SELJUK_DECORATED, MUDBRICK_SELJUK_ORNAMENTED};
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

