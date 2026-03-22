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
import org.dizzymii.millenaire2.village.BuildingLocation;
import org.dizzymii.millenaire2.village.BuildingProject;
import org.dizzymii.millenaire2.world.MillWorldData;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Handles building project GUI actions from the client.
 */
public final class BuildingProjectPacketHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private BuildingProjectPacketHandler() {}

    public static void handle(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            MillWorldData mw = MillWorldData.get(player.serverLevel());
            if (mw == null) return;

            Point townHallPos = readPoint(r);
            Building townHall = mw.getBuilding(townHallPos);
            if (townHall == null) {
                LOGGER.warn("Building project action: unknown townhall at " + townHallPos);
                return;
            }

            switch (actionId) {
                case MillPacketIds.GUIACTION_NEW_BUILDING_PROJECT,
                     MillPacketIds.GUIACTION_NEW_CUSTOM_BUILDING_PROJECT -> handleNewProject(player, townHall, r, actionId);
                case MillPacketIds.GUIACTION_UPDATE_CUSTOM_BUILDING_PROJECT -> handleUpdateCustom(player, mw, r);
                case MillPacketIds.GUIACTION_CONTROLLEDBUILDING_TOGGLEALLOWED -> handleToggleAllowed(player, townHall, r);
                case MillPacketIds.GUIACTION_CONTROLLEDBUILDING_FORGET -> handleForget(player, townHall, r);
                default -> LOGGER.warn("Unknown building project action: " + actionId);
            }
        } catch (Exception e) {
            LOGGER.error("Error handling building project action", e);
        } finally {
            r.release();
        }
    }

    private static void handleNewProject(ServerPlayer player, Building townHall, PacketDataHelper.Reader r, int actionId) {
        Point buildPos = readPoint(r);
        String planKey = r.readString();
        if (planKey == null || planKey.isBlank()) return;

        if (!townHall.buildingsBought.contains(planKey)) {
            townHall.buildingsBought.add(planKey);
        }

        BuildingProject bp = new BuildingProject();
        bp.key = planKey;
        bp.isCustomBuilding = actionId == MillPacketIds.GUIACTION_NEW_CUSTOM_BUILDING_PROJECT;
        bp.projectTier = BuildingProject.EnumProjects.PLAYER;
        BuildingLocation loc = new BuildingLocation();
        loc.planKey = planKey;
        loc.pos = buildPos;
        loc.isCustomBuilding = bp.isCustomBuilding;
        bp.location = loc;
        townHall.buildingProjects
                .computeIfAbsent(BuildingProject.EnumProjects.PLAYER, k -> new CopyOnWriteArrayList<>())
                .add(bp);

        player.sendSystemMessage(MillCommonUtilities.chatMsg("Project requested: " + planKey));
    }

    private static void handleUpdateCustom(ServerPlayer player, MillWorldData mw, PacketDataHelper.Reader r) {
        Point buildingPos = readPoint(r);
        Building existing = mw.getBuilding(buildingPos);
        if (existing != null) {
            player.sendSystemMessage(MillCommonUtilities.chatMsg("Custom building updated."));
        }
    }

    private static void handleToggleAllowed(ServerPlayer player, Building townHall, PacketDataHelper.Reader r) {
        String projectKey = r.readString();
        Point projectPos = readPoint(r);
        if (projectKey == null || projectKey.isBlank()) return;

        boolean allow = r.readBoolean();
        boolean changed = false;
        for (var projects : townHall.buildingProjects.values()) {
            for (BuildingProject project : projects) {
                if (project == null || project.location == null || project.key == null) continue;
                if (!projectKey.equals(project.key)) continue;
                if (project.location.pos != null && !project.location.pos.equals(projectPos)) continue;
                project.location.upgradesAllowed = allow;
                changed = true;
            }
        }
        if (changed) {
            player.sendSystemMessage(MillCommonUtilities.chatMsg("Project upgrades " + (allow ? "allowed" : "forbidden") + "."));
        }
    }

    private static void handleForget(ServerPlayer player, Building townHall, PacketDataHelper.Reader r) {
        String projectKey = r.readString();
        Point projectPos = readPoint(r);
        if (projectKey == null || projectKey.isBlank()) return;

        boolean changed = false;
        for (var projects : townHall.buildingProjects.values()) {
            boolean removed = projects.removeIf(project ->
                    project != null
                            && project.key != null
                            && project.location != null
                            && project.location.pos != null
                            && projectKey.equals(project.key)
                            && project.location.pos.equals(projectPos));
            changed |= removed;
        }
        changed |= townHall.buildingsBought.remove(projectKey);
        if (changed) {
            player.sendSystemMessage(MillCommonUtilities.chatMsg("Project forgotten: " + projectKey));
        }
    }

    private static Point readPoint(PacketDataHelper.Reader r) {
        int[] p = r.readBlockPos();
        return new Point(p[0], p[1], p[2]);
    }
}
