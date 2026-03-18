package org.dizzymii.millenaire2.village;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.culture.BuildingPlan;
import org.dizzymii.millenaire2.culture.BuildingPlanSet;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.culture.VillageType;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.item.TradeGood;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
    @Nullable public String planSetKey;
    @Nullable public String villageTypeKey;
    public int buildingLevel = 0;
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

    private final Set<MillVillager> villagers = new LinkedHashSet<>();
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

    // ========== Trade goods ==========

    public final List<TradeGood> tradeGoods = new ArrayList<>();

    /**
     * Get trade goods for this building. If the static list is empty,
     * resolve from the data-driven TradeGoodLoader based on villager types present.
     */
    public List<TradeGood> getTradeGoods() {
        if (!tradeGoods.isEmpty()) return tradeGoods;

        // Resolve from data-driven trade goods based on villager types in this building
        java.util.Set<String> vtypes = new java.util.HashSet<>();
        for (VillagerRecord vr : getVillagerRecords()) {
            if (vr.type != null) vtypes.add(vr.type);
        }
        List<TradeGood> resolved = new ArrayList<>();
        for (String vt : vtypes) {
            resolved.addAll(org.dizzymii.millenaire2.item.TradeGoodLoader.getTradeGoods(vt));
        }
        return resolved.isEmpty() ? tradeGoods : resolved;
    }

    // ========== Upgrade ==========

    /**
     * Attempt to upgrade this building to the next level.
     * Resolves the BuildingPlanSet from the culture and starts construction for the next level.
     * @return true if upgrade was started
     */
    public boolean tryUpgrade() {
        if (isUnderConstruction()) return false;
        if (cultureKey == null || planSetKey == null) return false;

        Culture culture = Culture.getCultureByName(cultureKey);
        if (culture == null) return false;

        BuildingPlanSet planSet = culture.planSets.get(planSetKey);
        if (planSet == null) return false;

        int nextLevel = buildingLevel + 1;
        BuildingPlan nextPlan = planSet.getPlan(nextLevel);
        if (nextPlan == null) return false;

        // Place all upgrade blocks instantly
        if (pos != null && nextPlan.hasImage() && world instanceof ServerLevel serverLevel) {
            ConstructionIP cip = ConstructionIP.fromBuildingPlan(nextPlan, pos, serverLevel);
            if (cip != null) {
                int placed = cip.placeBlocks(serverLevel, Integer.MAX_VALUE);
                buildingLevel = nextLevel;
                if (mw != null) mw.setDirty();
                MillLog.minor("Building", "Upgraded to level " + nextLevel + ": placed " + placed + " blocks for " + name);
                return true;
            }
        }
        return false;
    }

    /**
     * Check if this building has more upgrade levels available.
     */
    public boolean canUpgrade() {
        if (isUnderConstruction()) return false;
        if (cultureKey == null || planSetKey == null) return false;
        Culture culture = Culture.getCultureByName(cultureKey);
        if (culture == null) return false;
        BuildingPlanSet planSet = culture.planSets.get(planSetKey);
        if (planSet == null) return false;
        return planSet.getPlan(buildingLevel + 1) != null;
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

        // Progress construction (config-driven blocks per slow tick)
        if (currentConstruction != null && !currentConstruction.isComplete()
                && world instanceof ServerLevel serverLevel) {
            int placed = currentConstruction.placeBlocks(serverLevel,
                    org.dizzymii.millenaire2.MillConfig.constructionBlocksPerTick);
            if (placed > 0 && mw != null) mw.setDirty();
            if (currentConstruction.isComplete()) {
                MillLog.minor("Building", "Construction complete for: " + name);
                currentConstruction = null;
                if (mw != null) mw.setDirty();
            }
        }

        // Spawn missing villagers if this is an active townhall or building
        if (isActive && isTownhall && tickCounter % 200 == 0) {
            checkAndSpawnVillagers();
        }

        // Village expansion: check for upgrades every ~60 seconds (1200 ticks)
        if (isActive && isTownhall && !isUnderConstruction() && tickCounter % 1200 == 0) {
            checkVillageExpansion();
        }

        // Resource production every ~10 seconds (200 ticks)
        if (isActive && tickCounter % 200 == 0) {
            tickResourceProduction();
        }

        // Diplomacy: raid check on townhalls (configurable interval)
        if (isActive && isTownhall && mw != null
                && tickCounter % DiplomacyManager.raidCheckIntervalTicks == 0) {
            DiplomacyManager.checkRaidTrigger(this, mw);
        }

        // Diplomacy: relation decay every ~60 minutes (72000 ticks)
        if (isActive && isTownhall && tickCounter % 72000 == 0) {
            DiplomacyManager.tickRelationDecay(this);
        }
    }

    // ========== Village expansion ==========

    /**
     * Townhall checks all buildings in the village for possible upgrades,
     * then checks if new buildings from the VillageType should be constructed.
     */
    private void checkVillageExpansion() {
        if (mw == null || cultureKey == null) return;
        Culture culture = Culture.getCultureByName(cultureKey);
        if (culture == null) return;

        // 1. Try to upgrade existing buildings that are idle
        for (Building b : mw.getBuildingsMap().values()) {
            if (b == this) continue;
            if (!isSameVillage(b)) continue;
            if (b.isUnderConstruction()) continue;
            if (b.canUpgrade()) {
                if (b.tryUpgrade()) {
                    MillLog.minor("Building", "Village expansion: upgrading " + b.getName());
                    return; // One upgrade per cycle
                }
            }
        }

        // 2. Try to build new buildings from village type definition
        if (villageTypeKey == null) return;
        VillageType vtype = culture.villageTypes.get(villageTypeKey);
        if (vtype == null) vtype = culture.loneBuildingTypes.get(villageTypeKey);
        if (vtype == null) return;

        // Collect what plan sets are already built in this village
        java.util.Set<String> builtPlanSets = new java.util.HashSet<>();
        for (Building b : mw.getBuildingsMap().values()) {
            if (!isSameVillage(b)) continue;
            if (b.planSetKey != null) builtPlanSets.add(b.planSetKey);
        }

        // Check core buildings first, then secondary
        String needed = findNeededBuilding(vtype.coreBuildings, builtPlanSets, culture);
        if (needed == null) {
            needed = findNeededBuilding(vtype.secondaryBuildings, builtPlanSets, culture);
        }

        if (needed != null) {
            startNewBuilding(needed, culture);
            return;
        }

        // 3. Try to build walls if culture has wall types and village is large enough
        if (builtPlanSets.size() >= 3 && !culture.wallTypes.isEmpty()) {
            tryBuildWallSegment(culture, builtPlanSets);
        }
    }

    @Nullable
    private String findNeededBuilding(List<String> candidates, java.util.Set<String> alreadyBuilt, Culture culture) {
        for (String planSetKey : candidates) {
            if (alreadyBuilt.contains(planSetKey)) continue;
            BuildingPlanSet planSet = culture.planSets.get(planSetKey);
            if (planSet != null && planSet.getInitialPlan() != null) {
                return planSetKey;
            }
        }
        return null;
    }

    /**
     * Start construction of a new building near the townhall.
     */
    private void startNewBuilding(String newPlanSetKey, Culture culture) {
        if (pos == null || !(world instanceof ServerLevel serverLevel)) return;
        BuildingPlanSet planSet = culture.planSets.get(newPlanSetKey);
        if (planSet == null) return;
        BuildingPlan initialPlan = planSet.getInitialPlan();
        if (initialPlan == null || !initialPlan.hasImage()) return;

        // Find a position near the townhall (offset by existing building count)
        int buildingCount = 0;
        for (Building b : mw.getBuildingsMap().values()) {
            if (isSameVillage(b)) buildingCount++;
        }
        int offsetX = ((buildingCount % 4) - 1) * 20;
        int offsetZ = ((buildingCount / 4) + 1) * 20;
        Point newPos = new Point(pos.x + offsetX, pos.y, pos.z + offsetZ);

        // Create the building
        Building newBuilding = new Building();
        newBuilding.cultureKey = cultureKey;
        newBuilding.planSetKey = newPlanSetKey;
        newBuilding.villageTypeKey = villageTypeKey;
        newBuilding.buildingLevel = 0;
        newBuilding.isActive = true;
        newBuilding.setPos(newPos);
        newBuilding.setTownHallPos(pos);
        newBuilding.setName(planSet.name != null ? planSet.name : newPlanSetKey);
        newBuilding.mw = mw;
        newBuilding.world = world;

        // Place all blocks instantly
        ConstructionIP cip = ConstructionIP.fromBuildingPlan(initialPlan, newPos, serverLevel);
        if (cip != null) {
            int placed = cip.placeBlocks(serverLevel, Integer.MAX_VALUE);
            mw.addBuilding(newBuilding, newPos);
            mw.setDirty();
            MillLog.minor("Building", "Village expansion: placed " + placed + " blocks for " + newPlanSetKey + " at " + newPos);
        }
    }

    /**
     * Attempt to place a wall segment around the village perimeter.
     * Picks the first WallType from the culture and places blocks in a ring.
     */
    private void tryBuildWallSegment(Culture culture, java.util.Set<String> builtPlanSets) {
        if (pos == null || !(world instanceof ServerLevel serverLevel)) return;

        // Check if wall plan sets exist and are already built
        for (BuildingPlanSet ps : culture.planSets.values()) {
            if (ps.isWallSegment && !builtPlanSets.contains(ps.key)) {
                BuildingPlan wallPlan = ps.getInitialPlan();
                if (wallPlan != null && wallPlan.hasImage()) {
                    startNewBuilding(ps.key, culture);
                    return;
                }
            }
        }

        // Fallback: place wall blocks directly using WallType data
        org.dizzymii.millenaire2.culture.WallType wallType =
                culture.wallTypes.values().stream().findFirst().orElse(null);
        if (wallType == null || wallType.wallBlocks.isEmpty()) return;

        // Resolve the wall block
        net.minecraft.resources.ResourceLocation blockId =
                net.minecraft.resources.ResourceLocation.tryParse(wallType.wallBlocks.get(0));
        if (blockId == null) return;
        net.minecraft.world.level.block.Block wallBlock =
                net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(blockId);
        if (wallBlock == net.minecraft.world.level.block.Blocks.AIR) return;
        net.minecraft.world.level.block.state.BlockState wallState = wallBlock.defaultBlockState();

        // Calculate village bounding box from buildings
        int minX = pos.x, maxX = pos.x, minZ = pos.z, maxZ = pos.z;
        for (Building b : mw.getBuildingsMap().values()) {
            if (!isSameVillage(b) || b.getPos() == null) continue;
            minX = Math.min(minX, b.getPos().x - 10);
            maxX = Math.max(maxX, b.getPos().x + 10);
            minZ = Math.min(minZ, b.getPos().z - 10);
            maxZ = Math.max(maxZ, b.getPos().z + 10);
        }

        // Place wall blocks along perimeter at ground level
        int y = pos.y;
        int placed = 0;
        for (int h = 0; h < wallType.height; h++) {
            for (int x = minX; x <= maxX; x++) {
                placeWallBlock(serverLevel, x, y + h, minZ, wallState);
                placeWallBlock(serverLevel, x, y + h, maxZ, wallState);
            }
            for (int z = minZ + 1; z < maxZ; z++) {
                placeWallBlock(serverLevel, minX, y + h, z, wallState);
                placeWallBlock(serverLevel, maxX, y + h, z, wallState);
            }
            placed++;
        }

        // Place top blocks if defined
        if (!wallType.topBlocks.isEmpty()) {
            net.minecraft.resources.ResourceLocation topId =
                    net.minecraft.resources.ResourceLocation.tryParse(wallType.topBlocks.get(0));
            if (topId != null) {
                net.minecraft.world.level.block.Block topBlock =
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(topId);
                if (topBlock != net.minecraft.world.level.block.Blocks.AIR) {
                    net.minecraft.world.level.block.state.BlockState topState = topBlock.defaultBlockState();
                    for (int x = minX; x <= maxX; x++) {
                        placeWallBlock(serverLevel, x, y + wallType.height, minZ, topState);
                        placeWallBlock(serverLevel, x, y + wallType.height, maxZ, topState);
                    }
                    for (int z = minZ + 1; z < maxZ; z++) {
                        placeWallBlock(serverLevel, minX, y + wallType.height, z, topState);
                        placeWallBlock(serverLevel, maxX, y + wallType.height, z, topState);
                    }
                }
            }
        }

        if (placed > 0) {
            MillLog.minor("Building", "Village wall construction: " + wallType.key + " around " + getName());
            mw.setDirty();
        }
    }

    private void placeWallBlock(ServerLevel level, int x, int y, int z,
                                net.minecraft.world.level.block.state.BlockState state) {
        net.minecraft.core.BlockPos bp = new net.minecraft.core.BlockPos(x, y, z);
        if (level.isEmptyBlock(bp) || level.getBlockState(bp).canBeReplaced()) {
            level.setBlock(bp, state, 3);
        }
    }

    /**
     * Check if another building belongs to the same village (same townhall pos).
     */
    boolean isSameVillage(Building other) {
        if (other == null) return false;
        Point myTh = isTownhall ? pos : getTownHallPos();
        Point otherTh = other.isTownhall ? other.getPos() : other.getTownHallPos();
        if (myTh == null || otherTh == null) return false;
        return myTh.equals(otherTh);
    }

    // ========== Resource production ==========

    /**
     * Produce resources based on building type using data-driven economy config.
     */
    private void tickResourceProduction() {
        // Only buildings with villagers produce resources
        if (vrecords.isEmpty()) return;

        if (VillageEconomyLoader.tickProduction(this) && mw != null) {
            mw.setDirty();
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
        if (planSetKey != null) tag.putString("planSetKey", planSetKey);
        if (villageTypeKey != null) tag.putString("villageTypeKey", villageTypeKey);
        tag.putInt("buildingLevel", buildingLevel);
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

        // Save active construction
        if (currentConstruction != null && !currentConstruction.isComplete()) {
            tag.put("construction", currentConstruction.save());
        }

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
        if (tag.contains("planSetKey")) b.planSetKey = tag.getString("planSetKey");
        if (tag.contains("villageTypeKey")) b.villageTypeKey = tag.getString("villageTypeKey");
        b.buildingLevel = tag.getInt("buildingLevel");
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

        // Load active construction
        if (tag.contains("construction", Tag.TAG_COMPOUND)) {
            b.currentConstruction = ConstructionIP.load(tag.getCompound("construction"));
        }

        return b;
    }
}
