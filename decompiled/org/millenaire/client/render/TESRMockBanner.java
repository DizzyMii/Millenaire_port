/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.BlockBanner$BlockBannerStanding
 *  net.minecraft.client.model.ModelBanner
 *  net.minecraft.client.renderer.BannerTextures
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.tileentity.TileEntityBanner
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 */
package org.millenaire.client.render;

import javax.annotation.Nullable;
import net.minecraft.block.BlockBanner;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.millenaire.common.entity.TileEntityMockBanner;

public class TESRMockBanner
extends TileEntitySpecialRenderer<TileEntityMockBanner> {
    private final ModelBanner bannerModel = new ModelBanner();

    @Nullable
    private ResourceLocation getBannerResourceLocation(TileEntityBanner bannerObj) {
        return BannerTextures.field_178466_c.func_187478_a(bannerObj.func_175116_e(), bannerObj.func_175114_c(), bannerObj.func_175110_d());
    }

    public void render(TileEntityMockBanner te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        boolean isWorldValid = te.func_145831_w() != null;
        boolean standing = !isWorldValid || te.func_145838_q() instanceof BlockBanner.BlockBannerStanding;
        int rotation = isWorldValid ? te.func_145832_p() : 0;
        long j = isWorldValid ? te.func_145831_w().func_82737_E() : 0L;
        GlStateManager.func_179094_E();
        if (standing) {
            GlStateManager.func_179109_b((float)((float)x + 0.5f), (float)((float)y + 0.5f), (float)((float)z + 0.5f));
            float f1 = (float)(rotation * 360) / 16.0f;
            GlStateManager.func_179114_b((float)(-f1), (float)0.0f, (float)1.0f, (float)0.0f);
            this.bannerModel.field_178688_b.field_78806_j = true;
        } else {
            float f2 = 0.0f;
            if (rotation == 2) {
                f2 = 180.0f;
            }
            if (rotation == 4) {
                f2 = 90.0f;
            }
            if (rotation == 5) {
                f2 = -90.0f;
            }
            GlStateManager.func_179109_b((float)((float)x + 0.5f), (float)((float)y - 0.16666667f), (float)((float)z + 0.5f));
            GlStateManager.func_179114_b((float)(-f2), (float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.func_179109_b((float)0.0f, (float)-0.3125f, (float)-0.4375f);
            this.bannerModel.field_178688_b.field_78806_j = false;
        }
        BlockPos blockpos = te.func_174877_v();
        float f3 = (float)(blockpos.func_177958_n() * 7 + blockpos.func_177956_o() * 9 + blockpos.func_177952_p() * 13) + (float)j + partialTicks;
        this.bannerModel.field_178690_a.field_78795_f = (-0.0125f + 0.01f * MathHelper.func_76134_b((float)(f3 * (float)Math.PI * 0.02f))) * (float)Math.PI;
        GlStateManager.func_179091_B();
        ResourceLocation resourcelocation = this.getBannerResourceLocation(te);
        if (resourcelocation != null) {
            this.func_147499_a(resourcelocation);
            GlStateManager.func_179094_E();
            GlStateManager.func_179152_a((float)0.6666667f, (float)-0.6666667f, (float)-0.6666667f);
            this.bannerModel.func_178687_a();
            GlStateManager.func_179121_F();
        }
        GlStateManager.func_179131_c((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
        GlStateManager.func_179121_F();
    }
}

