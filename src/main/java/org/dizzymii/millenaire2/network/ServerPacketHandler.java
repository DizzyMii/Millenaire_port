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

                // Offer an eligible quest first (original flow prioritises quest interactions)
                for (org.dizzymii.millenaire2.quest.Quest quest : org.dizzymii.millenaire2.quest.Quest.quests.values()) {
                    if (quest == null || quest.key == null || quest.steps.isEmpty()) {
                        continue;
                    }

                    boolean alreadyActive = false;
                    for (org.dizzymii.millenaire2.quest.QuestInstance qi : profile.questInstances) {
                        org.dizzymii.millenaire2.quest.Quest activeQuest = qi.quest;
                        String activeKey = activeQuest != null ? activeQuest.key : null;
                        if (activeKey != null && quest.key.equals(activeKey)) {
                            alreadyActive = true;
                            break;
                        }
                    }
                    if (alreadyActive) {
                        continue;
                    }

                    if (!quest.canStart(profile, mw, vPos)) {
                        continue;
                    }

                    if (quest.chanceperhour > 0.0) {
                        double chance = Math.max(0.0, Math.min(1.0, quest.chanceperhour));
                        if (player.getRandom().nextDouble() > chance) {
                            continue;
                        }
                    }

                    org.dizzymii.millenaire2.quest.QuestStep firstStep = quest.steps.get(0);
                    String description = pickLocalizedText(firstStep.descriptions, "Quest available");
                    String label = pickLocalizedText(firstStep.labels, quest.key);

                    ServerPacketSender.sendQuestInstance(
                            player,
                            quest.key,
                            0,
                            quest.steps.size(),
                            description,
                            label,
                            firstStep.rewardMoney,
                            firstStep.rewardReputation,
                            villager.getId(),
                            true
                    );
                    ServerPacketSender.sendOpenGui(player, MillPacketIds.GUI_QUEST, villager.getId(), villager.townHallPoint);
                    MillLog.minor("ServerPacketHandler", "Offered quest '" + quest.key + "' to " + player.getName().getString());
                    return;
                }

                ServerPacketSender.sendTradeData(player, villager.getId(),
                        building.getTradeGoods(), profile.deniers, rep, vName);
            }

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
        java.util.List<org.dizzymii.millenaire2.util.Point> villages = mw != null
                ? mw.getCombinedVillagesLoneBuildings()
                : java.util.List.of();

        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeInt(villages.size());
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
            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) {
                return;
            }
            org.dizzymii.millenaire2.world.UserProfile profile =
                    mw.getOrCreateProfile(player.getUUID(), player.getName().getString());
            org.dizzymii.millenaire2.village.Building townHall = findNearestTownHall(player, mw, 512.0);

            switch (actionId) {
                case MillPacketIds.GUIACTION_TRADE_TOGGLE_DONATION -> {
                    profile.donationActivated = !profile.donationActivated;
                    mw.setDirty();
                    ServerPacketSender.sendProfile(player, profile, org.dizzymii.millenaire2.world.UserProfile.UPDATE_ACTIONDATA);
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Donation mode " + (profile.donationActivated ? "enabled" : "disabled") + "."));
                }
                case MillPacketIds.GUIACTION_PUJAS_CHANGE_ENCHANTMENT -> {
                    final int pujaCost = 16;
                    if (profile.deniers < pujaCost) {
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "§c[Millénaire] Not enough deniers for pujas."));
                        return;
                    }
                    profile.deniers -= pujaCost;
                    if (townHall != null && townHall.getPos() != null) {
                        profile.adjustVillageReputation(townHall.getPos(), 32);
                    }
                    profile.addTag("pujas.performed");
                    mw.setDirty();
                    ServerPacketSender.sendProfile(player, profile, org.dizzymii.millenaire2.world.UserProfile.UPDATE_REPUTATION);
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Offering accepted."));
                }
                case MillPacketIds.GUIACTION_CHIEF_BUILDING -> {
                    if (townHall == null) {
                        return;
                    }
                    String planSet = r.hasRemaining() ? r.readString() : null;
                    if (planSet != null && !planSet.isEmpty()) {
                        townHall.buildingsBought.add(planSet);
                        mw.setDirty();
                    }
                }
                case MillPacketIds.GUIACTION_CHIEF_CROP -> {
                    String cropKey = r.hasRemaining() ? r.readString() : null;
                    if (cropKey != null && !cropKey.isEmpty()) {
                        mw.setGlobalTag("chief.crop." + cropKey);
                        mw.setDirty();
                    }
                }
                case MillPacketIds.GUIACTION_CHIEF_CONTROL -> {
                    String controlKey = r.hasRemaining() ? r.readString() : "default";
                    profile.addTag("chief.control." + controlKey);
                    mw.setDirty();
                }
                case MillPacketIds.GUIACTION_CHIEF_DIPLOMACY -> {
                    if (townHall == null || !r.hasRemaining()) {
                        return;
                    }
                    int[] targetPos = r.readBlockPos();
                    int relation = r.hasRemaining() ? r.readInt() : 0;
                    org.dizzymii.millenaire2.util.Point targetPoint =
                            new org.dizzymii.millenaire2.util.Point(targetPos[0], targetPos[1], targetPos[2]);
                    org.dizzymii.millenaire2.village.Building target = mw.getBuilding(targetPoint);
                    if (target != null && townHall.getPos() != null && target.getPos() != null) {
                        townHall.setRelation(target.getPos(), relation);
                        target.setRelation(townHall.getPos(), relation);
                        mw.setDirty();
                    }
                }
                case MillPacketIds.GUIACTION_CHIEF_SCROLL -> {
                    int scroll = r.hasRemaining() ? r.readInt() : 0;
                    profile.addTag("chief.scroll." + scroll);
                    mw.setDirty();
                }
                case MillPacketIds.GUIACTION_CHIEF_HUNTING_DROP -> {
                    boolean drop = r.hasRemaining() && r.readBoolean();
                    if (drop) {
                        profile.addTag("chief.hunting.drop");
                    } else {
                        profile.removeTag("chief.hunting.drop");
                    }
                    mw.setDirty();
                }
                default -> MillLog.minor("ServerPacketHandler", "Unhandled chief action " + actionId);
            }
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
            String questKey = r.readString();
            int villagerEntityId = r.readInt();

            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null || questKey == null || questKey.isEmpty()) {
                return;
            }
            org.dizzymii.millenaire2.world.UserProfile profile =
                    mw.getOrCreateProfile(player.getUUID(), player.getName().getString());

            if (actionId == MillPacketIds.GUIACTION_QUEST_COMPLETESTEP) {
                // Find the active quest instance for this player
                org.dizzymii.millenaire2.quest.QuestInstance active = null;
                for (org.dizzymii.millenaire2.quest.QuestInstance qi : profile.questInstances) {
                    org.dizzymii.millenaire2.quest.Quest qiQuest = qi.quest;
                    String qiQuestKey = qiQuest != null ? qiQuest.key : null;
                    if (qiQuestKey != null && questKey.equals(qiQuestKey)) {
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

                    org.dizzymii.millenaire2.quest.QuestStep acceptedStep = active.getCurrentStep();
                    if (acceptedStep != null && active.quest != null) {
                        ServerPacketSender.sendQuestInstance(
                                player,
                                active.quest.key,
                                active.currentStep,
                                active.quest.steps.size(),
                                pickLocalizedText(acceptedStep.descriptions, "Quest accepted"),
                                pickLocalizedText(acceptedStep.labels, active.quest.key),
                                acceptedStep.rewardMoney,
                                acceptedStep.rewardReputation,
                                villagerEntityId,
                                false
                        );
                    }
                }

                boolean finished = active.completeStep();
                if (finished) {
                    // Award money
                    org.dizzymii.millenaire2.quest.Quest activeQuest = active.quest;
                    org.dizzymii.millenaire2.quest.QuestStep lastStep = activeQuest != null
                            && active.currentStep > 0
                            && active.currentStep - 1 < activeQuest.steps.size()
                            ? activeQuest.steps.get(active.currentStep - 1)
                            : null;
                    if (lastStep != null && lastStep.rewardMoney > 0) {
                        profile.deniers += lastStep.rewardMoney;
                    }
                    profile.questInstances.remove(active);
                    ServerPacketSender.sendQuestInstanceDestroy(player, questKey);
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "\u00a76[Mill\u00e9naire]\u00a7r Quest '" + questKey + "' completed!"));
                } else {
                    org.dizzymii.millenaire2.quest.QuestStep nextStep = active.getCurrentStep();
                    if (nextStep != null && active.quest != null) {
                        ServerPacketSender.sendQuestInstance(
                                player,
                                active.quest.key,
                                active.currentStep,
                                active.quest.steps.size(),
                                pickLocalizedText(nextStep.descriptions, "Quest updated"),
                                pickLocalizedText(nextStep.labels, active.quest.key),
                                nextStep.rewardMoney,
                                nextStep.rewardReputation,
                                villagerEntityId,
                                false
                        );
                    }
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "\u00a76[Mill\u00e9naire]\u00a7r Quest step completed."));
                }
                mw.setDirty();

            } else if (actionId == MillPacketIds.GUIACTION_QUEST_REFUSE) {
                // Remove quest instance if it exists
                profile.questInstances.removeIf(qi -> {
                    org.dizzymii.millenaire2.quest.Quest qiQuest = qi.quest;
                    String qiQuestKey = qiQuest != null ? qiQuest.key : null;
                    return qiQuestKey != null && questKey.equals(qiQuestKey);
                });
                ServerPacketSender.sendQuestInstanceDestroy(player, questKey);
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
                    org.dizzymii.millenaire2.Millenaire2.getWorldData();
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
                    org.dizzymii.millenaire2.Millenaire2.getWorldData();
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
                org.dizzymii.millenaire2.Millenaire2.getWorldData();
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
            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) {
                return;
            }

            org.dizzymii.millenaire2.village.Building th = null;
            if (r.hasRemaining()) {
                int[] pos = r.readBlockPos();
                th = mw.getBuilding(new org.dizzymii.millenaire2.util.Point(pos[0], pos[1], pos[2]));
            }
            if (th == null) {
                th = findNearestTownHall(player, mw, 256.0);
            }
            if (th == null) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§c[Millénaire] No nearby townhall found for project action."));
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_NEW_BUILDING_PROJECT) {
                String planSet = r.hasRemaining() ? r.readString() : null;
                if (planSet != null && !planSet.isEmpty()) {
                    th.buildingsBought.add(planSet);
                    mw.setDirty();
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Building project requested: " + planSet));
                }
            } else if (actionId == MillPacketIds.GUIACTION_NEW_CUSTOM_BUILDING_PROJECT) {
                String customKey = r.hasRemaining() ? r.readString() : null;
                if (customKey != null && !customKey.isEmpty()) {
                    th.buildingsBought.add("custom:" + customKey);
                    mw.setDirty();
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Custom project requested: " + customKey));
                }
            } else if (actionId == MillPacketIds.GUIACTION_UPDATE_CUSTOM_BUILDING_PROJECT) {
                String customKey = r.hasRemaining() ? r.readString() : null;
                if (customKey != null && !customKey.isEmpty()) {
                    th.buildingsBought.remove("custom:" + customKey);
                    th.buildingsBought.add("custom:" + customKey);
                    mw.setDirty();
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Custom project updated: " + customKey));
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
            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) {
                return;
            }

            org.dizzymii.millenaire2.village.Building source = findNearestTownHall(player, mw, 512.0);
            if (source == null) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§c[Millénaire] No nearby townhall found for military action."));
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_MILITARY_CANCEL_RAID) {
                source.cancelRaid();
                mw.setDirty();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6[Millénaire]§r Raid cancelled."));
                return;
            }

            org.dizzymii.millenaire2.village.Building target = null;
            if (r.hasRemaining()) {
                int[] pos = r.readBlockPos();
                target = mw.getBuilding(new org.dizzymii.millenaire2.util.Point(pos[0], pos[1], pos[2]));
            }

            if (target == null || target.getPos() == null) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§c[Millénaire] Invalid military target."));
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_MILITARY_RELATIONS) {
                int relation = r.hasRemaining() ? r.readInt() : 0;
                source.setRelation(target.getPos(), relation);
                target.setRelation(source.getPos(), relation);
                mw.setDirty();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6[Millénaire]§r Relation set to " + relation + "."));
            } else if (actionId == MillPacketIds.GUIACTION_MILITARY_RAID) {
                source.raidTarget = target.getPos();
                source.underAttack = false;
                mw.setDirty();
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6[Millénaire]§r Raid target set."));
            }
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling military action", e);
        } finally {
            r.release();
        }
    }

    private static org.dizzymii.millenaire2.village.Building findNearestTownHall(
            ServerPlayer player,
            org.dizzymii.millenaire2.world.MillWorldData mw,
            double maxDistance) {
        org.dizzymii.millenaire2.village.Building nearest = null;
        double nearestDist = maxDistance;
        org.dizzymii.millenaire2.util.Point playerPoint = new org.dizzymii.millenaire2.util.Point(player.blockPosition());

        for (org.dizzymii.millenaire2.village.Building b : mw.allBuildings()) {
            org.dizzymii.millenaire2.util.Point pos = b.getPos();
            if (!b.isTownhall || pos == null) {
                continue;
            }
            double dist = playerPoint.distanceTo(pos);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = b;
            }
        }

        return nearest;
    }

    private static String pickLocalizedText(java.util.Map<String, String> map, String fallback) {
        if (map == null || map.isEmpty()) {
            return fallback;
        }
        if (map.containsKey("en") && map.get("en") != null) {
            return map.get("en");
        }
        for (String value : map.values()) {
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return fallback;
    }

    private static void handleImportTableAction(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw == null) {
                return;
            }

            if (actionId == MillPacketIds.GUIACTION_IMPORTTABLE_IMPORTBUILDINGPLAN) {
                String planKey = r.hasRemaining() ? r.readString() : null;
                if (planKey != null && !planKey.isEmpty()) {
                    mw.setGlobalTag("importplan:" + planKey);
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Imported building plan: " + planKey));
                }
            } else if (actionId == MillPacketIds.GUIACTION_IMPORTTABLE_CHANGESETTINGS) {
                boolean enabled = r.hasRemaining() && r.readBoolean();
                if (enabled) {
                    mw.setGlobalTag("importtable:enabled");
                } else {
                    mw.clearGlobalTag("importtable:enabled");
                }
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§6[Millénaire]§r Import table settings updated."));
            } else if (actionId == MillPacketIds.GUIACTION_IMPORTTABLE_CREATEBUILDING) {
                String cultureKey = r.hasRemaining() ? r.readString() : "norman";
                org.dizzymii.millenaire2.culture.Culture culture =
                        org.dizzymii.millenaire2.culture.Culture.getCultureByName(cultureKey);
                if (culture == null) {
                    culture = org.dizzymii.millenaire2.culture.Culture.getCultureByName("norman");
                }
                if (culture == null) {
                    return;
                }

                net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) player.level();
                boolean generated = org.dizzymii.millenaire2.world.WorldGenVillage.generateNewVillage(
                        level,
                        player.blockPosition(),
                        culture,
                        mw,
                        level.random);

                if (generated) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§6[Millénaire]§r Import table created new village building set."));
                } else {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                            "§c[Millénaire] Import table failed to create building."));
                }
            }
        } catch (Exception e) {
            MillLog.error("ServerPacketHandler", "Error handling import table action", e);
        } finally {
            r.release();
        }
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
                executeBuy(player, profile, good, reputation, villagePos, mw, building);
            } else {
                executeSell(player, profile, good, reputation, villagePos, mw, building);
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
                                    org.dizzymii.millenaire2.world.MillWorldData mw,
                                    org.dizzymii.millenaire2.village.Building building) {
        if (good.buyPrice <= 0 || good.item.isEmpty()) return;

        // Use supply-adjusted price based on building stock
        int stock = building.getBuildingStock(good);
        int price = good.getSupplyAdjustedBuyPrice(reputation, stock);

        if (profile.deniers < price) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§c[Millénaire]§r Not enough deniers! Need " + price + ", have " + profile.deniers));
            return;
        }

        // Check building has stock (if inventory-tracked)
        if (stock >= 0 && stock < good.quantity) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§c[Millénaire]§r Out of stock!"));
            return;
        }

        // Create the item stack with the correct quantity
        net.minecraft.world.item.ItemStack giveStack = good.item.copy();
        giveStack.setCount(good.quantity);

        // Give item to player
        if (!player.getInventory().add(giveStack)) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§c[Millénaire]§r Inventory full!"));
            return;
        }

        // Deduct from building inventory
        building.executeBuyFromBuilding(good, good.quantity);

        profile.deniers -= price;
        // Reputation gain from buying
        if (villagePos != null) {
            profile.adjustVillageReputation(villagePos, 1);
        }
        mw.setDirty();

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "§6[Millénaire]§r Bought " + good.quantity + "x " + good.item.getHoverName().getString()
                        + " for " + price + " deniers"));
        MillLog.minor("ServerPacketHandler", "Trade buy: " + player.getName().getString()
                + " bought " + good.quantity + "x " + good.item.getHoverName().getString() + " for " + price + "d");
    }

    private static void executeSell(ServerPlayer player,
                                     org.dizzymii.millenaire2.world.UserProfile profile,
                                     org.dizzymii.millenaire2.item.TradeGood good,
                                     int reputation,
                                     @javax.annotation.Nullable org.dizzymii.millenaire2.util.Point villagePos,
                                     org.dizzymii.millenaire2.world.MillWorldData mw,
                                     org.dizzymii.millenaire2.village.Building building) {
        if (good.sellPrice <= 0 || good.item.isEmpty()) return;

        // Use supply-adjusted price based on building stock
        int stock = building.getBuildingStock(good);
        int price = good.getSupplyAdjustedSellPrice(reputation, stock);
        if (price <= 0) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§c[Millénaire]§r This item has no value here right now."));
            return;
        }

        // Check player has enough of the item
        int totalInInventory = countPlayerItem(player, good.item);
        if (totalInInventory < good.quantity) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§c[Millénaire]§r You don't have enough! Need " + good.quantity + "."));
            return;
        }

        // Remove quantity items from player inventory
        int remaining = good.quantity;
        for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
            net.minecraft.world.item.ItemStack slotStack = player.getInventory().getItem(i);
            if (net.minecraft.world.item.ItemStack.isSameItemSameComponents(slotStack, good.item) && !slotStack.isEmpty()) {
                int take = Math.min(remaining, slotStack.getCount());
                slotStack.shrink(take);
                if (slotStack.isEmpty()) {
                    player.getInventory().setItem(i, net.minecraft.world.item.ItemStack.EMPTY);
                }
                remaining -= take;
            }
        }

        // Add to building inventory
        building.executeSellToBuilding(good, good.quantity);

        profile.deniers += price;
        // Reputation gain from selling
        if (villagePos != null) {
            profile.adjustVillageReputation(villagePos, 1);
        }
        mw.setDirty();

        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "§6[Millénaire]§r Sold " + good.quantity + "x " + good.item.getHoverName().getString()
                        + " for " + price + " deniers"));
        MillLog.minor("ServerPacketHandler", "Trade sell: " + player.getName().getString()
                + " sold " + good.quantity + "x " + good.item.getHoverName().getString() + " for " + price + "d");
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

    private static int countPlayerItem(ServerPlayer player, net.minecraft.world.item.ItemStack target) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            net.minecraft.world.item.ItemStack stack = player.getInventory().getItem(i);
            if (net.minecraft.world.item.ItemStack.isSameItemSameComponents(stack, target) && !stack.isEmpty()) {
                count += stack.getCount();
            }
        }
        return count;
    }
}
