package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerActionRuntime;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.util.Point;

import java.util.Map;

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
        if (resolvePendingAction(v)) {
            return true;
        }
        Building home = v.getHomeBuilding();
        InvItem wart = InvItem.get("netherwart");
        InvItem bottle = InvItem.get("bottle");
        InvItem potion = InvItem.get("akwardpotion");
        if (home == null || wart == null || bottle == null || potion == null) {
            return true;
        }
        if (home.resManager.countGoods(wart) < 1 || home.resManager.countGoods(bottle) < 1) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "brew_potions",
                VillagerActions.transformStoredGoods(home, Map.of(wart, 1, bottle, 1), Map.of(potion, 1)))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 60); }

    private boolean resolvePendingAction(MillVillager villager) {
        VillagerActionRuntime runtime = villager.getActionRuntime();
        if (runtime.hasAction()) {
            return false;
        }
        String actionKey = runtime.getLastCompletedActionKey();
        VillagerActionRuntime.Result result = runtime.getLastResult();
        if (actionKey == null || result.status() == VillagerActionRuntime.Status.IDLE) {
            return false;
        }
        if ("brew_potions".equals(actionKey)) {
            runtime.reset(villager);
            return true;
        }
        return false;
    }
}
