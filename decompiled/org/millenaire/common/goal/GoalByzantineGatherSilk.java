/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 */
package org.millenaire.common.goal;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.block.BlockSilkWorm;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="Gather ripe silk from a silk block.")
public class GoalByzantineGatherSilk
extends Goal {
    private static ItemStack[] SHEARS = new ItemStack[]{new ItemStack((Item)Items.field_151097_aZ, 1)};
    private static ItemStack[] SILK = new ItemStack[]{new ItemStack((Item)MillItems.SILK, 1)};

    public GoalByzantineGatherSilk() {
        this.maxSimultaneousInBuilding = 2;
        this.buildingLimit.put(InvItem.createInvItem(MillItems.SILK), 128);
        this.townhallLimit.put(InvItem.createInvItem(MillItems.SILK), 128);
        this.icon = InvItem.createInvItem(MillItems.SILK);
    }

    @Override
    public int actionDuration(MillVillager villager) {
        return 20;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) {
        ArrayList<Point> vp = new ArrayList<Point>();
        ArrayList<Point> buildingp = new ArrayList<Point>();
        for (Building silkFarm : villager.getTownHall().getBuildingsWithTag("silkwormfarm")) {
            Point p = silkFarm.getResManager().getSilkwormHarvestLocation();
            if (p == null) continue;
            vp.add(p);
            buildingp.add(silkFarm.getPos());
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
        return SILK;
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        return SHEARS;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) {
        boolean delayOver = !villager.lastGoalTime.containsKey(this) ? true : villager.field_70170_p.func_72820_D() > villager.lastGoalTime.get(this) + 2000L;
        for (Building kiln : villager.getTownHall().getBuildingsWithTag("silkwormfarm")) {
            int nb = kiln.getResManager().getNbSilkWormHarvestLocation();
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
        if (WorldUtilities.getBlock(villager.field_70170_p, villager.getGoalDestPoint()) == MillBlocks.SILK_WORM && WorldUtilities.getBlockState(villager.field_70170_p, villager.getGoalDestPoint()).func_177229_b(BlockSilkWorm.PROGRESS) == BlockSilkWorm.EnumType.SILKWORMFULL) {
            villager.addToInv(MillItems.SILK, 0, 1);
            villager.setBlockAndMetadata(villager.getGoalDestPoint(), MillBlocks.SILK_WORM, 0);
            villager.func_184609_a(EnumHand.MAIN_HAND);
            return false;
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        int p = 100 - villager.getTownHall().nbGoodAvailable(InvItem.createInvItem(MillItems.SILK, 1), false, false, false) * 2;
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

