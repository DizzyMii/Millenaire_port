/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDoublePlant$EnumBlockHalf
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ForgeHooks
 */
package org.millenaire.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.millenaire.common.block.BlockMillCrops;

public class BlockGrapeVine
extends BlockMillCrops {
    public static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> HALF = PropertyEnum.func_177709_a((String)"half", BlockDoublePlant.EnumBlockHalf.class);

    public BlockGrapeVine(String cropName, boolean requireIrrigation, boolean slowGrowth, ResourceLocation seed) {
        super(cropName, requireIrrigation, slowGrowth, seed);
        this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.LOWER));
    }

    @Override
    public boolean func_180671_f(World worldIn, BlockPos pos, IBlockState state) {
        if (state.func_177229_b(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
            return worldIn.func_180495_p(pos.func_177977_b()).func_177230_c() == this && worldIn.func_180495_p(pos.func_177977_b()).func_177229_b(HALF) == BlockDoublePlant.EnumBlockHalf.LOWER && super.func_180671_f(worldIn, pos.func_177977_b(), worldIn.func_180495_p(pos.func_177977_b()));
        }
        return worldIn.func_180495_p(pos.func_177984_a()).func_177230_c() == this && worldIn.func_180495_p(pos.func_177984_a()).func_177229_b(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER && super.func_180671_f(worldIn, pos, state);
    }

    public boolean func_176196_c(World worldIn, BlockPos pos) {
        return super.func_176196_c(worldIn, pos) && worldIn.func_175623_d(pos.func_177984_a());
    }

    protected void func_176475_e(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.func_180671_f(worldIn, pos, state)) {
            BlockGrapeVine lowerBlock;
            boolean upper = state.func_177229_b(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER;
            BlockPos upperPos = upper ? pos : pos.func_177984_a();
            BlockPos lowerPos = upper ? pos.func_177977_b() : pos;
            BlockGrapeVine upperBlock = upper ? this : worldIn.func_180495_p(upperPos).func_177230_c();
            BlockGrapeVine blockGrapeVine = lowerBlock = upper ? worldIn.func_180495_p(lowerPos).func_177230_c() : this;
            if (upperBlock == this) {
                worldIn.func_180501_a(upperPos, Blocks.field_150350_a.func_176223_P(), 2);
            }
            if (lowerBlock == this) {
                worldIn.func_180501_a(lowerPos, Blocks.field_150350_a.func_176223_P(), 3);
            }
        }
    }

    @Override
    protected BlockStateContainer func_180661_e() {
        return new BlockStateContainer((Block)this, new IProperty[]{AGE, HALF});
    }

    @Override
    public AxisAlignedBB func_185496_a(IBlockState state, IBlockAccess source, BlockPos pos) {
        return field_185505_j;
    }

    @Override
    public int func_176201_c(IBlockState state) {
        int i = this.getAge(state);
        return i |= state.func_177229_b(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER ? 8 : 0;
    }

    @Override
    public IBlockState func_176203_a(int meta) {
        return this.withAge(meta & 7).func_177226_a(HALF, (Comparable)((meta & 8) > 0 ? BlockDoublePlant.EnumBlockHalf.UPPER : BlockDoublePlant.EnumBlockHalf.LOWER));
    }

    @Override
    public void func_176474_b(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        int j;
        int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
        if (i > (j = this.getMaxAge())) {
            i = j;
        }
        if (state.func_177229_b(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
            worldIn.func_180501_a(pos.func_177977_b(), this.withAge(i), 2);
            worldIn.func_180501_a(pos, this.withAge(i).func_177226_a(HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER), 2);
        } else {
            worldIn.func_180501_a(pos, this.withAge(i), 2);
            worldIn.func_180501_a(pos.func_177984_a(), this.withAge(i).func_177226_a(HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER), 2);
        }
    }

    public void func_180633_a(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.func_180501_a(pos.func_177984_a(), this.func_176223_P().func_177226_a(HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER), 2);
    }

    @Override
    public void func_180650_b(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        float growthChance;
        int i;
        this.func_176475_e(worldIn, pos, state);
        if (state.func_177229_b(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
            return;
        }
        if (worldIn.func_175671_l(pos.func_177984_a()) >= 9 && (i = this.getAge(state)) < this.getMaxAge() && (growthChance = BlockGrapeVine.getGrowthChance(this, worldIn, pos)) > 0.0f && ForgeHooks.onCropsGrowPre((World)worldIn, (BlockPos)pos, (IBlockState)state, (rand.nextInt((int)(25.0f / growthChance)) == 0 ? 1 : 0) != 0)) {
            worldIn.func_180501_a(pos, this.withAge(i + 1), 2);
            worldIn.func_180501_a(pos.func_177984_a(), this.withAge(i + 1).func_177226_a(HALF, (Comparable)BlockDoublePlant.EnumBlockHalf.UPPER), 2);
            ForgeHooks.onCropsGrowPost((World)worldIn, (BlockPos)pos, (IBlockState)state, (IBlockState)worldIn.func_180495_p(pos));
        }
    }
}

