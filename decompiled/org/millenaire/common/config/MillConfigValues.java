/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 */
package org.millenaire.common.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import net.minecraft.block.Block;
import org.millenaire.common.advancements.GenericAdvancement;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.config.MillConfigParameter;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.LanguageData;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.virtualdir.VirtualDir;

public class MillConfigValues {
    public static int KeepActiveRadius = 200;
    public static int BackgroundRadius = 2000;
    public static int BanditRaidRadius = 1500;
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
    public static String questBiomeForest = "forest";
    public static String questBiomeDesert = "desert";
    public static String questBiomeMountain = "mountain";
    public static int LogNetwork = 0;
    public static boolean DEV = false;
    public static boolean displayNames = true;
    public static boolean displayStart = true;
    public static final String NEOL = System.getProperty("line.separator");
    public static final String EOL = "\n";
    public static List<Block> forbiddenBlocks = new ArrayList<Block>();
    public static boolean generateBuildingRes = false;
    public static boolean generateHelpData = false;
    public static boolean generateVillages = true;
    public static boolean generateLoneBuildings = true;
    public static boolean generateHamlets = false;
    public static boolean generateTranslationGap = false;
    public static boolean generateTravelBookExport = false;
    public static boolean languageLearning = true;
    public static boolean TRAVEL_BOOK_LEARNING = true;
    public static boolean stopDefaultVillages = false;
    public static boolean loadAllLanguages = true;
    public static boolean autoConvertProfiles = false;
    public static boolean jpsPathing = true;
    public static String main_language = "";
    public static String effective_language = "";
    public static String fallback_language = "en";
    private static boolean logfile = true;
    public static int maxChildrenNumber = 10;
    public static int minDistanceBetweenBuildings = 5;
    public static int minDistanceBetweenVillages = 500;
    public static int minDistanceBetweenVillagesAndLoneBuildings = 250;
    public static int minDistanceBetweenLoneBuildings = 500;
    public static int forcePreload = 0;
    public static int spawnProtectionRadius = 250;
    public static int VillageRadius = 80;
    public static int VillagersNamesDistance = 12;
    public static boolean BuildVillagePaths = true;
    public static boolean ignoreResourceCost = false;
    public static int VillagersSentenceInChatDistanceClient = 0;
    public static int VillagersSentenceInChatDistanceSP = 6;
    public static int villageSpawnCompletionMaxPercentage = 25;
    public static int villageSpawnCompletionMinDistance = 2000;
    public static int villageSpawnCompletionMaxDistance = 10000;
    public static int RaidingRate = 20;
    public static LanguageData mainLanguage = null;
    public static LanguageData fallbackLanguage = null;
    public static LanguageData serverMainLanguage = null;
    public static LanguageData serverFallbackLanguage = null;
    public static HashMap<String, LanguageData> loadedLanguages = new HashMap();
    public static String bonusCode = null;
    public static boolean bonusEnabled = false;
    public static boolean sendStatistics = true;
    public static boolean sendAdvancementLogin = false;
    public static long randomUid = (long)(Math.random() * 9.223372036854776E18);
    public static SortedSet<String> advancementsSurvival = new TreeSet<String>();
    public static SortedSet<String> advancementsCreative = new TreeSet<String>();
    public static HashMap<String, MillConfigParameter> configParameters = new HashMap();
    public static List<String> configPageTitles = new ArrayList<String>();
    public static List<String> configPageDesc = new ArrayList<String>();
    public static List<List<MillConfigParameter>> configPages = new ArrayList<List<MillConfigParameter>>();
    public static boolean logPerformed = false;

    public static String calculateLoginMD5(String login) {
        return MillConfigValues.md5(login + login.substring(1)).substring(0, 4);
    }

    public static void checkBonusCode(boolean manual) {
        if (Mill.proxy.getSinglePlayerName() == null) {
            bonusEnabled = false;
            return;
        }
        String login = Mill.proxy.getSinglePlayerName();
        if (bonusCode != null) {
            String calculatedCode = MillConfigValues.calculateLoginMD5(login);
            bonusEnabled = calculatedCode.equals(bonusCode);
        }
        if (!bonusEnabled && !manual) {
            new MillCommonUtilities.BonusThread(login).start();
        }
        if (manual && bonusCode != null && bonusCode.length() == 4) {
            if (bonusEnabled) {
                Mill.proxy.sendLocalChat(Mill.proxy.getTheSinglePlayer(), '2', LanguageUtilities.string("config.validbonuscode"));
            } else {
                Mill.proxy.sendLocalChat(Mill.proxy.getTheSinglePlayer(), '4', LanguageUtilities.string("config.invalidbonuscode"));
            }
        }
    }

