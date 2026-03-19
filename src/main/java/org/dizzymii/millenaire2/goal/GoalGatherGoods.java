package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

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
        Building th = v.getTownHallBuilding();
        GoalActionSupport.TransferChoice choice = GoalActionSupport.firstAvailableStoredGoods(th, 8);
        if (th == null || choice == null) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "gather_goods_" + choice.item().key,
                VillagerActions.takeStoredGoods(th, choice.item(), choice.amount()))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 20); }
}
