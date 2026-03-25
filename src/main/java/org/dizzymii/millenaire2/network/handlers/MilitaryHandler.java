package org.dizzymii.millenaire2.network.handlers;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.world.MillWorldData;

/**
 * Handles military actions (relation adjustments, raid planning, raid cancellation).
 * Dispatched from ServerPacketHandler for GUIACTION_MILITARY_* action IDs.
 */
public final class MilitaryHandler {

    private MilitaryHandler() {}

    public static void handle(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) return;

            Point townHallPos = r.readPoint();
            Building townHall = mw.getBuilding(townHallPos);
            if (townHall == null) {
                MillLog.warn("MilitaryHandler", "Military action: unknown townhall at " + townHallPos);
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_MILITARY_RELATIONS) {
                Point targetPos = r.readPoint();
                int amount = r.readInt();
                int current = townHall.getRelation(targetPos);
                townHall.setRelation(targetPos, current + amount);
                mw.setDirty();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6[Millénaire]§r Relation adjusted by " + amount + "."));

            } else if (actionId == MillPacketIds.GUIACTION_MILITARY_RAID) {
                Point targetPos = r.readPoint();
                townHall.raidTarget = targetPos;
                mw.setDirty();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6[Millénaire]§r Raid planned on " + targetPos));

            } else if (actionId == MillPacketIds.GUIACTION_MILITARY_CANCEL_RAID) {
                townHall.raidTarget = null;
                mw.setDirty();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6[Millénaire]§r Raid plan canceled."));
            }
        } catch (Exception e) {
            MillLog.error("MilitaryHandler", "Error handling military action", e);
        } finally {
            r.release();
        }
    }
}
