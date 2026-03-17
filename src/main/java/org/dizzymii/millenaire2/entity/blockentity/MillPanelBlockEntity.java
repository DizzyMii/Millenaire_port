package org.dizzymii.millenaire2.entity.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.entity.MillEntities;

/**
 * Block entity for the village Panel (info sign).
 * Ported from org.millenaire.common.entity.TileEntityPanel.
 * TODO: Implement panel display logic in a later phase.
 */
public class MillPanelBlockEntity extends BlockEntity {

    public MillPanelBlockEntity(BlockPos pos, BlockState state) {
        super(MillEntities.PANEL_BE.get(), pos, state);
    }
}
