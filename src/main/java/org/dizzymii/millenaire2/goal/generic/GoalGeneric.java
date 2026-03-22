package org.dizzymii.millenaire2.goal.generic;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;

/**
 * Data-driven goal loaded from config files.
 * Ported from org.millenaire.common.goal.generic.GoalGeneric.
 */
public class GoalGeneric extends Goal {

    public static void loadGenericGoals(java.util.HashMap<String, Goal> map) {
        // Generic goals are loaded from culture config files during Culture.load()
        // This method is called at startup as an entry point for any additional data-driven goals
        org.dizzymii.millenaire2.util.MillLog.minor(GoalGeneric.class, "Generic goal loader initialised");
    }

    @Override public GoalInformation getDestination(MillVillager v) { return null; }
    @Override public boolean performAction(MillVillager v) { return false; }
}
