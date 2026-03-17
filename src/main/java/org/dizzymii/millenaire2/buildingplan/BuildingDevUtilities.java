package org.dizzymii.millenaire2.buildingplan;

import org.dizzymii.millenaire2.util.MillLog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Development/debug utilities for building plans.
 * Ported from org.millenaire.common.buildingplan.BuildingDevUtilities (Forge 1.12.2).
 */
public final class BuildingDevUtilities {

    private BuildingDevUtilities() {}

    /**
     * Generates a resource cost summary for a building plan (block counts).
     */
    public static Map<String, Integer> generateBuildingResources(List<BuildingBlock> blocks) {
        Map<String, Integer> resources = new TreeMap<>();
        for (BuildingBlock bb : blocks) {
            if (bb.blockState != null) {
                String key = bb.blockState.getBlock().getDescriptionId();
                resources.merge(key, 1, Integer::sum);
            }
        }
        return resources;
    }

    /**
     * Validates a single building plan loaded from a PNG file.
     * Returns a list of warning messages. Empty list = valid.
     */
    public static List<String> validatePlan(File pngFile, int buildingWidth) {
        List<String> warnings = new ArrayList<>();

        Map<String, List<int[]>> specialPos = new HashMap<>();
        List<BuildingBlock> blocks = PngPlanLoader.loadPlan(pngFile, buildingWidth, 0, specialPos);

        if (blocks.isEmpty()) {
            warnings.add("Plan has no blocks: " + pngFile.getName());
        }

        // Check for sleeping positions
        if (!specialPos.containsKey(SpecialPointTypeList.bsleepingPos)) {
            warnings.add("No sleeping position defined in " + pngFile.getName());
        }

        // Check for main chest
        boolean hasChest = specialPos.containsKey(SpecialPointTypeList.bmainchestGuess)
                || specialPos.containsKey(SpecialPointTypeList.bmainchestTop)
                || specialPos.containsKey(SpecialPointTypeList.bmainchestBottom)
                || specialPos.containsKey(SpecialPointTypeList.bmainchestLeft)
                || specialPos.containsKey(SpecialPointTypeList.bmainchestRight);
        if (!hasChest) {
            warnings.add("No main chest position defined in " + pngFile.getName());
        }

        return warnings;
    }

    /**
     * Validates all PNG plans in a directory, logging any warnings.
     */
    public static int validateAllPlans(File planDirectory, int buildingWidth) {
        int totalWarnings = 0;
        File[] pngFiles = planDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (pngFiles == null) return 0;

        for (File png : pngFiles) {
            List<String> warnings = validatePlan(png, buildingWidth);
            for (String w : warnings) {
                MillLog.warn(null, "BuildingDevUtilities: " + w);
            }
            totalWarnings += warnings.size();
        }

        MillLog.minor(null, "Validated " + pngFiles.length + " plans, " + totalWarnings + " warnings.");
        return totalWarnings;
    }

    /**
     * Dumps all registered colour-to-block mappings to a text file.
     */
    public static void dumpBlockList(File outputFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("=== Millenaire Colour-to-Block Mappings ===");
            writer.newLine();
            writer.newLine();

            List<Map.Entry<Integer, PointType>> entries = new ArrayList<>(PointType.colourPoints.entrySet());
            entries.sort(Comparator.comparingInt(Map.Entry::getKey));

            for (Map.Entry<Integer, PointType> entry : entries) {
                int colour = entry.getKey();
                PointType pt = entry.getValue();
                String hex = String.format("%06X", colour);
                writer.write(hex + " -> " + pt.toString());
                writer.newLine();
            }

            MillLog.minor(null, "Dumped " + entries.size() + " block mappings to " + outputFile.getName());
        } catch (IOException e) {
            MillLog.error(null, "Failed to dump block list: " + e.getMessage(), e);
        }
    }

    /**
     * Prints resource costs for a plan to the log.
     */
    public static void logResourceCosts(File pngFile, int buildingWidth) {
        Map<String, List<int[]>> specialPos = new HashMap<>();
        List<BuildingBlock> blocks = PngPlanLoader.loadPlan(pngFile, buildingWidth, 0, specialPos);
        Map<String, Integer> resources = generateBuildingResources(blocks);

        MillLog.minor(null, "Resource costs for " + pngFile.getName() + ":");
        for (Map.Entry<String, Integer> entry : resources.entrySet()) {
            MillLog.minor(null, "  " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
