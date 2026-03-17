/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.entity.RenderLivingBase
 *  net.minecraft.client.renderer.entity.layers.LayerRenderer
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 */
package org.millenaire.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.millenaire.client.render.ModelFemaleAsymmetrical;
import org.millenaire.client.render.ModelFemaleSymmetrical;
import org.millenaire.client.render.ModelMillVillager;
import org.millenaire.common.entity.MillVillager;

public class LayerVillagerClothes
implements LayerRenderer<EntityLivingBase> {
    protected final ModelMillVillager modelCloth;
    private final RenderLivingBase<MillVillager> renderer;
    private final float alpha = 1.0f;
    private final float colorR = 1.0f;
    private final float colorG = 1.0f;
    private final float colorB = 1.0f;
    private final int layer;

    public LayerVillagerClothes(RenderLivingBase<MillVillager> rendererIn, ModelMillVillager modelbiped, int layer) {
        this.renderer = rendererIn;
        this.layer = layer;
        float offset = 0.1f * (float)(layer + 1);
        this.modelCloth = modelbiped instanceof ModelFemaleAsymmetrical ? new ModelFemaleAsymmetrical(offset) : (modelbiped instanceof ModelFemaleSymmetrical ? new ModelFemaleSymmetrical(offset) : new ModelMillVillager(offset));
    }

    public void func_177141_a(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.renderClothLayer((MillVillager)entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
    }

    private void renderClothLayer(MillVillager villager, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (villager.getClothTexturePath(this.layer) != null) {
            this.modelCloth.func_178686_a(this.renderer.func_177087_b());
            this.modelCloth.func_78086_a((EntityLivingBase)villager, limbSwing, limbSwingAmount, partialTicks);
            this.renderer.func_110776_a(villager.getClothTexturePath(this.layer));
            this.getClass();
            this.getClass();
            this.getClass();
            this.getClass();
            GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            this.modelCloth.func_78088_a((Entity)villager, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    public boolean func_177142_b() {
        return false;
    }
}

