package org.dizzymii.millenaire2.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.block.BlockFirePit;
import org.dizzymii.millenaire2.init.ModBlocks;

public class MillBlockStateProvider extends BlockStateProvider {

    public MillBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Millenaire2.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // ===== Simple cube blocks =====
        simpleBlockWithItem(ModBlocks.STONE_DECORATION);
        simpleBlockWithItem(ModBlocks.COOKED_BRICK);

        // Painted bricks
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_WHITE);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_ORANGE);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_MAGENTA);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_LIGHT_BLUE);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_YELLOW);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_LIME);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_PINK);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_GRAY);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_LIGHT_GRAY);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_CYAN);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_PURPLE);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_BLUE);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_BROWN);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_GREEN);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_RED);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_BLACK);

        // Decorated painted bricks
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_WHITE);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_ORANGE);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_MAGENTA);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_LIGHT_BLUE);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_YELLOW);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_LIME);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_PINK);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_GRAY);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_LIGHT_GRAY);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_CYAN);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_PURPLE);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_BLUE);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_BROWN);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_GREEN);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_RED);
        simpleBlockWithItem(ModBlocks.PAINTED_BRICK_DECO_BLACK);

        // Wood
        simpleBlockWithItem(ModBlocks.TIMBER_FRAME_PLAIN);
        simpleBlockWithItem(ModBlocks.TIMBER_FRAME_CROSS);
        simpleBlockWithItem(ModBlocks.THATCH);

        // Earth
        simpleBlockWithItem(ModBlocks.MUD_BRICK);
        simpleBlockWithItem(ModBlocks.MUD_BRICK_EXTENDED);

        // Sandstone
        simpleBlockWithItem(ModBlocks.SANDSTONE_CARVED);
        simpleBlockWithItem(ModBlocks.SANDSTONE_RED_CARVED);
        simpleBlockWithItem(ModBlocks.SANDSTONE_OCHRE_CARVED);
        simpleBlockWithItem(ModBlocks.SANDSTONE_DECORATED);
        simpleBlockWithItem(ModBlocks.BYZANTINE_STONE_ORNAMENT);
        simpleBlockWithItem(ModBlocks.BYZANTINE_SANDSTONE_ORNAMENT);

        // Tiles
        simpleBlockWithItem(ModBlocks.BYZANTINE_TILES);
        simpleBlockWithItem(ModBlocks.BYZANTINE_STONE_TILES);
        simpleBlockWithItem(ModBlocks.BYZANTINE_SANDSTONE_TILES);
        simpleBlockWithItem(ModBlocks.GRAY_TILES);
        simpleBlockWithItem(ModBlocks.GREEN_TILES);
        simpleBlockWithItem(ModBlocks.RED_TILES);

        // Functional
        simpleBlockWithItem(ModBlocks.WET_BRICK);
        simpleBlockWithItem(ModBlocks.SILK_WORM_BLOCK);
        simpleBlockWithItem(ModBlocks.SNAIL_SOIL);
        simpleBlockWithItem(ModBlocks.SOD);
        simpleBlockWithItem(ModBlocks.ALCHEMIST_EXPLOSIVE);
        simpleBlockWithItem(ModBlocks.ROSETTE);
        simpleBlockWithItem(ModBlocks.STAINED_GLASS);
        simpleBlockWithItem(ModBlocks.MILL_STATUE);
        simpleBlockWithItem(ModBlocks.ICE_BRICK);
        simpleBlockWithItem(ModBlocks.SNOW_BRICK);

        // Paths
        simpleBlockWithItem(ModBlocks.PATH_DIRT);
        simpleBlockWithItem(ModBlocks.PATH_GRAVEL);
        simpleBlockWithItem(ModBlocks.PATH_SLABS);
        simpleBlockWithItem(ModBlocks.PATH_SANDSTONE);
        simpleBlockWithItem(ModBlocks.PATH_GRAVEL_SLABS);
        simpleBlockWithItem(ModBlocks.PATH_OCHRE_TILES);
        simpleBlockWithItem(ModBlocks.PATH_SNOW);

        // Special / functional (horizontal facing blocks)
        horizontalBlockWithItem(ModBlocks.LOCKED_CHEST);
        firePitBlock(ModBlocks.FIRE_PIT);
        horizontalBlockWithItem(ModBlocks.PANEL);
        horizontalBlockWithItem(ModBlocks.IMPORT_TABLE);
        simpleBlockWithItem(ModBlocks.BED_STRAW);
        simpleBlockWithItem(ModBlocks.BED_CHARPOY);

        // Mock blocks
        simpleBlockWithItem(ModBlocks.MARKER_BLOCK);
        simpleBlockWithItem(ModBlocks.MAIN_CHEST);
        simpleBlockWithItem(ModBlocks.ANIMAL_SPAWN);
        simpleBlockWithItem(ModBlocks.SOURCE);
        simpleBlockWithItem(ModBlocks.FREE_BLOCK);
        simpleBlockWithItem(ModBlocks.TREE_SPAWN);
        simpleBlockWithItem(ModBlocks.SOIL_BLOCK);
        simpleBlockWithItem(ModBlocks.DECOR_BLOCK);

        // ===== Stairs =====
        stairsBlock(ModBlocks.STAIRS_TIMBERFRAME, ModBlocks.TIMBER_FRAME_PLAIN);
        stairsBlock(ModBlocks.STAIRS_MUDBRICK, ModBlocks.MUD_BRICK);
        stairsBlock(ModBlocks.STAIRS_COOKEDBRICK, ModBlocks.COOKED_BRICK);
        stairsBlock(ModBlocks.STAIRS_THATCH, ModBlocks.THATCH);
        stairsBlock(ModBlocks.STAIRS_SANDSTONE_CARVED, ModBlocks.SANDSTONE_CARVED);
        stairsBlock(ModBlocks.STAIRS_SANDSTONE_RED_CARVED, ModBlocks.SANDSTONE_RED_CARVED);
        stairsBlock(ModBlocks.STAIRS_SANDSTONE_OCHRE_CARVED, ModBlocks.SANDSTONE_OCHRE_CARVED);
        stairsBlock(ModBlocks.STAIRS_BYZANTINE_TILES, ModBlocks.BYZANTINE_TILES);
        stairsBlock(ModBlocks.STAIRS_GRAY_TILES, ModBlocks.GRAY_TILES);
        stairsBlock(ModBlocks.STAIRS_GREEN_TILES, ModBlocks.GREEN_TILES);
        stairsBlock(ModBlocks.STAIRS_RED_TILES, ModBlocks.RED_TILES);

        // ===== Slabs =====
        slabBlock(ModBlocks.SLAB_WOOD_DECO, ModBlocks.TIMBER_FRAME_PLAIN);
        slabBlock(ModBlocks.SLAB_STONE_DECO, ModBlocks.STONE_DECORATION);
        slabBlock(ModBlocks.SLAB_SANDSTONE_CARVED, ModBlocks.SANDSTONE_CARVED);
        slabBlock(ModBlocks.SLAB_SANDSTONE_RED_CARVED, ModBlocks.SANDSTONE_RED_CARVED);
        slabBlock(ModBlocks.SLAB_SANDSTONE_OCHRE_CARVED, ModBlocks.SANDSTONE_OCHRE_CARVED);
        slabBlock(ModBlocks.SLAB_BYZANTINE_TILES, ModBlocks.BYZANTINE_TILES);
        slabBlock(ModBlocks.SLAB_GRAY_TILES, ModBlocks.GRAY_TILES);
        slabBlock(ModBlocks.SLAB_GREEN_TILES, ModBlocks.GREEN_TILES);
        slabBlock(ModBlocks.SLAB_RED_TILES, ModBlocks.RED_TILES);

        // Path slabs
        slabBlock(ModBlocks.SLAB_PATH_DIRT, ModBlocks.PATH_DIRT);
        slabBlock(ModBlocks.SLAB_PATH_GRAVEL, ModBlocks.PATH_GRAVEL);
        slabBlock(ModBlocks.SLAB_PATH_SLABS, ModBlocks.PATH_SLABS);
        slabBlock(ModBlocks.SLAB_PATH_SANDSTONE, ModBlocks.PATH_SANDSTONE);
        slabBlock(ModBlocks.SLAB_PATH_GRAVEL_SLABS, ModBlocks.PATH_GRAVEL_SLABS);
        slabBlock(ModBlocks.SLAB_PATH_OCHRE_TILES, ModBlocks.PATH_OCHRE_TILES);
        slabBlock(ModBlocks.SLAB_PATH_SNOW, ModBlocks.PATH_SNOW);

        // ===== Walls =====
        wallBlockWithItem(ModBlocks.WALL_MUD_BRICK, ModBlocks.MUD_BRICK);
        wallBlockWithItem(ModBlocks.WALL_SANDSTONE_CARVED, ModBlocks.SANDSTONE_CARVED);
        wallBlockWithItem(ModBlocks.WALL_SANDSTONE_RED_CARVED, ModBlocks.SANDSTONE_RED_CARVED);
        wallBlockWithItem(ModBlocks.WALL_SANDSTONE_OCHRE_CARVED, ModBlocks.SANDSTONE_OCHRE_CARVED);
        wallBlockWithItem(ModBlocks.WALL_SNOW, ModBlocks.SNOW_BRICK);

        // ===== Panes / Bars =====
        paneBlockWithItem(ModBlocks.PAPER_WALL);
        paneBlockWithItem(ModBlocks.WOODEN_BARS);
        paneBlockWithItem(ModBlocks.WOODEN_BARS_INDIAN);
        paneBlockWithItem(ModBlocks.WOODEN_BARS_ROSETTE);
        paneBlockWithItem(ModBlocks.WOODEN_BARS_DARK);

        // ===== Crops =====
        crossBlock(ModBlocks.CROP_RICE);
        crossBlock(ModBlocks.CROP_TURMERIC);
        crossBlock(ModBlocks.CROP_MAIZE);
        crossBlock(ModBlocks.CROP_COTTON);
        crossBlock(ModBlocks.CROP_VINE);

        // ===== Saplings =====
        crossBlock(ModBlocks.SAPLING_APPLE);
        crossBlock(ModBlocks.SAPLING_OLIVE);
        crossBlock(ModBlocks.SAPLING_PISTACHIO);
        crossBlock(ModBlocks.SAPLING_CHERRY);
        crossBlock(ModBlocks.SAPLING_SAKURA);

        // ===== Leaves =====
        simpleBlockWithItem(ModBlocks.LEAVES_APPLE);
        simpleBlockWithItem(ModBlocks.LEAVES_OLIVE);
        simpleBlockWithItem(ModBlocks.LEAVES_PISTACHIO);
        simpleBlockWithItem(ModBlocks.LEAVES_CHERRY);
        simpleBlockWithItem(ModBlocks.LEAVES_SAKURA);
        simpleBlockWithItem(ModBlocks.FRUIT_LEAVES);
    }

    // ===== Helpers =====

    private void simpleBlockWithItem(DeferredBlock<? extends Block> block) {
        simpleBlockWithItem(block.get(), cubeAll(block.get()));
    }

    private void stairsBlock(DeferredBlock<StairBlock> stairBlock, DeferredBlock<Block> textureSource) {
        stairsBlock(stairBlock.get(), blockTexture(textureSource.get()));
        simpleBlockItem(stairBlock.get(), new ModelFile.UncheckedModelFile(
                modLoc("block/" + stairBlock.getId().getPath())));
    }

    private void slabBlock(DeferredBlock<SlabBlock> slabBlock, DeferredBlock<Block> fullBlock) {
        slabBlock(slabBlock.get(), blockTexture(fullBlock.get()), blockTexture(fullBlock.get()));
        simpleBlockItem(slabBlock.get(), new ModelFile.UncheckedModelFile(
                modLoc("block/" + slabBlock.getId().getPath())));
    }

    private void wallBlockWithItem(DeferredBlock<WallBlock> wallBlock, DeferredBlock<Block> textureSource) {
        wallBlock(wallBlock.get(), blockTexture(textureSource.get()));
        itemModels().wallInventory(wallBlock.getId().getPath(),
                blockTexture(textureSource.get()));
    }

    private void paneBlockWithItem(DeferredBlock<IronBarsBlock> paneBlock) {
        paneBlockWithRenderType(paneBlock.get(),
                blockTexture(paneBlock.get()),
                modLoc("block/" + paneBlock.getId().getPath() + "_top"),
                "cutout");
        simpleBlockItem(paneBlock.get(), new ModelFile.UncheckedModelFile(
                modLoc("block/" + paneBlock.getId().getPath())));
    }

    private void crossBlock(DeferredBlock<Block> block) {
        simpleBlock(block.get(), models().cross(block.getId().getPath(),
                blockTexture(block.get())).renderType("cutout"));
    }

    private void horizontalBlockWithItem(DeferredBlock<? extends Block> block) {
        ModelFile model = cubeAll(block.get());
        horizontalBlock(block.get(), model);
        simpleBlockItem(block.get(), model);
    }

    private void firePitBlock(DeferredBlock<BlockFirePit> block) {
        ModelFile offModel = cubeAll(block.get());
        ModelFile onModel = models().cubeAll(block.getId().getPath() + "_lit", blockTexture(block.get()));
        getVariantBuilder(block.get()).forAllStates(state -> {
            int yRot = ((int) state.getValue(BlockFirePit.FACING).toYRot() + 180) % 360;
            ModelFile m = state.getValue(BlockFirePit.LIT) ? onModel : offModel;
            return ConfiguredModel.builder().modelFile(m).rotationY(yRot).build();
        });
        simpleBlockItem(block.get(), offModel);
    }
}

