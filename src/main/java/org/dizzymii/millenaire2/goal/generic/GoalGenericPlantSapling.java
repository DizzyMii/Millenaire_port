package org.dizzymii.millenaire2.goal.generic;

import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Data-driven sapling planting goal: plant tree saplings.
 * Covers: plantsaplingappletreeorchard, plantsaplingappletreehome, etc.
 */
public class GoalGenericPlantSapling extends GoalGeneric {

    @Override
    public boolean performAction(MillVillager villager) {
        // Plant sapling on valid ground
        return true;
    }
}
