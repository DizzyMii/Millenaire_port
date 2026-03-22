package org.dizzymii.millenaire2.world;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.VillagerRecord;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-world Millénaire data — tracks all buildings, villagers, profiles, and global tags.
 * Extends SavedData for automatic persistence with the Minecraft world.
 * Ported from org.millenaire.common.world.MillWorldData (Forge 1.12.2).
 */
public class MillWorldData extends SavedData {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String DATA_NAME = Millenaire2.MODID + "_world";

    // ========== NBT key constants ==========
    private static final String NBT_ENABLED = "enabled";
    private static final String NBT_GENERATE_VILLAGES = "generateVillages";
    private static final String NBT_LAST_UPDATE = "lastUpdate";
    private static final String NBT_BUILDINGS = "buildings";
    private static final String NBT_WORLD_POS = "worldPos";
    private static final String NBT_GLOBAL_TAGS = "globalTags";
    private static final String NBT_TAG = "tag";
    private static final String NBT_PROFILES = "profiles";
    private static final String NBT_TRIED_CHUNKS = "triedChunks";

    // ========== Fields ==========
    private final ConcurrentHashMap<Point, Building> buildings = new ConcurrentHashMap<>();
    private final Set<Long> triedChunkKeys = Collections.synchronizedSet(new HashSet<>());
    private final ConcurrentHashMap<Long, VillagerRecord> villagerRecords = new ConcurrentHashMap<>();
    public final List<String> globalTags = new ArrayList<>();
    public ConcurrentHashMap<UUID, UserProfile> profiles = new ConcurrentHashMap<>();

    @Nullable public Level world;

    private static final int STALE_CLEANUP_INTERVAL = 12000; // every 10 minutes
    private int cleanupCounter = 0;

