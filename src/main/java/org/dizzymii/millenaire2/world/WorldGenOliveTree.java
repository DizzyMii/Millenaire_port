package org.dizzymii.millenaire2.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.init.ModBlocks;

/**
 * Custom tree generator for olive trees — shorter, bushier canopy.
 * Ported from org.millenaire.common.world.WorldGenOliveTree (Forge 1.12.2).
 */
public class WorldGenOliveTree {

    public boolean generate(ServerLevel level, RandomSource random, BlockPos pos) {
        int trunkHeight = 3 + random.nextInt(2);

        for (int y = 0; y <= trunkHeight + 2; y++) {
            if (!level.isEmptyBlock(pos.above(y)) && !level.getBlockState(pos.above(y)).canBeReplaced()) {
                return false;
            }
        }

        BlockState log = Blocks.OAK_LOG.defaultBlockState();
        BlockState leaves = ModBlocks.FRUIT_LEAVES != null
                ? ModBlocks.FRUIT_LEAVES.get().defaultBlockState()
                : Blocks.OAK_LEAVES.defaultBlockState();

        for (int y = 0; y < trunkHeight; y++) {
            level.setBlock(pos.above(y), log, 3);
        }

        // Bushy canopy
        for (int dy = trunkHeight - 1; dy <= trunkHeight + 1; dy++) {
            int radius = 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx == 0 && dz == 0 && dy < trunkHeight) continue;
                    if (Math.abs(dx) + Math.abs(dz) > 3) continue;
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

