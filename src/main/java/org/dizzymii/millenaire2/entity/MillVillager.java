package org.dizzymii.millenaire2.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.FamilyData;

import javax.annotation.Nullable;
import java.util.HashMap;
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
    private static final EntityDataAccessor<String> DATA_SPOUSE_NAME =
            SynchedEntityData.defineId(MillVillager.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_ANIM_STATE =
            SynchedEntityData.defineId(MillVillager.class, EntityDataSerializers.INT);

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
    public final FamilyData familyData = new FamilyData();

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
        builder.define(DATA_SPOUSE_NAME, "");
        builder.define(DATA_ANIM_STATE, VillagerAnimState.IDLE.getId());
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
    public String getSpouseName() { return this.entityData.get(DATA_SPOUSE_NAME); }
    public void setSpouseName(String name) { this.entityData.set(DATA_SPOUSE_NAME, name); }
    public VillagerAnimState getAnimState() { return VillagerAnimState.fromId(this.entityData.get(DATA_ANIM_STATE)); }
    public void setAnimState(VillagerAnimState state) { this.entityData.set(DATA_ANIM_STATE, state.getId()); }

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
        goalTickCounter++;
        if (goalTickCounter >= GOAL_TICK_INTERVAL) {
            goalTickCounter = 0;
            tickGoalSelection();
        }
        tickGoalExecution();
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
        long dayTime = this.level().getDayTime() % 24000;
        int hour = (int) (dayTime / 1000 + 6) % 24;
        boolean isNight = !this.level().isDay();

        // TOP PRIORITY: If there is active construction, work on it regardless of time
        // (Original Millenaire: villagers build as soon as construction is queued)
        if (!isNight) {
            if (tryActivateGoal("construction", Goal.construction)) return;
            if (tryActivateGoal("getresourcesforbuild", Goal.getResourcesForBuild)) return;
        }

        // Night: sleep at home
        if (isNight) {
            if (tryActivateGoal("sleep", Goal.sleep)) return;
            if (tryGoHome()) return;
            return;
        }

        // Daytime: run villager-type-specific goals (farming, lumbering, fishing, etc.)
        if (vtype != null && !vtype.goals.isEmpty()) {
            for (String gKey : vtype.goals) {
                Goal g = Goal.goals.get(gKey);
                if (g == null) continue;
                if (!g.canBeDoneInDayTime()) continue;

                if (g.minimumHour >= 0 && hour < g.minimumHour) continue;
                if (g.maximumHour >= 0 && hour > g.maximumHour) continue;

                Long lastTime = lastGoalTime.get(g);
                if (lastTime != null && (this.level().getGameTime() - lastTime) < Goal.STANDARD_DELAY / 50) {
                    continue;
                }

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

        // Evening (18-20): socialise, eat
        if (hour >= 18 && hour < 20) {
            if (tryActivateGoal("gosocialise", Goal.gosocialise)) return;
            if (tryActivateGoal("eatatinn", Goal.goals.get("eatatinn"))) return;
        }

        // Late evening (20+): go home
        if (hour >= 20) {
            if (tryGoHome()) return;
        }

        // Fallback: socialise or wander near home
        if (tryActivateGoal("gosocialise", Goal.gosocialise)) return;

        Point wanderTarget = housePoint != null ? housePoint : townHallPoint;
        if (wanderTarget != null) {
            int dx = this.getRandom().nextInt(11) - 5;
            int dz = this.getRandom().nextInt(11) - 5;
            Point wander = new Point(wanderTarget.x + dx, wanderTarget.y, wanderTarget.z + dz);
            this.getNavigation().moveTo(wander.x + 0.5, wander.y, wander.z + 0.5, 0.5);
        }
    }

    private boolean tryActivateGoal(String key, @Nullable Goal g) {
        if (g == null) return false;
        try {
            GoalInformation info = g.getDestination(this);
            if (info != null && info.hasTarget()) {
                setActiveGoal(key, g, info);
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    private boolean tryGoHome() {
        Point home = housePoint != null ? housePoint : townHallPoint;
        if (home == null) return false;
        double dist = this.distanceToSqr(home.x + 0.5, home.y, home.z + 0.5);
        if (dist < 4.0) return false;
        this.getNavigation().moveTo(home.x + 0.5, home.y, home.z + 0.5, 0.6);
        return true;
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

    private void clearGoal() {
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
        if (goalInformation.targetPoint != null) {
            double dist = this.distanceToSqr(
                    goalInformation.targetPoint.x + 0.5,
                    goalInformation.targetPoint.y,
                    goalInformation.targetPoint.z + 0.5
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
        // Save inventory
        ListTag invList = new ListTag();
        for (Map.Entry<InvItem, Integer> entry : inventory.entrySet()) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.putString("key", entry.getKey().key);
            itemTag.putInt("count", entry.getValue());
            invList.add(itemTag);
        }
        tag.put("millInventory", invList);
        tag.put("familyData", familyData.save());
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
        if (tag.contains("familyData")) {
            familyData.load(tag.getCompound("familyData"));
            setSpouseName(familyData.getSpouseName());
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

    // --- Marriage helpers ---

    /**
     * Marries this villager to another. Updates both villagers' family data and synched spouse name.
     */
    public void marryTo(MillVillager partner) {
        String thisFullName = getFirstName() + " " + getFamilyName();
        String partnerFullName = partner.getFirstName() + " " + partner.getFamilyName();

        this.familyData.marry(partner.getVillagerId(), partnerFullName);
        partner.familyData.marry(this.getVillagerId(), thisFullName);

        // Female takes male's family name (with maiden name preserved)
        if (this.isFemale()) {
            this.familyData.setMaidenName(this.getFamilyName());
            this.setFamilyName(partner.getFamilyName());
        } else if (partner.isFemale()) {
            partner.familyData.setMaidenName(partner.getFamilyName());
            partner.setFamilyName(this.getFamilyName());
        }

        this.setSpouseName(partnerFullName);
        partner.setSpouseName(thisFullName);
    }

    public boolean isMarried() { return familyData.isMarried(); }
    public boolean isChildVillager() { return familyData.isChild(); }

    // --- Held item rendering ---

    /**
     * Syncs the custom heldItem/heldItemOffHand fields into vanilla equipment slots
     * so that the built-in ItemInHandLayer renders them.
     * Call this whenever heldItem or heldItemOffHand changes.
     */
    public void syncHeldItems() {
        this.setItemSlot(EquipmentSlot.MAINHAND, heldItem);
        this.setItemSlot(EquipmentSlot.OFFHAND, heldItemOffHand);
    }

    /**
     * Sets the main hand item and syncs it to the equipment slot for rendering.
     */
    public void setHeldItem(ItemStack stack) {
        this.heldItem = stack;
        this.setItemSlot(EquipmentSlot.MAINHAND, stack);
    }

    /**
     * Sets the off-hand item and syncs it to the equipment slot for rendering.
     */
    public void setHeldItemOffHand(ItemStack stack) {
        this.heldItemOffHand = stack;
        this.setItemSlot(EquipmentSlot.OFFHAND, stack);
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
