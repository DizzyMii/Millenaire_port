package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

/**
 * Villager delivers collected trade goods to their shop building.
 */
public class GoalDeliverResourcesShop extends Goal {

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
        Building home = v.getHomeBuilding();
        if (home == null || v.inventory.isEmpty()) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "deliver_resources_shop", VillagerActions.storeAllInventory(home))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 10); }
}
