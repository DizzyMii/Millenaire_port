package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;

public class GoalBeSeller extends Goal {
    @Override public GoalInformation getDestination(MillVillager villager) throws Exception { return null; }
    @Override public boolean performAction(MillVillager villager) throws Exception { return false; }
}
