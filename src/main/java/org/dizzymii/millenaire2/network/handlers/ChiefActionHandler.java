package org.dizzymii.millenaire2.network.handlers;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.util.MillLog;

/**
 * Handles chief GUI actions (building priorities, crop selection, diplomacy, etc.).
 * Dispatched from ServerPacketHandler for GUIACTION_CHIEF_* and related action IDs.
 */
public final class ChiefActionHandler {

    private ChiefActionHandler() {}

    public static void handle(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MillLog.minor("ChiefActionHandler", "Chief action " + actionId + " from " + player.getName().getString());
        // Chief actions modify village building priorities, crop selection, diplomacy, etc.
        // Actual village modification deferred to when Village tick system is complete
    }
}
