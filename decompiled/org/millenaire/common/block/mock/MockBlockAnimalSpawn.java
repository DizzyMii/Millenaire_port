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

public class MockBlockAnimalSpawn
extends Block
implements IMetaBlockName {
    public static final PropertyEnum<Creature> CREATURE = PropertyEnum.func_177709_a((String)"creature", Creature.class);

    public MockBlockAnimalSpawn(String blockName) {
        super(Material.field_151576_e);
        this.func_149649_H();
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149722_s();
        this.func_149647_a(MillBlocks.tabMillenaireContentCreator);
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{CREATURE});
    }

    public int func_180651_a(IBlockState state) {
        return ((Creature)((Object)state.func_177229_b(CREATURE))).getMetadata();
    }

    public int func_176201_c(IBlockState state) {
        return ((Creature)((Object)state.func_177229_b(MockBlockAnimalSpawn.CREATURE))).meta;
    }

    @Override
    public String getSpecialName(ItemStack stack) {
        return "tile.millenaire." + this.getRegistryName().func_110623_a() + "." + ((Creature)((Object)this.func_176203_a(stack.func_77960_j()).func_177229_b(CREATURE))).func_176610_l();
    }

    public IBlockState func_176203_a(int meta) {
        return this.func_176223_P().func_177226_a(CREATURE, (Comparable)((Object)Creature.fromMeta(meta)));
    }

    @SideOnly(value=Side.CLIENT)
    public void func_149666_a(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (Creature enumtype : Creature.values()) {
            items.add((Object)new ItemStack((Block)this, 1, enumtype.getMetadata()));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        for (Creature enumtype : Creature.values()) {
            ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)enumtype.getMetadata(), (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "creature=" + enumtype.func_176610_l()));
        }
    }

    public boolean func_180639_a(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.field_72995_K) {
            return true;
        }
        if (playerIn.func_184586_b(EnumHand.MAIN_HAND).func_77973_b() == MillItems.NEGATION_WAND) {
            int meta = state.func_177230_c().func_176201_c(state) + 1;
            if (Creature.fromMeta(meta) == null) {
                meta = 0;
            }
            worldIn.func_180501_a(pos, state.func_177226_a(CREATURE, (Comparable)((Object)Creature.fromMeta(meta))), 3);
            Mill.proxy.sendLocalChat(playerIn, 'a', Creature.fromMeta((int)meta).name);
            return true;
        }
        return false;
    }

    public static enum Creature implements IStringSerializable
    {
        COW(0, "cow"),
        PIG(1, "pig"),
        SHEEP(2, "sheep"),
        CHICKEN(3, "chicken"),
        SQUID(4, "squid"),
        WOLF(5, "wolf"),
        POLARBEAR(6, "polarbear");

        public final int meta;
        public final String name;

        public static Creature fromMeta(int meta) {
            for (Creature t : Creature.values()) {
                if (t.meta != meta) continue;
                return t;
            }
            return null;
        }

        public static int getMetaFromName(String name) {
            for (Creature creature : Creature.values()) {
                if (!creature.name.equalsIgnoreCase(name)) continue;
                return creature.meta;
            }
            return -1;
        }

        private Creature(int m, String n2) {
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
            return "Animal Spawn (" + this.name + ")";
        }
    }
}

