/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 */
package org.millenaire.common.goal;

import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="Fish from fishing holes at home, bringing in standard fish.")
public class GoalFish
extends Goal {
    private static ItemStack[] fishingRod = new ItemStack[]{new ItemStack((Item)Items.field_151112_aM, 1)};

    public GoalFish() {
        this.buildingLimit.put(InvItem.createInvItem(Items.field_151115_aP, 0), 128);
        this.buildingLimit.put(InvItem.createInvItem(Items.field_179566_aV, 0), 128);
        this.icon = InvItem.createInvItem((Item)Items.field_151112_aM);
    }

    @Override
    public int actionDuration(MillVillager villager) {
        return 500;
    }

    protected void addFishResults(MillVillager villager) {
        villager.addToInv(Items.field_151115_aP, 1);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        List<Building> vb = villager.getTownHall().getBuildingsWithTag("fishingspot");
        Building closest = null;
        for (Building b : vb) {
            if (closest != null && !(villager.getPos().horizontalDistanceToSquared(b.getResManager().getSleepingPos()) < villager.getPos().horizontalDistanceToSquared(closest.getResManager().getSleepingPos()))) continue;
            closest = b;
        }
        if (closest == null || closest.getResManager().fishingspots.size() == 0) {
            return null;
        }
        return this.packDest(closest.getResManager().fishingspots.get(MillCommonUtilities.randomInt(closest.getResManager().fishingspots.size())), closest);
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {
        return fishingRod;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) throws Exception {
        for (Building b : villager.getTownHall().getBuildings()) {
            if (b.getResManager().fishingspots.size() <= 0) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean lookAtGoal() {
        return true;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        this.addFishResults(villager);
        villager.func_184609_a(EnumHand.MAIN_HAND);
        return true;
    }

    @Override
    public int priority(MillVillager villager) throws Exception {
        if (villager.getGoalBuildingDest() == null) {
            return 20;
        }
        return 100 - villager.getGoalBuildingDest().countGoods(Items.field_151115_aP);
    }

    @Override
    public boolean stuckAction(MillVillager villager) throws Exception {
        return this.performAction(villager);
    }
}

