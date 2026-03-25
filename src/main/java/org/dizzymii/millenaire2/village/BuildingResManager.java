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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages resources, stalls, special positions, and locked chests for a Building.
 * Ported from org.millenaire.common.village.BuildingResManager (Forge 1.12.2).
 */
public class BuildingResManager {

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
        AtomicBoolean success = new AtomicBoolean(false);
        resources.compute(item, (k, current) -> {
            if (current == null || current < count) return current;
            success.set(true);
            int remaining = current - count;
            return remaining <= 0 ? null : remaining;
        });
        return success.get();
    }

    // ========== NBT persistence ==========

    public void save(CompoundTag parent, String prefix) {
        // Save resources
        ListTag resList = new ListTag();
        for (Map.Entry<InvItem, Integer> entry : resources.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putString("key", entry.getKey().key);
            t.putInt("count", entry.getValue());
            resList.add(t);
        }
        parent.put(prefix + "resources", resList);

        // Save stalls
        ListTag stallList = new ListTag();
        for (Point p : stalls) {
            CompoundTag t = new CompoundTag();
            p.writeToNBT(t, "p");
            stallList.add(t);
        }
        parent.put(prefix + "stalls", stallList);

        // Save sleeping positions
        ListTag sleepList = new ListTag();
        for (Point p : sleepingPositions) {
            CompoundTag t = new CompoundTag();
            p.writeToNBT(t, "p");
            sleepList.add(t);
        }
        parent.put(prefix + "sleeping", sleepList);
    }

    public void load(CompoundTag parent, String prefix) {
        resources.clear();
        stalls.clear();
        sleepingPositions.clear();

        // Load resources
        if (parent.contains(prefix + "resources", Tag.TAG_LIST)) {
            ListTag resList = parent.getList(prefix + "resources", Tag.TAG_COMPOUND);
            for (int i = 0; i < resList.size(); i++) {
                CompoundTag t = resList.getCompound(i);
                String key = t.getString("key");
                int count = t.getInt("count");
                InvItem item = InvItem.get(key);
                if (item != null && count > 0) {
                    resources.put(item, count);
                }
            }
        }

        // Load stalls
        if (parent.contains(prefix + "stalls", Tag.TAG_LIST)) {
            ListTag stallList = parent.getList(prefix + "stalls", Tag.TAG_COMPOUND);
            for (int i = 0; i < stallList.size(); i++) {
                Point p = Point.readFromNBT(stallList.getCompound(i), "p");
                if (p != null) stalls.add(p);
            }
        }

        // Load sleeping positions
        if (parent.contains(prefix + "sleeping", Tag.TAG_LIST)) {
            ListTag sleepList = parent.getList(prefix + "sleeping", Tag.TAG_COMPOUND);
            for (int i = 0; i < sleepList.size(); i++) {
                Point p = Point.readFromNBT(sleepList.getCompound(i), "p");
                if (p != null) sleepingPositions.add(p);
            }
        }
    }
}
