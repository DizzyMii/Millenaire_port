package org.dizzymii.millenaire2.network.handlers;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.BuildingLocation;
import org.dizzymii.millenaire2.village.BuildingProject;
import org.dizzymii.millenaire2.world.MillWorldData;

/**
 * Handles building project actions from the client.
 * Covers GUIACTION_NEW_BUILDING_PROJECT, GUIACTION_NEW_CUSTOM_BUILDING_PROJECT,
 * GUIACTION_UPDATE_CUSTOM_BUILDING_PROJECT, GUIACTION_CONTROLLEDBUILDING_TOGGLEALLOWED,
 * and GUIACTION_CONTROLLEDBUILDING_FORGET.
 */
public final class BuildingProjectHandler {

    private BuildingProjectHandler() {}

    public static void handle(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) return;

            Point townHallPos = r.readPoint();
            Building townHall = mw.getBuilding(townHallPos);
            if (townHall == null) {
                MillLog.warn("BuildingProjectHandler", "Building project action: unknown townhall at " + townHallPos);
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_NEW_BUILDING_PROJECT
                    || actionId == MillPacketIds.GUIACTION_NEW_CUSTOM_BUILDING_PROJECT) {
                handleNewProject(actionId, r, player, townHall, mw);
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_UPDATE_CUSTOM_BUILDING_PROJECT) {
                handleUpdateCustomProject(r, player, mw);
                return;
            }

            String projectKey = r.readString();
            Point projectPos = r.readPoint();
            if (projectKey == null || projectKey.isBlank()) return;

            if (actionId == MillPacketIds.GUIACTION_CONTROLLEDBUILDING_TOGGLEALLOWED) {
                handleToggleAllowed(r, player, townHall, projectKey, projectPos, mw);
            } else if (actionId == MillPacketIds.GUIACTION_CONTROLLEDBUILDING_FORGET) {
                handleForgetProject(player, townHall, projectKey, projectPos, mw);
            }
        } catch (Exception e) {
            MillLog.error("BuildingProjectHandler", "Error handling building project action", e);
        } finally {
            r.release();
        }
    }

    private static void handleNewProject(int actionId, PacketDataHelper.Reader r,
                                          ServerPlayer player, Building townHall, MillWorldData mw) {
        Point buildPos = r.readPoint();
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
                .computeIfAbsent(BuildingProject.EnumProjects.PLAYER,
                        k -> new java.util.concurrent.CopyOnWriteArrayList<>())
                .add(bp);

        mw.setDirty();
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "§6[Millénaire]§r Project requested: " + planKey));
    }

    private static void handleUpdateCustomProject(PacketDataHelper.Reader r,
                                                   ServerPlayer player, MillWorldData mw) {
        Point buildingPos = r.readPoint();
        Building existing = mw.getBuilding(buildingPos);
        if (existing != null) {
            mw.setDirty();
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§6[Millénaire]§r Custom building updated."));
        }
    }

    private static void handleToggleAllowed(PacketDataHelper.Reader r, ServerPlayer player,
                                             Building townHall, String projectKey,
                                             Point projectPos, MillWorldData mw) {
        boolean allow = r.readBoolean();
        boolean changed = false;
        for (java.util.List<BuildingProject> projects : townHall.buildingProjects.values()) {
            for (BuildingProject project : projects) {
                if (project == null || project.location == null || project.key == null) continue;
                if (!projectKey.equals(project.key)) continue;
                if (project.location.pos != null && !project.location.pos.equals(projectPos)) continue;
                project.location.upgradesAllowed = allow;
                changed = true;
            }
        }
        if (changed) {
            mw.setDirty();
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§6[Millénaire]§r Project upgrades " + (allow ? "allowed" : "forbidden") + "."));
        }
    }

    private static void handleForgetProject(ServerPlayer player, Building townHall,
                                             String projectKey, Point projectPos, MillWorldData mw) {
        boolean changed = false;
        for (java.util.List<BuildingProject> projects : townHall.buildingProjects.values()) {
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
            mw.setDirty();
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§6[Millénaire]§r Project forgotten: " + projectKey));
        }
    }
}
