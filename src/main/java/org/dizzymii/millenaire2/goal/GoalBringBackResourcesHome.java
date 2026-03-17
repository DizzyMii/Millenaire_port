package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager brings gathered resources back to their home building's storage.
 */
public class GoalBringBackResourcesHome extends Goal {

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
        // TODO: Transfer villager inventory to building chest via BuildingResManager
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 10; }
}
