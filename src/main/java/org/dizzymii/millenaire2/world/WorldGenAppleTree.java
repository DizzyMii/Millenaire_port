package org.dizzymii.millenaire2.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.block.MillBlocks;

/**
 * Custom tree generator for apple trees.
 * Generates a small oak-style tree with fruit leaves from MillBlocks.
 * Ported from org.millenaire.common.world.WorldGenAppleTree (Forge 1.12.2).
 */
public class WorldGenAppleTree {

    private static final int MIN_TRUNK = 4;
    private static final int MAX_TRUNK = 6;

    public boolean generate(ServerLevel level, RandomSource random, BlockPos pos) {
        int trunkHeight = MIN_TRUNK + random.nextInt(MAX_TRUNK - MIN_TRUNK + 1);

        // Check space
        for (int y = 0; y <= trunkHeight + 2; y++) {
            if (!level.isEmptyBlock(pos.above(y)) && !level.getBlockState(pos.above(y)).canBeReplaced()) {
                return false;
            }
        }

        BlockState log = Blocks.OAK_LOG.defaultBlockState();
        BlockState leaves = MillBlocks.FRUIT_LEAVES != null
                ? MillBlocks.FRUIT_LEAVES.get().defaultBlockState()
                : Blocks.OAK_LEAVES.defaultBlockState();

        // Place trunk
        for (int y = 0; y < trunkHeight; y++) {
            level.setBlock(pos.above(y), log, 3);
        }

        // Place canopy (sphere-ish)
        int canopyBase = trunkHeight - 2;
        for (int dy = canopyBase; dy <= trunkHeight + 1; dy++) {
            int radius = (dy <= trunkHeight - 1) ? 2 : 1;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx == 0 && dz == 0 && dy < trunkHeight) continue; // trunk space
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
