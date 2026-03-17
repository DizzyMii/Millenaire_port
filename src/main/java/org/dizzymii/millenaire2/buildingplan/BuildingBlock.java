package org.dizzymii.millenaire2.buildingplan;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Represents a single block placement within a building plan.
 * Handles rotation, special block logic (doors, beds, chests), and placement.
 * Ported from org.millenaire.common.buildingplan.BuildingBlock (Forge 1.12.2).
 */
public class BuildingBlock {

    @Nullable public PointType pointType;
    @Nullable public BlockState blockState;
    public int x, y, z;
    public boolean secondStep = false;

    public BuildingBlock() {}

    public BuildingBlock(PointType pt, int x, int y, int z) {
        this.pointType = pt;
        this.blockState = pt.getBlockState();
        this.x = x;
        this.y = y;
        this.z = z;
        this.secondStep = pt.secondStep;
    }

    public BlockPos getBlockPos(BlockPos origin, int orientation) {
        // TODO: Apply rotation based on building orientation (0/1/2/3)
        return origin.offset(x, y, z);
    }

    public boolean place(Level level, BlockPos origin, int orientation) {
        if (blockState == null) return false;
        BlockPos pos = getBlockPos(origin, orientation);
        // TODO: Handle special block placement (doors, beds, signs, chests, banners)
        level.setBlock(pos, blockState, 3);
        return true;
    }

    // TODO: readFromStream, writeToStream, rotateBlockState, handleSpecialBlocks
}
