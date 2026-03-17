/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.IGrowable
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.stats.StatList
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import java.util.ArrayList;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.BlockMillSapling;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.Point;

public class BlockFruitLeaves
extends BlockLeaves
implements IGrowable {
    public static final PropertyInteger AGE = PropertyInteger.func_177719_a((String)"age", (int)0, (int)3);
    private final BlockMillSapling.EnumMillWoodType type;
    private final ResourceLocation fruitRL;
    private final ResourceLocation saplingRL;

    public BlockFruitLeaves(String blockName, BlockMillSapling.EnumMillWoodType type, ResourceLocation saplingRL, ResourceLocation fruitRL) {
        this.type = type;
        this.fruitRL = fruitRL;
        this.saplingRL = saplingRL;
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149647_a(MillBlocks.tabMillenaire);
        Mill.proxy.setGraphicsLevel(this, true);
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a((IProperty)AGE, (Comparable)Integer.valueOf(0)).func_177226_a((IProperty)field_176236_b, (Comparable)Boolean.valueOf(true)).func_177226_a((IProperty)field_176237_a, (Comparable)Boolean.valueOf(true)));
    }

    public boolean func_176473_a(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return !this.isMaxAge(state);
    }

    public boolean func_180670_a(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{AGE, field_176236_b, field_176237_a});
    }

    public int func_180651_a(IBlockState state) {
        return 0;
    }

    protected int getAge(IBlockState state) {
        return (Integer)state.func_177229_b((IProperty)this.getAgeProperty());
    }

    protected PropertyInteger getAgeProperty() {
        return AGE;
    }

    public Item func_180660_a(IBlockState state, Random rand, int fortune) {
        return Item.func_150898_a((Block)Block.func_149684_b((String)this.saplingRL.toString()));
    }

    public int getMaxAge() {
        return 3;
    }

    public int func_176201_c(IBlockState state) {
        int i = 0;
        i |= ((Integer)state.func_177229_b((IProperty)AGE)).intValue();
        if (!((Boolean)state.func_177229_b((IProperty)field_176237_a)).booleanValue()) {
            i |= 4;
        }
        if (((Boolean)state.func_177229_b((IProperty)field_176236_b)).booleanValue()) {
            i |= 8;
        }
        return i;
    }

    public IBlockState func_176203_a(int meta) {
        return this.func_176223_P().func_177226_a((IProperty)AGE, (Comparable)Integer.valueOf(meta & 3)).func_177226_a((IProperty)field_176237_a, (Comparable)Boolean.valueOf((meta & 4) == 0)).func_177226_a((IProperty)field_176236_b, (Comparable)Boolean.valueOf((meta & 8) > 0));
    }

    public BlockMillSapling.EnumMillWoodType getType() {
        return this.type;
    }

    public BlockPlanks.EnumType func_176233_b(int meta) {
        return null;
    }

    public void func_176474_b(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        worldIn.func_180501_a(pos, this.withAge(this.getMaxAge()), 2);
    }

    public void func_180657_a(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        if (!worldIn.field_72995_K && stack.func_77973_b() == Items.field_151097_aZ) {
            player.func_71029_a(StatList.func_188055_a((Block)this));
        } else {
            super.func_180657_a(worldIn, player, pos, state, te, stack);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), ""));
    }

    public boolean isMaxAge(IBlockState state) {
        return (Integer)state.func_177229_b((IProperty)this.getAgeProperty()) >= this.getMaxAge();
    }

    public boolean func_180639_a(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (this.getAge(state) == this.getMaxAge()) {
            BlockItemUtilities.checkForHarvestTheft(player, pos);
            BlockFruitLeaves.func_180635_a((World)worldIn, (BlockPos)pos.func_177977_b(), (ItemStack)new ItemStack(Item.func_111206_d((String)this.fruitRL.toString()), 1));
            worldIn.func_175656_a(pos, state.func_177226_a((IProperty)AGE, (Comparable)Integer.valueOf(0)));
            return true;
        }
        return false;
    }

    public NonNullList<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return NonNullList.func_191197_a((int)1, (Object)new ItemStack((Block)this, 1, 0));
    }

    public void func_180650_b(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        int currentAge;
        super.func_180650_b(worldIn, pos, state, rand);
        long worldTime = worldIn.func_72820_D() % 24000L;
        int targetAge = 0;
        if (worldTime > 3000L && worldTime < 5000L) {
            targetAge = 1;
        } else if (worldTime > 5000L && worldTime < 6000L) {
            targetAge = 2;
        } else if (worldTime > 6000L && worldTime < 10000L) {
            targetAge = 3;
        }
        int validCurrentAge = targetAge - 1;
        if (validCurrentAge < 0) {
            validCurrentAge = this.getMaxAge();
        }
        if ((currentAge = ((Integer)state.func_177229_b((IProperty)AGE)).intValue()) == validCurrentAge) {
            ArrayList<Point> pointsToTest = new ArrayList<Point>();
            pointsToTest.add(new Point(pos));
            for (int count = 0; !pointsToTest.isEmpty() && count < 10000; ++count) {
                Point p = (Point)pointsToTest.get(pointsToTest.size() - 1);
                IBlockState bs = p.getBlockActualState(worldIn);
                if (bs.func_177230_c() == this && (Integer)bs.func_177229_b((IProperty)AGE) == validCurrentAge) {
                    p.setBlockState(worldIn, bs.func_177226_a((IProperty)AGE, (Comparable)Integer.valueOf(targetAge)));
                    for (int dx = -1; dx < 2; ++dx) {
                        for (int dy = -1; dy < 2; ++dy) {
                            for (int dz = -1; dz < 2; ++dz) {
                                pointsToTest.add(p.getRelative(dx, dy, dz));
                            }
                        }
                    }
                }
                pointsToTest.remove(p);
            }
        }
    }

    public IBlockState withAge(int age) {
        return this.func_176223_P().func_177226_a((IProperty)this.getAgeProperty(), (Comparable)Integer.valueOf(age));
    }
}

