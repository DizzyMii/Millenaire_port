package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

/**
 * Villager collects construction resources from the townhall for a building project.
 */
public class GoalGetResourcesForBuild extends Goal {

    { this.tags.add(TAG_CONSTRUCTION); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Point th = v.townHallPoint;
        if (th != null && v.constructionJobId >= 0) {
            return new GoalInformation(th, 4);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        Building th = v.getTownHallBuilding();
        GoalActionSupport.TransferChoice choice = GoalActionSupport.firstAvailableStoredGoods(th, 16);
        if (th == null || choice == null) {
            return true;
        }
        return switch (GoalActionSupport.advanceAction(v, "get_resources_for_build_" + choice.item().key,
                VillagerActions.takeStoredGoods(th, choice.item(), choice.amount()))) {
            case RUNNING -> false;
            case SUCCESS, FAILED -> true;
        };
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 15); }
}
