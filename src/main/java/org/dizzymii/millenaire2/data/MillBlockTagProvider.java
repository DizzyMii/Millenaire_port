package org.dizzymii.millenaire2.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.init.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class MillBlockTagProvider extends BlockTagsProvider {

    public MillBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Millenaire2.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Mineable with pickaxe
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.STONE_DECORATION.get())
                .add(ModBlocks.COOKED_BRICK.get())
                .add(ModBlocks.PAINTED_BRICK_WHITE.get())
                .add(ModBlocks.PAINTED_BRICK_ORANGE.get())
                .add(ModBlocks.PAINTED_BRICK_MAGENTA.get())
                .add(ModBlocks.PAINTED_BRICK_LIGHT_BLUE.get())
                .add(ModBlocks.PAINTED_BRICK_YELLOW.get())
                .add(ModBlocks.PAINTED_BRICK_LIME.get())
                .add(ModBlocks.PAINTED_BRICK_PINK.get())
                .add(ModBlocks.PAINTED_BRICK_GRAY.get())
                .add(ModBlocks.PAINTED_BRICK_LIGHT_GRAY.get())
                .add(ModBlocks.PAINTED_BRICK_CYAN.get())
                .add(ModBlocks.PAINTED_BRICK_PURPLE.get())
                .add(ModBlocks.PAINTED_BRICK_BLUE.get())
                .add(ModBlocks.PAINTED_BRICK_BROWN.get())
                .add(ModBlocks.PAINTED_BRICK_GREEN.get())
                .add(ModBlocks.PAINTED_BRICK_RED.get())
                .add(ModBlocks.PAINTED_BRICK_BLACK.get())
                .add(ModBlocks.SANDSTONE_CARVED.get())
                .add(ModBlocks.SANDSTONE_RED_CARVED.get())
                .add(ModBlocks.SANDSTONE_OCHRE_CARVED.get())
                .add(ModBlocks.SANDSTONE_DECORATED.get())
                .add(ModBlocks.BYZANTINE_STONE_ORNAMENT.get())
                .add(ModBlocks.BYZANTINE_SANDSTONE_ORNAMENT.get())
                .add(ModBlocks.BYZANTINE_TILES.get())
                .add(ModBlocks.BYZANTINE_STONE_TILES.get())
                .add(ModBlocks.BYZANTINE_SANDSTONE_TILES.get())
                .add(ModBlocks.GRAY_TILES.get())
                .add(ModBlocks.GREEN_TILES.get())
                .add(ModBlocks.RED_TILES.get())
                .add(ModBlocks.ICE_BRICK.get())
                .add(ModBlocks.ROSETTE.get())
                .add(ModBlocks.MILL_STATUE.get())
                .add(ModBlocks.ALCHEMIST_EXPLOSIVE.get())
                .add(ModBlocks.FIRE_PIT.get())
                .add(ModBlocks.LOCKED_CHEST.get())
                // Stairs
                .add(ModBlocks.STAIRS_COOKEDBRICK.get())
                .add(ModBlocks.STAIRS_SANDSTONE_CARVED.get())
                .add(ModBlocks.STAIRS_SANDSTONE_RED_CARVED.get())
                .add(ModBlocks.STAIRS_SANDSTONE_OCHRE_CARVED.get())
                .add(ModBlocks.STAIRS_BYZANTINE_TILES.get())
                .add(ModBlocks.STAIRS_GRAY_TILES.get())
                .add(ModBlocks.STAIRS_GREEN_TILES.get())
                .add(ModBlocks.STAIRS_RED_TILES.get())
                // Slabs
                .add(ModBlocks.SLAB_STONE_DECO.get())
                .add(ModBlocks.SLAB_SANDSTONE_CARVED.get())
                .add(ModBlocks.SLAB_SANDSTONE_RED_CARVED.get())
                .add(ModBlocks.SLAB_SANDSTONE_OCHRE_CARVED.get())
                .add(ModBlocks.SLAB_BYZANTINE_TILES.get())
                .add(ModBlocks.SLAB_GRAY_TILES.get())
                .add(ModBlocks.SLAB_GREEN_TILES.get())
                .add(ModBlocks.SLAB_RED_TILES.get())
                // Walls
                .add(ModBlocks.WALL_SANDSTONE_CARVED.get())
                .add(ModBlocks.WALL_SANDSTONE_RED_CARVED.get())
                .add(ModBlocks.WALL_SANDSTONE_OCHRE_CARVED.get());

        // Mineable with axe
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.TIMBER_FRAME_PLAIN.get())
                .add(ModBlocks.TIMBER_FRAME_CROSS.get())
                .add(ModBlocks.THATCH.get())
                .add(ModBlocks.PANEL.get())
                .add(ModBlocks.IMPORT_TABLE.get())
                .add(ModBlocks.BED_STRAW.get())
                .add(ModBlocks.BED_CHARPOY.get())
                .add(ModBlocks.STAIRS_TIMBERFRAME.get())
                .add(ModBlocks.STAIRS_THATCH.get())
                .add(ModBlocks.SLAB_WOOD_DECO.get());

        // Mineable with shovel
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.MUD_BRICK.get())
                .add(ModBlocks.MUD_BRICK_EXTENDED.get())
                .add(ModBlocks.WET_BRICK.get())
                .add(ModBlocks.SNAIL_SOIL.get())
                .add(ModBlocks.SOD.get())
                .add(ModBlocks.SNOW_BRICK.get())
                .add(ModBlocks.PATH_DIRT.get())
                .add(ModBlocks.PATH_GRAVEL.get())
                .add(ModBlocks.PATH_SNOW.get())
                .add(ModBlocks.STAIRS_MUDBRICK.get())
                .add(ModBlocks.WALL_MUD_BRICK.get())
                .add(ModBlocks.WALL_SNOW.get());

        // Walls tag
        tag(BlockTags.WALLS)
                .add(ModBlocks.WALL_MUD_BRICK.get())
                .add(ModBlocks.WALL_SANDSTONE_CARVED.get())
                .add(ModBlocks.WALL_SANDSTONE_RED_CARVED.get())
                .add(ModBlocks.WALL_SANDSTONE_OCHRE_CARVED.get())
                .add(ModBlocks.WALL_SNOW.get());

        // Stairs tag
        tag(BlockTags.STAIRS)
                .add(ModBlocks.STAIRS_TIMBERFRAME.get())
                .add(ModBlocks.STAIRS_MUDBRICK.get())
                .add(ModBlocks.STAIRS_COOKEDBRICK.get())
                .add(ModBlocks.STAIRS_THATCH.get())
                .add(ModBlocks.STAIRS_SANDSTONE_CARVED.get())
                .add(ModBlocks.STAIRS_SANDSTONE_RED_CARVED.get())
                .add(ModBlocks.STAIRS_SANDSTONE_OCHRE_CARVED.get())
                .add(ModBlocks.STAIRS_BYZANTINE_TILES.get())
                .add(ModBlocks.STAIRS_GRAY_TILES.get())
                .add(ModBlocks.STAIRS_GREEN_TILES.get())
                .add(ModBlocks.STAIRS_RED_TILES.get());

        // Slabs tag
        tag(BlockTags.SLABS)
                .add(ModBlocks.SLAB_WOOD_DECO.get())
                .add(ModBlocks.SLAB_STONE_DECO.get())
                .add(ModBlocks.SLAB_SANDSTONE_CARVED.get())
                .add(ModBlocks.SLAB_SANDSTONE_RED_CARVED.get())
                .add(ModBlocks.SLAB_SANDSTONE_OCHRE_CARVED.get())
                .add(ModBlocks.SLAB_BYZANTINE_TILES.get())
                .add(ModBlocks.SLAB_GRAY_TILES.get())
                .add(ModBlocks.SLAB_GREEN_TILES.get())
                .add(ModBlocks.SLAB_RED_TILES.get())
                .add(ModBlocks.SLAB_PATH_DIRT.get())
                .add(ModBlocks.SLAB_PATH_GRAVEL.get())
                .add(ModBlocks.SLAB_PATH_SLABS.get())
                .add(ModBlocks.SLAB_PATH_SANDSTONE.get())
                .add(ModBlocks.SLAB_PATH_GRAVEL_SLABS.get())
                .add(ModBlocks.SLAB_PATH_OCHRE_TILES.get())
                .add(ModBlocks.SLAB_PATH_SNOW.get());

        // Leaves tag
        tag(BlockTags.LEAVES)
                .add(ModBlocks.LEAVES_APPLE.get())
                .add(ModBlocks.LEAVES_OLIVE.get())
                .add(ModBlocks.LEAVES_PISTACHIO.get())
                .add(ModBlocks.LEAVES_CHERRY.get())
                .add(ModBlocks.LEAVES_SAKURA.get());

        // Saplings tag
        tag(BlockTags.SAPLINGS)
                .add(ModBlocks.SAPLING_APPLE.get())
                .add(ModBlocks.SAPLING_OLIVE.get())
                .add(ModBlocks.SAPLING_PISTACHIO.get())
                .add(ModBlocks.SAPLING_CHERRY.get())
                .add(ModBlocks.SAPLING_SAKURA.get());

        // Crops tag
        tag(BlockTags.CROPS)
                .add(ModBlocks.CROP_RICE.get())
                .add(ModBlocks.CROP_TURMERIC.get())
                .add(ModBlocks.CROP_MAIZE.get())
                .add(ModBlocks.CROP_COTTON.get())
                .add(ModBlocks.CROP_VINE.get());
    }
}

