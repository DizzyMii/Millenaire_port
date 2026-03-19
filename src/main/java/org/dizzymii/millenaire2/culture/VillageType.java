package org.dizzymii.millenaire2.culture;

import org.dizzymii.millenaire2.data.ConfigAnnotations.ConfigField;
import org.dizzymii.millenaire2.data.ConfigAnnotations.FieldDocumentation;
import org.dizzymii.millenaire2.data.ConfigAnnotations.ParameterType;
import org.dizzymii.millenaire2.data.ParametersManager;
import org.dizzymii.millenaire2.util.MillLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a type of village or lone building within a culture (fort, abbey, market, etc.).
 * Controls world generation, building composition, biome restrictions, and banners.
 *
 * Ported from org.millenaire.common.culture.VillageType.
 */
public class VillageType {

    public String key = null;
    public Culture culture;
    public boolean lonebuilding = false;

    @ConfigField(type = ParameterType.STRINGDISPLAY)
    @FieldDocumentation(explanation = "Name of the village type in the culture's language.")
    public String name = null;

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "banner_basecolor")
    @FieldDocumentation(explanation = "Banner base colors.")
    public List<String> banner_baseColors = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "banner_patterncolor")
    @FieldDocumentation(explanation = "Banner pattern colors.")
    public List<String> banner_patternsColors = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "banner_chargecolor")
    @FieldDocumentation(explanation = "Banner charge colors.")
    public List<String> banner_chargeColors = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "banner_pattern")
    @FieldDocumentation(explanation = "Banner patterns.")
    public List<String> banner_Patterns = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "banner_chargepattern")
    @FieldDocumentation(explanation = "Banner charge patterns.")
    public List<String> banner_chargePatterns = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "banner_json")
    @FieldDocumentation(explanation = "JSON banner definitions.")
    public List<String> banner_JSONs = new ArrayList<>();

    @ConfigField(type = ParameterType.BOOLEAN, paramName = "travelbook_display", defaultValue = "true")
    @FieldDocumentation(explanation = "Whether to display in the Travel Book.")
    public boolean travelBookDisplay = true;

    @ConfigField(type = ParameterType.INTEGER)
    @FieldDocumentation(explanation = "Generation weight.", explanationCategory = "World Generation")
    public int weight;

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "biome")
    @FieldDocumentation(explanation = "Biomes this village can spawn in.", explanationCategory = "World Generation")
    public List<String> biomes = new ArrayList<>();

    @ConfigField(type = ParameterType.INTEGER, defaultValue = "-1")
    @FieldDocumentation(explanation = "Max number in a world. -1 for no limit.", explanationCategory = "World Generation")
    public int max;

    @ConfigField(type = ParameterType.FLOAT, defaultValue = "0.6")
    @FieldDocumentation(explanation = "% of village that must be in the appropriate biome.", explanationCategory = "World Generation")
    public float minimumBiomeValidity;

    @ConfigField(type = ParameterType.BOOLEAN, defaultValue = "true")
    @FieldDocumentation(explanation = "Whether to generate on MP servers.", explanationCategory = "World Generation")
    public boolean generateOnServer;

    @ConfigField(type = ParameterType.BOOLEAN, paramName = "generateforplayer", defaultValue = "false")
    @FieldDocumentation(explanation = "Whether generated for a specific player.", explanationCategory = "World Generation")
    public boolean generatedForPlayer;

    @ConfigField(type = ParameterType.INTEGER, defaultValue = "-1")
    @FieldDocumentation(explanation = "Min distance from spawn. -1 for no limit.", explanationCategory = "World Generation")
    public int minDistanceFromSpawn;

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "requiredtag")
    @FieldDocumentation(explanation = "Global tags required for generation.", explanationCategory = "World Generation")
    public List<String> requiredTags = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "forbiddentag")
    @FieldDocumentation(explanation = "Global tags that prevent generation.", explanationCategory = "World Generation")
    public List<String> forbiddenTags = new ArrayList<>();

    @ConfigField(type = ParameterType.BOOLEAN, defaultValue = "false")
    @FieldDocumentation(explanation = "Key lone buildings get generation priority.", explanationCategory = "World Generation")
    public boolean keyLonebuilding;

    @ConfigField(type = ParameterType.BOOLEAN, defaultValue = "false")
    @FieldDocumentation(explanation = "Player-controlled village.", explanationCategory = "Village type")
    public boolean playerControlled;

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "hameau")
    @FieldDocumentation(explanation = "Hamlet types generated around this village.", explanationCategory = "Village type")
    public List<String> hamlets = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING, paramName = "type")
    @FieldDocumentation(explanation = "Special type (e.g. 'hamlet').", explanationCategory = "Village type")
    public String specialType = null;

    @ConfigField(type = ParameterType.BOOLEAN)
    @FieldDocumentation(explanation = "Whether spawnable with a wand.", explanationCategory = "Village type")
    public boolean spawnable;

    @ConfigField(type = ParameterType.INTEGER)
    @FieldDocumentation(explanation = "Village radius override.", explanationCategory = "Village Behaviour")
    public int radius;

    // Building references stored as string keys — resolved after all plans are loaded
    @ConfigField(type = ParameterType.STRING, paramName = "centre")
    @FieldDocumentation(explanation = "Centre building plan set key.", explanationCategory = "Village Buildings")
    public String centreBuilding = null;

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "start")
    @FieldDocumentation(explanation = "Starting buildings.", explanationCategory = "Village Buildings")
    public List<String> startBuildings = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "player")
    @FieldDocumentation(explanation = "Player-purchasable buildings.", explanationCategory = "Village Buildings")
    public List<String> playerBuildings = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "core")
    @FieldDocumentation(explanation = "Core buildings.", explanationCategory = "Village Buildings")
    public List<String> coreBuildings = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "secondary")
    @FieldDocumentation(explanation = "Secondary buildings.", explanationCategory = "Village Buildings")
    public List<String> secondaryBuildings = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "never")
    @FieldDocumentation(explanation = "Excluded buildings.", explanationCategory = "Village Buildings")
    public List<String> excludedBuildings = new ArrayList<>();

    // --- Villager definitions ---
    @ConfigField(type = ParameterType.STRING_ADD, paramName = "villager")
    @FieldDocumentation(explanation = "Villager types for this village.")
    public List<String> villagers = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "lonevillager")
    @FieldDocumentation(explanation = "Villager types for lone buildings.")
    public List<String> loneVillagers = new ArrayList<>();

    @ConfigField(type = ParameterType.BOOLEAN, paramName = "carriesraid", defaultValue = "false")
    @FieldDocumentation(explanation = "Whether this village type carries out raids.")
    public boolean carriesRaid = false;

    // --- Legacy params (parsed to suppress warnings, used for player-controlled villages) ---
    @ConfigField(type = ParameterType.STRING_ADD, paramName = "custombuilding")
    @FieldDocumentation(explanation = "Custom buildings for player-controlled villages.")
    public List<String> customBuildings = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "pathmaterial")
    @FieldDocumentation(explanation = "Path materials used between buildings.")
    public List<String> pathMaterials = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING, paramName = "icon")
    @FieldDocumentation(explanation = "Icon identifier for UI display.")
    public String icon = null;

    @ConfigField(type = ParameterType.INTEGER, paramName = "maxsimultaneousconstructions", defaultValue = "1")
    @FieldDocumentation(explanation = "Max simultaneous constructions.")
    public int maxSimultaneousConstructions = 1;

    @ConfigField(type = ParameterType.STRING, paramName = "qualifier")
    @FieldDocumentation(explanation = "Default village name qualifier.")
    public String qualifierName = null;

    @ConfigField(type = ParameterType.STRING, paramName = "hillqualifier")
    @FieldDocumentation(explanation = "Qualifier for hilly terrain.")
    public String hillQualifier = null;

    @ConfigField(type = ParameterType.STRING, paramName = "mountainqualifier")
    @FieldDocumentation(explanation = "Qualifier for mountain terrain.")
    public String mountainQualifier = null;

    @ConfigField(type = ParameterType.STRING, paramName = "desertqualifier")
    @FieldDocumentation(explanation = "Qualifier for desert terrain.")
    public String desertQualifier = null;

    @ConfigField(type = ParameterType.STRING, paramName = "forestqualifier")
    @FieldDocumentation(explanation = "Qualifier for forest terrain.")
    public String forestQualifier = null;

    @ConfigField(type = ParameterType.STRING, paramName = "lavaqualifier")
    @FieldDocumentation(explanation = "Qualifier for volcanic terrain.")
    public String lavaQualifier = null;

    @ConfigField(type = ParameterType.STRING, paramName = "lakequalifier")
    @FieldDocumentation(explanation = "Qualifier for lake terrain.")
    public String lakeQualifier = null;

    @ConfigField(type = ParameterType.STRING, paramName = "oceanqualifier")
    @FieldDocumentation(explanation = "Qualifier for ocean terrain.")
    public String oceanQualifier = null;

    public VillageType(Culture culture, String key) {
        this.culture = culture;
        this.key = key;
    }

    public static VillageType loadFromFile(File file, Culture culture, boolean isLoneBuilding) {
        String key = file.getName().replace(".txt", "").toLowerCase();
        VillageType vt = new VillageType(culture, key);
        vt.lonebuilding = isLoneBuilding;

        try {
            Object result = ParametersManager.loadFromFile(file, vt, null, "village type");
            if (result == null) return null;

            MillLog.minor(vt, "Loaded village type: " + vt.key + " (lone=" + vt.lonebuilding + ")");
            return vt;
        } catch (Exception e) {
            MillLog.error(null, "Error loading village type: " + file.getName(), e);
            return null;
        }
    }

    @Override
    public String toString() {
        return "VillageType[" + key + "]";
    }
}
