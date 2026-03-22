package org.dizzymii.millenaire2.network.handler;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.client.Minecraft;
import org.dizzymii.millenaire2.network.payloads.TradeDataPayload;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side cache and handling for trade data packets.
 */
public final class ClientTradePacketHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    // Client-side cache of trade data (populated by TradeDataPayload before GUI opens)
    public static final List<TradeGoodClientEntry> tradeGoodsCache = new ArrayList<>();
    public static int cachedDeniers = 0;
    public static int cachedReputation = 0;
    public static String cachedVillagerName = "";
    public static int cachedVillagerEntityId = -1;

    private ClientTradePacketHandler() {}

    public static void handle(TradeDataPayload p) {
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
        LOGGER.debug("Received trade data: " + goods.size() + " goods, " + cachedDeniers + " deniers");
    }

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
}
