package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;

public class GoalSleep extends Goal {
    @Override public boolean canBeDoneAtNight() { return true; }
    @Override public boolean canBeDoneInDayTime() { return false; }
    @Override public GoalInformation getDestination(MillVillager villager) throws Exception { return null; }
    @Override public boolean performAction(MillVillager villager) throws Exception { return false; }
}
