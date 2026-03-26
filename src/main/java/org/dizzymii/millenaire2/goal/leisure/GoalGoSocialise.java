package org.dizzymii.millenaire2.goal.leisure;

import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.Point;

import java.util.List;

/**
 * Villager walks to the town center or a gathering spot and socialises with other villagers.
 */
public class GoalGoSocialise extends Goal {
    public GoalGoSocialise() { this.leisure = true; }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        // Head toward the townhall area as the social gathering point
        Point th = v.townHallPoint;
        if (th != null) {
            return new GoalInformation(th, 8);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        // Look around at nearby villagers — simple presence check
        AABB area = v.getBoundingBox().inflate(10);
        List<MillVillager> nearby = v.level().getEntitiesOfClass(MillVillager.class, area, e -> e != v);
        // Socialising happens by proximity; just wait a bit
        long elapsed = v.level().getGameTime() - v.goalStarted;
        if (elapsed > 400) { // ~20 seconds
            return true;
        }
        return false;
    }

    @Override
    public boolean allowRandomMoves() { return true; }

    @Override
    public int actionDuration(MillVillager v) { return 40; }
}
