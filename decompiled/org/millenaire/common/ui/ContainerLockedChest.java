/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.ui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.millenaire.common.village.Building;

public class ContainerLockedChest
extends Container {
    private final IInventory lowerChestInventory;
    private final int numRows;
    private final boolean locked;

    public ContainerLockedChest(IInventory playerInventory, IInventory chestInventory, EntityPlayer player, Building building, boolean locked) {
        this.locked = locked;
        this.lowerChestInventory = chestInventory;
        this.numRows = chestInventory.func_70302_i_() / 9;
        chestInventory.func_174889_b(player);
        int i = (this.numRows - 4) * 18;
        for (int j = 0; j < this.numRows; ++j) {
            for (int k = 0; k < 9; ++k) {
                if (locked) {
                    this.func_75146_a(new LockedSlot(chestInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
                    continue;
                }
                this.func_75146_a(new CachedSlot(chestInventory, k + j * 9, 8 + k * 18, 18 + j * 18, building));
            }
        }
        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.func_75146_a(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }
        for (int i1 = 0; i1 < 9; ++i1) {
            this.func_75146_a(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
        }
    }

    public boolean func_75145_c(EntityPlayer playerIn) {
        return this.lowerChestInventory.func_70300_a(playerIn);
    }

    public IInventory getLowerChestInventory() {
        return this.lowerChestInventory;
    }

    public void func_75134_a(EntityPlayer playerIn) {
        super.func_75134_a(playerIn);
        this.lowerChestInventory.func_174886_c(playerIn);
    }

    public ItemStack func_184996_a(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        Slot slot;
        if (slotId >= 0 && slotId < this.field_75151_b.size() && (slot = (Slot)this.field_75151_b.get(slotId)) != null && slot instanceof LockedSlot && this.locked) {
            return ItemStack.field_190927_a;
        }
        return super.func_184996_a(slotId, dragType, clickTypeIn, player);
    }

    public ItemStack func_82846_b(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.field_190927_a;
        Slot slot = (Slot)this.field_75151_b.get(index);
        if (slot != null && slot.func_75216_d()) {
            ItemStack itemstack1 = slot.func_75211_c();
            itemstack = itemstack1.func_77946_l();
            if (index < this.numRows * 9 ? !this.func_75135_a(itemstack1, this.numRows * 9, this.field_75151_b.size(), true) : !this.func_75135_a(itemstack1, 0, this.numRows * 9, false)) {
                return ItemStack.field_190927_a;
            }
            if (itemstack1.func_190926_b()) {
                slot.func_75215_d(ItemStack.field_190927_a);
            } else {
                slot.func_75218_e();
            }
        }
        return itemstack;
    }

    public static class LockedSlot
    extends Slot {
        public LockedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        public boolean func_82869_a(EntityPlayer playerIn) {
            return false;
        }
    }

    public static class CachedSlot
    extends Slot {
        final Building building;

        public CachedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition, Building building) {
            super(inventoryIn, index, xPosition, yPosition);
            this.building = building;
        }

        public void func_75218_e() {
            super.func_75218_e();
            if (this.building != null) {
                this.building.invalidateInventoryCache();
            }
        }
    }
}

