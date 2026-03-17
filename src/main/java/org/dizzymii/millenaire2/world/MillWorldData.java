package org.dizzymii.millenaire2.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.VillagerRecord;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Per-world Millénaire data — tracks all buildings, villagers, profiles, and global tags.
 * Extends SavedData for automatic persistence with the Minecraft world.
 * Ported from org.millenaire.common.world.MillWorldData (Forge 1.12.2).
 */
public class MillWorldData extends SavedData {

    private static final String DATA_NAME = Millenaire2.MODID + "_world";

    // ========== Fields ==========
    private final HashMap<Point, Building> buildings = new HashMap<>();
    private final HashMap<Long, VillagerRecord> villagerRecords = new HashMap<>();
    public final List<String> globalTags = new ArrayList<>();
    public HashMap<UUID, UserProfile> profiles = new HashMap<>();

    @Nullable public Level world;

    public boolean millenaireEnabled = true;
    public boolean generateVillages = true;
    public long lastWorldUpdate = 0L;

    // ========== Factory ==========

    /**
     * Get or create the MillWorldData for a server level.
     */
    public static MillWorldData get(ServerLevel level) {
        MillWorldData data = level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(MillWorldData::new, MillWorldData::load),
                DATA_NAME
        );
        data.world = level;
        // Set world reference on all buildings
        for (Building b : data.buildings.values()) {
            b.world = level;
        }
        return data;
    }

    public MillWorldData() {}

    // ========== Building management ==========

    public void addBuilding(Building b, Point p) {
        buildings.put(p, b);
        b.world = this.world;
        setDirty();
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

    public Map<Point, Building> getBuildingsMap() {
        return buildings;
    }

    public void removeBuilding(Point p) {
        buildings.remove(p);
        setDirty();
    }

    // ========== Villager records ==========

    public void addVillagerRecord(VillagerRecord vr) {
        villagerRecords.put(vr.getVillagerId(), vr);
        setDirty();
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
        setDirty();
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
            setDirty();
            return p;
        });
    }

    // ========== Global tags ==========

    public boolean hasGlobalTag(String tag) {
        return globalTags.contains(tag);
    }

    public void addGlobalTag(String tag) {
        if (!globalTags.contains(tag)) {
            globalTags.add(tag);
            setDirty();
        }
    }

    public void removeGlobalTag(String tag) {
        if (globalTags.remove(tag)) {
            setDirty();
        }
    }

    // ========== Tick ==========

    /**
     * Called from the server tick event. Ticks all loaded buildings.
     */
    public void tick() {
        if (world == null || world.isClientSide) return;
        lastWorldUpdate = world.getGameTime();

        for (Building b : buildings.values()) {
            if (b.isActive) {
                b.tick();
            }
        }
    }

    // ========== NBT persistence ==========

    @Override
    public CompoundTag save(CompoundTag root, HolderLookup.Provider registries) {
        root.putBoolean("enabled", millenaireEnabled);
        root.putBoolean("generateVillages", generateVillages);
        root.putLong("lastUpdate", lastWorldUpdate);

        // Save buildings
        ListTag buildingList = new ListTag();
        for (Map.Entry<Point, Building> entry : buildings.entrySet()) {
            CompoundTag bTag = entry.getValue().save();
            entry.getKey().writeToNBT(bTag, "worldPos");
            buildingList.add(bTag);
        }
        root.put("buildings", buildingList);

        // Save global tags
        ListTag tagList = new ListTag();
        for (String tag : globalTags) {
            CompoundTag t = new CompoundTag();
            t.putString("tag", tag);
            tagList.add(t);
        }
        root.put("globalTags", tagList);

        // Save player profiles
        ListTag profileList = new ListTag();
        for (Map.Entry<UUID, UserProfile> entry : profiles.entrySet()) {
            CompoundTag pTag = entry.getValue().save();
            profileList.add(pTag);
        }
        root.put("profiles", profileList);

        MillLog.minor("MillWorldData", "Saved " + buildings.size() + " buildings, " + profiles.size() + " profiles.");
        return root;
    }

    public static MillWorldData load(CompoundTag root, HolderLookup.Provider registries) {
        MillWorldData data = new MillWorldData();
        data.millenaireEnabled = root.getBoolean("enabled");
        data.generateVillages = root.getBoolean("generateVillages");
        data.lastWorldUpdate = root.getLong("lastUpdate");

        // Load buildings
        if (root.contains("buildings", Tag.TAG_LIST)) {
            ListTag buildingList = root.getList("buildings", Tag.TAG_COMPOUND);
            for (int i = 0; i < buildingList.size(); i++) {
                CompoundTag bTag = buildingList.getCompound(i);
                Point pos = Point.readFromNBT(bTag, "worldPos");
                if (pos != null) {
                    Building b = Building.load(bTag);
                    data.buildings.put(pos, b);
                }
            }
        }

        // Load global tags
        if (root.contains("globalTags", Tag.TAG_LIST)) {
            ListTag tagList = root.getList("globalTags", Tag.TAG_COMPOUND);
            for (int i = 0; i < tagList.size(); i++) {
                data.globalTags.add(tagList.getCompound(i).getString("tag"));
            }
        }

        // Load player profiles
        if (root.contains("profiles", Tag.TAG_LIST)) {
            ListTag profileList = root.getList("profiles", Tag.TAG_COMPOUND);
            for (int i = 0; i < profileList.size(); i++) {
                UserProfile p = UserProfile.load(profileList.getCompound(i));
                if (p.uuid != null) {
                    data.profiles.put(p.uuid, p);
                }
            }
        }

        MillLog.minor("MillWorldData", "Loaded " + data.buildings.size() + " buildings, " + data.profiles.size() + " profiles.");
        return data;
    }
}
