package org.dizzymii.millenaire2.buildingplan;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.MillLog;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * Represents a colour-coded point in a building plan PNG.
 * Maps a colour to a block/special type, with optional cost.
 * Ported from org.millenaire.common.buildingplan.PointType (Forge 1.12.2).
 */
public class PointType {

    public static final String SUBTYPE_SIGN = "sign";
    public static final String SUBTYPE_MAINCHEST = "mainchest";
    public static final String SUBTYPE_LOCKEDCHEST = "lockedchest";
    public static final String SUBTYPE_VILLAGEBANNERWALL = "villageBannerWall";
    public static final String SUBTYPE_VILLAGEBANNERSTANDING = "villageBannerStanding";
    public static final String SUBTYPE_CULTUREBANNERWALL = "cultureBannerWall";
    public static final String SUBTYPE_CULTUREBANNERSTANDING = "cultureBannerStanding";

    public static HashMap<Integer, PointType> colourPoints = new HashMap<>();

    final int colour;
    @Nullable final String specialType;
    final String label;
    @Nullable private final Block block;
    @Nullable private final BlockState blockState;
    @Nullable private InvItem costItem = null;
    @Nullable private BlockState costBlockState = null;
    private int costQuantity = 1;
    boolean secondStep = false;

    public PointType(int colour, String name) {
        this.specialType = name;
        this.colour = colour;
        this.block = null;
        this.label = name;
        this.blockState = null;
    }

    public PointType(int colour, String label, Block block, BlockState blockState, boolean secondStep) {
        this.colour = colour;
        this.block = block;
        this.blockState = blockState;
        this.secondStep = secondStep;
        this.specialType = null;
        this.label = label;
    }

    @Nullable public Block getBlock() { return block; }
    @Nullable public BlockState getBlockState() { return blockState; }
    @Nullable public String getSpecialType() { return specialType; }
    public int getCostQuantity() { return costQuantity; }

    public boolean isSubType(String type) {
        return specialType != null && specialType.startsWith(type);
    }

    public boolean isType(String type) {
        return type.equalsIgnoreCase(specialType);
    }

    public void setCost(BlockState blockState, int quantity) {
        this.costBlockState = blockState;
        this.costQuantity = quantity;
        this.costItem = null;
    }

    public void setCost(InvItem item, int quantity) {
        this.costBlockState = null;
        this.costQuantity = quantity;
        this.costItem = item;
    }

    public void setCostToFree() {
        this.costBlockState = null;
        this.costQuantity = 0;
        this.costItem = null;
    }

    @Override
    public String toString() {
        return label + "/" + specialType + "/" + colour + "/" + block + "/" + blockState;
    }

    /**
     * Register a standard block point type.
     */
    public static PointType registerBlock(int colour, String label, Block block, boolean secondStep) {
        PointType pt = new PointType(colour, label, block, block.defaultBlockState(), secondStep);
        colourPoints.put(colour, pt);
        return pt;
    }

    /**
     * Register a special (non-block) point type.
     */
    public static PointType registerSpecial(int colour, String label) {
        PointType pt = new PointType(colour, label);
        colourPoints.put(colour, pt);
        return pt;
    }

    /**
     * Parse a colour point definition from a data file line.
     * Format: "colour;blockId" or "colour;specialType"
     */
    public static void readColourPoint(String line) {
        try {
            String[] parts = line.split(";");
            if (parts.length < 2) return;
            int colour = Integer.parseInt(parts[0].trim(), 16);
            String blockId = parts[1].trim();

            // Check if it's a special type
            if (SpecialPointTypeList.isSpecialPointTypeKnow(blockId)) {
                registerSpecial(colour, blockId);
                return;
            }

            // Try to resolve as a block
            ResourceLocation rl = ResourceLocation.parse(blockId);
            Block block = BuiltInRegistries.BLOCK.get(rl);
            if (block != Blocks.AIR || "minecraft:air".equals(blockId)) {
                boolean isSecondStep = parts.length > 2 && "2".equals(parts[2].trim());
                registerBlock(colour, blockId, block, isSecondStep);
            } else {
                MillLog.warn(null, "PointType: unknown block: " + blockId);
            }
        } catch (Exception e) {
            MillLog.warn(null, "PointType: failed to parse colour point: " + line);
        }
    }

