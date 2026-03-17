/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.MillLog;

@DocumentedElement.Documentation(value="Go and sell to the player.")
public class GoalBeSeller
extends Goal {
    public static final int sellingRadius = 7;
    private static ItemStack[] PURSE = new ItemStack[]{new ItemStack((Item)MillItems.PURSE, 1)};
    private static ItemStack[] DENIER = new ItemStack[]{new ItemStack((Item)MillItems.DENIER, 1)};

    public GoalBeSeller() {
        this.floatingIcon = this.icon = InvItem.createInvItem(MillItems.PURSE);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) {
        return this.packDest(villager.getTownHall().sellingPlace);
    }

    @Override
    public ItemStack[] getHeldItemsDestination(MillVillager villager) throws Exception {
        return DENIER;
    }

    @Override
    public ItemStack[] getHeldItemsOffHandDestination(MillVillager villager) throws Exception {
        return PURSE;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) {
        return false;
    }

    @Override
    public boolean isStillValidSpecific(MillVillager villager) throws Exception {
        boolean valid;
        if (villager.getTownHall().sellingPlace == null) {
            return false;
        }
        EntityPlayer player = villager.field_70170_p.func_184137_a((double)villager.getTownHall().sellingPlace.getiX(), (double)villager.getTownHall().sellingPlace.getiY(), (double)villager.getTownHall().sellingPlace.getiZ(), 7.0, false);
        boolean bl = valid = player != null && villager.getTownHall().sellingPlace.distanceTo((Entity)player) < 7.0;
        if (!valid && MillConfigValues.LogWifeAI >= 1) {
            MillLog.major(this, "Selling goal no longer valid.");
        }
        return valid;
    }

    @Override
    public boolean lookAtPlayer() {
        return true;
    }

    @Override
    public void onAccept(MillVillager villager) {
        EntityPlayer player = villager.field_70170_p.func_184137_a((double)villager.getTownHall().sellingPlace.getiX(), (double)villager.getTownHall().sellingPlace.getiY(), (double)villager.getTownHall().sellingPlace.getiZ(), 7.0, false);
        ServerSender.sendTranslatedSentence(player, 'f', "ui.sellercoming", villager.func_70005_c_());
    }

    @Override
    public void onComplete(MillVillager villager) {
        EntityPlayer player = villager.field_70170_p.func_184137_a((double)villager.getTownHall().getResManager().getSellingPos().getiX(), (double)villager.getTownHall().getResManager().getSellingPos().getiY(), (double)villager.getTownHall().getResManager().getSellingPos().getiZ(), 17.0, false);
        ServerSender.sendTranslatedSentence(player, 'f', "ui.tradecomplete", villager.func_70005_c_());
        villager.getTownHall().seller = null;
        villager.getTownHall().sellingPlace = null;
    }

    @Override
    public boolean performAction(MillVillager villager) {
        if (villager.getTownHall().sellingPlace == null) {
            MillLog.error(this, "villager.townHall.sellingPlace is null.");
            return true;
        }
        return false;
    }

    @Override
    public int priority(MillVillager villager) {
        return 0;
    }

    @Override
    public int range(MillVillager villager) {
        return 2;
    }
}

