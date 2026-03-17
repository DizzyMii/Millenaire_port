/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.annotedparameters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.culture.WallType;
import org.millenaire.common.entity.VillagerConfig;
import org.millenaire.common.goal.generic.GoalGeneric;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;

public class ParametersManager {
    private static Map<String, Map<String, List<AnnotedParameter>>> parametersCacheByCategory = new HashMap<String, Map<String, List<AnnotedParameter>>>();
    private static Map<String, Map<String, AnnotedParameter>> parametersCache = new HashMap<String, Map<String, AnnotedParameter>>();

    private static void generateAnnotedParametersHelp(String directoryName, String fileName, Class targetClass, String fieldCategory, boolean recursive, String explanations) {
        File directory = directoryName != null ? new File(MillCommonUtilities.getMillenaireHelpDir(), directoryName) : MillCommonUtilities.getMillenaireHelpDir();
        directory.mkdirs();
        File file = new File(directory, fileName);
        try {
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            writer.write(explanations + "\n" + "\n");
            Map<String, List<AnnotedParameter>> parametersByExplanationCategory = null;
            if (recursive) {
                for (Class currentClass = targetClass; currentClass != null; currentClass = currentClass.getSuperclass()) {
                    Map<String, List<AnnotedParameter>> parametersByExplanationCategoryTemp = ParametersManager.getParametersByExplanationCategory(currentClass, fieldCategory);
                    if (parametersByExplanationCategory == null) {
                        parametersByExplanationCategory = parametersByExplanationCategoryTemp;
                        continue;
                    }
                    for (String key : parametersByExplanationCategoryTemp.keySet()) {
                        if (!parametersByExplanationCategory.containsKey(key)) {
                            parametersByExplanationCategory.put(key, parametersByExplanationCategoryTemp.get(key));
                            continue;
                        }
                        parametersByExplanationCategory.get(key).addAll((Collection<AnnotedParameter>)parametersByExplanationCategoryTemp.get(key));
                    }
                }
            } else {
                parametersByExplanationCategory = ParametersManager.getParametersByExplanationCategory(targetClass, fieldCategory);
            }
            for (String category : parametersByExplanationCategory.keySet()) {
                if (category.length() > 0) {
                    writer.write("\n=== " + category + " ===" + "\n" + "\n");
                }
                for (AnnotedParameter parameter : parametersByExplanationCategory.get(category)) {
                    writer.write(parameter.configName + " (" + parameter.type.io.description + "):" + "\n");
                    writer.write(parameter.explanation + "\n");
                    if (parameter.defaultValueString != null) {
                        writer.write("Default value: " + parameter.defaultValueString + "\n");
                    }
                    writer.write("\n");
                }
                writer.write("\n");
            }
            writer.close();
        }
        catch (Exception e) {
            MillLog.printException(e);
        }
    }

    public static void generateHelpFiles() {
        ParametersManager.generateAnnotedParametersHelp(null, "Cultures.txt", Culture.class, null, false, "List of valid parameters for the culture files.");
        ParametersManager.generateAnnotedParametersHelp(null, "Village Types.txt", VillageType.class, null, false, "List of valid parameters for the village files.");
        ParametersManager.generateAnnotedParametersHelp(null, "Wall Types.txt", WallType.class, null, false, "List of valid parameters for the wall files.");
        ParametersManager.generateAnnotedParametersHelp(null, "Villager Types.txt", VillagerType.class, null, false, "List of valid parameters for the villager files.");
        ParametersManager.generateAnnotedParametersHelp(null, "Villager Config.txt", VillagerConfig.class, null, false, "Series of parameters for various villager behaviour, such as which tools to use or what armour to wear. Every villager gets the default config applied, plus a specific one if defined.");
        ParametersManager.generateAnnotedParametersHelp(null, "Buildings general parameters.txt", BuildingPlan.class, "init", false, "List of valid parameters for building files that applies for the entire building with the 'building.' prefix.");
        ParametersManager.generateAnnotedParametersHelp(null, "Buildings upgrade parameters.txt", BuildingPlan.class, "upgrade", true, "List of valid parameters for building files that applies for a specific upgrade with the 'initial' or 'upgradeX' prefixes.");
        ParametersManager.generateAnnotedParametersHelp("goals", "all goal parameters.txt", GoalGeneric.class, null, true, "List of parameters usable in all generic goals:");
        for (Class genericGoalClass : GoalGeneric.GENERIC_GOAL_CLASSES) {
            try {
                String goalType = (String)genericGoalClass.getField("GOAL_TYPE").get(null);
                if (!MillConfigValues.generateHelpData) continue;
                ParametersManager.generateAnnotedParametersHelp("goals", goalType + " goal parameters.txt", genericGoalClass, null, false, "List of parameters usable in " + goalType + " goals:");
            }
            catch (Exception e) {
                MillLog.printException("Exception when generating goal help files:", e);
            }
        }
    }

