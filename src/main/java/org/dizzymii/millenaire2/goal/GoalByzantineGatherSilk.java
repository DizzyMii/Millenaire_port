package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

/**
 * Byzantine villager gathers silk (string) - simulates silk worm farming.
 */
public class GoalByzantineGatherSilk extends Goal {

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
        InvItem string = InvItem.get("minecraft:string");
        if (string != null) v.addToInv(string, 1 + v.level().random.nextInt(3));
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 40; }
}
