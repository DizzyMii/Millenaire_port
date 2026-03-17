package org.dizzymii.millenaire2.village;

import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
        int current = countGoods(item);
        if (current < count) return false;
        int remaining = current - count;
        if (remaining <= 0) resources.remove(item);
        else resources.put(item, remaining);
        return true;
    }

    // TODO: Implement NBT save/load, locked chest management, stall allocation, packet sync
}