    private static void detectSubmods() {
        File modDirs = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "mods");
        modDirs.mkdirs();
        String mods = "";
        for (File mod : modDirs.listFiles()) {
            if (!mod.isDirectory() || mod.isHidden()) continue;
            Mill.loadingDirs.add(mod);
            mods = mods + mod.getName() + " ";
        }
        if (mods.length() == 0) {
            MillLog.writeText("Starting new session.");
        } else {
            MillLog.writeText("Starting new session. Mods: " + mods);
        }
    }

    private static String getAdvancementString(Set<String> advancements) {
        String advancementsDone = "";
        for (GenericAdvancement advancement : MillAdvancements.MILL_ADVANCEMENTS) {
            if (!advancements.contains(advancement.getKey())) continue;
            if (advancementsDone.length() > 0) {
                advancementsDone = advancementsDone + ",";
            }
            advancementsDone = advancementsDone + advancement.getKey();
        }
        return advancementsDone;
    }

    public static void initConfig() {
        MillConfigValues.initConfigItems();
        boolean mainConfig = MillConfigValues.readConfigFile(Mill.proxy.getConfigFile(), true);
        if (!mainConfig) {
            System.err.println("ERREUR: Impossible de trouver le fichier de configuration " + Mill.proxy.getConfigFile().getAbsolutePath() + ". V\u00e9rifiez que le dossier millenaire est bien dans minecraft/mods/");
            System.err.println("ERROR: Could not find the config file at " + Mill.proxy.getConfigFile().getAbsolutePath() + ". Check that the millenaire directory is in minecraft/mods/");
            if (!Mill.proxy.isTrueServer()) {
                Mill.displayMillenaireLocationError = true;
            }
            Mill.startupError = true;
            return;
        }
        MillConfigValues.readConfigFile(Mill.proxy.getCustomConfigFile(), false);
        MillConfigValues.writeConfigFile();
        if (logfile) {
            MillLog.initLogFileWriter();
        }
        Mill.loadingDirs.add(MillCommonUtilities.getMillenaireContentDir());
        MillConfigValues.detectSubmods();
        Mill.loadingDirs.add(MillCommonUtilities.getMillenaireCustomContentDir());
        try {
            Mill.virtualLoadingDir = new VirtualDir(Mill.loadingDirs);
        }
        catch (Exception e) {
            MillLog.printException(e);
        }
    }

    private static void initConfigItems() {
        try {
            ArrayList<MillConfigParameter> configSection = new ArrayList<MillConfigParameter>();
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("fallback_language"), "fallback_language", "en", "fr"));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("languageLearning"), "language_learning", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("TRAVEL_BOOK_LEARNING"), "travel_book_learning", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("loadAllLanguages"), "load_all_languages", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("displayStart"), "display_start", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("displayNames"), "display_names", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("VillagersNamesDistance"), "villagers_names_distance", 5, 10, 20, 30, 50));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("VillagersSentenceInChatDistanceSP"), "villagers_sentence_in_chat_distance_sp", 0, 1, 2, 3, 4, 6, 10));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("VillagersSentenceInChatDistanceClient"), "villagers_sentence_in_chat_distance_client", 0, 1, 2, 3, 4, 6, 10));
            configPages.add(configSection);
            configPageTitles.add("config.page.uisettings");
            configPageDesc.add(null);
            configSection = new ArrayList();
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("generateVillages"), "generate_villages", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("generateLoneBuildings"), "generate_lone_buildings", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("generateHamlets"), "generate_hamlets", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("minDistanceBetweenVillages"), "min_village_distance", 300, 450, 600, 800, 1000));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("minDistanceBetweenVillagesAndLoneBuildings"), "min_village_lonebuilding_distance", 100, 200, 300, 500, 800));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("minDistanceBetweenLoneBuildings"), "min_lonebuilding_distance", 300, 450, 600, 800, 1000));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("spawnProtectionRadius"), "spawn_protection_radius", 0, 50, 100, 150, 250, 500));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("villageSpawnCompletionMaxPercentage"), "village_spawn_completion_max_percentage", 0, 10, 25, 50, 75, 100));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("villageSpawnCompletionMinDistance"), "village_spawn_completion_min_distance", 0, 1000, 2000, 5000, 10000, 25000));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("villageSpawnCompletionMaxDistance"), "village_spawn_completion_max_distance", 0, 5000, 10000, 25000, 100000));
            configPages.add(configSection);
            configPageTitles.add("config.page.worldgeneration");
            configPageDesc.add("config.page.worldgeneration.desc");
            configSection = new ArrayList();
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("KeepActiveRadius"), "keep_active_radius", 0, 100, 150, 200, 250, 300, 400, 500, 1000, 2000));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("VillageRadius"), "village_radius", 50, 60, 70, 80, 90, 100, 120));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("minDistanceBetweenBuildings"), "min_distance_between_buildings", 0, 1, 2, 3, 4));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("BuildVillagePaths"), "village_paths", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("maxChildrenNumber"), "max_children_number", 2, 5, 10, 15, 20));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("BackgroundRadius"), "background_radius", 0, 200, 500, 1000, 1500, 2000, 2500, 3000));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("BanditRaidRadius"), "bandit_raid_radius", 0, 200, 500, 1000, 1500, 2000));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("RaidingRate"), "raiding_rate", 0, 10, 20, 50, 100));
            configPages.add(configSection);
            configPageTitles.add("config.page.villagebehaviour");
            configPageDesc.add("config.page.villagebehaviour.desc");
            configSection = new ArrayList();
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("sendStatistics"), "send_statistics", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("sendAdvancementLogin"), "send_advancement_login", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("autoConvertProfiles"), "auto_convert_profiles", new Object[0]));
            configPages.add(configSection);
            configPageTitles.add("config.page.system");
            configPageDesc.add("config.page.system.desc");
            configSection = new ArrayList();
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("generateTranslationGap"), "generate_translation_gap", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("generateTravelBookExport"), "generate_travel_book_export", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("generateHelpData"), "generate_help_data", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("generateBuildingRes"), "generate_building_res", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("ignoreResourceCost"), "ignore_resource_cost", new Object[0]));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogTileEntityBuilding"), "LogTileEntityBuilding", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogWorldGeneration"), "LogWorldGeneration", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogDiplomacy"), "LogDiplomacy", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogWifeAI"), "LogWifeAI", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogVillager"), "LogVillager", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogQuest"), "LogQuest", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogPathing"), "LogPathing", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogConnections"), "LogConnections", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogGetPath"), "LogGetPath", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogLumberman"), "LogLumberman", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogBuildingPlan"), "LogBuildingPlan", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogGeneralAI"), "LogGeneralAI", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogSelling"), "LogSelling", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogHybernation"), "LogHybernation", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogOther"), "LogOther", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogChildren"), "LogChildren", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogCattleFarmer"), "LogCattleFarmer", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogMiner"), "LogMiner", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogVillage"), "LogVillage", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogWorldInfo"), "LogWorldInfo", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogPujas"), "LogPujas", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogVillagerSpawn"), "LogVillagerSpawn", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogVillagePaths"), "LogVillagePaths", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogNetwork"), "LogNetwork", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogMerchant"), "LogMerchant", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogCulture"), "LogCulture", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogTranslation"), "LogTranslation", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogChunkLoader"), "LogChunkLoader", 5).setDisplayDev(true));
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("LogTags"), "LogTags", 5).setDisplayDev(true));
            configPages.add(configSection);
            configPageTitles.add("config.page.devtools");
            configPageDesc.add(null);
            configSection = new ArrayList();
            configSection.add(new MillConfigParameter(MillConfigValues.class.getField("bonusCode"), "bonus_code", 6).setMaxStringLength(4));
            configPages.add(configSection);
            configPageTitles.add("config.page.bonus");
            configPageDesc.add("config.page.bonus.desc");
            for (List<MillConfigParameter> aConfigPage : configPages) {
                for (MillConfigParameter config : aConfigPage) {
                    configParameters.put(config.key, config);
                }
            }
        }
        catch (Exception e) {
            MillLog.error(null, "Exception when initialising config items: " + e);
        }
    }

    private static String md5(String input) {
        String result = input;
        if (input != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(input.getBytes());
                BigInteger hash = new BigInteger(1, md.digest());
                result = hash.toString(16);
                while (result.length() < 32) {
                    result = "0" + result;
                }
            }
            catch (NoSuchAlgorithmException e) {
                MillLog.printException("Exception in md5():", e);
            }
        }
        return result;
    }

    private static boolean readConfigFile(File file, boolean defaultFile) {
        if (!file.exists()) {
            return false;
        }
        try {
            String line;
            BufferedReader reader = MillCommonUtilities.getReader(file);
            long validationKey = 0L;
            while ((line = reader.readLine()) != null) {
                String[] advancementValues;
                String[] temp;
                if (line.trim().length() <= 0 || line.startsWith("//") || (temp = line.split("=")).length != 2) continue;
                String key = temp[0].trim().toLowerCase();
                String value = temp[1];
                boolean configHandled = false;
                if (configParameters.containsKey(key)) {
                    configParameters.get(key).setValueFromString(value, defaultFile);
                    configHandled = true;
                }
                if (configHandled) continue;
                if (key.equalsIgnoreCase("devmode")) {
                    DEV = Boolean.parseBoolean(value);
                    continue;
                }
                if (key.equalsIgnoreCase("console")) {
                    MillLog.console = Boolean.parseBoolean(value);
                    continue;
                }
                if (key.equalsIgnoreCase("logfile")) {
                    logfile = Boolean.parseBoolean(value);
                    continue;
                }
                if (key.equalsIgnoreCase("stop_default_villages")) {
                    stopDefaultVillages = Boolean.parseBoolean(value);
                    continue;
                }
                if (key.equalsIgnoreCase("language")) {
                    main_language = value.toLowerCase();
                    continue;
                }
                if (key.equalsIgnoreCase("forbidden_blocks")) {
                    for (String name : value.split(",")) {
                        if (Block.func_149684_b((String)name) != null) {
                            forbiddenBlocks.add(Block.func_149684_b((String)name));
                            continue;
                        }
                        System.out.println("Could not read forbidden name: " + name);
                    }
                    continue;
                }
                if (key.equalsIgnoreCase("force_preload_radius")) {
                    forcePreload = Integer.parseInt(value) / 16;
                    continue;
                }
                if (key.equalsIgnoreCase("quest_biome_forest")) {
                    questBiomeForest = value.trim().toLowerCase();
                    continue;
                }
                if (key.equalsIgnoreCase("quest_biome_desert")) {
                    questBiomeDesert = value.trim().toLowerCase();
                    continue;
                }
                if (key.equalsIgnoreCase("quest_biome_mountain")) {
                    questBiomeMountain = value.trim().toLowerCase();
                    continue;
                }
                if (key.equalsIgnoreCase("random_uid")) {
                    randomUid = Long.parseLong(value);
                    continue;
                }
                if (key.equalsIgnoreCase("advancements_survival")) {
                    for (String advancement : advancementValues = value.split(",")) {
                        advancementsSurvival.add(advancement);
                    }
                    continue;
                }
                if (key.equalsIgnoreCase("advancements_creative")) {
                    for (String advancement : advancementValues = value.split(",")) {
                        advancementsCreative.add(advancement);
                    }
                    continue;
                }
                if (key.equalsIgnoreCase("validation")) {
                    validationKey = Long.parseLong(value.trim());
                    continue;
                }
                MillLog.error(null, "Unknown config on line: " + line);
            }
            reader.close();
            System.out.println("Read config in " + file.getName() + ". Logging: " + MillLog.console + "/" + logfile);
            if (randomUid == 204051766008600576L || randomUid == 6625358011945542656L) {
                DEV = false;
                randomUid = (long)(Math.random() * 9.223372036854776E18);
                bonusCode = null;
                for (String key : configParameters.keySet()) {
                    if (MillConfigValues.configParameters.get((Object)key).defaultVal == null) continue;
                    configParameters.get(key).setValue(MillConfigValues.configParameters.get((Object)key).defaultVal);
                }
                file.delete();
                MillConfigValues.writeConfigFile();
                System.out.println("Detected a config file from the ones mistakenly included in beta 10 and 11. Resetting configs.");
            }
            if (!defaultFile && validationKey != MillAdvancements.computeKey()) {
                advancementsCreative.clear();
                advancementsSurvival.clear();
            }
            return true;
        }
        catch (Exception e) {
            MillLog.printException(e);
            return false;
        }
    }

    public static void writeBaseConfigFile() {
        File file = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "config-base.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                System.err.println("Could not create config file at " + file.getAbsolutePath());
            }
        }
        try {
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            LanguageData main = mainLanguage;
            LanguageData fr = loadedLanguages.get("fr");
            LanguageData en = loadedLanguages.get("en");
            for (int i = 0; i < configPages.size(); ++i) {
                mainLanguage = fr;
                String frTitle = LanguageUtilities.string(configPageTitles.get(i));
                mainLanguage = en;
                String enTitle = LanguageUtilities.string(configPageTitles.get(i));
                writer.write("//--------------------------------------------------------------------------------------------\n");
                writer.write("//       " + frTitle + "    -    " + enTitle + EOL);
                writer.write("//--------------------------------------------------------------------------------------------\n\n");
                for (int j = 0; j < configPages.get(i).size(); ++j) {
                    MillConfigParameter config = configPages.get(i).get(j);
                    mainLanguage = fr;
                    writer.write("//" + config.getLabel() + "; " + config.getDesc() + EOL);
                    mainLanguage = en;
                    writer.write("//" + config.getLabel() + "; " + config.getDesc() + EOL);
                    writer.write(config.key + "=" + config.getDefaultValueForDisplay() + EOL + EOL);
                }
            }
            mainLanguage = main;
            writer.close();
        }
        catch (Exception e) {
            MillLog.printException("Exception in writeBaseConfigFile:", e);
        }
    }

    public static void writeConfigFile() {
        boolean randomUidSaved = false;
        boolean advancementsSurvivalSaved = false;
        boolean advancementsCreativeSaved = false;
        File file = Mill.proxy.getCustomConfigFile();
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                System.err.println("Could not create config file at " + file.getAbsolutePath());
            }
        }
        try {
            String advancementsDone;
            String line;
            BufferedReader reader = MillCommonUtilities.getReader(file);
            ArrayList<String> toWrite = new ArrayList<String>();
            HashSet<MillConfigParameter> configsWritten = new HashSet<MillConfigParameter>();
            while ((line = reader.readLine()) != null) {
                boolean handled = false;
                if (line.trim().length() > 0 && !line.startsWith("//")) {
                    String[] temp = line.split("=");
                    String key = temp[0].trim().toLowerCase();
                    Object value = "";
                    if (temp.length > 1) {
                        value = temp[1];
                    }
                    if (configParameters.containsKey(key)) {
                        if (configParameters.get(key).compareValuesFromString((String)value)) {
                            configsWritten.add(configParameters.get(key));
                        } else {
                            toWrite.add(key + "=" + configParameters.get(key).getSaveValue());
                            configsWritten.add(configParameters.get(key));
                            handled = true;
                        }
                    } else if (key.equals("random_uid")) {
                        if (Long.parseLong((String)value) > 0L) {
                            randomUidSaved = true;
                        } else {
                            handled = true;
                        }
                    } else if (key.equals("advancements_survival")) {
                        toWrite.add(key + "=" + MillConfigValues.getAdvancementString(advancementsSurvival));
                        handled = true;
                        advancementsSurvivalSaved = true;
                    } else if (key.equals("advancements_creative")) {
                        toWrite.add(key + "=" + MillConfigValues.getAdvancementString(advancementsCreative));
                        handled = true;
                        advancementsCreativeSaved = true;
                    } else if (key.equals("validation")) {
                        handled = true;
                    }
                }
                if (handled) continue;
                toWrite.add(line);
            }
            reader.close();
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            for (String s : toWrite) {
                writer.write(s + EOL);
            }
            for (MillConfigParameter config : configParameters.values()) {
                if (configsWritten.contains(config) || config.isCurrentValueTheDefault()) continue;
                writer.write("//" + config.getLabel() + "; " + config.getDesc() + EOL);
                writer.write(config.key + "=" + config.getSaveValue() + EOL + EOL);
            }
            if (!randomUidSaved) {
                writer.write("//Unique random identifier to count unique players if statistics are enabled\n");
                writer.write("random_uid=" + randomUid + EOL + EOL);
            }
            if (!advancementsCreativeSaved && (advancementsDone = MillConfigValues.getAdvancementString(advancementsCreative)) != null) {
                writer.write("//Advancement completed for anonymous statistics\n");
                writer.write("advancements_creative=" + advancementsDone + EOL + EOL);
            }
            if (!advancementsSurvivalSaved && (advancementsDone = MillConfigValues.getAdvancementString(advancementsSurvival)) != null) {
                writer.write("//Advancement completed for anonymous statistics\n");
                writer.write("advancements_survival=" + advancementsDone + EOL + EOL);
            }
            writer.write("validation=" + MillAdvancements.computeKey() + EOL);
            writer.close();
        }
        catch (Exception e) {
            MillLog.printException("Exception in writeConfigFile:", e);
        }
    }
}

