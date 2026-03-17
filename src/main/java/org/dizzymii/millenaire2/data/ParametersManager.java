package org.dizzymii.millenaire2.data;

import org.dizzymii.millenaire2.data.ConfigAnnotations.ConfigField;
import org.dizzymii.millenaire2.data.ConfigAnnotations.FieldDocumentation;
import org.dizzymii.millenaire2.data.ConfigAnnotations.ParameterType;
import org.dizzymii.millenaire2.util.MillCommonUtilities;
import org.dizzymii.millenaire2.util.MillLog;

import java.io.BufferedReader;
import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Annotation-driven data file loader.
 * Reads key=value .txt files and maps values to annotated fields on target objects.
 * This is the backbone of Millénaire's data-driven content system.
 *
 * Ported from org.millenaire.common.annotedparameters.ParametersManager.
 */
public class ParametersManager {

    private static final Map<String, Map<String, AnnotedParam>> parametersCache = new HashMap<>();
    private static final Map<String, Map<String, List<AnnotedParam>>> parametersCacheByCategory = new HashMap<>();

    /**
     * Represents a single annotated parameter discovered via reflection.
     */
    public static class AnnotedParam {
        public final Field field;
        public final ParameterType type;
        public final String configName;
        public final String defaultValueString;
        public final String explanation;
        public final String explanationCategory;

        public AnnotedParam(Field field) {
            this.field = field;
            ConfigField cf = field.getAnnotation(ConfigField.class);
            String name = cf.paramName().toLowerCase();
            if (name.isEmpty()) {
                name = field.getName().toLowerCase();
            }
            this.configName = name;
            this.type = cf.type();
            String dv = cf.defaultValue();
            this.defaultValueString = dv.isEmpty() ? null : dv;

            if (field.isAnnotationPresent(FieldDocumentation.class)) {
                FieldDocumentation doc = field.getAnnotation(FieldDocumentation.class);
                this.explanation = doc.explanation().isEmpty() ? null : doc.explanation();
                this.explanationCategory = doc.explanationCategory();
            } else {
                this.explanation = null;
                this.explanationCategory = "";
            }
        }

