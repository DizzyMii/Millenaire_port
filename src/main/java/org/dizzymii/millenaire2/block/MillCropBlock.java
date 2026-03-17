package org.dizzymii.millenaire2.block;

import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * Base crop block for Millénaire custom crops (rice, turmeric, maize, cotton, vine).
 * Uses the standard 8-stage growth model from vanilla CropBlock.
 * Specific seed/harvest items will be wired in Phase 3 (items).
 *
 * Ported from org.millenaire.common.block.BlockMillCrops.
 */
public class MillCropBlock extends CropBlock {

    public MillCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
