/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.goal.leisure;

import java.util.ArrayList;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="Makes villagers go to buildings with the leisure tag, so they can meet people to chat with.")
public class GoalGoSocialise
extends Goal {
    public GoalGoSocialise() {
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
        Point dest = null;
        Building destB = null;
        ArrayList<Building> possibleDests = new ArrayList<Building>();
        for (Building b : villager.getTownHall().getBuildings()) {
            if (!b.containsTags("leasure")) continue;
            possibleDests.add(b);
        }
        if (possibleDests.isEmpty()) {
            possibleDests.add(villager.getTownHall());
        }
        destB = (Building)possibleDests.get(MillCommonUtilities.randomInt(possibleDests.size()));
        dest = destB.getResManager().getLeasurePos();
        return this.packDest(dest, destB);
    }

    @Override
    public boolean performAction(MillVillager villager) {
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        return 5;
    }

    @Override
    public int range(MillVillager villager) {
        return 5;
    }
}

