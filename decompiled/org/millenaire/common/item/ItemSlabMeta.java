/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.item.ItemSlab
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import org.millenaire.common.item.IMetaBlockName;

public class ItemSlabMeta
extends ItemSlab {
    public ItemSlabMeta(BlockSlab halfBlock, BlockSlab fullBlock) {
        super((Block)halfBlock, halfBlock, fullBlock);
        if (!(this.field_150939_a instanceof IMetaBlockName)) {
            throw new IllegalArgumentException(String.format("The given Block %s is not an instance of IMetaBlockName!", this.field_150939_a.func_149739_a()));
        }
        this.func_77656_e(0);
        this.func_77627_a(true);
    }

    public int func_77647_b(int damage) {
        return damage;
    }

    public String func_77667_c(ItemStack stack) {
        return ((IMetaBlockName)this.field_150939_a).getSpecialName(stack);
    }
}

