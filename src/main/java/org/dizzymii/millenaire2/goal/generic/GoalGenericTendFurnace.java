package org.dizzymii.millenaire2.goal.generic;

import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Data-driven tend-furnace goal: manage a furnace in a building.
 * Covers: tendfurnace, tendfurnacekitchen, tendfurnacebrickkiln, etc.
 */
public class GoalGenericTendFurnace extends GoalGeneric {

    @Override
    public boolean performAction(MillVillager villager) {
        // Tend furnace: ensure fuel, check output
        return true;
    }
}
