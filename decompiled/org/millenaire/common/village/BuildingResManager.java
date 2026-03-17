/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockCocoa
 *  net.minecraft.block.BlockOldLog
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.BlockSapling
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 */
package org.millenaire.common.village;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.millenaire.common.block.BlockMillSapling;
import org.millenaire.common.block.BlockSilkWorm;
import org.millenaire.common.block.BlockSnailSoil;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.TileEntityLockedChest;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

public class BuildingResManager {
    public CopyOnWriteArrayList<Point> brickspot = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> chests = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> fishingspots = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> sugarcanesoils = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> healingspots = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> furnaces = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> firepits = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> brewingStands = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> signs = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> banners = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> cultureBanners = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<CopyOnWriteArrayList<Point>> sources = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<IBlockState> sourceTypes = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<CopyOnWriteArrayList<Point>> spawns = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<ResourceLocation> spawnTypes = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<CopyOnWriteArrayList<Point>> mobSpawners = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<ResourceLocation> mobSpawnerTypes = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<CopyOnWriteArrayList<Point>> soils = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<ResourceLocation> soilTypes = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> stalls = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> woodspawn = new CopyOnWriteArrayList();
    public ConcurrentHashMap<Point, String> woodspawnTypes = new ConcurrentHashMap();
    public CopyOnWriteArrayList<Point> netherwartsoils = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> silkwormblock = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> snailsoilblock = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList<Point> dispenderUnknownPowder = new CopyOnWriteArrayList();
    private Point sleepingPos = null;
    private Point sellingPos = null;
    private Point craftingPos = null;
    private Point defendingPos = null;
    private Point shelterPos = null;
    private Point pathStartPos = null;
    private Point leasurePos = null;
    private final Building building;

    public BuildingResManager(Building b) {
        this.building = b;
    }

    public void addMobSpawnerPoint(ResourceLocation type, Point p) {
        if (!this.mobSpawnerTypes.contains(type)) {
            CopyOnWriteArrayList<Point> spawnsPoint = new CopyOnWriteArrayList<Point>();
            spawnsPoint.add(p);
            this.mobSpawners.add(spawnsPoint);
            this.mobSpawnerTypes.add(type);
        } else {
            for (int i = 0; i < this.mobSpawnerTypes.size(); ++i) {
                if (!this.mobSpawnerTypes.get(i).equals((Object)type) || this.mobSpawners.get(i).contains(p)) continue;
                this.mobSpawners.get(i).add(p);
            }
        }
    }

    public void addSoilPoint(ResourceLocation type, Point p) {
        if (!this.soilTypes.contains(type)) {
            CopyOnWriteArrayList<Point> spawnsPoint = new CopyOnWriteArrayList<Point>();
            spawnsPoint.add(p);
            this.soils.add(spawnsPoint);
            this.soilTypes.add(type);
        } else {
            for (int i = 0; i < this.soilTypes.size(); ++i) {
                if (!this.soilTypes.get(i).equals((Object)type) || this.soils.get(i).contains(p)) continue;
                this.soils.get(i).add(p);
            }
        }
    }

    public void addSourcePoint(IBlockState blockState, Point p) {
        if (!this.sourceTypes.contains(blockState)) {
            CopyOnWriteArrayList<Point> spawnsPoint = new CopyOnWriteArrayList<Point>();
            spawnsPoint.add(p);
            this.sources.add(spawnsPoint);
            this.sourceTypes.add(blockState);
        } else {
            for (int i = 0; i < this.sourceTypes.size(); ++i) {
                if (!this.sourceTypes.get(i).equals(blockState) || this.sources.get(i).contains(p)) continue;
                this.sources.get(i).add(p);
            }
        }
    }

    public void addSpawnPoint(ResourceLocation type, Point p) {
        if (!this.spawnTypes.contains(type)) {
            CopyOnWriteArrayList<Point> spawnsPoint = new CopyOnWriteArrayList<Point>();
            spawnsPoint.add(p);
            this.spawns.add(spawnsPoint);
            this.spawnTypes.add(type);
        } else {
            for (int i = 0; i < this.spawnTypes.size(); ++i) {
                if (!this.spawnTypes.get(i).equals((Object)type) || this.spawns.get(i).contains(p)) continue;
                this.spawns.get(i).add(p);
            }
        }
    }

