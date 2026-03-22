package org.dizzymii.millenaire2;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MillConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // --- World Generation ---
    private static final ModConfigSpec.BooleanValue GENERATE_VILLAGES = BUILDER
            .comment("Whether to generate Millénaire villages in new chunks")
            .define("worldgen.generateVillages", true);

    private static final ModConfigSpec.BooleanValue STOP_DEFAULT_VILLAGES = BUILDER
            .comment("Whether to stop vanilla village generation")
            .define("worldgen.stopDefaultVillages", false);

    private static final ModConfigSpec.IntValue VILLAGE_MIN_DISTANCE = BUILDER
            .comment("Minimum distance between Millénaire villages (in blocks)")
            .defineInRange("worldgen.villageMinDistance", 250, 50, 10000);

    private static final ModConfigSpec.IntValue VILLAGE_MAX_DISTANCE = BUILDER
            .comment("Maximum distance for new village generation (in blocks)")
            .defineInRange("worldgen.villageMaxDistance", 350, 100, 10000);

    // --- UI ---
    private static final ModConfigSpec.BooleanValue DISPLAY_VILLAGER_NAMES = BUILDER
            .comment("Display names, occupations and current task above villagers' heads")
            .define("ui.displayVillagerNames", true);

    private static final ModConfigSpec.IntValue VILLAGER_NAMES_DISTANCE = BUILDER
            .comment("Distance from which villager names are visible")
            .defineInRange("ui.villagerNamesDistance", 20, 1, 100);

    private static final ModConfigSpec.BooleanValue LANGUAGE_LEARNING = BUILDER
            .comment("Whether languages need to be learned through interaction with cultures")
            .define("ui.languageLearning", true);

    private static final ModConfigSpec.BooleanValue DISPLAY_START_TEXT = BUILDER
            .comment("Display the Millénaire version and Creation Quest status at startup")
            .define("ui.displayStartText", true);

    // --- Logging ---
    private static final ModConfigSpec.IntValue LOG_GENERAL = BUILDER
            .comment("General logging level (0=none, 1=major, 2=verbose)")
            .defineInRange("logging.general", 1, 0, 2);

    private static final ModConfigSpec.IntValue LOG_WORLD_GEN = BUILDER
            .comment("World generation logging level")
            .defineInRange("logging.worldGeneration", 1, 0, 2);

    private static final ModConfigSpec.IntValue LOG_CONSTRUCTION = BUILDER
            .comment("Construction logging level")
            .defineInRange("logging.construction", 0, 0, 2);

    private static final ModConfigSpec.IntValue LOG_VILLAGER = BUILDER
            .comment("Villager AI logging level")
            .defineInRange("logging.villager", 0, 0, 2);

    // --- Gameplay ---
    private static final ModConfigSpec.BooleanValue RAIDS_ENABLED = BUILDER
            .comment("Whether village raids are enabled")
            .define("gameplay.raidsEnabled", true);

    private static final ModConfigSpec.BooleanValue USE_CUSTOM_PATHING = BUILDER
            .comment("Use custom A*/JPS pathfinding instead of vanilla navigation. Experimental — leave false for stability.")
            .define("gameplay.useCustomPathing", false);

    private static final ModConfigSpec.IntValue CONSTRUCTION_BLOCKS_PER_TICK = BUILDER
            .comment("Number of blocks placed per slow tick during construction")
            .defineInRange("gameplay.constructionBlocksPerTick", 5, 1, 50);

    private static final ModConfigSpec.BooleanValue GENERATE_HELP_DATA = BUILDER
            .comment("Generate help data files (dev option)")
            .define("dev.generateHelpData", false);

    private static final ModConfigSpec.BooleanValue DEV_MODE = BUILDER
            .comment("Enable developer mode features")
            .define("dev.devMode", false);

    static final ModConfigSpec SPEC = BUILDER.build();

    // --- Accessors (read directly from spec; always current, no mutable cache) ---
    public static boolean generateVillages() { return GENERATE_VILLAGES.get(); }
    public static boolean stopDefaultVillages() { return STOP_DEFAULT_VILLAGES.get(); }
    public static int villageMinDistance() { return VILLAGE_MIN_DISTANCE.get(); }
    public static int villageMaxDistance() { return VILLAGE_MAX_DISTANCE.get(); }
    public static boolean displayVillagerNames() { return DISPLAY_VILLAGER_NAMES.get(); }
    public static int villagerNamesDistance() { return VILLAGER_NAMES_DISTANCE.get(); }
    public static boolean languageLearning() { return LANGUAGE_LEARNING.get(); }
    public static boolean displayStartText() { return DISPLAY_START_TEXT.get(); }
    public static int logGeneral() { return LOG_GENERAL.get(); }
    public static int logWorldGeneration() { return LOG_WORLD_GEN.get(); }
    public static int logConstruction() { return LOG_CONSTRUCTION.get(); }
    public static int logVillager() { return LOG_VILLAGER.get(); }
    public static boolean raidsEnabled() { return RAIDS_ENABLED.get(); }
    public static boolean useCustomPathing() { return USE_CUSTOM_PATHING.get(); }
    public static int constructionBlocksPerTick() { return CONSTRUCTION_BLOCKS_PER_TICK.get(); }
    public static boolean generateHelpData() { return GENERATE_HELP_DATA.get(); }
    public static boolean devMode() { return DEV_MODE.get(); }
}
