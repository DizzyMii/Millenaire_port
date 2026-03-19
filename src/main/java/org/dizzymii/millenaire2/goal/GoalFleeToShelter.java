package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

import javax.annotation.Nullable;

/**
 * Civilian villagers flee to their home (or townhall) when the village is under attack.
 * They stay inside until the raid ends.
 */
public class GoalFleeToShelter extends Goal {

    @Override public boolean canBeDoneAtNight() { return true; }
    @Override public boolean isFightingGoal() { return false; }
    @Override public boolean isInterruptedByRaid() { return false; }

    @Override
    @Nullable
    public GoalInformation getDestination(MillVillager villager) {
        // Flee to home, or townhall if no home
        Point home = villager.housePoint;
        if (home != null) {
            return new GoalInformation(home, 1);
        }
        Point th = villager.townHallPoint;
        if (th != null) {
            return new GoalInformation(th, 1);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager villager) {
        // Stay at shelter until raid is over
        Building th = villager.getTownHallBuilding();
        if (th == null || !th.underAttack) {
            return true; // Raid over or no village
        }
        // Already at destination — just wait
        return false;
    }

    @Override
    public int actionDuration(MillVillager villager) { return 40; }
}
