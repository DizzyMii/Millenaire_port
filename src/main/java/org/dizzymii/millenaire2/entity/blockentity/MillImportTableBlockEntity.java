package org.dizzymii.millenaire2.entity.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillEntities;

/**
 * Block entity for the Import Table (trade/import interface).
 * Ported from org.millenaire.common.entity.TileEntityImportTable.
 * TODO: Implement import/trade logic in a later phase.
 */
public class MillImportTableBlockEntity extends BlockEntity {

    public MillImportTableBlockEntity(BlockPos pos, BlockState state) {
        super(MillEntities.IMPORT_TABLE_BE.get(), pos, state);
    }
}
