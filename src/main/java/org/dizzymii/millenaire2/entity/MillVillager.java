package org.dizzymii.millenaire2.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.SimpleContainer;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.ai.behavior.GoToHomeBuilding;
import org.dizzymii.millenaire2.entity.ai.behavior.MillGoalBehaviour;
import org.dizzymii.millenaire2.entity.ai.sensor.ThreatSensor;
import org.dizzymii.millenaire2.entity.ai.sensor.VillageSensor;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract base entity for all Millénaire villagers.
 * Ported from org.millenaire.common.entity.MillVillager (Forge 1.12.2).
 *
 * In NeoForge 1.21.1, EntityCreature → PathfinderMob.
 * IEntityAdditionalSpawnData is replaced by synched entity data or custom packets.
 */
public abstract class MillVillager extends PathfinderMob implements SmartBrainOwner<MillVillager> {

    // --- Synched data ---
    private static final EntityDataAccessor<String> DATA_FIRST_NAME =
            SynchedEntityData.defineId(MillVillager.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_FAMILY_NAME =
            SynchedEntityData.defineId(MillVillager.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_GENDER =
            SynchedEntityData.defineId(MillVillager.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_CULTURE =
            SynchedEntityData.defineId(MillVillager.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> DATA_GOAL_KEY =
            SynchedEntityData.defineId(MillVillager.class, EntityDataSerializers.STRING);

    // --- Constants ---
    public static final int MALE = 1;
    public static final int FEMALE = 2;
    public static final int ATTACK_RANGE = 80;
    public static final int ATTACK_RANGE_DEFENSIVE = 20;
    public static final int ARCHER_RANGE = 20;
    public static final int MAX_CHILD_SIZE = 20;
    private static final double DEFAULT_MOVE_SPEED = 0.5;

    // --- Instance fields (ported from original) ---
    @Nullable public VillagerType vtype;
    public int action = 0;
    @Nullable public String goalKey = null;
    @Nullable public Goal currentGoal = null;
    @Nullable private GoalInformation goalInformation = null;
    @Nullable private Point pathDestPoint;
    @Nullable public Point housePoint = null;
    @Nullable public Point prevPoint = null;
    @Nullable public Point townHallPoint = null;
    public boolean extraLog = false;
    public long timer = 0L;
    public long actionStart = 0L;
    public boolean allowRandomMoves = false;
    public boolean stopMoving = false;
    public boolean registered = false;
    public int longDistanceStuck;
    public boolean nightActionPerformed = false;
    public long speech_started = 0L;
    public long pathingTime;
    public int nbPathsCalculated = 0;
    public long goalStarted = 0L;
    public int constructionJobId = -1;
    public int heldItemCount = 0;
    @Nullable public String speech_key = null;
    public int speech_variant = 0;
    @Nullable public String dialogueKey = null;
    public int dialogueRole = 0;
    public long dialogueStart = 0L;
    public boolean dialogueChat = false;
    @Nullable public String dialogueTargetFirstName = null;
    @Nullable public String dialogueTargetLastName = null;
    public int visitorNbNights = 0;
    public int foreignMerchantStallId = -1;
    public boolean lastAttackByPlayer = false;
    public HashMap<Goal, Long> lastGoalTime = new HashMap<>();
    @Nullable public String hiredBy = null;
    public boolean aggressiveStance = false;
    public long hiredUntil = 0L;
    public boolean isUsingBow;
    public boolean isUsingHandToHand;
    public boolean isRaider = false;
    private long villagerId = -1L;

    // --- Lifecycle state (Phase 1 parity) ---
    private int lifecycleTickCounter = 0;
    private long lastVillagerSyncTick = 0L;
    private static final int VILLAGER_SYNC_INTERVAL = 60; // ticks between full packet syncs
    private static final int STUCK_THRESHOLD_TICKS = 200; // ticks before declaring stuck
    private static final int HIRED_CHECK_INTERVAL = 20; // ticks between hired-state checks
    private static final int NIGHT_ACTION_CHECK_INTERVAL = 100; // ticks between night-action checks
    private int stuckCounter = 0;
    @Nullable private Point lastStuckCheckPos = null;
    private boolean pathFailedSinceLastTick = false;
    private int consecutivePathFailures = 0;
    private static final int MAX_PATH_FAILURES_BEFORE_RESET = 5;

    public final VillagerInventory inventory = new VillagerInventory();
    private final VillagerActionRuntime actionRuntime = new VillagerActionRuntime();
    private final org.dizzymii.millenaire2.pathing.PathNavigateSimple jpsNavigator =
            new org.dizzymii.millenaire2.pathing.PathNavigateSimple(this);

    protected MillVillager(EntityType<? extends MillVillager> type, Level level) {
        super(type, level);
    }

    // ========== SmartBrainOwner Implementation ==========

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends MillVillager>> getSensors() {
        return ObjectArrayList.of(
                new VillageSensor(),
                new ThreatSensor()
        );
    }

    @Override
    public BrainActivityGroup<? extends MillVillager> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new org.dizzymii.millenaire2.entity.ai.behavior.JpsMoveToWalkTarget()
        );
    }

    @Override
    public BrainActivityGroup<? extends MillVillager> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<MillVillager>(
                        new MillGoalBehaviour(),
                        new GoToHomeBuilding(),
                        new org.dizzymii.millenaire2.entity.ai.behavior.RandomVillagerStroll()
                ),
                new OneRandomBehaviour<>(
                        new SetRandomLookTarget<>(),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
                )
        );
    }

