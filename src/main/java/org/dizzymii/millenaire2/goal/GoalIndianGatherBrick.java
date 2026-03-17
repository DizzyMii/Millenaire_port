package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Indian villager gathers dried bricks from drying area and brings them to storage.
 */
public class GoalIndianGatherBrick extends Goal {

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
        // TODO: Collect dried bricks from drying zone
        InvItem brick = InvItem.get("minecraft:brick");
        if (brick != null) v.addToInv(brick, 2 + v.level().random.nextInt(3));
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 20; }
}
