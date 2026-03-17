/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.inventory.SlotFurnaceFuel
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraftforge.items.IItemHandler
 *  net.minecraftforge.items.SlotItemHandler
 */
package org.millenaire.common.ui.firepit;

import javax.annotation.Nonnull;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotFirePitFuel
extends SlotItemHandler {
    public SlotFirePitFuel(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public int func_178170_b(@Nonnull ItemStack stack) {
        return SlotFurnaceFuel.func_178173_c_((ItemStack)stack) ? 1 : super.func_178170_b(stack);
    }

    public boolean func_75214_a(@Nonnull ItemStack stack) {
        return TileEntityFurnace.func_145954_b((ItemStack)stack) || SlotFurnaceFuel.func_178173_c_((ItemStack)stack);
    }
}

