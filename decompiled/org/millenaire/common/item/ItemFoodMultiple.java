/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.CriteriaTriggers
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Items
 *  net.minecraft.init.MobEffects
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.stats.StatList
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.block.MillBlocks;

public class ItemFoodMultiple
extends ItemFood {
    public static final int DAMAGE_PER_PORTION = 64;
    private static final ItemStack MILKSTACK = new ItemStack(Items.field_151117_aB, 1);
    private final int healthAmount;
    private final boolean drink;
    private final int regenerationDuration;
    private final int drunkDuration;
    private PotionEffect potionId = null;
    private boolean clearEffects = false;

    public ItemFoodMultiple(String foodName, int healthAmount, int regenerationDuration, int foodAmount, float saturation, boolean drink, int drunkDuration) {
        super(foodAmount, saturation, false);
        this.healthAmount = healthAmount;
        this.drink = drink;
        this.regenerationDuration = regenerationDuration;
        this.drunkDuration = drunkDuration;
        if (healthAmount > 0) {
            this.func_77848_i();
        }
        this.func_77637_a(MillBlocks.tabMillenaire);
        this.func_77655_b("millenaire." + foodName);
        this.setRegistryName(foodName);
        this.func_77625_d(1);
    }

    public int getDrunkDuration() {
        return this.drunkDuration;
    }

    public int getHealthAmount() {
        return this.healthAmount;
    }

    public EnumAction func_77661_b(ItemStack itemstack) {
        if (this.drink) {
            return EnumAction.DRINK;
        }
        return EnumAction.EAT;
    }

    public PotionEffect getPotionId() {
        return this.potionId;
    }

    public int getRegenerationDuration() {
        return this.regenerationDuration;
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)this, (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    public boolean isClearEffects() {
        return this.clearEffects;
    }

    public boolean isDrink() {
        return this.drink;
    }

    public ItemStack func_77654_b(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)entityLiving;
            if (!worldIn.field_72995_K && this.clearEffects) {
                entityLiving.curePotionEffects(MILKSTACK);
            }
            entityplayer.func_71024_bL().func_151686_a((ItemFood)this, stack);
            entityplayer.func_70691_i((float)this.healthAmount);
            worldIn.func_184148_a((EntityPlayer)null, entityplayer.field_70165_t, entityplayer.field_70163_u, entityplayer.field_70161_v, SoundEvents.field_187739_dZ, SoundCategory.PLAYERS, 0.5f, worldIn.field_73012_v.nextFloat() * 0.1f + 0.9f);
            this.func_77849_c(stack, worldIn, entityplayer);
            entityplayer.func_71029_a(StatList.func_188057_b((Item)this));
            if (this.drink) {
                MillAdvancements.CHEERS.grant(entityplayer);
            }
            if (this.regenerationDuration > 0) {
                entityplayer.func_70690_d(new PotionEffect(MobEffects.field_76428_l, this.regenerationDuration * 20, 0));
            }
            if (this.drunkDuration > 0) {
                entityplayer.func_70690_d(new PotionEffect(MobEffects.field_76431_k, this.drunkDuration * 20, 0));
            }
            if (entityplayer instanceof EntityPlayerMP) {
                CriteriaTriggers.field_193138_y.func_193148_a((EntityPlayerMP)entityplayer, stack);
            }
        }
        if (stack.func_77952_i() + 64 < stack.func_77958_k()) {
            stack.func_77964_b(stack.func_77952_i() + 64);
        } else {
            stack.func_190920_e(stack.func_190916_E() - 1);
        }
        return stack;
    }

    public ItemFoodMultiple setClearEffects(boolean clearEffects) {
        this.clearEffects = clearEffects;
        if (clearEffects) {
            this.func_77848_i();
        }
        return this;
    }

    public ItemFood func_185070_a(PotionEffect effect, float probability) {
        super.func_185070_a(effect, probability);
        this.potionId = effect;
        this.func_77848_i();
        return this;
    }
}

