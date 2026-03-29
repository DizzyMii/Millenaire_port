package org.dizzymii.millenaire2.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Renderer for MillVillager (male).
 * Ported from org.millenaire.client.render.RenderMillVillager (Forge 1.12.2).
 *
 * In NeoForge 1.21.1, renderers use EntityRendererProvider and HumanoidMobRenderer.
 */
public class MillVillagerRenderer extends HumanoidMobRenderer<MillVillager, MillVillagerModel> {

    private static final ResourceLocation DEFAULT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "textures/entity/villager_default.png");

    /**
     * Creates the renderer, bakes the villager model layer, adds the clothing
     * overlay layer, and sets the shadow radius to 0.5.
     *
     * @param context the renderer provider context supplied by NeoForge
     */
    public MillVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new MillVillagerModel(context.bakeLayer(MillVillagerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new LayerVillagerClothes<>(this));
    }

    /**
     * Resolves the skin texture for the given villager entity.
     * Delegates to {@link VillagerTextureHelper#resolveTexture} and falls back to
     * {@link #DEFAULT_TEXTURE} when no culture-specific texture is available.
     *
     * @param entity the villager entity being rendered
     * @return the {@link ResourceLocation} of the skin texture to use
     */
    @Override
    public ResourceLocation getTextureLocation(MillVillager entity) {
        return VillagerTextureHelper.resolveTexture(entity, DEFAULT_TEXTURE);
    }

    private static final long SPEECH_DISPLAY_TICKS = 100; // 5 seconds

    /**
     * Renders the villager's name tag and, if the villager is currently speaking,
     * an additional speech-bubble line above the name tag.
     * <p>
     * The speech bubble is visible for {@value #SPEECH_DISPLAY_TICKS} ticks after
     * {@link MillVillager#speech_started}; once expired the {@code speech_key}
     * field is cleared on the client.
     *
     * @param entity      the villager being rendered
     * @param displayName the component to render as the name tag
     * @param poseStack   the current pose stack
     * @param buffer      the multi-buffer source
     * @param packedLight the packed sky/block light
     * @param partialTick the partial tick for interpolation
     */
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

    /**
     * Applies a uniform scale to the pose stack based on the villager's type.
     * Uses {@link org.dizzymii.millenaire2.culture.VillagerType#baseScale} when available;
     * a {@code baseScale} of {@code 1.0f} results in no transformation.
     *
     * @param entity          the villager being rendered
     * @param poseStack       the current pose stack
     * @param partialTickTime the partial tick for interpolation
     */
    @Override
    protected void scale(MillVillager entity, PoseStack poseStack, float partialTickTime) {
        // Apply villager-type-specific scale if available
        if (entity.vtype != null && entity.vtype.baseScale != 1.0f) {
            float s = entity.vtype.baseScale;
            poseStack.scale(s, s, s);
        }
    }
}
