/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package org.millenaire.common.goal.leisure;

import net.minecraft.entity.Entity;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;

@DocumentedElement.Documentation(value="Makes the villager 'rest' at home. Has a priority of 0 and is there to ensure there is always something to do.")
public class GoalGoRest
extends Goal {
    public GoalGoRest() {
        this.leasure = true;
        this.travelBookShow = false;
        this.sprint = false;
    }

    @Override
    public int actionDuration(MillVillager villager) {
        return 200;
    }

    @Override
    public boolean allowRandomMoves() {
        return true;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) {
        return this.packDest(villager.getHouse().getResManager().getSleepingPos());
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) {
        return villager.getHouse().getPos().distanceTo((Entity)villager) > 5.0;
    }

    @Override
    public boolean performAction(MillVillager villager) {
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        return 0;
    }

    @Override
    public int range(MillVillager villager) {
        return 10;
    }
}

