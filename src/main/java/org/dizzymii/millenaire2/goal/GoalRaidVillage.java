package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Hostile villager raids another village, attacking near the target village's townhall.
 */
public class GoalRaidVillage extends Goal {

    @Override public boolean isFightingGoal() { return true; }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        // TODO: Determine target village to raid from raid coordinator
        Point th = v.townHallPoint;
        if (th != null) {
            return new GoalInformation(th, 10);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // TODO: Attack buildings/villagers of the target village
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 60; }
}
