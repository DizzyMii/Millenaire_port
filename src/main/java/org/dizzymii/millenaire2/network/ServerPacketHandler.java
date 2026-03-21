package org.dizzymii.millenaire2.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.entity.MillVillager;
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
        if (!(context.player() instanceof ServerPlayer player)) return;

        int entityId = payload.entityId();
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

        org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.world.MillWorldData.get(player.serverLevel());
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

        // Open trade GUI for the villager
        ServerPacketSender.sendOpenGui(player, MillPacketIds.GUI_TRADE, villager.getId(), villager.townHallPoint);
        MillLog.minor("ServerPacketHandler", "Player " + player.getName().getString()
                + " interacted with villager " + villager.getFirstName());
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
            case MillPacketIds.GUIACTION_CONTROLLEDBUILDING_TOGGLEALLOWED:
            case MillPacketIds.GUIACTION_CONTROLLEDBUILDING_FORGET:
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

    private static void handleQuestAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            String questKey = r.readString();
            int villagerEntityId = r.readInt();

            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.world.MillWorldData.get(player.serverLevel());
            org.dizzymii.millenaire2.world.UserProfile profile = mw.getProfile(player.getUUID());

            if (actionId == MillPacketIds.GUIACTION_QUEST_COMPLETESTEP) {
                // Find the active quest instance for this player
                org.dizzymii.millenaire2.quest.QuestInstance active = null;
                for (org.dizzymii.millenaire2.quest.QuestInstance qi : profile.questInstances) {
                    if (qi.quest != null && questKey.equals(qi.quest.key)) {
                        active = qi;
                        break;
                    }
                }

                if (active == null) {
                    // No active instance — this is a new quest acceptance (step 0)
                    org.dizzymii.millenaire2.quest.Quest quest = org.dizzymii.millenaire2.quest.Quest.quests.get(questKey);
                    if (quest == null) {
                        MillLog.warn("ServerPacketHandler", "Quest not found: " + questKey);
                        return;
                    }
                    // Create new quest instance
                    java.util.HashMap<String, org.dizzymii.millenaire2.quest.QuestInstanceVillager> vils = new java.util.HashMap<>();
                    active = new org.dizzymii.millenaire2.quest.QuestInstance(mw, quest, profile, vils, System.currentTimeMillis());
                    profile.questInstances.add(active);
                    MillLog.minor("ServerPacketHandler", "Quest '" + questKey + "' accepted by " + player.getName().getString());
                }

                boolean finished = active.completeStep();
                if (finished) {
                    // Award money
                    org.dizzymii.millenaire2.quest.QuestStep lastStep = active.quest != null && active.currentStep > 0
                            ? active.quest.steps.get(active.currentStep - 1) : null;
                    if (lastStep != null && lastStep.rewardMoney > 0) {
                        profile.deniers += lastStep.rewardMoney;
                    }
                    profile.questInstances.remove(active);
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "\u00a76[Mill\u00e9naire]\u00a7r Quest '" + questKey + "' completed!"));
                } else {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "\u00a76[Mill\u00e9naire]\u00a7r Quest step completed."));
                }
                mw.setDirty();

            } else if (actionId == MillPacketIds.GUIACTION_QUEST_REFUSE) {
                // Remove quest instance if it exists
                profile.questInstances.removeIf(qi -> qi.quest != null && questKey.equals(qi.quest.key));
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a76[Mill\u00e9naire]\u00a7r Quest declined."));
                mw.setDirty();
            }
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

    private static void handleHireAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            org.dizzymii.millenaire2.world.MillWorldData mw =
                    org.dizzymii.millenaire2.world.MillWorldData.get(player.serverLevel());
            if (mw == null) return;
            org.dizzymii.millenaire2.world.UserProfile profile =
                    mw.getOrCreateProfile(player.getUUID(), player.getName().getString());

            if (actionId == MillPacketIds.GUIACTION_HIRE_HIRE) {
                String unitType = r.readString();
                int cost = switch (unitType) {
                    case "soldier" -> 64;
                    case "archer" -> 96;
                    case "knight" -> 128;
                    default -> 0;
                };
                if (cost == 0) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "\u00a7c[Millénaire] Unknown unit type: " + unitType));
                    return;
                }
                if (profile.deniers < cost) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "\u00a7c[Millénaire] Not enough deniers (need " + cost + ", have " + profile.deniers + ")"));
                    return;
                }
                profile.deniers -= cost;
                mw.setDirty();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a76[Millénaire]\u00a7r Hired a " + unitType + " for " + cost + " deniers."));
                MillLog.minor("ServerPacketHandler", "Player " + player.getName().getString()
                        + " hired " + unitType + " for " + cost + " deniers");

            } else if (actionId == MillPacketIds.GUIACTION_HIRE_RELEASE) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a76[Millénaire]\u00a7r Hired soldier released."));

            } else if (actionId == MillPacketIds.GUIACTION_HIRE_EXTEND) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a76[Millénaire]\u00a7r Hire extended."));

            } else if (actionId == MillPacketIds.GUIACTION_TOGGLE_STANCE) {
                int stance = r.readInt();
                String stanceName = stance == 0 ? "Patrol" : "Defend";
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a76[Millénaire]\u00a7r Military stance set to: " + stanceName));
            }
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling hire action", e);
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

    private static void handleBuildingProject(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            org.dizzymii.millenaire2.world.MillWorldData mw =
                    org.dizzymii.millenaire2.world.MillWorldData.get(player.serverLevel());
            if (mw == null) return;

            org.dizzymii.millenaire2.util.Point townHallPos = readPoint(r);
            org.dizzymii.millenaire2.village.Building townHall = mw.getBuilding(townHallPos);
            if (townHall == null) {
                MillLog.warn("ServerPacketHandler", "Building project action: unknown townhall at " + townHallPos);
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_NEW_BUILDING_PROJECT
                    || actionId == MillPacketIds.GUIACTION_NEW_CUSTOM_BUILDING_PROJECT) {
                org.dizzymii.millenaire2.util.Point buildPos = readPoint(r);
                String planKey = r.readString();
                if (planKey == null || planKey.isBlank()) return;

                if (!townHall.buildingsBought.contains(planKey)) {
                    townHall.buildingsBought.add(planKey);
                }

                org.dizzymii.millenaire2.village.BuildingProject bp = new org.dizzymii.millenaire2.village.BuildingProject();
                bp.key = planKey;
                bp.isCustomBuilding = actionId == MillPacketIds.GUIACTION_NEW_CUSTOM_BUILDING_PROJECT;
                bp.projectTier = org.dizzymii.millenaire2.village.BuildingProject.EnumProjects.PLAYER;
                org.dizzymii.millenaire2.village.BuildingLocation loc = new org.dizzymii.millenaire2.village.BuildingLocation();
                loc.planKey = planKey;
                loc.pos = buildPos;
                loc.isCustomBuilding = bp.isCustomBuilding;
                bp.location = loc;
                townHall.buildingProjects
                        .computeIfAbsent(org.dizzymii.millenaire2.village.BuildingProject.EnumProjects.PLAYER,
                                k -> new java.util.concurrent.CopyOnWriteArrayList<>())
                        .add(bp);

                mw.setDirty();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6[Millénaire]§r Project requested: " + planKey));
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_UPDATE_CUSTOM_BUILDING_PROJECT) {
                org.dizzymii.millenaire2.util.Point buildingPos = readPoint(r);
                org.dizzymii.millenaire2.village.Building existing = mw.getBuilding(buildingPos);
                if (existing != null) {
                    mw.setDirty();
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Custom building updated."));
                }
                return;
            }

            String projectKey = r.readString();
            org.dizzymii.millenaire2.util.Point projectPos = readPoint(r);
            if (projectKey == null || projectKey.isBlank()) return;

            if (actionId == MillPacketIds.GUIACTION_CONTROLLEDBUILDING_TOGGLEALLOWED) {
                boolean allow = r.readBoolean();
                boolean changed = false;
                for (java.util.List<org.dizzymii.millenaire2.village.BuildingProject> projects : townHall.buildingProjects.values()) {
                    for (org.dizzymii.millenaire2.village.BuildingProject project : projects) {
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
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_CONTROLLEDBUILDING_FORGET) {
                boolean changed = false;
                for (java.util.List<org.dizzymii.millenaire2.village.BuildingProject> projects : townHall.buildingProjects.values()) {
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
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling building project action", e);
        } finally {
            r.release();
        }
    }

    private static void handleMilitaryAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            org.dizzymii.millenaire2.world.MillWorldData mw =
                    org.dizzymii.millenaire2.world.MillWorldData.get(player.serverLevel());
            if (mw == null) return;

            org.dizzymii.millenaire2.util.Point townHallPos = readPoint(r);
            org.dizzymii.millenaire2.village.Building townHall = mw.getBuilding(townHallPos);
            if (townHall == null) {
                MillLog.warn("ServerPacketHandler", "Military action: unknown townhall at " + townHallPos);
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_MILITARY_RELATIONS) {
                org.dizzymii.millenaire2.util.Point targetPos = readPoint(r);
                int amount = r.readInt();
                int current = townHall.getRelation(targetPos);
                townHall.setRelation(targetPos, current + amount);
                mw.setDirty();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6[Millénaire]§r Relation adjusted by " + amount + "."));
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_MILITARY_RAID) {
                org.dizzymii.millenaire2.util.Point targetPos = readPoint(r);
                townHall.raidTarget = targetPos;
                mw.setDirty();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6[Millénaire]§r Raid planned on " + targetPos));
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_MILITARY_CANCEL_RAID) {
                townHall.raidTarget = null;
                mw.setDirty();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6[Millénaire]§r Raid plan canceled."));
            }
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
            if (actionId == MillPacketIds.GUIACTION_IMPORTTABLE_IMPORTBUILDINGPLAN) {
                org.dizzymii.millenaire2.util.Point tablePos = readPoint(r);
                String source = r.readString();
                String buildingKey = r.readString();
                boolean importAll = r.readBoolean();
                int variation = r.readInt();
                int level = r.readInt();
                int orientation = r.readInt();
                boolean importMockBlocks = r.readBoolean();

                MillLog.minor("ServerPacketHandler", "ImportTable import request: table=" + tablePos
                        + " source=" + source + " key=" + buildingKey + " all=" + importAll
                        + " var=" + variation + " lvl=" + level + " ori=" + orientation
                        + " mock=" + importMockBlocks);
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_IMPORTTABLE_CHANGESETTINGS) {
                org.dizzymii.millenaire2.util.Point tablePos = readPoint(r);
                int upgradeLevel = r.readInt();
                int orientation = r.readInt();
                int startingLevel = r.readInt();
                boolean exportSnow = r.readBoolean();
                boolean importMockBlocks = r.readBoolean();
                boolean convertToPreserveGround = r.readBoolean();
                boolean exportRegularChests = r.readBoolean();

                MillLog.minor("ServerPacketHandler", "ImportTable settings: table=" + tablePos
                        + " upgrade=" + upgradeLevel + " ori=" + orientation + " start=" + startingLevel
                        + " snow=" + exportSnow + " mock=" + importMockBlocks
                        + " preserve=" + convertToPreserveGround + " chests=" + exportRegularChests);
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_IMPORTTABLE_CREATEBUILDING) {
                org.dizzymii.millenaire2.util.Point tablePos = readPoint(r);
                int length = r.readInt();
                int width = r.readInt();
                int startingLevel = r.readInt();
                boolean clearGround = r.readBoolean();

                MillLog.minor("ServerPacketHandler", "ImportTable create building: table=" + tablePos
                        + " len=" + length + " width=" + width + " start=" + startingLevel
                        + " clear=" + clearGround);
            }
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling import table action", e);
        } finally {
            r.release();
        }
    }

    private static org.dizzymii.millenaire2.util.Point readPoint(PacketDataHelper.Reader r) {
        int[] p = r.readBlockPos();
        return new org.dizzymii.millenaire2.util.Point(p[0], p[1], p[2]);
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
            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.world.MillWorldData.get(player.serverLevel());
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
