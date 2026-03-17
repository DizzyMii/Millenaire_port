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
 * Defines a wall type used in village perimeters.
 * Each culture can define multiple wall styles.
 *
 * Ported from org.millenaire.common.culture.WallType.
 */
public class WallType {

    public String key;
    public Culture culture;

    @ConfigField(type = ParameterType.STRINGDISPLAY)
    @FieldDocumentation(explanation = "Name of the wall type.")
    public String name;

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "wallblock")
    @FieldDocumentation(explanation = "Block IDs used for the wall.")
    public List<String> wallBlocks = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "topblock")
    @FieldDocumentation(explanation = "Block IDs used for the wall top.")
    public List<String> topBlocks = new ArrayList<>();

    @ConfigField(type = ParameterType.INTEGER, defaultValue = "3")
    @FieldDocumentation(explanation = "Height of the wall.")
    public int height = 3;

    @ConfigField(type = ParameterType.INTEGER, defaultValue = "0")
    @FieldDocumentation(explanation = "Generation weight.")
    public int weight = 0;

    public WallType(Culture culture, String key) {
        this.culture = culture;
        this.key = key;
    }

    public static WallType loadFromFile(File file, Culture culture) {
        String key = file.getName().replace(".txt", "").toLowerCase();
        WallType wt = new WallType(culture, key);

        try {
            Object result = ParametersManager.loadFromFile(file, wt, null, "wall type");
            if (result == null) return null;
            MillLog.minor(wt, "Loaded wall type: " + wt.key);
            return wt;
        } catch (Exception e) {
            MillLog.error(null, "Error loading wall type: " + file.getName(), e);
            return null;
        }
    }

    @Override
    public String toString() {
        return "WallType[" + key + "]";
    }
}
