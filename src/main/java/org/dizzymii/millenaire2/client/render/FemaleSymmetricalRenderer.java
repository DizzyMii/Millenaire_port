package org.dizzymii.millenaire2.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Renderer for female symmetrical villagers (e.g. Norman, Japanese women).
 */
public class FemaleSymmetricalRenderer extends HumanoidMobRenderer<MillVillager.GenericSymmFemale, FemaleSymmetricalModel> {

    private static final ResourceLocation DEFAULT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "textures/entity/villager_default.png");

    public FemaleSymmetricalRenderer(EntityRendererProvider.Context context) {
        super(context, new FemaleSymmetricalModel(context.bakeLayer(FemaleSymmetricalModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(MillVillager.GenericSymmFemale entity) {
        return DEFAULT_TEXTURE;
    }
}
