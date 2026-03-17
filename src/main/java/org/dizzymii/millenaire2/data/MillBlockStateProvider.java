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
import org.dizzymii.millenaire2.block.MillBlocks;

public class MillBlockStateProvider extends BlockStateProvider {

    public MillBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Millenaire2.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // ===== Simple cube blocks =====
        simpleBlockWithItem(MillBlocks.STONE_DECORATION);
        simpleBlockWithItem(MillBlocks.COOKED_BRICK);

        // Painted bricks
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_WHITE);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_ORANGE);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_MAGENTA);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_LIGHT_BLUE);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_YELLOW);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_LIME);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_PINK);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_GRAY);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_LIGHT_GRAY);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_CYAN);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_PURPLE);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_BLUE);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_BROWN);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_GREEN);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_RED);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_BLACK);

        // Decorated painted bricks
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_WHITE);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_ORANGE);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_MAGENTA);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_LIGHT_BLUE);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_YELLOW);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_LIME);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_PINK);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_GRAY);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_LIGHT_GRAY);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_CYAN);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_PURPLE);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_BLUE);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_BROWN);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_GREEN);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_RED);
        simpleBlockWithItem(MillBlocks.PAINTED_BRICK_DECO_BLACK);

        // Wood
        simpleBlockWithItem(MillBlocks.TIMBER_FRAME_PLAIN);
        simpleBlockWithItem(MillBlocks.TIMBER_FRAME_CROSS);
        simpleBlockWithItem(MillBlocks.THATCH);

        // Earth
        simpleBlockWithItem(MillBlocks.MUD_BRICK);
        simpleBlockWithItem(MillBlocks.MUD_BRICK_EXTENDED);

        // Sandstone
        simpleBlockWithItem(MillBlocks.SANDSTONE_CARVED);
        simpleBlockWithItem(MillBlocks.SANDSTONE_RED_CARVED);
        simpleBlockWithItem(MillBlocks.SANDSTONE_OCHRE_CARVED);
        simpleBlockWithItem(MillBlocks.SANDSTONE_DECORATED);
        simpleBlockWithItem(MillBlocks.BYZANTINE_STONE_ORNAMENT);
        simpleBlockWithItem(MillBlocks.BYZANTINE_SANDSTONE_ORNAMENT);

        // Tiles
        simpleBlockWithItem(MillBlocks.BYZANTINE_TILES);
        simpleBlockWithItem(MillBlocks.BYZANTINE_STONE_TILES);
        simpleBlockWithItem(MillBlocks.BYZANTINE_SANDSTONE_TILES);
        simpleBlockWithItem(MillBlocks.GRAY_TILES);
        simpleBlockWithItem(MillBlocks.GREEN_TILES);
        simpleBlockWithItem(MillBlocks.RED_TILES);

        // Functional
        simpleBlockWithItem(MillBlocks.WET_BRICK);
        simpleBlockWithItem(MillBlocks.SILK_WORM_BLOCK);
        simpleBlockWithItem(MillBlocks.SNAIL_SOIL);
        simpleBlockWithItem(MillBlocks.SOD);
        simpleBlockWithItem(MillBlocks.ALCHEMIST_EXPLOSIVE);
        simpleBlockWithItem(MillBlocks.ROSETTE);
        simpleBlockWithItem(MillBlocks.STAINED_GLASS);
        simpleBlockWithItem(MillBlocks.MILL_STATUE);
        simpleBlockWithItem(MillBlocks.ICE_BRICK);
        simpleBlockWithItem(MillBlocks.SNOW_BRICK);

        // Paths
        simpleBlockWithItem(MillBlocks.PATH_DIRT);
        simpleBlockWithItem(MillBlocks.PATH_GRAVEL);
        simpleBlockWithItem(MillBlocks.PATH_SLABS);
        simpleBlockWithItem(MillBlocks.PATH_SANDSTONE);
        simpleBlockWithItem(MillBlocks.PATH_GRAVEL_SLABS);
        simpleBlockWithItem(MillBlocks.PATH_OCHRE_TILES);
        simpleBlockWithItem(MillBlocks.PATH_SNOW);

        // Special / functional (horizontal facing blocks)
        horizontalBlockWithItem(MillBlocks.LOCKED_CHEST);
        firePitBlock(MillBlocks.FIRE_PIT);
        horizontalBlockWithItem(MillBlocks.PANEL);
        horizontalBlockWithItem(MillBlocks.IMPORT_TABLE);
        simpleBlockWithItem(MillBlocks.BED_STRAW);
        simpleBlockWithItem(MillBlocks.BED_CHARPOY);

        // Mock blocks
        simpleBlockWithItem(MillBlocks.MARKER_BLOCK);
        simpleBlockWithItem(MillBlocks.MAIN_CHEST);
        simpleBlockWithItem(MillBlocks.ANIMAL_SPAWN);
        simpleBlockWithItem(MillBlocks.SOURCE);
        simpleBlockWithItem(MillBlocks.FREE_BLOCK);
        simpleBlockWithItem(MillBlocks.TREE_SPAWN);
        simpleBlockWithItem(MillBlocks.SOIL_BLOCK);
        simpleBlockWithItem(MillBlocks.DECOR_BLOCK);

        // ===== Stairs =====
        stairsBlock(MillBlocks.STAIRS_TIMBERFRAME, MillBlocks.TIMBER_FRAME_PLAIN);
        stairsBlock(MillBlocks.STAIRS_MUDBRICK, MillBlocks.MUD_BRICK);
        stairsBlock(MillBlocks.STAIRS_COOKEDBRICK, MillBlocks.COOKED_BRICK);
        stairsBlock(MillBlocks.STAIRS_THATCH, MillBlocks.THATCH);
        stairsBlock(MillBlocks.STAIRS_SANDSTONE_CARVED, MillBlocks.SANDSTONE_CARVED);
        stairsBlock(MillBlocks.STAIRS_SANDSTONE_RED_CARVED, MillBlocks.SANDSTONE_RED_CARVED);
        stairsBlock(MillBlocks.STAIRS_SANDSTONE_OCHRE_CARVED, MillBlocks.SANDSTONE_OCHRE_CARVED);
        stairsBlock(MillBlocks.STAIRS_BYZANTINE_TILES, MillBlocks.BYZANTINE_TILES);
        stairsBlock(MillBlocks.STAIRS_GRAY_TILES, MillBlocks.GRAY_TILES);
        stairsBlock(MillBlocks.STAIRS_GREEN_TILES, MillBlocks.GREEN_TILES);
        stairsBlock(MillBlocks.STAIRS_RED_TILES, MillBlocks.RED_TILES);

        // ===== Slabs =====
        slabBlock(MillBlocks.SLAB_WOOD_DECO, MillBlocks.TIMBER_FRAME_PLAIN);
        slabBlock(MillBlocks.SLAB_STONE_DECO, MillBlocks.STONE_DECORATION);
        slabBlock(MillBlocks.SLAB_SANDSTONE_CARVED, MillBlocks.SANDSTONE_CARVED);
        slabBlock(MillBlocks.SLAB_SANDSTONE_RED_CARVED, MillBlocks.SANDSTONE_RED_CARVED);
        slabBlock(MillBlocks.SLAB_SANDSTONE_OCHRE_CARVED, MillBlocks.SANDSTONE_OCHRE_CARVED);
        slabBlock(MillBlocks.SLAB_BYZANTINE_TILES, MillBlocks.BYZANTINE_TILES);
        slabBlock(MillBlocks.SLAB_GRAY_TILES, MillBlocks.GRAY_TILES);
        slabBlock(MillBlocks.SLAB_GREEN_TILES, MillBlocks.GREEN_TILES);
        slabBlock(MillBlocks.SLAB_RED_TILES, MillBlocks.RED_TILES);

        // Path slabs
        slabBlock(MillBlocks.SLAB_PATH_DIRT, MillBlocks.PATH_DIRT);
        slabBlock(MillBlocks.SLAB_PATH_GRAVEL, MillBlocks.PATH_GRAVEL);
        slabBlock(MillBlocks.SLAB_PATH_SLABS, MillBlocks.PATH_SLABS);
        slabBlock(MillBlocks.SLAB_PATH_SANDSTONE, MillBlocks.PATH_SANDSTONE);
        slabBlock(MillBlocks.SLAB_PATH_GRAVEL_SLABS, MillBlocks.PATH_GRAVEL_SLABS);
        slabBlock(MillBlocks.SLAB_PATH_OCHRE_TILES, MillBlocks.PATH_OCHRE_TILES);
        slabBlock(MillBlocks.SLAB_PATH_SNOW, MillBlocks.PATH_SNOW);

        // ===== Walls =====
        wallBlockWithItem(MillBlocks.WALL_MUD_BRICK, MillBlocks.MUD_BRICK);
        wallBlockWithItem(MillBlocks.WALL_SANDSTONE_CARVED, MillBlocks.SANDSTONE_CARVED);
        wallBlockWithItem(MillBlocks.WALL_SANDSTONE_RED_CARVED, MillBlocks.SANDSTONE_RED_CARVED);
        wallBlockWithItem(MillBlocks.WALL_SANDSTONE_OCHRE_CARVED, MillBlocks.SANDSTONE_OCHRE_CARVED);
        wallBlockWithItem(MillBlocks.WALL_SNOW, MillBlocks.SNOW_BRICK);

        // ===== Panes / Bars =====
        paneBlockWithItem(MillBlocks.PAPER_WALL);
        paneBlockWithItem(MillBlocks.WOODEN_BARS);
        paneBlockWithItem(MillBlocks.WOODEN_BARS_INDIAN);
        paneBlockWithItem(MillBlocks.WOODEN_BARS_ROSETTE);
        paneBlockWithItem(MillBlocks.WOODEN_BARS_DARK);

        // ===== Crops =====
        crossBlock(MillBlocks.CROP_RICE);
        crossBlock(MillBlocks.CROP_TURMERIC);
        crossBlock(MillBlocks.CROP_MAIZE);
        crossBlock(MillBlocks.CROP_COTTON);
        crossBlock(MillBlocks.CROP_VINE);

        // ===== Saplings =====
        crossBlock(MillBlocks.SAPLING_APPLE);
        crossBlock(MillBlocks.SAPLING_OLIVE);
        crossBlock(MillBlocks.SAPLING_PISTACHIO);
        crossBlock(MillBlocks.SAPLING_CHERRY);
        crossBlock(MillBlocks.SAPLING_SAKURA);

        // ===== Leaves =====
        simpleBlockWithItem(MillBlocks.LEAVES_APPLE);
        simpleBlockWithItem(MillBlocks.LEAVES_OLIVE);
        simpleBlockWithItem(MillBlocks.LEAVES_PISTACHIO);
        simpleBlockWithItem(MillBlocks.LEAVES_CHERRY);
        simpleBlockWithItem(MillBlocks.LEAVES_SAKURA);
        simpleBlockWithItem(MillBlocks.FRUIT_LEAVES);
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
