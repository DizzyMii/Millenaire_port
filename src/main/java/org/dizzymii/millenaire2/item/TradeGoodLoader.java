package org.dizzymii.millenaire2.item;

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
import org.dizzymii.millenaire2.util.MillLog;

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

    private static final Map<String, List<TradeGood>> SELL_GOODS = new HashMap<>();
    private static final Map<String, List<TradeGood>> BUY_GOODS = new HashMap<>();
    private static final Map<String, List<TradeGood>> CULTURE_GOODS = new HashMap<>();
    private static List<TradeGood> DEFAULT_SELLS = new ArrayList<>();
    private static List<TradeGood> DEFAULT_BUYS = new ArrayList<>();
    private static boolean loaded = false;

    /**
     * Load trade goods from the server's resource manager.
     */
    public static void loadFromServer(@Nullable MinecraftServer server) {
        SELL_GOODS.clear();
        BUY_GOODS.clear();
        CULTURE_GOODS.clear();
        DEFAULT_SELLS.clear();
        DEFAULT_BUYS.clear();
        loaded = false;

        if (server == null) return;

        try {
            ResourceManager rm = server.getResourceManager();
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("millenaire2", "trade/trade_goods.json");
            Optional<Resource> opt = rm.getResource(loc);
            if (opt.isEmpty()) {
                MillLog.warn("TradeGoodLoader", "No trade_goods.json found");
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
                MillLog.minor("TradeGoodLoader", "Loaded trade goods for " + SELL_GOODS.size() + " villager types");
            }
        } catch (Exception e) {
            MillLog.error("TradeGoodLoader", "Failed to load trade_goods.json", e);
        }
    }

    private static List<TradeGood> parseTradeList(JsonArray arr, boolean isSell) {
        List<TradeGood> list = new ArrayList<>();
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            String itemId = obj.get("item").getAsString();
            int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
            int price = obj.has("price") ? obj.get("price").getAsInt() : 1;
            int quantity = obj.has("quantity") ? obj.get("quantity").getAsInt() : 1;
            String invItemKey = obj.has("invItemKey") ? obj.get("invItemKey").getAsString() : null;

            ResourceLocation rl = ResourceLocation.parse(itemId);
            Item item = BuiltInRegistries.ITEM.get(rl);
            if (item == Items.AIR && !"minecraft:air".equals(itemId)) {
                MillLog.warn("TradeGoodLoader", "Unknown item: " + itemId);
                continue;
            }

            ItemStack stack = new ItemStack(item, count);
            TradeGood good;
            if (isSell) {
                good = new TradeGood(stack, price, 0, quantity, invItemKey);
            } else {
                good = new TradeGood(stack, 0, price, quantity, invItemKey);
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

        // Datapack-level goods take priority
        List<TradeGood> sells = SELL_GOODS.getOrDefault(villagerTypeKey, DEFAULT_SELLS);
        List<TradeGood> buys = BUY_GOODS.getOrDefault(villagerTypeKey, DEFAULT_BUYS);
        result.addAll(sells);
        result.addAll(buys);

        // Culture-level goods supplement if no datapack overrides exist
        if (!SELL_GOODS.containsKey(villagerTypeKey) && !BUY_GOODS.containsKey(villagerTypeKey)) {
            List<TradeGood> cultureGoods = CULTURE_GOODS.get(villagerTypeKey);
            if (cultureGoods != null && !cultureGoods.isEmpty()) {
                result.addAll(cultureGoods);
            }
        }
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

    /**
     * Register trade goods from a culture for a specific villager type key.
     * Called during culture loading to populate culture-level shop goods.
     */
    public static void registerCultureGoods(String villagerTypeKey, List<TradeGood> goods) {
        if (goods == null || goods.isEmpty()) return;
        CULTURE_GOODS.computeIfAbsent(villagerTypeKey, k -> new ArrayList<>()).addAll(goods);
    }

    /**
     * Get culture-registered goods for a villager type.
     */
    public static List<TradeGood> getCultureGoods(String villagerTypeKey) {
        return CULTURE_GOODS.getOrDefault(villagerTypeKey, List.of());
    }

    public static boolean isLoaded() {
        return loaded;
    }
}
