/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.goal;

import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;

@DocumentedElement.Documentation(value="Makes villagers hide in the town hall during a raid.")
public class GoalHide
extends Goal {
    public GoalHide() {
        this.travelBookShow = false;
    }

    @Override
    public boolean canBeDoneAtNight() {
        return true;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        if (villager.getPos().distanceToSquared(villager.getTownHall().getResManager().getShelterPos()) <= 9.0) {
            return null;
        }
        return this.packDest(villager.getTownHall().getResManager().getShelterPos(), villager.getTownHall());
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) throws Exception {
        return false;
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
}

