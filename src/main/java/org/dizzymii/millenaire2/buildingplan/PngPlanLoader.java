package org.dizzymii.millenaire2.buildingplan;

import org.dizzymii.millenaire2.util.MillLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads building plans from PNG colour maps.
 * Each pixel colour maps to a PointType defining the block to place.
 * Ported from org.millenaire.common.buildingplan.PngPlanLoader (Forge 1.12.2).
 */
public final class PngPlanLoader {

    private PngPlanLoader() {}

    public static List<BuildingBlock> loadPlan(File pngFile, int level) {
        List<BuildingBlock> blocks = new ArrayList<>();
        // TODO: Read PNG, iterate pixels, map colours to PointType.colourPoints,
        //       create BuildingBlock instances with correct x/y/z offsets
        MillLog.major(null, "PngPlanLoader.loadPlan stub for: " + pngFile.getName());
        return blocks;
    }

    public static int[][][] readPngLayers(File baseDir, String planName) {
        // TODO: Read multiple PNG layers (ground, level0, level1, ...)
        //       Return 3D array [layer][x][z] of colour values
        return new int[0][0][0];
    }

    // TODO: getColour(BufferedImage, int, int), loadBlockList()
}
