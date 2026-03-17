package org.dizzymii.millenaire2.ui.firepit;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SlotFirePitInput extends Slot {
    public SlotFirePitInput(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }
    // TODO: Implement input validation (only accept cookable items)
}
