package org.dizzymii.millenaire2.ui.firepit;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SlotFirePitFuel extends Slot {
    public SlotFirePitFuel(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    @Override
    public boolean mayPlace(net.minecraft.world.item.ItemStack stack) {
        return stack.getBurnTime(net.minecraft.world.item.crafting.RecipeType.SMELTING) > 0;
    }
}
