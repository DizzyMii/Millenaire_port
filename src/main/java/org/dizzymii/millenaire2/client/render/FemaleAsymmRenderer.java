package org.dizzymii.millenaire2.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Renderer for female asymmetrical villagers (Indian sari, etc.).
 */
public class FemaleAsymmRenderer extends HumanoidMobRenderer<MillVillager.GenericAsymmFemale, FemaleAsymmetricalModel> {

    private static final ResourceLocation DEFAULT_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");

    public FemaleAsymmRenderer(EntityRendererProvider.Context context) {
        super(context, new FemaleAsymmetricalModel(context.bakeLayer(FemaleAsymmetricalModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MillVillager.GenericAsymmFemale entity) {
        return VillagerTextureHelper.resolveTexture(entity, DEFAULT_TEXTURE);
    }
}
