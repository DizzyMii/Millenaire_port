package org.dizzymii.millenaire2.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.ai.VillagerAIController;
import org.dizzymii.millenaire2.entity.ai.VillagerCombatHandler;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.goal.GoalInformation;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;
import java.util.HashMap;

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

    // --- NBT key constants ---
    private static final String NBT_FIRST_NAME = "firstName";
    private static final String NBT_FAMILY_NAME = "familyName";
    private static final String NBT_GENDER = "gender";
    private static final String NBT_CULTURE_KEY = "cultureKey";
    private static final String NBT_VILLAGER_ID = "villagerId";
    private static final String NBT_IS_RAIDER = "isRaider";
    private static final String NBT_AGGRESSIVE_STANCE = "aggressiveStance";
    private static final String NBT_HOUSE = "house";
    private static final String NBT_TH = "th";
    private static final String NBT_GOAL_KEY = "goalKey";
    private static final String NBT_HIRED_BY = "hiredBy";
    private static final String NBT_HIRED_UNTIL = "hiredUntil";
    private static final String NBT_VILLAGER_TYPE = "villagerType";
    private static final String NBT_INVENTORY = "millInventory";

    // --- Constants ---
    public static final int MALE = 1;
    public static final int FEMALE = 2;
    public static final int ATTACK_RANGE = 80;
    public static final int ATTACK_RANGE_DEFENSIVE = 20;
    public static final int ARCHER_RANGE = 20;
    public static final int MAX_CHILD_SIZE = 20;
    private static final double DEFAULT_MOVE_SPEED = 0.5;

    public enum DayPhase {
        NIGHT,      // 18000-23999, 0-5999: sleep/hide
        MORNING,    // 6000-7999: eat, prepare
        WORK,       // 8000-11999: construction, type-specific goals
        AFTERNOON,  // 12000-15999: continue work or trade
        EVENING,    // 16000-17999: socialise, go home
    }

    public DayPhase currentPhase = DayPhase.WORK;

    // --- Sub-system controllers ---
    private final VillagerAIController ai = new VillagerAIController(this);
    public final VillagerInventory villagerInventory = new VillagerInventory();

    // --- Instance fields (ported from original) ---
    @Nullable public VillagerType vtype;
    public int action = 0;
    @Nullable public String goalKey = null;
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

    /** Exposes the AI controller for systems that need to interact with it (e.g. combat). */
    public VillagerAIController getAIController() { return ai; }

    /** Syncs the active goal key to clients via entity data. Called by VillagerAIController. */
    public void setGoalKeySync(String key) { this.entityData.set(DATA_GOAL_KEY, key); }

    // --- Goal accessors (delegate to AI controller) ---
    @Nullable public GoalInformation getGoalInformation() { return ai.getGoalInformation(); }
    public void setGoalInformation(@Nullable GoalInformation info) { /* managed by VillagerAIController */ }
    @Nullable public Goal getCurrentGoal() { return ai.getCurrentGoal(); }
    @Nullable public Point getPathDestPoint() { return ai.getPathDestPoint(); }
    public void setPathDestPoint(@Nullable Point p) { ai.setPathDestPoint(p); }

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
        ai.tick();
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
        if (result && !this.level().isClientSide && this.isAlive()) {
            VillagerCombatHandler.onHurt(this, source);
        }
        return result;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide) {
            VillagerCombatHandler.onDeath(this, source);
        }
    }

    // ========== NBT persistence ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString(NBT_FIRST_NAME, getFirstName());
        tag.putString(NBT_FAMILY_NAME, getFamilyName());
        tag.putInt(NBT_GENDER, getGender());
        tag.putString(NBT_CULTURE_KEY, getCultureKey());
        tag.putLong(NBT_VILLAGER_ID, villagerId);
        tag.putBoolean(NBT_IS_RAIDER, isRaider);
        tag.putBoolean(NBT_AGGRESSIVE_STANCE, aggressiveStance);
        if (housePoint != null) {
            housePoint.writeToNBT(tag, NBT_HOUSE);
        }
        if (townHallPoint != null) {
            townHallPoint.writeToNBT(tag, NBT_TH);
        }
        if (goalKey != null) {
            tag.putString(NBT_GOAL_KEY, goalKey);
        }
        if (hiredBy != null) {
            tag.putString(NBT_HIRED_BY, hiredBy);
            tag.putLong(NBT_HIRED_UNTIL, hiredUntil);
        }
        if (vtypeKey != null) {
            tag.putString(NBT_VILLAGER_TYPE, vtypeKey);
        } else if (vtype != null) {
            tag.putString(NBT_VILLAGER_TYPE, vtype.key);
        }
        tag.put(NBT_INVENTORY, villagerInventory.saveToNBT());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setFirstName(tag.getString(NBT_FIRST_NAME));
        setFamilyName(tag.getString(NBT_FAMILY_NAME));
        setGender(tag.getInt(NBT_GENDER));
        setCultureKey(tag.getString(NBT_CULTURE_KEY));
        villagerId = tag.getLong(NBT_VILLAGER_ID);
        isRaider = tag.getBoolean(NBT_IS_RAIDER);
        aggressiveStance = tag.getBoolean(NBT_AGGRESSIVE_STANCE);
        housePoint = Point.readFromNBT(tag, NBT_HOUSE);
        townHallPoint = Point.readFromNBT(tag, NBT_TH);
        if (tag.contains(NBT_GOAL_KEY)) {
            goalKey = tag.getString(NBT_GOAL_KEY);
            ai.restoreGoalFromKey(goalKey);
        }
        if (tag.contains(NBT_HIRED_BY)) {
            hiredBy = tag.getString(NBT_HIRED_BY);
            hiredUntil = tag.getLong(NBT_HIRED_UNTIL);
        }
        if (tag.contains(NBT_INVENTORY, Tag.TAG_LIST)) {
            villagerInventory.loadFromNBT(tag.getList(NBT_INVENTORY, net.minecraft.nbt.Tag.TAG_COMPOUND));
        }
        if (tag.contains(NBT_VILLAGER_TYPE)) {
            setVillagerTypeKey(tag.getString(NBT_VILLAGER_TYPE));
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

    // --- Inventory helpers (delegate to VillagerInventory) ---
    public int countInv(InvItem item) { return villagerInventory.count(item); }
    public void addToInv(InvItem item, int count) { villagerInventory.add(item, count); }
    public void removeFromInv(InvItem item, int count) { villagerInventory.remove(item, count); }

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
