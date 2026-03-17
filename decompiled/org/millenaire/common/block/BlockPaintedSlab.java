/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package org.millenaire.common.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.block.BlockHalfSlab;
import org.millenaire.common.block.IPaintedBlock;
import org.millenaire.common.block.MillBlocks;

public class BlockPaintedSlab
extends BlockHalfSlab
implements IPaintedBlock {
    private final EnumDyeColor colour;
    private final String blockType;

    public BlockPaintedSlab(String blockType, Block baseBlock, EnumDyeColor colour) {
        super(baseBlock);
        this.blockType = blockType;
        this.colour = colour;
        this.func_149663_c("millenaire." + blockType + "_" + colour.func_176610_l());
        this.setRegistryName(blockType + "_" + colour.func_176610_l());
        this.setHarvestLevel("pickaxe", 0);
        this.func_149711_c(1.5f);
        this.func_149752_b(10.0f);
    }

    @Override
    public String getBlockType() {
        return this.blockType;
    }

    @Override
    public EnumDyeColor getDyeColour() {
        return this.colour;
    }

    public ItemStack func_185473_a(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(MillBlocks.PAINTED_BRICK_MAP.get(this.getBlockType()).get(this.colour));
    }

    public Item func_180660_a(IBlockState state, Random rand, int fortune) {
        return Item.func_150898_a((Block)MillBlocks.PAINTED_BRICK_MAP.get(this.getBlockType()).get(this.colour));
    }
}

