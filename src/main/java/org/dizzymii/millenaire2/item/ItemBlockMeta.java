package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class ItemBlockMeta extends BlockItem {
    public ItemBlockMeta(Block block, Properties props) {
        super(block, props);
    }
    // In 1.21.1, block variants use blockstates instead of metadata; default BlockItem behavior suffices
}
