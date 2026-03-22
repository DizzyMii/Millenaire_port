package org.dizzymii.millenaire2.goal;

import net.minecraft.server.level.ServerLevel;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.ConstructionIP;

/**
 * Villager walks to a construction site and places blocks step-by-step.
 * Delegates actual block placement to the Building's ConstructionIP.
 */
public class GoalConstructionStepByStep extends Goal {

    { this.tags.add(TAG_CONSTRUCTION); }

    @Override
    public GoalInformation getDestination(MillVillager villager) throws Exception {
        // Find a building under construction that this villager can work on
        Building building = findConstructionSite(villager);
        if (building == null) return null;

        Point buildPos = building.getPos();
        if (buildPos == null) return null;

        return new GoalInformation(buildPos, building, 5);
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        GoalInformation info = villager.getGoalInformation();
        Building building = info != null ? info.targetBuilding : null;
        if (building == null) return true; // No building, done

        ConstructionIP cip = building.currentConstruction;
        if (cip == null || cip.isComplete()) return true; // Nothing to build

        if (!(villager.level() instanceof ServerLevel serverLevel)) return true;

        // Place a few blocks per action tick
        int placed = cip.placeBlocks(serverLevel, 3);
        if (placed > 0) {
            villager.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            if (building.getWorldData() != null) building.getWorldData().setDirty();
        }

        if (cip.isComplete()) {
            building.currentConstruction = null;
            if (building.getWorldData() != null) building.getWorldData().setDirty();
            return true;
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager villager) { return 15; }

    /**
     * Find a building under construction that this villager should work on.
     * Prioritises the villager's home building, then the town hall.
     */
    private Building findConstructionSite(MillVillager villager) {
        Building home = villager.getHomeBuilding();
        if (home != null && home.isUnderConstruction()) return home;

        Building th = villager.getTownHallBuilding();
        if (th != null && th.isUnderConstruction()) return th;

        return null;
    }
}
