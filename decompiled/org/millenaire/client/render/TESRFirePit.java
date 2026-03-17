/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.vecmath.Quat4f
 *  javax.vecmath.Vector3f
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.client.renderer.block.model.ItemCameraTransforms$TransformType
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.common.model.TRSRTransformation
 *  org.lwjgl.util.vector.Quaternion
 */
package org.millenaire.client.render;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.model.TRSRTransformation;
import org.lwjgl.util.vector.Quaternion;
import org.millenaire.common.block.BlockFirePit;
import org.millenaire.common.entity.TileEntityFirePit;

public class TESRFirePit
extends TileEntitySpecialRenderer<TileEntityFirePit> {
    private static final TRSRTransformation[] COOKING_POSITIONS = new TRSRTransformation[]{TESRFirePit.get(0.5f, 1.0f, 0.4f, 25.0f, 180.0f, -45.0f, 0.35f), TESRFirePit.get(0.5f, 0.9f, 0.5f, 0.0f, 45.0f, -45.0f, 0.35f), TESRFirePit.get(0.5f, 1.0f, 0.6f, -25.0f, 180.0f, -45.0f, 0.35f)};
    private static final TRSRTransformation[] COOKED_POSITIONS = new TRSRTransformation[]{TESRFirePit.get(0.5f, 0.9f, 0.4f, 25.0f, 180.0f, -45.0f, 0.35f), TESRFirePit.get(0.5f, 0.9f, 0.5f, 0.0f, -45.0f, -45.0f, 0.35f), TESRFirePit.get(0.5f, 0.9f, 0.6f, -25.0f, 180.0f, -45.0f, 0.35f)};
    private static Quaternion transformation = new Quaternion();

    private static void apply(TRSRTransformation transform) {
        Vector3f translate = transform.getTranslation();
        Quat4f left = transform.getLeftRot();
        Quat4f right = transform.getRightRot();
        Vector3f scale = transform.getScale();
        GlStateManager.func_179109_b((float)translate.x, (float)translate.y, (float)translate.z);
        transformation.set(left.x, left.y, left.z, left.w);
        GlStateManager.func_187444_a((Quaternion)transformation);
        transformation.set(right.x, right.y, right.z, right.w);
        GlStateManager.func_187444_a((Quaternion)transformation);
        GlStateManager.func_179152_a((float)scale.x, (float)scale.y, (float)scale.z);
    }

    private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new TRSRTransformation(new Vector3f(tx, ty, tz), TRSRTransformation.quatFromXYZDegrees((Vector3f)new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null);
    }

    private static void render(ItemStack stack, RenderItem item) {
        item.func_181564_a(stack, ItemCameraTransforms.TransformType.FIXED);
    }

    private static void renderPlacedItem(RenderItem item, ItemStack stack, TRSRTransformation transform) {
        if (!stack.func_190926_b()) {
            GlStateManager.func_179094_E();
            TESRFirePit.apply(transform);
            TESRFirePit.render(stack, item);
            GlStateManager.func_179121_F();
        }
    }

    public void render(TileEntityFirePit te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        RenderItem item = Minecraft.func_71410_x().func_175599_af();
        IBlockState firePitBS = te.func_145831_w().func_180495_p(te.func_174877_v());
        if (!(firePitBS.func_177230_c() instanceof BlockFirePit)) {
            return;
        }
        double alignment = ((BlockFirePit.EnumAlignment)((Object)firePitBS.func_177229_b(BlockFirePit.ALIGNMENT))).angle;
        GlStateManager.func_179094_E();
        GlStateManager.func_179137_b((double)(x + 0.5), (double)(y + 0.5), (double)(z + 0.5));
        GlStateManager.func_179114_b((float)((float)alignment), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.func_179137_b((double)-0.5, (double)-0.5, (double)-0.5);
        ItemStack fuel = te.fuel.getStackInSlot(0);
        if (!fuel.func_190926_b()) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179137_b((double)0.5, (double)0.2, (double)0.5);
            GlStateManager.func_179114_b((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            int posTotal = te.func_174877_v().func_177958_n() + te.func_174877_v().func_177956_o() + te.func_174877_v().func_177952_p() & 3;
            GlStateManager.func_179114_b((float)(90 * posTotal), (float)0.0f, (float)0.0f, (float)1.0f);
            GlStateManager.func_179139_a((double)0.5, (double)0.5, (double)0.5);
            TESRFirePit.render(fuel, item);
            GlStateManager.func_179121_F();
        }
        for (int i = 0; i < 3; ++i) {
            ItemStack input = te.inputs.getStackInSlot(i);
            ItemStack output = te.outputs.getStackInSlot(i);
            TESRFirePit.renderPlacedItem(item, input, COOKING_POSITIONS[i]);
            TESRFirePit.renderPlacedItem(item, output, COOKED_POSITIONS[i]);
        }
        GlStateManager.func_179121_F();
    }
}

