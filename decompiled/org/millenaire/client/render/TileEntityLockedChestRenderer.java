/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockChest
 *  net.minecraft.client.model.ModelChest
 *  net.minecraft.client.model.ModelLargeChest
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.client.render;

import java.util.Calendar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.entity.TileEntityLockedChest;

@SideOnly(value=Side.CLIENT)
public class TileEntityLockedChestRenderer
extends TileEntitySpecialRenderer<TileEntityLockedChest> {
    private static final ResourceLocation TEXTURE_CHRISTMAS_DOUBLE = new ResourceLocation("textures/entity/chest/christmas_double.png");
    private static final ResourceLocation TEXTURE_NORMAL_DOUBLE = new ResourceLocation("textures/entity/chest/normal_double.png");
    private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
    private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("textures/entity/chest/normal.png");
    private final ModelChest simpleChest = new ModelChest();
    private final ModelChest largeChest = new ModelLargeChest();
    private boolean isChristmas;

    public TileEntityLockedChestRenderer() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            this.isChristmas = true;
        }
    }

    public void render(TileEntityLockedChest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        int i;
        GlStateManager.func_179126_j();
        GlStateManager.func_179143_c((int)515);
        GlStateManager.func_179132_a((boolean)true);
        if (te.func_145830_o()) {
            Block block = te.func_145838_q();
            i = te.func_145832_p();
            if (block instanceof BlockChest && i == 0) {
                ((BlockChest)block).func_176455_e(te.func_145831_w(), te.func_174877_v(), te.func_145831_w().func_180495_p(te.func_174877_v()));
                i = te.func_145832_p();
            }
            te.checkForAdjacentChests();
        } else {
            i = 0;
        }
        if (te.adjacentChestZNeg == null && te.adjacentChestXNeg == null) {
            float f2;
            float f1;
            ModelChest modelchest;
            if (te.adjacentChestXPos == null && te.adjacentChestZPos == null) {
                modelchest = this.simpleChest;
                if (destroyStage >= 0) {
                    this.func_147499_a(field_178460_a[destroyStage]);
                    GlStateManager.func_179128_n((int)5890);
                    GlStateManager.func_179094_E();
                    GlStateManager.func_179152_a((float)4.0f, (float)4.0f, (float)1.0f);
                    GlStateManager.func_179109_b((float)0.0625f, (float)0.0625f, (float)0.0625f);
                    GlStateManager.func_179128_n((int)5888);
                } else if (this.isChristmas) {
                    this.func_147499_a(TEXTURE_CHRISTMAS);
                } else {
                    this.func_147499_a(TEXTURE_NORMAL);
                }
            } else {
                modelchest = this.largeChest;
                if (destroyStage >= 0) {
                    this.func_147499_a(field_178460_a[destroyStage]);
                    GlStateManager.func_179128_n((int)5890);
                    GlStateManager.func_179094_E();
                    GlStateManager.func_179152_a((float)8.0f, (float)4.0f, (float)1.0f);
                    GlStateManager.func_179109_b((float)0.0625f, (float)0.0625f, (float)0.0625f);
                    GlStateManager.func_179128_n((int)5888);
                } else if (this.isChristmas) {
                    this.func_147499_a(TEXTURE_CHRISTMAS_DOUBLE);
                } else {
                    this.func_147499_a(TEXTURE_NORMAL_DOUBLE);
                }
            }
            GlStateManager.func_179094_E();
            GlStateManager.func_179091_B();
            if (destroyStage < 0) {
                GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
            }
            GlStateManager.func_179109_b((float)((float)x), (float)((float)y + 1.0f), (float)((float)z + 1.0f));
            GlStateManager.func_179152_a((float)1.0f, (float)-1.0f, (float)-1.0f);
            GlStateManager.func_179109_b((float)0.5f, (float)0.5f, (float)0.5f);
            int j = 0;
            if (i == 2) {
                j = 180;
            }
            if (i == 3) {
                j = 0;
            }
            if (i == 4) {
                j = 90;
            }
            if (i == 5) {
                j = -90;
            }
            if (i == 2 && te.adjacentChestXPos != null) {
                GlStateManager.func_179109_b((float)1.0f, (float)0.0f, (float)0.0f);
            }
            if (i == 5 && te.adjacentChestZPos != null) {
                GlStateManager.func_179109_b((float)0.0f, (float)0.0f, (float)-1.0f);
            }
            GlStateManager.func_179114_b((float)j, (float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.func_179109_b((float)-0.5f, (float)-0.5f, (float)-0.5f);
            float f = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;
            if (te.adjacentChestZNeg != null && (f1 = te.adjacentChestZNeg.prevLidAngle + (te.adjacentChestZNeg.lidAngle - te.adjacentChestZNeg.prevLidAngle) * partialTicks) > f) {
                f = f1;
            }
            if (te.adjacentChestXNeg != null && (f2 = te.adjacentChestXNeg.prevLidAngle + (te.adjacentChestXNeg.lidAngle - te.adjacentChestXNeg.prevLidAngle) * partialTicks) > f) {
                f = f2;
            }
            f = 1.0f - f;
            f = 1.0f - f * f * f;
            modelchest.field_78234_a.field_78795_f = -(f * 1.5707964f);
            modelchest.func_78231_a();
            GlStateManager.func_179101_C();
            GlStateManager.func_179121_F();
            GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            if (destroyStage >= 0) {
                GlStateManager.func_179128_n((int)5890);
                GlStateManager.func_179121_F();
                GlStateManager.func_179128_n((int)5888);
            }
        }
    }
}

