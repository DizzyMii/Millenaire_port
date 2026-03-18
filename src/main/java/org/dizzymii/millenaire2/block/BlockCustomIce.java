package org.dizzymii.millenaire2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Custom ice block used in Millenaire villages.
 * Unlike vanilla ice, does not melt from light — purely decorative.
 */
public class BlockCustomIce extends HalfTransparentBlock {
    public BlockCustomIce(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // No melting — intentionally empty to override vanilla ice behavior
    }
}
