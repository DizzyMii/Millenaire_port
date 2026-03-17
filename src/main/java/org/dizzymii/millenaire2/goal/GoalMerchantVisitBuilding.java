package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Travelling merchant visits a village building to conduct trade.
 */
public class GoalMerchantVisitBuilding extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        // TODO: Pick a random building in the village to visit
        Point th = v.townHallPoint;
        if (th != null) {
            return new GoalInformation(th, 5);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // TODO: Exchange goods with building inventory
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 30; }
}
