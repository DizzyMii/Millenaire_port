package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Merchant visits an inn in the village to rest and stay overnight.
 */
public class GoalMerchantVisitInn extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        // TODO: Locate inn building in village
        Point th = v.townHallPoint;
        if (th != null) {
            return new GoalInformation(th, 5);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        v.stopMoving = true;
        long elapsed = v.level().getGameTime() - v.goalStarted;
        if (elapsed > 400) {
            v.stopMoving = false;
            return true;
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager v) { return 40; }
}
