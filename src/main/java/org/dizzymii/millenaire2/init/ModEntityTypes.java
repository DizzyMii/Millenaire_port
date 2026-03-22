package org.dizzymii.millenaire2.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.entity.EntityTargetedBlaze;
import org.dizzymii.millenaire2.entity.EntityTargetedGhast;
import org.dizzymii.millenaire2.entity.EntityTargetedWitherSkeleton;
import org.dizzymii.millenaire2.entity.EntityWallDecoration;
import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Registry for all Millénaire entity types.
 * Split from MillEntities as part of 0.2 package restructure.
 */
public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Millenaire2.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<MillVillager>> MILL_VILLAGER =
            ENTITY_TYPES.register("mill_villager",
                    () -> EntityType.Builder.of(MillVillager::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.95F).clientTrackingRange(8).updateInterval(3)
                            .build("mill_villager"));

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

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }

    public static void init() {
        // Class loading triggers all static final fields above
    }
}
