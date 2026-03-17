package org.dizzymii.millenaire2.entity.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillEntities;

/**
 * Block entity for the Fire Pit.
 * Ported from org.millenaire.common.entity.TileEntityFirePit.
 * TODO: Implement cooking/smelting logic in a later phase.
 */
public class MillFirePitBlockEntity extends BlockEntity {

    public MillFirePitBlockEntity(BlockPos pos, BlockState state) {
        super(MillEntities.FIRE_PIT_BE.get(), pos, state);
    }
}
