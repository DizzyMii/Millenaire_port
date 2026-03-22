package org.dizzymii.millenaire2.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.block.entity.MillFirePitBlockEntity;
import org.dizzymii.millenaire2.block.entity.MillImportTableBlockEntity;
import org.dizzymii.millenaire2.block.entity.MillLockedChestBlockEntity;
import org.dizzymii.millenaire2.block.entity.MillPanelBlockEntity;

/**
 * Registry for all Millénaire block entity types.
 * Split from MillEntities as part of 0.2 package restructure.
 */
public class ModBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Millenaire2.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillFirePitBlockEntity>> FIRE_PIT_BE =
            BLOCK_ENTITY_TYPES.register("fire_pit",
                    () -> BlockEntityType.Builder.of(
                            MillFirePitBlockEntity::new,
                            ModBlocks.FIRE_PIT.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillLockedChestBlockEntity>> LOCKED_CHEST_BE =
            BLOCK_ENTITY_TYPES.register("locked_chest",
                    () -> BlockEntityType.Builder.of(
                            MillLockedChestBlockEntity::new,
                            ModBlocks.LOCKED_CHEST.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillPanelBlockEntity>> PANEL_BE =
            BLOCK_ENTITY_TYPES.register("panel",
                    () -> BlockEntityType.Builder.of(
                            MillPanelBlockEntity::new,
                            ModBlocks.PANEL.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillImportTableBlockEntity>> IMPORT_TABLE_BE =
            BLOCK_ENTITY_TYPES.register("import_table",
                    () -> BlockEntityType.Builder.of(
                            MillImportTableBlockEntity::new,
                            ModBlocks.IMPORT_TABLE.get()
                    ).build(null));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }

    public static void init() {
        // Class loading triggers all static final fields above
    }
}
