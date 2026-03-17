/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSlab
 *  net.minecraft.block.BlockStairs
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.world.World
 */
package org.millenaire.common.utilities;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.millenaire.common.block.IBlockPath;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingBlock;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;

public class PathUtilities {
    private static final boolean PATH_RAISE = false;
    private static final boolean PATH_DROP = true;

    private static boolean attemptPathBuild(Building th, World world, List<BuildingBlock> pathPoints, Point p, Block pathBlock, int pathMeta) {
        IBlockState blockState = p.getBlockActualState(world);
        if (th.isPointProtectedFromPathBuilding(p)) {
            return false;
        }
        if (p.getRelative(0.0, 2.0, 0.0).isBlockPassable(world) && p.getAbove().isBlockPassable(world) && PathUtilities.canPathBeBuiltHere(blockState)) {
            pathPoints.add(new BuildingBlock(p, pathBlock, pathMeta));
            return true;
        }
        return false;
    }

    public static List<BuildingBlock> buildPath(Building th, List<AStarNode> path, Block pathBlock, int pathMeta, int pathWidth) {
        AStarNode node;
        int ip;
        ArrayList<BuildingBlock> pathPoints = new ArrayList<BuildingBlock>();
        boolean lastNodeHalfSlab = false;
        boolean[] pathShouldBuild = new boolean[path.size()];
        for (ip = 0; ip < path.size(); ++ip) {
            pathShouldBuild[ip] = true;
        }
        for (ip = 0; ip < path.size(); ++ip) {
            node = path.get(ip);
            Point p = new Point(node);
            BuildingLocation l = th.getLocationAtCoordPlanar(p);
            if (l == null) continue;
            if (ip == 0) {
                pathShouldBuild[ip] = true;
                PathUtilities.clearPathForward(path, pathShouldBuild, th, l, ip);
                continue;
            }
            if (ip == path.size() - 1) {
                pathShouldBuild[ip] = true;
                PathUtilities.clearPathBackward(path, pathShouldBuild, th, l, ip);
                continue;
            }
            boolean stablePath = PathUtilities.isPointOnStablePath(p, th.world);
            if (!stablePath) continue;
            pathShouldBuild[ip] = true;
            PathUtilities.clearPathBackward(path, pathShouldBuild, th, l, ip);
            PathUtilities.clearPathForward(path, pathShouldBuild, th, l, ip);
        }
        for (ip = 0; ip < path.size(); ++ip) {
            if (pathShouldBuild[ip]) {
                Point p;
                node = path.get(ip);
                AStarNode lastNode = null;
                AStarNode nextNode = null;
                if (ip > 0) {
                    lastNode = path.get(ip - 1);
                }
                if (ip + 1 < path.size()) {
                    nextNode = path.get(ip + 1);
                }
                boolean halfSlab = false;
                if (lastNode != null && nextNode != null) {
                    p = new Point(node);
                    Point nextp = new Point(nextNode);
                    Point lastp = new Point(lastNode);
                    if (!PathUtilities.isStairsOrSlabOrChest(th.world, nextp.getBelow()) && !PathUtilities.isStairsOrSlabOrChest(th.world, lastp.getBelow())) {
                        if (p.x == lastp.x && p.x == nextp.x || p.z != lastp.z || p.z != nextp.z) {
                            // empty if block
                        }
                        if (lastNode.y == nextNode.y && node.y < lastNode.y && p.getRelative(0.0, lastNode.y - node.y, 0.0).isBlockPassable(th.world) && p.getRelative(0.0, lastNode.y - node.y + 1, 0.0).isBlockPassable(th.world)) {
                            halfSlab = true;
                        } else if (!lastNodeHalfSlab && node.y == lastNode.y && node.y > nextNode.y) {
                            halfSlab = true;
                        } else if (!lastNodeHalfSlab && node.y == nextNode.y && node.y > lastNode.y) {
                            halfSlab = true;
                        }
                    } else {
                        Block block = p.getBelow().getBlock(th.world);
                        if (BlockItemUtilities.isPathSlab(block)) {
                            halfSlab = true;
                        }
                    }
                }
                p = new Point(node).getBelow();
                Block nodePathBlock = pathBlock;
                if (BlockItemUtilities.isPath(nodePathBlock) && halfSlab) {
                    nodePathBlock = ((IBlockPath)nodePathBlock).getSingleSlab();
                }
                PathUtilities.attemptPathBuild(th, th.world, pathPoints, p, nodePathBlock, pathMeta);
                if (lastNode != null) {
                    int dx = p.getiX() - lastNode.x;
                    int dz = p.getiZ() - lastNode.z;
                    int nbPass = 1;
                    if (dx != 0 && dz != 0) {
                        nbPass = 2;
                    }
                    for (int i = 0; i < nbPass; ++i) {
                        boolean success;
                        int direction = i == 0 ? 1 : -1;
                        Point secondPoint = null;
                        Point secondPointAlternate = null;
                        Point thirdPoint = null;
                        if (pathWidth > 1) {
                            if (dx == 0 && direction == 1) {
                                secondPoint = p.getRelative(direction, 0.0, 0.0);
                                secondPointAlternate = p.getRelative(-direction, 0.0, 0.0);
                            } else if (dz == 0 && direction == 1) {
                                secondPoint = p.getRelative(0.0, 0.0, direction);
                                secondPointAlternate = p.getRelative(0.0, 0.0, -direction);
                            } else {
                                secondPoint = p.getRelative(dx * direction, 0.0, 0.0);
                                thirdPoint = p.getRelative(0.0, 0.0, dz * direction);
                            }
                        } else if (dx != 0 && dz != 0) {
                            secondPoint = p.getRelative(dx * direction, 0.0, 0.0);
                            secondPointAlternate = p.getRelative(0.0, 0.0, dz * direction);
                        }
                        if (secondPoint != null && !(success = PathUtilities.attemptPathBuild(th, th.world, pathPoints, secondPoint, nodePathBlock, pathMeta)) && secondPointAlternate != null) {
                            PathUtilities.attemptPathBuild(th, th.world, pathPoints, secondPointAlternate, nodePathBlock, pathMeta);
                        }
                        if (thirdPoint == null) continue;
                        PathUtilities.attemptPathBuild(th, th.world, pathPoints, thirdPoint, nodePathBlock, pathMeta);
                    }
                }
                lastNodeHalfSlab = halfSlab;
                continue;
            }
            lastNodeHalfSlab = false;
        }
        return pathPoints;
    }

