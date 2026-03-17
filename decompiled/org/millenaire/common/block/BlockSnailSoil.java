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
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.item.IMetaBlockName;

public class BlockSnailSoil
extends Block
implements IMetaBlockName {
    public static final PropertyEnum<EnumType> PROGRESS = PropertyEnum.func_177709_a((String)"progress", EnumType.class);

    public BlockSnailSoil(String blockName) {
        super(Material.field_151578_c);
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149647_a(MillBlocks.tabMillenaire);
        this.func_149675_a(true);
        this.setHarvestLevel("space", 0);
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(PROGRESS, (Comparable)((Object)EnumType.SNAIL_SOIL_EMPTY)));
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{PROGRESS});
    }

    public int func_180651_a(IBlockState state) {
        return 0;
    }

    public int func_176201_c(IBlockState state) {
        return ((EnumType)((Object)state.func_177229_b(PROGRESS))).getMetadata();
    }

    @Override
    public String getSpecialName(ItemStack stack) {
        return "tile.millenaire." + ((EnumType)((Object)this.func_176203_a(stack.func_77960_j()).func_177229_b(PROGRESS))).func_176610_l();
    }

    public IBlockState func_176203_a(int meta) {
        return this.func_176223_P().func_177226_a(PROGRESS, (Comparable)((Object)EnumType.byMetadata(meta)));
    }

    @SideOnly(value=Side.CLIENT)
    public void func_149666_a(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add((Object)new ItemStack((Block)this, 1, 0));
        items.add((Object)new ItemStack((Block)this, 1, 3));
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        for (EnumType enumtype : EnumType.values()) {
            ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)enumtype.getMetadata(), (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "progress=" + enumtype.func_176610_l()));
        }
    }

    public void func_180650_b(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        int currentValue = ((EnumType)((Object)state.func_177229_b(PROGRESS))).getMetadata();
        if (currentValue < 3) {
            boolean airAboveWater;
            boolean waterAbove = worldIn.func_180495_p(pos.func_177984_a()).func_177230_c() == Blocks.field_150355_j;
            boolean bl = airAboveWater = !worldIn.func_180495_p(pos.func_177984_a().func_177984_a()).func_191058_s();
            if (waterAbove && airAboveWater && rand.nextInt(2) == 0) {
                IBlockState newState = state.func_177226_a(PROGRESS, (Comparable)((Object)EnumType.byMetadata(++currentValue)));
                worldIn.func_175656_a(pos, newState);
            }
        }
    }

    public static final class EnumType
    extends Enum<EnumType>
    implements IStringSerializable {
        public static final /* enum */ EnumType SNAIL_SOIL_EMPTY;
        public static final /* enum */ EnumType SNAIL_SOIL_IP1;
        public static final /* enum */ EnumType SNAIL_SOIL_IP2;
        public static final /* enum */ EnumType SNAIL_SOIL_FULL;
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
            SNAIL_SOIL_EMPTY = new EnumType(0, "snail_soil_empty");
            SNAIL_SOIL_IP1 = new EnumType(1, "snail_soil_ip1");
            SNAIL_SOIL_IP2 = new EnumType(2, "snail_soil_ip2");
            SNAIL_SOIL_FULL = new EnumType(3, "snail_soil_full");
            $VALUES = new EnumType[]{SNAIL_SOIL_EMPTY, SNAIL_SOIL_IP1, SNAIL_SOIL_IP2, SNAIL_SOIL_FULL};
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

