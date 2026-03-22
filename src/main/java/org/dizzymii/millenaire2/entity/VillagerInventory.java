package org.dizzymii.millenaire2.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.dizzymii.millenaire2.item.InvItem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the item inventory for a Millénaire villager.
 * Extracted from MillVillager to separate inventory concerns from entity logic.
 */
public class VillagerInventory {

    private static final String NBT_INV_KEY = "key";
    private static final String NBT_INV_COUNT = "count";

    private final HashMap<InvItem, Integer> items = new HashMap<>();

    public int count(InvItem item) {
        return items.getOrDefault(item, 0);
    }

    public void add(InvItem item, int amount) {
        items.merge(item, amount, Integer::sum);
        Integer current = items.get(item);
        if (current != null && current <= 0) {
            items.remove(item);
        }
    }

    public void remove(InvItem item, int amount) {
        add(item, -amount);
    }

    public Map<InvItem, Integer> getAll() {
        return Collections.unmodifiableMap(items);
    }

    public void clear() {
        items.clear();
    }

    public ListTag saveToNBT() {
        ListTag list = new ListTag();
        for (Map.Entry<InvItem, Integer> entry : items.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putString(NBT_INV_KEY, entry.getKey().key);
            tag.putInt(NBT_INV_COUNT, entry.getValue());
            list.add(tag);
        }
        return list;
    }

    public void loadFromNBT(ListTag list) {
        items.clear();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            String key = tag.getString(NBT_INV_KEY);
            int count = tag.getInt(NBT_INV_COUNT);
            InvItem invItem = InvItem.get(key);
            if (invItem != null && count > 0) {
                items.put(invItem, count);
            }
        }
    }
}
