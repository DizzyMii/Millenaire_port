package org.dizzymii.millenaire2.goal;
import org.dizzymii.millenaire2.entity.MillVillager;
public class GoalHuntMonster extends Goal {
    @Override public boolean isFightingGoal() { return true; }
    @Override public GoalInformation getDestination(MillVillager v) { return null; }
    @Override public boolean performAction(MillVillager v) { return false; }
}
