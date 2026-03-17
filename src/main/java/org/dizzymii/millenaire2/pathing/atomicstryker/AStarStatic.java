package org.dizzymii.millenaire2.pathing.atomicstryker;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.dizzymii.millenaire2.util.MillLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Static utility methods for A* pathfinding — node viability, distance, candidate generation.
 * Ported from org.millenaire.common.pathing.atomicstryker.AStarStatic (Forge 1.12.2).
 */
public class AStarStatic {

    static final int[][] candidates = {
            {0, 0, -1, 1}, {0, 0, 1, 1}, {0, 1, 0, 1}, {1, 0, 0, 1}, {-1, 0, 0, 1},
            {1, 1, 0, 2}, {-1, 1, 0, 2}, {0, 1, 1, 2}, {0, 1, -1, 2},
            {1, -1, 0, 1}, {-1, -1, 0, 1}, {0, -1, 1, 1}, {0, -1, -1, 1}
    };

    static final int[][] candidates_allowdrops = {
            {0, 0, -1, 1}, {0, 0, 1, 1}, {1, 0, 0, 1}, {-1, 0, 0, 1},
            {1, 1, 0, 2}, {-1, 1, 0, 2}, {0, 1, 1, 2}, {0, 1, -1, 2},
            {1, -1, 0, 1}, {-1, -1, 0, 1}, {0, -1, 1, 1}, {0, -1, -1, 1},
            {1, -2, 0, 1}, {-1, -2, 0, 1}, {0, -2, 1, 1}, {0, -2, -1, 1}
    };

    private static final int ENTITY_HEIGHT = 2;

    public static double getDistanceBetweenNodes(AStarNode a, AStarNode b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double dz = a.z - b.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static boolean isViable(Level level, AStarNode node, int entityHeight, AStarConfig config) {
        BlockPos below = new BlockPos(node.x, node.y - 1, node.z);
        BlockState belowState = level.getBlockState(below);

        // Must have solid ground or be on a ladder/climbable
        boolean grounded = belowState.isSolid();
        boolean onClimbable = isBlockClimbable(level, new BlockPos(node.x, node.y, node.z));
        boolean inWater = config.canSwim && isBlockWater(level, new BlockPos(node.x, node.y, node.z));

        if (!grounded && !onClimbable && !inWater) return false;

        // Enough headroom
        for (int h = 0; h < entityHeight; h++) {
            BlockPos check = new BlockPos(node.x, node.y + h, node.z);
            if (!isBlockPassable(level, check, config)) return false;
        }
        return true;
    }

    /**
     * Check if a block position is passable (can walk through).
     * Updated for 1.21.1 block API — handles slabs, stairs, signs,
     * buttons, pressure plates, carpet, snow layers, scaffolding,
     * waterlogged blocks, and campfires.
     */
    public static boolean isBlockPassable(Level level, BlockPos pos, AStarConfig config) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        // Air is always passable
        if (state.isAir()) return true;

        // Doors are passable if config allows
        if (block instanceof DoorBlock) return config.canUseDoors;

        // Fence gates are passable (villagers can open them)
        if (block instanceof FenceGateBlock) return true;

        // Trapdoors are passable
        if (block instanceof TrapDoorBlock) return true;

        // Climbable blocks are passable
        if (block instanceof LadderBlock || block instanceof VineBlock) return true;

        // Scaffolding is passable (climbable)
        if (block instanceof ScaffoldingBlock) return true;

        // Signs are passable (no collision)
        if (block instanceof SignBlock || block instanceof WallSignBlock) return true;

        // Buttons and pressure plates are passable (negligible collision)
        if (block instanceof ButtonBlock || block instanceof PressurePlateBlock) return true;

        // Carpet is passable (thin layer, no real collision)
        if (block instanceof CarpetBlock) return true;

        // Snow layers: passable if thin (< 4 layers), blocked if thick
        if (block instanceof SnowLayerBlock) {
            int layers = state.getValue(SnowLayerBlock.LAYERS);
            return layers < 4;
        }

        // Campfires block movement (they damage entities)
        if (block instanceof CampfireBlock) return false;

        // Water / waterlogged blocks are passable if can swim
        if (isBlockWater(level, pos)) return config.canSwim;

        // Leaves can be cleared
        if (config.canClearLeaves && state.is(BlockTags.LEAVES)) return true;

        // Fences and walls block movement (1.5 block height)
        if (block instanceof FenceBlock) return false;
        if (state.is(BlockTags.WALLS)) return false;

        // Top-half slabs block passage; bottom-half and double slabs are solid ground
        if (block instanceof SlabBlock) {
            SlabType type = state.getValue(SlabBlock.TYPE);
            return type == SlabType.BOTTOM;
        }

        // Stairs: top-half stairs block passage at this height
        if (block instanceof StairBlock) {
            return state.getValue(StairBlock.HALF) == Half.BOTTOM;
        }

        // Non-solid blocks (flowers, tall grass, etc.) are passable
        return !state.isSolid();
    }

