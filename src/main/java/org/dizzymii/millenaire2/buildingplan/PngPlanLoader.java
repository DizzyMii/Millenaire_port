package org.dizzymii.millenaire2.buildingplan;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.world.level.block.Blocks;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Loads building plans from PNG colour maps.
 * Each pixel colour maps to a PointType defining the block to place.
 * Ported from org.millenaire.common.buildingplan.PngPlanLoader (Forge 1.12.2).
 */
public final class PngPlanLoader {
    private static final Logger LOGGER = LogUtils.getLogger();

    private PngPlanLoader() {}

    /**
     * Load a building plan from a PNG file.
     * Image layout: floors side by side, each buildingWidth wide. Height = length.
     *
     * @param pngFile        the PNG image file
     * @param buildingWidth  width per floor strip
     * @param altitudeOffset altitude offset from ground
     * @param specialPositions populated with special position data found in the plan
     * @return list of BuildingBlock instances
     */
    public static List<BuildingBlock> loadPlan(File pngFile, int buildingWidth, int altitudeOffset,
                                                Map<String, List<int[]>> specialPositions) {
        List<BuildingBlock> blocks = new ArrayList<>();

        try {
            BufferedImage image = ImageIO.read(pngFile);
            if (image == null) {
                LOGGER.error("PngPlanLoader: Failed to read: " + pngFile.getName());
                return blocks;
            }

            int imgWidth = image.getWidth();
            int imgHeight = image.getHeight();

            if (buildingWidth <= 0) buildingWidth = imgWidth;
            int nbFloors = imgWidth / Math.max(buildingWidth, 1);
            if (nbFloors <= 0) nbFloors = 1;

            for (int floor = 0; floor < nbFloors; floor++) {
                int y = floor + altitudeOffset;
                for (int x = 0; x < buildingWidth && (floor * buildingWidth + x) < imgWidth; x++) {
                    for (int z = 0; z < imgHeight; z++) {
                        int pixelX = floor * buildingWidth + x;
                        int rgb = image.getRGB(pixelX, z) & 0xFFFFFF;

                        if (rgb == 0xFFFFFF) continue; // empty

                        PointType pt = PointType.colourPoints.get(rgb);
                        if (pt == null) continue; // unknown colour

                        // Special position (no block)
                        if (pt.getBlock() == null && pt.getSpecialType() != null) {
                            specialPositions.computeIfAbsent(pt.getSpecialType(), k -> new ArrayList<>())
                                    .add(new int[]{x, y, z});
                            // Some specials also place a block
                            BuildingBlock bb = createSpecialBlock(pt.getSpecialType(), x, y, z);
                            if (bb != null) blocks.add(bb);
                            continue;
                        }

                        blocks.add(new BuildingBlock(pt, x, y, z));
                    }
                }
            }

            LOGGER.debug("PngPlanLoader: " + blocks.size() + " blocks from " + pngFile.getName());

        } catch (Exception e) {
            LOGGER.error("PngPlanLoader: error loading: " + pngFile.getName(), e);
        }

        return blocks;
    }

    /**
     * Read raw pixel colors from a PNG as [floor][x][z].
     */
    public static int[][][] readPngLayers(File pngFile, int buildingWidth) {
        try {
            BufferedImage image = ImageIO.read(pngFile);
            if (image == null) return new int[0][0][0];

            int imgWidth = image.getWidth();
            int imgHeight = image.getHeight();
            if (buildingWidth <= 0) buildingWidth = imgWidth;
            int nbFloors = imgWidth / Math.max(buildingWidth, 1);
            if (nbFloors <= 0) nbFloors = 1;

            int[][][] data = new int[nbFloors][buildingWidth][imgHeight];
            for (int floor = 0; floor < nbFloors; floor++) {
                for (int x = 0; x < buildingWidth && (floor * buildingWidth + x) < imgWidth; x++) {
                    for (int z = 0; z < imgHeight; z++) {
                        data[floor][x][z] = image.getRGB(floor * buildingWidth + x, z) & 0xFFFFFF;
                    }
                }
            }
            return data;
        } catch (Exception e) {
            LOGGER.error("PngPlanLoader: error reading layers: " + pngFile.getName(), e);
            return new int[0][0][0];
        }
    }

    /**
     * Some special types need a block placed (e.g. soil types).
     */
    private static BuildingBlock createSpecialBlock(String specialType, int x, int y, int z) {
        return switch (specialType) {
            case SpecialPointTypeList.bsoil, SpecialPointTypeList.bmaizesoil,
                 SpecialPointTypeList.bcarrotsoil, SpecialPointTypeList.bpotatosoil,
                 SpecialPointTypeList.bflowersoil -> {
                BuildingBlock bb = new BuildingBlock();
                bb.blockState = Blocks.FARMLAND.defaultBlockState();
                bb.x = x; bb.y = y; bb.z = z;
                yield bb;
            }
            case SpecialPointTypeList.bricesoil, SpecialPointTypeList.bsugarcanesoil -> {
                BuildingBlock bb = new BuildingBlock();
                bb.blockState = Blocks.WATER.defaultBlockState();
                bb.x = x; bb.y = y; bb.z = z;
                yield bb;
            }
            case SpecialPointTypeList.bgrass -> {
                BuildingBlock bb = new BuildingBlock();
                bb.blockState = Blocks.GRASS_BLOCK.defaultBlockState();
                bb.x = x; bb.y = y; bb.z = z;
                yield bb;
            }
            default -> null;
        };
    }
}
