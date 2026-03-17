/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.millenaire.common.item.IMetaBlockName;

public class ItemBlockMeta
extends ItemBlock {
    public ItemBlockMeta(Block block) {
        super(block);
        if (!(block instanceof IMetaBlockName)) {
            throw new IllegalArgumentException(String.format("The given Block %s is not an instance of IMetaBlockName!", block.func_149739_a()));
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

