package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Villager collects construction resources from the townhall for a building project.
 */
public class GoalGetResourcesForBuild extends Goal {

    { this.tags.add(TAG_CONSTRUCTION); }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Point th = v.townHallPoint;
        if (th != null && v.constructionJobId >= 0) {
            return new GoalInformation(th, 4);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // TODO: Transfer needed blocks from townhall storage to villager inventory
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 15; }
}
