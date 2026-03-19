package org.dizzymii.millenaire2.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Raiding villager paths toward the target village's townhall and attacks
 * enemy villagers and other living entities near it.
 */
public class GoalRaidVillage extends Goal {

    { this.tags.add(TAG_CONSTRUCTION); }

    @Override public boolean isFightingGoal() { return true; }
    @Override public boolean canBeDoneAtNight() { return true; }
    @Override public boolean isInterruptedByRaid() { return false; }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        // Head toward the raid target village, not own townhall
        Point raidTarget = getRaidTarget(v);
        if (raidTarget != null) {
            return new GoalInformation(raidTarget, 5);
        }
        // Fallback: own townhall
        Point th = v.townHallPoint;
        if (th != null) {
            return new GoalInformation(th, 10);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // Find nearest attackable entity: enemy villagers first, then other living entities
        AABB area = v.getBoundingBox().inflate(MillVillager.ATTACK_RANGE);
        LivingEntity target = findTarget(v, area);

        if (target == null || !target.isAlive()) {
            return true; // No targets in range
        }

        double dist = v.distanceToSqr(target);
        if (dist <= 4.0) {
            v.doHurtTarget(target);
        } else {
            v.getNavigation().moveTo(target, 1.3);
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager v) { return 60; }

    @Nullable
    private LivingEntity findTarget(MillVillager v, AABB area) {
        // Priority 1: enemy villagers (from the target village)
        List<MillVillager> villagers = v.level().getEntitiesOfClass(MillVillager.class, area,
                e -> e != v && e.isAlive() && !isFriendly(v, e));
        if (!villagers.isEmpty()) {
            return findNearest(v, villagers);
        }

        // Priority 2: any other living entity (players, iron golems, etc.)
        List<LivingEntity> others = v.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != v && e.isAlive() && !(e instanceof MillVillager));
        if (!others.isEmpty()) {
            return findNearest(v, others);
        }
        return null;
    }

    private <T extends LivingEntity> T findNearest(MillVillager v, List<T> entities) {
        T nearest = entities.get(0);
        double nearestDist = v.distanceToSqr(nearest);
        for (T e : entities) {
            double d = v.distanceToSqr(e);
            if (d < nearestDist) { nearestDist = d; nearest = e; }
        }
        return nearest;
    }

    private boolean isFriendly(MillVillager self, MillVillager other) {
        // Same townhall = friendly
        if (self.townHallPoint != null && self.townHallPoint.equals(other.townHallPoint)) {
            return true;
        }
        return false;
    }

    @Nullable
    private Point getRaidTarget(MillVillager v) {
        Building th = v.getTownHallBuilding();
        if (th != null && th.raidTarget != null) {
            return th.raidTarget;
        }
        return null;
    }
}