    private static String getCacheKey(Class targetClass, String fieldCategory) {
        String cacheKey = targetClass.getCanonicalName();
        if (fieldCategory != null) {
            cacheKey = cacheKey + "_" + fieldCategory;
        }
        return cacheKey;
    }

    private static Map<String, AnnotedParameter> getGenericParametersForTarget(Object target, String fieldCategory) {
        Map<String, AnnotedParameter> parameters = null;
        for (Class<?> targetClass = target.getClass(); targetClass != null; targetClass = targetClass.getSuperclass()) {
            Map<String, AnnotedParameter> parametersTemp = ParametersManager.getParameters(targetClass, fieldCategory);
            if (parameters == null) {
                parameters = parametersTemp;
                continue;
            }
            parameters.putAll(parametersTemp);
        }
        return parameters;
    }

    private static Map<String, AnnotedParameter> getParameters(Class targetClass, String fieldCategory) {
        ParametersManager.loadAnnotedParameters(targetClass, fieldCategory);
        String cacheKey = ParametersManager.getCacheKey(targetClass, fieldCategory);
        return parametersCache.get(cacheKey);
    }

    private static Map<String, List<AnnotedParameter>> getParametersByExplanationCategory(Class targetClass, String fieldCategory) {
        ParametersManager.loadAnnotedParameters(targetClass, fieldCategory);
        String cacheKey = ParametersManager.getCacheKey(targetClass, fieldCategory);
        return parametersCacheByCategory.get(cacheKey);
    }

    public static void initAnnotedParameterData(Object target, Object previousTarget, String fieldCategory, Culture culture) {
        Map<String, AnnotedParameter> parameters = ParametersManager.getGenericParametersForTarget(target, fieldCategory);
        if (previousTarget == null) {
            for (AnnotedParameter param : parameters.values()) {
                if (param.defaultValueString == null) continue;
                param.parseValue(culture, target, param.defaultValueString);
            }
        } else {
            for (AnnotedParameter param : parameters.values()) {
                try {
                    Object previousValue = param.field.get(previousTarget);
                    if (previousValue == null) continue;
                    try {
                        param.field.setAccessible(true);
                        if (previousValue instanceof ArrayList) {
                            param.field.set(target, new ArrayList((List)previousValue));
                            continue;
                        }
                        if (previousValue instanceof HashMap) {
                            param.field.set(target, new HashMap((HashMap)previousValue));
                            continue;
                        }
                        param.field.set(target, previousValue);
                    }
                    catch (IllegalAccessException | IllegalArgumentException e) {
                        MillLog.printException(e);
                    }
                }
                catch (IllegalAccessException | IllegalArgumentException e1) {
                    MillLog.printException(e1);
                }
            }
        }
    }

