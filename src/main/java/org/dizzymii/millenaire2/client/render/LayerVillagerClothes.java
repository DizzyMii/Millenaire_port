package org.dizzymii.millenaire2.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Render layer for villager clothing overlays.
 * Ported from org.millenaire.client.render.LayerVillagerClothes (Forge 1.12.2).
 *
 * Renders a second texture pass on top of the base skin, using the villager type's
 * clothing texture if one is defined.
 */
public class LayerVillagerClothes<T extends MillVillager, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    public LayerVillagerClothes(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, T entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        VillagerType vtype = entity.vtype;
        if (vtype == null) return;

        // Resolve clothing texture from villager type
        String clothingTex = vtype.clothingTexture;
        if (clothingTex == null || clothingTex.isEmpty()) return;

        ResourceLocation clothingRL = resolveClothingTexture(clothingTex);
        if (clothingRL == null) return;

        // Render the parent model again with the clothing texture
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(clothingRL));
        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight,
                OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }

    private static ResourceLocation resolveClothingTexture(String texPath) {
        if (texPath.contains(":")) {
            return ResourceLocation.tryParse(texPath);
        }
        if (texPath.startsWith("textures/")) {
            return ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, texPath);
        }
        String fullPath = "textures/entity/" + texPath;
        if (!fullPath.endsWith(".png")) {
            fullPath += ".png";
        }
        return ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, fullPath);
    }
}
