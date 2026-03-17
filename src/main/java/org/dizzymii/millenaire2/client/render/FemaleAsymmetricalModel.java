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
        // Female chest geometry
        meshDef.getRoot().getChild("body")
                .addOrReplaceChild("chest", net.minecraft.client.model.geom.builders.CubeListBuilder.create()
                        .texOffs(20, 22)
                        .addBox(-3.0F, -4.5F, -3.5F, 6.0F, 3.0F, 2.0F),
                net.minecraft.client.model.geom.PartPose.ZERO);
        // Asymmetric sari drape over left shoulder
        meshDef.getRoot().getChild("body")
                .addOrReplaceChild("sari", net.minecraft.client.model.geom.builders.CubeListBuilder.create()
                        .texOffs(40, 22)
                        .addBox(-5.0F, -6.0F, -2.5F, 3.0F, 12.0F, 5.0F),
                net.minecraft.client.model.geom.PartPose.ZERO);
        return LayerDefinition.create(meshDef, 64, 32);
    }
}
