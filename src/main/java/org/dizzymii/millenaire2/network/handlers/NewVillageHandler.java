package org.dizzymii.millenaire2.network.handlers;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.WorldGenVillage;

/**
 * Handles the new village creation action from the client.
 * Dispatched from ServerPacketHandler for GUIACTION_NEWVILLAGE.
 */
public final class NewVillageHandler {

    private NewVillageHandler() {}

    public static void handle(byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            String cultureKey = r.readString();

            Culture culture = Culture.getCultureByName(cultureKey);
            if (culture == null) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a7c[Millénaire] Unknown culture: " + cultureKey));
                return;
            }

            ServerLevel level = (ServerLevel) player.level();
            BlockPos playerPos = player.blockPosition();
            MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) return;

            boolean generated = WorldGenVillage.generateNewVillage(level, playerPos, culture, mw, level.random);

            if (generated) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a76[Millénaire]\u00a7r Summoned a " + cultureKey + " village!"));
                mw.setDirty();
            } else {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a7c[Millénaire] Failed to generate village. Check terrain or culture data."));
            }
        } catch (Exception e) {
            MillLog.error("NewVillageHandler", "Error handling new village", e);
        } finally {
            r.release();
        }
    }
}
