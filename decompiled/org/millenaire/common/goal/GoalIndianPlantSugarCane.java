/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 */
package org.millenaire.common.goal;

import java.util.ArrayList;
import net.minecraft.block.Block;
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

@DocumentedElement.Documentation(value="Plant sugarcane in a building with the sugar cane plantation tag.")
public class GoalIndianPlantSugarCane
extends Goal {
    private static ItemStack[] SUGARCANE = new ItemStack[]{new ItemStack(Items.field_151120_aE, 1)};

    public GoalIndianPlantSugarCane() {
        this.tags.add("tag_agriculture");
        this.icon = InvItem.createInvItem(Items.field_151120_aE);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) {
        ArrayList<Point> vp = new ArrayList<Point>();
        ArrayList<Point> buildingp = new ArrayList<Point>();
        for (Building plantation : villager.getTownHall().getBuildingsWithTag("sugarplantation")) {
            Point p = plantation.getResManager().getSugarCanePlantingLocation();
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
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        return SUGARCANE;
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
            int nb = kiln.getResManager().getNbSugarCanePlantingLocation();
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
        Block block = villager.getBlock(villager.getGoalDestPoint());
        Point cropPoint = villager.getGoalDestPoint().getAbove();
        block = villager.getBlock(cropPoint);
        if (block == Blocks.field_150350_a || block == Blocks.field_150362_t) {
            villager.setBlock(cropPoint, (Block)Blocks.field_150436_aH);
            villager.func_184609_a(EnumHand.MAIN_HAND);
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
}

