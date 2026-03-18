package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

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
        org.dizzymii.millenaire2.village.Building home = v.getHomeBuilding();
        if (home != null) {
            for (java.util.Map.Entry<org.dizzymii.millenaire2.item.InvItem, Integer> entry : v.inventory.entrySet()) {
                home.resManager.storeGoods(entry.getKey(), entry.getValue());
            }
        }
        v.inventory.clear();
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 10; }
}
