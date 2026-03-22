package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Travelling merchant visits a village building to conduct trade.
 */
public class GoalMerchantVisitBuilding extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        // Visit a random building — use townhall as hub for now
        Point th = v.getTownHallPoint();
        if (th != null) {
            return new GoalInformation(th, 5);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // Exchange: sell some inventory to the building, buy some from it
        org.dizzymii.millenaire2.village.Building th = v.getTownHallBuilding();
        if (th != null) {
            // Deposit what the merchant carries
            for (java.util.Map.Entry<org.dizzymii.millenaire2.item.InvItem, Integer> entry :
                    new java.util.ArrayList<>(v.villagerInventory.getAll().entrySet())) {
                int sell = Math.min(entry.getValue(), 4);
                if (sell > 0) {
                    v.removeFromInv(entry.getKey(), sell);
                    th.resManager.storeGoods(entry.getKey(), sell);
                }
            }
            // Pick up some goods from the building
            for (java.util.Map.Entry<org.dizzymii.millenaire2.item.InvItem, Integer> entry : th.resManager.resources.entrySet()) {
                int buy = Math.min(entry.getValue(), 4);
                if (buy > 0 && th.resManager.takeGoods(entry.getKey(), buy)) {
                    v.addToInv(entry.getKey(), buy);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 30; }
}
