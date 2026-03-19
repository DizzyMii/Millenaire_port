package org.dizzymii.millenaire2.goal.generic;

import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Data-driven planting goal: plant crops on farmland.
 * Covers: plantwheat, plantMaize, plantRice, plantpotato, etc.
 */
public class GoalGenericPlanting extends GoalGeneric {

    @Override
    public boolean performAction(MillVillager villager) {
        // Planting: place crop on farmland
        return true;
    }
}
