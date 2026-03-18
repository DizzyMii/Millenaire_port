package org.dizzymii.millenaire2.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

import java.util.List;

public class GoalDefendVillage extends Goal {
    @Override public boolean canBeDoneAtNight() { return true; }
    @Override public boolean isFightingGoal() { return true; }
    @Override public boolean isInterruptedByRaid() { return false; }

    @Override
    public GoalInformation getDestination(MillVillager villager) {
        LivingEntity target = findNearestHostile(villager);
        if (target != null) {
            return new GoalInformation(new Point(target.blockPosition()), 2);
        }
        // If attacked by player, target attacker
        if (villager.getLastHurtByMob() != null) {
            LivingEntity attacker = villager.getLastHurtByMob();
            return new GoalInformation(new Point(attacker.blockPosition()), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager villager) {
        LivingEntity target = findNearestHostile(villager);
        if (target == null) {
            target = villager.getLastHurtByMob();
        }
        if (target == null || !target.isAlive()) {
            return true; // No more targets
        }

        double dist = villager.distanceToSqr(target);
        if (dist <= 4.0) {
            villager.doHurtTarget(target);
        } else {
            villager.getNavigation().moveTo(target, 1.2);
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager villager) { return 5; }

    private LivingEntity findNearestHostile(MillVillager villager) {
        AABB searchArea = villager.getBoundingBox().inflate(MillVillager.ATTACK_RANGE_DEFENSIVE);
        List<Monster> hostiles = villager.level().getEntitiesOfClass(Monster.class, searchArea);
        Monster nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Monster m : hostiles) {
            double d = villager.distanceToSqr(m);
            if (d < nearestDist) {
                nearestDist = d;
                nearest = m;
            }
        }
        return nearest;
    }
}
