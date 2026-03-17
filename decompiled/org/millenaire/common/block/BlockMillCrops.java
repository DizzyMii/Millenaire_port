/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.IGrowable
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.NonNullList
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.common.IPlantable
 */
package org.millenaire.common.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

public class BlockMillCrops
extends BlockBush
implements IGrowable {
    public static final PropertyInteger AGE = PropertyInteger.func_177719_a((String)"age", (int)0, (int)7);
    private static final AxisAlignedBB[] CROPS_AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.125, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.375, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.625, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.75, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.875, 1.0), new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)};
    private final boolean requireIrrigation;
    private final boolean slowGrowth;
    private final ResourceLocation seed;

    protected static float getGrowthChance(BlockMillCrops blockIn, World worldIn, BlockPos pos) {
        int irrigation = WorldUtilities.getBlockMeta(worldIn, new Point(pos).getBelow());
        if (blockIn.requireIrrigation && irrigation == 0) {
            return 0.0f;
        }
        return !blockIn.slowGrowth ? 8.0f : 4.0f;
    }

    public BlockMillCrops(String cropName, boolean requireIrrigation, boolean slowGrowth, ResourceLocation seed) {
        this.requireIrrigation = requireIrrigation;
        this.slowGrowth = slowGrowth;
        this.func_149675_a(true);
        this.func_149647_a(null);
        this.func_149711_c(0.0f);
        this.func_149672_a(SoundType.field_185850_c);
        this.seed = seed;
        this.func_149663_c("millenaire." + cropName);
        this.setRegistryName(cropName);
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a((IProperty)AGE, (Comparable)Integer.valueOf(0)));
    }

    public boolean func_180671_f(World worldIn, BlockPos pos, IBlockState state) {
        IBlockState soil = worldIn.func_180495_p(pos.func_177977_b());
        return (worldIn.func_175699_k(pos) >= 8 || worldIn.func_175678_i(pos)) && soil.func_177230_c().canSustainPlant(soil, (IBlockAccess)worldIn, pos.func_177977_b(), EnumFacing.UP, (IPlantable)this);
    }

    public boolean func_176473_a(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return !this.isMaxAge(state);
    }

    protected boolean func_185514_i(IBlockState state) {
        return state.func_177230_c() == Blocks.field_150458_ak;
    }

    public boolean func_180670_a(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{AGE});
    }

    protected int getAge(IBlockState state) {
        return (Integer)state.func_177229_b((IProperty)this.getAgeProperty());
    }

    protected PropertyInteger getAgeProperty() {
        return AGE;
    }

    protected int getBonemealAgeIncrease(World worldIn) {
        return MathHelper.func_76136_a((Random)worldIn.field_73012_v, (int)2, (int)5);
    }

    public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CROPS_AABB[(Integer)state.func_177229_b((IProperty)this.getAgeProperty())];
    }

    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        Random rand;
        super.getDrops(drops, world, pos, state, 0);
        int age = this.getAge(state);
        Random random = rand = world instanceof World ? ((World)world).field_73012_v : new Random();
        if (age >= this.getMaxAge()) {
            for (int i = 0; i < 3 + fortune; ++i) {
                if (rand.nextInt(2 * this.getMaxAge()) > age) continue;
                drops.add((Object)new ItemStack(this.getSeed(), 1, 0));
            }
        }
    }

    public ItemStack func_185473_a(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this.getSeed());
    }

    public Item func_180660_a(IBlockState state, Random rand, int fortune) {
        return Item.func_111206_d((String)this.seed.toString());
    }

    public int getMaxAge() {
        return 7;
    }

    public int func_176201_c(IBlockState state) {
        return this.getAge(state);
    }

    protected Item getSeed() {
        return Item.func_111206_d((String)this.seed.toString());
    }

    public IBlockState func_176203_a(int meta) {
        return this.withAge(meta);
    }

    public void grow(World worldIn, BlockPos pos, IBlockState state) {
        int j;
        int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
        if (i > (j = this.getMaxAge())) {
            i = j;
        }
        worldIn.func_180501_a(pos, this.withAge(i), 2);
    }

    public void func_176474_b(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        this.grow(worldIn, pos, state);
    }

    public void func_180657_a(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.func_180657_a(worldIn, player, pos, state, te, stack);
        BlockItemUtilities.checkForHarvestTheft(player, pos);
    }

    public boolean isMaxAge(IBlockState state) {
        return (Integer)state.func_177229_b((IProperty)this.getAgeProperty()) >= this.getMaxAge();
    }

    public int func_149745_a(Random par1Random) {
        return 1;
    }

    public void func_180650_b(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        float growthChance;
        int i;
        super.func_180650_b(worldIn, pos, state, rand);
        if (worldIn.func_175671_l(pos.func_177984_a()) >= 9 && (i = this.getAge(state)) < this.getMaxAge() && (growthChance = BlockMillCrops.getGrowthChance(this, worldIn, pos)) > 0.0f && ForgeHooks.onCropsGrowPre((World)worldIn, (BlockPos)pos, (IBlockState)state, (rand.nextInt((int)(25.0f / growthChance)) == 0 ? 1 : 0) != 0)) {
            worldIn.func_180501_a(pos, this.withAge(i + 1), 2);
            ForgeHooks.onCropsGrowPost((World)worldIn, (BlockPos)pos, (IBlockState)state, (IBlockState)worldIn.func_180495_p(pos));
        }
    }

    public IBlockState withAge(int age) {
        return this.func_176223_P().func_177226_a((IProperty)this.getAgeProperty(), (Comparable)Integer.valueOf(age));
    }
}

