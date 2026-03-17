package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager goes to the townhall to collect goods needed by their household.
 */
public class GoalGetGoodsForHousehold extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Point th = v.townHallPoint;
        if (th != null) {
            return new GoalInformation(th, 4);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // TODO: Query household needs from BuildingResManager and transfer from townhall
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 15; }
}
