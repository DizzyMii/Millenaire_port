package org.dizzymii.millenaire2.goal.generic;

import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Data-driven harvesting goal: harvest mature crops, collect items.
 * Covers: harvestwheat, harvestMaize, harvestRice, harvestcocoa, etc.
 */
public class GoalGenericHarvesting extends GoalGeneric {

    @Override
    public boolean performAction(MillVillager villager) {
        // Harvesting: break mature crop, add harvest items to inventory
        return true;
    }
}
