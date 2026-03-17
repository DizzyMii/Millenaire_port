package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager walks to a construction site and places blocks step-by-step.
 * Integrates with ConstructionIP and BuildingProject for block sequencing.
 */
public class GoalConstructionStepByStep extends Goal {

    { this.tags.add(TAG_CONSTRUCTION); }

    @Override
    public GoalInformation getDestination(MillVillager villager) throws Exception {
        // Find a building that needs construction work
        Point townHall = villager.townHallPoint;
        if (townHall == null) return null;

        // TODO: Query village's BuildingProject list for pending construction
        //       For now, return townhall as placeholder destination when constructionJobId is set
        if (villager.constructionJobId >= 0) {
            return new GoalInformation(townHall, 5);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        // TODO: Get the ConstructionIP for the assigned job and place the next block
        //       For now, simulate construction by ticking the job counter
        long elapsed = villager.level().getGameTime() - villager.actionStart;
        if (elapsed > 40) {
            // Simulate placing a block — reset for next cycle
            villager.constructionJobId = -1;
            return true;
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager villager) { return 20; }
}
