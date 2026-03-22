package org.dizzymii.millenaire2.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Unified renderer for all MillVillager entities.
 * Dispatches to one of three internal HumanoidMobRenderer delegates based on
 * the entity's DATA_BODY_MODEL synched field (MALE, SYMM_FEMALE, ASYMM_FEMALE).
 */
public class MillVillagerRenderer extends EntityRenderer<MillVillager> {

    static final ResourceLocation DEFAULT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "textures/entity/villager_default.png");

    private static final long SPEECH_DISPLAY_TICKS = 100;

    private final MaleDelegate maleDelegate;
    private final SymmFemaleDelegate symmFemaleDelegate;
    private final AsymmFemaleDelegate asymmFemaleDelegate;

    public MillVillagerRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        maleDelegate = new MaleDelegate(ctx);
        symmFemaleDelegate = new SymmFemaleDelegate(ctx);
        asymmFemaleDelegate = new AsymmFemaleDelegate(ctx);
    }

    private HumanoidMobRenderer<MillVillager, ?> getDelegate(MillVillager entity) {
        return switch (entity.getBodyModel()) {
            case SYMM_FEMALE -> symmFemaleDelegate;
            case ASYMM_FEMALE -> asymmFemaleDelegate;
            default -> maleDelegate;
        };
    }

    @Override
    public void render(MillVillager entity, float yaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int light) {
        getDelegate(entity).render(entity, yaw, partialTick, poseStack, buffer, light);
    }

    @Override
    public ResourceLocation getTextureLocation(MillVillager entity) {
        return VillagerTextureHelper.resolveTexture(entity, DEFAULT_TEXTURE);
    }

    // ── Shared rendering helpers ──────────────────────────────────────────────

    static void renderSpeechAndName(HumanoidMobRenderer<MillVillager, ?> renderer,
                                    MillVillager entity, Component displayName,
                                    PoseStack poseStack, MultiBufferSource buffer,
                                    int packedLight, float partialTick) {
        if (!renderer.shouldShowName(entity)) return;
        if (entity.getSpeechKey() != null && entity.level() != null) {
            long elapsed = entity.level().getGameTime() - entity.getSpeechStarted();
            if (elapsed >= 0 && elapsed < SPEECH_DISPLAY_TICKS) {
                String speechText = org.dizzymii.millenaire2.util.VillageUtilities
                        .getVillagerSentence("", entity.getSpeechKey());
                poseStack.pushPose();
                poseStack.translate(0, 0.25, 0);
                renderer.renderNameTag(entity, Component.literal("\u00a7e" + speechText),
                        poseStack, buffer, packedLight, partialTick);
                poseStack.popPose();
            } else {
                entity.setSpeechKey(null);
            }
        }
        renderer.renderNameTag(entity, entity.getDisplayName(), poseStack, buffer, packedLight, partialTick);
    }

    static void applyScale(MillVillager entity, PoseStack poseStack) {
        if (entity.getVillagerType() != null && entity.getVillagerType().baseScale != 1.0f) {
            float s = entity.getVillagerType().baseScale;
            poseStack.scale(s, s, s);
        }
    }

    // ── Internal delegates ────────────────────────────────────────────────────

    static final class MaleDelegate extends HumanoidMobRenderer<MillVillager, MillVillagerModel> {
        MaleDelegate(EntityRendererProvider.Context ctx) {
            super(ctx, new MillVillagerModel(ctx.bakeLayer(MillVillagerModel.LAYER_LOCATION)), 0.5F);
            this.addLayer(new LayerVillagerClothes<>(this));
        }
        @Override public ResourceLocation getTextureLocation(MillVillager entity) {
            return VillagerTextureHelper.resolveTexture(entity, DEFAULT_TEXTURE);
        }
        @Override protected void renderNameTag(MillVillager entity, Component displayName,
                PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTick) {
            renderSpeechAndName(this, entity, displayName, poseStack, buffer, packedLight, partialTick);
        }
        @Override protected void scale(MillVillager entity, PoseStack poseStack, float partialTickTime) {
            applyScale(entity, poseStack);
        }
    }

    static final class SymmFemaleDelegate extends HumanoidMobRenderer<MillVillager, FemaleSymmetricalModel> {
        SymmFemaleDelegate(EntityRendererProvider.Context ctx) {
            super(ctx, new FemaleSymmetricalModel(ctx.bakeLayer(FemaleSymmetricalModel.LAYER_LOCATION)), 0.5F);
        }
        @Override public ResourceLocation getTextureLocation(MillVillager entity) {
            return VillagerTextureHelper.resolveTexture(entity, DEFAULT_TEXTURE);
        }
        @Override protected void renderNameTag(MillVillager entity, Component displayName,
                PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTick) {
            renderSpeechAndName(this, entity, displayName, poseStack, buffer, packedLight, partialTick);
        }
        @Override protected void scale(MillVillager entity, PoseStack poseStack, float partialTickTime) {
            applyScale(entity, poseStack);
        }
    }

    static final class AsymmFemaleDelegate extends HumanoidMobRenderer<MillVillager, FemaleAsymmetricalModel> {
        AsymmFemaleDelegate(EntityRendererProvider.Context ctx) {
            super(ctx, new FemaleAsymmetricalModel(ctx.bakeLayer(FemaleAsymmetricalModel.LAYER_LOCATION)), 0.5F);
        }
        @Override public ResourceLocation getTextureLocation(MillVillager entity) {
            return VillagerTextureHelper.resolveTexture(entity, DEFAULT_TEXTURE);
        }
        @Override protected void renderNameTag(MillVillager entity, Component displayName,
                PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTick) {
            renderSpeechAndName(this, entity, displayName, poseStack, buffer, packedLight, partialTick);
        }
        @Override protected void scale(MillVillager entity, PoseStack poseStack, float partialTickTime) {
            applyScale(entity, poseStack);
        }
    }
}
