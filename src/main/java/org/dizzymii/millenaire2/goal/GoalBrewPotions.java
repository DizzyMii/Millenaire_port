package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager goes to their workshop to brew potions.
 */
public class GoalBrewPotions extends Goal {

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
        // TODO: Consume ingredients and produce potions via building inventory
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 60; }
}
