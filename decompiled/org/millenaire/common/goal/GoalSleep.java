/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBed
 *  net.minecraft.world.World
 */
package org.millenaire.common.goal;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.world.World;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

@DocumentedElement.Documentation(value="Go to sleep at home at night. This goal is auto-added to all villagers.")
public class GoalSleep
extends Goal {
    public GoalSleep() {
        this.travelBookShow = false;
        this.sprint = false;
    }

    @Override
    public int actionDuration(MillVillager villager) throws Exception {
        return 10;
    }

    @Override
    public boolean allowRandomMoves() throws Exception {
        return false;
    }

    @Override
    public boolean canBeDoneAtNight() {
        return true;
    }

    @Override
    public boolean canBeDoneInDayTime() {
        return false;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        World world = villager.field_70170_p;
        Point sleepPos = villager.getHouse().getResManager().getSleepingPos();
        ArrayList<Point> beds = new ArrayList<Point>();
        for (int xDelta = 0; xDelta < 6; ++xDelta) {
            for (int yDelta = 0; yDelta < 6; ++yDelta) {
                for (int zDelta = 0; zDelta < 6; ++zDelta) {
                    for (int l = 0; l < 8; ++l) {
                        int meta;
                        Point p = sleepPos.getRelative(xDelta * (1 - (l & 1) * 2), yDelta * (1 - (l & 2)), zDelta * (1 - (l & 4) / 2));
                        Block block = WorldUtilities.getBlock(world, p);
                        if (!(block instanceof BlockBed) || ((meta = WorldUtilities.getBlockMeta(world, p)) & 8) != 0) continue;
                        boolean alreadyTaken = false;
                        for (MillVillager v : villager.getHouse().getKnownVillagers()) {
                            if (v == villager || v.getGoalDestPoint() == null || !v.getGoalDestPoint().equals(p)) continue;
                            alreadyTaken = true;
                        }
                        if (alreadyTaken) continue;
                        beds.add(p);
                    }
                }
            }
        }
        if (beds.size() > 0) {
            return this.packDest((Point)beds.get(0), villager.getHouse());
        }
        ArrayList<Point> feetPos = new ArrayList<Point>();
        for (int xDelta = 0; xDelta < 6; ++xDelta) {
            for (int yDelta = 0; yDelta < 6; ++yDelta) {
                for (int zDelta = 0; zDelta < 6; ++zDelta) {
                    for (int l = 0; l < 8; ++l) {
                        Point topBlock;
                        Point p = sleepPos.getRelative(xDelta * (1 - (l & 1) * 2), yDelta * (1 - (l & 2)), zDelta * (1 - (l & 4) / 2));
                        if (p.isBlockPassable(world) || !p.getAbove().isBlockPassable(world) || !p.getRelative(0.0, 2.0, 0.0).isBlockPassable(world) || (topBlock = WorldUtilities.findTopNonPassableBlock(world, p.getiX(), p.getiZ())) == null || !(topBlock.y > p.y + 1.0)) continue;
                        float angle = villager.getBedOrientationInDegrees();
                        int dx = 0;
                        int dz = 0;
                        if (angle == 0.0f) {
                            dx = 1;
                        } else if (angle == 90.0f) {
                            dz = 1;
                        } else if (angle == 180.0f) {
                            dx = -1;
                        } else if (angle == 270.0f) {
                            dz = -1;
                        }
                        Point p2 = p.getRelative(dx, 0.0, dz);
                        if (p2.isBlockPassable(world) || !p2.getAbove().isBlockPassable(world) || !p2.getRelative(0.0, 2.0, 0.0).isBlockPassable(world) || (topBlock = WorldUtilities.findTopNonPassableBlock(world, p2.getiX(), p2.getiZ())) == null || !(topBlock.y > p2.y + 1.0)) continue;
                        p = p.getAbove();
                        boolean alreadyTaken = false;
                        for (MillVillager v : villager.getHouse().getKnownVillagers()) {
                            if (v == villager || v.getGoalDestPoint() == null) continue;
                            if (v.getGoalDestPoint().equals(p)) {
                                alreadyTaken = true;
                            }
                            if (v.getGoalDestPoint().equals(p.getRelative(1.0, 0.0, 0.0))) {
                                alreadyTaken = true;
                            }
                            if (v.getGoalDestPoint().equals(p.getRelative(0.0, 0.0, 1.0))) {
                                alreadyTaken = true;
                            }
                            if (v.getGoalDestPoint().equals(p.getRelative(-1.0, 0.0, 0.0))) {
                                alreadyTaken = true;
                            }
                            if (!v.getGoalDestPoint().equals(p.getRelative(0.0, 0.0, -1.0))) continue;
                            alreadyTaken = true;
                        }
                        if (alreadyTaken) continue;
                        feetPos.add(p);
                    }
                }
            }
        }
        for (MillVillager v : villager.getHouse().getKnownVillagers()) {
            if (v == villager || v.getGoalDestPoint() == null) continue;
            feetPos.remove(v.getGoalDestPoint());
            feetPos.remove(v.getGoalDestPoint().getRelative(1.0, 0.0, 0.0));
            feetPos.remove(v.getGoalDestPoint().getRelative(0.0, 0.0, 1.0));
            feetPos.remove(v.getGoalDestPoint().getRelative(-1.0, 0.0, 0.0));
            feetPos.remove(v.getGoalDestPoint().getRelative(0.0, 0.0, -1.0));
        }
        if (feetPos.size() > 0) {
            return this.packDest((Point)feetPos.get(0), villager.getHouse());
        }
        return this.packDest(sleepPos, villager.getHouse());
    }

    @Override
    public String labelKeyWhileTravelling(MillVillager villager) {
        return this.key + "_travelling";
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        if (!villager.nightActionPerformed) {
            villager.nightActionPerformed = villager.performNightAction();
        }
        villager.shouldLieDown = true;
        float angle = villager.getBedOrientationInDegrees();
        double dx = 0.5;
        double dz = 0.5;
        double fdx = 0.0;
        double fdz = 0.0;
        if (angle == 0.0f) {
            dx = 0.95;
            fdx = -10.0;
        } else if (angle == 90.0f) {
            dz = 0.95;
            fdz = -10.0;
        } else if (angle == 180.0f) {
            dx = 0.05;
            fdx = 10.0;
        } else if (angle == 270.0f) {
            dz = 0.05;
            fdz = 10.0;
        }
        float floatingHeight = villager.getBlock(villager.getGoalDestPoint()) instanceof BlockBed ? 0.7f : 0.2f;
        villager.func_70107_b(villager.getGoalDestPoint().x + dx, villager.getGoalDestPoint().y + (double)floatingHeight, villager.getGoalDestPoint().z + dz);
        villager.facePoint(villager.getPos().getRelative(fdx, 1.0, fdz), 100.0f, 100.0f);
        return false;
    }

    @Override
    public int priority(MillVillager villager) throws Exception {
        return 50;
    }

    @Override
    public int range(MillVillager villager) {
        return 2;
    }

    @Override
    public boolean shouldVillagerLieDown() {
        return true;
    }
}

