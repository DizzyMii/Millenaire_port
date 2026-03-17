/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.goal;

import java.util.HashMap;
import java.util.List;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="Gets goods required by the household (food, inputs for crafting...) from the TH or shops and loads them in the inventory. Paired with delivergoodshousehold, which delivers them.")
public class GoalGetGoodsForHousehold
extends Goal {
    public GoalGetGoodsForHousehold() {
        this.travelBookShow = false;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        return this.getDestination(villager, false);
    }

    public Goal.GoalInformation getDestination(MillVillager villager, boolean test) throws Exception {
        List<Building> buildings = null;
        boolean delayOver = !test ? true : (!villager.lastGoalTime.containsKey(this) ? true : villager.field_70170_p.func_72820_D() > villager.lastGoalTime.get(this) + 2000L);
        for (MillVillager v : villager.getHouse().getKnownVillagers()) {
            HashMap<InvItem, Integer> goods = v.requiresGoods();
            int nb = 0;
            for (InvItem key : goods.keySet()) {
                if (villager.getHouse().countGoods(key.getItem(), key.meta) >= goods.get(key) / 2) continue;
                if (buildings == null) {
                    buildings = villager.getTownHall().getBuildings();
                }
                for (Building building : buildings) {
                    int nbav = building.nbGoodAvailable(key, false, false, false);
                    if (nbav <= 0 || building == villager.getHouse() || !delayOver && (nb += nbav) <= 16) continue;
                    return this.packDest(building.getResManager().getSellingPos(), building);
                }
            }
        }
        return null;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) throws Exception {
        return this.getDestination(villager, true) != null;
    }

    @Override
    public boolean lookAtGoal() {
        return true;
    }

    @Override
    public String nextGoal(MillVillager villager) throws Exception {
        return Goal.deliverGoodsHousehold.key;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        Building shop = villager.getGoalBuildingDest();
        if (shop == null || shop == villager.getHouse()) {
            return true;
        }
        for (MillVillager v : villager.getHouse().getKnownVillagers()) {
            HashMap<InvItem, Integer> goods = v.requiresGoods();
            for (InvItem key : goods.keySet()) {
                if (villager.getHouse().countGoods(key.getItem(), key.meta) >= goods.get(key)) continue;
                int nb = Math.min(shop.nbGoodAvailable(key, false, false, false), goods.get(key));
                villager.takeFromBuilding(shop, key.getItem(), key.meta, nb);
            }
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) throws Exception {
        int nb = 0;
        List<Building> shops = villager.getTownHall().getShops();
        for (MillVillager v : villager.getHouse().getKnownVillagers()) {
            HashMap<InvItem, Integer> goods = v.requiresGoods();
            for (InvItem key : goods.keySet()) {
                if (villager.getHouse().countGoods(key.getItem(), key.meta) >= goods.get(key) / 2) continue;
                for (Building shop : shops) {
                    int nbav = shop.nbGoodAvailable(key, false, false, false);
                    if (nbav <= 0 || shop == villager.getHouse()) continue;
                    nb += nbav;
                }
            }
        }
        return nb * 20;
    }
}

