package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager goes to their home/workshop to retrieve the appropriate tool for their next job.
 */
public class GoalGetTool extends Goal {

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
        // TODO: Check villager type's required tool and equip from building chest
        return true; // Tool retrieved (or no tool system yet)
    }

    @Override
    public int actionDuration(MillVillager v) { return 10; }
}
