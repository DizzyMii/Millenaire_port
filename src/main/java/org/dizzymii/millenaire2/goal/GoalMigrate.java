package org.dizzymii.millenaire2.goal;

import net.minecraft.server.level.ServerLevel;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.VillagerRecord;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;

/**
 * Villager migrates from one village to another.
 * Triggered when a villager's record has originalVillagePos set to a different village
 * than current townHallPoint, or when the village scheduler assigns migration.
 * The villager walks to the target village's townhall and re-registers there.
 */
public class GoalMigrate extends Goal {

    @Override public boolean canBeDoneAtNight() { return false; }

    @Override
    @Nullable
    public GoalInformation getDestination(MillVillager v) {
        Point target = getMigrationTarget(v);
        if (target != null) {
            return new GoalInformation(target, 3);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        if (!(v.level() instanceof ServerLevel serverLevel)) return true;

        Point target = getMigrationTarget(v);
        if (target == null) return true;

        // Check if we've arrived (within 5 blocks of target)
        double dist = v.blockPosition().distSqr(new net.minecraft.core.BlockPos(target.x, target.y, target.z));
        if (dist > 25) {
            return false; // Still walking
        }

        // Arrived — reassign to new village
        MillWorldData mw = MillWorldData.get(serverLevel);
        Building newTownhall = mw.getBuilding(target);
        if (newTownhall == null) return true;

        // Update villager record
        VillagerRecord vr = mw.getVillagerRecord(v.getVillagerId());
        if (vr != null) {
            // Remove from old village
            Building oldTh = v.getTownHallBuilding();
            if (oldTh != null) {
                oldTh.getVillagerRecords().removeIf(r -> r.getVillagerId() == v.getVillagerId());
            }

            // Add to new village
            vr.setTownHallPos(target);
            newTownhall.getVillagerRecords().add(vr);
        }

        // Update entity
        v.townHallPoint = target;
        v.housePoint = null; // Will be assigned a new house later

        mw.setDirty();
        return true;
    }

    @Override
    public int actionDuration(MillVillager v) { return 40; }

    @Nullable
    private Point getMigrationTarget(MillVillager v) {
        if (!(v.level() instanceof ServerLevel serverLevel)) return null;
        MillWorldData mw = MillWorldData.get(serverLevel);
        VillagerRecord vr = mw.getVillagerRecord(v.getVillagerId());
        if (vr == null) return null;

        // Migration target is stored in originalVillagePos when it differs from current townhall
        if (vr.originalVillagePos != null && v.townHallPoint != null
                && !vr.originalVillagePos.equals(v.townHallPoint)) {
            return vr.originalVillagePos;
        }
        return null;
    }
}
