package org.dizzymii.millenaire2.network.handler;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.util.Point;

/**
 * Handles import table GUI actions from the client.
 */
public final class ImportTablePacketHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ImportTablePacketHandler() {}

    public static void handle(int actionId, byte[] data, IPayloadContext context) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            switch (actionId) {
                case MillPacketIds.GUIACTION_IMPORTTABLE_IMPORTBUILDINGPLAN -> handleImportBuildingPlan(r);
                case MillPacketIds.GUIACTION_IMPORTTABLE_CHANGESETTINGS -> handleChangeSettings(r);
                case MillPacketIds.GUIACTION_IMPORTTABLE_CREATEBUILDING -> handleCreateBuilding(r);
                default -> LOGGER.warn("Unknown import table action: " + actionId);
            }
        } catch (Exception e) {
            LOGGER.error("Error handling import table action", e);
        } finally {
            r.release();
        }
    }

    private static void handleImportBuildingPlan(PacketDataHelper.Reader r) {
        Point tablePos = readPoint(r);
        String source = r.readString();
        String buildingKey = r.readString();
        boolean importAll = r.readBoolean();
        int variation = r.readInt();
        int level = r.readInt();
        int orientation = r.readInt();
        boolean importMockBlocks = r.readBoolean();

        LOGGER.debug("ImportTable import request: table=" + tablePos
                + " source=" + source + " key=" + buildingKey + " all=" + importAll
                + " var=" + variation + " lvl=" + level + " ori=" + orientation
                + " mock=" + importMockBlocks);
    }

    private static void handleChangeSettings(PacketDataHelper.Reader r) {
        Point tablePos = readPoint(r);
        int upgradeLevel = r.readInt();
        int orientation = r.readInt();
        int startingLevel = r.readInt();
        boolean exportSnow = r.readBoolean();
        boolean importMockBlocks = r.readBoolean();
        boolean convertToPreserveGround = r.readBoolean();
        boolean exportRegularChests = r.readBoolean();

        LOGGER.debug("ImportTable settings: table=" + tablePos
                + " upgrade=" + upgradeLevel + " ori=" + orientation + " start=" + startingLevel
                + " snow=" + exportSnow + " mock=" + importMockBlocks
                + " preserve=" + convertToPreserveGround + " chests=" + exportRegularChests);
    }

    private static void handleCreateBuilding(PacketDataHelper.Reader r) {
        Point tablePos = readPoint(r);
        int length = r.readInt();
        int width = r.readInt();
        int startingLevel = r.readInt();
        boolean clearGround = r.readBoolean();

        LOGGER.debug("ImportTable create building: table=" + tablePos
                + " len=" + length + " width=" + width + " start=" + startingLevel
                + " clear=" + clearGround);
    }

    private static Point readPoint(PacketDataHelper.Reader r) {
        int[] p = r.readBlockPos();
        return new Point(p[0], p[1], p[2]);
    }
}
