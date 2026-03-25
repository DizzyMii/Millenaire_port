package org.dizzymii.millenaire2.network.handlers;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.world.MillWorldData;

/**
 * Handles the negation wand action that removes a nearby village.
 * Dispatched from ServerPacketHandler for GUIACTION_NEGATION_WAND.
 */
public final class NegationWandHandler {

    private NegationWandHandler() {}

    public static void handle(byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
        if (mw == null) return;

        net.minecraft.core.BlockPos playerPos = player.blockPosition();
        Point playerPoint = new Point(playerPos.getX(), playerPos.getY(), playerPos.getZ());

        // Find the nearest village within 200 blocks
        Building nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (Building b : mw.allBuildings()) {
            if (!b.isTownhall || b.getPos() == null) continue;
            double dist = playerPoint.distanceTo(b.getPos());
            if (dist < 200 && dist < nearestDist) {
                nearest = b;
                nearestDist = dist;
            }
        }

        if (nearest == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "\u00a7c[Millénaire] No village found within 200 blocks."));
            return;
        }

        String villageName = nearest.getName() != null ? nearest.getName() : "Unknown";

        // Remove all buildings belonging to this village
        java.util.List<Point> toRemove = new java.util.ArrayList<>();
        Point thPos = nearest.getPos();
        for (var entry : mw.getBuildingsMap().entrySet()) {
            Building b = entry.getValue();
            Point bTh = b.isTownhall ? b.getPos() : b.getTownHallPos();
            if (bTh != null && bTh.equals(thPos)) {
                toRemove.add(entry.getKey());
            }
        }
        for (Point key : toRemove) {
            mw.removeBuilding(key);
        }
        mw.setDirty();

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "\u00a76[Millénaire]\u00a7r Village '" + villageName + "' removed (" + toRemove.size() + " buildings)."));
        MillLog.minor("NegationWandHandler", "Negation wand: removed village '" + villageName
                + "' (" + toRemove.size() + " buildings) by " + player.getName().getString());
    }
}
