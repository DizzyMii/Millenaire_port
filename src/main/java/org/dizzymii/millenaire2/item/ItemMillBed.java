package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class ItemMillBed extends BlockItem {
    public ItemMillBed(Block block, Properties props) {
        super(block, props);
    }
    // Delegates to BlockItem placement; dual-block logic handled by the bed block itself
}
