package org.dizzymii.millenaire2.buildingplan;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
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
    public boolean freePlacement = false;

    public BuildingBlock() {}

    public BuildingBlock(PointType pt, int x, int y, int z) {
        this.pointType = pt;
        this.blockState = pt.getBlockState();
        this.x = x;
        this.y = y;
        this.z = z;
        this.secondStep = pt.secondStep;
    }

    /**
     * Get the world position for this block, applying rotation around the origin.
     * Orientation: 0=north (no rotation), 1=west (90° CW), 2=south (180°), 3=east (270°).
     */
    public BlockPos getBlockPos(BlockPos origin, int orientation) {
        int rx, rz;
        switch (orientation) {
            case 1 -> { rx = z;  rz = -x; } // 90° CW
            case 2 -> { rx = -x; rz = -z; } // 180°
            case 3 -> { rx = -z; rz = x;  } // 270° CW
            default -> { rx = x; rz = z;  } // 0° (north)
        }
        return origin.offset(rx, y, rz);
    }

    /**
     * Place this block in the world at the correct rotated position.
     */
    public boolean place(Level level, BlockPos origin, int orientation) {
        if (blockState == null) return false;
        BlockPos pos = getBlockPos(origin, orientation);

        BlockState rotated = rotateBlockState(blockState, orientation);
        level.setBlock(pos, rotated, 3);
        return true;
    }

    /**
     * Rotate a block state to match the building orientation.
     */
    public static BlockState rotateBlockState(BlockState state, int orientation) {
        if (orientation == 0) return state;

        Rotation rotation = switch (orientation) {
            case 1 -> Rotation.CLOCKWISE_90;
            case 2 -> Rotation.CLOCKWISE_180;
            case 3 -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };

        return state.rotate(rotation);
    }
}
