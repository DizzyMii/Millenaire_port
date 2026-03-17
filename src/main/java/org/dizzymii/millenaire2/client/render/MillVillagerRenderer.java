package org.dizzymii.millenaire2.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Renderer for MillVillager (male).
 * Ported from org.millenaire.client.render.RenderMillVillager (Forge 1.12.2).
 *
 * In NeoForge 1.21.1, renderers use EntityRendererProvider and HumanoidMobRenderer.
 * Full rendering features (name plates, path debug, quest icons) will be added later.
 */
public class MillVillagerRenderer extends HumanoidMobRenderer<MillVillager, MillVillagerModel> {

    private static final ResourceLocation DEFAULT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "textures/entity/villager_default.png");

    public MillVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new MillVillagerModel(context.bakeLayer(MillVillagerModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MillVillager entity) {
        // TODO: Return culture-specific texture from VillagerConfig/VillagerRecord
        return DEFAULT_TEXTURE;
    }
}
