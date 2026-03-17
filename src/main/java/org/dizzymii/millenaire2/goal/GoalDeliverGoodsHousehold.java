package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager delivers goods collected from the townhall to their household building.
 */
public class GoalDeliverGoodsHousehold extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Point home = v.housePoint;
        if (home != null) {
            return new GoalInformation(home, 3);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // TODO: Deposit carried goods into the household building chest
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 10; }
}
