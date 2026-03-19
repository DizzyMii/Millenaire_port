package org.dizzymii.millenaire2.goal.generic;

import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.util.MillCommonUtilities;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-driven goal loaded from config files in goals/genericXxx directories.
 * Ported from org.millenaire.common.goal.generic.GoalGeneric (Forge 1.12.2).
 *
 * Each .txt file defines a goal with parameters parsed at startup.
 * The category (crafting, visit, mining, etc.) determines the runtime behavior.
 */
public class GoalGeneric extends Goal {

    // --- Common parameters loaded from data files ---
    public String buildingTag = null;
    public String requiredTag = null;
    public boolean townhallGoal = false;
    public int goalPriority = 50;
    public int priorityRandom = 10;
    public int durationMs = 5000;
    public int reoccurDelayMs = 5000;
    public int goalRange = 3;
    public String sentenceKey = null;
    public String goalLabelKey = null;
    public List<String> heldItems = new ArrayList<>();
    public List<String> heldItemsOffhand = new ArrayList<>();
    public List<String> heldItemsDestination = new ArrayList<>();
    public boolean allowRandomMovesFlag = false;
    public boolean holdWeapons = false;
    public boolean lookAtGoal = false;
    public String sound = null;
    public String icon = null;
    public String floatingIcon = null;
    public List<String> targetVillagerGoals = new ArrayList<>();
    public String targetPosition = null;
    public String category = "visit";

    // Crafting/cooking specific
    public List<String[]> inputs = new ArrayList<>();   // each: [itemKey, count]
    public List<String[]> outputs = new ArrayList<>();  // each: [itemKey, count]
    public String itemToCook = null;
    public int minimumToCook = 16;

    // Mining specific
    public String sourceBlockState = null;
    public List<String[]> loot = new ArrayList<>();     // each: [itemKey, count]

    // Harvesting specific
    public String cropType = null;
    public List<String[]> harvestItems = new ArrayList<>(); // each: [itemKey, chance, tag?]
    public String harvestBlockState = null;

    // Slaughter specific
    // (uses loot for drops)

    public GoalGeneric() {}

    @Override
    public GoalInformation getDestination(MillVillager villager) {
        // Try to find a valid target based on goal configuration
        Point target = findTargetPoint(villager);
        if (target != null) {
            return new GoalInformation(target, goalRange);
        }
        return null;
    }

    @Override
    public boolean performAction(MillVillager villager) {
        // Base generic action: wait the configured duration, then complete
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        return goalPriority + villager.getRandom().nextInt(Math.max(priorityRandom, 1));
    }

    @Override
    public int actionDuration(MillVillager villager) {
        // Convert ms to ticks (1 tick = 50ms)
        return Math.max(durationMs / 50, 1);
    }

    @Override
    public int range(MillVillager villager) {
        return goalRange;
    }

    @Override
    public boolean allowRandomMoves() {
        return allowRandomMovesFlag;
    }

    @Override
    public boolean canBeDoneAtNight() {
        return leasure;
    }

    @Override
    public String labelKey(MillVillager villager) {
        return goalLabelKey != null ? goalLabelKey : key;
    }

    /**
     * Find the target point for this goal based on configuration.
     * Priority: buildingTag match → townhall → home → villager position.
     */
    @Nullable
    protected Point findTargetPoint(MillVillager villager) {
        // If targeting villagers doing specific goals, find one
        if (!targetVillagerGoals.isEmpty()) {
            Point found = findVillagerDoingGoal(villager);
            if (found != null) return found;
        }

        // If townhall goal, go to townhall
        if (townhallGoal && villager.townHallPoint != null) {
            return villager.townHallPoint;
        }

        // If building tag set, try to find matching building via townhall area
        // (simplified: use townhall for tagged goals since that's the village center)
        if (buildingTag != null && !buildingTag.isEmpty()) {
            Point building = findBuildingWithTag(villager, buildingTag);
            if (building != null) return building;
            // Fallback to townhall for any building-tagged goal
            if (villager.townHallPoint != null) return villager.townHallPoint;
        }

        // Default: home building
        if (villager.housePoint != null) return villager.housePoint;

        // Last resort: current position
        return new Point(villager.blockPosition());
    }

