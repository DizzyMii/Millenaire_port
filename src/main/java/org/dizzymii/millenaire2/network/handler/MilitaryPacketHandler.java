package org.dizzymii.millenaire2.network.handler;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.dizzymii.millenaire2.util.MillCommonUtilities;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.world.MillWorldData;

/**
 * Handles military relations and raid GUI actions from the client.
 */
public final class MilitaryPacketHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private MilitaryPacketHandler() {}

    public static void handle(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            MillWorldData mw = MillWorldData.get(player.serverLevel());
            if (mw == null) return;

            Point townHallPos = readPoint(r);
            Building townHall = mw.getBuilding(townHallPos);
            if (townHall == null) {
                LOGGER.warn("Military action: unknown townhall at " + townHallPos);
                return;
            }

            switch (actionId) {
                case MillPacketIds.GUIACTION_MILITARY_RELATIONS -> handleRelations(player, townHall, r);
                case MillPacketIds.GUIACTION_MILITARY_RAID -> handleRaid(player, townHall, r);
                case MillPacketIds.GUIACTION_MILITARY_CANCEL_RAID -> handleCancelRaid(player, townHall);
                default -> LOGGER.warn("Unknown military action: " + actionId);
            }
        } catch (Exception e) {
            LOGGER.error("Error handling military action", e);
        } finally {
            r.release();
        }
    }

    private static void handleRelations(ServerPlayer player, Building townHall, PacketDataHelper.Reader r) {
        Point targetPos = readPoint(r);
        int amount = r.readInt();
        int current = townHall.getRelation(targetPos);
        townHall.setRelation(targetPos, current + amount);
        player.sendSystemMessage(MillCommonUtilities.chatMsg("Relation adjusted by " + amount + "."));
    }

    private static void handleRaid(ServerPlayer player, Building townHall, PacketDataHelper.Reader r) {
        Point targetPos = readPoint(r);
        townHall.raidTarget = targetPos;
        player.sendSystemMessage(MillCommonUtilities.chatMsg("Raid planned on " + targetPos));
    }

    private static void handleCancelRaid(ServerPlayer player, Building townHall) {
        townHall.raidTarget = null;
        player.sendSystemMessage(MillCommonUtilities.chatMsg("Raid plan canceled."));
    }

    private static Point readPoint(PacketDataHelper.Reader r) {
        int[] p = r.readBlockPos();
        return new Point(p[0], p[1], p[2]);
    }
}
