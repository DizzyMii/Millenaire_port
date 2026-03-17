/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.BlockNewLog
 *  net.minecraft.block.BlockOldLeaf
 *  net.minecraft.block.BlockOldLog
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.BlockSapling
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.monster.EntityCreeper
 *  net.minecraft.entity.monster.EntityEnderman
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.passive.EntityAnimal
 *  net.minecraft.entity.passive.EntityChicken
 *  net.minecraft.entity.passive.EntityCow
 *  net.minecraft.entity.passive.EntityPig
 *  net.minecraft.entity.passive.EntitySheep
 *  net.minecraft.entity.passive.EntitySquid
 *  net.minecraft.entity.passive.EntityWolf
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBanner
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.CompressedStreamTools
 *  net.minecraft.nbt.JsonToNBT
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTException
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityBanner
 *  net.minecraft.tileentity.TileEntityDispenser
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraft.world.gen.ChunkProviderServer
 *  net.minecraft.world.gen.feature.WorldGenBirchTree
 *  net.minecraft.world.gen.feature.WorldGenCanopyTree
 *  net.minecraft.world.gen.feature.WorldGenSavannaTree
 *  net.minecraft.world.gen.feature.WorldGenTaiga2
 *  net.minecraft.world.gen.feature.WorldGenTrees
 */
package org.millenaire.common.village;