    /**
     * Find a building in the village that has the specified tag.
     */
    @Nullable
    protected Point findBuildingWithTag(MillVillager villager, String tag) {
        org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
        if (mw == null) return null;

        String tagLower = tag.toLowerCase();
        for (org.dizzymii.millenaire2.village.Building b : mw.allBuildings()) {
            if (b.getPos() == null || b.planSetKey == null) continue;
            // Check if the building's plan set has the required tag
            org.dizzymii.millenaire2.culture.Culture culture = villager.getCulture();
            if (culture == null) continue;
            org.dizzymii.millenaire2.culture.BuildingPlanSet planSet = culture.planSets.get(b.planSetKey);
            if (planSet != null && planSet.tags != null) {
                for (String t : planSet.tags) {
                    if (t.equalsIgnoreCase(tagLower)) {
                        return b.getPos();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Find a nearby villager performing one of the target goals.
     */
    @Nullable
    protected Point findVillagerDoingGoal(MillVillager villager) {
        if (!(villager.level() instanceof net.minecraft.server.level.ServerLevel)) return null;

        List<MillVillager> nearby = villager.level().getEntitiesOfClass(
                MillVillager.class,
                villager.getBoundingBox().inflate(30),
                e -> e != villager
        );

        for (MillVillager other : nearby) {
            String otherGoalKey = other.goalKey;
            if (otherGoalKey != null) {
                for (String targetKey : targetVillagerGoals) {
                    if (targetKey.equalsIgnoreCase(otherGoalKey)) {
                        return new Point(other.blockPosition());
                    }
                }
            }
        }
        return null;
    }

    // ================================================================
    // DATA LOADER — scans goals/ directories and registers all goals
    // ================================================================

    private static final String[] CATEGORY_DIRS = {
            "genericcrafting", "genericcooking", "genericmining",
            "genericharvesting", "genericplanting", "genericplantsapling",
            "genericslaughteranimal", "genericvisit", "generictakefrombuilding",
            "generictendfurnace", "genericgatherblocks"
    };

    /**
     * Load all generic goals from the goals/ directory tree.
     * Called during mod initialization from Goal.initGoals().
     */
    public static void loadGenericGoals() {
        File goalsRoot = new File(MillCommonUtilities.getMillenaireContentDir(), "goals");
        if (!goalsRoot.exists() || !goalsRoot.isDirectory()) {
            MillLog.warn(GoalGeneric.class, "Goals directory not found: " + goalsRoot.getAbsolutePath());
            return;
        }

        int totalLoaded = 0;

        for (String categoryDir : CATEGORY_DIRS) {
            File catDir = new File(goalsRoot, categoryDir);
            if (!catDir.exists() || !catDir.isDirectory()) continue;

            String category = categoryDir.replace("generic", "");
            totalLoaded += loadGoalsFromDirectory(catDir, category);
        }

        MillLog.minor(GoalGeneric.class, "Loaded " + totalLoaded + " generic goals from " + goalsRoot.getAbsolutePath());
    }

    /**
     * Recursively load goal .txt files from a directory and its subdirectories.
     */
    private static int loadGoalsFromDirectory(File dir, String category) {
        int count = 0;
        File[] files = dir.listFiles();
        if (files == null) return 0;

        for (File f : files) {
            if (f.isDirectory()) {
                // Recurse into culture-specific subdirectories
                count += loadGoalsFromDirectory(f, category);
            } else if (f.getName().endsWith(".txt")) {
                GoalGeneric goal = loadGoalFromFile(f, category);
                if (goal != null) {
                    String goalKey = f.getName().replace(".txt", "").toLowerCase();
                    // Only register if not already registered (hardcoded goals take priority)
                    if (Goal.goals != null && !Goal.goals.containsKey(goalKey)) {
                        Goal.registerGoal(goalKey, goal);
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * Parse a single goal .txt file into a GoalGeneric instance.
     */
    @Nullable
    private static GoalGeneric loadGoalFromFile(File file, String category) {
        GoalGeneric goal = createForCategory(category);
        goal.category = category;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                int eq = line.indexOf('=');
                if (eq < 0) continue;

                String param = line.substring(0, eq).trim().toLowerCase();
                String value = line.substring(eq + 1).trim();

                parseParameter(goal, param, value);
            }
        } catch (Exception e) {
            MillLog.error(GoalGeneric.class, "Error loading goal: " + file.getName(), e);
            return null;
        }

        return goal;
    }

    /**
     * Create the appropriate GoalGeneric subclass for a category.
     */
    private static GoalGeneric createForCategory(String category) {
        return switch (category) {
            case "crafting" -> new GoalGenericCrafting();
            case "cooking" -> new GoalGenericCooking();
            case "mining" -> new GoalGenericMining();
            case "harvesting" -> new GoalGenericHarvesting();
            case "planting" -> new GoalGenericPlanting();
            case "plantsapling" -> new GoalGenericPlantSapling();
            case "slaughteranimal" -> new GoalGenericSlaughter();
            case "visit" -> new GoalGenericVisit();
            case "takefrombuilding" -> new GoalGenericTakeFromBuilding();
            case "tendfurnace" -> new GoalGenericTendFurnace();
            case "gatherblocks" -> new GoalGenericGatherBlocks();
            default -> new GoalGenericVisit(); // safe default
        };
    }

    /**
     * Parse a key=value parameter into the goal's fields.
     */
    private static void parseParameter(GoalGeneric goal, String param, String value) {
        switch (param) {
            case "buildingtag" -> goal.buildingTag = value;
            case "requiredtag" -> goal.requiredTag = value;
            case "townhallgoal" -> goal.townhallGoal = "true".equalsIgnoreCase(value);
            case "priority" -> goal.goalPriority = parseIntSafe(value, 50);
            case "priorityrandom" -> goal.priorityRandom = parseIntSafe(value, 10);
            case "duration" -> goal.durationMs = parseIntSafe(value, 5000);
            case "reoccurdelay" -> goal.reoccurDelayMs = parseIntSafe(value, 5000);
            case "range" -> goal.goalRange = parseIntSafe(value, 3);
            case "sentencekey" -> goal.sentenceKey = value;
            case "labelkey" -> goal.goalLabelKey = value;
            case "helditems" -> goal.heldItems = parseStringList(value);
            case "helditemsoffhand" -> goal.heldItemsOffhand = parseStringList(value);
            case "helditemsdestination" -> goal.heldItemsDestination = parseStringList(value);
            case "allowrandommoves" -> goal.allowRandomMovesFlag = "true".equalsIgnoreCase(value);
            case "holdweapons" -> goal.holdWeapons = "true".equalsIgnoreCase(value);
            case "lookatgoal" -> goal.lookAtGoal = "true".equalsIgnoreCase(value);
            case "sound" -> goal.sound = value;
            case "leasure" -> goal.leasure = "true".equalsIgnoreCase(value);
            case "sprint" -> { goal.sprint = "true".equalsIgnoreCase(value); }
            case "icon" -> goal.icon = value;
            case "floatingicon" -> goal.floatingIcon = value;
            case "travelbookshow" -> goal.travelBookShow = "true".equalsIgnoreCase(value);
            case "minimumhour" -> goal.minimumHour = parseIntSafe(value, -1);
            case "maximumhour" -> goal.maximumHour = parseIntSafe(value, -1);
            case "maxsimultaneousinbuilding" -> goal.maxSimultaneousInBuilding = parseIntSafe(value, 0);
            case "maxsimultaneoustotal" -> goal.maxSimultaneousTotal = parseIntSafe(value, 0);
            case "targetposition" -> goal.targetPosition = value;
            case "targetvillagergoals" -> goal.targetVillagerGoals = parseStringList(value);
            // Category-specific
            case "input" -> goal.inputs.add(parseItemCount(value));
            case "output" -> goal.outputs.add(parseItemCount(value));
            case "itemtocook" -> goal.itemToCook = value;
            case "minimumtocook" -> goal.minimumToCook = parseIntSafe(value, 16);
            case "sourceblockstate" -> goal.sourceBlockState = value;
            case "loot" -> goal.loot.add(parseItemCount(value));
            case "croptype" -> goal.cropType = value;
            case "harvestitem" -> goal.harvestItems.add(parseHarvestItem(value));
            case "harvestblockstate" -> goal.harvestBlockState = value;
            case "tag" -> goal.tags.add(value.toLowerCase());
            case "buildinglimit" -> {
                // Stored on the Goal base class
            }
            case "townhalllimit", "villagelimit" -> {
                // Stored on the Goal base class
            }
            default -> {
                // Unknown parameter — silently ignore
            }
        }
    }

    // --- Parsing helpers ---

    private static int parseIntSafe(String value, int defaultVal) {
        try { return Integer.parseInt(value.trim()); }
        catch (NumberFormatException e) { return defaultVal; }
    }

    private static List<String> parseStringList(String value) {
        List<String> list = new ArrayList<>();
        for (String s : value.split(",")) {
            String trimmed = s.trim();
            if (!trimmed.isEmpty()) list.add(trimmed);
        }
        return list;
    }

    private static String[] parseItemCount(String value) {
        String[] parts = value.split(",");
        if (parts.length >= 2) {
            return new String[]{parts[0].trim(), parts[1].trim()};
        }
        return new String[]{value.trim(), "1"};
    }

    private static String[] parseHarvestItem(String value) {
        String[] parts = value.split(",");
        String[] result = new String[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = parts[i].trim();
        }
        return result;
    }
}
