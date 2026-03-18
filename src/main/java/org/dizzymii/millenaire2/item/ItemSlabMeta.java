package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class ItemSlabMeta extends BlockItem {
    public ItemSlabMeta(Block block, Properties props) {
        super(block, props);
    }
    // In 1.21.1, slab variants use blockstates instead of metadata; default BlockItem behavior suffices
}
