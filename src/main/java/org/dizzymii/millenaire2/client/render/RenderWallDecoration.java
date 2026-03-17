package org.dizzymii.millenaire2.client.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.entity.EntityWallDecoration;

/**
 * Renderer for wall decoration entities (tapestries, paintings, etc.).
 * Ported from org.millenaire.client.render.RenderWallDecoration (Forge 1.12.2).
 */
public class RenderWallDecoration extends EntityRenderer<EntityWallDecoration> {
    public RenderWallDecoration(EntityRendererProvider.Context context) {
        super(context);
    }

    private static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath("millenaire2", "textures/entity/wall_decoration/default.png"),
            ResourceLocation.fromNamespaceAndPath("millenaire2", "textures/entity/wall_decoration/norman_tapestry.png"),
            ResourceLocation.fromNamespaceAndPath("millenaire2", "textures/entity/wall_decoration/indian_statue.png"),
            ResourceLocation.fromNamespaceAndPath("millenaire2", "textures/entity/wall_decoration/mayan_statue.png"),
            ResourceLocation.fromNamespaceAndPath("millenaire2", "textures/entity/wall_decoration/byzantine_icon_small.png"),
            ResourceLocation.fromNamespaceAndPath("millenaire2", "textures/entity/wall_decoration/byzantine_icon_medium.png"),
            ResourceLocation.fromNamespaceAndPath("millenaire2", "textures/entity/wall_decoration/byzantine_icon_large.png"),
            ResourceLocation.fromNamespaceAndPath("millenaire2", "textures/entity/wall_decoration/hide_hanging.png"),
            ResourceLocation.fromNamespaceAndPath("millenaire2", "textures/entity/wall_decoration/japanese_painting_small.png"),
            ResourceLocation.fromNamespaceAndPath("millenaire2", "textures/entity/wall_decoration/japanese_painting_medium.png"),
            ResourceLocation.fromNamespaceAndPath("millenaire2", "textures/entity/wall_decoration/japanese_painting_large.png"),
    };

    @Override
    public ResourceLocation getTextureLocation(EntityWallDecoration entity) {
        int type = entity.getDecorationType();
        if (type >= 0 && type < TEXTURES.length) {
            return TEXTURES[type];
        }
        return TEXTURES[0];
    }

    @Override
    public void render(EntityWallDecoration entity, float yaw, float partialTick,
                       com.mojang.blaze3d.vertex.PoseStack poseStack,
                       net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - yaw));
        // Flat quad rendering; actual texture binding done via getTextureLocation
        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
