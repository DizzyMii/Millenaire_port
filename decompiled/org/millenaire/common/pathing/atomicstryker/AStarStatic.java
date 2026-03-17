/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockWall
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.init.Blocks
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package org.millenaire.common.pathing.atomicstryker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockWall;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.millenaire.common.block.BlockMillWall;
import org.millenaire.common.pathing.atomicstryker.AS_PathEntity;
import org.millenaire.common.pathing.atomicstryker.AS_PathPoint;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.BlockStateUtilities;
import org.millenaire.common.utilities.ThreadSafeUtilities;

public class AStarStatic {
    static final int[][] candidates = new int[][]{{0, 0, -1, 1}, {0, 0, 1, 1}, {0, 1, 0, 1}, {1, 0, 0, 1}, {-1, 0, 0, 1}, {1, 1, 0, 2}, {-1, 1, 0, 2}, {0, 1, 1, 2}, {0, 1, -1, 2}, {1, -1, 0, 1}, {-1, -1, 0, 1}, {0, -1, 1, 1}, {0, -1, -1, 1}};
    static final int[][] candidates_allowdrops = new int[][]{{0, 0, -1, 1}, {0, 0, 1, 1}, {1, 0, 0, 1}, {-1, 0, 0, 1}, {1, 1, 0, 2}, {-1, 1, 0, 2}, {0, 1, 1, 2}, {0, 1, -1, 2}, {1, -1, 0, 1}, {-1, -1, 0, 1}, {0, -1, 1, 1}, {0, -1, -1, 1}, {1, -2, 0, 1}, {-1, -2, 0, 1}, {0, -2, 1, 1}, {0, -2, -1, 1}};

    public static AStarNode[] getAccessNodesSorted(World world, int workerX, int workerY, int workerZ, int posX, int posY, int posZ, AStarConfig config) throws ThreadSafeUtilities.ChunkAccessException {
        AStarNode check;
        ArrayList<AStarNode> resultList = new ArrayList<AStarNode>();
        for (int xIter = -2; xIter <= 2; ++xIter) {
            for (int zIter = -2; zIter <= 2; ++zIter) {
                for (int yIter = -3; yIter <= 2; ++yIter) {
                    check = new AStarNode(posX + xIter, posY + yIter, posZ + zIter, Math.abs(xIter) + Math.abs(yIter), null);
                    if (!AStarStatic.isViable(world, check, 1, config)) continue;
                    resultList.add(check);
                }
            }
        }
        Collections.sort(resultList);
        int count = 0;
        AStarNode[] returnVal = new AStarNode[resultList.size()];
        while (!resultList.isEmpty() && (check = (AStarNode)resultList.get(0)) != null) {
            returnVal[count] = check;
            resultList.remove(0);
            ++count;
        }
        return returnVal;
    }

    public static double getDistanceBetweenCoords(int x, int y, int z, int posX, int posY, int posZ) {
        return Math.sqrt(Math.pow(x - posX, 2.0) + Math.pow(y - posY, 2.0) + Math.pow(z - posZ, 2.0));
    }

