package org.dizzymii.millenaire2.goal;

import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

import java.util.List;

/**
 * Warrior villager actively patrols and hunts hostile mobs within range.
 */
public class GoalHuntMonster extends Goal {

    @Override public boolean isFightingGoal() { return true; }
    @Override public boolean canBeDoneAtNight() { return true; }
    @Override public boolean isInterruptedByRaid() { return false; }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        Monster target = findTarget(v);
        if (target != null) {
            return new GoalInformation(new Point(target.blockPosition()), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        Monster target = findTarget(v);
        if (target == null || !target.isAlive()) {
            return true;
        }
        double dist = v.distanceToSqr(target);
        if (dist <= 4.0) {
            v.doHurtTarget(target);
        } else {
            v.getNavigation().moveTo(target, 1.2);
        }
        return false;
    }

    @Override
    public int actionDuration(MillVillager v) { return 5; }

    private Monster findTarget(MillVillager v) {
        AABB area = v.getBoundingBox().inflate(MillVillager.ATTACK_RANGE);
        List<Monster> mobs = v.level().getEntitiesOfClass(Monster.class, area);
        Monster nearest = null;
        double best = Double.MAX_VALUE;
        for (Monster m : mobs) {
            double d = v.distanceToSqr(m);
            if (d < best) { best = d; nearest = m; }
        }
        return nearest;
    }
}
