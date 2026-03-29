package org.dizzymii.millenaire2.entity;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.brain.VillagerBrainConfig;
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
public abstract class MillVillager extends PathfinderMob {

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
    private static final int GOAL_TICK_INTERVAL = 20; // Check goals every second
    /** Ticks after being hurt during which the villager is considered aggroed. */
    private static final int AGGRO_LINGER_TICKS = 200;

    // --- Instance fields (ported from original) ---
    @Nullable public VillagerType vtype;
    public int action = 0;
    @Nullable public String goalKey = null;
    @Nullable private Goal currentGoal = null;
    @Nullable private GoalInformation goalInformation = null;
    @Nullable private Point pathDestPoint;
    @Nullable public Point housePoint = null;
    @Nullable public Point prevPoint = null;
    @Nullable public Point townHallPoint = null;
    public boolean extraLog = false;
    public ItemStack heldItem = ItemStack.EMPTY;
    public ItemStack heldItemOffHand = ItemStack.EMPTY;
    public long timer = 0L;
    public long actionStart = 0L;
    public boolean allowRandomMoves = false;
    public boolean stopMoving = false;
    public boolean registered = false;
    public int longDistanceStuck;
    public boolean nightActionPerformed = false;
    public long speech_started = 0L;
    public HashMap<InvItem, Integer> inventory = new HashMap<>();
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
    private int goalTickCounter = 0;
    /** Counts down from {@link #AGGRO_LINGER_TICKS} after the last attack. */
    private int aggroTicks = 0;

    /** Controller that owns goal selection and execution logic for this villager. */
    private final VillagerGoalController goalController = new VillagerGoalController(this);

    /** Returns the goal controller (used by Brain behaviours). */
    public VillagerGoalController getGoalController() {
        return goalController;
    }

    /** {@code true} while the villager has been recently attacked and may fight back. */
    public boolean isAggroed() {
        return aggroTicks > 0;
    }

    /**
     * Creates a new {@link MillVillager} of the given entity type in the given level.
     * Called by the entity factory during spawning.
     *
     * @param type  the entity type
     * @param level the level to spawn into
     */
    protected MillVillager(EntityType<? extends MillVillager> type, Level level) {
        super(type, level);
    }

