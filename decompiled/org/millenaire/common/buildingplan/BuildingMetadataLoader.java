/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.buildingplan;

import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.millenaire.common.annotedparameters.ParametersManager;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;

public class BuildingMetadataLoader {
    private static boolean EXPORT_AND_REPLACE = false;
    List<String> lines = new ArrayList<String>();
    boolean legacyFile = false;

    public static void exportAllBuildingPlansTextFiles() {
        int count = 0;
        for (Culture culture : Culture.ListCultures) {
            for (BuildingPlanSet planSet : culture.ListPlanSets) {
                BuildingMetadataLoader.exportBuildingPlanTextFile(planSet);
                ++count;
            }
        }
        MillLog.major(null, "Exported " + count + " plans to the new format.");
    }

    private static void exportBuildingPlanTextFile(BuildingPlanSet planSet) {
        File exportDirectory = new File(new File(MillCommonUtilities.getMillenaireCustomContentDir(), "Converted Buildings"), planSet.culture.key);
        exportDirectory.mkdirs();
        for (int variation = 0; variation < planSet.plans.size(); ++variation) {
            File file = !EXPORT_AND_REPLACE ? new File(exportDirectory, planSet.key + "_" + (char)(65 + variation) + ".txt") : new File(planSet.mainFile.getParentFile(), planSet.key + "_" + (char)(65 + variation) + ".txt");
            file.delete();
            try {
                BufferedWriter writer = MillCommonUtilities.getAppendWriter(file);
                writer.write("//Parameters for the building as a whole\n");
                ParametersManager.writeAnnotedParameters(writer, planSet.plans.get(variation)[0], "init", null, "building");
                writer.write("\n");
                BuildingPlan previousPlan = null;
                for (int level = 0; level < planSet.plans.get(variation).length; ++level) {
                    BuildingPlan plan;
                    int result;
                    String prefix;
                    if (level == 0) {
                        writer.write("//Parameters for initial construction\n");
                        prefix = "initial";
                    } else {
                        prefix = "upgrade" + level;
                    }
                    if (level == 1) {
                        writer.write("//Parameters for specific upgrades\n");
                    }
                    if ((result = ParametersManager.writeAnnotedParameters(writer, plan = planSet.plans.get(variation)[level], "upgrade", previousPlan, prefix)) > 0) {
                        writer.write("\n");
                    }
                    previousPlan = plan;
                }
                writer.close();
                continue;
            }
            catch (Exception e) {
                MillLog.printException(e);
            }
        }
    }

