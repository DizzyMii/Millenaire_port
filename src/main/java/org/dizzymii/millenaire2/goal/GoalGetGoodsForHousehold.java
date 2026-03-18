package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager goes to the townhall to collect goods needed by their household.
 */
public class GoalGetGoodsForHousehold extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Point th = v.townHallPoint;
        if (th != null) {
            return new GoalInformation(th, 4);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        org.dizzymii.millenaire2.village.Building th = v.getTownHallBuilding();
        if (th != null) {
            for (java.util.Map.Entry<org.dizzymii.millenaire2.item.InvItem, Integer> entry : th.resManager.resources.entrySet()) {
                int take = Math.min(entry.getValue(), 4);
                if (take > 0 && th.resManager.takeGoods(entry.getKey(), take)) {
                    v.addToInv(entry.getKey(), take);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 15; }
}
