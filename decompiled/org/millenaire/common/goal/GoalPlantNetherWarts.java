/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 */
package org.millenaire.common.goal;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.Point;

@DocumentedElement.Documentation(value="Plant nether warts at home (for free).")
public class GoalPlantNetherWarts
extends Goal {
    private static ItemStack[] WARTS = new ItemStack[]{new ItemStack(Items.field_151075_bm, 1)};

    public GoalPlantNetherWarts() {
        this.icon = InvItem.createInvItem(Items.field_151075_bm);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) {
        return this.packDest(villager.getHouse().getResManager().getNetherWartsPlantingLocation(), villager.getHouse());
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        return WARTS;
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
        Block block = villager.getBlock(villager.getGoalDestPoint());
        Point cropPoint = villager.getGoalDestPoint().getAbove();
        block = villager.getBlock(cropPoint);
        if (block == Blocks.field_150350_a) {
            villager.setBlockAndMetadata(cropPoint, Blocks.field_150388_bm, 0);
            villager.func_184609_a(EnumHand.MAIN_HAND);
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        return 100;
    }
}

