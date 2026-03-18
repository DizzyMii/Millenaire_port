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
        if (blockEntity.isLit()) {
            // Render flame particles
            net.minecraft.core.BlockPos pos = blockEntity.getBlockPos();
            net.minecraft.world.level.Level level = blockEntity.getLevel();
            if (level != null && level.random.nextInt(4) == 0) {
                double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.4;
                double y = pos.getY() + 0.5;
                double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.4;
                level.addParticle(net.minecraft.core.particles.ParticleTypes.FLAME, x, y, z, 0, 0.02, 0);
                level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE, x, y + 0.2, z, 0, 0.03, 0);
            }
        }

        // Render cooking item floating above the fire pit
        net.minecraft.world.item.ItemStack cooking = blockEntity.getCookingItem();
        if (!cooking.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.0, 0.5);
            float rotation = (blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0) + partialTick;
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation * 2.0F));
            poseStack.scale(0.5F, 0.5F, 0.5F);
            net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderStatic(
                    cooking, net.minecraft.world.item.ItemDisplayContext.FIXED,
                    packedLight, packedOverlay, poseStack, buffer, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }
    }
}
