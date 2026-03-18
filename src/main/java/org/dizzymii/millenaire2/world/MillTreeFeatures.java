package org.dizzymii.millenaire2.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.dizzymii.millenaire2.Millenaire2;

/**
 * Resource keys for Millénaire custom tree configured features.
 * The actual feature configurations are defined in JSON data files under
 * data/millenaire2/worldgen/configured_feature/.
 */
public class MillTreeFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> APPLE_TREE =
            createKey("apple_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OLIVE_TREE =
            createKey("olive_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PISTACHIO_TREE =
            createKey("pistachio_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHERRY_MILL_TREE =
            createKey("cherry_mill_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SAKURA_TREE =
            createKey("sakura_tree");

    private static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, name));
    }
}
