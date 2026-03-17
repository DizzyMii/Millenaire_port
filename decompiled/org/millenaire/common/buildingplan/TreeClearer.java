/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockLog
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.world.World
 */
package org.millenaire.common.buildingplan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.IntPoint;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.BuildingLocation;

public class TreeClearer {
    private static final int LEAF_CLEARING_Y_END = 30;
    private static final int LEAF_CLEARING_Y_START = -10;
    private static final int LOG_SEARCH_MARGIN = 4;
    private static final int LEAF_CLEAR_MARGIN = 2;
    private static final int NON_DECAY_RANGE = 3;
    public static long cumulatedTimeTreeFinding = 0L;
    public static long cumulatedTimeLeaveDecay = 0L;
    private final World world;
    private final BuildingLocation location;
    private final BuildingPlan plan;
    final Set<IntPoint> pointsTested = new HashSet<IntPoint>();
    final Set<IntPoint> pointsTree = new HashSet<IntPoint>();
    IBlockState decayingLeaves = Blocks.field_150350_a.func_176223_P();
    IBlockState decayedTree = Blocks.field_150350_a.func_176223_P();
    IBlockState nonDecayingLeaves = null;
    IBlockState nonDecayedTree = null;
    boolean testMode = false;

    public TreeClearer(BuildingPlan plan, BuildingLocation location, World world) {
        this.location = location;
        this.world = world;
        this.plan = plan;
        if (this.testMode) {
            this.decayingLeaves = Blocks.field_150359_w.func_176223_P();
            this.decayedTree = Blocks.field_150340_R.func_176223_P();
            this.nonDecayingLeaves = Blocks.field_150399_cn.func_176203_a(15);
            this.nonDecayedTree = Blocks.field_150339_S.func_176223_P();
        }
    }

    public void cleanup() {
        long startTime = System.nanoTime();
        this.findTrees();
        cumulatedTimeTreeFinding += System.nanoTime() - startTime;
        startTime = System.nanoTime();
        this.decayLogsAndLeaves();
        cumulatedTimeLeaveDecay += System.nanoTime() - startTime;
    }

    private void decayLogsAndLeaves() {
        long startTime = System.nanoTime();
        HashSet<IntPoint> nonDecayPosSet = new HashSet<IntPoint>();
        for (IntPoint logPos : this.pointsTree) {
            for (int dx = -3; dx <= 3; ++dx) {
                for (int dz = -3; dz <= 3; ++dz) {
                    for (int dy = -3; dy <= 3; ++dy) {
                        Block block;
                        IntPoint nonDecayPoint = logPos.getRelative(dx, dy, dz);
                        nonDecayPosSet.add(nonDecayPoint);
                        if (!this.testMode || this.isLogBlock(block = nonDecayPoint.getBlock(this.world)) || block.func_176223_P() == this.nonDecayedTree) continue;
                        nonDecayPoint.setBlockState(this.world, Blocks.field_150399_cn.func_176203_a(0));
                    }
                }
            }
            if (this.nonDecayedTree == null) continue;
            logPos.setBlockState(this.world, this.nonDecayedTree);
        }
        int nbLeavesDecayed = 0;
        int nbLeavesSpared = 0;
        int x = this.location.pos.getiX();
        int z = this.location.pos.getiZ();
        int orientation = this.location.orientation;
        int randomWoolColour = MillCommonUtilities.randomInt(16);
        for (int dx = -this.plan.areaToClearLengthBefore - 2; dx < this.plan.length + this.plan.areaToClearLengthAfter + 2; ++dx) {
            for (int dz = -this.plan.areaToClearWidthBefore - 2; dz < this.plan.width + this.plan.areaToClearWidthAfter + 2; ++dz) {
                boolean isZOutsidePlan;
                boolean isXOutsidePlan = dx < 0 || dx > this.plan.length;
                boolean bl = isZOutsidePlan = dz < 0 || dz > this.plan.width;
                if (!isXOutsidePlan && !isZOutsidePlan) continue;
                for (int y = this.location.pos.getiY() + -10; y < this.location.pos.getiY() + 30; ++y) {
                    IntPoint p = BuildingPlan.adjustForOrientation(x, y, z, dx - this.plan.lengthOffset, dz - this.plan.widthOffset, orientation).getIntPoint();
                    Block block = p.getBlock(this.world);
                    if (this.isLogBlock(block)) {
                        if (this.pointsTree.contains(p)) continue;
                        p.setBlockState(this.world, this.decayedTree);
                        continue;
                    }
                    if (!this.isLeaveBlock(block)) continue;
                    if (!nonDecayPosSet.contains(p)) {
                        ++nbLeavesDecayed;
                        p.setBlockState(this.world, this.decayingLeaves);
                        continue;
                    }
                    ++nbLeavesSpared;
                    if (this.nonDecayingLeaves == null) continue;
                    p.setBlockState(this.world, this.nonDecayingLeaves);
                }
                if (!this.testMode) continue;
                IntPoint p = BuildingPlan.adjustForOrientation(x, this.location.pos.getiY() + 15, z, dx - this.plan.lengthOffset, dz - this.plan.widthOffset, orientation).getIntPoint();
                p.setBlockState(this.world, Blocks.field_150399_cn.func_176203_a(randomWoolColour));
            }
        }
        if (MillConfigValues.LogWorldGeneration >= 1) {
            MillLog.debug(this, "Finished decaying " + nbLeavesDecayed + " leaves. Spared " + nbLeavesSpared + " . Total time in ns: " + (System.nanoTime() - startTime));
        }
    }

