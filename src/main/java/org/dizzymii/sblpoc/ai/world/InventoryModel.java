package org.dizzymii.sblpoc.ai.world;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * High-level inventory queries wrapping PocNpc's SimpleContainer.
 * Refreshed by InventoryStateSensor every ~20 ticks.
 */
public class InventoryModel {

    private final SimpleContainer inventory;
    private ItemStack mainHand;
    private ItemStack offHand;

    // Cached item counts (refreshed on demand)
    private final Map<Item, Integer> itemCounts = new HashMap<>();
    private boolean dirty = true;

    public InventoryModel(SimpleContainer inventory) {
        this.inventory = inventory;
        this.mainHand = ItemStack.EMPTY;
        this.offHand = ItemStack.EMPTY;
    }

    /**
     * Call when equipment changes to keep model in sync.
     */
    public void updateHands(ItemStack mainHand, ItemStack offHand) {
        this.mainHand = mainHand;
        this.offHand = offHand;
    }

    /**
     * Mark the model as needing a recount.
     */
    public void markDirty() {
        dirty = true;
    }

    private void refreshIfDirty() {
        if (!dirty) return;
        itemCounts.clear();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                itemCounts.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }
        // Include held items
        if (!mainHand.isEmpty()) {
            itemCounts.merge(mainHand.getItem(), mainHand.getCount(), Integer::sum);
        }
        if (!offHand.isEmpty()) {
            itemCounts.merge(offHand.getItem(), offHand.getCount(), Integer::sum);
        }
        dirty = false;
    }

    // ========== Queries ==========

    public boolean hasItem(Item item, int minCount) {
        refreshIfDirty();
        return itemCounts.getOrDefault(item, 0) >= minCount;
    }

    public boolean hasItem(Item item) {
        return hasItem(item, 1);
    }

    public int countItem(Item item) {
        refreshIfDirty();
        return itemCounts.getOrDefault(item, 0);
    }

    /**
     * Get all item counts as a snapshot map.
     */
    public Map<Item, Integer> getItemCounts() {
        refreshIfDirty();
        return new HashMap<>(itemCounts);
    }

    /**
     * Find the first inventory slot matching a predicate. Returns -1 if not found.
     */
    public int findSlot(Predicate<ItemStack> predicate) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (predicate.test(inventory.getItem(i))) return i;
        }
        return -1;
    }

    /**
     * Count empty inventory slots.
     */
    public int getEmptySlots() {
        int count = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).isEmpty()) count++;
        }
        return count;
    }

    /**
     * Get the best tool for mining a given block, or null if none.
     */
    @Nullable
    public ToolLookup getBestToolFor(BlockState blockState) {
        ToolLookup best = null;
        float bestSpeed = 1.0f;

        // Check main hand
        float mainSpeed = mainHand.getDestroySpeed(blockState);
        if (mainSpeed > bestSpeed) {
            best = new ToolLookup(mainHand, -1); // -1 = already in hand
            bestSpeed = mainSpeed;
        }

        // Check inventory
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;
            float speed = stack.getDestroySpeed(blockState);
            if (speed > bestSpeed) {
                best = new ToolLookup(stack, i);
                bestSpeed = speed;
            }
        }

        return best;
    }

    /**
     * Total armor protection value from all equipped armor.
     * Note: PocNpc doesn't wear armor yet; this is for future use.
     */
    public int getArmorScore() {
        // For now, return 0. Will be populated when armor equipping is added.
        return 0;
    }

    /**
     * Total nutrition available from all food in inventory.
     */
    public int getFoodSupply() {
        int total = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                FoodProperties food = stack.getFoodProperties(null);
                if (food != null) {
                    total += food.nutrition() * stack.getCount();
                }
            }
        }
        return total;
    }

    /**
     * Check if we have any weapon (sword or axe).
     */
    public boolean hasWeapon() {
        if (mainHand.getItem() instanceof SwordItem || mainHand.getItem() instanceof AxeItem) return true;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            Item item = inventory.getItem(i).getItem();
            if (item instanceof SwordItem || item instanceof AxeItem) return true;
        }
        return false;
    }

    /**
     * Check if we have a pickaxe of at least a certain tier.
     */
    public boolean hasPickaxe() {
        if (mainHand.getItem() instanceof PickaxeItem) return true;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (inventory.getItem(i).getItem() instanceof PickaxeItem) return true;
        }
        return false;
    }

    /**
     * Get the highest tier tool we have (for GOAP state).
     */
    public ToolTier getHighestToolTier() {
        ToolTier best = ToolTier.NONE;
        best = checkTier(mainHand, best);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            best = checkTier(inventory.getItem(i), best);
        }
        return best;
    }

    private static ToolTier checkTier(ItemStack stack, ToolTier current) {
        Item item = stack.getItem();
        if (item instanceof TieredItem tiered) {
            Tier tier = tiered.getTier();
            ToolTier mapped = mapTier(tier);
            if (mapped.ordinal() > current.ordinal()) return mapped;
        }
        return current;
    }

    private static ToolTier mapTier(Tier tier) {
        if (tier == Tiers.NETHERITE) return ToolTier.NETHERITE;
        if (tier == Tiers.DIAMOND) return ToolTier.DIAMOND;
        if (tier == Tiers.IRON) return ToolTier.IRON;
        if (tier == Tiers.STONE) return ToolTier.STONE;
        if (tier == Tiers.WOOD) return ToolTier.WOOD;
        return ToolTier.WOOD;
    }

    // ========== Inner types ==========

    public enum ToolTier {
        NONE, WOOD, STONE, IRON, DIAMOND, NETHERITE
    }

    public record ToolLookup(ItemStack stack, int inventorySlot) {}
}
