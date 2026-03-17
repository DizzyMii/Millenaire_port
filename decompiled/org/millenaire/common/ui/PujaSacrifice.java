/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Enchantments
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemBow
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemSpade
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.text.ITextComponent
 */
package org.millenaire.common.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

public class PujaSacrifice
implements IInventory {
    public static final int TOOL = 1;
    public static final int ARMOUR = 2;
    public static final int HELMET = 3;
    public static final int BOOTS = 4;
    public static final int SWORD_AXE = 5;
    public static final int SWORD = 6;
    public static final int BOW = 7;
    public static final int UNBREAKABLE = 8;
    public static PrayerTarget[] PUJA_TARGETS = new PrayerTarget[]{new PrayerTarget(Enchantments.field_185305_q, "pujas.god0", 0, 188, 46, 188, 1), new PrayerTarget(Enchantments.field_185307_s, "pujas.god1", 0, 205, 46, 205, 1), new PrayerTarget(Enchantments.field_185308_t, "pujas.god2", 0, 222, 46, 222, 1), new PrayerTarget(Enchantments.field_185306_r, "pujas.god3", 0, 239, 46, 239, 1)};
    public static PrayerTarget[] MAYAN_TARGETS = new PrayerTarget[]{new PrayerTarget(Enchantments.field_180310_c, "mayan.god0", 0, 188, 120, 188, 2), new PrayerTarget(Enchantments.field_77329_d, "mayan.god1", 20, 188, 140, 188, 2), new PrayerTarget(Enchantments.field_185297_d, "mayan.god2", 40, 188, 160, 188, 2), new PrayerTarget(Enchantments.field_180308_g, "mayan.god3", 60, 188, 180, 188, 2), new PrayerTarget(Enchantments.field_92091_k, "mayan.god4", 80, 188, 200, 188, 2), new PrayerTarget(Enchantments.field_185298_f, "mayan.god5", 100, 188, 120, 188, 3), new PrayerTarget(Enchantments.field_185299_g, "mayan.god6", 0, 208, 120, 208, 3), new PrayerTarget(Enchantments.field_180309_e, "mayan.god7", 20, 208, 140, 208, 4), new PrayerTarget(Enchantments.field_185302_k, "mayan.god8", 40, 208, 160, 208, 5), new PrayerTarget(Enchantments.field_185303_l, "mayan.god9", 0, 188, 120, 188, 5), new PrayerTarget(Enchantments.field_180312_n, "mayan.god10", 80, 188, 200, 188, 5), new PrayerTarget(Enchantments.field_180313_o, "mayan.god11", 60, 208, 180, 208, 6), new PrayerTarget(Enchantments.field_77334_n, "mayan.god12", 20, 188, 140, 188, 6), new PrayerTarget(Enchantments.field_185304_p, "mayan.god13", 80, 208, 200, 208, 6), new PrayerTarget(Enchantments.field_185309_u, "mayan.god14", 40, 208, 160, 208, 7), new PrayerTarget(Enchantments.field_185310_v, "mayan.god15", 60, 208, 180, 208, 7), new PrayerTarget(Enchantments.field_185311_w, "mayan.god16", 20, 188, 140, 188, 7), new PrayerTarget(Enchantments.field_185312_x, "mayan.god17", 80, 208, 200, 208, 7), new PrayerTarget(Enchantments.field_185307_s, "mayan.god18", 100, 208, 220, 208, 8)};
    public static int PUJA_DURATION = 30;
    public static final short PUJA = 0;
    public static final short MAYAN = 1;
    private ItemStack[] items;
    public PrayerTarget currentTarget = null;
    public int offeringProgress = 0;
    public int offeringNeeded = 1;
    public short pujaProgress = 0;
    public Building temple = null;
    public MillVillager priest = null;
    public short type = 0;

    public static boolean validForItem(int type, Item item) {
        if (type == 1) {
            return item instanceof ItemSpade || item instanceof ItemAxe || item instanceof ItemPickaxe;
        }
        if (type == 2) {
            return item instanceof ItemArmor;
        }
        if (type == 3) {
            return item instanceof ItemArmor && ((ItemArmor)item).field_77881_a == EntityEquipmentSlot.HEAD;
        }
        if (type == 4) {
            return item instanceof ItemArmor && ((ItemArmor)item).field_77881_a == EntityEquipmentSlot.FEET;
        }
        if (type == 5) {
            return item instanceof ItemSword || item instanceof ItemAxe;
        }
        if (type == 6) {
            return item instanceof ItemSword;
        }
        if (type == 7) {
            return item instanceof ItemBow;
        }
        if (type == 8) {
            return item instanceof ItemSword || item instanceof ItemArmor || item instanceof ItemBow;
        }
        return false;
    }

    public PujaSacrifice(Building temple, NBTTagCompound tag) {
        this.temple = temple;
        if (temple.containsTags("sacrifices")) {
            this.type = 1;
        }
        this.readFromNBT(tag);
    }

    public PujaSacrifice(Building temple, short type) {
        this.temple = temple;
        this.items = new ItemStack[this.func_70302_i_()];
        for (int i = 0; i < this.items.length; ++i) {
            this.items[i] = ItemStack.field_190927_a;
        }
        this.type = type;
    }

    public void calculateOfferingsNeeded() {
        int currentLevel;
        this.offeringNeeded = 0;
        if (this.items[4] == ItemStack.field_190927_a || this.currentTarget == null) {
            return;
        }
        ItemStack tool = this.items[4];
        if (EnchantmentHelper.func_77506_a((Enchantment)this.currentTarget.enchantment, (ItemStack)tool) >= this.currentTarget.enchantment.func_77325_b()) {
            return;
        }
        if (!this.currentTarget.enchantment.func_92089_a(tool)) {
            return;
        }
        int nbother = 0;
        if (tool.func_77948_v()) {
            NBTTagList nbttaglist = tool.func_77986_q();
            nbother = nbttaglist.func_74745_c();
            Map existingEnchantments = EnchantmentHelper.func_82781_a((ItemStack)tool);
            for (Enchantment enchId : existingEnchantments.keySet()) {
                if (enchId == this.currentTarget.enchantment || enchId.func_191560_c(this.currentTarget.enchantment)) continue;
                return;
            }
        }
        if ((currentLevel = EnchantmentHelper.func_77506_a((Enchantment)this.currentTarget.enchantment, (ItemStack)tool)) > 0) {
            --nbother;
        }
        int cost = 50 + this.currentTarget.enchantment.func_77321_a(currentLevel + 1) * 10;
        cost *= nbother / 2 + 1;
        if (MillConfigValues.LogPujas >= 2) {
            MillLog.minor(this, "Offering needed: " + cost);
        }
        this.offeringNeeded = cost;
    }

    public boolean canPray() {
        if (this.offeringNeeded <= this.offeringProgress) {
            return false;
        }
        return this.items[0] != ItemStack.field_190927_a;
    }

    public void changeEnchantment(int i) {
        if (this.currentTarget == this.getTargets().get(i)) {
            return;
        }
        this.currentTarget = this.getTargets().get(i);
        this.offeringProgress = 0;
        this.calculateOfferingsNeeded();
    }

    public void func_174888_l() {
    }

    public void func_174886_c(EntityPlayer player) {
    }

    private void completeOffering() {
        int currentlevel = EnchantmentHelper.func_77506_a((Enchantment)this.currentTarget.enchantment, (ItemStack)this.items[4]);
        if (currentlevel == 0) {
            this.items[4].func_77966_a(this.currentTarget.enchantment, 1);
        } else {
            NBTTagList enchList = this.items[4].func_77986_q();
            for (int i = 0; i < enchList.func_74745_c(); ++i) {
                Enchantment e = Enchantment.func_185262_c((int)enchList.func_150305_b(i).func_74765_d("id"));
                if (e != this.currentTarget.enchantment) continue;
                enchList.func_150305_b(i).func_74777_a("lvl", (short)(currentlevel + 1));
            }
        }
        this.offeringProgress = 0;
        this.calculateOfferingsNeeded();
        this.temple.getTownHall().requestSave("Puja/sacrifice offering complete");
    }

    public ItemStack func_70298_a(int slot, int nb) {
        if (this.items[slot] != ItemStack.field_190927_a) {
            if (this.items[slot].func_190916_E() <= nb) {
                ItemStack itemstack = this.items[slot];
                this.items[slot] = ItemStack.field_190927_a;
                return itemstack;
            }
            ItemStack itemstack1 = this.items[slot].func_77979_a(nb);
            if (this.items[slot].func_190916_E() == 0) {
                this.items[slot] = ItemStack.field_190927_a;
            }
            return itemstack1;
        }
        return ItemStack.field_190927_a;
    }

    private void endPuja() {
        ItemStack offer = this.items[0];
        if (offer == ItemStack.field_190927_a) {
            return;
        }
        int offerValue = this.getOfferingValue(offer);
        this.offeringProgress += offerValue;
        offer.func_190920_e(offer.func_190916_E() - 1);
        if (offer.func_190916_E() == 0) {
            this.items[0] = ItemStack.field_190927_a;
        }
        if (this.offeringProgress >= this.offeringNeeded) {
            this.completeOffering();
        }
    }

    public ITextComponent func_145748_c_() {
        return null;
    }

    public int func_174887_a_(int id) {
        return 0;
    }

    public int func_174890_g() {
        return 0;
    }

    public int func_70297_j_() {
        return 64;
    }

    public String func_70005_c_() {
        return LanguageUtilities.string("pujas.invanme");
    }

    public int getOfferingProgressScaled(int scale) {
        if (this.offeringNeeded == 0) {
            return 0;
        }
        return this.offeringProgress * scale / this.offeringNeeded;
    }

    public int getOfferingValue(ItemStack is) {
        if (this.type == 0) {
            return this.getOfferingValuePuja(is);
        }
        if (this.type == 1) {
            return this.getOfferingValueMayan(is);
        }
        return 0;
    }

    public int getOfferingValueMayan(ItemStack is) {
        if (is.func_77973_b() == Items.field_151144_bL) {
            return 4096;
        }
        if (is.func_77973_b() == Items.field_151073_bk) {
            return 384;
        }
        if (is.func_77973_b() == Items.field_151072_bj) {
            return 64;
        }
        if (is.func_77973_b() == MillItems.CACAUHAA) {
            return 64;
        }
        if (is.func_77973_b() == Items.field_151076_bf) {
            return 1;
        }
        if (is.func_77973_b() == Items.field_151082_bd) {
            return 1;
        }
        if (is.func_77973_b() == Items.field_151147_al) {
            return 1;
        }
        if (is.func_77973_b() == Items.field_151115_aP) {
            return 1;
        }
        if (is.func_77973_b() == Items.field_151116_aA) {
            return 1;
        }
        if (is.func_77973_b() == Items.field_151100_aR && is.func_77952_i() == 0) {
            return 1;
        }
        if (is.func_77973_b() == Items.field_151123_aH) {
            return 1;
        }
        if (is.func_77973_b() == Items.field_151078_bh) {
            return 2;
        }
        if (is.func_77973_b() == Items.field_151103_aS) {
            return 2;
        }
        if (is.func_77973_b() == Items.field_151064_bs) {
            return 4;
        }
        if (is.func_77973_b() == Items.field_151016_H) {
            return 4;
        }
        if (is.func_77973_b() == Items.field_151070_bp) {
            return 4;
        }
        if (is.func_77973_b() == Items.field_151079_bi) {
            return 6;
        }
        return 0;
    }

    public int getOfferingValuePuja(ItemStack is) {
        if (is.func_77973_b() == Items.field_151045_i) {
            return 384;
        }
        if (is.func_77973_b() == Items.field_151117_aB) {
            return 128;
        }
        if (is.func_77973_b() == Items.field_151153_ao) {
            return 96;
        }
        if (is.func_77973_b() == Items.field_151043_k) {
            return 64;
        }
        if (is.func_77973_b() == MillItems.RICE) {
            return 8;
        }
        if (is.func_77973_b() == MillItems.RASGULLA) {
            return 64;
        }
        if (is.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150328_O) || is.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150327_N)) {
            return 16;
        }
        if (is.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150329_H) || is.func_77973_b() == Items.field_151034_e) {
            return 8;
        }
        if (is.func_77973_b() == Item.func_150898_a((Block)Blocks.field_150325_L) && is.func_77952_i() == 0) {
            return 8;
        }
        if (is.func_77973_b() == Items.field_151127_ba) {
            return 4;
        }
        return 0;
    }

    public int getPujaProgressScaled(int scale) {
        return this.pujaProgress * scale / PUJA_DURATION;
    }

    public int func_70302_i_() {
        return 5;
    }

    public ItemStack func_70301_a(int par1) {
        return this.items[par1];
    }

    public List<PrayerTarget> getTargets() {
        if (this.items[4] == ItemStack.field_190927_a) {
            return new ArrayList<PrayerTarget>();
        }
        if (this.type == 0) {
            ArrayList<PrayerTarget> targets = new ArrayList<PrayerTarget>();
            for (PrayerTarget t : PUJA_TARGETS) {
                if (!t.validForItem(this.items[4].func_77973_b())) continue;
                targets.add(t);
            }
            return targets;
        }
        if (this.type == 1) {
            ArrayList<PrayerTarget> targets = new ArrayList<PrayerTarget>();
            for (PrayerTarget t : MAYAN_TARGETS) {
                if (!t.validForItem(this.items[4].func_77973_b())) continue;
                targets.add(t);
            }
            return targets;
        }
        return new ArrayList<PrayerTarget>();
    }

    public boolean func_145818_k_() {
        return false;
    }

    public boolean isActive() {
        return false;
    }

    public boolean func_191420_l() {
        return false;
    }

    public boolean func_94041_b(int i, ItemStack itemstack) {
        return true;
    }

    public boolean func_70300_a(EntityPlayer player) {
        return false;
    }

    public void func_70296_d() {
    }

    public void func_174889_b(EntityPlayer player) {
    }

    public boolean performPuja(MillVillager priest) {
        this.priest = priest;
        if (this.pujaProgress == 0) {
            boolean success = this.startPuja();
            if (success) {
                this.pujaProgress = 1;
            }
            return success;
        }
        if (this.pujaProgress >= PUJA_DURATION) {
            this.endPuja();
            this.pujaProgress = 0;
            return this.canPray();
        }
        this.pujaProgress = (short)(this.pujaProgress + 1);
        return this.canPray();
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        NBTTagList nbttaglist = par1NBTTagCompound.func_150295_c("Items", 10);
        this.items = new ItemStack[this.func_70302_i_()];
        for (int i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.func_150305_b(i);
            byte byte0 = nbttagcompound.func_74771_c("Slot");
            if (byte0 < 0 || byte0 >= this.items.length) continue;
            this.items[byte0] = new ItemStack(nbttagcompound);
        }
        Enchantment ench = Enchantment.func_185262_c((int)par1NBTTagCompound.func_74765_d("enchantmentTarget"));
        if (ench != null) {
            for (PrayerTarget t : this.getTargets()) {
                if (t.enchantment != ench) continue;
                this.currentTarget = t;
            }
        }
        if (MillConfigValues.LogPujas >= 2) {
            MillLog.minor(this, "Reading enchantmentTarget: " + ench + ", " + this.currentTarget);
        }
        this.offeringProgress = par1NBTTagCompound.func_74765_d("offeringProgress");
        this.pujaProgress = par1NBTTagCompound.func_74765_d("pujaProgress");
        this.calculateOfferingsNeeded();
    }

    public ItemStack func_70304_b(int par1) {
        if (this.items[par1] != ItemStack.field_190927_a) {
            ItemStack itemstack = this.items[par1];
            this.items[par1] = ItemStack.field_190927_a;
            this.func_70296_d();
            return itemstack;
        }
        return ItemStack.field_190927_a;
    }

    public void func_174885_b(int id, int value) {
    }

    public void func_70299_a(int par1, ItemStack par2ItemStack) {
        this.items[par1] = par2ItemStack;
        if (par2ItemStack != ItemStack.field_190927_a && par2ItemStack.func_190916_E() > this.func_70297_j_()) {
            par2ItemStack.func_190920_e(this.func_70297_j_());
        }
        this.func_70296_d();
    }

    private boolean startPuja() {
        int money = MillCommonUtilities.countMoney(this);
        if (money == 0) {
            return false;
        }
        if (this.offeringNeeded == 0 || this.offeringProgress >= this.offeringNeeded) {
            return false;
        }
        if (this.items[0] == ItemStack.field_190927_a) {
            return false;
        }
        int denier = (money -= 8) % 64;
        int denier_argent = (money - denier) / 64 % 64;
        int denier_or = (money - denier - denier_argent * 64) / 4096;
        this.items[1] = denier == 0 ? ItemStack.field_190927_a : new ItemStack((Item)MillItems.DENIER, denier);
        this.items[2] = denier_argent == 0 ? ItemStack.field_190927_a : new ItemStack((Item)MillItems.DENIER_ARGENT, denier_argent);
        this.items[3] = denier_or == 0 ? ItemStack.field_190927_a : new ItemStack((Item)MillItems.DENIER_OR, denier_or);
        return true;
    }

    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        if (this.currentTarget != null) {
            par1NBTTagCompound.func_74777_a("enchantmentTarget", (short)Enchantment.func_185258_b((Enchantment)this.currentTarget.enchantment));
            if (MillConfigValues.LogPujas >= 2) {
                MillLog.minor(this, "Writing enchantmentTarget: " + this.currentTarget.enchantment + ", " + this.currentTarget);
            }
        }
        par1NBTTagCompound.func_74777_a("offeringProgress", (short)this.offeringProgress);
        par1NBTTagCompound.func_74777_a("pujaProgress", this.pujaProgress);
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.items.length; ++i) {
            if (this.items[i] == null) continue;
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.func_74774_a("Slot", (byte)i);
            this.items[i].func_77955_b(nbttagcompound);
            nbttaglist.func_74742_a((NBTBase)nbttagcompound);
        }
        par1NBTTagCompound.func_74782_a("Items", (NBTBase)nbttaglist);
    }

    public static class PrayerTarget {
        public final Enchantment enchantment;
        public final String mouseOver;
        public final int startX;
        public final int startY;
        public final int startXact;
        public final int startYact;
        public final int toolType;

        public PrayerTarget(Enchantment enchantment, String mouseOver, int startX, int startY, int startXact, int startYact, int toolType) {
            this.enchantment = enchantment;
            this.mouseOver = mouseOver;
            this.startX = startX;
            this.startY = startY;
            this.startXact = startXact;
            this.startYact = startYact;
            this.toolType = toolType;
        }

        public boolean validForItem(Item item) {
            return PujaSacrifice.validForItem(this.toolType, item);
        }
    }
}

