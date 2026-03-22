package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Byzantine villager gathers snails for purple dye production.
 */
public class GoalByzantineGatherSnails extends Goal {

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Point home = v.getHousePoint();
        if (home != null) {
            return new GoalInformation(home, 5);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        InvItem dye = InvItem.get("minecraft:purple_dye");
        if (dye != null) v.addToInv(dye, 1);
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 40; }
}
