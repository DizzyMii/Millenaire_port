package org.dizzymii.millenaire2.goal.generic;

import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Data-driven visit goal: walk to a building or villager and wait.
 * Covers: gopray, patrol, inspectconstruction, goplay, godrink, etc.
 */
public class GoalGenericVisit extends GoalGeneric {

    @Override
    public boolean performAction(MillVillager villager) {
        // Visit goals just wait at the destination for the configured duration
        // The action duration handles the wait; when called, we're done
        return true;
    }

    @Override
    public boolean canBeDoneAtNight() {
        // Visit goals that are leisure can be done at night
        return leasure;
    }
}
