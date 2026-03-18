package org.dizzymii.millenaire2.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Custom tree generator for pistachio trees — small, compact canopy.
 * Ported from org.millenaire.common.world.WorldGenPistachio (Forge 1.12.2).
 */
public class WorldGenPistachio {

    public boolean generate(ServerLevel level, RandomSource random, BlockPos pos) {
        int trunkHeight = 3 + random.nextInt(2);

        for (int y = 0; y <= trunkHeight + 1; y++) {
            if (!level.isEmptyBlock(pos.above(y)) && !level.getBlockState(pos.above(y)).canBeReplaced()) {
                return false;
            }
        }

        BlockState log = Blocks.OAK_LOG.defaultBlockState();
        BlockState leaves = Blocks.OAK_LEAVES.defaultBlockState();

        for (int y = 0; y < trunkHeight; y++) {
            level.setBlock(pos.above(y), log, 3);
        }

        // Compact canopy
        for (int dy = trunkHeight - 1; dy <= trunkHeight; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0 && dy < trunkHeight) continue;
                    BlockPos leafPos = pos.offset(dx, dy, dz);
                    if (level.isEmptyBlock(leafPos)) {
                        level.setBlock(leafPos, leaves, 3);
                    }
                }
            }
        }
        // Top leaf
        BlockPos top = pos.above(trunkHeight + 1);
        if (level.isEmptyBlock(top)) level.setBlock(top, leaves, 3);
        return true;
    }
}
