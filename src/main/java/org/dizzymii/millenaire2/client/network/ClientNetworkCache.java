package org.dizzymii.millenaire2.client.network;

import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Client-side packet cache shared between packet handlers and client GUIs/books.
 */
public final class ClientNetworkCache {

    private ClientNetworkCache() {}

    public static final List<VillageListClientEntry> villageListCache = new ArrayList<>();

    public static final List<TradeGoodClientEntry> tradeGoodsCache = new ArrayList<>();
    public static int cachedDeniers = 0;
    public static int cachedReputation = 0;
    public static String cachedVillagerName = "";
    public static int cachedVillagerEntityId = -1;

    @Nullable public static QuestClientEntry cachedQuest = null;
    public static int cachedQuestVillagerEntityId = -1;

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
