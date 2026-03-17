/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.EnumDyeColor
 */
package org.millenaire.common.item;

import net.minecraft.item.EnumDyeColor;
import org.millenaire.common.block.BlockPaintedBricks;
import org.millenaire.common.item.ItemMill;

public class ItemPaintBucket
extends ItemMill {
    private final EnumDyeColor colour;

    public ItemPaintBucket(String baseName, EnumDyeColor colour) {
        super(baseName + "_" + BlockPaintedBricks.getColorName(colour));
        this.colour = colour;
    }

    public EnumDyeColor getColour() {
        return this.colour;
    }
}

