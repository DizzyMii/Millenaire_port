package org.dizzymii.millenaire2.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.network.handler.ClientQuestPacketHandler;
import org.dizzymii.millenaire2.network.handler.ClientTradePacketHandler;
import org.dizzymii.millenaire2.network.payloads.*;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Client-side handler for server-to-client packets.
 * Each public method handles a specific dedicated payload type.
 */
public final class ClientPacketHandler {

    // Client-side cache of village list entries (for GUI display)
    public static final List<VillageListClientEntry> villageListCache = new ArrayList<>();

    private ClientPacketHandler() {}

    // ========== Villager sync ==========

    public static void handleVillagerSync(VillagerSyncPayload p) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Entity entity = mc.level.getEntity(p.entityId());
        if (entity instanceof MillVillager villager) {
            villager.setVillagerId(p.villagerId());
            villager.setFirstName(p.firstName());
            villager.setFamilyName(p.familyName());
            villager.setGender(p.gender());
            villager.setCultureKey(p.cultureKey());
            villager.setVillagerTypeKey(p.vtypeKey());
            villager.goalKey = p.goalKey();
            villager.isRaider = p.isRaider();
            villager.aggressiveStance = p.aggressiveStance();
            villager.isUsingBow = p.usingBow();
            villager.isUsingHandToHand = p.usingHandToHand();
            villager.speech_key = p.speechKey();
            villager.speech_variant = p.speechVariant();
            villager.speech_started = p.speechStarted();
            villager.housePoint = p.housePoint();
            villager.townHallPoint = p.townHallPoint();
        }
    }

    // ========== Villager speech bubble ==========

    public static void handleVillagerSpeech(VillagerSpeechPayload p) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Entity entity = mc.level.getEntity(p.entityId());
        if (entity instanceof MillVillager villager) {
            villager.speech_key = p.speechKey();
            villager.speech_variant = p.variant();
            villager.speech_started = mc.level.getGameTime();
        }
    }

    // ========== Translated chat ==========

    public static void handleTranslatedChat(TranslatedChatPayload p) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            StringBuilder msg = new StringBuilder(p.translationKey());
            for (String arg : p.args()) {
                if (arg != null) msg.append(" ").append(arg);
            }
            mc.player.displayClientMessage(Component.literal(msg.toString()), false);
        }
    }

    // ========== Village list ==========

    public static void handleVillageList(VillageListPayload p) {
        villageListCache.clear();
        for (VillageListPayload.Entry e : p.entries()) {
            villageListCache.add(new VillageListClientEntry(
                    e.pos(), e.cultureKey(), e.name(), e.distance(), e.isLoneBuilding()));
        }
        MillLog.minor("ClientPacketHandler", "Received village list with " + p.entries().size() + " entries.");
    }

    // ========== Profile sync ==========

    public static void handleProfile(PlayerProfilePayload p) {
        MillLog.minor("ClientPacketHandler", "Received profile update type: " + p.updateType());
        // Profile data is logged; full client-side caching will use a mirror of UserProfile
    }

    // ========== Open GUI ==========

    public static void handleOpenGui(OpenGuiPayload p) {
        MillLog.minor("ClientPacketHandler", "Open GUI request: " + p.guiId() + " entity=" + p.entityId());
        org.dizzymii.millenaire2.client.ClientGuiHandler.openGui(p.guiId());
    }

    // ========== Building sync ==========

    public static void handleBuildingSync(BuildingSyncPayload p) {
        MillLog.minor("ClientPacketHandler", "Building sync: " + p.name() + " at " + p.pos()
                + " culture=" + p.cultureKey() + " townhall=" + p.isTownhall());
        // Client-side building cache will be updated when client village tracking is implemented
    }

    // ========== Locked chest ==========

    public static void handleLockedChest(LockedChestPayload p) {
        MillLog.minor("ClientPacketHandler", "Locked chest data for entity: " + p.chestEntityId());
        // Chest contents will be displayed in the locked chest GUI screen
    }

    // ========== Map info ==========

    public static void handleMapInfo(MapInfoPayload p) {
        MillLog.minor("ClientPacketHandler", "Map info received: " + p.villageCount() + " villages");
        // Village markers for minimap overlay will be cached here
    }

    // ========== Quest instance ==========

    public static void handleQuestInstance(QuestInstancePayload p) {
        ClientQuestPacketHandler.handle(p);
    }

    // ========== Trade data ==========

    public static void handleTradeData(TradeDataPayload p) {
        ClientTradePacketHandler.handle(p);
    }

    // ========== Client data classes ==========

    public static class VillageListClientEntry {
        @Nullable public final Point pos;
        @Nullable public final String cultureKey;
        @Nullable public final String name;
        public final int distance;
        public final boolean isLoneBuilding;

        public VillageListClientEntry(@Nullable Point pos, @Nullable String cultureKey,
                                      @Nullable String name, int distance, boolean isLoneBuilding) {
            this.pos = pos;
            this.cultureKey = cultureKey;
            this.name = name;
            this.distance = distance;
            this.isLoneBuilding = isLoneBuilding;
        }
    }
}