    public HashMap<InvItem, Integer> getChestsContent() {
        HashMap<InvItem, Integer> contents = new HashMap<InvItem, Integer>();
        for (Point p : this.chests) {
            TileEntityLockedChest chest = p.getMillChest(this.building.world);
            if (chest == null) continue;
            for (int i = 0; i < chest.func_70302_i_(); ++i) {
                ItemStack stack = chest.func_70301_a(i);
                if (stack == null || stack.func_77973_b() == Items.field_190931_a) continue;
                InvItem key = InvItem.createInvItem(stack);
                if (stack == null) continue;
                if (contents.containsKey(key)) {
                    contents.put(key, stack.func_190916_E() + contents.get(key));
                    continue;
                }
                contents.put(key, stack.func_190916_E());
            }
        }
        return contents;
    }

    public Point getCocoaHarvestLocation() {
        for (int i = 0; i < this.soilTypes.size(); ++i) {
            if (!this.soilTypes.get(i).equals((Object)Mill.CROP_CACAO)) continue;
            for (Point p : this.soils.get(i)) {
                IBlockState state = p.getBlockActualState(this.building.world);
                if (state.func_177230_c() != Blocks.field_150375_by || (Integer)state.func_177229_b((IProperty)BlockCocoa.field_176501_a) < 2) continue;
                return p;
            }
        }
        return null;
    }

    public Point getCocoaPlantingLocation() {
        for (int i = 0; i < this.soilTypes.size(); ++i) {
            if (!this.soilTypes.get(i).equals((Object)Mill.CROP_CACAO)) continue;
            for (Point p : this.soils.get(i)) {
                if (p.getBlock(this.building.world) != Blocks.field_150350_a) continue;
                if (p.getNorth().getBlock(this.building.world) == Blocks.field_150364_r && this.isBlockJungleWood(p.getNorth().getBlockActualState(this.building.world))) {
                    return p;
                }
                if (p.getEast().getBlock(this.building.world) == Blocks.field_150364_r && this.isBlockJungleWood(p.getEast().getBlockActualState(this.building.world))) {
                    return p;
                }
                if (p.getSouth().getBlock(this.building.world) == Blocks.field_150364_r && this.isBlockJungleWood(p.getSouth().getBlockActualState(this.building.world))) {
                    return p;
                }
                if (p.getWest().getBlock(this.building.world) != Blocks.field_150364_r || !this.isBlockJungleWood(p.getWest().getBlockActualState(this.building.world))) continue;
                return p;
            }
        }
        return null;
    }

    public Point getCraftingPos() {
        if (this.craftingPos != null) {
            return this.craftingPos;
        }
        if (this.sellingPos != null) {
            return this.sellingPos;
        }
        return this.sleepingPos;
    }

    public Point getDefendingPos() {
        if (this.defendingPos != null) {
            return this.defendingPos;
        }
        if (this.sellingPos != null) {
            return this.sellingPos;
        }
        return this.sleepingPos;
    }

    public Point getEmptyBrickLocation() {
        if (this.brickspot.size() == 0) {
            return null;
        }
        for (int i = 0; i < this.brickspot.size(); ++i) {
            Point p = this.brickspot.get(i);
            if (WorldUtilities.getBlock(this.building.world, p) != Blocks.field_150350_a) continue;
            return p;
        }
        return null;
    }

    public Point getFullBrickLocation() {
        if (this.brickspot.size() == 0) {
            return null;
        }
        for (int i = 0; i < this.brickspot.size(); ++i) {
            Point p = this.brickspot.get(i);
            if (WorldUtilities.getBlockState(this.building.world, p) != MillBlocks.BS_MUD_BRICK) continue;
            return p;
        }
        return null;
    }

    public Point getLeasurePos() {
        if (this.leasurePos != null) {
            return this.leasurePos;
        }
        return this.getSellingPos();
    }

