/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockFaceShape
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumBlockRenderType
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.IStringSerializable
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.common.EnumPlantType
 *  net.minecraftforge.common.IPlantable
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block.mock;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.IMetaBlockName;
import org.millenaire.common.item.MillItems;

public class MockBlockMarker
extends Block
implements IMetaBlockName {
    protected static final AxisAlignedBB CARPET_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);
    public static final PropertyEnum<Type> VARIANT = PropertyEnum.func_177709_a((String)"variant", Type.class);

    public MockBlockMarker(String blockName) {
        super(Material.field_151576_e);
        this.func_149649_H();
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149722_s();
        this.func_149647_a(MillBlocks.tabMillenaireContentCreator);
        this.field_149783_u = true;
        this.field_149787_q = false;
    }

    public boolean func_176209_a(IBlockState state, boolean hitIfLiquid) {
        return true;
    }

    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        EnumPlantType plantType = plantable.getPlantType(world, pos.func_177972_a(direction));
        switch (plantType) {
            case Desert: {
                return state.func_177229_b(VARIANT) == Type.PRESERVE_GROUND;
            }
            case Plains: {
                return state.func_177229_b(VARIANT) == Type.PRESERVE_GROUND;
            }
        }
        return false;
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{VARIANT});
    }

    public int func_180651_a(IBlockState state) {
        return ((Type)((Object)state.func_177229_b(VARIANT))).getMetadata();
    }

    public float func_185485_f(IBlockState state) {
        return 1.0f;
    }

    public BlockFaceShape func_193383_a(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        if (state.func_177229_b(VARIANT) == Type.PRESERVE_GROUND) {
            return BlockFaceShape.SOLID;
        }
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (state.func_177229_b(VARIANT) == Type.PRESERVE_GROUND) {
            return field_185505_j;
        }
        return CARPET_AABB;
    }

    @Nullable
    public AxisAlignedBB func_180646_a(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        if (blockState.func_177229_b(VARIANT) == Type.PRESERVE_GROUND) {
            return field_185505_j;
        }
        return field_185506_k;
    }

    public int func_176201_c(IBlockState state) {
        return ((Type)((Object)state.func_177229_b(MockBlockMarker.VARIANT))).meta;
    }

    public EnumBlockRenderType func_149645_b(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    public AxisAlignedBB func_180640_a(IBlockState state, World worldIn, BlockPos pos) {
        return new AxisAlignedBB((double)pos.func_177958_n(), (double)pos.func_177956_o(), (double)pos.func_177952_p(), (double)pos.func_177958_n() + 1.0, (double)pos.func_177956_o() + 1.0, (double)pos.func_177952_p() + 1.0);
    }

    @Override
    public String getSpecialName(ItemStack stack) {
        return "tile.millenaire." + this.getRegistryName().func_110623_a() + "." + ((Type)((Object)this.func_176203_a(stack.func_77960_j()).func_177229_b(VARIANT))).func_176610_l();
    }

    public IBlockState func_176203_a(int meta) {
        return this.func_176223_P().func_177226_a(VARIANT, (Comparable)((Object)Type.fromMeta(meta)));
    }

    @SideOnly(value=Side.CLIENT)
    public void func_149666_a(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (Type enumtype : Type.values()) {
            items.add((Object)new ItemStack((Block)this, 1, enumtype.getMetadata()));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        for (Type enumtype : Type.values()) {
            ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)enumtype.getMetadata(), (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "variant=" + enumtype.func_176610_l()));
        }
    }

    public boolean func_149686_d(IBlockState state) {
        return state.func_177229_b(VARIANT) == Type.PRESERVE_GROUND;
    }

    public boolean func_149662_c(IBlockState state) {
        return state.func_177229_b(VARIANT) == Type.PRESERVE_GROUND;
    }

    public boolean func_180639_a(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.field_72995_K) {
            return true;
        }
        if (playerIn.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() == MillItems.NEGATION_WAND) {
            int meta = state.func_177230_c().func_176201_c(state) + 1;
            if (Type.fromMeta(meta) == null) {
                meta = 0;
            }
            worldIn.func_180501_a(pos, state.func_177226_a(VARIANT, (Comparable)((Object)Type.fromMeta(meta))), 3);
            Mill.proxy.sendLocalChat(playerIn, 'a', Type.fromMeta((int)meta).name);
            return true;
        }
        return false;
    }

    public void func_180655_c(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        int color = 0xFFFFFF;
        switch ((Type)((Object)stateIn.func_177229_b(VARIANT))) {
            case PRESERVE_GROUND: {
                return;
            }
            case SLEEPING_POS: {
                color = 14680244;
                break;
            }
            case SELLING_POS: {
                color = 65484;
                break;
            }
            case CRAFTING_POS: {
                color = 1158400;
                break;
            }
            case DEFENDING_POS: {
                color = 0xFF0000;
                break;
            }
            case SHELTER_POS: {
                color = 8323127;
                break;
            }
            case PATH_START_POS: {
                color = 721110;
                break;
            }
            case LEISURE_POS: {
                color = 0xF08800;
                break;
            }
            case STALL: {
                color = 0x969600;
                break;
            }
            case BRICK_SPOT: {
                color = 0x870000;
                break;
            }
            case HEALING_SPOT: {
                color = 53760;
                break;
            }
            case FISHING_SPOT: {
                color = 120;
            }
        }
        double r = (double)(color >> 16 & 0xFF) / 255.0;
        double g = (double)(color >> 8 & 0xFF) / 255.0;
        double b = (double)(color & 0xFF) / 255.0;
        worldIn.func_175688_a(EnumParticleTypes.SPELL_MOB, (double)pos.func_177958_n() + 0.5, (double)pos.func_177956_o() + 0.5, (double)pos.func_177952_p() + 0.5, r, g, b, new int[0]);
    }

    public static enum Type implements IStringSerializable
    {
        PRESERVE_GROUND(0, "preserveground"),
        SLEEPING_POS(1, "sleepingpos"),
        SELLING_POS(2, "sellingpos"),
        CRAFTING_POS(3, "craftingpos"),
        DEFENDING_POS(4, "defendingpos"),
        SHELTER_POS(5, "shelterpos"),
        PATH_START_POS(6, "pathstartpos"),
        LEISURE_POS(7, "leisurepos"),
        STALL(8, "stall"),
        BRICK_SPOT(9, "brickspot"),
        HEALING_SPOT(10, "healingspot"),
        FISHING_SPOT(11, "fishingspot");

        public final int meta;
        public final String name;

        public static Type fromMeta(int meta) {
            for (Type t : Type.values()) {
                if (t.meta != meta) continue;
                return t;
            }
            return null;
        }

        public static int getMetaFromName(String name) {
            for (Type type : Type.values()) {
                if (!type.name.equalsIgnoreCase(name)) continue;
                return type.meta;
            }
            return -1;
        }

        private Type(int m, String n2) {
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
            return "Marker Pos (" + this.name + ")";
        }
    }
}

