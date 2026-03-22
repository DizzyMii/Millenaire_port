package org.dizzymii.millenaire2.culture;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.dizzymii.millenaire2.data.ConfigAnnotations;
import org.dizzymii.millenaire2.data.ConfigAnnotations.ConfigField;
import org.dizzymii.millenaire2.data.ConfigAnnotations.FieldDocumentation;
import org.dizzymii.millenaire2.data.ConfigAnnotations.ParameterType;
import org.dizzymii.millenaire2.data.ParametersManager;
import org.dizzymii.millenaire2.util.MillCommonUtilities;
import org.dizzymii.millenaire2.util.VirtualDir;

import java.io.File;
import java.util.*;

/**
 * Represents a Millénaire culture (Norman, Indian, Mayan, Japanese, Byzantine, etc.).
 * Each culture defines its own villager types, village types, building plans, names, and trade goods.
 * Loaded from data files at startup via VirtualDir + ParametersManager.
 *
 * Ported from org.millenaire.common.culture.Culture.
 */
public class Culture {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int LANGUAGE_FLUENT = 500;
    public static final int LANGUAGE_MODERATE = 200;
    public static final int LANGUAGE_BEGINNER = 100;

    public static final List<Culture> LIST_CULTURES = new ArrayList<>();
    private static final HashMap<String, Culture> cultures = new HashMap<>();

    public String key;

