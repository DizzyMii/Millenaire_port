package org.dizzymii.millenaire2.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.block.BlockFirePit;
import org.dizzymii.millenaire2.block.BlockFruitLeaves;
import org.dizzymii.millenaire2.block.BlockImportTable;
import org.dizzymii.millenaire2.block.BlockLockedChest;
import org.dizzymii.millenaire2.block.BlockPanel;
import org.dizzymii.millenaire2.block.MillCropBlock;

/**
 * Central registry for all Millénaire blocks.
 * Blocks are registered via DeferredRegister in BLOCKS.
 */
public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Millenaire2.MODID);

    // ========== Property helpers ==========
    private static BlockBehaviour.Properties stoneProps() {
        return BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.STONE).requiresCorrectToolForDrops();
    }
    private static BlockBehaviour.Properties woodProps() {
        return BlockBehaviour.Properties.of().strength(2.0f, 3.0f).sound(SoundType.WOOD);
    }
    private static BlockBehaviour.Properties earthProps() {
        return BlockBehaviour.Properties.of().strength(1.0f, 3.0f).sound(SoundType.GRAVEL);
    }
    private static BlockBehaviour.Properties pathProps(SoundType sound) {
        return BlockBehaviour.Properties.of().strength(0.65f).sound(sound);
    }
    private static BlockBehaviour.Properties sandstoneProps() {
        return BlockBehaviour.Properties.of().strength(0.8f).sound(SoundType.STONE).requiresCorrectToolForDrops();
    }
    private static BlockBehaviour.Properties tileProps() {
        return BlockBehaviour.Properties.of().strength(2.0f, 10.0f).sound(SoundType.STONE).requiresCorrectToolForDrops();
    }

    // ========== Decorative Stone ==========
    public static final DeferredBlock<Block> STONE_DECORATION = BLOCKS.registerSimpleBlock("stone_decoration", stoneProps());
    public static final DeferredBlock<Block> COOKED_BRICK = BLOCKS.registerSimpleBlock("cooked_brick", stoneProps());

    // Painted bricks (all 16 dye colors)
    public static final DeferredBlock<Block> PAINTED_BRICK_WHITE = BLOCKS.registerSimpleBlock("painted_brick_white", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_ORANGE = BLOCKS.registerSimpleBlock("painted_brick_orange", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_MAGENTA = BLOCKS.registerSimpleBlock("painted_brick_magenta", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_LIGHT_BLUE = BLOCKS.registerSimpleBlock("painted_brick_light_blue", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_YELLOW = BLOCKS.registerSimpleBlock("painted_brick_yellow", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_LIME = BLOCKS.registerSimpleBlock("painted_brick_lime", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_PINK = BLOCKS.registerSimpleBlock("painted_brick_pink", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_GRAY = BLOCKS.registerSimpleBlock("painted_brick_gray", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_LIGHT_GRAY = BLOCKS.registerSimpleBlock("painted_brick_light_gray", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_CYAN = BLOCKS.registerSimpleBlock("painted_brick_cyan", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_PURPLE = BLOCKS.registerSimpleBlock("painted_brick_purple", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_BLUE = BLOCKS.registerSimpleBlock("painted_brick_blue", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_BROWN = BLOCKS.registerSimpleBlock("painted_brick_brown", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_GREEN = BLOCKS.registerSimpleBlock("painted_brick_green", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_RED = BLOCKS.registerSimpleBlock("painted_brick_red", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_BLACK = BLOCKS.registerSimpleBlock("painted_brick_black", stoneProps());

    // Decorated painted bricks (all 16 dye colors)
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_WHITE = BLOCKS.registerSimpleBlock("painted_brick_deco_white", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_ORANGE = BLOCKS.registerSimpleBlock("painted_brick_deco_orange", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_MAGENTA = BLOCKS.registerSimpleBlock("painted_brick_deco_magenta", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_LIGHT_BLUE = BLOCKS.registerSimpleBlock("painted_brick_deco_light_blue", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_YELLOW = BLOCKS.registerSimpleBlock("painted_brick_deco_yellow", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_LIME = BLOCKS.registerSimpleBlock("painted_brick_deco_lime", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_PINK = BLOCKS.registerSimpleBlock("painted_brick_deco_pink", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_GRAY = BLOCKS.registerSimpleBlock("painted_brick_deco_gray", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_LIGHT_GRAY = BLOCKS.registerSimpleBlock("painted_brick_deco_light_gray", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_CYAN = BLOCKS.registerSimpleBlock("painted_brick_deco_cyan", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_PURPLE = BLOCKS.registerSimpleBlock("painted_brick_deco_purple", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_BLUE = BLOCKS.registerSimpleBlock("painted_brick_deco_blue", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_BROWN = BLOCKS.registerSimpleBlock("painted_brick_deco_brown", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_GREEN = BLOCKS.registerSimpleBlock("painted_brick_deco_green", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_RED = BLOCKS.registerSimpleBlock("painted_brick_deco_red", stoneProps());
    public static final DeferredBlock<Block> PAINTED_BRICK_DECO_BLACK = BLOCKS.registerSimpleBlock("painted_brick_deco_black", stoneProps());

    // ========== Decorative Wood ==========
    public static final DeferredBlock<Block> TIMBER_FRAME_PLAIN = BLOCKS.registerSimpleBlock("timber_frame_plain", woodProps());
    public static final DeferredBlock<Block> TIMBER_FRAME_CROSS = BLOCKS.registerSimpleBlock("timber_frame_cross", woodProps());
    public static final DeferredBlock<Block> THATCH = BLOCKS.registerSimpleBlock("thatch", woodProps());

    // ========== Decorative Earth ==========
    public static final DeferredBlock<Block> MUD_BRICK = BLOCKS.registerSimpleBlock("mud_brick", earthProps());
    public static final DeferredBlock<Block> MUD_BRICK_EXTENDED = BLOCKS.registerSimpleBlock("mud_brick_extended", earthProps());

    // ========== Sandstone Variants ==========
    public static final DeferredBlock<Block> SANDSTONE_CARVED = BLOCKS.registerSimpleBlock("sandstone_carved", sandstoneProps());
    public static final DeferredBlock<Block> SANDSTONE_RED_CARVED = BLOCKS.registerSimpleBlock("sandstone_red_carved", sandstoneProps());
    public static final DeferredBlock<Block> SANDSTONE_OCHRE_CARVED = BLOCKS.registerSimpleBlock("sandstone_ochre_carved", sandstoneProps());
    public static final DeferredBlock<Block> SANDSTONE_DECORATED = BLOCKS.registerSimpleBlock("sandstone_decorated", sandstoneProps());

    // Byzantine ornaments
    public static final DeferredBlock<Block> BYZANTINE_STONE_ORNAMENT = BLOCKS.registerSimpleBlock("byzantine_stone_ornament", sandstoneProps());
    public static final DeferredBlock<Block> BYZANTINE_SANDSTONE_ORNAMENT = BLOCKS.registerSimpleBlock("byzantine_sandstone_ornament", sandstoneProps());

    // ========== Tile Blocks ==========
    public static final DeferredBlock<Block> BYZANTINE_TILES = BLOCKS.registerSimpleBlock("byzantine_tiles", tileProps());
    public static final DeferredBlock<Block> BYZANTINE_STONE_TILES = BLOCKS.registerSimpleBlock("byzantine_stone_tiles", tileProps());
    public static final DeferredBlock<Block> BYZANTINE_SANDSTONE_TILES = BLOCKS.registerSimpleBlock("byzantine_sandstone_tiles", tileProps());
    public static final DeferredBlock<Block> GRAY_TILES = BLOCKS.registerSimpleBlock("gray_tiles", tileProps());
    public static final DeferredBlock<Block> GREEN_TILES = BLOCKS.registerSimpleBlock("green_tiles", tileProps());
    public static final DeferredBlock<Block> RED_TILES = BLOCKS.registerSimpleBlock("red_tiles", tileProps());

    // ========== Stairs ==========
    public static final DeferredBlock<StairBlock> STAIRS_TIMBERFRAME = BLOCKS.register("stairs_timberframe",
            () -> new StairBlock(TIMBER_FRAME_PLAIN.get().defaultBlockState(), woodProps()));
    public static final DeferredBlock<StairBlock> STAIRS_MUDBRICK = BLOCKS.register("stairs_mudbrick",
            () -> new StairBlock(MUD_BRICK.get().defaultBlockState(), earthProps()));
    public static final DeferredBlock<StairBlock> STAIRS_COOKEDBRICK = BLOCKS.register("stairs_cookedbrick",
            () -> new StairBlock(COOKED_BRICK.get().defaultBlockState(), stoneProps()));
    public static final DeferredBlock<StairBlock> STAIRS_THATCH = BLOCKS.register("stairs_thatch",
            () -> new StairBlock(THATCH.get().defaultBlockState(), woodProps()));
    public static final DeferredBlock<StairBlock> STAIRS_SANDSTONE_CARVED = BLOCKS.register("stairs_sandstone_carved",
            () -> new StairBlock(SANDSTONE_CARVED.get().defaultBlockState(), sandstoneProps()));
    public static final DeferredBlock<StairBlock> STAIRS_SANDSTONE_RED_CARVED = BLOCKS.register("stairs_sandstone_red_carved",
            () -> new StairBlock(SANDSTONE_RED_CARVED.get().defaultBlockState(), sandstoneProps()));
    public static final DeferredBlock<StairBlock> STAIRS_SANDSTONE_OCHRE_CARVED = BLOCKS.register("stairs_sandstone_ochre_carved",
            () -> new StairBlock(SANDSTONE_OCHRE_CARVED.get().defaultBlockState(), sandstoneProps()));
    public static final DeferredBlock<StairBlock> STAIRS_BYZANTINE_TILES = BLOCKS.register("stairs_byzantine_tiles",
            () -> new StairBlock(BYZANTINE_TILES.get().defaultBlockState(), tileProps()));
    public static final DeferredBlock<StairBlock> STAIRS_GRAY_TILES = BLOCKS.register("stairs_gray_tiles",
            () -> new StairBlock(GRAY_TILES.get().defaultBlockState(), tileProps()));
    public static final DeferredBlock<StairBlock> STAIRS_GREEN_TILES = BLOCKS.register("stairs_green_tiles",
            () -> new StairBlock(GREEN_TILES.get().defaultBlockState(), tileProps()));
    public static final DeferredBlock<StairBlock> STAIRS_RED_TILES = BLOCKS.register("stairs_red_tiles",
            () -> new StairBlock(RED_TILES.get().defaultBlockState(), tileProps()));

    // ========== Slabs ==========
    public static final DeferredBlock<SlabBlock> SLAB_WOOD_DECO = BLOCKS.register("slab_wood_deco",
            () -> new SlabBlock(woodProps()));
    public static final DeferredBlock<SlabBlock> SLAB_STONE_DECO = BLOCKS.register("slab_stone_deco",
            () -> new SlabBlock(stoneProps()));
    public static final DeferredBlock<SlabBlock> SLAB_SANDSTONE_CARVED = BLOCKS.register("slab_sandstone_carved",
            () -> new SlabBlock(sandstoneProps()));
    public static final DeferredBlock<SlabBlock> SLAB_SANDSTONE_RED_CARVED = BLOCKS.register("slab_sandstone_red_carved",
            () -> new SlabBlock(sandstoneProps()));
    public static final DeferredBlock<SlabBlock> SLAB_SANDSTONE_OCHRE_CARVED = BLOCKS.register("slab_sandstone_ochre_carved",
            () -> new SlabBlock(sandstoneProps()));
    public static final DeferredBlock<SlabBlock> SLAB_BYZANTINE_TILES = BLOCKS.register("slab_byzantine_tiles",
            () -> new SlabBlock(tileProps()));
    public static final DeferredBlock<SlabBlock> SLAB_GRAY_TILES = BLOCKS.register("slab_gray_tiles",
            () -> new SlabBlock(tileProps()));
    public static final DeferredBlock<SlabBlock> SLAB_GREEN_TILES = BLOCKS.register("slab_green_tiles",
            () -> new SlabBlock(tileProps()));
    public static final DeferredBlock<SlabBlock> SLAB_RED_TILES = BLOCKS.register("slab_red_tiles",
            () -> new SlabBlock(tileProps()));

    // Path slabs
    public static final DeferredBlock<SlabBlock> SLAB_PATH_DIRT = BLOCKS.register("slab_path_dirt",
            () -> new SlabBlock(pathProps(SoundType.GRAVEL)));
    public static final DeferredBlock<SlabBlock> SLAB_PATH_GRAVEL = BLOCKS.register("slab_path_gravel",
            () -> new SlabBlock(pathProps(SoundType.GRAVEL)));
    public static final DeferredBlock<SlabBlock> SLAB_PATH_SLABS = BLOCKS.register("slab_path_slabs",
            () -> new SlabBlock(pathProps(SoundType.STONE)));
    public static final DeferredBlock<SlabBlock> SLAB_PATH_SANDSTONE = BLOCKS.register("slab_path_sandstone",
            () -> new SlabBlock(pathProps(SoundType.STONE)));
    public static final DeferredBlock<SlabBlock> SLAB_PATH_GRAVEL_SLABS = BLOCKS.register("slab_path_gravel_slabs",
            () -> new SlabBlock(pathProps(SoundType.STONE)));
    public static final DeferredBlock<SlabBlock> SLAB_PATH_OCHRE_TILES = BLOCKS.register("slab_path_ochre_tiles",
            () -> new SlabBlock(pathProps(SoundType.STONE)));
    public static final DeferredBlock<SlabBlock> SLAB_PATH_SNOW = BLOCKS.register("slab_path_snow",
            () -> new SlabBlock(pathProps(SoundType.SNOW)));

    // ========== Walls ==========
    public static final DeferredBlock<WallBlock> WALL_MUD_BRICK = BLOCKS.register("wall_mud_brick",
            () -> new WallBlock(earthProps()));
    public static final DeferredBlock<WallBlock> WALL_SANDSTONE_CARVED = BLOCKS.register("wall_sandstone_carved",
            () -> new WallBlock(sandstoneProps()));
    public static final DeferredBlock<WallBlock> WALL_SANDSTONE_RED_CARVED = BLOCKS.register("wall_sandstone_red_carved",
            () -> new WallBlock(sandstoneProps()));
    public static final DeferredBlock<WallBlock> WALL_SANDSTONE_OCHRE_CARVED = BLOCKS.register("wall_sandstone_ochre_carved",
            () -> new WallBlock(sandstoneProps()));
    public static final DeferredBlock<WallBlock> WALL_SNOW = BLOCKS.register("wall_snow",
            () -> new WallBlock(BlockBehaviour.Properties.of().strength(0.4f).sound(SoundType.SNOW)));

    // ========== Pane / Bars Blocks ==========
    public static final DeferredBlock<IronBarsBlock> PAPER_WALL = BLOCKS.register("paper_wall",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3f).sound(SoundType.WOOL).noOcclusion()));
    public static final DeferredBlock<IronBarsBlock> WOODEN_BARS = BLOCKS.register("wooden_bars",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3f).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredBlock<IronBarsBlock> WOODEN_BARS_INDIAN = BLOCKS.register("wooden_bars_indian",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3f).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredBlock<IronBarsBlock> WOODEN_BARS_ROSETTE = BLOCKS.register("wooden_bars_rosette",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3f).sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredBlock<IronBarsBlock> WOODEN_BARS_DARK = BLOCKS.register("wooden_bars_dark",
            () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(0.3f).sound(SoundType.WOOD).noOcclusion()));

    // ========== Functional Blocks ==========
    public static final DeferredBlock<Block> WET_BRICK = BLOCKS.registerSimpleBlock("wet_brick",
            BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.GRAVEL));
    public static final DeferredBlock<Block> SILK_WORM_BLOCK = BLOCKS.registerSimpleBlock("silk_worm",
            BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> SNAIL_SOIL = BLOCKS.registerSimpleBlock("snail_soil",
            BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.GRAVEL));
    public static final DeferredBlock<Block> SOD = BLOCKS.registerSimpleBlock("sod",
            BlockBehaviour.Properties.of().strength(0.6f).sound(SoundType.GRASS));
    public static final DeferredBlock<Block> ALCHEMIST_EXPLOSIVE = BLOCKS.registerSimpleBlock("alchemist_explosive",
            BlockBehaviour.Properties.of().strength(2.0f, 10.0f).sound(SoundType.STONE));
    public static final DeferredBlock<Block> ROSETTE = BLOCKS.registerSimpleBlock("rosette", stoneProps());
    public static final DeferredBlock<Block> STAINED_GLASS = BLOCKS.registerSimpleBlock("stained_glass",
            BlockBehaviour.Properties.of().strength(0.3f).sound(SoundType.GLASS).noOcclusion());
    public static final DeferredBlock<Block> MILL_STATUE = BLOCKS.registerSimpleBlock("mill_statue", stoneProps());

    // Ice & Snow
    public static final DeferredBlock<Block> ICE_BRICK = BLOCKS.registerSimpleBlock("ice_brick",
            BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.GLASS).friction(0.98f).noOcclusion());
    public static final DeferredBlock<Block> SNOW_BRICK = BLOCKS.registerSimpleBlock("snow_brick",
            BlockBehaviour.Properties.of().strength(0.4f).sound(SoundType.SNOW));

    // ========== Path Blocks ==========
    public static final DeferredBlock<Block> PATH_DIRT = BLOCKS.registerSimpleBlock("path_dirt", pathProps(SoundType.GRAVEL));
    public static final DeferredBlock<Block> PATH_GRAVEL = BLOCKS.registerSimpleBlock("path_gravel", pathProps(SoundType.GRAVEL));
    public static final DeferredBlock<Block> PATH_SLABS = BLOCKS.registerSimpleBlock("path_slabs", pathProps(SoundType.STONE));
    public static final DeferredBlock<Block> PATH_SANDSTONE = BLOCKS.registerSimpleBlock("path_sandstone", pathProps(SoundType.STONE));
    public static final DeferredBlock<Block> PATH_GRAVEL_SLABS = BLOCKS.registerSimpleBlock("path_gravel_slabs", pathProps(SoundType.STONE));
    public static final DeferredBlock<Block> PATH_OCHRE_TILES = BLOCKS.registerSimpleBlock("path_ochre_tiles", pathProps(SoundType.STONE));
    public static final DeferredBlock<Block> PATH_SNOW = BLOCKS.registerSimpleBlock("path_snow", pathProps(SoundType.SNOW));

    // ========== Crop Blocks ==========
    public static final DeferredBlock<Block> CROP_RICE = BLOCKS.register("crop_rice",
            () -> new MillCropBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
    public static final DeferredBlock<Block> CROP_TURMERIC = BLOCKS.register("crop_turmeric",
            () -> new MillCropBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
    public static final DeferredBlock<Block> CROP_MAIZE = BLOCKS.register("crop_maize",
            () -> new MillCropBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
    public static final DeferredBlock<Block> CROP_COTTON = BLOCKS.register("crop_cotton",
            () -> new MillCropBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
    public static final DeferredBlock<Block> CROP_VINE = BLOCKS.register("crop_vine",
            () -> new MillCropBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP)));

    // ========== Saplings & Leaves ==========
    public static final DeferredBlock<Block> SAPLING_APPLE = BLOCKS.registerSimpleBlock("sapling_appletree",
            BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
    public static final DeferredBlock<Block> SAPLING_OLIVE = BLOCKS.registerSimpleBlock("sapling_olivetree",
            BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
    public static final DeferredBlock<Block> SAPLING_PISTACHIO = BLOCKS.registerSimpleBlock("sapling_pistachio",
            BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
    public static final DeferredBlock<Block> SAPLING_CHERRY = BLOCKS.registerSimpleBlock("sapling_cherry",
            BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.GRASS));
    public static final DeferredBlock<Block> SAPLING_SAKURA = BLOCKS.registerSimpleBlock("sapling_sakura",
            BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.GRASS));

    public static final DeferredBlock<Block> LEAVES_APPLE = BLOCKS.registerSimpleBlock("leaves_appletree",
            BlockBehaviour.Properties.of().strength(0.2f).sound(SoundType.GRASS).noOcclusion().randomTicks());
    public static final DeferredBlock<Block> LEAVES_OLIVE = BLOCKS.registerSimpleBlock("leaves_olivetree",
            BlockBehaviour.Properties.of().strength(0.2f).sound(SoundType.GRASS).noOcclusion().randomTicks());
    public static final DeferredBlock<Block> LEAVES_PISTACHIO = BLOCKS.registerSimpleBlock("leaves_pistachio",
            BlockBehaviour.Properties.of().strength(0.2f).sound(SoundType.GRASS).noOcclusion().randomTicks());
    public static final DeferredBlock<Block> LEAVES_CHERRY = BLOCKS.registerSimpleBlock("leaves_cherry",
            BlockBehaviour.Properties.of().strength(0.2f).sound(SoundType.GRASS).noOcclusion().randomTicks());
    public static final DeferredBlock<Block> LEAVES_SAKURA = BLOCKS.registerSimpleBlock("leaves_sakura",
            BlockBehaviour.Properties.of().strength(0.2f).sound(SoundType.GRASS).noOcclusion().randomTicks());

    // ========== Fruit Leaves ==========
    public static final DeferredBlock<BlockFruitLeaves> FRUIT_LEAVES = BLOCKS.register("fruit_leaves",
            () -> new BlockFruitLeaves(BlockBehaviour.Properties.of().strength(0.2f).sound(SoundType.GRASS).noOcclusion().randomTicks()));

    // ========== Special Blocks (with BlockEntity wiring) ==========
    public static final DeferredBlock<BlockLockedChest> LOCKED_CHEST = BLOCKS.register("locked_chest",
            () -> new BlockLockedChest(BlockBehaviour.Properties.of().strength(2.5f).sound(SoundType.WOOD)));
    public static final DeferredBlock<BlockFirePit> FIRE_PIT = BLOCKS.register("fire_pit",
            () -> new BlockFirePit(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.STONE)
                    .lightLevel(state -> state.getValue(BlockFirePit.LIT) ? 15 : 0)));
    public static final DeferredBlock<BlockPanel> PANEL = BLOCKS.register("panel",
            () -> new BlockPanel(BlockBehaviour.Properties.of().strength(2.5f).sound(SoundType.WOOD)));
    public static final DeferredBlock<BlockImportTable> IMPORT_TABLE = BLOCKS.register("import_table",
            () -> new BlockImportTable(BlockBehaviour.Properties.of().strength(2.5f).sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> BED_STRAW = BLOCKS.registerSimpleBlock("bed_straw",
            BlockBehaviour.Properties.of().strength(0.2f).sound(SoundType.WOOL));
    public static final DeferredBlock<Block> BED_CHARPOY = BLOCKS.registerSimpleBlock("bed_charpoy",
            BlockBehaviour.Properties.of().strength(0.2f).sound(SoundType.WOOL));

    // ========== Mock Blocks (invisible plan markers) ==========
    private static BlockBehaviour.Properties mockProps() {
        return BlockBehaviour.Properties.of().strength(-1.0f, 3600000.0f).noLootTable().noOcclusion();
    }
    public static final DeferredBlock<Block> MARKER_BLOCK = BLOCKS.registerSimpleBlock("markerblock", mockProps());
    public static final DeferredBlock<Block> MAIN_CHEST = BLOCKS.registerSimpleBlock("mainchest", mockProps());
    public static final DeferredBlock<Block> ANIMAL_SPAWN = BLOCKS.registerSimpleBlock("animalspawn", mockProps());
    public static final DeferredBlock<Block> SOURCE = BLOCKS.registerSimpleBlock("source", mockProps());
    public static final DeferredBlock<Block> FREE_BLOCK = BLOCKS.registerSimpleBlock("freeblock", mockProps());
    public static final DeferredBlock<Block> TREE_SPAWN = BLOCKS.registerSimpleBlock("treespawn", mockProps());
    public static final DeferredBlock<Block> SOIL_BLOCK = BLOCKS.registerSimpleBlock("soil", mockProps());
    public static final DeferredBlock<Block> DECOR_BLOCK = BLOCKS.registerSimpleBlock("decorblock", mockProps());

    /**
     * Called from Millenaire2 constructor to force class loading and register all blocks.
     */
    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }

    public static void init() {
        // Class loading triggers all static final fields above
    }
}

