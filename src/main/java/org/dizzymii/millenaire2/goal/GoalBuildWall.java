package org.dizzymii.millenaire2.goal;

import net.minecraft.server.level.ServerLevel;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.VillageWallPlanner;
import org.dizzymii.millenaire2.village.VillageWallPlanner.WallSegment;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Villager builds wall segments around the village perimeter.
 * Finds the next unbuilt wall segment, walks to it, and constructs it.
 */
public class GoalBuildWall extends Goal {

    { this.tags.add(TAG_CONSTRUCTION); }

    @Override
    @Nullable
    public GoalInformation getDestination(MillVillager v) {
        WallSegment seg = findNextSegment(v);
        if (seg != null) {
            return new GoalInformation(seg.start, 3);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        if (!(v.level() instanceof ServerLevel serverLevel)) return true;

        WallSegment seg = findNextSegment(v);
        if (seg == null) return true;

        VillageWallPlanner.buildSegment(serverLevel, seg);
        return true; // One segment per action cycle
    }

    @Override
    public int actionDuration(MillVillager v) { return 60; }

    @Nullable
    private WallSegment findNextSegment(MillVillager v) {
        Building th = v.getTownHallBuilding();
        if (th == null || th.getPos() == null) return null;
        if (!(v.level() instanceof ServerLevel serverLevel)) return null;

        MillWorldData mw = MillWorldData.get(serverLevel);

        // Collect all village buildings
        List<Building> villageBuildings = new ArrayList<>();
        for (Building b : mw.allBuildings()) {
            if (th.isSameVillage(b)) {
                villageBuildings.add(b);
            }
        }

        // Plan wall (or retrieve cached plan — for now we recompute)
        List<WallSegment> segments = VillageWallPlanner.planWall(th, villageBuildings, 5);

        // Find first unbuilt segment
        for (WallSegment seg : segments) {
            if (!seg.built) return seg;
        }
        return null;
    }
}
