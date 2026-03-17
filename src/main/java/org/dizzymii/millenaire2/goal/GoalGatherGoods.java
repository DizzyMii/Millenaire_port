package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager gathers goods/resources from the environment (e.g. crops, ores).
 * This is a generic gathering task assigned by the village economy.
 */
public class GoalGatherGoods extends Goal {

    { this.tags.add(TAG_AGRICULTURE); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        // Go to townhall area to pick up gathering assignments
        Point th = v.townHallPoint;
        if (th != null) {
            return new GoalInformation(th, 5);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // Gather resources from townhall's resource pool for the village economy
        org.dizzymii.millenaire2.village.Building th = v.getTownHallBuilding();
        if (th != null) {
            for (java.util.Map.Entry<org.dizzymii.millenaire2.item.InvItem, Integer> entry : th.resManager.resources.entrySet()) {
                int take = Math.min(entry.getValue(), 8);
                if (take > 0 && th.resManager.takeGoods(entry.getKey(), take)) {
                    v.addToInv(entry.getKey(), take);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 20; }
}