    public int getNbEmptyBrickLocation() {
        if (this.brickspot.size() == 0) {
            return 0;
        }
        int nb = 0;
        for (int i = 0; i < this.brickspot.size(); ++i) {
            Point p = this.brickspot.get(i);
            if (WorldUtilities.getBlock(this.building.world, p) != Blocks.field_150350_a) continue;
            ++nb;
        }
        return nb;
    }

    public int getNbFullBrickLocation() {
        if (this.brickspot.size() == 0) {
            return 0;
        }
        int nb = 0;
        for (int i = 0; i < this.brickspot.size(); ++i) {
            Point p = this.brickspot.get(i);
            if (WorldUtilities.getBlockState(this.building.world, p) != MillBlocks.BS_MUD_BRICK) continue;
            ++nb;
        }
        return nb;
    }

    public int getNbNetherWartHarvestLocation() {
        if (this.netherwartsoils.size() == 0) {
            return 0;
        }
        int nb = 0;
        for (int i = 0; i < this.netherwartsoils.size(); ++i) {
            Point p = this.netherwartsoils.get(i);
            if (WorldUtilities.getBlock(this.building.world, p.getAbove()) != Blocks.field_150388_bm || WorldUtilities.getBlockMeta(this.building.world, p.getAbove()) < 3) continue;
            ++nb;
        }
        return nb;
    }

    public int getNbNetherWartPlantingLocation() {
        if (this.netherwartsoils.size() == 0) {
            return 0;
        }
        int nb = 0;
        for (int i = 0; i < this.netherwartsoils.size(); ++i) {
            Point p = this.netherwartsoils.get(i);
            if (WorldUtilities.getBlock(this.building.world, p.getAbove()) != Blocks.field_150350_a) continue;
            ++nb;
        }
        return nb;
    }

    public int getNbSilkWormHarvestLocation() {
        if (this.silkwormblock.size() == 0) {
            return 0;
        }
        int nb = 0;
        for (int i = 0; i < this.silkwormblock.size(); ++i) {
            Point p = this.silkwormblock.get(i);
            if (WorldUtilities.getBlockState(this.building.world, p) != MillBlocks.SILK_WORM.func_176223_P().func_177226_a(BlockSilkWorm.PROGRESS, (Comparable)((Object)BlockSilkWorm.EnumType.SILKWORMFULL))) continue;
            ++nb;
        }
        return nb;
    }

    public int getNbSnailSoilHarvestLocation() {
        if (this.snailsoilblock.size() == 0) {
            return 0;
        }
        int nb = 0;
        for (int i = 0; i < this.snailsoilblock.size(); ++i) {
            Point p = this.snailsoilblock.get(i);
            if (WorldUtilities.getBlockState(this.building.world, p) != MillBlocks.SNAIL_SOIL.func_176223_P().func_177226_a(BlockSnailSoil.PROGRESS, (Comparable)((Object)BlockSnailSoil.EnumType.SNAIL_SOIL_FULL))) continue;
            ++nb;
        }
        return nb;
    }

    public int getNbSugarCaneHarvestLocation() {
        if (this.sugarcanesoils.size() == 0) {
            return 0;
        }
        int nb = 0;
        for (int i = 0; i < this.sugarcanesoils.size(); ++i) {
            Point p = this.sugarcanesoils.get(i);
            if (WorldUtilities.getBlock(this.building.world, p.getRelative(0.0, 2.0, 0.0)) != Blocks.field_150436_aH) continue;
            ++nb;
        }
        return nb;
    }

    public int getNbSugarCanePlantingLocation() {
        if (this.sugarcanesoils.size() == 0) {
            return 0;
        }
        int nb = 0;
        for (int i = 0; i < this.sugarcanesoils.size(); ++i) {
            Point p = this.sugarcanesoils.get(i);
            if (WorldUtilities.getBlock(this.building.world, p.getAbove()) != Blocks.field_150350_a) continue;
            ++nb;
        }
        return nb;
    }

