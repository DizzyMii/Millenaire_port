package org.dizzymii.millenaire2.village;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages resources, stalls, special positions, and locked chests for a Building.
 * Ported from org.millenaire.common.village.BuildingResManager (Forge 1.12.2).
 */
public class BuildingResManager {

    // ========== NBT key constants ==========
    private static final String NBT_SUFFIX_RESOURCES = "resources";
    private static final String NBT_SUFFIX_STALLS = "stalls";
    private static final String NBT_SUFFIX_SLEEPING = "sleeping";
    private static final String NBT_RES_KEY = "key";
    private static final String NBT_RES_COUNT = "count";
    private static final String NBT_POINT = "p";

    private final Building building;
    public ConcurrentHashMap<InvItem, Integer> resources = new ConcurrentHashMap<>();
    public CopyOnWriteArrayList<Point> stalls = new CopyOnWriteArrayList<>();
    public List<Point> sleepingPositions = new ArrayList<>();

    public BuildingResManager(Building building) {
        this.building = building;
    }

    public Point getSleepingPos() {
        if (sleepingPositions.isEmpty()) return null;
        return sleepingPositions.get(0);
    }

    public int countGoods(InvItem item) {
        return resources.getOrDefault(item, 0);
    }

    public void storeGoods(InvItem item, int count) {
        resources.merge(item, count, Integer::sum);
    }

    public boolean takeGoods(InvItem item, int count) {
        int current = countGoods(item);
        if (current < count) return false;
        int remaining = current - count;
        if (remaining <= 0) resources.remove(item);
        else resources.put(item, remaining);
        return true;
    }

    // ========== NBT persistence ==========

    public void save(CompoundTag parent, String prefix) {
        // Save resources
        ListTag resList = new ListTag();
        for (Map.Entry<InvItem, Integer> entry : resources.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putString(NBT_RES_KEY, entry.getKey().key);
            t.putInt(NBT_RES_COUNT, entry.getValue());
            resList.add(t);
        }
        parent.put(prefix + NBT_SUFFIX_RESOURCES, resList);

        // Save stalls
        ListTag stallList = new ListTag();
        for (Point p : stalls) {
            CompoundTag t = new CompoundTag();
            p.writeToNBT(t, NBT_POINT);
            stallList.add(t);
        }
        parent.put(prefix + NBT_SUFFIX_STALLS, stallList);

        // Save sleeping positions
        ListTag sleepList = new ListTag();
        for (Point p : sleepingPositions) {
            CompoundTag t = new CompoundTag();
            p.writeToNBT(t, NBT_POINT);
            sleepList.add(t);
        }
        parent.put(prefix + NBT_SUFFIX_SLEEPING, sleepList);
    }

    public void load(CompoundTag parent, String prefix) {
        resources.clear();
        stalls.clear();
        sleepingPositions.clear();

        // Load resources
        if (parent.contains(prefix + NBT_SUFFIX_RESOURCES, Tag.TAG_LIST)) {
            ListTag resList = parent.getList(prefix + NBT_SUFFIX_RESOURCES, Tag.TAG_COMPOUND);
            for (int i = 0; i < resList.size(); i++) {
                CompoundTag t = resList.getCompound(i);
                String key = t.getString(NBT_RES_KEY);
                int count = t.getInt(NBT_RES_COUNT);
                InvItem item = InvItem.get(key);
                if (item != null && count > 0) {
                    resources.put(item, count);
                }
            }
        }

        // Load stalls
        if (parent.contains(prefix + NBT_SUFFIX_STALLS, Tag.TAG_LIST)) {
            ListTag stallList = parent.getList(prefix + NBT_SUFFIX_STALLS, Tag.TAG_COMPOUND);
            for (int i = 0; i < stallList.size(); i++) {
                Point p = Point.readFromNBT(stallList.getCompound(i), NBT_POINT);
                if (p != null) stalls.add(p);
            }
        }

        // Load sleeping positions
        if (parent.contains(prefix + NBT_SUFFIX_SLEEPING, Tag.TAG_LIST)) {
            ListTag sleepList = parent.getList(prefix + NBT_SUFFIX_SLEEPING, Tag.TAG_COMPOUND);
            for (int i = 0; i < sleepList.size(); i++) {
                Point p = Point.readFromNBT(sleepList.getCompound(i), NBT_POINT);
                if (p != null) sleepingPositions.add(p);
            }
        }
    }
}