    public static boolean canPathBeBuiltHere(IBlockState blockState) {
        Block block = blockState.func_177230_c();
        if (BlockItemUtilities.isPath(block)) {
            return (Boolean)blockState.func_177229_b((IProperty)IBlockPath.STABLE) == false;
        }
        return BlockItemUtilities.isBlockPathReplaceable(block) || BlockItemUtilities.isBlockDecorativePlant(block);
    }

    public static boolean canPathBeBuiltHereOld(IBlockState blockState) {
        Block block = blockState.func_177230_c();
        if (block == Blocks.field_150346_d || block == Blocks.field_150349_c || block == Blocks.field_150354_m || block == Blocks.field_150351_n || block == Blocks.field_150405_ch || BlockItemUtilities.isBlockDecorativePlant(block)) {
            return true;
        }
        return BlockItemUtilities.isPath(block) && (Boolean)blockState.func_177229_b((IProperty)IBlockPath.STABLE) == false;
    }

    private static void clearPathBackward(List<AStarNode> path, boolean[] pathShouldBuild, Building th, BuildingLocation l, int index) {
        BuildingLocation l2;
        Point np;
        int i;
        boolean exit = false;
        boolean leadsToBorder = false;
        for (i = index - 1; i >= 0 && !exit; --i) {
            np = new Point(path.get(i));
            l2 = th.getLocationAtCoordPlanar(np);
            if (l2 != l) {
                leadsToBorder = true;
                exit = true;
                continue;
            }
            if (!PathUtilities.isPointOnStablePath(np, th.world)) continue;
            exit = true;
        }
        if (!leadsToBorder) {
            exit = false;
            for (i = index - 1; i >= 0 && !exit; --i) {
                np = new Point(path.get(i));
                l2 = th.getLocationAtCoordPlanar(np);
                if (l2 != l) {
                    exit = true;
                    continue;
                }
                if (PathUtilities.isPointOnStablePath(np, th.world)) {
                    exit = true;
                    continue;
                }
                pathShouldBuild[i] = false;
            }
        }
    }

    public static void clearPathBlock(Point p, World world) {
        IBlockState bs = p.getBlockActualState(world);
        if (bs.func_177230_c() instanceof IBlockPath && !((Boolean)bs.func_177229_b((IProperty)IBlockPath.STABLE)).booleanValue()) {
            IBlockState blockStateBelow = p.getBelow().getBlockActualState(world);
            if (WorldUtilities.getBlockStateValidGround(blockStateBelow, true) != null) {
                p.setBlockState(world, WorldUtilities.getBlockStateValidGround(blockStateBelow, true));
            } else {
                p.setBlock(world, Blocks.field_150346_d, 0, true, false);
            }
        }
    }

    private static void clearPathForward(List<AStarNode> path, boolean[] pathShouldBuild, Building th, BuildingLocation l, int index) {
        BuildingLocation l2;
        Point np;
        int i;
        boolean exit = false;
        boolean leadsToBorder = false;
        for (i = index + 1; i < path.size() && !exit; ++i) {
            np = new Point(path.get(i));
            l2 = th.getLocationAtCoordPlanar(np);
            if (l2 != l) {
                leadsToBorder = true;
                exit = true;
                continue;
            }
            if (!PathUtilities.isPointOnStablePath(np, th.world)) continue;
            exit = true;
        }
        if (!leadsToBorder) {
            exit = false;
            for (i = index + 1; i < path.size() && !exit; ++i) {
                np = new Point(path.get(i));
                l2 = th.getLocationAtCoordPlanar(np);
                if (l2 != l) {
                    exit = true;
                    continue;
                }
                if (PathUtilities.isPointOnStablePath(np, th.world)) {
                    exit = true;
                    continue;
                }
                pathShouldBuild[i] = false;
            }
        }
    }

    public static boolean isPointOnStablePath(Point p, World world) {
        IBlockState bs;
        Block block = p.getBlock(world);
        if (block instanceof IBlockPath && ((Boolean)(bs = p.getBlockActualState(world)).func_177229_b((IProperty)IBlockPath.STABLE)).booleanValue()) {
            return true;
        }
        block = p.getBelow().getBlock(world);
        return block instanceof IBlockPath && (Boolean)(bs = p.getBelow().getBlockActualState(world)).func_177229_b((IProperty)IBlockPath.STABLE) != false;
    }

    private static boolean isStairsOrSlabOrChest(World world, Point p) {
        Block block = p.getBlock(world);
        if (block == Blocks.field_150486_ae || block == MillBlocks.LOCKED_CHEST || block == Blocks.field_150462_ai || block == Blocks.field_150460_al || block == Blocks.field_150470_am) {
            return true;
        }
        if (block instanceof BlockStairs) {
            return true;
        }
        return block instanceof BlockSlab && !block.func_176223_P().func_185914_p();
    }
}