        /**
         * Parse a string value from a data file and set the target field.
         */
        @SuppressWarnings("unchecked")
        public void parseValue(Object target, String value) {
            value = value.trim();
            try {
                switch (this.type) {
                    case STRING, STRINGDISPLAY -> field.set(target, value);
                    case BOOLEAN -> field.set(target, MillCommonUtilities.safeParseBoolean(value));
                    case INTEGER, MILLISECONDS -> field.set(target, MillCommonUtilities.safeParseInt(value, 0));
                    case FLOAT -> field.set(target, (float) MillCommonUtilities.safeParseDouble(value, 0.0));
                    case STRING_LIST -> {
                        List<String> list = new ArrayList<>();
                        Collections.addAll(list, value.split(","));
                        field.set(target, list);
                    }
                    case STRING_ADD, STRING_CASE_SENSITIVE_ADD -> {
                        Object current = field.get(target);
                        if (current instanceof List) {
                            ((List<String>) current).add(value);
                        }
                    }
                    case STRING_INTEGER_ADD -> {
                        String[] parts = value.split(",");
                        if (parts.length >= 2) {
                            Object current = field.get(target);
                            if (current instanceof Map) {
                                ((Map<String, Integer>) current).put(
                                        parts[0].trim().toLowerCase(),
                                        MillCommonUtilities.safeParseInt(parts[1].trim(), 0)
                                );
                            }
                        }
                    }
                    case TRANSLATED_STRING_ADD -> {
                        String[] parts = value.split(",", 2);
                        if (parts.length == 2) {
                            Object current = field.get(target);
                            if (current instanceof Map) {
                                ((Map<String, String>) current).put(
                                        parts[0].trim().toLowerCase(),
                                        parts[1].trim()
                                );
                            }
                        }
                    }
                    case INTEGER_ARRAY -> {
                        String[] parts = value.split(",");
                        int[] arr = new int[parts.length];
                        for (int i = 0; i < parts.length; i++) {
                            arr[i] = MillCommonUtilities.safeParseInt(parts[i].trim(), 0);
                        }
                        field.set(target, arr);
                    }
                    case RESOURCE_LOCATION -> field.set(target, value.trim().toLowerCase());
                    case INVITEM, INVITEM_ADD, INVITEM_PAIR, INVITEM_NUMBER_ADD, INVITEM_PRICE_ADD,
                         STRING_INVITEM_ADD, ITEMSTACK_ARRAY -> {
                        // These require InvItem resolution — store raw string for now,
                        // will be resolved when the InvItem registry is loaded.
                        storeRawValue(target, field, value);
                    }
                    case ENTITY_ID -> field.set(target, value.trim().toLowerCase());
                    case BLOCK_ID, BLOCKSTATE, BLOCKSTATE_ADD -> {
                        storeRawValue(target, field, value);
                    }
                    case BONUS_ITEM_ADD, STARTING_ITEM_ADD -> {
                        storeRawValue(target, field, value);
                    }
                    case POS_TYPE -> field.set(target, value.trim().toLowerCase());
                    case GOAL_ADD -> {
                        Object current = field.get(target);
                        if (current instanceof List) {
                            ((List<String>) current).add(value.trim());
                        }
                    }
                    case TOOLCATEGORIES_ADD -> {
                        Object current = field.get(target);
                        if (current instanceof List) {
                            ((List<String>) current).add(value.trim().toLowerCase());
                        }
                    }
                    case GENDER -> field.set(target, value.trim().toLowerCase());
                    case DIRECTION -> field.set(target, value.trim().toLowerCase());
                    case CLOTHES -> {
                        storeRawValue(target, field, value);
                    }
                    case VILLAGERCONFIG -> field.set(target, value.trim().toLowerCase());
                    case BUILDING, BUILDING_ADD, BUILDINGCUSTOM, BUILDINGCUSTOM_ADD,
                         VILLAGER_ADD, SHOP, WALL_TYPE -> {
                        storeRawValue(target, field, value);
                    }
                    case RANDOM_BRICK_COLOUR_ADD, BRICK_COLOUR_THEME_ADD -> {
                        storeRawValue(target, field, value);
                    }
                }
            } catch (Exception e) {
                MillLog.error(target, "Error parsing value '" + value + "' for param " + configName + ": " + e.getMessage());
            }
        }

        /**
         * Store raw string value for complex types that need deferred resolution.
         */
        @SuppressWarnings("unchecked")
        private void storeRawValue(Object target, Field field, String value) throws IllegalAccessException {
            Object current = field.get(target);
            if (current instanceof List) {
                ((List<String>) current).add(value.trim());
            } else if (current instanceof Map) {
                // For maps, try to parse key,value
                String[] parts = value.split(",", 2);
                if (parts.length >= 2) {
                    ((Map<String, String>) current).put(parts[0].trim(), parts[1].trim());
                }
            } else {
                field.set(target, value.trim());
            }
        }
    }

    /**
     * Initialize annotated parameter defaults on a target object.
     */
    public static void initDefaults(Object target, String fieldCategory) {
        Map<String, AnnotedParam> params = getParametersForTarget(target, fieldCategory);
        for (AnnotedParam param : params.values()) {
            if (param.defaultValueString != null) {
                param.parseValue(target, param.defaultValueString);
            }
        }
        if (target instanceof DefaultValueOverloaded dvo) {
            dvo.applyDefaultSettings();
        }
    }