    public Point getNetherWartsHarvestLocation() {
        Point p;
        int start;
        int i;
        if (this.netherwartsoils.size() == 0) {
            return null;
        }
        for (i = start = MillCommonUtilities.randomInt(this.netherwartsoils.size()); i < this.netherwartsoils.size(); ++i) {
            p = this.netherwartsoils.get(i);
            if (WorldUtilities.getBlock(this.building.world, p.getAbove()) != Blocks.field_150388_bm || WorldUtilities.getBlockMeta(this.building.world, p.getAbove()) != 3) continue;
            return p;
        }
        for (i = 0; i < start; ++i) {
            p = this.netherwartsoils.get(i);
            if (WorldUtilities.getBlock(this.building.world, p.getAbove()) != Blocks.field_150388_bm || WorldUtilities.getBlockMeta(this.building.world, p.getAbove()) != 3) continue;
            return p;
        }
        return null;
    }

    public Point getNetherWartsPlantingLocation() {
        Point p;
        int start;
        int i;
        if (this.netherwartsoils.size() == 0) {
            return null;
        }
        for (i = start = MillCommonUtilities.randomInt(this.netherwartsoils.size()); i < this.netherwartsoils.size(); ++i) {
            p = this.netherwartsoils.get(i);
            if (WorldUtilities.getBlock(this.building.world, p.getAbove()) != Blocks.field_150350_a || WorldUtilities.getBlock(this.building.world, p) != Blocks.field_150425_aM) continue;
            return p;
        }
        for (i = 0; i < start; ++i) {
            p = this.netherwartsoils.get(i);
            if (WorldUtilities.getBlock(this.building.world, p.getAbove()) != Blocks.field_150350_a || WorldUtilities.getBlock(this.building.world, p) != Blocks.field_150425_aM) continue;
            return p;
        }
        return null;
    }

    public Point getPathStartPos() {
        if (this.pathStartPos != null) {
            return this.pathStartPos;
        }
        return this.getSellingPos();
    }

    public Point getPlantingLocation() {
        for (Point p : this.woodspawn) {
            Block block = WorldUtilities.getBlock(this.building.world, p);
            if (block != Blocks.field_150350_a && block != Blocks.field_150431_aC && (!BlockItemUtilities.isBlockDecorativePlant(block) || block instanceof BlockSapling || block instanceof BlockMillSapling)) continue;
            return p;
        }
        return null;
    }

    public String getPlantingLocationType(Point target) {
        return this.woodspawnTypes.get(target);
    }

    public Point getSellingPos() {
        if (this.sellingPos != null) {
            return this.sellingPos;
        }
        return this.sleepingPos;
    }

    public Point getShelterPos() {
        if (this.shelterPos != null) {
            return this.shelterPos;
        }
        return this.sleepingPos;
    }

    public Point getSilkwormHarvestLocation() {
        Point p;
        int start;
        int i;
        if (this.silkwormblock.size() == 0) {
            return null;
        }
        for (i = start = MillCommonUtilities.randomInt(this.silkwormblock.size()); i < this.silkwormblock.size(); ++i) {
            p = this.silkwormblock.get(i);
            if (WorldUtilities.getBlockState(this.building.world, p) != MillBlocks.SILK_WORM.func_176223_P().func_177226_a(BlockSilkWorm.PROGRESS, (Comparable)((Object)BlockSilkWorm.EnumType.SILKWORMFULL))) continue;
            return p;
        }
        for (i = 0; i < start; ++i) {
            p = this.silkwormblock.get(i);
            if (WorldUtilities.getBlockState(this.building.world, p) != MillBlocks.SILK_WORM.func_176223_P().func_177226_a(BlockSilkWorm.PROGRESS, (Comparable)((Object)BlockSilkWorm.EnumType.SILKWORMFULL))) continue;
            return p;
        }
        return null;
    }

    public Point getSleepingPos() {
        return this.sleepingPos;
    }

