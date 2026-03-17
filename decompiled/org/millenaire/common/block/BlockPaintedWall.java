/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.EnumDyeColor
 */
package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.item.EnumDyeColor;
import org.millenaire.common.block.BlockMillWall;
import org.millenaire.common.block.IPaintedBlock;

public class BlockPaintedWall
extends BlockMillWall
implements IPaintedBlock {
    private final String baseBlockName;
    private final EnumDyeColor colour;

    public BlockPaintedWall(String baseBlockName, Block baseBlock, EnumDyeColor colour) {
        super(baseBlockName + "_" + colour.func_176610_l(), baseBlock);
        this.baseBlockName = baseBlockName;
        this.colour = colour;
        this.setHarvestLevel("pickaxe", 0);
        this.field_149783_u = true;
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

