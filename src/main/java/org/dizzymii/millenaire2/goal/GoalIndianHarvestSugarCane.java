package org.dizzymii.millenaire2.goal;
import org.dizzymii.millenaire2.entity.MillVillager;
public class GoalIndianHarvestSugarCane extends Goal {
    @Override public GoalInformation getDestination(MillVillager v) { return null; }
    @Override public boolean performAction(MillVillager v) { return false; }
}
