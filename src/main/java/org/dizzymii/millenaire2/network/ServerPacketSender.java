package org.dizzymii.millenaire2.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.network.payloads.*;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.world.UserProfile;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Server-side utility for building and sending S2C packets.
 * Constructs dedicated payload records and sends them via PacketDistributor.
 */
public final class ServerPacketSender {

    private ServerPacketSender() {}

    // ========== Villager sync ==========

    /**
     * Send a villager data sync packet to a specific player.
     * Contains identity, position, culture, goal, and appearance data.
     */
    public static void sendVillagerSync(ServerPlayer target, MillVillager villager) {
        VillagerSyncPayload payload = buildVillagerSyncPayload(villager);
        PacketDistributor.sendToPlayer(target, payload);
    }

    /**
     * Build a VillagerSyncPayload from a villager (also used in tests).
     */
    public static VillagerSyncPayload buildVillagerSyncPayload(MillVillager villager) {
        return new VillagerSyncPayload(
                villager.getId(), villager.getVillagerId(),
                villager.getFirstName(), villager.getFamilyName(), villager.getGender(),
                villager.getCultureKey(), villager.vtypeKey, villager.goalKey,
                villager.isRaider, villager.aggressiveStance,
                (float) villager.getX(), (float) villager.getY(), (float) villager.getZ(),
                villager.isUsingBow, villager.isUsingHandToHand,
                villager.speech_key, villager.speech_variant, villager.speech_started,
                villager.housePoint, villager.townHallPoint
        );
    }

    /**
     * Send a villager speech bubble to a specific player.
     */
    public static void sendVillagerSentence(ServerPlayer target, int entityId,
                                             String speechKey, int variant, String cultureKey) {
        PacketDistributor.sendToPlayer(target,
                new VillagerSpeechPayload(entityId, speechKey, variant, cultureKey));
    }

    // ========== Translated chat ==========

    /**
     * Send a translated chat message to a player.
     */
    public static void sendTranslatedChat(ServerPlayer target, String translationKey,
                                           String cultureKey, String... args) {
        PacketDistributor.sendToPlayer(target,
                new TranslatedChatPayload(translationKey, cultureKey, args));
    }

    // ========== Village list ==========

    /**
     * Send the list of known villages to a player.
     * Each entry: position, culture key, village name, distance.
     */
    public static void sendVillageList(ServerPlayer target, List<VillageListEntry> entries) {
        List<VillageListPayload.Entry> payloadEntries = new ArrayList<>(entries.size());
        for (VillageListEntry entry : entries) {
            payloadEntries.add(new VillageListPayload.Entry(
                    entry.pos, entry.cultureKey, entry.name, entry.distance, entry.isLoneBuilding));
        }
        PacketDistributor.sendToPlayer(target, new VillageListPayload(payloadEntries));
    }

    // ========== Profile sync ==========

    /**
     * Send player profile (reputation, language levels, unlocked content) to a player.
     */
    public static void sendProfile(ServerPlayer target, UserProfile profile, int updateType) {
        PacketDataHelper.Writer w = new PacketDataHelper.Writer();

        switch (updateType) {
            case UserProfile.UPDATE_ALL -> {
                writeReputations(w, profile);
                writeLanguages(w, profile);
            }
            case UserProfile.UPDATE_REPUTATION -> writeReputations(w, profile);
            case UserProfile.UPDATE_LANGUAGE -> writeLanguages(w, profile);
            default -> {
                // Minimal update — just the type marker is enough
            }
        }

        PacketDistributor.sendToPlayer(target,
                new PlayerProfilePayload(updateType, w.toByteArray()));
    }

    // ========== Open GUI ==========

    /**
     * Tell a client to open a specific GUI.
     */
    public static void sendOpenGui(ServerPlayer target, int guiId, int entityId, @Nullable Point villagePos) {
        PacketDistributor.sendToPlayer(target,
                new OpenGuiPayload(guiId, entityId, villagePos));
    }

    // ========== Trade data ==========

    /**
     * Send trade goods list and player deniers to a player opening the trade GUI.
     */
    public static void sendTradeData(ServerPlayer target, int villagerEntityId,
                                      java.util.List<org.dizzymii.millenaire2.item.TradeGood> goods,
                                      int deniers, int reputation, String villagerName) {
        List<TradeDataPayload.Entry> entries = new ArrayList<>(goods.size());
        for (org.dizzymii.millenaire2.item.TradeGood good : goods) {
            entries.add(new TradeDataPayload.Entry(
                    good.item.isEmpty() ? "" : net.minecraft.core.registries.BuiltInRegistries.ITEM
                            .getKey(good.item.getItem()).toString(),
                    good.item.getCount(),
                    good.buyPrice, good.sellPrice,
                    good.getAdjustedBuyPrice(reputation), good.getAdjustedSellPrice(reputation)));
        }
        PacketDistributor.sendToPlayer(target,
                new TradeDataPayload(villagerEntityId, villagerName, deniers, reputation, entries));
    }

    // ========== Internal helpers ==========

    private static void writeReputations(PacketDataHelper.Writer w, UserProfile profile) {
        // Culture reputations — write count then key/value pairs
        // Profile doesn't expose its maps directly, so we write what's accessible
        // For now write an empty count — will be filled when profile maps are exposed
        w.writeInt(0);
    }

    private static void writeLanguages(PacketDataHelper.Writer w, UserProfile profile) {
        w.writeInt(0);
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
