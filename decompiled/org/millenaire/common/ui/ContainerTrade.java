/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ClickType
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.MathHelper
 */
package org.millenaire.common.ui;

import java.util.ArrayList;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.world.UserProfile;

public class ContainerTrade
extends Container {
    public static final int DONATION_REP_MULTIPLIER = 4;
    private Building building;
    private MillVillager merchant;
    public int nbRowSelling = 0;
    public int nbRowBuying = 0;

    /*
     * WARNING - void declaration
     */
    public ContainerTrade(EntityPlayer player, Building building) {
        void var6_12;
        void var6_10;
        this.building = building;
        Set<TradeGood> sellingGoods = building.getSellingGoods(player);
        int slotnb = 0;
        if (sellingGoods != null) {
            for (TradeGood tradeGood : sellingGoods) {
                int slotrow = slotnb / 13;
                this.func_75146_a(new TradeSlot(building, player, true, tradeGood, 8 + 18 * (slotnb - 13 * slotrow), 32 + slotrow * 18));
                ++slotnb;
            }
        }
        this.nbRowSelling = slotnb / 13 + 1;
        Set<TradeGood> buyingGoods = building.getBuyingGoods(player);
        slotnb = 0;
        if (buyingGoods != null) {
            for (TradeGood g : buyingGoods) {
                int slotrow = slotnb / 13;
                this.func_75146_a(new TradeSlot(building, player, false, g, 8 + 18 * (slotnb - 13 * slotrow), 86 + slotrow * 18));
                ++slotnb;
            }
        }
        this.nbRowBuying = slotnb / 13 + 1;
        boolean bl = false;
        while (var6_10 < 3) {
            for (int k1 = 0; k1 < 9; ++k1) {
                this.func_75146_a(new Slot((IInventory)player.field_71071_by, k1 + var6_10 * 9 + 9, 8 + k1 * 18 + 36, 103 + var6_10 * 18 + 37));
            }
            ++var6_10;
        }
        boolean bl2 = false;
        while (var6_12 < 9) {
            this.func_75146_a(new Slot((IInventory)player.field_71071_by, (int)var6_12, 8 + var6_12 * 18 + 36, 198));
            ++var6_12;
        }
        if (!building.world.field_72995_K) {
            UserProfile userProfile = building.mw.getProfile(player);
            this.unlockTradableGoods(userProfile);
        }
    }

    public ContainerTrade(EntityPlayer player, MillVillager merchant) {
        this.merchant = merchant;
        int slotnb = 0;
        Set<TradeGood> sellingGoods = merchant.merchantSells.keySet();
        if (sellingGoods != null) {
            for (TradeGood g : sellingGoods) {
                int slotrow = slotnb / 13;
                this.func_75146_a(new MerchantSlot(merchant, player, g, 8 + 18 * (slotnb - 13 * slotrow), 32 + slotrow * 18));
                ++slotnb;
            }
        }
        this.nbRowSelling = slotnb / 13 + 1;
        for (int l = 0; l < 3; ++l) {
            for (int k1 = 0; k1 < 9; ++k1) {
                this.func_75146_a(new Slot((IInventory)player.field_71071_by, k1 + l * 9 + 9, 8 + k1 * 18 + 36, 103 + l * 18 + 37));
            }
        }
        for (int i1 = 0; i1 < 9; ++i1) {
            this.func_75146_a(new Slot((IInventory)player.field_71071_by, i1, 8 + i1 * 18 + 36, 198));
        }
        if (!merchant.field_70170_p.field_72995_K) {
            UserProfile profile = merchant.mw.getProfile(player);
            this.unlockTradableGoods(profile);
        }
    }

    public boolean func_75145_c(EntityPlayer entityplayer) {
        return true;
    }

    public ItemStack func_184996_a(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        TradeGood good = null;
        if (slotId >= 0 && slotId < this.field_75151_b.size()) {
            Slot slot = (Slot)this.field_75151_b.get(slotId);
            if (slot != null && slot instanceof TradeSlot) {
                TradeSlot tslot = (TradeSlot)slot;
                good = tslot.good;
                UserProfile profile = this.building.mw.getProfile(player);
                int nbItems = 1;
                if (clickTypeIn == ClickType.QUICK_MOVE) {
                    nbItems = 64;
                } else if (clickTypeIn == ClickType.PICKUP && dragType == 1) {
                    nbItems = 8;
                }
                if (tslot.isProblem() == null) {
                    int playerMoney = MillCommonUtilities.countMoney((IInventory)player.field_71071_by);
                    if (tslot.sellingSlot) {
                        EntityPlayer owner;
                        if (playerMoney < good.getCalculatedSellingPrice(this.building, player) * nbItems) {
                            nbItems = MathHelper.func_76141_d((float)(playerMoney / good.getCalculatedSellingPrice(this.building, player)));
                        }
                        if (!good.autoGenerate && this.building.countGoods(good.item.getItem(), good.item.meta) < nbItems) {
                            nbItems = this.building.countGoods(good.item.getItem(), good.item.meta);
                        }
                        nbItems = MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, good.item.getItem(), good.item.meta, nbItems);
                        MillCommonUtilities.changeMoney((IInventory)player.field_71071_by, -good.getCalculatedSellingPrice(this.building, player) * nbItems, player);
                        if (!good.autoGenerate) {
                            this.building.takeGoods(good.item.getItem(), good.item.meta, nbItems);
                        }
                        if (this.building.getTownHall().controlledBy != null && !this.building.getTownHall().controlledBy.equals(player.func_110124_au()) && (owner = this.building.world.func_152378_a(this.building.getTownHall().controlledBy)) != null) {
                            MillAdvancements.MP_NEIGHBOURTRADE.grant(owner);
                        }
                        this.building.adjustReputation(player, good.getCalculatedSellingPrice(this.building, player) * nbItems);
                        this.building.getTownHall().adjustLanguage(player, nbItems);
                    } else {
                        EntityPlayer owner;
                        if (MillCommonUtilities.countChestItems((IInventory)player.field_71071_by, good.item.getItem(), good.item.meta) < nbItems) {
                            nbItems = MillCommonUtilities.countChestItems((IInventory)player.field_71071_by, good.item.getItem(), good.item.meta);
                        }
                        nbItems = this.building.storeGoods(good.item.getItem(), good.item.meta, nbItems);
                        WorldUtilities.getItemsFromChest((IInventory)player.field_71071_by, good.item.getItem(), good.item.meta, nbItems);
                        if (!profile.donationActivated) {
                            MillCommonUtilities.changeMoney((IInventory)player.field_71071_by, good.getCalculatedBuyingPrice(this.building, player) * nbItems, player);
                        }
                        if (this.building.getTownHall().controlledBy != null && !this.building.getTownHall().controlledBy.equals(player.func_110124_au()) && (owner = this.building.world.func_152378_a(this.building.getTownHall().controlledBy)) != null) {
                            MillAdvancements.MP_NEIGHBOURTRADE.grant(owner);
                        }
                        int repMultiplier = 1;
                        if (profile.donationActivated) {
                            repMultiplier = 4;
                        }
                        this.building.adjustReputation(player, good.getCalculatedBuyingPrice(this.building, player) * nbItems * repMultiplier);
                        this.building.getTownHall().adjustLanguage(player, nbItems);
                    }
                }
                this.func_75142_b();
                this.building.invalidateInventoryCache();
                if (!this.building.world.field_72995_K) {
                    this.building.sendChestPackets(player);
                }
                if (!this.building.world.field_72995_K) {
                    this.unlockTradableGoods(profile);
                }
                return slot.func_75211_c();
            }
            if (slot != null && slot instanceof MerchantSlot) {
                MerchantSlot tslot = (MerchantSlot)slot;
                good = tslot.good;
                int nbItems = 1;
                if (dragType == 1) {
                    nbItems = 64;
                }
                if (tslot.isProblem() == null) {
                    int playerMoney = MillCommonUtilities.countMoney((IInventory)player.field_71071_by);
                    if (playerMoney < good.getCalculatedSellingPrice(this.merchant) * nbItems) {
                        nbItems = MathHelper.func_76141_d((float)(playerMoney / good.getCalculatedSellingPrice(this.merchant)));
                    }
                    if (this.merchant.getHouse().countGoods(good.item) < nbItems) {
                        nbItems = this.merchant.getHouse().countGoods(good.item);
                    }
                    nbItems = MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, good.item.getItem(), good.item.meta, nbItems);
                    MillCommonUtilities.changeMoney((IInventory)player.field_71071_by, -good.getCalculatedSellingPrice(this.merchant) * nbItems, player);
                    this.merchant.getHouse().takeGoods(good.item, nbItems);
                    Mill.getMillWorld(player.field_70170_p).getProfile(player).adjustLanguage(this.merchant.getCulture().key, nbItems);
                }
                this.func_75142_b();
                this.merchant.getHouse().invalidateInventoryCache();
                if (!this.merchant.getHouse().world.field_72995_K) {
                    this.merchant.getHouse().sendChestPackets(player);
                }
                if (!this.merchant.field_70170_p.field_72995_K) {
                    UserProfile profile = this.merchant.mw.getProfile(player);
                    this.unlockTradableGoods(profile);
                }
                return slot.func_75211_c();
            }
        }
        return null;
    }

    private void unlockTradableGoods(UserProfile profile) {
        ArrayList<TradeGood> unlockedGoods = new ArrayList<TradeGood>();
        for (Slot slot : this.field_75151_b) {
            MerchantSlot merchantSlot;
            if (slot instanceof TradeSlot) {
                TradeSlot tradeSlot = (TradeSlot)slot;
                if (tradeSlot.isProblem() != null) continue;
                unlockedGoods.add(tradeSlot.good);
                continue;
            }
            if (!(slot instanceof MerchantSlot) || (merchantSlot = (MerchantSlot)slot).isProblem() != null) continue;
            unlockedGoods.add(merchantSlot.good);
        }
        if (!unlockedGoods.isEmpty()) {
            if (this.building != null) {
                profile.unlockTradeGoods(this.building.culture, unlockedGoods);
            } else if (this.merchant != null) {
                profile.unlockTradeGoods(this.merchant.getCulture(), unlockedGoods);
            }
        }
    }

    public static class TradeSlot
    extends Slot {
        public final Building building;
        public final EntityPlayer player;
        public final TradeGood good;
        public final boolean sellingSlot;

        public TradeSlot(Building building, EntityPlayer player, boolean sellingSlot, TradeGood good, int xpos, int ypos) {
            super((IInventory)player.field_71071_by, -1, xpos, ypos);
            this.building = building;
            if (good.item.item == Items.field_190931_a) {
                MillLog.error(good, "Trying to add air to the trade UI.");
            }
            this.good = good;
            this.player = player;
            this.sellingSlot = sellingSlot;
        }

        public ItemStack func_75209_a(int i) {
            return null;
        }

        public boolean func_75216_d() {
            return this.func_75211_c() != null;
        }

        public int func_75219_a() {
            return 0;
        }

        public ItemStack func_75211_c() {
            if (this.sellingSlot) {
                return new ItemStack(this.good.item.getItem(), Math.max(Math.min(this.building.countGoods(this.good.item.getItem(), this.good.item.meta), 99), 1), this.good.item.meta);
            }
            return new ItemStack(this.good.item.getItem(), Math.max(Math.min(MillCommonUtilities.countChestItems((IInventory)this.player.field_71071_by, this.good.item.getItem(), this.good.item.meta), 99), 1), this.good.item.meta);
        }

        public boolean func_75214_a(ItemStack itemstack) {
            return true;
        }

        public String isProblem() {
            if (this.sellingSlot) {
                if (this.building.countGoods(this.good.item.getItem(), this.good.item.meta) < 1 && this.good.requiredTag != null && !this.building.containsTags(this.good.requiredTag)) {
                    return LanguageUtilities.string("ui.missingequipment") + ": " + this.good.requiredTag;
                }
                if (this.building.countGoods(this.good.item.getItem(), this.good.item.meta) < 1 && !this.good.autoGenerate) {
                    return LanguageUtilities.string("ui.outofstock");
                }
                if (this.building.getTownHall().getReputation(this.player) < this.good.minReputation) {
                    return LanguageUtilities.string("ui.reputationneeded", this.building.culture.getReputationLevelLabel(this.good.minReputation));
                }
                int playerMoney = MillCommonUtilities.countMoney((IInventory)this.player.field_71071_by);
                if (playerMoney < this.good.getCalculatedSellingPrice(this.building, this.player)) {
                    return LanguageUtilities.string("ui.missingdeniers").replace("<0>", "" + (this.good.getCalculatedSellingPrice(this.building, this.player) - playerMoney));
                }
            } else if (MillCommonUtilities.countChestItems((IInventory)this.player.field_71071_by, this.good.item.getItem(), this.good.item.meta) == 0) {
                return LanguageUtilities.string("ui.noneininventory");
            }
            return null;
        }

        public void func_75218_e() {
        }

        public void func_75215_d(ItemStack itemstack) {
        }

        public String toString() {
            return this.good.name + (this.sellingSlot ? LanguageUtilities.string("ui.selling") : LanguageUtilities.string("ui.buying"));
        }
    }

    public static class MerchantSlot
    extends Slot {
        public MillVillager merchant;
        public EntityPlayer player;
        public final TradeGood good;

        public MerchantSlot(MillVillager merchant, EntityPlayer player, TradeGood good, int xpos, int ypos) {
            super(null, -1, xpos, ypos);
            this.merchant = merchant;
            this.good = good;
            this.player = player;
        }

        public ItemStack func_75209_a(int i) {
            return null;
        }

        public boolean func_75216_d() {
            return this.func_75211_c() != null;
        }

        public int func_75219_a() {
            return 0;
        }

        public ItemStack func_75211_c() {
            return new ItemStack(this.good.item.getItem(), Math.max(Math.min(this.merchant.getHouse().countGoods(this.good.item), 99), 1), this.good.item.meta);
        }

        public boolean func_75214_a(ItemStack itemstack) {
            return true;
        }

        public String isProblem() {
            if (this.merchant.getHouse().countGoods(this.good.item) < 1) {
                return LanguageUtilities.string("ui.outofstock");
            }
            int playerMoney = MillCommonUtilities.countMoney((IInventory)this.player.field_71071_by);
            if (this.merchant.getCulture().getTradeGood(this.good.item) != null) {
                if (playerMoney < this.good.getCalculatedSellingPrice(this.merchant)) {
                    return LanguageUtilities.string("ui.missingdeniers").replace("<0>", "" + (this.good.getCalculatedSellingPrice(this.merchant) - playerMoney));
                }
            } else {
                MillLog.error(null, "Unknown trade good: " + this.good);
            }
            return null;
        }

        public void func_75218_e() {
        }

        public void func_75215_d(ItemStack itemstack) {
        }

        public String toString() {
            return this.good.getName();
        }
    }
}

