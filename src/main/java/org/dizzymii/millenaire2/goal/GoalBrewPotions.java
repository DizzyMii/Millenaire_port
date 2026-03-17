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
        // Consume ingredients from home building and produce potions
        org.dizzymii.millenaire2.village.Building home = v.getHomeBuilding();
        if (home != null) {
            // Try to consume nether wart + glass bottle to produce a potion
            org.dizzymii.millenaire2.item.InvItem wart = org.dizzymii.millenaire2.item.InvItem.get("minecraft:nether_wart");
            org.dizzymii.millenaire2.item.InvItem bottle = org.dizzymii.millenaire2.item.InvItem.get("minecraft:glass_bottle");
            org.dizzymii.millenaire2.item.InvItem potion = org.dizzymii.millenaire2.item.InvItem.get("minecraft:potion");
            if (wart != null && bottle != null && potion != null
                    && home.resManager.takeGoods(wart, 1)
                    && home.resManager.takeGoods(bottle, 1)) {
                home.resManager.storeGoods(potion, 1);
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 60; }
}
