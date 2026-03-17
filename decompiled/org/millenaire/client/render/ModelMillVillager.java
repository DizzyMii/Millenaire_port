/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBiped
 *  net.minecraft.entity.Entity
 */
package org.millenaire.client.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import org.millenaire.common.entity.MillVillager;

public class ModelMillVillager
extends ModelBiped {
    public ModelMillVillager() {
        this(0.0f);
    }

    public ModelMillVillager(float f) {
        super(f);
    }

    public void func_78087_a(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.func_78087_a(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        MillVillager villager = (MillVillager)entityIn;
        if (villager.travelBookMockVillager && villager.heldItem != null) {
            this.field_178724_i.field_78795_f = -0.6f;
            this.field_178724_i.field_78808_h = -0.2f;
        }
        if (villager.travelBookMockVillager && villager.heldItemOffHand != null) {
            this.field_178723_h.field_78795_f = -0.5f;
            this.field_178723_h.field_78808_h = 0.1f;
        }
    }
}

