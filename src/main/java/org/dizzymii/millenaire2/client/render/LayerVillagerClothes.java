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

    /**
     * Creates the clothing-overlay layer and attaches it to the given parent renderer.
     *
     * @param parent the parent renderer that owns this layer
     */
    public LayerVillagerClothes(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    /**
     * Renders the clothing-texture overlay on top of the villager's base skin.
     * <p>
     * Resolves the clothing texture from the villager's {@link VillagerType}; if none is
     * configured the layer is a no-op.  The parent model is re-rendered with the clothing
     * texture using a translucent render type so transparent parts show through correctly.
     *
     * @param poseStack       the current pose stack
     * @param buffer          the multi-buffer source for render types
     * @param packedLight     the packed sky/block light values
     * @param entity          the villager being rendered
     * @param limbSwing       the limb swing animation angle
     * @param limbSwingAmount the amount of limb swing
     * @param partialTick     the partial tick for interpolation
     * @param ageInTicks      the entity's age in ticks
     * @param netHeadYaw      the head yaw relative to the body
     * @param headPitch       the head pitch
     */
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

    /**
     * Converts a clothing texture path string from villager type data into a
     * {@link ResourceLocation}.  Accepted formats:
     * <ul>
     *   <li>{@code "millenaire2:textures/entity/foo.png"} — explicit namespace, used as-is.</li>
     *   <li>{@code "textures/entity/foo.png"} — no namespace, {@code millenaire2} is prepended.</li>
     *   <li>{@code "foo"} or {@code "foo/bar"} — short form, expanded to
     *       {@code millenaire2:textures/entity/foo.png}.</li>
     * </ul>
     *
     * @param texPath the raw texture path from the culture data file
     * @return the resolved {@code ResourceLocation}, or {@code null} if parsing fails
     */
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
