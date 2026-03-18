package org.dizzymii.millenaire2.goal.leisure;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager goes home and rests, standing still for a period.
 */
public class GoalGoRest extends Goal {
    public GoalGoRest() { this.leasure = true; }

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
        v.stopMoving = true;
        long elapsed = v.level().getGameTime() - v.goalStarted;
        if (elapsed > 600) { // ~30 seconds
            v.stopMoving = false;
            return true;
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager v) { return 40; }
}
