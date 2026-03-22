package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

public class GoalHide extends Goal {
    @Override public boolean canBeDoneAtNight() { return true; }
    @Override public boolean isInterruptedByRaid() { return false; }
    @Override public boolean isFightingGoal() { return false; }

    @Override
    public GoalInformation getDestination(MillVillager villager) {
        Point home = villager.getHousePoint();
        if (home != null) {
            return new GoalInformation(home, 3);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager villager) {
        villager.setStopMoving(true);
        // Stay hidden for a while, then check if danger has passed
        long elapsed = villager.level().getGameTime() - villager.getGoalStarted();
        if (elapsed > 600) { // ~30 seconds
            villager.setStopMoving(false);
            return true;
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager villager) { return 40; }
}
