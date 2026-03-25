package org.dizzymii.millenaire2.network.handlers;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;

/**
 * Handles import table actions (importing building plans, changing settings, creating buildings).
 * Dispatched from ServerPacketHandler for GUIACTION_IMPORTTABLE_* action IDs.
 */
public final class ImportTableHandler {

    private ImportTableHandler() {}

    public static void handle(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            if (actionId == MillPacketIds.GUIACTION_IMPORTTABLE_IMPORTBUILDINGPLAN) {
                handleImportBuildingPlan(r);
            } else if (actionId == MillPacketIds.GUIACTION_IMPORTTABLE_CHANGESETTINGS) {
                handleChangeSettings(r);
            } else if (actionId == MillPacketIds.GUIACTION_IMPORTTABLE_CREATEBUILDING) {
                handleCreateBuilding(r);
            }
        } catch (Exception e) {
            MillLog.error("ImportTableHandler", "Error handling import table action", e);
        } finally {
            r.release();
        }
    }

    private static void handleImportBuildingPlan(PacketDataHelper.Reader r) {
        Point tablePos = r.readPoint();
        String source = r.readString();
        String buildingKey = r.readString();
        boolean importAll = r.readBoolean();
        int variation = r.readInt();
        int level = r.readInt();
        int orientation = r.readInt();
        boolean importMockBlocks = r.readBoolean();

        MillLog.minor("ImportTableHandler", "ImportTable import request: table=" + tablePos
                + " source=" + source + " key=" + buildingKey + " all=" + importAll
                + " var=" + variation + " lvl=" + level + " ori=" + orientation
                + " mock=" + importMockBlocks);
    }

    private static void handleChangeSettings(PacketDataHelper.Reader r) {
        Point tablePos = r.readPoint();
        int upgradeLevel = r.readInt();
        int orientation = r.readInt();
        int startingLevel = r.readInt();
        boolean exportSnow = r.readBoolean();
        boolean importMockBlocks = r.readBoolean();
        boolean convertToPreserveGround = r.readBoolean();
        boolean exportRegularChests = r.readBoolean();

        MillLog.minor("ImportTableHandler", "ImportTable settings: table=" + tablePos
                + " upgrade=" + upgradeLevel + " ori=" + orientation + " start=" + startingLevel
                + " snow=" + exportSnow + " mock=" + importMockBlocks
                + " preserve=" + convertToPreserveGround + " chests=" + exportRegularChests);
    }

    private static void handleCreateBuilding(PacketDataHelper.Reader r) {
        Point tablePos = r.readPoint();
        int length = r.readInt();
        int width = r.readInt();
        int startingLevel = r.readInt();
        boolean clearGround = r.readBoolean();

        MillLog.minor("ImportTableHandler", "ImportTable create building: table=" + tablePos
                + " len=" + length + " width=" + width + " start=" + startingLevel
                + " clear=" + clearGround);
    }
}
