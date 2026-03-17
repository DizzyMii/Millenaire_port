/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 */
package org.millenaire.common.goal;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.Point;

@DocumentedElement.Documentation(value="Harvest grown nether warts froom home.")
public class GoalHarvestWarts
extends Goal {
    private static ItemStack[] WARTS = new ItemStack[]{new ItemStack(Items.field_151075_bm, 1)};

    public GoalHarvestWarts() {
        this.icon = InvItem.createInvItem(Items.field_151075_bm);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) {
        return this.packDest(villager.getHouse().getResManager().getNetherWartsHarvestLocation(), villager.getHouse());
    }

    @Override
    public ItemStack[] getHeldItemsOffHandDestination(MillVillager villager) {
        return WARTS;
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        return villager.getBestHoeStack();
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) {
        return this.getDestination(villager).getDest() != null;
    }

    @Override
    public boolean lookAtGoal() {
        return true;
    }

    @Override
    public boolean performAction(MillVillager villager) {
        Point cropPoint = villager.getGoalDestPoint().getAbove();
        if (villager.getBlock(cropPoint) == Blocks.field_150388_bm && villager.getBlockMeta(cropPoint) == 3) {
            villager.setBlockAndMetadata(cropPoint, Blocks.field_150350_a, 0);
            villager.getHouse().storeGoods(Items.field_151075_bm, 1);
            villager.func_184609_a(EnumHand.MAIN_HAND);
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        int p = 100 - villager.getHouse().countGoods(Items.field_151075_bm) * 4;
        return p;
    }
}

