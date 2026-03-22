package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

public class GoalSleep extends Goal {
    @Override public boolean canBeDoneAtNight() { return true; }
    @Override public boolean canBeDoneInDayTime() { return false; }
    @Override public boolean isInterruptedByRaid() { return true; }

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
        // At home — just stay put until daytime
        villager.setStopMoving(true);
        if (villager.level().isDay()) {
            villager.setStopMoving(false);
            return true; // Done sleeping
        }
        return false; // Keep sleeping
    }

    @Override
    public int actionDuration(MillVillager villager) { return 100; }
}
