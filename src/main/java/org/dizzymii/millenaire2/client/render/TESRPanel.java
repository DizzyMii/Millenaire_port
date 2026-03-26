package org.dizzymii.millenaire2.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.dizzymii.millenaire2.entity.blockentity.MillPanelBlockEntity;

/**
 * Block entity renderer for village info panels (sign-like text display).
 * Renders the village name and culture as floating text above the panel block.
 */
public class TESRPanel implements BlockEntityRenderer<MillPanelBlockEntity> {

    private final Font font;

    public TESRPanel(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(MillPanelBlockEntity blockEntity, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        String villageName = blockEntity.getVillageName();
        String cultureName = blockEntity.getCultureName();
        if (villageName.isEmpty() && cultureName.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.1, 0.5);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        if (!villageName.isEmpty()) {
            float offset = (float) (-font.width(villageName) / 2);
            font.drawInBatch(villageName, offset, 0, 0xFFFFFF, false,
                    poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL,
                    0x40000000, packedLight);
        }
        if (!cultureName.isEmpty()) {
            float offset = (float) (-font.width(cultureName) / 2);
            font.drawInBatch(cultureName, offset, 10, 0xCCCCCC, false,
                    poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL,
                    0x40000000, packedLight);
        }

        poseStack.popPose();
    }
}
