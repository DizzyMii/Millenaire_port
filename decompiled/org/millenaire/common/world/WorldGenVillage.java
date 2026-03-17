/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.chunk.IChunkProvider
 *  net.minecraft.world.gen.IChunkGenerator
 *  net.minecraftforge.fml.common.IWorldGenerator
 *  net.minecraftforge.fml.relauncher.ReflectionHelper
 */
package org.millenaire.common.world;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.buildingplan.TreeClearer;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.ItemParchment;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.pathing.atomicstryker.RegionMapper;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.village.VillageMapInfo;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;
import org.millenaire.common.world.VillageWallGenerator;

public class WorldGenVillage
implements IWorldGenerator {
    private static final int HAMLET_ATTEMPT_ANGLE_STEPS = 36;
    private static final int CHUNK_DISTANCE_LOAD_TEST = 8;
    private static final int HAMLET_MAX_DISTANCE = 350;
    private static final int HAMLET_MIN_DISTANCE = 250;
    private static final double MINIMUM_USABLE_BLOCK_PERC = 0.7;
    private static Field FIELD_BIOME_NAME = ReflectionHelper.findField(Biome.class, (String)"biomeName", (String)"field_76791_y");
    private static HashSet<Integer> chunkCoordsTried = new HashSet();

    public static boolean generateBedrockLoneBuilding(Point p, World world, VillageType village, Random random, int minDistance, int maxDistance, EntityPlayer player) throws MillLog.MillenaireException {
        if (world.field_72995_K) {
            return false;
        }
        if (WorldGenVillage.isWithinSpawnRadiusProtection(world, village, p)) {
            return false;
        }
        if (village.centreBuilding == null) {
            MillLog.printException(new MillLog.MillenaireException("Tried to create a bedrock lone building without a centre."));
            return false;
        }
        if (MillConfigValues.LogWorldGeneration >= 1) {
            MillLog.major(null, "Generating bedrockbuilding: " + village);
        }
        BuildingPlan plan = village.centreBuilding.getRandomStartingPlan();
        BuildingLocation location = null;
        for (int i = 0; i < 100 && location == null; ++i) {
            int x = minDistance + MillCommonUtilities.randomInt(maxDistance - minDistance);
            int z = minDistance + MillCommonUtilities.randomInt(maxDistance - minDistance);
            if (MillCommonUtilities.chanceOn(2)) {
                x = -x;
            }
            if (MillCommonUtilities.chanceOn(2)) {
                z = -z;
            }
            BuildingPlan.LocationReturn lr = plan.testSpotBedrock(world, p.getiX() + x, p.getiZ() + z);
            location = lr.location;
        }
        if (location == null) {
            MillLog.major(null, "No spot found for: " + village);
            int x = minDistance + MillCommonUtilities.randomInt(maxDistance - minDistance);
            int z = minDistance + MillCommonUtilities.randomInt(maxDistance - minDistance);
            if (MillCommonUtilities.chanceOn(2)) {
                x = -x;
            }
            if (MillCommonUtilities.chanceOn(2)) {
                z = -z;
            }
            location = new BuildingLocation(plan, new Point(p.getiX() + x, 2.0, p.getiZ() + z), 0);
            location.bedrocklevel = true;
        }
        if (WorldGenVillage.isWithinSpawnRadiusProtection(world, village, location.pos)) {
            return false;
        }
        List<BuildingPlan.LocationBuildingPair> lbps = village.centreBuilding.buildLocation(Mill.getMillWorld(world), village, location, true, true, null, false, false, null);
        Building townHallEntity = lbps.get((int)0).building;
        if (MillConfigValues.LogWorldGeneration >= 1) {
            MillLog.major(null, "Registering building: " + townHallEntity);
        }
        townHallEntity.villageType = village;
        townHallEntity.findName(null);
        townHallEntity.initialiseBuildingProjects();
        townHallEntity.registerBuildingLocation(location);
        for (BuildingPlan.LocationBuildingPair lbp : lbps) {
            if (lbp == lbps.get(0)) continue;
            townHallEntity.registerBuildingEntity(lbp.building);
            townHallEntity.registerBuildingLocation(lbp.location);
        }
        townHallEntity.initialiseVillage();
        String playerName = null;
        if (player != null) {
            playerName = player.func_70005_c_();
        }
        Mill.getMillWorld(world).registerLoneBuildingsLocation(world, townHallEntity.getPos(), townHallEntity.getVillageQualifiedName(), townHallEntity.villageType, townHallEntity.culture, true, playerName);
        MillLog.major(null, "Finished bedrock building " + village + " at " + townHallEntity.getPos());
        return true;
    }

    private static boolean isWithinSpawnRadiusProtection(World world, VillageType villageType, Point villagePos) {
        if (MillConfigValues.spawnProtectionRadius == 0) {
            return false;
        }
        int villageRadius = MillConfigValues.VillageRadius;
        if (villageType != null) {
            villageRadius = villageType.radius;
        }
        if (villagePos.horizontalDistanceTo(world.func_175694_M()) < (double)(MillConfigValues.spawnProtectionRadius + villageRadius)) {
            if (MillConfigValues.LogWorldGeneration >= 3) {
                MillLog.debug(null, "Blocking spawn at " + villagePos + ". Distance to spawn: " + villagePos.horizontalDistanceTo(world.func_175694_M()) + ", min acceptable : " + (MillConfigValues.spawnProtectionRadius + villageRadius));
            }
            return true;
        }
        return false;
    }

    private int computeChunkCoordsHash(int x, int z) {
        return (x >> 4) + (z >> 4 << 16);
    }

    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.field_73011_w.getDimension() != 0) {
            return;
        }
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < trace.length; ++i) {
            if (!trace[i].getClassName().equals(this.getClass().getName())) continue;
            return;
        }
        Point generationPoint = new Point(chunkX * 16, 0.0, chunkZ * 16);
        double distanceToSpawn = generationPoint.horizontalDistanceTo(world.func_175694_M());
        float completionRatio = 0.0f;
        if (MillConfigValues.villageSpawnCompletionMaxPercentage > 0 && distanceToSpawn > (double)MillConfigValues.villageSpawnCompletionMinDistance) {
            completionRatio = distanceToSpawn > (double)MillConfigValues.villageSpawnCompletionMaxDistance ? (float)(MillConfigValues.villageSpawnCompletionMaxPercentage / 100) : (float)((double)MillConfigValues.villageSpawnCompletionMaxPercentage * ((distanceToSpawn - (double)MillConfigValues.villageSpawnCompletionMinDistance) / (double)MillConfigValues.villageSpawnCompletionMaxDistance) / 100.0);
            completionRatio = (float)(Math.random() * (double)completionRatio);
        }
        try {
            this.generateVillageAtPoint(world, random, chunkX * 16, 0, chunkZ * 16, null, true, false, true, Integer.MAX_VALUE, null, null, null, completionRatio);
        }
        catch (Exception e) {
            MillLog.printException("Exception when attempting to generate village in " + world + " (dimension: " + world.field_73011_w.getDimension() + ")", e);
        }
    }

    private boolean generateCustomVillage(Point p, World world, VillageType villageType, EntityPlayer player, Random random) throws MillLog.MillenaireException {
        long startTime = System.nanoTime();
        MillWorldData mw = Mill.getMillWorld(world);
        BuildingLocation location = new BuildingLocation(villageType.customCentre, p, true);
        Building townHall = new Building(mw, villageType.culture, villageType, location, true, true, p);
        villageType.customCentre.registerResources(townHall, location);
        townHall.initialise(player, true);
        BuildingProject project = new BuildingProject(villageType.customCentre, location);
        if (!townHall.buildingProjects.containsKey((Object)BuildingProject.EnumProjects.CUSTOMBUILDINGS)) {
            townHall.buildingProjects.put(BuildingProject.EnumProjects.CUSTOMBUILDINGS, new CopyOnWriteArrayList());
        }
        townHall.buildingProjects.get((Object)BuildingProject.EnumProjects.CUSTOMBUILDINGS).add(project);
        townHall.initialiseVillage();
        mw.registerVillageLocation(world, townHall.getPos(), townHall.getVillageQualifiedName(), townHall.villageType, townHall.culture, true, player.func_70005_c_());
        townHall.initialiseRelations(null);
        townHall.updateWorldInfo();
        townHall.storeItemStack(ItemParchment.createParchmentForVillage(townHall.getTownHall()));
        if (MillConfigValues.LogWorldGeneration >= 1) {
            MillLog.major(this, "New custom village generated at " + p + ", took: " + (System.nanoTime() - startTime));
        }
        return true;
    }

    private void generateHamlet(World world, VillageType hamlet, Point centralVillage, String name, Random random, float completionRatio) {
        boolean generated = false;
        for (int minRadius = 250; !generated && minRadius < 350; minRadius += 50) {
            double angle = 0.06280000000000001 * (double)MillCommonUtilities.randomInt(100);
            for (int attempts = 0; !generated && attempts < 36; ++attempts) {
                int radius = minRadius + MillCommonUtilities.randomInt(40);
                int dx = (int)(Math.cos(angle += 0.17444444444444446) * (double)radius);
                int dz = (int)(Math.sin(angle) * (double)radius);
                if (MillConfigValues.LogWorldGeneration >= 1) {
                    MillLog.major(this, "Trying to generate a hamlet " + hamlet + " around: " + (centralVillage.getiX() + dx) + "/" + (centralVillage.getiZ() + dz));
                }
                generated = this.generateVillageAtPoint(world, random, centralVillage.getiX() + dx, 0, centralVillage.getiZ() + dz, null, false, true, false, 250, hamlet, name, centralVillage, completionRatio);
            }
        }
        if (!generated && MillConfigValues.LogWorldGeneration >= 1) {
            MillLog.major(this, "Could not generate hamlet " + hamlet);
        }
    }

    private boolean generateVillage(Point targetPos, World world, VillageType villageType, EntityPlayer player, EntityPlayer closestPlayer, Random random, int minDistance, String name, Point parentVillage, float completionRatio, boolean testBiomeValidity, boolean alwaysSpawn) throws MillLog.MillenaireException {
        List<BuildingLocation> wallLocations;
        boolean biomeValidity;
        if (testBiomeValidity && !(biomeValidity = this.isInValidBiomes(world, villageType, targetPos))) {
            return false;
        }
        VillageMapInfo winfo = new VillageMapInfo();
        ArrayList<BuildingLocation> plannedBuildings = new ArrayList<BuildingLocation>();
        MillWorldData mw = Mill.getMillWorld(world);
        targetPos = new Point(targetPos.x, WorldUtilities.findTopSoilBlock(world, targetPos.getiX(), targetPos.getiZ()), targetPos.z);
        for (int x = targetPos.getChunkX() - villageType.radius / 16 - 1; x <= targetPos.getChunkX() + villageType.radius / 16; ++x) {
            for (int z = targetPos.getChunkZ() - villageType.radius / 16 - 1; z <= targetPos.getChunkZ() + villageType.radius / 16; ++z) {
                if (world.func_190526_b(x * 16, z * 16)) continue;
                world.func_72863_F().func_186025_d(x, z);
            }
        }
        winfo.update(world, plannedBuildings, targetPos, villageType.radius);
        if (!alwaysSpawn && !this.isUsableArea(winfo, targetPos, villageType.radius)) {
            return false;
        }
        long startTime = System.nanoTime();
        BuildingLocation location = villageType.centreBuilding.getRandomStartingPlan().findBuildingLocation(winfo, null, targetPos, villageType.radius, random, 3);
        if (location == null) {
            if (MillConfigValues.LogWorldGeneration >= 2) {
                MillLog.minor(this, "Could not find place for central building: " + villageType.centreBuilding);
            }
            if (player != null) {
                ServerSender.sendTranslatedSentence(player, '6', "ui.generatenotenoughspace", new String[0]);
            }
            return false;
        }
        Point thPos = location.pos;
        if (WorldGenVillage.isWithinSpawnRadiusProtection(world, villageType, thPos)) {
            if (Mill.proxy.isTrueServer()) {
                if (player != null) {
                    ServerSender.sendTranslatedSentence(player, '6', "ui.tooclosetospawn", new String[0]);
                }
                return false;
            }
            if (!alwaysSpawn) {
                return false;
            }
        } else if (MillConfigValues.LogWorldGeneration >= 2) {
            MillLog.minor(this, "Distance to spawn of " + location.pos.horizontalDistanceTo(world.func_175694_M()) + " is sufficient. Pos: " + thPos + ", spawn: " + world.func_175694_M());
        }
        if (!alwaysSpawn) {
            int minDistanceWithLoneBuildings;
            int minDistanceWithVillages;
            if (villageType.lonebuilding) {
                if (villageType.isKeyLoneBuildingForGeneration(closestPlayer)) {
                    minDistanceWithVillages = Math.min(minDistance, MillConfigValues.minDistanceBetweenVillagesAndLoneBuildings) / 2;
                    minDistanceWithLoneBuildings = Math.min(minDistance, MillConfigValues.minDistanceBetweenLoneBuildings) / 2;
                } else {
                    minDistanceWithVillages = Math.min(minDistance, MillConfigValues.minDistanceBetweenVillagesAndLoneBuildings);
                    minDistanceWithLoneBuildings = Math.min(minDistance, MillConfigValues.minDistanceBetweenLoneBuildings);
                }
            } else {
                minDistanceWithVillages = Math.min(minDistance, MillConfigValues.minDistanceBetweenVillages);
                minDistanceWithLoneBuildings = Math.min(minDistance, MillConfigValues.minDistanceBetweenVillagesAndLoneBuildings);
            }
            for (Point thp : mw.villagesList.pos) {
                if (!(thPos.distanceTo(thp) < (double)minDistanceWithVillages)) continue;
                if (MillConfigValues.LogWorldGeneration >= 1) {
                    MillLog.major(this, "Found a nearby village with final position. TargetPos / ThPos distance: " + targetPos.directionTo(thPos));
                }
                return false;
            }
            for (Point thp : mw.loneBuildingsList.pos) {
                if (!(thPos.distanceTo(thp) < (double)minDistanceWithLoneBuildings)) continue;
                if (MillConfigValues.LogWorldGeneration >= 1) {
                    MillLog.major(this, "Found a nearby lone building final position. TargetPos / ThPos distance: " + targetPos.directionTo(thPos));
                }
                return false;
            }
        }
        if (MillConfigValues.LogWorldGeneration >= 2) {
            MillLog.minor(this, "Place found for TownHall (village type: " + villageType.key + "). Checking for the rest.");
        }
        plannedBuildings.add(location);
        winfo.update(world, plannedBuildings, thPos, villageType.radius);
        RegionMapper regionMapper = new RegionMapper();
        regionMapper.createConnectionsTable(winfo, thPos);
        boolean areaChanged = false;
        VillageWallGenerator wallGenerator = new VillageWallGenerator(world);
        if (villageType.innerWallType != null) {
            wallLocations = wallGenerator.computeWallBuildingLocations(villageType, villageType.innerWallType, villageType.innerWallRadius, regionMapper, thPos, winfo);
            plannedBuildings.addAll(wallLocations);
            areaChanged = winfo.update(world, plannedBuildings, thPos, villageType.radius);
            if (areaChanged) {
                regionMapper.createConnectionsTable(winfo, thPos);
            }
        }
        if (villageType.outerWallType != null) {
            wallLocations = wallGenerator.computeWallBuildingLocations(villageType, villageType.outerWallType, 0, regionMapper, thPos, winfo);
            plannedBuildings.addAll(wallLocations);
            areaChanged = winfo.update(world, plannedBuildings, thPos, villageType.radius);
            if (areaChanged) {
                regionMapper.createConnectionsTable(winfo, thPos);
            }
        }
        boolean couldBuildKeyBuildings = true;
        for (BuildingPlanSet planSet : villageType.startBuildings) {
            location = planSet.getRandomStartingPlan().findBuildingLocation(winfo, regionMapper, thPos, villageType.radius, random, -1);
            if (location != null) {
                plannedBuildings.add(location);
                areaChanged = winfo.update(world, plannedBuildings, thPos, villageType.radius);
                if (!areaChanged) continue;
                regionMapper.createConnectionsTable(winfo, thPos);
                continue;
            }
            couldBuildKeyBuildings = false;
            if (MillConfigValues.LogWorldGeneration < 2) continue;
            MillLog.minor(this, "Couldn't build " + planSet.key + ".");
        }
        if (MillConfigValues.LogWorldGeneration >= 3) {
            MillLog.debug(this, "Time taken for finding if building possible: " + (System.nanoTime() - startTime));
        }
        if (!couldBuildKeyBuildings) {
            if (player != null) {
                ServerSender.sendTranslatedSentence(player, '6', "ui.generatenotenoughspacevillage", new String[0]);
            }
            return false;
        }
        if (MillConfigValues.LogWorldGeneration >= 1) {
            MillLog.major(this, thPos + ": Generating village with completion of: " + completionRatio);
        }
        if (MillConfigValues.LogWorldGeneration >= 1) {
            for (BuildingLocation bl : plannedBuildings) {
                MillLog.major(this, "Building " + bl.planKey + ": " + bl.minx + "/" + bl.minz + " to " + bl.maxx + "/" + bl.maxz);
            }
        }
        startTime = System.nanoTime();
        TreeClearer.cumulatedTimeTreeFinding = 0L;
        TreeClearer.cumulatedTimeLeaveDecay = 0L;
        List<BuildingPlan.LocationBuildingPair> lbps = villageType.centreBuilding.buildLocation(mw, villageType, (BuildingLocation)plannedBuildings.get(0), true, true, null, false, false, player);
        Building townHallEntity = lbps.get((int)0).building;
        if (MillConfigValues.LogWorldGeneration >= 1) {
            MillLog.major(this, "Registering building: " + townHallEntity);
        }
        townHallEntity.villageType = villageType;
        townHallEntity.findName(name);
        townHallEntity.initialiseBuildingProjects();
        townHallEntity.registerBuildingLocation((BuildingLocation)plannedBuildings.get(0));
        for (BuildingPlan.LocationBuildingPair lbp : lbps) {
            if (lbp == lbps.get(0)) continue;
            townHallEntity.registerBuildingEntity(lbp.building);
            townHallEntity.registerBuildingLocation(lbp.location);
        }
        for (int i = 1; i < plannedBuildings.size(); ++i) {
            BuildingLocation bl = (BuildingLocation)plannedBuildings.get(i);
            BuildingPlanSet planSet = villageType.culture.getBuildingPlanSet(bl.planKey);
            if (bl.level == -1) {
                if (completionRatio > 0.0f && Math.random() <= (double)completionRatio || (double)completionRatio > 0.33) {
                    bl.level = 0;
                } else {
                    BuildingProject project = new BuildingProject(planSet);
                    project.location = bl;
                    if (planSet.getFirstStartingPlan().isWallSegment) {
                        if (!townHallEntity.buildingProjects.containsKey((Object)BuildingProject.EnumProjects.WALLBUILDING)) {
                            townHallEntity.buildingProjects.put(BuildingProject.EnumProjects.WALLBUILDING, new CopyOnWriteArrayList());
                        }
                        townHallEntity.buildingProjects.get((Object)BuildingProject.EnumProjects.WALLBUILDING).add(project);
                    } else {
                        townHallEntity.buildingProjects.get((Object)BuildingProject.EnumProjects.EXTRA).add(project);
                    }
                }
            }
            if (bl.level < 0) continue;
            lbps = planSet.buildLocation(mw, villageType, bl, true, false, townHallEntity, false, false, player);
            if (MillConfigValues.LogWorldGeneration >= 1) {
                MillLog.major(this, "Registering building: " + bl.planKey);
            }
            for (BuildingPlan.LocationBuildingPair lbp : lbps) {
                townHallEntity.registerBuildingEntity(lbp.building);
                townHallEntity.registerBuildingLocation(lbp.location);
            }
        }
        townHallEntity.initialiseVillage();
        if (completionRatio > 0.0f && !villageType.playerControlled) {
            int nbBuildingRushed;
            int nbRushed;
            int nbRushedBuildingsTarget = 0;
            for (BuildingProject project : townHallEntity.getFlatProjectList()) {
                if (project.location != null) {
                    nbRushedBuildingsTarget = project.planSet.plans.get(project.location.getVariation()).length - project.location.level;
                    continue;
                }
                nbRushedBuildingsTarget = project.planSet.getFirstStartingPlan().plan.length;
            }
            nbRushedBuildingsTarget = (int)((float)townHallEntity.getNbProjects() * completionRatio);
            for (nbBuildingRushed = townHallEntity.rushCurrentConstructions(true); nbBuildingRushed < nbRushedBuildingsTarget || completionRatio >= 1.0f; nbBuildingRushed += nbRushed) {
                nbRushed = 0;
                for (int rushAttempts = 0; rushAttempts < 3 && nbRushed == 0; ++rushAttempts) {
                    nbRushed = townHallEntity.rushCurrentConstructions(true);
                }
                if (nbRushed != 0) continue;
                MillLog.temp(townHallEntity, "Finished rushing at " + nbBuildingRushed + " on " + nbRushedBuildingsTarget + " as no more rushing was possible.");
                break;
            }
            townHallEntity.resetConstructionsAndGoals();
            MillLog.temp(townHallEntity, "Finished rushing at " + nbBuildingRushed + " on " + nbRushedBuildingsTarget + ".");
        }
        String playerName = null;
        if (closestPlayer != null) {
            playerName = closestPlayer.func_70005_c_();
        }
        if (villageType.lonebuilding) {
            mw.registerLoneBuildingsLocation(world, townHallEntity.getPos(), townHallEntity.getVillageQualifiedName(), townHallEntity.villageType, townHallEntity.culture, true, playerName);
        } else {
            mw.registerVillageLocation(world, townHallEntity.getPos(), townHallEntity.getVillageQualifiedName(), townHallEntity.villageType, townHallEntity.culture, true, playerName);
            townHallEntity.initialiseRelations(parentVillage);
            if (villageType.playerControlled) {
                townHallEntity.storeItemStack(ItemParchment.createParchmentForVillage(townHallEntity.getTownHall()));
            }
        }
        if (MillConfigValues.LogWorldGeneration >= 1) {
            MillLog.major(this, "New village generated at " + thPos + ", took: " + (System.nanoTime() - startTime) / 1000000L + " ms, of which tree finding: " + TreeClearer.cumulatedTimeTreeFinding / 1000000L + "ms and leave & log decay: " + TreeClearer.cumulatedTimeLeaveDecay / 1000000L + "ms.");
        }
        for (String key : villageType.hamlets) {
            VillageType hamlet = villageType.culture.getVillageType(key);
            if (hamlet == null) continue;
            if (MillConfigValues.LogWorldGeneration >= 1) {
                MillLog.major(this, "Trying to generate a hamlet: " + hamlet);
            }
            this.generateHamlet(world, hamlet, townHallEntity.getPos(), townHallEntity.getVillageNameWithoutQualifier(), random, completionRatio);
        }
        return true;
    }

    public boolean generateVillageAtPoint(World world, Random random, int x, int y, int z, EntityPlayer generatingPlayer, boolean checkForUnloaded, boolean alwaysGenerate, boolean testBiomeValidity, int minDistance, VillageType specificVillageType, String name, Point parentVillage, float completionRatio) {
        EntityPlayer closestPlayer;
        if (world.field_72995_K) {
            return false;
        }
        MillWorldData mw = Mill.getMillWorld(world);
        if (mw == null) {
            return false;
        }
        boolean generateVillages = MillConfigValues.generateVillages;
        if (mw.generateVillagesSet) {
            generateVillages = mw.generateVillages;
        }
        if (!Mill.loadingComplete || !generateVillages && !MillConfigValues.generateLoneBuildings && !alwaysGenerate) {
            return false;
        }
        Point p = new Point(x, y, z);
        if (WorldGenVillage.isWithinSpawnRadiusProtection(world, specificVillageType, p)) {
            if (Mill.proxy.isTrueServer()) {
                if (generatingPlayer != null) {
                    ServerSender.sendTranslatedSentence(generatingPlayer, '6', "ui.tooclosetospawn", new String[0]);
                }
                return false;
            }
            if (!alwaysGenerate) {
                return false;
            }
        }
        if ((closestPlayer = generatingPlayer) == null) {
            closestPlayer = world.func_184137_a((double)x, 64.0, (double)z, 200.0, false);
        }
        try {
            boolean success;
            boolean result;
            VillageType villageType;
            boolean canAttemptVillage;
            if (MillConfigValues.LogWorldGeneration >= 3) {
                MillLog.debug(this, "Called for point: " + x + "/" + y + "/" + z);
            }
            MillCommonUtilities.random = random;
            if (checkForUnloaded) {
                int villageRadius = MillConfigValues.VillageRadius;
                if (specificVillageType != null) {
                    villageRadius = specificVillageType.radius;
                }
                if ((p = this.generateVillageAtPoint_checkForUnloaded(world, x, y, z, generatingPlayer, p, villageRadius)) == null) {
                    return false;
                }
            }
            long startTime = System.nanoTime();
            chunkCoordsTried.add(this.computeChunkCoordsHash(p.getiX(), p.getiZ()));
            if ((generateVillages || alwaysGenerate) && (canAttemptVillage = this.generateVillageAtPoint_canAttemptVillage(world, generatingPlayer, minDistance, mw, p, startTime)) && (villageType = specificVillageType == null ? this.generateVillageAtPoint_findVillageType(world, p.getiX(), p.getiZ(), mw, closestPlayer) : specificVillageType) != null && (result = villageType.customCentre == null ? this.generateVillage(p, world, villageType, generatingPlayer, closestPlayer, random, minDistance, name, parentVillage, completionRatio, testBiomeValidity, alwaysGenerate) : this.generateCustomVillage(p, world, villageType, generatingPlayer, random))) {
                return true;
            }
            if (generatingPlayer != null || !MillConfigValues.generateLoneBuildings || specificVillageType != null) {
                return false;
            }
            boolean keyLoneBuildingsOnly = false;
            int minDistanceWithVillages = Math.min(minDistance, MillConfigValues.minDistanceBetweenVillagesAndLoneBuildings);
            int minDistanceWithLoneBuildings = Math.min(minDistance, MillConfigValues.minDistanceBetweenLoneBuildings);
            for (Point thp : mw.villagesList.pos) {
                if (p.distanceTo(thp) < (double)(minDistanceWithVillages / 2)) {
                    if (MillConfigValues.LogWorldGeneration >= 3) {
                        MillLog.debug(this, "Time taken for finding near villages: " + (System.nanoTime() - startTime));
                    }
                    return false;
                }
                if (!(p.distanceTo(thp) < (double)minDistanceWithVillages)) continue;
                keyLoneBuildingsOnly = true;
            }
            for (Point thp : mw.loneBuildingsList.pos) {
                if (p.distanceTo(thp) < (double)(minDistanceWithLoneBuildings / 4)) {
                    if (MillConfigValues.LogWorldGeneration >= 3) {
                        MillLog.debug(this, "Time taken for finding near villages: " + (System.nanoTime() - startTime));
                    }
                    return false;
                }
                if (!(p.distanceTo(thp) < (double)minDistanceWithLoneBuildings)) continue;
                keyLoneBuildingsOnly = true;
            }
            if (MillConfigValues.LogWorldGeneration >= 3) {
                MillLog.debug(this, "Time taken for finding near villages (not found): " + (System.nanoTime() - startTime));
            }
            ArrayList<VillageType> acceptableLoneBuildingsType = new ArrayList<VillageType>();
            HashMap<String, Integer> nbLoneBuildings = new HashMap<String, Integer>();
            for (String string : mw.loneBuildingsList.types) {
                if (nbLoneBuildings.containsKey(string)) {
                    nbLoneBuildings.put(string, (Integer)nbLoneBuildings.get(string) + 1);
                    continue;
                }
                nbLoneBuildings.put(string, 1);
            }
            String biomeName = this.getBiomeNameAtPos(world, p.getiX(), p.getiZ());
            for (Culture c : Culture.ListCultures) {
                for (VillageType vt : c.listLoneBuildingTypes) {
                    if (!vt.isValidForGeneration(mw, closestPlayer, nbLoneBuildings, new Point(x, 60.0, z), biomeName, keyLoneBuildingsOnly)) continue;
                    acceptableLoneBuildingsType.add(vt);
                }
            }
            if (acceptableLoneBuildingsType.size() == 0) {
                return false;
            }
            VillageType villageType2 = (VillageType)MillCommonUtilities.getWeightedChoice(acceptableLoneBuildingsType, closestPlayer);
            if (MillConfigValues.LogWorldGeneration >= 2) {
                MillLog.minor(null, "Attempting to find lone building: " + villageType2);
            }
            if (villageType2 == null) {
                return false;
            }
            if (villageType2.isKeyLoneBuildingForGeneration(closestPlayer) && MillConfigValues.LogWorldGeneration >= 1) {
                MillLog.major(null, "Attempting to generate key lone building: " + villageType2.key);
            }
            if ((success = this.generateVillage(p, world, villageType2, generatingPlayer, closestPlayer, random, minDistance, name, null, completionRatio, testBiomeValidity, alwaysGenerate)) && closestPlayer != null && villageType2.isKeyLoneBuildingForGeneration(closestPlayer) && villageType2.keyLoneBuildingGenerateTag != null) {
                UserProfile profile = mw.getProfile(closestPlayer);
                profile.clearTag(villageType2.keyLoneBuildingGenerateTag);
            }
            return success;
        }
        catch (Exception e) {
            MillLog.printException("Exception when generating village:", e);
            return false;
        }
    }

    private boolean generateVillageAtPoint_canAttemptVillage(World world, EntityPlayer generatingPlayer, int minDistance, MillWorldData mw, Point p, long startTime) {
        boolean canAttemptVillage = true;
        int minDistanceVillages = Math.min(minDistance, MillConfigValues.minDistanceBetweenVillages);
        int minDistanceLoneBuildings = Math.min(minDistance, MillConfigValues.minDistanceBetweenVillagesAndLoneBuildings);
        if (generatingPlayer == null) {
            for (Point thp : mw.villagesList.pos) {
                if (!(p.distanceTo(thp) < (double)minDistanceVillages)) continue;
                if (MillConfigValues.LogWorldGeneration >= 3) {
                    MillLog.debug(this, "Time taken for finding near villages: " + (System.nanoTime() - startTime));
                }
                canAttemptVillage = false;
            }
            for (Point thp : mw.loneBuildingsList.pos) {
                if (!(p.distanceTo(thp) < (double)minDistanceLoneBuildings)) continue;
                if (MillConfigValues.LogWorldGeneration >= 3) {
                    MillLog.debug(this, "Time taken for finding near lone buildings: " + (System.nanoTime() - startTime));
                }
                canAttemptVillage = false;
            }
        }
        if (MillConfigValues.LogWorldGeneration >= 3) {
            MillLog.debug(this, "Time taken for finding near villages (not found): " + (System.nanoTime() - startTime));
        }
        return canAttemptVillage;
    }

    private Point generateVillageAtPoint_checkForUnloaded(World world, int x, int y, int z, EntityPlayer generatingPlayer, Point p, int villageRadius) {
        boolean areaLoaded = false;
        int chunkRadius = villageRadius / 16 + 2;
        if (!WorldUtilities.checkChunksGenerated(world, x - 16 * chunkRadius, z - 16 * chunkRadius, x + 16 * chunkRadius, z + 16 * chunkRadius)) {
            for (int i = -8; i <= 8 && !areaLoaded; ++i) {
                for (int j = -8; j <= 8 && !areaLoaded; ++j) {
                    int tx = x + i * 16;
                    int tz = z + j * 16;
                    if (chunkCoordsTried.contains(this.computeChunkCoordsHash(tx, tz)) || !WorldUtilities.checkChunksGenerated(world, tx - 16 * chunkRadius, tz - 16 * chunkRadius, tx + 16 * chunkRadius, tz + 16 * chunkRadius)) continue;
                    areaLoaded = true;
                    p = new Point((tx >> 4) * 16 + 8, 0.0, (tz >> 4) * 16 + 8);
                }
            }
        } else {
            areaLoaded = true;
        }
        if (!areaLoaded) {
            if (generatingPlayer != null) {
                ServerSender.sendTranslatedSentence(generatingPlayer, '6', "ui.worldnotgenerated", new String[0]);
            }
            return null;
        }
        return p;
    }

    private VillageType generateVillageAtPoint_findVillageType(World world, int x, int z, MillWorldData mw, EntityPlayer closestPlayer) throws IllegalArgumentException, IllegalAccessException {
        ArrayList<VillageType> acceptableVillageType = new ArrayList<VillageType>();
        HashMap<String, Integer> nbVillages = new HashMap<String, Integer>();
        for (String type : mw.villagesList.types) {
            if (nbVillages.containsKey(type)) {
                nbVillages.put(type, (Integer)nbVillages.get(type) + 1);
                continue;
            }
            nbVillages.put(type, 1);
        }
        String biomeName = this.getBiomeNameAtPos(world, x, z);
        for (Culture c : Culture.ListCultures) {
            for (VillageType vt : c.listVillageTypes) {
                if (!vt.isValidForGeneration(Mill.getMillWorld(world), closestPlayer, nbVillages, new Point(x, 60.0, z), biomeName, false)) continue;
                acceptableVillageType.add(vt);
            }
        }
        VillageType village = acceptableVillageType.size() != 0 ? (VillageType)MillCommonUtilities.getWeightedChoice(acceptableVillageType, closestPlayer) : null;
        return village;
    }

    private String getBiomeNameAtPos(World world, int x, int z) throws IllegalAccessException {
        String biomeName = ((String)FIELD_BIOME_NAME.get(world.func_180494_b(new BlockPos(x, 0, z)))).toLowerCase();
        return biomeName;
    }

    private boolean isInValidBiomes(World world, VillageType villageType, Point p) {
        int biomeTotalCounter = 0;
        int biomeValidCounter = 0;
        for (int x = p.getiX() - villageType.radius; x <= p.getiX() + villageType.radius; x += 16) {
            for (int z = p.getiZ() - villageType.radius; z <= p.getiZ() + villageType.radius; z += 16) {
                try {
                    String localBiome = this.getBiomeNameAtPos(world, x, z);
                    ++biomeTotalCounter;
                    if (!villageType.biomes.contains(localBiome)) continue;
                    ++biomeValidCounter;
                    continue;
                }
                catch (IllegalAccessException e) {
                    MillLog.printException(e);
                }
            }
        }
        float validPerc = (float)biomeValidCounter / (float)biomeTotalCounter;
        if (MillConfigValues.LogWorldGeneration >= 2) {
            MillLog.minor(villageType, "Biome validity around " + p + ": " + validPerc + ", required: " + villageType.getMinimumBiomeValidity());
        }
        return validPerc >= villageType.getMinimumBiomeValidity();
    }

    private boolean isUsableArea(VillageMapInfo winfo, Point centre, int radius) {
        int nbtiles = 0;
        int usabletiles = 0;
        for (int i = 0; i < winfo.length; ++i) {
            for (int j = 0; j < winfo.width; ++j) {
                ++nbtiles;
                if (!winfo.canBuild[i][j]) continue;
                ++usabletiles;
            }
        }
        return (double)usabletiles * 1.0 / (double)nbtiles > 0.7;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "@" + this.hashCode();
    }
}

