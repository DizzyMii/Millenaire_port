package org.dizzymii.millenaire2.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.block.MillBlocks;
import org.dizzymii.millenaire2.block.entity.MillFirePitBlockEntity;
import org.dizzymii.millenaire2.block.entity.MillImportTableBlockEntity;
import org.dizzymii.millenaire2.block.entity.MillLockedChestBlockEntity;
import org.dizzymii.millenaire2.block.entity.MillPanelBlockEntity;

/**
 * Central registration for entity types and block entity types.
 */
public class MillEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Millenaire2.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Millenaire2.MODID);

    // ========== Entity Types ==========

    public static final DeferredHolder<EntityType<?>, EntityType<MillVillager.GenericMale>> GENERIC_MALE =
            ENTITY_TYPES.register("generic_villager",
                    () -> EntityType.Builder.of(MillVillager.GenericMale::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.95F).clientTrackingRange(8).updateInterval(3)
                            .build("generic_villager"));

    public static final DeferredHolder<EntityType<?>, EntityType<MillVillager.GenericSymmFemale>> GENERIC_SYMM_FEMALE =
            ENTITY_TYPES.register("generic_symm_female",
                    () -> EntityType.Builder.of(MillVillager.GenericSymmFemale::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.95F).clientTrackingRange(8).updateInterval(3)
                            .build("generic_symm_female"));

    public static final DeferredHolder<EntityType<?>, EntityType<MillVillager.GenericAsymmFemale>> GENERIC_ASYMM_FEMALE =
            ENTITY_TYPES.register("generic_asymm_female",
                    () -> EntityType.Builder.of(MillVillager.GenericAsymmFemale::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.95F).clientTrackingRange(8).updateInterval(3)
                            .build("generic_asymm_female"));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityWallDecoration>> WALL_DECORATION =
            ENTITY_TYPES.register("wall_decoration",
                    () -> EntityType.Builder.of(EntityWallDecoration::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE)
                            .build("wall_decoration"));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityTargetedBlaze>> TARGETED_BLAZE =
            ENTITY_TYPES.register("targeted_blaze",
                    () -> EntityType.Builder.of(EntityTargetedBlaze::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.8F).clientTrackingRange(8)
                            .build("targeted_blaze"));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityTargetedWitherSkeleton>> TARGETED_WITHER_SKELETON =
            ENTITY_TYPES.register("targeted_wither_skeleton",
                    () -> EntityType.Builder.of(EntityTargetedWitherSkeleton::new, MobCategory.MONSTER)
                            .sized(0.7F, 2.4F).clientTrackingRange(8)
                            .build("targeted_wither_skeleton"));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityTargetedGhast>> TARGETED_GHAST =
            ENTITY_TYPES.register("targeted_ghast",
                    () -> EntityType.Builder.of(EntityTargetedGhast::new, MobCategory.MONSTER)
                            .sized(4.0F, 4.0F).clientTrackingRange(10)
                            .build("targeted_ghast"));

    // ========== Block Entity Types ==========

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillFirePitBlockEntity>> FIRE_PIT_BE =
            BLOCK_ENTITY_TYPES.register("fire_pit",
                    () -> BlockEntityType.Builder.of(
                            MillFirePitBlockEntity::new,
                            MillBlocks.FIRE_PIT.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillLockedChestBlockEntity>> LOCKED_CHEST_BE =
            BLOCK_ENTITY_TYPES.register("locked_chest",
                    () -> BlockEntityType.Builder.of(
                            MillLockedChestBlockEntity::new,
                            MillBlocks.LOCKED_CHEST.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillPanelBlockEntity>> PANEL_BE =
            BLOCK_ENTITY_TYPES.register("panel",
                    () -> BlockEntityType.Builder.of(
                            MillPanelBlockEntity::new,
                            MillBlocks.PANEL.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MillImportTableBlockEntity>> IMPORT_TABLE_BE =
            BLOCK_ENTITY_TYPES.register("import_table",
                    () -> BlockEntityType.Builder.of(
                            MillImportTableBlockEntity::new,
                            MillBlocks.IMPORT_TABLE.get()
                    ).build(null));

    /**
     * Called from Millenaire2 constructor to force class loading.
     */
    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
    }

    public static void init() {
        // Class loading triggers all static final fields above
    }
}
