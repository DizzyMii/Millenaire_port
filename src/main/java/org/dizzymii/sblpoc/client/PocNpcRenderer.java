package org.dizzymii.sblpoc.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.sblpoc.PocNpc;

/**
 * Simple renderer for the PoC NPC using the standard humanoid (player) model.
 * HumanoidMobRenderer already includes ItemInHandLayer, so held items
 * (sword, shield) and eating animations render automatically.
 */
public class PocNpcRenderer extends HumanoidMobRenderer<PocNpc, HumanoidModel<PocNpc>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/steve.png");

    public PocNpcRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(PocNpc entity) {
        return TEXTURE;
    }
}
