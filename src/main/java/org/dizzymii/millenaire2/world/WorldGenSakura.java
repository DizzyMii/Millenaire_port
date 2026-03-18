package org.dizzymii.millenaire2.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Custom tree generator for sakura (cherry blossom) trees — tall with spreading canopy.
 * Ported from org.millenaire.common.world.WorldGenSakura (Forge 1.12.2).
 */
public class WorldGenSakura {

    public boolean generate(ServerLevel level, RandomSource random, BlockPos pos) {
        int trunkHeight = 5 + random.nextInt(3);

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

        // Wide, spreading canopy
        for (int dy = trunkHeight - 2; dy <= trunkHeight + 1; dy++) {
            int radius = (dy <= trunkHeight - 1) ? 3 : 2;
            if (dy == trunkHeight + 1) radius = 1;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx == 0 && dz == 0 && dy < trunkHeight) continue;
                    if (dx * dx + dz * dz > radius * radius + 1) continue;
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
