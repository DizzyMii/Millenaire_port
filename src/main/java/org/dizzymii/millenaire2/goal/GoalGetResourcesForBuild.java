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
        if (th == null) return null;

        // Only fetch resources if there is active construction in the village
        org.dizzymii.millenaire2.village.Building thBuilding = v.getTownHallBuilding();
        if (thBuilding == null) return null;
        boolean anyConstruction = false;
        if (thBuilding.mw != null) {
            for (org.dizzymii.millenaire2.village.Building b : thBuilding.mw.allBuildings()) {
                if (b.isUnderConstruction() && thBuilding.isSameVillage(b)) {
                    anyConstruction = true;
                    break;
                }
            }
        }
        if (!anyConstruction) return null;

        return new GoalInformation(th, 4);
    }

    @Override
    public boolean performAction(MillVillager v) {
        org.dizzymii.millenaire2.village.Building th = v.getTownHallBuilding();
        if (th != null) {
            // Take up to 16 of the most available resource from townhall for construction
            for (java.util.Map.Entry<org.dizzymii.millenaire2.item.InvItem, Integer> entry : th.resManager.resources.entrySet()) {
                int take = Math.min(entry.getValue(), 16);
                if (take > 0 && th.resManager.takeGoods(entry.getKey(), take)) {
                    v.addToInv(entry.getKey(), take);
                    break; // One stack per trip
                }
            }
        }
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 15; }
}
