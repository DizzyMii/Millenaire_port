package org.dizzymii.millenaire2.village;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Core village building class — manages a single building instance within a village.
 * Ported from org.millenaire.common.village.Building (Forge 1.12.2).
 */
public class Building {

    // ========== Relation constants ==========
    public static final int RELATION_NEUTRAL = 0;
    public static final int RELATION_FAIR = 10;
    public static final int RELATION_DECENT = 30;
    public static final int RELATION_GOOD = 50;
    public static final int RELATION_VERYGOOD = 70;
    public static final int RELATION_EXCELLENT = 90;
    public static final int RELATION_CHILLY = -10;
    public static final int RELATION_BAD = -30;
    public static final int RELATION_VERYBAD = -50;
    public static final int RELATION_ATROCIOUS = -70;
    public static final int RELATION_OPENCONFLICT = -90;
    public static final int RELATION_MAX = 100;
    public static final int RELATION_MIN = -100;
    public static final int MIN_REPUTATION_FOR_TRADE = -1024;
    public static final int MAX_REPUTATION = 32768;

    // ========== Key fields ==========
    @Nullable public String cultureKey;
    public boolean isActive = false;
    public boolean isAreaLoaded = false;
    public boolean chestLocked = false;
    public boolean isTownhall = false;
    public boolean isInn = false;
    public boolean isMarket = false;
    public boolean hasVisitors = false;
    public boolean hasAutoSpawn = false;
    public boolean underAttack = false;

    @Nullable public BuildingLocation location;
    @Nullable public VillagerRecord merchantRecord = null;
    @Nullable private String name;
    private String qualifier = "";

    @Nullable private Point pos;
    @Nullable private Point townHallPos;

    public CopyOnWriteArrayList<Point> buildings = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<String> buildingsBought = new CopyOnWriteArrayList<>();
    public ConcurrentHashMap<BuildingProject.EnumProjects, CopyOnWriteArrayList<BuildingProject>> buildingProjects = new ConcurrentHashMap<>();

    private final Set<Object> villagers = new LinkedHashSet<>(); // TODO: Set<MillVillager>
    private final HashMap<Long, VillagerRecord> vrecords = new HashMap<>();
    private final ConcurrentHashMap<Point, Integer> relations = new ConcurrentHashMap<>();

    public CopyOnWriteArrayList<String> raidsPerformed = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<String> raidsSuffered = new CopyOnWriteArrayList<>();
    @Nullable public Point raidTarget;

    @Nullable public UUID controlledBy = null;
    @Nullable public String controlledByName = null;

    @Nullable public MillWorldData mw;
    @Nullable public Level world;

    // ========== Accessors ==========

    @Nullable
    public Point getPos() { return pos; }
    public void setPos(@Nullable Point p) { this.pos = p; }

    @Nullable
    public Point getTownHallPos() { return townHallPos; }
    public void setTownHallPos(@Nullable Point p) { this.townHallPos = p; }

    @Nullable
    public String getName() { return name; }
    public void setName(@Nullable String n) { this.name = n; }

    public String getQualifier() { return qualifier; }
    public void setQualifier(String q) { this.qualifier = q; }

    public Collection<VillagerRecord> getVillagerRecords() { return vrecords.values(); }

    @Nullable
    public VillagerRecord getVillagerRecord(long id) { return vrecords.get(id); }

    public void addVillagerRecord(VillagerRecord vr) { vrecords.put(vr.getVillagerId(), vr); }

    public void removeVillagerRecord(long id) { vrecords.remove(id); }

    public int getRelation(Point villagePos) {
        return relations.getOrDefault(villagePos, RELATION_NEUTRAL);
    }

    public void setRelation(Point villagePos, int value) {
        relations.put(villagePos, Math.max(RELATION_MIN, Math.min(RELATION_MAX, value)));
    }

    public Set<Point> getKnownVillages() { return relations.keySet(); }

    // ========== Resource manager ==========

    public final BuildingResManager resManager = new BuildingResManager(this);

    // ========== Construction ==========

    @Nullable public ConstructionIP currentConstruction = null;

    public boolean isUnderConstruction() {
        return currentConstruction != null;
    }

    // ========== Tick ==========

    private int tickCounter = 0;

