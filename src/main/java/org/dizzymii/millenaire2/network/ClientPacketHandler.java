package org.dizzymii.millenaire2.network;

import org.dizzymii.millenaire2.network.payloads.MillGenericS2CPayload;
import org.dizzymii.millenaire2.util.MillLog;

/**
 * Client-side handler for server-to-client packets.
 * Dispatches incoming generic payloads to the appropriate handler based on packet type.
 *
 * TODO: Implement individual packet handlers as village/building/GUI systems are ported.
 */
public final class ClientPacketHandler {

    private ClientPacketHandler() {}

    public static void handleGenericS2C(MillGenericS2CPayload payload) {
        int type = payload.packetType();
        int subType = payload.subType();

        switch (type) {
            case MillPacketIds.PACKET_BUILDING:
                // TODO: handle building data sync
                break;
            case MillPacketIds.PACKET_VILLAGER:
                // TODO: handle villager data sync
                break;
            case MillPacketIds.PACKET_MILLCHEST:
                // TODO: handle locked chest GUI data
                break;
            case MillPacketIds.PACKET_MAPINFO:
                // TODO: handle map info
                break;
            case MillPacketIds.PACKET_VILLAGELIST:
                // TODO: handle village list
                break;
            case MillPacketIds.PACKET_OPENGUI:
                // TODO: open the appropriate GUI based on subType
                break;
            case MillPacketIds.PACKET_TRANSLATED_CHAT:
                // TODO: display translated chat message
                break;
            case MillPacketIds.PACKET_PROFILE:
                // TODO: handle player profile sync
                break;
            case MillPacketIds.PACKET_QUESTINSTANCE:
                // TODO: handle quest instance sync
                break;
            case MillPacketIds.PACKET_VILLAGER_SENTENCE:
                // TODO: handle villager speech bubble
                break;
            default:
                MillLog.warn("ClientPacketHandler", "Unknown S2C packet type: " + type + "/" + subType);
                break;
        }
    }
}
