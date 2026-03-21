package org.dizzymii.millenaire2.network;

import net.neoforged.neoforge.network.PacketDistributor;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.network.payloads.*;

/**
 * Client-side utility for building and sending C2S packets.
 * Constructs dedicated payload records and sends them via PacketDistributor.
 */
public final class ClientPacketSender {

    private ClientPacketSender() {}

    // ========== Villager interaction ==========

    /**
     * Request interaction data for a villager (triggers sync + potential GUI open).
     */
    public static void sendVillagerInteractRequest(int entityId) {
        PacketDistributor.sendToServer(new VillagerInteractPayload(entityId));
    }

    // ========== Village list request ==========

    /**
     * Request the list of known villages from the server.
     */
    public static void sendVillageListRequest() {
        PacketDistributor.sendToServer(new VillageListRequestPayload());
    }

    // ========== Release number declaration ==========

    /**
     * Declare this client's mod version to the server on login.
     */
    public static void sendDeclareReleaseNumber() {
        PacketDistributor.sendToServer(new DeclareReleasePayload(Millenaire2.VERSION));
    }

    // ========== GUI actions ==========

    /**
     * Send a GUI action to the server (e.g. trade confirmation, hire, quest choice).
     */
    public static void sendGuiAction(int actionId, byte[] data) {
        PacketDistributor.sendToServer(new GuiActionPayload(actionId, data));
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
        PacketDistributor.sendToServer(new DevCommandPayload(commandId, new byte[0]));
    }

    // ========== Map info request ==========

    /**
     * Request map information from the server.
     */
    public static void sendMapInfoRequest() {
        PacketDistributor.sendToServer(new MapInfoRequestPayload());
    }
}
