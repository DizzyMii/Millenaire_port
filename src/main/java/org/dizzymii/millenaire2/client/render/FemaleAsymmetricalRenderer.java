package org.dizzymii.millenaire2.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Renderer for female asymmetrical villagers (e.g. Indian women with sari).
 */
public class FemaleAsymmetricalRenderer extends HumanoidMobRenderer<MillVillager.GenericAsymmFemale, FemaleAsymmetricalModel> {

    private static final ResourceLocation DEFAULT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "textures/entity/villager_default.png");

    public FemaleAsymmetricalRenderer(EntityRendererProvider.Context context) {
        super(context, new FemaleAsymmetricalModel(context.bakeLayer(FemaleAsymmetricalModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MillVillager.GenericAsymmFemale entity) {
        return DEFAULT_TEXTURE;
    }
}
