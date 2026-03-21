package org.dizzymii.millenaire2.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.dizzymii.millenaire2.entity.MillVillager;
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

    // Client-side cache of trade data (populated by PACKET_SHOP before GUI opens)
    public static final List<TradeGoodClientEntry> tradeGoodsCache = new ArrayList<>();
    public static int cachedDeniers = 0;
    public static int cachedReputation = 0;
    public static String cachedVillagerName = "";
    public static int cachedVillagerEntityId = -1;

    // Client-side cache of quest data (populated by PACKET_QUESTINSTANCE)
    @Nullable public static QuestClientEntry cachedQuest = null;
    public static int cachedQuestVillagerEntityId = -1;

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
        cachedQuest = new QuestClientEntry(p.questKey(), p.stepIndex(), p.totalSteps(),
                p.stepDescription(), p.stepLabel(), p.rewardMoney(), p.rewardRep(), p.isOffer());
        cachedQuestVillagerEntityId = p.villagerEntityId();

        MillLog.minor("ClientPacketHandler", "Quest sync: " + p.questKey()
                + " step=" + p.stepIndex() + "/" + p.totalSteps() + " offer=" + p.isOffer());
    }

    // ========== Trade data ==========

    public static void handleTradeData(TradeDataPayload p) {
        cachedVillagerEntityId = p.villagerEntityId();
        cachedVillagerName = p.villagerName();
        cachedDeniers = p.deniers();
        cachedReputation = p.reputation();
        tradeGoodsCache.clear();
        List<TradeDataPayload.Entry> goods = p.goods();
        for (int i = 0; i < goods.size(); i++) {
            TradeDataPayload.Entry e = goods.get(i);
            tradeGoodsCache.add(new TradeGoodClientEntry(i, e.itemId(), e.itemCount(),
                    e.buyPrice(), e.sellPrice(), e.adjustedBuy(), e.adjustedSell()));
        }
        MillLog.minor("ClientPacketHandler", "Received trade data: " + goods.size() + " goods, " + cachedDeniers + " deniers");
    }

    // ========== Client data classes ==========

    public static class TradeGoodClientEntry {
        public final int index;
        public final String itemId;
        public final int itemCount;
        public final int buyPrice;
        public final int sellPrice;
        public final int adjustedBuy;
        public final int adjustedSell;

        public TradeGoodClientEntry(int index, String itemId, int itemCount,
                                     int buyPrice, int sellPrice, int adjBuy, int adjSell) {
            this.index = index;
            this.itemId = itemId;
            this.itemCount = itemCount;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
            this.adjustedBuy = adjBuy;
            this.adjustedSell = adjSell;
        }
    }

    public static class QuestClientEntry {
        public final String questKey;
        public final int stepIndex;
        public final int totalSteps;
        public final String stepDescription;
        public final String stepLabel;
        public final int rewardMoney;
        public final int rewardReputation;
        public final boolean isOffer;

        public QuestClientEntry(String questKey, int stepIndex, int totalSteps,
                                 String stepDescription, String stepLabel,
                                 int rewardMoney, int rewardReputation, boolean isOffer) {
            this.questKey = questKey;
            this.stepIndex = stepIndex;
            this.totalSteps = totalSteps;
            this.stepDescription = stepDescription;
            this.stepLabel = stepLabel;
            this.rewardMoney = rewardMoney;
            this.rewardReputation = rewardReputation;
            this.isOffer = isOffer;
        }
    }

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
