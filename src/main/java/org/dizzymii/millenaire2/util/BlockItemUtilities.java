package org.dizzymii.millenaire2.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.HashSet;
import java.util.Set;

/**
 * Block and item classification utilities for village AI.
 * Ported from org.millenaire.common.utilities.BlockItemUtilities (Forge 1.12.2).
 */
public final class BlockItemUtilities {

    private BlockItemUtilities() {}

    private static final Set<String> FORBIDDEN_BLOCKS = new HashSet<>();
    private static final Set<String> GROUND_BLOCKS = new HashSet<>();
    private static final Set<String> DANGER_BLOCKS = new HashSet<>();
    private static final Set<String> WATER_BLOCKS = new HashSet<>();
    private static final Set<String> PATH_REPLACEABLE_BLOCKS = new HashSet<>();

    public static boolean isBlockSolid(Block block) {
        if (block == null) return false;
        BlockState state = block.defaultBlockState();
        return state.isSolid();
    }

    public static boolean isBlockWalkable(Block block) {
        if (block == null) return false;
        return isBlockSolid(block) && !(block instanceof LiquidBlock);
    }

    public static boolean isBlockLiquid(Block block) {
        return block instanceof LiquidBlock;
    }

    public static boolean isBlockAir(Level level, BlockPos pos) {
        return level.isEmptyBlock(pos);
    }

    public static boolean isBlockWater(Block block) {
        return block == Blocks.WATER;
    }

    public static boolean isBlockDangerous(Block block) {
        return block == Blocks.LAVA || block == Blocks.FIRE || block == Blocks.CACTUS
                || block == Blocks.SWEET_BERRY_BUSH || block == Blocks.WITHER_ROSE;
    }

    public static boolean isGroundBlock(Block block) {
        if (block == null) return false;
        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT
                || block == Blocks.SAND || block == Blocks.GRAVEL
                || block == Blocks.STONE || block == Blocks.COBBLESTONE
                || block == Blocks.CLAY || block == Blocks.FARMLAND
                || block == Blocks.DIRT_PATH;
    }

    public static boolean isTreeBlock(Block block) {
        if (block == null) return false;
        String name = block.getDescriptionId();
        return name.contains("log") || name.contains("leaves") || name.contains("wood");
    }

    // TODO: loadBlockSets(File), checkForHarvestTheft, isPathReplaceable, isForbidden
    // TODO: full material-based classification from decompiled source
}
