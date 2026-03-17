package org.dizzymii.millenaire2.buildingplan;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.block.MillBlocks;
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
     *
     * IMPORTANT: Every colour (int key) must be unique. If two entries share
     * a colour the second silently overwrites the first. All conflicts from
     * the original 1.12.2 table have been resolved with unique colours below.
     */
    public static void registerDefaults() {
        colourPoints.clear();

        // ===== Terrain / empty =====
        registerSpecial(0xFFFFFF, SpecialPointTypeList.bempty);
        registerSpecial(0x00FF00, SpecialPointTypeList.bpreserveground);
        registerSpecial(0x80FF80, SpecialPointTypeList.ballbuttrees);
        registerSpecial(0x008000, SpecialPointTypeList.bgrass);

        // ===== Common building blocks =====
        registerBlock(0x808080, "cobblestone", Blocks.COBBLESTONE, false);
        registerBlock(0xC0C0C0, "stone", Blocks.STONE, false);
        registerBlock(0xB8B8B8, "smooth_stone", Blocks.SMOOTH_STONE, false);
        registerBlock(0x404040, "stone_bricks", Blocks.STONE_BRICKS, false);
        registerBlock(0x484848, "mossy_stone_bricks", Blocks.MOSSY_STONE_BRICKS, false);
        registerBlock(0x383838, "cracked_stone_bricks", Blocks.CRACKED_STONE_BRICKS, false);
        registerBlock(0x303030, "chiseled_stone_bricks", Blocks.CHISELED_STONE_BRICKS, false);
        registerBlock(0x787878, "mossy_cobblestone", Blocks.MOSSY_COBBLESTONE, false);
        registerBlock(0x686868, "andesite", Blocks.ANDESITE, false);
        registerBlock(0x707070, "polished_andesite", Blocks.POLISHED_ANDESITE, false);
        registerBlock(0x989898, "diorite", Blocks.DIORITE, false);
        registerBlock(0xA0A0A8, "polished_diorite", Blocks.POLISHED_DIORITE, false);
        registerBlock(0xB06050, "granite", Blocks.GRANITE, false);
        registerBlock(0xB87060, "polished_granite", Blocks.POLISHED_GRANITE, false);
        registerBlock(0x282828, "deepslate_bricks", Blocks.DEEPSLATE_BRICKS, false);

        // ===== Wood — planks =====
        registerBlock(0x905020, "oak_planks", Blocks.OAK_PLANKS, false);
        registerBlock(0xA06030, "spruce_planks", Blocks.SPRUCE_PLANKS, false);
        registerBlock(0xC09050, "birch_planks", Blocks.BIRCH_PLANKS, false);
        registerBlock(0x602810, "dark_oak_planks", Blocks.DARK_OAK_PLANKS, false);
        registerBlock(0xB87040, "jungle_planks", Blocks.JUNGLE_PLANKS, false);
        registerBlock(0xD04020, "acacia_planks", Blocks.ACACIA_PLANKS, false);
        registerBlock(0x704828, "mangrove_planks", Blocks.MANGROVE_PLANKS, false);

        // ===== Wood — logs =====
        registerBlock(0x503010, "oak_log", Blocks.OAK_LOG, false);
        registerBlock(0x302008, "spruce_log", Blocks.SPRUCE_LOG, false);
        registerBlock(0x704020, "birch_log", Blocks.BIRCH_LOG, false);
        registerBlock(0x401808, "dark_oak_log", Blocks.DARK_OAK_LOG, false);
        registerBlock(0x584018, "jungle_log", Blocks.JUNGLE_LOG, false);
        registerBlock(0x603818, "acacia_log", Blocks.ACACIA_LOG, false);
        registerBlock(0x483020, "mangrove_log", Blocks.MANGROVE_LOG, false);
        registerBlock(0x906838, "stripped_oak_log", Blocks.STRIPPED_OAK_LOG, false);

        // ===== Sandstone =====
        registerBlock(0xE0D0A0, "sandstone", Blocks.SANDSTONE, false);
        registerBlock(0xF0E8C0, "smooth_sandstone", Blocks.SMOOTH_SANDSTONE, false);
        registerBlock(0xD0C090, "cut_sandstone", Blocks.CUT_SANDSTONE, false);
        registerBlock(0xD8C898, "chiseled_sandstone", Blocks.CHISELED_SANDSTONE, false);
        registerBlock(0xC0A070, "red_sandstone", Blocks.RED_SANDSTONE, false);
        registerBlock(0xC8A878, "smooth_red_sandstone", Blocks.SMOOTH_RED_SANDSTONE, false);

        // ===== Terracotta =====
        registerBlock(0xD08040, "terracotta", Blocks.TERRACOTTA, false);
        registerBlock(0xD0A080, "white_terracotta", Blocks.WHITE_TERRACOTTA, false);
        registerBlock(0xA06828, "orange_terracotta", Blocks.ORANGE_TERRACOTTA, false);
        registerBlock(0xB06838, "brown_terracotta", Blocks.BROWN_TERRACOTTA, false);
        registerBlock(0xC08060, "yellow_terracotta", Blocks.YELLOW_TERRACOTTA, false);

        // ===== Bricks & glass =====
        registerBlock(0xFF0000, "bricks", Blocks.BRICKS, false);
        registerBlock(0xFFC080, "glass", Blocks.GLASS, true);
        registerBlock(0xFFA000, "glass_pane", Blocks.GLASS_PANE, true);
        registerBlock(0xA0A0C0, "iron_bars", Blocks.IRON_BARS, true);

        // ===== Functional vanilla blocks =====
        registerBlock(0xF0F0F0, "snow_block", Blocks.SNOW_BLOCK, false);
        registerBlock(0x0000FF, "water", Blocks.WATER, false);
        registerBlock(0xFF8000, "lava", Blocks.LAVA, false);
        registerBlock(0x000000, "obsidian", Blocks.OBSIDIAN, false);
        registerBlock(0xFFFF00, "glowstone", Blocks.GLOWSTONE, false);
        registerBlock(0x200000, "nether_bricks", Blocks.NETHER_BRICKS, false);
        registerBlock(0x604830, "crafting_table", Blocks.CRAFTING_TABLE, false);
        registerBlock(0x585858, "furnace", Blocks.FURNACE, false);
        registerBlock(0x708090, "anvil", Blocks.ANVIL, false);
        registerBlock(0x282018, "bookshelf", Blocks.BOOKSHELF, false);
        registerBlock(0x604000, "chest", Blocks.CHEST, true);
        registerBlock(0x480000, "brewing_stand", Blocks.BREWING_STAND, true);
        registerBlock(0xFFF0D0, "white_wool", Blocks.WHITE_WOOL, false);
        registerBlock(0xF0D0A0, "brown_wool", Blocks.BROWN_WOOL, false);
        registerBlock(0xE8E8E0, "white_carpet", Blocks.WHITE_CARPET, true);
        registerBlock(0x706040, "dirt", Blocks.DIRT, false);
        registerBlock(0xD0D000, "sand", Blocks.SAND, false);
        registerBlock(0xA09080, "gravel", Blocks.GRAVEL, false);
        registerBlock(0xE0E8E0, "clay", Blocks.CLAY, false);
        registerBlock(0x103010, "hay_block", Blocks.HAY_BLOCK, false);
        registerBlock(0x180808, "soul_sand", Blocks.SOUL_SAND, false);

        // ===== Second-step blocks (placed after first pass) =====
        registerBlock(0xFF8080, "torch", Blocks.TORCH, true);
        registerBlock(0x804000, "oak_door", Blocks.OAK_DOOR, true);
        registerBlock(0x804800, "spruce_door", Blocks.SPRUCE_DOOR, true);
        registerBlock(0x805000, "dark_oak_door", Blocks.DARK_OAK_DOOR, true);
        registerBlock(0x604020, "oak_trapdoor", Blocks.OAK_TRAPDOOR, true);
        registerBlock(0x402000, "oak_fence", Blocks.OAK_FENCE, true);
        registerBlock(0x201000, "oak_fence_gate", Blocks.OAK_FENCE_GATE, true);
        registerBlock(0x301800, "spruce_fence", Blocks.SPRUCE_FENCE, true);
        registerBlock(0x180C00, "spruce_fence_gate", Blocks.SPRUCE_FENCE_GATE, true);
        registerBlock(0x8080FF, "ladder", Blocks.LADDER, true);
        registerBlock(0x404008, "cobblestone_wall", Blocks.COBBLESTONE_WALL, true);
        registerBlock(0x404010, "stone_brick_wall", Blocks.STONE_BRICK_WALL, true);
        registerBlock(0xC06000, "oak_sign", Blocks.OAK_SIGN, true);

        // ===== Stairs =====
        registerBlock(0x008080, "oak_stairs", Blocks.OAK_STAIRS, true);
        registerBlock(0x004040, "stone_stairs", Blocks.STONE_STAIRS, true);
        registerBlock(0x006060, "cobblestone_stairs", Blocks.COBBLESTONE_STAIRS, true);
        registerBlock(0x00C0C0, "sandstone_stairs", Blocks.SANDSTONE_STAIRS, true);
        registerBlock(0xC00000, "brick_stairs", Blocks.BRICK_STAIRS, true);
        registerBlock(0x505060, "stone_brick_stairs", Blocks.STONE_BRICK_STAIRS, true);
        registerBlock(0x005050, "spruce_stairs", Blocks.SPRUCE_STAIRS, true);
        registerBlock(0x003030, "dark_oak_stairs", Blocks.DARK_OAK_STAIRS, true);
        registerBlock(0x007070, "birch_stairs", Blocks.BIRCH_STAIRS, true);

        // ===== Slabs =====
        registerBlock(0x808040, "oak_slab", Blocks.OAK_SLAB, false);
        registerBlock(0x404020, "cobblestone_slab", Blocks.COBBLESTONE_SLAB, false);
        registerBlock(0x606030, "stone_brick_slab", Blocks.STONE_BRICK_SLAB, false);
        registerBlock(0xC0C060, "sandstone_slab", Blocks.SANDSTONE_SLAB, false);
        registerBlock(0x505028, "stone_slab", Blocks.STONE_SLAB, false);
        registerBlock(0x707038, "spruce_slab", Blocks.SPRUCE_SLAB, false);
        registerBlock(0x909048, "birch_slab", Blocks.BIRCH_SLAB, false);
        registerBlock(0xA0A050, "brick_slab", Blocks.BRICK_SLAB, false);

        // ===== Beds =====
        registerBlock(0xE00000, "red_bed", Blocks.RED_BED, true);
        registerBlock(0xE0E0F0, "white_bed", Blocks.WHITE_BED, true);

        // ===== Millénaire mod blocks =====
        registerBlock(0xA07848, "timber_frame_plain", MillBlocks.TIMBER_FRAME_PLAIN.get(), false);
        registerBlock(0xA08050, "timber_frame_cross", MillBlocks.TIMBER_FRAME_CROSS.get(), false);
        registerBlock(0xC0A040, "thatch", MillBlocks.THATCH.get(), false);
        registerBlock(0x806040, "mud_brick", MillBlocks.MUD_BRICK.get(), false);
        registerBlock(0x887048, "mud_brick_extended", MillBlocks.MUD_BRICK_EXTENDED.get(), false);
        registerBlock(0xC04020, "cooked_brick", MillBlocks.COOKED_BRICK.get(), false);
        registerBlock(0xA89070, "stone_decoration", MillBlocks.STONE_DECORATION.get(), false);
        registerBlock(0xD8C0A8, "sandstone_carved", MillBlocks.SANDSTONE_CARVED.get(), false);
        registerBlock(0xC8A088, "sandstone_red_carved", MillBlocks.SANDSTONE_RED_CARVED.get(), false);
        registerBlock(0xD0B890, "sandstone_ochre_carved", MillBlocks.SANDSTONE_OCHRE_CARVED.get(), false);
        registerBlock(0xD8C8B0, "sandstone_decorated", MillBlocks.SANDSTONE_DECORATED.get(), false);
        registerBlock(0xB09878, "byzantine_stone_ornament", MillBlocks.BYZANTINE_STONE_ORNAMENT.get(), false);
        registerBlock(0xB8A088, "byzantine_sandstone_ornament", MillBlocks.BYZANTINE_SANDSTONE_ORNAMENT.get(), false);
        registerBlock(0xA88068, "byzantine_tiles", MillBlocks.BYZANTINE_TILES.get(), false);
        registerBlock(0xA88870, "byzantine_stone_tiles", MillBlocks.BYZANTINE_STONE_TILES.get(), false);
        registerBlock(0xB89880, "byzantine_sandstone_tiles", MillBlocks.BYZANTINE_SANDSTONE_TILES.get(), false);
        registerBlock(0x909088, "gray_tiles", MillBlocks.GRAY_TILES.get(), false);
        registerBlock(0x609060, "green_tiles", MillBlocks.GREEN_TILES.get(), false);
        registerBlock(0xC06060, "red_tiles", MillBlocks.RED_TILES.get(), false);
        registerBlock(0xE0C080, "paper_wall", MillBlocks.PAPER_WALL.get(), true);
        registerBlock(0x806848, "wooden_bars", MillBlocks.WOODEN_BARS.get(), true);
        registerBlock(0x907858, "rosette", MillBlocks.ROSETTE.get(), false);
        registerBlock(0xC0B0A0, "stained_glass", MillBlocks.STAINED_GLASS.get(), true);
        registerBlock(0xD0E0F0, "ice_brick", MillBlocks.ICE_BRICK.get(), false);
        registerBlock(0xE8E8F0, "snow_brick", MillBlocks.SNOW_BRICK.get(), false);
        registerBlock(0xC8B090, "sod", MillBlocks.SOD.get(), false);

        // Millénaire mod stairs (second step)
        registerBlock(0x00A088, "stairs_timberframe", MillBlocks.STAIRS_TIMBERFRAME.get(), true);
        registerBlock(0x008068, "stairs_mudbrick", MillBlocks.STAIRS_MUDBRICK.get(), true);
        registerBlock(0x00B090, "stairs_cookedbrick", MillBlocks.STAIRS_COOKEDBRICK.get(), true);
        registerBlock(0x009078, "stairs_thatch", MillBlocks.STAIRS_THATCH.get(), true);

        // Millénaire path blocks
        registerBlock(0x706848, "path_dirt", MillBlocks.PATH_DIRT.get(), false);
        registerBlock(0x908878, "path_gravel", MillBlocks.PATH_GRAVEL.get(), false);
        registerBlock(0xA09888, "path_slabs", MillBlocks.PATH_SLABS.get(), false);
        registerBlock(0xD0C0A8, "path_sandstone", MillBlocks.PATH_SANDSTONE.get(), false);

        // ===== Agriculture =====
        registerSpecial(0x00C000, SpecialPointTypeList.bsoil);
        registerSpecial(0x00A000, SpecialPointTypeList.bricesoil);
        registerSpecial(0x00E000, SpecialPointTypeList.bmaizesoil);
        registerSpecial(0x00D000, SpecialPointTypeList.bcarrotsoil);
        registerSpecial(0x00B000, SpecialPointTypeList.bpotatosoil);
        registerSpecial(0x00F000, SpecialPointTypeList.bflowersoil);
        registerSpecial(0x009000, SpecialPointTypeList.bsugarcanesoil);
        registerSpecial(0x009800, SpecialPointTypeList.bturmericsoil);
        registerSpecial(0x008800, SpecialPointTypeList.bnetherwartsoil);
        registerSpecial(0x00A800, SpecialPointTypeList.bvinesoil);
        registerSpecial(0x00B800, SpecialPointTypeList.bcottonsoil);
        registerSpecial(0x00C800, SpecialPointTypeList.bsilkwormblock);
        registerSpecial(0x00D800, SpecialPointTypeList.bsnailsoilblock);
        registerSpecial(0x00E800, SpecialPointTypeList.bcacaospot);

        // ===== Special / functional positions =====
        registerSpecial(0x0000C0, SpecialPointTypeList.bsleepingPos);
        registerSpecial(0x0000A0, SpecialPointTypeList.bsellingPos);
        registerSpecial(0x000080, SpecialPointTypeList.bcraftingPos);
        registerSpecial(0x000060, SpecialPointTypeList.bdefendingPos);
        registerSpecial(0x000040, SpecialPointTypeList.bshelterPos);
        registerSpecial(0x000020, SpecialPointTypeList.bpathStartPos);
        registerSpecial(0x0000E0, SpecialPointTypeList.bleasurePos);
        registerSpecial(0x800080, SpecialPointTypeList.bstall);

        // ===== Chest positions =====
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

        // ===== Guessed blocks (unique colours — no conflicts) =====
        registerSpecial(0xFF4040, SpecialPointTypeList.btorchGuess);
        registerSpecial(0x4040FF, SpecialPointTypeList.bladderGuess);
        registerSpecial(0xC08000, SpecialPointTypeList.bsignwallGuess);
        registerSpecial(0x606858, SpecialPointTypeList.bfurnaceGuess);

        // ===== Free blocks (unique colours — no conflicts) =====
        registerSpecial(0xBEBEBE, SpecialPointTypeList.bfreestone);
        registerSpecial(0xE0E000, SpecialPointTypeList.bfreesand);
        registerSpecial(0xE0D080, SpecialPointTypeList.bfreesandstone);
        registerSpecial(0xA0A0A0, SpecialPointTypeList.bfreegravel);
        registerSpecial(0xE0E0E0, SpecialPointTypeList.bfreewool);
        registerSpecial(0x909090, SpecialPointTypeList.bfreecobblestone);
        registerSpecial(0x505050, SpecialPointTypeList.bfreestonebrick);
        registerSpecial(0x484038, SpecialPointTypeList.bfreepaintedbrick);
        registerSpecial(0x70A050, SpecialPointTypeList.bfreegrass_block);

        MillLog.minor(null, "PointType: registered " + colourPoints.size() + " default colour mappings");
    }
}
