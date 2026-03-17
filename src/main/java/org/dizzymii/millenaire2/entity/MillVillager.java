package org.dizzymii.millenaire2.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.goal.MillGoal;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;

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

    // --- Constants ---
    public static final int MALE = 1;
    public static final int FEMALE = 2;
    public static final int ATTACK_RANGE = 80;
    public static final int ATTACK_RANGE_DEFENSIVE = 20;
    public static final int ARCHER_RANGE = 20;
    public static final int MAX_CHILD_SIZE = 20;
    private static final double DEFAULT_MOVE_SPEED = 0.5;

    // --- Instance fields (ported from original) ---
    public VillagerType vtype;
    public int action = 0;
    public String goalKey = null;
    private MillGoal.GoalInformation goalInformation = null;
    private Point pathDestPoint;
    public Point housePoint = null;
    public Point prevPoint = null;
    public Point townHallPoint = null;
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
    public String speech_key = null;
    public int speech_variant = 0;
    public String dialogueKey = null;
    public int dialogueRole = 0;
    public long dialogueStart = 0L;
    public boolean dialogueChat = false;
    public String dialogueTargetFirstName = null;
    public String dialogueTargetLastName = null;
    public int visitorNbNights = 0;
    public int foreignMerchantStallId = -1;
    public boolean lastAttackByPlayer = false;
    public HashMap<MillGoal, Long> lastGoalTime = new HashMap<>();
    public String hiredBy = null;
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
    }

    // --- Name accessors ---
    public String getFirstName() { return this.entityData.get(DATA_FIRST_NAME); }
    public void setFirstName(String name) { this.entityData.set(DATA_FIRST_NAME, name); }
    public String getFamilyName() { return this.entityData.get(DATA_FAMILY_NAME); }
    public void setFamilyName(String name) { this.entityData.set(DATA_FAMILY_NAME, name); }
    public int getGender() { return this.entityData.get(DATA_GENDER); }
    public void setGender(int gender) { this.entityData.set(DATA_GENDER, gender); }

    public long getVillagerId() { return villagerId; }
    public void setVillagerId(long id) { this.villagerId = id; }

    public boolean isMale() { return getGender() == MALE; }
    public boolean isFemale() { return getGender() == FEMALE; }

    // --- Goal accessors ---
    public MillGoal.GoalInformation getGoalInformation() { return goalInformation; }
    public void setGoalInformation(MillGoal.GoalInformation info) { this.goalInformation = info; }
    public Point getPathDestPoint() { return pathDestPoint; }
    public void setPathDestPoint(Point p) { this.pathDestPoint = p; }

    // --- NBT persistence ---
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("firstName", getFirstName());
        tag.putString("familyName", getFamilyName());
        tag.putInt("gender", getGender());
        tag.putLong("villagerId", villagerId);
        tag.putBoolean("isRaider", isRaider);
        tag.putBoolean("aggressiveStance", aggressiveStance);
        if (housePoint != null) {
            tag.putInt("houseX", housePoint.x);
            tag.putInt("houseY", housePoint.y);
            tag.putInt("houseZ", housePoint.z);
        }
        if (townHallPoint != null) {
            tag.putInt("thX", townHallPoint.x);
            tag.putInt("thY", townHallPoint.y);
            tag.putInt("thZ", townHallPoint.z);
        }
        if (goalKey != null) {
            tag.putString("goalKey", goalKey);
        }
        if (hiredBy != null) {
            tag.putString("hiredBy", hiredBy);
            tag.putLong("hiredUntil", hiredUntil);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setFirstName(tag.getString("firstName"));
        setFamilyName(tag.getString("familyName"));
        setGender(tag.getInt("gender"));
        villagerId = tag.getLong("villagerId");
        isRaider = tag.getBoolean("isRaider");
        aggressiveStance = tag.getBoolean("aggressiveStance");
        if (tag.contains("houseX")) {
            housePoint = new Point(tag.getInt("houseX"), tag.getInt("houseY"), tag.getInt("houseZ"));
        }
        if (tag.contains("thX")) {
            townHallPoint = new Point(tag.getInt("thX"), tag.getInt("thY"), tag.getInt("thZ"));
        }
        if (tag.contains("goalKey")) {
            goalKey = tag.getString("goalKey");
        }
        if (tag.contains("hiredBy")) {
            hiredBy = tag.getString("hiredBy");
            hiredUntil = tag.getLong("hiredUntil");
        }
    }

    // --- Inventory helpers ---
    public int countInv(InvItem item) {
        return inventory.getOrDefault(item, 0);
    }

    public void addToInv(InvItem item, int count) {
        inventory.merge(item, count, Integer::sum);
        if (inventory.get(item) <= 0) {
            inventory.remove(item);
        }
    }

    public void removeFromInv(InvItem item, int count) {
        addToInv(item, -count);
    }

    // TODO: Full behavior (tick logic, goal execution, pathing, combat, etc.) in later phases
}
