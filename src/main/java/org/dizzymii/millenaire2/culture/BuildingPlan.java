package org.dizzymii.millenaire2.culture;

import org.dizzymii.millenaire2.data.ConfigAnnotations.ConfigField;
import org.dizzymii.millenaire2.data.ConfigAnnotations.FieldDocumentation;
import org.dizzymii.millenaire2.data.ConfigAnnotations.ParameterType;
import org.dizzymii.millenaire2.buildingplan.BuildingBlock;
import org.dizzymii.millenaire2.buildingplan.PointType;
import org.dizzymii.millenaire2.buildingplan.SpecialPointTypeList;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.VirtualDir;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a single level/upgrade of a building.
 * Building structures are defined by PNG images where pixel colors map to block types.
 * The .txt file provides metadata while the PNG provides the actual block layout.
 *
 * Ported from org.millenaire.common.buildingplan.BuildingPlan.
 */
public class BuildingPlan {

    public static final int NORTH_FACING = 0;
    public static final int WEST_FACING = 1;
    public static final int SOUTH_FACING = 2;
    public static final int EAST_FACING = 3;
    public static final String[] FACING_KEYS = new String[]{"north", "west", "south", "east"};

    public final BuildingPlanSet parentSet;
    public final String upgradeKey;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.INTEGER)
    @FieldDocumentation(explanation = "Length of the building (PNG height).")
    public int length;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.INTEGER)
    @FieldDocumentation(explanation = "Width of the building (PNG floor width).")
    public int width;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.INTEGER, defaultValue = "5")
    @FieldDocumentation(explanation = "Area to clear around the building.")
    public int areaToClear;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.INTEGER, defaultValue = "1")
    @FieldDocumentation(explanation = "Orientation of the building within the plan.")
    public int buildingOrientation;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.INTEGER, defaultValue = "0")
    @FieldDocumentation(explanation = "Altitude offset from ground level.")
    public int altitudeOffset;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.INTEGER, defaultValue = "10")
    @FieldDocumentation(explanation = "Depth of foundations.")
    public int foundationDepth;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.DIRECTION)
    @FieldDocumentation(explanation = "Preferred facing direction.")
    public String startingOrientation = null;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.INTEGER, defaultValue = "0")
    @FieldDocumentation(explanation = "Priority for construction order.")
    public int priority;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.INTEGER, defaultValue = "0")
    @FieldDocumentation(explanation = "Price in deniers for the player to purchase this upgrade.")
    public int price;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.STRING_ADD, paramName = "villager")
    @FieldDocumentation(explanation = "Villager types that live in this building at this level.")
    public List<String> villagers = new ArrayList<>();

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.STRING_ADD, paramName = "shop")
    @FieldDocumentation(explanation = "Shop types available at this level.")
    public List<String> shops = new ArrayList<>();

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.STRING_ADD, paramName = "subbuilding")
    @FieldDocumentation(explanation = "Sub-buildings unlocked at this level.")
    public List<String> subBuildings = new ArrayList<>();

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.STRING_ADD, paramName = "tag")
    @FieldDocumentation(explanation = "Tags for this upgrade level.")
    public List<String> tags = new ArrayList<>();

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.STRING, paramName = "pngfile")
    @FieldDocumentation(explanation = "Name of the PNG plan file for this level.")
    public String pngFileName = null;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.INTEGER, paramName = "startlevel", defaultValue = "0")
    @FieldDocumentation(explanation = "Starting ground level offset for this upgrade.")
    public int startLevel = 0;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.STRING, paramName = "nativename")
    @FieldDocumentation(explanation = "Native-language name of the building at this level.")
    public String nativeName = null;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.STRING, paramName = "male")
    @FieldDocumentation(explanation = "Male villager type key for this level.")
    public String male = null;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.STRING, paramName = "female")
    @FieldDocumentation(explanation = "Female villager type key for this level.")
    public String female = null;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.STRING_ADD, paramName = "signs")
    @FieldDocumentation(explanation = "Sign text entries for this level.")
    public List<String> signs = new ArrayList<>();

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.INTEGER, paramName = "pathlevel", defaultValue = "0")
    @FieldDocumentation(explanation = "Path building level for this upgrade.")
    public int pathLevel = 0;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.BOOLEAN, paramName = "rebuildpath", defaultValue = "false")
    @FieldDocumentation(explanation = "Whether to rebuild paths at this upgrade level.")
    public boolean rebuildPath = false;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.BOOLEAN, paramName = "nopathstobuilding", defaultValue = "false")
    @FieldDocumentation(explanation = "Whether to suppress path generation to this building.")
    public boolean noPathsToBuilding = false;

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.STRING_ADD, paramName = "villagetag")
    @FieldDocumentation(explanation = "Tags added to the village when this level is reached.")
    public List<String> villageTags = new ArrayList<>();

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.STRING_ADD, paramName = "cleartag")
    @FieldDocumentation(explanation = "Tags removed from the village when this level is reached.")
    public List<String> clearTags = new ArrayList<>();

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.STRING_ADD, paramName = "forbiddentaginvillage")
    @FieldDocumentation(explanation = "Tags that prevent this upgrade from being built.")
    public List<String> forbiddenTagsInVillage = new ArrayList<>();

    @ConfigField(fieldCategory = "upgrade", type = ParameterType.INTEGER, paramName = "prioritymovein", defaultValue = "0")
    @FieldDocumentation(explanation = "Priority for villager move-in to this building.")
    public int priorityMoveIn = 0;

    // --- Runtime data ---
    public int nbFloors = 0;
    public int groundFloor = 0;
    private BufferedImage planImage = null;

    // Block data: [x][z][floor] -> pixel color (to be mapped to blocks)
    private int[][][] blockData = null;

    // Special positions found in the plan
    public final Map<String, List<int[]>> specialPositions = new HashMap<>();

    public int planIndex = 0;

    public BuildingPlan(BuildingPlanSet parentSet, String upgradeKey) {
        this.parentSet = parentSet;
        this.upgradeKey = upgradeKey;
    }

    /**
     * Load the PNG plan image for this building level.
     * The PNG encodes blocks as pixel colors. Each horizontal strip is one floor.
     */
    public void loadPlanImage(VirtualDir buildingsDir) {
        // Use parentSet dimensions as fallback if plan-level are 0
        if (width <= 0 && parentSet.width > 0) width = parentSet.width;
        if (length <= 0 && parentSet.length > 0) length = parentSet.length;

        String fileName = pngFileName;
        if (fileName == null) {
            // Original Millenaire convention: key + levelIndex + ".png"
            // e.g. armoury_a0.png (initial), armoury_a1.png (upgrade1)
            fileName = parentSet.key + planIndex + ".png";
        }

        File imageFile = buildingsDir.getChildFileRecursive(fileName);
        if (imageFile == null) {
            imageFile = buildingsDir.getChildFile(fileName);
        }

        if (imageFile == null || !imageFile.exists()) {
            MillLog.warn(this, "Plan image not found: " + fileName + " for " + parentSet.key + "/" + upgradeKey);
            return;
        }

        try {
            planImage = ImageIO.read(imageFile);
            if (planImage != null) {
                parsePlanImage();
            }
        } catch (Exception e) {
            MillLog.error(this, "Error loading plan image: " + fileName, e);
        }
    }

    /**
     * Parse the loaded PNG into block data.
     * The image is laid out as: width pixels wide, with each floor side by side.
     * Height of image = length of building.
     * Number of floors = image width / building width.
     */
    private void parsePlanImage() {
        if (planImage == null) return;

        int imgWidth = planImage.getWidth();
        int imgHeight = planImage.getHeight();

        // If width/length not set from config, try to infer
        if (width <= 0) width = imgWidth;
        if (length <= 0) length = imgHeight;

        if (width <= 0 || length <= 0) return;

        nbFloors = imgWidth / Math.max(width, 1);
        if (nbFloors <= 0) nbFloors = 1;

        blockData = new int[width][length][nbFloors];
        specialPositions.clear();

        for (int floor = 0; floor < nbFloors; floor++) {
            for (int x = 0; x < width && (floor * width + x) < imgWidth; x++) {
                for (int z = 0; z < length && z < imgHeight; z++) {
                    int pixelX = floor * width + x;
                    int rgb = planImage.getRGB(pixelX, z) & 0xFFFFFF;
                    blockData[x][z][floor] = rgb;

                    PointType pt = PointType.colourPoints.get(rgb);
                    if (pt != null && pt.getBlock() == null && pt.getSpecialType() != null) {
                        specialPositions.computeIfAbsent(pt.getSpecialType(), k -> new ArrayList<>())
                                .add(new int[]{x, floor + altitudeOffset, z});
                    }
                }
            }
        }

        MillLog.minor(this, "Parsed plan: " + width + "x" + length + "x" + nbFloors
                + " for " + parentSet.key + "/" + upgradeKey);
    }

    /**
     * Get the block color at a specific position in the plan.
     * @return RGB color value, or -1 if out of bounds
     */
    public int getBlockColor(int x, int z, int floor) {
        if (blockData == null) return -1;
        if (x < 0 || x >= width || z < 0 || z >= length || floor < 0 || floor >= nbFloors) return -1;
        return blockData[x][z][floor];
    }

    public boolean hasImage() {
        return planImage != null;
    }

    /**
     * Resolve the parsed colour data into concrete BuildingBlock placements.
     */
    public List<BuildingBlock> resolveBuildingBlocks() {
        return resolveBuildingBlocks(false);
    }

    /**
     * Resolve parsed colour data into concrete BuildingBlock placements, with optional mirroring.
     */
    public List<BuildingBlock> resolveBuildingBlocks(boolean mirrorX) {
        List<BuildingBlock> blocks = new ArrayList<>();
        if (blockData == null) {
            return blocks;
        }

        for (int floor = 0; floor < nbFloors; floor++) {
            int y = floor + altitudeOffset;
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < length; z++) {
                    int rgb = getBlockColor(x, z, floor);
                    if (rgb < 0 || rgb == 0xFFFFFF) {
                        continue;
                    }

                    PointType pt = PointType.colourPoints.get(rgb);
                    if (pt == null) {
                        continue;
                    }

                    int planX = mirrorX ? (width - 1 - x) : x;

                    if (pt.getBlock() != null) {
                        blocks.add(new BuildingBlock(pt, planX, y, z));
                        continue;
                    }

                    BuildingBlock specialBlock = createSpecialBlock(pt.getSpecialType(), planX, y, z);
                    if (specialBlock != null) {
                        blocks.add(specialBlock);
                    }
                }
            }
        }

        return blocks;
    }

    /**
     * Compute required block counts from resolved plan blocks.
     */
    public Map<String, Integer> computeResourceCost() {
        Map<String, Integer> costs = new HashMap<>();
        for (BuildingBlock bb : resolveBuildingBlocks()) {
            if (bb.freePlacement) continue;
            BlockState state = bb.blockState;
            if (state == null || state.is(Blocks.AIR)) {
                continue;
            }
            String key = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
            costs.merge(key, 1, Integer::sum);
        }
        return costs;
    }

    @javax.annotation.Nullable
    private static BuildingBlock createSpecialBlock(String specialType, int x, int y, int z) {
        if (specialType == null) {
            return null;
        }

        BuildingBlock bb = new BuildingBlock();
        bb.x = x;
        bb.y = y;
        bb.z = z;

        // --- Agriculture soil types ---
        if (specialType.equals(SpecialPointTypeList.bsoil)
                || specialType.equals(SpecialPointTypeList.bmaizesoil)
                || specialType.equals(SpecialPointTypeList.bcarrotsoil)
                || specialType.equals(SpecialPointTypeList.bpotatosoil)
                || specialType.equals(SpecialPointTypeList.bflowersoil)
                || specialType.equals(SpecialPointTypeList.bturmericsoil)
                || specialType.equals(SpecialPointTypeList.bcottonsoil)) {
            bb.blockState = Blocks.FARMLAND.defaultBlockState();
            return bb;
        }

        if (specialType.equals(SpecialPointTypeList.bricesoil)
                || specialType.equals(SpecialPointTypeList.bsugarcanesoil)) {
            bb.blockState = Blocks.WATER.defaultBlockState();
            return bb;
        }

        if (specialType.equals(SpecialPointTypeList.bnetherwartsoil)) {
            bb.blockState = Blocks.SOUL_SAND.defaultBlockState();
            return bb;
        }

        if (specialType.equals(SpecialPointTypeList.bvinesoil)) {
            bb.blockState = Blocks.DIRT.defaultBlockState();
            return bb;
        }

        if (specialType.equals(SpecialPointTypeList.bcacaospot)) {
            bb.blockState = Blocks.JUNGLE_LOG.defaultBlockState();
            return bb;
        }

        if (specialType.equals(SpecialPointTypeList.bgrass)) {
            bb.blockState = Blocks.GRASS_BLOCK.defaultBlockState();
            return bb;
        }

        // --- Guessed blocks (orientation-sensitive, placed second pass) ---
        if (specialType.equals(SpecialPointTypeList.btorchGuess)) {
            bb.blockState = Blocks.TORCH.defaultBlockState();
            bb.secondStep = true;
            return bb;
        }

        if (specialType.equals(SpecialPointTypeList.bladderGuess)) {
            bb.blockState = Blocks.LADDER.defaultBlockState();
            bb.secondStep = true;
            return bb;
        }

        if (specialType.equals(SpecialPointTypeList.bsignwallGuess)) {
            bb.blockState = Blocks.OAK_WALL_SIGN.defaultBlockState();
            bb.secondStep = true;
            return bb;
        }

        if (specialType.equals(SpecialPointTypeList.bfurnaceGuess)) {
            bb.blockState = Blocks.FURNACE.defaultBlockState();
            return bb;
        }

        // --- Chest positions → actual chest blocks ---
        if (specialType.startsWith("mainchest") || specialType.startsWith("lockedchest")) {
            bb.blockState = Blocks.CHEST.defaultBlockState();
            bb.secondStep = true;
            return bb;
        }

        // --- Free blocks (no resource cost — map to real block for placement) ---
        if (specialType.equals(SpecialPointTypeList.bfreestone)) {
            bb.blockState = Blocks.STONE.defaultBlockState();
            bb.freePlacement = true;
            return bb;
        }
        if (specialType.equals(SpecialPointTypeList.bfreesand)) {
            bb.blockState = Blocks.SAND.defaultBlockState();
            bb.freePlacement = true;
            return bb;
        }
        if (specialType.equals(SpecialPointTypeList.bfreesandstone)) {
            bb.blockState = Blocks.SANDSTONE.defaultBlockState();
            bb.freePlacement = true;
            return bb;
        }
        if (specialType.equals(SpecialPointTypeList.bfreegravel)) {
            bb.blockState = Blocks.GRAVEL.defaultBlockState();
            bb.freePlacement = true;
            return bb;
        }
        if (specialType.equals(SpecialPointTypeList.bfreewool)) {
            bb.blockState = Blocks.WHITE_WOOL.defaultBlockState();
            bb.freePlacement = true;
            return bb;
        }
        if (specialType.equals(SpecialPointTypeList.bfreecobblestone)) {
            bb.blockState = Blocks.COBBLESTONE.defaultBlockState();
            bb.freePlacement = true;
            return bb;
        }
        if (specialType.equals(SpecialPointTypeList.bfreestonebrick)) {
            bb.blockState = Blocks.STONE_BRICKS.defaultBlockState();
            bb.freePlacement = true;
            return bb;
        }
        if (specialType.equals(SpecialPointTypeList.bfreepaintedbrick)) {
            bb.blockState = Blocks.BRICKS.defaultBlockState();
            bb.freePlacement = true;
            return bb;
        }
        if (specialType.equals(SpecialPointTypeList.bfreegrass_block)) {
            bb.blockState = Blocks.GRASS_BLOCK.defaultBlockState();
            bb.freePlacement = true;
            return bb;
        }

        // --- Terrain specials that don't place blocks ---
        if (specialType.equals(SpecialPointTypeList.bempty)
                || specialType.equals(SpecialPointTypeList.bpreserveground)
                || specialType.equals(SpecialPointTypeList.ballbuttrees)) {
            return null;
        }

        // --- Functional position markers (no block, just position tracking) ---
        if (specialType.equals(SpecialPointTypeList.bsleepingPos)
                || specialType.equals(SpecialPointTypeList.bsellingPos)
                || specialType.equals(SpecialPointTypeList.bcraftingPos)
                || specialType.equals(SpecialPointTypeList.bdefendingPos)
                || specialType.equals(SpecialPointTypeList.bshelterPos)
                || specialType.equals(SpecialPointTypeList.bpathStartPos)
                || specialType.equals(SpecialPointTypeList.bleasurePos)
                || specialType.equals(SpecialPointTypeList.bstall)) {
            return null;
        }

        // --- Mod-specific agriculture that needs special handling ---
        if (specialType.equals(SpecialPointTypeList.bsilkwormblock)
                || specialType.equals(SpecialPointTypeList.bsnailsoilblock)) {
            bb.blockState = Blocks.DIRT.defaultBlockState();
            return bb;
        }

        return null;
    }

    @Override
    public String toString() {
        return "BuildingPlan[" + parentSet.key + "/" + upgradeKey + "]";
    }
}
