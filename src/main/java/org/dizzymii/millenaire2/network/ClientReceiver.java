package org.dizzymii.millenaire2.network;

/**
 * Legacy entry point — no longer used.
 * All S2C payload handling is now registered directly in {@link MillNetworking}
 * with dedicated payload types routed to {@link ClientPacketHandler}.
 */
public class ClientReceiver {
    private ClientReceiver() {}
}
