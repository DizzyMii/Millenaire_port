package org.dizzymii.millenaire2.client.render;

import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.MillVillager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves the skin texture for a villager based on culture/villager type data.
 * Caches resolved ResourceLocations per villager ID to avoid repeated parsing.
 */
public final class VillagerTextureHelper {

    private static final Map<Long, ResourceLocation> TEXTURE_CACHE = new HashMap<>();

    private VillagerTextureHelper() {}

    /**
     * Resolve the texture for a villager entity. Uses VillagerType.textures list
     * if available, otherwise falls back to the default texture.
     */
    public static ResourceLocation resolveTexture(MillVillager villager, ResourceLocation defaultTexture) {
        long vid = villager.getVillagerId();
        if (vid > 0) {
            ResourceLocation cached = TEXTURE_CACHE.get(vid);
            if (cached != null) return cached;
        }

        VillagerType vtype = villager.vtype;
        if (vtype != null && !vtype.textures.isEmpty()) {
            List<String> textures = vtype.textures;
            // Deterministic pick based on villager ID for consistency
            int index = vid > 0 ? (int) (Math.abs(vid) % textures.size()) : 0;
            String texPath = textures.get(index);
            ResourceLocation rl = parseTextureLocation(texPath);
            if (rl != null) {
                if (vid > 0) TEXTURE_CACHE.put(vid, rl);
                return rl;
            }
        }

        return defaultTexture;
    }

    /**
     * Parse a texture path string from culture data into a ResourceLocation.
     * Accepts formats like:
     * - "millenaire2:textures/entity/norman/farmer.png"
     * - "textures/entity/norman/farmer.png" (assumes millenaire2 namespace)
     * - "norman/farmer" (assumes millenaire2:textures/entity/ prefix and .png suffix)
     */
    private static ResourceLocation parseTextureLocation(String texPath) {
        if (texPath == null || texPath.isEmpty()) return null;

        if (texPath.contains(":")) {
            // Already has namespace
            return ResourceLocation.tryParse(texPath);
        }

        if (texPath.startsWith("textures/")) {
            return ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, texPath);
        }

        // Short form: assume textures/entity/ prefix
        String fullPath = "textures/entity/" + texPath;
        if (!fullPath.endsWith(".png")) {
            fullPath += ".png";
        }
        return ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, fullPath);
    }

    /**
     * Clear the texture cache (e.g. on world unload or resource reload).
     */
    public static void clearCache() {
        TEXTURE_CACHE.clear();
    }
}
