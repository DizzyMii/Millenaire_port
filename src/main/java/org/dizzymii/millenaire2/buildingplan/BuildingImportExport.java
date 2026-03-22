package org.dizzymii.millenaire2.buildingplan;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Import/export buildings from the world to plan files and vice versa.
 * Ported from org.millenaire.common.buildingplan.BuildingImportExport (Forge 1.12.2).
 */
public final class BuildingImportExport {
    private static final Logger LOGGER = LogUtils.getLogger();

    private BuildingImportExport() {}

    /**
     * Export a region of blocks from the world into a PNG building plan + metadata file.
     * corner1 and corner2 define the bounding box. Floors are stacked on the Y axis.
     */
    public static void exportBuilding(Level level, BlockPos corner1, BlockPos corner2, File outputDir, String name) {
        int minX = Math.min(corner1.getX(), corner2.getX());
        int minY = Math.min(corner1.getY(), corner2.getY());
        int minZ = Math.min(corner1.getZ(), corner2.getZ());
        int maxX = Math.max(corner1.getX(), corner2.getX());
        int maxY = Math.max(corner1.getY(), corner2.getY());
        int maxZ = Math.max(corner1.getZ(), corner2.getZ());

        int width = maxX - minX + 1;
        int height = maxZ - minZ + 1;
        int floors = maxY - minY + 1;

        // Build reverse colour lookup: BlockState -> colour
        Map<String, Integer> blockToColour = new HashMap<>();
        for (Map.Entry<Integer, PointType> entry : PointType.colourPoints.entrySet()) {
            BlockState bs = entry.getValue().getBlockState();
            if (bs != null) {
                blockToColour.put(bs.getBlock().getDescriptionId(), entry.getKey());
            }
        }

        // Create PNG image: floors side by side, each 'width' wide
        int imgWidth = width * floors;
        BufferedImage image = new BufferedImage(imgWidth, height, BufferedImage.TYPE_INT_RGB);

        int blockCount = 0;
        for (int floor = 0; floor < floors; floor++) {
            int y = minY + floor;
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < height; z++) {
                    BlockPos pos = new BlockPos(minX + x, y, minZ + z);
                    BlockState state = level.getBlockState(pos);

                    int colour = 0xFFFFFF; // default: empty
                    if (!state.isAir()) {
                        Integer mapped = blockToColour.get(state.getBlock().getDescriptionId());
                        if (mapped != null) {
                            colour = mapped;
                        } else {
                            colour = 0x808080; // fallback: unknown block -> cobblestone grey
                        }
                        blockCount++;
                    }

                    image.setRGB(floor * width + x, z, colour);
                }
            }
        }

        // Write PNG
        if (!outputDir.exists()) outputDir.mkdirs();
        File pngFile = new File(outputDir, name + ".png");
        try {
            ImageIO.write(image, "PNG", pngFile);
            LOGGER.debug("Exported " + blockCount + " blocks to " + pngFile.getName());
        } catch (IOException e) {
            LOGGER.error("Failed to write building PNG: " + pngFile.getAbsolutePath(), e);
            return;
        }

        // Write metadata
        File metaFile = new File(outputDir, name + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaFile))) {
            writer.write("name=" + name);
            writer.newLine();
            writer.write("width=" + width);
            writer.newLine();
            writer.write("floors=" + floors);
            writer.newLine();
            writer.write("length=" + height);
            writer.newLine();
            writer.write("radius=" + Math.max(width, height) / 2);
            writer.newLine();
            LOGGER.debug("Wrote metadata to " + metaFile.getName());
        } catch (IOException e) {
            LOGGER.error("Failed to write metadata: " + metaFile.getAbsolutePath(), e);
        }
    }

    /**
     * Import a building plan and place it in the world at the given origin with rotation.
     */
    public static void importBuilding(Level level, BlockPos origin, File planDir, String planName, int orientation) {
        File pngFile = new File(planDir, planName + ".png");
        File metaFile = new File(planDir, planName + ".txt");

        if (!pngFile.exists()) {
            LOGGER.error("Plan PNG not found: " + pngFile.getAbsolutePath());
            return;
        }

        // Load metadata for width
        int buildingWidth = 0;
        int altitudeOffset = 0;
        if (metaFile.exists()) {
            Map<String, String> meta = BuildingMetadataLoader.loadMetadata(metaFile);
            try {
                if (meta.containsKey("width")) buildingWidth = Integer.parseInt(meta.get("width"));
            } catch (NumberFormatException ignored) {}
        }

        // Load and place blocks
        Map<String, List<int[]>> specialPositions = new HashMap<>();
        List<BuildingBlock> blocks = PngPlanLoader.loadPlan(pngFile, buildingWidth, altitudeOffset, specialPositions);

        // First pass: non-second-step blocks
        int placed = 0;
        for (BuildingBlock bb : blocks) {
            if (!bb.secondStep) {
                if (bb.place(level, origin, orientation)) placed++;
            }
        }

        // Second pass: second-step blocks (torches, doors, etc.)
        for (BuildingBlock bb : blocks) {
            if (bb.secondStep) {
                if (bb.place(level, origin, orientation)) placed++;
            }
        }

        LOGGER.debug("Imported " + placed + " blocks for " + planName + " at " + origin);
    }

    /**
     * Validates that a plan directory contains the required files.
     */
    public static boolean validatePlan(File planDir, String planName) {
        File pngFile = new File(planDir, planName + ".png");
        return pngFile.exists();
    }
}
