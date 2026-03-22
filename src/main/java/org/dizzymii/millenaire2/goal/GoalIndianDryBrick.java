package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Indian villager dries mud bricks in the sun at their home.
 */
public class GoalIndianDryBrick extends Goal {

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
        // Produce dried bricks from sun-drying process
        InvItem brick = InvItem.get("minecraft:brick");
        if (brick != null) v.addToInv(brick, 1 + v.level().random.nextInt(2));
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 40; }
}
