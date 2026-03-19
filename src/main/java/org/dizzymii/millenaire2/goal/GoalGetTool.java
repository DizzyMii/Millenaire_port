package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.action.VillagerActions;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

/**
 * Villager goes to their home/workshop to retrieve the appropriate tool for their next job.
 */
public class GoalGetTool extends Goal {

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
        if (v.vtype == null || v.vtype.toolsCategoriesNeeded.isEmpty()) {
            return true;
        }
        Building home = v.getHomeBuilding();
        if (home == null) {
            return true;
        }
        for (String toolCat : v.vtype.toolsCategoriesNeeded) {
            InvItem tool = InvItem.get(toolCat);
            if (tool == null) {
                continue;
            }
            if (v.countInv(tool) == 0) {
                return switch (GoalActionSupport.advanceAction(v, "get_tool_" + tool.key,
                        VillagerActions.takeStoredGoods(home, tool, 1))) {
                    case RUNNING -> false;
                    case SUCCESS, FAILED -> true;
                };
            }
            if (!v.getSelectedInventoryItem().is(tool.getItem())) {
                return switch (GoalActionSupport.advanceAction(v, "equip_tool_" + tool.key,
                        VillagerActions.equip(tool.key))) {
                    case RUNNING -> false;
                    case SUCCESS, FAILED -> true;
                };
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return GoalActionSupport.runtimeBackedDuration(v, 10); }
}
