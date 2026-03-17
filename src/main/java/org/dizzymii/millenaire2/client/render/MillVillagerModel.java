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

    public MillVillagerModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDef = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        return LayerDefinition.create(meshDef, 64, 32);
    }

    // TODO: Override setupAnim for held-item pose adjustments (travel book mock villager)
}