    /** Cached set of building positions whose chunks were loaded at last refresh. */
    private final Set<Point> activePoints = ConcurrentHashMap.newKeySet();
    private int activeRefreshCounter = 0;
    private static final int ACTIVE_REFRESH_INTERVAL = 40; // rebuild every 2 seconds

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
        // Set level context on all buildings
        for (Building b : data.buildings.values()) {
            b.setLevelContext(level, data);
        }
        return data;
    }

    public MillWorldData() {}

    // ========== Building management ==========

    public void addBuilding(Building b, Point p) {
        buildings.put(p, b);
        b.setLevelContext(this.world, this);
        if (b.isActive) activePoints.add(p);
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
        activePoints.remove(p);
        setDirty();
    }

    /**
     * Returns the nearest building within {@code maxDist} blocks of {@code pos},
     * using a bounding-box pre-filter to avoid calling distanceTo() on distant buildings.
     */
    @Nullable
    public Building getBuildingNear(Point pos, double maxDist) {
        double closest = maxDist;
        Building result = null;
        for (Map.Entry<Point, Building> entry : buildings.entrySet()) {
            Point bPos = entry.getKey();
            if (Math.abs(bPos.x - pos.x) > maxDist) continue;
            if (Math.abs(bPos.z - pos.z) > maxDist) continue;
            double dist = bPos.distanceTo(pos);
            if (dist < closest) {
                closest = dist;
                result = entry.getValue();
            }
        }
        return result;
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

    // ========== Tried chunk tracking (village generation dedup) ==========

    public boolean hasTriedChunk(long chunkKey) {
        return triedChunkKeys.contains(chunkKey);
    }

    public void markChunkTried(long chunkKey) {
        triedChunkKeys.add(chunkKey);
    }

    public void clearTriedChunks() {
        triedChunkKeys.clear();
    }

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
     * Only ticks buildings in loaded chunks to avoid unnecessary work.
     */
    public void tick() {
        if (world == null || world.isClientSide) return;
        if (!(world instanceof net.minecraft.server.level.ServerLevel sl)) return;
        lastWorldUpdate = world.getGameTime();

        // Periodically rebuild active set — avoids calling hasChunk() for every building every tick
        activeRefreshCounter++;
        if (activeRefreshCounter >= ACTIVE_REFRESH_INTERVAL) {
            activeRefreshCounter = 0;
            rebuildActivePoints(sl);
        }

        // Only tick buildings whose chunks were loaded at last refresh
        for (Point pos : activePoints) {
            Building b = buildings.get(pos);
            if (b != null) b.tick();
        }

        // Periodic stale data cleanup
        cleanupCounter++;
        if (cleanupCounter >= STALE_CLEANUP_INTERVAL) {
            cleanupCounter = 0;
            cleanupStaleData(sl);
        }
    }

    private void rebuildActivePoints(net.minecraft.server.level.ServerLevel sl) {
        activePoints.clear();
        for (Map.Entry<Point, Building> entry : buildings.entrySet()) {
            Point pos = entry.getKey();
            Building b = entry.getValue();
            if (b.isActive && sl.getChunkSource().hasChunk(pos.x >> 4, pos.z >> 4)) {
                activePoints.add(pos);
            }
        }
    }

    /**
     * Removes stale records: dead villager records with no corresponding building,
     * buildings at invalid positions (no chunk loaded in a while), and empty profile entries.
     */
    private void cleanupStaleData(net.minecraft.server.level.ServerLevel sl) {
        // Remove villager records whose home building no longer exists
        villagerRecords.entrySet().removeIf(entry -> {
            VillagerRecord vr = entry.getValue();
            Point housePos = vr.getHousePos();
            if (housePos == null) return false;
            return !buildings.containsKey(housePos);
        });
        setDirty();
        LOGGER.debug("Stale data cleanup: " + buildings.size() + " buildings, " + villagerRecords.size() + " records.");
    }

    // ========== NBT persistence ==========

    @Override
    public CompoundTag save(CompoundTag root, HolderLookup.Provider registries) {
        root.putBoolean(NBT_ENABLED, millenaireEnabled);
        root.putBoolean(NBT_GENERATE_VILLAGES, generateVillages);
        root.putLong(NBT_LAST_UPDATE, lastWorldUpdate);

        // Save buildings
        ListTag buildingList = new ListTag();
        for (Map.Entry<Point, Building> entry : buildings.entrySet()) {
            CompoundTag bTag = entry.getValue().save();
            entry.getKey().writeToNBT(bTag, NBT_WORLD_POS);
            buildingList.add(bTag);
        }
        root.put(NBT_BUILDINGS, buildingList);

        // Save global tags
        ListTag tagList = new ListTag();
        for (String tag : globalTags) {
            CompoundTag t = new CompoundTag();
            t.putString(NBT_TAG, tag);
            tagList.add(t);
        }
        root.put(NBT_GLOBAL_TAGS, tagList);

        // Save tried chunk keys
        long[] chunkArr = new long[triedChunkKeys.size()];
        int ci = 0;
        for (long k : triedChunkKeys) chunkArr[ci++] = k;
        root.putLongArray(NBT_TRIED_CHUNKS, chunkArr);

        // Save player profiles
        ListTag profileList = new ListTag();
        for (Map.Entry<UUID, UserProfile> entry : profiles.entrySet()) {
            CompoundTag pTag = entry.getValue().save();
            profileList.add(pTag);
        }
        root.put(NBT_PROFILES, profileList);

        LOGGER.debug("Saved " + buildings.size() + " buildings, " + profiles.size() + " profiles.");
        return root;
    }

    public static MillWorldData load(CompoundTag root, HolderLookup.Provider registries) {
        MillWorldData data = new MillWorldData();
        data.millenaireEnabled = root.contains(NBT_ENABLED) ? root.getBoolean(NBT_ENABLED) : true;
        data.generateVillages = root.contains(NBT_GENERATE_VILLAGES) ? root.getBoolean(NBT_GENERATE_VILLAGES) : true;
        data.lastWorldUpdate = root.getLong(NBT_LAST_UPDATE);

        // Load buildings
        if (root.contains(NBT_BUILDINGS, Tag.TAG_LIST)) {
            ListTag buildingList = root.getList(NBT_BUILDINGS, Tag.TAG_COMPOUND);
            for (int i = 0; i < buildingList.size(); i++) {
                CompoundTag bTag = buildingList.getCompound(i);
                Point pos = Point.readFromNBT(bTag, NBT_WORLD_POS);
                if (pos != null) {
                    Building b = Building.load(bTag);
                    data.buildings.put(pos, b);
                }
            }
        }

        // Load global tags
        if (root.contains(NBT_GLOBAL_TAGS, Tag.TAG_LIST)) {
            ListTag tagList = root.getList(NBT_GLOBAL_TAGS, Tag.TAG_COMPOUND);
            for (int i = 0; i < tagList.size(); i++) {
                data.globalTags.add(tagList.getCompound(i).getString(NBT_TAG));
            }
        }

        // Load player profiles
        if (root.contains(NBT_PROFILES, Tag.TAG_LIST)) {
            ListTag profileList = root.getList(NBT_PROFILES, Tag.TAG_COMPOUND);
            for (int i = 0; i < profileList.size(); i++) {
                UserProfile p = UserProfile.load(profileList.getCompound(i));
                if (p.uuid != null) {
                    data.profiles.put(p.uuid, p);
                }
            }
        }

        // Load tried chunk keys
        if (root.contains(NBT_TRIED_CHUNKS)) {
            for (long k : root.getLongArray(NBT_TRIED_CHUNKS)) {
                data.triedChunkKeys.add(k);
            }
        }

        LOGGER.debug("Loaded " + data.buildings.size() + " buildings, " + data.profiles.size() + " profiles.");
        return data;
    }
}
