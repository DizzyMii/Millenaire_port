package org.dizzymii.millenaire2.item;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Represents a trade good entry for villager trading.
 * Ported from org.millenaire.common.item.TradeGood (Forge 1.12.2).
 *
 * Supports:
 * - Base buy/sell prices with reputation discounts
 * - Supply/demand modifiers based on building stock levels
 * - InvItem key reference for building inventory integration
 * - Quantity per trade (batch trading)
 */
public class TradeGood {
    public ItemStack item = ItemStack.EMPTY;
    public int buyPrice = 0;
    public int sellPrice = 0;
    public int quantity = 1;
    @Nullable public String invItemKey = null;

    private static final double MAX_REPUTATION_DISCOUNT = 0.20;
    private static final int MAX_REPUTATION = 1000;
    private static final double SUPPLY_PRICE_MIN = 0.5;
    private static final double SUPPLY_PRICE_MAX = 2.0;
    private static final int SUPPLY_BASELINE = 32;

    public TradeGood() {}

    public TradeGood(ItemStack item, int buyPrice, int sellPrice) {
        this.item = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public TradeGood(ItemStack item, int buyPrice, int sellPrice, int quantity, @Nullable String invItemKey) {
        this.item = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.quantity = Math.max(1, quantity);
        this.invItemKey = invItemKey;
    }

    public boolean matches(ItemStack other) {
        return !item.isEmpty() && ItemStack.isSameItemSameComponents(item, other);
    }

    public int getAdjustedBuyPrice(int reputation) {
        double discount = Math.min(reputation, MAX_REPUTATION) / (MAX_REPUTATION / MAX_REPUTATION_DISCOUNT);
        return Math.max(1, (int) (buyPrice * (1.0 - discount)));
    }

    public int getAdjustedSellPrice(int reputation) {
        double bonus = Math.min(reputation, MAX_REPUTATION) / (MAX_REPUTATION / MAX_REPUTATION_DISCOUNT);
        return (int) (sellPrice * (1.0 + bonus));
    }

    /**
     * Get buy price adjusted for both reputation and supply level.
     * When building has high stock, buy price drops (supply > demand).
     * When building has low stock, buy price rises (scarcity).
     *
     * @param reputation player reputation with the village
     * @param buildingStock current stock in the building (-1 to skip supply adjustment)
     */
    public int getSupplyAdjustedBuyPrice(int reputation, int buildingStock) {
        int base = getAdjustedBuyPrice(reputation);
        if (buildingStock < 0) return base;
        double supplyFactor = computeSupplyFactor(buildingStock);
        // Invert: high supply = lower buy price for player
        double adjustedFactor = 2.0 - supplyFactor;
        adjustedFactor = Math.max(SUPPLY_PRICE_MIN, Math.min(SUPPLY_PRICE_MAX, adjustedFactor));
        return Math.max(1, (int) (base * adjustedFactor));
    }

    /**
     * Get sell price adjusted for both reputation and supply level.
     * When building has high stock, sell price drops (doesn't need more).
     * When building has low stock, sell price rises (needs the goods).
     *
     * @param reputation player reputation with the village
     * @param buildingStock current stock in the building (-1 to skip supply adjustment)
     */
    public int getSupplyAdjustedSellPrice(int reputation, int buildingStock) {
        int base = getAdjustedSellPrice(reputation);
        if (buildingStock < 0) return base;
        double supplyFactor = computeSupplyFactor(buildingStock);
        // Direct: high supply = lower sell price for player
        double adjustedFactor = 2.0 - supplyFactor;
        adjustedFactor = Math.max(SUPPLY_PRICE_MIN, Math.min(SUPPLY_PRICE_MAX, adjustedFactor));
        return Math.max(0, (int) (base * adjustedFactor));
    }

    /**
     * Compute a supply factor from 0.5 (low stock) to 2.0 (high stock).
     * Baseline is SUPPLY_BASELINE items.
     */
    private double computeSupplyFactor(int stock) {
        if (stock <= 0) return SUPPLY_PRICE_MIN;
        double ratio = (double) stock / SUPPLY_BASELINE;
        return Math.max(SUPPLY_PRICE_MIN, Math.min(SUPPLY_PRICE_MAX, ratio));
    }

    /**
     * Resolve the InvItem for this trade good. Uses invItemKey if set,
     * otherwise tries to find an InvItem matching the ItemStack.
     */
    @Nullable
    public InvItem resolveInvItem() {
        if (invItemKey != null) {
            return InvItem.get(invItemKey);
        }
        if (!item.isEmpty()) {
            return InvItem.findByItem(item.getItem());
        }
        return null;
    }
}
