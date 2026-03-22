package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Child villager transitions to adult status at their home.
 */
public class GoalChildBecomeAdult extends Goal {

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
        // Transition child to adult using the altkey from VillagerType
        if (v.getVillagerType() != null && v.getVillagerType().altkey != null) {
            org.dizzymii.millenaire2.culture.Culture culture = v.getCulture();
            if (culture != null) {
                org.dizzymii.millenaire2.culture.VillagerType adultType = culture.getVillagerType(v.getVillagerType().altkey);
                if (adultType != null) {
                    v.setVillagerType(adultType);
                    v.refreshDimensions(); // Update bounding box for adult size
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 10; }
}
