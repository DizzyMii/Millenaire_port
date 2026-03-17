package org.dizzymii.millenaire2.block;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

/**
 * Custom Millenaire crop block.
 * Max age and seed/crop items are set per-instance via suppliers registered in MillBlocks.
 * Drops are handled via loot tables.
 */
public class BlockMillCrops extends CropBlock {

    private final int maxAge;
    private final Supplier<? extends ItemLike> seedItem;

    public BlockMillCrops(BlockBehaviour.Properties props, int maxAge, Supplier<? extends ItemLike> seedItem) {
        super(props);
        this.maxAge = maxAge;
        this.seedItem = seedItem;
    }

    public BlockMillCrops(BlockBehaviour.Properties props) {
        this(props, 7, () -> net.minecraft.world.item.Items.WHEAT_SEEDS);
    }

    @Override
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seedItem.get();
    }
}
