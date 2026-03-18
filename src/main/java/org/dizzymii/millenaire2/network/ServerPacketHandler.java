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

            // Resolve building trade goods and player profile
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

            // Check for quest offering before opening trade GUI
            boolean questOffered = false;
            if (mw != null) {
                org.dizzymii.millenaire2.world.UserProfile prof =
                        mw.getOrCreateProfile(player.getUUID(), player.getName().getString());
                org.dizzymii.millenaire2.util.Point vp = building != null
                        ? (building.getTownHallPos() != null ? building.getTownHallPos() : building.getPos())
                        : null;
                questOffered = tryOfferQuest(player, villager, prof, mw, vp);
            }

            // Open trade GUI if no quest was offered
            if (!questOffered) {
                ServerPacketSender.sendOpenGui(player, MillPacketIds.GUI_TRADE, villager.getId(), villager.townHallPoint);
            }
            MillLog.minor("ServerPacketHandler", "Player " + player.getName().getString()
                    + " interacted with villager " + villager.getFirstName()
                    + (questOffered ? " (quest offered)" : " (trade)"));
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
            case MillPacketIds.GUIACTION_TRADE_BUY:
            case MillPacketIds.GUIACTION_TRADE_SELL:
                handleTradeAction(actionId, data, context);
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

        org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();

        if (mw != null) {
            java.util.Collection<org.dizzymii.millenaire2.village.Building> buildings = mw.allBuildings();
            // Count townhalls (village markers) for the minimap
            int count = 0;
            for (org.dizzymii.millenaire2.village.Building b : buildings) {
                if (b.isTownhall && b.isActive && b.getPos() != null) count++;
            }
            w.writeInt(count);
            for (org.dizzymii.millenaire2.village.Building b : buildings) {
                if (!b.isTownhall || !b.isActive || b.getPos() == null) continue;
                org.dizzymii.millenaire2.util.Point pos = b.getPos();
                w.writeInt(pos.x);
                w.writeInt(pos.y);
                w.writeInt(pos.z);
                w.writeString(b.cultureKey != null ? b.cultureKey : "");
                w.writeString(b.getName() != null ? b.getName() : "Village");
                w.writeInt(b.getVillagerRecords().size());
            }
        } else {
            w.writeInt(0);
        }

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
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int entityId = r.hasRemaining() ? r.readInt() : -1;
            Entity entity = entityId >= 0 ? player.level().getEntity(entityId) : null;
            MillVillager villager = entity instanceof MillVillager mv ? mv : null;
            org.dizzymii.millenaire2.village.Building building = villager != null
                    ? (villager.getTownHallBuilding() != null ? villager.getTownHallBuilding() : villager.getHomeBuilding())
                    : null;
            if (building == null || !building.isTownhall) {
                MillLog.warn("ServerPacketHandler", "Chief action " + actionId + ": no valid townhall");
                return;
            }
            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) return;

            switch (actionId) {
                case MillPacketIds.GUIACTION_CHIEF_BUILDING -> {
                    String planSetKey = r.hasRemaining() ? r.readString() : "";
                    int priority = r.hasRemaining() ? r.readInt() : 0;
                    if (!planSetKey.isEmpty()) {
                        org.dizzymii.millenaire2.village.BuildingProject bp =
                                new org.dizzymii.millenaire2.village.BuildingProject(planSetKey, null,
                                        org.dizzymii.millenaire2.village.BuildingProject.EnumProjects.PLAYER);
                        bp.priority = priority;
                        building.buildingProjects
                                .computeIfAbsent(org.dizzymii.millenaire2.village.BuildingProject.EnumProjects.PLAYER,
                                        k -> new java.util.concurrent.CopyOnWriteArrayList<>())
                                .add(bp);
                        mw.setDirty();
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "§6[Millénaire]§r Building project '" + planSetKey + "' added."));
                    }
                }
                case MillPacketIds.GUIACTION_CHIEF_CROP -> {
                    MillLog.minor("ServerPacketHandler", "Chief crop change for " + building.getName());
                    mw.setDirty();
                }
                case MillPacketIds.GUIACTION_CHIEF_CONTROL -> {
                    if (building.controlledBy != null && building.controlledBy.equals(player.getUUID())) {
                        building.controlledBy = null;
                        building.controlledByName = null;
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "§6[Millénaire]§r Released control of " + building.getName()));
                    } else {
                        building.controlledBy = player.getUUID();
                        building.controlledByName = player.getGameProfile().getName();
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "§6[Millénaire]§r Now controlling " + building.getName()));
                    }
                    mw.setDirty();
                }
                case MillPacketIds.GUIACTION_CHIEF_DIPLOMACY -> {
                    if (r.hasRemaining()) {
                        int targetX = r.readInt();
                        int targetY = r.readInt();
                        int targetZ = r.readInt();
                        int delta = r.hasRemaining() ? r.readInt() : 10;
                        org.dizzymii.millenaire2.util.Point targetVillage = new org.dizzymii.millenaire2.util.Point(targetX, targetY, targetZ);
                        int oldRel = building.getRelation(targetVillage);
                        building.setRelation(targetVillage, oldRel + delta);
                        mw.setDirty();
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "§6[Millénaire]§r Relations adjusted by " + delta));
                    }
                }
                case MillPacketIds.GUIACTION_CHIEF_SCROLL -> {
                    MillLog.minor("ServerPacketHandler", "Chief scroll for " + building.getName());
                }
                case MillPacketIds.GUIACTION_CHIEF_HUNTING_DROP -> {
                    MillLog.minor("ServerPacketHandler", "Chief hunting drop for " + building.getName());
                }
                case MillPacketIds.GUIACTION_PUJAS_CHANGE_ENCHANTMENT -> {
                    MillLog.minor("ServerPacketHandler", "Pujas enchantment change for " + building.getName());
                }
                case MillPacketIds.GUIACTION_TRADE_TOGGLE_DONATION -> {
                    org.dizzymii.millenaire2.world.UserProfile profile =
                            mw.getOrCreateProfile(player.getUUID(), player.getName().getString());
                    profile.donationActivated = !profile.donationActivated;
                    mw.setDirty();
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Donations " + (profile.donationActivated ? "activated" : "deactivated")));
                }
            }
            MillLog.minor("ServerPacketHandler", "Chief action " + actionId + " from " + player.getName().getString());
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling chief action", e);
        } finally {
            r.release();
        }
    }

    private static void handleQuestAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            long questUniqueId = r.hasRemaining() ? r.readLong() : -1;
            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) return;
            org.dizzymii.millenaire2.world.UserProfile profile =
                    mw.getOrCreateProfile(player.getUUID(), player.getName().getString());

            org.dizzymii.millenaire2.quest.QuestInstance target = null;
            for (org.dizzymii.millenaire2.quest.QuestInstance qi : profile.questInstances) {
                if (qi.uniqueid == questUniqueId) { target = qi; break; }
            }
            if (target == null) {
                MillLog.warn("ServerPacketHandler", "Quest instance " + questUniqueId + " not found");
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_QUEST_COMPLETESTEP) {
                boolean finished = target.completeStep();
                if (finished) {
                    profile.questInstances.remove(target);
                    org.dizzymii.millenaire2.advancement.PlayerListeners.onQuestComplete(player);
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Quest '" + (target.quest != null ? target.quest.key : "?") + "' completed!"));
                } else {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Quest step completed. Next step ready."));
                }
            } else if (actionId == MillPacketIds.GUIACTION_QUEST_REFUSE) {
                target.failStep();
                profile.questInstances.remove(target);
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6[Millénaire]§r Quest refused."));
            }
            mw.setDirty();
            MillLog.minor("ServerPacketHandler", "Quest action " + actionId + " from " + player.getName().getString());
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling quest action", e);
        } finally {
            r.release();
        }
    }

    private static void handleNewVillage(byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            String cultureKey = r.readString();
            String villageType = r.readString();

            org.dizzymii.millenaire2.culture.Culture culture = org.dizzymii.millenaire2.culture.Culture.getCultureByName(cultureKey);
            if (culture == null) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§c[Millénaire]§r Unknown culture: " + cultureKey));
                return;
            }
            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) return;

            net.minecraft.core.BlockPos blockPos = player.blockPosition();
            boolean success = org.dizzymii.millenaire2.world.WorldGenVillage.generateNewVillage(
                    (net.minecraft.server.level.ServerLevel) player.level(), blockPos, culture, mw, player.level().random);
            if (success) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6[Millénaire]§r New " + cultureKey + " village created at your location!"));
            } else {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§c[Millénaire]§r Failed to create village. Check terrain or culture data."));
            }
            MillLog.minor("ServerPacketHandler", "New village request: culture=" + cultureKey
                    + " type=" + villageType + " from " + player.getName().getString());
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling new village", e);
        } finally {
            r.release();
        }
    }

    private static void handleHireAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int entityId = r.hasRemaining() ? r.readInt() : -1;
            Entity entity = entityId >= 0 ? player.level().getEntity(entityId) : null;
            if (!(entity instanceof MillVillager villager)) {
                MillLog.warn("ServerPacketHandler", "Hire action: villager " + entityId + " not found");
                return;
            }

            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) return;
            org.dizzymii.millenaire2.world.UserProfile profile =
                    mw.getOrCreateProfile(player.getUUID(), player.getName().getString());

            // Find the VillagerRecord
            org.dizzymii.millenaire2.village.Building home = villager.getHomeBuilding();
            if (home == null) home = villager.getTownHallBuilding();
            org.dizzymii.millenaire2.village.VillagerRecord vr = home != null
                    ? home.getVillagerRecord(villager.getVillagerId()) : null;

            int hireCost = 64; // deniers per hire period
            switch (actionId) {
                case MillPacketIds.GUIACTION_HIRE_HIRE -> {
                    if (profile.deniers < hireCost) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "§c[Millénaire]§r Not enough deniers! Need " + hireCost));
                        return;
                    }
                    profile.deniers -= hireCost;
                    villager.hiredBy = player.getUUID().toString();
                    if (vr != null) vr.awayhired = true;
                    mw.setDirty();
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Hired " + villager.getFirstName() + "!"));
                }
                case MillPacketIds.GUIACTION_HIRE_EXTEND -> {
                    if (villager.hiredBy == null || !villager.hiredBy.equals(player.getUUID().toString())) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "§c[Millénaire]§r This villager is not hired by you."));
                        return;
                    }
                    if (profile.deniers < hireCost / 2) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "§c[Millénaire]§r Not enough deniers! Need " + (hireCost / 2)));
                        return;
                    }
                    profile.deniers -= hireCost / 2;
                    mw.setDirty();
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Extended hire of " + villager.getFirstName() + "."));
                }
                case MillPacketIds.GUIACTION_HIRE_RELEASE -> {
                    villager.hiredBy = null;
                    if (vr != null) vr.awayhired = false;
                    mw.setDirty();
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Released " + villager.getFirstName() + "."));
                }
                case MillPacketIds.GUIACTION_TOGGLE_STANCE -> {
                    villager.aggressiveStance = !villager.aggressiveStance;
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r " + villager.getFirstName() + " stance: "
                                    + (villager.aggressiveStance ? "Aggressive" : "Passive")));
                }
            }
            MillLog.minor("ServerPacketHandler", "Hire action " + actionId + " from " + player.getName().getString());
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling hire action", e);
        } finally {
            r.release();
        }
    }

    private static void handleNegationWand(byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int x = r.readInt();
            int y = r.readInt();
            int z = r.readInt();
            int radius = r.hasRemaining() ? r.readInt() : 64;

            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) return;
            // Store the negation zone as a global tag so village generation skips it
            String negTag = "negation_" + x + "_" + y + "_" + z + "_" + radius;
            mw.addGlobalTag(negTag);
            mw.setDirty();
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§6[Millénaire]§r Negation zone set at [" + x + ", " + y + ", " + z + "] radius " + radius));
            MillLog.minor("ServerPacketHandler", "Negation wand: " + negTag + " by " + player.getName().getString());
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling negation wand", e);
        } finally {
            r.release();
        }
    }

    private static void handleBuildingProject(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int entityId = r.hasRemaining() ? r.readInt() : -1;
            String planSetKey = r.hasRemaining() ? r.readString() : "";

            org.dizzymii.millenaire2.village.Building building = resolveVillageBuilding(player, entityId);
            if (building == null) return;
            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) return;

            switch (actionId) {
                case MillPacketIds.GUIACTION_NEW_BUILDING_PROJECT -> {
                    org.dizzymii.millenaire2.village.BuildingProject bp =
                            new org.dizzymii.millenaire2.village.BuildingProject(planSetKey, null,
                                    org.dizzymii.millenaire2.village.BuildingProject.EnumProjects.PLAYER);
                    building.buildingProjects
                            .computeIfAbsent(org.dizzymii.millenaire2.village.BuildingProject.EnumProjects.PLAYER,
                                    k -> new java.util.concurrent.CopyOnWriteArrayList<>())
                            .add(bp);
                    mw.setDirty();
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Building project '" + planSetKey + "' queued."));
                }
                case MillPacketIds.GUIACTION_NEW_CUSTOM_BUILDING_PROJECT -> {
                    org.dizzymii.millenaire2.village.BuildingProject bp =
                            new org.dizzymii.millenaire2.village.BuildingProject(planSetKey, null,
                                    org.dizzymii.millenaire2.village.BuildingProject.EnumProjects.PLAYER);
                    bp.isCustomBuilding = true;
                    building.buildingProjects
                            .computeIfAbsent(org.dizzymii.millenaire2.village.BuildingProject.EnumProjects.PLAYER,
                                    k -> new java.util.concurrent.CopyOnWriteArrayList<>())
                            .add(bp);
                    mw.setDirty();
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Custom building project '" + planSetKey + "' queued."));
                }
                case MillPacketIds.GUIACTION_UPDATE_CUSTOM_BUILDING_PROJECT -> {
                    int priority = r.hasRemaining() ? r.readInt() : 0;
                    var playerProjects = building.buildingProjects.get(
                            org.dizzymii.millenaire2.village.BuildingProject.EnumProjects.PLAYER);
                    if (playerProjects != null) {
                        for (org.dizzymii.millenaire2.village.BuildingProject bp : playerProjects) {
                            if (planSetKey.equals(bp.key)) {
                                bp.priority = priority;
                                break;
                            }
                        }
                    }
                    mw.setDirty();
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Updated project '" + planSetKey + "' priority."));
                }
            }
            MillLog.minor("ServerPacketHandler", "Building project action " + actionId + " from " + player.getName().getString());
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling building project", e);
        } finally {
            r.release();
        }
    }

    private static void handleMilitaryAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int entityId = r.hasRemaining() ? r.readInt() : -1;
            org.dizzymii.millenaire2.village.Building building = resolveVillageBuilding(player, entityId);
            if (building == null) return;
            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) return;

            switch (actionId) {
                case MillPacketIds.GUIACTION_MILITARY_RELATIONS -> {
                    if (r.hasRemaining()) {
                        int tx = r.readInt();
                        int ty = r.readInt();
                        int tz = r.readInt();
                        org.dizzymii.millenaire2.util.Point targetVillage = new org.dizzymii.millenaire2.util.Point(tx, ty, tz);
                        int current = building.getRelation(targetVillage);
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "§6[Millénaire]§r Relations with village at [" + tx + "," + ty + "," + tz + "]: " + current));
                    }
                }
                case MillPacketIds.GUIACTION_MILITARY_RAID -> {
                    if (r.hasRemaining()) {
                        int tx = r.readInt();
                        int ty = r.readInt();
                        int tz = r.readInt();
                        org.dizzymii.millenaire2.util.Point targetVillage = new org.dizzymii.millenaire2.util.Point(tx, ty, tz);
                        building.raidTarget = targetVillage;
                        building.underAttack = false;
                        // Mark raiding villagers
                        for (org.dizzymii.millenaire2.village.VillagerRecord vr : building.getVillagerRecords()) {
                            if (!vr.killed && !vr.awayhired) {
                                vr.awayraiding = true;
                            }
                        }
                        // Decrease relations
                        building.setRelation(targetVillage, building.getRelation(targetVillage) - 30);
                        building.raidsPerformed.add(tx + "," + ty + "," + tz);
                        mw.setDirty();
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "§6[Millénaire]§r Raid launched against village at [" + tx + "," + ty + "," + tz + "]!"));
                    }
                }
                case MillPacketIds.GUIACTION_MILITARY_CANCEL_RAID -> {
                    building.raidTarget = null;
                    for (org.dizzymii.millenaire2.village.VillagerRecord vr : building.getVillagerRecords()) {
                        vr.awayraiding = false;
                    }
                    mw.setDirty();
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Raid cancelled."));
                }
            }
            MillLog.minor("ServerPacketHandler", "Military action " + actionId + " from " + player.getName().getString());
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling military action", e);
        } finally {
            r.release();
        }
    }

    private static void handleImportTableAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            switch (actionId) {
                case MillPacketIds.GUIACTION_IMPORTTABLE_IMPORTBUILDINGPLAN -> {
                    String planPath = r.hasRemaining() ? r.readString() : "";
                    MillLog.minor("ServerPacketHandler", "Import building plan: " + planPath);
                    org.dizzymii.millenaire2.advancement.PlayerListeners.onBuildingPlanImported(player);
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Building plan imported."));
                }
                case MillPacketIds.GUIACTION_IMPORTTABLE_CHANGESETTINGS -> {
                    MillLog.minor("ServerPacketHandler", "Import table settings changed.");
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Import settings updated."));
                }
                case MillPacketIds.GUIACTION_IMPORTTABLE_CREATEBUILDING -> {
                    String planKey = r.hasRemaining() ? r.readString() : "";
                    MillLog.minor("ServerPacketHandler", "Create building from import: " + planKey);
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Building created from plan '" + planKey + "'."));
                }
            }
            MillLog.minor("ServerPacketHandler", "Import table action " + actionId + " from " + player.getName().getString());
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling import table action", e);
        } finally {
            r.release();
        }
    }

    /**
     * Helper: resolve the townhall Building from an entity ID or nearest villager.
     */
    @javax.annotation.Nullable
    private static org.dizzymii.millenaire2.village.Building resolveVillageBuilding(
            ServerPlayer player, int entityId) {
        Entity entity = entityId >= 0 ? player.level().getEntity(entityId) : null;
        if (entity instanceof MillVillager mv) {
            org.dizzymii.millenaire2.village.Building b = mv.getTownHallBuilding();
            if (b != null) return b;
            return mv.getHomeBuilding();
        }
        // Fallback: find nearest village
        org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
        if (mw == null) return null;
        double best = Double.MAX_VALUE;
        org.dizzymii.millenaire2.village.Building nearest = null;
        for (org.dizzymii.millenaire2.village.Building b : mw.allBuildings()) {
            if (!b.isTownhall || b.getPos() == null) continue;
            double dist = player.distanceToSqr(b.getPos().x, b.getPos().y, b.getPos().z);
            if (dist < best) { best = dist; nearest = b; }
        }
        return nearest;
    }

    // ========== Quest offering ==========

    /**
     * Try to offer a quest to the player during villager interaction.
     * Checks all loaded quests against eligibility (reputation, tags, chance).
     * Returns true if a quest was offered (quest GUI opened instead of trade).
     */
    private static boolean tryOfferQuest(ServerPlayer player, MillVillager villager,
                                          org.dizzymii.millenaire2.world.UserProfile profile,
                                          org.dizzymii.millenaire2.world.MillWorldData mw,
                                          @javax.annotation.Nullable org.dizzymii.millenaire2.util.Point villagePos) {
        if (org.dizzymii.millenaire2.quest.Quest.quests.isEmpty()) return false;

        // Don't offer if player already has max simultaneous quests
        if (profile.questInstances.size() >= 3) return false;

        // Check each quest for eligibility
        for (org.dizzymii.millenaire2.quest.Quest quest : org.dizzymii.millenaire2.quest.Quest.quests.values()) {
            if (quest.key == null) continue;
            if (!quest.canStart(profile, mw, villagePos)) continue;

            // Check if player already has this quest active
            boolean alreadyActive = false;
            for (org.dizzymii.millenaire2.quest.QuestInstance qi : profile.questInstances) {
                if (qi.quest != null && quest.key.equals(qi.quest.key)) {
                    alreadyActive = true;
                    break;
                }
            }
            if (alreadyActive) continue;

            // Probability check (chanceperhour converted to per-interaction ~1/20 of hourly)
            double chance = quest.chanceperhour / 20.0;
            if (chance > 0 && player.level().random.nextDouble() > chance) continue;

            // Quest is eligible — send offer to client and open quest GUI
            ServerPacketSender.sendQuestInstance(player, villager.getId(), quest, 0, true);
            ServerPacketSender.sendOpenGui(player, MillPacketIds.GUI_QUEST, villager.getId(), villagePos);

            MillLog.minor("ServerPacketHandler", "Offered quest '" + quest.key + "' to " + player.getName().getString());
            return true;
        }
        return false;
    }

    // ========== Trade ==========

    /**
     * Handle a trade buy or sell action from the client.
     * Packet data: int entityId, int tradeGoodIndex
     */
    private static void handleTradeAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            int entityId = r.readInt();
            int goodIndex = r.readInt();

            Entity entity = player.level().getEntity(entityId);
            if (!(entity instanceof MillVillager villager)) {
                MillLog.warn("ServerPacketHandler", "Trade: villager entity " + entityId + " not found");
                return;
            }

            // Resolve the building with trade goods
            org.dizzymii.millenaire2.village.Building building = villager.getHomeBuilding();
            if (building == null) building = villager.getTownHallBuilding();
            if (building == null) {
                MillLog.warn("ServerPacketHandler", "Trade: no building for villager " + villager.getFirstName());
                return;
            }

            java.util.List<org.dizzymii.millenaire2.item.TradeGood> goods = building.getTradeGoods();
            if (goodIndex < 0 || goodIndex >= goods.size()) {
                MillLog.warn("ServerPacketHandler", "Trade: invalid good index " + goodIndex);
                return;
            }

            org.dizzymii.millenaire2.item.TradeGood good = goods.get(goodIndex);

            // Get player profile for deniers and reputation
            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) return;
            org.dizzymii.millenaire2.world.UserProfile profile =
                    mw.getOrCreateProfile(player.getUUID(), player.getName().getString());

            org.dizzymii.millenaire2.util.Point villagePos =
                    building.getTownHallPos() != null ? building.getTownHallPos() : building.getPos();
            int reputation = villagePos != null ? profile.getVillageReputation(villagePos) : 0;

            if (actionId == MillPacketIds.GUIACTION_TRADE_BUY) {
                executeBuy(player, profile, good, reputation, villagePos, mw);
            } else {
                executeSell(player, profile, good, reputation, villagePos, mw);
            }
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling trade action", e);
        } finally {
            r.release();
        }
    }

    private static void executeBuy(ServerPlayer player,
                                    org.dizzymii.millenaire2.world.UserProfile profile,
                                    org.dizzymii.millenaire2.item.TradeGood good,
                                    int reputation,
                                    @javax.annotation.Nullable org.dizzymii.millenaire2.util.Point villagePos,
                                    org.dizzymii.millenaire2.world.MillWorldData mw) {
        int price = good.getAdjustedBuyPrice(reputation);
        if (price <= 0 || good.item.isEmpty()) return;

        if (profile.deniers < price) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§c[Millénaire]§r Not enough deniers! Need " + price + ", have " + profile.deniers));
            return;
        }

        // Give item to player
        if (!player.getInventory().add(good.item.copy())) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§c[Millénaire]§r Inventory full!"));
            return;
        }

        profile.deniers -= price;
        // Reputation gain from buying
        if (villagePos != null) {
            profile.adjustVillageReputation(villagePos, 1);
        }
        mw.setDirty();

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "§6[Millénaire]§r Bought " + good.item.getHoverName().getString()
                        + " for " + price + " deniers"));
        MillLog.minor("ServerPacketHandler", "Trade buy: " + player.getName().getString()
                + " bought " + good.item.getHoverName().getString() + " for " + price + "d");
    }

    private static void executeSell(ServerPlayer player,
                                     org.dizzymii.millenaire2.world.UserProfile profile,
                                     org.dizzymii.millenaire2.item.TradeGood good,
                                     int reputation,
                                     @javax.annotation.Nullable org.dizzymii.millenaire2.util.Point villagePos,
                                     org.dizzymii.millenaire2.world.MillWorldData mw) {
        int price = good.getAdjustedSellPrice(reputation);
        if (price <= 0 || good.item.isEmpty()) return;

        // Check player has the item
        int slot = findItemSlot(player, good.item);
        if (slot < 0) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§c[Millénaire]§r You don't have that item!"));
            return;
        }

        // Remove one item from player inventory
        net.minecraft.world.item.ItemStack slotStack = player.getInventory().getItem(slot);
        slotStack.shrink(1);
        if (slotStack.isEmpty()) {
            player.getInventory().setItem(slot, net.minecraft.world.item.ItemStack.EMPTY);
        }

        profile.deniers += price;
        // Reputation gain from selling
        if (villagePos != null) {
            profile.adjustVillageReputation(villagePos, 1);
        }
        mw.setDirty();

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "§6[Millénaire]§r Sold " + good.item.getHoverName().getString()
                        + " for " + price + " deniers"));
        MillLog.minor("ServerPacketHandler", "Trade sell: " + player.getName().getString()
                + " sold " + good.item.getHoverName().getString() + " for " + price + "d");
    }

    private static int findItemSlot(ServerPlayer player, net.minecraft.world.item.ItemStack target) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            net.minecraft.world.item.ItemStack stack = player.getInventory().getItem(i);
            if (net.minecraft.world.item.ItemStack.isSameItemSameComponents(stack, target) && !stack.isEmpty()) {
                return i;
            }
        }
        return -1;
    }
}
