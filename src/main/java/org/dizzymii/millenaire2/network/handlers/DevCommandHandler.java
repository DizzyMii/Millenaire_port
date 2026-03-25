package org.dizzymii.millenaire2.network.handlers;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.util.MillLog;

/**
 * Handles developer/debug commands sent by operator clients.
 * Only players with permission level ≥ 2 may trigger these actions.
 * Dispatched from ServerPacketHandler for PACKET_DEVCOMMAND.
 */
public final class DevCommandHandler {

    private DevCommandHandler() {}

    public static void handle(int commandId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        if (!player.hasPermissions(2)) {
            MillLog.warn("DevCommandHandler", "Non-op player tried dev command: " + player.getName().getString());
            return;
        }

        switch (commandId) {
            case MillPacketIds.DEV_COMMAND_TOGGLE_AUTO_MOVE:
                MillLog.minor("DevCommandHandler", "Toggle auto-move for " + player.getName().getString());
                toggleNearestVillagerAutoMove(player);
                break;
            case MillPacketIds.DEV_COMMAND_TEST_PATH:
                MillLog.minor("DevCommandHandler", "Test path for " + player.getName().getString());
                triggerTestPathfinding(player);
                break;
            default:
                MillLog.warn("DevCommandHandler", "Unknown dev command: " + commandId);
                break;
        }
    }

    private static void toggleNearestVillagerAutoMove(ServerPlayer player) {
        java.util.List<MillVillager> nearby = player.level().getEntitiesOfClass(
                MillVillager.class, player.getBoundingBox().inflate(16));
        if (!nearby.isEmpty()) {
            MillVillager nearest = nearby.get(0);
            nearest.stopMoving = !nearest.stopMoving;
            MillLog.minor("DevCommandHandler", "Toggled auto-move on " + nearest.getFirstName()
                    + " -> stopMoving=" + nearest.stopMoving);
        }
    }

    private static void triggerTestPathfinding(ServerPlayer player) {
        java.util.List<MillVillager> nearby = player.level().getEntitiesOfClass(
                MillVillager.class, player.getBoundingBox().inflate(32));
        if (!nearby.isEmpty()) {
            MillVillager villager = nearby.get(0);
            villager.getNavigation().moveTo(player.getX(), player.getY(), player.getZ(), 1.0);
            MillLog.minor("DevCommandHandler", "Test path: " + villager.getFirstName()
                    + " -> player pos");
        }
    }
}
