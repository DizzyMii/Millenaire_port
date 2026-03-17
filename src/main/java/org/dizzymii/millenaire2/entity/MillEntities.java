package org.dizzymii.millenaire2.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.block.MillBlocks;
import org.dizzymii.millenaire2.entity.blockentity.MillFirePitBlockEntity;
import org.dizzymii.millenaire2.entity.blockentity.MillImportTableBlockEntity;
import org.dizzymii.millenaire2.entity.blockentity.MillLockedChestBlockEntity;
import org.dizzymii.millenaire2.entity.blockentity.MillPanelBlockEntity;

/**
 * Central registration for entity types and block entity types.
 * Entity types for MillVillager subclasses will be registered when concrete
 * villager classes are created in a later phase.
 */
public class MillEntities {

    // ========== Block Entity Types ==========

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillFirePitBlockEntity>> FIRE_PIT_BE =
            Millenaire2.BLOCK_ENTITY_TYPES.register("fire_pit",
                    () -> BlockEntityType.Builder.of(
                            MillFirePitBlockEntity::new,
                            MillBlocks.FIRE_PIT.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillLockedChestBlockEntity>> LOCKED_CHEST_BE =
            Millenaire2.BLOCK_ENTITY_TYPES.register("locked_chest",
                    () -> BlockEntityType.Builder.of(
                            MillLockedChestBlockEntity::new,
                            MillBlocks.LOCKED_CHEST.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillPanelBlockEntity>> PANEL_BE =
            Millenaire2.BLOCK_ENTITY_TYPES.register("panel",
                    () -> BlockEntityType.Builder.of(
                            MillPanelBlockEntity::new,
                            MillBlocks.PANEL.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillImportTableBlockEntity>> IMPORT_TABLE_BE =
            Millenaire2.BLOCK_ENTITY_TYPES.register("import_table",
                    () -> BlockEntityType.Builder.of(
                            MillImportTableBlockEntity::new,
                            MillBlocks.IMPORT_TABLE.get()
                    ).build(null));

    // TODO: Entity types for MillVillager subclasses (GenericVillager, etc.) in a later phase
    // TODO: EntityWallDecoration, EntityTargetedBlaze, etc.

    /**
     * Called from Millenaire2 constructor to force class loading.
     */
    public static void init() {
        // Class loading triggers all static final fields above
    }
}
