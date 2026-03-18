package org.dizzymii.millenaire2.client.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.VillagerAnimState;

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

    @Override
    public void setupAnim(MillVillager entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        VillagerAnimState animState = entity.getAnimState();
        switch (animState) {
            case WORKING:
                // Rhythmic arm swing (like mining/hammering)
                float workCycle = ageInTicks * 0.15F;
                this.rightArm.xRot = -1.2F + Mth.sin(workCycle) * 0.5F;
                this.leftArm.xRot = -0.4F;
                break;

            case SLEEPING:
                // Lying flat - rotate body and limbs
                this.head.xRot = 0.0F;
                this.head.yRot = 0.0F;
                this.rightArm.xRot = -0.1F;
                this.leftArm.xRot = -0.1F;
                this.rightLeg.xRot = 0.0F;
                this.leftLeg.xRot = 0.0F;
                break;

            case COMBAT_MELEE:
                // Aggressive stance - arms raised
                this.rightArm.xRot = -1.0F + Mth.sin(ageInTicks * 0.3F) * 0.3F;
                this.leftArm.xRot = -0.5F;
                break;

            case COMBAT_BOW:
                // Bow drawing pose
                this.rightArm.xRot = -1.5F;
                this.rightArm.yRot = -0.1F;
                this.leftArm.xRot = -1.5F;
                this.leftArm.yRot = 0.5F;
                break;

            case SITTING:
                // Bent legs for sitting
                this.rightLeg.xRot = -1.4F;
                this.leftLeg.xRot = -1.4F;
                this.rightArm.xRot = -0.3F;
                this.leftArm.xRot = -0.3F;
                break;

            case EATING:
                // One hand raised to mouth
                this.rightArm.xRot = -1.8F;
                this.rightArm.yRot = 0.3F;
                break;

            case IDLE:
            case WALKING:
            default:
                // Default HumanoidModel animation handles these
                break;
        }
    }
}
