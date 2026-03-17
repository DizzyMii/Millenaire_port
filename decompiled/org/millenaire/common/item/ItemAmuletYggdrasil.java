/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.IItemPropertyGetter
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.item.ItemMill;
import org.millenaire.common.utilities.Point;

public class ItemAmuletYggdrasil
extends ItemMill {
    public ItemAmuletYggdrasil(String itemName) {
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
                    int level = 0;
                    Point p = new Point((Entity)entityIn);
                    level = (int)Math.floor(p.getiY());
                    if (level > 127) {
                        level = 127;
                    }
                    this.savedScore = level / 8;
                    return this.savedScore;
                }
                return this.savedScore;
            }
        });
    }
}

