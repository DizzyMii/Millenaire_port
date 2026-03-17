package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

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
        // TODO: Integrate with village resource system to assign gather targets
        return true; // Complete immediately for now
    }

    @Override
    public int actionDuration(MillVillager v) { return 20; }
}
