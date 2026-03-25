package org.dizzymii.millenaire2.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.network.handlers.BuildingProjectHandler;
import org.dizzymii.millenaire2.network.handlers.ChiefActionHandler;
import org.dizzymii.millenaire2.network.handlers.DevCommandHandler;
import org.dizzymii.millenaire2.network.handlers.HireHandler;
import org.dizzymii.millenaire2.network.handlers.ImportTableHandler;
import org.dizzymii.millenaire2.network.handlers.MilitaryHandler;
import org.dizzymii.millenaire2.network.handlers.NegationWandHandler;
import org.dizzymii.millenaire2.network.handlers.NewVillageHandler;
import org.dizzymii.millenaire2.network.handlers.QuestHandler;
import org.dizzymii.millenaire2.network.handlers.TradeHandler;
import org.dizzymii.millenaire2.network.payloads.MillGenericC2SPayload;
import org.dizzymii.millenaire2.util.MillLog;

/**
 * Server-side handler for client-to-server packets.
 * Dispatches incoming generic payloads to the appropriate handler based on packet type.
 *
 * Each logical group of actions is handled by a dedicated class in the
 * {@code network.handlers} subpackage for easier maintainability and debugging.
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
                DevCommandHandler.handle(subType, payload.data(), context);
                break;
            default:
                MillLog.warn("ServerPacketHandler", "Unknown C2S packet type: " + type + "/" + subType);
                break;
        }
    }

    // ========== GUI action dispatcher ==========

    private static void handleGuiAction(int actionId, byte[] data, IPayloadContext context) {
        switch (actionId) {
            case MillPacketIds.GUIACTION_CHIEF_BUILDING:
            case MillPacketIds.GUIACTION_CHIEF_CROP:
            case MillPacketIds.GUIACTION_CHIEF_CONTROL:
            case MillPacketIds.GUIACTION_CHIEF_DIPLOMACY:
            case MillPacketIds.GUIACTION_CHIEF_SCROLL:
            case MillPacketIds.GUIACTION_CHIEF_HUNTING_DROP:
            case MillPacketIds.GUIACTION_PUJAS_CHANGE_ENCHANTMENT:
            case MillPacketIds.GUIACTION_TRADE_TOGGLE_DONATION:
                ChiefActionHandler.handle(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_QUEST_COMPLETESTEP:
            case MillPacketIds.GUIACTION_QUEST_REFUSE:
                QuestHandler.handle(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_NEWVILLAGE:
                NewVillageHandler.handle(data, context);
                break;
            case MillPacketIds.GUIACTION_HIRE_HIRE:
            case MillPacketIds.GUIACTION_HIRE_EXTEND:
            case MillPacketIds.GUIACTION_HIRE_RELEASE:
            case MillPacketIds.GUIACTION_TOGGLE_STANCE:
                HireHandler.handle(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_TRADE_BUY:
            case MillPacketIds.GUIACTION_TRADE_SELL:
                TradeHandler.handle(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_NEGATION_WAND:
                NegationWandHandler.handle(data, context);
                break;
            case MillPacketIds.GUIACTION_NEW_BUILDING_PROJECT:
            case MillPacketIds.GUIACTION_NEW_CUSTOM_BUILDING_PROJECT:
            case MillPacketIds.GUIACTION_UPDATE_CUSTOM_BUILDING_PROJECT:
            case MillPacketIds.GUIACTION_CONTROLLEDBUILDING_TOGGLEALLOWED:
            case MillPacketIds.GUIACTION_CONTROLLEDBUILDING_FORGET:
                BuildingProjectHandler.handle(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_MILITARY_RELATIONS:
            case MillPacketIds.GUIACTION_MILITARY_RAID:
            case MillPacketIds.GUIACTION_MILITARY_CANCEL_RAID:
                MilitaryHandler.handle(actionId, data, context);
                break;
            case MillPacketIds.GUIACTION_IMPORTTABLE_IMPORTBUILDINGPLAN:
            case MillPacketIds.GUIACTION_IMPORTTABLE_CHANGESETTINGS:
            case MillPacketIds.GUIACTION_IMPORTTABLE_CREATEBUILDING:
                ImportTableHandler.handle(actionId, data, context);
                break;
            default:
                MillLog.warn("ServerPacketHandler", "Unknown GUI action: " + actionId);
                break;
        }
    }

    // ========== Villager interaction ==========

    private static void handleVillagerInteract(byte[] data, IPayloadContext context) {
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int entityId = r.readInt();

            if (!(context.player() instanceof ServerPlayer player)) return;
            net.minecraft.world.entity.Entity entity = player.level().getEntity(entityId);
            if (!(entity instanceof MillVillager villager)) {
                MillLog.warn("ServerPacketHandler", "Villager interact: entity " + entityId + " not found");
                return;
            }

            ServerPacketSender.sendVillagerSync(player, villager);

            org.dizzymii.millenaire2.village.Building building = villager.getHomeBuilding();
            if (building == null) building = villager.getTownHallBuilding();

            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw != null && building != null) {
                org.dizzymii.millenaire2.world.UserProfile profile =
                        mw.getOrCreateProfile(player.getUUID(), player.getName().getString());
                org.dizzymii.millenaire2.util.Point vPos =
                        building.getTownHallPos() != null ? building.getTownHallPos() : building.getPos();
                int rep = vPos != null ? profile.getVillageReputation(vPos) : 0;
                String vName = villager.getFirstName() + " " + villager.getFamilyName();
                ServerPacketSender.sendTradeData(player, villager.getId(),
                        building.getTradeGoods(), profile.deniers, rep, vName);
            }

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

    // ========== Map info ==========

    private static void handleMapInfoRequest(IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        MillLog.minor("ServerPacketHandler", "Map info requested by " + player.getName().getString());
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
            int contentCount = r.hasRemaining() ? r.readInt() : 0;
            MillLog.minor("ServerPacketHandler", "Client declared " + contentCount + " available content packs");
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling available content", e);
        } finally {
            r.release();
        }
    }
}
