package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Child villager transitions to adult status at their home.
 */
public class GoalChildBecomeAdult extends Goal {

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
        // TODO: Transition child to adult - assign adult VillagerType, resize model
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 10; }
}