    /**
     * Check if a block is climbable (ladder, vine).
     */
    public static boolean isBlockClimbable(Level level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        return block instanceof LadderBlock || block instanceof VineBlock;
    }

    /**
     * Check if a block is water or waterlogged.
     */
    public static boolean isBlockWater(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() == Blocks.WATER) return true;
        FluidState fluid = state.getFluidState();
        return fluid.is(Fluids.WATER) || fluid.is(Fluids.FLOWING_WATER);
    }

    /**
     * Get valid neighbour nodes from the current node.
     */
    public static List<AStarNode> getNeighbours(Level level, AStarNode current, AStarNode target,
                                                  AStarConfig config) {
        List<AStarNode> neighbours = new ArrayList<>();
        int[][] cands = config.allowDropping ? candidates_allowdrops : candidates;

        for (int[] c : cands) {
            int nx = current.x + c[0];
            int ny = current.y + c[1];
            int nz = current.z + c[2];
            int cost = c[3];

            AStarNode neighbour = new AStarNode(nx, ny, nz, current.getG() + cost, current, target);
            if (isViable(level, neighbour, ENTITY_HEIGHT, config)) {
                neighbours.add(neighbour);
            }
        }

        // Diagonal moves if allowed
        if (config.canTakeDiagonals) {
            int[][] diags = {
                    {1, 0, 1, 2}, {1, 0, -1, 2}, {-1, 0, 1, 2}, {-1, 0, -1, 2}
            };
            for (int[] d : diags) {
                int nx = current.x + d[0];
                int ny = current.y + d[1];
                int nz = current.z + d[2];
                int cost = d[3];

                AStarNode neighbour = new AStarNode(nx, ny, nz, current.getG() + cost, current, target);
                if (isViable(level, neighbour, ENTITY_HEIGHT, config)) {
                    neighbours.add(neighbour);
                }
            }
        }

        return neighbours;
    }

    public static AStarNode[] getAccessNodesSorted(Level level, int workerX, int workerY, int workerZ,
                                                    int posX, int posY, int posZ, AStarConfig config) {
        List<AStarNode> resultList = new ArrayList<>();
        for (int xIter = -2; xIter <= 2; xIter++) {
            for (int zIter = -2; zIter <= 2; zIter++) {
                for (int yIter = -3; yIter <= 2; yIter++) {
                    AStarNode check = new AStarNode(posX + xIter, posY + yIter, posZ + zIter,
                            Math.abs(xIter) + Math.abs(yIter), null);
                    if (isViable(level, check, ENTITY_HEIGHT, config)) {
                        resultList.add(check);
                    }
                }
            }
        }
        Collections.sort(resultList);
        return resultList.toArray(new AStarNode[0]);
    }

    /**
     * Reconstruct path from goal node back to start by following parent chain.
     */
    public static List<AStarNode> reconstructPath(AStarNode goalNode) {
        List<AStarNode> path = new ArrayList<>();
        AStarNode current = goalNode;
        while (current != null) {
            path.add(0, current);
            current = current.parent;
        }
        return path;
    }

    /**
     * Validation utility: logs passability results for common block types.
     * Call once during server start to verify the 1.21.1 block API integration
     * is producing correct results.
     */
    public static void auditPassability(Level level, BlockPos origin) {
        AStarConfig defaultCfg = new AStarConfig(true, false, false, true, true);
        Block[] testBlocks = {
                Blocks.AIR, Blocks.STONE, Blocks.OAK_PLANKS, Blocks.OAK_DOOR,
                Blocks.OAK_FENCE, Blocks.OAK_FENCE_GATE, Blocks.OAK_TRAPDOOR,
                Blocks.LADDER, Blocks.VINE, Blocks.WATER, Blocks.GLASS_PANE,
                Blocks.OAK_SLAB, Blocks.OAK_STAIRS, Blocks.SNOW,
                Blocks.OAK_SIGN, Blocks.OAK_BUTTON, Blocks.STONE_PRESSURE_PLATE,
                Blocks.WHITE_CARPET, Blocks.SCAFFOLDING, Blocks.CAMPFIRE,
                Blocks.COBBLESTONE_WALL, Blocks.IRON_BARS, Blocks.OAK_LEAVES
        };
        StringBuilder sb = new StringBuilder("AStarStatic passability audit:\n");
        for (Block b : testBlocks) {
            BlockState state = b.defaultBlockState();
            level.setBlock(origin, state, 2);
            boolean passable = isBlockPassable(level, origin, defaultCfg);
            sb.append(String.format("  %-25s -> %s%n",
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(b), passable ? "PASS" : "BLOCK"));
        }
        level.setBlock(origin, Blocks.AIR.defaultBlockState(), 2);
        MillLog.minor(null, sb.toString());
    }
}
