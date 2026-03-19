package org.dizzymii.millenaire2.goal.generic;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.LegacyBlockMapping;
import org.dizzymii.millenaire2.util.MillCommonUtilities;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

final class GenericGoalDefinition {

    enum Family {
        VISIT,
        TAKE_FROM_BUILDING,
        CRAFTING,
        COOKING,
        TEND_FURNACE,
        GATHER_BLOCKS,
        HARVESTING,
        PLANTING,
        MINING,
        SLAUGHTER_ANIMAL,
        UNKNOWN;

        static Family fromDirectoryName(String directoryName) {
            return switch (Goal.normalizeKey(directoryName)) {
                case "genericvisit" -> VISIT;
                case "generictakefrombuilding" -> TAKE_FROM_BUILDING;
                case "genericcrafting" -> CRAFTING;
                case "genericcooking" -> COOKING;
                case "generictendfurnace" -> TEND_FURNACE;
                case "genericgatherblocks" -> GATHER_BLOCKS;
                case "genericharvesting" -> HARVESTING;
                case "genericplanting" -> PLANTING;
                case "genericmining" -> MINING;
                case "genericslaughteranimal" -> SLAUGHTER_ANIMAL;
                default -> UNKNOWN;
            };
        }
    }

    static final class ItemAmount {
        final String itemKey;
        final int count;

        ItemAmount(String itemKey, int count) {
            this.itemKey = Goal.normalizeKey(itemKey);
            this.count = Math.max(1, count);
        }

        @Nullable
        InvItem resolve() {
            return InvItem.get(itemKey);
        }
    }

    static final class ChanceItem {
        final String itemKey;
        final int chance;
        @Nullable final String requiredTag;

        ChanceItem(String itemKey, int chance, @Nullable String requiredTag) {
            this.itemKey = Goal.normalizeKey(itemKey);
            this.chance = Math.max(0, chance);
            this.requiredTag = requiredTag == null || requiredTag.isBlank() ? null : Goal.normalizeKey(requiredTag);
        }

        @Nullable
        InvItem resolve() {
            return InvItem.get(itemKey);
        }
    }

    static final class ItemPair {
        final String firstItemKey;
        final String secondItemKey;

        ItemPair(String firstItemKey, String secondItemKey) {
            this.firstItemKey = Goal.normalizeKey(firstItemKey);
            this.secondItemKey = Goal.normalizeKey(secondItemKey);
        }

        @Nullable
        InvItem first() {
            return InvItem.get(firstItemKey);
        }

        @Nullable
        InvItem second() {
            return InvItem.get(secondItemKey);
        }
    }

    final Family family;
    final String goalKey;
    String buildingTag = "";
    boolean townHallGoal = false;
    String targetPosition = "";
    int durationTicks = 10;
    boolean allowRandomMoves = false;
    boolean leisure = false;
    boolean sprint = true;
    int minimumHour = -1;
    int maximumHour = -1;
    int maxSimultaneousInBuilding = 0;
    int maxSimultaneousTotal = 0;
    int range = 3;
    String sentenceKey = "";
    String labelKey = "";
    String sound = "";
    String cropType = "";
    String soilType = "";
    String seedItemKey = "";
    String itemToCookKey = "";
    String animalKey = "";
    boolean collectInBuilding = false;
    int minimumPickup = 0;
    int minimumFuel = 0;
    int minimumToCook = 1;
    @Nullable ItemAmount collectGood;
    @Nullable ItemPair itemsBalance;
    @Nullable String gatherBlockState;
    @Nullable String resultingBlockState;
    @Nullable String sourceBlockState;
    final List<String> harvestBlockStates = new ArrayList<>();
    final List<String> plantBlockStates = new ArrayList<>();
    final List<String> heldItems = new ArrayList<>();
    final List<String> heldItemsOffHand = new ArrayList<>();
    final List<String> goalTags = new ArrayList<>();
    final List<String> requiredTags = new ArrayList<>();
    final List<ItemAmount> inputs = new ArrayList<>();
    final List<ItemAmount> outputs = new ArrayList<>();
    final List<ItemAmount> buildingLimits = new ArrayList<>();
    final List<ItemAmount> townHallLimits = new ArrayList<>();
    final List<ItemAmount> villageLimits = new ArrayList<>();
    final List<ItemAmount> lootItems = new ArrayList<>();
    final List<ChanceItem> harvestItems = new ArrayList<>();
    final List<ChanceItem> bonusItems = new ArrayList<>();

