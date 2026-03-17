package org.dizzymii.millenaire2.network;

/**
 * Sends network packets from the client to the server.
 * Ported from org.millenaire.network.ClientSender (Forge 1.12.2).
 */
public class ClientSender {
    /**
     * Delegates to ClientPacketSender which handles all C2S packet building and sending.
     * This class provides a legacy entry point matching the original mod's structure.
     */
    public static void sendVillagerInteract(int entityId) {
        ClientPacketSender.sendVillagerInteractRequest(entityId);
    }

    public static void sendGuiAction(int actionId, byte[] data) {
        ClientPacketSender.sendGuiAction(actionId, data);
    }

    public static void sendVillageListRequest() {
        ClientPacketSender.sendVillageListRequest();
    }

    public static void sendMapInfoRequest() {
        ClientPacketSender.sendMapInfoRequest();
    }
}
