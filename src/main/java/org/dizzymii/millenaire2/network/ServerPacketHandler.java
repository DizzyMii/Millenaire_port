package org.dizzymii.millenaire2.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.network.payloads.MillGenericC2SPayload;
import org.dizzymii.millenaire2.util.MillLog;

/**
 * Server-side handler for client-to-server packets.
 * Dispatches incoming generic payloads to the appropriate handler based on packet type.
 */
public final class ServerPacketHandler {

    private ServerPacketHandler() {}

    public static void handleGenericC2S(MillGenericC2SPayload payload, IPayloadContext context) {
        int type = payload.packetType();
        int subType = payload.subType();

        switch (type) {
            case MillPacketIds.PACKET_GUIACTION:
                handleGuiAction(subType, payload.data(), context);
                break;
            case MillPacketIds.PACKET_VILLAGELIST_REQUEST:
                handleVillageListRequest(context);
                break;
            case MillPacketIds.PACKET_DECLARERELEASENUMBER:
                handleDeclareReleaseNumber(payload.data(), context);
                break;
            case MillPacketIds.PACKET_MAPINFO_REQUEST:
                handleMapInfoRequest(context);
                break;
            case MillPacketIds.PACKET_VILLAGERINTERACT_REQUEST:
                handleVillagerInteract(payload.data(), context);
                break;
            case MillPacketIds.PACKET_AVAILABLECONTENT:
                handleAvailableContent(payload.data(), context);
                break;
            case MillPacketIds.PACKET_DEVCOMMAND:
                handleDevCommand(subType, payload.data(), context);
                break;
            default:
                MillLog.warn("ServerPacketHandler", "Unknown C2S packet type: " + type + "/" + subType);
                break;
        }
    }

    // ========== Villager interaction ==========

    private static void handleVillagerInteract(byte[] data, IPayloadContext context) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int entityId = r.readInt();

            if (!(context.player() instanceof ServerPlayer player)) return;
            Entity entity = player.level().getEntity(entityId);
            if (!(entity instanceof MillVillager villager)) {
                MillLog.warn("ServerPacketHandler", "Villager interact: entity " + entityId + " not found");
                return;
            }

            // Send full villager sync to the interacting player
            ServerPacketSender.sendVillagerSync(player, villager);

            // Open trade GUI for the villager
            ServerPacketSender.sendOpenGui(player, MillPacketIds.GUI_TRADE, villager.getId(), villager.townHallPoint);
            MillLog.minor("ServerPacketHandler", "Player " + player.getName().getString()
                    + " interacted with villager " + villager.getFirstName());
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling villager interact", e);
        } finally {
            r.release();
        }
    }

    // ========== Village list request ==========

    private static void handleVillageListRequest(IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
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

    private static void handleDeclareReleaseNumber(byte[] data, IPayloadContext context) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            String releaseNumber = r.readString();
            MillLog.minor("ServerPacketHandler", "Client declared release: " + releaseNumber);
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling release declaration", e);
        } finally {
            r.release();
        }
    }

    // ========== Dev commands ==========

    private static void handleDevCommand(int commandId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        // Only allow ops
        if (!player.hasPermissions(2)) {
            MillLog.warn("ServerPacketHandler", "Non-op player tried dev command: " + player.getName().getString());
            return;
        }

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

    private static void handleGuiAction(int actionId, byte[] data, IPayloadContext context) {
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
                handleQuestAction(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_NEWVILLAGE:
                handleNewVillage(data, context);
                break;
            case MillPacketIds.GUIACTION_HIRE_HIRE:
            case MillPacketIds.GUIACTION_HIRE_EXTEND:
            case MillPacketIds.GUIACTION_HIRE_RELEASE:
            case MillPacketIds.GUIACTION_TOGGLE_STANCE:
                handleHireAction(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_NEGATION_WAND:
                handleNegationWand(data, context);
                break;
            case MillPacketIds.GUIACTION_NEW_BUILDING_PROJECT:
            case MillPacketIds.GUIACTION_NEW_CUSTOM_BUILDING_PROJECT:
            case MillPacketIds.GUIACTION_UPDATE_CUSTOM_BUILDING_PROJECT:
                handleBuildingProject(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_MILITARY_RELATIONS:
            case MillPacketIds.GUIACTION_MILITARY_RAID:
            case MillPacketIds.GUIACTION_MILITARY_CANCEL_RAID:
                handleMilitaryAction(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_IMPORTTABLE_IMPORTBUILDINGPLAN:
            case MillPacketIds.GUIACTION_IMPORTTABLE_CHANGESETTINGS:
            case MillPacketIds.GUIACTION_IMPORTTABLE_CREATEBUILDING:
                handleImportTableAction(actionId, data, context);
                break;
            default:
                MillLog.warn("ServerPacketHandler", "Unknown GUI action: " + actionId);
                break;
        }
    }

    // ========== Map info ==========

    private static void handleMapInfoRequest(IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MillLog.minor("ServerPacketHandler", "Map info requested by " + player.getName().getString());
        // Map info packet contains village positions and culture markers for the minimap
        // Currently sends empty data — will be populated when MillWorldData tracks village positions
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeInt(0); // village count
        org.dizzymii.millenaire2.network.payloads.MillGenericS2CPayload payload =
                new org.dizzymii.millenaire2.network.payloads.MillGenericS2CPayload(
                        MillPacketIds.PACKET_MAPINFO, 0, w.toByteArray());
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, payload);
    }

    // ========== Available content ==========

    private static void handleAvailableContent(byte[] data, IPayloadContext context) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            // Client declares what cultures/content packs it has available
            int contentCount = r.hasRemaining() ? r.readInt() : 0;
            MillLog.minor("ServerPacketHandler", "Client declared " + contentCount + " available content packs");
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling available content", e);
        } finally {
            r.release();
        }
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

    private static void handleQuestAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MillLog.minor("ServerPacketHandler", "Quest action " + actionId + " from " + player.getName().getString());
        // Quest completion/refusal modifies the player's QuestInstance
        // Deferred to when Quest system is fully wired
    }

    private static void handleNewVillage(byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            String cultureKey = r.readString();
            String villageType = r.readString();
            MillLog.minor("ServerPacketHandler", "New village request: culture=" + cultureKey
                    + " type=" + villageType + " from " + player.getName().getString());
            // Village creation at player location — uses WorldGenVillage when fully wired
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling new village", e);
        } finally {
            r.release();
        }
    }

    private static void handleHireAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MillLog.minor("ServerPacketHandler", "Hire action " + actionId + " from " + player.getName().getString());
        // Hire/extend/release/toggle stance modifies villager assignment to player
    }

    private static void handleNegationWand(byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MillLog.minor("ServerPacketHandler", "Negation wand used by " + player.getName().getString());
        // Marks an area as excluded from village generation
    }

    private static void handleBuildingProject(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MillLog.minor("ServerPacketHandler", "Building project action " + actionId + " from " + player.getName().getString());
        // New/custom/update building project in village
    }

    private static void handleMilitaryAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MillLog.minor("ServerPacketHandler", "Military action " + actionId + " from " + player.getName().getString());
        // Military diplomacy: relations, raid, cancel raid between villages
    }

    private static void handleImportTableAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MillLog.minor("ServerPacketHandler", "Import table action " + actionId + " from " + player.getName().getString());
        // Import table: import building plan, change settings, create building from plan
    }
}
