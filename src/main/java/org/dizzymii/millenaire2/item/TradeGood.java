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
    // TODO: Implement trade good matching, reputation-based pricing
}
