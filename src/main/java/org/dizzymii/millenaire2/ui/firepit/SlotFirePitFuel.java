package org.dizzymii.millenaire2.ui.firepit;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SlotFirePitFuel extends Slot {
    public SlotFirePitFuel(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }
    // TODO: Implement fuel validation (only accept burnable items)
}
