package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager collects trade goods from the townhall to stock their shop.
 */
public class GoalGetResourcesForShops extends Goal {

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
        // TODO: Transfer trade goods from townhall to villager inventory
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 15; }
}