import com.mojang.authlib.GameProfile;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGenBirchTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.block.BlockMillStainedGlass;
import org.millenaire.common.block.BlockPaintedBricks;
import org.millenaire.common.block.IBlockPath;
import org.millenaire.common.block.IPaintedBlock;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingBlock;
import org.millenaire.common.buildingplan.BuildingCustomPlan;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.TileEntityFirePit;
import org.millenaire.common.entity.TileEntityLockedChest;
import org.millenaire.common.forge.BuildingChunkLoader;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.ItemParchment;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.pathing.atomicstryker.AStarNode;
import org.millenaire.common.pathing.atomicstryker.AStarPathPlannerJPS;
import org.millenaire.common.pathing.atomicstryker.IAStarPathedEntity;
import org.millenaire.common.pathing.atomicstryker.RegionMapper;
import org.millenaire.common.ui.MillMapInfo;
import org.millenaire.common.ui.PujaSacrifice;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.PathUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.ThreadSafeUtilities;
import org.millenaire.common.utilities.VillageUtilities;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.village.ConstructionIP;
import org.millenaire.common.village.VillageMapInfo;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.village.buildingmanagers.MarvelManager;
import org.millenaire.common.village.buildingmanagers.PanelManager;
import org.millenaire.common.village.buildingmanagers.ResManager;
import org.millenaire.common.village.buildingmanagers.VisitorManager;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class Building {
    public static final AStarConfig PATH_BUILDER_JPS_CONFIG = new AStarConfig(true, false, false, false, true);
    public static final int INVADER_SPAWNING_DELAY = 500;
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
    public static final String blTownhall = "townHall";
    private static final int LOCATION_SEARCH_DELAY = 80000;
    public static final int MIN_REPUTATION_FOR_TRADE = -1024;
    public static final int MAX_REPUTATION = 32768;
    public static final String versionCompatibility = "1.0";
    private boolean pathsChanged = false;
    private ItemStack bannerStack = ItemStack.field_190927_a;
    public String buildingGoal;
    public String buildingGoalIssue;
    public int buildingGoalLevel = 0;
    public BuildingLocation buildingGoalLocation = null;
    public int buildingGoalVariation = 0;
    public ConcurrentHashMap<BuildingProject.EnumProjects, CopyOnWriteArrayList<BuildingProject>> buildingProjects = new ConcurrentHashMap();
    public CopyOnWriteArrayList<Point> buildings = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<String> buildingsBought = new CopyOnWriteArrayList();
    public Culture culture;
    private boolean declaredPos = false;
    public HashMap<InvItem, Integer> exported = new HashMap();
    public HashMap<InvItem, Integer> imported = new HashMap();
    public boolean isActive = false;
    public boolean isAreaLoaded = false;
    public boolean chestLocked;
    public boolean isTownhall = false;
    public boolean isInn = false;
    public boolean isMarket = false;
    public boolean hasVisitors = false;
    public boolean hasAutoSpawn = false;
    private long lastFailedOtherLocationSearch = 0L;
    private long lastFailedProjectLocationSearch = 0L;
    public long lastPathingUpdate;
    private long lastSaved = 0L;
    public long lastVillagerRecordsRepair = 0L;
    public BuildingLocation location;
    public VillagerRecord merchantRecord = null;
    private String name = null;
    private String qualifier = "";
    public int nbNightsMerchant = 0;
    private HashMap<TradeGood, Integer> neededGoodsCached = new HashMap();
    private long neededGoodsLastGenerated = 0L;
    public boolean nightActionPerformed = false;
    public boolean noProjectsLeft = false;
    public RegionMapper regionMapper = null;
    public EntityPlayer closestPlayer = null;
    private Point pos = null;
    private boolean rebuildingRegionMapper = false;
    private boolean saveNeeded = false;
    private String saveReason = null;
    public MillVillager seller = null;
    public Point sellingPlace = null;
    private Point townHallPos = null;
    private Set<MillVillager> villagers = new LinkedHashSet<MillVillager>();
    public CopyOnWriteArrayList<String> visitorsList = new CopyOnWriteArrayList();
    private final Map<Long, VillagerRecord> vrecords = new HashMap<Long, VillagerRecord>();
    public VillageType villageType = null;
    private ConcurrentHashMap<Point, Integer> relations = new ConcurrentHashMap();
    public Point parentVillage = null;
    public VillageMapInfo winfo = new VillageMapInfo();
    public MillMapInfo mapInfo = null;
    public MillWorldData mw;
    public World world;
    private boolean nightBackgroundActionPerformed;
    private boolean updateRaidPerformed;
    public CopyOnWriteArrayList<String> raidsPerformed = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<String> raidsSuffered = new CopyOnWriteArrayList();
    public Point raidTarget;
    public long raidStart = 0L;
    public long raidPlanningStart;
    public boolean underAttack = false;
    private int nbAnimalsRespawned;
    public PujaSacrifice pujas = null;
    public UUID controlledBy = null;
    public String controlledByName = null;
    private SaveWorker saveWorker = null;
    private long lastGoodsRefresh = 0L;
    private boolean refreshGoodsNightActionPerformed;
    private BuildingChunkLoader chunkLoader = null;
    private final CopyOnWriteArrayList<ConstructionIP> constructionsIP = new CopyOnWriteArrayList();
    public List<List<BuildingBlock>> pathsToBuild = null;
    private PathCreatorQueue pathQueue = null;
    public int pathsToBuildIndex = 0;
    public int pathsToBuildPathIndex = 0;
    public List<Point> oldPathPointsToClear = null;
    public int oldPathPointsToClearIndex = 0;
    private boolean autobuildPaths = false;
    private final HashMap<String, LinkedHashMap<TradeGood, Integer>> shopBuys = new HashMap();
    private final HashMap<String, LinkedHashMap<TradeGood, Integer>> shopSells = new HashMap();
    private final ResManager resManager = new ResManager(this);
    public CopyOnWriteArrayList<Point> subBuildings = new CopyOnWriteArrayList();
    private Map<InvItem, Integer> inventoryCache = null;
    private MarvelManager marvelManager;
    private VisitorManager visitorManager = null;
    private PanelManager panelManager = null;
    private final CopyOnWriteArraySet<String> tags = new CopyOnWriteArraySet();
    public VillageType.BrickColourTheme brickColourTheme = null;

    public static void readBuildingPacket(MillWorldData mw, PacketBuffer ds) {
        Point pos = null;
        pos = StreamReadWrite.readNullablePoint(ds);
        Building building = mw.getBuilding(pos);
        boolean newbuilding = false;
        if (building == null) {
            building = new Building(mw);
            newbuilding = true;
        }
        building.pos = pos;
        try {
            building.isTownhall = ds.readBoolean();
            building.chestLocked = ds.readBoolean();
            building.controlledBy = StreamReadWrite.readNullableUUID(ds);
            building.controlledByName = StreamReadWrite.readNullableString(ds);
            building.townHallPos = StreamReadWrite.readNullablePoint(ds);
            String cultureKey = StreamReadWrite.readNullableString(ds);
            building.culture = Culture.getCultureByName(cultureKey);
            if (building.culture == null) {
                MillLog.error(building, "Received from the server a building of unknown culture: " + cultureKey);
            }
            String vtype = StreamReadWrite.readNullableString(ds);
            if (building.culture != null && building.culture.getVillageType(vtype) != null) {
                building.villageType = building.culture.getVillageType(vtype);
            } else if (building.culture != null && building.culture.getLoneBuildingType(vtype) != null) {
                building.villageType = building.culture.getLoneBuildingType(vtype);
            }
            building.location = StreamReadWrite.readNullableBuildingLocation(ds);
            building.addTags(StreamReadWrite.readStringCollection(ds), "reading tags client-side");
            building.buildingGoal = StreamReadWrite.readNullableString(ds);
            building.buildingGoalIssue = StreamReadWrite.readNullableString(ds);
            building.buildingGoalLevel = ds.readInt();
            building.buildingGoalVariation = ds.readInt();
            building.buildingGoalLocation = StreamReadWrite.readNullableBuildingLocation(ds);
            CopyOnWriteArrayList<Boolean> isCIPwall = StreamReadWrite.readBooleanList(ds);
            List<BuildingLocation> bls = StreamReadWrite.readBuildingLocationList(ds);
            building.getConstructionsInProgress().clear();
            int cip_id = 0;
            for (BuildingLocation bl : bls) {
                ConstructionIP cip = new ConstructionIP(building, cip_id, (Boolean)isCIPwall.get(cip_id));
                building.getConstructionsInProgress().add(cip);
                cip.setBuildingLocation(bl);
                ++cip_id;
            }
            building.buildingProjects = StreamReadWrite.readProjectListList(ds, building.culture);
            building.buildings = StreamReadWrite.readPointList(ds);
            building.buildingsBought = StreamReadWrite.readStringList(ds);
            building.relations = StreamReadWrite.readPointIntegerMap(ds);
            building.raidsPerformed = StreamReadWrite.readStringList(ds);
            building.raidsSuffered = StreamReadWrite.readStringList(ds);
            Map<Long, VillagerRecord> vrecords = StreamReadWrite.readVillagerRecordMap(mw, ds);
            for (VillagerRecord villagerRecord : vrecords.values()) {
                mw.registerVillagerRecord(villagerRecord, false);
            }
            building.pujas = StreamReadWrite.readOrUpdateNullablePuja(ds, building, building.pujas);
            building.visitorsList = StreamReadWrite.readStringList(ds);
            building.imported = StreamReadWrite.readInventory(ds);
            building.exported = StreamReadWrite.readInventory(ds);
            building.name = StreamReadWrite.readNullableString(ds);
            building.qualifier = StreamReadWrite.readNullableString(ds);
            building.raidTarget = StreamReadWrite.readNullablePoint(ds);
            building.raidPlanningStart = ds.readLong();
            building.raidStart = ds.readLong();
            building.resManager.readDataStream(ds);
            if (building.isTownhall && building.villageType.isMarvel()) {
                building.marvelManager = new MarvelManager(building);
                building.marvelManager.readDataStream(ds);
            }
        }
        catch (IOException e) {
            MillLog.printException(e);
        }
        if (newbuilding) {
            mw.addBuilding(building, pos);
        }
    }

    public static void readShopPacket(MillWorldData mw, PacketBuffer ds) {
        Point pos = null;
        pos = StreamReadWrite.readNullablePoint(ds);
        Building building = mw.getBuilding(pos);
        if (building == null) {
            MillLog.error(null, "Received shop packet for null building at: " + pos);
            return;
        }
        try {
            int nbBuys;
            int nbSells = ds.readInt();
            if (nbSells > 0) {
                LinkedHashMap<TradeGood, Integer> shopSellsPlayer = new LinkedHashMap<TradeGood, Integer>();
                for (int i = 0; i < nbSells; ++i) {
                    TradeGood g = StreamReadWrite.readNullableGoods(ds, building.culture);
                    shopSellsPlayer.put(g, ds.readInt());
                }
                building.shopSells.put(Mill.proxy.getSinglePlayerName(), shopSellsPlayer);
            }
            if ((nbBuys = ds.readInt()) > 0) {
                LinkedHashMap<TradeGood, Integer> shopBuysPlayer = new LinkedHashMap<TradeGood, Integer>();
                for (int i = 0; i < nbBuys; ++i) {
                    TradeGood g = StreamReadWrite.readNullableGoods(ds, building.culture);
                    shopBuysPlayer.put(g, ds.readInt());
                }
                building.shopBuys.put(Mill.proxy.getSinglePlayerName(), shopBuysPlayer);
            }
        }
        catch (MillLog.MillenaireException e) {
            MillLog.printException(e);
        }
    }

    private Building(MillWorldData mw) {
        this.mw = mw;
        this.world = mw.world;
    }

    public Building(MillWorldData mw, Culture c, VillageType villageType, BuildingLocation location, boolean townHall, boolean villageCreation, Point townHallPos) {
        MillLog.MillenaireException e;
        this.pos = location.chestPos;
        this.mw = mw;
        this.world = mw.world;
        this.location = location.clone();
        if (location.getPlan() != null) {
            this.addTags(location.getPlan().tags, "Adding plan tags");
        }
        this.culture = c;
        this.villageType = villageType;
        if (this.world == null) {
            e = new MillLog.MillenaireException("Null world!");
            MillLog.printException(e);
        }
        if (this.pos == null) {
            e = new MillLog.MillenaireException("Null pos!");
            MillLog.printException(e);
        }
        if (this.location == null) {
            e = new MillLog.MillenaireException("Null location!");
            MillLog.printException(e);
        }
        if (this.culture == null) {
            e = new MillLog.MillenaireException("Null culture!");
            MillLog.printException(e);
        }
        mw.addBuilding(this, location.chestPos);
        this.isTownhall = townHall;
        this.regionMapper = null;
        this.townHallPos = this.isTownhall ? this.getPos() : townHallPos;
        this.isTownhall = townHall;
        if (this.containsTags("inn") && !this.isTownhall) {
            this.isInn = true;
        }
        if (this.containsTags("market") && !this.isTownhall) {
            this.isMarket = true;
            this.hasVisitors = true;
        }
        if (this.containsTags("autospawnvillagers")) {
            this.hasAutoSpawn = true;
        }
        if (!location.getVisitors().isEmpty()) {
            this.hasVisitors = true;
        }
        if (this.containsTags("pujas")) {
            this.pujas = new PujaSacrifice(this, 0);
        }
        if (this.containsTags("sacrifices")) {
            this.pujas = new PujaSacrifice(this, 1);
        }
        if (this.isTownhall && villageType.isMarvel()) {
            this.marvelManager = new MarvelManager(this);
        }
    }

    public Building(MillWorldData mw, NBTTagCompound nbttagcompound) {
        this.mw = mw;
        this.world = mw.world;
        this.readFromNBT(nbttagcompound);
        if (this.pos == null) {
            MillLog.MillenaireException e = new MillLog.MillenaireException("Null pos!");
            MillLog.printException(e);
        }
        mw.addBuilding(this, this.pos);
    }

    public void addAdult(MillVillager child) throws MillLog.MillenaireException {
        MillVillager adult;
        String type = null;
        HashMap<String, Integer> villagerCount = new HashMap<String, Integer>();
        HashMap<String, Integer> residentCount = new HashMap<String, Integer>();
        List<String> residents = child.gender == 1 ? this.location.getMaleResidents() : this.location.getFemaleResidents();
        for (String string : residents) {
            if (residentCount.containsKey(string)) {
                residentCount.put(string, (Integer)residentCount.get(string) + 1);
                continue;
            }
            residentCount.put(string, 1);
        }
        for (VillagerRecord villagerRecord : this.getVillagerRecords().values()) {
            if (villagerCount.containsKey(villagerRecord.type)) {
                villagerCount.put(villagerRecord.type, (Integer)villagerCount.get(villagerRecord.type) + 1);
                continue;
            }
            villagerCount.put(villagerRecord.type, 1);
        }
        for (String string : residentCount.keySet()) {
            if (!villagerCount.containsKey(string)) {
                type = string;
                continue;
            }
            if ((Integer)villagerCount.get(string) >= (Integer)residentCount.get(string)) continue;
            type = string;
        }
        if (type == null) {
            MillLog.error(this, "Could not find a villager type to create. Gender: " + child.gender);
            MillLog.error(this, "Villager types: " + (child.gender == 1 ? MillCommonUtilities.flattenStrings(this.location.getMaleResidents()) : MillCommonUtilities.flattenStrings(this.location.getFemaleResidents())));
            String s = "";
            for (VillagerRecord vr : this.getVillagerRecords().values()) {
                s = s + vr.type + " ";
            }
            MillLog.error(this, "Current residents: " + s);
            return;
        }
        if (MillConfigValues.LogWorldGeneration >= 1) {
            MillLog.major(this, "Creating " + type + " with child " + child.func_70005_c_() + "/" + child.getVillagerId());
        }
        this.mw.removeVillagerRecord(child.getVillagerId());
        VillagerRecord adultRecord = VillagerRecord.createVillagerRecord(this.culture, type, this.mw, this.getPos(), this.getTownHallPos(), child.firstName, child.familyName, child.getVillagerId(), false);
        VillagerRecord villagerRecord = child.getRecord();
        if (villagerRecord != null) {
            adultRecord.rightHanded = villagerRecord.rightHanded;
        }
        if ((adult = MillVillager.createVillager(adultRecord, this.world, child.getPos(), false)) == null) {
            MillLog.error(this, "Couldn't create adult of type " + type + " from child " + child);
            return;
        }
        adultRecord.updateRecord(adult);
        for (VillagerRecord vr : this.getVillagerRecords().values()) {
            if (vr.gender == adult.gender) continue;
            if (adult.gender == 2) {
                adultRecord.maidenName = adultRecord.familyName;
                adultRecord.familyName = vr.familyName;
                adult.familyName = vr.familyName;
            }
            if (vr.gender == 2) {
                vr.maidenName = vr.familyName;
                vr.familyName = adult.familyName;
                MillVillager spouse = this.mw.getVillagerById(vr.getVillagerId());
                if (spouse != null) {
                    spouse.familyName = vr.familyName;
                }
            }
            adultRecord.spousesName = vr.getName();
            vr.spousesName = adult.func_70005_c_();
        }
        child.despawnVillager();
        this.world.func_72838_d((Entity)adult);
        if (this.isInn) {
            this.merchantCreated(adultRecord);
        } else {
            this.getPanelManager().updateSigns();
        }
    }

    public void addCustomBuilding(BuildingCustomPlan customBuilding, Point pos) throws MillLog.MillenaireException {
        BuildingLocation location = new BuildingLocation(customBuilding, pos, false);
        Building building = new Building(this.mw, this.culture, this.villageType, location, false, false, this.getPos());
        customBuilding.registerResources(building, location);
        building.initialise(null, false);
        this.registerBuildingEntity(building);
        BuildingProject project = new BuildingProject(customBuilding, location);
        if (!this.buildingProjects.containsKey((Object)BuildingProject.EnumProjects.CUSTOMBUILDINGS)) {
            this.buildingProjects.put(BuildingProject.EnumProjects.CUSTOMBUILDINGS, new CopyOnWriteArrayList());
        }
        this.buildingProjects.get((Object)BuildingProject.EnumProjects.CUSTOMBUILDINGS).add(project);
        if (MillConfigValues.LogBuildingPlan >= 1) {
            MillLog.major(this, "Created new Custom Building Entity: " + customBuilding.buildingKey + " at " + pos);
        }
    }

    public void addTags(Collection<String> newTags, String reason) {
        int nbTags = this.tags.size();
        ArrayList<String> addedTags = new ArrayList<String>();
        for (String tag : newTags) {
            if (this.tags.contains(tag)) continue;
            addedTags.add(tag);
            this.tags.add(tag);
        }
        if (MillConfigValues.LogTags >= 1 && addedTags.size() > 0 && !reason.contains("client-side")) {
            MillLog.major(this, "Added tags due to '" + reason + "': " + MillCommonUtilities.flattenStrings(addedTags) + ", went from " + nbTags + " to " + this.tags.size() + ". Current tags: " + MillCommonUtilities.flattenStrings(this.tags));
        }
    }

    public void addToExports(InvItem good, int quantity) {
        if (this.exported.containsKey(good)) {
            this.exported.put(good, this.exported.get(good) + quantity);
        } else {
            this.exported.put(good, quantity);
        }
    }

    public void addToImports(InvItem good, int quantity) {
        if (this.imported.containsKey(good)) {
            this.imported.put(good, this.imported.get(good) + quantity);
        } else {
            this.imported.put(good, quantity);
        }
    }

    public void adjustLanguage(EntityPlayer player, int l) {
        this.mw.getProfile(player).adjustLanguage(this.getTownHall().culture.key, l);
    }

    public void adjustRelation(Point villagePos, int change, boolean reset) {
        Building village;
        int relation = change;
        if (this.relations.containsKey(villagePos) && !reset) {
            relation += this.relations.get(villagePos).intValue();
        }
        if (relation > 100) {
            relation = 100;
        } else if (relation < -100) {
            relation = -100;
        }
        this.relations.put(villagePos, relation);
        this.saveNeeded = true;
        if (!this.isActive) {
            this.saveTownHall("distance relation change");
        }
        if ((village = this.mw.getBuilding(villagePos)) == null) {
            MillLog.error(this, "Could not find village at " + villagePos + " in order to adjust relation.");
        } else {
            village.relations.put(this.getPos(), relation);
            village.saveTownHall("distance relation change");
        }
    }

    public void adjustReputation(EntityPlayer player, int l) {
        this.mw.getProfile(player).adjustReputation(this.getTownHall(), l);
    }

    public void attemptMerchantMove(boolean forced) {
        ArrayList<Building> targets = new ArrayList<Building>();
        ArrayList<Building> backupTargets = new ArrayList<Building>();
        for (Point vp : this.getTownHall().relations.keySet()) {
            Building townHall = this.mw.getBuilding(vp);
            if (townHall == null || this.getTownHall() == null || townHall.villageType == this.getTownHall().villageType || this.getTownHall().relations.get(vp) < 0 && (this.getTownHall().relations.get(vp) < 0 || townHall.culture != this.culture) || !(this.getPos().distanceTo(townHall.getPos()) < 2000.0)) continue;
            if (MillConfigValues.LogMerchant >= 2) {
                MillLog.debug(this, "Considering village " + townHall.getVillageQualifiedName() + " for merchant : " + this.merchantRecord);
            }
            for (Building inn : townHall.getBuildingsWithTag("inn")) {
                boolean moveNeeded = false;
                HashMap<InvItem, Integer> content = this.resManager.getChestsContent();
                for (InvItem good : content.keySet()) {
                    if (content.get(good) <= 0 || inn.getTownHall().nbGoodNeeded(good.getItem(), good.meta) <= 0) continue;
                    moveNeeded = true;
                    break;
                }
                if (moveNeeded) {
                    if (inn.merchantRecord == null) {
                        targets.add(inn);
                        targets.add(inn);
                        targets.add(inn);
                    } else if (inn.nbNightsMerchant > 1 || forced) {
                        targets.add(inn);
                    }
                    if (MillConfigValues.LogMerchant < 2) continue;
                    MillLog.debug(this, "Found good move in " + townHall.getVillageQualifiedName() + " for merchant : " + this.merchantRecord);
                    continue;
                }
                if (this.nbNightsMerchant <= 3) continue;
                backupTargets.add(inn);
                if (MillConfigValues.LogMerchant < 2) continue;
                MillLog.debug(this, "Found backup move in " + townHall.getVillageQualifiedName() + " for merchant : " + this.merchantRecord);
            }
        }
        if (targets.size() == 0 && backupTargets.size() == 0) {
            if (MillConfigValues.LogMerchant >= 2) {
                MillLog.minor(this, "Failed to find a destination for merchant: " + this.merchantRecord);
            }
            return;
        }
        Building inn = targets.size() > 0 ? (Building)targets.get(MillCommonUtilities.randomInt(targets.size())) : (Building)backupTargets.get(MillCommonUtilities.randomInt(backupTargets.size()));
        if (inn.merchantRecord == null) {
            this.moveMerchant(inn);
        } else if (inn.nbNightsMerchant > 1 || forced) {
            this.swapMerchants(inn);
        }
    }

    private void attemptPlanNewRaid() {
        for (VillagerRecord vr : this.getVillagerRecords().values()) {
            if (!vr.raidingVillage) continue;
            return;
        }
        int raidingStrength = (int)((float)this.getVillageRaidingStrength() * 2.0f);
        if (MillConfigValues.LogDiplomacy >= 3) {
            MillLog.debug(this, "Checking out for new raid, strength: " + raidingStrength);
        }
        if (raidingStrength > 0) {
            ArrayList<Building> targets = new ArrayList<Building>();
            if (this.villageType.lonebuilding) {
                for (Building distantVillage : this.mw.allBuildings()) {
                    if (distantVillage == null || !distantVillage.isTownhall || distantVillage.villageType == null || distantVillage.villageType.lonebuilding || !(this.getPos().distanceTo(distantVillage.getPos()) < (double)MillConfigValues.BanditRaidRadius) || distantVillage.getVillageDefendingStrength() >= raidingStrength) continue;
                    if (MillConfigValues.LogDiplomacy >= 3) {
                        MillLog.debug(this, "Lone building valid target: " + distantVillage);
                    }
                    targets.add(distantVillage);
                }
            } else {
                for (Point p : this.relations.keySet()) {
                    Building distantVillage;
                    if (this.relations.get(p) >= -90 || (distantVillage = this.mw.getBuilding(p)) == null) continue;
                    if (MillConfigValues.LogDiplomacy >= 3) {
                        MillLog.debug(this, "Testing village valid target: " + distantVillage + "/" + distantVillage.getVillageDefendingStrength());
                    }
                    if (distantVillage.getVillageDefendingStrength() >= raidingStrength) continue;
                    if (MillConfigValues.LogDiplomacy >= 3) {
                        MillLog.debug(this, "Village valid target: " + distantVillage);
                    }
                    targets.add(distantVillage);
                }
            }
            if (!targets.isEmpty()) {
                Building target = (Building)targets.get(MillCommonUtilities.randomInt(targets.size()));
                if (this.isActive || target.isActive) {
                    this.planRaid(target);
                }
            }
        }
    }

    public List<TradeGood> calculateBuyingGoods(IInventory playerInventory) {
        BuildingPlan goalPlan;
        if (!this.culture.shopBuys.containsKey(this.location.shop) && !this.culture.shopBuysOptional.containsKey(this.location.shop)) {
            return null;
        }
        List<TradeGood> baseGoods = this.culture.shopBuys.get(this.location.shop);
        ArrayList<TradeGood> extraGoods = new ArrayList<TradeGood>();
        if (this.culture.shopBuysOptional.containsKey(this.location.shop)) {
            for (TradeGood g : this.culture.shopBuysOptional.get(this.location.shop)) {
                if (playerInventory != null && MillCommonUtilities.countChestItems(playerInventory, g.item.getItem(), g.item.meta) <= 0) continue;
                extraGoods.add(g);
            }
        }
        if (this.isTownhall && (goalPlan = this.getCurrentGoalBuildingPlan()) != null) {
            for (InvItem key : goalPlan.resCost.keySet()) {
                if (key.meta == -1) continue;
                boolean found = false;
                for (TradeGood tg : baseGoods) {
                    if (tg.item.getItem() != key.getItem() || tg.item.meta != key.meta) continue;
                    found = true;
                }
                if (found) continue;
                if (this.culture.getTradeGood(key) != null) {
                    extraGoods.add(this.culture.getTradeGood(key));
                    continue;
                }
                extraGoods.add(new TradeGood("generated", this.culture, key));
            }
        }
        if (extraGoods.size() == 0) {
            return baseGoods;
        }
        ArrayList<TradeGood> finalGoods = new ArrayList<TradeGood>();
        for (TradeGood good : baseGoods) {
            finalGoods.add(good);
        }
        for (TradeGood good : extraGoods) {
            finalGoods.add(good);
        }
        return finalGoods;
    }

    private void calculateInventoryCache() {
        InvItem invItem;
        ItemStack stack;
        this.inventoryCache = new HashMap<InvItem, Integer>();
        for (Point p : this.resManager.chests) {
            TileEntityLockedChest chest = p.getMillChest(this.world);
            if (chest == null) continue;
            for (int i = 0; i < chest.func_70302_i_(); ++i) {
                stack = chest.func_70301_a(i);
                if (stack.func_190926_b()) continue;
                invItem = InvItem.createInvItem(stack);
                if (this.inventoryCache.containsKey(invItem)) {
                    this.inventoryCache.put(invItem, this.inventoryCache.get(invItem) + stack.func_190916_E());
                    continue;
                }
                this.inventoryCache.put(invItem, stack.func_190916_E());
            }
        }
        for (Point p : this.resManager.furnaces) {
            ItemStack stack2;
            TileEntityFurnace furnace = p.getFurnace(this.world);
            if (furnace == null || (stack2 = furnace.func_70301_a(2)) == null || stack2.func_190926_b()) continue;
            InvItem invItem2 = InvItem.createInvItem(stack2);
            if (this.inventoryCache.containsKey(invItem2)) {
                this.inventoryCache.put(invItem2, this.inventoryCache.get(invItem2) + stack2.func_190916_E());
                continue;
            }
            this.inventoryCache.put(invItem2, stack2.func_190916_E());
        }
        for (Point p : this.resManager.firepits) {
            TileEntityFirePit firepit = p.getFirePit(this.world);
            if (firepit == null) continue;
            for (int slotPos = 0; slotPos < 3; ++slotPos) {
                stack = firepit.outputs.getStackInSlot(slotPos);
                if (stack == null || stack.func_190926_b()) continue;
                invItem = InvItem.createInvItem(stack);
                if (this.inventoryCache.containsKey(invItem)) {
                    this.inventoryCache.put(invItem, this.inventoryCache.get(invItem) + stack.func_190916_E());
                    continue;
                }
                this.inventoryCache.put(invItem, stack.func_190916_E());
            }
        }
    }

    public void calculatePathsToClear() {
        if (this.pathsToBuild != null) {
            List<List<BuildingBlock>> pathsToBuildLocal = this.pathsToBuild;
            long startTime = System.currentTimeMillis();
            ArrayList<Point> oldPathPointsToClearNew = new ArrayList<Point>();
            HashSet<Point> newPathPoints = new HashSet<Point>();
            for (List<BuildingBlock> path : pathsToBuildLocal) {
                for (BuildingBlock bp : path) {
                    newPathPoints.add(bp.p);
                }
            }
            int minX = Math.max(this.winfo.mapStartX, this.getPos().getiX() - this.villageType.radius);
            int maxX = Math.min(this.winfo.mapStartX + this.winfo.length - 1, this.getPos().getiX() + this.villageType.radius);
            int minZ = Math.max(this.winfo.mapStartZ, this.getPos().getiZ() - this.villageType.radius);
            int maxZ = Math.min(this.winfo.mapStartZ + this.winfo.width - 1, this.getPos().getiZ() + this.villageType.radius);
            for (int x = minX; x < maxX; ++x) {
                for (int z = minZ; z < maxZ; ++z) {
                    short basey = this.winfo.topGround[x - this.winfo.mapStartX][z - this.winfo.mapStartZ];
                    for (int dy = -2; dy < 4; ++dy) {
                        Point p;
                        int y = dy + basey;
                        IBlockState blockState = WorldUtilities.getBlockState(this.world, x, y, z);
                        if (!BlockItemUtilities.isPath(blockState.func_177230_c()) || ((Boolean)blockState.func_177229_b((IProperty)IBlockPath.STABLE)).booleanValue() || newPathPoints.contains(p = new Point(x, y, z))) continue;
                        oldPathPointsToClearNew.add(p);
                    }
                }
            }
            this.oldPathPointsToClearIndex = 0;
            this.oldPathPointsToClear = oldPathPointsToClearNew;
            if (MillConfigValues.LogVillagePaths >= 2) {
                MillLog.minor(this, "Finished looking for paths to clear. Found: " + this.oldPathPointsToClear.size() + ". Duration: " + (System.currentTimeMillis() - startTime) + " ms.");
            }
        }
    }

    public List<TradeGood> calculateSellingGoods(IInventory playerInventory) {
        if (!this.culture.shopSells.containsKey(this.location.shop)) {
            return null;
        }
        return this.culture.shopSells.get(this.location.shop);
    }

    public void callForHelp(EntityLivingBase attacker) {
        if (MillConfigValues.LogGeneralAI >= 3) {
            MillLog.debug(this, "Calling for help among: " + this.getKnownVillagers().size() + " villagers.");
        }
        for (MillVillager villager : this.getKnownVillagers()) {
            if (MillConfigValues.LogGeneralAI >= 3) {
                MillLog.debug(villager, "Testing villager. Will fight? " + villager.helpsInAttacks() + ". Current target? " + villager.func_70638_az() + ". Distance to threat: " + villager.getPos().distanceTo((Entity)attacker));
            }
            if (villager.func_70638_az() != null || !villager.helpsInAttacks() || villager.isRaider || !(villager.getPos().distanceTo((Entity)attacker) < 80.0)) continue;
            if (MillConfigValues.LogGeneralAI >= 1) {
                MillLog.major(villager, "Off to help a friend attacked by attacking: " + attacker);
            }
            villager.func_70624_b(attacker);
            villager.clearGoal();
            villager.speakSentence("calltoarms", 0, 50, 1);
        }
    }

    private boolean canAffordBuild(BuildingPlan plan) {
        if (plan == null) {
            MillLog.error(this, "Needed to compute plan cost but the plan is null.");
            return false;
        }
        if (plan.resCost == null) {
            MillLog.error(this, "Needed to compute plan cost but the plan cost map is null.");
            return false;
        }
        for (InvItem key : plan.resCost.keySet()) {
            if (plan.resCost.get(key) <= this.nbGoodAvailable(key, true, false, false)) continue;
            return false;
        }
        return true;
    }

    private boolean canAffordProject(BuildingPlan plan) {
        if (plan == null) {
            MillLog.error(this, "Needed to compute plan cost but the plan is null.");
            return false;
        }
        if (plan.resCost == null) {
            MillLog.error(this, "Needed to compute plan cost but the plan cost map is null.");
            return false;
        }
        for (InvItem key : plan.resCost.keySet()) {
            if (plan.resCost.get(key) <= this.countGoods(key)) continue;
            return false;
        }
        return true;
    }

    public void cancelBuilding(BuildingLocation location) {
        ConstructionIP cip = this.findConstructionIPforLocation(location);
        if (cip != null && location.isLocationSamePlace(cip.getBuildingLocation())) {
            cip.setBuildingLocation(null);
        }
        if (location.isLocationSamePlace(this.buildingGoalLocation)) {
            this.buildingGoalLocation = null;
            this.buildingGoal = null;
        }
        block0: for (List list : this.buildingProjects.values()) {
            for (BuildingProject project : list) {
                if (project.location != location) continue;
                list.remove(project);
                continue block0;
            }
        }
        Building building = location.getBuilding(this.world);
        if (building != null) {
            ArrayList<MillVillager> arrayList = new ArrayList<MillVillager>(building.villagers);
            for (MillVillager villager : arrayList) {
                villager.despawnVillagerSilent();
            }
            ArrayList<VillagerRecord> vrecordsCopy = new ArrayList<VillagerRecord>(building.getAllVillagerRecords());
            for (VillagerRecord vr : vrecordsCopy) {
                this.mw.removeVillagerRecord(vr.getVillagerId());
            }
        }
        this.buildings.remove(location.pos);
        this.winfo.removeBuildingLocation(location);
        this.mw.removeBuilding(location.pos);
    }

    public void cancelRaid() {
        if (MillConfigValues.LogDiplomacy >= 1) {
            MillLog.major(this, "Cancelling raid");
        }
        this.raidPlanningStart = 0L;
        this.raidStart = 0L;
        this.raidTarget = null;
    }

    public boolean canChildMoveIn(int pGender, String familyName) {
        if (pGender == 2 && this.location.getFemaleResidents().size() == 0) {
            return false;
        }
        if (pGender == 1 && this.location.getMaleResidents().size() == 0) {
            return false;
        }
        for (VillagerRecord vr : this.getVillagerRecords().values()) {
            if (vr.gender == pGender || vr.getType().isChild || !vr.familyName.equals(familyName) || !this.equals(vr.getHouse())) continue;
            return false;
        }
        int nbAdultSameSex = 0;
        for (VillagerRecord vr : this.getVillagerRecords().values()) {
            if (vr.gender != pGender || vr.getType() == null || vr.getType().isChild || !this.equals(vr.getHouse())) continue;
            ++nbAdultSameSex;
        }
        if (pGender == 1 && nbAdultSameSex >= this.location.getMaleResidents().size()) {
            return false;
        }
        return pGender != 2 || nbAdultSameSex < this.location.getFemaleResidents().size();
    }

    public void changeVillageName(String s) {
        int i;
        this.name = s;
        for (i = 0; i < this.mw.villagesList.pos.size(); ++i) {
            if (!this.mw.villagesList.pos.get(i).equals(this.getPos())) continue;
            this.mw.villagesList.names.set(i, this.getVillageQualifiedName());
        }
        for (i = 0; i < this.mw.loneBuildingsList.pos.size(); ++i) {
            if (!this.mw.loneBuildingsList.pos.get(i).equals(this.getPos())) continue;
            this.mw.loneBuildingsList.names.set(i, this.getVillageQualifiedName());
        }
    }

    public void changeVillageQualifier(String s) {
        int i;
        this.qualifier = s;
        for (i = 0; i < this.mw.villagesList.pos.size(); ++i) {
            if (!this.mw.villagesList.pos.get(i).equals(this.getPos())) continue;
            this.mw.villagesList.names.set(i, this.getVillageQualifiedName());
        }
        for (i = 0; i < this.mw.loneBuildingsList.pos.size(); ++i) {
            if (!this.mw.loneBuildingsList.pos.get(i).equals(this.getPos())) continue;
            this.mw.loneBuildingsList.names.set(i, this.getVillageQualifiedName());
        }
    }

    public void checkBattleStatus() {
        int nbAttackers = 0;
        int nbLiveAttackers = 0;
        int nbLiveDefenders = 0;
        Point attackingVillagePos = null;
        for (VillagerRecord villagerRecord : this.getVillagerRecords().values()) {
            if (villagerRecord.raidingVillage) {
                ++nbAttackers;
                if (!villagerRecord.killed) {
                    ++nbLiveAttackers;
                }
                attackingVillagePos = villagerRecord.originalVillagePos;
                continue;
            }
            if (!villagerRecord.getType().helpInAttacks || villagerRecord.killed || villagerRecord.awayraiding || villagerRecord.awayhired) continue;
            ++nbLiveDefenders;
        }
        if (this.isTownhall) {
            if (this.chestLocked && nbLiveDefenders == 0) {
                this.unlockAllChests();
                ServerSender.sendTranslatedSentenceInRange(this.world, this.getPos(), MillConfigValues.BackgroundRadius, '4', "ui.allchestsunlocked", this.getVillageQualifiedName());
            } else if (!this.chestLocked && nbLiveDefenders > 0) {
                this.lockAllBuildingsChests();
            }
        }
        if (nbAttackers > 0) {
            this.underAttack = true;
            if (nbLiveAttackers == 0 || nbLiveDefenders == 0) {
                boolean finish = false;
                if (nbLiveAttackers > 0) {
                    for (MillVillager v : this.getKnownVillagers()) {
                        if (finish || !v.isRaider || !(this.resManager.getDefendingPos().distanceToSquared((Entity)v) < 25.0)) continue;
                        finish = true;
                    }
                } else {
                    finish = true;
                }
                if (finish) {
                    if (attackingVillagePos == null) {
                        MillLog.error(this, "Wanted to end raid but can't find originating village's position.");
                        this.clearAllAttackers();
                    } else {
                        Building building = this.mw.getBuilding(attackingVillagePos);
                        if (building == null) {
                            this.clearAllAttackers();
                        } else {
                            boolean endedProperly = building.endRaid();
                            if (!endedProperly) {
                                this.clearAllAttackers();
                            }
                        }
                    }
                }
            }
        } else {
            this.underAttack = false;
        }
    }

    private void checkExploreTag(EntityPlayer player) {
        if (player != null && this.location.getPlan() != null && !this.mw.getProfile(player).isTagSet(this.location.getPlan().exploreTag) && this.resManager.getSleepingPos().distanceToSquared((Entity)player) < 16.0) {
            boolean valid = true;
            int x = this.resManager.getSleepingPos().getiX();
            int z = this.resManager.getSleepingPos().getiZ();
            while (valid && (x != (int)player.field_70165_t || z != (int)player.field_70161_v)) {
                Block block = WorldUtilities.getBlock(this.world, x, this.resManager.getSleepingPos().getiY() + 1, z);
                if (block != Blocks.field_150350_a && block.func_176223_P().func_185904_a().func_76230_c()) {
                    valid = false;
                    continue;
                }
                if (x > (int)player.field_70165_t) {
                    --x;
                    continue;
                }
                if (x < (int)player.field_70165_t) {
                    ++x;
                    continue;
                }
                if (z > (int)player.field_70161_v) {
                    --z;
                    continue;
                }
                if (z >= (int)player.field_70161_v) continue;
                ++z;
            }
            if (valid) {
                this.mw.getProfile(player).setTag(this.location.getPlan().exploreTag);
                ServerSender.sendTranslatedSentence(player, '2', "other.exploredbuilding", this.location.getPlan().nativeName);
            }
        }
    }

    private boolean checkProjectValidity(BuildingProject project, BuildingPlan plan) {
        if (plan.requiredGlobalTag != null && !this.mw.isGlobalTagSet(plan.requiredGlobalTag)) {
            return false;
        }
        if (!plan.requiredTags.isEmpty()) {
            if (project.location == null) {
                MillLog.error(this, "Plan " + plan + " has required tags but no location.");
                return false;
            }
            Building building = this.getBuildingFromLocation(project.location);
            if (building == null) {
                MillLog.error(this, "Plan " + plan + " has required tags but building can't be found.");
                return false;
            }
            for (String tag : plan.requiredTags) {
                if (building.containsTags(tag)) continue;
                if (MillConfigValues.LogTags >= 2) {
                    MillLog.minor(this, "Can't build plan " + plan + " as building is missing tag:" + tag);
                }
                return false;
            }
        }
        if (!plan.forbiddenTagsInVillage.isEmpty()) {
            for (String forbiddenTag : plan.forbiddenTagsInVillage) {
                Building matchingBuilding = this.getFirstBuildingWithTag(forbiddenTag);
                if (matchingBuilding == null) continue;
                if (MillConfigValues.LogTags >= 2) {
                    MillLog.minor(this, "Can't build plan " + plan + " as building " + matchingBuilding + " has tag " + forbiddenTag);
                }
                return false;
            }
        }
        if (plan.level > 0 && plan.containsTags("no_upgrade_till_wall_initialized")) {
            for (BuildingProject existingProject : this.getFlatProjectList()) {
                BuildingPlan existingProjectPlan = existingProject.getNextBuildingPlan(false);
                if (existingProjectPlan == null || !existingProjectPlan.isWallSegment || existingProjectPlan.level != 0) continue;
                if (MillConfigValues.LogTags >= 2) {
                    MillLog.minor(this, "Can't build plan " + plan + " as it requires all wall segments to be initialized.");
                }
                return false;
            }
        }
        for (String tag : plan.requiredVillageTags) {
            if (this.containsTags(tag)) continue;
            if (MillConfigValues.LogTags >= 2) {
                MillLog.minor(this, "Can't build plan " + plan + " as village is missing tag:" + tag);
            }
            return false;
        }
        if (project.location != null && !plan.requiredParentTags.isEmpty()) {
            Building parent = null;
            for (BuildingLocation alocation : this.getLocations()) {
                if (alocation.isSubBuildingLocation || !alocation.pos.equals(project.location.pos)) continue;
                parent = alocation.getBuilding(this.world);
            }
            if (parent == null) {
                MillLog.error(this, "Building plan " + plan + " has required parent tags but the parent for location " + project.location + " cannot be found.");
                return false;
            }
            for (String tag : plan.requiredParentTags) {
                if (parent.containsTags(tag)) continue;
                if (MillConfigValues.LogTags >= 2) {
                    MillLog.temp(this, "Can't build plan " + plan + " as parent is missing tag: " + tag);
                }
                return false;
            }
        }
        return true;
    }

    public void checkSeller() {
        if (!this.world.func_72935_r() || this.underAttack) {
            if (MillConfigValues.LogSelling >= 2) {
                MillLog.major(this, "Not sending seller because either: !world.isDaytime(): " + !this.world.func_72935_r() + " or underAttack: " + this.underAttack);
            }
            return;
        }
        if (this.closestPlayer == null || this.controlledBy(this.closestPlayer)) {
            if (this.closestPlayer == null && MillConfigValues.LogSelling >= 3) {
                MillLog.debug(this, "Not sending seller because there are no nearby player.");
            } else if (this.closestPlayer != null && this.controlledBy(this.closestPlayer) && MillConfigValues.LogSelling >= 2) {
                MillLog.minor(this, "Not sending seller because the nearby player owns the village.");
            }
            return;
        }
        if (!this.chestLocked && MillConfigValues.LogSelling >= 2) {
            MillLog.minor(this, "Not sending seller because village chests are not locked.");
        }
        if (this.getReputation(this.closestPlayer) < -1024 && MillConfigValues.LogSelling >= 2) {
            MillLog.minor(this, "Not sending seller because player's reputation is not sufficient: " + this.getReputation(this.closestPlayer));
        }
        if (this.closestPlayer != null && this.seller == null && this.getReputation(this.closestPlayer) >= -1024 && this.chestLocked) {
            this.sellingPlace = null;
            if (MillConfigValues.LogSelling >= 2) {
                MillLog.minor(this, "A seller is required for " + this.closestPlayer.func_70005_c_());
            }
            for (BuildingLocation l : this.getLocations()) {
                if (l.level < 0 || l.chestPos == null || l.shop == null || l.shop.length() <= 0) continue;
                if (l.getSellingPos() != null && l.getSellingPos().distanceTo((Entity)this.closestPlayer) < 3.0) {
                    this.sellingPlace = l.getSellingPos();
                    continue;
                }
                if (l.getSellingPos() != null || !(l.sleepingPos.distanceTo((Entity)this.closestPlayer) < 3.0)) continue;
                this.sellingPlace = l.sleepingPos;
            }
            if (this.sellingPlace == null && MillConfigValues.LogSelling >= 2) {
                MillLog.minor(this, "Can't send player because there is no selling place.");
            }
            if (this.sellingPlace != null) {
                if (MillConfigValues.LogSelling >= 2) {
                    MillLog.minor(this, "Checking through villagers to find a seller.");
                }
                for (MillVillager villager : this.getKnownVillagers()) {
                    if (!villager.isSeller() || this.getConstructionIPforBuilder(villager) != null || this.seller != null && !(this.sellingPlace.distanceToSquared((Entity)villager) < this.sellingPlace.distanceToSquared((Entity)this.seller))) continue;
                    this.seller = villager;
                }
                if (this.seller != null) {
                    this.seller.clearGoal();
                    this.seller.goalKey = Goal.beSeller.key;
                    Goal.beSeller.onAccept(this.seller);
                    if (MillConfigValues.LogSelling >= 3) {
                        MillLog.debug(this, "Sending seller: " + this.seller);
                    }
                }
            }
        }
    }

    public void checkWorkers() {
        if (this.seller != null && !Goal.beSeller.key.equals(this.seller.goalKey)) {
            this.seller = null;
        }
        for (ConstructionIP cip : this.getConstructionsInProgress()) {
            if (cip.getBuilder() == null || (cip.getBuilder().goalKey == null || Goal.getResourcesForBuild.key.equals(cip.getBuilder().goalKey) || Goal.construction.key.equals(cip.getBuilder().goalKey)) && cip.getId() == cip.getBuilder().constructionJobId) continue;
            if (MillConfigValues.LogBuildingPlan >= 1) {
                MillLog.major(this, cip.getBuilder().func_70005_c_() + " is no longer building.");
            }
            cip.setBuilder(null);
        }
    }

    public void choseAndApplyBrickTheme() {
        if (this.villageType.brickColourThemes.isEmpty()) {
            return;
        }
        this.brickColourTheme = (VillageType.BrickColourTheme)MillCommonUtilities.getWeightedChoice(this.villageType.brickColourThemes, null);
        for (Building b : this.getBuildings()) {
            if (!b.location.getPlan().randomBrickColours.isEmpty()) continue;
            b.location.initialiseBrickColoursFromTheme(this, this.brickColourTheme);
            for (int x = b.location.minx; x <= b.location.maxx; ++x) {
                for (int z = b.location.minz; z <= b.location.maxz; ++z) {
                    for (int y = b.location.miny - 20; y <= b.location.maxy + 20; ++y) {
                        EnumDyeColor currentColor;
                        BlockPos bp = new BlockPos(x, y, z);
                        IBlockState bs = this.world.func_180495_p(bp);
                        if (!(bs.func_177230_c() instanceof IPaintedBlock) || !b.location.paintedBricksColour.containsKey(currentColor = BlockPaintedBricks.getColourFromBlockState(bs))) continue;
                        this.world.func_175656_a(bp, BlockPaintedBricks.getBlockStateWithColour(bs, b.location.paintedBricksColour.get(currentColor)));
                    }
                }
            }
        }
    }

    private void clearAllAttackers() {
        int nbCleared = 0;
        ArrayList<VillagerRecord> villagerRecordsTemp = new ArrayList<VillagerRecord>(this.getAllVillagerRecords());
        for (VillagerRecord vr : villagerRecordsTemp) {
            if (!vr.raidingVillage) continue;
            this.mw.removeVillagerRecord(vr.getVillagerId());
            ++nbCleared;
        }
        if (MillConfigValues.LogDiplomacy >= 1) {
            MillLog.major(this, "Cleared " + nbCleared + " attackers.");
        }
        HashSet<MillVillager> villagersTemp = new HashSet<MillVillager>(this.getKnownVillagers());
        for (MillVillager villager : villagersTemp) {
            if (!villager.isRaider) continue;
            villager.despawnVillagerSilent();
            if (MillConfigValues.LogDiplomacy < 1) continue;
            MillLog.major(this, "Despawning invader: " + villager);
        }
    }

    public void clearOldPaths() {
        if (this.oldPathPointsToClear != null) {
            for (Point p : this.oldPathPointsToClear) {
                PathUtilities.clearPathBlock(p, this.world);
            }
            this.oldPathPointsToClear = null;
            this.pathsChanged = true;
            this.requestSave("paths clearing rushed");
        }
    }

    public void clearTags(Collection<String> tagsToClear, String reason) {
        int nbTags = this.tags.size();
        ArrayList<String> clearedTags = new ArrayList<String>();
        for (String tag : tagsToClear) {
            if (!this.tags.contains(tag)) continue;
            clearedTags.add(tag);
            this.tags.remove(tag);
        }
        if (MillConfigValues.LogTags >= 1 && clearedTags.size() > 0 && !reason.contains("client-side")) {
            MillLog.major(this, "Cleared tags due to '" + reason + "': " + MillCommonUtilities.flattenStrings(clearedTags) + ", went from " + nbTags + " to " + this.tags.size() + ". Current tags: " + MillCommonUtilities.flattenStrings(this.tags));
        }
    }

    private void completeConstruction(ConstructionIP cip) throws MillLog.MillenaireException {
        if (cip.getBuildingLocation() != null && cip.getBblocks() == null) {
            BuildingPlan plan = this.getBuildingPlanForConstruction(cip);
            this.registerBuildingLocation(cip.getBuildingLocation());
            this.updateWorldInfo();
            if (cip.getBuildingLocation() != null && cip.getBuildingLocation().isSameLocation(this.buildingGoalLocation)) {
                this.buildingGoalLocation = null;
                this.buildingGoal = null;
                this.buildingGoalIssue = null;
                this.buildingGoalLevel = -1;
            }
            cip.setBuilder(null);
            cip.setBuildingLocation(null);
            if (plan.rebuildPath || plan.level == 0 || plan.getPreviousBuildingPlan().pathLevel != plan.pathLevel) {
                this.recalculatePaths(false);
            }
        }
    }

    public int computeCurrentWallLevel() {
        int wallLevel = Integer.MAX_VALUE;
        boolean wallFound = false;
        List<BuildingProject> projects = this.getFlatProjectList();
        for (BuildingProject project : projects) {
            BuildingPlan initialPlan = project.getPlan(0, 0);
            BuildingPlan plan = project.getNextBuildingPlan(false);
            if (plan != null && plan.isWallSegment) {
                wallFound = true;
                if (!this.isValidProject(project)) continue;
                for (int i = 0; i < 10; ++i) {
                    if (i >= wallLevel || !plan.containsTags("wall_level_" + i)) continue;
                    wallLevel = i;
                }
                continue;
            }
            if (initialPlan == null || !initialPlan.isWallSegment) continue;
            wallFound = true;
        }
        if (!wallFound) {
            return -1;
        }
        return wallLevel;
    }

    public void computeShopGoods(EntityPlayer player) {
        List<TradeGood> buyingGoods;
        List<TradeGood> sellingGoods = this.calculateSellingGoods((IInventory)player.field_71071_by);
        if (sellingGoods != null) {
            LinkedHashMap<TradeGood, Integer> shopSellsPlayer = new LinkedHashMap<TradeGood, Integer>();
            for (TradeGood g : sellingGoods) {
                if (g.getBasicSellingPrice(this) <= 0) continue;
                shopSellsPlayer.put(g, g.getBasicSellingPrice(this));
            }
            this.shopSells.put(player.func_70005_c_(), shopSellsPlayer);
        }
        if ((buyingGoods = this.calculateBuyingGoods((IInventory)player.field_71071_by)) != null) {
            LinkedHashMap<TradeGood, Integer> shopBuysPlayer = new LinkedHashMap<TradeGood, Integer>();
            for (TradeGood g : buyingGoods) {
                if (g.getBasicBuyingPrice(this) <= 0) continue;
                shopBuysPlayer.put(g, g.getBasicBuyingPrice(this));
            }
            this.shopBuys.put(player.func_70005_c_(), shopBuysPlayer);
        }
    }

    public void constructCalculatedPaths() {
        if (this.pathsToBuild != null) {
            if (MillConfigValues.LogVillagePaths >= 2) {
                MillLog.minor(this, "Rebuilding calculated paths.");
            }
            for (List<BuildingBlock> path : this.pathsToBuild) {
                if (path.isEmpty()) continue;
                for (BuildingBlock bp : path) {
                    bp.pathBuild(this);
                }
            }
            this.pathsToBuild = null;
            this.pathsChanged = true;
            this.requestSave("paths rushed");
        }
    }

    public boolean containsTags(String tag) {
        return this.tags.contains(tag.toLowerCase());
    }

    public boolean controlledBy(EntityPlayer player) {
        if (!this.isTownhall && this.getTownHall() != null) {
            if (this.getTownHall() == this) {
                MillLog.error(this, "isTownHall is false but building is its own Town Hall.");
                return false;
            }
            return this.getTownHall().controlledBy(player);
        }
        return this.controlledBy != null && this.controlledBy.equals(this.mw.getProfile((EntityPlayer)player).uuid);
    }

    public int countChildren() {
        int nb = 0;
        for (VillagerRecord vr : this.getVillagerRecords().values()) {
            if (vr.getType() == null || !vr.getType().isChild) continue;
            ++nb;
        }
        return nb;
    }

    public int countGoods(Block block, int meta) {
        return this.countGoods(Item.func_150898_a((Block)block), meta);
    }

    public int countGoods(IBlockState blockState) {
        return this.countGoods(Item.func_150898_a((Block)blockState.func_177230_c()), blockState.func_177230_c().func_176201_c(blockState));
    }

    public int countGoods(InvItem iv) {
        return this.countGoods(iv.getItem(), iv.meta);
    }

    public int countGoods(Item item) {
        return this.countGoods(item, 0);
    }

    public int countGoods(Item item, int meta) {
        return this.getInventoryCountFromCache(InvItem.createInvItem(item, meta));
    }

    public int countGoodsOld(Item item, int meta) {
        int count = 0;
        for (Point p : this.resManager.chests) {
            TileEntityLockedChest chest = p.getMillChest(this.world);
            count += MillCommonUtilities.countChestItems((IInventory)chest, item, meta);
        }
        return count;
    }

    public int countVillageGoods(InvItem iv) {
        int count = 0;
        for (Building b : this.getBuildings()) {
            count += b.countGoods(iv.getItem(), iv.meta);
        }
        return count;
    }

    public MillVillager createChild(MillVillager mother, Building townHall, String fathersName) {
        try {
            int gender;
            String type;
            VillagerRecord vr;
            MillVillager child;
            if (MillConfigValues.LogWorldGeneration >= 2) {
                MillLog.minor(this, "Creating child: " + mother.familyName);
            }
            if ((child = MillVillager.createVillager(vr = VillagerRecord.createVillagerRecord(townHall.culture, type = (gender = this.getNewGender()) == 1 ? mother.getMaleChild() : mother.getFemaleChild(), this.mw, this.getPos(), this.getTownHallPos(), null, mother.familyName, -1L, false), this.world, this.resManager.getSleepingPos(), false)) == null) {
                throw new MillLog.MillenaireException("Child not instancied in createVillager");
            }
            vr.fathersName = fathersName;
            vr.mothersName = mother.func_70005_c_();
            this.world.func_72838_d((Entity)child);
            return child;
        }
        catch (Exception e) {
            Mill.proxy.sendChatAdmin("Error in createChild(). Check millenaire.log.");
            MillLog.error(this, "Exception in createChild.onUpdate(): ");
            MillLog.printException(e);
            return null;
        }
    }

    public MillVillager createNewVillager(String type) throws MillLog.MillenaireException {
        VillagerRecord vr = VillagerRecord.createVillagerRecord(this.culture, type, this.mw, this.getPos(), this.getTownHallPos(), null, null, -1L, false);
        MillVillager villager = MillVillager.createVillager(vr, this.world, this.resManager.getSleepingPos(), false);
        this.world.func_72838_d((Entity)villager);
        if (villager.vtype.isChild) {
            vr.size = 20;
            villager.growSize();
        }
        return villager;
    }

    public String createResidents() throws MillLog.MillenaireException {
        int startPos;
        int i;
        if (this.location.getMaleResidents().size() + this.location.getFemaleResidents().size() == 0) {
            return null;
        }
        String familyName = null;
        String husbandType = null;
        if (this.location.getMaleResidents().size() > 0 && !this.culture.getVillagerType((String)this.location.getMaleResidents().get((int)0)).isChild) {
            husbandType = this.location.getMaleResidents().get(0);
        }
        String wifeType = null;
        if (this.location.getFemaleResidents().size() > 0 && !this.culture.getVillagerType((String)this.location.getFemaleResidents().get((int)0)).isChild) {
            wifeType = this.location.getFemaleResidents().get(0);
        }
        if (MillConfigValues.LogMerchant >= 2) {
            MillLog.minor(this, "Creating " + husbandType + " and " + wifeType + ": " + familyName);
        }
        VillagerRecord husbandRecord = null;
        VillagerRecord wifeRecord = null;
        if (this.resManager.getSleepingPos() == null) {
            MillLog.error(this, "Wanted to create villagers but sleepingPos is null!");
            return "";
        }
        if (husbandType != null) {
            husbandRecord = VillagerRecord.createVillagerRecord(this.culture, husbandType, this.mw, this.getPos(), this.getTownHallPos(), null, null, -1L, false);
            MillVillager husband = MillVillager.createVillager(husbandRecord, this.world, this.resManager.getSleepingPos(), false);
            familyName = husband.familyName;
            this.world.func_72838_d((Entity)husband);
        }
        if (wifeType != null) {
            wifeRecord = VillagerRecord.createVillagerRecord(this.culture, wifeType, this.mw, this.getPos(), this.getTownHallPos(), null, familyName, -1L, false);
            MillVillager wife = MillVillager.createVillager(wifeRecord, this.world, this.resManager.getSleepingPos(), false);
            wifeRecord = new VillagerRecord(this.mw, wife);
            this.world.func_72838_d((Entity)wife);
        }
        if (MillConfigValues.LogWorldGeneration >= 1) {
            MillLog.major(this, "Records: " + wifeRecord + "/" + husbandRecord);
        }
        if (wifeRecord != null && husbandRecord != null) {
            wifeRecord.spousesName = husbandRecord.getName();
            husbandRecord.spousesName = wifeRecord.getName();
        }
        for (i = startPos = husbandType == null ? 0 : 1; i < this.location.getMaleResidents().size(); ++i) {
            this.createNewVillager(this.location.getMaleResidents().get(i));
        }
        for (i = startPos = wifeType == null ? 0 : 1; i < this.location.getFemaleResidents().size(); ++i) {
            this.createNewVillager(this.location.getFemaleResidents().get(i));
        }
        if (this.isInn) {
            this.merchantCreated(husbandRecord);
        } else {
            this.getPanelManager().updateSigns();
        }
        return familyName;
    }

    public void destroyVillage() {
        File file1;
        File buildingsDir;
        if (MillConfigValues.LogVillage >= 1) {
            MillLog.major(this, "Destroying the village!");
        }
        for (Point point : this.resManager.chests) {
            TileEntityLockedChest chest = point.getMillChest(this.world);
            if (chest == null) continue;
            chest.buildingPos = null;
        }
        for (Point point : this.buildings) {
            Building building = this.mw.getBuilding(point);
            if (building == null) continue;
            for (Point p2 : this.resManager.chests) {
                TileEntityLockedChest chest = p2.getMillChest(this.world);
                if (chest == null) continue;
                chest.buildingPos = null;
            }
        }
        ArrayList<MillVillager> villagersToDestroy = new ArrayList<MillVillager>(this.getKnownVillagers());
        for (MillVillager villager : villagersToDestroy) {
            villager.despawnVillager();
        }
        for (Point p : this.buildings) {
            this.mw.removeBuilding(p);
        }
        this.mw.removeVillageOrLoneBuilding(this.getPos());
        File file = this.mw.millenaireDir;
        if (!file.exists()) {
            file.mkdir();
        }
        if (!(buildingsDir = new File(file, "buildings")).exists()) {
            buildingsDir.mkdir();
        }
        if ((file1 = new File(buildingsDir, this.getPos().getPathString() + ".gz")).exists()) {
            file1.renameTo(new File(file, this.getPos().getPathString() + "ToDelete"));
            file1.delete();
        }
    }

    /*
     * WARNING - void declaration
     */
    public void displayInfos(EntityPlayer player) {
        if (this.location == null) {
            return;
        }
        int nbAdults = 0;
        int nbGrownChild = 0;
        for (MillVillager millVillager : this.getKnownVillagers()) {
            if (!millVillager.func_70631_g_()) {
                ++nbAdults;
                continue;
            }
            if (millVillager.getSize() != 20) continue;
            ++nbGrownChild;
        }
        ServerSender.sendChat(player, TextFormatting.GREEN, "It has " + this.getKnownVillagers().size() + " villagers registered. (" + nbAdults + " adults, " + nbGrownChild + " grown children)");
        ServerSender.sendChat(player, TextFormatting.GREEN, "Pos: " + this.getPos() + " sell pos:" + this.resManager.getSellingPos());
        if (this.isTownhall) {
            ServerSender.sendChat(player, TextFormatting.GREEN, "It has " + this.buildings.size() + " houses registered.");
            ServerSender.sendChat(player, TextFormatting.GREEN, "Connections build: " + (this.regionMapper != null));
            ServerSender.sendChat(player, TextFormatting.GREEN, "Village name: " + this.getVillageQualifiedName());
            for (ConstructionIP constructionIP : this.getConstructionsInProgress()) {
                if (constructionIP.getBuildingLocation() == null) continue;
                ServerSender.sendChat(player, TextFormatting.GREEN, "Construction IP: " + this.getBuildingPlanForConstruction(constructionIP) + " at " + constructionIP.getBuildingLocation());
                ServerSender.sendChat(player, TextFormatting.GREEN, "Current builder: " + constructionIP.getBuilder());
            }
            ServerSender.sendChat(player, TextFormatting.GREEN, "Current seller: " + this.seller);
            ServerSender.sendChat(player, TextFormatting.GREEN, "Rep: " + this.getReputation(player) + " bought: " + this.buildingsBought);
        }
        if (this.isInn) {
            ServerSender.sendChat(player, TextFormatting.GREEN, "Merchant: " + this.merchantRecord);
            ServerSender.sendChat(player, TextFormatting.GREEN, "Merchant nights: " + this.nbNightsMerchant);
        }
        if (this.getTags() == null) {
            ServerSender.sendChat(player, TextFormatting.GREEN, "UNKNOWN TAGS");
        } else if (this.getTags().size() > 0) {
            String s = "Tags: ";
            for (String tag : this.getTags()) {
                s = s + tag + " ";
            }
            ServerSender.sendChat(player, TextFormatting.GREEN, s);
        }
        if (this.resManager.chests.size() > 1) {
            ServerSender.sendChat(player, TextFormatting.GREEN, "Chests registered: " + this.resManager.chests.size());
        }
        if (this.resManager.furnaces.size() > 1) {
            ServerSender.sendChat(player, TextFormatting.GREEN, "Furnaces registered: " + this.resManager.furnaces.size());
        }
        if (this.resManager.firepits.size() > 1) {
            ServerSender.sendChat(player, TextFormatting.GREEN, "Firepits registered: " + this.resManager.firepits.size());
        }
        for (int i = 0; i < this.resManager.soilTypes.size(); ++i) {
            ServerSender.sendChat(player, TextFormatting.GREEN, "Fields registered: " + this.resManager.soilTypes.get(i) + ": " + this.resManager.soils.get(i).size());
        }
        if (this.resManager.sugarcanesoils.size() > 0) {
            ServerSender.sendChat(player, TextFormatting.GREEN, "Sugar cane soils registered: " + this.resManager.sugarcanesoils.size());
        }
        if (this.resManager.fishingspots.size() > 0) {
            ServerSender.sendChat(player, TextFormatting.GREEN, "Fishing spots registered: " + this.resManager.fishingspots.size());
        }
        if (this.resManager.stalls.size() > 0) {
            ServerSender.sendChat(player, TextFormatting.GREEN, "Stalls registered: " + this.resManager.stalls.size());
        }
        if (this.resManager.woodspawn.size() > 0) {
            ServerSender.sendChat(player, TextFormatting.GREEN, "Wood spawn registered: " + this.resManager.woodspawn.size());
        }
        if (this.resManager.spawns.size() > 0) {
            void var5_16;
            String s = "Pens: ";
            boolean bl = false;
            while (var5_16 < this.resManager.spawns.size()) {
                s = s + this.resManager.spawnTypes.get((int)var5_16) + ": " + this.resManager.spawns.get((int)var5_16).size() + " ";
                ++var5_16;
            }
            ServerSender.sendChat(player, TextFormatting.GREEN, s);
        }
        if (this.resManager.mobSpawners.size() > 0) {
            void var5_18;
            String s = "Mob spawners: ";
            boolean bl = false;
            while (var5_18 < this.resManager.mobSpawners.size()) {
                s = s + this.resManager.mobSpawnerTypes.get((int)var5_18) + ": " + this.resManager.mobSpawners.get((int)var5_18).size() + " ";
                ++var5_18;
            }
            ServerSender.sendChat(player, TextFormatting.GREEN, s);
        }
        if (this.resManager.sources.size() > 0) {
            void var5_20;
            String s = "Sources: ";
            boolean bl = false;
            while (var5_20 < this.resManager.sources.size()) {
                s = s + this.resManager.sourceTypes.get((int)var5_20).toString() + ": " + this.resManager.sources.get((int)var5_20).size() + " ";
                ++var5_20;
            }
            ServerSender.sendChat(player, TextFormatting.GREEN, s);
        }
        for (MillVillager millVillager : this.getKnownVillagers()) {
            if (millVillager == null) {
                ServerSender.sendChat(player, TextFormatting.GREEN, "NULL villager!");
                continue;
            }
            ServerSender.sendChat(player, TextFormatting.GREEN, millVillager.getClass().getSimpleName() + ": " + millVillager.getPos() + (millVillager.func_70089_S() ? "" : " DEAD") + " " + millVillager.getGoalLabel(millVillager.goalKey));
        }
        Object s = "LKey: " + this.location.planKey + " Shop: " + this.location.shop + " special: ";
        if (this.isTownhall) {
            s = (String)s + "Town Hall ";
        }
        if (this.isInn) {
            s = (String)s + "Inn ";
        }
        if (this.isMarket) {
            s = (String)s + "Market";
        }
        if (this.pujas != null) {
            s = (String)s + "Shrine ";
        }
        if (!((String)s).equals("")) {
            ServerSender.sendChat(player, TextFormatting.GREEN, (String)s);
        }
        if (this.pathsToBuild != null || this.oldPathPointsToClear != null) {
            s = this.pathsToBuild != null ? "pathsToBuild: " + this.pathsToBuild.size() + " " + this.pathsToBuildIndex + "/" + this.pathsToBuildPathIndex : "pathsToBuild:null";
            s = this.oldPathPointsToClear != null ? (String)s + " oldPathPointsToClear: " + this.oldPathPointsToClear.size() + " " + this.oldPathPointsToClearIndex : (String)s + " oldPathPointsToClear:null";
            ServerSender.sendChat(player, TextFormatting.GREEN, (String)s);
        }
        this.validateVillagerList();
    }

    private boolean endRaid() {
        boolean attackersWon;
        Building targetVillage = this.mw.getBuilding(this.raidTarget);
        if (targetVillage == null) {
            MillLog.error(this, "endRaid() called but couldn't find raidTarget at: " + this.raidTarget);
            return false;
        }
        if (targetVillage.location == null) {
            MillLog.error(this, "endRaid() called but target is missing its location at: " + this.raidTarget);
            return false;
        }
        if (MillConfigValues.LogDiplomacy >= 1) {
            MillLog.major(this, "Called to end raid on " + targetVillage);
        }
        float defendingForce = (float)targetVillage.getVillageDefendingStrength() * (1.0f + MillCommonUtilities.random.nextFloat());
        float attackingForce = (float)targetVillage.getVillageAttackerStrength() * (1.0f + MillCommonUtilities.random.nextFloat());
        if (attackingForce == 0.0f) {
            attackersWon = false;
        } else if (defendingForce == 0.0f) {
            attackersWon = true;
        } else {
            float ratio = attackingForce / defendingForce;
            boolean bl = attackersWon = (double)ratio > 1.2;
        }
        if (MillConfigValues.LogDiplomacy >= 1) {
            MillLog.major(this, "Result of raid: " + attackersWon + " (" + attackingForce + "/" + attackingForce + ")");
        }
        for (VillagerRecord vr : this.getVillagerRecords().values()) {
            if (!vr.awayraiding) continue;
            vr.awayraiding = false;
            VillagerRecord awayRecord = this.mw.getVillagerRecordById(vr.getOriginalId());
            if (awayRecord != null) {
                vr.killed = awayRecord.killed;
                continue;
            }
            vr.killed = false;
        }
        targetVillage.clearAllAttackers();
        for (MillVillager v : targetVillage.getKnownVillagers()) {
            if (v.func_70638_az() == null || !(v.func_70638_az() instanceof MillVillager)) continue;
            v.func_70624_b(null);
        }
        this.cancelRaid();
        targetVillage.underAttack = false;
        if (attackersWon) {
            EntityPlayer owner;
            int nbStolen = 0;
            String taken = "";
            for (TradeGood good : this.culture.goodsList) {
                if (nbStolen > 1024) continue;
                int nbToTake = this.nbGoodNeeded(good.item.getItem(), good.item.meta);
                if ((nbToTake = Math.min(nbToTake, Math.max(0, 1024 - nbStolen))) <= 0 || (nbToTake = Math.min(nbToTake, targetVillage.countGoods(good.item))) <= 0) continue;
                if (MillConfigValues.LogDiplomacy >= 3) {
                    MillLog.debug(this, "Able to take: " + nbToTake + " " + good.getName());
                }
                targetVillage.takeGoods(good.item, nbToTake);
                this.storeGoods(good.item, nbToTake);
                nbStolen += nbToTake;
                taken = taken + ";" + good.item.getItem() + "/" + good.item.meta + "/" + nbToTake;
            }
            this.raidsPerformed.add("success;" + targetVillage.getVillageQualifiedName() + taken);
            targetVillage.raidsSuffered.add("success;" + this.getVillageQualifiedName() + taken);
            if (MillConfigValues.LogDiplomacy >= 1) {
                MillLog.major(this, "Raid on " + targetVillage + " successfull (" + attackingForce + "/" + defendingForce + ")");
            }
            ServerSender.sendTranslatedSentenceInRange(this.world, this.getPos(), MillConfigValues.BackgroundRadius, '4', "raid.raidsuccesfull", this.getVillageQualifiedName(), targetVillage.getVillageQualifiedName(), "" + nbStolen);
            if (this.controlledBy != null && (owner = this.world.func_152378_a(this.controlledBy)) != null) {
                MillAdvancements.VIKING.grant(owner);
                if (this.culture.key.equals("seljuk") && targetVillage.culture.key.equals("byzantines")) {
                    MillAdvancements.ISTANBUL.grant(owner);
                }
                if (targetVillage.controlledBy != null && !this.controlledBy.equals(targetVillage.controlledBy)) {
                    MillAdvancements.MP_RAIDONPLAYER.grant(owner);
                }
            }
        } else {
            this.raidsPerformed.add("failure;" + targetVillage.getVillageQualifiedName());
            targetVillage.raidsSuffered.add("failure;" + this.getVillageQualifiedName());
            if (MillConfigValues.LogDiplomacy >= 1) {
                MillLog.major(this, "Raid on " + targetVillage + " failed (" + attackingForce + "/" + defendingForce + ")");
            }
            if (targetVillage.controlledBy != null && this.culture.key.equals("seljuk") && targetVillage.culture.key.equals("byzantines")) {
                EntityPlayer targetOwner = this.world.func_152378_a(targetVillage.controlledBy);
                MillAdvancements.NOTTODAY.grant(targetOwner);
            }
            ServerSender.sendTranslatedSentenceInRange(this.world, this.getPos(), MillConfigValues.BackgroundRadius, '4', "raid.raidfailed", this.getVillageQualifiedName(), targetVillage.getVillageQualifiedName());
        }
        MillLog.major(this, "Finished ending raid. Records: " + this.getVillagerRecords().size());
        targetVillage.saveTownHall("Raid on village ended");
        this.saveNeeded = true;
        this.saveReason = "Raid finished";
        return true;
    }

    public int estimateAbstractedProductionCapacity(InvItem invItem) {
        BuildingPlan plan = this.location.getPlan();
        if (plan != null && plan.abstractedProduction.containsKey(invItem)) {
            return plan.abstractedProduction.get(invItem);
        }
        return 0;
    }

    private void fillinBuildingLocationInProjects(BuildingLocation location) {
        this.mw.testLocations("fillinBuildingLocation start");
        boolean registered = false;
        for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
            if (!this.buildingProjects.containsKey((Object)ep)) continue;
            List projectsLevel = this.buildingProjects.get((Object)ep);
            ArrayList temp = new ArrayList(projectsLevel);
            for (BuildingProject project : temp) {
                int pos = 0;
                if (!registered && project.location == null && location.planKey.equals(project.key)) {
                    project.location = location;
                    registered = true;
                    if (MillConfigValues.LogBuildingPlan >= 2) {
                        MillLog.minor(this, "Registered building: " + location + " (level " + location.level + ", variation: " + location.getVariation() + ")");
                    }
                    if (project.location.level >= 0) {
                        for (String s : project.location.subBuildings) {
                            BuildingProject newproject = new BuildingProject(this.culture.getBuildingPlanSet(s), project.location.getPlan());
                            newproject.location = location.createLocationForSubBuilding(s);
                            projectsLevel.add(pos + 1, newproject);
                            if (MillConfigValues.LogBuildingPlan < 1) continue;
                            MillLog.major(this, "Adding sub-building to project list: " + newproject + " at pos " + pos + " in " + projectsLevel);
                        }
                    }
                    ++pos;
                    continue;
                }
                if (registered || project.location == null || project.location.level >= 0 || !project.location.isSameLocation(location)) continue;
                project.location = location;
                registered = true;
                if (MillConfigValues.LogBuildingPlan < 1) continue;
                MillLog.major(this, "Registered subbuilding: " + location + " (level " + location.level + ", variation: " + location.getVariation() + ")");
            }
        }
        if (!registered) {
            BuildingProject project;
            if (location.isCustomBuilding) {
                project = new BuildingProject(this.culture.getBuildingCustom(location.planKey), location);
                this.buildingProjects.get((Object)BuildingProject.EnumProjects.CUSTOMBUILDINGS).add(project);
            } else {
                project = new BuildingProject(this.culture.getBuildingPlanSet(location.planKey));
                project.location = location;
                if (this.villageType.playerControlled) {
                    this.buildingProjects.get((Object)BuildingProject.EnumProjects.CORE).add(project);
                } else if (location.getPlan().isWallSegment) {
                    if (!this.buildingProjects.containsKey((Object)BuildingProject.EnumProjects.WALLBUILDING)) {
                        this.buildingProjects.put(BuildingProject.EnumProjects.WALLBUILDING, new CopyOnWriteArrayList());
                    }
                    this.buildingProjects.get((Object)BuildingProject.EnumProjects.WALLBUILDING).add(project);
                } else {
                    this.buildingProjects.get((Object)BuildingProject.EnumProjects.EXTRA).add(project);
                }
            }
        }
        this.mw.testLocations("fillinBuildingLocation end");
    }

    public void fillStartingGoods() {
        if (this.location.getPlan() == null) {
            return;
        }
        for (Point p : this.resManager.chests) {
            TileEntityLockedChest chest = p.getMillChest(this.world);
            if (chest == null) continue;
            for (int i = 0; i < chest.func_70302_i_(); ++i) {
                chest.func_70299_a(i, ItemStack.field_190927_a);
            }
        }
        for (BuildingPlan.StartingGood sg : this.location.getPlan().startingGoods) {
            int chestId;
            TileEntityLockedChest chest;
            if (!MillCommonUtilities.probability(sg.probability)) continue;
            int nb = sg.fixedNumber;
            if (sg.randomNumber > 0) {
                nb += MillCommonUtilities.randomInt(sg.randomNumber + 1);
            }
            if (nb <= 0 || (chest = this.resManager.chests.get(chestId = MillCommonUtilities.randomInt(this.resManager.chests.size())).getMillChest(this.world)) == null) continue;
            MillCommonUtilities.putItemsInChest((IInventory)chest, sg.item.getItem(), sg.item.meta, nb);
        }
        this.invalidateInventoryCache();
        if (MillConfigValues.DEV) {
            this.testModeGoods();
        }
    }

    private Point findAttackerSpawnPoint(Point origin) {
        int x = origin.getiX() > this.pos.getiX() ? Math.min(this.winfo.length - 5, this.winfo.length / 2 + 50) : Math.max(5, this.winfo.length / 2 - 50);
        int z = origin.getiZ() > this.pos.getiZ() ? Math.min(this.winfo.width - 5, this.winfo.width / 2 + 50) : Math.max(5, this.winfo.width / 2 - 50);
        for (int i = 0; i < 40; ++i) {
            Chunk chunk;
            int tx = x + MillCommonUtilities.randomInt(5 + i) - MillCommonUtilities.randomInt(5 + i);
            int tz = z + MillCommonUtilities.randomInt(5 + i) - MillCommonUtilities.randomInt(5 + i);
            tx = Math.max(Math.min(tx, this.winfo.length - 1), 0);
            tz = Math.max(Math.min(tz, this.winfo.width - 1), 0);
            tx = Math.min(tx, this.winfo.length / 2 + 50);
            tx = Math.max(tx, this.winfo.length / 2 - 50);
            tz = Math.min(tz, this.winfo.width / 2 + 50);
            if (!this.winfo.canBuild[tx][tz = Math.max(tz, this.winfo.width / 2 - 50)] || !(chunk = this.world.func_175726_f(new BlockPos(this.winfo.mapStartX + tx, 0, this.winfo.mapStartZ + tz))).func_177410_o()) continue;
            return new Point(this.winfo.mapStartX + tx, WorldUtilities.findTopSoilBlock(this.world, this.winfo.mapStartX + tx, this.winfo.mapStartZ + tz) + 1, this.winfo.mapStartZ + tz);
        }
        return this.resManager.getDefendingPos();
    }

    /*
     * WARNING - void declaration
     */
    private boolean findBuildingConstruction(boolean ignoreCost) {
        boolean bl;
        if (this.buildingGoal == null) {
            return false;
        }
        if (this.regionMapper == null) {
            try {
                this.rebuildRegionMapper(true);
            }
            catch (MillLog.MillenaireException e) {
                MillLog.printException(e);
            }
        }
        ConstructionIP targetConstruction = null;
        int nbNonWallConstructions = 0;
        for (ConstructionIP constructionIP : this.getConstructionsInProgress()) {
            if (constructionIP.isWallConstruction()) continue;
            if (targetConstruction == null && constructionIP.getBuildingLocation() == null) {
                targetConstruction = constructionIP;
            }
            ++nbNonWallConstructions;
        }
        if (targetConstruction == null && nbNonWallConstructions < this.getSimultaneousConstructionSlots()) {
            targetConstruction = new ConstructionIP(this, this.getConstructionsInProgress().size(), false);
            this.getConstructionsInProgress().add(targetConstruction);
        }
        if (targetConstruction == null) {
            return false;
        }
        BuildingProject goalProject = null;
        if (this.findConstructionIPforLocation(this.buildingGoalLocation) == null && this.findConstructionIPforBuildingPlanKey(this.buildingGoal, false) == null) {
            for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
                if (!this.buildingProjects.containsKey((Object)ep)) continue;
                List projectsLevel = this.buildingProjects.get((Object)ep);
                for (BuildingProject project : projectsLevel) {
                    if (this.buildingGoalLocation != null && this.buildingGoalLocation.isSameLocation(project.location)) {
                        goalProject = project;
                        continue;
                    }
                    if (this.buildingGoalLocation != null || project.location != null || !this.buildingGoal.equals(project.key)) continue;
                    goalProject = project;
                }
            }
            if (MillConfigValues.LogBuildingPlan >= 3) {
                MillLog.debug(this, "Building goal project: " + goalProject + " ");
            }
            if (goalProject == null) {
                MillLog.error(this, "Could not find building project for " + this.buildingGoal + " and " + this.buildingGoalLocation + ", cancelling goal.");
                this.buildingGoal = null;
                this.buildingGoalLocation = null;
                return false;
            }
            if (goalProject.location != null && goalProject.location.level >= 0 && goalProject.location.upgradesAllowed) {
                if (ignoreCost || this.canAffordProject(goalProject.getPlan(this.buildingGoalVariation, this.buildingGoalLevel))) {
                    void var5_10;
                    if (this.buildingGoalLocation != null) {
                        BuildingLocation buildingLocation = this.buildingGoalLocation;
                    } else {
                        BuildingLocation buildingLocation = goalProject.location;
                    }
                    targetConstruction.startNewConstruction((BuildingLocation)var5_10, goalProject.getPlan(this.buildingGoalVariation, this.buildingGoalLevel).getBuildingPoints(this.world, (BuildingLocation)var5_10, false, false, false));
                    if (MillConfigValues.LogBuildingPlan >= 1) {
                        MillLog.major(this, "Upgrade project possible at: " + this.location + " for level " + this.buildingGoalLevel);
                    }
                    if (targetConstruction.getBblocks().length == 0) {
                        MillLog.error(this, "No bblocks for\t " + targetConstruction.getBuildingLocation());
                        try {
                            this.rushCurrentConstructions(false);
                        }
                        catch (Exception e) {
                            MillLog.printException("Exception when trying to rush building:", e);
                        }
                    }
                } else {
                    this.buildingGoalIssue = "ui.lackingresources";
                }
            } else if (goalProject.location != null && goalProject.location.level < 0) {
                if (ignoreCost || this.canAffordProject(goalProject.getPlan(this.buildingGoalVariation, this.buildingGoalLevel))) {
                    void var5_13;
                    if (this.buildingGoalLocation != null) {
                        BuildingLocation buildingLocation = this.buildingGoalLocation;
                    } else {
                        BuildingLocation buildingLocation = goalProject.location;
                    }
                    targetConstruction.startNewConstruction((BuildingLocation)var5_13, goalProject.getPlan(this.buildingGoalVariation, this.buildingGoalLevel).getBuildingPoints(this.world, (BuildingLocation)var5_13, false, false, false));
                    if (targetConstruction.getBblocks().length == 0) {
                        MillLog.error(this, "No bblocks for\t " + targetConstruction.getBuildingLocation());
                    }
                } else {
                    this.buildingGoalIssue = "ui.lackingresources";
                }
            } else if (goalProject.location == null) {
                boolean bl2;
                boolean bl3 = bl2 = ignoreCost || this.canAffordProject(goalProject.getPlan(this.buildingGoalVariation, 0));
                if (System.currentTimeMillis() - this.lastFailedProjectLocationSearch > 80000L && bl2) {
                    BuildingLocation location = goalProject.getPlan(this.buildingGoalVariation, 0).findBuildingLocation(this.winfo, this.regionMapper, this.location.pos, this.villageType.radius, MillCommonUtilities.getRandom(), -1);
                    this.lastFailedProjectLocationSearch = System.currentTimeMillis();
                    if (location != null) {
                        this.lastFailedProjectLocationSearch = 0L;
                        if (this.brickColourTheme != null && location.getPlan().randomBrickColours.isEmpty()) {
                            location.initialiseBrickColoursFromTheme(this, this.brickColourTheme);
                        }
                        this.buildingGoalLocation = location;
                        targetConstruction.startNewConstruction(location, goalProject.getPlan(this.buildingGoalVariation, 0).getBuildingPoints(this.world, location, false, false, false));
                        if (MillConfigValues.LogBuildingPlan >= 1) {
                            MillLog.major(this, "New project location: Loaded " + targetConstruction.getBblocks().length + " building blocks for " + goalProject.getPlan((int)this.buildingGoalVariation, (int)0).planName);
                        }
                        int groundLevel = WorldUtilities.findTopSoilBlock(this.world, location.pos.getiX(), location.pos.getiZ());
                        for (int i = groundLevel + 1; i < location.pos.getiY(); ++i) {
                            WorldUtilities.setBlockAndMetadata(this.world, location.pos, Blocks.field_150346_d, 0);
                        }
                        if (MillConfigValues.LogBuildingPlan >= 1) {
                            MillLog.major(this, "Found location for building project: " + location);
                        }
                    } else {
                        this.buildingGoalIssue = "ui.nospace";
                        this.lastFailedProjectLocationSearch = System.currentTimeMillis();
                        if (MillConfigValues.LogBuildingPlan >= 1) {
                            MillLog.major(this, "Searching for a location for the new project failed.");
                        }
                    }
                } else if (!bl2) {
                    this.buildingGoalIssue = "ui.lackingresources";
                    if (MillConfigValues.LogBuildingPlan >= 3) {
                        MillLog.debug(this, "Cannot afford building project.");
                    }
                } else {
                    this.buildingGoalIssue = "ui.nospace";
                }
            }
        }
        if (targetConstruction.getBuildingLocation() != null) {
            return true;
        }
        boolean bl4 = false;
        List<BuildingProject> possibleProjects = this.getAllPossibleProjects();
        ArrayList<BuildingProject> affordableProjects = new ArrayList<BuildingProject>();
        for (BuildingProject project : possibleProjects) {
            if (project.planSet == null || goalProject != null && project == goalProject) continue;
            if (project.location == null || project.location.level < 0) {
                if (this.findConstructionIPforBuildingPlanKey(project.planSet.key, true) != null || !ignoreCost && !this.canAffordBuild(project.planSet.getFirstStartingPlan())) continue;
                affordableProjects.add(project);
                continue;
            }
            if (this.findConstructionIPforLocation(project.location) != null) continue;
            BuildingPlan plan = project.getNextBuildingPlan(true);
            if (!ignoreCost && !this.canAffordBuild(plan)) continue;
            affordableProjects.add(project);
        }
        if (affordableProjects.isEmpty()) {
            this.lastFailedOtherLocationSearch = System.currentTimeMillis();
            return false;
        }
        BuildingProject newProject = BuildingProject.getRandomProject(affordableProjects);
        if (newProject.location == null || newProject.location.level < 0) {
            BuildingPlan plan = newProject.planSet.getRandomStartingPlan();
            BuildingLocation location = null;
            if (ignoreCost || this.canAffordBuild(plan)) {
                if (newProject.location == null && System.currentTimeMillis() - this.lastFailedOtherLocationSearch > 80000L) {
                    location = plan.findBuildingLocation(this.winfo, this.regionMapper, this.location.pos, this.villageType.radius, MillCommonUtilities.getRandom(), -1);
                } else if (newProject.location != null) {
                    location = newProject.location.createLocationForLevel(0);
                }
            }
            if (location != null) {
                this.lastFailedOtherLocationSearch = 0L;
                targetConstruction.startNewConstruction(location, plan.getBuildingPoints(this.world, location, false, false, false));
                if (MillConfigValues.LogBuildingPlan >= 1) {
                    MillLog.major(this, "New location non-project: Loaded " + targetConstruction.getBblocks().length + " building blocks for " + plan.planName);
                }
            } else {
                bl = true;
            }
        } else {
            int level = newProject.location.level + 1;
            int variation = newProject.location.getVariation();
            BuildingLocation bl5 = newProject.location.createLocationForLevel(level);
            targetConstruction.startNewConstruction(bl5, newProject.getPlan(variation, level).getBuildingPoints(this.world, bl5, false, false, false));
            if (MillConfigValues.LogBuildingPlan >= 1) {
                MillLog.major(this, "Upgrade non-project: Loaded " + targetConstruction.getBblocks().length + " building blocks for " + newProject.getPlan((int)variation, (int)level).planName + " upgrade. Old level: " + newProject.location.level + " New level: " + level);
            }
        }
        if (bl) {
            this.lastFailedOtherLocationSearch = System.currentTimeMillis();
        }
        return true;
    }

    private boolean findBuildingConstructionWall(boolean ignoreCost) {
        ConstructionIP targetConstruction = null;
        int nbWallConstructions = 0;
        for (ConstructionIP cip : this.getConstructionsInProgress()) {
            if (!cip.isWallConstruction()) continue;
            if (targetConstruction == null && cip.getBuildingLocation() == null) {
                targetConstruction = cip;
            }
            ++nbWallConstructions;
        }
        if (targetConstruction == null && nbWallConstructions < this.getSimultaneousWallConstructionSlots()) {
            targetConstruction = new ConstructionIP(this, this.getConstructionsInProgress().size(), true);
            this.getConstructionsInProgress().add(targetConstruction);
        }
        if (targetConstruction == null) {
            return false;
        }
        if (!this.buildingProjects.containsKey((Object)BuildingProject.EnumProjects.WALLBUILDING)) {
            return false;
        }
        List wallProjects = this.buildingProjects.get((Object)BuildingProject.EnumProjects.WALLBUILDING);
        for (BuildingProject project : wallProjects) {
            if (project.planSet != null && this.findConstructionIPforLocation(project.location) == null) {
                if (project.location.level < 0 && (ignoreCost || this.canAffordBuild(project.location.getPlan()))) {
                    BuildingPlan plan = project.location.getPlan();
                    if (this.isValidProject(project)) {
                        BuildingLocation location = project.location.createLocationForLevel(0);
                        targetConstruction.startNewConstruction(location, plan.getBuildingPoints(this.world, location, false, false, false));
                    }
                } else {
                    int level = project.location.level + 1;
                    int variation = project.location.getVariation();
                    if (level < project.getLevelsNumber(variation) && this.isValidUpgrade(project) && project.location.upgradesAllowed && (ignoreCost || this.canAffordBuild(project.getPlan(variation, level)))) {
                        BuildingLocation bl = project.location.createLocationForLevel(level);
                        targetConstruction.startNewConstruction(bl, project.getPlan(variation, level).getBuildingPoints(this.world, bl, false, false, false));
                        if (MillConfigValues.LogBuildingPlan >= 1) {
                            MillLog.major(this, " Wall upgrade non-project: Loaded " + targetConstruction.getBblocks().length + " building blocks for " + project.getPlan((int)variation, (int)level).planName + " upgrade. Old level: " + project.location.level + " New level: " + level);
                        }
                    }
                }
            }
            if (targetConstruction.getBuildingLocation() == null) continue;
            return true;
        }
        return false;
    }

    private boolean findBuildingProject() {
        List<BuildingProject> possibleProjects;
        if (this.buildingGoal != null && this.buildingGoal.length() > 0) {
            return false;
        }
        if (this.noProjectsLeft && (this.world.func_72820_D() + (long)this.hashCode()) % 600L != 3L) {
            return false;
        }
        this.buildingGoal = null;
        this.buildingGoalLocation = null;
        if (MillConfigValues.LogBuildingPlan >= 2) {
            MillLog.minor(this, "Searching for new building goal");
        }
        if ((possibleProjects = this.getAllPossibleProjects()).size() == 0) {
            this.noProjectsLeft = true;
            return false;
        }
        this.noProjectsLeft = false;
        BuildingProject project = BuildingProject.getRandomProject(possibleProjects);
        BuildingPlan plan = project.getNextBuildingPlan(true);
        this.buildingGoal = project.key;
        this.buildingGoalLevel = plan.level;
        this.buildingGoalVariation = plan.variation;
        if (project.location == null) {
            this.buildingGoalLocation = null;
            ConstructionIP cip = this.findConstructionIPforBuildingPlanKey(this.buildingGoal, true);
            if (cip != null) {
                this.buildingGoalLocation = cip.getBuildingLocation();
            }
        } else {
            this.buildingGoalLocation = project.location.createLocationForLevel(this.buildingGoalLevel);
        }
        if (MillConfigValues.LogBuildingPlan >= 1) {
            MillLog.major(this, "Picked new upgrade goal: " + this.buildingGoal + " level: " + this.buildingGoalLevel + " buildingGoalLocation: " + this.buildingGoalLocation);
        }
        return true;
    }

    public ConstructionIP findConstructionIPforBuildingPlanKey(String key, boolean newBuildingOnly) {
        for (ConstructionIP cip : this.getConstructionsInProgress()) {
            if (cip == null || cip.getBuildingLocation() == null || !cip.getBuildingLocation().getPlan().buildingKey.equals(key) || newBuildingOnly && cip.getBuildingLocation().level != 0) continue;
            return cip;
        }
        return null;
    }

    public ConstructionIP findConstructionIPforLocation(BuildingLocation bl) {
        if (bl == null) {
            return null;
        }
        for (ConstructionIP cip : this.getConstructionsInProgress()) {
            if (cip == null || !bl.isSameLocation(cip.getBuildingLocation())) continue;
            return cip;
        }
        return null;
    }

    public void findName(String pname) {
        if (pname != null) {
            this.name = pname;
        } else {
            if (this.villageType.nameList == null) {
                this.name = null;
                return;
            }
            this.name = this.culture.getRandomNameFromList(this.villageType.nameList);
        }
        ArrayList<String> qualifiers = new ArrayList<String>();
        for (String s : this.villageType.qualifiers) {
            qualifiers.add(s);
        }
        if (this.villageType.hillQualifier != null && this.pos.getiY() > 75 && this.pos.getiY() < 85) {
            qualifiers.add(this.villageType.hillQualifier);
        } else if (this.villageType.mountainQualifier != null && this.pos.getiY() >= 85) {
            qualifiers.add(this.villageType.mountainQualifier);
        }
        if (this.villageType.desertQualifier != null || this.villageType.forestQualifier != null || this.villageType.lavaQualifier != null || this.villageType.lakeQualifier != null || this.villageType.oceanQualifier != null) {
            int cactus = 0;
            int wood = 0;
            int lake = 0;
            int ocean = 0;
            int lava = 0;
            for (int i = -50; i < 50; ++i) {
                for (int j = -10; j < 20; ++j) {
                    for (int k = -50; k < 50; ++k) {
                        Block block = WorldUtilities.getBlock(this.world, i + this.pos.getiX(), j + this.pos.getiY(), k + this.pos.getiZ());
                        if (block == Blocks.field_150434_aF) {
                            ++cactus;
                            continue;
                        }
                        if (block == Blocks.field_150364_r || block == Blocks.field_150363_s) {
                            ++wood;
                            continue;
                        }
                        if (block == Blocks.field_150353_l) {
                            ++lava;
                            continue;
                        }
                        if (block != Blocks.field_150355_j || WorldUtilities.getBlock(this.world, i + this.pos.getiX(), j + this.pos.getiY() + 1, k + this.pos.getiZ()) != Blocks.field_150350_a) continue;
                        if (j + this.pos.getiY() < 65) {
                            ++ocean;
                            continue;
                        }
                        ++lake;
                    }
                }
            }
            if (this.villageType.desertQualifier != null && cactus > 0) {
                qualifiers.add(this.villageType.desertQualifier);
            }
            if (this.villageType.forestQualifier != null && wood > 40) {
                qualifiers.add(this.villageType.forestQualifier);
            }
            if (this.villageType.lavaQualifier != null && lava > 0) {
                qualifiers.add(this.villageType.lavaQualifier);
            }
            if (this.villageType.lakeQualifier != null && lake > 0) {
                qualifiers.add(this.villageType.lakeQualifier);
            }
            if (this.villageType.oceanQualifier != null && ocean > 0) {
                qualifiers.add(this.villageType.oceanQualifier);
            }
        }
        this.qualifier = qualifiers.size() > 0 ? (String)qualifiers.get(MillCommonUtilities.randomInt(qualifiers.size())) : "";
    }

    public void generateBannerPattern() {
        if (!this.villageType.banner_JSONs.isEmpty()) {
            String bannerJSON = this.villageType.banner_JSONs.get(MillCommonUtilities.randomInt(this.villageType.banner_JSONs.size())).replace("blockentitytag", "BlockEntityTag").replace("base", "Base").replace("pattern", "Pattern").replace("color", "Color");
            this.bannerStack = new ItemStack(Items.field_179564_cE, 1);
            try {
                this.bannerStack.func_77982_d(JsonToNBT.func_180713_a((String)bannerJSON));
                return;
            }
            catch (NBTException nbtException) {
                this.bannerStack = null;
                MillLog.error(this, "Bad banner JSON " + bannerJSON + ", using default banner settings");
            }
        }
        String baseColor = "black";
        if (!this.villageType.banner_baseColors.isEmpty()) {
            baseColor = this.villageType.banner_baseColors.get(MillCommonUtilities.randomInt(this.villageType.banner_baseColors.size()));
        }
        EnumDyeColor baseDyeColor = EnumDyeColor.BLACK;
        for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
            if (!enumDyeColor.func_176610_l().equals(baseColor)) continue;
            baseDyeColor = enumDyeColor;
        }
        NBTTagList patternList = new NBTTagList();
        if (!this.villageType.banner_patternsColors.isEmpty() && !this.villageType.banner_Patterns.isEmpty()) {
            String patternColor = this.villageType.banner_patternsColors.get(MillCommonUtilities.randomInt(this.villageType.banner_patternsColors.size()));
            int patternColorDamage = 0;
            for (EnumDyeColor dyeColor : EnumDyeColor.values()) {
                if (!dyeColor.func_176610_l().equals(patternColor)) continue;
                patternColorDamage = dyeColor.func_176767_b();
                break;
            }
            String string = this.villageType.banner_Patterns.get(MillCommonUtilities.randomInt(this.villageType.banner_Patterns.size()));
            String[] stringArray = string.split(",");
            int n = stringArray.length;
            for (int dyeColor = 0; dyeColor < n; ++dyeColor) {
                String pattern = stringArray[dyeColor];
                NBTTagCompound patternNBT = new NBTTagCompound();
                patternNBT.func_74778_a("Pattern", pattern);
                patternNBT.func_74768_a("Color", patternColorDamage);
                patternList.func_74742_a((NBTBase)patternNBT);
            }
        }
        if (!this.villageType.banner_chargeColors.isEmpty() && !this.villageType.banner_chargePatterns.isEmpty()) {
            String chargeColor = this.villageType.banner_chargeColors.get(MillCommonUtilities.randomInt(this.villageType.banner_chargeColors.size()));
            int chargeColorDamage = 0;
            for (EnumDyeColor dyeColor : EnumDyeColor.values()) {
                if (!dyeColor.func_176610_l().equals(chargeColor)) continue;
                chargeColorDamage = dyeColor.func_176767_b();
                break;
            }
            String string = this.villageType.banner_chargePatterns.get(MillCommonUtilities.randomInt(this.villageType.banner_chargePatterns.size()));
            for (String chargePattern : string.split(",")) {
                NBTTagCompound chargeNBT = new NBTTagCompound();
                chargeNBT.func_74778_a("Pattern", chargePattern);
                chargeNBT.func_74768_a("Color", chargeColorDamage);
                patternList.func_74742_a((NBTBase)chargeNBT);
            }
        }
        this.bannerStack = ItemBanner.func_190910_a((EnumDyeColor)baseDyeColor, (NBTTagList)patternList);
    }

    public Set<String> getAllFamilyNames() {
        HashSet<String> names = new HashSet<String>();
        for (VillagerRecord vr : this.vrecords.values()) {
            names.add(vr.familyName);
        }
        return names;
    }

    private List<BuildingProject> getAllPossibleProjects() {
        ArrayList<BuildingProject> possibleProjects = new ArrayList<BuildingProject>();
        boolean foundNewBuildingsLevel = false;
        for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
            if (!this.buildingProjects.containsKey((Object)ep) || ep == BuildingProject.EnumProjects.WALLBUILDING) continue;
            List projectsLevel = this.buildingProjects.get((Object)ep);
            boolean includedNewBuildings = false;
            for (BuildingProject project : projectsLevel) {
                project.projectTier = ep;
                if (!(project.location != null && project.location.level >= 0 || foundNewBuildingsLevel)) {
                    if (!this.isValidProject(project)) continue;
                    possibleProjects.add(project);
                    includedNewBuildings = true;
                    if (MillConfigValues.LogBuildingPlan >= 3) {
                        MillLog.debug(this, "Found a new building to add: " + project);
                    }
                    if (MillConfigValues.LogBuildingPlan < 2 || project.getChoiceWeight(null) >= 1) continue;
                    MillLog.minor(this, "Project has null or negative weight: " + project + ": " + project.getChoiceWeight(null));
                    continue;
                }
                if (project.location == null || !this.isValidUpgrade(project) || project.location.level < 0 || project.location.level >= project.getLevelsNumber(project.location.getVariation()) || !project.location.upgradesAllowed || project.getChoiceWeight(null) <= 0) continue;
                possibleProjects.add(project);
            }
            if (!includedNewBuildings) continue;
            foundNewBuildingsLevel = true;
        }
        return possibleProjects;
    }

    public Collection<VillagerRecord> getAllVillagerRecords() {
        return this.getVillagerRecords().values();
    }

    public int getAltitude(int x, int z) {
        if (this.winfo == null) {
            return -1;
        }
        if (x < this.winfo.mapStartX || x >= this.winfo.mapStartX + this.winfo.length || z < this.winfo.mapStartZ || z >= this.winfo.mapStartZ + this.winfo.width) {
            return -1;
        }
        return this.winfo.topGround[x - this.winfo.mapStartX][z - this.winfo.mapStartZ];
    }

    public ItemStack getBannerStack() {
        return this.bannerStack;
    }

    public Building getBuildingAtCoordPlanar(Point p) {
        for (Building b : this.getBuildings()) {
            if (!b.location.isInsidePlanar(p)) continue;
            return b;
        }
        return null;
    }

    public Building getBuildingFromLocation(BuildingLocation location) {
        for (Point p : this.buildings) {
            Building building = this.mw.getBuilding(p);
            if (building == null || !building.location.isSameLocation(location)) continue;
            return building;
        }
        return null;
    }

    public BuildingPlan getBuildingPlanForConstruction(ConstructionIP cip) {
        if (cip.getBuildingLocation() == null) {
            MillLog.error(this, "Couldn't find project for construction with no location: " + cip);
            return null;
        }
        for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
            if (!this.buildingProjects.containsKey((Object)ep)) continue;
            List projectsLevel = this.buildingProjects.get((Object)ep);
            for (BuildingProject project : projectsLevel) {
                if (cip.getBuildingLocation().level == 0 && (project.location == null || project.location.level < 0) && project.key.equals(cip.getBuildingLocation().planKey)) {
                    if (MillConfigValues.LogBuildingPlan >= 3) {
                        MillLog.debug(this, "Returning building plan for " + cip.getBuildingLocation() + ": " + project.getPlan(cip.getBuildingLocation().getVariation(), cip.getBuildingLocation().level));
                    }
                    return project.getPlan(cip.getBuildingLocation().getVariation(), cip.getBuildingLocation().level);
                }
                if (!cip.getBuildingLocation().isSameLocation(project.location)) continue;
                if (MillConfigValues.LogBuildingPlan >= 3) {
                    MillLog.debug(this, "Returning building plan for " + cip.getBuildingLocation() + ": " + project.getPlan(cip.getBuildingLocation().getVariation(), cip.getBuildingLocation().level));
                }
                return project.getPlan(cip.getBuildingLocation().getVariation(), cip.getBuildingLocation().level);
            }
        }
        MillLog.error(this, "Could not find project for current building location: " + cip.getBuildingLocation());
        return null;
    }

    public List<Building> getBuildings() {
        ArrayList<Building> vbuildings = new ArrayList<Building>();
        for (Point p : this.buildings) {
            Building building = this.mw.getBuilding(p);
            if (building == null || building.location == null) continue;
            vbuildings.add(building);
        }
        return vbuildings;
    }

    public List<Building> getBuildingsWithTag(String s) {
        ArrayList<Building> matches = new ArrayList<Building>();
        for (Point p : this.buildings) {
            Building building = this.mw.getBuilding(p);
            if (building == null || building.location == null || building.getTags() == null || !building.containsTags(s)) continue;
            matches.add(building);
        }
        return matches;
    }

    public Set<TradeGood> getBuyingGoods(EntityPlayer player) {
        if (!this.shopBuys.containsKey(player.func_70005_c_())) {
            return null;
        }
        return this.shopBuys.get(player.func_70005_c_()).keySet();
    }

    public int getBuyingPrice(TradeGood g, EntityPlayer player) {
        if (!this.shopBuys.containsKey(player.func_70005_c_()) || this.shopBuys.get(player.func_70005_c_()) == null) {
            return 0;
        }
        return this.shopBuys.get(player.func_70005_c_()).get(g);
    }

    public ConstructionIP getConstructionIPforBuilder(MillVillager builder) {
        for (ConstructionIP cip : this.getConstructionsInProgress()) {
            if (cip.getBuilder() != builder) continue;
            return cip;
        }
        return null;
    }

    public List<ConstructionIP> getConstructionsInProgress() {
        return this.constructionsIP;
    }

    public Point getCurrentClearPathPoint() {
        if (this.oldPathPointsToClear == null) {
            return null;
        }
        if (this.oldPathPointsToClearIndex >= this.oldPathPointsToClear.size()) {
            this.oldPathPointsToClear = null;
            return null;
        }
        return this.oldPathPointsToClear.get(this.oldPathPointsToClearIndex);
    }

    public BuildingPlan getCurrentGoalBuildingPlan() {
        if (this.buildingGoal == null) {
            return null;
        }
        for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
            if (!this.buildingProjects.containsKey((Object)ep)) continue;
            List projectsLevel = this.buildingProjects.get((Object)ep);
            for (BuildingProject project : projectsLevel) {
                if (!project.key.equals(this.buildingGoal)) continue;
                if (this.buildingGoalLocation == null) {
                    return project.getPlan(this.buildingGoalVariation, 0);
                }
                return project.getPlan(this.buildingGoalVariation, this.buildingGoalLocation.level);
            }
        }
        return null;
    }

    public BuildingBlock getCurrentPathBuildingBlock() {
        if (this.pathsToBuild == null) {
            return null;
        }
        while (true) {
            if (this.pathsToBuildIndex >= this.pathsToBuild.size()) {
                this.pathsToBuild = null;
                return null;
            }
            if (this.pathsToBuildPathIndex >= this.pathsToBuild.get(this.pathsToBuildIndex).size()) {
                ++this.pathsToBuildIndex;
                this.pathsToBuildPathIndex = 0;
                continue;
            }
            BuildingBlock b = this.pathsToBuild.get(this.pathsToBuildIndex).get(this.pathsToBuildPathIndex);
            IBlockState blockState = b.p.getBlockActualState(this.world);
            if (PathUtilities.canPathBeBuiltHere(blockState) && blockState != b.getBlockstate()) {
                return b;
            }
            ++this.pathsToBuildPathIndex;
        }
    }

    public Building getFirstBuildingWithTag(String s) {
        for (Point p : this.buildings) {
            Building building = this.mw.getBuilding(p);
            if (building == null || building.location == null || building.getTags() == null || !building.containsTags(s)) continue;
            return building;
        }
        return null;
    }

    public List<BuildingProject> getFlatProjectList() {
        ArrayList<BuildingProject> projects = new ArrayList<BuildingProject>();
        for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
            if (!this.buildingProjects.containsKey((Object)ep)) continue;
            List projectsLevel = this.buildingProjects.get((Object)ep);
            for (BuildingProject project : projectsLevel) {
                projects.add(project);
            }
        }
        return projects;
    }

    public String getGameBuildingName() {
        return this.location.getGameName();
    }

    public ItemStack getIcon() {
        BuildingPlan plan;
        BuildingPlanSet planSet = this.culture.getBuildingPlanSet(this.location.planKey);
        if (planSet != null && (plan = planSet.getPlan(this.location.getVariation(), this.location.level)) != null) {
            return plan.getIcon();
        }
        return ItemStack.field_190927_a;
    }

    public HashMap<TradeGood, Integer> getImportsNeededbyOtherVillages() {
        if (this.neededGoodsCached != null && System.currentTimeMillis() < this.neededGoodsLastGenerated + 60000L) {
            return this.neededGoodsCached;
        }
        this.neededGoodsCached = new HashMap();
        for (Point vp : this.mw.villagesList.pos) {
            Building townHall;
            Chunk chunk = this.world.func_175726_f(new BlockPos(vp.getiX(), 0, vp.getiZ()));
            if (!chunk.func_177410_o() || (townHall = this.mw.getBuilding(vp)) == null || this.getTownHall() == null || townHall.villageType == this.getTownHall().villageType || townHall.culture != this.getTownHall().culture || townHall.getBuildingsWithTag("inn").size() <= 0) continue;
            townHall.getNeededImportGoods(this.neededGoodsCached);
        }
        this.neededGoodsLastGenerated = System.currentTimeMillis();
        return this.neededGoodsCached;
    }

    private int getInventoryCountFromCache(InvItem invItem) {
        if (this.inventoryCache == null) {
            this.calculateInventoryCache();
        }
        if (invItem.item == Item.func_150898_a((Block)Blocks.field_150364_r) && invItem.meta == -1) {
            int count = 0;
            for (int meta = 0; meta < 15; ++meta) {
                InvItem invItemAdjusted = InvItem.createInvItem(invItem.item, meta);
                count += this.getInventoryCountFromCache(invItemAdjusted);
            }
            Item itemLog2 = Item.func_150898_a((Block)Blocks.field_150363_s);
            for (int meta = 0; meta < 15; ++meta) {
                InvItem invItemAdjusted = InvItem.createInvItem(itemLog2, meta);
                count += this.getInventoryCountFromCache(invItemAdjusted);
            }
            return count;
        }
        if (invItem.meta == -1) {
            int count = 0;
            for (int meta = 0; meta < 15; ++meta) {
                InvItem invItemAdjusted = InvItem.createInvItem(invItem.item, meta);
                count += this.getInventoryCountFromCache(invItemAdjusted);
            }
            return count;
        }
        if (this.inventoryCache.containsKey(invItem)) {
            return this.inventoryCache.get(invItem);
        }
        return 0;
    }

    public Set<MillVillager> getKnownVillagers() {
        return this.villagers;
    }

    public Set<Point> getKnownVillages() {
        return this.relations.keySet();
    }

    public BuildingLocation getLocationAtCoord(Point p) {
        return this.getLocationAtCoordWithTolerance(p, 0);
    }

    public BuildingLocation getLocationAtCoordPlanar(Point p) {
        for (ConstructionIP cip : this.getConstructionsInProgress()) {
            if (cip.getBuildingLocation() == null || !cip.getBuildingLocation().isInsidePlanar(p)) continue;
            return cip.getBuildingLocation();
        }
        for (BuildingLocation bl : this.getLocations()) {
            if (!bl.isInsidePlanar(p)) continue;
            return bl;
        }
        return null;
    }

    public BuildingLocation getLocationAtCoordWithTolerance(Point p, int tolerance) {
        for (ConstructionIP cip : this.getConstructionsInProgress()) {
            if (cip.getBuildingLocation() == null || !cip.getBuildingLocation().isInsideWithTolerance(p, tolerance)) continue;
            return cip.getBuildingLocation();
        }
        for (BuildingLocation bl : this.getLocations()) {
            if (bl.isSubBuildingLocation || !bl.isInsideWithTolerance(p, tolerance)) continue;
            return bl;
        }
        return null;
    }

    public List<BuildingLocation> getLocations() {
        ArrayList<BuildingLocation> locations = new ArrayList<BuildingLocation>();
        for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
            if (!this.buildingProjects.containsKey((Object)ep)) continue;
            List projectsLevel = this.buildingProjects.get((Object)ep);
            for (BuildingProject project : projectsLevel) {
                if (project.location == null) continue;
                locations.add(project.location);
            }
        }
        return locations;
    }

    public MarvelManager getMarvelManager() {
        return this.marvelManager;
    }

    public String getNativeBuildingName() {
        return this.location.getNativeName();
    }

    public int getNbProjects() {
        int nb = 0;
        for (List list : this.buildingProjects.values()) {
            nb += list.size();
        }
        return nb;
    }

    public void getNeededImportGoods(HashMap<TradeGood, Integer> neededGoods) {
        for (TradeGood good : this.culture.goodsList) {
            int nbneeded = this.nbGoodNeeded(good.item.getItem(), good.item.meta);
            if (nbneeded <= 0) continue;
            if (MillConfigValues.LogMerchant >= 3) {
                MillLog.debug(this, "Import needed: " + good.getName() + " - " + nbneeded);
            }
            if (neededGoods.containsKey(good)) {
                neededGoods.put(good, neededGoods.get(good) + nbneeded);
                continue;
            }
            neededGoods.put(good, nbneeded);
        }
    }

    public int getNewGender() {
        int nbmales = 0;
        int nbfemales = 0;
        for (VillagerRecord vr : this.getVillagerRecords().values()) {
            if (vr.gender == 1) {
                ++nbmales;
                continue;
            }
            ++nbfemales;
        }
        int maleChance = 3 + nbfemales - nbmales;
        return MillCommonUtilities.randomInt(6) < maleChance ? 1 : 2;
    }

    public PanelManager getPanelManager() {
        if (this.panelManager == null) {
            this.panelManager = new PanelManager(this);
        }
        return this.panelManager;
    }

    public Building getParentBuilding() {
        if (!this.location.isSubBuildingLocation) {
            return this;
        }
        Optional<BuildingLocation> parentLocation = this.getTownHall().getLocations().stream().filter(locationTested -> !locationTested.isSubBuildingLocation && locationTested.pos.equals(this.location.pos)).findFirst();
        if (parentLocation.isPresent()) {
            return parentLocation.get().getBuilding(this.world);
        }
        MillLog.error(this, "Can't find parent building. Returning itself instead.");
        return this;
    }

    public Point getPos() {
        return this.pos;
    }

    public String getQualifier() {
        return this.qualifier;
    }

    public Map<Point, Integer> getRelations() {
        return this.relations;
    }

    public int getRelationWithVillage(Point p) {
        if (this.relations.containsKey(p)) {
            return this.relations.get(p);
        }
        return 0;
    }

    public int getReputation(EntityPlayer player) {
        return this.mw.getProfile(player).getReputation(this);
    }

    public String getReputationLevelDesc(EntityPlayer player) {
        return this.culture.getReputationLevelDesc(this.getReputation(player));
    }

    public String getReputationLevelLabel(EntityPlayer player) {
        return this.culture.getReputationLevelLabel(this.getReputation(player));
    }

    public ResManager getResManager() {
        return this.resManager;
    }

    public Set<TradeGood> getSellingGoods(EntityPlayer player) {
        if (!this.shopSells.containsKey(player.func_70005_c_())) {
            MillLog.error(this, "No selling data from player " + player.func_70005_c_() + ", only has data for " + this.shopSells.keySet().toArray().toString());
            return null;
        }
        return this.shopSells.get(player.func_70005_c_()).keySet();
    }

    public int getSellingPrice(TradeGood g, EntityPlayer player) {
        if (player == null || !this.shopSells.containsKey(player.func_70005_c_())) {
            return 0;
        }
        return this.shopSells.get(player.func_70005_c_()).get(g);
    }

    public List<Building> getShops() {
        ArrayList<Building> shops = new ArrayList<Building>();
        for (Point p : this.buildings) {
            Building building = this.mw.getBuilding(p);
            if (building == null || building.location == null || building.location.shop == null || building.location.shop.length() <= 0) continue;
            shops.add(building);
        }
        return shops;
    }

    private int getSimultaneousConstructionSlots() {
        if (this.villageType == null) {
            return 1;
        }
        int nb = this.villageType.maxSimultaneousConstructions;
        for (BuildingLocation bl : this.getLocations()) {
            if (bl.getPlan() == null) continue;
            nb += bl.getPlan().extraSimultaneousConstructions;
        }
        return nb;
    }

    private int getSimultaneousWallConstructionSlots() {
        if (this.villageType == null) {
            return 1;
        }
        int nb = this.villageType.maxSimultaneousWallConstructions;
        for (BuildingLocation bl : this.getLocations()) {
            if (bl.getPlan() == null) continue;
            nb += bl.getPlan().extraSimultaneousWallConstructions;
        }
        return nb;
    }

    public Set<String> getTags() {
        return this.tags.stream().sorted().collect(Collectors.toCollection(TreeSet::new));
    }

    public Building getTownHall() {
        if (this.getTownHallPos() == null) {
            return null;
        }
        return this.mw.getBuilding(this.getTownHallPos());
    }

    public Point getTownHallPos() {
        return this.townHallPos;
    }

    public int getVillageAttackerStrength() {
        int strength = 0;
        for (VillagerRecord vr : this.getVillagerRecords().values()) {
            if (!vr.raidingVillage || vr.killed) continue;
            strength += vr.getMilitaryStrength();
        }
        return strength;
    }

    public int getVillageDefendingStrength() {
        int strength = 0;
        for (VillagerRecord vr : this.getVillagerRecords().values()) {
            if (vr.getType() == null || !vr.getType().helpInAttacks || vr.killed || vr.raidingVillage) continue;
            strength += vr.getMilitaryStrength();
        }
        return strength;
    }

    public int getVillageIrrigation() {
        int irrigation = 0;
        for (BuildingLocation bl : this.getLocations()) {
            if (bl.getPlan() == null) continue;
            irrigation += bl.getPlan().irrigation;
        }
        return irrigation;
    }

    public String getVillageNameWithoutQualifier() {
        if (this.name == null || this.name.length() == 0) {
            if (this.villageType != null) {
                return this.villageType.name;
            }
            return this.getNativeBuildingName();
        }
        return this.name;
    }

    public String getVillageQualifiedName() {
        if (this.name == null || this.name.length() == 0) {
            if (this.villageType != null) {
                return this.villageType.name;
            }
            return this.getNativeBuildingName();
        }
        if (this.getQualifier() == null || this.getQualifier().length() == 0) {
            return this.name;
        }
        return this.name + this.culture.qualifierSeparator.replaceAll("_", " ") + this.getQualifier();
    }

    public int getVillageRaidingStrength() {
        int strength = 0;
        for (VillagerRecord vr : this.getVillagerRecords().values()) {
            if (vr.getType() == null || !vr.getType().isRaider || vr.killed || vr.raidingVillage) continue;
            strength += vr.getMilitaryStrength();
        }
        return strength;
    }

    public VillagerRecord getVillagerRecordById(long id) {
        return this.getVillagerRecords().get(id);
    }

    public Map<Long, VillagerRecord> getVillagerRecords() {
        return this.vrecords;
    }

    public VisitorManager getVisitorManager() {
        if (this.visitorManager == null) {
            this.visitorManager = new VisitorManager(this);
        }
        return this.visitorManager;
    }

    public int getWoodCount() {
        if (!this.containsTags("grove")) {
            return 0;
        }
        int nb = 0;
        for (int i = this.location.minx - 3; i < this.location.maxx + 3; ++i) {
            for (int j = this.location.pos.getiY() - 1; j < this.location.pos.getiY() + 10; ++j) {
                for (int k = this.location.minz - 3; k < this.location.maxz + 3; ++k) {
                    if (WorldUtilities.getBlock(this.world, i, j, k) != Blocks.field_150364_r && WorldUtilities.getBlock(this.world, i, j, k) != Blocks.field_150363_s) continue;
                    ++nb;
                }
            }
        }
        return nb;
    }

    public Point getWoodLocation() {
        if (!this.containsTags("grove")) {
            return null;
        }
        for (int xPos = this.location.minx - 3; xPos < this.location.maxx + 3; ++xPos) {
            for (int yPos = this.location.miny - 1; yPos < this.location.maxy + 20; ++yPos) {
                for (int zPos = this.location.minz - 3; zPos < this.location.maxz + 3; ++zPos) {
                    Block block = WorldUtilities.getBlock(this.world, xPos, yPos, zPos);
                    if (block != Blocks.field_150364_r && block != Blocks.field_150363_s) continue;
                    Point p = new Point(xPos, yPos, zPos);
                    return p;
                }
            }
        }
        return null;
    }

    public void growTree(World world, int x, int y, int z, Random random) {
        BlockPos bp = new BlockPos(x, y, z);
        IBlockState saplingBlockState = WorldUtilities.getBlockState(world, x, y, z);
        if (saplingBlockState.func_177230_c() != Blocks.field_150345_g) {
            return;
        }
        BlockPlanks.EnumType saplingType = (BlockPlanks.EnumType)saplingBlockState.func_177229_b((IProperty)BlockSapling.field_176480_a);
        if (saplingType == BlockPlanks.EnumType.DARK_OAK) {
            if (!MillCommonUtilities.chanceOn(5)) {
                return;
            }
            WorldGenCanopyTree treeGenerator = null;
            if (WorldUtilities.getBlockState(world, x + 1, y, z).func_177230_c() != Blocks.field_150345_g || WorldUtilities.getBlockState(world, x, y, z + 1).func_177230_c() != Blocks.field_150345_g || WorldUtilities.getBlockState(world, x + 1, y, z + 1).func_177230_c() != Blocks.field_150345_g) {
                return;
            }
            WorldUtilities.setBlockAndMetadata(world, x, y, z, Blocks.field_150350_a, 0, true, false);
            WorldUtilities.setBlockAndMetadata(world, x + 1, y, z, Blocks.field_150350_a, 0, true, false);
            WorldUtilities.setBlockAndMetadata(world, x, y, z + 1, Blocks.field_150350_a, 0, true, false);
            WorldUtilities.setBlockAndMetadata(world, x + 1, y, z + 1, Blocks.field_150350_a, 0, true, false);
            treeGenerator = new WorldGenCanopyTree(true);
            boolean success = treeGenerator.func_180709_b(world, random, bp);
            if (!success) {
                WorldUtilities.setBlockstate(world, new Point(x, y, z), saplingBlockState, true, false);
                WorldUtilities.setBlockstate(world, new Point(x + 1, y, z), saplingBlockState, true, false);
                WorldUtilities.setBlockstate(world, new Point(x, y, z + 1), saplingBlockState, true, false);
                WorldUtilities.setBlockstate(world, new Point(x + 1, y, z + 1), saplingBlockState, true, false);
            }
        } else {
            WorldGenTrees treeGenerator = null;
            if (saplingType == BlockPlanks.EnumType.OAK) {
                treeGenerator = new WorldGenTrees(true);
            } else if (saplingType == BlockPlanks.EnumType.SPRUCE) {
                treeGenerator = new WorldGenTaiga2(true);
            } else if (saplingType == BlockPlanks.EnumType.BIRCH) {
                treeGenerator = new WorldGenBirchTree(true, false);
            } else if (saplingType == BlockPlanks.EnumType.JUNGLE) {
                IBlockState iblockstate = Blocks.field_150364_r.func_176223_P().func_177226_a((IProperty)BlockOldLog.field_176301_b, (Comparable)BlockPlanks.EnumType.JUNGLE);
                IBlockState iblockstate1 = Blocks.field_150362_t.func_176223_P().func_177226_a((IProperty)BlockOldLeaf.field_176239_P, (Comparable)BlockPlanks.EnumType.JUNGLE).func_177226_a((IProperty)BlockLeaves.field_176236_b, (Comparable)Boolean.valueOf(false));
                treeGenerator = new WorldGenTrees(true, 4, iblockstate, iblockstate1, false);
            } else if (saplingType == BlockPlanks.EnumType.ACACIA) {
                treeGenerator = new WorldGenSavannaTree(true);
            } else {
                MillLog.error(this, "Tried forcing a sapling to grow but its type is not recognised: " + saplingType);
            }
            if (treeGenerator != null) {
                WorldUtilities.setBlockAndMetadata(world, x, y, z, Blocks.field_150350_a, 0, true, false);
                boolean success = treeGenerator.func_180709_b(world, random, bp);
                if (!success) {
                    WorldUtilities.setBlockstate(world, new Point(x, y, z), saplingBlockState, true, false);
                }
            }
        }
    }

    private void handlePathingResult() {
        if (this.pathQueue != null) {
            Collections.reverse(this.pathQueue.pathsReceived);
            Collections.reverse(this.pathQueue.pathCreators);
            this.pathsToBuild = new ArrayList<List<BuildingBlock>>();
            for (int i = 0; i < this.pathQueue.pathsReceived.size(); ++i) {
                if (this.pathQueue.pathsReceived.get(i) == null) continue;
                this.pathsToBuild.add(PathUtilities.buildPath(this, (List)this.pathQueue.pathsReceived.get(i), ((PathCreator)((PathCreatorQueue)this.pathQueue).pathCreators.get((int)i)).pathConstructionGood.block, ((PathCreator)((PathCreatorQueue)this.pathQueue).pathCreators.get((int)i)).pathConstructionGood.meta, ((PathCreator)((PathCreatorQueue)this.pathQueue).pathCreators.get((int)i)).pathWidth));
            }
            this.pathsToBuildIndex = 0;
            this.pathsToBuildPathIndex = 0;
            this.calculatePathsToClear();
            this.pathsChanged = true;
            this.pathQueue = null;
        }
    }

    public void initialise(EntityPlayer owner, boolean villageCreation) {
        if (MillConfigValues.LogWorldGeneration >= 1) {
            MillLog.major(this, "Initialising building at " + this.getPos() + ", TH pos: " + this.getTownHallPos() + ", TH: " + this.getTownHall());
        }
        if (this.isHouse()) {
            try {
                this.initialiseHouse(villageCreation);
            }
            catch (Exception e) {
                MillLog.printException("Error when trying to create a building: " + this.name, e);
            }
            this.getPanelManager().updateSigns();
        }
        if (this.isTownhall) {
            this.initialiseTownHall(owner);
        } else {
            this.chestLocked = this.getTownHall().chestLocked;
            if (!this.chestLocked) {
                this.unlockChests();
            }
        }
        if (villageCreation && this.resManager.spawns.size() > 0) {
            this.updatePens(true);
        }
    }

    public void initialiseBuildingProjects() {
        if (this.villageType == null) {
            MillLog.error(this, "villageType is null!");
            return;
        }
        this.buildingProjects = this.villageType.getBuildingProjects();
    }

    public void initialiseConstruction(ConstructionIP cip, Point refPos) throws MillLog.MillenaireException {
        boolean isTownHall = false;
        if (cip.getBuildingLocation().equals(this.location)) {
            isTownHall = true;
        }
        if (cip.getBuildingLocation().level != 0) {
            MillLog.printException(new MillLog.MillenaireException("Trying to call initialiseConstruction on a location with non-0 level: " + cip.getBuildingLocation()));
            return;
        }
        Building building = new Building(this.mw, this.culture, this.villageType, cip.getBuildingLocation(), isTownHall, false, this.getPos());
        BuildingPlan plan = this.getBuildingPlanForConstruction(cip);
        plan.updateBuildingForPlan(building);
        building.initialise(null, false);
        this.registerBuildingEntity(building);
        if (MillConfigValues.LogBuildingPlan >= 1) {
            MillLog.major(this, "Created new Building Entity: " + plan.planName + " at " + refPos);
        }
        this.completeConstruction(cip);
    }

    private void initialiseHouse(boolean villageCreation) throws MillLog.MillenaireException {
        if (villageCreation) {
            this.createResidents();
        }
    }

    public void initialiseRelations(Point parentVillage) {
        if (this.villageType.lonebuilding) {
            return;
        }
        this.parentVillage = parentVillage;
        for (Point p : this.mw.villagesList.pos) {
            Building distantVillage;
            if (this.pos.sameBlock(p) || !(this.pos.distanceToSquared(p) < (double)(MillConfigValues.BackgroundRadius * MillConfigValues.BackgroundRadius)) || (distantVillage = this.mw.getBuilding(p)) == null) continue;
            if (parentVillage != null && (p.sameBlock(parentVillage) || parentVillage.sameBlock(distantVillage.parentVillage))) {
                this.adjustRelation(p, 100, true);
                continue;
            }
            if (this.villageType.playerControlled && this.controlledBy.equals(distantVillage.controlledBy)) {
                this.adjustRelation(p, 100, true);
                continue;
            }
            if (distantVillage.culture == this.culture) {
                this.adjustRelation(p, 50, true);
                continue;
            }
            this.adjustRelation(p, -30, true);
        }
    }

    public void initialiseTownHall(EntityPlayer controller) {
        if (this.name == null) {
            this.findName(null);
        }
        if (MillConfigValues.LogWorldGeneration >= 1) {
            MillLog.major(this, "Initialising town hall: " + this.getVillageQualifiedName());
        }
        this.buildings.add(this.getPos());
        if (this.villageType.playerControlled && controller != null) {
            UserProfile profile = this.mw.getProfile(controller);
            this.controlledBy = profile.uuid;
            profile.adjustReputation(this, 131072);
        }
    }

    public void initialiseVillage() {
        boolean noMenLeft = true;
        for (VillagerRecord vr : this.getVillagerRecords().values()) {
            if (vr.gender != 1 || vr.getType().isChild) continue;
            noMenLeft = false;
        }
        for (Point p : this.buildings) {
            Building b = this.mw.getBuilding(p);
            if (b == null) continue;
            if (noMenLeft) {
                b.unlockChests();
                continue;
            }
            b.lockChests();
        }
        this.choseAndApplyBrickTheme();
        this.recalculatePaths(true);
    }

    public void invalidateInventoryCache() {
        this.inventoryCache = null;
    }

    public boolean isDisplayableProject(BuildingProject project) {
        return !(project.getPlan((int)0, (int)0).requiredGlobalTag != null ? !this.mw.isGlobalTagSet(project.getPlan((int)0, (int)0).requiredGlobalTag) : project.getPlan((int)0, (int)0).isgift && !MillConfigValues.bonusEnabled);
    }

    public boolean isHouse() {
        return this.location != null && (this.location.getMaleResidents().size() > 0 || this.location.getFemaleResidents().size() > 0);
    }

    public boolean isPointProtectedFromPathBuilding(Point p) {
        Point above = p.getAbove();
        Point below = p.getBelow();
        for (Building b : this.getBuildings()) {
            if (b.location == null || !b.location.isInsidePlanar(p)) continue;
            if (b.containsTags("nopaths")) {
                return true;
            }
            if (b.resManager.soils != null) {
                for (List list : b.resManager.soils) {
                    if (!list.contains(p) && !list.contains(above) && !list.contains(below)) continue;
                    return true;
                }
            }
            if (b.resManager.sources == null) continue;
            for (List list : b.resManager.sources) {
                if (!list.contains(p) && !list.contains(above) && !list.contains(below)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean isReachableFromRegion(short regionId) {
        if (this.getTownHall().regionMapper == null) {
            return true;
        }
        if (this.getTownHall().regionMapper.regions[this.resManager.getSleepingPos().getiX() - this.getTownHall().winfo.mapStartX][this.resManager.getSleepingPos().getiZ() - this.getTownHall().winfo.mapStartZ] != regionId) {
            return false;
        }
        if (this.getTownHall().regionMapper.regions[this.resManager.getSellingPos().getiX() - this.getTownHall().winfo.mapStartX][this.resManager.getSellingPos().getiZ() - this.getTownHall().winfo.mapStartZ] != regionId) {
            return false;
        }
        if (this.getTownHall().regionMapper.regions[this.resManager.getDefendingPos().getiX() - this.getTownHall().winfo.mapStartX][this.resManager.getDefendingPos().getiZ() - this.getTownHall().winfo.mapStartZ] != regionId) {
            return false;
        }
        return this.getTownHall().regionMapper.regions[this.resManager.getShelterPos().getiX() - this.getTownHall().winfo.mapStartX][this.resManager.getShelterPos().getiZ() - this.getTownHall().winfo.mapStartZ] == regionId;
    }

    public boolean isValidProject(BuildingProject project) {
        BuildingPlan plan = project.getNextBuildingPlan(false);
        if (plan == null) {
            MillLog.error(this, "Building project " + project + " has no building plan.");
            return false;
        }
        if (!(this.villageType.playerControlled || plan.price <= 0 && !plan.isgift || this.buildingsBought.contains(project.key))) {
            return false;
        }
        return this.checkProjectValidity(project, plan);
    }

    public boolean isValidUpgrade(BuildingProject project) {
        if (project.location == null) {
            return false;
        }
        if (project.getPlan(project.location.getVariation(), project.location.level + 1) == null) {
            return false;
        }
        if (project.getPlan((int)project.location.getVariation(), (int)(project.location.level + 1)).version != project.location.version) {
            return false;
        }
        return this.checkProjectValidity(project, project.getPlan(project.location.getVariation(), project.location.level + 1));
    }

    private boolean isVillageChunksLoaded() {
        if (this.world.field_72995_K) {
            MillLog.printException("Trying to check chunk status client side", new Exception());
            return false;
        }
        ChunkProviderServer chunkProvider = ((WorldServer)this.world).func_72863_F();
        for (int x = this.winfo.mapStartX; x < this.winfo.mapStartX + this.winfo.width; x += 16) {
            for (int z = this.winfo.mapStartZ; z < this.winfo.mapStartZ + this.winfo.length; z += 16) {
                if (chunkProvider.func_73149_a(x >> 4, z >> 4)) continue;
                return false;
            }
        }
        return true;
    }

    private void killMobs() {
        if (this.winfo == null) {
            return;
        }
        Point start = new Point(this.location.pos.x - (double)this.villageType.radius, this.location.pos.getiY() - 20, this.location.pos.z - (double)this.villageType.radius);
        Point end = new Point(this.location.pos.x + (double)this.villageType.radius, this.location.pos.getiY() + 50, this.location.pos.z + (double)this.villageType.radius);
        if (this.containsTags("despawnallmobs")) {
            List<Entity> mobs = WorldUtilities.getEntitiesWithinAABB(this.world, EntityMob.class, start, end);
            for (Entity ent : mobs) {
                if (ent.field_70128_L) continue;
                if (MillConfigValues.LogTileEntityBuilding >= 3) {
                    MillLog.debug(this, "Killing mob " + ent + " at " + ent.field_70165_t + "/" + ent.field_70163_u + "/" + ent.field_70161_v);
                }
                ent.func_70106_y();
            }
        } else {
            List<Entity> creepers = WorldUtilities.getEntitiesWithinAABB(this.world, EntityCreeper.class, start, end);
            for (Entity ent : creepers) {
                if (ent.field_70128_L) continue;
                if (MillConfigValues.LogTileEntityBuilding >= 3) {
                    MillLog.debug(this, "Killing creeper " + ent + " at " + ent.field_70165_t + "/" + ent.field_70163_u + "/" + ent.field_70161_v);
                }
                ent.func_70106_y();
            }
            List<Entity> endermen = WorldUtilities.getEntitiesWithinAABB(this.world, EntityEnderman.class, start, end);
            for (Entity ent : endermen) {
                if (ent.field_70128_L) continue;
                if (MillConfigValues.LogTileEntityBuilding >= 3) {
                    MillLog.debug(this, "Killing enderman " + ent + " at " + ent.field_70165_t + "/" + ent.field_70163_u + "/" + ent.field_70161_v);
                }
                ent.func_70106_y();
            }
        }
    }

    private void loadChunks() {
        if (this.winfo != null && this.winfo.width > 0) {
            if (this.chunkLoader == null) {
                this.chunkLoader = new BuildingChunkLoader(this);
            }
            if (!this.chunkLoader.chunksLoaded) {
                this.chunkLoader.loadChunks();
            }
        }
    }

    public void lockAllBuildingsChests() {
        for (Point p : this.buildings) {
            Building b = this.mw.getBuilding(p);
            if (b == null) continue;
            b.lockChests();
        }
        this.saveNeeded = true;
        this.saveReason = "Locking chests";
    }

    public void lockChests() {
        this.chestLocked = true;
        for (Point p : this.resManager.chests) {
            TileEntityLockedChest chest = p.getMillChest(this.world);
            if (chest == null) continue;
            chest.buildingPos = this.getPos();
        }
    }

    public boolean lockedForPlayer(EntityPlayer player) {
        if (!this.chestLocked) {
            return false;
        }
        return !this.controlledBy(player);
    }

    private void merchantCreated(VillagerRecord villagerRecord) {
        if (MillConfigValues.LogMerchant >= 2) {
            MillLog.minor(this, "Creating a new merchant");
        }
        this.merchantRecord = villagerRecord;
        this.visitorsList.add("panels.startedtrading;" + this.merchantRecord.getName() + ";" + this.merchantRecord.getNativeOccupationName());
    }

    private void moveMerchant(Building destInn) {
        HashMap<InvItem, Integer> contents = this.resManager.getChestsContent();
        for (InvItem key : contents.keySet()) {
            int nb = this.takeGoods(key.getItem(), key.meta, 9999999);
            destInn.storeGoods(key.getItem(), key.meta, nb);
            destInn.addToImports(key, nb);
            this.addToExports(key, nb);
        }
        this.transferVillagerPermanently(this.merchantRecord, destInn);
        this.visitorsList.add("panels.merchantmovedout;" + this.merchantRecord.getName() + ";" + this.merchantRecord.getNativeOccupationName() + ";" + destInn.getTownHall().getVillageQualifiedName() + ";" + this.nbNightsMerchant);
        destInn.visitorsList.add("panels.merchantarrived;" + this.merchantRecord.getName() + ";" + this.merchantRecord.getNativeOccupationName() + ";" + this.getTownHall().getVillageQualifiedName());
        if (MillConfigValues.LogMerchant >= 1) {
            MillLog.major(this, "Moved merchant " + this.merchantRecord + " to " + destInn.getTownHall());
        }
        destInn.merchantRecord = this.merchantRecord;
        this.merchantRecord = null;
        this.nbNightsMerchant = 0;
    }

    public int nbGoodAvailable(IBlockState bs, boolean forConstruction, boolean forExport, boolean forShop) {
        return this.nbGoodAvailable(InvItem.createInvItem(bs), forConstruction, forExport, forShop);
    }

    public int nbGoodAvailable(InvItem ii, boolean forConstruction, boolean forExport, boolean forShop) {
        if (this.resManager.chests.isEmpty()) {
            return 0;
        }
        if (forShop && this.culture.shopNeeds.containsKey(this.location.shop)) {
            for (InvItem item : this.culture.shopNeeds.get(this.location.shop)) {
                if (!item.matches(ii)) continue;
                return 0;
            }
        }
        int nb = this.countGoods(ii.getItem(), ii.meta);
        if (this.isTownhall) {
            boolean projectHandled = false;
            BuildingPlan project = this.getCurrentGoalBuildingPlan();
            for (ConstructionIP cip : this.getConstructionsInProgress()) {
                if (cip.getBuildingLocation() == null) continue;
                BuildingPlan plan = cip.getBuildingLocation().getPlan();
                if (plan != null) {
                    for (InvItem key : plan.resCost.keySet()) {
                        int builderHas;
                        if (!key.matches(ii) || (builderHas = cip.getBuilder() != null ? cip.getBuilder().countInv(key) : 0) >= plan.resCost.get(key)) continue;
                        nb -= plan.resCost.get(key) - builderHas;
                    }
                }
                if (project != plan) continue;
                projectHandled = true;
            }
            if (!projectHandled && project != null) {
                for (InvItem key : project.resCost.keySet()) {
                    if (!key.matches(ii)) continue;
                    nb -= project.resCost.get(key).intValue();
                }
            }
        }
        boolean tradedHere = false;
        if (this.location.shop != null && this.culture.shopSells.containsKey(this.location.shop)) {
            for (TradeGood g : this.culture.shopSells.get(this.location.shop)) {
                if (!g.item.matches(ii)) continue;
                tradedHere = true;
            }
        }
        if (!forConstruction && (this.isTownhall || tradedHere || forExport)) {
            for (InvItem key : this.culture.getInvItemsWithTradeGoods()) {
                TradeGood good;
                if (!key.matches(ii) || this.culture.getTradeGood(key) == null || (good = this.culture.getTradeGood(key)) == null) continue;
                if (forExport) {
                    nb -= good.targetQuantity;
                    continue;
                }
                nb -= good.reservedQuantity;
            }
        }
        if (!forConstruction) {
            for (VillagerRecord vr : this.getVillagerRecords().values()) {
                if (vr.getHousePos() == null || !vr.getHousePos().equals(this.getPos()) || vr.getType() == null) continue;
                for (InvItem requiredItem : vr.getType().requiredFoodAndGoods.keySet()) {
                    if (!ii.matches(requiredItem)) continue;
                    nb -= vr.getType().requiredFoodAndGoods.get(requiredItem).intValue();
                }
            }
        }
        return Math.max(nb, 0);
    }

    public int nbGoodAvailable(Item item, int meta, boolean forConstruction, boolean forExport, boolean forShop) {
        return this.nbGoodAvailable(InvItem.createInvItem(item, meta), forConstruction, forExport, forShop);
    }

    public int nbGoodNeeded(Item item, int meta) {
        int needed;
        TradeGood good;
        int nb = this.countGoods(item, meta);
        for (ConstructionIP cip : this.getConstructionsInProgress()) {
            if (cip.getBuilder() == null || cip.getBuildingLocation() == null || !cip.getBuildingLocation().planKey.equals(this.buildingGoal)) continue;
            nb += cip.getBuilder().countInv(item, meta);
        }
        int targetAmount = 0;
        InvItem invitem = InvItem.createInvItem(item, meta);
        if (meta == -1) {
            for (int i = 0; i < 16; ++i) {
                TradeGood good2;
                if (this.culture.getTradeGood(invitem) == null || (good2 = this.culture.getTradeGood(InvItem.createInvItem(item, i))) == null) continue;
                targetAmount += good2.targetQuantity;
            }
        } else if (this.culture.getTradeGood(invitem) != null && (good = this.culture.getTradeGood(invitem)) != null) {
            targetAmount = good.targetQuantity;
        }
        BuildingPlan project = this.getCurrentGoalBuildingPlan();
        int neededForProject = 0;
        if (project != null) {
            for (InvItem key : project.resCost.keySet()) {
                if (key.getItem() != item || key.meta != meta && meta != -1 && key.meta != -1) continue;
                neededForProject += project.resCost.get(key).intValue();
            }
        }
        if ((needed = Math.max(neededForProject + targetAmount - nb, 0)) == 0) {
            return 0;
        }
        if (MillConfigValues.LogMerchant >= 3) {
            MillLog.debug(this, "Goods needed: " + invitem.getName() + ": " + targetAmount + "/" + neededForProject + "/" + nb);
        }
        return needed;
    }

    public void planRaid(Building target) {
        this.raidPlanningStart = this.world.func_72820_D();
        this.raidStart = 0L;
        this.raidTarget = target.getPos();
        if (MillConfigValues.LogDiplomacy >= 1) {
            MillLog.major(this, "raidTarget set: " + this.raidTarget + " name: " + target.name);
        }
        this.saveNeeded = true;
        this.saveReason = "Raid planned";
        ServerSender.sendTranslatedSentenceInRange(this.world, this.getPos(), MillConfigValues.BackgroundRadius, '4', "raid.planningstarted", this.getVillageQualifiedName(), target.getVillageQualifiedName());
    }

    public boolean readFromNBT(NBTTagCompound nbttagcompound) {
        try {
            BuildingPlan plan;
            int i;
            NBTTagCompound nbttagcompound1;
            String version = nbttagcompound.func_74779_i("versionCompatibility");
            if (!version.equals(versionCompatibility)) {
                MillLog.error(this, "Tried to load building with incompatible version: " + version);
                return false;
            }
            if (this.pos == null) {
                this.pos = Point.read(nbttagcompound, "pos");
            }
            this.chestLocked = nbttagcompound.func_74767_n("chestLocked");
            ArrayList<String> tags = new ArrayList<String>();
            NBTTagList nbttaglist = nbttagcompound.func_150295_c("tags", 10);
            for (int i2 = 0; i2 < nbttaglist.func_74745_c(); ++i2) {
                NBTTagCompound nbttagcompound12 = nbttaglist.func_150305_b(i2);
                String value = nbttagcompound12.func_74779_i("value");
                tags.add(value);
                if (MillConfigValues.LogTags < 2) continue;
                MillLog.minor(this, "Loading tag: " + value);
            }
            this.addTags(tags, "loading from NBT");
            if (this.getTags().size() > 0 && MillConfigValues.LogTags >= 1) {
                MillLog.major(this, "Tags loaded: " + MillCommonUtilities.flattenStrings(this.getTags()));
            }
            this.location = BuildingLocation.read(nbttagcompound, "buildingLocation", "self", this);
            if (this.location == null) {
                MillLog.error(this, "No location found!");
                return false;
            }
            String cultureKey = nbttagcompound.func_74779_i("culture");
            if (cultureKey.equals("hindi")) {
                MillLog.major(this, "Converting village culture from hindi to indian.");
                cultureKey = "indian";
            }
            this.culture = Culture.getCultureByName(cultureKey);
            if (this.culture == null) {
                MillLog.error(this, "Could not load culture: " + nbttagcompound.func_74779_i("culture") + ", skipping building.");
                return false;
            }
            this.isTownhall = nbttagcompound.func_74764_b("isTownhall") ? nbttagcompound.func_74767_n("isTownhall") : this.location.planKey.equals(blTownhall);
            this.townHallPos = Point.read(nbttagcompound, "townHallPos");
            this.nightActionPerformed = nbttagcompound.func_74767_n("nightActionPerformed");
            this.nightBackgroundActionPerformed = nbttagcompound.func_74767_n("nightBackgroundActionPerformed");
            this.nbAnimalsRespawned = nbttagcompound.func_74762_e("nbAnimalsRespawned");
            if (nbttagcompound.func_74764_b("villagersrecords")) {
                nbttaglist = nbttagcompound.func_150295_c("villagersrecords", 10);
                MillLog.major(this, "Loading " + nbttaglist.func_74745_c() + " villagers from building list.");
                for (int i3 = 0; i3 < nbttaglist.func_74745_c(); ++i3) {
                    nbttagcompound1 = nbttaglist.func_150305_b(i3);
                    VillagerRecord vr = VillagerRecord.read(this.mw, nbttagcompound1, "vr");
                    if (vr == null) {
                        MillLog.error(this, "Couldn't load VR record.");
                        continue;
                    }
                    this.mw.registerVillagerRecord(vr, false);
                    if (MillConfigValues.LogHybernation < 2) continue;
                    MillLog.minor(this, "Loaded VR: " + vr);
                }
                MillLog.major(this, "Finished loading villagers from building list.");
            }
            nbttaglist = nbttagcompound.func_150295_c("visitorsList", 10);
            for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
                nbttagcompound1 = nbttaglist.func_150305_b(i);
                this.visitorsList.add(nbttagcompound1.func_74779_i("visitor"));
            }
            nbttaglist = nbttagcompound.func_150295_c("subBuildings", 10);
            for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
                nbttagcompound1 = nbttaglist.func_150305_b(i);
                Point p = Point.read(nbttagcompound1, "pos");
                if (p == null) continue;
                this.subBuildings.add(p);
            }
            if (this.containsTags("pujas") || this.containsTags("sacrifices")) {
                this.pujas = new PujaSacrifice(this, nbttagcompound.func_74775_l("pujas"));
                if (MillConfigValues.LogPujas >= 2) {
                    MillLog.minor(this, "read pujas object");
                }
            }
            this.lastGoodsRefresh = nbttagcompound.func_74763_f("lastGoodsRefresh");
            if (this.containsTags("inn") && !this.isTownhall) {
                this.isInn = true;
                this.readInn(nbttagcompound);
            }
            if (this.isInn && this.getVillagerRecords().size() > 0) {
                this.merchantRecord = this.getVillagerRecords().get(this.getVillagerRecords().keySet().iterator().next());
            }
            if (this.containsTags("autospawnvillagers")) {
                this.hasAutoSpawn = true;
            }
            if (this.containsTags("market") && !this.isTownhall) {
                this.isMarket = true;
                this.hasVisitors = true;
            }
            if (!this.location.getVisitors().isEmpty()) {
                this.hasVisitors = true;
            }
            if (this.isTownhall) {
                if (MillConfigValues.LogHybernation >= 1) {
                    MillLog.major(this, "Loading Townhall data.");
                }
                this.readTownHall(nbttagcompound);
            }
            this.resManager.readFromNBT(nbttagcompound);
            if (this.isTownhall && this.villageType.isMarvel()) {
                this.marvelManager = new MarvelManager(this);
                this.marvelManager.readFromNBT(nbttagcompound);
            }
            if ((plan = this.location.getPlan()) != null) {
                if (plan.shop != null && this.location.shop == null) {
                    this.location.shop = plan.shop;
                }
                plan.updateTags(this);
            }
            if (MillConfigValues.LogTileEntityBuilding >= 3) {
                MillLog.debug(this, "Loading building. Type: " + this.location + ", pos: " + this.getPos());
            }
            return true;
        }
        catch (Exception e) {
            Mill.proxy.sendChatAdmin("Error when trying to load building. Check millenaire.log.");
            MillLog.error(this, "Error when trying to load building of type: " + this.location);
            MillLog.printException(e);
            return false;
        }
    }

    public void readInn(NBTTagCompound nbttagcompound) throws MillLog.MillenaireException {
        InvItem good;
        NBTTagCompound tag;
        int i;
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("importedGoods", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            tag = nbttaglist.func_150305_b(i);
            good = InvItem.createInvItem(Item.func_150899_d((int)tag.func_74762_e("itemid")), tag.func_74762_e("itemmeta"));
            this.imported.put(good, tag.func_74762_e("quantity"));
        }
        nbttaglist = nbttagcompound.func_150295_c("exportedGoods", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            tag = nbttaglist.func_150305_b(i);
            good = InvItem.createInvItem(Item.func_150899_d((int)tag.func_74762_e("itemid")), tag.func_74762_e("itemmeta"));
            this.exported.put(good, tag.func_74762_e("quantity"));
        }
        nbttaglist = nbttagcompound.func_150295_c("importedGoodsNew", 10);
        MillCommonUtilities.readInventory(nbttaglist, this.imported);
        nbttaglist = nbttagcompound.func_150295_c("exportedGoodsNew", 10);
        MillCommonUtilities.readInventory(nbttaglist, this.exported);
    }

    private void readPaths() {
        int i;
        int size;
        DataInputStream ds;
        FileInputStream fis;
        File buildingsDir = MillCommonUtilities.getBuildingsDir(this.world);
        File file1 = new File(buildingsDir, this.getPos().getPathString() + "_paths.bin");
        if (file1.exists()) {
            try {
                fis = new FileInputStream(file1);
                ds = new DataInputStream(fis);
                size = ds.readInt();
                this.pathsToBuild = new ArrayList<List<BuildingBlock>>();
                for (i = 0; i < size; ++i) {
                    ArrayList<BuildingBlock> path = new ArrayList<BuildingBlock>();
                    int sizePath = ds.readInt();
                    for (int j = 0; j < sizePath; ++j) {
                        Point p = new Point(ds.readInt(), ds.readShort(), ds.readInt());
                        BuildingBlock b = new BuildingBlock(p, ds);
                        path.add(b);
                    }
                    this.pathsToBuild.add(path);
                }
                ds.close();
            }
            catch (Exception e) {
                MillLog.printException("Error when reading pathsToBuild: ", e);
            }
        }
        if ((file1 = new File(buildingsDir, this.getPos().getPathString() + "_pathstoclear.bin")).exists()) {
            try {
                fis = new FileInputStream(file1);
                ds = new DataInputStream(fis);
                size = ds.readInt();
                this.oldPathPointsToClear = new ArrayList<Point>();
                for (i = 0; i < size; ++i) {
                    Point p = new Point(ds.readInt(), ds.readShort(), ds.readInt());
                    this.oldPathPointsToClear.add(p);
                }
                ds.close();
            }
            catch (Exception e) {
                MillLog.printException("Error when reading oldPathPointsToClear: ", e);
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    public void readTownHall(NBTTagCompound nbttagcompound) {
        void var5_23;
        void var5_21;
        NBTTagCompound nbttagcompound1;
        void var5_17;
        void var5_15;
        BuildingPlanSet planSet;
        int i;
        this.name = nbttagcompound.func_74779_i("name");
        this.qualifier = nbttagcompound.func_74779_i("qualifier");
        String vtype = nbttagcompound.func_74779_i("villageType");
        this.villageType = vtype.length() == 0 ? this.culture.getRandomVillage() : (this.culture.getVillageType(vtype) != null ? this.culture.getVillageType(vtype) : (this.culture.getLoneBuildingType(vtype) != null ? this.culture.getLoneBuildingType(vtype) : this.culture.getRandomVillage()));
        if (nbttagcompound.func_74779_i("controlledBy").length() > 0) {
            String controlledByName = nbttagcompound.func_74779_i("controlledBy");
            GameProfile profile = this.world.func_73046_m().func_152358_ax().func_152655_a(controlledByName);
            if (profile != null) {
                this.controlledBy = profile.getId();
                MillLog.major(this, "Converted controlledBy from name '" + controlledByName + "' to UUID " + this.controlledBy);
            } else {
                MillLog.error(this, "Could not convert controlledBy from name '" + controlledByName + "'.");
            }
        }
        if (!nbttagcompound.func_186857_a("controlledByUUID").equals(new UUID(0L, 0L))) {
            this.controlledBy = nbttagcompound.func_186857_a("controlledByUUID");
            GameProfile profile = this.mw.world.func_73046_m().func_152358_ax().func_152652_a(this.controlledBy);
            if (profile != null) {
                this.controlledByName = profile.getName();
            }
        }
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("buildings", 10);
        for (int i2 = 0; i2 < nbttaglist.func_74745_c(); ++i2) {
            NBTTagCompound nBTTagCompound = nbttaglist.func_150305_b(i2);
            Point p = Point.read(nBTTagCompound, "pos");
            if (p == null) continue;
            if (this.buildings.contains(p)) {
                MillLog.warning(this, "Trying to add a building that is already there: " + p);
                continue;
            }
            this.buildings.add(p);
        }
        this.initialiseBuildingProjects();
        nbttaglist = nbttagcompound.func_150295_c("locations", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            NBTTagCompound nBTTagCompound = nbttaglist.func_150305_b(i);
            BuildingLocation location = BuildingLocation.read(nBTTagCompound, "location", "locations", null);
            if (location == null) {
                MillLog.error(this, "Could not load building location. Skipping.");
                continue;
            }
            this.fillinBuildingLocationInProjects(location);
        }
        for (i = this.buildings.size() - 1; i >= 0; --i) {
            boolean bl = false;
            for (BuildingLocation buildingLocation : this.getLocations()) {
                if (!this.buildings.get(i).equals(buildingLocation.chestPos)) continue;
                bl = true;
            }
            if (bl) continue;
            MillLog.error(this, "Deleting building as could not find the location for it at: " + this.buildings.get(i));
            this.buildings.remove(i);
        }
        if (this.villageType.playerControlled) {
            for (List list : this.buildingProjects.values()) {
                ArrayList<BuildingProject> toDelete = new ArrayList<BuildingProject>();
                for (BuildingProject project : list) {
                    if (project.location != null) continue;
                    toDelete.add(project);
                }
                for (BuildingProject project : toDelete) {
                    list.remove(project);
                }
            }
        }
        this.buildingGoal = nbttagcompound.func_74779_i("buildingGoal");
        if (this.culture.getBuildingPlanSet(this.buildingGoal) == null) {
            this.buildingGoal = null;
            this.buildingGoalLevel = 0;
            this.buildingGoalVariation = 0;
            if (MillConfigValues.LogHybernation >= 1) {
                MillLog.major(this, "No goal found: " + this.buildingGoal);
            }
        } else {
            this.buildingGoalLevel = nbttagcompound.func_74762_e("buildingGoalLevel");
            this.buildingGoalVariation = nbttagcompound.func_74762_e("buildingGoalVariation");
            if (MillConfigValues.LogHybernation >= 1) {
                MillLog.major(this, "Reading building goal: " + this.buildingGoal);
            }
        }
        this.buildingGoalLocation = BuildingLocation.read(nbttagcompound, "buildingGoalLocation", "buildingGoalLocation", null);
        if (this.buildingGoalLocation != null && MillConfigValues.LogHybernation >= 1) {
            MillLog.major(this, "Loaded buildingGoalLocation: " + this.buildingGoalLocation);
        }
        this.buildingGoalIssue = nbttagcompound.func_74779_i("buildingGoalIssue");
        if (this.buildingGoal != null && (planSet = this.culture.getBuildingPlanSet(this.buildingGoal)) != null) {
            if (this.buildingGoalVariation >= planSet.plans.size() || this.buildingGoalLevel >= planSet.plans.get(this.buildingGoalVariation).length) {
                this.buildingGoal = null;
                this.buildingGoalLevel = 0;
                this.buildingGoalVariation = 0;
                this.buildingGoalLocation = null;
            } else if (this.buildingGoalLocation != null && this.buildingGoalLocation.version != planSet.plans.get((int)this.buildingGoalVariation)[0].version) {
                this.buildingGoal = null;
                this.buildingGoalLevel = 0;
                this.buildingGoalVariation = 0;
                this.buildingGoalLocation = null;
            }
        }
        int nbConstructions = nbttagcompound.func_74762_e("nbConstructions");
        boolean bl = false;
        while (var5_15 < nbConstructions) {
            ConstructionIP cip = new ConstructionIP(this, (int)var5_15, nbttagcompound.func_74767_n("buildingLocationIP_" + (int)var5_15 + "_isWall"));
            this.getConstructionsInProgress().add(cip);
            cip.setBuildingLocation(BuildingLocation.read(nbttagcompound, "buildingLocationIP_" + (int)var5_15, "buildingLocationIP_" + (int)var5_15, null));
            if (cip.getBuildingLocation() != null) {
                if (this.culture.getBuildingPlanSet(cip.getBuildingLocation().planKey) == null) {
                    cip.clearBuildingLocation();
                } else {
                    BuildingPlanSet buildingPlanSet = this.culture.getBuildingPlanSet(cip.getBuildingLocation().planKey);
                    if (cip.getBuildingLocation().level >= buildingPlanSet.plans.get(cip.getBuildingLocation().getVariation()).length) {
                        cip.clearBuildingLocation();
                    }
                }
                cip.readBblocks();
                cip.setBblockPos(nbttagcompound.func_74762_e("bblocksPos_" + (int)var5_15));
            }
            ++var5_15;
        }
        nbttaglist = nbttagcompound.func_150295_c("buildingsBought", 10);
        boolean bl2 = false;
        while (var5_17 < nbttaglist.func_74745_c()) {
            nbttagcompound1 = nbttaglist.func_150305_b((int)var5_17);
            this.buildingsBought.add(nbttagcompound1.func_74779_i("key"));
            ++var5_17;
        }
        this.parentVillage = Point.read(nbttagcompound, "parentVillage");
        if (nbttagcompound.func_74764_b("relations")) {
            void var5_19;
            nbttaglist = nbttagcompound.func_150295_c("relations", 10);
            boolean bl3 = false;
            while (var5_19 < nbttaglist.func_74745_c()) {
                nbttagcompound1 = nbttaglist.func_150305_b((int)var5_19);
                this.relations.put(Point.read(nbttagcompound1, "pos"), nbttagcompound1.func_74762_e("value"));
                ++var5_19;
            }
        }
        this.updateRaidPerformed = nbttagcompound.func_74767_n("updateRaidPerformed");
        this.nightBackgroundActionPerformed = nbttagcompound.func_74767_n("nightBackgroundActionPerformed");
        this.raidTarget = Point.read(nbttagcompound, "raidTarget");
        this.raidPlanningStart = nbttagcompound.func_74763_f("raidPlanningStart");
        this.raidStart = nbttagcompound.func_74763_f("raidStart");
        this.underAttack = nbttagcompound.func_74767_n("underAttack");
        nbttaglist = nbttagcompound.func_150295_c("raidsPerformed", 10);
        boolean bl4 = false;
        while (var5_21 < nbttaglist.func_74745_c()) {
            nbttagcompound1 = nbttaglist.func_150305_b((int)var5_21);
            this.raidsPerformed.add(nbttagcompound1.func_74779_i("raid"));
            ++var5_21;
        }
        nbttaglist = nbttagcompound.func_150295_c("raidsTaken", 10);
        boolean bl5 = false;
        while (var5_23 < nbttaglist.func_74745_c()) {
            nbttagcompound1 = nbttaglist.func_150305_b((int)var5_23);
            this.raidsSuffered.add(nbttagcompound1.func_74779_i("raid"));
            ++var5_23;
        }
        this.pathsToBuildIndex = nbttagcompound.func_74762_e("pathsToBuildIndex");
        this.pathsToBuildPathIndex = nbttagcompound.func_74762_e("pathsToBuildPathIndex");
        this.oldPathPointsToClearIndex = nbttagcompound.func_74762_e("oldPathPointsToClearIndex");
        String string = nbttagcompound.func_74779_i("brickColourTheme");
        if (this.villageType != null && string != null && string.length() > 0) {
            for (VillageType.BrickColourTheme brickColourTheme : this.villageType.brickColourThemes) {
                if (!brickColourTheme.key.equals(string)) continue;
                this.brickColourTheme = brickColourTheme;
            }
            if (this.brickColourTheme == null) {
                MillLog.warning(this, "Could not load brick colour theme: " + string);
            } else {
                MillLog.temp(this, "Loaded theme: " + this.brickColourTheme.key);
            }
        }
        this.readPaths();
        if (nbttagcompound.func_74764_b("bannerStack")) {
            this.bannerStack = new ItemStack(nbttagcompound.func_74775_l("bannerStack"));
        } else {
            this.generateBannerPattern();
        }
    }

    public boolean rebuildRegionMapper(boolean sync) throws MillLog.MillenaireException {
        this.updateWorldInfo();
        if (sync) {
            RegionMapper temp = new RegionMapper();
            if (temp.createConnectionsTable(this.winfo, this.resManager.getSleepingPos())) {
                this.regionMapper = temp;
                this.lastPathingUpdate = this.world.func_72820_D();
                return true;
            }
            this.regionMapper = null;
            this.lastPathingUpdate = this.world.func_72820_D();
            return false;
        }
        if (!this.rebuildingRegionMapper) {
            try {
                this.rebuildingRegionMapper = true;
                RegionMapperThread thread = new RegionMapperThread(this.winfo.clone());
                thread.setPriority(1);
                if (MillConfigValues.LogPathing >= 1) {
                    MillLog.major(this, "Thread starting.");
                }
                thread.start();
                if (MillConfigValues.LogPathing >= 1) {
                    MillLog.major(this, "Thread started.");
                }
            }
            catch (CloneNotSupportedException e) {
                MillLog.printException(e);
            }
            return true;
        }
        return true;
    }

    public void rebuildVillagerList() {
        LinkedHashSet<MillVillager> newSet = new LinkedHashSet<MillVillager>();
        for (MillVillager villager : this.mw.getAllKnownVillagers()) {
            if (villager.getHouse() != this && villager.getTownHall() != this) continue;
            newSet.add(villager);
        }
        this.villagers = newSet;
        if (MillConfigValues.LogVillagerSpawn >= 2) {
            List<Entity> nearbyVillagers = WorldUtilities.getEntitiesWithinAABB(this.world, MillVillager.class, this.pos, Math.max(this.winfo.length, this.winfo.width) / 2 + 20, 40);
            for (Entity villagerEntity : nearbyVillagers) {
                MillVillager villager = (MillVillager)villagerEntity;
                if (villager.getTownHall() != this && villager.getHouse() != this || this.villagers.contains(villager)) continue;
                MillLog.warning(this, "Found a villager nearby that isn't registered! : " + villager);
            }
        }
    }

    public void recalculatePaths(boolean autobuild) {
        if (!MillConfigValues.BuildVillagePaths) {
            return;
        }
        int nbPaths = 0;
        for (Building b : this.getBuildings()) {
            if (b == this || b.location == null || b.location.getPlan() == null || b.location.getPlan().isSubBuilding || b.resManager.getPathStartPos() == null) continue;
            ++nbPaths;
        }
        PathCreatorQueue queue = new PathCreatorQueue();
        this.autobuildPaths = autobuild;
        Point townHallPathPoint = this.resManager.getPathStartPos();
        ArrayList<Point> nodePoints = new ArrayList<Point>();
        for (Building b : this.getBuildings()) {
            if (b == this || !b.containsTags("pathnode")) continue;
            nodePoints.add(b.resManager.getPathStartPos());
        }
        if (MillConfigValues.LogVillagePaths >= 2) {
            MillLog.minor(this, "Launching path rebuild, expected paths number: " + nbPaths);
        }
        for (Building b : this.getBuildings()) {
            Point pathStartPos = b.resManager.getPathStartPos();
            if (b == this || b.location == null || b.location.getPlan() == null || b.location.getPlan().isSubBuilding || pathStartPos == null || b.location.getPlan().noPathsToBuilding) continue;
            InvItem pathMaterial = this.villageType.pathMaterial.get(0);
            if (b.location.getPlan().pathLevel < this.villageType.pathMaterial.size()) {
                pathMaterial = this.villageType.pathMaterial.get(b.location.getPlan().pathLevel);
            }
            Point pathDest = townHallPathPoint;
            if (!b.containsTags("pathnode")) {
                for (Point nodePoint : nodePoints) {
                    if (pathDest == townHallPathPoint) {
                        if (!(nodePoint.distanceTo(pathStartPos) * 1.3 < pathDest.distanceTo(pathStartPos))) continue;
                        pathDest = nodePoint;
                        continue;
                    }
                    if (!(nodePoint.distanceTo(pathStartPos) < pathDest.distanceTo(pathStartPos))) continue;
                    pathDest = nodePoint;
                }
                if (pathDest.distanceTo(pathStartPos) > 20.0) {
                    Point otherBuildingDest = null;
                    for (Building otherBuilding : this.getBuildings()) {
                        if (otherBuilding == this || otherBuilding.location == null || otherBuilding.location.getPlan() == null || otherBuilding.location.getPlan().isSubBuilding || otherBuilding.resManager.getPathStartPos() == null || otherBuilding.containsTags("pathnode") || otherBuilding.location.getPlan().noPathsToBuilding) continue;
                        Point otherBuildingPathStart = otherBuilding.resManager.getPathStartPos();
                        if (!(townHallPathPoint.distanceToSquared(pathStartPos) > townHallPathPoint.distanceToSquared(otherBuildingPathStart)) || !(otherBuildingPathStart.distanceTo(pathStartPos) * 1.5 < pathDest.distanceTo(pathStartPos)) || otherBuildingDest != null && !(pathStartPos.distanceToSquared(otherBuildingDest) > pathStartPos.distanceToSquared(otherBuildingPathStart))) continue;
                        otherBuildingDest = otherBuildingPathStart;
                    }
                    if (otherBuildingDest != null) {
                        pathDest = otherBuildingDest;
                    }
                }
            }
            PathCreator pathCreator = new PathCreator(queue, pathMaterial, b.location.getPlan().pathWidth, b, pathDest, pathStartPos);
            queue.addPathCreator(pathCreator);
        }
        queue.startNextPath();
    }

    private void refreshGoods() {
        if (this.location == null || this.location.getPlan() == null || this.location.getPlan().startingGoods.size() == 0) {
            return;
        }
        if (this.world.func_72935_r()) {
            this.refreshGoodsNightActionPerformed = false;
        } else if (!this.refreshGoodsNightActionPerformed) {
            long interval = this.chestLocked ? 20L : 100L;
            if (this.lastGoodsRefresh + interval * 24000L < this.world.func_72820_D() && this.chestLocked) {
                this.fillStartingGoods();
                this.lastGoodsRefresh = this.world.func_72820_D();
            }
            this.refreshGoodsNightActionPerformed = true;
        }
    }

    public void registerBuildingEntity(Building buildingEntity) throws MillLog.MillenaireException {
        if (this.buildings.contains(buildingEntity.getPos())) {
            MillLog.warning(this, "Trying to the register building that is already present: " + buildingEntity.getPos());
        } else {
            this.buildings.add(buildingEntity.getPos());
        }
        this.saveNeeded = true;
        this.saveReason = "Registering building";
    }

    public void registerBuildingLocation(BuildingLocation location) {
        BuildingPlan plan;
        boolean registered = false;
        for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
            if (this.buildingProjects.containsKey((Object)ep)) {
                List projectsLevel = this.buildingProjects.get((Object)ep);
                for (BuildingProject buildingProject : projectsLevel) {
                    if (location.level == 0 && location.isSubBuildingLocation) {
                        if (buildingProject.key.equals(location.planKey) && (buildingProject.location == null || buildingProject.location.level < 0)) {
                            if (buildingProject.location != null) {
                                location.upgradesAllowed = buildingProject.location.upgradesAllowed;
                            }
                            buildingProject.location = location.clone();
                            registered = true;
                            if (MillConfigValues.LogBuildingPlan >= 1) {
                                MillLog.major(this, "Updated building project: " + buildingProject + " with initial location.");
                            }
                        }
                    } else if (location.level == 0) {
                        if (buildingProject.key.equals(location.planKey) && (buildingProject.location == null || buildingProject.location.level < 0 && buildingProject.location.isSameLocation(location))) {
                            if (buildingProject.location != null) {
                                location.upgradesAllowed = buildingProject.location.upgradesAllowed;
                            }
                            buildingProject.location = location;
                            registered = true;
                            if (MillConfigValues.LogBuildingPlan >= 1) {
                                MillLog.major(this, "Updated building project: " + buildingProject + " with initial location.");
                            }
                        }
                    } else if (location.isSameLocation(buildingProject.location)) {
                        if (MillConfigValues.LogBuildingPlan >= 1) {
                            MillLog.major(this, "Updated building project: " + buildingProject + " from level " + buildingProject.location.level + " to " + location.level);
                        }
                        location.upgradesAllowed = buildingProject.location.upgradesAllowed;
                        buildingProject.location = location;
                        registered = true;
                    }
                    if (!registered) continue;
                    break;
                }
            }
            if (registered) break;
        }
        if (!registered) {
            BuildingProject project = new BuildingProject(location.getPlan().getPlanSet());
            project.location = location;
            if (location.getPlan().isWallSegment) {
                if (!this.buildingProjects.containsKey((Object)BuildingProject.EnumProjects.WALLBUILDING)) {
                    this.buildingProjects.put(BuildingProject.EnumProjects.WALLBUILDING, new CopyOnWriteArrayList());
                }
                this.buildingProjects.get((Object)BuildingProject.EnumProjects.WALLBUILDING).add(project);
            } else {
                this.buildingProjects.get((Object)BuildingProject.EnumProjects.EXTRA).add(project);
            }
        }
        if (MillConfigValues.LogBuildingPlan >= 1) {
            MillLog.major(this, "Registered building location: " + location);
        }
        if ((plan = location.getPlan()) != null) {
            for (Point p : this.buildings) {
                Building building = this.mw.getBuilding(p);
                if (building == null || building.location == null || !building.location.isSameLocation(location)) continue;
                building.location = building.location.createLocationForLevel(location.level);
                plan = location.getPlan();
                if (MillConfigValues.LogBuildingPlan < 1) continue;
                MillLog.major(this, "Updated building location for building: " + building + " now at upgrade: " + location.level);
            }
        }
        for (String s : location.subBuildings) {
            boolean found = false;
            List parentProjectLevel = null;
            int parentPos = 0;
            for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
                if (!this.buildingProjects.containsKey((Object)ep)) continue;
                List projectsLevel = this.buildingProjects.get((Object)ep);
                int pos = 0;
                for (BuildingProject project2 : projectsLevel) {
                    if (project2.location != null) {
                        if (project2.location.isLocationSamePlace(location) && project2.key.equals(s)) {
                            found = true;
                        } else if (project2.location.isSameLocation(location)) {
                            parentProjectLevel = projectsLevel;
                            parentPos = pos;
                        }
                    }
                    ++pos;
                }
            }
            if (found || parentProjectLevel == null) continue;
            if (this.culture.getBuildingPlanSet(s) == null) {
                MillLog.error(this, "Could not find plan for finished building: " + s);
                return;
            }
            BuildingProject buildingProject = new BuildingProject(this.culture.getBuildingPlanSet(s), location.getPlan());
            buildingProject.location = location.createLocationForSubBuilding(s);
            parentProjectLevel.add(parentPos + 1, buildingProject);
        }
        this.saveNeeded = true;
        this.saveReason = "Registering location";
    }

    public void registerVillagerRecord(VillagerRecord villagerRecord) {
        this.getVillagerRecords().put(villagerRecord.getVillagerId(), villagerRecord);
    }

    public boolean removeVillagerRecord(long vid) {
        return this.getVillagerRecords().remove(vid) != null;
    }

    public void requestSave(String reason) {
        this.saveNeeded = true;
        this.saveReason = reason;
    }

    public void resetConstructionsAndGoals() {
        this.constructionsIP.clear();
        this.buildingGoal = null;
        this.buildingGoalLocation = null;
    }

    public void respawnVillager(VillagerRecord vr, Point villagerPos) {
        MillVillager villager = MillVillager.createVillager(vr, this.world, villagerPos, true);
        if (villager == null) {
            MillLog.error(this, "Could not recreate villager " + vr + " of type " + vr.type);
        } else {
            if (!vr.killed) {
                if (MillConfigValues.LogVillagerSpawn >= 1) {
                    MillLog.major(this, "Giving the villager back " + vr.inventory.size() + " item types.");
                }
                for (InvItem iv : vr.inventory.keySet()) {
                    villager.inventory.put(iv, vr.inventory.get(iv));
                }
            }
            if (!vr.isTextureValid(vr.texture.func_110623_a())) {
                vr.texture = vr.getType().getNewTexture();
            }
            vr.killed = false;
            if (villager.getHouse() != null) {
                villager.setTexture(vr.texture);
                villager.isRaider = vr.raidingVillage;
                if (villager.func_70631_g_()) {
                    villager.computeChildScale();
                }
                this.world.func_72838_d((Entity)villager);
            }
        }
    }

    private void respawnVillagersIfNeeded() throws MillLog.MillenaireException {
        int time = (int)(this.world.func_72820_D() % 24000L);
        boolean resurect = time >= 13000 && time < 13100;
        for (VillagerRecord vr : this.getVillagerRecords().values()) {
            MillVillager foundVillager = this.mw.getVillagerById(vr.getVillagerId());
            if (foundVillager == null) {
                boolean respawn = false;
                if (!vr.flawedRecord) {
                    if (vr.raidingVillage) {
                        if (!vr.killed && this.world.func_72820_D() > vr.raiderSpawn + 500L) {
                            respawn = true;
                        }
                    } else if (!(vr.awayraiding || vr.awayhired || vr.getType().noResurrect || vr.killed && !resurect)) {
                        respawn = true;
                    }
                }
                if (!respawn) continue;
                if (MillConfigValues.LogVillagerSpawn >= 1) {
                    MillLog.major(this, "Recreating missing villager from record " + vr + ". Killed: " + vr.killed);
                }
                if (this.mw.getBuilding(vr.getHousePos()) == null) {
                    MillLog.error(this, "Error when trying to recreate a villager from record " + vr + ": couldn't load house at " + vr.getHousePos() + ".");
                    vr.flawedRecord = true;
                    continue;
                }
                Point villagerPos = vr.raidingVillage && vr.originalVillagePos != null ? this.findAttackerSpawnPoint(vr.originalVillagePos) : (this.underAttack ? (vr.getType().helpInAttacks ? this.resManager.getDefendingPos() : this.resManager.getShelterPos()) : this.mw.getBuilding((Point)vr.getHousePos()).resManager.getSleepingPos());
                this.respawnVillager(vr, villagerPos);
                continue;
            }
            if (vr.getHousePos() != null && vr.texture != null && vr.getNameKey() != null && vr.getNameKey().length() != 0 && !vr.getNameKey().equals("villager")) continue;
            MillLog.major(this, "Updating record for villager: " + foundVillager);
            vr.updateRecord(foundVillager);
            vr.flawedRecord = false;
        }
    }

    public int rushCurrentConstructions(boolean worldGeneration) throws MillLog.MillenaireException {
        int nbRushed = 0;
        for (ConstructionIP cip : this.getConstructionsInProgress()) {
            if (cip.getBuildingLocation() == null) continue;
            BuildingPlan plan = this.getBuildingPlanForConstruction(cip);
            ++nbRushed;
            List<BuildingPlan.LocationBuildingPair> lbps = cip.getBuildingLocation().isSameLocation(this.location) ? plan.build(this.mw, this.villageType, cip.getBuildingLocation(), worldGeneration, true, this, false, false, null, true) : plan.build(this.mw, this.villageType, cip.getBuildingLocation(), worldGeneration, false, this, false, false, null, true);
            for (BuildingPlan.LocationBuildingPair b : lbps) {
                this.registerBuildingEntity(b.building);
            }
            cip.clearBblocks();
            this.completeConstruction(cip);
        }
        this.updateConstructionQueue(worldGeneration);
        return nbRushed;
    }

    public void saveTownHall(String reason) {
        if (!this.world.field_72995_K && this.saveWorker == null) {
            this.saveWorker = new SaveWorker(reason);
            this.saveWorker.start();
        }
    }

    public void sendBuildingPacket(EntityPlayer player, boolean sendChest) {
        if (this.world.field_72995_K) {
            return;
        }
        if (this.culture == null) {
            MillLog.error(this, "Unknown culture for this village.");
            return;
        }
        if (sendChest) {
            this.sendChestPackets(player);
        }
        PacketBuffer data = ServerSender.getPacketBuffer();
        try {
            data.writeInt(2);
            StreamReadWrite.writeNullablePoint(this.getPos(), data);
            data.writeBoolean(this.isTownhall);
            data.writeBoolean(this.chestLocked);
            StreamReadWrite.writeNullableUUID(this.controlledBy, data);
            StreamReadWrite.writeNullableString(this.controlledByName, data);
            StreamReadWrite.writeNullablePoint(this.getTownHallPos(), data);
            StreamReadWrite.writeNullableString(this.culture.key, data);
            String vtype = null;
            if (this.villageType != null) {
                vtype = this.villageType.key;
            }
            StreamReadWrite.writeNullableString(vtype, data);
            StreamReadWrite.writeNullableBuildingLocation(this.location, data);
            StreamReadWrite.writeStringCollection(this.getTags(), data);
            StreamReadWrite.writeNullableString(this.buildingGoal, data);
            StreamReadWrite.writeNullableString(this.buildingGoalIssue, data);
            data.writeInt(this.buildingGoalLevel);
            data.writeInt(this.buildingGoalVariation);
            StreamReadWrite.writeNullableBuildingLocation(this.buildingGoalLocation, data);
            ArrayList<BuildingLocation> bls = new ArrayList<BuildingLocation>();
            ArrayList<Boolean> isCIPwall = new ArrayList<Boolean>();
            for (ConstructionIP cip : this.getConstructionsInProgress()) {
                isCIPwall.add(cip.isWallConstruction());
                bls.add(cip.getBuildingLocation());
            }
            StreamReadWrite.writeBooleanList(isCIPwall, data);
            StreamReadWrite.writeBuildingLocationList(bls, data);
            StreamReadWrite.writeProjectListList(this.buildingProjects, data);
            StreamReadWrite.writePointList(this.buildings, data);
            StreamReadWrite.writeStringList(this.buildingsBought, data);
            StreamReadWrite.writePointIntegerMap(this.relations, data);
            StreamReadWrite.writeStringList(this.raidsPerformed, data);
            StreamReadWrite.writeStringList(this.raidsSuffered, data);
            StreamReadWrite.writeVillagerRecordMap(this.getVillagerRecords(), data);
            StreamReadWrite.writeNullablePuja(this.pujas, data);
            StreamReadWrite.writeStringList(this.visitorsList, data);
            StreamReadWrite.writeInventory(this.imported, data);
            StreamReadWrite.writeInventory(this.exported, data);
            StreamReadWrite.writeNullableString(this.name, data);
            StreamReadWrite.writeNullableString(this.getQualifier(), data);
            StreamReadWrite.writeNullablePoint(this.raidTarget, data);
            data.writeLong(this.raidPlanningStart);
            data.writeLong(this.raidStart);
            this.resManager.sendBuildingPacket(data);
            if (this.marvelManager != null) {
                this.marvelManager.sendBuildingPacket(data);
            }
        }
        catch (IOException e) {
            MillLog.printException(this + ": Error in sendUpdatePacket", e);
        }
        this.mw.getProfile((EntityPlayer)player).buildingsSent.put(this.pos, this.mw.world.func_72820_D());
        ServerSender.sendPacketToPlayer(data, player);
    }

    public void sendChestPackets(EntityPlayer player) {
        for (Point p : this.resManager.chests) {
            TileEntityLockedChest chest = p.getMillChest(this.world);
            if (chest == null) continue;
            chest.buildingPos = this.getPos();
            chest.sendUpdatePacket(player);
        }
    }

    private void sendInitialBuildingPackets() {
        for (EntityPlayer entityPlayer : VillageUtilities.getServerPlayers(this.mw.world)) {
            UserProfile profile;
            if (!(this.pos.distanceToSquared((Entity)entityPlayer) < 256.0) || (profile = VillageUtilities.getServerProfile(this.mw.world, entityPlayer)) == null || profile.buildingsSent.containsKey(this.pos)) continue;
            this.sendBuildingPacket(entityPlayer, false);
        }
    }

    public void sendMapInfo(EntityPlayer player) {
        if (this.getTownHall() != null && this.getTownHall().winfo != null) {
            MillMapInfo minfo = new MillMapInfo(this.getTownHall(), this.getTownHall().winfo);
            minfo.sendMapInfoPacket(player);
        }
    }

    public void sendShopPacket(EntityPlayer player) {
        PacketBuffer data = ServerSender.getPacketBuffer();
        data.writeInt(11);
        StreamReadWrite.writeNullablePoint(this.getPos(), data);
        if (this.shopSells.containsKey(player.func_70005_c_())) {
            data.writeInt(this.shopSells.get(player.func_70005_c_()).size());
            for (TradeGood g : this.shopSells.get(player.func_70005_c_()).keySet()) {
                StreamReadWrite.writeNullableGoods(g, data);
                data.writeInt(this.shopSells.get(player.func_70005_c_()).get(g).intValue());
            }
        } else {
            data.writeInt(0);
        }
        if (this.shopBuys.containsKey(player.func_70005_c_())) {
            data.writeInt(this.shopBuys.get(player.func_70005_c_()).size());
            for (TradeGood g : this.shopBuys.get(player.func_70005_c_()).keySet()) {
                StreamReadWrite.writeNullableGoods(g, data);
                data.writeInt(this.shopBuys.get(player.func_70005_c_()).get(g).intValue());
            }
        } else {
            data.writeInt(0);
        }
        ServerSender.sendPacketToPlayer(data, player);
    }

    public void sendVillagerOnRaid(VillagerRecord vr, Building target) {
        if (MillConfigValues.LogDiplomacy >= 2) {
            MillLog.minor(this, "Sending villager " + vr + " to raid" + target + ".");
        }
        vr.awayraiding = true;
        VillagerRecord raidRecord = vr.generateRaidRecord(target);
        this.mw.registerVillagerRecord(raidRecord, true);
        MillVillager v = this.mw.getVillagerById(vr.getVillagerId());
        if (v != null) {
            v.despawnVillagerSilent();
            if (MillConfigValues.LogDiplomacy >= 3) {
                MillLog.debug(this, "Villager entity despawned.");
            }
        }
        target.getTownHall().saveTownHall("incoming villager");
    }

    public void setGoods(Item item, int meta, int newVal) {
        int nb = this.countGoods(item, meta);
        if (nb < newVal) {
            this.storeGoods(item, meta, newVal - nb);
        } else {
            this.takeGoods(item, meta, nb - newVal);
        }
    }

    public void setNewVillagerList(Set<MillVillager> villagers) {
        this.villagers = villagers;
    }

    private void startRaid() {
        Building distantVillage = this.mw.getBuilding(this.raidTarget);
        if (this.relations.get(this.raidTarget) != null && this.relations.get(this.raidTarget) > -90) {
            this.cancelRaid();
        }
        if (distantVillage != null) {
            this.raidStart = this.world.func_72820_D();
            int nbRaider = 0;
            ArrayList<VillagerRecord> vrecordsCopy = new ArrayList<VillagerRecord>(this.getVillagerRecords().values());
            for (VillagerRecord vr : vrecordsCopy) {
                if (!vr.getType().isRaider || vr.killed || vr.raidingVillage || vr.awayraiding || vr.awayhired) continue;
                if (MillConfigValues.LogDiplomacy >= 2) {
                    MillLog.minor(this, "Need to transfer raider; " + vr);
                }
                vr.getHouse().sendVillagerOnRaid(vr, distantVillage);
                ++nbRaider;
            }
            if (nbRaider > 0) {
                ServerSender.sendTranslatedSentenceInRange(this.world, this.getPos(), MillConfigValues.BackgroundRadius, '4', "raid.started", this.getVillageQualifiedName(), distantVillage.getVillageQualifiedName(), "" + nbRaider);
                distantVillage.cancelRaid();
                distantVillage.underAttack = true;
            } else {
                this.cancelRaid();
            }
        } else {
            this.cancelRaid();
        }
    }

    public int storeGoods(Block block, int toPut) {
        return this.storeGoods(Item.func_150898_a((Block)block), 0, toPut);
    }

    public int storeGoods(Block block, int meta, int toPut) {
        return this.storeGoods(Item.func_150898_a((Block)block), meta, toPut);
    }

    public int storeGoods(IBlockState bs, int toPut) {
        return this.storeGoods(Item.func_150898_a((Block)bs.func_177230_c()), bs.func_177230_c().func_176201_c(bs), toPut);
    }

    public int storeGoods(InvItem item, int toPut) {
        return this.storeGoods(item.getItem(), item.meta, toPut);
    }

    public int storeGoods(Item item, int toPut) {
        return this.storeGoods(item, 0, toPut);
    }

    public int storeGoods(Item item, int meta, int toPut) {
        int stored = 0;
        for (int i = 0; stored < toPut && i < this.resManager.chests.size(); ++i) {
            TileEntityLockedChest chest = this.resManager.chests.get(i).getMillChest(this.world);
            if (chest == null) continue;
            stored += MillCommonUtilities.putItemsInChest((IInventory)chest, item, meta, toPut - stored);
        }
        this.invalidateInventoryCache();
        return stored;
    }

    public boolean storeItemStack(ItemStack is) {
        for (Point p : this.resManager.chests) {
            TileEntityLockedChest chest = p.getMillChest(this.world);
            if (chest == null) continue;
            for (int i = 0; i < chest.func_70302_i_(); ++i) {
                ItemStack stack = chest.func_70301_a(i);
                if (!stack.func_190926_b()) continue;
                chest.func_70299_a(i, is);
                this.invalidateInventoryCache();
                return true;
            }
        }
        return false;
    }

    private void swapMerchants(Building destInn) {
        int nb;
        HashMap<InvItem, Integer> contents = this.resManager.getChestsContent();
        HashMap<InvItem, Integer> destContents = destInn.resManager.getChestsContent();
        for (InvItem key : contents.keySet()) {
            nb = this.takeGoods(key.getItem(), key.meta, (int)contents.get(key));
            destInn.storeGoods(key.getItem(), key.meta, nb);
            destInn.addToImports(key, nb);
            this.addToExports(key, nb);
        }
        for (InvItem key : destContents.keySet()) {
            nb = destInn.takeGoods(key.getItem(), key.meta, (int)destContents.get(key));
            this.storeGoods(key.getItem(), key.meta, nb);
            destInn.addToExports(key, nb);
            this.addToImports(key, nb);
        }
        VillagerRecord oldMerchant = this.merchantRecord;
        VillagerRecord newMerchant = destInn.merchantRecord;
        this.transferVillagerPermanently(this.merchantRecord, destInn);
        destInn.transferVillagerPermanently(destInn.merchantRecord, this);
        this.visitorsList.add("panels.merchantmovedout;" + oldMerchant.getName() + ";" + oldMerchant.getNativeOccupationName() + ";" + destInn.getTownHall().getVillageQualifiedName() + ";" + this.nbNightsMerchant);
        destInn.visitorsList.add("panels.merchantmovedout;" + newMerchant.getName() + ";" + newMerchant.getNativeOccupationName() + ";" + this.getTownHall().getVillageQualifiedName() + ";" + this.nbNightsMerchant);
        this.visitorsList.add("panels.merchantarrived;" + newMerchant.getName() + ";" + newMerchant.getNativeOccupationName() + ";" + destInn.getTownHall().getVillageQualifiedName());
        destInn.visitorsList.add("panels.merchantarrived;" + oldMerchant.getName() + ";" + oldMerchant.getNativeOccupationName() + ";" + this.getTownHall().getVillageQualifiedName());
        if (MillConfigValues.LogMerchant >= 1) {
            MillLog.major(this, "Swaped merchant " + oldMerchant + " and " + newMerchant + " with " + destInn.getTownHall());
        }
        this.merchantRecord = newMerchant;
        destInn.merchantRecord = oldMerchant;
        this.nbNightsMerchant = 0;
        destInn.nbNightsMerchant = 0;
        destInn.saveTownHall("merchant moved");
        this.saveNeeded = true;
        this.saveReason = "Swapped merchant";
    }

    public int takeGoods(Block block, int meta, int toTake) {
        return this.takeGoods(Item.func_150898_a((Block)block), meta, toTake);
    }

    public int takeGoods(IBlockState blockState, int toTake) {
        return this.takeGoods(Item.func_150898_a((Block)blockState.func_177230_c()), blockState.func_177230_c().func_176201_c(blockState), toTake);
    }

    public int takeGoods(InvItem item, int toTake) {
        return this.takeGoods(item.getItem(), item.meta, toTake);
    }

    public int takeGoods(Item item, int toTake) {
        return this.takeGoods(item, 0, toTake);
    }

    public int takeGoods(Item item, int meta, int toTake) {
        int i;
        int taken = 0;
        for (i = 0; taken < toTake && i < this.resManager.chests.size(); ++i) {
            TileEntityLockedChest chest = this.resManager.chests.get(i).getMillChest(this.world);
            if (chest == null) continue;
            taken += WorldUtilities.getItemsFromChest((IInventory)chest, item, meta, toTake - taken);
        }
        for (i = 0; taken < toTake && i < this.resManager.furnaces.size(); ++i) {
            TileEntityFurnace furnace = this.resManager.furnaces.get(i).getFurnace(this.world);
            if (furnace == null) continue;
            taken += WorldUtilities.getItemsFromFurnace(furnace, item, toTake - taken);
        }
        for (i = 0; taken < toTake && i < this.resManager.firepits.size(); ++i) {
            TileEntityFirePit firepit = (TileEntityFirePit)this.world.func_175625_s(this.resManager.firepits.get(i).getBlockPos());
            if (firepit == null) continue;
            taken += WorldUtilities.getItemsFromFirePit(firepit, item, toTake - taken);
        }
        this.invalidateInventoryCache();
        return taken;
    }

    public void testModeGoods() {
        if (this.isTownhall && !this.villageType.lonebuilding) {
            int stored = this.storeGoods(MillItems.DENIER_OR, 64);
            if (stored < 64) {
                MillLog.error(this, "Should have stored 64 test goods but stored only " + stored);
            }
            this.storeGoods(MillItems.SUMMONING_WAND, 1);
            if (this.culture.key.equals("indian")) {
                this.storeGoods(MillItems.INDIAN_STATUE, 64);
                this.storeGoods(MillBlocks.BS_MUD_BRICK, 2048);
                this.storeGoods(MillBlocks.PAINTED_BRICK_WHITE, 0, 2048);
                this.storeGoods(MillBlocks.PAINTED_BRICK_DECORATED_WHITE, 0, 512);
                this.storeGoods(Blocks.field_150322_A, 2048);
                this.storeGoods(Blocks.field_150348_b, 2048);
                this.storeGoods(Blocks.field_150347_e, 512);
                this.storeGoods(Blocks.field_150363_s, 0, 1024);
                this.storeGoods((Block)MillBlocks.BED_CHARPOY, 0, 64);
                this.storeGoods((Block)MillBlocks.WOODEN_BARS, 0, 64);
                this.storeGoods((Block)MillBlocks.WOODEN_BARS_INDIAN, 0, 64);
                this.storeGoods((Block)MillBlocks.WOOD_DECORATION, 2, 512);
            } else if (this.culture.key.equals("mayan")) {
                this.storeGoods(Blocks.field_150322_A, 512);
                this.storeGoods(Blocks.field_150348_b, 3500);
                this.storeGoods(Blocks.field_150347_e, 2048);
                this.storeGoods((Block)MillBlocks.STONE_DECORATION, 2, 64);
                this.storeGoods(Blocks.field_150364_r, 1, 512);
                this.storeGoods(Blocks.field_150364_r, 3, 1024);
            } else if (this.culture.key.equals("japanese")) {
                this.storeGoods(Blocks.field_150345_g, 64);
                this.storeGoods((Block)MillBlocks.WOOD_DECORATION, 2, 2048);
                this.storeGoods(Blocks.field_150351_n, 512);
                this.storeGoods((Block)MillBlocks.PAPER_WALL, 2048);
                this.storeGoods(Blocks.field_150348_b, 2048);
                this.storeGoods(Blocks.field_150347_e, 1024);
                this.storeGoods((Block)MillBlocks.WOOD_DECORATION, 0, 512);
                this.storeGoods((Block)MillBlocks.WOOD_DECORATION, 1, 512);
                this.storeGoods(Blocks.field_150364_r, 1, 512);
            } else if (this.culture.key.equals("byzantines")) {
                this.storeGoods(Blocks.field_150359_w, 512);
                this.storeGoods(Blocks.field_150347_e, 1500);
                this.storeGoods(Blocks.field_150348_b, 1500);
                this.storeGoods(Blocks.field_150336_V, 512);
                this.storeGoods(Blocks.field_150322_A, 512);
                this.storeGoods(Blocks.field_150325_L, 11, 64);
                this.storeGoods(Blocks.field_150325_L, 14, 64);
                this.storeGoods(Blocks.field_150364_r, 2, 128);
                this.storeGoods(Blocks.field_150342_X, 0, 64);
                this.storeGoods((Block)MillBlocks.BYZANTINE_TILES, 128);
                this.storeGoods((Block)MillBlocks.BYZANTINE_TILES_SLAB, 128);
                this.storeGoods((Block)MillBlocks.BYZANTINE_STONE_TILES, 128);
            } else if (this.culture.key.equals("norman")) {
                this.storeGoods(Blocks.field_150359_w, 64);
                this.storeGoods(Blocks.field_150347_e, 2048);
                this.storeGoods(Blocks.field_150348_b, 3500);
                this.storeGoods((Block)MillBlocks.WOOD_DECORATION, 0, 2048);
                this.storeGoods((Block)MillBlocks.WOOD_DECORATION, 1, 2048);
                this.storeGoods(Blocks.field_150325_L, 11, 64);
                this.storeGoods(Blocks.field_150325_L, 14, 64);
                this.storeGoods(Blocks.field_150364_r.func_176223_P().func_177226_a((IProperty)BlockOldLog.field_176301_b, (Comparable)BlockPlanks.EnumType.SPRUCE), 512);
                this.storeGoods(Blocks.field_150363_s.func_176223_P().func_177226_a((IProperty)BlockNewLog.field_176300_b, (Comparable)BlockPlanks.EnumType.DARK_OAK), 1024);
                this.storeGoods((Block)MillBlocks.BED_STRAW, 64);
                this.storeGoods(MillBlocks.STAINED_GLASS.func_176223_P().func_177226_a(BlockMillStainedGlass.VARIANT, (Comparable)((Object)BlockMillStainedGlass.EnumType.WHITE)), 64);
                this.storeGoods(MillBlocks.STAINED_GLASS.func_176223_P().func_177226_a(BlockMillStainedGlass.VARIANT, (Comparable)((Object)BlockMillStainedGlass.EnumType.YELLOW)), 64);
                this.storeGoods(MillBlocks.STAINED_GLASS.func_176223_P().func_177226_a(BlockMillStainedGlass.VARIANT, (Comparable)((Object)BlockMillStainedGlass.EnumType.YELLOW_RED)), 64);
                this.storeGoods(MillBlocks.STAINED_GLASS.func_176223_P().func_177226_a(BlockMillStainedGlass.VARIANT, (Comparable)((Object)BlockMillStainedGlass.EnumType.GREEN_BLUE)), 64);
                this.storeGoods(MillBlocks.STAINED_GLASS.func_176223_P().func_177226_a(BlockMillStainedGlass.VARIANT, (Comparable)((Object)BlockMillStainedGlass.EnumType.RED_BLUE)), 64);
                this.storeGoods((Block)MillBlocks.ROSETTE, 64);
                this.storeGoods((Block)MillBlocks.BED_STRAW, 64);
            } else if (this.culture.key.equals("inuits")) {
                this.storeGoods(MillBlocks.ICE_BRICK, 128);
                this.storeGoods((Block)MillBlocks.SNOW_BRICK, 1024);
                this.storeGoods(MillBlocks.SNOW_WALL, 128);
                this.storeGoods(Blocks.field_150364_r, 1, 1024);
                this.storeGoods(Blocks.field_150364_r, 2, 1024);
                this.storeGoods(Blocks.field_189880_di, 256);
                this.storeGoods((Block)MillBlocks.INUIT_CARVING, 0, 64);
            } else {
                this.storeGoods(Blocks.field_150359_w, 512);
                this.storeGoods(Blocks.field_150347_e, 2048);
                this.storeGoods(Blocks.field_150348_b, 3500);
                this.storeGoods((Block)MillBlocks.WOOD_DECORATION, 0, 2048);
                this.storeGoods((Block)MillBlocks.WOOD_DECORATION, 1, 2048);
                this.storeGoods(Blocks.field_150325_L, 11, 64);
                this.storeGoods(Blocks.field_150325_L, 14, 64);
            }
            this.storeGoods(Blocks.field_150364_r, 1024);
            this.storeGoods(Items.field_151042_j, 256);
            this.storeGoods(Blocks.field_150325_L, 64);
            this.storeGoods(Blocks.field_150351_n, 64);
            this.storeGoods((Block)Blocks.field_150354_m, 64);
            this.storeGoods((Block)MillBlocks.BED_STRAW, 0, 128);
        }
    }

    public void testShowGroundLevel() {
        for (int i = 0; i < this.winfo.length; ++i) {
            for (int j = 0; j < this.winfo.width; ++j) {
                Point p = new Point(this.winfo.mapStartX + i, this.winfo.topGround[i][j] - 1, this.winfo.mapStartZ + j);
                if (WorldUtilities.getBlock(this.world, p) == MillBlocks.LOCKED_CHEST) continue;
                if (!this.winfo.topAdjusted[i][j]) {
                    WorldUtilities.setBlockAndMetadata(this.world, p, Blocks.field_150325_L, this.regionMapper.regions[i][j] % 16);
                    continue;
                }
                WorldUtilities.setBlockAndMetadata(this.world, p, Blocks.field_150339_S, 0);
            }
        }
    }

    public String toString() {
        if (this.location != null) {
            return "(" + this.location + "/" + this.getVillageQualifiedName() + "/" + this.world + ")";
        }
        return super.toString();
    }

    public void transferVillagerPermanently(VillagerRecord vr, Building dest) {
        if (MillConfigValues.LogDiplomacy >= 2) {
            MillLog.minor(this, "Transfering villager " + vr + " permanently to " + dest + ".");
        }
        this.mw.removeVillagerRecord(vr.getVillagerId());
        vr.setHousePos(dest.getPos());
        vr.setTownHallPos(dest.getTownHall().getPos());
        this.mw.registerVillagerRecord(vr, true);
        MillVillager v = this.mw.getVillagerById(vr.getVillagerId());
        if (v != null) {
            this.villagers.remove(v);
            this.getTownHall().villagers.remove(v);
            if (dest.getTownHall().isActive) {
                v.setHousePoint(dest.getPos());
                v.setTownHallPoint(dest.getTownHall().getPos());
                v.isRaider = false;
                v.func_70107_b(dest.resManager.getSleepingPos().getiX(), dest.resManager.getSleepingPos().getiY(), dest.resManager.getSleepingPos().getiZ());
            } else {
                v.despawnVillager();
                if (MillConfigValues.LogDiplomacy >= 3) {
                    MillLog.debug(this, "Villager entity despawned.");
                }
            }
        }
        dest.getTownHall().saveTownHall("incoming villager");
    }

    private void triggerCompletionAdvancements() {
        String cultureKey;
        if (this.buildingGoal != null) {
            return;
        }
        if (!this.villageType.isRegularVillage()) {
            return;
        }
        EntityPlayer player = this.world.func_184137_a((double)this.pos.getiX(), (double)this.pos.getiY(), (double)this.pos.getiZ(), 5.0, false);
        if (player == null) {
            return;
        }
        if (this.getReputation(player) > 32768 && MillAdvancements.COMPLETE_ADVANCEMENTS.containsKey(cultureKey = this.culture.key.toLowerCase())) {
            MillAdvancements.COMPLETE_ADVANCEMENTS.get(cultureKey).grant(player);
        }
    }

    private void unloadChunks() {
        if (this.chunkLoader != null && this.chunkLoader.chunksLoaded) {
            this.chunkLoader.unloadChunks();
        }
    }

    public void unlockAllChests() {
        this.chestLocked = false;
        for (Point p : this.buildings) {
            Building b = this.mw.getBuilding(p);
            if (b == null) continue;
            b.unlockChests();
        }
        if (this.countGoods(MillItems.NEGATION_WAND) == 0) {
            this.storeGoods(MillItems.NEGATION_WAND, 1);
        }
    }

    public void unlockChests() {
        if (!this.isMarket) {
            this.chestLocked = false;
            for (Point p : this.resManager.chests) {
                TileEntityLockedChest chest = p.getMillChest(this.world);
                if (chest == null) continue;
                chest.buildingPos = this.getPos();
            }
        }
    }

    private void unlockForNearbyPlayers() {
        Point p = this.resManager.getSellingPos();
        if (p == null) {
            p = this.pos;
        }
        List players = this.world.func_175647_a(EntityPlayer.class, new AxisAlignedBB((double)(this.location.minx - 2), (double)(this.location.miny - 2), (double)(this.location.minz - 2), (double)(this.location.maxx + 2), (double)(this.location.maxy + 2), (double)(this.location.maxz + 2)), null);
        for (EntityPlayer player : players) {
            UserProfile profile = this.mw.getProfile(player);
            if (profile == null) continue;
            profile.unlockBuilding(this.culture, this.culture.getBuildingPlanSet(this.location.planKey));
            if (this.getTownHall() == null) continue;
            profile.unlockVillage(this.culture, this.getTownHall().villageType);
        }
    }

    private void updateAchievements() {
        EntityPlayer player;
        if (this.villageType == null) {
            return;
        }
        List<Entity> players = WorldUtilities.getEntitiesWithinAABB(this.world, EntityPlayer.class, this.getPos(), this.villageType.radius, 20);
        for (Entity ent : players) {
            int nbcultures;
            EntityPlayer player2 = (EntityPlayer)ent;
            if (this.villageType.lonebuilding) {
                MillAdvancements.EXPLORER.grant(player2);
            }
            if (this.containsTags("hof")) {
                MillAdvancements.PANTHEON.grant(player2);
            }
            if ((nbcultures = this.mw.nbCultureInGeneratedVillages()) >= 3) {
                MillAdvancements.MARCO_POLO.grant(player2);
            }
            if (nbcultures < Culture.ListCultures.size()) continue;
            MillAdvancements.MAGELLAN.grant(player2);
        }
        if (this.controlledBy != null && this.getVillagerRecords().size() >= 100 && (player = this.world.func_152378_a(this.controlledBy)) != null) {
            MillAdvancements.MEDIEVAL_METROPOLIS.grant(player);
        }
    }

    private void updateAutoSpawn() {
        if (!(this.world.func_72935_r() || this.location.getMaleResidents().size() <= 0 && this.location.getFemaleResidents().size() <= 0 || this.getVillagerRecords().size() != 0)) {
            try {
                this.createResidents();
            }
            catch (MillLog.MillenaireException e) {
                MillLog.printException("Exception when auto-spawning villagers for " + this + ":", e);
            }
        }
    }

    public void updateBackgroundVillage() {
        if (this.world.field_72995_K) {
            return;
        }
        if (this.villageType == null || !this.isTownhall || this.location == null) {
            return;
        }
        EntityPlayer player = this.world.func_184137_a((double)this.pos.getiX(), (double)this.pos.getiY(), (double)this.pos.getiZ(), (double)MillConfigValues.BackgroundRadius, false);
        if (player != null) {
            if (this.world.func_72935_r()) {
                this.nightBackgroundActionPerformed = false;
            } else if (!this.nightBackgroundActionPerformed) {
                if (this.villageType.carriesRaid && this.raidTarget == null && MillCommonUtilities.randomInt(100) < MillConfigValues.RaidingRate) {
                    if (MillConfigValues.LogDiplomacy >= 3) {
                        MillLog.debug(this, "Calling attemptPlanNewRaid");
                    }
                    this.attemptPlanNewRaid();
                }
                this.nightBackgroundActionPerformed = true;
            }
        }
        if (this.world.func_72820_D() % 24000L > 23500L && this.raidTarget != null) {
            if (!this.updateRaidPerformed) {
                if (MillConfigValues.LogDiplomacy >= 3) {
                    MillLog.debug(this, "Calling updateRaid for raid: " + this.raidPlanningStart + "/" + this.raidStart + "/" + this.world.func_72820_D());
                }
                this.updateRaid();
                this.updateRaidPerformed = true;
            }
        } else {
            this.updateRaidPerformed = false;
        }
    }

    public void updateBanners() {
        TileEntity te;
        if (this.getTownHall().bannerStack == ItemStack.field_190927_a) {
            this.getTownHall().generateBannerPattern();
        }
        for (Point p : this.resManager.banners) {
            te = this.mw.world.func_175625_s(p.getBlockPos());
            if (!(te instanceof TileEntityBanner)) continue;
            NBTTagCompound bannerCompound = this.getTownHall().bannerStack.func_179543_a("BlockEntityTag");
            boolean getBaseColorFromNBT = bannerCompound != null ? bannerCompound.func_74764_b("Base") : false;
            ((TileEntityBanner)te).func_175112_a(this.getTownHall().bannerStack, getBaseColorFromNBT);
            this.mw.world.func_184138_a(te.func_174877_v(), this.mw.world.func_180495_p(te.func_174877_v()), this.mw.world.func_180495_p(te.func_174877_v()), 3);
        }
        for (Point p : this.resManager.cultureBanners) {
            te = this.mw.world.func_175625_s(p.getBlockPos());
            if (!(te instanceof TileEntityBanner)) continue;
            ((TileEntityBanner)te).func_175112_a(this.culture.cultureBannerItemStack, true);
            this.mw.world.func_184138_a(te.func_174877_v(), this.mw.world.func_180495_p(te.func_174877_v()), this.mw.world.func_180495_p(te.func_174877_v()), 3);
        }
    }

    public void updateBuildingClient() {
        if ((this.world.func_72820_D() + (long)this.hashCode()) % 20L == 8L) {
            this.rebuildVillagerList();
        }
        if (this.isActive && this.isTownhall && (this.world.func_72820_D() + (long)this.hashCode()) % 100L == 48L) {
            this.triggerCompletionAdvancements();
        }
    }

    public void updateBuildingServer() {
        block40: {
            if (this.world.field_72995_K) {
                return;
            }
            if (this.mw.getBuilding(this.pos) != this) {
                MillLog.error(this, "Other building registered in my place: " + this.mw.getBuilding(this.pos));
            }
            if (this.location == null) {
                return;
            }
            if (this.isActive && (this.world.func_72820_D() + (long)this.hashCode()) % 40L == 15L) {
                this.rebuildVillagerList();
            }
            if (this.isActive && (this.world.func_72820_D() + (long)this.hashCode()) % 100L == 48L) {
                this.triggerCompletionAdvancements();
            }
            EntityPlayer player = this.world.func_184137_a((double)this.pos.getiX(), (double)this.pos.getiY(), (double)this.pos.getiZ(), (double)MillConfigValues.KeepActiveRadius, false);
            if (this.isTownhall) {
                EntityPlayer p;
                if (player != null && this.getPos().distanceTo((Entity)player) < (double)MillConfigValues.KeepActiveRadius) {
                    this.loadChunks();
                } else if (player == null || this.getPos().distanceTo((Entity)player) > (double)(MillConfigValues.KeepActiveRadius + 32)) {
                    this.unloadChunks();
                }
                this.isAreaLoaded = this.isVillageChunksLoaded();
                if (this.isActive && !this.isAreaLoaded) {
                    this.isActive = false;
                    for (Object o : this.world.field_73010_i) {
                        p = (EntityPlayer)o;
                        this.sendBuildingPacket(p, false);
                    }
                    if (MillConfigValues.LogChunkLoader >= 1) {
                        MillLog.major(this, "Becoming inactive");
                    }
                    this.saveTownHall("becoming inactive");
                } else if (!this.isActive && this.isAreaLoaded) {
                    for (Object o : this.world.field_73010_i) {
                        p = (EntityPlayer)o;
                        this.sendBuildingPacket(p, false);
                    }
                    if (MillConfigValues.LogChunkLoader >= 1) {
                        MillLog.major(this, "Becoming active");
                    }
                    this.isActive = true;
                }
                if (!this.isActive) {
                    return;
                }
            } else if (this.getTownHall() == null || !this.getTownHall().isActive) {
                return;
            }
            if (this.location == null) {
                return;
            }
            try {
                if (this.isTownhall && this.villageType != null) {
                    this.updateTownHall();
                }
                if (this.containsTags("grove")) {
                    this.updateGrove();
                }
                if (this.resManager.spawns.size() > 0) {
                    this.updatePens(false);
                }
                if (this.resManager.healingspots.size() > 0) {
                    this.updateHealingSpots();
                }
                if (this.resManager.mobSpawners.size() > 0 && player != null && this.pos.distanceToSquared((Entity)player) < 400.0) {
                    this.updateMobSpawners();
                }
                if (this.resManager.dispenderUnknownPowder.size() > 0) {
                    this.updateDispensers();
                }
                if (this.resManager.netherwartsoils.size() > 0) {
                    this.updateNetherWartSoils();
                }
                if (this.isInn) {
                    this.updateInn();
                }
                if (this.hasVisitors) {
                    this.getVisitorManager().update(false);
                }
                if (this.hasAutoSpawn) {
                    this.updateAutoSpawn();
                }
                if (Math.abs(this.world.func_72820_D() + (long)this.hashCode()) % 20L == 4L) {
                    this.unlockForNearbyPlayers();
                }
                this.getPanelManager().updateSigns();
                if (this.isTownhall) {
                    if (this.saveNeeded) {
                        this.saveTownHall("Save needed");
                    } else if (this.world.func_72820_D() - this.lastSaved > 1000L) {
                        this.saveTownHall("Delay up");
                    }
                }
                if (player != null && this.location.getPlan() != null && this.location.getPlan().exploreTag != null) {
                    this.checkExploreTag(player);
                }
                this.sendInitialBuildingPackets();
                if (MillCommonUtilities.chanceOn(100)) {
                    for (Point p : this.resManager.chests) {
                        if (p.getMillChest(this.world) == null) continue;
                        p.getMillChest((World)this.world).buildingPos = this.getPos();
                    }
                }
                this.refreshGoods();
            }
            catch (Exception e) {
                int nbRepeats = MillLog.printException("Exception in TileEntityBuilding.onUpdate() for building " + this + ": ", e);
                if (nbRepeats >= 5) break block40;
                Mill.proxy.sendChatAdmin(LanguageUtilities.string("ui.updateEntity"));
            }
        }
    }

    private boolean updateConstructionQueue(boolean ignoreCost) {
        boolean change = false;
        if (MillConfigValues.ignoreResourceCost) {
            ignoreCost = true;
        }
        change = this.findBuildingProject();
        change |= this.findBuildingConstruction(ignoreCost);
        if (this.getSimultaneousWallConstructionSlots() > 0) {
            change |= this.findBuildingConstructionWall(ignoreCost);
        }
        return change;
    }

    private void updateDispensers() {
        for (Point p : this.resManager.dispenderUnknownPowder) {
            TileEntityDispenser dispenser;
            if (!MillCommonUtilities.chanceOn(5000) || (dispenser = p.getDispenser(this.world)) == null) continue;
            MillCommonUtilities.putItemsInChest((IInventory)dispenser, MillItems.UNKNOWN_POWDER, 1);
        }
    }

    private void updateGrove() {
        for (Point p : this.resManager.woodspawn) {
            if (!MillCommonUtilities.chanceOn(4000) || WorldUtilities.getBlock(this.world, p) != Blocks.field_150345_g) continue;
            this.growTree(this.world, p.getiX(), p.getiY(), p.getiZ(), MillCommonUtilities.random);
        }
    }

    private void updateHealingSpots() {
        if (this.world.func_72820_D() % 100L == 0L) {
            for (Point p : this.resManager.healingspots) {
                EntityPlayer player = this.world.func_184137_a((double)p.getiX(), (double)p.getiY(), (double)p.getiZ(), 4.0, false);
                if (player == null || !(player.func_110143_aJ() < player.func_110138_aP())) continue;
                player.func_70606_j(player.func_110143_aJ() + 1.0f);
                ServerSender.sendTranslatedSentence(player, 'a', "other.buildinghealing", this.getNativeBuildingName());
            }
        }
    }

    private void updateInn() {
        if (this.world.func_72935_r()) {
            this.nightActionPerformed = false;
        } else if (!this.nightActionPerformed) {
            if (this.merchantRecord != null) {
                ++this.nbNightsMerchant;
                if (this.nbNightsMerchant > 1) {
                    this.attemptMerchantMove(false);
                }
            }
            this.nightActionPerformed = true;
        }
    }

    private void updateMobSpawners() {
        for (int i = 0; i < this.resManager.mobSpawners.size(); ++i) {
            for (int j = 0; j < this.resManager.mobSpawners.get(i).size(); ++j) {
                Class targetClass;
                List<Entity> mobs;
                int nbmob;
                Block block;
                if (!MillCommonUtilities.chanceOn(180) || (block = WorldUtilities.getBlock(this.world, this.resManager.mobSpawners.get(i).get(j))) != Blocks.field_150474_ac || (nbmob = (mobs = WorldUtilities.getEntitiesWithinAABB(this.world, targetClass = EntityList.getClass((ResourceLocation)this.resManager.mobSpawnerTypes.get(i)), this.resManager.mobSpawners.get(i).get(j), 10, 5)).size()) >= 4) continue;
                WorldUtilities.spawnMobsSpawner(this.world, this.resManager.mobSpawners.get(i).get(j), this.resManager.mobSpawnerTypes.get(i));
            }
        }
    }

    private void updateNetherWartSoils() {
        for (Point p : this.resManager.netherwartsoils) {
            int meta;
            if (!MillCommonUtilities.chanceOn(1000) || WorldUtilities.getBlock(this.world, p.getAbove()) != Blocks.field_150388_bm || (meta = WorldUtilities.getBlockMeta(this.world, p.getAbove())) >= 3) continue;
            WorldUtilities.setBlockMetadata(this.world, p.getAbove(), meta + 1);
        }
    }

    /*
     * WARNING - void declaration
     */
    private void updatePens(boolean completeRespawn) {
        if ((completeRespawn || !this.world.func_72935_r()) && (this.getVillagerRecords().size() > 0 || this.location.getMaleResidents().isEmpty() && this.location.getFemaleResidents().isEmpty()) && !this.world.field_72995_K) {
            int nbMaxRespawn = 0;
            for (List list : this.resManager.spawns) {
                nbMaxRespawn += list.size();
            }
            if (this.nbAnimalsRespawned <= nbMaxRespawn) {
                void var4_7;
                int sheep = 0;
                boolean bl = false;
                int pig = 0;
                int chicken = 0;
                int squid = 0;
                int wolves = 0;
                List<Entity> animals = WorldUtilities.getEntitiesWithinAABB(this.world, EntityAnimal.class, this.getPos(), 15, 20);
                for (Entity animal : animals) {
                    if (animal instanceof EntitySheep) {
                        ++sheep;
                        continue;
                    }
                    if (animal instanceof EntityPig) {
                        ++pig;
                        continue;
                    }
                    if (animal instanceof EntityCow) {
                        ++var4_7;
                        continue;
                    }
                    if (animal instanceof EntityChicken) {
                        ++chicken;
                        continue;
                    }
                    if (animal instanceof EntitySquid) {
                        ++squid;
                        continue;
                    }
                    if (!(animal instanceof EntityWolf)) continue;
                    ++wolves;
                }
                for (int i = 0; i < this.resManager.spawns.size(); ++i) {
                    int nb = 0;
                    if (this.resManager.spawnTypes.get(i).equals((Object)Mill.ENTITY_SHEEP)) {
                        nb = sheep;
                    } else if (this.resManager.spawnTypes.get(i).equals((Object)Mill.ENTITY_CHICKEN)) {
                        nb = chicken;
                    } else if (this.resManager.spawnTypes.get(i).equals((Object)Mill.ENTITY_PIG)) {
                        nb = pig;
                    } else if (this.resManager.spawnTypes.get(i).equals((Object)Mill.ENTITY_COW)) {
                        nb = var4_7;
                    } else if (this.resManager.spawnTypes.get(i).equals((Object)Mill.ENTITY_SQUID)) {
                        nb = squid;
                    } else if (this.resManager.spawnTypes.get(i).equals((Object)Mill.ENTITY_WOLF)) {
                        nb = wolves;
                    }
                    int multipliyer = 1;
                    if (this.resManager.spawnTypes.get(i).equals((Object)Mill.ENTITY_SQUID)) {
                        multipliyer = 2;
                    }
                    for (int j = 0; j < this.resManager.spawns.get(i).size() * multipliyer - nb; ++j) {
                        if (!completeRespawn && !MillCommonUtilities.chanceOn(100)) continue;
                        EntityLiving animal = (EntityLiving)EntityList.func_188429_b((ResourceLocation)this.resManager.spawnTypes.get(i), (World)this.world);
                        Point pen = this.resManager.spawns.get(i).get(MillCommonUtilities.randomInt(this.resManager.spawns.get(i).size()));
                        animal.func_70107_b((double)pen.getiX() + 0.5, (double)pen.getiY(), (double)pen.getiZ() + 0.5);
                        this.world.func_72838_d((Entity)animal);
                        ++this.nbAnimalsRespawned;
                    }
                }
            }
        } else {
            this.nbAnimalsRespawned = 0;
        }
    }

    private void updateRaid() {
        if (this.world.func_72820_D() > this.raidPlanningStart + 24000L && this.raidStart == 0L) {
            if (MillConfigValues.LogDiplomacy >= 2) {
                MillLog.minor(this, "Starting raid on " + this.mw.getBuilding(this.raidTarget));
            }
            this.startRaid();
        } else if (this.raidStart > 0L && this.world.func_72820_D() > this.raidStart + 23000L) {
            Building distantVillage = this.mw.getBuilding(this.raidTarget);
            if (distantVillage != null) {
                if (!distantVillage.isActive) {
                    this.endRaid();
                }
            } else {
                this.cancelRaid();
                for (VillagerRecord vr : this.getVillagerRecords().values()) {
                    vr.awayraiding = false;
                }
            }
        }
    }

    private void updateTownHall() throws MillLog.MillenaireException {
        GameProfile profile;
        if (this.getVillagerRecords().size() > 0) {
            this.updateWorldInfo();
        }
        if (MillConfigValues.autoConvertProfiles && !Mill.proxy.isTrueServer() && this.villageType.playerControlled && Mill.proxy.getTheSinglePlayer() != null && (this.controlledBy == null || !this.controlledBy.equals(Mill.proxy.getTheSinglePlayer().func_110124_au()))) {
            Iterator<Object> oldControlledBy = this.controlledBy;
            this.controlledBy = Mill.proxy.getTheSinglePlayer().func_110124_au();
            this.controlledByName = Mill.proxy.getTheSinglePlayer().func_70005_c_();
            MillLog.major(this, "Switched controller from " + oldControlledBy + " to " + this.controlledBy + " (" + this.controlledByName + "), the new single player.");
        }
        this.closestPlayer = this.world.func_184137_a((double)this.pos.getiX(), (double)this.pos.getiY(), (double)this.pos.getiZ(), 100.0, false);
        for (ConstructionIP constructionIP : this.getConstructionsInProgress()) {
            this.completeConstruction(constructionIP);
        }
        if ((this.world.func_72820_D() + (long)this.hashCode()) % 20L == 3L) {
            this.updateConstructionQueue(false);
        }
        this.checkSeller();
        this.checkWorkers();
        if ((this.world.func_72820_D() + (long)this.hashCode()) % 10L == 0L) {
            this.checkBattleStatus();
        }
        if ((this.world.func_72820_D() + (long)this.hashCode()) % 10L == 5L) {
            this.killMobs();
        }
        if (!this.declaredPos && this.world != null) {
            if (this.villageType.lonebuilding) {
                this.mw.registerLoneBuildingsLocation(this.world, this.getPos(), this.getVillageQualifiedName(), this.villageType, this.culture, false, null);
            } else {
                this.mw.registerVillageLocation(this.world, this.getPos(), this.getVillageQualifiedName(), this.villageType, this.culture, false, null);
            }
            this.declaredPos = true;
        }
        if (this.lastVillagerRecordsRepair == 0L) {
            this.lastVillagerRecordsRepair = this.world.func_72820_D();
        } else if (this.world.func_72820_D() - this.lastVillagerRecordsRepair >= 100L) {
            this.respawnVillagersIfNeeded();
            this.lastVillagerRecordsRepair = this.world.func_72820_D();
        }
        if (this.world.func_72935_r()) {
            this.nightActionPerformed = false;
        } else if (!this.nightActionPerformed) {
            if (!this.villageType.playerControlled && !this.villageType.lonebuilding) {
                for (EntityPlayer entityPlayer : VillageUtilities.getServerPlayers(this.world)) {
                    UserProfile profile2 = VillageUtilities.getServerProfile(this.world, entityPlayer);
                    if (profile2 == null) continue;
                    profile2.adjustDiplomacyPoint(this, 5);
                }
                for (Point point : this.relations.keySet()) {
                    Building village;
                    if (!MillCommonUtilities.chanceOn(10) || (village = this.mw.getBuilding(point)) == null) continue;
                    int relation = this.relations.get(point);
                    int improveChance = relation < -90 ? 0 : (relation < -50 ? 30 : (relation < 0 ? 40 : (relation > 90 ? 100 : (relation > 50 ? 70 : 60))));
                    if (MillCommonUtilities.randomInt(100) < improveChance) {
                        if (this.relations.get(point) >= 100) continue;
                        this.adjustRelation(point, 10 + MillCommonUtilities.randomInt(10), false);
                        ServerSender.sendTranslatedSentenceInRange(this.world, this.getPos(), MillConfigValues.KeepActiveRadius, '2', "ui.relationfriendly", this.getVillageQualifiedName(), village.getVillageQualifiedName(), VillageUtilities.getRelationName(this.relations.get(point)));
                        continue;
                    }
                    if (this.relations.get(point) <= -100) continue;
                    this.adjustRelation(point, -10 - MillCommonUtilities.randomInt(10), false);
                    ServerSender.sendTranslatedSentenceInRange(this.world, this.getPos(), MillConfigValues.KeepActiveRadius, '6', "ui.relationunfriendly", this.getVillageQualifiedName(), village.getVillageQualifiedName(), VillageUtilities.getRelationName(this.relations.get(point)));
                }
            }
            this.nightActionPerformed = true;
        }
        if (this.world.func_72820_D() % 1000L == 0L && (this.villageType.playerControlled || MillConfigValues.DEV) && this.countGoods(MillItems.PARCHMENT_VILLAGE_SCROLL, 0) == 0) {
            for (int i = 0; i < this.mw.villagesList.pos.size(); ++i) {
                Point point = this.mw.villagesList.pos.get(i);
                if (!this.getPos().sameBlock(point)) continue;
                this.storeItemStack(ItemParchment.createParchmentForVillage(this));
            }
        }
        if (this.villageType.playerControlled && this.world.func_72820_D() % 1000L == 0L && this.countGoods(MillItems.NEGATION_WAND) == 0) {
            this.storeGoods(MillItems.NEGATION_WAND, 1);
        }
        if (this.controlledBy != null && this.controlledByName == null && (profile = this.world.func_73046_m().func_152358_ax().func_152652_a(this.controlledBy)) != null) {
            this.controlledByName = profile.getName();
        }
        if (this.world.func_72820_D() % 200L == 0L) {
            this.updateAchievements();
        }
        this.handlePathingResult();
        if (this.autobuildPaths) {
            this.clearOldPaths();
            this.constructCalculatedPaths();
        }
        if (this.marvelManager != null) {
            this.marvelManager.update();
        }
    }

    public void updateWorldInfo() throws MillLog.MillenaireException {
        if (this.villageType == null) {
            MillLog.error(this, "updateWorldInfo: villageType is null");
            return;
        }
        ArrayList<BuildingLocation> locations = new ArrayList<BuildingLocation>();
        for (BuildingLocation l : this.getLocations()) {
            locations.add(l);
        }
        for (ConstructionIP cip : this.getConstructionsInProgress()) {
            if (cip.getBuildingLocation() == null) continue;
            locations.add(cip.getBuildingLocation());
        }
        if (this.winfo.world == null) {
            boolean areaChanged = this.winfo.update(this.world, locations, this.location.pos, this.villageType.radius);
            if (areaChanged) {
                this.rebuildRegionMapper(true);
            }
        } else {
            this.winfo.updateNextChunk();
        }
    }

    private void validateVillagerList() {
        ArrayList<Object> found;
        for (MillVillager v : this.getKnownVillagers()) {
            if (v == null) {
                MillLog.error(this, "Null value in villager list");
            }
            if (v.isReallyDead() && MillConfigValues.LogTileEntityBuilding >= 2) {
                MillLog.minor(this, "Villager " + v + " is dead.");
            }
            found = new ArrayList<Object>();
            for (VillagerRecord villagerRecord : this.getVillagerRecords().values()) {
                if (!villagerRecord.matches(v)) continue;
                found.add(villagerRecord);
            }
            if (found.size() == 0) {
                MillLog.error(this, "Villager " + v + " not present in records.");
                continue;
            }
            if (found.size() <= 1) continue;
            MillLog.error(this, "Villager " + v + " present " + found.size() + " times in records: ");
            for (VillagerRecord villagerRecord : found) {
                MillLog.major(this, villagerRecord.toString() + " / " + villagerRecord.hashCode());
            }
        }
        for (VillagerRecord vr : this.getVillagerRecords().values()) {
            found = new ArrayList();
            if (vr.getHousePos() == null) {
                MillLog.error(this, "Record " + vr + " has no house.");
            }
            for (MillVillager millVillager : this.getKnownVillagers()) {
                if (!vr.matches(millVillager)) continue;
                found.add(millVillager);
            }
            if (found.size() == vr.nb) continue;
            MillLog.error(this, "Record " + vr + " present " + found.size() + " times in villagers, previously: " + vr.nb + ". Villagers: ");
            for (MillVillager millVillager : found) {
                MillLog.major(this, millVillager.toString() + " / " + millVillager.hashCode());
            }
            vr.nb = found.size();
        }
    }

    private void writePaths() {
        DataOutputStream ds;
        FileOutputStream fos;
        File buildingsDir = MillCommonUtilities.getBuildingsDir(this.world);
        File file1 = new File(buildingsDir, this.getPos().getPathString() + "_paths.bin");
        if (this.pathsToBuild != null) {
            try {
                fos = new FileOutputStream(file1);
                ds = new DataOutputStream(fos);
                ds.writeInt(this.pathsToBuild.size());
                for (List<BuildingBlock> path : this.pathsToBuild) {
                    ds.writeInt(path.size());
                    for (BuildingBlock b : path) {
                        ds.writeInt(b.p.getiX());
                        ds.writeShort(b.p.getiY());
                        ds.writeInt(b.p.getiZ());
                        ds.writeInt(Block.func_149682_b((Block)b.block));
                        ds.writeByte(b.getMeta());
                        ds.writeByte(b.special);
                    }
                }
                ds.close();
                fos.close();
            }
            catch (IOException e) {
                MillLog.printException("Error when writing pathsToBuild: ", e);
            }
        } else {
            file1.renameTo(new File(buildingsDir, this.getPos().getPathString() + "ToDelete"));
            file1.delete();
        }
        file1 = new File(buildingsDir, this.getPos().getPathString() + "_pathstoclear.bin");
        if (this.oldPathPointsToClear != null) {
            try {
                fos = new FileOutputStream(file1);
                ds = new DataOutputStream(fos);
                ds.writeInt(this.oldPathPointsToClear.size());
                for (Point p : this.oldPathPointsToClear) {
                    ds.writeInt(p.getiX());
                    ds.writeShort(p.getiY());
                    ds.writeInt(p.getiZ());
                }
                ds.close();
                fos.close();
            }
            catch (IOException e) {
                MillLog.printException("Error when writing oldPathPointsToClear: ", e);
            }
        } else {
            file1.delete();
        }
        this.pathsChanged = false;
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        if (this.location == null) {
            MillLog.error(this, "Null location. Skipping write.");
            return;
        }
        nbttagcompound.func_74778_a("versionCompatibility", versionCompatibility);
        try {
            Set<String> tags;
            this.pos.write(nbttagcompound, "pos");
            this.location.writeToNBT(nbttagcompound, "buildingLocation", "self");
            nbttagcompound.func_74757_a("chestLocked", this.chestLocked);
            if (this.name != null && this.name.length() > 0) {
                nbttagcompound.func_74778_a("name", this.name);
            }
            nbttagcompound.func_74778_a("qualifier", this.getQualifier());
            nbttagcompound.func_74757_a("isTownhall", this.isTownhall);
            nbttagcompound.func_74778_a("culture", this.culture.key);
            if (this.villageType != null) {
                nbttagcompound.func_74778_a("villageType", this.villageType.key);
            }
            if (this.controlledBy != null) {
                nbttagcompound.func_186854_a("controlledByUUID", this.controlledBy);
            }
            if (this.getTownHallPos() != null) {
                this.getTownHallPos().write(nbttagcompound, "townHallPos");
            }
            nbttagcompound.func_74757_a("nightActionPerformed", this.nightActionPerformed);
            nbttagcompound.func_74757_a("nightBackgroundActionPerformed", this.nightBackgroundActionPerformed);
            nbttagcompound.func_74768_a("nbAnimalsRespawned", this.nbAnimalsRespawned);
            NBTTagList nbttaglist = new NBTTagList();
            for (Point p : this.buildings) {
                NBTTagCompound nbttagcompound12 = new NBTTagCompound();
                p.write(nbttagcompound12, "pos");
                nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
            }
            nbttagcompound.func_74782_a("buildings", (NBTBase)nbttaglist);
            for (ConstructionIP cip : this.getConstructionsInProgress()) {
                nbttagcompound.func_74768_a("bblocksPos_" + cip.getId(), cip.getBblocksPos());
                if (!cip.isBblocksChanged()) continue;
                cip.writeBblocks();
                if (MillConfigValues.LogHybernation < 1) continue;
                MillLog.major(this, "Saved bblocks.");
            }
            nbttaglist = new NBTTagList();
            for (Iterator<Object> iterator : BuildingProject.EnumProjects.values()) {
                if (!this.buildingProjects.containsKey(iterator)) continue;
                CopyOnWriteArrayList<BuildingProject> projectsLevel = this.buildingProjects.get(iterator);
                for (BuildingProject project : projectsLevel) {
                    if (project.location == null || project.location.isSubBuildingLocation) continue;
                    NBTTagCompound nbttagcompound13 = new NBTTagCompound();
                    project.location.writeToNBT(nbttagcompound13, "location", "buildingProjects");
                    nbttaglist.func_74742_a((NBTBase)nbttagcompound13);
                    if (MillConfigValues.LogHybernation < 1) continue;
                    MillLog.major(this, "Writing building location: " + project.location + " (level: " + project.location.level + ", variation: " + project.location.getVariation() + ")");
                }
                for (BuildingProject project : projectsLevel) {
                    if (project.location == null || !project.location.isSubBuildingLocation) continue;
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    project.location.writeToNBT(nbttagcompound1, "location", "buildingProjects");
                    nbttaglist.func_74742_a((NBTBase)nbttagcompound1);
                    if (MillConfigValues.LogHybernation < 1) continue;
                    MillLog.major(this, "Writing building location: " + project.location + " (level: " + project.location.level + ", variation: " + project.location.getVariation() + ")");
                }
            }
            nbttagcompound.func_74782_a("locations", (NBTBase)nbttaglist);
            if (this.buildingGoal != null) {
                nbttagcompound.func_74778_a("buildingGoal", this.buildingGoal);
                if (MillConfigValues.LogHybernation >= 1) {
                    MillLog.major(this, "Writing building goal: " + this.buildingGoal);
                }
            }
            nbttagcompound.func_74768_a("buildingGoalLevel", this.buildingGoalLevel);
            nbttagcompound.func_74768_a("buildingGoalVariation", this.buildingGoalVariation);
            if (this.buildingGoalIssue != null) {
                nbttagcompound.func_74778_a("buildingGoalIssue", this.buildingGoalIssue);
            }
            if (this.buildingGoalLocation != null) {
                this.buildingGoalLocation.writeToNBT(nbttagcompound, "buildingGoalLocation", "buildingGoalLocation");
                if (MillConfigValues.LogHybernation >= 1) {
                    MillLog.major(this, "Writing buildingGoalLocation: " + this.buildingGoalLocation);
                }
            }
            nbttagcompound.func_74768_a("nbConstructions", this.getConstructionsInProgress().size());
            for (ConstructionIP cip : this.getConstructionsInProgress()) {
                nbttagcompound.func_74757_a("buildingLocationIP_" + cip.getId() + "_isWall", cip.isWallConstruction());
                if (cip.getBuildingLocation() == null) continue;
                cip.getBuildingLocation().writeToNBT(nbttagcompound, "buildingLocationIP_" + cip.getId(), "buildingLocationIP_" + cip.getId());
                if (MillConfigValues.LogHybernation < 1) continue;
                MillLog.major(this, "Writing buildingLocationIP_" + cip.getId() + ": " + cip.getBuildingLocation());
            }
            nbttaglist = new NBTTagList();
            for (String s : this.visitorsList) {
                NBTTagCompound nbttagcompound14 = new NBTTagCompound();
                nbttagcompound14.func_74778_a("visitor", s);
                nbttaglist.func_74742_a((NBTBase)nbttagcompound14);
            }
            nbttagcompound.func_74782_a("visitorsList", (NBTBase)nbttaglist);
            nbttaglist = new NBTTagList();
            for (String s : this.buildingsBought) {
                NBTTagCompound nbttagcompound15 = new NBTTagCompound();
                nbttagcompound15.func_74778_a("key", s);
                nbttaglist.func_74742_a((NBTBase)nbttagcompound15);
            }
            nbttagcompound.func_74782_a("buildingsBought", (NBTBase)nbttaglist);
            nbttagcompound.func_74757_a("updateRaidPerformed", this.updateRaidPerformed);
            nbttagcompound.func_74757_a("nightBackgroundActionPerformed", this.nightBackgroundActionPerformed);
            nbttagcompound.func_74757_a("nightActionPerformed", this.nightActionPerformed);
            nbttagcompound.func_74757_a("underAttack", this.underAttack);
            if (this.raidTarget != null) {
                this.raidTarget.write(nbttagcompound, "raidTarget");
                nbttagcompound.func_74772_a("raidPlanningStart", this.raidPlanningStart);
                nbttagcompound.func_74772_a("raidStart", this.raidStart);
            }
            nbttaglist = new NBTTagList();
            for (String s : this.raidsPerformed) {
                NBTTagCompound nbttagcompound16 = new NBTTagCompound();
                nbttagcompound16.func_74778_a("raid", s);
                nbttaglist.func_74742_a((NBTBase)nbttagcompound16);
            }
            nbttagcompound.func_74782_a("raidsPerformed", (NBTBase)nbttaglist);
            nbttaglist = new NBTTagList();
            for (String s : this.raidsSuffered) {
                NBTTagCompound nbttagcompound17 = new NBTTagCompound();
                nbttagcompound17.func_74778_a("raid", s);
                nbttaglist.func_74742_a((NBTBase)nbttagcompound17);
            }
            nbttagcompound.func_74782_a("raidsTaken", (NBTBase)nbttaglist);
            if (this.villageType != null && !this.villageType.lonebuilding) {
                nbttaglist = new NBTTagList();
                for (Point p : this.relations.keySet()) {
                    Building dv = this.mw.getBuilding(p);
                    if (dv != null && dv.villageType == null) {
                        MillLog.error(dv, "No village type!");
                        continue;
                    }
                    if (dv == null || dv.villageType.lonebuilding) continue;
                    NBTTagCompound nBTTagCompound = new NBTTagCompound();
                    p.write(nBTTagCompound, "pos");
                    nBTTagCompound.func_74768_a("value", this.relations.get(p).intValue());
                    nbttaglist.func_74742_a((NBTBase)nBTTagCompound);
                }
                nbttagcompound.func_74782_a("relations", (NBTBase)nbttaglist);
            }
            if (this.parentVillage != null) {
                this.parentVillage.write(nbttagcompound, "parentVillage");
            }
            nbttaglist = MillCommonUtilities.writeInventory(this.imported);
            nbttagcompound.func_74782_a("importedGoodsNew", (NBTBase)nbttaglist);
            nbttaglist = MillCommonUtilities.writeInventory(this.exported);
            nbttagcompound.func_74782_a("exportedGoodsNew", (NBTBase)nbttaglist);
            if (MillConfigValues.LogTileEntityBuilding >= 3) {
                MillLog.debug(this, "Saving building. Location: " + this.location + ", pos: " + this.getPos());
            }
            nbttaglist = new NBTTagList();
            for (Point p : this.subBuildings) {
                NBTTagCompound nbttagcompound18 = new NBTTagCompound();
                p.write(nbttagcompound18, "pos");
                nbttaglist.func_74742_a((NBTBase)nbttagcompound18);
            }
            nbttagcompound.func_74782_a("subBuildings", (NBTBase)nbttaglist);
            if (this.pujas != null) {
                NBTTagCompound tag = new NBTTagCompound();
                this.pujas.writeToNBT(tag);
                nbttagcompound.func_74782_a("pujas", (NBTBase)tag);
            }
            nbttagcompound.func_74772_a("lastGoodsRefresh", this.lastGoodsRefresh);
            nbttagcompound.func_74768_a("pathsToBuildIndex", this.pathsToBuildIndex);
            nbttagcompound.func_74768_a("pathsToBuildPathIndex", this.pathsToBuildPathIndex);
            nbttagcompound.func_74768_a("oldPathPointsToClearIndex", this.oldPathPointsToClearIndex);
            if (this.brickColourTheme != null) {
                nbttagcompound.func_74778_a("brickColourTheme", this.brickColourTheme.key);
            }
            if ((tags = this.getTags()).size() > 0 && MillConfigValues.LogTags >= 1) {
                MillLog.major(this, "Tags to write: " + MillCommonUtilities.flattenStrings(tags));
            }
            nbttaglist = new NBTTagList();
            for (String tag : tags) {
                NBTTagCompound nBTTagCompound = new NBTTagCompound();
                nBTTagCompound.func_74778_a("value", tag);
                nbttaglist.func_74742_a((NBTBase)nBTTagCompound);
                if (MillConfigValues.LogTags < 3) continue;
                MillLog.debug(this, "Writing tag: " + tag);
            }
            nbttagcompound.func_74782_a("tags", (NBTBase)nbttaglist);
            this.resManager.writeToNBT(nbttagcompound);
            if (this.marvelManager != null) {
                this.marvelManager.writeToNBT(nbttagcompound);
            }
            if (this.pathsChanged) {
                this.writePaths();
            }
            if (this.isTownhall && this.bannerStack != null) {
                nbttagcompound.func_74782_a("bannerStack", (NBTBase)this.bannerStack.func_77955_b(new NBTTagCompound()));
            }
        }
        catch (Exception e) {
            Mill.proxy.sendChatAdmin("Error when trying to save building. Check millenaire.log.");
            MillLog.error(this, "Exception in Villager.onUpdate(): ");
            MillLog.printException(e);
        }
    }

    private class SaveWorker
    extends Thread {
        private final String reason;

        private SaveWorker(String reason) {
            this.reason = reason;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            if (!Building.this.isTownhall) {
                return;
            }
            Building building = Building.this;
            synchronized (building) {
                File buildingsDir;
                long startTime = System.currentTimeMillis();
                NBTTagCompound mainTag = new NBTTagCompound();
                NBTTagList nbttaglist = new NBTTagList();
                for (int i = 0; i < Building.this.buildings.size(); ++i) {
                    Building b;
                    Point p = Building.this.buildings.get(i);
                    if (p == null || (b = Building.this.mw.getBuilding(p)) == null) continue;
                    NBTTagCompound buildingTag = new NBTTagCompound();
                    b.writeToNBT(buildingTag);
                    nbttaglist.func_74742_a((NBTBase)buildingTag);
                }
                mainTag.func_74782_a("buildings", (NBTBase)nbttaglist);
                File millenaireDir = Building.this.mw.millenaireDir;
                if (!millenaireDir.exists()) {
                    millenaireDir.mkdir();
                }
                if (!(buildingsDir = new File(millenaireDir, "buildings")).exists()) {
                    buildingsDir.mkdir();
                }
                File tempFile = new File(buildingsDir, Building.this.getPos().getPathString() + "_temp.gz");
                try {
                    FileOutputStream fileoutputstream = new FileOutputStream(tempFile);
                    CompressedStreamTools.func_74799_a((NBTTagCompound)mainTag, (OutputStream)fileoutputstream);
                    fileoutputstream.flush();
                    fileoutputstream.close();
                    Path finalPath = new File(buildingsDir, Building.this.getPos().getPathString() + ".gz").toPath();
                    Files.move(tempFile.toPath(), finalPath, StandardCopyOption.REPLACE_EXISTING);
                }
                catch (IOException e) {
                    MillLog.printException(e);
                }
                if (MillConfigValues.LogHybernation >= 1) {
                    MillLog.major(Building.this, "Saved " + Building.this.buildings.size() + " buildings in " + (System.currentTimeMillis() - startTime) + " ms due to " + this.reason + " (" + Building.this.saveReason + ").");
                }
                Building.this.lastSaved = Building.this.world.func_72820_D();
                Building.this.saveNeeded = false;
                Building.this.saveReason = null;
                Building.this.saveWorker = null;
            }
        }
    }

    public class RegionMapperThread
    extends Thread {
        VillageMapInfo winfo;

        public RegionMapperThread(VillageMapInfo wi) {
            this.winfo = wi;
        }

        @Override
        public void run() {
            RegionMapper temp = new RegionMapper();
            if (MillConfigValues.LogPathing >= 1) {
                MillLog.major(this, "Start");
            }
            long tm = System.currentTimeMillis();
            try {
                if (temp.createConnectionsTable(this.winfo, Building.this.resManager.getSleepingPos())) {
                    Building.this.regionMapper = temp;
                    Building.this.lastPathingUpdate = Building.this.world.func_72820_D();
                } else {
                    Building.this.lastPathingUpdate = Building.this.world.func_72820_D();
                    Building.this.regionMapper = null;
                }
            }
            catch (MillLog.MillenaireException e) {
                MillLog.printException(e);
            }
            if (MillConfigValues.LogPathing >= 1) {
                MillLog.major(this, "Done: " + (double)(System.currentTimeMillis() - tm) * 1.0 / 1000.0);
            }
            Building.this.rebuildingRegionMapper = false;
        }
    }

    private class PathCreatorQueue {
        private final List<PathCreator> pathCreators = new ArrayList<PathCreator>();
        private final List<List<AStarNode>> pathsReceived = new ArrayList<List<AStarNode>>();
        int nbAnswers = 0;
        int pos = 0;

        PathCreatorQueue() {
        }

        public void addFailedPath() {
            this.pathsReceived.add(null);
            ++this.nbAnswers;
            if (this.isComplete()) {
                this.sendNewPathsToBuilding();
            } else {
                this.startNextPath();
            }
        }

        public void addPathCreator(PathCreator pathCreator) {
            this.pathCreators.add(pathCreator);
        }

        public void addReceivedPath(List<AStarNode> path) {
            this.pathsReceived.add(path);
            ++this.nbAnswers;
            if (this.isComplete()) {
                this.sendNewPathsToBuilding();
            } else {
                this.startNextPath();
            }
        }

        public boolean isComplete() {
            return this.pathCreators.size() == this.nbAnswers;
        }

        private void sendNewPathsToBuilding() {
            Building.this.pathQueue = this;
        }

        public void startNextPath() {
            block3: {
                if (this.pos < this.pathCreators.size()) {
                    PathCreator pathCreator = this.pathCreators.get(this.pos);
                    ++this.pos;
                    AStarPathPlannerJPS jpsPathPlanner = new AStarPathPlannerJPS(Building.this.world, pathCreator, false);
                    try {
                        jpsPathPlanner.getPath(pathCreator.startPos.getiX(), pathCreator.startPos.getiY(), pathCreator.startPos.getiZ(), pathCreator.endPos.getiX(), pathCreator.endPos.getiY(), pathCreator.endPos.getiZ(), PATH_BUILDER_JPS_CONFIG);
                    }
                    catch (ThreadSafeUtilities.ChunkAccessException e) {
                        if (MillConfigValues.LogChunkLoader < 1) break block3;
                        MillLog.major(this, "Chunk access violation while calculating new path.");
                    }
                }
            }
        }
    }

    private class PathCreator
    implements IAStarPathedEntity {
        final PathCreatorQueue queue;
        final InvItem pathConstructionGood;
        final int pathWidth;
        final Building destination;
        final Point startPos;
        final Point endPos;

        PathCreator(PathCreatorQueue info, InvItem pathConstructionGood, int pathWidth, Building destination, Point startPos, Point endPos) {
            this.pathConstructionGood = pathConstructionGood;
            this.pathWidth = pathWidth;
            this.destination = destination;
            this.queue = info;
            this.startPos = startPos;
            this.endPos = endPos;
        }

        @Override
        public void onFoundPath(List<AStarNode> result) {
            if (this.queue.isComplete()) {
                MillLog.error(Building.this, "onFoundPath triggered on completed info object.");
                return;
            }
            this.queue.addReceivedPath(result);
        }

        @Override
        public void onNoPathAvailable() {
            if (this.queue.isComplete()) {
                MillLog.error(Building.this, "onNoPathAvailable triggered on completed info object.");
                return;
            }
            if (MillConfigValues.LogVillagePaths >= 2) {
                MillLog.minor(Building.this, "Path calculation failed. Target: " + this.destination);
            }
            this.queue.addFailedPath();
        }
    }
}