    /**
     * Builds the default attribute set shared by all {@link MillVillager} subtypes.
     * <p>Sets max health (20), movement speed ({@value #DEFAULT_MOVE_SPEED}),
     * attack damage (2), and follow range (48 blocks).</p>
     *
     * @return a mutable builder pre-populated with villager attributes
     */
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, DEFAULT_MOVE_SPEED)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    /**
     * Registers the synched entity data keys used to synchronise villager state
     * (first name, family name, gender, culture key, and current goal key) to clients.
     *
     * @param builder the synched-entity-data builder supplied by NeoForge
     */
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
    /** Returns the villager's first (given) name, synced to all clients. */
    public String getFirstName() { return this.entityData.get(DATA_FIRST_NAME); }

    /**
     * Sets the villager's first (given) name and syncs it to all clients.
     *
     * @param name the new first name; must not be {@code null}
     */
    public void setFirstName(String name) { this.entityData.set(DATA_FIRST_NAME, name); }

    /** Returns the villager's family (surname) name, synced to all clients. */
    public String getFamilyName() { return this.entityData.get(DATA_FAMILY_NAME); }

    /**
     * Sets the villager's family (surname) name and syncs it to all clients.
     *
     * @param name the new family name; must not be {@code null}
     */
    public void setFamilyName(String name) { this.entityData.set(DATA_FAMILY_NAME, name); }

    /**
     * Returns the villager's gender as an integer constant.
     * Use {@link #isMale()} and {@link #isFemale()} for boolean tests.
     *
     * @return {@link #MALE} or {@link #FEMALE}
     */
    public int getGender() { return this.entityData.get(DATA_GENDER); }

    /**
     * Sets the villager's gender and syncs it to all clients.
     *
     * @param gender {@link #MALE} or {@link #FEMALE}
     */
    public void setGender(int gender) { this.entityData.set(DATA_GENDER, gender); }

    /**
     * Returns the culture identifier string for this villager (e.g. {@code "norman"}),
     * synced to all clients. Returns an empty string if no culture is assigned.
     */
    public String getCultureKey() { return this.entityData.get(DATA_CULTURE); }

    /**
     * Sets the culture identifier for this villager and syncs it to all clients.
     * Invalidates the cached {@link org.dizzymii.millenaire2.culture.Culture} reference.
     *
     * @param key the culture key (e.g. {@code "norman"})
     */
    public void setCultureKey(String key) { this.entityData.set(DATA_CULTURE, key); }

    /**
     * Returns the unique numeric identifier that links this entity to its
     * persistent {@link org.dizzymii.millenaire2.village.VillagerRecord}.
     * Returns {@code -1L} if the villager has not yet been assigned an ID.
     */
    public long getVillagerId() { return villagerId; }

    /**
     * Sets the unique numeric identifier that links this entity to its
     * persistent {@link org.dizzymii.millenaire2.village.VillagerRecord}.
     *
     * @param id the villager record ID
     */
    public void setVillagerId(long id) { this.villagerId = id; }

    /** Returns {@code true} if this villager's gender is {@link #MALE}. */
    public boolean isMale() { return getGender() == MALE; }

    /** Returns {@code true} if this villager's gender is {@link #FEMALE}. */
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
        if (cachedCulture != null && key.equals(cachedCulture.key)) return cachedCulture;
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
        if (culture != null) {
            vtype = culture.getVillagerType(vtypeKey);
        }
    }

    /**
     * Set the villager type key and immediately resolve the VillagerType.
     */
    public void setVillagerTypeKey(String key) {
        this.vtypeKey = key;
        resolveVillagerType();
    }

    // --- Goal accessors ---
    /**
     * Returns additional goal information (e.g. target entity or coordinates) for
     * the currently active goal, or {@code null} when no goal is running.
     */
    @Nullable public GoalInformation getGoalInformation() { return goalInformation; }

    /**
     * Sets the goal information for the currently active goal.
     * Called by {@link VillagerGoalController} when a new goal is started.
     *
     * @param info the new goal information, or {@code null} to clear
     */
    public void setGoalInformation(@Nullable GoalInformation info) { this.goalInformation = info; }

    /**
     * Returns the currently active {@link Goal} object, or {@code null} when idle.
     */
    @Nullable public Goal getCurrentGoal() { return currentGoal; }

    /**
     * Returns the navigation destination point used by the current pathing request,
     * or {@code null} if the villager is not currently navigating.
     */
    @Nullable public Point getPathDestPoint() { return pathDestPoint; }

    /**
     * Sets (or clears) the navigation destination point.
     * Called by {@link VillagerGoalController} to track the active path target.
     *
     * @param p the destination, or {@code null} to clear
     */
    public void setPathDestPoint(@Nullable Point p) { this.pathDestPoint = p; }

    // ========== Custom display name ==========

    /**
     * Returns the villager's full name as a chat {@link Component} in the form
     * {@code "FirstName FamilyName"}.  Falls back to the entity type name when
     * neither name field is populated.
     *
     * @return the display name component
     */
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

    /**
     * Returns {@code true} when the villager has a non-empty first name, suppressing
     * the vanilla entity-type label from the name tag renderer.
     */
    @Override
    public boolean hasCustomName() {
        return !getFirstName().isEmpty();
    }

    // ========== Brain setup ==========

    /**
     * Creates the {@link Brain.Provider} used to deserialise and reconstruct
     * this villager's brain.  The default implementation uses no custom memory
     * modules or sensors; behaviours read entity fields directly.
     * <p>
     * <b>Migration note:</b> When SmartBrainLib is integrated, replace with
     * {@code SmartBrainProvider.of(this)}.
     *
     * @return the brain provider for this villager
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Brain.Provider<MillVillager> brainProvider() {
        // No custom sensors or memory modules — behaviours read entity fields directly.
        // When SmartBrainLib is available, replace with SmartBrainProvider.of(this).
        return Brain.provider(List.of(), List.of());
    }

    /**
     * Constructs the villager's {@link Brain} by applying
     * {@link VillagerBrainConfig#configureBrain(Brain)} to the freshly created brain
     * returned by the provider.
     *
     * @param dynamic the serialised NBT brain data, or an empty dynamic for new entities
     * @return the fully configured brain
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        Brain<MillVillager> brain = (Brain<MillVillager>) this.brainProvider().makeBrain(dynamic);
        VillagerBrainConfig.configureBrain(brain);
        return brain;
    }

    /**
     * Returns this villager's brain, cast to {@code Brain<MillVillager>}.
     * The cast is safe because {@link #makeBrain} always produces a
     * {@code Brain<MillVillager>}.
     *
     * @return the villager brain
     */
    @Override
    @SuppressWarnings("unchecked")
    public Brain<MillVillager> getBrain() {
        return (Brain<MillVillager>) super.getBrain();
    }

    // ========== Tick logic ==========

    /**
     * Main per-tick update: calls the vanilla tick pipeline and, on the logical
     * server, invokes {@link #serverTick()} for goal/aggro decay logic.
     */
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            serverTick();
        }
    }

    /**
     * Server-side per-tick logic.
     * <ul>
     *   <li>Decrements the aggro counter until it reaches zero.</li>
     *   <li>Periodically (every {@value #GOAL_TICK_INTERVAL} ticks) calls
     *       {@link VillagerBrainConfig#updateActivity(MillVillager)} to switch
     *       the active Brain activity based on time-of-day and combat state.</li>
     * </ul>
     */
    private void serverTick() {
        // Decay aggro counter
        if (aggroTicks > 0) {
            aggroTicks--;
        }

        goalTickCounter++;
        if (goalTickCounter >= GOAL_TICK_INTERVAL) {
            goalTickCounter = 0;
            // Update Brain activity based on time-of-day / combat state
            VillagerBrainConfig.updateActivity(this);
        }
    }

    /**
     * Called each tick on the server after the standard AI step.
     * Ticks the villager's {@link Brain}, pushing it through the active-activity
     * behaviour pipeline and advancing any running behaviours.
     */
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.level() instanceof ServerLevel sl) {
            this.level().getProfiler().push("millVillagerBrain");
            this.getBrain().tick(sl, this);
            this.level().getProfiler().pop();
        }
    }

    // ========== Package-private bridge methods for VillagerGoalController ==========

    /** Sets the current goal and goal information. Called by {@link VillagerGoalController}. */
    void setCurrentGoalInternal(@Nullable Goal goal, @Nullable GoalInformation info) {
        this.currentGoal = goal;
        this.goalInformation = info;
    }

    /** Syncs the goal key to clients. Called by {@link VillagerGoalController}. */
    void setSynchedGoalKey(String key) {
        this.entityData.set(DATA_GOAL_KEY, key);
    }

    // ========== Player interaction ==========

    /**
     * Handles right-click interaction from a player.
     * <p>
     * On the server side, sends a villager-info message to the player and requests
     * the trade GUI to be opened.  Returns {@link InteractionResult#SUCCESS} on both
     * sides so the animation plays correctly.
     *
     * @param player the interacting player
     * @param hand   the hand used for the interaction
     * @return {@link InteractionResult#SUCCESS} always
     */
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

    /**
     * Handles incoming damage.  In addition to vanilla processing:
     * <ul>
     *   <li>Records whether the attacker is a player.</li>
     *   <li>Starts the aggro linger timer ({@value #AGGRO_LINGER_TICKS} ticks).</li>
     *   <li>If the villager type has {@code helpInAttacks} set, immediately switches
     *       to the {@code defendvillage} goal and the {@link net.minecraft.world.entity.schedule.Activity#FIGHT FIGHT}
     *       Brain activity.</li>
     * </ul>
     *
     * @param source the source of damage
     * @param amount the amount of damage to apply
     * @return {@code true} if the damage was actually applied
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player) {
            lastAttackByPlayer = true;
        }
        boolean result = super.hurt(source, amount);

        // Switch to defensive/combat goal if attacked
        if (result && !this.level().isClientSide && this.isAlive()) {
            aggroTicks = AGGRO_LINGER_TICKS;
            if (vtype != null && vtype.helpInAttacks && Goal.defendVillage != null) {
                try {
                    GoalInformation info = Goal.defendVillage.getDestination(this);
                    if (info != null && info.hasTarget()) {
                        goalController.setActiveGoal("defendvillage", Goal.defendVillage, info);
                        // Switch Brain to FIGHT activity immediately
                        this.getBrain().setActiveActivityIfPossible(
                                net.minecraft.world.entity.schedule.Activity.FIGHT);
                    }
                } catch (Exception e) {
                    MillLog.error(this, "[hurt] Error setting up combat goal", e);
                }
            }
        }
        return result;
    }

    /**
     * Called when the villager dies.  Performs vanilla death handling then:
     * <ul>
     *   <li>Removes the debug tracker entry via {@link VillagerDebugger#remove(MillVillager)}.</li>
     *   <li>Marks the corresponding {@link org.dizzymii.millenaire2.village.VillagerRecord}
     *       as killed in {@link org.dizzymii.millenaire2.world.MillWorldData}.</li>
     * </ul>
     *
     * @param source the cause of death
     */
    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide) {
            VillagerDebugger.remove(this);
            MillLog.major(this, "Villager died: " + getFirstName() + " " + getFamilyName()
                    + " at " + new Point(this.blockPosition()));
            // Notify village and update villager record
            org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            if (mw != null) {
                org.dizzymii.millenaire2.village.VillagerRecord vr = mw.getVillagerRecord(this.villagerId);
                if (vr != null) {
                    vr.killed = true;
                }
            }
        }
    }

    // ========== NBT persistence ==========

    /**
     * Writes all Millénaire-specific fields to NBT so they survive a world save/load cycle.
     * Fields written: names, gender, culture key, villager ID, raider/hired/aggressive flags,
     * house/town-hall coordinates, goal key, hired-by info, villager type, and the
     * in-memory item inventory.
     *
     * @param tag the compound tag to write into
     */
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

    /**
     * Reads all Millénaire-specific fields from NBT after a world save/load cycle,
     * restoring all data written by {@link #addAdditionalSaveData(CompoundTag)}.
     *
     * @param tag the compound tag to read from
     */
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
                currentGoal = Goal.goals.get(goalKey);
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
                    inventory.put(invItem, count);
                }
            }
        }
        if (tag.contains("villagerType")) {
            setVillagerTypeKey(tag.getString("villagerType"));
        }
    }

    // --- Building helpers ---

    /**
     * Looks up the {@link org.dizzymii.millenaire2.village.Building} that this villager's
     * house point belongs to.
     * <p>Always returns {@code null} on the client side or when
     * {@link #housePoint} is not set.</p>
     *
     * @return the home building, or {@code null} if unavailable
     */
    @Nullable
    public org.dizzymii.millenaire2.village.Building getHomeBuilding() {
        if (housePoint == null || this.level().isClientSide) return null;
        if (!(this.level() instanceof net.minecraft.server.level.ServerLevel sl)) return null;
        org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.world.MillWorldData.get(sl);
        return mw.getBuilding(housePoint);
    }

    /**
     * Looks up the {@link org.dizzymii.millenaire2.village.Building} that serves as
     * this villager's town hall.
     * <p>Always returns {@code null} on the client side or when
     * {@link #townHallPoint} is not set.</p>
     *
     * @return the town-hall building, or {@code null} if unavailable
     */
    @Nullable
    public org.dizzymii.millenaire2.village.Building getTownHallBuilding() {
        if (townHallPoint == null || this.level().isClientSide) return null;
        if (!(this.level() instanceof net.minecraft.server.level.ServerLevel sl)) return null;
        org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.world.MillWorldData.get(sl);
        return mw.getBuilding(townHallPoint);
    }

    // --- Inventory helpers ---

    /**
     * Returns the count of the given {@link InvItem} currently held in the
     * villager's logical inventory.  Returns {@code 0} if the item is absent.
     *
     * @param item the item to count
     * @return the quantity held, or {@code 0}
     */
    public int countInv(InvItem item) {
        return inventory.getOrDefault(item, 0);
    }

    /**
     * Adds (or subtracts) {@code count} of the given {@link InvItem} from the
     * villager's logical inventory.  If the resulting quantity drops to zero or
     * below, the entry is removed entirely.
     *
     * @param item  the item to adjust
     * @param count the amount to add (use a negative value to subtract)
     */
    public void addToInv(InvItem item, int count) {
        inventory.merge(item, count, Integer::sum);
        Integer current = inventory.get(item);
        if (current != null && current <= 0) {
            inventory.remove(item);
        }
    }

    /**
     * Removes {@code count} of the given {@link InvItem} from the villager's
     * logical inventory.  Equivalent to {@code addToInv(item, -count)}.
     *
     * @param item  the item to remove
     * @param count the amount to remove (positive value)
     */
    public void removeFromInv(InvItem item, int count) {
        addToInv(item, -count);
    }

    // ========== Concrete villager subclasses ==========

    /**
     * Generic male villager entity.
     * Uses the {@link MillVillagerModel} and {@link MillVillagerRenderer}.
     */
    public static class GenericMale extends MillVillager {
        /**
         * Creates a new {@code GenericMale} villager entity.
         *
         * @param type  the entity type
         * @param level the level to spawn into
         */
        public GenericMale(EntityType<? extends GenericMale> type, Level level) {
            super(type, level);
        }
    }

    /**
     * Generic female villager with a symmetrical body model (right-handed, 80% chance).
     * Uses the female symmetrical renderer.
     */
    public static class GenericSymmFemale extends MillVillager {
        /**
         * Creates a new {@code GenericSymmFemale} villager entity.
         *
         * @param type  the entity type
         * @param level the level to spawn into
         */
        public GenericSymmFemale(EntityType<? extends GenericSymmFemale> type, Level level) {
            super(type, level);
        }
    }

    /**
     * Generic female villager with an asymmetrical body model (left-handed, 20% chance).
     * Uses the female asymmetrical renderer.
     */
    public static class GenericAsymmFemale extends MillVillager {
        /**
         * Creates a new {@code GenericAsymmFemale} villager entity.
         *
         * @param type  the entity type
         * @param level the level to spawn into
         */
        public GenericAsymmFemale(EntityType<? extends GenericAsymmFemale> type, Level level) {
            super(type, level);
        }
    }
}
