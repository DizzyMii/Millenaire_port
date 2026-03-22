package org.dizzymii.millenaire2.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.network.handler.*;
import org.dizzymii.millenaire2.network.payloads.*;
import org.dizzymii.millenaire2.util.MillLog;

/**
 * Server-side handler for client-to-server packets.
 * Each public method handles a specific dedicated payload type.
 */
public final class ServerPacketHandler {

    private ServerPacketHandler() {}

    // ========== Villager interaction ==========

    public static void handleVillagerInteract(VillagerInteractPayload payload, IPayloadContext context) {
        VillagerInteractPacketHandler.handle(payload.entityId(), context);
    }

    // ========== Village list request ==========

    public static void handleVillageListRequest(IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.world.MillWorldData.get(player.serverLevel());
        java.util.List<ServerPacketSender.VillageListEntry> entries = new java.util.ArrayList<>();

        if (mw != null) {
            for (java.util.Map.Entry<org.dizzymii.millenaire2.util.Point, org.dizzymii.millenaire2.village.Building> e
                    : mw.getBuildingsMap().entrySet()) {
                org.dizzymii.millenaire2.village.Building b = e.getValue();
                org.dizzymii.millenaire2.util.Point bPos = e.getKey();
                String name = b.getName() != null ? b.getName() : "Unknown";
                String culture = b.cultureKey != null ? b.cultureKey : "";
                int dist = (int) Math.sqrt(
                        Math.pow(player.getX() - bPos.x, 2) + Math.pow(player.getZ() - bPos.z, 2));
                entries.add(new ServerPacketSender.VillageListEntry(
                        bPos, culture, name, dist, !b.isTownhall));
            }
        }

        ServerPacketSender.sendVillageList(player, entries);
        MillLog.minor("ServerPacketHandler", "Sent village list (" + entries.size() + " entries) to " + player.getName().getString());
    }

    // ========== Release number declaration ==========

    public static void handleDeclareRelease(DeclareReleasePayload payload, IPayloadContext context) {
        MillLog.minor("ServerPacketHandler", "Client declared release: " + payload.releaseNumber());
    }

    // ========== Dev commands ==========

    public static void handleDevCommand(DevCommandPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        // Only allow ops
        if (!player.hasPermissions(2)) {
            MillLog.warn("ServerPacketHandler", "Non-op player tried dev command: " + player.getName().getString());
            return;
        }

        int commandId = payload.commandId();
        switch (commandId) {
            case MillPacketIds.DEV_COMMAND_TOGGLE_AUTO_MOVE:
                MillLog.minor("ServerPacketHandler", "Toggle auto-move for " + player.getName().getString());
                toggleNearestVillagerAutoMove(player);
                break;
            case MillPacketIds.DEV_COMMAND_TEST_PATH:
                MillLog.minor("ServerPacketHandler", "Test path for " + player.getName().getString());
                triggerTestPathfinding(player);
                break;
            default:
                MillLog.warn("ServerPacketHandler", "Unknown dev command: " + commandId);
                break;
        }
    }

    // ========== GUI actions ==========

    public static void handleGuiAction(GuiActionPayload payload, IPayloadContext context) {
        handleGuiActionInternal(payload.actionId(), payload.data(), context);
    }

