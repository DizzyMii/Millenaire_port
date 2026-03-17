package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class ItemHalfSlab extends BlockItem {
    public ItemHalfSlab(Block block, Properties props) {
        super(block, props);
    }
    // Half slab placement logic handled by SlabBlock in 1.21.1; default BlockItem behavior suffices
}
