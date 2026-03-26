package org.dizzymii.millenaire2.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.dizzymii.millenaire2.client.network.ClientReceiver;
import org.dizzymii.millenaire2.network.payloads.MillGenericC2SPayload;
import org.dizzymii.millenaire2.network.payloads.MillGenericS2CPayload;
import org.dizzymii.millenaire2.util.MillLog;

/**
 * Central networking registration for Millénaire.
 * Uses NeoForge 1.21.1 payload-based networking.
 *
 * Call {@link #register(RegisterPayloadHandlersEvent)} from the mod event bus.
 */
public final class MillNetworking {

    private static final String PROTOCOL_VERSION = "1";

    private MillNetworking() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        // Server → Client
        registrar.playToClient(
                MillGenericS2CPayload.TYPE,
                MillGenericS2CPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> ClientReceiver.onPacketReceived(payload));
                }
        );

        // Client → Server
        registrar.playToServer(
                MillGenericC2SPayload.TYPE,
                MillGenericC2SPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> ServerPacketHandler.handleGenericC2S(payload, context));
                }
        );

        MillLog.major("MillNetworking", "Networking payloads registered.");
    }
}
