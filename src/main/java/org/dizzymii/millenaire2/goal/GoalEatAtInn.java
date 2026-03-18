package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager goes home or to the inn to eat a meal.
 * In the original Millenaire, villagers eat at their home building or
 * at a tavern/inn during morning and evening hours.
 */
public class GoalEatAtInn extends Goal {

    public GoalEatAtInn() { this.leasure = true; }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        // Prefer home building, fall back to townhall
        Point home = v.housePoint;
        if (home != null) return new GoalInformation(home, 3);
        Point th = v.townHallPoint;
        if (th != null) return new GoalInformation(th, 5);
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // Eating is a timed action — villager stays put for a while
        long elapsed = v.level().getGameTime() - v.goalStarted;
        if (elapsed > 200) { // ~10 seconds
            // Restore a small amount of health as a meal benefit
            if (v.getHealth() < v.getMaxHealth()) {
                v.heal(2.0f);
            }
            return true;
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager v) { return 30; }
}
