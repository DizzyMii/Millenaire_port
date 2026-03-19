package org.dizzymii.millenaire2.goal.generic;

import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Data-driven cooking goal: put items in furnace at a building.
 * Covers: cookchicken, cooksand, cookstone, cookpork, etc.
 */
public class GoalGenericCooking extends GoalGeneric {

    @Override
    public boolean performAction(MillVillager villager) {
        // Cooking: place itemToCook into furnace, wait for result
        return true;
    }
}
