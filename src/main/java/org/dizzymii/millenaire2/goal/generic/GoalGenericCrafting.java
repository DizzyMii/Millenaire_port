package org.dizzymii.millenaire2.goal.generic;

import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Data-driven crafting goal: consume input items, produce output items at a building.
 * Covers: makecider, makemayanaxe, makebread, makeleatherboots, etc.
 */
public class GoalGenericCrafting extends GoalGeneric {

    @Override
    public boolean performAction(MillVillager villager) {
        // Crafting: consume inputs from building/inventory, produce outputs
        // For now, just complete the action (item transfer will be refined later)
        return true;
    }
}
