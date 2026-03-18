package org.dizzymii.millenaire2.culture;

import org.dizzymii.millenaire2.data.ConfigAnnotations.ConfigField;
import org.dizzymii.millenaire2.data.ConfigAnnotations.FieldDocumentation;
import org.dizzymii.millenaire2.data.ConfigAnnotations.ParameterType;
import org.dizzymii.millenaire2.data.ParametersManager;
import org.dizzymii.millenaire2.util.MillCommonUtilities;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.VirtualDir;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A set of building plans representing a single building concept with upgrades.
 * For example, "fort_A" may have initial, upgrade1, upgrade2.
 * Each upgrade level is a BuildingPlan.
 *
 * Also handles loading the .txt building definition files, which contain both
 * set-level parameters (building.xxx=) and upgrade-level parameters (initial.xxx=, upgrade1.xxx=).
 *
 * Ported from org.millenaire.common.buildingplan.BuildingPlanSet.
 */
public class BuildingPlanSet {

    public String key;
    public Culture culture;
    public File sourceFile;

    @ConfigField(type = ParameterType.STRINGDISPLAY, paramName = "name")
    @FieldDocumentation(explanation = "Display name of the building.")
    public String name;

    @ConfigField(type = ParameterType.INTEGER, paramName = "weight", defaultValue = "1")
    @FieldDocumentation(explanation = "Generation weight for this building.")
    public int weight = 1;

    @ConfigField(type = ParameterType.STRING, paramName = "travelbook_category")
    @FieldDocumentation(explanation = "Travel Book category.")
    public String travelBookCategory = null;

