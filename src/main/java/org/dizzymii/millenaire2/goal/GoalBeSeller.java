package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager goes to their shop/stall and waits for player customers.
 */
public class GoalBeSeller extends Goal {

    { this.minimumHour = 8; this.maximumHour = 18; }

    @Override
    public GoalInformation getDestination(MillVillager villager) throws Exception {
        // Seller should go to their house (which is the shop)
        Point home = villager.getHousePoint();
        if (home != null) {
            return new GoalInformation(home, 3);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        // Stand at shop and wait — goal completes after a while to allow other goals
        villager.setStopMoving(true);
        long elapsed = villager.level().getGameTime() - villager.getGoalStarted();
        if (elapsed > 1200) { // ~60 seconds
            villager.setStopMoving(false);
            return true;
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager villager) { return 40; }

    @Override
    public boolean allowRandomMoves() { return true; }
}
