/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockBanner$BlockBannerHanging
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 */
package org.millenaire.common.block.mock;

import net.minecraft.block.BlockBanner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.millenaire.common.entity.TileEntityMockBanner;

public class MockBlockBannerHanging
extends BlockBanner.BlockBannerHanging {
    public final int bannerType;

    public MockBlockBannerHanging(int bannerType) {
        this.bannerType = bannerType;
    }

    public TileEntity func_149915_a(World worldIn, int meta) {
        return new TileEntityMockBanner();
    }
}

