package org.dizzymii.millenaire2.buildingplan;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.util.BlockItemUtilities;

/**
 * Clears trees from a building construction area.
 * Ported from org.millenaire.common.buildingplan.TreeClearer (Forge 1.12.2).
 */
public final class TreeClearer {

    private TreeClearer() {}

    public static int clearTreesInArea(Level level, BlockPos min, BlockPos max) {
        int cleared = 0;
        // TODO: Iterate area, find log/leaf blocks, remove them,
        //       optionally drop items or add to village inventory
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (BlockItemUtilities.isTreeBlock(level.getBlockState(pos).getBlock())) {
                        level.destroyBlock(pos, true);
                        cleared++;
                    }
                }
            }
        }
        return cleared;
    }
}
