package org.dizzymii.millenaire2.entity;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.dizzymii.millenaire2.item.InvItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Equipment and food configuration for villager types.
 * Ported from org.millenaire.common.entity.VillagerConfig (Forge 1.12.2).
 *
 * Loaded from culture config files; maps weapons, armor, tools, and food
 * to priority values for each villager config key.
 */
public class VillagerConfig {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String DEFAULT = "default";

    public static Map<String, VillagerConfig> villagerConfigs = new HashMap<>();
    public static VillagerConfig DEFAULT_CONFIG;

    public static final String CATEGORY_WEAPONSHANDTOHAND = "weaponsHandToHand";
    public static final String CATEGORY_WEAPONSRANGED = "weaponsRanged";
    public static final String CATEGORY_ARMOURSHELMET = "armoursHelmet";
    public static final String CATEGORY_ARMOURSCHESTPLATE = "armoursChestplate";
    public static final String CATEGORY_ARMOURSLEGGINGS = "armoursLeggings";
    public static final String CATEGORY_ARMOURSBOOTS = "armoursBoots";
    public static final String CATEGORY_TOOLSSWORD = "toolsSword";
    public static final String CATEGORY_TOOLSPICKAXE = "toolsPickaxe";
    public static final String CATEGORY_TOOLSAXE = "toolsAxe";
    public static final String CATEGORY_TOOLSHOE = "toolsHoe";
    public static final String CATEGORY_TOOLSSHOVEL = "toolsShovel";

    public final String key;

    // Equipment maps: item → priority
    public Map<InvItem, Integer> weapons = new HashMap<>();
    public Map<InvItem, Integer> weaponsHandToHand = new HashMap<>();
    public Map<InvItem, Integer> weaponsRanged = new HashMap<>();
    public Map<InvItem, Integer> armoursHelmet = new HashMap<>();
    public Map<InvItem, Integer> armoursChestplate = new HashMap<>();
    public Map<InvItem, Integer> armoursLeggings = new HashMap<>();
    public Map<InvItem, Integer> armoursBoots = new HashMap<>();
    public Map<InvItem, Integer> toolsSword = new HashMap<>();
    public Map<InvItem, Integer> toolsPickaxe = new HashMap<>();
    public Map<InvItem, Integer> toolsAxe = new HashMap<>();
    public Map<InvItem, Integer> toolsHoe = new HashMap<>();
    public Map<InvItem, Integer> toolsShovel = new HashMap<>();
    public Map<InvItem, Integer> foodsGrowth = new HashMap<>();
    public Map<InvItem, Integer> foodsConception = new HashMap<>();

    // Sorted lists (populated after loading)
    public List<InvItem> weaponsHandToHandSorted;
    public List<InvItem> weaponsRangedSorted;
    public List<InvItem> weaponsSorted;
    public List<InvItem> armoursHelmetSorted;
    public List<InvItem> armoursChestplateSorted;
    public List<InvItem> armoursLeggingsSorted;
    public List<InvItem> armoursBootsSorted;
    public List<InvItem> toolsSwordSorted;
    public List<InvItem> toolsPickaxeSorted;
    public List<InvItem> toolsAxeSorted;
    public List<InvItem> toolsHoeSorted;
    public List<InvItem> toolsShovelSorted;
    public List<InvItem> foodsGrowthSorted;
    public List<InvItem> foodsConceptionSorted;

    public Map<String, List<InvItem>> categories = new HashMap<>();

    public VillagerConfig(String key) {
        this.key = key;
    }

    /**
     * Get a config by key, creating a copy of default if not found.
     */
    public static VillagerConfig getOrCreate(String key) {
        if (villagerConfigs.containsKey(key)) {
            return villagerConfigs.get(key);
        }
        VillagerConfig config = new VillagerConfig(key);
        villagerConfigs.put(key, config);
        return config;
    }

    /**
     * Sort all equipment/food lists by priority (descending).
     */
    public void sortAll() {
        weaponsHandToHandSorted = sortByPriority(weaponsHandToHand);
        weaponsRangedSorted = sortByPriority(weaponsRanged);
        weaponsSorted = sortByPriority(weapons);
        armoursHelmetSorted = sortByPriority(armoursHelmet);
        armoursChestplateSorted = sortByPriority(armoursChestplate);
        armoursLeggingsSorted = sortByPriority(armoursLeggings);
        armoursBootsSorted = sortByPriority(armoursBoots);
        toolsSwordSorted = sortByPriority(toolsSword);
        toolsPickaxeSorted = sortByPriority(toolsPickaxe);
        toolsAxeSorted = sortByPriority(toolsAxe);
        toolsHoeSorted = sortByPriority(toolsHoe);
        toolsShovelSorted = sortByPriority(toolsShovel);
        foodsGrowthSorted = sortByPriority(foodsGrowth);
        foodsConceptionSorted = sortByPriority(foodsConception);

        categories.put(CATEGORY_WEAPONSHANDTOHAND, weaponsHandToHandSorted);
        categories.put(CATEGORY_WEAPONSRANGED, weaponsRangedSorted);
        categories.put(CATEGORY_ARMOURSHELMET, armoursHelmetSorted);
        categories.put(CATEGORY_ARMOURSCHESTPLATE, armoursChestplateSorted);
        categories.put(CATEGORY_ARMOURSLEGGINGS, armoursLeggingsSorted);
        categories.put(CATEGORY_ARMOURSBOOTS, armoursBootsSorted);
        categories.put(CATEGORY_TOOLSSWORD, toolsSwordSorted);
        categories.put(CATEGORY_TOOLSPICKAXE, toolsPickaxeSorted);
        categories.put(CATEGORY_TOOLSAXE, toolsAxeSorted);
        categories.put(CATEGORY_TOOLSHOE, toolsHoeSorted);
        categories.put(CATEGORY_TOOLSSHOVEL, toolsShovelSorted);
    }

    private static List<InvItem> sortByPriority(Map<InvItem, Integer> map) {
        List<InvItem> list = new ArrayList<>(map.keySet());
        list.sort((a, b) -> Integer.compare(map.getOrDefault(b, 0), map.getOrDefault(a, 0)));
        return Collections.unmodifiableList(list);
    }

    /**
     * Initialise default config. Called during mod setup.
     */
    public static void initDefaultConfig() {
        DEFAULT_CONFIG = new VillagerConfig(DEFAULT);
        villagerConfigs.put(DEFAULT, DEFAULT_CONFIG);
        LOGGER.info("Default villager config initialised.");
    }
}
