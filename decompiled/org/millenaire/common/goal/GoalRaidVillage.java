/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.goal;

import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;

@DocumentedElement.Documentation(value="Goal for village raiders, active when in the target village.")
public class GoalRaidVillage
extends Goal {
    public GoalRaidVillage() {
        this.travelBookShow = false;
    }

    @Override
    public boolean autoInterruptIfNoTarget() {
        return false;
    }

    @Override
    public boolean canBeDoneAtNight() {
        return true;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        return this.packDest(villager.getTownHall().getResManager().getDefendingPos(), villager.getTownHall());
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        return new ItemStack[]{villager.getWeapon()};
    }

    @Override
    public boolean isFightingGoal() {
        return true;
    }

    @Override
    public boolean isStillValidSpecific(MillVillager villager) throws Exception {
        return villager.getTownHall().underAttack;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        return false;
    }

    @Override
    public int priority(MillVillager villager) throws Exception {
        return 0;
    }

    @Override
    public int range(MillVillager villager) {
        return 1;
    }
}

