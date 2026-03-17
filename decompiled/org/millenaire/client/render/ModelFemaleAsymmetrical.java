/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 */
package org.millenaire.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.millenaire.client.render.ModelMillVillager;

public class ModelFemaleAsymmetrical
extends ModelMillVillager {
    private final ModelRenderer bipedBreast;

    public ModelFemaleAsymmetrical() {
        this(0.0f);
    }

    public ModelFemaleAsymmetrical(float f) {
        this(f, 0.0f);
    }

    public ModelFemaleAsymmetrical(float f, float f1) {
        this.field_78117_n = false;
        this.field_78116_c = new ModelRenderer((ModelBase)this, 0, 0);
        this.field_78116_c.func_78790_a(-4.0f, -8.0f, -4.0f, 8, 8, 8, f);
        this.field_78116_c.func_78793_a(0.0f, 0.0f + f1, 0.0f);
        this.field_178720_f = new ModelRenderer((ModelBase)this, 32, 0);
        this.field_178720_f.func_78790_a(-4.0f, -8.0f, -4.0f, 8, 8, 8, f + 0.5f);
        this.field_178720_f.func_78793_a(0.0f, 0.0f + f1, 0.0f);
        this.field_78115_e = new ModelRenderer((ModelBase)this, 16, 17);
        this.field_78115_e.func_78790_a(-3.5f, 0.0f, -1.5f, 7, 12, 3, f);
        this.field_78115_e.func_78793_a(0.0f, 0.0f + f1, 0.0f);
        this.bipedBreast = new ModelRenderer((ModelBase)this, 17, 18);
        this.bipedBreast.func_78790_a(-3.5f, 0.75f, -3.0f, 7, 4, 2, f);
        this.bipedBreast.func_78793_a(0.0f, 0.0f + f1, 0.0f);
        this.field_78115_e.func_78792_a(this.bipedBreast);
        this.field_178723_h = new ModelRenderer((ModelBase)this, 36, 17);
        this.field_178723_h.func_78790_a(-1.5f, -2.0f, -1.5f, 3, 12, 3, f);
        this.field_178723_h.func_78793_a(-5.0f, 2.0f + f1, 0.0f);
        this.field_178724_i = new ModelRenderer((ModelBase)this, 36, 17);
        this.field_178724_i.field_78809_i = true;
        this.field_178724_i.func_78790_a(-1.5f, -2.0f, -1.5f, 3, 12, 3, f);
        this.field_178724_i.func_78793_a(5.0f, 2.0f + f1, 0.0f);
        this.field_178721_j = new ModelRenderer((ModelBase)this, 0, 16);
        this.field_178721_j.func_78790_a(-2.0f, 0.0f, -2.0f, 4, 12, 4, f);
        this.field_178721_j.func_78793_a(-2.0f, 12.0f + f1, 0.0f);
        this.field_178722_k = new ModelRenderer((ModelBase)this, 48, 16);
        this.field_178722_k.func_78790_a(-2.0f, 0.0f, -2.0f, 4, 12, 4, f);
        this.field_178722_k.func_78793_a(2.0f, 12.0f + f1, 0.0f);
    }

    public void func_78088_a(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.func_78088_a(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }
}

