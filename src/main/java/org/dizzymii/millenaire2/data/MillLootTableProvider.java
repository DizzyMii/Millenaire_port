package org.dizzymii.millenaire2.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.dizzymii.millenaire2.init.ModBlocks;

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
            dropSelf(ModBlocks.STONE_DECORATION.get());
            dropSelf(ModBlocks.COOKED_BRICK.get());
            dropSelf(ModBlocks.PAINTED_BRICK_WHITE.get());
            dropSelf(ModBlocks.PAINTED_BRICK_ORANGE.get());
            dropSelf(ModBlocks.PAINTED_BRICK_MAGENTA.get());
            dropSelf(ModBlocks.PAINTED_BRICK_LIGHT_BLUE.get());
            dropSelf(ModBlocks.PAINTED_BRICK_YELLOW.get());
            dropSelf(ModBlocks.PAINTED_BRICK_LIME.get());
            dropSelf(ModBlocks.PAINTED_BRICK_PINK.get());
            dropSelf(ModBlocks.PAINTED_BRICK_GRAY.get());
            dropSelf(ModBlocks.PAINTED_BRICK_LIGHT_GRAY.get());
            dropSelf(ModBlocks.PAINTED_BRICK_CYAN.get());
            dropSelf(ModBlocks.PAINTED_BRICK_PURPLE.get());
            dropSelf(ModBlocks.PAINTED_BRICK_BLUE.get());
            dropSelf(ModBlocks.PAINTED_BRICK_BROWN.get());
            dropSelf(ModBlocks.PAINTED_BRICK_GREEN.get());
            dropSelf(ModBlocks.PAINTED_BRICK_RED.get());
            dropSelf(ModBlocks.PAINTED_BRICK_BLACK.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_WHITE.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_ORANGE.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_MAGENTA.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_LIGHT_BLUE.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_YELLOW.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_LIME.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_PINK.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_GRAY.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_LIGHT_GRAY.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_CYAN.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_PURPLE.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_BLUE.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_BROWN.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_GREEN.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_RED.get());
            dropSelf(ModBlocks.PAINTED_BRICK_DECO_BLACK.get());
            dropSelf(ModBlocks.TIMBER_FRAME_PLAIN.get());
            dropSelf(ModBlocks.TIMBER_FRAME_CROSS.get());
            dropSelf(ModBlocks.THATCH.get());
            dropSelf(ModBlocks.MUD_BRICK.get());
            dropSelf(ModBlocks.MUD_BRICK_EXTENDED.get());
            dropSelf(ModBlocks.SANDSTONE_CARVED.get());
            dropSelf(ModBlocks.SANDSTONE_RED_CARVED.get());
            dropSelf(ModBlocks.SANDSTONE_OCHRE_CARVED.get());
            dropSelf(ModBlocks.SANDSTONE_DECORATED.get());
            dropSelf(ModBlocks.BYZANTINE_STONE_ORNAMENT.get());
            dropSelf(ModBlocks.BYZANTINE_SANDSTONE_ORNAMENT.get());
            dropSelf(ModBlocks.BYZANTINE_TILES.get());
            dropSelf(ModBlocks.BYZANTINE_STONE_TILES.get());
            dropSelf(ModBlocks.BYZANTINE_SANDSTONE_TILES.get());
            dropSelf(ModBlocks.GRAY_TILES.get());
            dropSelf(ModBlocks.GREEN_TILES.get());
            dropSelf(ModBlocks.RED_TILES.get());
            dropSelf(ModBlocks.WET_BRICK.get());
            dropSelf(ModBlocks.SILK_WORM_BLOCK.get());
            dropSelf(ModBlocks.SNAIL_SOIL.get());
            dropSelf(ModBlocks.SOD.get());
            dropSelf(ModBlocks.ALCHEMIST_EXPLOSIVE.get());
            dropSelf(ModBlocks.ROSETTE.get());
            dropSelf(ModBlocks.STAINED_GLASS.get());
            dropSelf(ModBlocks.MILL_STATUE.get());
            dropSelf(ModBlocks.ICE_BRICK.get());
            dropSelf(ModBlocks.SNOW_BRICK.get());
            dropSelf(ModBlocks.PATH_DIRT.get());
            dropSelf(ModBlocks.PATH_GRAVEL.get());
            dropSelf(ModBlocks.PATH_SLABS.get());
            dropSelf(ModBlocks.PATH_SANDSTONE.get());
            dropSelf(ModBlocks.PATH_GRAVEL_SLABS.get());
            dropSelf(ModBlocks.PATH_OCHRE_TILES.get());
            dropSelf(ModBlocks.PATH_SNOW.get());
            dropSelf(ModBlocks.LOCKED_CHEST.get());
            dropSelf(ModBlocks.FIRE_PIT.get());
            dropSelf(ModBlocks.PANEL.get());
            dropSelf(ModBlocks.IMPORT_TABLE.get());
            dropSelf(ModBlocks.BED_STRAW.get());
            dropSelf(ModBlocks.BED_CHARPOY.get());

            // Stairs
            dropSelf(ModBlocks.STAIRS_TIMBERFRAME.get());
            dropSelf(ModBlocks.STAIRS_MUDBRICK.get());
            dropSelf(ModBlocks.STAIRS_COOKEDBRICK.get());
            dropSelf(ModBlocks.STAIRS_THATCH.get());
            dropSelf(ModBlocks.STAIRS_SANDSTONE_CARVED.get());
            dropSelf(ModBlocks.STAIRS_SANDSTONE_RED_CARVED.get());
            dropSelf(ModBlocks.STAIRS_SANDSTONE_OCHRE_CARVED.get());
            dropSelf(ModBlocks.STAIRS_BYZANTINE_TILES.get());
            dropSelf(ModBlocks.STAIRS_GRAY_TILES.get());
            dropSelf(ModBlocks.STAIRS_GREEN_TILES.get());
            dropSelf(ModBlocks.STAIRS_RED_TILES.get());

            // Slabs (drop 2 when double)
            add(ModBlocks.SLAB_WOOD_DECO.get(), createSlabItemTable(ModBlocks.SLAB_WOOD_DECO.get()));
            add(ModBlocks.SLAB_STONE_DECO.get(), createSlabItemTable(ModBlocks.SLAB_STONE_DECO.get()));
            add(ModBlocks.SLAB_SANDSTONE_CARVED.get(), createSlabItemTable(ModBlocks.SLAB_SANDSTONE_CARVED.get()));
            add(ModBlocks.SLAB_SANDSTONE_RED_CARVED.get(), createSlabItemTable(ModBlocks.SLAB_SANDSTONE_RED_CARVED.get()));
            add(ModBlocks.SLAB_SANDSTONE_OCHRE_CARVED.get(), createSlabItemTable(ModBlocks.SLAB_SANDSTONE_OCHRE_CARVED.get()));
            add(ModBlocks.SLAB_BYZANTINE_TILES.get(), createSlabItemTable(ModBlocks.SLAB_BYZANTINE_TILES.get()));
            add(ModBlocks.SLAB_GRAY_TILES.get(), createSlabItemTable(ModBlocks.SLAB_GRAY_TILES.get()));
            add(ModBlocks.SLAB_GREEN_TILES.get(), createSlabItemTable(ModBlocks.SLAB_GREEN_TILES.get()));
            add(ModBlocks.SLAB_RED_TILES.get(), createSlabItemTable(ModBlocks.SLAB_RED_TILES.get()));
            add(ModBlocks.SLAB_PATH_DIRT.get(), createSlabItemTable(ModBlocks.SLAB_PATH_DIRT.get()));
            add(ModBlocks.SLAB_PATH_GRAVEL.get(), createSlabItemTable(ModBlocks.SLAB_PATH_GRAVEL.get()));
            add(ModBlocks.SLAB_PATH_SLABS.get(), createSlabItemTable(ModBlocks.SLAB_PATH_SLABS.get()));
            add(ModBlocks.SLAB_PATH_SANDSTONE.get(), createSlabItemTable(ModBlocks.SLAB_PATH_SANDSTONE.get()));
            add(ModBlocks.SLAB_PATH_GRAVEL_SLABS.get(), createSlabItemTable(ModBlocks.SLAB_PATH_GRAVEL_SLABS.get()));
            add(ModBlocks.SLAB_PATH_OCHRE_TILES.get(), createSlabItemTable(ModBlocks.SLAB_PATH_OCHRE_TILES.get()));
            add(ModBlocks.SLAB_PATH_SNOW.get(), createSlabItemTable(ModBlocks.SLAB_PATH_SNOW.get()));

            // Walls
            dropSelf(ModBlocks.WALL_MUD_BRICK.get());
            dropSelf(ModBlocks.WALL_SANDSTONE_CARVED.get());
            dropSelf(ModBlocks.WALL_SANDSTONE_RED_CARVED.get());
            dropSelf(ModBlocks.WALL_SANDSTONE_OCHRE_CARVED.get());
            dropSelf(ModBlocks.WALL_SNOW.get());

            // Panes / Bars
            dropSelf(ModBlocks.PAPER_WALL.get());
            dropSelf(ModBlocks.WOODEN_BARS.get());
            dropSelf(ModBlocks.WOODEN_BARS_INDIAN.get());
            dropSelf(ModBlocks.WOODEN_BARS_ROSETTE.get());
            dropSelf(ModBlocks.WOODEN_BARS_DARK.get());

            // Saplings
            dropSelf(ModBlocks.SAPLING_APPLE.get());
            dropSelf(ModBlocks.SAPLING_OLIVE.get());
            dropSelf(ModBlocks.SAPLING_PISTACHIO.get());
            dropSelf(ModBlocks.SAPLING_CHERRY.get());
            dropSelf(ModBlocks.SAPLING_SAKURA.get());

            // Leaves (drop saplings with silk touch drops self)
            dropSelf(ModBlocks.LEAVES_APPLE.get());
            dropSelf(ModBlocks.LEAVES_OLIVE.get());
            dropSelf(ModBlocks.LEAVES_PISTACHIO.get());
            dropSelf(ModBlocks.LEAVES_CHERRY.get());
            dropSelf(ModBlocks.LEAVES_SAKURA.get());
            dropSelf(ModBlocks.FRUIT_LEAVES.get());

            // Mock blocks use noLootTable() — do NOT add loot entries for them

            // Crops (drop nothing for now, seeds handled by village trade)
            dropSelf(ModBlocks.CROP_RICE.get());
            dropSelf(ModBlocks.CROP_TURMERIC.get());
            dropSelf(ModBlocks.CROP_MAIZE.get());
            dropSelf(ModBlocks.CROP_COTTON.get());
            dropSelf(ModBlocks.CROP_VINE.get());
        }

        private static final Set<Block> NO_LOOT_BLOCKS = Set.of(
                ModBlocks.MARKER_BLOCK.get(), ModBlocks.MAIN_CHEST.get(),
                ModBlocks.ANIMAL_SPAWN.get(), ModBlocks.SOURCE.get(),
                ModBlocks.FREE_BLOCK.get(), ModBlocks.TREE_SPAWN.get(),
                ModBlocks.SOIL_BLOCK.get(), ModBlocks.DECOR_BLOCK.get()
        );

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModBlocks.BLOCKS.getEntries().stream()
                    .map(DeferredHolder::get)
                    .map(b -> (Block) b)
                    .filter(b -> !NO_LOOT_BLOCKS.contains(b))
                    .toList();
        }
    }
}

