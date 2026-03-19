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
 * Defines a type of villager within a culture (farmer, soldier, wife, child, etc.).
 * Loaded from data files like cultures/norman/villagers/normalvillagers/farmer.txt.
 *
 * Ported from org.millenaire.common.culture.VillagerType.
 */
public class VillagerType {

    // --- Tag constants ---
    public static final String TAG_LOCALMERCHANT = "localmerchant";
    public static final String TAG_FOREIGNMERCHANT = "foreignmerchant";
    public static final String TAG_CHILD = "child";
    public static final String TAG_CHIEF = "chief";
    public static final String TAG_SELLER = "seller";
    public static final String TAG_MEDITATES = "meditates";
    public static final String TAG_SACRIFICES = "performssacrifices";
    public static final String TAG_VISITOR = "visitor";
    public static final String TAG_HELPSINATTACKS = "helpinattacks";
    public static final String TAG_HOSTILE = "hostile";
    public static final String TAG_NOLEAFCLEARING = "noleafclearing";
    public static final String TAG_ARCHER = "archer";
    public static final String TAG_RAIDER = "raider";
    public static final String TAG_NOTELEPORT = "noteleport";
    public static final String TAG_HIDENAME = "hidename";
    public static final String TAG_SHOWHEALTH = "showhealth";
    public static final String TAG_DEFENSIVE = "defensive";
    public static final String TAG_NORESURRECT = "noresurrect";

    public String key;
    public Culture culture;

    @ConfigField(type = ParameterType.STRINGDISPLAY, paramName = "native_name")
    @FieldDocumentation(explanation = "Name of the villager in the culture's language.")
    public String name;

    @ConfigField(type = ParameterType.STRINGDISPLAY, paramName = "alt_native_name")
    @FieldDocumentation(explanation = "Alternate name (used for teens).")
    public String altname;

    @ConfigField(type = ParameterType.STRING, paramName = "alt_key")
    @FieldDocumentation(explanation = "Key of the alternate type (used for teens).")
    public String altkey;

    @ConfigField(type = ParameterType.STRING, paramName = "travelbook_category")
    @FieldDocumentation(explanation = "Category in the Travel Book.")
    public String travelBookCategory = null;

    @ConfigField(type = ParameterType.BOOLEAN, paramName = "travelbook_display", defaultValue = "true")
    @FieldDocumentation(explanation = "Whether to display in the Travel Book.")
    public boolean travelBookDisplay = true;

    @ConfigField(type = ParameterType.STRING)
    @FieldDocumentation(explanation = "The list to use for this villager's family name.")
    public String familyNameList;

    @ConfigField(type = ParameterType.STRING)
    @FieldDocumentation(explanation = "The list to use for this villager's given name.")
    public String firstNameList;

    @ConfigField(type = ParameterType.STRING)
    @FieldDocumentation(explanation = "Model to use.")
    public String model = null;

    @ConfigField(type = ParameterType.FLOAT, paramName = "baseheight", defaultValue = "1")
    @FieldDocumentation(explanation = "The villager's height scale (1 = two blocks).")
    public float baseScale = 1.0f;

    @ConfigField(type = ParameterType.FLOAT, paramName = "basespeed", defaultValue = "0.55")
    @FieldDocumentation(explanation = "The villager's speed.")
    public float baseSpeed = 0.55f;

    @ConfigField(type = ParameterType.GENDER)
    @FieldDocumentation(explanation = "Gender of the villager.")
    public String gender;

    @ConfigField(type = ParameterType.STRING)
    @FieldDocumentation(explanation = "Villager type for male offspring.")
    public String maleChild = null;

