package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager attends puja ceremony at the village temple.
 */
public class GoalPerformPuja extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        // TODO: Locate temple building
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
        if (elapsed > 300) {
            v.stopMoving = false;
            return true;
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager v) { return 30; }
}