    @Nullable
    public InvItem getCostInvItem() {
        return costItem;
    }

    @Nullable
    public BlockState getCostBlockState() {
        return costBlockState;
    }

    /**
     * Register default colour-to-block mappings used by all cultures.
     * These are the standard Millenaire plan colours.
     */
    public static void registerDefaults() {
        colourPoints.clear();

        // --- Terrain / empty ---
        registerSpecial(0xFFFFFF, SpecialPointTypeList.bempty);
        registerSpecial(0x00FF00, SpecialPointTypeList.bpreserveground);
        registerSpecial(0x80FF80, SpecialPointTypeList.ballbuttrees);
        registerSpecial(0x008000, SpecialPointTypeList.bgrass);

        // --- Common building blocks ---
        registerBlock(0x808080, "cobblestone", Blocks.COBBLESTONE, false);
        registerBlock(0xC0C0C0, "stone", Blocks.STONE, false);
        registerBlock(0x404040, "stone_bricks", Blocks.STONE_BRICKS, false);
        registerBlock(0x905020, "oak_planks", Blocks.OAK_PLANKS, false);
        registerBlock(0xA06030, "spruce_planks", Blocks.SPRUCE_PLANKS, false);
        registerBlock(0xC09050, "birch_planks", Blocks.BIRCH_PLANKS, false);
        registerBlock(0x602810, "dark_oak_planks", Blocks.DARK_OAK_PLANKS, false);
        registerBlock(0x503010, "oak_log", Blocks.OAK_LOG, false);
        registerBlock(0x302008, "spruce_log", Blocks.SPRUCE_LOG, false);
        registerBlock(0x704020, "birch_log", Blocks.BIRCH_LOG, false);
        registerBlock(0xE0D0A0, "sandstone", Blocks.SANDSTONE, false);
        registerBlock(0xF0E8C0, "smooth_sandstone", Blocks.SMOOTH_SANDSTONE, false);
        registerBlock(0xD0C090, "cut_sandstone", Blocks.CUT_SANDSTONE, false);
        registerBlock(0xD08040, "terracotta", Blocks.TERRACOTTA, false);
        registerBlock(0xFF0000, "bricks", Blocks.BRICKS, false);
        registerBlock(0xFFC080, "glass", Blocks.GLASS, true);
        registerBlock(0xFFA000, "glass_pane", Blocks.GLASS_PANE, true);
        registerBlock(0xF0F0F0, "snow_block", Blocks.SNOW_BLOCK, false);
        registerBlock(0x0000FF, "water", Blocks.WATER, false);
        registerBlock(0xFF8000, "lava", Blocks.LAVA, false);
        registerBlock(0x000000, "obsidian", Blocks.OBSIDIAN, false);
        registerBlock(0xFFFF00, "glowstone", Blocks.GLOWSTONE, false);
        registerBlock(0x200000, "nether_bricks", Blocks.NETHER_BRICKS, false);

        // --- Second-step blocks (placed after first pass) ---
        registerBlock(0xFF8080, "torch", Blocks.TORCH, true);
        registerBlock(0x804000, "oak_door", Blocks.OAK_DOOR, true);
        registerBlock(0x604020, "oak_trapdoor", Blocks.OAK_TRAPDOOR, true);
        registerBlock(0x402000, "oak_fence", Blocks.OAK_FENCE, true);
        registerBlock(0x201000, "oak_fence_gate", Blocks.OAK_FENCE_GATE, true);
        registerBlock(0x8080FF, "ladder", Blocks.LADDER, true);
        registerBlock(0x008080, "oak_stairs", Blocks.OAK_STAIRS, true);
        registerBlock(0x004040, "stone_stairs", Blocks.STONE_STAIRS, true);
        registerBlock(0x006060, "cobblestone_stairs", Blocks.COBBLESTONE_STAIRS, true);
        registerBlock(0x00C0C0, "sandstone_stairs", Blocks.SANDSTONE_STAIRS, true);
        registerBlock(0xC00000, "brick_stairs", Blocks.BRICK_STAIRS, true);
        registerBlock(0x606060, "stone_brick_stairs", Blocks.STONE_BRICK_STAIRS, true);
        registerBlock(0x808040, "oak_slab", Blocks.OAK_SLAB, false);
        registerBlock(0x404020, "cobblestone_slab", Blocks.COBBLESTONE_SLAB, false);
        registerBlock(0x606030, "stone_brick_slab", Blocks.STONE_BRICK_SLAB, false);
        registerBlock(0xC0C060, "sandstone_slab", Blocks.SANDSTONE_SLAB, false);

        // --- Agriculture ---
        registerSpecial(0x00C000, SpecialPointTypeList.bsoil);
        registerSpecial(0x00A000, SpecialPointTypeList.bricesoil);
        registerSpecial(0x00E000, SpecialPointTypeList.bmaizesoil);
        registerSpecial(0x00D000, SpecialPointTypeList.bcarrotsoil);
        registerSpecial(0x00B000, SpecialPointTypeList.bpotatosoil);
        registerSpecial(0x00F000, SpecialPointTypeList.bflowersoil);
        registerSpecial(0x009000, SpecialPointTypeList.bsugarcanesoil);

        // --- Special / functional positions ---
        registerSpecial(0x0000C0, SpecialPointTypeList.bsleepingPos);
        registerSpecial(0x0000A0, SpecialPointTypeList.bsellingPos);
        registerSpecial(0x000080, SpecialPointTypeList.bcraftingPos);
        registerSpecial(0x000060, SpecialPointTypeList.bdefendingPos);
        registerSpecial(0x000040, SpecialPointTypeList.bshelterPos);
        registerSpecial(0x000020, SpecialPointTypeList.bpathStartPos);
        registerSpecial(0x0000E0, SpecialPointTypeList.bleasurePos);
        registerSpecial(0x800080, SpecialPointTypeList.bstall);

        // --- Chest positions ---
        registerSpecial(0xC00080, SpecialPointTypeList.bmainchestGuess);
        registerSpecial(0xC000A0, SpecialPointTypeList.bmainchestTop);
        registerSpecial(0xC000C0, SpecialPointTypeList.bmainchestBottom);
        registerSpecial(0xC000E0, SpecialPointTypeList.bmainchestLeft);
        registerSpecial(0xC00060, SpecialPointTypeList.bmainchestRight);
        registerSpecial(0x800060, SpecialPointTypeList.blockedchestGuess);
        registerSpecial(0x800040, SpecialPointTypeList.blockedchestTop);
        registerSpecial(0x8000A0, SpecialPointTypeList.blockedchestBottom);
        registerSpecial(0x8000C0, SpecialPointTypeList.blockedchestLeft);
        registerSpecial(0x8000E0, SpecialPointTypeList.blockedchestRight);

        // --- Guessed blocks ---
        registerSpecial(0xFF4040, SpecialPointTypeList.btorchGuess);
        registerSpecial(0x4040FF, SpecialPointTypeList.bladderGuess);
        registerSpecial(0xC08000, SpecialPointTypeList.bsignwallGuess);
        registerSpecial(0x606060, SpecialPointTypeList.bfurnaceGuess);

        // --- Free blocks ---
        registerSpecial(0xC0C0C0, SpecialPointTypeList.bfreestone);
        registerSpecial(0xE0E000, SpecialPointTypeList.bfreesand);
        registerSpecial(0xE0D080, SpecialPointTypeList.bfreesandstone);
        registerSpecial(0xA0A0A0, SpecialPointTypeList.bfreegravel);
        registerSpecial(0xE0E0E0, SpecialPointTypeList.bfreewool);
        registerSpecial(0x909090, SpecialPointTypeList.bfreecobblestone);
        registerSpecial(0x505050, SpecialPointTypeList.bfreestonebrick);

        MillLog.minor(null, "PointType: registered " + colourPoints.size() + " default colour mappings");
    }
}