    public static Object loadAnnotedParameterData(File file, Object target, String fieldCategory, String fileType, Culture culture) {
        Map<String, AnnotedParameter> parameters = ParametersManager.getGenericParametersForTarget(target, fieldCategory);
        for (AnnotedParameter param : parameters.values()) {
            if (param.defaultValueString == null) continue;
            param.parseValue(culture, target, param.defaultValueString);
        }
        if (target instanceof DefaultValueOverloaded) {
            ((DefaultValueOverloaded)target).applyDefaultSettings();
        }
        boolean oldSeparatorWarning = false;
        try {
            String line;
            BufferedReader reader = MillCommonUtilities.getReader(file);
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() <= 0 || line.startsWith("//")) continue;
                String[] temp = line.split("=");
                if (temp.length < 2 && (temp = line.split(":")).length >= 2) {
                    oldSeparatorWarning = true;
                }
                if (temp.length < 2) {
                    MillLog.error(null, "Invalid line when loading " + fileType + ": " + file.getName() + ": " + line);
                    continue;
                }
                String key = temp[0].trim().toLowerCase();
                String value = line.substring(key.length() + 1, line.length());
                if (parameters.containsKey(key)) {
                    parameters.get(key).parseValue(culture, target, value);
                    continue;
                }
                MillLog.error(null, "Unknown line in " + fileType + ": " + file.getName() + ": " + line);
            }
            reader.close();
        }
        catch (Exception e) {
            MillLog.printException(e);
            return null;
        }
        if (oldSeparatorWarning) {
            MillLog.temp(target, "File " + file.getAbsolutePath() + " has legacy separator ( ':' instead of '='). It was loaded but it should be converted to the new separator.");
        }
        return target;
    }

    private static void loadAnnotedParameters(Class targetClass, String fieldCategory) {
        String cacheKey = ParametersManager.getCacheKey(targetClass, fieldCategory);
        if (parametersCache.containsKey(cacheKey)) {
            return;
        }
        LinkedHashMap parametersByExplanationCategory = new LinkedHashMap();
        for (Field field : targetClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ConfigAnnotations.ConfigField.class)) continue;
            boolean shouldInclude = true;
            if (fieldCategory != null) {
                String currentFieldCategory = field.getAnnotation(ConfigAnnotations.ConfigField.class).fieldCategory();
                shouldInclude = fieldCategory.equals(currentFieldCategory);
            }
            if (!shouldInclude) continue;
            String explanationCategory = "";
            if (field.isAnnotationPresent(ConfigAnnotations.FieldDocumentation.class)) {
                explanationCategory = field.getAnnotation(ConfigAnnotations.FieldDocumentation.class).explanationCategory();
            }
            if (!parametersByExplanationCategory.containsKey(explanationCategory)) {
                parametersByExplanationCategory.put(explanationCategory, new ArrayList());
            }
            ((List)parametersByExplanationCategory.get(explanationCategory)).add(new AnnotedParameter(field));
        }
        HashMap<String, AnnotedParameter> parametersMap = new HashMap<String, AnnotedParameter>();
        for (List parameters : parametersByExplanationCategory.values()) {
            for (AnnotedParameter param : parameters) {
                if (parametersMap.containsKey(param.configName)) {
                    MillLog.error(targetClass, "Parameter present twice: " + param.configName);
                }
                parametersMap.put(param.configName, param);
            }
        }
        parametersCacheByCategory.put(cacheKey, parametersByExplanationCategory);
        parametersCache.put(cacheKey, parametersMap);
    }

    public static void loadPrefixedAnnotedParameterData(List<String> lines, String prefix, Object target, String fieldCategory, String fileType, String fileName, Culture culture) {
        Map<String, AnnotedParameter> parameters = ParametersManager.getGenericParametersForTarget(target, fieldCategory);
        try {
            for (String line : lines) {
                if (line.trim().length() <= 0 || !line.startsWith(prefix + ".")) continue;
                String[] temp = line.split("=");
                if (temp.length < 2) {
                    MillLog.error(null, "Invalid line when loading " + fileType + ": " + fileName + ": " + line);
                    continue;
                }
                String key = temp[0].trim().toLowerCase().split("\\.")[1];
                String value = line.substring(temp[0].length() + 1, line.length());
                if (parameters.containsKey(key)) {
                    parameters.get(key).parseValue(culture, target, value);
                    continue;
                }
                MillLog.error(null, "Unknown line in " + fileType + ": " + fileName + ": " + line);
            }
        }
        catch (Exception e) {
            MillLog.printException(e);
        }
    }

    public static void writeAnnotedParameterFile(File file, Object target, String fieldCategory) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                System.err.println("Could not create file at " + file.getAbsolutePath());
            }
        }
        try {
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            ParametersManager.writeAnnotedParameters(writer, target, fieldCategory, null, null);
            writer.close();
        }
        catch (Exception e) {
            MillLog.printException(e);
        }
    }

    public static int writeAnnotedParameters(BufferedWriter writer, Object target, String fieldCategory, Object previousTarget, String prefix) throws Exception {
        Map<String, List<AnnotedParameter>> parametersByCategory = ParametersManager.getParametersByExplanationCategory(target.getClass(), fieldCategory);
        int linesWritten = 0;
        for (String category : parametersByCategory.keySet()) {
            for (AnnotedParameter param : parametersByCategory.get(category)) {
                Object valueToWrite;
                ArrayList value = param.field.get(target);
                if (value == null) {
                    valueToWrite = null;
                } else if (previousTarget == null) {
                    List<String> valuesToWrite;
                    valueToWrite = param.defaultValueString != null ? ((valuesToWrite = param.type.io.writeValue(param.field.get(target))).size() == 1 && param.defaultValueString.equals(valuesToWrite.get(0)) ? null : value) : (!param.type.io.skipWritingValue(value) ? value : null);
                } else {
                    Object oldValue = param.field.get(previousTarget);
                    if (oldValue == null) {
                        valueToWrite = value;
                    } else if (((Object)value).equals(oldValue)) {
                        valueToWrite = null;
                    } else if (value instanceof List) {
                        ArrayList newList = new ArrayList(value);
                        newList.removeAll((List)oldValue);
                        valueToWrite = newList;
                    } else if (value instanceof Map) {
                        HashMap newMap = new HashMap((Map)((Object)value));
                        Map previousMap = (Map)oldValue;
                        for (Object key : previousMap.keySet()) {
                            if (!newMap.containsKey(key) || !newMap.get(key).equals(previousMap.get(key))) continue;
                            newMap.remove(key);
                        }
                        valueToWrite = newMap;
                    } else {
                        valueToWrite = value;
                    }
                }
                if (valueToWrite == null) continue;
                List<String> values = param.type.io.writeValue(valueToWrite);
                for (String v : values) {
                    if (prefix != null) {
                        writer.write(prefix + ".");
                    }
                    writer.write(param.configName + "=" + v + "\n");
                    ++linesWritten;
                }
            }
        }
        return linesWritten;
    }

    public static interface DefaultValueOverloaded {
        public void applyDefaultSettings();
    }
}

