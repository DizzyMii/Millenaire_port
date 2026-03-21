package org.dizzymii.millenaire2.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.dizzymii.millenaire2.block.MillBlocks;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MillLootTableProvider extends LootTableProvider {

    public MillLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(MillBlockLoot::new, net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.BLOCK)
        ), registries);
    }

    public static class MillBlockLoot extends BlockLootSubProvider {

        protected MillBlockLoot(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate() {
            // Simple blocks drop themselves
            dropSelf(MillBlocks.STONE_DECORATION.get());
            dropSelf(MillBlocks.COOKED_BRICK.get());
            dropSelf(MillBlocks.PAINTED_BRICK_WHITE.get());
            dropSelf(MillBlocks.PAINTED_BRICK_ORANGE.get());
            dropSelf(MillBlocks.PAINTED_BRICK_MAGENTA.get());
            dropSelf(MillBlocks.PAINTED_BRICK_LIGHT_BLUE.get());
            dropSelf(MillBlocks.PAINTED_BRICK_YELLOW.get());
            dropSelf(MillBlocks.PAINTED_BRICK_LIME.get());
            dropSelf(MillBlocks.PAINTED_BRICK_PINK.get());
            dropSelf(MillBlocks.PAINTED_BRICK_GRAY.get());
            dropSelf(MillBlocks.PAINTED_BRICK_LIGHT_GRAY.get());
            dropSelf(MillBlocks.PAINTED_BRICK_CYAN.get());
            dropSelf(MillBlocks.PAINTED_BRICK_PURPLE.get());
            dropSelf(MillBlocks.PAINTED_BRICK_BLUE.get());
            dropSelf(MillBlocks.PAINTED_BRICK_BROWN.get());
            dropSelf(MillBlocks.PAINTED_BRICK_GREEN.get());
            dropSelf(MillBlocks.PAINTED_BRICK_RED.get());
            dropSelf(MillBlocks.PAINTED_BRICK_BLACK.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_WHITE.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_ORANGE.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_MAGENTA.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_LIGHT_BLUE.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_YELLOW.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_LIME.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_PINK.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_GRAY.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_LIGHT_GRAY.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_CYAN.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_PURPLE.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_BLUE.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_BROWN.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_GREEN.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_RED.get());
            dropSelf(MillBlocks.PAINTED_BRICK_DECO_BLACK.get());
            dropSelf(MillBlocks.TIMBER_FRAME_PLAIN.get());
            dropSelf(MillBlocks.TIMBER_FRAME_CROSS.get());
            dropSelf(MillBlocks.THATCH.get());
            dropSelf(MillBlocks.MUD_BRICK.get());
            dropSelf(MillBlocks.MUD_BRICK_EXTENDED.get());
            dropSelf(MillBlocks.SANDSTONE_CARVED.get());
            dropSelf(MillBlocks.SANDSTONE_RED_CARVED.get());
            dropSelf(MillBlocks.SANDSTONE_OCHRE_CARVED.get());
            dropSelf(MillBlocks.SANDSTONE_DECORATED.get());
            dropSelf(MillBlocks.BYZANTINE_STONE_ORNAMENT.get());
            dropSelf(MillBlocks.BYZANTINE_SANDSTONE_ORNAMENT.get());
            dropSelf(MillBlocks.BYZANTINE_TILES.get());
            dropSelf(MillBlocks.BYZANTINE_STONE_TILES.get());
            dropSelf(MillBlocks.BYZANTINE_SANDSTONE_TILES.get());
            dropSelf(MillBlocks.GRAY_TILES.get());
            dropSelf(MillBlocks.GREEN_TILES.get());
            dropSelf(MillBlocks.RED_TILES.get());
            dropSelf(MillBlocks.WET_BRICK.get());
            dropSelf(MillBlocks.SILK_WORM_BLOCK.get());
            dropSelf(MillBlocks.SNAIL_SOIL.get());
            dropSelf(MillBlocks.SOD.get());
            dropSelf(MillBlocks.ALCHEMIST_EXPLOSIVE.get());
            dropSelf(MillBlocks.ROSETTE.get());
            dropSelf(MillBlocks.STAINED_GLASS.get());
            dropSelf(MillBlocks.MILL_STATUE.get());
            dropSelf(MillBlocks.ICE_BRICK.get());
            dropSelf(MillBlocks.SNOW_BRICK.get());
            dropSelf(MillBlocks.PATH_DIRT.get());
            dropSelf(MillBlocks.PATH_GRAVEL.get());
            dropSelf(MillBlocks.PATH_SLABS.get());
            dropSelf(MillBlocks.PATH_SANDSTONE.get());
            dropSelf(MillBlocks.PATH_GRAVEL_SLABS.get());
            dropSelf(MillBlocks.PATH_OCHRE_TILES.get());
            dropSelf(MillBlocks.PATH_SNOW.get());
            dropSelf(MillBlocks.LOCKED_CHEST.get());
            dropSelf(MillBlocks.FIRE_PIT.get());
            dropSelf(MillBlocks.PANEL.get());
            dropSelf(MillBlocks.IMPORT_TABLE.get());
            dropSelf(MillBlocks.BED_STRAW.get());
            dropSelf(MillBlocks.BED_CHARPOY.get());

            // Stairs
            dropSelf(MillBlocks.STAIRS_TIMBERFRAME.get());
            dropSelf(MillBlocks.STAIRS_MUDBRICK.get());
            dropSelf(MillBlocks.STAIRS_COOKEDBRICK.get());
            dropSelf(MillBlocks.STAIRS_THATCH.get());
            dropSelf(MillBlocks.STAIRS_SANDSTONE_CARVED.get());
            dropSelf(MillBlocks.STAIRS_SANDSTONE_RED_CARVED.get());
            dropSelf(MillBlocks.STAIRS_SANDSTONE_OCHRE_CARVED.get());
            dropSelf(MillBlocks.STAIRS_BYZANTINE_TILES.get());
            dropSelf(MillBlocks.STAIRS_GRAY_TILES.get());
            dropSelf(MillBlocks.STAIRS_GREEN_TILES.get());
            dropSelf(MillBlocks.STAIRS_RED_TILES.get());

            // Slabs (drop 2 when double)
            add(MillBlocks.SLAB_WOOD_DECO.get(), createSlabItemTable(MillBlocks.SLAB_WOOD_DECO.get()));
            add(MillBlocks.SLAB_STONE_DECO.get(), createSlabItemTable(MillBlocks.SLAB_STONE_DECO.get()));
            add(MillBlocks.SLAB_SANDSTONE_CARVED.get(), createSlabItemTable(MillBlocks.SLAB_SANDSTONE_CARVED.get()));
            add(MillBlocks.SLAB_SANDSTONE_RED_CARVED.get(), createSlabItemTable(MillBlocks.SLAB_SANDSTONE_RED_CARVED.get()));
            add(MillBlocks.SLAB_SANDSTONE_OCHRE_CARVED.get(), createSlabItemTable(MillBlocks.SLAB_SANDSTONE_OCHRE_CARVED.get()));
            add(MillBlocks.SLAB_BYZANTINE_TILES.get(), createSlabItemTable(MillBlocks.SLAB_BYZANTINE_TILES.get()));
            add(MillBlocks.SLAB_GRAY_TILES.get(), createSlabItemTable(MillBlocks.SLAB_GRAY_TILES.get()));
            add(MillBlocks.SLAB_GREEN_TILES.get(), createSlabItemTable(MillBlocks.SLAB_GREEN_TILES.get()));
            add(MillBlocks.SLAB_RED_TILES.get(), createSlabItemTable(MillBlocks.SLAB_RED_TILES.get()));
            add(MillBlocks.SLAB_PATH_DIRT.get(), createSlabItemTable(MillBlocks.SLAB_PATH_DIRT.get()));
            add(MillBlocks.SLAB_PATH_GRAVEL.get(), createSlabItemTable(MillBlocks.SLAB_PATH_GRAVEL.get()));
            add(MillBlocks.SLAB_PATH_SLABS.get(), createSlabItemTable(MillBlocks.SLAB_PATH_SLABS.get()));
            add(MillBlocks.SLAB_PATH_SANDSTONE.get(), createSlabItemTable(MillBlocks.SLAB_PATH_SANDSTONE.get()));
            add(MillBlocks.SLAB_PATH_GRAVEL_SLABS.get(), createSlabItemTable(MillBlocks.SLAB_PATH_GRAVEL_SLABS.get()));
            add(MillBlocks.SLAB_PATH_OCHRE_TILES.get(), createSlabItemTable(MillBlocks.SLAB_PATH_OCHRE_TILES.get()));
            add(MillBlocks.SLAB_PATH_SNOW.get(), createSlabItemTable(MillBlocks.SLAB_PATH_SNOW.get()));

            // Walls
            dropSelf(MillBlocks.WALL_MUD_BRICK.get());
            dropSelf(MillBlocks.WALL_SANDSTONE_CARVED.get());
            dropSelf(MillBlocks.WALL_SANDSTONE_RED_CARVED.get());
            dropSelf(MillBlocks.WALL_SANDSTONE_OCHRE_CARVED.get());
            dropSelf(MillBlocks.WALL_SNOW.get());

            // Panes / Bars
            dropSelf(MillBlocks.PAPER_WALL.get());
            dropSelf(MillBlocks.WOODEN_BARS.get());
            dropSelf(MillBlocks.WOODEN_BARS_INDIAN.get());
            dropSelf(MillBlocks.WOODEN_BARS_ROSETTE.get());
            dropSelf(MillBlocks.WOODEN_BARS_DARK.get());

            // Saplings
            dropSelf(MillBlocks.SAPLING_APPLE.get());
            dropSelf(MillBlocks.SAPLING_OLIVE.get());
            dropSelf(MillBlocks.SAPLING_PISTACHIO.get());
            dropSelf(MillBlocks.SAPLING_CHERRY.get());
            dropSelf(MillBlocks.SAPLING_SAKURA.get());

            // Leaves (drop saplings with silk touch drops self)
            dropSelf(MillBlocks.LEAVES_APPLE.get());
            dropSelf(MillBlocks.LEAVES_OLIVE.get());
            dropSelf(MillBlocks.LEAVES_PISTACHIO.get());
            dropSelf(MillBlocks.LEAVES_CHERRY.get());
            dropSelf(MillBlocks.LEAVES_SAKURA.get());
            dropSelf(MillBlocks.FRUIT_LEAVES.get());

            // Mock blocks use noLootTable() — do NOT add loot entries for them

            // Crops (drop nothing for now, seeds handled by village trade)
            dropSelf(MillBlocks.CROP_RICE.get());
            dropSelf(MillBlocks.CROP_TURMERIC.get());
            dropSelf(MillBlocks.CROP_MAIZE.get());
            dropSelf(MillBlocks.CROP_COTTON.get());
            dropSelf(MillBlocks.CROP_VINE.get());
        }

        private static final Set<Block> NO_LOOT_BLOCKS = Set.of(
                MillBlocks.MARKER_BLOCK.get(), MillBlocks.MAIN_CHEST.get(),
                MillBlocks.ANIMAL_SPAWN.get(), MillBlocks.SOURCE.get(),
                MillBlocks.FREE_BLOCK.get(), MillBlocks.TREE_SPAWN.get(),
                MillBlocks.SOIL_BLOCK.get(), MillBlocks.DECOR_BLOCK.get()
        );

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return MillBlocks.BLOCKS.getEntries().stream()
                    .map(DeferredHolder::get)
                    .map(b -> (Block) b)
                    .filter(b -> !NO_LOOT_BLOCKS.contains(b))
                    .toList();
        }
    }
}
