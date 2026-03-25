package org.dizzymii.millenaire2.network.handlers;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.UserProfile;

/**
 * Handles villager hire actions from the client.
 * Covers GUIACTION_HIRE_HIRE, GUIACTION_HIRE_EXTEND, GUIACTION_HIRE_RELEASE,
 * and GUIACTION_TOGGLE_STANCE.
 */
public final class HireHandler {

    private HireHandler() {}

    public static void handle(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) return;
            UserProfile profile = mw.getOrCreateProfile(player.getUUID(), player.getName().getString());

            if (actionId == MillPacketIds.GUIACTION_HIRE_HIRE) {
                String unitType = r.readString();
                int cost = switch (unitType) {
                    case "soldier" -> 64;
                    case "archer" -> 96;
                    case "knight" -> 128;
                    default -> 0;
                };
                if (cost == 0) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "\u00a7c[Millénaire] Unknown unit type: " + unitType));
                    return;
                }
                if (profile.deniers < cost) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "\u00a7c[Millénaire] Not enough deniers (need " + cost + ", have " + profile.deniers + ")"));
                    return;
                }
                profile.deniers -= cost;
                mw.setDirty();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a76[Millénaire]\u00a7r Hired a " + unitType + " for " + cost + " deniers."));
                MillLog.minor("HireHandler", "Player " + player.getName().getString()
                        + " hired " + unitType + " for " + cost + " deniers");

            } else if (actionId == MillPacketIds.GUIACTION_HIRE_RELEASE) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a76[Millénaire]\u00a7r Hired soldier released."));

            } else if (actionId == MillPacketIds.GUIACTION_HIRE_EXTEND) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a76[Millénaire]\u00a7r Hire extended."));

            } else if (actionId == MillPacketIds.GUIACTION_TOGGLE_STANCE) {
                int stance = r.readInt();
                String stanceName = stance == 0 ? "Patrol" : "Defend";
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a76[Millénaire]\u00a7r Military stance set to: " + stanceName));
            }
        } catch (Exception e) {
            MillLog.error("HireHandler", "Error handling hire action", e);
        } finally {
            r.release();
        }
    }
}
