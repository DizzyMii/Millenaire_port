/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 */
package org.millenaire.common.goal;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="Go and dry bricks in a building with the kiln tag.")
public class GoalIndianDryBrick
extends Goal {
    public GoalIndianDryBrick() {
        this.maxSimultaneousInBuilding = 1;
        this.tags.add("tag_construction");
        this.icon = InvItem.createInvItem(MillBlocks.BS_WET_BRICK);
    }

    @Override
    public int actionDuration(MillVillager villager) {
        return 20;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws MillLog.MillenaireException {
        boolean minimumBricksNotRequired = villager.goalKey != null && villager.goalKey.equals(this.key) ? true : (!villager.lastGoalTime.containsKey(this) ? true : villager.field_70170_p.func_72820_D() > villager.lastGoalTime.get(this) + 2000L);
        ArrayList<Point> vp = new ArrayList<Point>();
        ArrayList<Point> buildingp = new ArrayList<Point>();
        for (Building kiln : villager.getTownHall().getBuildingsWithTag("brickkiln")) {
            Point p;
            if (!this.validateDest(villager, kiln)) continue;
            int nb = kiln.getResManager().getNbEmptyBrickLocation();
            boolean validTarget = false;
            if (nb > 0 && minimumBricksNotRequired) {
                validTarget = true;
            }
            if (nb > 4) {
                validTarget = true;
            }
            if (!validTarget || (p = kiln.getResManager().getEmptyBrickLocation()) == null) continue;
            vp.add(p);
            buildingp.add(kiln.getPos());
        }
        if (vp.isEmpty()) {
            return null;
        }
        Point p = (Point)vp.get(0);
        Point buildingP = (Point)buildingp.get(0);
        for (int i = 1; i < vp.size(); ++i) {
            if (!(((Point)vp.get(i)).horizontalDistanceToSquared((Entity)villager) < p.horizontalDistanceToSquared((Entity)villager))) continue;
            p = (Point)vp.get(i);
            buildingP = (Point)buildingp.get(i);
        }
        return this.packDest(p, buildingP);
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        return new ItemStack[]{new ItemStack((Item)MillItems.BRICK_MOULD, 1, 0)};
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) throws MillLog.MillenaireException {
        return this.getDestination(villager) != null;
    }

    @Override
    public boolean lookAtGoal() {
        return true;
    }

    @Override
    public boolean performAction(MillVillager villager) throws MillLog.MillenaireException {
        if (WorldUtilities.getBlock(villager.field_70170_p, villager.getGoalDestPoint()) == Blocks.field_150350_a) {
            villager.setBlockstate(villager.getGoalDestPoint(), MillBlocks.BS_WET_BRICK);
            villager.func_184609_a(EnumHand.MAIN_HAND);
        }
        if (villager.getGoalBuildingDest().getResManager().getNbEmptyBrickLocation() > 0) {
            villager.setGoalInformation(this.getDestination(villager));
            return false;
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        int p = 120;
        for (MillVillager v : villager.getTownHall().getKnownVillagers()) {
            if (!this.key.equals(v.goalKey)) continue;
            p /= 2;
        }
        return p;
    }

    @Override
    public boolean unreachableDestination(MillVillager villager) {
        return false;
    }
}

