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

    public boolean isPlayerControlled() { return controlledBy != null; }

    public boolean isControlledBy(UUID playerUUID) {
        return controlledBy != null && controlledBy.equals(playerUUID);
    }

    @Nullable public MillWorldData mw;
    @Nullable public Level world;

    @Nullable public VillageGeography geography;
    @Nullable public VillagePathPlanner pathPlanner;

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

    public List<String> getTags() {
        return location != null ? location.tags : List.of();
    }

    public List<String> getVillageTags() {
        return location != null ? location.villageTags : List.of();
    }

    public List<String> getClearTags() {
        return location != null ? location.clearTags : List.of();
    }

    public boolean hasTag(String tag) {
        String normalized = normalizeMetadataValue(tag);
        if (normalized.isEmpty()) {
            return false;
        }
        if (location != null) {
            for (String value : location.tags) {
                if (normalized.equals(normalizeMetadataValue(value))) {
                    return true;
                }
            }
        }
        return (BuildingTags.TAG_INN.equals(normalized) && isInn)
                || (BuildingTags.TAG_MARKET.equals(normalized) && isMarket)
                || (BuildingTags.TAG_AUTO_SPAWN_VILLAGERS.equals(normalized) && hasAutoSpawn);
    }

    public boolean hasShop(String shopKey) {
        return location != null && location.shop != null
                && normalizeMetadataValue(location.shop).equals(normalizeMetadataValue(shopKey));
    }

    @Nullable
    public Point getActivityPosition(String targetPosition) {
        if (location == null) {
            return pos;
        }
        Point resolved = switch (normalizeMetadataValue(targetPosition)) {
            case "sleep", "sleeping" -> location.sleepingPos;
            case "sell", "selling" -> location.sellingPos;
            case "craft", "crafting" -> location.craftingPos;
            case "shelter" -> location.shelterPos;
            case "defend", "defending" -> location.defendingPos;
            case "leasure", "leisure" -> location.leisurePos;
            case "chest", "mainchest", "lockedchest" -> location.chestPos;
            default -> location.pos;
        };
        return resolved != null ? resolved : pos;
    }

    public void applyPlanMetadata(@Nullable BuildingPlanSet planSet, @Nullable BuildingPlan plan) {
        BuildingLocation activeLocation = ensureLocation();
        ArrayList<String> combinedTags = new ArrayList<>();
        if (planSet != null) {
            activeLocation.planKey = planSet.key;
            combinedTags.addAll(planSet.tags);
        }
        if (plan != null) {
            combinedTags.addAll(plan.tags);
            activeLocation.width = plan.width;
            activeLocation.length = plan.length;
            activeLocation.level = buildingLevel;
            if (plan.priorityMoveIn > 0) {
                activeLocation.priorityMoveIn = plan.priorityMoveIn;
            }
            if (!plan.shops.isEmpty()) {
                activeLocation.shop = normalizeMetadataValue(plan.shops.get(0));
            }
        }
        replaceNormalized(activeLocation.tags, combinedTags);
        replaceNormalized(activeLocation.villageTags, plan != null ? plan.villageTags : List.of());
        replaceNormalized(activeLocation.clearTags, plan != null ? plan.clearTags : List.of());
        replaceNormalized(activeLocation.subBuildings, List.of());
        if (planSet != null) {
            appendNormalized(activeLocation.subBuildings, planSet.subBuildings);
        }
        if (plan != null) {
            appendNormalized(activeLocation.subBuildings, plan.subBuildings);
        }
        syncLegacyFlagsFromTags();
    }

    public void syncLegacyFlagsFromTags() {
        isInn = hasTag(BuildingTags.TAG_INN);
        isMarket = hasTag(BuildingTags.TAG_MARKET);
        hasAutoSpawn = hasTag(BuildingTags.TAG_AUTO_SPAWN_VILLAGERS);
    }

    private BuildingLocation ensureLocation() {
        if (location == null) {
            location = new BuildingLocation();
        }
        return location;
    }

    private static String normalizeMetadataValue(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private static void replaceNormalized(List<String> target, Collection<String> values) {
        target.clear();
        appendNormalized(target, values);
    }

    private static void appendNormalized(List<String> target, Collection<String> values) {
        for (String value : values) {
            String normalized = normalizeMetadataValue(value);
            if (!normalized.isEmpty() && !target.contains(normalized)) {
                target.add(normalized);
            }
        }
    }

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

        // Start construction from the plan
        if (pos != null && nextPlan.hasImage() && world instanceof ServerLevel serverLevel) {
            ConstructionIP cip = ConstructionIP.fromBuildingPlan(nextPlan, pos, serverLevel);
            if (cip != null) {
                currentConstruction = cip;
                buildingLevel = nextLevel;
                if (location != null) {
                    location.level = nextLevel;
                }
                applyPlanMetadata(planSet, nextPlan);
                org.dizzymii.millenaire2.world.WorldGenVillage.applyPlanSpecialPositions(this, nextPlan);
                if (mw != null) mw.setDirty();
                MillLog.minor("Building", "Started upgrade to level " + nextLevel + " for: " + name);
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

        // Path construction: place pending path blocks every slow tick (townhall only)
        if (isActive && isTownhall && world instanceof ServerLevel serverLvl) {
            tickPathConstruction(serverLvl);
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

    // ========== Path construction ==========

    /**
     * Place pending path blocks and trigger re-planning when needed.
     */
    private void tickPathConstruction(ServerLevel serverLevel) {
        // Re-plan paths every ~5 minutes (6000 ticks) or when planner is empty
        if (pathPlanner == null || (tickCounter % 6000 == 0 && !pathPlanner.hasPendingPaths())) {
            if (pathPlanner == null) pathPlanner = new VillagePathPlanner();
            updateGeography();
            pathPlanner.planPaths(this, geography, serverLevel);
        }

        // Place up to 2 path blocks per slow tick
        if (pathPlanner.hasPendingPaths()) {
            for (int i = 0; i < 2; i++) {
                Point p = pathPlanner.getNextPathBlock();
                if (p == null) break;
                if (VillagePathPlanner.placePathBlock(serverLevel, p)) {
                    pathPlanner.markPlaced(p);
                }
            }
        }
    }

    // ========== Village expansion ==========

    /**
     * Collect all BuildingLocations in this village for geography updates.
     */
    private List<BuildingLocation> collectVillageLocations() {
        List<BuildingLocation> locations = new ArrayList<>();
        if (location != null) locations.add(location);
        if (mw == null) return locations;
        for (Building b : mw.getBuildingsMap().values()) {
            if (b == this) continue;
            if (!isSameVillage(b)) continue;
            if (b.location != null) locations.add(b.location);
        }
        return locations;
    }

    /**
     * Ensure the VillageGeography is up-to-date. Called before building placement.
     */
    private void updateGeography() {
        if (pos == null || !(world instanceof ServerLevel serverLevel)) return;
        if (geography == null) geography = new VillageGeography();
        List<BuildingLocation> locations = collectVillageLocations();
        geography.update(serverLevel, locations, pos, 80);
    }

    /**
     * Townhall checks all buildings in the village for possible upgrades,
     * then checks if new buildings from the VillageType should be constructed.
     * Gated by: concurrent construction count, population, and resources.
     */
    private void checkVillageExpansion() {
        if (mw == null || cultureKey == null) return;
        Culture culture = Culture.getCultureByName(cultureKey);
        if (culture == null) return;

        if (villageTypeKey == null) return;
        VillageType vtype = culture.villageTypes.get(villageTypeKey);
        if (vtype == null) vtype = culture.loneBuildingTypes.get(villageTypeKey);
        if (vtype == null) return;

        // Gate: check concurrent construction limit
        int activeConstructions = countActiveConstructions();
        if (activeConstructions >= vtype.maxSimultaneousConstructions) return;

        // Gate: need at least 2 villagers before expanding beyond start buildings
        int villagerCount = countVillageVillagers();
        int buildingCount = countVillageBuildings();

        // Collect what plan sets are already built in this village (with counts)
        java.util.Map<String, Integer> builtPlanSetCounts = new java.util.HashMap<>();
        for (Building b : mw.getBuildingsMap().values()) {
            if (!isSameVillage(b)) continue;
            if (b.planSetKey != null) {
                builtPlanSetCounts.merge(b.planSetKey, 1, Integer::sum);
            }
        }
        java.util.Set<String> builtPlanSets = builtPlanSetCounts.keySet();

        // 1. Start buildings: always build these first if missing
        String needed = findNeededBuilding(vtype.startBuildings, builtPlanSets, culture);
        if (needed != null) {
            startNewBuilding(needed, culture);
            return;
        }

        // Gate: need at least 2 villagers for upgrades and further expansion
        if (villagerCount < 2 && buildingCount > 1) return;

        // 2. Try to upgrade existing buildings that are idle
        for (Building b : mw.getBuildingsMap().values()) {
            if (b == this) continue;
            if (!isSameVillage(b)) continue;
            if (b.isUnderConstruction()) continue;
            if (b.canUpgrade()) {
                if (b.tryUpgrade()) {
                    MillLog.minor("Building", "Village expansion: upgrading " + b.getName());
                    return; // One action per cycle
                }
            }
        }

        // 3. Core buildings: must all be present before secondary
        needed = findNeededBuilding(vtype.coreBuildings, builtPlanSets, culture);
        if (needed != null) {
            startNewBuilding(needed, culture);
            return;
        }

        // 4. Secondary buildings
        needed = findNeededBuilding(vtype.secondaryBuildings, builtPlanSets, culture);
        if (needed != null) {
            startNewBuilding(needed, culture);
        }
    }

    @Nullable
    private String findNeededBuilding(List<String> candidates, java.util.Set<String> alreadyBuilt, Culture culture) {
        for (String planSetKey : candidates) {
            if (alreadyBuilt.contains(planSetKey)) continue;
            // Skip excluded buildings
            BuildingPlanSet planSet = culture.planSets.get(planSetKey);
            if (planSet != null && planSet.getInitialPlan() != null) {
                return planSetKey;
            }
        }
        return null;
    }

    /**
     * Count buildings in this village currently under construction.
     */
    private int countActiveConstructions() {
        if (mw == null) return 0;
        int count = 0;
        for (Building b : mw.getBuildingsMap().values()) {
            if (!isSameVillage(b)) continue;
            if (b.isUnderConstruction()) count++;
        }
        return count;
    }

    /**
     * Count total villagers across all buildings in this village.
     */
    private int countVillageVillagers() {
        if (mw == null) return 0;
        int count = 0;
        for (Building b : mw.getBuildingsMap().values()) {
            if (!isSameVillage(b)) continue;
            count += b.vrecords.size();
        }
        return count;
    }

    /**
     * Count total buildings in this village.
     */
    private int countVillageBuildings() {
        if (mw == null) return 0;
        int count = 0;
        for (Building b : mw.getBuildingsMap().values()) {
            if (isSameVillage(b)) count++;
        }
        return count;
    }

    /**
     * Start construction of a new building near the townhall.
     * Uses VillageGeography to find a valid terrain location.
     */
    private void startNewBuilding(String newPlanSetKey, Culture culture) {
        if (pos == null || !(world instanceof ServerLevel serverLevel)) return;
        BuildingPlanSet planSet = culture.planSets.get(newPlanSetKey);
        if (planSet == null) return;
        BuildingPlan initialPlan = planSet.getInitialPlan();
        if (initialPlan == null || !initialPlan.hasImage()) return;

        // Update terrain grid and find a valid location
        updateGeography();
        int bldgLen = initialPlan.length > 0 ? initialPlan.length : 12;
        int bldgWid = initialPlan.width > 0 ? initialPlan.width : 12;

        Point newPos = null;
        if (geography != null) {
            newPos = geography.findBuildingLocation(
                    pos.x, pos.z, bldgLen, bldgWid, 8, 80, 3);
        }

        // Fallback to grid offset if geography search fails
        if (newPos == null) {
            int buildingCount = 0;
            if (mw != null) {
                for (Building b : mw.getBuildingsMap().values()) {
                    if (isSameVillage(b)) buildingCount++;
                }
            }
            int offsetX = ((buildingCount % 4) - 1) * 20;
            int offsetZ = ((buildingCount / 4) + 1) * 20;
            newPos = new Point(pos.x + offsetX, pos.y, pos.z + offsetZ);
            MillLog.minor("Building", "Geography search failed for " + newPlanSetKey + ", using grid offset");
        }

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
        newBuilding.location = new BuildingLocation();
        newBuilding.location.pos = newPos;
        newBuilding.location.cultureKey = cultureKey;
        newBuilding.location.orientation = 0;
        newBuilding.applyPlanMetadata(planSet, initialPlan);
        newBuilding.location.computeMargins();
        org.dizzymii.millenaire2.world.WorldGenVillage.applyPlanSpecialPositions(newBuilding, initialPlan);

        // Load blocks and compute resource cost
        java.util.List<org.dizzymii.millenaire2.buildingplan.BuildingBlock> planBlocks =
                org.dizzymii.millenaire2.world.WorldGenVillage.loadPlanBlocks(culture, planSet, initialPlan);
        if (!planBlocks.isEmpty()) {
            java.util.Map<String, Integer> cost = ResourceCostCalculator.computeCost(planBlocks);
            if (!cost.isEmpty() && !ResourceCostCalculator.hasResources(this, cost)) {
                MillLog.minor("Building", "Insufficient resources for " + newPlanSetKey
                        + ". Need: " + cost);
                return;
            }
            // Deduct resources
            if (!cost.isEmpty()) {
                ResourceCostCalculator.deductResources(this, cost);
                MillLog.minor("Building", "Deducted resources for " + newPlanSetKey + ": " + cost);
            }
        }

        // Start construction
        ConstructionIP cip = ConstructionIP.fromBuildingPlan(initialPlan, newPos, serverLevel);
        if (cip != null) {
            newBuilding.currentConstruction = cip;
            mw.addBuilding(newBuilding, newPos);
            mw.setDirty();

            // Register in geography to prevent overlap
            if (geography != null && newBuilding.location != null) {
                geography.registerNewBuilding(newBuilding.location);
            }

            MillLog.minor("Building", "Village expansion: new building " + newPlanSetKey + " at " + newPos);
        }
    }

    /**
     * Check if another building belongs to the same village (same townhall pos).
     */
    public boolean isSameVillage(Building other) {
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
        resManager.save(tag, "res.");

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
        if (tag.contains("planSetKey")) b.planSetKey = tag.getString("planSetKey");
        if (tag.contains("villageTypeKey")) b.villageTypeKey = tag.getString("villageTypeKey");
        b.buildingLevel = tag.getInt("buildingLevel");
        if (tag.contains("name")) b.name = tag.getString("name");
        b.qualifier = tag.getString("qualifier");

        b.pos = Point.readFromNBT(tag, "pos");
        b.townHallPos = Point.readFromNBT(tag, "th");

        b.location = BuildingLocation.read(tag, "loc");
        b.resManager.load(tag, "res.");
        b.syncLegacyFlagsFromTags();

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
