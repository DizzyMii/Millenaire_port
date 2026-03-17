/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.item.IItemPropertyGetter
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.item.ItemMill;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

public class ItemAmuletVishnu
extends ItemMill {
    private static final int radius = 20;

    public ItemAmuletVishnu(String itemName) {
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
                    double level = 0.0;
                    double closestDistance = Double.MAX_VALUE;
                    if (world != null && entityIn != null) {
                        Point p = new Point((Entity)entityIn);
                        List<Entity> entities = WorldUtilities.getEntitiesWithinAABB(world, EntityMob.class, p, 20, 20);
                        for (Entity ent : entities) {
                            if (!(p.distanceTo(ent) < closestDistance)) continue;
                            closestDistance = p.distanceTo(ent);
                        }
                    }
                    level = closestDistance > 20.0 ? 0.0 : (20.0 - closestDistance) / 20.0;
                    this.savedScore = (float)(level * 15.0);
                    this.lastUpdateTick = world.func_82737_E();
                    return this.savedScore;
                }
                return this.savedScore;
            }
        });
    }
}

