package org.dizzymii.millenaire2.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Renderer for MillVillager (male).
 * Ported from org.millenaire.client.render.RenderMillVillager (Forge 1.12.2).
 *
 * In NeoForge 1.21.1, renderers use EntityRendererProvider and HumanoidMobRenderer.
 */
public class MillVillagerRenderer extends HumanoidMobRenderer<MillVillager, MillVillagerModel> {

    private static final ResourceLocation DEFAULT_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/player/wide/steve.png");

    public MillVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new MillVillagerModel(context.bakeLayer(MillVillagerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new LayerVillagerClothes<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(MillVillager entity) {
        return VillagerTextureHelper.resolveTexture(entity, DEFAULT_TEXTURE);
    }

    private static final long SPEECH_DISPLAY_TICKS = 100; // 5 seconds

    @Override
    protected void renderNameTag(MillVillager entity, Component displayName,
                                 PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTick) {
        if (!this.shouldShowName(entity)) return;

        // Render speech bubble above the name if active
        if (entity.speech_key != null && entity.level() != null) {
            long elapsed = entity.level().getGameTime() - entity.speech_started;
            if (elapsed >= 0 && elapsed < SPEECH_DISPLAY_TICKS) {
                String speechText = org.dizzymii.millenaire2.util.VillageUtilities
                        .getVillagerSentence("", entity.speech_key);
                poseStack.pushPose();
                poseStack.translate(0, 0.25, 0);
                super.renderNameTag(entity, Component.literal("\u00a7e" + speechText),
                        poseStack, buffer, packedLight, partialTick);
                poseStack.popPose();
            } else {
                entity.speech_key = null;
            }
        }

        super.renderNameTag(entity, entity.getDisplayName(), poseStack, buffer, packedLight, partialTick);
    }

    @Override
    protected void scale(MillVillager entity, PoseStack poseStack, float partialTickTime) {
        // Apply villager-type-specific scale if available
        if (entity.vtype != null && entity.vtype.baseScale != 1.0f) {
            float s = entity.vtype.baseScale;
            poseStack.scale(s, s, s);
        }
    }
}
