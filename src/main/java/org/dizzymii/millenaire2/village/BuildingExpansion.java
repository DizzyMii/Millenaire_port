package org.dizzymii.millenaire2.village;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.culture.BuildingPlan;
import org.dizzymii.millenaire2.culture.BuildingPlanSet;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.culture.VillageType;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Village expansion logic extracted from Building — handles upgrading existing buildings
 * and constructing new ones to grow a village over time.
 * All methods are package-private static and operate on a townhall Building instance.
 */
class BuildingExpansion {
    private static final Logger LOGGER = LogUtils.getLogger();

    static void checkVillageExpansion(Building townhall) {
        MillWorldData mw = townhall.getWorldData();
        if (mw == null || townhall.cultureKey == null) return;
        Culture culture = Culture.getCultureByName(townhall.cultureKey);
        if (culture == null) return;

        // Gate: no concurrent construction anywhere in the village
        for (Building b : mw.getBuildingsMap().values()) {
            if (townhall.isSameVillage(b) && b.isUnderConstruction()) return;
        }

        // Gate: minimum population before expanding
        int population = 0;
        for (Building b : mw.getBuildingsMap().values()) {
            if (townhall.isSameVillage(b)) population += b.getVillagerRecords().size();
        }
        if (population < 2) return;

        // 1. Try to upgrade an existing idle building first
        for (Building b : mw.getBuildingsMap().values()) {
            if (b == townhall || !townhall.isSameVillage(b)) continue;
            if (b.canUpgrade() && b.tryUpgrade()) {
                LOGGER.debug("Village expansion: upgrading {}", b.getName());
                return;
            }
        }

        // 2. Build new buildings from the village type definition
        if (townhall.villageTypeKey == null) return;
        VillageType vtype = culture.villageTypes.get(townhall.villageTypeKey);
        if (vtype == null) vtype = culture.loneBuildingTypes.get(townhall.villageTypeKey);
        if (vtype == null) return;

        Set<String> builtPlanSets = new HashSet<>();
        for (Building b : mw.getBuildingsMap().values()) {
            if (townhall.isSameVillage(b) && b.planSetKey != null) {
                builtPlanSets.add(b.planSetKey);
            }
        }

        String needed = findNeededBuilding(vtype.coreBuildings, builtPlanSets, culture);
        if (needed == null) needed = findNeededBuilding(vtype.secondaryBuildings, builtPlanSets, culture);
        if (needed != null) startNewBuilding(townhall, needed, culture);
    }

    @Nullable
    private static String findNeededBuilding(List<String> candidates, Set<String> alreadyBuilt, Culture culture) {
        for (String key : candidates) {
            if (alreadyBuilt.contains(key)) continue;
            BuildingPlanSet planSet = culture.planSets.get(key);
            if (planSet != null && planSet.getInitialPlan() != null) return key;
        }
        return null;
    }

    private static void startNewBuilding(Building townhall, String newPlanSetKey, Culture culture) {
        Point pos = townhall.getPos();
        if (pos == null || !(townhall.getLevel() instanceof ServerLevel serverLevel)) return;
        MillWorldData mw = townhall.getWorldData();
        if (mw == null) return;

        BuildingPlanSet planSet = culture.planSets.get(newPlanSetKey);
        if (planSet == null) return;
        BuildingPlan initialPlan = planSet.getInitialPlan();
        if (initialPlan == null || !initialPlan.hasImage()) return;

        Point site = findBuildingSite(townhall, serverLevel, initialPlan.width, initialPlan.length);
        if (site == null) {
            LOGGER.debug("Village expansion: no valid site found for {}", newPlanSetKey);
            return;
        }

        Building newBuilding = new Building();
        newBuilding.cultureKey = townhall.cultureKey;
        newBuilding.planSetKey = newPlanSetKey;
        newBuilding.villageTypeKey = townhall.villageTypeKey;
        newBuilding.buildingLevel = 0;
        newBuilding.isActive = true;
        newBuilding.setPos(site);
        newBuilding.setTownHallPos(pos);
        newBuilding.setName(planSet.name != null ? planSet.name : newPlanSetKey);
        newBuilding.setLevelContext(townhall.getLevel(), mw);

        BuildingLocation loc = new BuildingLocation();
        loc.planKey = newPlanSetKey;
        loc.cultureKey = townhall.cultureKey;
        loc.pos = site;
        loc.width = initialPlan.width;
        loc.length = initialPlan.length;
        newBuilding.location = loc;

        ConstructionIP cip = ConstructionIP.fromBuildingPlan(initialPlan, site, serverLevel);
        if (cip != null) {
            cip.location = loc;
            newBuilding.currentConstruction = cip;
            mw.addBuilding(newBuilding, site);
            mw.setDirty();
            LOGGER.debug("Village expansion: new building {} at {}", newPlanSetKey, site);
        }
    }

    @Nullable
    private static Point findBuildingSite(Building townhall, ServerLevel level, int buildWidth, int buildLength) {
        Point pos = townhall.getPos();
        if (pos == null) return null;
        MillWorldData mw = townhall.getWorldData();
        if (mw == null) return null;

        for (int dist = 15; dist <= 60; dist += 5) {
            for (int angle = 0; angle < 8; angle++) {
                double rad = angle * Math.PI / 4.0;
                int cx = pos.x + (int)(Math.cos(rad) * dist);
                int cz = pos.z + (int)(Math.sin(rad) * dist);

                boolean overlaps = false;
                for (Building b : mw.getBuildingsMap().values()) {
                    if (b.getPos() != null && townhall.isSameVillage(b)) {
                        double d = new Point(cx, 0, cz).horizontalDistanceTo(b.getPos());
                        if (d < 12) { overlaps = true; break; }
                    }
                }
                if (overlaps) continue;

                int groundY = findGround(level, new BlockPos(cx, 0, cz));
                if (groundY < 0 || Math.abs(groundY - pos.y) > 8) continue;

                return new Point(cx, groundY, cz);
            }
        }
        return null;
    }

    static int findGround(ServerLevel level, BlockPos pos) {
        for (int y = level.getMaxBuildHeight() - 1; y > level.getMinBuildHeight(); y--) {
            BlockState state = level.getBlockState(new BlockPos(pos.getX(), y, pos.getZ()));
            if (!state.isAir() && !state.is(Blocks.WATER) && !state.canBeReplaced()) {
                return y + 1;
            }
        }
        return -1;
    }
}
