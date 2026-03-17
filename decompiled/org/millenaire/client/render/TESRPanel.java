/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.client.renderer.block.model.ItemCameraTransforms$TransformType
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.client.render.ModelPanel;
import org.millenaire.common.entity.TileEntityPanel;

@SideOnly(value=Side.CLIENT)
public class TESRPanel
extends TileEntitySpecialRenderer<TileEntityPanel> {
    private static final ResourceLocation PANEL_TEXTURE = new ResourceLocation("millenaire", "textures/entity/panels/default.png");
    private final ModelPanel model = new ModelPanel();

    private void drawIcon(int linePos, ItemStack icon, float xTranslate) {
        GlStateManager.func_179094_E();
        GlStateManager.func_179137_b((double)xTranslate, (double)((double)-0.74f + (double)linePos * 0.15), (double)-0.09);
        this.renderItem2d(icon, 0.3f);
        GlStateManager.func_179121_F();
    }

    public void render(TileEntityPanel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.func_179094_E();
        int k = te.func_145832_p();
        float f2 = 0.0f;
        if (k == 2) {
            f2 = 180.0f;
        }
        if (k == 4) {
            f2 = 90.0f;
        }
        if (k == 5) {
            f2 = -90.0f;
        }
        GlStateManager.func_179109_b((float)((float)x + 0.5f), (float)((float)y + 0.5f), (float)((float)z + 0.5f));
        GlStateManager.func_179114_b((float)(-f2), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.func_179109_b((float)0.0f, (float)0.0f, (float)-0.4375f);
        if (destroyStage >= 0) {
            this.func_147499_a(field_178460_a[destroyStage]);
            GlStateManager.func_179128_n((int)5890);
            GlStateManager.func_179094_E();
            GlStateManager.func_179152_a((float)4.0f, (float)2.0f, (float)1.0f);
            GlStateManager.func_179109_b((float)0.0625f, (float)0.0625f, (float)0.0625f);
            GlStateManager.func_179128_n((int)5888);
        } else {
            ResourceLocation texture = te.texture != null ? te.texture : PANEL_TEXTURE;
            this.func_147499_a(texture);
        }
        GlStateManager.func_179091_B();
        GlStateManager.func_179094_E();
        GlStateManager.func_179152_a((float)0.6666667f, (float)-0.6666667f, (float)-0.6666667f);
        this.model.renderSign();
        GlStateManager.func_179137_b((double)0.0, (double)0.24, (double)0.0);
        te.translateLines(this.func_147498_b());
        for (int pos = 0; pos < te.displayLines.size(); ++pos) {
            TileEntityPanel.PanelDisplayLine line = te.displayLines.get(pos);
            this.drawIcon(pos, line.leftIcon, -0.54f);
            this.drawIcon(pos, line.middleIcon, 0.08f);
            this.drawIcon(pos, line.rightIcon, 0.54f);
        }
        GlStateManager.func_179121_F();
        FontRenderer fontrenderer = this.func_147498_b();
        GlStateManager.func_179137_b((double)0.0, (double)0.25, (double)0.046666666865348816);
        GlStateManager.func_179152_a((float)0.010416667f, (float)-0.010416667f, (float)0.010416667f);
        GlStateManager.func_187432_a((float)0.0f, (float)0.0f, (float)-0.010416667f);
        GlStateManager.func_179132_a((boolean)false);
        if (destroyStage < 0) {
            for (int pos = 0; pos < te.displayLines.size(); ++pos) {
                TileEntityPanel.PanelDisplayLine line = te.displayLines.get(pos);
                if (line.centerLine) {
                    fontrenderer.func_78276_b(line.fullLine, -fontrenderer.func_78256_a(line.fullLine) / 2, pos * 10 - 15, 0);
                } else {
                    fontrenderer.func_78276_b(line.fullLine, -29, pos * 10 - 15, 0);
                }
                fontrenderer.func_78276_b(line.leftColumn, -29, pos * 10 - 15, 0);
                fontrenderer.func_78276_b(line.rightColumn, 11, pos * 10 - 15, 0);
            }
        }
        GlStateManager.func_179132_a((boolean)true);
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.func_179121_F();
        if (destroyStage >= 0) {
            GlStateManager.func_179128_n((int)5890);
            GlStateManager.func_179121_F();
            GlStateManager.func_179128_n((int)5888);
        }
    }

    private void renderItem2d(ItemStack itemStack, float scale) {
        if (!itemStack.func_190926_b()) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179152_a((float)(scale / 32.0f), (float)(scale / 32.0f), (float)-1.0E-4f);
            GlStateManager.func_179109_b((float)-8.0f, (float)-11.0f, (float)-420.0f);
            RenderItem renderItem = Minecraft.func_71410_x().func_175599_af();
            renderItem.func_180450_b(itemStack, 0, 0);
            GlStateManager.func_179121_F();
        }
    }

    private void renderItem3d(ItemStack itemstack) {
        if (!itemstack.func_190926_b()) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179140_f();
            if (itemstack.func_77973_b() instanceof ItemBlock) {
                GlStateManager.func_179139_a((double)0.25, (double)0.25, (double)0.25);
            } else {
                GlStateManager.func_179139_a((double)-0.15, (double)-0.15, (double)0.15);
            }
            GlStateManager.func_179123_a();
            RenderHelper.func_74519_b();
            Minecraft.func_71410_x().func_175599_af().func_181564_a(itemstack, ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.func_74518_a();
            GlStateManager.func_179099_b();
            GlStateManager.func_179145_e();
            GlStateManager.func_179121_F();
        }
    }
}

