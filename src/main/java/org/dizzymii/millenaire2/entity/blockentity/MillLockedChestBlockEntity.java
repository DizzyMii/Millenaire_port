package org.dizzymii.millenaire2.entity.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillEntities;

/**
 * Block entity for the Locked Chest (village storage).
 * Ported from org.millenaire.common.entity.TileEntityLockedChest.
 * TODO: Implement inventory and lock logic in a later phase.
 */
public class MillLockedChestBlockEntity extends BlockEntity {

    public MillLockedChestBlockEntity(BlockPos pos, BlockState state) {
        super(MillEntities.LOCKED_CHEST_BE.get(), pos, state);
    }
}
