package org.dizzymii.millenaire2.network;

import net.neoforged.neoforge.network.PacketDistributor;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.network.payloads.MillGenericC2SPayload;

/**
 * Client-side utility for building and sending C2S packets.
 * Encodes data structures into byte arrays via PacketDataHelper.Writer
 * and wraps them in MillGenericC2SPayload for transmission.
 */
public final class ClientPacketSender {

    private ClientPacketSender() {}

    // ========== Villager interaction ==========

    /**
     * Request interaction data for a villager (triggers sync + potential GUI open).
     */
    public static void sendVillagerInteractRequest(int entityId) {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeInt(entityId);

        MillGenericC2SPayload payload = new MillGenericC2SPayload(
                MillPacketIds.PACKET_VILLAGERINTERACT_REQUEST, 0, w.toByteArray());
        PacketDistributor.sendToServer(payload);
    }

    // ========== Village list request ==========

    /**
     * Request the list of known villages from the server.
     */
    public static void sendVillageListRequest() {
        MillGenericC2SPayload payload = new MillGenericC2SPayload(
                MillPacketIds.PACKET_VILLAGELIST_REQUEST, 0, new byte[0]);
        PacketDistributor.sendToServer(payload);
    }

    // ========== Release number declaration ==========

    /**
     * Declare this client's mod version to the server on login.
     */
    public static void sendDeclareReleaseNumber() {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeString(Millenaire2.VERSION);

        MillGenericC2SPayload payload = new MillGenericC2SPayload(
                MillPacketIds.PACKET_DECLARERELEASENUMBER, 0, w.toByteArray());
        PacketDistributor.sendToServer(payload);
    }

    // ========== GUI actions ==========

    /**
     * Send a GUI action to the server (e.g. trade confirmation, hire, quest choice).
     */
    public static void sendGuiAction(int actionId, byte[] data) {
        MillGenericC2SPayload payload = new MillGenericC2SPayload(
                MillPacketIds.PACKET_GUIACTION, actionId, data);
        PacketDistributor.sendToServer(payload);
    }

    /**
     * Send a GUI action with structured data built from a writer.
     */
    public static void sendGuiAction(int actionId, PacketDataHelper.Writer writer) {
        sendGuiAction(actionId, writer.toByteArray());
    }

    // ========== Dev commands ==========

    /**
     * Send a dev/debug command to the server.
     */
    public static void sendDevCommand(int commandId) {
        MillGenericC2SPayload payload = new MillGenericC2SPayload(
                MillPacketIds.PACKET_DEVCOMMAND, commandId, new byte[0]);
        PacketDistributor.sendToServer(payload);
    }

    // ========== Map info request ==========

    /**
     * Request map information from the server.
     */
    public static void sendMapInfoRequest() {
        MillGenericC2SPayload payload = new MillGenericC2SPayload(
                MillPacketIds.PACKET_MAPINFO_REQUEST, 0, new byte[0]);
        PacketDistributor.sendToServer(payload);
    }
}