    private void findTrees() {
        int x = this.location.pos.getiX();
        int z = this.location.pos.getiZ();
        int orientation = this.location.orientation;
        for (int dx = -this.plan.areaToClearLengthBefore - 4; dx < this.plan.length + this.plan.areaToClearLengthAfter + 4; ++dx) {
            for (int dz = -this.plan.areaToClearWidthBefore - 4; dz < this.plan.width + this.plan.areaToClearWidthAfter + 4; ++dz) {
                boolean isZOutsidePlan;
                boolean isXOutsidePlan = dx < 0 || dx > this.plan.length;
                boolean bl = isZOutsidePlan = dz < 0 || dz > this.plan.width;
                if (!isXOutsidePlan && !isZOutsidePlan) continue;
                for (int y = this.location.pos.getiY() + -10; y < this.location.pos.getiY() + 30; ++y) {
                    IntPoint p = BuildingPlan.adjustForOrientation(x, y, z, dx - this.plan.lengthOffset, dz - this.plan.widthOffset, orientation).getIntPoint();
                    if (this.pointsTested.contains(p) || !this.isLogBlock(p.getBlock(this.world)) || !BlockItemUtilities.isBlockGround(p.getBelow().getBlock(this.world))) continue;
                    this.handleTree(p);
                }
            }
        }
    }

    private void handleTree(IntPoint startingPos) {
        ArrayList<IntPoint> treePoints = new ArrayList<IntPoint>();
        ArrayList<IntPoint> pointsToTest = new ArrayList<IntPoint>();
        pointsToTest.add(startingPos);
        boolean abort = false;
        while (!pointsToTest.isEmpty() && !abort) {
            IntPoint p = (IntPoint)pointsToTest.get(pointsToTest.size() - 1);
            if (!this.pointsTested.contains(p)) {
                this.pointsTested.add(p);
                Block block = p.getBlock(this.world);
                if (this.isLogBlock(block)) {
                    treePoints.add(p);
                    pointsToTest.addAll(p.getAllNeightbours());
                }
                abort = treePoints.size() > 100 || p.horizontalDistanceToSquared(startingPos) > 100;
            }
            pointsToTest.remove(p);
        }
        this.pointsTree.addAll(treePoints);
    }

    private boolean isLeaveBlock(Block block) {
        return block instanceof BlockLeaves;
    }

    private boolean isLogBlock(Block block) {
        return block instanceof BlockLog;
    }
}

