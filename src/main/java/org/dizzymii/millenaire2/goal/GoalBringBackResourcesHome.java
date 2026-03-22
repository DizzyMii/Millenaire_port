package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

import java.util.Map;

/**
 * Villager brings gathered resources back to their home building's storage.
 */
public class GoalBringBackResourcesHome extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        if (v.villagerInventory.getAll().isEmpty()) return null;
        Point home = v.housePoint;
        if (home != null) {
            return new GoalInformation(home, 3);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        Building home = v.getHomeBuilding();
        if (home != null) {
            for (Map.Entry<InvItem, Integer> entry : v.villagerInventory.getAll().entrySet()) {
                home.resManager.storeGoods(entry.getKey(), entry.getValue());
            }
        }
        v.villagerInventory.clear();
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 10; }
}
