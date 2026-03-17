/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 */
package org.millenaire.common.goal;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.block.BlockSnailSoil;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="Gather snails from a snail soil block to make purple dye.")
public class GoalByzantineGatherSnails
extends Goal {
    private static ItemStack[] PURPLE_DYE = new ItemStack[]{new ItemStack(Items.field_151100_aR, EnumDyeColor.PURPLE.func_176767_b())};

    public GoalByzantineGatherSnails() {
        this.maxSimultaneousInBuilding = 2;
        this.buildingLimit.put(InvItem.createInvItem(Items.field_151100_aR, EnumDyeColor.PURPLE.func_176767_b()), 128);
        this.townhallLimit.put(InvItem.createInvItem(Items.field_151100_aR, EnumDyeColor.PURPLE.func_176767_b()), 128);
        this.icon = InvItem.createInvItem(Items.field_151100_aR, EnumDyeColor.PURPLE.func_176767_b());
    }

    @Override
    public int actionDuration(MillVillager villager) {
        return 20;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) {
        ArrayList<Point> vp = new ArrayList<Point>();
        ArrayList<Point> buildingp = new ArrayList<Point>();
        for (Building snailFamr : villager.getTownHall().getBuildingsWithTag("snailsfarm")) {
            Point p = snailFamr.getResManager().getSnailSoilHarvestLocation();
            if (p == null) continue;
            vp.add(p);
            buildingp.add(snailFamr.getPos());
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
        return PURPLE_DYE;
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        return villager.getBestShovelStack();
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) {
        boolean delayOver = !villager.lastGoalTime.containsKey(this) ? true : villager.field_70170_p.func_72820_D() > villager.lastGoalTime.get(this) + 2000L;
        for (Building kiln : villager.getTownHall().getBuildingsWithTag("snailsfarm")) {
            int nb = kiln.getResManager().getNbSnailSoilHarvestLocation();
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
        if (WorldUtilities.getBlock(villager.field_70170_p, villager.getGoalDestPoint()) == MillBlocks.SNAIL_SOIL && WorldUtilities.getBlockState(villager.field_70170_p, villager.getGoalDestPoint()).func_177229_b(BlockSnailSoil.PROGRESS) == BlockSnailSoil.EnumType.SNAIL_SOIL_FULL) {
            villager.addToInv(Items.field_151100_aR, EnumDyeColor.PURPLE.func_176767_b(), 1);
            villager.setBlockAndMetadata(villager.getGoalDestPoint(), MillBlocks.SNAIL_SOIL, 0);
            villager.func_184609_a(EnumHand.MAIN_HAND);
            return false;
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        int p = 100 - villager.getTownHall().nbGoodAvailable(InvItem.createInvItem(Items.field_151100_aR, EnumDyeColor.PURPLE.func_176767_b()), false, false, false) * 2;
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

