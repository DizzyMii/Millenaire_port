package org.dizzymii.millenaire2.world;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import org.dizzymii.millenaire2.culture.Culture;

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
 * Data-driven biome-to-culture mapping.
 * Loads from data/millenaire2/worldgen/biome_culture_map.json.
 * Users and datapacks can override or extend the mapping.
 */
public class BiomeCultureMapper {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<String, List<WeightedCulture>> BIOME_MAP = new HashMap<>();
    private static final List<WeightedCulture> DEFAULT_CULTURES = new ArrayList<>();
    private static boolean loaded = false;

    private record WeightedCulture(String cultureKey, int weight) {}

    /**
     * Load biome-culture mappings from the server's resource manager.
     */
    public static void loadFromServer(@Nullable MinecraftServer server) {
        BIOME_MAP.clear();
        DEFAULT_CULTURES.clear();
        loaded = false;

        if (server == null) return;

        try {
            ResourceManager rm = server.getResourceManager();
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("millenaire2", "worldgen/biome_culture_map.json");
            Optional<Resource> opt = rm.getResource(loc);
            if (opt.isEmpty()) {
                LOGGER.warn("No biome_culture_map.json found, using random culture selection");
                return;
            }

            try (InputStream is = opt.get().open();
                 InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                if (root.has("biome_mappings")) {
                    JsonObject mappings = root.getAsJsonObject("biome_mappings");
                    for (Map.Entry<String, JsonElement> entry : mappings.entrySet()) {
                        String biomeId = entry.getKey();
                        List<WeightedCulture> list = parseWeightedList(entry.getValue().getAsJsonArray());
                        if (!list.isEmpty()) {
                            BIOME_MAP.put(biomeId, list);
                        }
                    }
                }

                if (root.has("default_cultures")) {
                    DEFAULT_CULTURES.addAll(parseWeightedList(root.getAsJsonArray("default_cultures")));
                }

                loaded = true;
                LOGGER.debug("Loaded " + BIOME_MAP.size()
                        + " biome mappings, " + DEFAULT_CULTURES.size() + " default cultures");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load biome_culture_map.json", e);
        }
    }

    private static List<WeightedCulture> parseWeightedList(JsonArray arr) {
        List<WeightedCulture> list = new ArrayList<>();
        for (JsonElement el : arr) {
            JsonObject obj = el.getAsJsonObject();
            String key = obj.get("culture").getAsString();
            int weight = obj.has("weight") ? obj.get("weight").getAsInt() : 1;
            list.add(new WeightedCulture(key, weight));
        }
        return list;
    }

    /**
     * Select a culture appropriate for the biome at the given position.
     * Falls back to random selection if no mapping exists.
     */
    @Nullable
    public static Culture selectCulture(ServerLevel level, BlockPos pos, RandomSource random) {
        if (Culture.LIST_CULTURES.isEmpty()) return null;

        // Get biome at position
        Holder<Biome> biomeHolder = level.getBiome(pos);
        String biomeId = biomeHolder.unwrapKey()
                .map(key -> key.location().toString())
                .orElse("");

        // Look up mapping
        List<WeightedCulture> candidates = BIOME_MAP.get(biomeId);
        if (candidates == null || candidates.isEmpty()) {
            candidates = DEFAULT_CULTURES;
        }

        // If still no candidates, fall back to all loaded cultures
        if (candidates == null || candidates.isEmpty()) {
            return Culture.LIST_CULTURES.get(random.nextInt(Culture.LIST_CULTURES.size()));
        }

        // Weighted random selection
        Culture selected = pickWeighted(candidates, random);
        if (selected != null) return selected;

        // Final fallback
        return Culture.LIST_CULTURES.get(random.nextInt(Culture.LIST_CULTURES.size()));
    }

    @Nullable
    private static Culture pickWeighted(List<WeightedCulture> candidates, RandomSource random) {
        int totalWeight = 0;
        for (WeightedCulture wc : candidates) {
            totalWeight += wc.weight;
        }
        if (totalWeight <= 0) return null;

        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        for (WeightedCulture wc : candidates) {
            cumulative += wc.weight;
            if (roll < cumulative) {
                return Culture.getCultureByName(wc.cultureKey);
            }
        }
        return null;
    }

    public static boolean isLoaded() {
        return loaded;
    }
}
