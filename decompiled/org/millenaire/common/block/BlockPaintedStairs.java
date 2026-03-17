/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.item.EnumDyeColor
 */
package org.millenaire.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import org.millenaire.common.block.BlockMillStairs;
import org.millenaire.common.block.IPaintedBlock;

public class BlockPaintedStairs
extends BlockMillStairs
implements IPaintedBlock {
    private final String baseBlockName;
    private final EnumDyeColor colour;

    public BlockPaintedStairs(String baseBlockName, IBlockState baseBlock, EnumDyeColor colour) {
        super(baseBlockName + "_" + colour.func_176610_l(), baseBlock);
        this.baseBlockName = baseBlockName;
        this.colour = colour;
    }

    @Override
    public String getBlockType() {
        return this.baseBlockName;
    }

    @Override
    public EnumDyeColor getDyeColour() {
        return this.colour;
    }
}

