/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 */
package org.millenaire.common.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="Go to the temple to be available for pujas or sacrifices.")
public class GoalBePujaPerformer
extends Goal {
    public static final int sellingRadius = 7;

    public GoalBePujaPerformer() {
        this.travelBookShow = false;
        this.floatingIcon = InvItem.createInvItem(MillItems.INDIAN_STATUE);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) {
        Building temple = null;
        if (villager.canMeditate()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
        } else if (villager.canPerformSacrifices()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
        }
        if (temple != null && temple.pujas != null && (temple.pujas.priest == null || temple.pujas.priest == villager)) {
            if (MillConfigValues.LogPujas >= 3) {
                MillLog.debug(villager, "Destination for bepujaperformer: " + temple);
            }
            return this.packDest(temple.getResManager().getCraftingPos(), temple);
        }
        return null;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) {
        boolean valid;
        Building temple = null;
        if (villager.canMeditate()) {
            if (!villager.mw.isGlobalTagSet("pujas")) {
                return false;
            }
            temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
        } else if (villager.canPerformSacrifices()) {
            if (!villager.mw.isGlobalTagSet("mayansacrifices")) {
                return false;
            }
            temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
        }
        if (temple == null) {
            return false;
        }
        EntityPlayer player = villager.field_70170_p.func_184137_a((double)temple.getResManager().getCraftingPos().getiX(), (double)temple.getResManager().getCraftingPos().getiY(), (double)temple.getResManager().getCraftingPos().getiZ(), 7.0, false);
        boolean bl = valid = player != null && temple.getResManager().getCraftingPos().distanceTo((Entity)player) < 7.0;
        if (!valid) {
            return false;
        }
        return this.getDestination(villager) != null;
    }

    @Override
    public boolean isStillValidSpecific(MillVillager villager) throws Exception {
        boolean valid;
        Building temple = null;
        if (villager.canMeditate()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
        } else if (villager.canPerformSacrifices()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
        }
        if (temple == null) {
            return false;
        }
        EntityPlayer player = villager.field_70170_p.func_184137_a((double)temple.getResManager().getCraftingPos().getiX(), (double)temple.getResManager().getCraftingPos().getiY(), (double)temple.getResManager().getCraftingPos().getiZ(), 7.0, false);
        boolean bl = valid = player != null && temple.getResManager().getCraftingPos().distanceTo((Entity)player) < 7.0;
        if (!valid && MillConfigValues.LogPujas >= 1) {
            MillLog.major(this, "Be Puja Performer no longer valid.");
        }
        return valid && !temple.pujas.canPray();
    }

    @Override
    public String labelKey(MillVillager villager) {
        if (villager != null && villager.canPerformSacrifices()) {
            return "besacrificeperformer";
        }
        return this.key;
    }

    @Override
    public String labelKeyWhileTravelling(MillVillager villager) {
        if (villager != null && villager.canPerformSacrifices()) {
            return "besacrificeperformer";
        }
        return this.key;
    }

    @Override
    public boolean lookAtPlayer() {
        return true;
    }

    @Override
    public void onAccept(MillVillager villager) {
        Building temple = null;
        if (villager.canMeditate()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
        } else if (villager.canPerformSacrifices()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
        }
        if (temple == null) {
            return;
        }
        EntityPlayer player = villager.field_70170_p.func_184137_a((double)temple.getResManager().getCraftingPos().getiX(), (double)temple.getResManager().getCraftingPos().getiY(), (double)temple.getResManager().getCraftingPos().getiZ(), 7.0, false);
        if (villager.canMeditate()) {
            ServerSender.sendTranslatedSentence(player, 'f', "pujas.priestcoming", villager.func_70005_c_());
        } else if (villager.canPerformSacrifices()) {
            ServerSender.sendTranslatedSentence(player, 'f', "sacrifices.priestcoming", villager.func_70005_c_());
        }
    }

    @Override
    public boolean performAction(MillVillager villager) {
        Building temple = null;
        if (villager.canMeditate()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("pujas");
        } else if (villager.canPerformSacrifices()) {
            temple = villager.getTownHall().getFirstBuildingWithTag("sacrifices");
        }
        if (temple == null) {
            return true;
        }
        temple.pujas.priest = villager;
        return temple.pujas.canPray();
    }

    @Override
    public int priority(MillVillager villager) {
        return 300;
    }

    @Override
    public int range(MillVillager villager) {
        return 2;
    }
}