    @ConfigField(type = ParameterType.BOOLEAN, paramName = "travelbook_display", defaultValue = "true")
    @FieldDocumentation(explanation = "Whether to show in Travel Book.")
    public boolean travelBookDisplay = true;

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "tag")
    @FieldDocumentation(explanation = "Tags for the building.")
    public List<String> tags = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "subbuilding")
    @FieldDocumentation(explanation = "Sub-buildings that are part of this set.")
    public List<String> subBuildings = new ArrayList<>();

    @ConfigField(type = ParameterType.INTEGER, paramName = "length", defaultValue = "0")
    @FieldDocumentation(explanation = "Length of the building (PNG height).")
    public int length = 0;

    @ConfigField(type = ParameterType.INTEGER, paramName = "width", defaultValue = "0")
    @FieldDocumentation(explanation = "Width of the building (PNG floor width).")
    public int width = 0;

    @ConfigField(type = ParameterType.INTEGER, paramName = "buildingorientation", defaultValue = "1")
    @FieldDocumentation(explanation = "Default orientation of the building.")
    public int buildingOrientation = 1;

    @ConfigField(type = ParameterType.INTEGER, paramName = "version", defaultValue = "0")
    @FieldDocumentation(explanation = "Data file version number.")
    public int version = 0;

    @ConfigField(type = ParameterType.STRING, paramName = "icon")
    @FieldDocumentation(explanation = "Icon key for GUI display.")
    public String icon = null;

    @ConfigField(type = ParameterType.INTEGER, paramName = "max", defaultValue = "-1")
    @FieldDocumentation(explanation = "Max number of this building in a village. -1 for no limit.")
    public int max = -1;

    @ConfigField(type = ParameterType.STRING, paramName = "fixedorientation")
    @FieldDocumentation(explanation = "Fixed orientation direction if building cannot rotate.")
    public String fixedOrientation = null;

    @ConfigField(type = ParameterType.BOOLEAN, paramName = "issubbuilding", defaultValue = "false")
    @FieldDocumentation(explanation = "Whether this is a sub-building of another building.")
    public boolean isSubBuilding = false;

    @ConfigField(type = ParameterType.STRING, paramName = "parentbuildingplan")
    @FieldDocumentation(explanation = "Key of the parent building plan set if this is a sub-building.")
    public String parentBuildingPlan = null;

    @ConfigField(type = ParameterType.INTEGER, paramName = "areatoclear", defaultValue = "5")
    @FieldDocumentation(explanation = "Area to clear around the building.")
    public int areaToClear = 5;

    @ConfigField(type = ParameterType.INTEGER, paramName = "areatoclearlengthbefore", defaultValue = "0")
    @FieldDocumentation(explanation = "Area to clear before building along length axis.")
    public int areaToClearLengthBefore = 0;

    @ConfigField(type = ParameterType.INTEGER, paramName = "areatoclearlengthafter", defaultValue = "0")
    @FieldDocumentation(explanation = "Area to clear after building along length axis.")
    public int areaToClearLengthAfter = 0;

    @ConfigField(type = ParameterType.INTEGER, paramName = "areatoclearwidthbefore", defaultValue = "0")
    @FieldDocumentation(explanation = "Area to clear before building along width axis.")
    public int areaToClearWidthBefore = 0;

    @ConfigField(type = ParameterType.INTEGER, paramName = "areatoclearwidthafter", defaultValue = "0")
    @FieldDocumentation(explanation = "Area to clear after building along width axis.")
    public int areaToClearWidthAfter = 0;

    @ConfigField(type = ParameterType.BOOLEAN, paramName = "iswallsegment", defaultValue = "false")
    @FieldDocumentation(explanation = "Whether this building is a wall segment.")
    public boolean isWallSegment = false;

    @ConfigField(type = ParameterType.BOOLEAN, paramName = "isborderbuilding", defaultValue = "false")
    @FieldDocumentation(explanation = "Whether this is a border building.")
    public boolean isBorderBuilding = false;

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "startingsubbuilding")
    @FieldDocumentation(explanation = "Sub-buildings constructed at village start.")
    public List<String> startingSubBuildings = new ArrayList<>();

    // Upgrade plan references (populated during loading)
    public final List<BuildingPlan> plans = new ArrayList<>();
    public final Map<String, BuildingPlan> plansByKey = new HashMap<>();

    // Raw lines from the file (used for prefixed parameter loading)
    private List<String> rawLines;

    public BuildingPlanSet(Culture culture, String key) {
        this.culture = culture;
        this.key = key;
    }

    public static BuildingPlanSet loadFromFile(File file, Culture culture, VirtualDir buildingsDir) {
        String key = file.getName().replace(".txt", "").toLowerCase();
        BuildingPlanSet bps = new BuildingPlanSet(culture, key);
        bps.sourceFile = file;

        try {
            // Read all lines first
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = MillCommonUtilities.getReader(file)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            bps.rawLines = lines;

            // Load building-level parameters (building.xxx=)
            loadBuildingLevelParams(lines, bps);

            // Load upgrade plans: initial, upgrade1, upgrade2, ...
            loadUpgradePlans(lines, bps, buildingsDir);

            MillLog.minor(bps, "Loaded building plan set: " + bps.key + " (" + bps.plans.size() + " levels)");
            return bps;
        } catch (Exception e) {
            MillLog.error(null, "Error loading building plan set: " + file.getName(), e);
            return null;
        }
    }

    private static void loadBuildingLevelParams(List<String> lines, BuildingPlanSet bps) {
        // Extract building.xxx= lines and parse them as key=value
        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("//")) continue;
            if (!line.startsWith("building.")) continue;
            // These are handled by the ConfigField annotations on this class
        }
        // Use ParametersManager with prefixed loading
        ParametersManager.loadPrefixed(lines, "building", bps, null, "building plan set", bps.key);
    }

    private static void loadUpgradePlans(List<String> lines, BuildingPlanSet bps, VirtualDir buildingsDir) {
        // Check for "initial" prefix
        boolean hasInitial = lines.stream().anyMatch(l -> l.startsWith("initial."));
        if (hasInitial) {
            BuildingPlan initial = new BuildingPlan(bps, "initial");
            initial.planIndex = 0;
            ParametersManager.initDefaults(initial, "upgrade");
            ParametersManager.loadPrefixed(lines, "initial", initial, "upgrade", "building plan", bps.key);
            initial.loadPlanImage(buildingsDir);
            bps.plans.add(initial);
            bps.plansByKey.put("initial", initial);
        }

        // Check for upgrade1, upgrade2, etc.
        for (int i = 1; i <= 20; i++) {
            String prefix = "upgrade" + i;
            boolean hasUpgrade = lines.stream().anyMatch(l -> l.startsWith(prefix + "."));
            if (!hasUpgrade) break;

            BuildingPlan upgrade = new BuildingPlan(bps, prefix);
            upgrade.planIndex = i;
            ParametersManager.initDefaults(upgrade, "upgrade");
            ParametersManager.loadPrefixed(lines, prefix, upgrade, "upgrade", "building plan", bps.key);
            upgrade.loadPlanImage(buildingsDir);
            bps.plans.add(upgrade);
            bps.plansByKey.put(prefix, upgrade);
        }
    }

    public BuildingPlan getInitialPlan() {
        return plansByKey.get("initial");
    }

    public BuildingPlan getPlan(int level) {
        if (level < 0 || level >= plans.size()) return null;
        return plans.get(level);
    }

    @Override
    public String toString() {
        return "BuildingPlanSet[" + key + "]";
    }
}
