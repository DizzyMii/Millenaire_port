package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.Item;

public class ItemFoodMultiple extends Item {
    public ItemFoodMultiple(Properties props) {
        super(props);
    }
    // In 1.21.1, food nutrition is set via Item.Properties.food(); multi-variant handled by separate registrations
}
