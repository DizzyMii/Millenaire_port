package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager goes to their home/workshop to retrieve the appropriate tool for their next job.
 */
public class GoalGetTool extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Point home = v.getHousePoint();
        if (home != null) {
            return new GoalInformation(home, 3);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // Equip a tool from the home building if the villager type needs tool categories
        if (v.getVillagerType() != null && !v.getVillagerType().toolsCategoriesNeeded.isEmpty()) {
            org.dizzymii.millenaire2.village.Building home = v.getHomeBuilding();
            if (home != null) {
                for (String toolCat : v.getVillagerType().toolsCategoriesNeeded) {
                    org.dizzymii.millenaire2.item.InvItem tool = org.dizzymii.millenaire2.item.InvItem.get(toolCat);
                    if (tool != null && v.countInv(tool) == 0 && home.resManager.takeGoods(tool, 1)) {
                        v.addToInv(tool, 1);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 10; }
}
