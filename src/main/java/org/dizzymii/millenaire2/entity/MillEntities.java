package org.dizzymii.millenaire2.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.dizzymii.millenaire2.block.entity.MillFirePitBlockEntity;
import org.dizzymii.millenaire2.block.entity.MillImportTableBlockEntity;
import org.dizzymii.millenaire2.block.entity.MillLockedChestBlockEntity;
import org.dizzymii.millenaire2.block.entity.MillPanelBlockEntity;
import org.dizzymii.millenaire2.init.ModBlockEntityTypes;
import org.dizzymii.millenaire2.init.ModEntityTypes;

/**
 * @deprecated Use {@link ModEntityTypes} and {@link ModBlockEntityTypes} directly.
 *             Retained as a delegation shim for callers not yet migrated to the init/ split.
 */
@Deprecated
public final class MillEntities {

    // ========== Entity Types (delegate to ModEntityTypes) ==========

    public static final DeferredHolder<EntityType<?>, EntityType<MillVillager>> MILL_VILLAGER = ModEntityTypes.MILL_VILLAGER;
    public static final DeferredHolder<EntityType<?>, EntityType<EntityWallDecoration>> WALL_DECORATION = ModEntityTypes.WALL_DECORATION;
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTargetedBlaze>> TARGETED_BLAZE = ModEntityTypes.TARGETED_BLAZE;
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTargetedWitherSkeleton>> TARGETED_WITHER_SKELETON = ModEntityTypes.TARGETED_WITHER_SKELETON;
    public static final DeferredHolder<EntityType<?>, EntityType<EntityTargetedGhast>> TARGETED_GHAST = ModEntityTypes.TARGETED_GHAST;

    // ========== Block Entity Types (delegate to ModBlockEntityTypes) ==========

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillFirePitBlockEntity>> FIRE_PIT_BE = ModBlockEntityTypes.FIRE_PIT_BE;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillLockedChestBlockEntity>> LOCKED_CHEST_BE = ModBlockEntityTypes.LOCKED_CHEST_BE;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillPanelBlockEntity>> PANEL_BE = ModBlockEntityTypes.PANEL_BE;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillImportTableBlockEntity>> IMPORT_TABLE_BE = ModBlockEntityTypes.IMPORT_TABLE_BE;

    private MillEntities() {}
}

