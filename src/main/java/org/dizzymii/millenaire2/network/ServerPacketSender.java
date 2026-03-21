package org.dizzymii.millenaire2.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.quest.QuestInstance;
import org.dizzymii.millenaire2.quest.QuestStep;
import org.dizzymii.millenaire2.network.payloads.MillGenericS2CPayload;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.world.UserProfile;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Server-side utility for building and sending S2C packets.
 * Encodes data structures into byte arrays via PacketDataHelper.Writer
 * and wraps them in MillGenericS2CPayload for transmission.
 */
public final class ServerPacketSender {

    private ServerPacketSender() {}

    // ========== Villager sync ==========

    /**
     * Send a villager data sync packet to a specific player.
     * Contains identity, position, culture, goal, and appearance data.
     */
    public static void sendVillagerSync(ServerPlayer target, MillVillager villager) {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeInt(villager.getId());
        w.writeLong(villager.getVillagerId());
        w.writeString(villager.getFirstName());
        w.writeString(villager.getFamilyName());
        w.writeInt(villager.getGender());
        w.writeString(villager.getCultureKey());
        w.writeString(villager.vtypeKey);
        w.writeString(villager.goalKey);
        w.writeBoolean(villager.isRaider);
        w.writeBoolean(villager.aggressiveStance);
        w.writeFloat((float) villager.getX());
        w.writeFloat((float) villager.getY());
        w.writeFloat((float) villager.getZ());

        // Held item info
        w.writeBoolean(villager.isUsingBow);
        w.writeBoolean(villager.isUsingHandToHand);

        // Speech data
        w.writeString(villager.speech_key);
        w.writeInt(villager.speech_variant);
        w.writeLong(villager.speech_started);

        // House/town hall references
        writePoint(w, villager.housePoint);
        writePoint(w, villager.townHallPoint);

        MillGenericS2CPayload payload = new MillGenericS2CPayload(
                MillPacketIds.PACKET_VILLAGER, 0, w.toByteArray());
        PacketDistributor.sendToPlayer(target, payload);
    }

    public static void sendQuestInstance(ServerPlayer target, QuestInstance qi, int villagerEntityId, boolean isOffer) {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        QuestStep step = qi.getCurrentStep();
        org.dizzymii.millenaire2.quest.Quest quest = qi.quest;
        String questKey = quest != null && quest.key != null ? quest.key : "";
        int totalSteps = quest != null ? quest.steps.size() : 0;

        w.writeString(questKey);
        w.writeInt(qi.currentStep);
        w.writeInt(totalSteps);
        w.writeString(step != null ? step.getDescription("en") : "");
        w.writeString(step != null ? step.getLabel("en") : "");
        w.writeInt(step != null ? step.rewardMoney : 0);
        w.writeInt(step != null ? step.rewardReputation : 0);
        w.writeInt(villagerEntityId);
        w.writeBoolean(isOffer);

        MillGenericS2CPayload payload = new MillGenericS2CPayload(
                MillPacketIds.PACKET_QUESTINSTANCE, 0, w.toByteArray());
        PacketDistributor.sendToPlayer(target, payload);
    }

    public static void sendQuestInstanceDestroy(ServerPlayer target, String questKey) {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeString(questKey);
        MillGenericS2CPayload payload = new MillGenericS2CPayload(
                MillPacketIds.PACKET_QUESTINSTANCE_DESTROY, 0, w.toByteArray());
        PacketDistributor.sendToPlayer(target, payload);
    }

    /**
     * Send a villager speech bubble to a specific player.
     */
    public static void sendVillagerSentence(ServerPlayer target, int entityId,
                                             String speechKey, int variant, String cultureKey) {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeInt(entityId);
        w.writeString(speechKey);
        w.writeInt(variant);
        w.writeString(cultureKey);

        MillGenericS2CPayload payload = new MillGenericS2CPayload(
                MillPacketIds.PACKET_VILLAGER_SENTENCE, 0, w.toByteArray());
        PacketDistributor.sendToPlayer(target, payload);
    }

    // ========== Translated chat ==========

    /**
     * Send a translated chat message to a player.
     */
    public static void sendTranslatedChat(ServerPlayer target, String translationKey,
                                           String cultureKey, String... args) {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeString(translationKey);
        w.writeString(cultureKey);
        w.writeInt(args.length);
        for (String arg : args) {
            w.writeString(arg);
        }

        MillGenericS2CPayload payload = new MillGenericS2CPayload(
                MillPacketIds.PACKET_TRANSLATED_CHAT, 0, w.toByteArray());
        PacketDistributor.sendToPlayer(target, payload);
    }

    // ========== Village list ==========

    /**
     * Send the list of known villages to a player.
     * Each entry: position, culture key, village name, distance.
     */
    public static void sendVillageList(ServerPlayer target, List<VillageListEntry> entries) {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeInt(entries.size());
        for (VillageListEntry entry : entries) {
            writePoint(w, entry.pos);
            w.writeString(entry.cultureKey);
            w.writeString(entry.name);
            w.writeInt(entry.distance);
            w.writeBoolean(entry.isLoneBuilding);
        }

        MillGenericS2CPayload payload = new MillGenericS2CPayload(
                MillPacketIds.PACKET_VILLAGELIST, 0, w.toByteArray());
        PacketDistributor.sendToPlayer(target, payload);
    }

