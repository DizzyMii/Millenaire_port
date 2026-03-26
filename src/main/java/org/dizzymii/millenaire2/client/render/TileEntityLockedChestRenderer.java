package org.dizzymii.millenaire2.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.dizzymii.millenaire2.entity.blockentity.MillLockedChestBlockEntity;

/**
 * Block entity renderer for locked chests.
 * Renders a lock indicator (text) on the front face of the chest.
 */
public class TileEntityLockedChestRenderer implements BlockEntityRenderer<MillLockedChestBlockEntity> {

    private final Font font;

    public TileEntityLockedChestRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(MillLockedChestBlockEntity blockEntity, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        // Render a lock symbol floating above the chest
        poseStack.pushPose();
        poseStack.translate(0.5, 1.1, 0.5);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.02F, -0.02F, 0.02F);

        String lockIcon = "\uD83D\uDD12"; // Lock emoji
        float offset = (float) (-font.width(lockIcon) / 2);
        font.drawInBatch(lockIcon, offset, 0, 0xFF4444, false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL,
                0x40000000, packedLight);

        poseStack.popPose();
    }
}