    /**
     * Called every server tick while this building's area is loaded.
     * Handles periodic village updates: villager spawning, resource generation, etc.
     */
    public void tick() {
        if (world == null || world.isClientSide) return;
        tickCounter++;

        // Slow tick every 20 ticks (1 second)
        if (tickCounter % 20 == 0) {
            slowTick();
        }
    }

    private void slowTick() {
        // Check if area is loaded
        if (pos == null || world == null) return;
        isAreaLoaded = world.hasChunkAt(pos.toBlockPos());
        if (!isAreaLoaded) return;

        // Spawn missing villagers if this is an active townhall or building
        if (isActive && isTownhall && tickCounter % 200 == 0) {
            checkAndSpawnVillagers();
        }
    }

    // ========== Villager spawning ==========

    /**
     * Check VillagerRecords against loaded entities and spawn any missing villagers.
     */
    private void checkAndSpawnVillagers() {
        if (!(world instanceof ServerLevel serverLevel)) return;
        if (pos == null) return;

        Culture culture = cultureKey != null ? Culture.getCultureByName(cultureKey) : null;

        for (VillagerRecord vr : getVillagerRecords()) {
            if (vr.killed || vr.awayraiding || vr.awayhired) continue;

            // Check if this villager's entity is already loaded
            // (skip spawn if entity exists within range)
            boolean entityExists = !serverLevel.getEntitiesOfClass(
                    MillVillager.class,
                    net.minecraft.world.phys.AABB.ofSize(
                            new net.minecraft.world.phys.Vec3(pos.x, pos.y, pos.z), 128, 64, 128),
                    v -> v.getVillagerId() == vr.getVillagerId()
            ).isEmpty();

            if (entityExists) continue;

            // Spawn the villager
            spawnVillagerFromRecord(serverLevel, vr, culture);
        }
    }

    /**
     * Spawn a MillVillager entity from a VillagerRecord.
     */
    private void spawnVillagerFromRecord(ServerLevel level, VillagerRecord vr, @Nullable Culture culture) {
        if (pos == null) return;

        // Determine entity type based on gender and culture
        VillagerType vtype = null;
        if (culture != null && vr.type != null) {
            vtype = culture.getVillagerType(vr.type);
        }

        EntityType<? extends MillVillager> entityType;
        if (vr.gender == MillVillager.FEMALE) {
            // Use symmetrical female by default
            entityType = MillEntities.GENERIC_SYMM_FEMALE.get();
        } else {
            entityType = MillEntities.GENERIC_MALE.get();
        }

        MillVillager villager = entityType.create(level);
        if (villager == null) return;

        // Set position near the building
        Point spawnPos = vr.getHousePos() != null ? vr.getHousePos() : pos;
        villager.setPos(spawnPos.x + 0.5, spawnPos.y + 1.0, spawnPos.z + 0.5);

        // Populate from record
        villager.setVillagerId(vr.getVillagerId());
        if (vr.firstName != null) villager.setFirstName(vr.firstName);
        if (vr.familyName != null) villager.setFamilyName(vr.familyName);
        villager.setGender(vr.gender);
        if (cultureKey != null) villager.setCultureKey(cultureKey);
        if (vr.type != null) villager.setVillagerTypeKey(vr.type);
        villager.housePoint = vr.getHousePos();
        villager.townHallPoint = getTownHallPos();

        level.addFreshEntity(villager);
        MillLog.minor("Building", "Spawned villager: " + vr.firstName + " " + vr.familyName);
    }

    // ========== NBT persistence ==========

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("active", isActive);
        tag.putBoolean("townhall", isTownhall);
        tag.putBoolean("inn", isInn);
        tag.putBoolean("market", isMarket);
        tag.putBoolean("chestLocked", chestLocked);
        tag.putBoolean("hasAutoSpawn", hasAutoSpawn);
        tag.putBoolean("underAttack", underAttack);
        if (cultureKey != null) tag.putString("culture", cultureKey);
        if (name != null) tag.putString("name", name);
        tag.putString("qualifier", qualifier);

        if (pos != null) pos.writeToNBT(tag, "pos");
        if (townHallPos != null) townHallPos.writeToNBT(tag, "th");

        if (location != null) location.save(tag, "loc");

        if (controlledBy != null) {
            tag.putUUID("controlledBy", controlledBy);
            if (controlledByName != null) tag.putString("controlledByName", controlledByName);
        }

