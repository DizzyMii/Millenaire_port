/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class ModelPanel
extends ModelBase {
    public ModelRenderer signBoard = new ModelRenderer((ModelBase)this, 0, 0);

    public ModelPanel() {
        this.signBoard.func_78790_a(-12.0f, -12.0f, -1.0f, 24, 24, 2, 0.0f);
    }

    public void renderSign() {
        this.signBoard.func_78785_a(0.0625f);
    }
}