    public static double getDistanceBetweenNodes(AStarNode a, AStarNode b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2.0) + Math.pow(a.y - b.y, 2.0) + Math.pow(a.z - b.z, 2.0));
    }

    public static double getEntityLandSpeed(EntityLiving entLiving) {
        return Math.sqrt(entLiving.field_70159_w * entLiving.field_70159_w + entLiving.field_70179_y * entLiving.field_70179_y);
    }

    public static int getIntCoordFromDoubleCoord(double input) {
        return MathHelper.func_76128_c((double)input);
    }

    public static boolean isLadder(World world, Block b, int x, int y, int z) {
        if (b != null) {
            return b.isLadder(b.func_176223_P(), (IBlockAccess)world, new BlockPos(x, y, z), null);
        }
        return false;
    }

    public static boolean isPassableBlock(World world, int ix, int iy, int iz, AStarConfig config) throws ThreadSafeUtilities.ChunkAccessException {
        Block blockBelow;
        IBlockState blockState = ThreadSafeUtilities.getBlockState(world, ix, iy, iz);
        Block block = blockState.func_177230_c();
        if (iy > 0 && (BlockItemUtilities.isFence(blockBelow = ThreadSafeUtilities.getBlock(world, ix, iy - 1, iz)) || blockBelow == Blocks.field_150411_aY || blockBelow == Blocks.field_150386_bk || blockBelow instanceof BlockWall || blockBelow instanceof BlockMillWall)) {
            return false;
        }
        if (block != null) {
            if (!(config.canSwim || block != Blocks.field_150355_j && block != Blocks.field_150358_i)) {
                return false;
            }
            if (config.canUseDoors && (BlockItemUtilities.isWoodenDoor(block) || BlockItemUtilities.isFenceGate(block))) {
                return true;
            }
            if (config.canClearLeaves && block instanceof BlockLeaves) {
                if (block == Blocks.field_150362_t || block == Blocks.field_150361_u) {
                    if (((Boolean)blockState.func_177229_b((IProperty)BlockLeaves.field_176237_a)).booleanValue()) {
                        return true;
                    }
                } else if (BlockStateUtilities.hasPropertyByName(blockState, "decayable")) {
                    if (((Boolean)blockState.func_177229_b((IProperty)BlockLeaves.field_176237_a)).booleanValue()) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
            return ThreadSafeUtilities.isBlockPassable(block, world, ix, iy, iz);
        }
        return true;
    }

    public static boolean isViable(World world, AStarNode target, int yoffset, AStarConfig config) throws ThreadSafeUtilities.ChunkAccessException {
        return AStarStatic.isViable(world, target.x, target.y, target.z, yoffset, config);
    }

    public static boolean isViable(World world, int x, int y, int z, int yoffset, AStarConfig config) throws ThreadSafeUtilities.ChunkAccessException {
        Block block = ThreadSafeUtilities.getBlock(world, x, y, z);
        Block blockBelow = ThreadSafeUtilities.getBlock(world, x, y - 1, z);
        if (block == Blocks.field_150468_ap && AStarStatic.isPassableBlock(world, x, y + 1, z, config)) {
            return true;
        }
        if (!AStarStatic.isPassableBlock(world, x, y, z, config) || !AStarStatic.isPassableBlock(world, x, y + 1, z, config)) {
            return false;
        }
        if (blockBelow == Blocks.field_150355_j || blockBelow == Blocks.field_150358_i) {
            return false;
        }
        if (AStarStatic.isPassableBlock(world, x, y - 1, z, config)) {
            if (!config.canSwim) {
                return false;
            }
            if (block != Blocks.field_150355_j && block != Blocks.field_150358_i) {
                return false;
            }
        }
        if (yoffset < 0) {
            yoffset *= -1;
        }
        for (int ycheckhigher = 1; ycheckhigher <= yoffset; ++ycheckhigher) {
            if (AStarStatic.isPassableBlock(world, x, y + yoffset, z, config)) continue;
            return false;
        }
        return true;
    }

    public static AS_PathEntity translateAStarPathtoPathEntity(World world, List<AStarNode> input, AStarConfig config) throws ThreadSafeUtilities.ChunkAccessException {
        if (!config.canTakeDiagonals) {
            List<AStarNode> oldInput = input;
            input = new ArrayList<AStarNode>();
            for (int i = 0; i < oldInput.size() - 1; ++i) {
                AStarNode newNode;
                input.add(oldInput.get(i));
                if (oldInput.get((int)i).x == oldInput.get((int)(i + 1)).x || oldInput.get((int)i).z == oldInput.get((int)(i + 1)).z || oldInput.get((int)i).y != oldInput.get((int)(i + 1)).y) continue;
                if (!AStarStatic.isPassableBlock(world, oldInput.get((int)i).x, oldInput.get((int)i).y - 1, oldInput.get((int)(i + 1)).z, config) && AStarStatic.isPassableBlock(world, oldInput.get((int)i).x, oldInput.get((int)i).y, oldInput.get((int)(i + 1)).z, config) && AStarStatic.isPassableBlock(world, oldInput.get((int)i).x, oldInput.get((int)i).y + 1, oldInput.get((int)(i + 1)).z, config)) {
                    newNode = new AStarNode(oldInput.get((int)i).x, oldInput.get((int)i).y, oldInput.get((int)(i + 1)).z, 0, null);
                    input.add(newNode);
                    continue;
                }
                newNode = new AStarNode(oldInput.get((int)(i + 1)).x, oldInput.get((int)i).y, oldInput.get((int)i).z, 0, null);
                input.add(newNode);
            }
        }
        PathPoint[] points = new AS_PathPoint[input.size()];
        int i = 0;
        int size = input.size();
        while (size > 0) {
            AStarNode reading = input.get(size - 1);
            points[i] = new AS_PathPoint(reading.x, reading.y, reading.z);
            points[i].setIndex(i);
            points[i].setTotalPathDistance(i);
            points[i].setDistanceToNext(1.0f);
            points[i].setDistanceToTarget(size);
            if (i > 0) {
                points[i].setPrevious(points[i - 1]);
            }
            input.remove(size - 1);
            --size;
            ++i;
        }
        return new AS_PathEntity(points);
    }
}

