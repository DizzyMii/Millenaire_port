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
 * Female asymmetrical model (e.g. Indian women with sari).
 * Ported from org.millenaire.client.render.ModelFemaleAsymmetrical (Forge 1.12.2).
 */
public class FemaleAsymmetricalModel extends HumanoidModel<MillVillager.GenericAsymmFemale> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "mill_villager_female_asymm"), "main");

    public FemaleAsymmetricalModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDef = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        // TODO: Add asymmetric clothing cubes for sari/wrap model
        return LayerDefinition.create(meshDef, 64, 32);
    }
}
