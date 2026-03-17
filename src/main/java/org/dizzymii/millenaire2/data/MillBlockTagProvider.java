package org.dizzymii.millenaire2.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.block.MillBlocks;

import java.util.concurrent.CompletableFuture;

public class MillBlockTagProvider extends BlockTagsProvider {

    public MillBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Millenaire2.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Mineable with pickaxe
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(MillBlocks.STONE_DECORATION.get())
                .add(MillBlocks.COOKED_BRICK.get())
                .add(MillBlocks.PAINTED_BRICK_WHITE.get())
                .add(MillBlocks.PAINTED_BRICK_ORANGE.get())
                .add(MillBlocks.PAINTED_BRICK_MAGENTA.get())
                .add(MillBlocks.PAINTED_BRICK_LIGHT_BLUE.get())
                .add(MillBlocks.PAINTED_BRICK_YELLOW.get())
                .add(MillBlocks.PAINTED_BRICK_LIME.get())
                .add(MillBlocks.PAINTED_BRICK_PINK.get())
                .add(MillBlocks.PAINTED_BRICK_GRAY.get())
                .add(MillBlocks.PAINTED_BRICK_LIGHT_GRAY.get())
                .add(MillBlocks.PAINTED_BRICK_CYAN.get())
                .add(MillBlocks.PAINTED_BRICK_PURPLE.get())
                .add(MillBlocks.PAINTED_BRICK_BLUE.get())
                .add(MillBlocks.PAINTED_BRICK_BROWN.get())
                .add(MillBlocks.PAINTED_BRICK_GREEN.get())
                .add(MillBlocks.PAINTED_BRICK_RED.get())
                .add(MillBlocks.PAINTED_BRICK_BLACK.get())
                .add(MillBlocks.SANDSTONE_CARVED.get())
                .add(MillBlocks.SANDSTONE_RED_CARVED.get())
                .add(MillBlocks.SANDSTONE_OCHRE_CARVED.get())
                .add(MillBlocks.SANDSTONE_DECORATED.get())
                .add(MillBlocks.BYZANTINE_STONE_ORNAMENT.get())
                .add(MillBlocks.BYZANTINE_SANDSTONE_ORNAMENT.get())
                .add(MillBlocks.BYZANTINE_TILES.get())
                .add(MillBlocks.BYZANTINE_STONE_TILES.get())
                .add(MillBlocks.BYZANTINE_SANDSTONE_TILES.get())
                .add(MillBlocks.GRAY_TILES.get())
                .add(MillBlocks.GREEN_TILES.get())
                .add(MillBlocks.RED_TILES.get())
                .add(MillBlocks.ICE_BRICK.get())
                .add(MillBlocks.ROSETTE.get())
                .add(MillBlocks.MILL_STATUE.get())
                .add(MillBlocks.ALCHEMIST_EXPLOSIVE.get())
                .add(MillBlocks.FIRE_PIT.get())
                .add(MillBlocks.LOCKED_CHEST.get())
                // Stairs
                .add(MillBlocks.STAIRS_COOKEDBRICK.get())
                .add(MillBlocks.STAIRS_SANDSTONE_CARVED.get())
                .add(MillBlocks.STAIRS_SANDSTONE_RED_CARVED.get())
                .add(MillBlocks.STAIRS_SANDSTONE_OCHRE_CARVED.get())
                .add(MillBlocks.STAIRS_BYZANTINE_TILES.get())
                .add(MillBlocks.STAIRS_GRAY_TILES.get())
                .add(MillBlocks.STAIRS_GREEN_TILES.get())
                .add(MillBlocks.STAIRS_RED_TILES.get())
                // Slabs
                .add(MillBlocks.SLAB_STONE_DECO.get())
                .add(MillBlocks.SLAB_SANDSTONE_CARVED.get())
                .add(MillBlocks.SLAB_SANDSTONE_RED_CARVED.get())
                .add(MillBlocks.SLAB_SANDSTONE_OCHRE_CARVED.get())
                .add(MillBlocks.SLAB_BYZANTINE_TILES.get())
                .add(MillBlocks.SLAB_GRAY_TILES.get())
                .add(MillBlocks.SLAB_GREEN_TILES.get())
                .add(MillBlocks.SLAB_RED_TILES.get())
                // Walls
                .add(MillBlocks.WALL_SANDSTONE_CARVED.get())
                .add(MillBlocks.WALL_SANDSTONE_RED_CARVED.get())
                .add(MillBlocks.WALL_SANDSTONE_OCHRE_CARVED.get());

        // Mineable with axe
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(MillBlocks.TIMBER_FRAME_PLAIN.get())
                .add(MillBlocks.TIMBER_FRAME_CROSS.get())
                .add(MillBlocks.THATCH.get())
                .add(MillBlocks.PANEL.get())
                .add(MillBlocks.IMPORT_TABLE.get())
                .add(MillBlocks.BED_STRAW.get())
                .add(MillBlocks.BED_CHARPOY.get())
                .add(MillBlocks.STAIRS_TIMBERFRAME.get())
                .add(MillBlocks.STAIRS_THATCH.get())
                .add(MillBlocks.SLAB_WOOD_DECO.get());

