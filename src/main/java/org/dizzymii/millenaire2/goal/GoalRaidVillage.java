package org.dizzymii.millenaire2.goal;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

/**
 * Hostile villager raids another village, attacking near the target village's townhall.
 */
public class GoalRaidVillage extends Goal {

    @Override public boolean isFightingGoal() { return true; }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        // Raid villagers head toward their own townhall area (raid coordinator assigns target later)
        Point th = v.getTownHallPoint();
        if (th != null) {
            return new GoalInformation(th, 10);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // Attack nearest living entity that isn't a friendly villager
        net.minecraft.world.phys.AABB area = v.getBoundingBox().inflate(MillVillager.ATTACK_RANGE);
        java.util.List<net.minecraft.world.entity.LivingEntity> targets =
                v.level().getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, area,
                        e -> e != v && e.isAlive() && !(e instanceof MillVillager));
        if (targets.isEmpty()) return true; // No targets, raid over

        net.minecraft.world.entity.LivingEntity nearest = targets.get(0);
        double nearestDist = v.distanceToSqr(nearest);
        for (net.minecraft.world.entity.LivingEntity e : targets) {
            double d = v.distanceToSqr(e);
            if (d < nearestDist) { nearestDist = d; nearest = e; }
        }
        if (nearestDist <= 4.0) {
            v.doHurtTarget(nearest);
        } else {
            v.getNavigation().moveTo(nearest, 1.2);
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager v) { return 60; }
}
