package org.dizzymii.millenaire2.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Thread-safe world access utilities — validates chunk loading before block access.
 * Ported from org.millenaire.common.utilities.ThreadSafeUtilities (Forge 1.12.2).
 */
public final class ThreadSafeUtilities {

    private ThreadSafeUtilities() {}

    public static Block getBlock(Level level, int x, int y, int z) throws ChunkAccessException {
        validateCoords(level, x, z);
        return level.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static BlockState getBlockState(Level level, int x, int y, int z) throws ChunkAccessException {
        validateCoords(level, x, z);
        return level.getBlockState(new BlockPos(x, y, z));
    }

    public static boolean isChunkLoaded(Level level, int x, int z) {
        return level.hasChunk(x >> 4, z >> 4);
    }

    private static void validateCoords(Level level, int x, int z) throws ChunkAccessException {
        if (!level.hasChunk(x >> 4, z >> 4)) {
            throw new ChunkAccessException(
                    "Attempting to access coordinate in unloaded chunk at " + x + "/" + z, x, z);
        }
    }

    public static class ChunkAccessException extends Exception {
        public final int x;
        public final int z;

        public ChunkAccessException(String message, int x, int z) {
            super(message);
            this.x = x;
            this.z = z;
        }
    }
}