        // Mineable with shovel
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(MillBlocks.MUD_BRICK.get())
                .add(MillBlocks.MUD_BRICK_EXTENDED.get())
                .add(MillBlocks.WET_BRICK.get())
                .add(MillBlocks.SNAIL_SOIL.get())
                .add(MillBlocks.SOD.get())
                .add(MillBlocks.SNOW_BRICK.get())
                .add(MillBlocks.PATH_DIRT.get())
                .add(MillBlocks.PATH_GRAVEL.get())
                .add(MillBlocks.PATH_SNOW.get())
                .add(MillBlocks.STAIRS_MUDBRICK.get())
                .add(MillBlocks.WALL_MUD_BRICK.get())
                .add(MillBlocks.WALL_SNOW.get());

        // Walls tag
        tag(BlockTags.WALLS)
                .add(MillBlocks.WALL_MUD_BRICK.get())
                .add(MillBlocks.WALL_SANDSTONE_CARVED.get())
                .add(MillBlocks.WALL_SANDSTONE_RED_CARVED.get())
                .add(MillBlocks.WALL_SANDSTONE_OCHRE_CARVED.get())
                .add(MillBlocks.WALL_SNOW.get());

        // Stairs tag
        tag(BlockTags.STAIRS)
                .add(MillBlocks.STAIRS_TIMBERFRAME.get())
                .add(MillBlocks.STAIRS_MUDBRICK.get())
                .add(MillBlocks.STAIRS_COOKEDBRICK.get())
                .add(MillBlocks.STAIRS_THATCH.get())
                .add(MillBlocks.STAIRS_SANDSTONE_CARVED.get())
                .add(MillBlocks.STAIRS_SANDSTONE_RED_CARVED.get())
                .add(MillBlocks.STAIRS_SANDSTONE_OCHRE_CARVED.get())
                .add(MillBlocks.STAIRS_BYZANTINE_TILES.get())
                .add(MillBlocks.STAIRS_GRAY_TILES.get())
                .add(MillBlocks.STAIRS_GREEN_TILES.get())
                .add(MillBlocks.STAIRS_RED_TILES.get());

        // Slabs tag
        tag(BlockTags.SLABS)
                .add(MillBlocks.SLAB_WOOD_DECO.get())
                .add(MillBlocks.SLAB_STONE_DECO.get())
                .add(MillBlocks.SLAB_SANDSTONE_CARVED.get())
                .add(MillBlocks.SLAB_SANDSTONE_RED_CARVED.get())
                .add(MillBlocks.SLAB_SANDSTONE_OCHRE_CARVED.get())
                .add(MillBlocks.SLAB_BYZANTINE_TILES.get())
                .add(MillBlocks.SLAB_GRAY_TILES.get())
                .add(MillBlocks.SLAB_GREEN_TILES.get())
                .add(MillBlocks.SLAB_RED_TILES.get())
                .add(MillBlocks.SLAB_PATH_DIRT.get())
                .add(MillBlocks.SLAB_PATH_GRAVEL.get())
                .add(MillBlocks.SLAB_PATH_SLABS.get())
                .add(MillBlocks.SLAB_PATH_SANDSTONE.get())
                .add(MillBlocks.SLAB_PATH_GRAVEL_SLABS.get())
                .add(MillBlocks.SLAB_PATH_OCHRE_TILES.get())
                .add(MillBlocks.SLAB_PATH_SNOW.get());

        // Leaves tag
        tag(BlockTags.LEAVES)
                .add(MillBlocks.LEAVES_APPLE.get())
                .add(MillBlocks.LEAVES_OLIVE.get())
                .add(MillBlocks.LEAVES_PISTACHIO.get())
                .add(MillBlocks.LEAVES_CHERRY.get())
                .add(MillBlocks.LEAVES_SAKURA.get());

        // Saplings tag
        tag(BlockTags.SAPLINGS)
                .add(MillBlocks.SAPLING_APPLE.get())
                .add(MillBlocks.SAPLING_OLIVE.get())
                .add(MillBlocks.SAPLING_PISTACHIO.get())
                .add(MillBlocks.SAPLING_CHERRY.get())
                .add(MillBlocks.SAPLING_SAKURA.get());

        // Crops tag
        tag(BlockTags.CROPS)
                .add(MillBlocks.CROP_RICE.get())
                .add(MillBlocks.CROP_TURMERIC.get())
                .add(MillBlocks.CROP_MAIZE.get())
                .add(MillBlocks.CROP_COTTON.get())
                .add(MillBlocks.CROP_VINE.get());
    }
}
