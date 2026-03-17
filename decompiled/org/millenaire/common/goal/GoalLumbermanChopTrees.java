/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 */
package org.millenaire.common.goal;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="Go chop trees in a grove.")
public class GoalLumbermanChopTrees
extends Goal {
    public GoalLumbermanChopTrees() {
        this.maxSimultaneousInBuilding = 1;
        this.townhallLimit.put(InvItem.createInvItem(Blocks.field_150364_r, -1), 4096);
        this.icon = InvItem.createInvItem(Items.field_151036_c);
    }

    @Override
    public int actionDuration(MillVillager villager) {
        int toolEfficiency = (int)villager.getBestAxe().func_150893_a(new ItemStack((Item)villager.getBestAxe(), 1), Blocks.field_150364_r.func_176223_P());
        return 20 - toolEfficiency * 2;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) {
        ArrayList<Point> woodPos = new ArrayList<Point>();
        ArrayList<Point> buildingp = new ArrayList<Point>();
        for (Building grove : villager.getTownHall().getBuildingsWithTag("grove")) {
            Point p;
            if (grove.getWoodCount() <= 4 || (p = grove.getWoodLocation()) == null) continue;
            woodPos.add(p);
            buildingp.add(grove.getPos());
            if (MillConfigValues.LogLumberman < 3) continue;
            MillLog.debug(this, "Found location in grove: " + p + ". Targeted block: " + p.getBlock(villager.field_70170_p));
        }
        if (woodPos.isEmpty()) {
            return null;
        }
        Point p = (Point)woodPos.get(0);
        Point buildingP = (Point)buildingp.get(0);
        for (int i = 1; i < woodPos.size(); ++i) {
            if (!(((Point)woodPos.get(i)).horizontalDistanceToSquared((Entity)villager) < p.horizontalDistanceToSquared((Entity)villager))) continue;
            p = (Point)woodPos.get(i);
            buildingP = (Point)buildingp.get(i);
        }
        if (MillConfigValues.LogLumberman >= 3) {
            MillLog.debug(this, "Going to gather wood around: " + p + ". Targeted block: " + p.getBlock(villager.field_70170_p));
        }
        return this.packDest(p, buildingP);
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        return villager.getBestAxeStack();
    }

    @Override
    public AStarConfig getPathingConfig(MillVillager villager) {
        if (!villager.canVillagerClearLeaves()) {
            return JPS_CONFIG_CHOPLUMBER_NO_LEAVES;
        }
        return JPS_CONFIG_CHOPLUMBER;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) {
        if (villager.countInv(Blocks.field_150364_r, -1) > 64) {
            return false;
        }
        return this.getDestination(villager) != null;
    }

    @Override
    public boolean lookAtGoal() {
        return true;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        boolean woodFound = false;
        if (MillConfigValues.LogLumberman >= 3) {
            MillLog.debug(this, "Attempting to gather wood around: " + villager.getGoalDestPoint() + ", central block: " + villager.getGoalDestPoint().getBlock(villager.field_70170_p));
        }
        for (int deltaY = 12; deltaY > -12; --deltaY) {
            for (int deltaX = -3; deltaX < 4; ++deltaX) {
                for (int deltaZ = -3; deltaZ < 4; ++deltaZ) {
                    Point p = villager.getGoalDestPoint().getRelative(deltaX, deltaY, deltaZ);
                    Block block = villager.getBlock(p);
                    if (block != Blocks.field_150364_r && block != Blocks.field_150363_s && block != Blocks.field_150362_t && block != Blocks.field_150361_u) continue;
                    if (!woodFound) {
                        int meta;
                        if (block == Blocks.field_150364_r || block == Blocks.field_150363_s) {
                            meta = villager.getBlockMeta(p) & 3;
                            villager.setBlock(p, Blocks.field_150350_a);
                            villager.func_184609_a(EnumHand.MAIN_HAND);
                            if (block == Blocks.field_150364_r) {
                                villager.addToInv(Blocks.field_150364_r, meta, 1);
                            } else {
                                villager.addToInv(Blocks.field_150363_s, meta, 1);
                            }
                            woodFound = true;
                            if (MillConfigValues.LogLumberman < 3) continue;
                            MillLog.debug(this, "Gathered wood at: " + p);
                            continue;
                        }
                        meta = WorldUtilities.getBlockMeta(villager.field_70170_p, p);
                        if (block == Blocks.field_150362_t) {
                            if (MillCommonUtilities.randomInt(4) == 0) {
                                villager.addToInv(Blocks.field_150345_g, meta & 3, 1);
                            }
                        } else if ((meta & 3) == 0) {
                            if (MillCommonUtilities.randomInt(4) == 0) {
                                villager.addToInv(Blocks.field_150345_g, 4, 1);
                            }
                        } else if (MillCommonUtilities.randomInt(2) == 0) {
                            villager.addToInv(Blocks.field_150345_g, 5, 1);
                        }
                        villager.setBlock(p, Blocks.field_150350_a);
                        villager.func_184609_a(EnumHand.MAIN_HAND);
                        if (MillConfigValues.LogLumberman < 3) continue;
                        MillLog.debug(this, "Destroyed leaves at: " + p);
                        continue;
                    }
                    if (MillConfigValues.LogLumberman >= 3) {
                        MillLog.debug(this, "More wood found.");
                    }
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        return Math.max(10, 125 - villager.countInv(Blocks.field_150364_r, -1));
    }

    @Override
    public int range(MillVillager villager) {
        return 8;
    }
}