    private static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }

    private static void validateBuildingPlan(BuildingPlan buildingPlan) {
        if (buildingPlan.culture != null) {
            for (String maleVillager : buildingPlan.maleResident) {
                if (buildingPlan.culture.villagerTypes.get((Object)maleVillager).gender != 2) continue;
                MillLog.error(buildingPlan, "Attempted to add a female villager using the 'male' tag: " + maleVillager);
            }
            for (String femaleVillager : buildingPlan.femaleResident) {
                if (buildingPlan.culture.villagerTypes.get((Object)femaleVillager).gender != 1) continue;
                MillLog.error(buildingPlan, "Attempted to add a male villager using the 'female' tag: " + femaleVillager);
            }
        }
    }

    public BuildingMetadataLoader(List<String> lines) {
        this.lines = lines;
        if (lines.size() > 0 && lines.get(0).contains("length:")) {
            this.legacyFile = true;
        }
    }

    private void initParametersPostHandling(BuildingPlan plan) {
        if (plan.areaToClearLengthBefore == -1) {
            plan.areaToClearLengthBefore = plan.areaToClear;
        }
        if (plan.areaToClearLengthAfter == -1) {
            plan.areaToClearLengthAfter = plan.areaToClear;
        }
        if (plan.areaToClearWidthBefore == -1) {
            plan.areaToClearWidthBefore = plan.areaToClear;
        }
        if (plan.areaToClearWidthAfter == -1) {
            plan.areaToClearWidthAfter = plan.areaToClear;
        }
    }

    private void legacyInitialisePlanConfig(BuildingPlan buildingPlan, BuildingPlan previousUpgradePlan) {
        if (previousUpgradePlan == null) {
            buildingPlan.max = 1;
            buildingPlan.priority = 1;
            buildingPlan.priorityMoveIn = 10;
            buildingPlan.areaToClear = 1;
            buildingPlan.startLevel = 0;
            buildingPlan.buildingOrientation = 1;
            buildingPlan.minDistance = 0.0f;
            buildingPlan.maxDistance = 1.0f;
            buildingPlan.reputation = 0;
            buildingPlan.price = 0;
            buildingPlan.showTownHallSigns = true;
        } else {
            buildingPlan.max = previousUpgradePlan.max;
            buildingPlan.priority = previousUpgradePlan.priority;
            buildingPlan.priorityMoveIn = previousUpgradePlan.priorityMoveIn;
            buildingPlan.nativeName = previousUpgradePlan.nativeName;
            buildingPlan.areaToClear = previousUpgradePlan.areaToClear;
            buildingPlan.startLevel = previousUpgradePlan.startLevel;
            buildingPlan.buildingOrientation = previousUpgradePlan.buildingOrientation;
            buildingPlan.signOrder = previousUpgradePlan.signOrder;
            buildingPlan.tags = new ArrayList<String>(previousUpgradePlan.tags);
            buildingPlan.villageTags = new ArrayList<String>(previousUpgradePlan.villageTags);
            buildingPlan.parentTags = new ArrayList<String>(previousUpgradePlan.parentTags);
            buildingPlan.requiredTags = new ArrayList<String>(previousUpgradePlan.requiredTags);
            buildingPlan.requiredParentTags = new ArrayList<String>(previousUpgradePlan.requiredParentTags);
            buildingPlan.requiredVillageTags = new ArrayList<String>(previousUpgradePlan.requiredVillageTags);
            buildingPlan.farFromTag = new HashMap<String, Integer>(previousUpgradePlan.farFromTag);
            buildingPlan.maleResident = previousUpgradePlan.maleResident;
            buildingPlan.femaleResident = previousUpgradePlan.femaleResident;
            buildingPlan.shop = previousUpgradePlan.shop;
            buildingPlan.width = previousUpgradePlan.width;
            buildingPlan.length = previousUpgradePlan.length;
            buildingPlan.minDistance = previousUpgradePlan.minDistance;
            buildingPlan.maxDistance = previousUpgradePlan.maxDistance;
            buildingPlan.reputation = previousUpgradePlan.reputation;
            buildingPlan.isgift = previousUpgradePlan.isgift;
            buildingPlan.price = previousUpgradePlan.price;
            buildingPlan.pathLevel = previousUpgradePlan.pathLevel;
            buildingPlan.pathWidth = previousUpgradePlan.pathWidth;
            buildingPlan.subBuildings = new ArrayList<String>(previousUpgradePlan.subBuildings);
            buildingPlan.startingSubBuildings = new ArrayList<String>();
            buildingPlan.startingGoods = new ArrayList<BuildingPlan.StartingGood>();
            buildingPlan.parent = previousUpgradePlan;
            if (MillConfigValues.LogBuildingPlan >= 2) {
                String s = "";
                for (String s2 : buildingPlan.subBuildings) {
                    s = s + s2 + " ";
                }
                if (s.length() > 0) {
                    MillLog.minor(buildingPlan, "Copied sub-buildings from parent: " + s);
                }
            }
            buildingPlan.showTownHallSigns = previousUpgradePlan.showTownHallSigns;
            buildingPlan.exploreTag = previousUpgradePlan.exploreTag;
            buildingPlan.irrigation = previousUpgradePlan.irrigation;
            buildingPlan.isSubBuilding = previousUpgradePlan.isSubBuilding;
            buildingPlan.abstractedProduction = new HashMap<InvItem, Integer>(previousUpgradePlan.abstractedProduction);
        }
    }

    private void legacyReadConfigLine(BuildingPlan buildingPlan, String line, boolean importPlan) {
        String[] configs;
        for (String config : configs = line.split(";", -1)) {
            String[] temp;
            if (config.split(":").length != 2) continue;
            String key = config.split(":")[0].toLowerCase();
            String value = config.split(":")[1];
            if (key.equalsIgnoreCase("max")) {
                buildingPlan.max = Integer.parseInt(value);
                continue;
            }
            if (key.equalsIgnoreCase("priority")) {
                buildingPlan.priority = Integer.parseInt(value);
                continue;
            }
            if (key.equalsIgnoreCase("moveinpriority")) {
                buildingPlan.priorityMoveIn = Integer.parseInt(value);
                continue;
            }
            if (key.equalsIgnoreCase("french") || key.equalsIgnoreCase("native")) {
                buildingPlan.nativeName = value;
                continue;
            }
            if (key.equalsIgnoreCase("english") || key.startsWith("name_")) {
                if (key.equals("english")) {
                    buildingPlan.translatedNames.put("en", value);
                    continue;
                }
                buildingPlan.translatedNames.put(key.split("_")[1], value);
                continue;
            }
            if (key.equalsIgnoreCase("around")) {
                buildingPlan.areaToClear = Integer.parseInt(value);
                continue;
            }
            if (key.equalsIgnoreCase("startLevel")) {
                buildingPlan.startLevel = Integer.parseInt(value);
                continue;
            }
            if (key.equalsIgnoreCase("orientation")) {
                buildingPlan.buildingOrientation = Integer.parseInt(value);
                continue;
            }
            if (key.equalsIgnoreCase("pathlevel")) {
                buildingPlan.pathLevel = Integer.parseInt(value);
                continue;
            }
            if (key.equalsIgnoreCase("rebuildpath")) {
                buildingPlan.rebuildPath = Boolean.parseBoolean(value);
                continue;
            }
            if (key.equalsIgnoreCase("isgift")) {
                buildingPlan.isgift = Boolean.parseBoolean(value);
                continue;
            }
            if (key.equalsIgnoreCase("pathwidth")) {
                buildingPlan.pathWidth = Integer.parseInt(value);
                continue;
            }
            if (key.equalsIgnoreCase("reputation")) {
                try {
                    buildingPlan.reputation = MillCommonUtilities.readInteger(value);
                }
                catch (Exception e) {
                    buildingPlan.reputation = 0;
                    MillLog.error(null, "Error when reading reputation line in " + buildingPlan.getLoadedFromFile().getName() + ": " + line + " : " + e.getMessage());
                }
                continue;
            }
            if (key.equalsIgnoreCase("price")) {
                try {
                    buildingPlan.price = MillCommonUtilities.readInteger(value);
                }
                catch (Exception e) {
                    buildingPlan.price = 0;
                    MillLog.error(buildingPlan, "Error when reading reputation line in " + buildingPlan.getLoadedFromFile().getName() + ": " + line + " : " + e.getMessage());
                }
                continue;
            }
            if (key.equalsIgnoreCase("version")) {
                try {
                    buildingPlan.version = MillCommonUtilities.readInteger(value);
                }
                catch (Exception e) {
                    buildingPlan.version = 0;
                    MillLog.error(buildingPlan, "Error when reading version line in: " + line + " : " + e.getMessage());
                }
                continue;
            }
            if (key.equalsIgnoreCase("length")) {
                buildingPlan.length = Integer.parseInt(value);
                continue;
            }
            if (key.equalsIgnoreCase("width")) {
                buildingPlan.width = Integer.parseInt(value);
                continue;
            }
            if (!importPlan && key.equalsIgnoreCase("male")) {
                if (buildingPlan.culture.villagerTypes.containsKey(value.toLowerCase())) {
                    if (buildingPlan.culture.villagerTypes.get((Object)value.toLowerCase()).gender == 2) {
                        MillLog.error(buildingPlan, "Attempted to add a female villager using the 'male' tag: " + value);
                        continue;
                    }
                    buildingPlan.maleResident.add(value.toLowerCase());
                    continue;
                }
                MillLog.error(buildingPlan, "Attempted to load unknown male villager: " + value);
                continue;
            }
            if (!importPlan && key.equalsIgnoreCase("female")) {
                if (buildingPlan.culture.villagerTypes.containsKey(value.toLowerCase())) {
                    if (buildingPlan.culture.villagerTypes.get((Object)value.toLowerCase()).gender == 1) {
                        MillLog.error(buildingPlan, "Attempted to add a male villager using the 'female' tag: " + value);
                        continue;
                    }
                    buildingPlan.femaleResident.add(value.toLowerCase());
                    continue;
                }
                MillLog.error(buildingPlan, "Attempted to load unknown female villager: " + value);
                continue;
            }
            if (!importPlan && key.equalsIgnoreCase("visitor")) {
                if (buildingPlan.culture.villagerTypes.containsKey(value.toLowerCase())) {
                    buildingPlan.visitors.add(value.toLowerCase());
                    continue;
                }
                MillLog.error(buildingPlan, "Attempted to load unknown visitor: " + value);
                continue;
            }
            if (key.equalsIgnoreCase("exploretag")) {
                buildingPlan.exploreTag = value.toLowerCase();
                continue;
            }
            if (key.equalsIgnoreCase("requiredglobalTag")) {
                buildingPlan.requiredGlobalTag = value.toLowerCase();
                continue;
            }
            if (key.equalsIgnoreCase("irrigation")) {
                buildingPlan.irrigation = Integer.parseInt(value);
                continue;
            }
            if (!importPlan && key.equalsIgnoreCase("shop")) {
                if (buildingPlan.culture == null) continue;
                if (buildingPlan.culture.shopBuys.containsKey(value) || buildingPlan.culture.shopSells.containsKey(value) || buildingPlan.culture.shopBuysOptional.containsKey(value)) {
                    buildingPlan.shop = value;
                    continue;
                }
                MillLog.error(buildingPlan, "Undefined shop type: " + value);
                continue;
            }
            if (key.equalsIgnoreCase("minDistance")) {
                buildingPlan.minDistance = Float.parseFloat(value) / 100.0f;
                continue;
            }
            if (key.equalsIgnoreCase("maxDistance")) {
                buildingPlan.maxDistance = Float.parseFloat(value) / 100.0f;
                continue;
            }
            if (key.equalsIgnoreCase("fixedorientation")) {
                if (value.equalsIgnoreCase("east")) {
                    buildingPlan.fixedOrientation = 3;
                    continue;
                }
                if (value.equalsIgnoreCase("west")) {
                    buildingPlan.fixedOrientation = 1;
                    continue;
                }
                if (value.equalsIgnoreCase("north")) {
                    buildingPlan.fixedOrientation = 0;
                    continue;
                }
                if (value.equalsIgnoreCase("south")) {
                    buildingPlan.fixedOrientation = 2;
                    continue;
                }
                MillLog.error(buildingPlan, "Unknown fixed orientation: " + value);
                continue;
            }
            if (key.equalsIgnoreCase("signs")) {
                temp = value.split(",");
                if (temp[0].length() <= 0) continue;
                buildingPlan.signOrder = new int[temp.length];
                for (int i = 0; i < temp.length; ++i) {
                    buildingPlan.signOrder[i] = Integer.parseInt(temp[i]);
                }
                continue;
            }
            if (key.equalsIgnoreCase("tag")) {
                buildingPlan.tags.add(value.toLowerCase());
                continue;
            }
            if (key.equalsIgnoreCase("villageTag")) {
                buildingPlan.villageTags.add(value.toLowerCase());
                continue;
            }
            if (key.equalsIgnoreCase("parentTag")) {
                buildingPlan.parentTags.add(value.toLowerCase());
                continue;
            }
            if (key.equalsIgnoreCase("requiredTag")) {
                buildingPlan.requiredTags.add(value.toLowerCase());
                continue;
            }
            if (key.equalsIgnoreCase("requiredVillageTag")) {
                buildingPlan.requiredVillageTags.add(value.toLowerCase());
                continue;
            }
            if (key.equalsIgnoreCase("requiredParentTag")) {
                buildingPlan.requiredParentTags.add(value.toLowerCase());
                continue;
            }
            if (key.equalsIgnoreCase("farFromTag")) {
                buildingPlan.farFromTag.put(value.split(",")[0].toLowerCase(), Integer.parseInt(value.split(",")[1]));
                continue;
            }
            if (key.equalsIgnoreCase("subbuilding")) {
                buildingPlan.subBuildings.add(value);
                continue;
            }
            if (key.equalsIgnoreCase("startingsubbuilding")) {
                buildingPlan.startingSubBuildings.add(value);
                continue;
            }
            if (!importPlan && key.equalsIgnoreCase("startinggood")) {
                temp = value.split(",");
                if (temp.length != 4) {
                    MillLog.error(buildingPlan, "Error when reading starting good: expected four fields, found " + temp.length + ": " + value);
                    continue;
                }
                String s = temp[0];
                if (!InvItem.INVITEMS_BY_NAME.containsKey(s)) {
                    MillLog.error(buildingPlan, "Error when reading starting good: unknown good: " + s);
                    continue;
                }
                BuildingPlan.StartingGood sg = new BuildingPlan.StartingGood(InvItem.INVITEMS_BY_NAME.get(s), Double.parseDouble(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
                buildingPlan.startingGoods.add(sg);
                continue;
            }
            if (key.equalsIgnoreCase("type")) {
                if (!value.equalsIgnoreCase("subbuilding")) continue;
                buildingPlan.isSubBuilding = true;
                continue;
            }
            if (key.equalsIgnoreCase("showtownhallsigns")) {
                buildingPlan.showTownHallSigns = Boolean.parseBoolean(value);
                continue;
            }
            if (key.equalsIgnoreCase("abstractedProduction")) {
                if (InvItem.INVITEMS_BY_NAME.containsKey(value.split(",")[0].toLowerCase())) {
                    InvItem iv = InvItem.INVITEMS_BY_NAME.get(value.split(",")[0].toLowerCase());
                    int quantity = Integer.parseInt(value.split(",")[1]);
                    if (iv.meta >= 0) {
                        buildingPlan.abstractedProduction.put(iv, quantity);
                        continue;
                    }
                    MillLog.error(buildingPlan, "Abstracted production goods should not include generic goods like any_wood. Skipping it.");
                    continue;
                }
                MillLog.error(buildingPlan, "Unknown abstracted production good found when loading building plan : " + value);
                continue;
            }
            if (importPlan) continue;
            MillLog.error(buildingPlan, "Could not recognise key on line: " + config);
        }
        if (buildingPlan.isSubBuilding) {
            buildingPlan.max = 0;
        }
        if (buildingPlan.priority < 1) {
            MillLog.error(buildingPlan, "Null or negative weight found in config!");
        }
        if (MillConfigValues.LogBuildingPlan >= 3) {
            String s = "";
            for (String s2 : buildingPlan.subBuildings) {
                s = s + s2 + " ";
            }
            if (s.length() > 0) {
                MillLog.minor(buildingPlan, "Sub-buildings after read: " + s);
            }
        }
    }

    public void loadDataForPlan(BuildingPlan plan, BuildingPlan previousPlan, boolean importPlan) {
        if (this.legacyFile) {
            this.legacyInitialisePlanConfig(plan, previousPlan);
            if (this.lines.size() > plan.level) {
                this.legacyReadConfigLine(plan, this.lines.get(plan.level), importPlan);
            }
        } else {
            ParametersManager.initAnnotedParameterData(plan, previousPlan, "init", plan.culture);
            ParametersManager.initAnnotedParameterData(plan, previousPlan, "upgrade", plan.culture);
            if (plan.level == 0) {
                ParametersManager.loadPrefixedAnnotedParameterData(this.lines, "building", plan, "init", "building", plan.getLoadedFromFile().getName(), plan.getCulture());
                this.initParametersPostHandling(plan);
            }
            String prefix = plan.level == 0 ? "initial" : "upgrade" + plan.level;
            ParametersManager.loadPrefixedAnnotedParameterData(this.lines, prefix, plan, "upgrade", "building", plan.getLoadedFromFile().getName(), plan.getCulture());
            BuildingMetadataLoader.validateBuildingPlan(plan);
        }
    }
}