    private static void handleGuiActionInternal(int actionId, byte[] data, IPayloadContext context) {
        switch (actionId) {
            case MillPacketIds.GUIACTION_CHIEF_BUILDING:
            case MillPacketIds.GUIACTION_CHIEF_CROP:
            case MillPacketIds.GUIACTION_CHIEF_CONTROL:
            case MillPacketIds.GUIACTION_CHIEF_DIPLOMACY:
            case MillPacketIds.GUIACTION_CHIEF_SCROLL:
            case MillPacketIds.GUIACTION_CHIEF_HUNTING_DROP:
                handleChiefAction(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_QUEST_COMPLETESTEP:
            case MillPacketIds.GUIACTION_QUEST_REFUSE:
                QuestPacketHandler.handle(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_NEWVILLAGE:
                handleNewVillage(data, context);
                break;
            case MillPacketIds.GUIACTION_HIRE_HIRE:
            case MillPacketIds.GUIACTION_HIRE_EXTEND:
            case MillPacketIds.GUIACTION_HIRE_RELEASE:
            case MillPacketIds.GUIACTION_TOGGLE_STANCE:
                HirePacketHandler.handle(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_TRADE_BUY:
            case MillPacketIds.GUIACTION_TRADE_SELL:
                TradePacketHandler.handle(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_PUJAS_CHANGE_ENCHANTMENT:
            case MillPacketIds.GUIACTION_TRADE_TOGGLE_DONATION:
                handleChiefAction(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_NEGATION_WAND:
                handleNegationWand(data, context);
                break;
            case MillPacketIds.GUIACTION_NEW_BUILDING_PROJECT:
            case MillPacketIds.GUIACTION_NEW_CUSTOM_BUILDING_PROJECT:
            case MillPacketIds.GUIACTION_UPDATE_CUSTOM_BUILDING_PROJECT:
            case MillPacketIds.GUIACTION_CONTROLLEDBUILDING_TOGGLEALLOWED:
            case MillPacketIds.GUIACTION_CONTROLLEDBUILDING_FORGET:
                BuildingProjectPacketHandler.handle(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_MILITARY_RELATIONS:
            case MillPacketIds.GUIACTION_MILITARY_RAID:
            case MillPacketIds.GUIACTION_MILITARY_CANCEL_RAID:
                MilitaryPacketHandler.handle(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_IMPORTTABLE_IMPORTBUILDINGPLAN:
            case MillPacketIds.GUIACTION_IMPORTTABLE_CHANGESETTINGS:
            case MillPacketIds.GUIACTION_IMPORTTABLE_CREATEBUILDING:
                ImportTablePacketHandler.handle(actionId, data, context);
                break;
            default:
                MillLog.warn("ServerPacketHandler", "Unknown GUI action: " + actionId);
                break;
        }
    }

    // ========== Map info ==========

    public static void handleMapInfoRequest(IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MillLog.minor("ServerPacketHandler", "Map info requested by " + player.getName().getString());
        // Map info packet contains village positions and culture markers for the minimap
        // Currently sends empty data — will be populated when MillWorldData tracks village positions
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, new MapInfoPayload(0));
    }

    // ========== Available content ==========

    public static void handleAvailableContent(AvailableContentPayload payload, IPayloadContext context) {
        MillLog.minor("ServerPacketHandler", "Client declared " + payload.contentCount() + " available content packs");
    }

    // ========== Dev command helpers ==========

    private static void toggleNearestVillagerAutoMove(ServerPlayer player) {
        java.util.List<MillVillager> nearby = player.level().getEntitiesOfClass(
                MillVillager.class, player.getBoundingBox().inflate(16));
        if (!nearby.isEmpty()) {
            MillVillager nearest = nearby.get(0);
            nearest.stopMoving = !nearest.stopMoving;
            MillLog.minor("ServerPacketHandler", "Toggled auto-move on " + nearest.getFirstName()
                    + " -> stopMoving=" + nearest.stopMoving);
        }
    }

    private static void triggerTestPathfinding(ServerPlayer player) {
        java.util.List<MillVillager> nearby = player.level().getEntitiesOfClass(
                MillVillager.class, player.getBoundingBox().inflate(32));
        if (!nearby.isEmpty()) {
            MillVillager villager = nearby.get(0);
            villager.getNavigation().moveTo(player.getX(), player.getY(), player.getZ(), 1.0);
            MillLog.minor("ServerPacketHandler", "Test path: " + villager.getFirstName()
                    + " -> player pos");
        }
    }

    // ========== GUI action sub-handlers ==========

    private static void handleChiefAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MillLog.minor("ServerPacketHandler", "Chief action " + actionId + " from " + player.getName().getString());
        // Chief actions modify village building priorities, crop selection, diplomacy, etc.
        // Actual village modification deferred to when Village tick system is complete
    }

    
    private static void handleNewVillage(byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            String cultureKey = r.readString();

            org.dizzymii.millenaire2.culture.Culture culture =
                    org.dizzymii.millenaire2.culture.Culture.getCultureByName(cultureKey);
            if (culture == null) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a7c[Millénaire] Unknown culture: " + cultureKey));
                return;
            }

            net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) player.level();
            net.minecraft.core.BlockPos playerPos = player.blockPosition();
            org.dizzymii.millenaire2.world.MillWorldData mw =
                    org.dizzymii.millenaire2.world.MillWorldData.get(player.serverLevel());
            if (mw == null) return;

            boolean generated = org.dizzymii.millenaire2.world.WorldGenVillage
                    .generateNewVillage(level, playerPos, culture, mw, level.random);

            if (generated) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a76[Millénaire]\u00a7r Summoned a " + cultureKey + " village!"));
                mw.setDirty();
            } else {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a7c[Millénaire] Failed to generate village. Check terrain or culture data."));
            }
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling new village", e);
        } finally {
            r.release();
        }
    }

    
    private static void handleNegationWand(byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        org.dizzymii.millenaire2.world.MillWorldData mw =
                org.dizzymii.millenaire2.world.MillWorldData.get(player.serverLevel());
        if (mw == null) return;

        net.minecraft.core.BlockPos playerPos = player.blockPosition();
        org.dizzymii.millenaire2.util.Point playerPoint =
                new org.dizzymii.millenaire2.util.Point(playerPos.getX(), playerPos.getY(), playerPos.getZ());

        // Find the nearest village within 200 blocks
        org.dizzymii.millenaire2.village.Building nearest = null;
        double nearestDist = Double.MAX_VALUE;
        for (org.dizzymii.millenaire2.village.Building b : mw.allBuildings()) {
            if (!b.isTownhall || b.getPos() == null) continue;
            double dist = playerPoint.distanceTo(b.getPos());
            if (dist < 200 && dist < nearestDist) {
                nearest = b;
                nearestDist = dist;
            }
        }

        if (nearest == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "\u00a7c[Millénaire] No village found within 200 blocks."));
            return;
        }

        String villageName = nearest.getName() != null ? nearest.getName() : "Unknown";

        // Remove all buildings belonging to this village
        java.util.List<org.dizzymii.millenaire2.util.Point> toRemove = new java.util.ArrayList<>();
        org.dizzymii.millenaire2.util.Point thPos = nearest.getPos();
        for (var entry : mw.getBuildingsMap().entrySet()) {
            org.dizzymii.millenaire2.village.Building b = entry.getValue();
            org.dizzymii.millenaire2.util.Point bTh = b.isTownhall ? b.getPos() : b.getTownHallPos();
            if (bTh != null && bTh.equals(thPos)) {
                toRemove.add(entry.getKey());
            }
        }
        for (org.dizzymii.millenaire2.util.Point key : toRemove) {
            mw.removeBuilding(key);
        }
        mw.setDirty();

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "\u00a76[Millénaire]\u00a7r Village '" + villageName + "' removed (" + toRemove.size() + " buildings)."));
        MillLog.minor("ServerPacketHandler", "Negation wand: removed village '" + villageName
                + "' (" + toRemove.size() + " buildings) by " + player.getName().getString());
    }
}
