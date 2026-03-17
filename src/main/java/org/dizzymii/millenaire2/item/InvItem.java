package org.dizzymii.millenaire2.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.dizzymii.millenaire2.util.LegacyBlockMapping;
import org.dizzymii.millenaire2.util.MillLog;

import java.util.HashMap;
import java.util.Map;

/**
 * Millénaire's internal item reference system.
 * Maps string keys (from data files) to Minecraft Item instances.
 * Handles both mod items and vanilla items, including legacy 1.12.2 ID mapping.
 *
 * Ported from org.millenaire.common.item.InvItem.
 */
public class InvItem {

    /** Registry of all known InvItems by their Millénaire key. */
    private static final Map<String, InvItem> ITEMS = new HashMap<>();

    /** The Millénaire string key (e.g. "wheat", "normanbroadsword", "ciderapple"). */
    public final String key;

    /** The resolved Minecraft Item. */
    private Item item;

    /** The resource location string used for resolution. */
    private final String itemId;

    private InvItem(String key, String itemId) {
        this.key = key;
        this.itemId = itemId;
        this.item = null; // Resolved lazily
    }

    /**
     * Get the resolved Minecraft Item. Resolves lazily on first call.
     */
    public Item getItem() {
        if (item == null) {
            resolve();
        }
        return item;
    }

    /**
     * Create an ItemStack of this item with the given count.
     */
    public ItemStack getItemStack(int count) {
        Item resolved = getItem();
        if (resolved == null || resolved == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(resolved, count);
    }

    /**
     * Create an ItemStack of this item with count 1.
     */
    public ItemStack getItemStack() {
        return getItemStack(1);
    }

    private void resolve() {
        try {
            ResourceLocation rl = ResourceLocation.parse(itemId);
            item = BuiltInRegistries.ITEM.get(rl);
            if (item == Items.AIR && !"minecraft:air".equals(itemId)) {
                MillLog.warn(this, "Could not resolve item: " + key + " -> " + itemId);
            }
        } catch (Exception e) {
            MillLog.error(this, "Failed to resolve item: " + key + " -> " + itemId, e);
            item = Items.AIR;
        }
    }

    // --- Static registry methods ---

    /**
     * Register an InvItem from Millénaire data files.
     * @param key The Millénaire item key (e.g. "wheat")
     * @param legacyItemId The 1.12.2 item ID, possibly with metadata (e.g. "minecraft:wheat;0")
     */
    public static InvItem register(String key, String legacyItemId) {
        // Map legacy ID to 1.21.1 ID
        String modernId = LegacyBlockMapping.mapItem(legacyItemId);
        InvItem invItem = new InvItem(key, modernId);
        ITEMS.put(key, invItem);
        return invItem;
    }

    /**
     * Register an InvItem that maps directly to a modern item ID (no legacy mapping needed).
     */
    public static InvItem registerDirect(String key, String modernItemId) {
        InvItem invItem = new InvItem(key, modernItemId);
        ITEMS.put(key, invItem);
        return invItem;
    }

    /**
     * Get an InvItem by its Millénaire key.
     */
    public static InvItem get(String key) {
        return ITEMS.get(key);
    }

    /**
     * Get an InvItem by its Millénaire key, returning a fallback if not found.
     */
    public static InvItem getOrDefault(String key, InvItem fallback) {
        return ITEMS.getOrDefault(key, fallback);
    }

    /**
     * Check if an InvItem with the given key exists.
     */
    public static boolean exists(String key) {
        return ITEMS.containsKey(key);
    }

    /**
     * Get all registered InvItems.
     */
    public static Map<String, InvItem> getAll() {
        return ITEMS;
    }

    /**
     * Clear all registered InvItems (for reload).
     */
    public static void clear() {
        ITEMS.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvItem other)) return false;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "InvItem{" + key + " -> " + itemId + "}";
    }
}