    // ========== Profile sync ==========

    /**
     * Send player profile (reputation, language levels, unlocked content) to a player.
     */
    public static void sendProfile(ServerPlayer target, UserProfile profile, int updateType) {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeInt(updateType);

        switch (updateType) {
            case UserProfile.UPDATE_ALL -> {
                // Send all reputation data
                writeReputations(w, profile);
                writeLanguages(w, profile);
            }
            case UserProfile.UPDATE_REPUTATION -> writeReputations(w, profile);
            case UserProfile.UPDATE_LANGUAGE -> writeLanguages(w, profile);
            default -> {
                // Minimal update — just the type marker is enough
            }
        }

        MillGenericS2CPayload payload = new MillGenericS2CPayload(
                MillPacketIds.PACKET_PROFILE, updateType, w.toByteArray());
        PacketDistributor.sendToPlayer(target, payload);
    }

    // ========== Open GUI ==========

    /**
     * Tell a client to open a specific GUI.
     */
    public static void sendOpenGui(ServerPlayer target, int guiId, int entityId, @Nullable Point villagePos) {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeInt(guiId);
        w.writeInt(entityId);
        writePoint(w, villagePos);

        MillGenericS2CPayload payload = new MillGenericS2CPayload(
                MillPacketIds.PACKET_OPENGUI, guiId, w.toByteArray());
        PacketDistributor.sendToPlayer(target, payload);
    }

    // ========== Trade data ==========

    /**
     * Send trade goods list and player deniers to a player opening the trade GUI.
     */
    public static void sendTradeData(ServerPlayer target, int villagerEntityId,
                                      java.util.List<org.dizzymii.millenaire2.item.TradeGood> goods,
                                      int deniers, int reputation, String villagerName) {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();
        w.writeInt(villagerEntityId);
        w.writeString(villagerName);
        w.writeInt(deniers);
        w.writeInt(reputation);
        w.writeInt(goods.size());
        for (org.dizzymii.millenaire2.item.TradeGood good : goods) {
            w.writeString(good.item.isEmpty() ? "" : net.minecraft.core.registries.BuiltInRegistries.ITEM
                    .getKey(good.item.getItem()).toString());
            w.writeInt(good.item.getCount());
            w.writeInt(good.buyPrice);
            w.writeInt(good.sellPrice);
            w.writeInt(good.getAdjustedBuyPrice(reputation));
            w.writeInt(good.getAdjustedSellPrice(reputation));
        }

        MillGenericS2CPayload payload = new MillGenericS2CPayload(
                MillPacketIds.PACKET_SHOP, 0, w.toByteArray());
        PacketDistributor.sendToPlayer(target, payload);
    }

    // ========== Internal helpers ==========

    private static void writePoint(PacketDataHelper.Writer w, @Nullable Point p) {
        if (p != null) {
            w.writeBoolean(true);
            w.writeInt(p.x);
            w.writeInt(p.y);
            w.writeInt(p.z);
        } else {
            w.writeBoolean(false);
        }
    }

    private static void writeReputations(PacketDataHelper.Writer w, UserProfile profile) {
        java.util.Map<Point, Integer> villages = profile.getVillageReputations();
        java.util.Map<String, Integer> cultures = profile.getCultureReputations();

        w.writeInt(villages.size());
        for (java.util.Map.Entry<Point, Integer> entry : villages.entrySet()) {
            Point p = entry.getKey();
            w.writeInt(p.x);
            w.writeInt(p.y);
            w.writeInt(p.z);
            w.writeInt(entry.getValue());
        }

        w.writeInt(cultures.size());
        for (java.util.Map.Entry<String, Integer> entry : cultures.entrySet()) {
            w.writeString(entry.getKey());
            w.writeInt(entry.getValue());
        }
    }

    private static void writeLanguages(PacketDataHelper.Writer w, UserProfile profile) {
        java.util.Map<String, Integer> langs = profile.getCultureLanguages();
        w.writeInt(langs.size());
        for (java.util.Map.Entry<String, Integer> entry : langs.entrySet()) {
            w.writeString(entry.getKey());
            w.writeInt(entry.getValue());
        }
    }

    // ========== Data classes ==========

    /**
     * Entry for the village list packet.
     */
    public static class VillageListEntry {
        public final Point pos;
        public final String cultureKey;
        public final String name;
        public final int distance;
        public final boolean isLoneBuilding;

        public VillageListEntry(Point pos, String cultureKey, String name, int distance, boolean isLoneBuilding) {
            this.pos = pos;
            this.cultureKey = cultureKey;
            this.name = name;
            this.distance = distance;
            this.isLoneBuilding = isLoneBuilding;
        }
    }
}
