package org.dizzymii.millenaire2.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Defend village from hostile mobs and enemy raiding villagers.
 * Military villagers actively seek targets; civilians only fight back when attacked.
 */
public class GoalDefendVillage extends Goal {
    @Override public boolean canBeDoneAtNight() { return true; }
    @Override public boolean isFightingGoal() { return true; }
    @Override public boolean isInterruptedByRaid() { return false; }

    @Override
    public GoalInformation getDestination(MillVillager villager) {
        LivingEntity target = findBestTarget(villager);
        if (target != null) {
            return new GoalInformation(new Point(target.blockPosition()), 2);
        }
        // If attacked, target attacker
        if (villager.getLastHurtByMob() != null) {
            LivingEntity attacker = villager.getLastHurtByMob();
            return new GoalInformation(new Point(attacker.blockPosition()), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager villager) {
        LivingEntity target = findBestTarget(villager);
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

    @Nullable
    private LivingEntity findBestTarget(MillVillager villager) {
        AABB searchArea = villager.getBoundingBox().inflate(MillVillager.ATTACK_RANGE_DEFENSIVE);

        // Priority 1: enemy raiding villagers
        List<MillVillager> enemyVillagers = villager.level().getEntitiesOfClass(MillVillager.class, searchArea,
                e -> e != villager && e.isAlive() && isEnemy(villager, e));
        if (!enemyVillagers.isEmpty()) {
            return findNearest(villager, enemyVillagers);
        }

        // Priority 2: hostile mobs
        List<Monster> hostiles = villager.level().getEntitiesOfClass(Monster.class, searchArea);
        if (!hostiles.isEmpty()) {
            return findNearest(villager, hostiles);
        }

        return null;
    }

    private <T extends LivingEntity> T findNearest(MillVillager villager, List<T> entities) {
        T nearest = entities.get(0);
        double nearestDist = villager.distanceToSqr(nearest);
        for (T e : entities) {
            double d = villager.distanceToSqr(e);
            if (d < nearestDist) {
                nearestDist = d;
                nearest = e;
            }
        }
        return nearest;
    }

    /**
     * A villager is an enemy if they belong to a different village that is currently
     * attacking ours (i.e. they are raiding).
     */
    private boolean isEnemy(MillVillager self, MillVillager other) {
        // Different village
        if (self.townHallPoint != null && self.townHallPoint.equals(other.townHallPoint)) {
            return false; // Same village, not enemy
        }
        // Check if the other villager's village is raiding ours
        Building otherTh = other.getTownHallBuilding();
        if (otherTh != null && otherTh.raidTarget != null
                && self.townHallPoint != null && otherTh.raidTarget.equals(self.townHallPoint)) {
            return true;
        }
        // Check if our village is marked under attack
        Building selfTh = self.getTownHallBuilding();
        if (selfTh != null && selfTh.underAttack) {
            // Any foreign villager near us during an attack is hostile
            return true;
        }
        return false;
    }
}
