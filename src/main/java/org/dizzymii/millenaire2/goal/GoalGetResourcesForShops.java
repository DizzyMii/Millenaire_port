package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

/**
 * Villager collects trade goods from the townhall to stock their shop.
 */
public class GoalGetResourcesForShops extends Goal {

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
        Building th = v.getTownHallBuilding();
        GoalActionSupport.TransferChoice choice = GoalActionSupport.firstAvailableStoredGoods(th, 8);
        if (th == null || choice == null) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "get_resources_for_shop_" + choice.item().key,
                VillagerActions.takeStoredGoods(th, choice.item(), choice.amount()))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 15); }
}
