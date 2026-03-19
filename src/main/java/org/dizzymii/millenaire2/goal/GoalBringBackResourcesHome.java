package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

/**
 * Villager brings gathered resources back to their home building's storage.
 */
public class GoalBringBackResourcesHome extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        if (v.inventory.isEmpty()) return null;
        Point home = v.housePoint;
        if (home != null) {
            return new GoalInformation(home, 3);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        Building home = v.getHomeBuilding();
        if (home == null || v.inventory.isEmpty()) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "bring_back_resources_home", VillagerActions.storeAllInventory(home))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 10); }
}