        // Save villager records
        ListTag vrList = new ListTag();
        for (VillagerRecord vr : vrecords.values()) {
            CompoundTag vrTag = new CompoundTag();
            vrTag.putLong("id", vr.getVillagerId());
            vrTag.putInt("gender", vr.gender);
            if (vr.firstName != null) vrTag.putString("firstName", vr.firstName);
            if (vr.familyName != null) vrTag.putString("familyName", vr.familyName);
            if (vr.type != null) vrTag.putString("type", vr.type);
            if (vr.getCultureKey() != null) vrTag.putString("culture", vr.getCultureKey());
            vrTag.putBoolean("killed", vr.killed);
            vrTag.putBoolean("awayraiding", vr.awayraiding);
            vrTag.putBoolean("awayhired", vr.awayhired);
            vrTag.putFloat("scale", vr.scale);
            if (vr.getHousePos() != null) vr.getHousePos().writeToNBT(vrTag, "house");
            if (vr.getTownHallPos() != null) vr.getTownHallPos().writeToNBT(vrTag, "th");
            vrList.add(vrTag);
        }
        tag.put("villagers", vrList);

        // Save relations
        ListTag relList = new ListTag();
        for (var entry : relations.entrySet()) {
            CompoundTag relTag = new CompoundTag();
            entry.getKey().writeToNBT(relTag, "p");
            relTag.putInt("val", entry.getValue());
            relList.add(relTag);
        }
        tag.put("relations", relList);

        return tag;
    }

    public static Building load(CompoundTag tag) {
        Building b = new Building();
        b.isActive = tag.getBoolean("active");
        b.isTownhall = tag.getBoolean("townhall");
        b.isInn = tag.getBoolean("inn");
        b.isMarket = tag.getBoolean("market");
        b.chestLocked = tag.getBoolean("chestLocked");
        b.hasAutoSpawn = tag.getBoolean("hasAutoSpawn");
        b.underAttack = tag.getBoolean("underAttack");
        if (tag.contains("culture")) b.cultureKey = tag.getString("culture");
        if (tag.contains("name")) b.name = tag.getString("name");
        b.qualifier = tag.getString("qualifier");

        b.pos = Point.readFromNBT(tag, "pos");
        b.townHallPos = Point.readFromNBT(tag, "th");

        b.location = BuildingLocation.read(tag, "loc");

        if (tag.hasUUID("controlledBy")) {
            b.controlledBy = tag.getUUID("controlledBy");
            if (tag.contains("controlledByName")) b.controlledByName = tag.getString("controlledByName");
        }

        // Load villager records
        if (tag.contains("villagers", Tag.TAG_LIST)) {
            ListTag vrList = tag.getList("villagers", Tag.TAG_COMPOUND);
            for (int i = 0; i < vrList.size(); i++) {
                CompoundTag vrTag = vrList.getCompound(i);
                VillagerRecord vr = new VillagerRecord();
                vr.setVillagerId(vrTag.getLong("id"));
                vr.gender = vrTag.getInt("gender");
                if (vrTag.contains("firstName")) vr.firstName = vrTag.getString("firstName");
                if (vrTag.contains("familyName")) vr.familyName = vrTag.getString("familyName");
                if (vrTag.contains("type")) vr.type = vrTag.getString("type");
                if (vrTag.contains("culture")) vr.setCultureKey(vrTag.getString("culture"));
                vr.killed = vrTag.getBoolean("killed");
                vr.awayraiding = vrTag.getBoolean("awayraiding");
                vr.awayhired = vrTag.getBoolean("awayhired");
                vr.scale = vrTag.getFloat("scale");
                vr.setHousePos(Point.readFromNBT(vrTag, "house"));
                vr.setTownHallPos(Point.readFromNBT(vrTag, "th"));
                b.addVillagerRecord(vr);
            }
        }

        // Load relations
        if (tag.contains("relations", Tag.TAG_LIST)) {
            ListTag relList = tag.getList("relations", Tag.TAG_COMPOUND);
            for (int i = 0; i < relList.size(); i++) {
                CompoundTag relTag = relList.getCompound(i);
                Point p = Point.readFromNBT(relTag, "p");
                if (p != null) {
                    b.relations.put(p, relTag.getInt("val"));
                }
            }
        }

        return b;
    }
}
