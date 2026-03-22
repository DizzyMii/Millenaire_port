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

    // ========== NBT key constants ==========
    private static final String NBT_ACTIVE = "active";
    private static final String NBT_TOWNHALL = "townhall";
    private static final String NBT_INN = "inn";
    private static final String NBT_MARKET = "market";
    private static final String NBT_CHEST_LOCKED = "chestLocked";
    private static final String NBT_HAS_AUTO_SPAWN = "hasAutoSpawn";
    private static final String NBT_UNDER_ATTACK = "underAttack";
    private static final String NBT_CULTURE = "culture";
    private static final String NBT_PLAN_SET_KEY = "planSetKey";
    private static final String NBT_VILLAGE_TYPE_KEY = "villageTypeKey";
    private static final String NBT_BUILDING_LEVEL = "buildingLevel";
    private static final String NBT_NAME = "name";
    private static final String NBT_QUALIFIER = "qualifier";
    private static final String NBT_POS = "pos";
    private static final String NBT_TH = "th";
    private static final String NBT_LOC = "loc";
    private static final String NBT_CONTROLLED_BY = "controlledBy";
    private static final String NBT_CONTROLLED_BY_NAME = "controlledByName";
    private static final String NBT_RAID_TARGET = "raidTarget";
    private static final String NBT_ACTIVE_RAID_START_TICK = "activeRaidStartTick";
    private static final String NBT_LAST_RAID_GAME_TIME = "lastRaidGameTime";
    private static final String NBT_RAIDS_PERFORMED = "raidsPerformed";
    private static final String NBT_RAIDS_SUFFERED = "raidsSuffered";
    private static final String NBT_VILLAGERS = "villagers";
    private static final String NBT_RELATIONS = "relations";
    private static final String NBT_VR_ID = "id";
    private static final String NBT_VR_GENDER = "gender";
    private static final String NBT_VR_FIRST_NAME = "firstName";
    private static final String NBT_VR_FAMILY_NAME = "familyName";
    private static final String NBT_VR_TYPE = "type";
    private static final String NBT_VR_KILLED = "killed";
    private static final String NBT_VR_AWAY_RAIDING = "awayraiding";
    private static final String NBT_VR_AWAY_HIRED = "awayhired";
    private static final String NBT_VR_SCALE = "scale";
    private static final String NBT_VR_HOUSE = "house";
    private static final String NBT_REL_POINT = "p";
    private static final String NBT_REL_VAL = "val";

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
    public long activeRaidStartTick = -1L;
    public long lastRaidGameTime = -1L;

    @Nullable public UUID controlledBy = null;
    @Nullable public String controlledByName = null;

    @Nullable private MillWorldData mw;
    @Nullable private Level world;

    // ========== Context accessors ==========

    @Nullable public Level getLevel() { return world; }
    @Nullable public MillWorldData getWorldData() { return mw; }

    public void setLevelContext(@Nullable Level level, @Nullable MillWorldData worldData) {
        this.world = level;
        this.mw = worldData;
    }

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

    // ========== Trade execution ==========

    /**
     * Player buys an item from this building's shop.
     * @return true if the trade was successful
     */
    public boolean executeBuy(org.dizzymii.millenaire2.world.UserProfile profile, TradeGood good, int quantity) {
        if (good.buyPrice <= 0 || good.item.isEmpty()) return false;
        Point thPos = getTownHallPos() != null ? getTownHallPos() : getPos();
        int rep = thPos != null ? profile.getVillageReputation(thPos) : 0;
        int unitPrice = good.getAdjustedBuyPrice(rep);
        int totalCost = unitPrice * quantity;

        if (profile.deniers < totalCost) return false;

        // Check building has stock (via resManager)
        org.dizzymii.millenaire2.item.InvItem invItem = org.dizzymii.millenaire2.item.InvItem.fromItemStack(good.item);
        if (invItem != null) {
            int stock = resManager.countGoods(invItem);
            if (stock < quantity) return false;
            resManager.takeGoods(invItem, quantity);
        }

        profile.deniers -= totalCost;
        if (thPos != null) profile.adjustVillageReputation(thPos, 1);
        if (mw != null) mw.setDirty();
        return true;
    }

    /**
     * Player sells an item to this building's shop.
     * @return true if the trade was successful
     */
    public boolean executeSell(org.dizzymii.millenaire2.world.UserProfile profile, TradeGood good, int quantity) {
        if (good.sellPrice <= 0 || good.item.isEmpty()) return false;
        Point thPos = getTownHallPos() != null ? getTownHallPos() : getPos();
        int rep = thPos != null ? profile.getVillageReputation(thPos) : 0;
        int unitPrice = good.getAdjustedSellPrice(rep);
        int totalEarning = unitPrice * quantity;

        // Store goods in building inventory
        org.dizzymii.millenaire2.item.InvItem invItem = org.dizzymii.millenaire2.item.InvItem.fromItemStack(good.item);
        if (invItem != null) {
            resManager.storeGoods(invItem, quantity);
        }

        profile.deniers += totalEarning;
        if (thPos != null) profile.adjustVillageReputation(thPos, 1);
        if (mw != null) mw.setDirty();
        return true;
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
                    org.dizzymii.millenaire2.MillConfig.constructionBlocksPerTick());
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

        // Raid lifecycle update (resolve active raid, release raiders on completion)
        if (isActive && isTownhall && mw != null && raidTarget != null && tickCounter % 20 == 0) {
            DiplomacyManager.updateRaidState(this, mw);
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

        // Gate: no concurrent construction in village
        for (Building b : mw.getBuildingsMap().values()) {
            if (isSameVillage(b) && b.isUnderConstruction()) return;
        }

        // Gate: minimum population before expanding
        int population = 0;
        for (Building b : mw.getBuildingsMap().values()) {
            if (isSameVillage(b)) population += b.getVillagerRecords().size();
        }
        if (population < 2) return;

        // 1. Try to upgrade existing buildings that are idle
        for (Building b : mw.getBuildingsMap().values()) {
            if (b == this) continue;
            if (!isSameVillage(b)) continue;
            if (b.canUpgrade()) {
                if (b.tryUpgrade()) {
                    MillLog.minor("Building", "Village expansion: upgrading " + b.getName());
                    return;
                }
            }
        }

        // 2. Try to build new buildings from village type definition
        if (villageTypeKey == null) return;
        VillageType vtype = culture.villageTypes.get(villageTypeKey);
        if (vtype == null) vtype = culture.loneBuildingTypes.get(villageTypeKey);
        if (vtype == null) return;

        java.util.Set<String> builtPlanSets = new java.util.HashSet<>();
        for (Building b : mw.getBuildingsMap().values()) {
            if (!isSameVillage(b)) continue;
            if (b.planSetKey != null) builtPlanSets.add(b.planSetKey);
        }

        String needed = findNeededBuilding(vtype.coreBuildings, builtPlanSets, culture);
        if (needed == null) {
            needed = findNeededBuilding(vtype.secondaryBuildings, builtPlanSets, culture);
        }

        if (needed != null) {
            startNewBuilding(needed, culture);
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

        // Spiral search for a valid site near the townhall
        Point site = findBuildingSite(serverLevel, initialPlan.width, initialPlan.length);
        if (site == null) {
            MillLog.minor("Building", "Village expansion: no valid site found for " + newPlanSetKey);
            return;
        }

        Building newBuilding = new Building();
        newBuilding.cultureKey = cultureKey;
        newBuilding.planSetKey = newPlanSetKey;
        newBuilding.villageTypeKey = villageTypeKey;
        newBuilding.buildingLevel = 0;
        newBuilding.isActive = true;
        newBuilding.setPos(site);
        newBuilding.setTownHallPos(pos);
        newBuilding.setName(planSet.name != null ? planSet.name : newPlanSetKey);
        newBuilding.setLevelContext(world, mw);

        BuildingLocation loc = new BuildingLocation();
        loc.planKey = newPlanSetKey;
        loc.cultureKey = cultureKey;
        loc.pos = site;
        loc.width = initialPlan.width;
        loc.length = initialPlan.length;
        newBuilding.location = loc;

        ConstructionIP cip = ConstructionIP.fromBuildingPlan(initialPlan, site, serverLevel);
        if (cip != null) {
            cip.location = loc;
            newBuilding.currentConstruction = cip;
            mw.addBuilding(newBuilding, site);
            mw.setDirty();
            MillLog.minor("Building", "Village expansion: new building " + newPlanSetKey + " at " + site);
        }
    }

    @Nullable
    private Point findBuildingSite(ServerLevel level, int buildWidth, int buildLength) {
        if (pos == null) return null;
        int minDist = 15;
        int maxDist = 60;
        int step = 5;

        for (int dist = minDist; dist <= maxDist; dist += step) {
            for (int angle = 0; angle < 8; angle++) {
                double rad = angle * Math.PI / 4.0;
                int cx = pos.x + (int)(Math.cos(rad) * dist);
                int cz = pos.z + (int)(Math.sin(rad) * dist);

                // Check overlap with existing buildings
                boolean overlaps = false;
                for (Building b : mw.getBuildingsMap().values()) {
                    if (b.getPos() != null && isSameVillage(b)) {
                        double d = new Point(cx, 0, cz).horizontalDistanceTo(b.getPos());
                        if (d < 12) { overlaps = true; break; }
                    }
                }
                if (overlaps) continue;

                // Find ground level
                net.minecraft.core.BlockPos check = new net.minecraft.core.BlockPos(cx, 0, cz);
                int groundY = findGround(level, check);
                if (groundY < 0) continue;
                if (Math.abs(groundY - pos.y) > 8) continue;

                return new Point(cx, groundY, cz);
            }
        }
        return null;
    }

    private static int findGround(ServerLevel level, net.minecraft.core.BlockPos pos) {
        for (int y = level.getMaxBuildHeight() - 1; y > level.getMinBuildHeight(); y--) {
            net.minecraft.core.BlockPos check = new net.minecraft.core.BlockPos(pos.getX(), y, pos.getZ());
            net.minecraft.world.level.block.state.BlockState state = level.getBlockState(check);
            if (!state.isAir() && !state.is(net.minecraft.world.level.block.Blocks.WATER) && !state.canBeReplaced()) {
                return y + 1;
            }
        }
        return -1;
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
        villager.setHousePoint(vr.getHousePos());
        villager.setTownHallPoint(getTownHallPos());

        level.addFreshEntity(villager);
        MillLog.minor("Building", "Spawned villager: " + vr.firstName + " " + vr.familyName);
    }

    // ========== NBT persistence ==========

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(NBT_ACTIVE, isActive);
        tag.putBoolean(NBT_TOWNHALL, isTownhall);
        tag.putBoolean(NBT_INN, isInn);
        tag.putBoolean(NBT_MARKET, isMarket);
        tag.putBoolean(NBT_CHEST_LOCKED, chestLocked);
        tag.putBoolean(NBT_HAS_AUTO_SPAWN, hasAutoSpawn);
        tag.putBoolean(NBT_UNDER_ATTACK, underAttack);
        if (cultureKey != null) tag.putString(NBT_CULTURE, cultureKey);
        if (planSetKey != null) tag.putString(NBT_PLAN_SET_KEY, planSetKey);
        if (villageTypeKey != null) tag.putString(NBT_VILLAGE_TYPE_KEY, villageTypeKey);
        tag.putInt(NBT_BUILDING_LEVEL, buildingLevel);
        if (name != null) tag.putString(NBT_NAME, name);
        tag.putString(NBT_QUALIFIER, qualifier);

        if (pos != null) pos.writeToNBT(tag, NBT_POS);
        if (townHallPos != null) townHallPos.writeToNBT(tag, NBT_TH);

        if (location != null) location.save(tag, NBT_LOC);

        if (controlledBy != null) {
            tag.putUUID(NBT_CONTROLLED_BY, controlledBy);
            if (controlledByName != null) tag.putString(NBT_CONTROLLED_BY_NAME, controlledByName);
        }

        if (raidTarget != null) {
            raidTarget.writeToNBT(tag, NBT_RAID_TARGET);
        }
        tag.putLong(NBT_ACTIVE_RAID_START_TICK, activeRaidStartTick);
        tag.putLong(NBT_LAST_RAID_GAME_TIME, lastRaidGameTime);

        ListTag raidsPerformedTag = new ListTag();
        for (String name : raidsPerformed) {
            CompoundTag rt = new CompoundTag();
            rt.putString(NBT_NAME, name);
            raidsPerformedTag.add(rt);
        }
        tag.put(NBT_RAIDS_PERFORMED, raidsPerformedTag);

        ListTag raidsSufferedTag = new ListTag();
        for (String name : raidsSuffered) {
            CompoundTag rt = new CompoundTag();
            rt.putString(NBT_NAME, name);
            raidsSufferedTag.add(rt);
        }
        tag.put(NBT_RAIDS_SUFFERED, raidsSufferedTag);

        // Save villager records
        ListTag vrList = new ListTag();
        for (VillagerRecord vr : vrecords.values()) {
            CompoundTag vrTag = new CompoundTag();
            vrTag.putLong(NBT_VR_ID, vr.getVillagerId());
            vrTag.putInt(NBT_VR_GENDER, vr.gender);
            if (vr.firstName != null) vrTag.putString(NBT_VR_FIRST_NAME, vr.firstName);
            if (vr.familyName != null) vrTag.putString(NBT_VR_FAMILY_NAME, vr.familyName);
            if (vr.type != null) vrTag.putString(NBT_VR_TYPE, vr.type);
            if (vr.getCultureKey() != null) vrTag.putString(NBT_CULTURE, vr.getCultureKey());
            vrTag.putBoolean(NBT_VR_KILLED, vr.killed);
            vrTag.putBoolean(NBT_VR_AWAY_RAIDING, vr.awayraiding);
            vrTag.putBoolean(NBT_VR_AWAY_HIRED, vr.awayhired);
            vrTag.putFloat(NBT_VR_SCALE, vr.scale);
            if (vr.getHousePos() != null) vr.getHousePos().writeToNBT(vrTag, NBT_VR_HOUSE);
            if (vr.getTownHallPos() != null) vr.getTownHallPos().writeToNBT(vrTag, NBT_TH);
            vrList.add(vrTag);
        }
        tag.put(NBT_VILLAGERS, vrList);

        // Save relations
        ListTag relList = new ListTag();
        for (var entry : relations.entrySet()) {
            CompoundTag relTag = new CompoundTag();
            entry.getKey().writeToNBT(relTag, NBT_REL_POINT);
            relTag.putInt(NBT_REL_VAL, entry.getValue());
            relList.add(relTag);
        }
        tag.put(NBT_RELATIONS, relList);

        return tag;
    }

    public static Building load(CompoundTag tag) {
        Building b = new Building();
        b.isActive = tag.getBoolean(NBT_ACTIVE);
        b.isTownhall = tag.getBoolean(NBT_TOWNHALL);
        b.isInn = tag.getBoolean(NBT_INN);
        b.isMarket = tag.getBoolean(NBT_MARKET);
        b.chestLocked = tag.getBoolean(NBT_CHEST_LOCKED);
        b.hasAutoSpawn = tag.getBoolean(NBT_HAS_AUTO_SPAWN);
        b.underAttack = tag.getBoolean(NBT_UNDER_ATTACK);
        if (tag.contains(NBT_CULTURE)) b.cultureKey = tag.getString(NBT_CULTURE);
        if (tag.contains(NBT_PLAN_SET_KEY)) b.planSetKey = tag.getString(NBT_PLAN_SET_KEY);
        if (tag.contains(NBT_VILLAGE_TYPE_KEY)) b.villageTypeKey = tag.getString(NBT_VILLAGE_TYPE_KEY);
        b.buildingLevel = tag.getInt(NBT_BUILDING_LEVEL);
        if (tag.contains(NBT_NAME)) b.name = tag.getString(NBT_NAME);
        b.qualifier = tag.getString(NBT_QUALIFIER);

        b.pos = Point.readFromNBT(tag, NBT_POS);
        b.townHallPos = Point.readFromNBT(tag, NBT_TH);

        b.location = BuildingLocation.read(tag, NBT_LOC);

        if (tag.hasUUID(NBT_CONTROLLED_BY)) {
            b.controlledBy = tag.getUUID(NBT_CONTROLLED_BY);
            if (tag.contains(NBT_CONTROLLED_BY_NAME)) b.controlledByName = tag.getString(NBT_CONTROLLED_BY_NAME);
        }

        b.raidTarget = Point.readFromNBT(tag, NBT_RAID_TARGET);
        b.activeRaidStartTick = tag.contains(NBT_ACTIVE_RAID_START_TICK) ? tag.getLong(NBT_ACTIVE_RAID_START_TICK) : -1L;
        b.lastRaidGameTime = tag.contains(NBT_LAST_RAID_GAME_TIME) ? tag.getLong(NBT_LAST_RAID_GAME_TIME) : -1L;

        if (tag.contains(NBT_RAIDS_PERFORMED, Tag.TAG_LIST)) {
            ListTag rp = tag.getList(NBT_RAIDS_PERFORMED, Tag.TAG_COMPOUND);
            for (int i = 0; i < rp.size(); i++) {
                b.raidsPerformed.add(rp.getCompound(i).getString(NBT_NAME));
            }
        }
        if (tag.contains(NBT_RAIDS_SUFFERED, Tag.TAG_LIST)) {
            ListTag rs = tag.getList(NBT_RAIDS_SUFFERED, Tag.TAG_COMPOUND);
            for (int i = 0; i < rs.size(); i++) {
                b.raidsSuffered.add(rs.getCompound(i).getString(NBT_NAME));
            }
        }

        // Load villager records
        if (tag.contains(NBT_VILLAGERS, Tag.TAG_LIST)) {
            ListTag vrList = tag.getList(NBT_VILLAGERS, Tag.TAG_COMPOUND);
            for (int i = 0; i < vrList.size(); i++) {
                CompoundTag vrTag = vrList.getCompound(i);
                VillagerRecord vr = new VillagerRecord();
                vr.setVillagerId(vrTag.getLong(NBT_VR_ID));
                vr.gender = vrTag.getInt(NBT_VR_GENDER);
                if (vrTag.contains(NBT_VR_FIRST_NAME)) vr.firstName = vrTag.getString(NBT_VR_FIRST_NAME);
                if (vrTag.contains(NBT_VR_FAMILY_NAME)) vr.familyName = vrTag.getString(NBT_VR_FAMILY_NAME);
                if (vrTag.contains(NBT_VR_TYPE)) vr.type = vrTag.getString(NBT_VR_TYPE);
                if (vrTag.contains(NBT_CULTURE)) vr.setCultureKey(vrTag.getString(NBT_CULTURE));
                vr.killed = vrTag.getBoolean(NBT_VR_KILLED);
                vr.awayraiding = vrTag.getBoolean(NBT_VR_AWAY_RAIDING);
                vr.awayhired = vrTag.getBoolean(NBT_VR_AWAY_HIRED);
                vr.scale = vrTag.getFloat(NBT_VR_SCALE);
                vr.setHousePos(Point.readFromNBT(vrTag, NBT_VR_HOUSE));
                vr.setTownHallPos(Point.readFromNBT(vrTag, NBT_TH));
                b.addVillagerRecord(vr);
            }
        }

        // Load relations
        if (tag.contains(NBT_RELATIONS, Tag.TAG_LIST)) {
            ListTag relList = tag.getList(NBT_RELATIONS, Tag.TAG_COMPOUND);
            for (int i = 0; i < relList.size(); i++) {
                CompoundTag relTag = relList.getCompound(i);
                Point p = Point.readFromNBT(relTag, NBT_REL_POINT);
                if (p != null) {
                    b.relations.put(p, relTag.getInt(NBT_REL_VAL));
                }
            }
        }

        return b;
    }
}
