/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.util.math.MathHelper
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.SlotItemHandler
 */
package org.millenaire.common.ui.firepit;

import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotFirePitOutput
extends SlotItemHandler {
    private final EntityPlayer player;
    private int removeCount;

    public SlotFirePitOutput(EntityPlayer player, IItemHandler handler, int slotIndex, int xPosition, int yPosition) {
        super(handler, slotIndex, xPosition, yPosition);
        this.player = player;
    }

    @Nonnull
    public ItemStack func_75209_a(int amount) {
        if (this.func_75216_d()) {
            this.removeCount += Math.min(amount, this.func_75211_c().func_190916_E());
        }
        return super.func_75209_a(amount);
    }

    public boolean func_75214_a(@Nonnull ItemStack stack) {
        return false;
    }

    protected void func_75208_c(ItemStack stack) {
        stack.func_77980_a(this.player.field_70170_p, this.player, this.removeCount);
        if (!this.player.field_70170_p.field_72995_K) {
            int i = this.removeCount;
            float f = FurnaceRecipes.func_77602_a().func_151398_b(stack);
            if (f == 0.0f) {
                i = 0;
            } else if (f < 1.0f) {
                int j = MathHelper.func_76141_d((float)((float)i * f));
                if (j < MathHelper.func_76123_f((float)((float)i * f)) && Math.random() < (double)((float)i * f - (float)j)) {
                    ++j;
                }
                i = j;
            }
            while (i > 0) {
                int k = EntityXPOrb.func_70527_a((int)i);
                i -= k;
                this.player.field_70170_p.func_72838_d((Entity)new EntityXPOrb(this.player.field_70170_p, this.player.field_70165_t, this.player.field_70163_u + 0.5, this.player.field_70161_v + 0.5, k));
            }
        }
        this.removeCount = 0;
        FMLCommonHandler.instance().firePlayerSmeltedEvent(this.player, stack);
    }

    protected void func_75210_a(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.func_75208_c(stack);
    }

    @Nonnull
    public ItemStack func_190901_a(EntityPlayer thePlayer, @Nonnull ItemStack stack) {
        this.func_75208_c(stack);
        super.func_190901_a(thePlayer, stack);
        return stack;
    }
}

