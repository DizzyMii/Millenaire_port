package org.dizzymii.millenaire2.village;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;

/**
 * Villager spawning logic extracted from Building.
 * Checks persisted VillagerRecords and spawns any entities that are missing from the world.
 */
class BuildingSpawner {
    private static final Logger LOGGER = LogUtils.getLogger();

    static void checkAndSpawnVillagers(Building building) {
        if (!(building.getLevel() instanceof ServerLevel serverLevel)) return;
        Point pos = building.getPos();
        if (pos == null) return;

        Culture culture = building.cultureKey != null ? Culture.getCultureByName(building.cultureKey) : null;

        for (VillagerRecord vr : building.getVillagerRecords()) {
            if (vr.killed || vr.awayraiding || vr.awayhired) continue;

            boolean entityExists = !serverLevel.getEntitiesOfClass(
                    MillVillager.class,
                    AABB.ofSize(new Vec3(pos.x, pos.y, pos.z), 128, 64, 128),
                    v -> v.getVillagerId() == vr.getVillagerId()
            ).isEmpty();

            if (!entityExists) {
                spawnVillagerFromRecord(building, serverLevel, vr, culture);
            }
        }
    }

    private static void spawnVillagerFromRecord(Building building, ServerLevel level,
                                                VillagerRecord vr, @Nullable Culture culture) {
        Point pos = building.getPos();
        if (pos == null) return;

        MillVillager villager = MillEntities.MILL_VILLAGER.get().create(level);
        if (villager == null) return;
        villager.setBodyModel(vr.gender == MillVillager.FEMALE
                ? MillVillager.BodyModel.SYMM_FEMALE
                : MillVillager.BodyModel.MALE);

        Point spawnPos = vr.getHousePos() != null ? vr.getHousePos() : pos;
        villager.setPos(spawnPos.x + 0.5, spawnPos.y + 1.0, spawnPos.z + 0.5);
        villager.setVillagerId(vr.getVillagerId());
        if (vr.firstName != null) villager.setFirstName(vr.firstName);
        if (vr.familyName != null) villager.setFamilyName(vr.familyName);
        villager.setGender(vr.gender);
        if (building.cultureKey != null) villager.setCultureKey(building.cultureKey);
        if (vr.type != null) villager.setVillagerTypeKey(vr.type);
        villager.setHousePoint(vr.getHousePos());
        villager.setTownHallPoint(building.getTownHallPos());

        level.addFreshEntity(villager);
        LOGGER.debug("Spawned villager: {} {}", vr.firstName, vr.familyName);
    }
}
