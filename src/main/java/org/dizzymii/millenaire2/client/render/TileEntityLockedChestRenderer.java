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

    /** Minecraft's default font supports basic ASCII/Latin; use a bracket-based lock symbol. */
    private static final String LOCK_LABEL = "[Locked]";
    private static final int LOCK_TEXT_COLOR = 0xFF4444;
    private static final int TEXT_BACKGROUND_COLOR = 0x40000000;

    private final Font font;

    public TileEntityLockedChestRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(MillLockedChestBlockEntity blockEntity, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        // Render a lock label floating above the chest
        poseStack.pushPose();
        poseStack.translate(0.5, 1.1, 0.5);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.02F, -0.02F, 0.02F);

        float offset = (float) (-font.width(LOCK_LABEL) / 2);
        font.drawInBatch(LOCK_LABEL, offset, 0, LOCK_TEXT_COLOR, false,
                poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL,
                TEXT_BACKGROUND_COLOR, packedLight);

        poseStack.popPose();
    }
}