    @ConfigField(type = ParameterType.STRING)
    @FieldDocumentation(explanation = "Villager type for female offspring.")
    public String femaleChild = null;

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "texture")
    @FieldDocumentation(explanation = "Textures for the villager (random pick).")
    public List<String> textures = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING, paramName = "clothingtexture")
    @FieldDocumentation(explanation = "Overlay clothing texture rendered on top of the base skin.")
    public String clothingTexture = null;

    @ConfigField(type = ParameterType.GOAL_ADD, paramName = "goal")
    @FieldDocumentation(explanation = "Goal a villager can pursue.")
    public List<String> goals = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "bringbackhomegood")
    @FieldDocumentation(explanation = "Items the villager brings home.")
    public List<String> bringBackHomeGoods = new ArrayList<>();

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "collectgood")
    @FieldDocumentation(explanation = "Items the villager collects nearby.")
    public List<String> collectGoods = new ArrayList<>();

    @ConfigField(type = ParameterType.TOOLCATEGORIES_ADD, paramName = "toolneededclass")
    @FieldDocumentation(explanation = "Tool categories needed.")
    public List<String> toolsCategoriesNeeded = new ArrayList<>();

    @ConfigField(type = ParameterType.INTEGER, defaultValue = "1")
    @FieldDocumentation(explanation = "Attack strength.")
    public int baseAttackStrength;

    @ConfigField(type = ParameterType.INTEGER, paramName = "experiencegiven")
    @FieldDocumentation(explanation = "XP given on kill.")
    public int expgiven = 0;

    @ConfigField(type = ParameterType.INTEGER, paramName = "health", defaultValue = "30")
    @FieldDocumentation(explanation = "Health points.")
    public int health;

    @ConfigField(type = ParameterType.INTEGER, paramName = "hiringcost")
    @FieldDocumentation(explanation = "Cost in deniers to hire.")
    public int hireCost;

    @ConfigField(type = ParameterType.INTEGER)
    @FieldDocumentation(explanation = "Weight for random selection.")
    public int chanceWeight = 0;

    @ConfigField(type = ParameterType.STRING_ADD, paramName = "tag")
    @FieldDocumentation(explanation = "Tags controlling special behaviours.")
    public List<String> tags = new ArrayList<>();

    // --- Derived boolean flags (set after loading) ---
    public boolean isChild = false;
    public boolean isChief = false;
    public boolean canSell = false;
    public boolean canMeditate = false;
    public boolean canPerformSacrifices = false;
    public boolean visitor = false;
    public boolean helpInAttacks = false;
    public boolean isLocalMerchant = false;
    public boolean isForeignMerchant = false;
    public boolean hostile = false;
    public boolean isArcher = false;
    public boolean noleafclearing = false;
    public boolean isRaider = false;
    public boolean noTeleport = false;
    public boolean hideName = false;
    public boolean showHealth = false;
    public boolean isDefensive = false;
    public boolean noResurrect = false;

    public VillagerType(Culture culture, String key) {
        this.culture = culture;
        this.key = key;
    }

    public static VillagerType loadFromFile(File file, Culture culture) {
        String key = file.getName().replace(".txt", "").toLowerCase();
        VillagerType vt = new VillagerType(culture, key);
        try {
            Object result = ParametersManager.loadFromFile(file, vt, null, "villager type");
            if (result == null) return null;

            // Derive boolean flags from tags
            vt.isChild = vt.containsTag(TAG_CHILD);
            vt.isChief = vt.containsTag(TAG_CHIEF);
            vt.canSell = vt.containsTag(TAG_SELLER);
            vt.canMeditate = vt.containsTag(TAG_MEDITATES);
            vt.canPerformSacrifices = vt.containsTag(TAG_SACRIFICES);
            vt.visitor = vt.containsTag(TAG_VISITOR);
            vt.helpInAttacks = vt.containsTag(TAG_HELPSINATTACKS);
            vt.isLocalMerchant = vt.containsTag(TAG_LOCALMERCHANT);
            vt.isForeignMerchant = vt.containsTag(TAG_FOREIGNMERCHANT);
            vt.hostile = vt.containsTag(TAG_HOSTILE);
            vt.noleafclearing = vt.containsTag(TAG_NOLEAFCLEARING);
            vt.isArcher = vt.containsTag(TAG_ARCHER);
            vt.isRaider = vt.containsTag(TAG_RAIDER);
            vt.noTeleport = vt.containsTag(TAG_NOTELEPORT);
            vt.hideName = vt.containsTag(TAG_HIDENAME);
            vt.showHealth = vt.containsTag(TAG_SHOWHEALTH);
            vt.isDefensive = vt.containsTag(TAG_DEFENSIVE);
            vt.noResurrect = vt.containsTag(TAG_NORESURRECT);

            // Normalize all goal keys to lowercase for consistent lookup
            if (vt.goals != null) {
                vt.goals.replaceAll(String::toLowerCase);
            }

            MillLog.minor(vt, "Loaded villager type: " + vt.key);
            return vt;
        } catch (Exception e) {
            MillLog.error(null, "Error loading villager type: " + file.getName(), e);
            return null;
        }
    }

    public boolean containsTag(String tag) {
        return tags.contains(tag.toLowerCase());
    }

    public boolean hasChildren() {
        return maleChild != null || femaleChild != null;
    }

    @Override
    public String toString() {
        return "VillagerType[" + key + "]";
    }
}
