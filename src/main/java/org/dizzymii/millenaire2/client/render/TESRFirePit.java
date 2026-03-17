package org.dizzymii.millenaire2.client.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.dizzymii.millenaire2.entity.blockentity.MillFirePitBlockEntity;

/**
 * Block entity renderer for the fire pit (flame particles, cooking animation).
 * Ported from org.millenaire.client.render.TESRFirePit (Forge 1.12.2).
 */
public class TESRFirePit implements BlockEntityRenderer<MillFirePitBlockEntity> {
    public TESRFirePit(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(MillFirePitBlockEntity blockEntity, float partialTick,
                       com.mojang.blaze3d.vertex.PoseStack poseStack,
                       net.minecraft.client.renderer.MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        // TODO: Implement fire pit rendering (flames, cooking item display)
    }
}
