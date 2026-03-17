package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class ItemMillSapling extends BlockItem {
    public ItemMillSapling(Block block, Properties props) {
        super(block, props);
    }
    // Delegates to BlockItem placement; sapling growth logic handled by BlockMillSapling
}
