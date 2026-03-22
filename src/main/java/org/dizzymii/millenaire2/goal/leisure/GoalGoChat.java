package org.dizzymii.millenaire2.goal.leisure;

import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.Point;

import java.util.List;

/**
 * Villager finds a nearby villager and walks to them for a chat.
 */
public class GoalGoChat extends Goal {
    public GoalGoChat() { this.leasure = true; }

    @Override
    public GoalInformation getDestination(MillVillager v) {
        MillVillager partner = findChatPartner(v);
        if (partner != null) {
            return new GoalInformation(new Point(partner.blockPosition()), 3);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        v.setStopMoving(true);
        long elapsed = v.level().getGameTime() - v.getGoalStarted();
        if (elapsed > 200) { // ~10 seconds
            v.setStopMoving(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean allowRandomMoves() { return true; }

    @Override
    public int actionDuration(MillVillager v) { return 20; }

    private MillVillager findChatPartner(MillVillager v) {
        AABB area = v.getBoundingBox().inflate(20);
        List<MillVillager> nearby = v.level().getEntitiesOfClass(MillVillager.class, area, e -> e != v);
        return nearby.isEmpty() ? null : nearby.get(0);
    }
}
