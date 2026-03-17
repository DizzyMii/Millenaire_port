package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager removes old or obsolete path blocks that are no longer needed.
 */
public class GoalClearOldPath extends Goal {

    { this.tags.add(TAG_CONSTRUCTION); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Point th = v.townHallPoint;
        if (th != null) {
            return new GoalInformation(th, 5);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // TODO: Remove obsolete path blocks based on village layout changes
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 20; }
}
