package org.dizzymii.millenaire2.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.VillagerRecord;

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
    private static final int RECORD_SYNC_INTERVAL = 200;

    // --- Combat fields ---
    @Nullable public LivingEntity attackTarget = null;
    public int attackCooldown = 0;
    private static final int MELEE_COOLDOWN = 20;
    private static final int RANGED_COOLDOWN = 40;
    private static final double CALL_FOR_HELP_RANGE = 16.0;
    private static final int DOOR_INTERACT_RANGE_SQ = 4;

    // --- Door/leaf tracking ---
    @Nullable private BlockPos lastOpenedDoor = null;
    private int doorCloseTimer = 0;
    private static final int DOOR_CLOSE_DELAY = 40;

    // --- Child/lifecycle ---
    public int childAge = 0;
    private static final int CHILD_GROWTH_AGE = 24000 * 4; // ~4 MC days

    protected MillVillager(EntityType<? extends MillVillager> type, Level level) {
        super(type, level);
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
    @Nullable public GoalInformation getGoalInformation() { return goalInformation; }
    public void setGoalInformation(@Nullable GoalInformation info) { this.goalInformation = info; }
    @Nullable public Goal getCurrentGoal() { return currentGoal; }
    @Nullable public Point getPathDestPoint() { return pathDestPoint; }
    public void setPathDestPoint(@Nullable Point p) { this.pathDestPoint = p; }

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
            serverTick();
        }
    }

    private void serverTick() {
        updateHired();

        // Combat tick (every tick for responsiveness)
        tickCombat();

        goalTickCounter++;
        if (goalTickCounter >= GOAL_TICK_INTERVAL) {
            goalTickCounter = 0;
            tickGoalSelection();
        }
        tickGoalExecution();

        // Door/fence-gate handling (every 5 ticks)
        handleDoorsAndFenceGates();

        // Leaf clearing (every 10 ticks)
        handleLeafClearing();

        // Crop detrample (every 20 ticks)
        detrampleCrops();

        // Dialogue expiry
        tickDialogue();

        if (this.tickCount % RECORD_SYNC_INTERVAL == 0) {
            syncRecordToWorld();
        }

        // Night actions (once per night)
        performNightActionFull();
    }

    /**
     * Select a new goal if the current one is finished or invalid.
     */
    private void tickGoalSelection() {
        if (Goal.goals == null || Goal.goals.isEmpty()) return;

        // Ensure VillagerType is resolved (needed after NBT load)
        if (vtype == null && vtypeKey != null) {
            resolveVillagerType();
        }

        // If we have a valid active goal, check if it's still valid
        if (currentGoal != null && goalKey != null) {
            try {
                if (!currentGoal.isStillValid(this)) {
                    clearGoal();
                }
            } catch (Exception e) {
                MillLog.error(this, "Error checking goal validity: " + goalKey, e);
                clearGoal();
            }
        }

        // If no goal, pick a new one
        if (currentGoal == null) {
            selectNewGoal();
        }
    }

    private void selectNewGoal() {
        boolean isNight = !this.level().isDay();

        // Try to find a suitable goal from the villager type's goal list
        if (vtype != null && !vtype.goals.isEmpty()) {
            for (String gKey : vtype.goals) {
                Goal g = Goal.goals.get(gKey);
                if (g == null) continue;
                if (isNight && !g.canBeDoneAtNight()) continue;
                if (!isNight && !g.canBeDoneInDayTime()) continue;

                // Check time-of-day restrictions
                if (g.minimumHour >= 0 || g.maximumHour >= 0) {
                    long dayTime = this.level().getDayTime() % 24000;
                    int hour = (int) (dayTime / 1000 + 6) % 24;
                    if (g.minimumHour >= 0 && hour < g.minimumHour) continue;
                    if (g.maximumHour >= 0 && hour > g.maximumHour) continue;
                }

                // Check cooldown
                Long lastTime = lastGoalTime.get(g);
                if (lastTime != null && (this.level().getGameTime() - lastTime) < Goal.STANDARD_DELAY / 50) {
                    continue;
                }

                // Try to get a destination for this goal
                try {
                    GoalInformation info = g.getDestination(this);
                    if (info != null && info.hasTarget()) {
                        setActiveGoal(gKey, g, info);
                        return;
                    }
                } catch (Exception e) {
                    MillLog.error(this, "Error getting destination for goal: " + gKey, e);
                }
            }
        }

        // Fallback: sleep at night, idle wander during day
        if (isNight && Goal.sleep != null) {
            try {
                GoalInformation info = Goal.sleep.getDestination(this);
                if (info != null && info.hasTarget()) {
                    setActiveGoal("sleep", Goal.sleep, info);
                    return;
                }
            } catch (Exception ignored) {}
        }

        // Daytime idle: try socialise, otherwise wander near home
        if (!isNight) {
            if (Goal.gosocialise != null) {
                try {
                    GoalInformation info = Goal.gosocialise.getDestination(this);
                    if (info != null && info.hasTarget()) {
                        setActiveGoal("gosocialise", Goal.gosocialise, info);
                        return;
                    }
                } catch (Exception ignored) {}
            }
            // Random wander near home point
            Point wanderTarget = housePoint != null ? housePoint : townHallPoint;
            if (wanderTarget != null) {
                int dx = this.getRandom().nextInt(11) - 5;
                int dz = this.getRandom().nextInt(11) - 5;
                Point wander = new Point(wanderTarget.x + dx, wanderTarget.y, wanderTarget.z + dz);
                this.getNavigation().moveTo(wander.x + 0.5, wander.y, wander.z + 0.5, 0.5);
            }
        }
    }

    private void setActiveGoal(String key, Goal goal, GoalInformation info) {
        this.goalKey = key;
        this.currentGoal = goal;
        this.goalInformation = info;
        this.goalStarted = this.level().getGameTime();
        this.actionStart = this.level().getGameTime();
        this.entityData.set(DATA_GOAL_KEY, key);

        if (info.targetPoint != null) {
            this.pathDestPoint = info.targetPoint;
            // Navigate toward the goal target
            this.getNavigation().moveTo(
                    info.targetPoint.x + 0.5,
                    info.targetPoint.y,
                    info.targetPoint.z + 0.5,
                    currentGoal.sprint ? 1.0 : 0.6
            );
        }
    }

    public void clearGoal() {
        if (currentGoal != null) {
            lastGoalTime.put(currentGoal, this.level().getGameTime());
        }
        this.goalKey = null;
        this.currentGoal = null;
        this.goalInformation = null;
        this.pathDestPoint = null;
        this.actionStart = 0;
        this.entityData.set(DATA_GOAL_KEY, "");
    }

    /**
     * Execute the current goal's action if we've arrived at the destination.
     */
    private void tickGoalExecution() {
        if (currentGoal == null || goalInformation == null) return;

        // Check if we're close enough to the target to perform the action
        Point targetPoint = goalInformation.targetPoint;
        if (targetPoint != null) {
            double dist = this.distanceToSqr(
                    targetPoint.x + 0.5,
                    targetPoint.y,
                    targetPoint.z + 0.5
            );
            int range = currentGoal.range(this);
            if (dist > (range * range + 1)) {
                // Still travelling
                return;
            }
        }

        // Check action duration
        try {
            int duration = currentGoal.actionDuration(this);
            if ((this.level().getGameTime() - actionStart) < duration) {
                return; // Still waiting for action to complete
            }

            boolean finished = currentGoal.performAction(this);
            if (finished) {
                clearGoal();
            } else {
                // Reset action timer for next cycle
                actionStart = this.level().getGameTime();
            }
        } catch (Exception e) {
            MillLog.error(this, "Error executing goal: " + goalKey, e);
            clearGoal();
        }
    }

    private void syncRecordToWorld() {
        if (this.level().isClientSide) {
            return;
        }
        org.dizzymii.millenaire2.world.MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
        if (mw == null || villagerId < 0) {
            return;
        }

        org.dizzymii.millenaire2.village.VillagerRecord vr = mw.getVillagerRecord(villagerId);
        if (vr == null) {
            vr = new org.dizzymii.millenaire2.village.VillagerRecord();
            vr.setVillagerId(villagerId);
        }

        vr.updateRecord(this);
        mw.registerVillagerRecord(vr, true);
    }

    private void updateHired() {
        if (hiredBy == null || hiredBy.isEmpty()) {
            return;
        }

        if (hiredUntil > 0 && this.level().getGameTime() >= hiredUntil) {
            hiredBy = null;
            hiredUntil = 0L;
            aggressiveStance = false;
            clearGoal();
        }
    }

    public boolean isChildVillager() {
        VillagerType type = vtype;
        return type != null && type.isChild;
    }

    public boolean helpsInAttacks() {
        VillagerType type = vtype;
        return type != null && type.helpInAttacks;
    }

    public boolean isForeignMerchant() {
        VillagerType type = vtype;
        return type != null && type.isForeignMerchant;
    }

    public boolean isLocalMerchant() {
        VillagerType type = vtype;
        return type != null && type.isLocalMerchant;
    }

    public boolean isHostile() {
        VillagerType type = vtype;
        return type != null && type.hostile;
    }

    public boolean isChief() {
        VillagerType type = vtype;
        return type != null && type.isChief;
    }

    public String getNameString() {
        String first = getFirstName();
        String family = getFamilyName();
        if (first.isEmpty()) return family;
        if (family.isEmpty()) return first;
        return first + " " + family;
    }

    public String getNativeOccupationName() {
        VillagerType type = vtype;
        if (type == null || type.name == null) {
            return "";
        }
        return type.name;
    }

    public String getGameOccupation() {
        VillagerType type = vtype;
        if (type == null || type.name == null) {
            return "";
        }
        return type.name;
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

        // Switch to defensive/combat goal if attacked
        if (result && !this.level().isClientSide && this.isAlive()) {
            if (vtype != null && vtype.helpInAttacks && Goal.defendVillage != null) {
                try {
                    GoalInformation info = Goal.defendVillage.getDestination(this);
                    if (info != null && info.hasTarget()) {
                        setActiveGoal("defendvillage", Goal.defendVillage, info);
                    }
                } catch (Exception ignored) {}
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
        // Sprint 3: additional fields
        tag.putInt("childAge", childAge);
        tag.putInt("visitorNbNights", visitorNbNights);
        tag.putBoolean("nightActionPerformed", nightActionPerformed);
        tag.putInt("foreignMerchantStallId", foreignMerchantStallId);
        tag.putInt("constructionJobId", constructionJobId);
        if (speech_key != null) {
            tag.putString("speechKey", speech_key);
            tag.putInt("speechVariant", speech_variant);
        }
        if (dialogueKey != null) {
            tag.putString("dialogueKey", dialogueKey);
            tag.putInt("dialogueRole", dialogueRole);
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
        // Sprint 3: additional fields
        childAge = tag.getInt("childAge");
        visitorNbNights = tag.getInt("visitorNbNights");
        nightActionPerformed = tag.getBoolean("nightActionPerformed");
        foreignMerchantStallId = tag.getInt("foreignMerchantStallId");
        constructionJobId = tag.getInt("constructionJobId");
        if (tag.contains("speechKey")) {
            speech_key = tag.getString("speechKey");
            speech_variant = tag.getInt("speechVariant");
        }
        if (tag.contains("dialogueKey")) {
            dialogueKey = tag.getString("dialogueKey");
            dialogueRole = tag.getInt("dialogueRole");
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

    // --- Inventory helpers ---
    public int countInv(InvItem item) {
        return inventory.getOrDefault(item, 0);
    }

    public void addToInv(InvItem item, int count) {
        inventory.merge(item, count, Integer::sum);
        Integer current = inventory.get(item);
        if (current != null && current <= 0) {
            inventory.remove(item);
        }
    }

    public void removeFromInv(InvItem item, int count) {
        addToInv(item, -count);
    }

    // ========== Combat system ==========

    public void attackEntity(LivingEntity target) {
        if (target == null || !target.isAlive() || this.level().isClientSide) return;
        this.attackTarget = target;

        double distSq = this.distanceToSqr(target);
        boolean useRanged = hasBow() && vtype != null && vtype.isArcher && distSq > 9.0;

        if (useRanged) {
            attackEntityWithRangedAttack(target);
        } else {
            // Melee attack
            this.isUsingHandToHand = true;
            this.isUsingBow = false;
            float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

            // Weapon bonus
            ItemStack weapon = getBestWeapon();
            if (!weapon.isEmpty() && weapon.getItem() instanceof SwordItem) {
                damage += weapon.getItem().getDamage(weapon);
                this.heldItem = weapon;
            }

            target.hurt(this.damageSources().mobAttack(this), damage);
            this.attackCooldown = MELEE_COOLDOWN;
        }
    }

    public void attackEntityWithRangedAttack(LivingEntity target) {
        if (target == null || this.level().isClientSide) return;
        this.isUsingBow = true;
        this.isUsingHandToHand = false;

        Arrow arrow = new Arrow(this.level(), this, new ItemStack(Items.ARROW), null);
        double dx = target.getX() - this.getX();
        double dy = target.getY() + target.getEyeHeight() - 1.1 - arrow.getY();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        arrow.shoot(dx, dy + dist * 0.2, dz, 1.6F, 6.0F);
        arrow.setOwner(this);

        this.level().addFreshEntity(arrow);
        this.attackCooldown = RANGED_COOLDOWN;
    }

    public void callForHelp(LivingEntity attacker) {
        if (this.level().isClientSide || attacker == null) return;

        AABB searchBox = this.getBoundingBox().inflate(CALL_FOR_HELP_RANGE);
        List<MillVillager> nearby = this.level().getEntitiesOfClass(MillVillager.class, searchBox);
        for (MillVillager ally : nearby) {
            if (ally == this || !ally.isAlive()) continue;
            if (!isSameVillage(ally)) continue;
            if (ally.helpsInAttacks() && ally.attackTarget == null) {
                ally.attackTarget = attacker;
            }
        }
    }

    public boolean isSameVillage(MillVillager other) {
        if (other == null) return false;
        if (townHallPoint == null || other.townHallPoint == null) return false;
        return townHallPoint.equals(other.townHallPoint);
    }

    private void tickCombat() {
        if (attackCooldown > 0) attackCooldown--;

        if (attackTarget != null) {
            if (!attackTarget.isAlive() || attackTarget.isRemoved()) {
                attackTarget = null;
                isUsingBow = false;
                isUsingHandToHand = false;
                return;
            }
            double distSq = this.distanceToSqr(attackTarget);
            if (distSq > ATTACK_RANGE * ATTACK_RANGE) {
                attackTarget = null;
                isUsingBow = false;
                isUsingHandToHand = false;
                return;
            }
            if (attackCooldown <= 0) {
                attackEntity(attackTarget);
            }
            // Navigate toward target
            if (distSq > 4.0) {
                this.getNavigation().moveTo(attackTarget, isUsingBow ? 0.6 : 1.0);
            }
        }

        // Aggro nearby hostile mobs if defensive
        if (this.tickCount % 40 == 0 && vtype != null && (vtype.helpInAttacks || vtype.isDefensive)) {
            if (attackTarget == null) {
                AABB scan = this.getBoundingBox().inflate(vtype.isDefensive ? ATTACK_RANGE_DEFENSIVE : ATTACK_RANGE);
                List<Monster> hostiles = this.level().getEntitiesOfClass(Monster.class, scan);
                if (!hostiles.isEmpty()) {
                    attackTarget = hostiles.get(0);
                }
            }
        }
    }

    // ========== Door / fence-gate / leaf handling ==========

    private void handleDoorsAndFenceGates() {
        if (this.tickCount % 5 != 0) return;

        // Close previously opened door after delay
        if (lastOpenedDoor != null) {
            doorCloseTimer++;
            if (doorCloseTimer >= DOOR_CLOSE_DELAY) {
                BlockState state = this.level().getBlockState(lastOpenedDoor);
                if (state.getBlock() instanceof DoorBlock door) {
                    if (state.getValue(BlockStateProperties.OPEN)) {
                        door.setOpen(null, this.level(), state, lastOpenedDoor, false);
                    }
                } else if (state.getBlock() instanceof FenceGateBlock) {
                    if (state.getValue(BlockStateProperties.OPEN)) {
                        this.level().setBlock(lastOpenedDoor,
                                state.setValue(BlockStateProperties.OPEN, false), 3);
                    }
                }
                lastOpenedDoor = null;
                doorCloseTimer = 0;
            }
        }

        // Open doors/fence gates near us if we're trying to path through
        BlockPos feetPos = this.blockPosition();
        for (BlockPos checkPos : new BlockPos[]{feetPos, feetPos.above()}) {
            BlockState bs = this.level().getBlockState(checkPos);
            if (bs.getBlock() instanceof DoorBlock door) {
                if (!bs.getValue(BlockStateProperties.OPEN)) {
                    door.setOpen(null, this.level(), bs, checkPos, true);
                    lastOpenedDoor = checkPos;
                    doorCloseTimer = 0;
                }
            } else if (bs.getBlock() instanceof FenceGateBlock) {
                if (!bs.getValue(BlockStateProperties.OPEN)) {
                    this.level().setBlock(checkPos,
                            bs.setValue(BlockStateProperties.OPEN, true), 3);
                    lastOpenedDoor = checkPos;
                    doorCloseTimer = 0;
                }
            }
        }
    }

    private void handleLeafClearing() {
        if (vtype != null && vtype.noleafclearing) return;
        if (this.tickCount % 10 != 0) return;

        BlockPos headPos = this.blockPosition().above();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos check = headPos.offset(dx, 0, dz);
                BlockState state = this.level().getBlockState(check);
                if (state.getBlock() instanceof LeavesBlock) {
                    this.level().destroyBlock(check, false);
                }
                BlockPos above = check.above();
                BlockState stateAbove = this.level().getBlockState(above);
                if (stateAbove.getBlock() instanceof LeavesBlock) {
                    this.level().destroyBlock(above, false);
                }
            }
        }
    }

    private void detrampleCrops() {
        if (this.tickCount % 20 != 0) return;
        BlockPos below = this.blockPosition().below();
        BlockState state = this.level().getBlockState(below);
        if (state.is(Blocks.DIRT) && this.level().getBlockState(below.above()).isAir()) {
            // Check if farmland was recently trampled → restore it
            BlockPos twoBelow = below.below();
            if (this.level().getBlockState(twoBelow).is(Blocks.WATER) ||
                    this.level().getBlockState(below.north()).is(Blocks.FARMLAND) ||
                    this.level().getBlockState(below.south()).is(Blocks.FARMLAND) ||
                    this.level().getBlockState(below.east()).is(Blocks.FARMLAND) ||
                    this.level().getBlockState(below.west()).is(Blocks.FARMLAND)) {
                this.level().setBlock(below, Blocks.FARMLAND.defaultBlockState(), 3);
            }
        }
    }

    // ========== Night actions (expanded) ==========

    private void performNightActionFull() {
        if (this.level().isDay()) {
            nightActionPerformed = false;
            return;
        }
        if (nightActionPerformed) return;
        nightActionPerformed = true;

        if (isChildVillager()) {
            childAge++;
            if (childAge >= CHILD_GROWTH_AGE) {
                attemptChildGrowth();
            }
            return;
        }

        if (vtype != null && vtype.visitor) {
            visitorNbNights++;
            if (visitorNbNights >= 3) {
                visitorDeparture();
            }
            return;
        }

        // Adult night action: attempt child conception
        if (vtype != null && vtype.hasChildren() && isFemale()) {
            attemptChildConception();
        }
    }

    private void attemptChildConception() {
        org.dizzymii.millenaire2.village.Building home = getHomeBuilding();
        if (home == null) return;

        // Need a male partner in the same building
        boolean hasMalePartner = false;
        for (VillagerRecord vr : home.getVillagerRecords()) {
            if (vr.gender == MALE && !vr.killed && vr.type != null) {
                VillagerType partnerType = vr.getType();
                if (partnerType != null && !partnerType.isChild) {
                    hasMalePartner = true;
                    break;
                }
            }
        }
        if (!hasMalePartner) return;

        // Count existing children
        int childCount = 0;
        for (VillagerRecord vr : home.getVillagerRecords()) {
            if (!vr.killed) {
                VillagerType vrType = vr.getType();
                if (vrType != null && vrType.isChild) {
                    childCount++;
                }
            }
        }
        if (childCount >= 2) return; // Max 2 children per household

        // Random chance per night (~10%)
        if (this.getRandom().nextFloat() > 0.10f) return;

        // Create child
        String childTypeKey = this.getRandom().nextBoolean() ?
                vtype.maleChild : vtype.femaleChild;
        if (childTypeKey == null) {
            childTypeKey = vtype.maleChild != null ? vtype.maleChild : vtype.femaleChild;
        }
        if (childTypeKey == null) return;

        org.dizzymii.millenaire2.culture.Culture culture = getCulture();
        if (culture == null) return;

        VillagerRecord childRecord = new VillagerRecord();
        childRecord.setVillagerId(Math.abs(System.nanoTime() ^ this.getRandom().nextLong()));
        childRecord.setCultureKey(getCultureKey());
        childRecord.type = childTypeKey;
        childRecord.gender = childTypeKey.equals(vtype.maleChild) ? MALE : FEMALE;
        childRecord.setHousePos(housePoint);
        childRecord.setTownHallPos(townHallPoint);
        childRecord.fathersName = getFamilyName();
        childRecord.mothersName = getFirstName();

        // Generate name from culture
        VillagerType childVType = culture.getVillagerType(childTypeKey);
        if (childVType != null) {
            childRecord.firstName = culture.getRandomName(childVType.firstNameList);
            childRecord.familyName = getFamilyName();
        }

        home.addVillagerRecord(childRecord);
        MillLog.minor(this, "Child born: " + childRecord.getName() +
                " in " + (home.getName() != null ? home.getName() : "building"));
    }

    private void attemptChildGrowth() {
        if (vtype == null || getCulture() == null) return;
        org.dizzymii.millenaire2.culture.Culture culture = getCulture();

        // Find the adult type this child grows into
        String adultTypeKey = vtype.altkey;
        if (adultTypeKey == null) return;

        VillagerType adultType = culture.getVillagerType(adultTypeKey);
        if (adultType == null) return;

        this.vtypeKey = adultTypeKey;
        this.vtype = adultType;
        this.childAge = 0;

        MillLog.minor(this, "Child grew up: " + getFirstName() + " → " + adultTypeKey);
    }

    private void visitorDeparture() {
        MillLog.minor(this, "Visitor departing: " + getFirstName() + " " + getFamilyName());
        this.discard();
    }

    // ========== Equipment system ==========

    public ItemStack getBestWeapon() {
        ItemStack best = ItemStack.EMPTY;
        float bestDamage = 0;
        for (Map.Entry<InvItem, Integer> entry : inventory.entrySet()) {
            ItemStack stack = entry.getKey().getItemStack();
            if (stack.getItem() instanceof SwordItem) {
                float dmg = stack.getItem().getDamage(stack);
                if (dmg > bestDamage) {
                    bestDamage = dmg;
                    best = stack;
                }
            }
        }
        return best;
    }

    public ItemStack getBestTool(Class<? extends TieredItem> toolClass) {
        ItemStack best = ItemStack.EMPTY;
        float bestTier = -1;
        for (Map.Entry<InvItem, Integer> entry : inventory.entrySet()) {
            ItemStack stack = entry.getKey().getItemStack();
            if (toolClass.isInstance(stack.getItem())) {
                TieredItem tiered = (TieredItem) stack.getItem();
                float tier = tiered.getTier().getAttackDamageBonus();
                if (tier > bestTier) {
                    bestTier = tier;
                    best = stack;
                }
            }
        }
        return best;
    }

    public ItemStack getBestAxe() { return getBestTool(AxeItem.class); }
    public ItemStack getBestPickaxe() { return getBestTool(PickaxeItem.class); }
    public ItemStack getBestShovel() { return getBestTool(ShovelItem.class); }
    public ItemStack getBestHoe() { return getBestTool(HoeItem.class); }

    public boolean hasBow() {
        for (InvItem item : inventory.keySet()) {
            if (item.getItemStack().getItem() instanceof BowItem) return true;
        }
        return false;
    }

    public ItemStack getWeaponOrBow() {
        if (vtype != null && vtype.isArcher && hasBow()) {
            for (InvItem item : inventory.keySet()) {
                if (item.getItemStack().getItem() instanceof BowItem) return item.getItemStack();
            }
        }
        return getBestWeapon();
    }

    // ========== Speech and dialogue ==========

    public void speakSentence(String key, int variant) {
        this.speech_key = key;
        this.speech_variant = variant;
        this.speech_started = this.level().getGameTime();
    }

    public void clearSpeech() {
        this.speech_key = null;
        this.speech_variant = 0;
        this.speech_started = 0L;
    }

    private void tickDialogue() {
        if (speech_key != null) {
            long elapsed = this.level().getGameTime() - speech_started;
            if (elapsed > 100) { // 5 seconds
                clearSpeech();
            }
        }

        if (dialogueKey != null) {
            long elapsed = this.level().getGameTime() - dialogueStart;
            if (elapsed > 200) { // 10 seconds
                dialogueKey = null;
                dialogueTargetFirstName = null;
                dialogueTargetLastName = null;
                dialogueStart = 0;
            }
        }
    }

    // ========== Villager spawning factory ==========

    @Nullable
    public static MillVillager createVillager(VillagerRecord record, ServerLevel level) {
        if (record == null || level == null) return null;

        VillagerType vt = record.getType();
        int gender = record.gender;

        // Determine entity type based on gender and model
        EntityType<? extends MillVillager> entityType;
        if (gender == FEMALE) {
            if (vt != null && "asymmetrical".equals(vt.model)) {
                entityType = MillEntities.GENERIC_ASYMM_FEMALE.get();
            } else {
                entityType = MillEntities.GENERIC_SYMM_FEMALE.get();
            }
        } else {
            entityType = MillEntities.GENERIC_MALE.get();
        }

        MillVillager villager = entityType.create(level);
        if (villager == null) return null;

        // Apply record data
        villager.setVillagerId(record.getVillagerId());
        villager.setFirstName(record.firstName != null ? record.firstName : "");
        villager.setFamilyName(record.familyName != null ? record.familyName : "");
        villager.setGender(gender);
        villager.setCultureKey(record.getCultureKey() != null ? record.getCultureKey() : "");
        villager.housePoint = record.getHousePos() != null ? new Point(record.getHousePos()) : null;
        villager.townHallPoint = record.getTownHallPos() != null ? new Point(record.getTownHallPos()) : null;
        villager.isRaider = record.raidingVillage;

        if (record.type != null) {
            villager.setVillagerTypeKey(record.type);
        }

        // Copy inventory
        villager.inventory.clear();
        for (Map.Entry<String, Integer> entry : record.inventory.entrySet()) {
            InvItem item = InvItem.get(entry.getKey());
            if (item != null && entry.getValue() > 0) {
                villager.inventory.put(item, entry.getValue());
            }
        }

        // Apply VillagerType attributes
        if (vt != null) {
            villager.getAttribute(Attributes.MAX_HEALTH).setBaseValue(vt.health);
            villager.setHealth(vt.health);
            villager.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(vt.baseSpeed);
            villager.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(vt.baseAttackStrength);
        }

        return villager;
    }

    // ========== Network serialization ==========

    public void writeVillagerStreamData(FriendlyByteBuf buf) {
        buf.writeLong(villagerId);
        buf.writeUtf(getFirstName());
        buf.writeUtf(getFamilyName());
        buf.writeInt(getGender());
        buf.writeUtf(getCultureKey());
        buf.writeUtf(vtypeKey != null ? vtypeKey : "");
        buf.writeBoolean(isRaider);
        buf.writeBoolean(aggressiveStance);
        buf.writeUtf(goalKey != null ? goalKey : "");
        buf.writeFloat(this.getHealth());
        buf.writeFloat(this.getMaxHealth());

        // Position
        buf.writeDouble(this.getX());
        buf.writeDouble(this.getY());
        buf.writeDouble(this.getZ());

        // House/townhall
        buf.writeBoolean(housePoint != null);
        if (housePoint != null) {
            buf.writeInt(housePoint.x);
            buf.writeInt(housePoint.y);
            buf.writeInt(housePoint.z);
        }
        buf.writeBoolean(townHallPoint != null);
        if (townHallPoint != null) {
            buf.writeInt(townHallPoint.x);
            buf.writeInt(townHallPoint.y);
            buf.writeInt(townHallPoint.z);
        }

        // Speech
        buf.writeBoolean(speech_key != null);
        if (speech_key != null) {
            buf.writeUtf(speech_key);
            buf.writeInt(speech_variant);
        }

        // Hired state
        buf.writeBoolean(hiredBy != null);
        if (hiredBy != null) {
            buf.writeUtf(hiredBy);
        }

        // Inventory size
        buf.writeInt(inventory.size());
        for (Map.Entry<InvItem, Integer> entry : inventory.entrySet()) {
            buf.writeUtf(entry.getKey().key);
            buf.writeInt(entry.getValue());
        }
    }

    public void readVillagerStreamData(FriendlyByteBuf buf) {
        villagerId = buf.readLong();
        setFirstName(buf.readUtf());
        setFamilyName(buf.readUtf());
        setGender(buf.readInt());
        setCultureKey(buf.readUtf());
        String vtKey = buf.readUtf();
        if (!vtKey.isEmpty()) setVillagerTypeKey(vtKey);
        isRaider = buf.readBoolean();
        aggressiveStance = buf.readBoolean();
        goalKey = buf.readUtf();
        if (goalKey.isEmpty()) goalKey = null;
        float hp = buf.readFloat();
        float maxHp = buf.readFloat();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
        this.setHealth(hp);

        // Position
        this.setPos(buf.readDouble(), buf.readDouble(), buf.readDouble());

        // House/townhall
        if (buf.readBoolean()) {
            housePoint = new Point(buf.readInt(), buf.readInt(), buf.readInt());
        }
        if (buf.readBoolean()) {
            townHallPoint = new Point(buf.readInt(), buf.readInt(), buf.readInt());
        }

        // Speech
        if (buf.readBoolean()) {
            speech_key = buf.readUtf();
            speech_variant = buf.readInt();
        } else {
            speech_key = null;
        }

        // Hired state
        if (buf.readBoolean()) {
            hiredBy = buf.readUtf();
        } else {
            hiredBy = null;
        }

        // Inventory
        inventory.clear();
        int invSize = buf.readInt();
        for (int i = 0; i < invSize; i++) {
            String key = buf.readUtf();
            int count = buf.readInt();
            InvItem item = InvItem.get(key);
            if (item != null && count > 0) {
                inventory.put(item, count);
            }
        }
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
