/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.goal;

import java.util.ArrayList;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="Move to a house with an empty 'job' and become adult. Only valid for fully grown children.")
public class GoalChildBecomeAdult
extends Goal {
    public GoalChildBecomeAdult() {
        this.maxSimultaneousInBuilding = 1;
        this.travelBookShow = false;
    }

    @Override
    public boolean allowRandomMoves() {
        return true;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws MillLog.MillenaireException {
        if (villager.getSize() < 20) {
            return null;
        }
        ArrayList<Point> possibleDest = new ArrayList<Point>();
        ArrayList<Point> possibleDestBuilding = new ArrayList<Point>();
        int maxPriority = 0;
        for (Building house : villager.getTownHall().getBuildings()) {
            if (house == null || house.equals(villager.getHouse()) || !house.isHouse() || !house.canChildMoveIn(villager.gender, villager.familyName) || house.location.priorityMoveIn < maxPriority || !this.validateDest(villager, house)) continue;
            if (house.location.priorityMoveIn > maxPriority) {
                possibleDest.clear();
                possibleDestBuilding.clear();
                maxPriority = house.location.priorityMoveIn;
            }
            possibleDest.add(house.getResManager().getSleepingPos());
            possibleDestBuilding.add(house.getPos());
        }
        if (possibleDest.size() > 0) {
            int rand = MillCommonUtilities.randomInt(possibleDest.size());
            return this.packDest((Point)possibleDest.get(rand), (Point)possibleDestBuilding.get(rand));
        }
        return null;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) throws MillLog.MillenaireException {
        return this.getDestination(villager) != null;
    }

    @Override
    public boolean performAction(MillVillager villager) throws MillLog.MillenaireException {
        Building house = villager.getGoalBuildingDest();
        if (house != null && house.canChildMoveIn(villager.gender, villager.familyName)) {
            if (MillConfigValues.LogChildren >= 1) {
                MillLog.major(this, "Adding new adult to house of type " + house.location + ". Gender: " + villager.gender);
            }
            house.addAdult(villager);
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        return 100;
    }
}