    private GenericGoalDefinition(Family family, String goalKey) {
        this.family = family;
        this.goalKey = goalKey;
    }

    static GenericGoalDefinition parse(File file, Family family, String goalKey) throws IOException {
        GenericGoalDefinition definition = new GenericGoalDefinition(family, goalKey);
        try (BufferedReader reader = MillCommonUtilities.getReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("//") || !trimmed.contains("=")) {
                    continue;
                }
                int separator = trimmed.indexOf('=');
                String key = Goal.normalizeKey(trimmed.substring(0, separator));
                String value = trimmed.substring(separator + 1).trim();
                switch (key) {
                    case "buildingtag" -> definition.buildingTag = Goal.normalizeKey(value);
                    case "townhallgoal" -> definition.townHallGoal = MillCommonUtilities.safeParseBoolean(value);
                    case "targetposition" -> definition.targetPosition = Goal.normalizeKey(value);
                    case "duration" -> definition.durationTicks = Math.max(10, MillCommonUtilities.safeParseInt(value, 500) / 50);
                    case "allowrandommoves" -> definition.allowRandomMoves = MillCommonUtilities.safeParseBoolean(value);
                    case "leasure" -> definition.leisure = MillCommonUtilities.safeParseBoolean(value);
                    case "sprint" -> definition.sprint = MillCommonUtilities.safeParseBoolean(value);
                    case "minimumhour" -> definition.minimumHour = MillCommonUtilities.safeParseInt(value, -1);
                    case "maximumhour" -> definition.maximumHour = MillCommonUtilities.safeParseInt(value, -1);
                    case "maxsimultaneousinbuilding" -> definition.maxSimultaneousInBuilding = Math.max(0, MillCommonUtilities.safeParseInt(value, 0));
                    case "maxsimultaneoustotal" -> definition.maxSimultaneousTotal = Math.max(0, MillCommonUtilities.safeParseInt(value, 0));
                    case "range" -> definition.range = Math.max(1, MillCommonUtilities.safeParseInt(value, 3));
                    case "sentencekey" -> definition.sentenceKey = Goal.normalizeKey(value);
                    case "labelkey" -> definition.labelKey = Goal.normalizeKey(value);
                    case "sound" -> definition.sound = Goal.normalizeKey(value);
                    case "helditems" -> definition.heldItems.addAll(parseItemList(value));
                    case "helditemsoffhand" -> definition.heldItemsOffHand.addAll(parseItemList(value));
                    case "input" -> definition.inputs.add(parseItemAmount(value));
                    case "output" -> definition.outputs.add(parseItemAmount(value));
                    case "buildinglimit" -> definition.buildingLimits.add(parseItemAmount(value));
                    case "townhalllimit" -> definition.townHallLimits.add(parseItemAmount(value));
                    case "villagelimit" -> definition.villageLimits.add(parseItemAmount(value));
                    case "collect_good" -> definition.collectGood = parseItemAmount(value);
                    case "minimumpickup" -> definition.minimumPickup = Math.max(0, MillCommonUtilities.safeParseInt(value, 0));
                    case "minimumfuel" -> definition.minimumFuel = Math.max(0, MillCommonUtilities.safeParseInt(value, 0));
                    case "minimumtocook" -> definition.minimumToCook = Math.max(1, MillCommonUtilities.safeParseInt(value, 1));
                    case "itemtocook" -> definition.itemToCookKey = Goal.normalizeKey(value);
                    case "itemsbalance" -> definition.itemsBalance = parseItemPair(value);
                    case "gatherblockstate" -> definition.gatherBlockState = value;
                    case "resultingblockstate" -> definition.resultingBlockState = value;
                    case "sourceblockstate" -> definition.sourceBlockState = value;
                    case "harvestblockstate" -> definition.harvestBlockStates.add(value);
                    case "plantblockstate" -> definition.plantBlockStates.add(value);
                    case "harvestitem" -> definition.harvestItems.add(parseChanceItem(value));
                    case "loot" -> definition.lootItems.add(parseItemAmount(value));
                    case "seed" -> definition.seedItemKey = Goal.normalizeKey(value);
                    case "croptype" -> definition.cropType = Goal.normalizeKey(value);
                    case "soiltype" -> definition.soilType = Goal.normalizeKey(value);
                    case "animalkey" -> definition.animalKey = Goal.normalizeKey(value);
                    case "bonusitem" -> definition.bonusItems.add(parseChanceItem(value));
                    case "requiredtag" -> definition.requiredTags.add(Goal.normalizeKey(value));
                    case "tag" -> definition.goalTags.add(Goal.normalizeKey(value));
                    case "collectinbuilding" -> definition.collectInBuilding = MillCommonUtilities.safeParseBoolean(value);
                }
            }
        }
        if (definition.sentenceKey.isEmpty()) {
            definition.sentenceKey = goalKey;
        }
        if (definition.labelKey.isEmpty()) {
            definition.labelKey = goalKey;
        }
        return definition;
    }

    @Nullable
    BlockState parseGatherBlockState() {
        return parseBlockState(gatherBlockState);
    }

    @Nullable
    BlockState parseResultingBlockState() {
        return parseBlockState(resultingBlockState);
    }

    @Nullable
    BlockState parseSourceBlockState() {
        return parseBlockState(sourceBlockState);
    }

    @Nullable
    BlockState parseFirstHarvestBlockState() {
        return harvestBlockStates.isEmpty() ? null : parseBlockState(harvestBlockStates.get(0));
    }

    List<BlockState> parseHarvestBlockStates() {
        List<BlockState> states = new ArrayList<>();
        for (String rawState : harvestBlockStates) {
            BlockState parsed = parseBlockState(rawState);
            if (parsed != null) {
                states.add(parsed);
            }
        }
        return states;
    }

    @Nullable
    BlockState parseFirstPlantBlockState() {
        return plantBlockStates.isEmpty() ? null : parseBlockState(plantBlockStates.get(0));
    }

    List<BlockState> parsePlantBlockStates() {
        List<BlockState> states = new ArrayList<>();
        for (String rawState : plantBlockStates) {
            BlockState parsed = parseBlockState(rawState);
            if (parsed != null) {
                states.add(parsed);
            }
        }
        return states;
    }

    private static List<String> parseItemList(String value) {
        List<String> items = new ArrayList<>();
        for (String token : value.split(",")) {
            String normalized = Goal.normalizeKey(token);
            if (!normalized.isEmpty()) {
                items.add(normalized);
            }
        }
        return items;
    }

    private static ItemAmount parseItemAmount(String value) {
        String[] tokens = value.split(",");
        String itemKey = tokens.length > 0 ? Goal.normalizeKey(tokens[0]) : "";
        int count = tokens.length > 1 ? MillCommonUtilities.safeParseInt(tokens[1], 1) : 1;
        return new ItemAmount(itemKey, count);
    }

    private static ChanceItem parseChanceItem(String value) {
        String[] tokens = value.split(",");
        String itemKey = tokens.length > 0 ? Goal.normalizeKey(tokens[0]) : "";
        int chance = tokens.length > 1 ? MillCommonUtilities.safeParseInt(tokens[1], 100) : 100;
        String requiredTag = tokens.length > 2 ? tokens[2] : null;
        return new ChanceItem(itemKey, chance, requiredTag);
    }

    private static ItemPair parseItemPair(String value) {
        String[] tokens = value.split(",");
        String first = tokens.length > 0 ? tokens[0] : "";
        String second = tokens.length > 1 ? tokens[1] : "";
        return new ItemPair(first, second);
    }

    @Nullable
    private static BlockState parseBlockState(@Nullable String rawSpec) {
        if (rawSpec == null || rawSpec.isBlank()) {
            return null;
        }
        String trimmed = rawSpec.trim();
        String baseSpec = trimmed;
        String propertySpec = "";
        int separator = trimmed.indexOf(';');
        if (separator >= 0) {
            baseSpec = trimmed.substring(0, separator).trim();
            propertySpec = trimmed.substring(separator + 1).trim();
        }
        String baseId = normalizeBlockId(baseSpec);
        if (!propertySpec.isEmpty() && !propertySpec.contains("=")) {
            baseId = LegacyBlockMapping.mapBlock(baseId + ";" + propertySpec);
            propertySpec = "";
        }
        LinkedHashMap<String, String> properties = parseProperties(propertySpec);
        baseId = remapLegacyVariant(baseId, properties);
        try {
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(baseId));
            BlockState state = block.defaultBlockState();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                state = applyProperty(state, entry.getKey(), entry.getValue());
            }
            return state;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String normalizeBlockId(String blockId) {
        String trimmed = blockId.trim().toLowerCase(Locale.ROOT);
        return trimmed.contains(":") ? trimmed : "minecraft:" + trimmed;
    }

    private static LinkedHashMap<String, String> parseProperties(String propertySpec) {
        LinkedHashMap<String, String> properties = new LinkedHashMap<>();
        if (propertySpec == null || propertySpec.isBlank()) {
            return properties;
        }
        for (String token : propertySpec.split(",")) {
            String trimmed = token.trim();
            int separator = trimmed.indexOf('=');
            if (separator < 0) {
                continue;
            }
            String key = Goal.normalizeKey(trimmed.substring(0, separator));
            String value = Goal.normalizeKey(trimmed.substring(separator + 1));
            if (!key.isEmpty() && !value.isEmpty()) {
                properties.put(key, value);
            }
        }
        return properties;
    }

    private static String remapLegacyVariant(String baseId, Map<String, String> properties) {
        String type = properties.get("type");
        if ("minecraft:red_flower".equals(baseId) && type != null) {
            properties.remove("type");
            return LegacyBlockMapping.mapBlock(switch (type) {
                case "poppy" -> "minecraft:red_flower;0";
                case "blue_orchid" -> "minecraft:red_flower;1";
                case "allium" -> "minecraft:red_flower;2";
                case "azure_bluet" -> "minecraft:red_flower;3";
                case "red_tulip" -> "minecraft:red_flower;4";
                case "orange_tulip" -> "minecraft:red_flower;5";
                case "white_tulip" -> "minecraft:red_flower;6";
                case "pink_tulip" -> "minecraft:red_flower;7";
                case "oxeye_daisy" -> "minecraft:red_flower;8";
                default -> "minecraft:red_flower";
            });
        }
        if ("minecraft:yellow_flower".equals(baseId)) {
            properties.remove("type");
            return LegacyBlockMapping.mapBlock("minecraft:yellow_flower");
        }
        String variant = properties.get("variant");
        if ("minecraft:stone".equals(baseId) && variant != null) {
            properties.remove("variant");
            return LegacyBlockMapping.mapBlock(switch (variant) {
                case "granite" -> "minecraft:stone;1";
                case "smooth_granite" -> "minecraft:stone;2";
                case "diorite" -> "minecraft:stone;3";
                case "smooth_diorite" -> "minecraft:stone;4";
                case "andesite" -> "minecraft:stone;5";
                case "smooth_andesite" -> "minecraft:stone;6";
                default -> "minecraft:stone";
            });
        }
        if ("minecraft:double_plant".equals(baseId) && variant != null) {
            properties.remove("variant");
            return LegacyBlockMapping.mapBlock(switch (variant) {
                case "sunflower" -> "minecraft:double_plant;0";
                case "double_syringa", "lilac" -> "minecraft:double_plant;1";
                case "double_grass" -> "minecraft:double_plant;2";
                case "double_fern" -> "minecraft:double_plant;3";
                case "double_rose", "rose_bush" -> "minecraft:double_plant;4";
                case "paeonia", "peony" -> "minecraft:double_plant;5";
                default -> "minecraft:double_plant";
            });
        }
        return LegacyBlockMapping.mapBlock(baseId);
    }

    private static BlockState applyProperty(BlockState state, String propertyName, String valueName) {
        for (Property<?> property : state.getProperties()) {
            if (property.getName().equals(propertyName)) {
                return applyPropertyValue(state, property, valueName);
            }
        }
        return state;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T extends Comparable<T>> BlockState applyPropertyValue(BlockState state, Property property, String valueName) {
        Optional<T> value = property.getValue(valueName);
        if (value.isPresent()) {
            return state.setValue(property, value.get());
        }
        return state;
    }
}
