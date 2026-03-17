/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.item;

import org.millenaire.common.item.ItemMill;

public class ItemClothes
extends ItemMill {
    private final String clothName;
    private final int priority;

    public ItemClothes(String itemName, int priority) {
        super(itemName);
        this.func_77656_e(0);
        this.clothName = itemName;
        this.priority = priority;
    }

    public String getClothName(int meta) {
        return this.clothName;
    }

    public int getClothPriority(int meta) {
        return this.priority;
    }
}

