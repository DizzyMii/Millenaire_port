package org.dizzymii.millenaire2.goal.generic;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;

/**
 * Data-driven goal loaded from config files.
 * Ported from org.millenaire.common.goal.generic.GoalGeneric.
 */
public class GoalGeneric extends Goal {

    public static void loadGenericGoals() {
        // TODO: Load generic goals from config/data files
    }

    @Override public GoalInformation getDestination(MillVillager v) { return null; }
    @Override public boolean performAction(MillVillager v) { return false; }
}
