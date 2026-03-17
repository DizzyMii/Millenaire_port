/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.goal;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.MillCommonUtilities;

@DocumentedElement.Documentation(value="For market merchants, hold their stalls so the player can trade with them.")
public class GoalForeignMerchantKeepStall
extends Goal {
    private static ItemStack[] PURSE = new ItemStack[]{new ItemStack((Item)MillItems.PURSE, 1)};
    private static ItemStack[] DENIER_ARGENT = new ItemStack[]{new ItemStack((Item)MillItems.DENIER_ARGENT, 1)};

    public GoalForeignMerchantKeepStall() {
        this.icon = InvItem.createInvItem(MillItems.PURSE);
    }

    @Override
    public int actionDuration(MillVillager villager) throws Exception {
        return 1200;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        if (villager.foreignMerchantStallId >= villager.getHouse().getResManager().stalls.size()) {
            return null;
        }
        return this.packDest(villager.getHouse().getResManager().stalls.get(villager.foreignMerchantStallId), villager.getHouse());
    }

    @Override
    public ItemStack[] getHeldItemsDestination(MillVillager villager) throws Exception {
        return DENIER_ARGENT;
    }

    @Override
    public ItemStack[] getHeldItemsOffHandDestination(MillVillager villager) throws Exception {
        return PURSE;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) throws Exception {
        return true;
    }

    @Override
    public boolean lookAtPlayer() {
        return true;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        return MillCommonUtilities.chanceOn(600);
    }

    @Override
    public int priority(MillVillager villager) throws Exception {
        return MillCommonUtilities.randomInt(50);
    }
}