    public Point getSnailSoilHarvestLocation() {
        Point p;
        int start;
        int i;
        if (this.snailsoilblock.size() == 0) {
            return null;
        }
        for (i = start = MillCommonUtilities.randomInt(this.snailsoilblock.size()); i < this.snailsoilblock.size(); ++i) {
            p = this.snailsoilblock.get(i);
            if (WorldUtilities.getBlockState(this.building.world, p) != MillBlocks.SNAIL_SOIL.func_176223_P().func_177226_a(BlockSnailSoil.PROGRESS, (Comparable)((Object)BlockSnailSoil.EnumType.SNAIL_SOIL_FULL))) continue;
            return p;
        }
        for (i = 0; i < start; ++i) {
            p = this.snailsoilblock.get(i);
            if (WorldUtilities.getBlockState(this.building.world, p) != MillBlocks.SNAIL_SOIL.func_176223_P().func_177226_a(BlockSnailSoil.PROGRESS, (Comparable)((Object)BlockSnailSoil.EnumType.SNAIL_SOIL_FULL))) continue;
            return p;
        }
        return null;
    }

    public List<Point> getSoilPoints(ResourceLocation type) {
        for (int i = 0; i < this.soilTypes.size(); ++i) {
            if (!this.soilTypes.get(i).equals((Object)type)) continue;
            return this.soils.get(i);
        }
        return null;
    }

    public Point getSugarCaneHarvestLocation() {
        Point p;
        int start;
        int i;
        if (this.sugarcanesoils.size() == 0) {
            return null;
        }
        for (i = start = MillCommonUtilities.randomInt(this.sugarcanesoils.size()); i < this.sugarcanesoils.size(); ++i) {
            p = this.sugarcanesoils.get(i);
            if (WorldUtilities.getBlock(this.building.world, p.getRelative(0.0, 2.0, 0.0)) != Blocks.field_150436_aH) continue;
            return p;
        }
        for (i = 0; i < start; ++i) {
            p = this.sugarcanesoils.get(i);
            if (WorldUtilities.getBlock(this.building.world, p.getRelative(0.0, 2.0, 0.0)) != Blocks.field_150436_aH) continue;
            return p;
        }
        return null;
    }

    public Point getSugarCanePlantingLocation() {
        Point p;
        int start;
        int i;
        if (this.sugarcanesoils.size() == 0) {
            return null;
        }
        for (i = start = MillCommonUtilities.randomInt(this.sugarcanesoils.size()); i < this.sugarcanesoils.size(); ++i) {
            p = this.sugarcanesoils.get(i);
            if (WorldUtilities.getBlock(this.building.world, p.getAbove()) != Blocks.field_150350_a) continue;
            return p;
        }
        for (i = 0; i < start; ++i) {
            p = this.sugarcanesoils.get(i);
            if (WorldUtilities.getBlock(this.building.world, p.getAbove()) != Blocks.field_150350_a) continue;
            return p;
        }
        return null;
    }

    private boolean isBlockJungleWood(IBlockState state) {
        return state.func_177229_b((IProperty)BlockOldLog.field_176301_b) == BlockPlanks.EnumType.JUNGLE;
    }

