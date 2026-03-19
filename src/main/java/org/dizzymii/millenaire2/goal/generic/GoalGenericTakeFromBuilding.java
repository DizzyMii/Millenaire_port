package org.dizzymii.millenaire2.goal.generic;

import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Data-driven take-from-building goal: take items from a building's inventory.
 * Covers: fetchbreadbyz, fetchironbyz, fetchsandstonebyz, etc.
 */
public class GoalGenericTakeFromBuilding extends GoalGeneric {

    @Override
    public boolean performAction(MillVillager villager) {
        // Take specified items from building inventory
        return true;
    }
}
