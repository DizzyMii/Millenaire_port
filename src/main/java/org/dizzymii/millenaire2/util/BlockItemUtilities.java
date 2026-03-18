package org.dizzymii.millenaire2.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import java.util.HashSet;
import java.util.Set;

/**
 * Block and item classification utilities for village AI.
 * Ported from org.millenaire.common.utilities.BlockItemUtilities (Forge 1.12.2).
 */
public final class BlockItemUtilities {

    private BlockItemUtilities() {}

    private static final Set<String> FORBIDDEN_BLOCKS = new HashSet<>();
    private static final Set<String> GROUND_BLOCKS = new HashSet<>();
    private static final Set<String> DANGER_BLOCKS = new HashSet<>();
    private static final Set<String> WATER_BLOCKS = new HashSet<>();
    private static final Set<String> PATH_REPLACEABLE_BLOCKS = new HashSet<>();

    public static boolean isBlockSolid(Block block) {
        if (block == null) return false;
        BlockState state = block.defaultBlockState();
        return state.isSolid();
    }

    public static boolean isBlockWalkable(Block block) {
        if (block == null) return false;
        return isBlockSolid(block) && !(block instanceof LiquidBlock);
    }

    public static boolean isBlockLiquid(Block block) {
        return block instanceof LiquidBlock;
    }

    public static boolean isBlockAir(Level level, BlockPos pos) {
        return level.isEmptyBlock(pos);
    }

    public static boolean isBlockWater(Block block) {
        return block == Blocks.WATER;
    }

    public static boolean isBlockDangerous(Block block) {
        return block == Blocks.LAVA || block == Blocks.FIRE || block == Blocks.CACTUS
                || block == Blocks.SWEET_BERRY_BUSH || block == Blocks.WITHER_ROSE;
    }

    public static boolean isGroundBlock(Block block) {
        if (block == null) return false;
        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT
                || block == Blocks.SAND || block == Blocks.GRAVEL
                || block == Blocks.STONE || block == Blocks.COBBLESTONE
                || block == Blocks.CLAY || block == Blocks.FARMLAND
                || block == Blocks.DIRT_PATH;
    }

    public static boolean isTreeBlock(Block block) {
        if (block == null) return false;
        String name = block.getDescriptionId();
        return name.contains("log") || name.contains("leaves") || name.contains("wood");
    }

    public static boolean isPathReplaceable(Block block) {
        if (block == null) return false;
        return block == Blocks.SHORT_GRASS || block == Blocks.TALL_GRASS
                || block == Blocks.FERN || block == Blocks.LARGE_FERN
                || block == Blocks.DEAD_BUSH || block == Blocks.DANDELION
                || block == Blocks.POPPY || block == Blocks.SNOW
                || isTreeBlock(block);
    }

    public static boolean isForbidden(Block block) {
        if (block == null) return false;
        return block == Blocks.BEDROCK || block == Blocks.END_PORTAL
                || block == Blocks.END_PORTAL_FRAME || block == Blocks.COMMAND_BLOCK
                || block == Blocks.BARRIER || block == Blocks.STRUCTURE_BLOCK
                || block == Blocks.SPAWNER;
    }

    public static boolean isCropBlock(Block block) {
        if (block == null) return false;
        String name = block.getDescriptionId();
        return name.contains("wheat") || name.contains("carrots") || name.contains("potatoes")
                || name.contains("beetroots") || name.contains("melon_stem")
                || name.contains("pumpkin_stem") || name.contains("nether_wart")
                || name.contains("cocoa") || name.contains("sugar_cane");
    }

    public static boolean isOreBlock(Block block) {
        if (block == null) return false;
        String name = block.getDescriptionId();
        return name.contains("_ore") || name.contains("ancient_debris");
    }

    public static boolean isStoneVariant(Block block) {
        if (block == null) return false;
        return block == Blocks.STONE || block == Blocks.COBBLESTONE
                || block == Blocks.STONE_BRICKS || block == Blocks.MOSSY_COBBLESTONE
                || block == Blocks.MOSSY_STONE_BRICKS || block == Blocks.SMOOTH_STONE
                || block == Blocks.ANDESITE || block == Blocks.DIORITE
                || block == Blocks.GRANITE || block == Blocks.DEEPSLATE
                || block == Blocks.COBBLED_DEEPSLATE || block == Blocks.TUFF;
    }

    public static boolean isWoodVariant(Block block) {
        if (block == null) return false;
        String name = block.getDescriptionId();
        return name.contains("planks") || name.contains("log") || name.contains("wood")
                || name.contains("stripped_");
    }

    /**
     * Checks if the given block is one that a villager would consider "harvestable"
     * (i.e., mature crops or resource blocks within their village territory).
     */
    public static boolean isHarvestable(Block block) {
        return isCropBlock(block) || isTreeBlock(block);
    }

    /**
     * Loads block classification sets from a config file (key=value, comma-separated block IDs).
     */
    public static void loadBlockSets(java.io.File configFile) {
        if (configFile == null || !configFile.exists()) return;
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(configFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq <= 0) continue;
                String key = line.substring(0, eq).trim().toLowerCase();
                String[] values = line.substring(eq + 1).trim().split(",");
                Set<String> target = switch (key) {
                    case "forbidden" -> FORBIDDEN_BLOCKS;
                    case "ground" -> GROUND_BLOCKS;
                    case "danger" -> DANGER_BLOCKS;
                    case "water" -> WATER_BLOCKS;
                    case "path_replaceable" -> PATH_REPLACEABLE_BLOCKS;
                    default -> null;
                };
                if (target != null) {
                    for (String v : values) {
                        String t = v.trim();
                        if (!t.isEmpty()) target.add(t);
                    }
                }
            }
        } catch (Exception e) {
            // Silently ignore config load errors
        }
    }
}
