package org.dizzymii.millenaire2.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.culture.VillageType;
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
    public static class VillageList {
        public final List<String> names = new ArrayList<>();
        public final List<Point> pos = new ArrayList<>();
        public final List<String> cultures = new ArrayList<>();
        public final List<String> types = new ArrayList<>();

        public void clear() {
            names.clear();
            pos.clear();
            cultures.clear();
            types.clear();
        }

        public void add(String name, Point point, String cultureKey, String typeKey) {
            names.add(name == null ? "" : name);
            pos.add(point);
            cultures.add(cultureKey == null ? "" : cultureKey);
            types.add(typeKey == null ? "" : typeKey);
        }
    }

    private final HashMap<Point, Building> buildings = new HashMap<>();
    private final HashMap<Long, VillagerRecord> villagerRecords = new HashMap<>();
    public final List<String> globalTags = new ArrayList<>();
    public final VillageList villagesList = new VillageList();
    public final VillageList loneBuildingsList = new VillageList();
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
        // Set world and mw references on all buildings
        for (Building b : data.buildings.values()) {
            b.world = level;
            b.mw = data;
        }
        return data;
    }

    public MillWorldData() {}

    // ========== Building management ==========

    public void addBuilding(Building b, Point p) {
        buildings.put(p, b);
        b.world = this.world;
        b.mw = this;
        updateVillageListsForBuilding(b, p);
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
        Building removed = buildings.remove(p);
        if (removed != null) {
            removeFromVillageList(villagesList, p);
            removeFromVillageList(loneBuildingsList, p);
        }
        setDirty();
    }

    // ========== Villager records ==========

    public void addVillagerRecord(VillagerRecord vr) {
        villagerRecords.put(vr.getVillagerId(), vr);
        setDirty();
    }

    public void registerVillagerRecord(VillagerRecord vr, boolean markDirty) {
        if (vr == null) {
            return;
        }
        villagerRecords.put(vr.getVillagerId(), vr);
        if (markDirty) {
            setDirty();
        }
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

    public void clearVillagerOfId(long id) {
        villagerRecords.remove(id);
        for (Building b : buildings.values()) {
            b.removeVillagerRecord(id);
        }
        setDirty();
    }

    public void refreshVillageAndLoneBuildingLists() {
        villagesList.clear();
        loneBuildingsList.clear();
        for (Map.Entry<Point, Building> entry : buildings.entrySet()) {
            updateVillageListsForBuilding(entry.getValue(), entry.getKey());
        }
        setDirty();
    }

    public List<Point> getCombinedVillagesLoneBuildings() {
        List<Point> combined = new ArrayList<>(villagesList.pos);
        combined.addAll(loneBuildingsList.pos);
        return combined;
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

    public boolean isGlobalTagSet(String tag) {
        return hasGlobalTag(tag);
    }

    public void setGlobalTag(String tag) {
        addGlobalTag(tag);
    }

    public void clearGlobalTag(String tag) {
        removeGlobalTag(tag);
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

    private void updateVillageListsForBuilding(Building b, Point p) {
        if (!b.isTownhall) {
            return;
        }

        String cultureKey = b.cultureKey == null ? "" : b.cultureKey;
        String typeKey = b.villageTypeKey == null ? "" : b.villageTypeKey;
        String displayName = b.getName() == null ? "" : b.getName();

        removeFromVillageList(villagesList, p);
        removeFromVillageList(loneBuildingsList, p);

        VillageType vType = null;
        if (!cultureKey.isEmpty()) {
            Culture culture = Culture.getCultureByName(cultureKey);
            if (culture != null && !typeKey.isEmpty()) {
                vType = culture.villageTypes.get(typeKey);
                if (vType == null) {
                    vType = culture.loneBuildingTypes.get(typeKey);
                }
            }
        }

        if (vType != null && vType.lonebuilding) {
            loneBuildingsList.add(displayName, p, cultureKey, typeKey);
        } else {
            villagesList.add(displayName, p, cultureKey, typeKey);
        }
    }

    private void removeFromVillageList(VillageList list, Point p) {
        for (int i = list.pos.size() - 1; i >= 0; i--) {
            if (list.pos.get(i).equals(p)) {
                list.pos.remove(i);
                list.names.remove(i);
                list.cultures.remove(i);
                list.types.remove(i);
            }
        }
    }

    private static ListTag saveVillageList(VillageList list) {
        ListTag out = new ListTag();
        int size = list.pos.size();
        for (int i = 0; i < size; i++) {
            CompoundTag entry = new CompoundTag();
            list.pos.get(i).writeToNBT(entry, "pos");
            entry.putString("name", list.names.get(i));
            entry.putString("culture", list.cultures.get(i));
            entry.putString("type", list.types.get(i));
            out.add(entry);
        }
        return out;
    }

    private static void loadVillageList(CompoundTag root, String key, VillageList target) {
        target.clear();
        if (!root.contains(key, Tag.TAG_LIST)) {
            return;
        }
        ListTag list = root.getList(key, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            Point point = Point.readFromNBT(entry, "pos");
            if (point == null) {
                continue;
            }
            target.add(entry.getString("name"), point, entry.getString("culture"), entry.getString("type"));
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

        root.put("villagesList", saveVillageList(villagesList));
        root.put("loneBuildingsList", saveVillageList(loneBuildingsList));

        // Save global tags
        ListTag tagList = new ListTag();
        for (String tag : globalTags) {
            CompoundTag t = new CompoundTag();
            t.putString("tag", tag);
            tagList.add(t);
        }
        root.put("globalTags", tagList);

        // Save global villager records
        ListTag villagerRecordList = new ListTag();
        for (VillagerRecord vr : villagerRecords.values()) {
            villagerRecordList.add(vr.save());
        }
        root.put("villagerRecords", villagerRecordList);

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

        loadVillageList(root, "villagesList", data.villagesList);
        loadVillageList(root, "loneBuildingsList", data.loneBuildingsList);
        if (data.villagesList.pos.isEmpty() && data.loneBuildingsList.pos.isEmpty()) {
            data.refreshVillageAndLoneBuildingLists();
        }

        // Load global tags
        if (root.contains("globalTags", Tag.TAG_LIST)) {
            ListTag tagList = root.getList("globalTags", Tag.TAG_COMPOUND);
            for (int i = 0; i < tagList.size(); i++) {
                data.globalTags.add(tagList.getCompound(i).getString("tag"));
            }
        }

        // Load global villager records
        if (root.contains("villagerRecords", Tag.TAG_LIST)) {
            ListTag villagerRecordList = root.getList("villagerRecords", Tag.TAG_COMPOUND);
            for (int i = 0; i < villagerRecordList.size(); i++) {
                VillagerRecord vr = VillagerRecord.load(villagerRecordList.getCompound(i));
                data.villagerRecords.put(vr.getVillagerId(), vr);
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
