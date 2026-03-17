package org.dizzymii.millenaire2.world;

import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.VillagerRecord;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Per-world Millénaire data — tracks all buildings, villagers, profiles, and global tags.
 * Ported from org.millenaire.common.world.MillWorldData (Forge 1.12.2).
 *
 * This is a stub capturing key fields and method signatures.
 * Full persistence, tick logic, and village generation will be added incrementally.
 */
public class MillWorldData {

    // ========== Fields ==========
    private final HashMap<Point, Building> buildings = new HashMap<>();
    private final HashMap<Long, VillagerRecord> villagerRecords = new HashMap<>();
    public final List<String> globalTags = new ArrayList<>();
    public HashMap<UUID, UserProfile> profiles = new HashMap<>();

    @Nullable public Level world;
    @Nullable public File millenaireDir;
    @Nullable public File saveDir;

    public boolean millenaireEnabled = false;
    public boolean generateVillages = false;
    public boolean generateVillagesSet = false;
    public long lastWorldUpdate = 0L;

    // ========== Building management ==========

    public void addBuilding(Building b, Point p) {
        buildings.put(p, b);
    }

    @Nullable
    public Building getBuilding(Point p) {
        return buildings.get(p);
    }

    public boolean buildingExists(Point p) {
        return buildings.containsKey(p);
    }

    public Collection<Building> allBuildings() {
        return buildings.values();
    }

    public void removeBuilding(Point p) {
        buildings.remove(p);
    }

    // ========== Villager records ==========

    public void addVillagerRecord(VillagerRecord vr) {
        villagerRecords.put(vr.getVillagerId(), vr);
    }

    @Nullable
    public VillagerRecord getVillagerRecord(long id) {
        return villagerRecords.get(id);
    }

    public Collection<VillagerRecord> allVillagerRecords() {
        return villagerRecords.values();
    }

    public void removeVillagerRecord(long id) {
        villagerRecords.remove(id);
    }

    // ========== Profile management ==========

    @Nullable
    public UserProfile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    public UserProfile getOrCreateProfile(UUID uuid, String playerName) {
        return profiles.computeIfAbsent(uuid, id -> {
            UserProfile p = new UserProfile();
            p.uuid = id;
            p.playerName = playerName;
            return p;
        });
    }

    // ========== Global tags ==========

    public boolean hasGlobalTag(String tag) {
        return globalTags.contains(tag);
    }

    public void addGlobalTag(String tag) {
        if (!globalTags.contains(tag)) globalTags.add(tag);
    }

    // TODO: NBT save/load, world tick, village generation trigger, chunk loading
}
