package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Indian priest performs puja ceremony at their temple/home.
 */
public class GoalBePujaPerformer extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Point home = v.getHousePoint();
        if (home != null) {
            return new GoalInformation(home, 3);
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
