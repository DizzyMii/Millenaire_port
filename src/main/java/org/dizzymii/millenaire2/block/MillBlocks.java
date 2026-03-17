package org.dizzymii.millenaire2.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * Central registry for all Millénaire blocks.
 * Blocks are registered via DeferredRegister in Millenaire2.BLOCKS.
 */
public class MillBlocks {

    // ===== Decorative Stone =====
    public static final DeferredBlock<Block> STONE_DECORATION = Millenaire2.BLOCKS.registerSimpleBlock(
            "stone_decoration",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_WHITE = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_white",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_ORANGE = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_orange",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_MAGENTA = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_magenta",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_LIGHT_BLUE = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_light_blue",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_YELLOW = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_yellow",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_LIME = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_lime",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_PINK = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_pink",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_GRAY = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_gray",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_LIGHT_GRAY = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_light_gray",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_CYAN = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_cyan",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_PURPLE = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_purple",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_BLUE = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_blue",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_BROWN = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_brown",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_GREEN = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_green",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_RED = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_red",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> PAINTED_BRICK_BLACK = Millenaire2.BLOCKS.registerSimpleBlock(
            "painted_brick_black",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    // ===== Decorative Wood =====
    public static final DeferredBlock<Block> TIMBER_FRAME_PLAIN = Millenaire2.BLOCKS.registerSimpleBlock(
            "timber_frame_plain",
            BlockBehaviour.Properties.of().strength(2.0f, 3.0f).sound(SoundType.WOOD));

    public static final DeferredBlock<Block> TIMBER_FRAME_CROSS = Millenaire2.BLOCKS.registerSimpleBlock(
            "timber_frame_cross",
            BlockBehaviour.Properties.of().strength(2.0f, 3.0f).sound(SoundType.WOOD));

    // ===== Decorative Earth =====
    public static final DeferredBlock<Block> MUD_BRICK = Millenaire2.BLOCKS.registerSimpleBlock(
            "mud_brick",
            BlockBehaviour.Properties.of().strength(1.0f, 3.0f).sound(SoundType.GRAVEL));

    public static final DeferredBlock<Block> MUD_BRICK_EXTENDED = Millenaire2.BLOCKS.registerSimpleBlock(
            "mud_brick_extended",
            BlockBehaviour.Properties.of().strength(1.0f, 3.0f).sound(SoundType.GRAVEL));

    // ===== Functional Blocks =====
    public static final DeferredBlock<Block> WET_BRICK = Millenaire2.BLOCKS.registerSimpleBlock(
            "wet_brick",
            BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.GRAVEL));

    public static final DeferredBlock<Block> SILK_WORM_BLOCK = Millenaire2.BLOCKS.registerSimpleBlock(
            "silk_worm",
            BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.GRASS));

    public static final DeferredBlock<Block> SNAIL_SOIL = Millenaire2.BLOCKS.registerSimpleBlock(
            "snail_soil",
            BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.GRAVEL));

    public static final DeferredBlock<Block> SOD = Millenaire2.BLOCKS.registerSimpleBlock(
            "sod",
            BlockBehaviour.Properties.of().strength(0.6f).sound(SoundType.GRASS));

    // ===== Path Blocks =====
    public static final DeferredBlock<Block> PATH_DIRT = Millenaire2.BLOCKS.registerSimpleBlock(
            "path_dirt",
            BlockBehaviour.Properties.of().strength(0.65f).sound(SoundType.GRAVEL));

    public static final DeferredBlock<Block> PATH_GRAVEL = Millenaire2.BLOCKS.registerSimpleBlock(
            "path_gravel",
            BlockBehaviour.Properties.of().strength(0.65f).sound(SoundType.GRAVEL));

    public static final DeferredBlock<Block> PATH_SAND = Millenaire2.BLOCKS.registerSimpleBlock(
            "path_sand",
            BlockBehaviour.Properties.of().strength(0.65f).sound(SoundType.SAND));

    public static final DeferredBlock<Block> PATH_SANDSTONE = Millenaire2.BLOCKS.registerSimpleBlock(
            "path_sandstone",
            BlockBehaviour.Properties.of().strength(0.8f).sound(SoundType.STONE));

    // ===== Rosette =====
    public static final DeferredBlock<Block> ROSETTE = Millenaire2.BLOCKS.registerSimpleBlock(
            "rosette",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    // ===== Alchemist Explosive =====
    public static final DeferredBlock<Block> ALCHEMIST_EXPLOSIVE = Millenaire2.BLOCKS.registerSimpleBlock(
            "alchemist_explosive",
            BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.GRASS));

    // ===== Sandstone Variants =====
    public static final DeferredBlock<Block> MILL_SANDSTONE = Millenaire2.BLOCKS.registerSimpleBlock(
            "mill_sandstone",
            BlockBehaviour.Properties.of().strength(0.8f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    public static final DeferredBlock<Block> MILL_SANDSTONE_DECORATED = Millenaire2.BLOCKS.registerSimpleBlock(
            "mill_sandstone_decorated",
            BlockBehaviour.Properties.of().strength(0.8f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    // ===== Statue =====
    public static final DeferredBlock<Block> MILL_STATUE = Millenaire2.BLOCKS.registerSimpleBlock(
            "mill_statue",
            BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops());

    // TODO: Phase 2 will add the following complex blocks that need custom Block subclasses:
    // - Crop blocks (rice, turmeric, maize, cotton, vine) extending CropBlock
    // - GrapeVine block with custom growth
    // - FruitLeaves (apple, cherry, olive, pistachio)
    // - Sapling blocks with custom tree gen
    // - FirePit + BlockEntity
    // - LockedChest + BlockEntity
    // - MillBed + BlockEntity
    // - Panel + BlockEntity
    // - ImportTable + BlockEntity
    // - Slab/Stair/Wall variants for each decorative block
    // - Pane blocks (mill_pane, bars, rosette_bars)
    // - Stained glass variants
    // - Custom ice, custom snow
    // - Oriented brick/slab variants
    // - Mock blocks for building plans

    /**
     * Called from Millenaire2 constructor to force class loading and register all blocks.
     */
    public static void init() {
        // Class loading triggers all static final fields above
    }
}
