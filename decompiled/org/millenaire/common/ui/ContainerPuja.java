/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemSpade
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 */
package org.millenaire.common.ui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.ui.PujaSacrifice;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

public class ContainerPuja
extends Container {
    PujaSacrifice shrine;
    ToolSlot slotTool;

    public ContainerPuja(EntityPlayer player, Building temple) {
        try {
            this.shrine = temple.pujas;
            this.slotTool = new ToolSlot(temple.pujas, 4, 86, 37);
            this.func_75146_a(new OfferingSlot(temple.pujas, 0, 26, 19));
            this.func_75146_a(new MoneySlot(temple.pujas, 1, 8, 55));
            this.func_75146_a(new MoneySlot(temple.pujas, 2, 26, 55));
            this.func_75146_a(new MoneySlot(temple.pujas, 3, 44, 55));
            this.func_75146_a(this.slotTool);
            for (int i = 0; i < 3; ++i) {
                for (int k = 0; k < 9; ++k) {
                    this.func_75146_a(new Slot((IInventory)player.field_71071_by, k + i * 9 + 9, 8 + k * 18, 106 + i * 18));
                }
            }
            for (int j = 0; j < 9; ++j) {
                this.func_75146_a(new Slot((IInventory)player.field_71071_by, j, 8 + j * 18, 164));
            }
        }
        catch (Exception e) {
            MillLog.printException("Exception in ContainerPuja(): ", e);
        }
    }

    public boolean func_75145_c(EntityPlayer entityplayer) {
        return true;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public ItemStack func_82846_b(EntityPlayer par1EntityPlayer, int stackID) {
        ItemStack itemstack = ItemStack.field_190927_a;
        Slot slot = (Slot)this.field_75151_b.get(stackID);
        if (slot == null || !slot.func_75216_d()) return itemstack;
        ItemStack itemstack1 = slot.func_75211_c();
        itemstack = itemstack1.func_77946_l();
        if (stackID == 4) {
            if (!this.func_75135_a(itemstack1, 5, 41, true)) {
                return ItemStack.field_190927_a;
            }
            slot.func_75220_a(itemstack1, itemstack);
        } else if (stackID > 4) {
            if (itemstack1.func_77973_b() == MillItems.DENIER || itemstack1.func_77973_b() == MillItems.DENIER_ARGENT || itemstack1.func_77973_b() == MillItems.DENIER_OR) {
                if (!this.func_75135_a(itemstack1, 1, 4, false)) {
                    return ItemStack.field_190927_a;
                }
            } else if (this.shrine.getOfferingValue(itemstack1) > 0) {
                if (!this.func_75135_a(itemstack1, 0, 1, false)) {
                    return ItemStack.field_190927_a;
                }
            } else {
                if (!this.slotTool.func_75214_a(itemstack1)) return ItemStack.field_190927_a;
                if (!this.func_75135_a(itemstack1, 4, 5, false)) {
                    return ItemStack.field_190927_a;
                }
            }
        } else if (!this.func_75135_a(itemstack1, 5, 41, false)) {
            return ItemStack.field_190927_a;
        }
        if (itemstack1.func_190916_E() == 0) {
            slot.func_75215_d(ItemStack.field_190927_a);
        } else {
            slot.func_75218_e();
        }
        if (itemstack1.func_190916_E() == itemstack.func_190916_E()) {
            return ItemStack.field_190927_a;
        }
        slot.func_190901_a(par1EntityPlayer, itemstack1);
        return itemstack;
    }

    public static class ToolSlot
    extends Slot {
        PujaSacrifice shrine;

        public ToolSlot(PujaSacrifice shrine, int par2, int par3, int par4) {
            super((IInventory)shrine, par2, par3, par4);
            this.shrine = shrine;
        }

        public boolean func_75214_a(ItemStack is) {
            Item item = is.func_77973_b();
            if (this.shrine.type == 1) {
                return item instanceof ItemSword || item instanceof ItemArmor || item instanceof ItemBow || item instanceof ItemAxe;
            }
            return item instanceof ItemSpade || item instanceof ItemAxe || item instanceof ItemPickaxe;
        }

        public void func_75218_e() {
            this.shrine.calculateOfferingsNeeded();
            if (!this.shrine.temple.world.field_72995_K) {
                this.shrine.temple.getTownHall().requestSave("Puja tool slot changed");
            }
            super.func_75218_e();
        }
    }

    public static class OfferingSlot
    extends Slot {
        PujaSacrifice shrine;

        public OfferingSlot(PujaSacrifice shrine, int par2, int par3, int par4) {
            super((IInventory)shrine, par2, par3, par4);
            this.shrine = shrine;
        }

        public boolean func_75214_a(ItemStack par1ItemStack) {
            return this.shrine.getOfferingValue(par1ItemStack) > 0;
        }

        public void func_75218_e() {
            if (!this.shrine.temple.world.field_72995_K) {
                this.shrine.temple.getTownHall().requestSave("Puja offering slot changed");
            }
            super.func_75218_e();
        }
    }

    public static class MoneySlot
    extends Slot {
        PujaSacrifice shrine;

        public MoneySlot(PujaSacrifice shrine, int par2, int par3, int par4) {
            super((IInventory)shrine, par2, par3, par4);
            this.shrine = shrine;
        }

        public boolean func_75214_a(ItemStack is) {
            return is.func_77973_b() == MillItems.DENIER || is.func_77973_b() == MillItems.DENIER_OR || is.func_77973_b() == MillItems.DENIER_ARGENT;
        }

        public void func_75218_e() {
            if (!this.shrine.temple.world.field_72995_K) {
                this.shrine.temple.getTownHall().requestSave("Puja money slot changed");
            }
            super.func_75218_e();
        }
    }
}

