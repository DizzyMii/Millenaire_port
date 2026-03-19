package org.dizzymii.millenaire2.goal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Hired villager follows the player who hired them.
 * The villager stays within a short distance and assists in combat if needed.
 * Set villager.hiredByPlayer to the player's UUID to activate.
 */
public class GoalFollowPlayer extends Goal {

    private static final double FOLLOW_DISTANCE_SQ = 4.0 * 4.0;
    private static final double MAX_TELEPORT_DISTANCE_SQ = 48.0 * 48.0;

    @Override public boolean canBeDoneAtNight() { return true; }
    @Override public boolean isInterruptedByRaid() { return false; }

    @Override
    @Nullable
    public GoalInformation getDestination(MillVillager v) {
        Player player = findHiringPlayer(v);
        if (player != null) {
            return new GoalInformation(new Point(player.blockPosition()), 2);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager v) {
        Player player = findHiringPlayer(v);
        if (player == null) {
            return true; // Player gone or not hired
        }

        double distSq = v.distanceToSqr(player);

        // Teleport if too far away
        if (distSq > MAX_TELEPORT_DISTANCE_SQ) {
            v.teleportTo(player.getX(), player.getY(), player.getZ());
            return false;
        }

        // Follow if too far
        if (distSq > FOLLOW_DISTANCE_SQ) {
            v.getNavigation().moveTo(player, 1.1);
        }

        return false; // Never finishes on its own — player must dismiss
    }

    @Override
    public int actionDuration(MillVillager v) { return 20; }

    @Nullable
    private Player findHiringPlayer(MillVillager v) {
        if (v.hiredBy == null || v.hiredBy.isEmpty()) return null;
        // Check if hire has expired
        if (v.hiredUntil > 0 && v.level().getGameTime() > v.hiredUntil) {
            v.hiredBy = null;
            return null;
        }
        if (!(v.level() instanceof ServerLevel serverLevel)) return null;
        try {
            UUID uuid = UUID.fromString(v.hiredBy);
            return serverLevel.getPlayerByUUID(uuid);
        } catch (IllegalArgumentException e) {
            // hiredBy might be a player name, search by name
            for (Player p : serverLevel.players()) {
                if (p.getName().getString().equals(v.hiredBy)) return p;
            }
            return null;
        }
    }
}
