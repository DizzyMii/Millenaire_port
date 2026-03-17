/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.BlockSapling
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.tileentity.TileEntitySign
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 */
package org.millenaire.common.buildingplan;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.buildingplan.BuildingFileFiler;
import org.millenaire.common.buildingplan.IBuildingPlan;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.virtualdir.VirtualDir;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;

public class BuildingCustomPlan
implements IBuildingPlan {
    public final Culture culture;
    public String nativeName;
    public String shop = null;
    public String buildingKey;
    public String gameNameKey = null;
    public final Map<String, String> names = new HashMap<String, String>();
    public List<String> maleResident = new ArrayList<String>();
    public List<String> femaleResident = new ArrayList<String>();
    public List<String> visitors = new ArrayList<String>();
    public int priorityMoveIn = 1;
    public int radius = 6;
    public int heightRadius = 4;
    public List<String> tags = new ArrayList<String>();
    public ResourceLocation cropType = null;
    public ResourceLocation spawnType = null;
    public Map<TypeRes, Integer> minResources = new HashMap<TypeRes, Integer>();
    public Map<TypeRes, Integer> maxResources = new HashMap<TypeRes, Integer>();

    public static Map<String, BuildingCustomPlan> loadCustomBuildings(VirtualDir cultureVirtualDir, Culture culture) {
        HashMap<String, BuildingCustomPlan> buildingCustoms = new HashMap<String, BuildingCustomPlan>();
        VirtualDir customBuildingsVirtualDir = cultureVirtualDir.getChildDirectory("custombuildings");
        BuildingFileFiler textFiles = new BuildingFileFiler(".txt");
        for (File file : customBuildingsVirtualDir.listFilesRecursive(textFiles)) {
            try {
                if (MillConfigValues.LogBuildingPlan >= 1) {
                    MillLog.major(file, "Loaded custom building");
                }
                BuildingCustomPlan buildingCustom = new BuildingCustomPlan(file, culture);
                buildingCustoms.put(buildingCustom.buildingKey, buildingCustom);
            }
            catch (Exception e) {
                MillLog.printException("Error when loading " + file.getAbsolutePath(), e);
            }
        }
        return buildingCustoms;
    }

    public BuildingCustomPlan(Culture culture, String key) {
        this.culture = culture;
        this.buildingKey = key;
    }

    public BuildingCustomPlan(File file, Culture culture) throws IOException {
        this.culture = culture;
        this.buildingKey = file.getName().split("\\.")[0];
        BufferedReader reader = MillCommonUtilities.getReader(file);
        String line = reader.readLine();
        this.readConfigLine(line);
        if (MillConfigValues.LogBuildingPlan >= 1) {
            MillLog.major(this, "Loaded custom building " + this.buildingKey + this.nativeName + " pop: " + this.maleResident + "/" + this.femaleResident);
        }
        if (!this.minResources.containsKey((Object)TypeRes.SIGN)) {
            MillLog.error(this, "No signs in custom building.");
        }
    }

    private void adjustLocationSize(BuildingLocation location, Map<TypeRes, List<Point>> resources) {
        int startX = location.pos.getiX();
        int startY = location.pos.getiY();
        int startZ = location.pos.getiZ();
        int endX = location.pos.getiX();
        int endY = location.pos.getiY();
        int endZ = location.pos.getiZ();
        for (TypeRes type : resources.keySet()) {
            for (Point p : resources.get((Object)type)) {
                if (startX >= p.getiX()) {
                    startX = p.getiX();
                }
                if (startY >= p.getiY()) {
                    startY = p.getiY();
                }
                if (startZ >= p.getiZ()) {
                    startZ = p.getiZ();
                }
                if (endX <= p.getiX()) {
                    endX = p.getiX();
                }
                if (endY <= p.getiY()) {
                    endY = p.getiY();
                }
                if (endZ > p.getiZ()) continue;
                endZ = p.getiZ();
            }
        }
        location.minx = startX - 1;
        location.maxx = endX + 1;
        location.miny = startY - 1;
        location.maxy = endY + 1;
        location.minz = startZ - 1;
        location.maxz = endZ + 1;
        location.length = location.maxx - location.minx + 1;
        location.width = location.maxz - location.minz + 1;
        location.computeMargins();
    }

    public Map<TypeRes, List<Point>> findResources(World world, Point pos, Building townHall, BuildingLocation currentLocation) {
        HashMap<TypeRes, List<Point>> resources = new HashMap<TypeRes, List<Point>>();
        for (int currentRadius = 0; currentRadius < this.radius; ++currentRadius) {
            for (int y = pos.getiY() - this.heightRadius + 1; y < pos.getiY() + this.heightRadius + 1; ++y) {
                int x;
                int z = pos.getiZ() - currentRadius;
                for (x = pos.getiX() - currentRadius; x <= pos.getiX() + currentRadius; ++x) {
                    this.handlePoint(x, y, z, world, resources, townHall, currentLocation);
                }
                x = pos.getiX() - currentRadius;
                for (z = pos.getiZ() - currentRadius + 1; z <= pos.getiZ() + currentRadius - 1; ++z) {
                    this.handlePoint(x, y, z, world, resources, townHall, currentLocation);
                }
                z = pos.getiZ() + currentRadius;
                for (x = pos.getiX() - currentRadius; x <= pos.getiX() + currentRadius; ++x) {
                    this.handlePoint(x, y, z, world, resources, townHall, currentLocation);
                }
                x = pos.getiX() + currentRadius;
                for (z = pos.getiZ() - currentRadius + 1; z <= pos.getiZ() + currentRadius - 1; ++z) {
                    this.handlePoint(x, y, z, world, resources, townHall, currentLocation);
                }
            }
        }
        return resources;
    }

    @Override
    public Culture getCulture() {
        return this.culture;
    }

    @Override
    public List<String> getFemaleResident() {
        return this.femaleResident;
    }

    public String getFullDisplayName() {
        String name = this.nativeName;
        if (this.getNameTranslated() != null && this.getNameTranslated().length() > 0) {
            name = name + " (" + this.getNameTranslated() + ")";
        }
        return name;
    }

    @Override
    public List<String> getMaleResident() {
        return this.maleResident;
    }

    @Override
    public String getNameTranslated() {
        if (this.culture.canReadBuildingNames()) {
            return this.culture.getCustomBuildingGameName(this);
        }
        return "";
    }

    @Override
    public String getNativeName() {
        return this.nativeName;
    }

    @Override
    public List<String> getVisitors() {
        return this.visitors;
    }

    private void handlePoint(int x, int y, int z, World world, Map<TypeRes, List<Point>> resources, Building townHall, BuildingLocation currentLocation) {
        TypeRes res;
        BuildingLocation locationAtPos;
        Point p = new Point(x, y, z);
        if (!(townHall == null || (locationAtPos = townHall.getLocationAtCoord(p)) != null && locationAtPos.equals(currentLocation))) {
            for (BuildingLocation bl : townHall.getLocations()) {
                if (currentLocation != null && currentLocation.isSameLocation(bl) || !bl.isInsideZone(p)) continue;
                return;
            }
        }
        if ((res = this.identifyRes(world, p)) != null && this.maxResources.containsKey((Object)res)) {
            if (resources.containsKey((Object)res) && resources.get((Object)res).size() < this.maxResources.get((Object)res)) {
                resources.get((Object)res).add(p);
            } else if (!resources.containsKey((Object)res)) {
                ArrayList<Point> points = new ArrayList<Point>();
                points.add(p);
                resources.put(res, points);
            }
        }
    }

    private TypeRes identifyRes(World world, Point p) {
        Block b = p.getBlock(world);
        int meta = p.getMeta(world);
        if (b.equals(Blocks.field_150486_ae) || b.equals((Object)MillBlocks.LOCKED_CHEST)) {
            return TypeRes.CHEST;
        }
        if (b.equals(Blocks.field_150462_ai)) {
            return TypeRes.CRAFT;
        }
        if (b.equals(Blocks.field_150444_as) || b.equals((Object)MillBlocks.PANEL)) {
            return TypeRes.SIGN;
        }
        if (b.equals(Blocks.field_150458_ak)) {
            return TypeRes.FIELD;
        }
        if (b.equals(Blocks.field_150407_cf)) {
            return TypeRes.SPAWN;
        }
        if (b.equals(Blocks.field_150345_g) || (b.equals(Blocks.field_150364_r) || b.equals(Blocks.field_150363_s)) && p.getBelow().getBlock(world).equals(Blocks.field_150346_d)) {
            return TypeRes.SAPLING;
        }
        if (b.equals(Blocks.field_150325_L) && p.getMeta(world) == 4) {
            return TypeRes.STALL;
        }
        if ((b.equals(Blocks.field_150348_b) || b.equals(Blocks.field_150322_A) || b.equals(Blocks.field_150354_m) || b.equals(Blocks.field_150351_n) || b.equals(Blocks.field_150435_aG)) && (p.getAbove().getBlock(world).equals(Blocks.field_150350_a) || p.getRelative(1.0, 0.0, 0.0).getBlock(world).equals(Blocks.field_150350_a) || p.getRelative(-1.0, 0.0, 0.0).getBlock(world).equals(Blocks.field_150350_a) || p.getRelative(0.0, 0.0, 1.0).getBlock(world).equals(Blocks.field_150350_a) || p.getRelative(0.0, 0.0, -1.0).getBlock(world).equals(Blocks.field_150350_a))) {
            return TypeRes.MINING;
        }
        if (b.equals(Blocks.field_150460_al)) {
            return TypeRes.FURNACE;
        }
        if (b.equals((Object)MillBlocks.FIRE_PIT)) {
            return TypeRes.FIRE_PIT;
        }
        if (b.equals(MillBlocks.WET_BRICK) && meta == 0) {
            return TypeRes.MUDBRICK;
        }
        if (b.equals(Blocks.field_150436_aH) && !p.getBelow().getBlock(world).equals(Blocks.field_150436_aH)) {
            return TypeRes.SUGAR;
        }
        if (b.equals(Blocks.field_150325_L) && p.getMeta(world) == 3) {
            return TypeRes.FISHING;
        }
        if (b.equals(Blocks.field_150325_L) && p.getMeta(world) == 0) {
            return TypeRes.SILK;
        }
        if (b.equals(Blocks.field_150325_L) && p.getMeta(world) == 11) {
            Point[] neighbours = new Point[]{p.getRelative(1.0, 0.0, 0.0), p.getRelative(-1.0, 0.0, 0.0), p.getRelative(0.0, 0.0, 1.0), p.getRelative(0.0, 0.0, -1.0)};
            boolean waterAround = true;
            for (Point p2 : neighbours) {
                if (p2.getBlock(world).equals(Blocks.field_150355_j)) continue;
                waterAround = false;
            }
            if (waterAround) {
                return TypeRes.SQUID;
            }
        }
        if (b.equals(Blocks.field_150375_by)) {
            return TypeRes.CACAO;
        }
        return null;
    }

    private void readConfigLine(String line) {
        String[] configs;
        for (String config : configs = line.split(";", -1)) {
            if (config.split(":").length != 2) continue;
            String key = config.split(":")[0].toLowerCase();
            String value = config.split(":")[1];
            if (key.equalsIgnoreCase("moveinpriority")) {
                this.priorityMoveIn = Integer.parseInt(value);
                continue;
            }
            if (key.equalsIgnoreCase("radius")) {
                this.radius = Integer.parseInt(value);
                continue;
            }
            if (key.equalsIgnoreCase("heightradius")) {
                this.heightRadius = Integer.parseInt(value);
                continue;
            }
            if (key.equalsIgnoreCase("native")) {
                this.nativeName = value;
                continue;
            }
            if (key.equalsIgnoreCase("gameNameKey")) {
                this.gameNameKey = value;
                continue;
            }
            if (key.equalsIgnoreCase("cropType")) {
                this.cropType = new ResourceLocation(value);
                continue;
            }
            if (key.equalsIgnoreCase("spawnType")) {
                this.spawnType = new ResourceLocation(value);
                continue;
            }
            if (key.startsWith("name_")) {
                this.names.put(key, value);
                continue;
            }
            if (key.equalsIgnoreCase("male")) {
                if (this.culture.villagerTypes.containsKey(value.toLowerCase())) {
                    this.maleResident.add(value.toLowerCase());
                    continue;
                }
                MillLog.error(this, "Attempted to load unknown male villager: " + value);
                continue;
            }
            if (key.equalsIgnoreCase("female")) {
                if (this.culture.villagerTypes.containsKey(value.toLowerCase())) {
                    this.femaleResident.add(value.toLowerCase());
                    continue;
                }
                MillLog.error(this, "Attempted to load unknown female villager: " + value);
                continue;
            }
            if (key.equalsIgnoreCase("visitor")) {
                if (this.culture.villagerTypes.containsKey(value.toLowerCase())) {
                    this.visitors.add(value.toLowerCase());
                    continue;
                }
                MillLog.error(this, "Attempted to load unknown visitor: " + value);
                continue;
            }
            if (key.equalsIgnoreCase("shop")) {
                if (this.culture.shopBuys.containsKey(value) || this.culture.shopSells.containsKey(value) || this.culture.shopBuysOptional.containsKey(value)) {
                    this.shop = value;
                    continue;
                }
                MillLog.error(this, "Undefined shop type: " + value);
                continue;
            }
            if (key.equalsIgnoreCase("tag")) {
                this.tags.add(value.toLowerCase());
                continue;
            }
            boolean found = false;
            for (TypeRes typeRes : TypeRes.values()) {
                if (!typeRes.key.equals(key)) continue;
                try {
                    found = true;
                    if (value.contains("-")) {
                        this.minResources.put(typeRes, Integer.parseInt(value.split("-")[0]));
                        this.maxResources.put(typeRes, Integer.parseInt(value.split("-")[1]));
                        continue;
                    }
                    this.minResources.put(typeRes, Integer.parseInt(value));
                    this.maxResources.put(typeRes, Integer.parseInt(value));
                }
                catch (Exception e) {
                    MillLog.printException("Exception while parsing res " + typeRes.key + " in custom file " + this.buildingKey + " of culture " + this.culture.key + ":", e);
                }
            }
            if (found) continue;
            MillLog.error(this, "Could not recognise key on line: " + config);
        }
    }

    public void registerResources(Building building, BuildingLocation location) {
        Map<TypeRes, List<Point>> resources = this.findResources(building.world, location.pos, building.getTownHall(), location);
        this.adjustLocationSize(location, resources);
        building.getResManager().setSleepingPos(location.pos.getAbove());
        location.sleepingPos = location.pos.getAbove();
        if (resources.containsKey((Object)TypeRes.CHEST)) {
            building.getResManager().chests.clear();
            for (Point chestP : resources.get((Object)TypeRes.CHEST)) {
                if (chestP.getBlock(building.world).equals(Blocks.field_150486_ae)) {
                    int meta = chestP.getMeta(building.world);
                    chestP.setBlock(building.world, (Block)MillBlocks.LOCKED_CHEST, meta, false, false);
                }
                building.getResManager().chests.add(chestP);
            }
        }
        if (resources.containsKey((Object)TypeRes.CRAFT) && resources.get((Object)TypeRes.CRAFT).size() > 0) {
            location.craftingPos = resources.get((Object)TypeRes.CRAFT).get(0);
            building.getResManager().setCraftingPos(resources.get((Object)TypeRes.CRAFT).get(0));
        }
        this.registerSigns(building, resources);
        if (this.cropType != null && resources.containsKey((Object)TypeRes.FIELD)) {
            building.getResManager().soils.clear();
            building.getResManager().soilTypes.clear();
            for (Point p : resources.get((Object)TypeRes.FIELD)) {
                building.getResManager().addSoilPoint(this.cropType, p);
            }
        }
        if (this.spawnType != null && resources.containsKey((Object)TypeRes.SPAWN)) {
            building.getResManager().spawns.clear();
            building.getResManager().spawnTypes.clear();
            for (Point p : resources.get((Object)TypeRes.SPAWN)) {
                p.setBlock(building.world, Blocks.field_150350_a, 0, true, false);
                building.getResManager().addSpawnPoint(this.spawnType, p);
            }
        }
        if (resources.containsKey((Object)TypeRes.SAPLING)) {
            building.getResManager().woodspawn.clear();
            for (Point p : resources.get((Object)TypeRes.SAPLING)) {
                building.getResManager().woodspawn.add(p);
                IBlockState bs = building.world.func_180495_p(p.getBlockPos());
                if (bs.func_177230_c() == Blocks.field_150345_g) {
                    if (bs.func_177229_b((IProperty)BlockSapling.field_176480_a) == BlockPlanks.EnumType.OAK) {
                        building.getResManager().woodspawnTypes.put(p, "oakspawn");
                        continue;
                    }
                    if (bs.func_177229_b((IProperty)BlockSapling.field_176480_a) == BlockPlanks.EnumType.SPRUCE) {
                        building.getResManager().woodspawnTypes.put(p, "pinespawn");
                        continue;
                    }
                    if (bs.func_177229_b((IProperty)BlockSapling.field_176480_a) == BlockPlanks.EnumType.BIRCH) {
                        building.getResManager().woodspawnTypes.put(p, "birchspawn");
                        continue;
                    }
                    if (bs.func_177229_b((IProperty)BlockSapling.field_176480_a) == BlockPlanks.EnumType.JUNGLE) {
                        building.getResManager().woodspawnTypes.put(p, "junglespawn");
                        continue;
                    }
                    if (bs.func_177229_b((IProperty)BlockSapling.field_176480_a) == BlockPlanks.EnumType.ACACIA) {
                        building.getResManager().woodspawnTypes.put(p, "acaciaspawn");
                        continue;
                    }
                    if (bs.func_177229_b((IProperty)BlockSapling.field_176480_a) != BlockPlanks.EnumType.DARK_OAK) continue;
                    building.getResManager().woodspawnTypes.put(p, "darkoakspawn");
                    continue;
                }
                if (bs.func_177230_c() == MillBlocks.SAPLING_APPLETREE) {
                    building.getResManager().woodspawnTypes.put(p, "appletreespawn");
                    continue;
                }
                if (bs.func_177230_c() == MillBlocks.SAPLING_OLIVETREE) {
                    building.getResManager().woodspawnTypes.put(p, "olivetreespawn");
                    continue;
                }
                if (bs.func_177230_c() == MillBlocks.SAPLING_PISTACHIO) {
                    building.getResManager().woodspawnTypes.put(p, "pistachiotreespawn");
                    continue;
                }
                if (bs.func_177230_c() == MillBlocks.SAPLING_CHERRY) {
                    building.getResManager().woodspawnTypes.put(p, "cherrytreespawn");
                    continue;
                }
                if (bs.func_177230_c() != MillBlocks.SAPLING_SAKURA) continue;
                building.getResManager().woodspawnTypes.put(p, "sakuratreespawn");
            }
        }
        if (resources.containsKey((Object)TypeRes.STALL)) {
            building.getResManager().stalls.clear();
            for (Point p : resources.get((Object)TypeRes.STALL)) {
                p.setBlock(building.world, Blocks.field_150350_a, 0, true, false);
                building.getResManager().stalls.add(p);
            }
        }
        if (resources.containsKey((Object)TypeRes.MINING)) {
            building.getResManager().sources.clear();
            building.getResManager().sourceTypes.clear();
            for (Point p : resources.get((Object)TypeRes.MINING)) {
                building.getResManager().addSourcePoint(p.getBlockActualState(building.world), p);
            }
        }
        if (resources.containsKey((Object)TypeRes.FURNACE)) {
            building.getResManager().furnaces.clear();
            for (Point p : resources.get((Object)TypeRes.FURNACE)) {
                building.getResManager().furnaces.add(p);
            }
        }
        if (resources.containsKey((Object)TypeRes.FIRE_PIT)) {
            building.getResManager().firepits.clear();
            for (Point p : resources.get((Object)TypeRes.FIRE_PIT)) {
                building.getResManager().firepits.add(p);
            }
        }
        if (resources.containsKey((Object)TypeRes.MUDBRICK)) {
            building.getResManager().brickspot.clear();
            for (Point p : resources.get((Object)TypeRes.MUDBRICK)) {
                building.getResManager().brickspot.add(p);
            }
        }
        if (resources.containsKey((Object)TypeRes.SUGAR)) {
            building.getResManager().sugarcanesoils.clear();
            for (Point p : resources.get((Object)TypeRes.SUGAR)) {
                building.getResManager().sugarcanesoils.add(p);
            }
        }
        if (resources.containsKey((Object)TypeRes.FISHING)) {
            building.getResManager().fishingspots.clear();
            for (Point p : resources.get((Object)TypeRes.FISHING)) {
                p.setBlock(building.world, Blocks.field_150350_a, 0, true, false);
                building.getResManager().fishingspots.add(p);
            }
        }
        if (resources.containsKey((Object)TypeRes.SILK)) {
            building.getResManager().silkwormblock.clear();
            for (Point p : resources.get((Object)TypeRes.SILK)) {
                p.setBlock(building.world, MillBlocks.SILK_WORM, 0, true, false);
                building.getResManager().silkwormblock.add(p);
            }
        }
        if (resources.containsKey((Object)TypeRes.SNAILS)) {
            building.getResManager().snailsoilblock.clear();
            for (Point p : resources.get((Object)TypeRes.SNAILS)) {
                p.setBlock(building.world, MillBlocks.SNAIL_SOIL, 0, true, false);
                building.getResManager().snailsoilblock.add(p);
            }
        }
        if (resources.containsKey((Object)TypeRes.SQUID)) {
            int squidSpawnPos = -1;
            for (int i = 0; i < building.getResManager().spawnTypes.size(); ++i) {
                if (!building.getResManager().spawnTypes.get(i).equals((Object)new ResourceLocation("Squid"))) continue;
                squidSpawnPos = i;
            }
            if (squidSpawnPos > -1) {
                building.getResManager().spawns.get(squidSpawnPos).clear();
            }
            for (Point p : resources.get((Object)TypeRes.SQUID)) {
                p.setBlock(building.world, (Block)Blocks.field_150355_j, 0, true, false);
                building.getResManager().addSpawnPoint(new ResourceLocation("Squid"), p);
            }
        }
        if (resources.containsKey((Object)TypeRes.CACAO)) {
            int cocoaSoilPos = -1;
            for (int i = 0; i < building.getResManager().soilTypes.size(); ++i) {
                if (!building.getResManager().soilTypes.get(i).equals((Object)Mill.CROP_CACAO)) continue;
                cocoaSoilPos = i;
            }
            if (cocoaSoilPos > -1) {
                building.getResManager().soils.get(cocoaSoilPos).clear();
            }
            for (Point p : resources.get((Object)TypeRes.CACAO)) {
                building.getResManager().addSoilPoint(Mill.CROP_CACAO, p);
            }
        }
        this.updateTags(building);
    }

    private void registerSigns(Building building, Map<TypeRes, List<Point>> resources) {
        building.getResManager().signs.clear();
        HashMap<Integer, Point> signsWithPos = new HashMap<Integer, Point>();
        ArrayList<Point> otherSigns = new ArrayList<Point>();
        if (resources.containsKey((Object)TypeRes.SIGN)) {
            for (Point signP : resources.get((Object)TypeRes.SIGN)) {
                TileEntitySign signEntity = signP.getSign(building.world);
                int signPos = -1;
                if (signEntity != null) {
                    try {
                        signPos = Integer.parseInt(signEntity.field_145915_a[0].func_150260_c()) - 1;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                if (signPos > -1 && !signsWithPos.containsKey(signPos)) {
                    signsWithPos.put(signPos, signP);
                } else {
                    otherSigns.add(signP);
                }
                if (!signP.getBlock(building.world).equals(Blocks.field_150444_as)) continue;
                int meta = signP.getMeta(building.world);
                signP.setBlock(building.world, (Block)MillBlocks.PANEL, meta, true, false);
            }
        }
        int signNumber = signsWithPos.size() + otherSigns.size();
        for (int i = 0; i < signNumber; ++i) {
            building.getResManager().signs.add(null);
        }
        for (Integer pos : signsWithPos.keySet()) {
            if (pos < signNumber) {
                building.getResManager().signs.set(pos, (Point)signsWithPos.get(pos));
                continue;
            }
            otherSigns.add((Point)signsWithPos.get(pos));
        }
        int posInOthers = 0;
        for (int i = 0; i < signNumber; ++i) {
            if (building.getResManager().signs.get(i) != null) continue;
            building.getResManager().signs.set(i, (Point)otherSigns.get(posInOthers));
            ++posInOthers;
        }
    }

    public String toString() {
        return "custom:" + this.buildingKey + ":" + this.culture.key;
    }

    private void updateTags(Building building) {
        if (!this.tags.isEmpty()) {
            building.addTags(this.tags, this.buildingKey + ": registering new tags");
            if (MillConfigValues.LogTags >= 2) {
                MillLog.minor(this, "Applying tags: " + this.tags.stream().collect(Collectors.joining(", ")) + ", result: " + building.getTags().stream().collect(Collectors.joining(", ")));
            }
        }
    }

    public static enum TypeRes {
        CHEST("chest"),
        CRAFT("craft"),
        SIGN("sign"),
        FIELD("field"),
        SPAWN("spawn"),
        SAPLING("sapling"),
        STALL("stall"),
        MINING("mining"),
        FURNACE("furnace"),
        FIRE_PIT("fire_pit"),
        MUDBRICK("mudbrick"),
        SUGAR("sugar"),
        FISHING("fishing"),
        SILK("silk"),
        SNAILS("snails"),
        SQUID("squid"),
        CACAO("cacao");

        public final String key;

        private TypeRes(String key) {
            this.key = key;
        }
    }
}

