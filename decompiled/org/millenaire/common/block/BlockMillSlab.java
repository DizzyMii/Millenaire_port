/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 */
package org.millenaire.common.block;

import net.minecraft.block.Block;
import org.millenaire.common.block.BlockHalfSlab;

public class BlockMillSlab
extends BlockHalfSlab {
    public BlockMillSlab(String name, Block baseBlock) {
        super(baseBlock);
        this.func_149663_c("millenaire." + name);
        this.setRegistryName(name);
        this.setHarvestLevel("pickaxe", 0);
        this.func_149711_c(1.5f);
        this.func_149752_b(10.0f);
    }
}