    public void readDataStream(PacketBuffer ds) throws IOException {
        this.chests = StreamReadWrite.readPointList(ds);
        this.furnaces = StreamReadWrite.readPointList(ds);
        this.firepits = StreamReadWrite.readPointList(ds);
        this.signs = StreamReadWrite.readPointList(ds);
        this.stalls = StreamReadWrite.readPointList(ds);
        this.banners = StreamReadWrite.readPointList(ds);
        this.cultureBanners = StreamReadWrite.readPointList(ds);
        for (Point p : this.chests) {
            TileEntityLockedChest chest = p.getMillChest(this.building.mw.world);
            if (chest == null) continue;
            chest.buildingPos = this.building.getPos();
        }
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        Point p;
        NBTTagList nbttaglist2;
        CopyOnWriteArrayList<Point> v;
        Point p2;
        NBTTagCompound nbttagcompound1;
        int i;
        this.sleepingPos = Point.read(nbttagcompound, "spawnPos");
        this.sellingPos = Point.read(nbttagcompound, "sellingPos");
        this.craftingPos = Point.read(nbttagcompound, "craftingPos");
        this.defendingPos = Point.read(nbttagcompound, "defendingPos");
        this.shelterPos = Point.read(nbttagcompound, "shelterPos");
        this.pathStartPos = Point.read(nbttagcompound, "pathStartPos");
        this.leasurePos = Point.read(nbttagcompound, "leasurePos");
        if (this.sleepingPos == null) {
            this.sleepingPos = this.building.getPos().getAbove();
        }
        NBTTagList nbttaglist = nbttagcompound.func_150295_c("chests", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null || this.chests.contains(p2)) continue;
            this.chests.add(p2);
        }
        if (!this.chests.contains(this.building.getPos())) {
            this.chests.add(0, this.building.getPos());
        }
        nbttaglist = nbttagcompound.func_150295_c("furnaces", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.furnaces.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("firepits", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.firepits.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("brewingStands", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.brewingStands.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("banners", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.banners.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("culturebanners", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.cultureBanners.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("signs", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            this.signs.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("netherwartsoils", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.netherwartsoils.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("silkwormblock", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.silkwormblock.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("snailsoilblock", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.snailsoilblock.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("sugarcanesoils", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.sugarcanesoils.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("fishingspots", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.fishingspots.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("healingspots", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.healingspots.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("stalls", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.stalls.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("woodspawn", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.woodspawn.add(p2);
            String type = nbttagcompound1.func_74779_i("type");
            if (type == null) continue;
            this.woodspawnTypes.put(p2, type);
        }
        nbttaglist = nbttagcompound.func_150295_c("brickspot", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            p2 = Point.read(nbttagcompound1, "pos");
            if (p2 == null) continue;
            this.brickspot.add(p2);
        }
        nbttaglist = nbttagcompound.func_150295_c("spawns", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            ResourceLocation spawnType = new ResourceLocation(nbttagcompound1.func_74779_i("type"));
            this.spawnTypes.add(spawnType);
            CopyOnWriteArrayList<Point> v2 = new CopyOnWriteArrayList<Point>();
            NBTTagList nbttaglist22 = nbttagcompound1.func_150295_c("points", 10);
            for (int j = 0; j < nbttaglist22.func_74745_c(); ++j) {
                NBTTagCompound nbttagcompound2 = nbttaglist22.func_150305_b(j);
                Point p3 = Point.read(nbttagcompound2, "pos");
                if (p3 == null) continue;
                v2.add(p3);
                if (MillConfigValues.LogHybernation < 2) continue;
                MillLog.minor(this, "Loaded spawn point: " + p3);
            }
            this.spawns.add(v2);
            if (MillConfigValues.LogHybernation < 2) continue;
            MillLog.minor(this, "Loaded " + v2.size() + " spawn points for " + this.spawnTypes.get(i));
        }
        nbttaglist = nbttagcompound.func_150295_c("mobspawns", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            this.mobSpawnerTypes.add(new ResourceLocation(nbttagcompound1.func_74779_i("type")));
            v = new CopyOnWriteArrayList<Point>();
            nbttaglist2 = nbttagcompound1.func_150295_c("points", 10);
            for (int j = 0; j < nbttaglist2.func_74745_c(); ++j) {
                NBTTagCompound nbttagcompound2 = nbttaglist2.func_150305_b(j);
                p = Point.read(nbttagcompound2, "pos");
                if (p == null) continue;
                v.add(p);
                if (MillConfigValues.LogHybernation < 2) continue;
                MillLog.minor(this, "Loaded spawn point: " + p);
            }
            this.mobSpawners.add(v);
            if (MillConfigValues.LogHybernation < 2) continue;
            MillLog.minor(this, "Loaded " + v.size() + " mob spawn points for " + this.spawnTypes.get(i));
        }
        nbttaglist = nbttagcompound.func_150295_c("sources", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            if (nbttagcompound1.func_74764_b("block_rl")) {
                Block block = Block.func_149684_b((String)nbttagcompound1.func_74779_i("block_rl"));
                int meta = nbttagcompound1.func_74762_e("block_meta");
                IBlockState blockState = block.func_176203_a(meta);
                this.sourceTypes.add(blockState);
            } else {
                int blockId = nbttagcompound1.func_74762_e("type");
                this.sourceTypes.add(Block.func_149729_e((int)blockId).func_176223_P());
            }
            v = new CopyOnWriteArrayList();
            nbttaglist2 = nbttagcompound1.func_150295_c("points", 10);
            for (int j = 0; j < nbttaglist2.func_74745_c(); ++j) {
                NBTTagCompound nbttagcompound2 = nbttaglist2.func_150305_b(j);
                p = Point.read(nbttagcompound2, "pos");
                if (p == null) continue;
                v.add(p);
                if (MillConfigValues.LogHybernation < 3) continue;
                MillLog.debug(this, "Loaded source point: " + p);
            }
            this.sources.add(v);
            if (MillConfigValues.LogHybernation < 1) continue;
            MillLog.debug(this, "Loaded " + v.size() + " sources points for " + this.sourceTypes.get(i).toString());
        }
        nbttaglist = nbttagcompound.func_150295_c("genericsoils", 10);
        for (i = 0; i < nbttaglist.func_74745_c(); ++i) {
            nbttagcompound1 = nbttaglist.func_150305_b(i);
            ResourceLocation type = new ResourceLocation(nbttagcompound1.func_74779_i("type"));
            nbttaglist2 = nbttagcompound1.func_150295_c("points", 10);
            for (int j = 0; j < nbttaglist2.func_74745_c(); ++j) {
                NBTTagCompound nbttagcompound2 = nbttaglist2.func_150305_b(j);
                p = Point.read(nbttagcompound2, "pos");
                if (p == null) continue;
                this.addSoilPoint(type, p);
            }
        }
        for (Point p4 : this.chests) {
            if (!this.building.world.func_190526_b(p4.getiX() / 16, p4.getiZ() / 16) || p4.getMillChest(this.building.world) == null) continue;
            p4.getMillChest((World)this.building.world).buildingPos = this.building.getPos();
        }
    }

    public void sendBuildingPacket(PacketBuffer data) throws IOException {
        StreamReadWrite.writePointList(this.chests, data);
        StreamReadWrite.writePointList(this.furnaces, data);
        StreamReadWrite.writePointList(this.firepits, data);
        StreamReadWrite.writePointList(this.signs, data);
        StreamReadWrite.writePointList(this.stalls, data);
        StreamReadWrite.writePointList(this.banners, data);
        StreamReadWrite.writePointList(this.cultureBanners, data);
    }

    public void setCraftingPos(Point p) {
        this.craftingPos = p;
    }

    public void setDefendingPos(Point p) {
        this.defendingPos = p;
    }

    public void setLeasurePos(Point p) {
        this.leasurePos = p;
    }

    public void setPathStartPos(Point p) {
        this.pathStartPos = p;
    }

    public void setSellingPos(Point p) {
        this.sellingPos = p;
    }

    public void setShelterPos(Point p) {
        this.shelterPos = p;
    }

    public void setSleepingPos(Point p) {
        this.sleepingPos = p;
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound2;
        NBTTagList nbttaglist2;
        NBTTagCompound nbttagcompound1;
        int i;
        NBTTagCompound nbttagcompound12;
        if (this.sleepingPos != null) {
            this.sleepingPos.write(nbttagcompound, "spawnPos");
        }
        if (this.sellingPos != null) {
            this.sellingPos.write(nbttagcompound, "sellingPos");
        }
        if (this.craftingPos != null) {
            this.craftingPos.write(nbttagcompound, "craftingPos");
        }
        if (this.defendingPos != null) {
            this.defendingPos.write(nbttagcompound, "defendingPos");
        }
        if (this.shelterPos != null) {
            this.shelterPos.write(nbttagcompound, "shelterPos");
        }
        if (this.pathStartPos != null) {
            this.pathStartPos.write(nbttagcompound, "pathStartPos");
        }
        if (this.leasurePos != null) {
            this.leasurePos.write(nbttagcompound, "leasurePos");
        }
        NBTTagList nbttaglist = new NBTTagList();
        for (Point p : this.signs) {
            nbttagcompound12 = new NBTTagCompound();
            if (p != null) {
                p.write(nbttagcompound12, "pos");
            } else {
                nbttagcompound12.func_74780_a("pos_xCoord", 0.0);
                nbttagcompound12.func_74780_a("pos_yCoord", 0.0);
                nbttagcompound12.func_74780_a("pos_zCoord", 0.0);
            }
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("signs", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.netherwartsoils) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("netherwartsoils", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.silkwormblock) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("silkwormblock", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.snailsoilblock) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("snailsoilblock", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.sugarcanesoils) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("sugarcanesoils", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.fishingspots) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("fishingspots", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.healingspots) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("healingspots", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.stalls) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("stalls", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.woodspawn) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            if (this.woodspawnTypes.containsKey(p)) {
                nbttagcompound12.func_74778_a("type", this.woodspawnTypes.get(p));
            }
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("woodspawn", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.brickspot) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("brickspot", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (i = 0; i < this.spawns.size(); ++i) {
            nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.func_74778_a("type", this.spawnTypes.get(i).toString());
            nbttaglist2 = new NBTTagList();
            for (Point p : this.spawns.get(i)) {
                nbttagcompound2 = new NBTTagCompound();
                p.write(nbttagcompound2, "pos");
                nbttaglist2.func_74742_a((NBTBase)nbttagcompound2);
            }
            nbttagcompound1.func_74782_a("points", (NBTBase)nbttaglist2);
            nbttaglist.func_74742_a((NBTBase)nbttagcompound1);
        }
        nbttagcompound.func_74782_a("spawns", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (i = 0; i < this.soilTypes.size(); ++i) {
            nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.func_74778_a("type", this.soilTypes.get(i).toString());
            nbttaglist2 = new NBTTagList();
            for (Point p : this.soils.get(i)) {
                nbttagcompound2 = new NBTTagCompound();
                p.write(nbttagcompound2, "pos");
                nbttaglist2.func_74742_a((NBTBase)nbttagcompound2);
            }
            nbttagcompound1.func_74782_a("points", (NBTBase)nbttaglist2);
            nbttaglist.func_74742_a((NBTBase)nbttagcompound1);
        }
        nbttagcompound.func_74782_a("genericsoils", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (i = 0; i < this.mobSpawners.size(); ++i) {
            nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.func_74778_a("type", this.mobSpawnerTypes.get(i).toString());
            nbttaglist2 = new NBTTagList();
            for (Point p : this.mobSpawners.get(i)) {
                nbttagcompound2 = new NBTTagCompound();
                p.write(nbttagcompound2, "pos");
                nbttaglist2.func_74742_a((NBTBase)nbttagcompound2);
            }
            nbttagcompound1.func_74782_a("points", (NBTBase)nbttaglist2);
            nbttaglist.func_74742_a((NBTBase)nbttagcompound1);
        }
        nbttagcompound.func_74782_a("mobspawns", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (i = 0; i < this.sources.size(); ++i) {
            nbttagcompound1 = new NBTTagCompound();
            nbttagcompound1.func_74778_a("block_rl", this.sourceTypes.get(i).func_177230_c().getRegistryName().toString());
            nbttagcompound1.func_74768_a("block_meta", this.sourceTypes.get(i).func_177230_c().func_176201_c(this.sourceTypes.get(i)));
            nbttaglist2 = new NBTTagList();
            for (Point p : this.sources.get(i)) {
                nbttagcompound2 = new NBTTagCompound();
                p.write(nbttagcompound2, "pos");
                nbttaglist2.func_74742_a((NBTBase)nbttagcompound2);
            }
            nbttagcompound1.func_74782_a("points", (NBTBase)nbttaglist2);
            nbttaglist.func_74742_a((NBTBase)nbttagcompound1);
        }
        nbttagcompound.func_74782_a("sources", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.chests) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("chests", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.furnaces) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("furnaces", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.firepits) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("firepits", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.brewingStands) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("brewingStands", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.banners) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("banners", (NBTBase)nbttaglist);
        nbttaglist = new NBTTagList();
        for (Point p : this.cultureBanners) {
            nbttagcompound12 = new NBTTagCompound();
            p.write(nbttagcompound12, "pos");
            nbttaglist.func_74742_a((NBTBase)nbttagcompound12);
        }
        nbttagcompound.func_74782_a("culturebanners", (NBTBase)nbttaglist);
    }
}

