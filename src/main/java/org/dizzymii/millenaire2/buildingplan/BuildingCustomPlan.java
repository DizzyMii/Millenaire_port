package org.dizzymii.millenaire2.buildingplan;

import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.VirtualDir;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom building plan loaded from culture data files.
 * Defines residents, resources, tags, and building dimensions.
 * Ported from org.millenaire.common.buildingplan.BuildingCustomPlan (Forge 1.12.2).
 */
public class BuildingCustomPlan implements IBuildingPlan {

    public final Culture culture;
    public String nativeName;
    public String shop = null;
    public String buildingKey;
    public String gameNameKey = null;
    public final Map<String, String> names = new HashMap<>();
    public List<String> maleResident = new ArrayList<>();
    public List<String> femaleResident = new ArrayList<>();
    public List<String> visitors = new ArrayList<>();
    public int priorityMoveIn = 1;
    public int radius = 6;
    public int heightRadius = 4;
    public List<String> tags = new ArrayList<>();
    public ResourceLocation cropType = null;
    public ResourceLocation spawnType = null;

    public BuildingCustomPlan(Culture culture, String buildingKey) {
        this.culture = culture;
        this.buildingKey = buildingKey;
        this.nativeName = buildingKey;
    }

    @Override
    public Culture getCulture() { return culture; }

    @Override
    public List<String> getFemaleResident() { return femaleResident; }

    @Override
    public List<String> getMaleResident() { return maleResident; }

    @Override
    public String getNameTranslated() {
        // Translated name lookup via LanguageUtilities; falls back to native name
        if (gameNameKey != null) {
            String translated = org.dizzymii.millenaire2.util.LanguageUtilities.string(gameNameKey);
            if (!translated.isEmpty() && !translated.equals(gameNameKey)) return translated;
        }
        return nativeName;
    }

    @Override
    public String getNativeName() { return nativeName; }

    @Override
    public List<String> getVisitors() { return visitors; }

    /**
     * Load all custom building plans from a culture's "buildings" directory.
     * Each .txt file defines one custom building.
     */
    public static List<BuildingCustomPlan> loadCustomBuildings(VirtualDir buildingsDir, Culture culture) {
        List<BuildingCustomPlan> plans = new ArrayList<>();
        if (buildingsDir == null || !buildingsDir.exists()) return plans;

        List<File> txtFiles = buildingsDir.listFiles(new BuildingFileFiler(".txt"));
        for (File file : txtFiles) {
            BuildingCustomPlan plan = loadFromFile(file, culture);
            if (plan != null) {
                plans.add(plan);
            }
        }
        MillLog.minor(null, "Loaded " + plans.size() + " custom buildings for " + culture.key);
        return plans;
    }

    /**
     * Load a single custom building plan from a .txt metadata file.
     */
    public static BuildingCustomPlan loadFromFile(File file, Culture culture) {
        try {
            String key = file.getName().replace(".txt", "").toLowerCase();
            BuildingCustomPlan plan = new BuildingCustomPlan(culture, key);
            Map<String, String> meta = BuildingMetadataLoader.loadMetadata(file);
            BuildingMetadataLoader.applyMetadata(plan, meta);
            return plan;
        } catch (Exception e) {
            MillLog.error(null, "Failed to load custom building: " + file.getName(), e);
            return null;
        }
    }
}
