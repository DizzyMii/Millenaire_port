package org.dizzymii.millenaire2.client.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.entity.MillVillager;

/**
 * Villager model (male). Extends HumanoidModel for biped rendering.
 * Ported from org.millenaire.client.render.ModelMillVillager (Forge 1.12.2).
 */
public class MillVillagerModel extends HumanoidModel<MillVillager> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "mill_villager"), "main");

    /**
     * Creates the villager model from the baked {@link ModelPart} tree.
     *
     * @param root the root model part produced by baking {@link #createBodyLayer()}
     */
    public MillVillagerModel(ModelPart root) {
        super(root);
    }

    /**
     * Defines the geometry for the villager model layer.
     * Uses the standard humanoid mesh ({@link HumanoidModel#createMesh}) with a
     * 64×32 texture atlas and no cube deformation.
     *
     * @return the layer definition to register with the model-layer system
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDef = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        return LayerDefinition.create(meshDef, 64, 32);
    }

    // Default HumanoidModel animation is used; held-item pose uses vanilla arm logic
}
