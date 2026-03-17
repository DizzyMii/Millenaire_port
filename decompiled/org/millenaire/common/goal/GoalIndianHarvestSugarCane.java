/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 */
package org.millenaire.common.goal;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="Gather sugar cane from a building with the sugar cane plantation tag.")
public class GoalIndianHarvestSugarCane
extends Goal {
    private static ItemStack[] SUGARCANE = new ItemStack[]{new ItemStack(Items.field_151120_aE, 1)};

    public GoalIndianHarvestSugarCane() {
        this.tags.add("tag_agriculture");
        this.icon = InvItem.createInvItem(Items.field_151120_aE);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) {
        ArrayList<Point> vp = new ArrayList<Point>();
        ArrayList<Point> buildingp = new ArrayList<Point>();
        for (Building plantation : villager.getTownHall().getBuildingsWithTag("sugarplantation")) {
            Point p = plantation.getResManager().getSugarCaneHarvestLocation();
            if (p == null) continue;
            vp.add(p);
            buildingp.add(plantation.getPos());
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
        return SUGARCANE;
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        return villager.getBestHoeStack();
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) {
        int nbsimultaneous = 0;
        for (MillVillager v : villager.getTownHall().getKnownVillagers()) {
            if (v == villager || !this.key.equals(v.goalKey)) continue;
            ++nbsimultaneous;
        }
        if (nbsimultaneous > 2) {
            return false;
        }
        boolean delayOver = !villager.lastGoalTime.containsKey(this) ? true : villager.field_70170_p.func_72820_D() > villager.lastGoalTime.get(this) + 2000L;
        for (Building kiln : villager.getTownHall().getBuildingsWithTag("sugarplantation")) {
            int nb = kiln.getResManager().getNbSugarCaneHarvestLocation();
            if (nb > 0 && delayOver) {
                return true;
            }
            if (nb <= 4) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean lookAtGoal() {
        return true;
    }

    @Override
    public boolean performAction(MillVillager villager) {
        double rand;
        float irrigation;
        int nbcrop;
        Point cropPoint = villager.getGoalDestPoint().getRelative(0.0, 3.0, 0.0);
        if (villager.getBlock(cropPoint) == Blocks.field_150436_aH) {
            villager.setBlockAndMetadata(cropPoint, Blocks.field_150350_a, 0);
            nbcrop = 1;
            irrigation = villager.getTownHall().getVillageIrrigation();
            rand = Math.random();
            if (rand < (double)(irrigation / 100.0f)) {
                ++nbcrop;
            }
            villager.addToInv(Items.field_151120_aE, nbcrop);
        }
        if (villager.getBlock(cropPoint = villager.getGoalDestPoint().getRelative(0.0, 2.0, 0.0)) == Blocks.field_150436_aH) {
            villager.setBlockAndMetadata(cropPoint, Blocks.field_150350_a, 0);
            nbcrop = 1;
            irrigation = villager.getTownHall().getVillageIrrigation();
            rand = Math.random();
            if (rand < (double)(irrigation / 100.0f)) {
                ++nbcrop;
            }
            villager.func_184609_a(EnumHand.MAIN_HAND);
            villager.addToInv(Items.field_151120_aE, nbcrop);
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        int p = 200 - villager.getTownHall().nbGoodAvailable(Items.field_151120_aE, 0, false, false, false) * 4;
        for (MillVillager v : villager.getTownHall().getKnownVillagers()) {
            if (!this.key.equals(v.goalKey)) continue;
            p /= 2;
        }
        return p;
    }
}

