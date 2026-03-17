package org.dizzymii.millenaire2.client.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

/**
 * Render layer for villager clothing overlays.
 * Ported from org.millenaire.client.render.LayerVillagerClothes (Forge 1.12.2).
 */
public class LayerVillagerClothes<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public LayerVillagerClothes(net.minecraft.client.renderer.entity.RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(com.mojang.blaze3d.vertex.PoseStack poseStack,
                       net.minecraft.client.renderer.MultiBufferSource buffer,
                       int packedLight, T entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        // TODO: Implement clothing texture overlay rendering
    }
}
