package org.dizzymii.millenaire2.item;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Data-driven trade goods loader.
 * Reads from data/millenaire2/trade/trade_goods.json.
 * Users and datapacks can override or extend trade offerings.
 */
public class TradeGoodLoader {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<String, List<TradeGood>> SELL_GOODS = new HashMap<>();
    private static final Map<String, List<TradeGood>> BUY_GOODS = new HashMap<>();
    private static List<TradeGood> DEFAULT_SELLS = new ArrayList<>();
    private static List<TradeGood> DEFAULT_BUYS = new ArrayList<>();
    private static boolean loaded = false;

    /**
     * Load trade goods from the server's resource manager.
     */
    public static void loadFromServer(@Nullable MinecraftServer server) {
        SELL_GOODS.clear();
        BUY_GOODS.clear();
        DEFAULT_SELLS.clear();
        DEFAULT_BUYS.clear();
        loaded = false;

        if (server == null) return;

        try {
            ResourceManager rm = server.getResourceManager();
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("millenaire2", "trade/trade_goods.json");
            Optional<Resource> opt = rm.getResource(loc);
            if (opt.isEmpty()) {
                LOGGER.warn("No trade_goods.json found");
                return;
            }

            try (InputStream is = opt.get().open();
                 InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                if (root.has("villager_trades")) {
                    JsonObject trades = root.getAsJsonObject("villager_trades");
                    for (Map.Entry<String, JsonElement> entry : trades.entrySet()) {
                        String vtypeKey = entry.getKey();
                        JsonObject vTrades = entry.getValue().getAsJsonObject();

                        if (vTrades.has("sells")) {
                            SELL_GOODS.put(vtypeKey, parseTradeList(vTrades.getAsJsonArray("sells"), true));
                        }
                        if (vTrades.has("buys")) {
                            BUY_GOODS.put(vtypeKey, parseTradeList(vTrades.getAsJsonArray("buys"), false));
                        }
                    }
                }

                if (root.has("default_trades")) {
                    JsonObject defaults = root.getAsJsonObject("default_trades");
                    if (defaults.has("sells")) {
                        DEFAULT_SELLS = parseTradeList(defaults.getAsJsonArray("sells"), true);
                    }
                    if (defaults.has("buys")) {
                        DEFAULT_BUYS = parseTradeList(defaults.getAsJsonArray("buys"), false);
                    }
                }

                loaded = true;
                LOGGER.debug("Loaded trade goods for " + SELL_GOODS.size() + " villager types");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load trade_goods.json", e);
        }
    }

    private static List<TradeGood> parseTradeList(JsonArray arr, boolean isSell) {
        List<TradeGood> list = new ArrayList<>();
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            String itemId = obj.get("item").getAsString();
            int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
            int price = obj.has("price") ? obj.get("price").getAsInt() : 1;

            ResourceLocation rl = ResourceLocation.parse(itemId);
            Item item = BuiltInRegistries.ITEM.get(rl);
            if (item == Items.AIR && !"minecraft:air".equals(itemId)) {
                LOGGER.warn("Unknown item: " + itemId);
                continue;
            }

            ItemStack stack = new ItemStack(item, count);
            TradeGood good;
            if (isSell) {
                good = new TradeGood(stack, price, 0);
            } else {
                good = new TradeGood(stack, 0, price);
            }
            list.add(good);
        }
        return list;
    }

    /**
     * Get all trade goods (both buy and sell) for a given villager type key.
     * Returns combined list suitable for the trade GUI.
     */
    public static List<TradeGood> getTradeGoods(String villagerTypeKey) {
        List<TradeGood> result = new ArrayList<>();

        List<TradeGood> sells = SELL_GOODS.getOrDefault(villagerTypeKey, DEFAULT_SELLS);
        List<TradeGood> buys = BUY_GOODS.getOrDefault(villagerTypeKey, DEFAULT_BUYS);

        result.addAll(sells);
        result.addAll(buys);
        return result;
    }

    /**
     * Get sell goods only for a villager type.
     */
    public static List<TradeGood> getSellGoods(String villagerTypeKey) {
        return SELL_GOODS.getOrDefault(villagerTypeKey, DEFAULT_SELLS);
    }

    /**
     * Get buy goods only for a villager type.
     */
    public static List<TradeGood> getBuyGoods(String villagerTypeKey) {
        return BUY_GOODS.getOrDefault(villagerTypeKey, DEFAULT_BUYS);
    }

    public static boolean isLoaded() {
        return loaded;
    }
}
