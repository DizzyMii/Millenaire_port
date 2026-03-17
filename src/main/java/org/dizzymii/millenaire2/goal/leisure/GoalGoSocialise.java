package org.dizzymii.millenaire2.goal.leisure;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;

public class GoalGoSocialise extends Goal {
    public GoalGoSocialise() { this.leasure = true; }
    @Override public GoalInformation getDestination(MillVillager v) { return null; }
    @Override public boolean performAction(MillVillager v) { return false; }
}
