package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Merchant visits an inn in the village to rest and stay overnight.
 */
public class GoalMerchantVisitInn extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        // Go to townhall area (inn location resolved from village buildings when available)
        Point th = v.getTownHallPoint();
        if (th != null) {
            return new GoalInformation(th, 5);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        v.setStopMoving(true);
        long elapsed = v.level().getGameTime() - v.getGoalStarted();
        if (elapsed > 400) {
            v.setStopMoving(false);
            return true;
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager v) { return 40; }
}
