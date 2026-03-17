package org.dizzymii.millenaire2.config;

/**
 * All mod configuration values — distances, logging levels, feature toggles.
 * Ported from org.millenaire.common.config.MillConfigValues (Forge 1.12.2).
 *
 * These are static defaults. Future phases will wire them to NeoForge's config system.
 */
public class MillConfigValues {

    // --- Radius settings ---
    public static int KeepActiveRadius = 200;
    public static int BackgroundRadius = 2000;
    public static int BanditRaidRadius = 1500;
    public static int VillageRadius = 80;

    // --- Logging levels (0 = off, higher = more verbose) ---
    public static int LogBuildingPlan = 0;
    public static int LogCattleFarmer = 0;
    public static int LogChildren = 0;
    public static int LogTranslation = 0;
    public static int LogConnections = 0;
    public static int LogCulture = 0;
    public static int LogDiplomacy = 0;
    public static int LogGeneralAI = 0;
    public static int LogGetPath = 0;
    public static int LogHybernation = 0;
    public static int LogLumberman = 0;
    public static int LogMerchant = 0;
    public static int LogMiner = 0;
    public static int LogOther = 0;
    public static int LogPathing = 0;
    public static int LogSelling = 0;
    public static int LogTileEntityBuilding = 0;
    public static int LogVillage = 0;
    public static int LogVillager = 0;
    public static int LogQuest = 0;
    public static int LogWifeAI = 0;
    public static int LogWorldGeneration = 0;
    public static int LogWorldInfo = 0;
    public static int LogPujas = 0;
    public static int LogVillagerSpawn = 0;
    public static int LogVillagePaths = 0;
    public static int LogChunkLoader = 0;
    public static int LogTags = 0;
    public static int LogNetwork = 0;

    // --- Feature toggles ---
    public static boolean DEV = false;
    public static boolean displayNames = true;
    public static boolean displayStart = true;
    public static boolean generateVillages = true;
    public static boolean generateLoneBuildings = true;
    public static boolean generateHamlets = false;
    public static boolean languageLearning = true;
    public static boolean TRAVEL_BOOK_LEARNING = true;
    public static boolean stopDefaultVillages = false;
    public static boolean loadAllLanguages = true;
    public static boolean autoConvertProfiles = false;
    public static boolean jpsPathing = true;
    public static boolean generateBuildingRes = false;
    public static boolean generateHelpData = false;
    public static boolean generateTranslationGap = false;
    public static boolean generateTravelBookExport = false;

    // --- Language ---
    public static String main_language = "";
    public static String effective_language = "";
    public static String fallback_language = "en";

    // --- Quest biomes ---
    public static String questBiomeForest = "forest";
    public static String questBiomeDesert = "desert";
    public static String questBiomeMountain = "mountain";

    // --- Distance constraints ---
    public static int maxChildrenNumber = 10;
    public static int minDistanceBetweenBuildings = 5;
    public static int minDistanceBetweenVillages = 500;
    public static int minDistanceBetweenVillagesAndLoneBuildings = 250;
    public static int minDistanceBetweenLoneBuildings = 500;
    public static int forcePreload = 0;
    public static int spawnProtectionRadius = 250;

    // --- Constants ---
    public static final String NEOL = System.getProperty("line.separator");
    public static final String EOL = "\n";

    public static final java.util.Set<String> forbiddenBlocks = new java.util.HashSet<>();

    public static void loadConfig() {
        // NeoForge config wiring: values are read from the NeoForge config system
        // in a later phase. For now, all values use their static defaults above.
        org.dizzymii.millenaire2.util.MillLog.minor("Config", "MillConfigValues loaded (defaults)");
    }

    public static void saveConfig() {
        // NeoForge config system auto-persists; this method is a compatibility stub.
        org.dizzymii.millenaire2.util.MillLog.minor("Config", "MillConfigValues saved");
    }
}
