/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.IItemPropertyGetter
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.item.ItemMill;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

public class ItemAmuletAlchemist
extends ItemMill {
    private static final int radius = 5;

    public ItemAmuletAlchemist(String itemName) {
        super(itemName);
        this.func_185043_a(new ResourceLocation("score"), new IItemPropertyGetter(){
            @SideOnly(value=Side.CLIENT)
            long lastUpdateTick;
            @SideOnly(value=Side.CLIENT)
            float savedScore;

            @SideOnly(value=Side.CLIENT)
            public float func_185085_a(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entityIn) {
                if (entityIn == null) {
                    return 0.0f;
                }
                world = entityIn.field_70170_p;
                if (world.func_82737_E() != this.lastUpdateTick) {
                    float score = 0.0f;
                    if (world != null && entityIn != null) {
                        Point p = new Point((Entity)entityIn);
                        int startY = Math.max(p.getiY() - 5, 0);
                        int endY = Math.min(p.getiY() + 5, 127);
                        for (int i = p.getiX() - 5; i < p.getiX() + 5; ++i) {
                            for (int j = p.getiZ() - 5; j < p.getiZ() + 5; ++j) {
                                for (int k = startY; k < endY; ++k) {
                                    Block block = WorldUtilities.getBlock(world, i, k, j);
                                    if (block == Blocks.field_150365_q) {
                                        score += 1.0f;
                                        continue;
                                    }
                                    if (block == Blocks.field_150482_ag) {
                                        score += 30.0f;
                                        continue;
                                    }
                                    if (block == Blocks.field_150412_bA) {
                                        score += 30.0f;
                                        continue;
                                    }
                                    if (block == Blocks.field_150352_o) {
                                        score += 10.0f;
                                        continue;
                                    }
                                    if (block == Blocks.field_150366_p) {
                                        score += 5.0f;
                                        continue;
                                    }
                                    if (block == Blocks.field_150369_x) {
                                        score += 10.0f;
                                        continue;
                                    }
                                    if (block == Blocks.field_150450_ax) {
                                        score += 5.0f;
                                        continue;
                                    }
                                    if (block != Blocks.field_150439_ay) continue;
                                    score += 5.0f;
                                }
                            }
                        }
                    }
                    if (score > 100.0f) {
                        score = 100.0f;
                    }
                    this.savedScore = score * 15.0f / 100.0f;
                    this.lastUpdateTick = world.func_82737_E();
                    return this.savedScore;
                }
                return this.savedScore;
            }
        });
    }
}

