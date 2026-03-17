package org.dizzymii.millenaire2.buildingplan;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.io.File;

/**
 * Import/export buildings from the world to plan files and vice versa.
 * Ported from org.millenaire.common.buildingplan.BuildingImportExport (Forge 1.12.2).
 */
public final class BuildingImportExport {

    private BuildingImportExport() {}

    public static void exportBuilding(Level level, BlockPos corner1, BlockPos corner2, File outputDir, String name) {
        // TODO: Scan blocks between corners, create PNG plan layers,
        //       write metadata file, output to culture directory
    }

    public static void importBuilding(Level level, BlockPos origin, File planDir, String planName, int orientation) {
        // TODO: Read PNG plan layers, translate to BuildingBlocks,
        //       place blocks in world with rotation
    }

    // TODO: exportBuildingPng, generateMetadata, validatePlan
}
