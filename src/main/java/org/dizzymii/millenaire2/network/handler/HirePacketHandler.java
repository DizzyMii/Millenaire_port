package org.dizzymii.millenaire2.network.handler;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.UserProfile;

/**
 * Handles hire/release/extend military GUI actions from the client.
 */
public final class HirePacketHandler {

    private HirePacketHandler() {}

    public static void handle(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            MillWorldData mw = MillWorldData.get(player.serverLevel());
            if (mw == null) return;
            UserProfile profile = mw.getOrCreateProfile(player.getUUID(), player.getName().getString());

            switch (actionId) {
                case MillPacketIds.GUIACTION_HIRE_HIRE -> handleHire(player, profile, r);
                case MillPacketIds.GUIACTION_HIRE_RELEASE -> handleRelease(player);
                case MillPacketIds.GUIACTION_HIRE_EXTEND -> handleExtend(player);
                case MillPacketIds.GUIACTION_TOGGLE_STANCE -> handleToggleStance(player, r);
                default -> MillLog.warn("HirePacketHandler", "Unknown hire action: " + actionId);
            }
        } catch (Exception e) {
            MillLog.error("HirePacketHandler", "Error handling hire action", e);
        } finally {
            r.release();
        }
    }

    private static void handleHire(ServerPlayer player, UserProfile profile, PacketDataHelper.Reader r) {
        String unitType = r.readString();
        int cost = switch (unitType) {
            case "soldier" -> 64;
            case "archer" -> 96;
            case "knight" -> 128;
            default -> 0;
        };
        if (cost == 0) {
            player.sendSystemMessage(Component.literal("\u00a7c[Millénaire] Unknown unit type: " + unitType));
            return;
        }
        if (profile.deniers < cost) {
            player.sendSystemMessage(Component.literal(
                    "\u00a7c[Millénaire] Not enough deniers (need " + cost + ", have " + profile.deniers + ")"));
            return;
        }
        profile.deniers -= cost;
        MillLog.minor("HirePacketHandler", "Player " + player.getName().getString()
                + " hired " + unitType + " for " + cost + " deniers");
        player.sendSystemMessage(Component.literal(
                "\u00a76[Millénaire]\u00a7r Hired a " + unitType + " for " + cost + " deniers."));
    }

    private static void handleRelease(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("\u00a76[Millénaire]\u00a7r Hired soldier released."));
    }

    private static void handleExtend(ServerPlayer player) {
        player.sendSystemMessage(Component.literal("\u00a76[Millénaire]\u00a7r Hire extended."));
    }

    private static void handleToggleStance(ServerPlayer player, PacketDataHelper.Reader r) {
        int stance = r.readInt();
        String stanceName = stance == 0 ? "Patrol" : "Defend";
        player.sendSystemMessage(Component.literal(
                "\u00a76[Millénaire]\u00a7r Military stance set to: " + stanceName));
    }
}
