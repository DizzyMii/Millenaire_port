package org.dizzymii.millenaire2.buildingplan;

import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.culture.Culture;

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
        // TODO: Look up translated name from LanguageUtilities
        return gameNameKey != null ? gameNameKey : nativeName;
    }

    @Override
    public String getNativeName() { return nativeName; }

    @Override
    public List<String> getVisitors() { return visitors; }

    // TODO: loadCustomBuildings(VirtualDir, Culture), loadFromFile(File), resource maps
}