    @ConfigField(type = ParameterType.STRING, defaultValue = " ")
    @FieldDocumentation(explanation = "Separator between a village's name and its qualifier.")
    public String qualifierSeparator = " ";

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "knownCrop")
    @FieldDocumentation(explanation = "A crop known to the culture, that can be taught to the player.")
    public List<String> knownCrops = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "knownHuntingDrop")
    @FieldDocumentation(explanation = "A hunting drop known to the culture.")
    public List<String> knownHuntingDrops = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "travelBookVillagerCategory")
    @FieldDocumentation(explanation = "A category of villagers for the Travel Book.")
    public List<String> travelBookVillagerCategories = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "travelBookBuildingCategory")
    @FieldDocumentation(explanation = "A category of buildings for the Travel Book.")
    public List<String> travelBookBuildingCategories = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "travelBookTradeGoodCategory")
    @FieldDocumentation(explanation = "A category of trade goods for the Travel Book.")
    public List<String> travelBookTradeGoodCategories = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_INVITEM_ADD, paramName = "travelBookCategoryIcon")
    @FieldDocumentation(explanation = "The icon to use for this Travel Book category.")
    public Map<String, String> travelBookCategoriesIcons = new HashMap<>();

    @ConfigField(type = ParameterType.STRING, defaultValue = "")
    @FieldDocumentation(explanation = "A JSON object that specifies the culture's banner's appearance.")
    public String cultureBanner = "";

    @ConfigField(type = ParameterType.RESOURCE_LOCATION, defaultValue = "millenaire2:textures/entity/panels/default.png")
    @FieldDocumentation(explanation = "A resource path to a panel texture.")
    public String panelTexture = "millenaire2:textures/entity/panels/default.png";

    // --- Runtime data (populated during loading) ---
    public final Map<String, VillagerType> villagerTypes = new HashMap<>();
    public List<VillagerType> listVillagerTypes = new ArrayList<>();

    public final Map<String, VillageType> villageTypes = new HashMap<>();
    public List<VillageType> listVillageTypes = new ArrayList<>();

    public final Map<String, VillageType> loneBuildingTypes = new HashMap<>();
    public List<VillageType> listLoneBuildingTypes = new ArrayList<>();

    public final Map<String, BuildingPlanSet> planSets = new HashMap<>();
    public List<BuildingPlanSet> listPlanSets = new ArrayList<>();

    public final Map<String, WallType> wallTypes = new HashMap<>();

    public final Map<String, List<String>> nameLists = new HashMap<>();

    // --- Static methods ---

    public static Culture getCultureByName(String name) {
        return cultures.get(name);
    }

    public static java.util.Set<String> getAllCultureKeys() {
        return cultures.keySet();
    }

    /**
     * Clear all loaded cultures (for reload support).
     */
    public static void clearCultures() {
        cultures.clear();
        LIST_CULTURES.clear();
    }

    public static boolean loadCultures() {
        clearCultures();

        File contentDir = MillCommonUtilities.getMillenaireContentDir();
        File culturesDir = new File(contentDir, "cultures");
        File customContentDir = MillCommonUtilities.getMillenaireCustomContentDir();
        File customCulturesDir = new File(customContentDir, "cultures");

        if (!culturesDir.exists()) {
            LOGGER.error("Cultures directory not found: " + culturesDir.getAbsolutePath());
            return false;
        }

        // Collect all culture directory names from both default and custom
        java.util.Set<String> cultureNames = new java.util.TreeSet<>();
        File[] defaultDirs = culturesDir.listFiles(File::isDirectory);
        if (defaultDirs != null) {
            for (File d : defaultDirs) cultureNames.add(d.getName());
        }
        if (customCulturesDir.exists()) {
            File[] customDirs = customCulturesDir.listFiles(File::isDirectory);
            if (customDirs != null) {
                for (File d : customDirs) cultureNames.add(d.getName());
            }
        }

        for (String cultureName : cultureNames) {
            LOGGER.info("Loading culture: " + cultureName);

            // Build a VirtualDir merging default + custom (custom overrides default)
            List<File> sources = new ArrayList<>();
            File defaultDir = new File(culturesDir, cultureName);
            if (defaultDir.exists()) sources.add(defaultDir);
            File customDir = new File(customCulturesDir, cultureName);
            if (customDir.exists()) sources.add(customDir);

            VirtualDir vdir;
            if (sources.size() == 1) {
                vdir = new VirtualDir(sources.get(0));
            } else {
                try {
                    vdir = new VirtualDir(sources);
                } catch (Exception e) {
                    LOGGER.error("Error creating VirtualDir for culture: " + cultureName, e);
                    continue;
                }
            }

            Culture culture = new Culture(cultureName);
            culture.load(vdir);
            cultures.put(culture.key, culture);
            LIST_CULTURES.add(culture);
        }

        LOGGER.info("Finished loading " + LIST_CULTURES.size() + " cultures.");
        return !LIST_CULTURES.isEmpty();
    }

    // --- Instance methods ---

    public Culture(String key) {
        this.key = key;
    }

    public void load(VirtualDir cultureDir) {
        // Load culture.txt
        File cultureFile = cultureDir.getChildFile("culture.txt");
        if (cultureFile != null) {
            ParametersManager.loadFromFile(cultureFile, this, null, "culture");
        }

        // Load name lists
        loadNameLists(cultureDir);

        // Load villager types
        loadVillagerTypes(cultureDir);

        // Load wall types
        loadWallTypes(cultureDir);

        // Load building plans
        loadBuildingPlans(cultureDir);

        // Load village types
        loadVillageTypes(cultureDir);

        // Load lone building types
        loadLoneBuildingTypes(cultureDir);
    }

    private void loadNameLists(VirtualDir cultureDir) {
        VirtualDir namesDir = cultureDir.getChildDirectory("namelists");
        if (namesDir == null || !namesDir.exists()) return;

        for (File file : namesDir.listFiles()) {
            if (!file.getName().endsWith(".txt")) continue;
            String listName = file.getName().replace(".txt", "").toLowerCase();
            List<String> names = new ArrayList<>();
            try (var reader = MillCommonUtilities.getReader(file)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("//")) {
                        names.add(line);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error loading name list: " + file.getName(), e);
            }
            nameLists.put(listName, names);
        }
    }

    private void loadVillagerTypes(VirtualDir cultureDir) {
        VirtualDir villagersDir = cultureDir.getChildDirectory("villagers");
        if (villagersDir == null || !villagersDir.exists()) return;

        for (VirtualDir subDir : villagersDir.listSubDirs()) {
            for (File file : subDir.listFiles()) {
                if (!file.getName().endsWith(".txt")) continue;
                VillagerType vt = VillagerType.loadFromFile(file, this);
                if (vt != null) {
                    villagerTypes.put(vt.key, vt);
                    listVillagerTypes.add(vt);
                }
            }
        }
    }

    private void loadWallTypes(VirtualDir cultureDir) {
        VirtualDir wallsDir = cultureDir.getChildDirectory("walls");
        if (wallsDir == null || !wallsDir.exists()) return;

        for (File file : wallsDir.listFiles()) {
            if (!file.getName().endsWith(".txt")) continue;
            WallType wt = WallType.loadFromFile(file, this);
            if (wt != null) {
                wallTypes.put(wt.key, wt);
            }
        }
    }

    private void loadBuildingPlans(VirtualDir cultureDir) {
        VirtualDir buildingsDir = cultureDir.getChildDirectory("buildings");
        if (buildingsDir == null || !buildingsDir.exists()) return;

        for (VirtualDir subDir : buildingsDir.listSubDirs()) {
            for (File file : subDir.listFiles()) {
                if (!file.getName().endsWith(".txt")) continue;
                BuildingPlanSet bps = BuildingPlanSet.loadFromFile(file, this, subDir);
                if (bps != null) {
                    planSets.put(bps.key, bps);
                    listPlanSets.add(bps);
                }
            }
        }
    }

    private void loadVillageTypes(VirtualDir cultureDir) {
        VirtualDir villagesDir = cultureDir.getChildDirectory("villages");
        if (villagesDir == null || !villagesDir.exists()) return;

        for (File file : villagesDir.listFiles()) {
            if (!file.getName().endsWith(".txt")) continue;
            VillageType vt = VillageType.loadFromFile(file, this, false);
            if (vt != null) {
                villageTypes.put(vt.key, vt);
                listVillageTypes.add(vt);
            }
        }
    }

    private void loadLoneBuildingTypes(VirtualDir cultureDir) {
        VirtualDir loneDir = cultureDir.getChildDirectory("lonebuildings");
        if (loneDir == null || !loneDir.exists()) return;

        for (File file : loneDir.listFiles()) {
            if (!file.getName().endsWith(".txt")) continue;
            VillageType vt = VillageType.loadFromFile(file, this, true);
            if (vt != null) {
                loneBuildingTypes.put(vt.key, vt);
                listLoneBuildingTypes.add(vt);
            }
        }
    }

    public String getRandomName(String listName) {
        List<String> names = nameLists.get(listName.toLowerCase());
        if (names == null || names.isEmpty()) return "???";
        return names.get(MillCommonUtilities.randomInt(names.size()));
    }

    public VillagerType getVillagerType(String key) {
        return villagerTypes.get(key);
    }

    @Override
    public String toString() {
        return "Culture[" + key + "]";
    }
}
