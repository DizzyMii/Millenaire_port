/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 */
package org.millenaire.common.goal;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="Pick up dried bricks from a kiln.")
public class GoalIndianGatherBrick
extends Goal {
    private static ItemStack[] MUD_BRICK = new ItemStack[]{BlockItemUtilities.getItemStackFromBlockState(MillBlocks.BS_MUD_BRICK, 1)};

    public GoalIndianGatherBrick() {
        this.maxSimultaneousInBuilding = 1;
        this.townhallLimit.put(InvItem.createInvItem(MillBlocks.BS_MUD_BRICK), 4096);
        this.tags.add("tag_construction");
        this.icon = InvItem.createInvItem(MillBlocks.BS_MUD_BRICK);
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
            int nb = kiln.getResManager().getNbFullBrickLocation();
            boolean validTarget = false;
            if (nb > 0 && minimumBricksNotRequired) {
                validTarget = true;
            }
            if (nb > 4) {
                validTarget = true;
            }
            if (!validTarget || (p = kiln.getResManager().getFullBrickLocation()) == null) continue;
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
    public ItemStack[] getHeldItemsOffHandDestination(MillVillager villager) {
        return MUD_BRICK;
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        return villager.getBestPickaxeStack();
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
        if (WorldUtilities.getBlockState(villager.field_70170_p, villager.getGoalDestPoint()) == MillBlocks.BS_MUD_BRICK) {
            villager.addToInv(MillBlocks.BS_MUD_BRICK, 1);
            villager.setBlockAndMetadata(villager.getGoalDestPoint(), Blocks.field_150350_a, 0);
            villager.func_184609_a(EnumHand.MAIN_HAND);
        }
        if (villager.getGoalBuildingDest().getResManager().getNbFullBrickLocation() > 0) {
            villager.setGoalInformation(this.getDestination(villager));
            return false;
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        int p = 100 - villager.getTownHall().nbGoodAvailable(MillBlocks.BS_MUD_BRICK, false, false, false) * 2;
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

