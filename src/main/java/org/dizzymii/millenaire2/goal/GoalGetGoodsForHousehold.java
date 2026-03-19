package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

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
        Building th = v.getTownHallBuilding();
        GoalActionSupport.TransferChoice choice = GoalActionSupport.firstAvailableStoredGoods(th, 4);
        if (th == null || choice == null) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "get_goods_for_household_" + choice.item().key,
                VillagerActions.takeStoredGoods(th, choice.item(), choice.amount()))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 15); }
}
