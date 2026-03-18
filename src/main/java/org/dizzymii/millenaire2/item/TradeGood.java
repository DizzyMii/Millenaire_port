package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.ItemStack;

/**
 * Represents a trade good entry for villager trading.
 * Ported from org.millenaire.common.item.TradeGood (Forge 1.12.2).
 */
public class TradeGood {
    public ItemStack item = ItemStack.EMPTY;
    public int buyPrice = 0;
    public int sellPrice = 0;

    public TradeGood() {}
    public TradeGood(ItemStack item, int buyPrice, int sellPrice) {
        this.item = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public boolean matches(net.minecraft.world.item.ItemStack other) {
        return !item.isEmpty() && net.minecraft.world.item.ItemStack.isSameItemSameComponents(item, other);
    }

    public int getAdjustedBuyPrice(int reputation) {
        // Discount up to 20% for max reputation (1000)
        double discount = Math.min(reputation, 1000) / 5000.0;
        return Math.max(1, (int) (buyPrice * (1.0 - discount)));
    }

    public int getAdjustedSellPrice(int reputation) {
        // Bonus up to 20% for max reputation (1000)
        double bonus = Math.min(reputation, 1000) / 5000.0;
        return (int) (sellPrice * (1.0 + bonus));
    }
}
