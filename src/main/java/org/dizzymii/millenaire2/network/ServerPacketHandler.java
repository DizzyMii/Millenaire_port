package org.dizzymii.millenaire2.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.network.payloads.MillGenericC2SPayload;
import org.dizzymii.millenaire2.util.MillLog;

/**
 * Server-side handler for client-to-server packets.
 * Dispatches incoming generic payloads to the appropriate handler based on packet type.
 *
 * TODO: Implement individual packet handlers as village/building/GUI systems are ported.
 */
public final class ServerPacketHandler {

    private ServerPacketHandler() {}

    public static void handleGenericC2S(MillGenericC2SPayload payload, IPayloadContext context) {
        int type = payload.packetType();
        int subType = payload.subType();

        switch (type) {
            case MillPacketIds.PACKET_GUIACTION:
                handleGuiAction(subType, payload.data(), context);
                break;
            case MillPacketIds.PACKET_VILLAGELIST_REQUEST:
                // TODO: send village list to requesting player
                break;
            case MillPacketIds.PACKET_DECLARERELEASENUMBER:
                // TODO: handle release number declaration
                break;
            case MillPacketIds.PACKET_MAPINFO_REQUEST:
                // TODO: send map info
                break;
            case MillPacketIds.PACKET_VILLAGERINTERACT_REQUEST:
                // TODO: handle villager interaction request
                break;
            case MillPacketIds.PACKET_AVAILABLECONTENT:
                // TODO: handle available content declaration
                break;
            case MillPacketIds.PACKET_DEVCOMMAND:
                // TODO: handle dev commands
                break;
            default:
                MillLog.warn("ServerPacketHandler", "Unknown C2S packet type: " + type + "/" + subType);
                break;
        }
    }

    private static void handleGuiAction(int actionId, byte[] data, IPayloadContext context) {
        switch (actionId) {
            case MillPacketIds.GUIACTION_CHIEF_BUILDING:
            case MillPacketIds.GUIACTION_CHIEF_CROP:
            case MillPacketIds.GUIACTION_CHIEF_CONTROL:
            case MillPacketIds.GUIACTION_CHIEF_DIPLOMACY:
            case MillPacketIds.GUIACTION_CHIEF_SCROLL:
            case MillPacketIds.GUIACTION_CHIEF_HUNTING_DROP:
                // TODO: handle chief GUI actions
                break;
            case MillPacketIds.GUIACTION_QUEST_COMPLETESTEP:
            case MillPacketIds.GUIACTION_QUEST_REFUSE:
                // TODO: handle quest actions
                break;
            case MillPacketIds.GUIACTION_NEWVILLAGE:
                // TODO: handle new village creation
                break;
            case MillPacketIds.GUIACTION_HIRE_HIRE:
            case MillPacketIds.GUIACTION_HIRE_EXTEND:
            case MillPacketIds.GUIACTION_HIRE_RELEASE:
            case MillPacketIds.GUIACTION_TOGGLE_STANCE:
                // TODO: handle hire actions
                break;
            case MillPacketIds.GUIACTION_NEGATION_WAND:
                // TODO: handle negation wand
                break;
            case MillPacketIds.GUIACTION_NEW_BUILDING_PROJECT:
            case MillPacketIds.GUIACTION_NEW_CUSTOM_BUILDING_PROJECT:
            case MillPacketIds.GUIACTION_UPDATE_CUSTOM_BUILDING_PROJECT:
                // TODO: handle building project actions
                break;
            case MillPacketIds.GUIACTION_MILITARY_RELATIONS:
            case MillPacketIds.GUIACTION_MILITARY_RAID:
            case MillPacketIds.GUIACTION_MILITARY_CANCEL_RAID:
                // TODO: handle military actions
                break;
            case MillPacketIds.GUIACTION_IMPORTTABLE_IMPORTBUILDINGPLAN:
            case MillPacketIds.GUIACTION_IMPORTTABLE_CHANGESETTINGS:
            case MillPacketIds.GUIACTION_IMPORTTABLE_CREATEBUILDING:
                // TODO: handle import table actions
                break;
            default:
                MillLog.warn("ServerPacketHandler", "Unknown GUI action: " + actionId);
                break;
        }
    }
}
