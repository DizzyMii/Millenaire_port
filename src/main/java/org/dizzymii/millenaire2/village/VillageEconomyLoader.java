package org.dizzymii.millenaire2.village;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dizzymii.millenaire2.item.InvItem;

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
 * Data-driven village economy configuration loader.
 * Reads from data/millenaire2/economy/village_economy.json.
 */
public class VillageEconomyLoader {
    private static final Logger LOGGER = LogUtils.getLogger();

    // Expansion thresholds
    public static int upgradeCheckIntervalTicks = 1200;
    public static int minBuildingsForWalls = 3;
    public static int minBuildingsForSecondary = 2;
    public static int maxConcurrentConstructions = 1;
    public static int constructionBlocksPerVillagerTick = 3;

    // Resource production per building type: key -> list of (InvItem key, count)
    private static final Map<String, List<ResourceEntry>> PRODUCTION = new HashMap<>();
    private static final Map<String, List<ResourceEntry>> CONSUMPTION = new HashMap<>();
    private static final Map<String, Integer> CAPACITY = new HashMap<>();
    private static int defaultCapacity = 64;

    private static boolean loaded = false;

    public record ResourceEntry(String itemKey, int count) {}

    public static void loadFromServer(@Nullable MinecraftServer server) {
        PRODUCTION.clear();
        CONSUMPTION.clear();
        CAPACITY.clear();
        loaded = false;

        if (server == null) return;

        try {
            ResourceManager rm = server.getResourceManager();
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("millenaire2", "economy/village_economy.json");
            Optional<Resource> opt = rm.getResource(loc);
            if (opt.isEmpty()) {
                LOGGER.warn("No village_economy.json found, using defaults");
                return;
            }

            try (InputStream is = opt.get().open();
                 InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                // Parse expansion thresholds
                if (root.has("expansion")) {
                    JsonObject exp = root.getAsJsonObject("expansion");
                    upgradeCheckIntervalTicks = getInt(exp, "upgrade_check_interval_ticks", 1200);
                    minBuildingsForWalls = getInt(exp, "min_buildings_for_walls", 3);
                    minBuildingsForSecondary = getInt(exp, "min_buildings_for_secondary", 2);
                    maxConcurrentConstructions = getInt(exp, "max_concurrent_constructions", 1);
                    constructionBlocksPerVillagerTick = getInt(exp, "construction_blocks_per_villager_tick", 3);
                }

                // Parse resource production
                if (root.has("resource_production")) {
                    JsonObject prod = root.getAsJsonObject("resource_production");
                    for (Map.Entry<String, JsonElement> entry : prod.entrySet()) {
                        String key = entry.getKey();
                        if (key.startsWith("_")) continue;
                        JsonObject bldg = entry.getValue().getAsJsonObject();
                        if (bldg.has("items")) {
                            PRODUCTION.put(key, parseResourceList(bldg.getAsJsonArray("items")));
                        }
                        if (bldg.has("consumes")) {
                            CONSUMPTION.put(key, parseResourceList(bldg.getAsJsonArray("consumes")));
                        }
                    }
                }

                // Parse capacity
                if (root.has("resource_capacity")) {
                    JsonObject cap = root.getAsJsonObject("resource_capacity");
                    for (Map.Entry<String, JsonElement> entry : cap.entrySet()) {
                        String key = entry.getKey();
                        if (key.startsWith("_")) continue;
                        if ("default".equals(key)) {
                            defaultCapacity = entry.getValue().getAsInt();
                        } else {
                            CAPACITY.put(key, entry.getValue().getAsInt());
                        }
                    }
                }

                loaded = true;
                LOGGER.debug("Loaded economy: "
                        + PRODUCTION.size() + " production rules, "
                        + CAPACITY.size() + " capacity overrides");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load village_economy.json", e);
        }
    }

    private static List<ResourceEntry> parseResourceList(JsonArray arr) {
        List<ResourceEntry> list = new ArrayList<>();
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            String itemKey = obj.get("item").getAsString();
            int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
            list.add(new ResourceEntry(itemKey, count));
        }
        return list;
    }

    private static int getInt(JsonObject obj, String key, int def) {
        return obj.has(key) ? obj.get(key).getAsInt() : def;
    }

    /**
     * Get production entries for a building plan set key.
     */
    public static List<ResourceEntry> getProduction(String planSetKey) {
        return PRODUCTION.getOrDefault(planSetKey, List.of());
    }

    /**
     * Get consumption entries for a building plan set key.
     */
    public static List<ResourceEntry> getConsumption(String planSetKey) {
        return CONSUMPTION.getOrDefault(planSetKey, List.of());
    }

    /**
     * Get max resource capacity for a building plan set key.
     */
    public static int getCapacity(String planSetKey) {
        return CAPACITY.getOrDefault(planSetKey, defaultCapacity);
    }

    /**
     * Apply production for a building, consuming inputs and storing outputs.
     * Returns true if any production occurred.
     */
    public static boolean tickProduction(Building building) {
        if (building.planSetKey == null) return false;

        List<ResourceEntry> produces = getProduction(building.planSetKey);
        List<ResourceEntry> consumes = getConsumption(building.planSetKey);
        int cap = getCapacity(building.planSetKey);

        // Check if we can consume required inputs
        for (ResourceEntry re : consumes) {
            InvItem item = InvItem.get(re.itemKey);
            if (item == null) return false;
            if (building.resManager.countGoods(item) < re.count) return false;
        }

        // Consume inputs
        for (ResourceEntry re : consumes) {
            InvItem item = InvItem.get(re.itemKey);
            if (item != null) building.resManager.takeGoods(item, re.count);
        }

        // Produce outputs (respecting capacity)
        boolean produced = false;
        for (ResourceEntry re : produces) {
            InvItem item = InvItem.get(re.itemKey);
            if (item != null) {
                int current = building.resManager.countGoods(item);
                if (current < cap) {
                    int toAdd = Math.min(re.count, cap - current);
                    building.resManager.storeGoods(item, toAdd);
                    produced = true;
                }
            }
        }
        return produced;
    }

    public static boolean isLoaded() {
        return loaded;
    }
}
