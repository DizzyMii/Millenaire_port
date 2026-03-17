/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.entity;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations;
import org.millenaire.common.annotedparameters.ParametersManager;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.virtualdir.VirtualDir;
import org.millenaire.common.village.Building;

public class VillagerConfig {
    private static final String DEFAULT = "default";
    public static Map<String, VillagerConfig> villagerConfigs = new HashMap<String, VillagerConfig>();
    public static VillagerConfig DEFAULT_CONFIG;
    public static String CATEGORY_WEAPONSHANDTOHAND;
    public static String CATEGORY_WEAPONSRANGED;
    public static String CATEGORY_ARMOURSHELMET;
    public static String CATEGORY_ARMOURSCHESTPLATE;
    public static String CATEGORY_ARMOURSLEGGINGS;
    public static String CATEGORY_ARMOURSBOOTS;
    public static String CATEGORY_TOOLSSWORD;
    public static String CATEGORY_TOOLSPICKAXE;
    public static String CATEGORY_TOOLSAXE;
    public static String CATEGORY_TOOLSHOE;
    public static String CATEGORY_TOOLSSHOVEL;
    public final String key;
    public Map<InvItem, Integer> weapons = new HashMap<InvItem, Integer>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="weaponHandToHandPriority")
    @ConfigAnnotations.FieldDocumentation(explanation="A hand to hand weapon and its use priority. If the priority is 0, won't get used.")
    public Map<InvItem, Integer> weaponsHandToHand = new HashMap<InvItem, Integer>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="weaponRangedPriority")
    @ConfigAnnotations.FieldDocumentation(explanation="A ranged weapon and its use priority. If the priority is 0, won't get used.")
    public Map<InvItem, Integer> weaponsRanged = new HashMap<InvItem, Integer>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="armourHelmetPriority")
    @ConfigAnnotations.FieldDocumentation(explanation="A helmet and its use priority. If the priority is 0, won't get used.")
    public Map<InvItem, Integer> armoursHelmet = new HashMap<InvItem, Integer>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="armourChestplatePriority")
    @ConfigAnnotations.FieldDocumentation(explanation="A chest plate and its use priority. If the priority is 0, won't get used.")
    public Map<InvItem, Integer> armoursChestplate = new HashMap<InvItem, Integer>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="armourLeggingsPriority")
    @ConfigAnnotations.FieldDocumentation(explanation="A leggings and its use priority. If the priority is 0, won't get used.")
    public Map<InvItem, Integer> armoursLeggings = new HashMap<InvItem, Integer>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="armourBootsPriority")
    @ConfigAnnotations.FieldDocumentation(explanation="A pair of boots and its use priority. If the priority is 0, won't get used.")
    public Map<InvItem, Integer> armoursBoots = new HashMap<InvItem, Integer>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="toolSwordPriority")
    @ConfigAnnotations.FieldDocumentation(explanation="A tool and its use priority. If the priority is 0, won't get used. Villagers will get a 'better' one if available.")
    public Map<InvItem, Integer> toolsSword = new HashMap<InvItem, Integer>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="toolPickaxePriority")
    @ConfigAnnotations.FieldDocumentation(explanation="A tool and its use priority. If the priority is 0, won't get used. Villagers will get a 'better' one if available.")
    public Map<InvItem, Integer> toolsPickaxe = new HashMap<InvItem, Integer>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="toolAxePriority")
    @ConfigAnnotations.FieldDocumentation(explanation="A tool and its use priority. If the priority is 0, won't get used. Villagers will get a 'better' one if available.")
    public Map<InvItem, Integer> toolsAxe = new HashMap<InvItem, Integer>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="toolHoePriority")
    @ConfigAnnotations.FieldDocumentation(explanation="A tool and its use priority. If the priority is 0, won't get used. Villagers will get a 'better' one if available.")
    public Map<InvItem, Integer> toolsHoe = new HashMap<InvItem, Integer>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="toolShovelPriority")
    @ConfigAnnotations.FieldDocumentation(explanation="A tool and its use priority. If the priority is 0, won't get used. Villagers will get a 'better' one if available.")
    public Map<InvItem, Integer> toolsShovel = new HashMap<InvItem, Integer>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="foodGrowthValue")
    @ConfigAnnotations.FieldDocumentation(explanation="A food a child can eat to grow and its growth value.")
    public Map<InvItem, Integer> foodsGrowth = new HashMap<InvItem, Integer>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.INVITEM_NUMBER_ADD, paramName="foodConceptionValue")
    @ConfigAnnotations.FieldDocumentation(explanation="A food an adult can eat to increase conception chances and the increase value.")
    public Map<InvItem, Integer> foodsConception = new HashMap<InvItem, Integer>();
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
    public Map<String, List<InvItem>> categories = new HashMap<String, List<InvItem>>();

    private static VillagerConfig copyDefault(String key) {
        VillagerConfig newConfig = new VillagerConfig(key);
        for (Field field : VillagerConfig.class.getFields()) {
            try {
                if (field.getType() != Map.class) continue;
                Map map = (Map)field.get(DEFAULT_CONFIG);
                field.set(newConfig, new HashMap(map));
            }
            catch (Exception e) {
                MillLog.printException("Exception when duplicating maps: " + field, e);
            }
        }
        return newConfig;
    }

    private static List<File> getVillagerConfigFiles() {
        VirtualDir virtualConfigDir = Mill.virtualLoadingDir.getChildDirectory("villagerconfig");
        return virtualConfigDir.listFilesRecursive(new MillCommonUtilities.ExtFileFilter("txt"));
    }

    public static void loadConfigs() {
        DEFAULT_CONFIG = new VillagerConfig(DEFAULT);
        ParametersManager.loadAnnotedParameterData(Mill.virtualLoadingDir.getChildDirectory("villagerconfig").getChildFile("default.txt"), DEFAULT_CONFIG, null, "villager config", null);
        DEFAULT_CONFIG.initData();
        for (File file : VillagerConfig.getVillagerConfigFiles()) {
            if (file.getName().equals("default.txt")) continue;
            String key = file.getName().split("\\.")[0].toLowerCase();
            VillagerConfig config = VillagerConfig.copyDefault(key);
            ParametersManager.loadAnnotedParameterData(file, config, null, "villager config", null);
            config.initData();
            villagerConfigs.put(key, config);
        }
    }

    public VillagerConfig(String key) {
        this.key = key;
    }

    public InvItem getBestAxe(MillVillager villager) {
        return this.getBestItem(this.toolsAxeSorted, villager);
    }

    public InvItem getBestConceptionFood(Building house) {
        return this.getBestItemInBuilding(this.foodsConceptionSorted, house);
    }

    public InvItem getBestHoe(MillVillager villager) {
        return this.getBestItem(this.toolsHoeSorted, villager);
    }

    private InvItem getBestItem(List<InvItem> sortedItems, MillVillager villager) {
        for (InvItem invItem : sortedItems) {
            if (villager.countInv(invItem.item) <= 0) continue;
            return invItem;
        }
        return null;
    }

    public InvItem getBestItemByCategoryName(String categoryName, MillVillager villager) {
        return this.getBestItem(this.categories.get(categoryName), villager);
    }

    private InvItem getBestItemInBuilding(List<InvItem> sortedItems, Building house) {
        for (InvItem invItem : sortedItems) {
            if (house.countGoods(invItem.item) <= 0) continue;
            return invItem;
        }
        return null;
    }

    public InvItem getBestPickaxe(MillVillager villager) {
        return this.getBestItem(this.toolsPickaxeSorted, villager);
    }

    public InvItem getBestShovel(MillVillager villager) {
        return this.getBestItem(this.toolsShovelSorted, villager);
    }

    public InvItem getBestSword(MillVillager villager) {
        return this.getBestItem(this.toolsSwordSorted, villager);
    }

    public InvItem getBestWeapon(MillVillager villager) {
        return this.getBestItem(this.weaponsSorted, villager);
    }

    public InvItem getBestWeaponHandToHand(MillVillager villager) {
        return this.getBestItem(this.weaponsHandToHandSorted, villager);
    }

    public InvItem getBestWeaponRanged(MillVillager villager) {
        return this.getBestItem(this.weaponsRangedSorted, villager);
    }

    private void initData() {
        this.weapons.putAll(this.weaponsHandToHand);
        this.weapons.putAll(this.weaponsRanged);
        for (Field field : VillagerConfig.class.getFields()) {
            try {
                ParameterizedType pt;
                if (field.getType() != Map.class || (pt = (ParameterizedType)field.getGenericType()).getActualTypeArguments()[0] != InvItem.class || pt.getActualTypeArguments()[1] != Integer.class) continue;
                Map map = (Map)field.get(this);
                HashSet keysCopy = new HashSet(map.keySet());
                for (InvItem item : keysCopy) {
                    if ((Integer)map.get(item) > 0) continue;
                    map.remove(item);
                }
                ArrayList sortedList = new ArrayList(map.keySet());
                Collections.sort(sortedList, (key1, key2) -> ((Comparable)map.get(key2)).compareTo(map.get(key1)));
                Field listField = VillagerConfig.class.getDeclaredField(field.getName() + "Sorted");
                listField.set(this, sortedList);
                this.categories.put(field.getName().toLowerCase(), sortedList);
            }
            catch (Exception e) {
                MillLog.printException("Exception when creating sorted list for field: " + field, e);
            }
        }
    }

    static {
        CATEGORY_WEAPONSHANDTOHAND = "weaponshandtohand";
        CATEGORY_WEAPONSRANGED = "weaponsranged";
        CATEGORY_ARMOURSHELMET = "armourshelmet";
        CATEGORY_ARMOURSCHESTPLATE = "armourschestplate";
        CATEGORY_ARMOURSLEGGINGS = "armoursleggings";
        CATEGORY_ARMOURSBOOTS = "armoursboots";
        CATEGORY_TOOLSSWORD = "toolssword";
        CATEGORY_TOOLSPICKAXE = "toolspickaxe";
        CATEGORY_TOOLSAXE = "toolsaxe";
        CATEGORY_TOOLSHOE = "toolshoe";
        CATEGORY_TOOLSSHOVEL = "toolsshovel";
    }
}