    @Override
    public BrainActivityGroup<? extends MillVillager> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new org.dizzymii.millenaire2.entity.ai.behavior.DefendVillageBehaviour()
        );
    }

    @Override
    protected void customServerAiStep() {
        ensureRuntimeDefaults();
        tickBrain(this);
        lifecycleTickCounter++;

        // --- Lifecycle sub-ticks (mirrors original MillVillager.onLivingUpdate) ---
        tickHiredState();
        tickNightAction();
        tickDialogue();
        tickStuckDetection();
        tickVillagerRecordRefresh();
        tickVillagerPacketSync();

        // Diagnostic logging every 5 seconds (100 ticks)
        if (lifecycleTickCounter % 100 == 0) {
            var brain = this.getBrain();
            String activeGoal = brain.getMemory(org.dizzymii.millenaire2.entity.ai.MillMemoryTypes.ACTIVE_GOAL_KEY.get()).orElse("(none)");
            MillLog.minor("BrainDebug",
                    getName().getString() + " | goal=" + activeGoal
                    + " walk=" + brain.getMemory(MemoryModuleType.WALK_TARGET).isPresent()
                    + " vtype=" + (vtype != null ? vtype.key : "null")
                    + " hired=" + (hiredBy != null)
                    + " stuck=" + stuckCounter
                    + " pos=" + blockPosition()
            );
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, DEFAULT_MOVE_SPEED)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FIRST_NAME, "");
        builder.define(DATA_FAMILY_NAME, "");
        builder.define(DATA_GENDER, 0);
        builder.define(DATA_CULTURE, "");
        builder.define(DATA_GOAL_KEY, "");
    }

    // --- Name accessors ---
    public String getFirstName() { return this.entityData.get(DATA_FIRST_NAME); }
    public void setFirstName(String name) { this.entityData.set(DATA_FIRST_NAME, name); }
    public String getFamilyName() { return this.entityData.get(DATA_FAMILY_NAME); }
    public void setFamilyName(String name) { this.entityData.set(DATA_FAMILY_NAME, name); }
    public int getGender() { return this.entityData.get(DATA_GENDER); }
    public void setGender(int gender) { this.entityData.set(DATA_GENDER, gender); }
    public String getCultureKey() { return this.entityData.get(DATA_CULTURE); }
    public void setCultureKey(String key) { this.entityData.set(DATA_CULTURE, key); }

    public long getVillagerId() { return villagerId; }
    public void setVillagerId(long id) { this.villagerId = id; }

    public boolean isMale() { return getGender() == MALE; }
    public boolean isFemale() { return getGender() == FEMALE; }

    // --- Culture/VillagerType resolution ---
    @Nullable private org.dizzymii.millenaire2.culture.Culture cachedCulture = null;
    @Nullable public String vtypeKey = null;

    /**
     * Resolve and cache the Culture object from the synced culture key.
     */
    @Nullable
    public org.dizzymii.millenaire2.culture.Culture getCulture() {
        String key = getCultureKey();
        if (key.isEmpty()) return null;
        if (cachedCulture != null && cachedCulture.key.equals(key)) return cachedCulture;
        cachedCulture = org.dizzymii.millenaire2.culture.Culture.getCultureByName(key);
        return cachedCulture;
    }

    /**
     * Resolve the VillagerType from the culture using the vtypeKey.
     * Also caches the result into the vtype field.
     */
    public void resolveVillagerType() {
        if (vtypeKey == null || vtypeKey.isEmpty()) return;
        org.dizzymii.millenaire2.culture.Culture culture = getCulture();
        if (culture == null) {
            MillLog.warn(this, "resolveVillagerType: no culture for key='" + getCultureKey() + "'");
            return;
        }
        vtype = culture.getVillagerType(vtypeKey);
        if (vtype == null) {
            MillLog.warn(this, "resolveVillagerType: culture '" + culture.key
                    + "' has no villager type '" + vtypeKey + "' (available: "
                    + culture.villagerTypes.size() + " types)");
        }
    }

    /**
     * Set the villager type key and immediately resolve the VillagerType.
     */
    public void setVillagerTypeKey(String key) {
        this.vtypeKey = key;
        resolveVillagerType();
    }

    private boolean emergencyDefaultsWarned = false;

    private void ensureRuntimeDefaults() {
        if (villagerId == -1L) {
            long generatedId = this.getUUID().getMostSignificantBits() ^ this.getUUID().getLeastSignificantBits();
            this.villagerId = generatedId == -1L ? this.getUUID().getLeastSignificantBits() : generatedId;
        }

        if (getGender() == 0) {
            setGender(this instanceof GenericMale ? MALE : FEMALE);
        }

        org.dizzymii.millenaire2.culture.Culture culture = getCulture();
        if (culture == null) {
            culture = org.dizzymii.millenaire2.culture.Culture.getCultureByName("norman");
            if (culture == null && !org.dizzymii.millenaire2.culture.Culture.LIST_CULTURES.isEmpty()) {
                culture = org.dizzymii.millenaire2.culture.Culture.LIST_CULTURES.get(0);
            }

            if (culture != null) {
                setCultureKey(culture.key);
                cachedCulture = culture;
            }
        }

        if ((vtype == null || vtypeKey == null || vtypeKey.isEmpty()) && culture != null) {
            VillagerType fallbackType = selectDefaultVillagerType(culture, getGender());
            if (fallbackType != null) {
                setVillagerTypeKey(fallbackType.key);
            }
        }

        // BULLETPROOF: If vtype is STILL null after all resolution, create synthetic emergency type
        if (vtype == null) {
            vtype = createEmergencyVillagerType();
            vtypeKey = "_fallback";
            if (!emergencyDefaultsWarned) {
                MillLog.warn(this, "Using emergency fallback VillagerType for villager at " + blockPosition());
                emergencyDefaultsWarned = true;
            }
        }

        // BULLETPROOF: Ensure housePoint is never null — fallback goals depend on it
        if (housePoint == null && townHallPoint != null) {
            housePoint = townHallPoint;
        }
        if (housePoint == null) {
            housePoint = new Point(this.blockPosition());
        }

        if (culture != null && vtype != null) {
            if (getFirstName().isEmpty()) {
                String first = vtype.firstNameList != null && !vtype.firstNameList.isEmpty()
                        ? culture.getRandomName(vtype.firstNameList)
                        : vtype.key;
                setFirstName(first);
            }

            if (getFamilyName().isEmpty()) {
                String family = vtype.familyNameList != null && !vtype.familyNameList.isEmpty()
                        ? culture.getRandomName(vtype.familyNameList)
                        : culture.key;
                setFamilyName(family);
            }
        }

        // Ensure villager has a name even without culture
        if (getFirstName().isEmpty()) {
            setFirstName("Villager");
        }
    }

    /**
     * Create a synthetic emergency VillagerType with basic goals.
     * This ensures the AI always has something to work with.
     */
    private VillagerType createEmergencyVillagerType() {
        org.dizzymii.millenaire2.culture.Culture culture = getCulture();
        VillagerType emergency = new VillagerType(culture, "_fallback");
        emergency.gender = getGender() == FEMALE ? "female" : "male";
        emergency.isChild = false;
        emergency.goals = new java.util.ArrayList<>();
        emergency.goals.add("gosocialise");
        emergency.goals.add("gorest");
        emergency.goals.add("sleep");
        return emergency;
    }

    @Nullable
    private VillagerType selectDefaultVillagerType(org.dizzymii.millenaire2.culture.Culture culture, int gender) {
        String expectedGender = gender == FEMALE ? "female" : "male";
        VillagerType fallback = null;

        for (VillagerType type : culture.listVillagerTypes) {
            if (type == null || type.isChild || type.goals == null || type.goals.isEmpty()) {
                continue;
            }

            if (type.gender != null && expectedGender.equalsIgnoreCase(type.gender)) {
                return type;
            }

            if (fallback == null) {
                fallback = type;
            }
        }

        if (fallback != null) {
            return fallback;
        }

        for (VillagerType type : culture.listVillagerTypes) {
            if (type != null && !type.isChild) {
                return type;
            }
        }

        return culture.listVillagerTypes.isEmpty() ? null : culture.listVillagerTypes.get(0);
    }

    // --- Goal accessors ---
    @Nullable public GoalInformation getGoalInformation() { return goalInformation; }
    public void setGoalInformation(@Nullable GoalInformation info) { this.goalInformation = info; }
    @Nullable public Goal getCurrentGoal() { return currentGoal; }
    @Nullable public Point getPathDestPoint() { return pathDestPoint; }
    public void setPathDestPoint(@Nullable Point p) { this.pathDestPoint = p; }
    public VillagerActionRuntime getActionRuntime() { return actionRuntime; }
    public org.dizzymii.millenaire2.pathing.PathNavigateSimple getJpsNavigator() { return jpsNavigator; }
    public SimpleContainer getInventoryContainer() { return inventory.asContainer(); }
    public int getSelectedInventorySlot() { return inventory.getSelectedSlot(); }
    public void setSelectedInventorySlot(int slot) { inventory.setSelectedSlot(slot); }
    public ItemStack getSelectedInventoryItem() { return inventory.getSelectedItem(); }
    public void syncSelectedItemToHands() {
        ItemStack selected = getSelectedInventoryItem();
        this.setItemSlot(EquipmentSlot.MAINHAND, selected.copy());
        this.heldItemCount = selected.getCount();
    }

    // ========== Custom display name ==========

    @Override
    public Component getDisplayName() {
        String first = getFirstName();
        String family = getFamilyName();
        if (!first.isEmpty() && !family.isEmpty()) {
            return Component.literal(first + " " + family);
        } else if (!first.isEmpty()) {
            return Component.literal(first);
        }
        return super.getDisplayName();
    }

    @Override
    public boolean hasCustomName() {
        return !getFirstName().isEmpty();
    }

    // ========== Tick logic ==========

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            // Ensure VillagerType is resolved (needed after NBT load)
            if (vtype == null && vtypeKey != null) {
                resolveVillagerType();
            }
            // Tick JPS navigator for async pathfinding
            jpsNavigator.tick();
            // Sync brain goal key to synched data for display
            syncGoalKeyFromBrain();
        }
    }

    /**
     * Update synched goal key from Brain memory for display purposes.
     */
    public void syncGoalKeyFromBrain() {
        String key = this.getBrain().getMemory(
                org.dizzymii.millenaire2.entity.ai.MillMemoryTypes.ACTIVE_GOAL_KEY.get()
        ).orElse("");
        if (!key.equals(this.entityData.get(DATA_GOAL_KEY))) {
            this.entityData.set(DATA_GOAL_KEY, key);
            this.goalKey = key.isEmpty() ? null : key;
        }
    }

    // ========== Player interaction ==========

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // Send chat message with villager info for now
        String name = getFirstName() + " " + getFamilyName();
        String culture = getCultureKey();
        String goalDisplay = goalKey != null ? goalKey : "idle";
        player.sendSystemMessage(Component.literal(
                "§6[Millénaire]§r " + name +
                (culture.isEmpty() ? "" : " (" + culture + ")") +
                " — " + goalDisplay
        ));

        // Request server to open the trade GUI for this villager
        if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
            org.dizzymii.millenaire2.network.ServerPacketSender.sendVillagerSync(sp, this);
            org.dizzymii.millenaire2.network.ServerPacketSender.sendOpenGui(
                    sp, org.dizzymii.millenaire2.network.MillPacketIds.GUI_TRADE, this.getId(), this.townHallPoint);
        }
        return InteractionResult.SUCCESS;
    }

    // ========== Combat ==========

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player) {
            lastAttackByPlayer = true;
        }
        boolean result = super.hurt(source, amount);

        // Set HURT_BY memory so Brain behaviours can react
        if (result && !this.level().isClientSide && this.isAlive()) {
            this.getBrain().setMemory(MemoryModuleType.HURT_BY, source);
            if (source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker) {
                this.getBrain().setMemory(MemoryModuleType.HURT_BY_ENTITY, attacker);
            }
        }
        return result;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide) {
            MillLog.major(this, "Villager died: " + getFirstName() + " " + getFamilyName()
                    + " at " + new Point(this.blockPosition()));
            // Notify village and update villager record
            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw != null) {
                org.dizzymii.millenaire2.village.VillagerRecord vr = mw.getVillagerRecord(this.villagerId);
                if (vr != null) {
                    vr.killed = true;
                }
                // Notify the home building so it updates its entity tracking
                if (housePoint != null) {
                    org.dizzymii.millenaire2.village.Building home = mw.getBuilding(housePoint);
                    if (home != null) {
                        home.onVillagerDeath(this.villagerId);
                    }
                }
                mw.setDirty();
            }
        }
    }

    // ========== NBT persistence ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("firstName", getFirstName());
        tag.putString("familyName", getFamilyName());
        tag.putInt("gender", getGender());
        tag.putString("cultureKey", getCultureKey());
        tag.putLong("villagerId", villagerId);
        tag.putBoolean("isRaider", isRaider);
        tag.putBoolean("aggressiveStance", aggressiveStance);
        if (housePoint != null) {
            housePoint.writeToNBT(tag, "house");
        }
        if (townHallPoint != null) {
            townHallPoint.writeToNBT(tag, "th");
        }
        if (goalKey != null) {
            tag.putString("goalKey", goalKey);
        }
        if (hiredBy != null) {
            tag.putString("hiredBy", hiredBy);
            tag.putLong("hiredUntil", hiredUntil);
        }
        if (vtypeKey != null) {
            tag.putString("villagerType", vtypeKey);
        } else if (vtype != null) {
            tag.putString("villagerType", vtype.key);
        }
        // Save inventory
        ListTag invList = new ListTag();
        for (Map.Entry<InvItem, Integer> entry : inventory.entrySet()) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.putString("key", entry.getKey().key);
            itemTag.putInt("count", entry.getValue());
            invList.add(itemTag);
        }
        tag.put("millInventory", invList);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setFirstName(tag.getString("firstName"));
        setFamilyName(tag.getString("familyName"));
        setGender(tag.getInt("gender"));
        setCultureKey(tag.getString("cultureKey"));
        villagerId = tag.getLong("villagerId");
        isRaider = tag.getBoolean("isRaider");
        aggressiveStance = tag.getBoolean("aggressiveStance");
        housePoint = Point.readFromNBT(tag, "house");
        townHallPoint = Point.readFromNBT(tag, "th");
        if (tag.contains("goalKey")) {
            goalKey = tag.getString("goalKey");
            if (Goal.goals != null) {
                currentGoal = Goal.getGoal(goalKey);
            }
        }
        if (tag.contains("hiredBy")) {
            hiredBy = tag.getString("hiredBy");
            hiredUntil = tag.getLong("hiredUntil");
        }
        // Load inventory
        inventory.clear();
        if (tag.contains("millInventory", Tag.TAG_LIST)) {
            ListTag invList = tag.getList("millInventory", Tag.TAG_COMPOUND);
            for (int i = 0; i < invList.size(); i++) {
                CompoundTag itemTag = invList.getCompound(i);
                String key = itemTag.getString("key");
                int count = itemTag.getInt("count");
                InvItem invItem = InvItem.get(key);
                if (invItem != null && count > 0) {
                    inventory.add(invItem, count);
                }
            }
        }
        if (tag.contains("villagerType")) {
            setVillagerTypeKey(tag.getString("villagerType"));
        }
    }

    // --- Building helpers ---
    @Nullable
    public org.dizzymii.millenaire2.village.Building getHomeBuilding() {
        if (housePoint == null || this.level().isClientSide) return null;
        if (!(this.level() instanceof net.minecraft.server.level.ServerLevel sl)) return null;
        org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.world.MillWorldData.get(sl);
        return mw.getBuilding(housePoint);
    }

    @Nullable
    public org.dizzymii.millenaire2.village.Building getTownHallBuilding() {
        if (townHallPoint == null || this.level().isClientSide) return null;
        if (!(this.level() instanceof net.minecraft.server.level.ServerLevel sl)) return null;
        org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.world.MillWorldData.get(sl);
        return mw.getBuilding(townHallPoint);
    }

    // ========== Lifecycle hooks (Phase 1 parity) ==========

    /**
     * Mirrors original updateHired(). Checks if hire has expired and releases the villager.
     * Only runs every HIRED_CHECK_INTERVAL ticks for efficiency.
     */
    private void tickHiredState() {
        if (hiredBy == null) return;
        if (lifecycleTickCounter % HIRED_CHECK_INTERVAL != 0) return;

        long gameTime = this.level().getGameTime();
        if (hiredUntil > 0 && gameTime >= hiredUntil) {
            MillLog.minor(this, "Hire expired for " + getFirstName() + " " + getFamilyName());
            releaseFromHire();
        }
    }

    /**
     * Release this villager from hired service, restoring normal village behavior.
     */
    public void releaseFromHire() {
        this.hiredBy = null;
        this.hiredUntil = 0L;
        this.aggressiveStance = false;
        this.isRaider = false;
        // Clear combat memories so they return to idle behavior
        this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
        this.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    /**
     * Mirrors original performNightAction(). Handles going home at night,
     * sleeping, and night-specific behaviors. Only checks periodically.
     */
    private void tickNightAction() {
        if (lifecycleTickCounter % NIGHT_ACTION_CHECK_INTERVAL != 0) return;
        if (!(this.level() instanceof net.minecraft.server.level.ServerLevel sl)) return;

        long dayTime = sl.getDayTime() % 24000L;
        boolean isNight = dayTime >= 13000 && dayTime < 23000;

        if (isNight && !nightActionPerformed) {
            nightActionPerformed = true;
            performNightAction();
        } else if (!isNight && nightActionPerformed) {
            nightActionPerformed = false;
        }
    }

    /**
     * Execute night-time behavior: return home, rest, and reset daily state.
     * Hired villagers follow their employer instead of going home.
     */
    private void performNightAction() {
        if (hiredBy != null) return; // hired villagers don't go home

        // Set walk target to home building if available
        if (housePoint != null) {
            this.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                    new net.minecraft.world.entity.ai.memory.WalkTarget(
                            housePoint.toBlockPos(), 1.0f, 2));
        }

        // Reset daily goal timers
        lastGoalTime.clear();
        longDistanceStuck = 0;
    }

    /**
     * Mirrors original updateDialogue(). Manages dialogue timers and cleanup.
     */
    private void tickDialogue() {
        if (dialogueKey == null) return;

        long gameTime = this.level().getGameTime();
        // Dialogue auto-expires after 200 ticks (10 seconds)
        if (dialogueStart > 0 && gameTime - dialogueStart > 200L) {
            clearDialogue();
        }
    }

    /**
     * Clear active dialogue state.
     */
    public void clearDialogue() {
        this.dialogueKey = null;
        this.dialogueRole = 0;
        this.dialogueStart = 0L;
        this.dialogueChat = false;
        this.dialogueTargetFirstName = null;
        this.dialogueTargetLastName = null;
    }

    /**
     * Start a dialogue with another villager or player.
     */
    public void startDialogue(String key, int role, @Nullable String targetFirst, @Nullable String targetLast) {
        this.dialogueKey = key;
        this.dialogueRole = role;
        this.dialogueStart = this.level().getGameTime();
        this.dialogueChat = false;
        this.dialogueTargetFirstName = targetFirst;
        this.dialogueTargetLastName = targetLast;
    }

    /**
     * Mirrors original pathFailedSinceLastTick() and stuck recovery.
     * Detects when the villager hasn't moved and resets goals/path.
     */
    private void tickStuckDetection() {
        Point currentPos = new Point(this.blockPosition());

        // Check if we have a walk target but haven't moved
        boolean hasWalkTarget = this.getBrain().getMemory(MemoryModuleType.WALK_TARGET).isPresent();

        if (hasWalkTarget) {
            if (lastStuckCheckPos != null && lastStuckCheckPos.equals(currentPos)) {
                stuckCounter++;
            } else {
                stuckCounter = 0;
                lastStuckCheckPos = currentPos;
            }

            if (stuckCounter >= STUCK_THRESHOLD_TICKS) {
                onStuck();
                stuckCounter = 0;
            }
        } else {
            stuckCounter = 0;
            lastStuckCheckPos = currentPos;
        }

        // Reset path failure flag each tick
        if (pathFailedSinceLastTick) {
            consecutivePathFailures++;
            pathFailedSinceLastTick = false;

            if (consecutivePathFailures >= MAX_PATH_FAILURES_BEFORE_RESET) {
                MillLog.minor(this, "Too many path failures, resetting goal state");
                resetGoalState();
                consecutivePathFailures = 0;
            }
        } else {
            consecutivePathFailures = 0;
        }
    }

    /**
     * Called when the villager is detected as stuck (hasn't moved for STUCK_THRESHOLD_TICKS).
     */
    private void onStuck() {
        longDistanceStuck++;
        MillLog.minor(this, getFirstName() + " is stuck (count=" + longDistanceStuck + ") at " + blockPosition());

        if (longDistanceStuck >= 3) {
            // Teleport to home or town hall as last resort
            Point tp = housePoint != null ? housePoint : townHallPoint;
            if (tp != null) {
                this.teleportTo(tp.x + 0.5, tp.y + 1.0, tp.z + 0.5);
                MillLog.minor(this, "Teleported stuck villager " + getFirstName() + " to " + tp);
            }
            longDistanceStuck = 0;
        }

        resetGoalState();
    }

    /**
     * Reset all active goal and path state so the villager can pick a new goal.
     */
    public void resetGoalState() {
        this.currentGoal = null;
        this.goalKey = null;
        this.goalInformation = null;
        this.pathDestPoint = null;
        this.stopMoving = false;
        this.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        this.getBrain().eraseMemory(org.dizzymii.millenaire2.entity.ai.MillMemoryTypes.ACTIVE_GOAL_KEY.get());
    }

    /**
     * Called by the pathfinding system when a path calculation fails.
     */
    public void onPathFailed() {
        this.pathFailedSinceLastTick = true;
    }

    /**
     * Mirrors original updateVillagerRecord(). Syncs live entity state back
     * to the persistent VillagerRecord every ~100 ticks.
     */
    private void tickVillagerRecordRefresh() {
        if (lifecycleTickCounter % 100 != 0) return;
        if (villagerId == -1L) return;

        org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
        if (mw == null) return;

        org.dizzymii.millenaire2.village.VillagerRecord vr = mw.getVillagerRecord(villagerId);
        if (vr == null) return;

        // Sync position via accessors
        vr.setHousePos(this.housePoint);
        vr.setTownHallPos(this.townHallPoint);

        // Sync identity fields
        vr.firstName = getFirstName();
        vr.familyName = getFamilyName();
        vr.gender = getGender();

        mw.setDirty();
    }

    /**
     * Mirrors original sendVillagerPacket(). Throttled sync of villager state
     * to nearby players for client display.
     */
    private void tickVillagerPacketSync() {
        long gameTime = this.level().getGameTime();
        if (gameTime - lastVillagerSyncTick < VILLAGER_SYNC_INTERVAL) return;
        lastVillagerSyncTick = gameTime;

        if (!(this.level() instanceof net.minecraft.server.level.ServerLevel sl)) return;

        // Send villager sync packet to all players within 64 blocks
        for (net.minecraft.server.level.ServerPlayer sp : sl.getServer().getPlayerList().getPlayers()) {
            if (sp.level() == sl && sp.distanceToSqr(this) < 64 * 64) {
                org.dizzymii.millenaire2.network.ServerPacketSender.sendVillagerSync(sp, this);
            }
        }
    }

    // --- Inventory helpers ---
    public int countInv(InvItem item) {
        return inventory.count(item);
    }

    public void addToInv(InvItem item, int count) {
        if (count > 0) {
            inventory.add(item, count);
        } else if (count < 0) {
            inventory.remove(item, -count);
        }
    }

    public void removeFromInv(InvItem item, int count) {
        inventory.remove(item, count);
    }

    // ========== Concrete villager subclasses ==========

    public static class GenericMale extends MillVillager {
        public GenericMale(EntityType<? extends GenericMale> type, Level level) {
            super(type, level);
        }
    }

    public static class GenericSymmFemale extends MillVillager {
        public GenericSymmFemale(EntityType<? extends GenericSymmFemale> type, Level level) {
            super(type, level);
        }
    }

    public static class GenericAsymmFemale extends MillVillager {
        public GenericAsymmFemale(EntityType<? extends GenericAsymmFemale> type, Level level) {
            super(type, level);
        }
    }
}
