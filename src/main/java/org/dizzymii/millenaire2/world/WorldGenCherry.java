package org.dizzymii.millenaire2.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Custom tree generator for cherry trees — pink leaves variant.
 * Ported from org.millenaire.common.world.WorldGenCherry (Forge 1.12.2).
 */
public class WorldGenCherry {

    public boolean generate(ServerLevel level, RandomSource random, BlockPos pos) {
        int trunkHeight = 4 + random.nextInt(2);

        for (int y = 0; y <= trunkHeight + 2; y++) {
            if (!level.isEmptyBlock(pos.above(y)) && !level.getBlockState(pos.above(y)).canBeReplaced()) {
                return false;
            }
        }

        BlockState log = Blocks.CHERRY_LOG.defaultBlockState();
        BlockState leaves = Blocks.CHERRY_LEAVES.defaultBlockState();

        for (int y = 0; y < trunkHeight; y++) {
            level.setBlock(pos.above(y), log, 3);
        }

        // Round canopy
        for (int dy = trunkHeight - 2; dy <= trunkHeight + 1; dy++) {
            int radius = (dy == trunkHeight + 1) ? 1 : 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx == 0 && dz == 0 && dy < trunkHeight) continue;
                    if (Math.abs(dx) == radius && Math.abs(dz) == radius && random.nextBoolean()) continue;
                    BlockPos leafPos = pos.offset(dx, dy, dz);
                    if (level.isEmptyBlock(leafPos)) {
                        level.setBlock(leafPos, leaves, 3);
                    }
                }
            }
        }
        return true;
    }
}
