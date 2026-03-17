package org.dizzymii.millenaire2.network;

/**
 * Handles incoming network packets on the client side.
 * Ported from org.millenaire.network.ClientReceiver (Forge 1.12.2).
 */
public class ClientReceiver {
    /**
     * Delegates to ClientPacketHandler which handles all S2C packet dispatching.
     * This class provides a legacy entry point matching the original mod's structure.
     */
    public static void onPacketReceived(org.dizzymii.millenaire2.network.payloads.MillGenericS2CPayload payload) {
        ClientPacketHandler.handleGenericS2C(payload);
    }
}