    /**
     * Load data from a .txt file into an annotated target object.
     * Returns the target, or null on error.
     */
    public static Object loadFromFile(File file, Object target, String fieldCategory, String fileType) {
        initDefaults(target, fieldCategory);

        Map<String, AnnotedParam> params = getParametersForTarget(target, fieldCategory);
        boolean oldSeparatorWarning = false;

        try (BufferedReader reader = MillCommonUtilities.getReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("//")) continue;

                String[] temp = line.split("=", 2);
                if (temp.length < 2) {
                    temp = line.split(":", 2);
                    if (temp.length >= 2) {
                        oldSeparatorWarning = true;
                    }
                }
                if (temp.length < 2) {
                    MillLog.error(null, "Invalid line in " + fileType + ": " + file.getName() + ": " + line);
                    continue;
                }

                String key = temp[0].trim().toLowerCase();
                String value = temp[1];

                if (params.containsKey(key)) {
                    params.get(key).parseValue(target, value);
                } else {
                    MillLog.warn(null, "Unknown param in " + fileType + " " + file.getName() + ": " + key);
                }
            }
        } catch (Exception e) {
            MillLog.error(null, "Error loading " + fileType + " from " + file.getName(), e);
            return null;
        }

        if (oldSeparatorWarning) {
            MillLog.warn(target, "File " + file.getName() + " uses legacy ':' separator. Please convert to '='.");
        }

        return target;
    }

    /**
     * Load prefixed parameters from pre-read lines.
     * Used for building files where lines like "initial.key=value" or "upgrade1.key=value" target different objects.
     */
    public static void loadPrefixed(List<String> lines, String prefix, Object target, String fieldCategory, String fileType, String fileName) {
        Map<String, AnnotedParam> params = getParametersForTarget(target, fieldCategory);
        for (String line : lines) {
            if (line.trim().isEmpty() || !line.startsWith(prefix + ".")) continue;

            String[] temp = line.split("=", 2);
            if (temp.length < 2) {
                MillLog.error(null, "Invalid line in " + fileType + " " + fileName + ": " + line);
                continue;
            }

            String key = temp[0].trim().toLowerCase();
            // Remove prefix: "initial.someparam" -> "someparam"
            String[] keyParts = key.split("\\.", 2);
            if (keyParts.length < 2) continue;
            key = keyParts[1];

            String value = temp[1];

            if (params.containsKey(key)) {
                params.get(key).parseValue(target, value);
            } else {
                MillLog.warn(null, "Unknown prefixed param in " + fileType + " " + fileName + ": " + key);
            }
        }
    }

    // --- Internal cache management ---

    private static Map<String, AnnotedParam> getParametersForTarget(Object target, String fieldCategory) {
        Map<String, AnnotedParam> result = new HashMap<>();
        for (Class<?> clazz = target.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            Map<String, AnnotedParam> classParams = getParameters(clazz, fieldCategory);
            if (classParams != null) {
                // Subclass params take precedence
                classParams.forEach(result::putIfAbsent);
            }
        }
        return result;
    }

    private static Map<String, AnnotedParam> getParameters(Class<?> targetClass, String fieldCategory) {
        String cacheKey = getCacheKey(targetClass, fieldCategory);
        if (!parametersCache.containsKey(cacheKey)) {
            loadAnnotedParameters(targetClass, fieldCategory);
        }
        return parametersCache.get(cacheKey);
    }

    private static String getCacheKey(Class<?> targetClass, String fieldCategory) {
        String key = targetClass.getCanonicalName();
        if (fieldCategory != null) {
            key = key + "_" + fieldCategory;
        }
        return key;
    }

    private static void loadAnnotedParameters(Class<?> targetClass, String fieldCategory) {
        String cacheKey = getCacheKey(targetClass, fieldCategory);
        if (parametersCache.containsKey(cacheKey)) return;

        LinkedHashMap<String, List<AnnotedParam>> byCategory = new LinkedHashMap<>();
        HashMap<String, AnnotedParam> byName = new HashMap<>();

        for (Field field : targetClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ConfigField.class)) continue;

            if (fieldCategory != null) {
                String fc = field.getAnnotation(ConfigField.class).fieldCategory();
                if (!fieldCategory.equals(fc)) continue;
            }

            AnnotedParam param = new AnnotedParam(field);
            String expCat = param.explanationCategory;

            byCategory.computeIfAbsent(expCat, k -> new ArrayList<>()).add(param);

            if (byName.containsKey(param.configName)) {
                MillLog.error(targetClass, "Duplicate parameter: " + param.configName);
            }
            byName.put(param.configName, param);
        }

        parametersCacheByCategory.put(cacheKey, byCategory);
        parametersCache.put(cacheKey, byName);
    }

    /**
     * Interface for objects that need to apply additional default settings after annotation defaults.
     */
    public interface DefaultValueOverloaded {
        void applyDefaultSettings();
    }
}
