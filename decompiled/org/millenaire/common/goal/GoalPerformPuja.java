/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="Perform a puja or a Maya sacrifice.")
public class GoalPerformPuja
extends Goal {
    public GoalPerformPuja() {
        this.floatingIcon = this.icon = InvItem.createInvItem(MillItems.INDIAN_STATUE);
    }

    @Override
    public int actionDuration(MillVillager villager) throws Exception {
        return 5;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        Building temple = null;
        if (villager.canMeditate()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
        } else if (villager.canPerformSacrifices()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
        }
        if (temple != null && temple.pujas != null && (temple.pujas.priest == null || temple.pujas.priest == villager) && temple.pujas.canPray()) {
            return this.packDest(temple.getResManager().getCraftingPos(), temple);
        }
        return null;
    }

    @Override
    public ItemStack[] getHeldItemsDestination(MillVillager villager) {
        Building temple = null;
        if (villager.canMeditate()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
        } else if (villager.canPerformSacrifices()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
        }
        if (temple.pujas.func_70301_a(0) != null) {
            return new ItemStack[]{temple.pujas.func_70301_a(0)};
        }
        return null;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) throws Exception {
        if (villager.canMeditate() ? !villager.mw.isGlobalTagSet("pujas") : villager.canPerformSacrifices() && !villager.mw.isGlobalTagSet("mayansacrifices")) {
            return false;
        }
        return this.getDestination(villager) != null;
    }

    @Override
    public String labelKey(MillVillager villager) {
        if (villager != null && villager.canPerformSacrifices()) {
            return "performsacrifices";
        }
        return this.key;
    }

    @Override
    public String labelKeyWhileTravelling(MillVillager villager) {
        if (villager != null && villager.canPerformSacrifices()) {
            return "performsacrifices";
        }
        return this.key;
    }

    @Override
    public boolean lookAtGoal() {
        return true;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        Building temple = null;
        if (villager.canMeditate()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
        } else if (villager.canPerformSacrifices()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
        }
        boolean canContinue = temple.pujas.performPuja(villager);
        EntityPlayer player = villager.field_70170_p.func_72890_a((Entity)villager, 16.0);
        if (player != null) {
            temple.sendBuildingPacket(player, false);
        }
        return !canContinue;
    }

    @Override
    public int priority(MillVillager villager) throws Exception {
        return 500;
    }

    @Override
    public boolean swingArms() {
        return true;
    }
}

