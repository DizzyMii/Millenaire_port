/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBed
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.BlockFenceGate
 *  net.minecraft.block.BlockHorizontal
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCreature
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.monster.EntityCreeper
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntityArrow
 *  net.minecraft.entity.projectile.EntityTippedArrow
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemTool
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.pathfinding.Path
 *  net.minecraft.pathfinding.PathPoint
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumHandSide
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.EnumDifficulty
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
 */
package org.millenaire.common.entity;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.block.BlockFruitLeaves;
import org.millenaire.common.block.BlockMillCrops;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.CultureLanguage;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.entity.VillagerConfig;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.ItemClothes;
import org.millenaire.common.item.ItemMillenaireBow;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.pathing.atomicstryker.AS_PathEntity;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.pathing.atomicstryker.AStarPathPlannerJPS;
import org.millenaire.common.pathing.atomicstryker.AStarStatic;
import org.millenaire.common.pathing.atomicstryker.IAStarPathedEntity;
import org.millenaire.common.quest.QuestInstance;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.BlockStateUtilities;
import org.millenaire.common.utilities.DevModUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.ThreadSafeUtilities;
import org.millenaire.common.utilities.VillageUtilities;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.village.ConstructionIP;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public abstract class MillVillager
extends EntityCreature
implements IEntityAdditionalSpawnData,
IAStarPathedEntity {
    private static final UUID SPRINT_SPEED_BOOST_ID = UUID.fromString("B9766B59-8456-5632-BC1F-2EE2A276D836");
    private static final AttributeModifier SPRINT_SPEED_BOOST = new AttributeModifier(SPRINT_SPEED_BOOST_ID, "Sprint speed boost", 0.1, 1);
    private static final double DEFAULT_MOVE_SPEED = 0.5;
    public static final int ATTACK_RANGE_DEFENSIVE = 20;
    private static final String FREE_CLOTHES = "free";
    private static final String NATURAL = "natural";
    private static final int CONCEPTION_CHANCE = 2;
    private static final int VISITOR_NB_NIGHTS_BEFORE_LEAVING = 5;
    public static final int MALE = 1;
    public static final int FEMALE = 2;
    public static final ResourceLocation GENERIC_VILLAGER = new ResourceLocation("millenaire", "GenericVillager");
    public static final ResourceLocation GENERIC_ASYMM_FEMALE = new ResourceLocation("millenaire", "GenericAsimmFemale");
    public static final ResourceLocation GENERIC_SYMM_FEMALE = new ResourceLocation("millenaire", "GenericSimmFemale");
    public static final ResourceLocation GENERIC_ZOMBIE = new ResourceLocation("millenaire", "GenericZombie");
    private static ItemStack[] WOODDEN_HOE_STACK = new ItemStack[]{new ItemStack(Items.field_151017_I, 1)};
    private static ItemStack[] WOODDEN_SHOVEL_STACK = new ItemStack[]{new ItemStack(Items.field_151038_n, 1)};
    private static ItemStack[] WOODDEN_PICKAXE_STACK = new ItemStack[]{new ItemStack(Items.field_151039_o, 1)};
    private static ItemStack[] WOODDEN_AXE_STACK = new ItemStack[]{new ItemStack(Items.field_151053_p, 1)};
    static final int GATHER_RANGE = 20;
    private static final int HOLD_DURATION = 20;
    public static final int ATTACK_RANGE = 80;
    public static final int ARCHER_RANGE = 20;
    public static final int MAX_CHILD_SIZE = 20;
    private static final AStarConfig JPS_CONFIG_DEFAULT = new AStarConfig(true, false, false, false, true);
    private static final AStarConfig JPS_CONFIG_NO_LEAVES = new AStarConfig(true, false, false, false, false);
    public VillagerType vtype;
    public int action = 0;
    public String goalKey = null;
    private Goal.GoalInformation goalInformation = null;
    private Point pathDestPoint;
    private Building house = null;
    private Building townHall = null;
    public Point housePoint = null;
    public Point prevPoint = null;
    public Point townHallPoint = null;
    public boolean extraLog = false;
    public String firstName = "";
    public String familyName = "";
    public ItemStack heldItem = ItemStack.field_190927_a;
    public ItemStack heldItemOffHand = ItemStack.field_190927_a;
    public long timer = 0L;
    public long actionStart = 0L;
    public boolean allowRandomMoves = false;
    public boolean stopMoving = false;
    public int gender = 0;
    public boolean registered = false;
    public int longDistanceStuck;
    public boolean nightActionPerformed = false;
    public long speech_started = 0L;
    public HashMap<InvItem, Integer> inventory;
    public Block previousBlock;
    public int previousBlockMeta;
    public long pathingTime;
    public long timeSinceLastPathingTimeDisplay;
    private long villagerId = -1L;
    public int nbPathsCalculated = 0;
    public int nbPathNoStart = 0;
    public int nbPathNoEnd = 0;
    public int nbPathAborted = 0;
    public int nbPathFailure = 0;
    public long goalStarted = 0L;
    public int constructionJobId = -1;
    public int heldItemCount = 0;
    public int heldItemId = -1;
    public int heldItemOffHandId = -1;
    public String speech_key = null;
    public int speech_variant = 0;
    public String dialogueKey = null;
    public int dialogueRole = 0;
    public long dialogueStart = 0L;
    public char dialogueColour = (char)102;
    public boolean dialogueChat = false;
    public String dialogueTargetFirstName = null;
    public String dialogueTargetLastName = null;
    private Point doorToClose = null;
    public int visitorNbNights = 0;
    public int foreignMerchantStallId = -1;
    public boolean lastAttackByPlayer = false;
    public HashMap<Goal, Long> lastGoalTime = new HashMap();
    public String hiredBy = null;
    public boolean aggressiveStance = false;
    public long hiredUntil = 0L;
    public boolean isUsingBow;
    public boolean isUsingHandToHand;
    public boolean isRaider = false;
    public AStarPathPlannerJPS pathPlannerJPS;
    public AS_PathEntity pathEntity;
    public int updateCounter = 0;
    public long client_lastupdated;
    public MillWorldData mw;
    private boolean pathFailedSincelastTick = false;
    private List<AStarNode> pathCalculatedSinceLastTick = null;
    private int localStuck = 0;
    private final ResourceLocation[] clothTexture = new ResourceLocation[2];
    private String clothName = null;
    public boolean shouldLieDown = false;
    public LinkedHashMap<TradeGood, Integer> merchantSells = new LinkedHashMap();
    public ResourceLocation texture = null;
    private int attackTime;
    public boolean isDeadOnServer = false;
    public boolean travelBookMockVillager = false;

    public static MillVillager createMockVillager(VillagerRecord villagerRecord, World world) {
        MillVillager villager = (MillVillager)EntityList.func_188429_b((ResourceLocation)villagerRecord.getType().getEntityName(), (World)world);
        if (villager == null) {
            MillLog.error(villagerRecord, "Could not create mock villager of dynamic type: " + villagerRecord.getType() + " entity: " + villagerRecord.getType().getEntityName());
            return null;
        }
        villager.vtype = villagerRecord.getType();
        villager.gender = villagerRecord.getType().gender;
        villager.firstName = villagerRecord.firstName;
        villager.familyName = villagerRecord.familyName;
        villager.texture = villagerRecord.texture;
        villager.func_70606_j(villager.func_110138_aP());
        villager.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a((double)villagerRecord.getType().health);
        villager.updateClothTexturePath();
        return villager;
    }

    public static MillVillager createVillager(VillagerRecord villagerRecord, World world, Point spawnPos, boolean respawn) {
        if (world.field_72995_K || !(world instanceof WorldServer)) {
            MillLog.printException("Tried creating a villager in client world: " + world, new Exception());
            return null;
        }
        if (villagerRecord == null) {
            MillLog.error(villagerRecord, "Tried creating villager from a null record");
            return null;
        }
        if (villagerRecord.getType() == null) {
            MillLog.error(null, "Tried creating villager of null type: " + villagerRecord.getType());
            return null;
        }
        MillVillager villager = (MillVillager)EntityList.func_188429_b((ResourceLocation)villagerRecord.getType().getEntityName(), (World)world);
        if (villager == null) {
            MillLog.error(villagerRecord, "Could not create villager of dynamic type: " + villagerRecord.getType() + " entity: " + villagerRecord.getType().getEntityName());
            return null;
        }
        villager.housePoint = villagerRecord.getHousePos();
        villager.townHallPoint = villagerRecord.getTownHallPos();
        villager.vtype = villagerRecord.getType();
        villager.setVillagerId(villagerRecord.getVillagerId());
        villager.gender = villagerRecord.getType().gender;
        villager.firstName = villagerRecord.firstName;
        villager.familyName = villagerRecord.familyName;
        villager.texture = villagerRecord.texture;
        villager.func_70606_j(villager.func_110138_aP());
        villager.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a((double)villagerRecord.getType().health);
        villager.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a((double)villagerRecord.getType().baseSpeed);
        villager.updateClothTexturePath();
        if (!respawn) {
            for (InvItem item : villagerRecord.getType().startingInv.keySet()) {
                villager.addToInv(item.getItem(), item.meta, (int)villagerRecord.getType().startingInv.get(item));
            }
        }
        villager.func_70107_b(spawnPos.x, spawnPos.y, spawnPos.z);
        if (MillConfigValues.LogVillagerSpawn >= 1) {
            MillLog.major(villager, "Created new villager from record.");
        }
        return villager;
    }

    public static void readVillagerPacket(PacketBuffer data) {
        try {
            long villager_id = data.readLong();
            if (Mill.clientWorld.getVillagerById(villager_id) != null) {
                Mill.clientWorld.getVillagerById(villager_id).readVillagerStreamdata(data);
            } else if (MillConfigValues.LogNetwork >= 2) {
                MillLog.minor(null, "readVillagerPacket for unknown villager: " + villager_id);
            }
        }
        catch (IOException e) {
            MillLog.printException(e);
        }
    }

    public MillVillager(World world) {
        super(world);
        this.field_70170_p = world;
        this.mw = Mill.getMillWorld(world);
        this.inventory = new HashMap();
        this.func_70606_j(this.func_110138_aP());
        this.field_70178_ae = true;
        this.client_lastupdated = world.func_72820_D();
        if (!world.field_72995_K) {
            this.pathPlannerJPS = new AStarPathPlannerJPS(world, this, MillConfigValues.jpsPathing);
        }
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5);
        if (MillConfigValues.LogVillagerSpawn >= 3) {
            Exception e = new Exception();
            MillLog.printException("Creating villager " + this + " in world: " + world, e);
        }
    }

    public void addToInv(Block block, int nb) {
        this.addToInv(Item.func_150898_a((Block)block), 0, nb);
    }

    public void addToInv(Block block, int meta, int nb) {
        this.addToInv(Item.func_150898_a((Block)block), meta, nb);
    }

    public void addToInv(IBlockState bs, int nb) {
        this.addToInv(Item.func_150898_a((Block)bs.func_177230_c()), bs.func_177230_c().func_176201_c(bs), nb);
    }

    public void addToInv(InvItem iv, int nb) {
        this.addToInv(iv.getItem(), iv.meta, nb);
    }

    public void addToInv(Item item, int nb) {
        this.addToInv(item, 0, nb);
    }

    public void addToInv(Item item, int meta, int nb) {
        InvItem key = InvItem.createInvItem(item, meta);
        if (this.inventory.containsKey(key)) {
            this.inventory.put(key, this.inventory.get(key) + nb);
        } else {
            this.inventory.put(key, nb);
        }
        this.updateVillagerRecord();
        this.updateClothTexturePath();
    }

    protected void func_110147_ax() {
        super.func_110147_ax();
        this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5);
        this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a((double)this.computeMaxHealth());
    }

    private void applyPathCalculatedSinceLastTick() {
        try {
            AS_PathEntity path = AStarStatic.translateAStarPathtoPathEntity(this.field_70170_p, this.pathCalculatedSinceLastTick, this.getPathingConfig());
            this.registerNewPath(path);
        }
        catch (Exception e) {
            MillLog.printException("Exception when finding JPS path:", e);
        }
        this.pathCalculatedSinceLastTick = null;
    }

    public boolean attackEntity(Entity entity) {
        double distance = this.getPos().distanceTo(entity);
        if (this.vtype.isArcher && distance > 5.0 && this.hasBow()) {
            this.isUsingBow = true;
            this.attackEntity_testHiredGoon(entity);
            if (distance < 20.0 && entity instanceof EntityLivingBase) {
                if (this.attackTime <= 0) {
                    this.attackTime = 100;
                    this.func_184609_a(EnumHand.MAIN_HAND);
                    float distanceFactor = (float)(distance / 20.0);
                    distanceFactor = MathHelper.func_76131_a((float)distanceFactor, (float)0.1f, (float)1.0f);
                    this.attackEntityWithRangedAttack((EntityLivingBase)entity, distanceFactor);
                } else {
                    --this.attackTime;
                }
            }
        } else {
            if (this.attackTime <= 0 && distance < 2.0 && entity.func_174813_aQ().field_72337_e > this.func_174813_aQ().field_72338_b && entity.func_174813_aQ().field_72338_b < this.func_174813_aQ().field_72337_e) {
                this.attackTime = 20;
                this.func_184609_a(EnumHand.MAIN_HAND);
                this.attackEntity_testHiredGoon(entity);
                return entity.func_70097_a(DamageSource.func_76358_a((EntityLivingBase)this), (float)this.getAttackStrength());
            }
            --this.attackTime;
            this.isUsingHandToHand = true;
        }
        return true;
    }

    private void attackEntity_testHiredGoon(Entity targetedEntity) {
        EntityPlayer owner;
        if (targetedEntity instanceof EntityPlayer && this.hiredBy != null && (owner = this.field_70170_p.func_72924_a(this.hiredBy)) != null && owner != targetedEntity) {
            MillAdvancements.MP_HIREDGOON.grant(owner);
        }
    }

    public boolean func_70097_a(DamageSource ds, float i) {
        if (ds.func_76346_g() == null && ds != DamageSource.field_76380_i) {
            return false;
        }
        boolean hadFullHealth = this.func_110138_aP() == this.func_110143_aJ();
        boolean b = super.func_70097_a(ds, i);
        Entity entity = ds.func_76346_g();
        this.lastAttackByPlayer = false;
        if (entity != null && entity instanceof EntityLivingBase) {
            if (entity instanceof EntityPlayer) {
                if (!((EntityPlayer)entity).func_175149_v() && !((EntityPlayer)entity).func_184812_l_()) {
                    this.lastAttackByPlayer = true;
                    EntityPlayer player = (EntityPlayer)entity;
                    if (!this.isRaider) {
                        UserProfile serverProfile;
                        if (this.vtype != null && !this.vtype.hostile && (serverProfile = VillageUtilities.getServerProfile(player.field_70170_p, player)) != null) {
                            serverProfile.adjustReputation(this.getTownHall(), (int)(-i * 10.0f));
                        }
                        if (this.field_70170_p.func_175659_aa() != EnumDifficulty.PEACEFUL && this.func_110143_aJ() < this.func_110138_aP() - 10.0f) {
                            this.func_70624_b((EntityLivingBase)entity);
                            this.clearGoal();
                            if (this.getTownHall() != null) {
                                this.getTownHall().callForHelp((EntityLivingBase)entity);
                            }
                        }
                        if (this.vtype != null && !this.vtype.hostile && hadFullHealth && (player.func_184586_b(EnumHand.MAIN_HAND) == null || MillCommonUtilities.getItemWeaponDamage(player.func_184607_cu().func_77973_b()) <= 1.0) && !this.field_70170_p.field_72995_K) {
                            ServerSender.sendTranslatedSentence(player, '6', "ui.communicationexplanations", new String[0]);
                        }
                    }
                    if (this.lastAttackByPlayer && this.func_110143_aJ() <= 0.0f) {
                        if (this.vtype != null && this.vtype.hostile) {
                            MillAdvancements.SELF_DEFENSE.grant(player);
                        } else {
                            MillAdvancements.DARK_SIDE.grant(player);
                        }
                    }
                }
            } else if (entity instanceof MillVillager) {
                MillVillager attackingVillager = (MillVillager)entity;
                if (this.isRaider != attackingVillager.isRaider || this.getTownHall() != attackingVillager.getTownHall()) {
                    this.func_70624_b((EntityLivingBase)entity);
                    this.clearGoal();
                    if (this.getTownHall() != null) {
                        this.getTownHall().callForHelp((EntityLivingBase)entity);
                    }
                }
            } else {
                this.func_70624_b((EntityLivingBase)entity);
                this.clearGoal();
                if (this.getTownHall() != null) {
                    this.getTownHall().callForHelp((EntityLivingBase)entity);
                }
            }
        }
        return b;
    }

    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
        Item item;
        EntityArrow entityarrow = this.getArrow(distanceFactor);
        double d0 = target.field_70165_t - this.field_70165_t;
        double d1 = target.func_174813_aQ().field_72338_b + (double)(target.field_70131_O / 3.0f) - entityarrow.field_70163_u;
        double d2 = target.field_70161_v - this.field_70161_v;
        double d3 = MathHelper.func_76133_a((double)(d0 * d0 + d2 * d2));
        float speedFactor = 1.0f;
        float damageBonus = 0.0f;
        ItemStack weapon = this.getWeapon();
        if (weapon != null && (item = weapon.func_77973_b()) instanceof ItemMillenaireBow) {
            ItemMillenaireBow bow = (ItemMillenaireBow)item;
            if (bow.speedFactor > speedFactor) {
                speedFactor = bow.speedFactor;
            }
            if (bow.damageBonus > damageBonus) {
                damageBonus = bow.damageBonus;
            }
        }
        entityarrow.func_70239_b(entityarrow.func_70242_d() + (double)damageBonus);
        entityarrow.func_70186_c(d0, d1 + d3 * (double)0.2f, d2, 1.6f, (float)(14 - this.field_70170_p.func_175659_aa().func_151525_a() * 4) * speedFactor);
        this.func_184185_a(SoundEvents.field_187866_fi, 1.0f, 1.0f / (this.func_70681_au().nextFloat() * 0.4f + 0.8f));
        this.field_70170_p.func_72838_d((Entity)entityarrow);
    }

    public boolean attemptChildConception() {
        int nbChildren = 0;
        for (MillVillager villager : this.getHouse().getKnownVillagers()) {
            if (!villager.func_70631_g_()) continue;
            ++nbChildren;
        }
        if (nbChildren > 1) {
            if (MillConfigValues.LogChildren >= 3) {
                MillLog.debug(this, "Wife already has " + nbChildren + " children, no need for more.");
            }
            return true;
        }
        int nbChildVillage = this.getTownHall().countChildren();
        if (nbChildVillage > MillConfigValues.maxChildrenNumber) {
            if (MillConfigValues.LogChildren >= 3) {
                MillLog.debug(this, "Village already has " + nbChildVillage + ", no need for more.");
            }
            return true;
        }
        boolean couldMoveIn = false;
        for (Point housePoint : this.getTownHall().buildings) {
            Building house = this.mw.getBuilding(housePoint);
            if (house == null || house.equals(this.getHouse()) || !house.isHouse() || !house.canChildMoveIn(1, this.familyName) && !house.canChildMoveIn(2, this.familyName)) continue;
            couldMoveIn = true;
        }
        if (nbChildVillage > 5 && !couldMoveIn) {
            if (MillConfigValues.LogChildren >= 3) {
                MillLog.debug(this, "Village already has " + nbChildVillage + " and no slot is available for the new child.");
            }
            return true;
        }
        List<Entity> entities = WorldUtilities.getEntitiesWithinAABB(this.field_70170_p, MillVillager.class, this.getPos(), 4, 2);
        boolean manFound = false;
        for (Entity ent : entities) {
            MillVillager villager = (MillVillager)ent;
            if (villager.gender != 1 || villager.func_70631_g_()) continue;
            manFound = true;
        }
        if (!manFound) {
            return false;
        }
        if (MillConfigValues.LogChildren >= 3) {
            MillLog.debug(this, "Less than two kids and man present, trying for new child.");
        }
        boolean createChild = false;
        int conceptionChances = 2;
        InvItem conceptionFood = this.getConfig().getBestConceptionFood(this.getHouse());
        if (conceptionFood != null) {
            this.getHouse().takeGoods(conceptionFood, 1);
            conceptionChances += this.getConfig().foodsConception.get(conceptionFood).intValue();
        }
        if (MillCommonUtilities.randomInt(10) < conceptionChances) {
            createChild = true;
            if (MillConfigValues.LogChildren >= 2) {
                MillLog.minor(this, "Conceiving child. Food available: " + conceptionFood);
            }
        } else if (MillConfigValues.LogChildren >= 2) {
            MillLog.minor(this, "Failed to conceive child. Food available: " + conceptionFood);
        }
        if (MillConfigValues.DEV) {
            createChild = true;
        }
        if (createChild) {
            this.getHouse().createChild(this, this.getTownHall(), this.getRecord().spousesName);
        }
        return true;
    }

    public void calculateMerchantGoods() {
        for (InvItem key : this.vtype.foreignMerchantStock.keySet()) {
            if (this.getCulture().getTradeGood(key) == null || this.getBasicForeignMerchantPrice(key) <= 0) continue;
            this.merchantSells.put(this.getCulture().getTradeGood(key), this.getBasicForeignMerchantPrice(key));
        }
    }

    public boolean func_184652_a(EntityPlayer player) {
        return false;
    }

    public boolean func_70692_ba() {
        return false;
    }

    public boolean canMeditate() {
        return this.vtype.canMeditate;
    }

    public boolean canPerformSacrifices() {
        return this.vtype.canPerformSacrifices;
    }

    public boolean canVillagerClearLeaves() {
        return !this.vtype.noleafclearing;
    }

    private void checkGoalHeldItems(Goal goal, Point target) throws Exception {
        if (this.heldItemCount > 20) {
            ItemStack[] heldItems = null;
            heldItems = target != null && target.horizontalDistanceTo((Entity)this) < (double)goal.range(this) ? goal.getHeldItemsDestination(this) : goal.getHeldItemsTravelling(this);
            if (heldItems != null && heldItems.length > 0) {
                this.heldItemId = (this.heldItemId + 1) % heldItems.length;
                this.heldItem = heldItems[this.heldItemId];
            }
            heldItems = null;
            heldItems = target != null && target.horizontalDistanceTo((Entity)this) < (double)goal.range(this) ? goal.getHeldItemsOffHandDestination(this) : goal.getHeldItemsOffHandTravelling(this);
            if (heldItems != null && heldItems.length > 0) {
                this.heldItemOffHandId = (this.heldItemOffHandId + 1) % heldItems.length;
                this.heldItemOffHand = heldItems[this.heldItemOffHandId];
            }
            this.heldItemCount = 0;
        }
        if (this.heldItemCount == 0 && goal.swingArms(this)) {
            this.func_184609_a(EnumHand.MAIN_HAND);
        }
        ++this.heldItemCount;
    }

    public void checkGoals() throws Exception {
        Goal goal = Goal.goals.get(this.goalKey);
        if (goal == null) {
            MillLog.error(this, "Invalid goal key: " + this.goalKey);
            this.goalKey = null;
            return;
        }
        if (this.getGoalDestEntity() != null) {
            if (this.getGoalDestEntity().field_70128_L) {
                this.setGoalDestEntity(null);
                this.setPathDestPoint(null, 0);
            } else {
                this.setPathDestPoint(new Point(this.getGoalDestEntity()), 2);
            }
        }
        Point target = null;
        boolean continuingGoal = true;
        if (this.getPathDestPoint() != null) {
            target = this.getPathDestPoint();
            if (this.pathEntity != null && this.pathEntity.func_75874_d() > 0) {
                target = new Point(this.pathEntity.func_75870_c());
            }
        }
        this.speakSentence(goal.sentenceKey());
        if (this.getGoalDestPoint() == null && this.getGoalDestEntity() == null) {
            goal.setVillagerDest(this);
            if (MillConfigValues.LogGeneralAI >= 2 && this.extraLog) {
                MillLog.minor(this, "Goal destination: " + this.getGoalDestPoint() + "/" + this.getGoalDestEntity());
            }
        } else if (target != null && target.horizontalDistanceTo((Entity)this) < (double)goal.range(this)) {
            if (this.actionStart == 0L) {
                this.stopMoving = goal.stopMovingWhileWorking();
                this.actionStart = this.field_70170_p.func_72820_D();
                this.shouldLieDown = goal.shouldVillagerLieDown();
                if (MillConfigValues.LogGeneralAI >= 2 && this.extraLog) {
                    MillLog.minor(this, "Starting action: " + this.actionStart);
                }
            }
            if (this.field_70170_p.func_72820_D() - this.actionStart >= (long)goal.actionDuration(this)) {
                if (goal.performAction(this)) {
                    this.clearGoal();
                    this.goalKey = goal.nextGoal(this);
                    this.stopMoving = false;
                    this.shouldLieDown = false;
                    this.heldItem = ItemStack.field_190927_a;
                    this.heldItemOffHand = ItemStack.field_190927_a;
                    continuingGoal = false;
                    if (MillConfigValues.LogGeneralAI >= 2 && this.extraLog) {
                        MillLog.minor(this, "Goal performed. Now doing: " + this.goalKey);
                    }
                } else {
                    this.stopMoving = goal.stopMovingWhileWorking();
                }
                this.actionStart = 0L;
                this.goalStarted = this.field_70170_p.func_72820_D();
            }
        } else {
            this.stopMoving = false;
            this.shouldLieDown = false;
        }
        if (!continuingGoal) {
            return;
        }
        if (goal.isStillValid(this)) {
            if (this.field_70170_p.func_72820_D() - this.goalStarted > goal.stuckDelay(this)) {
                boolean actionDone = goal.stuckAction(this);
                if (actionDone) {
                    this.goalStarted = this.field_70170_p.func_72820_D();
                }
                if (goal.isStillValid(this)) {
                    this.allowRandomMoves = goal.allowRandomMoves();
                    if (this.stopMoving) {
                        this.field_70699_by.func_75499_g();
                        this.pathEntity = null;
                    }
                    this.checkGoalHeldItems(goal, target);
                }
            } else {
                this.checkGoalHeldItems(goal, target);
            }
        } else {
            this.stopMoving = false;
            this.shouldLieDown = false;
            goal.onComplete(this);
            this.clearGoal();
            this.goalKey = goal.nextGoal(this);
            this.heldItemCount = 21;
            this.heldItemId = -1;
            this.heldItemOffHandId = -1;
        }
    }

    public void clearGoal() {
        this.setGoalDestPoint(null);
        this.setGoalBuildingDestPoint(null);
        this.setGoalDestEntity(null);
        this.goalKey = null;
        this.shouldLieDown = false;
    }

    private boolean closeFenceGate(int i, int j, int k) {
        Point p = new Point(i, j, k);
        IBlockState state = p.getBlockActualState(this.field_70170_p);
        if (BlockItemUtilities.isFenceGate(state.func_177230_c()) && ((Boolean)state.func_177229_b((IProperty)BlockFenceGate.field_176466_a)).booleanValue()) {
            p.setBlockState(this.field_70170_p, state.func_177226_a((IProperty)BlockFenceGate.field_176466_a, (Comparable)Boolean.valueOf(false)));
            return true;
        }
        return false;
    }

    public void computeChildScale() {
        if (this.getRecord() == null) {
            return;
        }
        this.getRecord().scale = this.getSize() == 20 ? (this.gender == 1 ? 0.9f : 0.8f) : 0.5f + (float)this.getSize() / 100.0f;
    }

    public float computeMaxHealth() {
        if (this.vtype == null || this.getRecord() == null) {
            return 40.0f;
        }
        if (this.func_70631_g_()) {
            return 10 + this.getSize();
        }
        return this.vtype.health;
    }

    private List<PathPoint> computeNewPath(Point dest) {
        block9: {
            if (this.getPos().sameBlock(dest)) {
                return null;
            }
            try {
                Goal goal;
                if (this.goalKey != null && Goal.goals.containsKey(this.goalKey) && (double)(goal = Goal.goals.get(this.goalKey)).range(this) >= this.getPos().horizontalDistanceTo(this.getPathDestPoint())) {
                    return null;
                }
                if (this.pathPlannerJPS.isBusy()) {
                    this.pathPlannerJPS.stopPathSearch(true);
                }
                AStarNode destNode = null;
                AStarNode[] possibles = AStarStatic.getAccessNodesSorted(this.field_70170_p, this.doubleToInt(this.field_70165_t), this.doubleToInt(this.field_70163_u), this.doubleToInt(this.field_70161_v), this.getPathDestPoint().getiX(), this.getPathDestPoint().getiY(), this.getPathDestPoint().getiZ(), this.getPathingConfig());
                if (possibles.length != 0) {
                    destNode = possibles[0];
                }
                if (destNode != null) {
                    Point startPos = this.getPos().getBelow();
                    if (!startPos.isBlockPassable(this.field_70170_p) && !(startPos = startPos.getAbove()).isBlockPassable(this.field_70170_p)) {
                        startPos = startPos.getAbove();
                    }
                    this.pathPlannerJPS.getPath(this.doubleToInt(this.field_70165_t), this.doubleToInt(this.field_70163_u), this.doubleToInt(this.field_70161_v), destNode.x, destNode.y, destNode.z, this.getPathingConfig());
                } else {
                    this.onNoPathAvailable();
                }
            }
            catch (ThreadSafeUtilities.ChunkAccessException e) {
                if (MillConfigValues.LogChunkLoader < 2) break block9;
                MillLog.minor(this, "Chunk access violation while calculating path.");
            }
        }
        return null;
    }

    public int countInv(Block block, int meta) {
        return this.countInv(InvItem.createInvItem(Item.func_150898_a((Block)block), meta));
    }

    public int countInv(IBlockState blockState) {
        return this.countInv(InvItem.createInvItem(Item.func_150898_a((Block)blockState.func_177230_c()), blockState.func_177230_c().func_176201_c(blockState)));
    }

    public int countInv(InvItem key) {
        if (key.block == Blocks.field_150364_r && key.meta == -1) {
            int nb = 0;
            InvItem tkey = InvItem.createInvItem(Item.func_150898_a((Block)Blocks.field_150364_r), 0);
            if (this.inventory.containsKey(tkey)) {
                nb += this.inventory.get(tkey).intValue();
            }
            if (this.inventory.containsKey(tkey = InvItem.createInvItem(Item.func_150898_a((Block)Blocks.field_150364_r), 1))) {
                nb += this.inventory.get(tkey).intValue();
            }
            if (this.inventory.containsKey(tkey = InvItem.createInvItem(Item.func_150898_a((Block)Blocks.field_150364_r), 2))) {
                nb += this.inventory.get(tkey).intValue();
            }
            if (this.inventory.containsKey(tkey = InvItem.createInvItem(Item.func_150898_a((Block)Blocks.field_150364_r), 3))) {
                nb += this.inventory.get(tkey).intValue();
            }
            if (this.inventory.containsKey(tkey = InvItem.createInvItem(Item.func_150898_a((Block)Blocks.field_150363_s), 0))) {
                nb += this.inventory.get(tkey).intValue();
            }
            if (this.inventory.containsKey(tkey = InvItem.createInvItem(Item.func_150898_a((Block)Blocks.field_150363_s), 1))) {
                nb += this.inventory.get(tkey).intValue();
            }
            return nb;
        }
        if (key.meta == -1) {
            int nb = 0;
            for (int i = 0; i < 16; ++i) {
                InvItem tkey = InvItem.createInvItem(key.item, i);
                if (!this.inventory.containsKey(tkey)) continue;
                nb += this.inventory.get(tkey).intValue();
            }
            return nb;
        }
        if (this.inventory.containsKey(key)) {
            return this.inventory.get(key);
        }
        return 0;
    }

    public int countInv(Item item) {
        return this.countInv(item, 0);
    }

    public int countInv(Item item, int meta) {
        return this.countInv(InvItem.createInvItem(item, meta));
    }

    public int countItemsAround(Item[] items, int radius) {
        List<Entity> list = WorldUtilities.getEntitiesWithinAABB(this.field_70170_p, EntityItem.class, this.getPos(), radius, radius);
        int count = 0;
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                if (list.get(i).getClass() != EntityItem.class) continue;
                EntityItem entity = (EntityItem)list.get(i);
                if (entity.field_70128_L) continue;
                for (Item id : items) {
                    if (id != entity.func_92059_d().func_77973_b()) continue;
                    ++count;
                }
            }
        }
        return count;
    }

    public void despawnVillager() {
        EntityPlayer owner;
        if (this.field_70170_p.field_72995_K) {
            return;
        }
        if (this.hiredBy != null && (owner = this.field_70170_p.func_72924_a(this.hiredBy)) != null) {
            ServerSender.sendTranslatedSentence(owner, '4', "hire.hiredied", this.func_70005_c_());
        }
        this.mw.clearVillagerOfId(this.getVillagerId());
        super.func_70106_y();
    }

    public void despawnVillagerSilent() {
        if (MillConfigValues.LogVillagerSpawn >= 3) {
            Exception e = new Exception();
            MillLog.printException("Despawning villager: " + this, e);
        }
        this.mw.clearVillagerOfId(this.getVillagerId());
        super.func_70106_y();
    }

    public void detrampleCrops() {
        if (this.getPos().sameBlock(this.prevPoint) && (this.previousBlock == Blocks.field_150464_aj || this.previousBlock instanceof BlockMillCrops) && this.getBlock(this.getPos()) != Blocks.field_150350_a && this.getBlock(this.getPos().getBelow()) == Blocks.field_150346_d) {
            this.setBlock(this.getPos(), this.previousBlock);
            this.setBlockMetadata(this.getPos(), this.previousBlockMeta);
            this.setBlock(this.getPos().getBelow(), Blocks.field_150458_ak);
        }
        this.previousBlock = this.getBlock(this.getPos());
        this.previousBlockMeta = this.getBlockMeta(this.getPos());
    }

    public int doubleToInt(double input) {
        return AStarStatic.getIntCoordFromDoubleCoord(input);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MillVillager)) {
            return false;
        }
        MillVillager v = (MillVillager)obj;
        return this.getVillagerId() == v.villagerId;
    }

    public void func_70625_a(Entity par1Entity, float par2, float par3) {
    }

    public void faceEntityMill(Entity entityIn, float par2, float par3) {
        this.func_70671_ap().func_75651_a(entityIn, par2, par3);
    }

    public void facePoint(Point p, float par2, float par3) {
        double x = p.x + 0.5;
        double z = p.z + 0.5;
        double y = p.y + 1.0;
        this.func_70671_ap().func_75650_a(x, y, z, 10.0f, (float)this.func_70646_bf());
    }

    private void foreignMerchantUpdate() {
        if (this.foreignMerchantStallId < 0) {
            for (int i = 0; i < this.getHouse().getResManager().stalls.size() && this.foreignMerchantStallId < 0; ++i) {
                boolean taken = false;
                for (MillVillager v : this.getHouse().getKnownVillagers()) {
                    if (v.foreignMerchantStallId != i) continue;
                    taken = true;
                }
                if (taken) continue;
                this.foreignMerchantStallId = i;
            }
        }
        if (this.foreignMerchantStallId < 0) {
            this.foreignMerchantStallId = 0;
        }
    }

    private Goal getActiveGoal() {
        if (this.goalKey != null && Goal.goals.containsKey(this.goalKey)) {
            return Goal.goals.get(this.goalKey);
        }
        return null;
    }

    protected EntityArrow getArrow(float distanceFactor) {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.field_70170_p, (EntityLivingBase)this);
        entitytippedarrow.func_190547_a((EntityLivingBase)this, distanceFactor);
        return entitytippedarrow;
    }

    public int getAttackStrength() {
        int attackStrength = this.vtype.baseAttackStrength;
        ItemStack weapon = this.getWeapon();
        if (weapon != null) {
            attackStrength = (int)((double)attackStrength + Math.ceil((float)MillCommonUtilities.getItemWeaponDamage(weapon.func_77973_b()) / 2.0f));
        }
        return attackStrength;
    }

    public int getBasicForeignMerchantPrice(InvItem item) {
        if (this.getTownHall() == null) {
            return 0;
        }
        if (this.getCulture().getTradeGood(item) != null) {
            if (this.getCulture() != this.getTownHall().culture) {
                return (int)((double)this.getCulture().getTradeGood((InvItem)item).foreignMerchantPrice * 1.5);
            }
            return this.getCulture().getTradeGood((InvItem)item).foreignMerchantPrice;
        }
        return 0;
    }

    public float getBedOrientationInDegrees() {
        Block block;
        Point ref = this.getPos();
        if (this.getGoalDestPoint() != null) {
            ref = this.getGoalDestPoint();
        }
        if ((block = WorldUtilities.getBlock(this.field_70170_p, ref)) instanceof BlockBed) {
            IBlockState state = ref.getBlockActualState(this.field_70170_p);
            EnumFacing side = (EnumFacing)state.func_177229_b((IProperty)BlockHorizontal.field_185512_D);
            if (side == EnumFacing.SOUTH) {
                return 0.0f;
            }
            if (side == EnumFacing.NORTH) {
                return 180.0f;
            }
            if (side == EnumFacing.EAST) {
                return 270.0f;
            }
            if (side == EnumFacing.WEST) {
                return 90.0f;
            }
        } else {
            if (WorldUtilities.getBlock(this.field_70170_p, ref.getSouth()) == Blocks.field_150350_a) {
                return 0.0f;
            }
            if (WorldUtilities.getBlock(this.field_70170_p, ref.getWest()) == Blocks.field_150350_a) {
                return 90.0f;
            }
            if (WorldUtilities.getBlock(this.field_70170_p, ref.getNorth()) == Blocks.field_150350_a) {
                return 180.0f;
            }
            if (WorldUtilities.getBlock(this.field_70170_p, ref.getEast()) == Blocks.field_150350_a) {
                return 270.0f;
            }
        }
        return 0.0f;
    }

    public ItemTool getBestAxe() {
        InvItem bestItem = this.getConfig().getBestAxe(this);
        if (bestItem != null) {
            return (ItemTool)bestItem.item;
        }
        return (ItemTool)Items.field_151053_p;
    }

    public ItemStack[] getBestAxeStack() {
        InvItem bestItem = this.getConfig().getBestAxe(this);
        if (bestItem != null) {
            return bestItem.staticStackArray;
        }
        return WOODDEN_AXE_STACK;
    }

    public ItemStack[] getBestHoeStack() {
        InvItem bestItem = this.getConfig().getBestHoe(this);
        if (bestItem != null) {
            return bestItem.staticStackArray;
        }
        return WOODDEN_HOE_STACK;
    }

    public ItemTool getBestPickaxe() {
        InvItem bestItem = this.getConfig().getBestPickaxe(this);
        if (bestItem != null) {
            return (ItemTool)bestItem.item;
        }
        return (ItemTool)Items.field_151039_o;
    }

    public ItemStack[] getBestPickaxeStack() {
        InvItem bestItem = this.getConfig().getBestPickaxe(this);
        if (bestItem != null) {
            return bestItem.staticStackArray;
        }
        return WOODDEN_PICKAXE_STACK;
    }

    public ItemTool getBestShovel() {
        InvItem bestItem = this.getConfig().getBestShovel(this);
        if (bestItem != null) {
            return (ItemTool)bestItem.item;
        }
        return (ItemTool)Items.field_151038_n;
    }

    public ItemStack[] getBestShovelStack() {
        InvItem bestItem = this.getConfig().getBestShovel(this);
        if (bestItem != null) {
            return bestItem.staticStackArray;
        }
        return WOODDEN_SHOVEL_STACK;
    }

    public Block getBlock(Point p) {
        return WorldUtilities.getBlock(this.field_70170_p, p);
    }

    public int getBlockMeta(Point p) {
        return WorldUtilities.getBlockMeta(this.field_70170_p, p);
    }

    public float func_180484_a(BlockPos pos) {
        if (!this.allowRandomMoves) {
            if (MillConfigValues.LogPathing >= 3 && this.extraLog) {
                MillLog.debug(this, "Forbiding random moves. Current goal: " + Goal.goals.get(this.goalKey) + " Returning: " + -99999.0f);
            }
            return Float.NEGATIVE_INFINITY;
        }
        Point rp = new Point(pos);
        double dist = rp.distanceTo(this.housePoint);
        if (WorldUtilities.getBlock(this.field_70170_p, rp.getBelow()) == Blocks.field_150458_ak) {
            return -50.0f;
        }
        if (dist > 10.0) {
            return -((float)dist);
        }
        return MillCommonUtilities.randomInt(10);
    }

    public EntityItem getClosestItemVertical(List<InvItem> goods, int radius, int vertical) {
        return WorldUtilities.getClosestItemVertical(this.field_70170_p, this.getPos(), goods, radius, vertical);
    }

    public ResourceLocation getClothTexturePath(int layer) {
        return this.clothTexture[layer];
    }

    public VillagerConfig getConfig() {
        if (this.vtype == null || this.vtype.villagerConfig == null) {
            return VillagerConfig.DEFAULT_CONFIG;
        }
        return this.vtype.villagerConfig;
    }

    public Culture getCulture() {
        if (this.vtype == null) {
            return null;
        }
        return this.vtype.culture;
    }

    public ConstructionIP getCurrentConstruction() {
        ConstructionIP cip;
        if (this.constructionJobId > -1 && this.constructionJobId < this.getTownHall().getConstructionsInProgress().size() && ((cip = this.getTownHall().getConstructionsInProgress().get(this.constructionJobId)).getBuilder() == null || cip.getBuilder() == this)) {
            return cip;
        }
        return null;
    }

    public Goal getCurrentGoal() {
        if (Goal.goals.containsKey(this.goalKey)) {
            return Goal.goals.get(this.goalKey);
        }
        return null;
    }

    protected int func_70693_a(EntityPlayer par1EntityPlayer) {
        return this.vtype.expgiven;
    }

    public String getFemaleChild() {
        return this.vtype.femaleChild;
    }

    public String getGameOccupationName(String playername) {
        if (this.getCulture() == null || this.vtype == null || this.getRecord() == null) {
            return "";
        }
        if (!this.getCulture().canReadVillagerNames()) {
            return "";
        }
        if (this.func_70631_g_() && this.getSize() == 20) {
            return this.getCulture().getCultureString("villager." + this.vtype.altkey);
        }
        return this.getCulture().getCultureString("villager." + this.vtype.key);
    }

    public String getGameSpeech(String playername) {
        if (this.getCulture() == null) {
            return null;
        }
        String speech = VillageUtilities.getVillagerSentence(this, playername, false);
        if (speech != null) {
            int duration = 10 + speech.length() / 5;
            if (this.speech_started + (long)(20 * (duration = Math.min(duration, 30))) < this.field_70170_p.func_72820_D()) {
                return null;
            }
        }
        return speech;
    }

    public int getGatheringRange() {
        return 20;
    }

    public String getGenderString() {
        if (this.gender == 1) {
            return "male";
        }
        return "female";
    }

    public Building getGoalBuildingDest() {
        return this.mw.getBuilding(this.getGoalBuildingDestPoint());
    }

    public Point getGoalBuildingDestPoint() {
        if (this.goalInformation == null) {
            return null;
        }
        return this.goalInformation.getDestBuildingPos();
    }

    public Entity getGoalDestEntity() {
        if (this.goalInformation == null) {
            return null;
        }
        return this.goalInformation.getTargetEnt();
    }

    public Point getGoalDestPoint() {
        if (this.goalInformation == null) {
            return null;
        }
        return this.goalInformation.getDest();
    }

    public String getGoalLabel(String goal) {
        if (Goal.goals.containsKey(goal)) {
            return Goal.goals.get(goal).gameName(this);
        }
        return "none";
    }

    public List<Goal> getGoals() {
        if (this.vtype != null) {
            return this.vtype.goals;
        }
        return null;
    }

    public List<InvItem> getGoodsToBringBackHome() {
        return this.vtype.bringBackHomeGoods;
    }

    public List<InvItem> getGoodsToCollect() {
        return this.vtype.collectGoods;
    }

    public int getHireCost(EntityPlayer player) {
        int cost = this.vtype.hireCost;
        if (this.getTownHall().controlledBy(player)) {
            cost /= 2;
        }
        return cost;
    }

    public Building getHouse() {
        if (this.house != null) {
            return this.house;
        }
        if (MillConfigValues.LogVillager >= 3 && this.extraLog) {
            MillLog.debug(this, "Seeking uncached house");
        }
        if (this.mw != null) {
            this.house = this.mw.getBuilding(this.housePoint);
            return this.house;
        }
        return null;
    }

    public Set<InvItem> getInventoryKeys() {
        return this.inventory.keySet();
    }

    public List<InvItem> getItemsNeeded() {
        return this.vtype.itemsNeeded;
    }

    public ItemStack func_184582_a(EntityEquipmentSlot slotIn) {
        if (slotIn == EntityEquipmentSlot.HEAD) {
            for (InvItem item : this.getConfig().armoursHelmetSorted) {
                if (this.countInv(item) <= 0) continue;
                return item.getItemStack();
            }
            return ItemStack.field_190927_a;
        }
        if (slotIn == EntityEquipmentSlot.CHEST) {
            for (InvItem item : this.getConfig().armoursChestplateSorted) {
                if (this.countInv(item) <= 0) continue;
                return item.getItemStack();
            }
            return ItemStack.field_190927_a;
        }
        if (slotIn == EntityEquipmentSlot.LEGS) {
            for (InvItem item : this.getConfig().armoursLeggingsSorted) {
                if (this.countInv(item) <= 0) continue;
                return item.getItemStack();
            }
            return ItemStack.field_190927_a;
        }
        if (slotIn == EntityEquipmentSlot.FEET) {
            for (InvItem item : this.getConfig().armoursBootsSorted) {
                if (this.countInv(item) <= 0) continue;
                return item.getItemStack();
            }
            return ItemStack.field_190927_a;
        }
        if (this.heldItem != null && slotIn == EntityEquipmentSlot.MAINHAND) {
            return this.heldItem;
        }
        if (this.heldItemOffHand != null && slotIn == EntityEquipmentSlot.OFFHAND) {
            return this.heldItemOffHand;
        }
        return ItemStack.field_190927_a;
    }

    public String getMaleChild() {
        return this.vtype.maleChild;
    }

    public String func_70005_c_() {
        return this.firstName + " " + this.familyName;
    }

    public String getNameKey() {
        if (this.vtype == null || this.getRecord() == null) {
            return "";
        }
        if (this.func_70631_g_() && this.getSize() == 20) {
            return this.vtype.altkey;
        }
        return this.vtype.key;
    }

    public String getNativeOccupationName() {
        if (this.vtype == null) {
            return null;
        }
        if (this.func_70631_g_() && this.getSize() == 20) {
            return this.vtype.altname;
        }
        return this.vtype.name;
    }

    public String getNativeSpeech(String playername) {
        if (this.getCulture() == null) {
            return null;
        }
        String speech = VillageUtilities.getVillagerSentence(this, playername, true);
        if (speech != null) {
            int duration = 10 + speech.length() / 5;
            if (this.speech_started + (long)(20 * (duration = Math.min(duration, 30))) < this.field_70170_p.func_72820_D()) {
                return null;
            }
        }
        return speech;
    }

    public Point getPathDestPoint() {
        return this.pathDestPoint;
    }

    private AStarConfig getPathingConfig() {
        if (this.getActiveGoal() != null) {
            return this.getActiveGoal().getPathingConfig(this);
        }
        return this.getVillagerPathingConfig();
    }

    public PathPoint getPathPointPos() {
        return new PathPoint(MathHelper.func_76128_c((double)this.func_174813_aQ().field_72340_a), MathHelper.func_76128_c((double)this.func_174813_aQ().field_72338_b), MathHelper.func_76128_c((double)this.func_174813_aQ().field_72339_c));
    }

    public Point getPos() {
        return new Point(this.field_70165_t, this.field_70163_u, this.field_70161_v);
    }

    public EnumHandSide func_184591_cq() {
        if (this.getRecord() != null && this.getRecord().rightHanded) {
            return EnumHandSide.RIGHT;
        }
        return EnumHandSide.LEFT;
    }

    public String getRandomFamilyName() {
        return this.getCulture().getRandomNameFromList(this.vtype.familyNameList);
    }

    public VillagerRecord getRecord() {
        if (this.mw == null) {
            return null;
        }
        return this.mw.getVillagerRecordById(this.getVillagerId());
    }

    public int getSize() {
        if (this.getRecord() == null) {
            return 0;
        }
        return this.getRecord().size;
    }

    public MillVillager getSpouse() {
        if (this.getHouse() == null || this.func_70631_g_()) {
            return null;
        }
        for (MillVillager v : this.getHouse().getKnownVillagers()) {
            if (v.func_70631_g_() || v.gender == this.gender) continue;
            return v;
        }
        return null;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public List<String> getToolsCategoriesNeeded() {
        return this.vtype.toolsCategoriesNeeded;
    }

    public int func_70658_aO() {
        if (this.getRecord() == null) {
            return 0;
        }
        return this.getRecord().getTotalArmorValue();
    }

    public Building getTownHall() {
        if (this.townHall != null) {
            return this.townHall;
        }
        if (MillConfigValues.LogVillager >= 3 && this.extraLog) {
            MillLog.debug(this, "Seeking uncached townHall");
        }
        if (this.mw != null) {
            this.townHall = this.mw.getBuilding(this.townHallPoint);
            return this.townHall;
        }
        return null;
    }

    public long getVillagerId() {
        return this.villagerId;
    }

    public AStarConfig getVillagerPathingConfig() {
        if (this.vtype.noleafclearing) {
            return JPS_CONFIG_NO_LEAVES;
        }
        return JPS_CONFIG_DEFAULT;
    }

    public ItemStack getWeapon() {
        InvItem weapon;
        if (this.vtype == null) {
            return ItemStack.field_190927_a;
        }
        if (this.isUsingBow && (weapon = this.getConfig().getBestWeaponRanged(this)) != null) {
            return weapon.getItemStack();
        }
        if ((this.isUsingHandToHand || !this.vtype.isArcher) && (weapon = this.getConfig().getBestWeaponHandToHand(this)) != null) {
            return weapon.getItemStack();
        }
        if (this.vtype.startingWeapon != null) {
            return this.vtype.startingWeapon.getItemStack();
        }
        return ItemStack.field_190927_a;
    }

    public void growSize() {
        if (this.getRecord() == null) {
            return;
        }
        int growth = 2;
        int nb = 0;
        nb = this.getHouse().takeGoods(Items.field_151110_aK, 1);
        if (nb == 1) {
            growth += 1 + MillCommonUtilities.randomInt(5);
        }
        for (InvItem food : this.getConfig().foodsGrowthSorted) {
            if (growth >= 10 || this.getRecord().size + growth >= 20 || this.getHouse().countGoods(food) <= 0) continue;
            this.getHouse().takeGoods(food, 1);
            growth += this.getConfig().foodsGrowth.get(food) + MillCommonUtilities.randomInt(this.getConfig().foodsGrowth.get(food));
        }
        this.getRecord().size += growth;
        if (this.getRecord().size > 20) {
            this.getRecord().size = 20;
        }
        this.computeChildScale();
        if (MillConfigValues.LogChildren >= 2) {
            MillLog.minor(this, "Child growing by " + growth + ", new size: " + this.getRecord().size);
        }
    }

    private void handleDoorsAndFenceGates() {
        if (this.doorToClose != null && (this.pathEntity == null || this.pathEntity.func_75874_d() == 0 || this.pathEntity.getPastTargetPathPoint(2) != null && this.doorToClose.sameBlock(this.pathEntity.getPastTargetPathPoint(2)))) {
            if (BlockItemUtilities.isWoodenDoor(this.getBlock(this.doorToClose))) {
                if (((Boolean)this.doorToClose.getBlockActualState(this.field_70170_p).func_177229_b((IProperty)BlockDoor.field_176519_b)).booleanValue()) {
                    this.toggleDoor(this.doorToClose);
                }
                for (Point nearbyDoor : new Point[]{this.doorToClose.getNorth(), this.doorToClose.getSouth(), this.doorToClose.getEast(), this.doorToClose.getWest()}) {
                    if (!BlockItemUtilities.isWoodenDoor(this.getBlock(nearbyDoor)) || !((Boolean)nearbyDoor.getBlockActualState(this.field_70170_p).func_177229_b((IProperty)BlockDoor.field_176519_b)).booleanValue()) continue;
                    this.toggleDoor(nearbyDoor);
                }
                this.doorToClose = null;
            } else if (BlockItemUtilities.isFenceGate(this.getBlock(this.doorToClose))) {
                if (this.closeFenceGate(this.doorToClose.getiX(), this.doorToClose.getiY(), this.doorToClose.getiZ())) {
                    this.doorToClose = null;
                }
            } else {
                this.doorToClose = null;
            }
        }
        if (this.pathEntity != null && this.pathEntity.func_75874_d() > 0) {
            Block nextTargetPathPointBlock;
            PathPoint p = null;
            if (this.pathEntity.getCurrentTargetPathPoint() != null) {
                Block currentTargetPathPointBlock = WorldUtilities.getBlock(this.field_70170_p, this.pathEntity.getCurrentTargetPathPoint().field_75839_a, this.pathEntity.getCurrentTargetPathPoint().field_75837_b, this.pathEntity.getCurrentTargetPathPoint().field_75838_c);
                if (BlockItemUtilities.isWoodenDoor(currentTargetPathPointBlock)) {
                    p = this.pathEntity.getCurrentTargetPathPoint();
                }
            } else if (this.pathEntity.getNextTargetPathPoint() != null && BlockItemUtilities.isWoodenDoor(nextTargetPathPointBlock = WorldUtilities.getBlock(this.field_70170_p, this.pathEntity.getNextTargetPathPoint().field_75839_a, this.pathEntity.getNextTargetPathPoint().field_75837_b, this.pathEntity.getNextTargetPathPoint().field_75838_c))) {
                p = this.pathEntity.getNextTargetPathPoint();
            }
            if (p != null) {
                Point point = new Point(p);
                if (!((Boolean)point.getBlockActualState(this.field_70170_p).func_177229_b((IProperty)BlockDoor.field_176519_b)).booleanValue()) {
                    this.toggleDoor(new Point(p));
                }
                this.doorToClose = new Point(p);
            } else {
                if (this.pathEntity.getNextTargetPathPoint() != null && BlockItemUtilities.isFenceGate(WorldUtilities.getBlock(this.field_70170_p, this.pathEntity.getNextTargetPathPoint().field_75839_a, this.pathEntity.getNextTargetPathPoint().field_75837_b, this.pathEntity.getNextTargetPathPoint().field_75838_c))) {
                    p = this.pathEntity.getNextTargetPathPoint();
                } else if (this.pathEntity.getCurrentTargetPathPoint() != null && BlockItemUtilities.isFenceGate(WorldUtilities.getBlock(this.field_70170_p, this.pathEntity.getCurrentTargetPathPoint().field_75839_a, this.pathEntity.getCurrentTargetPathPoint().field_75837_b, this.pathEntity.getCurrentTargetPathPoint().field_75838_c))) {
                    p = this.pathEntity.getCurrentTargetPathPoint();
                }
                if (p != null) {
                    Point point = new Point(p);
                    this.openFenceGate(p.field_75839_a, p.field_75837_b, p.field_75838_c);
                    this.doorToClose = point;
                }
            }
        }
    }

    private void handleLeaveClearing() {
        if (this.pathEntity != null && this.pathEntity.func_75874_d() > 0) {
            Point p;
            ArrayList<Point> pointsToCheck = new ArrayList<Point>();
            if (this.pathEntity.getCurrentTargetPathPoint() != null) {
                p = new Point(this.pathEntity.getCurrentTargetPathPoint());
                pointsToCheck.add(p);
                pointsToCheck.add(p.getAbove());
            }
            if (this.pathEntity.getNextTargetPathPoint() != null) {
                p = new Point(this.pathEntity.getNextTargetPathPoint());
                for (int dx = -1; dx < 2; ++dx) {
                    for (int dz = -1; dz < 2; ++dz) {
                        pointsToCheck.add(p.getRelative(dx, 0.0, dz));
                        pointsToCheck.add(p.getRelative(dx, 1.0, dz));
                    }
                }
            }
            for (Point point : pointsToCheck) {
                IBlockState blockState = point.getBlockActualState(this.field_70170_p);
                if (!(blockState.func_177230_c() instanceof BlockLeaves)) continue;
                if (blockState.func_177230_c() == Blocks.field_150362_t || blockState.func_177230_c() == Blocks.field_150361_u) {
                    if (!((Boolean)blockState.func_177229_b((IProperty)BlockLeaves.field_176237_a)).booleanValue()) continue;
                    WorldUtilities.setBlock(this.field_70170_p, point, Blocks.field_150350_a, true, true);
                    continue;
                }
                if (blockState.func_177230_c() instanceof BlockFruitLeaves) continue;
                if (BlockStateUtilities.hasPropertyByName(blockState, "decayable")) {
                    if (!((Boolean)blockState.func_177229_b((IProperty)BlockLeaves.field_176237_a)).booleanValue()) continue;
                    WorldUtilities.setBlock(this.field_70170_p, point, Blocks.field_150350_a, true, true);
                    continue;
                }
                WorldUtilities.setBlock(this.field_70170_p, point, Blocks.field_150350_a, true, true);
            }
        }
    }

    private boolean hasBow() {
        return this.getConfig().getBestWeaponRanged(this) != null;
    }

    public boolean hasChildren() {
        return this.vtype.maleChild != null && this.vtype.femaleChild != null;
    }

    public int hashCode() {
        return (int)this.getVillagerId();
    }

    public boolean helpsInAttacks() {
        return this.vtype.helpInAttacks;
    }

    public void interactDev(EntityPlayer entityplayer) {
        DevModUtilities.villagerInteractDev(entityplayer, this);
    }

    public boolean interactSpecial(EntityPlayer entityplayer) {
        if (this.getTownHall() == null) {
            MillLog.error(this, "Trying to interact with a villager with no TH.");
        }
        if (this.getHouse() == null) {
            MillLog.error(this, "Trying to interact with a villager with no house.");
        }
        if (this.isChief()) {
            ServerSender.displayVillageChiefGUI(entityplayer, this);
            return true;
        }
        UserProfile profile = this.mw.getProfile(entityplayer);
        if (this.canMeditate() && this.mw.isGlobalTagSet("pujas") || this.canPerformSacrifices() && this.mw.isGlobalTagSet("mayansacrifices")) {
            if (MillConfigValues.LogPujas >= 3) {
                MillLog.debug(this, "canMeditate");
            }
            if (this.getTownHall().getReputation(entityplayer) >= -1024) {
                for (BuildingLocation l : this.getTownHall().getLocations()) {
                    if (l.level < 0 || l.getSellingPos() == null || !(l.getSellingPos().distanceTo((Entity)this) < 8.0)) continue;
                    Building b = l.getBuilding(this.field_70170_p);
                    if (b.pujas == null) continue;
                    if (MillConfigValues.LogPujas >= 3) {
                        MillLog.debug(this, "Found shrine: " + b);
                    }
                    Point p = b.getPos();
                    entityplayer.openGui((Object)Mill.instance, 6, this.field_70170_p, p.getiX(), p.getiY(), p.getiZ());
                    return true;
                }
            } else {
                ServerSender.sendTranslatedSentence(entityplayer, 'f', "ui.sellerboycott", this.func_70005_c_());
                return false;
            }
        }
        if (this.isSeller() && !this.getTownHall().controlledBy(entityplayer)) {
            if (this.getTownHall().getReputation(entityplayer) >= -1024 && this.getTownHall().chestLocked) {
                for (BuildingLocation l : this.getTownHall().getLocations()) {
                    if (l.level < 0 || l.shop == null || l.shop.length() <= 0 || !(l.getSellingPos() != null && l.getSellingPos().distanceTo((Entity)this) < 5.0) && !(l.sleepingPos.distanceTo((Entity)this) < 5.0)) continue;
                    ServerSender.displayVillageTradeGUI(entityplayer, l.getBuilding(this.field_70170_p));
                    return true;
                }
            } else {
                if (!this.getTownHall().chestLocked) {
                    ServerSender.sendTranslatedSentence(entityplayer, 'f', "ui.sellernotcurrently possible", this.func_70005_c_());
                    return false;
                }
                ServerSender.sendTranslatedSentence(entityplayer, 'f', "ui.sellerboycott", this.func_70005_c_());
                return false;
            }
        }
        if (this.isForeignMerchant()) {
            ServerSender.displayMerchantTradeGUI(entityplayer, this);
            return true;
        }
        if (this.vtype.hireCost > 0) {
            if (this.hiredBy == null || this.hiredBy.equals(entityplayer.func_70005_c_())) {
                ServerSender.displayHireGUI(entityplayer, this);
                return true;
            }
            ServerSender.sendTranslatedSentence(entityplayer, 'f', "hire.hiredbyotherplayer", this.func_70005_c_(), this.hiredBy);
            return false;
        }
        if (this.isLocalMerchant() && !profile.villagersInQuests.containsKey(this.getVillagerId())) {
            ServerSender.sendTranslatedSentence(entityplayer, '6', "other.localmerchantinteract", this.func_70005_c_());
            return false;
        }
        return false;
    }

    public boolean isChief() {
        return this.vtype.isChief;
    }

    public boolean func_70631_g_() {
        if (this.vtype == null) {
            return false;
        }
        return this.vtype.isChild;
    }

    public boolean isForeignMerchant() {
        return this.vtype.isForeignMerchant;
    }

    public boolean isHostile() {
        return this.vtype.hostile;
    }

    public boolean isLocalMerchant() {
        return this.vtype.isLocalMerchant;
    }

    protected boolean func_70610_aX() {
        return this.func_110143_aJ() <= 0.0f || this.isVillagerSleeping();
    }

    public boolean isReallyDead() {
        return this.field_70128_L && this.func_110143_aJ() <= 0.0f;
    }

    public boolean isSeller() {
        return this.vtype.canSell;
    }

    public boolean isTextureValid(String texture) {
        if (this.vtype != null) {
            return this.vtype.isTextureValid(texture);
        }
        return true;
    }

    public boolean isVillagerSleeping() {
        return this.shouldLieDown;
    }

    public boolean isVisitor() {
        if (this.vtype == null) {
            return false;
        }
        return this.vtype.visitor;
    }

    private void jumpToDest() {
        Point jumpTo = WorldUtilities.findVerticalStandingPos(this.field_70170_p, this.getPathDestPoint());
        if (jumpTo != null && jumpTo.distanceTo(this.getPathDestPoint()) < 4.0) {
            if (MillConfigValues.LogPathing >= 1 && this.extraLog) {
                MillLog.major(this, "Jumping from " + this.getPos() + " to " + jumpTo);
            }
            this.func_70107_b((double)jumpTo.getiX() + 0.5, (double)jumpTo.getiY() + 0.5, (double)jumpTo.getiZ() + 0.5);
            this.longDistanceStuck = 0;
            this.localStuck = 0;
        } else if (this.goalKey != null && Goal.goals.containsKey(this.goalKey)) {
            Goal goal = Goal.goals.get(this.goalKey);
            try {
                goal.unreachableDestination(this);
            }
            catch (Exception e) {
                MillLog.printException(this + ": Exception in handling unreachable dest for goal " + this.goalKey, e);
            }
        }
    }

    public void killVillager() {
        VillagerRecord vr;
        EntityPlayer owner;
        if (this.field_70170_p.field_72995_K || !(this.field_70170_p instanceof WorldServer)) {
            super.func_70106_y();
            return;
        }
        for (InvItem iv : this.inventory.keySet()) {
            if (this.inventory.get(iv) <= 0) continue;
            WorldUtilities.spawnItem(this.field_70170_p, this.getPos(), new ItemStack(iv.getItem(), this.inventory.get(iv).intValue(), iv.meta), 0.0f);
        }
        if (this.hiredBy != null && (owner = this.field_70170_p.func_72924_a(this.hiredBy)) != null) {
            ServerSender.sendTranslatedSentence(owner, 'f', "hire.hiredied", this.func_70005_c_());
        }
        if ((vr = this.getRecord()) != null) {
            if (MillConfigValues.LogGeneralAI >= 1) {
                MillLog.major(this, this.getTownHall() + ": Villager has been killed!");
            }
            vr.killed = true;
        }
        super.func_70106_y();
    }

    private void leaveVillage() {
        for (InvItem iv : this.vtype.foreignMerchantStock.keySet()) {
            this.getHouse().takeGoods(iv.getItem(), iv.meta, (int)this.vtype.foreignMerchantStock.get(iv));
        }
        this.mw.removeVillagerRecord(this.villagerId);
        this.despawnVillager();
    }

    public void localMerchantUpdate() throws Exception {
        if (this.getHouse() != null && this.getHouse() == this.getTownHall()) {
            List<Building> buildings = this.getTownHall().getBuildingsWithTag("inn");
            Building inn = null;
            for (Building building : buildings) {
                if (building.merchantRecord != null) continue;
                inn = building;
            }
            if (inn == null) {
                this.mw.removeVillagerRecord(this.villagerId);
                this.despawnVillager();
                MillLog.error(this, "Merchant had Town Hall as house and inn is full. Killing him.");
            } else {
                this.setHousePoint(inn.getPos());
                VillagerRecord vr = this.getRecord();
                vr.updateRecord(this);
                this.mw.registerVillagerRecord(vr, true);
                MillLog.error(this, "Merchant had Town Hall as house. Moving him to the inn.");
            }
        }
    }

    public void func_70645_a(DamageSource cause) {
        super.func_70645_a(cause);
    }

    @Override
    public void onFoundPath(List<AStarNode> result) {
        this.pathCalculatedSinceLastTick = result;
    }

    public void func_70636_d() {
        super.func_70636_d();
        this.func_82168_bl();
        this.setFacingDirection();
        if (this.isVillagerSleeping()) {
            this.field_70159_w = 0.0;
            this.field_70181_x = 0.0;
            this.field_70179_y = 0.0;
        }
    }

    @Override
    public void onNoPathAvailable() {
        this.pathFailedSincelastTick = true;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void func_70071_h_() {
        long startTime = System.nanoTime();
        if (this.field_70170_p.field_73011_w.getDimension() != 0) {
            this.despawnVillagerSilent();
        }
        try {
            if (this.vtype == null) {
                if (!this.field_70128_L) {
                    MillLog.error(this, "Unknown villager type. Killing him.");
                    this.despawnVillagerSilent();
                }
                return;
            }
            if (this.pathFailedSincelastTick) {
                this.pathFailedSinceLastTick();
            }
            if (this.pathCalculatedSinceLastTick != null) {
                this.applyPathCalculatedSinceLastTick();
            }
            if (this.field_70170_p.field_72995_K) {
                super.func_70071_h_();
                return;
            }
            if (this.field_70128_L) {
                super.func_70071_h_();
                return;
            }
            if (Math.abs(this.field_70170_p.func_72820_D() + (long)this.hashCode()) % 10L == 2L) {
                this.sendVillagerPacket();
            }
            if (Math.abs(this.field_70170_p.func_72820_D() + (long)this.hashCode()) % 40L == 4L) {
                this.unlockForNearbyPlayers();
            }
            if (this.hiredBy != null) {
                this.updateHired();
                super.func_70071_h_();
                return;
            }
            if (this.getTownHall() == null || this.getHouse() == null) {
                return;
            }
            if (this.getTownHall() != null && !this.getTownHall().isActive) {
                return;
            }
            if (this.getPos().distanceTo(this.getTownHall().getPos()) > (double)(this.getTownHall().villageType.radius + 100)) {
                MillLog.error(this, "Villager is far away from village. Despawning him.");
                this.despawnVillagerSilent();
            }
            try {
                block82: {
                    block81: {
                        PathPoint nextPoint;
                        double newdistance;
                        block80: {
                            block79: {
                                ++this.timer;
                                if (this.func_110143_aJ() < this.func_110138_aP() & MillCommonUtilities.randomInt(1600) == 0) {
                                    this.func_70606_j(this.func_110143_aJ() + 1.0f);
                                }
                                this.detrampleCrops();
                                this.allowRandomMoves = true;
                                this.stopMoving = false;
                                if (this.getTownHall() == null || this.getHouse() == null) {
                                    super.func_70071_h_();
                                    return;
                                }
                                if (Goal.beSeller.key.equals(this.goalKey)) {
                                    this.townHall.seller = this;
                                } else if (Goal.getResourcesForBuild.key.equals(this.goalKey) || Goal.construction.key.equals(this.goalKey)) {
                                    if (MillConfigValues.LogTileEntityBuilding >= 3) {
                                        MillLog.debug(this, "Registering as builder for: " + this.townHall);
                                    }
                                    if (this.constructionJobId > -1 && this.townHall.getConstructionsInProgress().size() > this.constructionJobId) {
                                        this.townHall.getConstructionsInProgress().get(this.constructionJobId).setBuilder(this);
                                    }
                                }
                                if (this.getTownHall().underAttack) {
                                    if (this.goalKey == null || !this.goalKey.equals(Goal.raidVillage.key) && !this.goalKey.equals(Goal.defendVillage.key) && !this.goalKey.equals(Goal.hide.key)) {
                                        this.clearGoal();
                                    }
                                    if (this.isRaider) {
                                        this.goalKey = Goal.raidVillage.key;
                                        this.targetDefender();
                                    } else if (this.helpsInAttacks()) {
                                        this.goalKey = Goal.defendVillage.key;
                                        this.targetRaider();
                                    } else {
                                        this.goalKey = Goal.hide.key;
                                    }
                                    this.checkGoals();
                                }
                                if (this.func_70638_az() == null) break block79;
                                if (this.vtype.isDefensive && this.getPos().distanceTo(this.getHouse().getResManager().getDefendingPos()) > 20.0) {
                                    this.func_70624_b(null);
                                } else if (!this.func_70638_az().func_70089_S() || this.getPos().distanceTo((Entity)this.func_70638_az()) > 80.0 || this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL && this.func_70638_az() instanceof EntityPlayer) {
                                    this.func_70624_b(null);
                                }
                                if (this.func_70638_az() != null) {
                                    this.shouldLieDown = false;
                                    this.attackEntity((Entity)this.func_70638_az());
                                    if (!this.func_70638_az().field_70160_al) {
                                        this.setPathDestPoint(new Point((Entity)this.func_70638_az()), 1);
                                        break block80;
                                    } else {
                                        Point posToAttack = new Point((Entity)this.func_70638_az());
                                        while (posToAttack.y > 0.0 && posToAttack.isBlockPassable(this.field_70170_p)) {
                                            posToAttack = posToAttack.getBelow();
                                        }
                                        if (posToAttack != null) {
                                            this.setPathDestPoint(posToAttack.getAbove(), 3);
                                        }
                                    }
                                }
                                break block80;
                            }
                            if (this.isHostile() && this.field_70170_p.func_175659_aa() != EnumDifficulty.PEACEFUL && this.getTownHall().closestPlayer != null && this.getPos().distanceTo((Entity)this.getTownHall().closestPlayer) <= 80.0) {
                                int range = 80;
                                if (this.vtype.isDefensive) {
                                    range = 20;
                                }
                                this.func_70624_b((EntityLivingBase)this.field_70170_p.func_184137_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, (double)range, true));
                                this.clearGoal();
                            }
                        }
                        if (this.func_70638_az() != null) {
                            this.setGoalDestPoint(new Point((Entity)this.func_70638_az()));
                            this.heldItem = this.getWeapon();
                            this.heldItemOffHand = ItemStack.field_190927_a;
                            if (this.goalKey != null && !Goal.goals.get(this.goalKey).isFightingGoal()) {
                                this.clearGoal();
                            }
                        } else if (!this.getTownHall().underAttack) {
                            if (this.field_70170_p.func_72935_r()) {
                                EntityItem item;
                                this.speakSentence("greeting", 12000, 3, 10);
                                this.nightActionPerformed = false;
                                List<InvItem> goods = this.getGoodsToCollect();
                                if (goods != null && (this.field_70170_p.func_72820_D() + this.getVillagerId()) % 20L == 0L && (item = this.getClosestItemVertical(goods, 5, 30)) != null) {
                                    item.func_70106_y();
                                    if (item.func_92059_d().func_77973_b() == Item.func_150898_a((Block)Blocks.field_150345_g)) {
                                        this.addToInv(item.func_92059_d().func_77973_b(), item.func_92059_d().func_77952_i() & 3, 1);
                                    } else {
                                        this.addToInv(item.func_92059_d().func_77973_b(), item.func_92059_d().func_77952_i(), 1);
                                    }
                                }
                                this.specificUpdate();
                                if (!this.isRaider) {
                                    if (this.goalKey == null) {
                                        this.setNextGoal();
                                    }
                                    if (this.goalKey != null) {
                                        this.checkGoals();
                                    } else {
                                        this.shouldLieDown = false;
                                    }
                                }
                            } else if (!this.isRaider) {
                                if (this.goalKey == null) {
                                    this.setNextGoal();
                                }
                                if (this.goalKey != null) {
                                    this.checkGoals();
                                } else {
                                    this.shouldLieDown = false;
                                }
                            }
                        }
                        if (this.getPathDestPoint() == null || this.pathEntity == null || this.pathEntity.func_75874_d() <= 0 || this.stopMoving) break block81;
                        double olddistance = this.prevPoint.horizontalDistanceToSquared(this.getPathDestPoint());
                        this.longDistanceStuck = olddistance - (newdistance = this.getPos().horizontalDistanceToSquared(this.getPathDestPoint())) < 2.0E-4 ? ++this.longDistanceStuck : --this.longDistanceStuck;
                        if (this.longDistanceStuck < 0) {
                            this.longDistanceStuck = 0;
                        }
                        if (this.pathEntity != null && this.pathEntity.func_75874_d() > 1 && MillConfigValues.LogPathing >= 2 && this.extraLog) {
                            MillLog.minor(this, "Stuck: " + this.longDistanceStuck + " pos " + this.getPos() + " node: " + this.pathEntity.getCurrentTargetPathPoint() + " next node: " + this.pathEntity.getNextTargetPathPoint() + " dest: " + this.getPathDestPoint());
                        }
                        if (this.longDistanceStuck > 3000 && (!this.vtype.noTeleport || this.getRecord() != null && this.getRecord().raidingVillage)) {
                            this.jumpToDest();
                        }
                        if ((nextPoint = this.pathEntity.getNextTargetPathPoint()) != null) {
                            olddistance = this.prevPoint.distanceToSquared(nextPoint);
                            this.localStuck = olddistance - (newdistance = this.getPos().distanceToSquared(nextPoint)) < 2.0E-4 ? (this.localStuck += 4) : --this.localStuck;
                            if (this.localStuck < 0) {
                                this.localStuck = 0;
                            }
                            if (this.localStuck > 30) {
                                this.field_70699_by.func_75499_g();
                                this.pathEntity = null;
                            }
                            if (this.localStuck > 100) {
                                this.func_70107_b((double)nextPoint.field_75839_a + 0.5, (double)nextPoint.field_75837_b + 0.5, (double)nextPoint.field_75838_c + 0.5);
                                this.localStuck = 0;
                            }
                        }
                        break block82;
                    }
                    this.longDistanceStuck = 0;
                    this.localStuck = 0;
                }
                if (this.getPathDestPoint() != null && !this.stopMoving) {
                    this.updatePathIfNeeded(this.getPathDestPoint());
                }
                if (this.stopMoving || this.pathPlannerJPS.isBusy()) {
                    this.field_70699_by.func_75499_g();
                    this.pathEntity = null;
                }
                this.prevPoint = this.getPos();
                if (this.canVillagerClearLeaves() && Math.abs(this.field_70170_p.func_72820_D() + (long)this.hashCode()) % 10L == 6L) {
                    this.handleLeaveClearing();
                }
                this.handleDoorsAndFenceGates();
                if (System.currentTimeMillis() - this.timeSinceLastPathingTimeDisplay > 10000L) {
                    if (this.pathingTime > 500L) {
                        if (this.getPathDestPoint() != null) {
                            MillLog.warning(this, "Pathing time in last 10 secs: " + this.pathingTime + " dest: " + this.getPathDestPoint() + " dest bid: " + WorldUtilities.getBlock(this.field_70170_p, this.getPathDestPoint()) + " above bid: " + WorldUtilities.getBlock(this.field_70170_p, this.getPathDestPoint().getAbove()));
                        } else {
                            MillLog.warning(this, "Pathing time in last 10 secs: " + this.pathingTime + " null dest point.");
                        }
                        MillLog.warning(this, "nbPathsCalculated: " + this.nbPathsCalculated + " nbPathNoStart: " + this.nbPathNoStart + " nbPathNoEnd: " + this.nbPathNoEnd + " nbPathAborted: " + this.nbPathAborted + " nbPathFailure: " + this.nbPathFailure);
                        if (this.goalKey != null) {
                            MillLog.warning(this, "Current goal: " + Goal.goals.get(this.goalKey));
                        }
                    }
                    this.timeSinceLastPathingTimeDisplay = System.currentTimeMillis();
                    this.pathingTime = 0L;
                    this.nbPathsCalculated = 0;
                    this.nbPathNoStart = 0;
                    this.nbPathNoEnd = 0;
                    this.nbPathAborted = 0;
                    this.nbPathFailure = 0;
                }
            }
            catch (MillLog.MillenaireException e) {
                Mill.proxy.sendChatAdmin(this.func_70005_c_() + ": Error in onUpdate(). Check millenaire.log.");
                MillLog.error(this, e.getMessage());
            }
            catch (Exception e) {
                Mill.proxy.sendChatAdmin(this.func_70005_c_() + ": Error in onUpdate(). Check millenaire.log.");
                MillLog.error(this, "Exception in Villager.onUpdate(): ");
                MillLog.printException(e);
            }
            if (Math.abs(this.field_70170_p.func_72820_D() + (long)this.hashCode()) % 10L == 5L) {
                this.triggerMobAttacks();
            }
            this.updateDialogue();
            this.isUsingBow = false;
            this.isUsingHandToHand = false;
            super.func_70071_h_();
            if (MillConfigValues.DEV) {
                if (this.getPathDestPoint() == null || this.pathPlannerJPS.isBusy() || this.pathEntity == null) {
                    // empty if block
                }
                if (this.getPathDestPoint() != null && this.getGoalDestPoint() != null && !(this.getPathDestPoint().distanceTo(this.getGoalDestPoint()) > 20.0)) {
                    // empty if block
                }
            }
        }
        catch (Exception e) {
            MillLog.printException("Exception in onUpdate() of villager: " + this, e);
        }
        if (this.getTownHall() != null) {
            this.mw.reportTime(this.getTownHall(), System.nanoTime() - startTime, true);
        }
    }

    private boolean openFenceGate(int i, int j, int k) {
        Point p = new Point(i, j, k);
        IBlockState state = p.getBlockActualState(this.field_70170_p);
        if (BlockItemUtilities.isFenceGate(state.func_177230_c()) && !((Boolean)state.func_177229_b((IProperty)BlockFenceGate.field_176466_a)).booleanValue()) {
            p.setBlockState(this.field_70170_p, state.func_177226_a((IProperty)BlockFenceGate.field_176466_a, (Comparable)Boolean.valueOf(true)));
        }
        return true;
    }

    private void pathFailedSinceLastTick() {
        if (!this.vtype.noTeleport || this.getRecord() != null && this.getRecord().raidingVillage) {
            this.jumpToDest();
        }
        this.pathFailedSincelastTick = false;
    }

    public boolean performNightAction() {
        if (this.getRecord() == null || this.getHouse() == null || this.getTownHall() == null) {
            return false;
        }
        if (this.func_70631_g_()) {
            if (this.getSize() < 20) {
                this.growSize();
            } else {
                this.teenagerNightAction();
            }
        }
        if (this.getHouse().hasVisitors) {
            this.visitorNightAction();
        }
        if (this.hasChildren()) {
            return this.attemptChildConception();
        }
        return true;
    }

    public boolean func_184645_a(EntityPlayer entityplayer, EnumHand hand) {
        if (this.isVillagerSleeping()) {
            return true;
        }
        MillAdvancements.FIRST_CONTACT.grant(entityplayer);
        if (this.vtype != null && (this.vtype.key.equals("indian_sadhu") || this.vtype.key.equals("alchemist"))) {
            MillAdvancements.MAITRE_A_PENSER.grant(entityplayer);
        }
        if (this.field_70170_p.field_72995_K) {
            return true;
        }
        UserProfile profile = this.mw.getProfile(entityplayer);
        if (profile.villagersInQuests.containsKey(this.getVillagerId())) {
            QuestInstance qi = profile.villagersInQuests.get(this.getVillagerId());
            if (qi.getCurrentVillager().id == this.getVillagerId()) {
                ServerSender.displayQuestGUI(entityplayer, this);
            } else {
                this.interactSpecial(entityplayer);
            }
        } else {
            this.interactSpecial(entityplayer);
        }
        if (MillConfigValues.DEV) {
            this.interactDev(entityplayer);
        }
        return true;
    }

    public int putInBuilding(Building building, Item item, int meta, int nb) {
        nb = this.takeFromInv(item, meta, nb);
        building.storeGoods(item, meta, nb);
        return nb;
    }

    public void func_70037_a(NBTTagCompound nbttagcompound) {
        int i;
        super.func_70037_a(nbttagcompound);
        String type = nbttagcompound.func_74779_i("vtype");
        String culture = nbttagcompound.func_74779_i("culture");
        if (Culture.getCultureByName(culture) != null) {
            if (Culture.getCultureByName(culture).getVillagerType(type) != null) {
                this.vtype = Culture.getCultureByName(culture).getVillagerType(type);
            } else {
                MillLog.error(this, "Could not load dynamic NPC: unknown type: " + type + " in culture: " + culture);
            }
        } else {
            MillLog.error(this, "Could not load dynamic NPC: unknown culture: " + culture);
        }
        this.texture = new ResourceLocation("millenaire", nbttagcompound.func_74779_i("texture"));
        this.housePoint = Point.read(nbttagcompound, "housePos");
        if (this.housePoint == null) {
            MillLog.error(this, "Error when loading villager: housePoint null");
            Mill.proxy.sendChatAdmin(this.func_70005_c_() + ": Could not load house position. Check millenaire.log");
        }
        this.townHallPoint = Point.read(nbttagcompound, "townHallPos");
        if (this.townHallPoint == null) {
            MillLog.error(this, "Error when loading villager: townHallPoint null");
            Mill.proxy.sendChatAdmin(this.func_70005_c_() + ": Could not load town hall position. Check millenaire.log");
        }
        this.setGoalDestPoint(Point.read(nbttagcompound, "destPoint"));
        this.setPathDestPoint(Point.read(nbttagcompound, "pathDestPoint"), 0);
        this.setGoalBuildingDestPoint(Point.read(nbttagcompound, "destBuildingPoint"));
        this.prevPoint = Point.read(nbttagcompound, "prevPoint");
        this.doorToClose = Point.read(nbttagcompound, "doorToClose");
        this.action = nbttagcompound.func_74762_e("action");
        this.goalKey = nbttagcompound.func_74779_i("goal");
        if (this.goalKey.trim().length() == 0) {
            this.goalKey = null;
        }
        if (this.goalKey != null && !Goal.goals.containsKey(this.goalKey)) {
            this.goalKey = null;
        }
        this.constructionJobId = nbttagcompound.func_74762_e("constructionJobId");
        this.dialogueKey = nbttagcompound.func_74779_i("dialogueKey");
        this.dialogueStart = nbttagcompound.func_74763_f("dialogueStart");
        this.dialogueRole = nbttagcompound.func_74762_e("dialogueRole");
        this.dialogueColour = (char)nbttagcompound.func_74762_e("dialogueColour");
        this.dialogueChat = nbttagcompound.func_74767_n("dialogueChat");
        if (this.dialogueKey.trim().length() == 0) {
            this.dialogueKey = null;
        }
        this.familyName = nbttagcompound.func_74779_i("familyName");
        this.firstName = nbttagcompound.func_74779_i("firstName");
        this.gender = nbttagcompound.func_74762_e("gender");
        if (nbttagcompound.func_74764_b("villager_lid")) {
            this.setVillagerId(Math.abs(nbttagcompound.func_74763_f("villager_lid")));
        }
        if (!this.isTextureValid(this.texture.func_110623_a())) {
            ResourceLocation newTexture = this.vtype.getNewTexture();
            MillLog.major(this, "Texture " + this.texture.func_110623_a() + " cannot be found, replacing it with " + newTexture.func_110623_a());
            this.texture = newTexture;
        }
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("inventoryNew", 10);
        MillCommonUtilities.readInventory(nbttaglist, this.inventory);
        this.previousBlock = Block.func_149729_e((int)nbttagcompound.func_74762_e("previousBlock"));
        this.previousBlockMeta = nbttagcompound.func_74762_e("previousBlockMeta");
        this.hiredBy = nbttagcompound.func_74779_i("hiredBy");
        this.hiredUntil = nbttagcompound.func_74763_f("hiredUntil");
        this.aggressiveStance = nbttagcompound.func_74767_n("aggressiveStance");
        this.isRaider = nbttagcompound.func_74767_n("isRaider");
        this.visitorNbNights = nbttagcompound.func_74762_e("visitorNbNights");
        if (this.hiredBy.equals("")) {
            this.hiredBy = null;
        }
        if (nbttagcompound.func_74764_b("clothTexture")) {
            this.clothTexture[0] = new ResourceLocation("millenaire", nbttagcompound.func_74779_i("clothTexture"));
        } else {
            for (i = 0; i < 2; ++i) {
                if (nbttagcompound.func_74779_i("clothTexture_" + i).length() > 0) {
                    String texture = nbttagcompound.func_74779_i("clothTexture_" + i);
                    if (texture.contains(":")) {
                        this.clothTexture[i] = new ResourceLocation(texture);
                        continue;
                    }
                    this.clothTexture[i] = new ResourceLocation("millenaire", texture);
                    continue;
                }
                this.clothTexture[i] = null;
            }
        }
        this.clothName = nbttagcompound.func_74779_i("clothName");
        if (this.clothName.equals("")) {
            this.clothName = null;
            for (i = 0; i < 2; ++i) {
                this.clothTexture[i] = null;
            }
        }
        this.updateClothTexturePath();
    }

    public void func_70020_e(NBTTagCompound compound) {
        super.func_70020_e(compound);
    }

    public void readSpawnData(ByteBuf ds) {
        PacketBuffer data = new PacketBuffer(ds);
        try {
            this.setVillagerId(data.readLong());
            this.readVillagerStreamdata(data);
        }
        catch (IOException e) {
            MillLog.printException("Error in readSpawnData for villager " + this, e);
        }
    }

    private void readVillagerStreamdata(PacketBuffer data) throws IOException {
        Entity ent;
        int goalDestEntityID;
        Culture culture = Culture.getCultureByName(StreamReadWrite.readNullableString(data));
        String vt = StreamReadWrite.readNullableString(data);
        if (culture != null) {
            this.vtype = culture.getVillagerType(vt);
        }
        this.texture = StreamReadWrite.readNullableResourceLocation(data);
        this.goalKey = StreamReadWrite.readNullableString(data);
        this.constructionJobId = data.readInt();
        this.housePoint = StreamReadWrite.readNullablePoint(data);
        this.townHallPoint = StreamReadWrite.readNullablePoint(data);
        this.firstName = StreamReadWrite.readNullableString(data);
        this.familyName = StreamReadWrite.readNullableString(data);
        this.gender = data.readInt();
        this.hiredBy = StreamReadWrite.readNullableString(data);
        this.aggressiveStance = data.readBoolean();
        this.hiredUntil = data.readLong();
        this.isUsingBow = data.readBoolean();
        this.isUsingHandToHand = data.readBoolean();
        this.isRaider = data.readBoolean();
        this.speech_key = StreamReadWrite.readNullableString(data);
        this.speech_variant = data.readInt();
        this.speech_started = data.readLong();
        this.heldItem = StreamReadWrite.readNullableItemStack(data);
        this.heldItemOffHand = StreamReadWrite.readNullableItemStack(data);
        this.inventory = StreamReadWrite.readInventory(data);
        this.clothName = StreamReadWrite.readNullableString(data);
        for (int i = 0; i < 2; ++i) {
            this.clothTexture[i] = StreamReadWrite.readNullableResourceLocation(data);
        }
        this.setGoalDestPoint(StreamReadWrite.readNullablePoint(data));
        this.shouldLieDown = data.readBoolean();
        this.dialogueTargetFirstName = StreamReadWrite.readNullableString(data);
        this.dialogueTargetLastName = StreamReadWrite.readNullableString(data);
        this.dialogueColour = data.readChar();
        this.dialogueChat = data.readBoolean();
        this.func_70606_j(data.readFloat());
        this.visitorNbNights = data.readInt();
        UUID uuid = StreamReadWrite.readNullableUUID(data);
        if (uuid != null) {
            Entity targetEntity = WorldUtilities.getEntityByUUID(this.field_70170_p, uuid);
            if (targetEntity != null && targetEntity instanceof EntityLivingBase) {
                this.func_70624_b((EntityLivingBase)targetEntity);
            } else {
                this.func_70624_b(null);
            }
        } else {
            this.func_70624_b(null);
        }
        int nbMerchantSells = data.readInt();
        if (nbMerchantSells > -1) {
            this.merchantSells.clear();
            for (int i = 0; i < nbMerchantSells; ++i) {
                try {
                    TradeGood g = StreamReadWrite.readNullableGoods(data, culture);
                    this.merchantSells.put(g, data.readInt());
                    continue;
                }
                catch (MillLog.MillenaireException e) {
                    MillLog.printException(e);
                }
            }
        }
        if ((goalDestEntityID = data.readInt()) != -1 && (ent = this.field_70170_p.func_73045_a(goalDestEntityID)) != null) {
            this.setGoalDestEntity(ent);
        }
        this.isDeadOnServer = data.readBoolean();
        this.client_lastupdated = this.field_70170_p.func_72820_D();
    }

    public void registerNewPath(AS_PathEntity path) throws Exception {
        if (path == null) {
            boolean handled = false;
            if (this.goalKey != null) {
                Goal goal = Goal.goals.get(this.goalKey);
                handled = goal.unreachableDestination(this);
            }
            if (!handled) {
                this.clearGoal();
            }
        } else {
            try {
                this.field_70699_by.func_75484_a((Path)path, 0.5);
            }
            catch (Exception e) {
                MillLog.major(null, "Goal : " + this.goalKey);
                MillLog.major(null, "Path to : " + this.pathDestPoint.x + "/" + this.pathDestPoint.y + "/" + this.pathDestPoint.z);
                MillLog.printException(this + ": Pathing error detected", e);
            }
            this.pathEntity = path;
            this.field_70702_br = 0.0f;
        }
    }

    public void registerNewPath(List<PathPoint> result) throws Exception {
        AS_PathEntity path = null;
        if (result != null) {
            PathPoint[] pointsCopy = new PathPoint[result.size()];
            int i = 0;
            for (PathPoint p : result) {
                PathPoint p2;
                pointsCopy[i] = p == null ? null : (p2 = new PathPoint(p.field_75839_a, p.field_75837_b, p.field_75838_c));
                ++i;
            }
            path = new AS_PathEntity(pointsCopy);
        }
        this.registerNewPath(path);
    }

    public HashMap<InvItem, Integer> requiresGoods() {
        if (this.func_70631_g_() && this.getSize() < 20) {
            return this.vtype.requiredFoodAndGoods;
        }
        if (this.hasChildren() && this.getHouse() != null && this.getHouse().getKnownVillagers().size() < 4) {
            return this.vtype.requiredFoodAndGoods;
        }
        return this.vtype.requiredGoods;
    }

    private void sendVillagerPacket() {
        PacketBuffer data = ServerSender.getPacketBuffer();
        try {
            data.writeInt(3);
            this.writeVillagerStreamData((ByteBuf)data, false);
        }
        catch (IOException e) {
            MillLog.printException(this + ": Error in sendVillagerPacket", e);
        }
        ServerSender.sendPacketToPlayersInRange(data, this.getPos(), 100);
    }

    public boolean setBlock(Point p, Block block) {
        return WorldUtilities.setBlock(this.field_70170_p, p, block, true, true);
    }

    public boolean setBlockAndMetadata(Point p, Block block, int metadata) {
        return WorldUtilities.setBlockAndMetadata(this.field_70170_p, p, block, metadata, true, true);
    }

    public boolean setBlockMetadata(Point p, int metadata) {
        return WorldUtilities.setBlockMetadata(this.field_70170_p, p, metadata);
    }

    public boolean setBlockstate(Point p, IBlockState bs) {
        return WorldUtilities.setBlockstate(this.field_70170_p, p, bs, true, true);
    }

    public void func_70106_y() {
        if (this.func_110143_aJ() <= 0.0f) {
            this.killVillager();
        }
        super.func_70106_y();
    }

    private void setFacingDirection() {
        if (this.func_70638_az() != null) {
            this.faceEntityMill((Entity)this.func_70638_az(), 30.0f, 30.0f);
            return;
        }
        if (this.goalKey != null && (this.getGoalDestPoint() != null || this.getGoalDestEntity() != null)) {
            EntityPlayer player;
            Goal goal = Goal.goals.get(this.goalKey);
            if (goal.lookAtGoal()) {
                if (this.getGoalDestEntity() != null && this.getPos().distanceTo(this.getGoalDestEntity()) < (double)goal.range(this)) {
                    this.faceEntityMill(this.getGoalDestEntity(), 10.0f, 10.0f);
                } else if (this.getGoalDestPoint() != null && this.getPos().distanceTo(this.getGoalDestPoint()) < (double)goal.range(this)) {
                    this.facePoint(this.getGoalDestPoint(), 10.0f, 10.0f);
                }
            }
            if (goal.lookAtPlayer() && (player = this.field_70170_p.func_72890_a((Entity)this, 10.0)) != null) {
                this.faceEntityMill((Entity)player, 10.0f, 10.0f);
                return;
            }
        }
    }

    public void setGoalBuildingDestPoint(Point newDest) {
        if (this.goalInformation == null) {
            this.goalInformation = new Goal.GoalInformation(null, null, null);
        }
        this.goalInformation.setDestBuildingPos(newDest);
    }

    public void setGoalDestEntity(Entity ent) {
        if (this.goalInformation == null) {
            this.goalInformation = new Goal.GoalInformation(null, null, null);
        }
        this.goalInformation.setTargetEnt(ent);
        if (ent != null) {
            this.setPathDestPoint(new Point(ent), 2);
        }
        if (ent instanceof MillVillager) {
            MillVillager v = (MillVillager)ent;
            this.dialogueTargetFirstName = v.firstName;
            this.dialogueTargetLastName = v.familyName;
        }
    }

    public void setGoalDestPoint(Point newDest) {
        if (this.goalInformation == null) {
            this.goalInformation = new Goal.GoalInformation(null, null, null);
        }
        this.goalInformation.setDest(newDest);
        this.setPathDestPoint(newDest, 0);
    }

    public void setGoalInformation(Goal.GoalInformation info) {
        this.goalInformation = info;
        if (info != null) {
            if (info.getTargetEnt() != null) {
                this.setPathDestPoint(new Point(info.getTargetEnt()), 2);
            } else if (info.getDest() != null) {
                this.setPathDestPoint(info.getDest(), 0);
            } else {
                this.setPathDestPoint(null, 0);
            }
        } else {
            this.setPathDestPoint(null, 0);
        }
    }

    public void setHousePoint(Point p) {
        this.housePoint = p;
        this.house = null;
    }

    public void setInv(Item item, int meta, int nb) {
        this.inventory.put(InvItem.createInvItem(item, meta), nb);
        this.updateVillagerRecord();
    }

    public void setNextGoal() throws Exception {
        ConstructionIP cip;
        Goal nextGoal = null;
        this.clearGoal();
        for (Goal goal : this.getGoals()) {
            if (!goal.isPossible(this)) continue;
            if (MillConfigValues.LogGeneralAI >= 2 && this.extraLog) {
                MillLog.minor(this, "Priority for goal " + goal.gameName(this) + ": " + goal.priority(this));
            }
            if (nextGoal == null || nextGoal.leasure && !goal.leasure) {
                nextGoal = goal;
                continue;
            }
            if (nextGoal != null && nextGoal.priority(this) >= goal.priority(this)) continue;
            nextGoal = goal;
        }
        if (MillConfigValues.LogGeneralAI >= 2 && this.extraLog) {
            MillLog.minor(this, "Selected this: " + nextGoal);
        }
        if (nextGoal != null) {
            this.speakSentence(nextGoal.key + ".chosen");
            this.goalKey = nextGoal.key;
            this.heldItem = ItemStack.field_190927_a;
            this.heldItemOffHand = ItemStack.field_190927_a;
            this.heldItemCount = Integer.MAX_VALUE;
            nextGoal.onAccept(this);
            this.goalStarted = this.field_70170_p.func_72820_D();
            this.lastGoalTime.put(nextGoal, this.field_70170_p.func_72820_D());
            IAttributeInstance iattributeinstance = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
            iattributeinstance.func_111124_b(SPRINT_SPEED_BOOST);
            if (nextGoal.sprint) {
                iattributeinstance.func_111121_a(SPRINT_SPEED_BOOST);
            }
        } else {
            this.goalKey = null;
        }
        if (MillConfigValues.LogBuildingPlan >= 1 && nextGoal != null && nextGoal.key.equals(Goal.getResourcesForBuild.key) && (cip = this.getCurrentConstruction()) != null) {
            MillLog.major(this, this.func_70005_c_() + " is new builder, for: " + cip.getBuildingLocation().planKey + "_" + cip.getBuildingLocation().level + ". Blocks loaded: " + cip.getBblocks().length);
        }
    }

    public void setPathDestPoint(Point newDest, int tolerance) {
        if (!(newDest != null && newDest.equals(this.pathDestPoint) || this.pathDestPoint != null && newDest != null && !((double)tolerance < newDest.distanceTo(this.pathDestPoint)))) {
            this.field_70699_by.func_75499_g();
            this.pathEntity = null;
        }
        this.pathDestPoint = newDest;
    }

    public void setTexture(ResourceLocation tx) {
        this.texture = tx;
    }

    public void setTownHallPoint(Point p) {
        this.townHallPoint = p;
        this.townHall = null;
    }

    public void setVillagerId(long villagerId) {
        this.villagerId = villagerId;
    }

    public void speakSentence(String key) {
        this.speakSentence(key, 600, 3, 1);
    }

    public void speakSentence(String key, int delay, int distance, int chanceOn) {
        if ((long)delay > this.field_70170_p.func_72820_D() - this.speech_started) {
            return;
        }
        if (!MillCommonUtilities.chanceOn(chanceOn)) {
            return;
        }
        if (this.getTownHall() == null || this.getTownHall().closestPlayer == null || this.getPos().distanceTo((Entity)this.getTownHall().closestPlayer) > (double)distance) {
            return;
        }
        key = key.toLowerCase();
        this.speech_key = null;
        if (this.getCulture().hasSentences(this.getNameKey() + "." + key)) {
            this.speech_key = this.getNameKey() + "." + key;
        } else if (this.getCulture().hasSentences(this.getGenderString() + "." + key)) {
            this.speech_key = this.getGenderString() + "." + key;
        } else if (this.getCulture().hasSentences("villager." + key)) {
            this.speech_key = "villager." + key;
        }
        if (this.speech_key != null) {
            this.speech_variant = MillCommonUtilities.randomInt(this.getCulture().getSentences(this.speech_key).size());
            this.speech_started = this.field_70170_p.func_72820_D();
            this.sendVillagerPacket();
            ServerSender.sendVillageSentenceInRange(this.field_70170_p, this.getPos(), 30, this);
        }
    }

    public void specificUpdate() throws Exception {
        if (this.isLocalMerchant()) {
            this.localMerchantUpdate();
        }
        if (this.isForeignMerchant()) {
            this.foreignMerchantUpdate();
        }
    }

    public int takeFromBuilding(Building building, Item item, int meta, int nb) {
        if (item == Item.func_150898_a((Block)Blocks.field_150364_r) && meta == -1) {
            int total = 0;
            int nb2 = building.takeGoods(item, 0, nb);
            this.addToInv(item, 0, nb2);
            total += nb2;
            nb2 = building.takeGoods(item, 1, nb - total);
            this.addToInv(item, 0, nb2);
            total += nb2;
            nb2 = building.takeGoods(item, 2, nb - total);
            this.addToInv(item, 0, nb2);
            total += nb2;
            nb2 = building.takeGoods(item, 3, nb - total);
            this.addToInv(item, 0, nb2);
            total += nb2;
            nb2 = building.takeGoods(Item.func_150898_a((Block)Blocks.field_150363_s), 0, nb - total);
            this.addToInv(item, 0, nb2);
            total += nb2;
            nb2 = building.takeGoods(Item.func_150898_a((Block)Blocks.field_150363_s), 1, nb - total);
            this.addToInv(item, 0, nb2);
            return total += nb2;
        }
        nb = building.takeGoods(item, meta, nb);
        this.addToInv(item, meta, nb);
        return nb;
    }

    public int takeFromInv(Block block, int meta, int nb) {
        return this.takeFromInv(Item.func_150898_a((Block)block), meta, nb);
    }

    public int takeFromInv(IBlockState blockState, int nb) {
        return this.takeFromInv(Item.func_150898_a((Block)blockState.func_177230_c()), blockState.func_177230_c().func_176201_c(blockState), nb);
    }

    public int takeFromInv(InvItem item, int nb) {
        return this.takeFromInv(item.getItem(), item.meta, nb);
    }

    public int takeFromInv(Item item, int meta, int nb) {
        if (item == Item.func_150898_a((Block)Blocks.field_150364_r) && meta == -1) {
            int nb2;
            InvItem key;
            int i;
            int total = 0;
            for (i = 0; i < 16; ++i) {
                key = InvItem.createInvItem(item, i);
                if (!this.inventory.containsKey(key)) continue;
                nb2 = Math.min(nb, this.inventory.get(key));
                this.inventory.put(key, this.inventory.get(key) - nb2);
                total += nb2;
            }
            for (i = 0; i < 16; ++i) {
                key = InvItem.createInvItem(Item.func_150898_a((Block)Blocks.field_150363_s), i);
                if (!this.inventory.containsKey(key)) continue;
                nb2 = Math.min(nb, this.inventory.get(key));
                this.inventory.put(key, this.inventory.get(key) - nb2);
                total += nb2;
            }
            this.updateVillagerRecord();
            return total;
        }
        InvItem key = InvItem.createInvItem(item, meta);
        if (this.inventory.containsKey(key)) {
            nb = Math.min(nb, this.inventory.get(key));
            this.inventory.put(key, this.inventory.get(key) - nb);
            this.updateVillagerRecord();
            this.updateClothTexturePath();
            return nb;
        }
        return 0;
    }

    private void targetDefender() {
        int bestDist = Integer.MAX_VALUE;
        MillVillager target = null;
        for (MillVillager v : this.getTownHall().getKnownVillagers()) {
            if (!v.helpsInAttacks() || v.isRaider || !(this.getPos().distanceToSquared((Entity)v) < (double)bestDist)) continue;
            target = v;
            bestDist = (int)this.getPos().distanceToSquared((Entity)v);
        }
        if (target != null && this.getPos().distanceToSquared((Entity)target) <= 100.0) {
            this.func_70624_b((EntityLivingBase)target);
        }
    }

    private void targetRaider() {
        int bestDist = Integer.MAX_VALUE;
        MillVillager target = null;
        for (MillVillager v : this.getTownHall().getKnownVillagers()) {
            if (!v.isRaider || !(this.getPos().distanceToSquared((Entity)v) < (double)bestDist)) continue;
            target = v;
            bestDist = (int)this.getPos().distanceToSquared((Entity)v);
        }
        if (target != null && this.getPos().distanceToSquared((Entity)target) <= 25.0) {
            this.func_70624_b((EntityLivingBase)target);
        }
    }

    private void teenagerNightAction() {
        for (Point p : this.getTownHall().getKnownVillages()) {
            Building distantVillage;
            if (this.getTownHall().getRelationWithVillage(p) <= 90 || (distantVillage = this.mw.getBuilding(p)) == null || distantVillage.culture != this.getCulture() || distantVillage == this.getTownHall()) continue;
            boolean canMoveIn = false;
            if (MillConfigValues.LogChildren >= 1) {
                MillLog.major(this, "Attempting to move to village: " + distantVillage.getVillageQualifiedName());
            }
            Building distantInn = null;
            for (Building distantBuilding : distantVillage.getBuildings()) {
                if (!canMoveIn && distantBuilding != null && distantBuilding.isHouse()) {
                    if (!distantBuilding.canChildMoveIn(this.gender, this.familyName)) continue;
                    canMoveIn = true;
                    continue;
                }
                if (distantInn != null || !distantBuilding.isInn || distantBuilding.getAllVillagerRecords().size() >= 2) continue;
                distantInn = distantBuilding;
            }
            if (!canMoveIn || distantInn == null) continue;
            if (MillConfigValues.LogChildren >= 1) {
                MillLog.major(this, "Moving to village: " + distantVillage.getVillageQualifiedName());
            }
            this.getHouse().transferVillagerPermanently(this.getRecord(), distantInn);
            distantInn.visitorsList.add("panels.childarrived;" + this.func_70005_c_() + ";" + this.getTownHall().getVillageQualifiedName());
        }
    }

    public boolean teleportTo(double d, double d1, double d2) {
        int k;
        int j;
        double d3 = this.field_70165_t;
        double d4 = this.field_70163_u;
        double d5 = this.field_70161_v;
        this.field_70165_t = d;
        this.field_70163_u = d1;
        this.field_70161_v = d2;
        boolean flag = false;
        int i = MathHelper.func_76128_c((double)this.field_70165_t);
        if (this.field_70170_p.func_175667_e(new BlockPos(i, j = MathHelper.func_76128_c((double)this.field_70163_u), k = MathHelper.func_76128_c((double)this.field_70161_v)))) {
            boolean flag1 = false;
            while (!flag1 && j > 0) {
                IBlockState bs = WorldUtilities.getBlockState(this.field_70170_p, i, j - 1, k);
                if (bs.func_177230_c() == Blocks.field_150350_a || !bs.func_185904_a().func_76230_c()) {
                    this.field_70163_u -= 1.0;
                    --j;
                    continue;
                }
                flag1 = true;
            }
            if (flag1) {
                this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
                if (this.field_70170_p.func_184144_a((Entity)this, this.func_174813_aQ()).size() == 0 && !this.field_70170_p.func_72953_d(this.func_174813_aQ())) {
                    flag = true;
                }
            }
        }
        if (!flag) {
            this.func_70107_b(d3, d4, d5);
            return false;
        }
        return true;
    }

    public boolean teleportToEntity(Entity entity) {
        Vec3d vec3d = new Vec3d(this.field_70165_t - entity.field_70165_t, this.func_174813_aQ().field_72338_b + (double)(this.field_70131_O / 2.0f) - entity.field_70163_u + (double)entity.func_70047_e(), this.field_70161_v - entity.field_70161_v);
        vec3d = vec3d.func_72432_b();
        double d = 16.0;
        double d1 = this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5) * 8.0 - vec3d.field_72450_a * 16.0;
        double d2 = this.field_70163_u + (double)(this.field_70146_Z.nextInt(16) - 8) - vec3d.field_72448_b * 16.0;
        double d3 = this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5) * 8.0 - vec3d.field_72449_c * 16.0;
        return this.teleportTo(d1, d2, d3);
    }

    private void toggleDoor(Point p) {
        IBlockState state = p.getBlockActualState(this.field_70170_p);
        state = (Boolean)state.func_177229_b((IProperty)BlockDoor.field_176519_b) != false ? state.func_177226_a((IProperty)BlockDoor.field_176519_b, (Comparable)Boolean.valueOf(false)) : state.func_177226_a((IProperty)BlockDoor.field_176519_b, (Comparable)Boolean.valueOf(true));
        p.setBlockState(this.field_70170_p, state);
    }

    public String toString() {
        if (this.vtype != null) {
            return this.func_70005_c_() + "/" + this.vtype.key + "/" + this.getVillagerId() + "/" + this.getPos();
        }
        return this.func_70005_c_() + "/none/" + this.getVillagerId() + "/" + this.getPos();
    }

    private void triggerMobAttacks() {
        List<Entity> entities = WorldUtilities.getEntitiesWithinAABB(this.field_70170_p, EntityMob.class, this.getPos(), 16, 5);
        for (Entity ent : entities) {
            EntityMob mob = (EntityMob)ent;
            if (mob.func_70638_az() != null || !mob.func_70685_l((Entity)this)) continue;
            mob.func_70624_b((EntityLivingBase)this);
        }
    }

    private void unlockForNearbyPlayers() {
        UserProfile profile;
        EntityPlayer player = this.field_70170_p.func_184137_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, 5.0, false);
        if (player != null && (profile = this.mw.getProfile(player)) != null) {
            profile.unlockVillager(this.getCulture(), this.vtype);
        }
    }

    private void updateClothTexturePath() {
        block8: {
            block7: {
                if (this.vtype == null) {
                    return;
                }
                boolean[] naturalLayers = this.vtype.getClothLayersOfType(NATURAL);
                String bestClothName = null;
                int clothLevel = -1;
                if (this.vtype.hasClothTexture(FREE_CLOTHES)) {
                    bestClothName = FREE_CLOTHES;
                    clothLevel = 0;
                }
                for (InvItem iv : this.inventory.keySet()) {
                    ItemClothes clothes;
                    if (!(iv.item instanceof ItemClothes) || this.inventory.get(iv) <= 0 || (clothes = (ItemClothes)iv.item).getClothPriority(iv.meta) <= clothLevel || !this.vtype.hasClothTexture(clothes.getClothName(iv.meta))) continue;
                    bestClothName = clothes.getClothName(iv.meta);
                    clothLevel = clothes.getClothPriority(iv.meta);
                }
                if (bestClothName == null) break block7;
                if (bestClothName.equals(this.clothName)) break block8;
                this.clothName = bestClothName;
                for (int layer = 0; layer < 2; ++layer) {
                    String texture = naturalLayers[layer] ? this.vtype.getRandomClothTexture(NATURAL, layer) : this.vtype.getRandomClothTexture(bestClothName, layer);
                    if (texture != null && texture.length() > 0) {
                        if (texture.contains(":")) {
                            this.clothTexture[layer] = new ResourceLocation(texture);
                            continue;
                        }
                        this.clothTexture[layer] = new ResourceLocation("millenaire", texture);
                        continue;
                    }
                    this.clothTexture[layer] = null;
                }
                break block8;
            }
            this.clothName = null;
            for (int i = 0; i < 2; ++i) {
                this.clothTexture[i] = null;
            }
        }
    }

    private void updateDialogue() {
        if (this.dialogueKey == null) {
            return;
        }
        CultureLanguage.Dialogue d = this.getCulture().getDialogue(this.dialogueKey);
        if (d == null) {
            this.dialogueKey = null;
            return;
        }
        long timePassed = this.field_70170_p.func_72820_D() - this.dialogueStart;
        if ((long)(d.timeDelays.get(d.timeDelays.size() - 1) + 100) < timePassed) {
            this.dialogueKey = null;
            return;
        }
        String toSpeakKey = null;
        for (int i = 0; i < d.speechBy.size(); ++i) {
            if (this.dialogueRole != d.speechBy.get(i) || timePassed < (long)d.timeDelays.get(i).intValue()) continue;
            toSpeakKey = "chat_" + d.key + "_" + i;
        }
        if (!(toSpeakKey == null || this.speech_key != null && this.speech_key.contains(toSpeakKey))) {
            this.speakSentence(toSpeakKey, 0, 10, 1);
        }
    }

    private void updateHired() {
        try {
            if (this.func_110143_aJ() < this.func_110138_aP() & MillCommonUtilities.randomInt(1600) == 0) {
                this.func_70606_j(this.func_110143_aJ() + 1.0f);
            }
            EntityPlayer entityplayer = this.field_70170_p.func_72924_a(this.hiredBy);
            if (this.field_70170_p.func_72820_D() > this.hiredUntil) {
                if (entityplayer != null) {
                    ServerSender.sendTranslatedSentence(entityplayer, 'f', "hire.hireover", this.func_70005_c_());
                }
                this.hiredBy = null;
                this.hiredUntil = 0L;
                VillagerRecord vr = this.getRecord();
                if (vr != null) {
                    vr.awayhired = false;
                }
                return;
            }
            if (this.func_70638_az() != null) {
                if (this.getPos().distanceTo((Entity)this.func_70638_az()) > 80.0 || this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL || this.func_70638_az().field_70128_L) {
                    this.func_70624_b(null);
                }
            } else if (this.isHostile() && this.field_70170_p.func_175659_aa() != EnumDifficulty.PEACEFUL && this.getTownHall().closestPlayer != null && this.getPos().distanceTo((Entity)this.getTownHall().closestPlayer) <= 80.0) {
                this.func_70624_b((EntityLivingBase)this.field_70170_p.func_184137_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, 100.0, true));
            }
            if (this.func_70638_az() == null) {
                List list = this.field_70170_p.func_72872_a(EntityCreature.class, new AxisAlignedBB(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70165_t + 1.0, this.field_70163_u + 1.0, this.field_70161_v + 1.0).func_72321_a(16.0, 8.0, 16.0));
                for (Object o : list) {
                    EntityCreature creature;
                    if (this.func_70638_az() != null || (creature = (EntityCreature)o).func_70638_az() != entityplayer || creature instanceof EntityCreeper) continue;
                    this.func_70624_b((EntityLivingBase)creature);
                }
                if (this.func_70638_az() == null && this.aggressiveStance) {
                    list = this.field_70170_p.func_72872_a(EntityMob.class, new AxisAlignedBB(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70165_t + 1.0, this.field_70163_u + 1.0, this.field_70161_v + 1.0).func_72321_a(16.0, 8.0, 16.0));
                    if (!list.isEmpty()) {
                        this.func_70624_b((EntityLivingBase)list.get(this.field_70170_p.field_73012_v.nextInt(list.size())));
                        if (this.func_70638_az() instanceof EntityCreeper) {
                            this.func_70624_b(null);
                        }
                    }
                    if (this.func_70638_az() == null) {
                        list = this.field_70170_p.func_72872_a(MillVillager.class, new AxisAlignedBB(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70165_t + 1.0, this.field_70163_u + 1.0, this.field_70161_v + 1.0).func_72321_a(16.0, 8.0, 16.0));
                        for (Object o : list) {
                            MillVillager villager;
                            if (this.func_70638_az() != null || !(villager = (MillVillager)o).isHostile()) continue;
                            this.func_70624_b((EntityLivingBase)villager);
                        }
                    }
                }
            }
            EntityLivingBase target = null;
            if (this.func_70638_az() != null) {
                target = this.func_70638_az();
                this.heldItem = this.getWeapon();
                this.heldItemOffHand = ItemStack.field_190927_a;
                Path newPathEntity = this.func_70661_as().func_75494_a((Entity)target);
                if (newPathEntity != null) {
                    this.func_70661_as().func_75484_a(newPathEntity, 0.5);
                }
                this.attackEntity((Entity)this.func_70638_az());
            } else {
                this.heldItem = ItemStack.field_190927_a;
                this.heldItemOffHand = ItemStack.field_190927_a;
                target = entityplayer;
                int dist = (int)this.getPos().distanceTo((Entity)target);
                if (dist > 16) {
                    this.teleportToEntity((Entity)entityplayer);
                } else if (dist > 4) {
                    Path newPathEntity;
                    boolean rebuildPath = false;
                    if (this.func_70661_as().func_75505_d() == null) {
                        rebuildPath = true;
                    } else {
                        Point currentTargetPoint = new Point(this.func_70661_as().func_75505_d().func_75870_c());
                        if (currentTargetPoint.distanceTo((Entity)entityplayer) > 2.0) {
                            rebuildPath = true;
                        }
                    }
                    if (rebuildPath && (newPathEntity = this.func_70661_as().func_75494_a((Entity)target)) != null) {
                        this.func_70661_as().func_75484_a(newPathEntity, 0.5);
                    }
                }
            }
            this.prevPoint = this.getPos();
            this.handleDoorsAndFenceGates();
        }
        catch (Exception e) {
            MillLog.printException("Error in hired onUpdate():", e);
        }
    }

    private void updatePathIfNeeded(Point dest) throws Exception {
        if (dest == null) {
            return;
        }
        if (this.pathEntity != null && this.pathEntity.func_75874_d() > 0 && !MillCommonUtilities.chanceOn(50) && this.pathEntity.getCurrentTargetPathPoint() != null) {
            this.func_70661_as().func_75484_a((Path)this.pathEntity, 0.5);
        } else if (!this.pathPlannerJPS.isBusy()) {
            this.computeNewPath(dest);
        }
    }

    public float func_70663_b(float f, float f1, float f2) {
        float f3;
        for (f3 = f1 - f; f3 < -180.0f; f3 += 360.0f) {
        }
        while (f3 >= 180.0f) {
            f3 -= 360.0f;
        }
        if (f3 > f2) {
            f3 = f2;
        }
        if (f3 < -f2) {
            f3 = -f2;
        }
        return f + f3;
    }

    public void updateVillagerRecord() {
        if (!this.field_70170_p.field_72995_K) {
            this.getRecord().updateRecord(this);
        }
    }

    private boolean visitorNightAction() {
        ++this.visitorNbNights;
        if (this.visitorNbNights > 5) {
            this.leaveVillage();
        } else if (this.isForeignMerchant()) {
            boolean hasItems = false;
            for (InvItem key : this.vtype.foreignMerchantStock.keySet()) {
                if (this.getHouse().countGoods(key) <= 0) continue;
                hasItems = true;
            }
            if (!hasItems) {
                this.leaveVillage();
            }
        }
        return true;
    }

    public void func_70014_b(NBTTagCompound nbttagcompound) {
        try {
            if (this.vtype == null) {
                MillLog.error(this, "Not saving villager due to null vtype.");
                return;
            }
            super.func_70014_b(nbttagcompound);
            nbttagcompound.func_74778_a("vtype", this.vtype.key);
            nbttagcompound.func_74778_a("culture", this.getCulture().key);
            nbttagcompound.func_74778_a("texture", this.texture.func_110623_a());
            if (this.housePoint != null) {
                this.housePoint.write(nbttagcompound, "housePos");
            }
            if (this.townHallPoint != null) {
                this.townHallPoint.write(nbttagcompound, "townHallPos");
            }
            if (this.getGoalDestPoint() != null) {
                this.getGoalDestPoint().write(nbttagcompound, "destPoint");
            }
            if (this.getGoalBuildingDestPoint() != null) {
                this.getGoalBuildingDestPoint().write(nbttagcompound, "destBuildingPoint");
            }
            if (this.getPathDestPoint() != null) {
                this.getPathDestPoint().write(nbttagcompound, "pathDestPoint");
            }
            if (this.prevPoint != null) {
                this.prevPoint.write(nbttagcompound, "prevPoint");
            }
            if (this.doorToClose != null) {
                this.doorToClose.write(nbttagcompound, "doorToClose");
            }
            nbttagcompound.func_74768_a("action", this.action);
            if (this.goalKey != null) {
                nbttagcompound.func_74778_a("goal", this.goalKey);
            }
            nbttagcompound.func_74768_a("constructionJobId", this.constructionJobId);
            nbttagcompound.func_74778_a("firstName", this.firstName);
            nbttagcompound.func_74778_a("familyName", this.familyName);
            nbttagcompound.func_74768_a("gender", this.gender);
            nbttagcompound.func_74772_a("lastSpeechLong", this.speech_started);
            nbttagcompound.func_74772_a("villager_lid", this.getVillagerId());
            if (this.dialogueKey != null) {
                nbttagcompound.func_74778_a("dialogueKey", this.dialogueKey);
                nbttagcompound.func_74772_a("dialogueStart", this.dialogueStart);
                nbttagcompound.func_74768_a("dialogueRole", this.dialogueRole);
                nbttagcompound.func_74768_a("dialogueColour", (int)this.dialogueColour);
                nbttagcompound.func_74757_a("dialogueChat", this.dialogueChat);
            }
            NBTTagList nbttaglist = MillCommonUtilities.writeInventory(this.inventory);
            nbttagcompound.func_74782_a("inventoryNew", (NBTBase)nbttaglist);
            nbttagcompound.func_74768_a("previousBlock", Block.func_149682_b((Block)this.previousBlock));
            nbttagcompound.func_74768_a("previousBlockMeta", this.previousBlockMeta);
            if (this.hiredBy != null) {
                nbttagcompound.func_74778_a("hiredBy", this.hiredBy);
                nbttagcompound.func_74772_a("hiredUntil", this.hiredUntil);
                nbttagcompound.func_74757_a("aggressiveStance", this.aggressiveStance);
            }
            nbttagcompound.func_74757_a("isRaider", this.isRaider);
            nbttagcompound.func_74768_a("visitorNbNights", this.visitorNbNights);
            if (this.clothName != null) {
                nbttagcompound.func_74778_a("clothName", this.clothName);
                for (int layer = 0; layer < 2; ++layer) {
                    if (this.clothTexture[layer] == null) continue;
                    nbttagcompound.func_74778_a("clothTexture_" + layer, this.clothTexture[layer].toString());
                }
            }
        }
        catch (Exception e) {
            MillLog.printException("Exception when attempting to save villager " + this, e);
        }
    }

    public void writeSpawnData(ByteBuf ds) {
        try {
            this.writeVillagerStreamData(ds, true);
        }
        catch (IOException e) {
            MillLog.printException("Error in writeSpawnData for villager " + this, e);
        }
    }

    private void writeVillagerStreamData(ByteBuf bb, boolean isSpawn) throws IOException {
        if (this.vtype == null) {
            MillLog.error(this, "Cannot write stream data due to null vtype.");
            return;
        }
        PacketBuffer data = bb instanceof PacketBuffer ? (PacketBuffer)bb : new PacketBuffer(bb);
        data.writeLong(this.getVillagerId());
        StreamReadWrite.writeNullableString(this.vtype.culture.key, data);
        StreamReadWrite.writeNullableString(this.vtype.key, data);
        StreamReadWrite.writeNullableResourceLocation(this.texture, data);
        StreamReadWrite.writeNullableString(this.goalKey, data);
        data.writeInt(this.constructionJobId);
        StreamReadWrite.writeNullablePoint(this.housePoint, data);
        StreamReadWrite.writeNullablePoint(this.townHallPoint, data);
        StreamReadWrite.writeNullableString(this.firstName, data);
        StreamReadWrite.writeNullableString(this.familyName, data);
        data.writeInt(this.gender);
        StreamReadWrite.writeNullableString(this.hiredBy, data);
        data.writeBoolean(this.aggressiveStance);
        data.writeLong(this.hiredUntil);
        data.writeBoolean(this.isUsingBow);
        data.writeBoolean(this.isUsingHandToHand);
        data.writeBoolean(this.isRaider);
        StreamReadWrite.writeNullableString(this.speech_key, data);
        data.writeInt(this.speech_variant);
        data.writeLong(this.speech_started);
        StreamReadWrite.writeNullableItemStack(this.heldItem, data);
        StreamReadWrite.writeNullableItemStack(this.heldItemOffHand, data);
        StreamReadWrite.writeInventory(this.inventory, data);
        StreamReadWrite.writeNullableString(this.clothName, data);
        for (int i = 0; i < 2; ++i) {
            StreamReadWrite.writeNullableResourceLocation(this.clothTexture[i], data);
        }
        StreamReadWrite.writeNullablePoint(this.getGoalDestPoint(), data);
        data.writeBoolean(this.shouldLieDown);
        StreamReadWrite.writeNullableString(this.dialogueTargetFirstName, data);
        StreamReadWrite.writeNullableString(this.dialogueTargetLastName, data);
        data.writeChar((int)this.dialogueColour);
        data.writeBoolean(this.dialogueChat);
        data.writeFloat(this.func_110143_aJ());
        data.writeInt(this.visitorNbNights);
        if (this.func_70638_az() != null) {
            StreamReadWrite.writeNullableUUID(this.func_70638_az().func_110124_au(), data);
        } else {
            StreamReadWrite.writeNullableUUID(null, data);
        }
        if (isSpawn) {
            this.calculateMerchantGoods();
            data.writeInt(this.merchantSells.size());
            for (TradeGood g : this.merchantSells.keySet()) {
                StreamReadWrite.writeNullableGoods(g, data);
                data.writeInt(this.merchantSells.get(g).intValue());
            }
        } else {
            data.writeInt(-1);
        }
        if (this.getGoalDestEntity() != null) {
            data.writeInt(this.getGoalDestEntity().func_145782_y());
        } else {
            data.writeInt(-1);
        }
        data.writeBoolean(this.field_70128_L);
    }

    public static class InvItemAlphabeticalComparator
    implements Comparator<InvItem> {
        @Override
        public int compare(InvItem arg0, InvItem arg1) {
            return arg0.getName().compareTo(arg1.getName());
        }
    }

    public static class EntityGenericSymmFemale
    extends MillVillager {
        public EntityGenericSymmFemale(World world) {
            super(world);
        }
    }

    public static class EntityGenericMale
    extends MillVillager {
        public EntityGenericMale(World world) {
            super(world);
        }
    }

    public static class EntityGenericAsymmFemale
    extends MillVillager {
        public EntityGenericAsymmFemale(World world) {
            super(world);
        }
    }
}

