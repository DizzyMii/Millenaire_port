package org.dizzymii.millenaire2.village;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.culture.VillagerType;
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;

import javax.annotation.Nullable;

/**
 * Handles villager entity spawning for a {@link Building}.
 *
 * <p>Checks each {@link VillagerRecord} in a building and spawns any missing live entities,
 * preventing duplicate spawns when the entity is already loaded in the world.</p>
 */
public final class VillagerSpawner {

    private VillagerSpawner() {}

    /**
     * Checks villager records for the given building and spawns entities for any that are
     * missing from the world.
     *
     * @param building the building whose villagers should be checked
     * @param level    the server level to search and spawn entities in
     */
    public static void checkAndSpawnVillagers(Building building, ServerLevel level) {
        Point pos = building.getPos();
        if (pos == null) return;

        Culture culture = building.cultureKey != null
                ? Culture.getCultureByName(building.cultureKey) : null;

        for (VillagerRecord vr : building.getVillagerRecords()) {
            if (vr.killed || vr.awayraiding || vr.awayhired) continue;

            // Skip if entity already loaded within range
            boolean entityExists = !level.getEntitiesOfClass(
                    MillVillager.class,
                    net.minecraft.world.phys.AABB.ofSize(
                            new net.minecraft.world.phys.Vec3(pos.x, pos.y, pos.z), 128, 64, 128),
                    v -> v.getVillagerId() == vr.getVillagerId()
            ).isEmpty();

            if (entityExists) continue;

            spawnVillagerFromRecord(building, level, vr, culture);
        }
    }

    /**
     * Creates and adds a {@link MillVillager} entity to the world from the given record.
     *
     * @param building the owning building (provides position and culture context)
     * @param level    the server level to spawn into
     * @param vr       the villager record describing the entity to create
     * @param culture  the resolved culture for the building, or {@code null}
     */
    private static void spawnVillagerFromRecord(Building building, ServerLevel level,
                                                 VillagerRecord vr, @Nullable Culture culture) {
        Point pos = building.getPos();
        if (pos == null) return;

        VillagerType vtype = null;
        if (culture != null && vr.type != null) {
            vtype = culture.getVillagerType(vr.type);
        }

        EntityType<? extends MillVillager> entityType =
                vr.gender == MillVillager.FEMALE
                        ? MillEntities.GENERIC_SYMM_FEMALE.get()
                        : MillEntities.GENERIC_MALE.get();

        MillVillager villager = entityType.create(level);
        if (villager == null) return;

        Point spawnPos = vr.getHousePos() != null ? vr.getHousePos() : pos;
        villager.setPos(spawnPos.x + 0.5, spawnPos.y + 1.0, spawnPos.z + 0.5);

        villager.setVillagerId(vr.getVillagerId());
        if (vr.firstName != null) villager.setFirstName(vr.firstName);
        if (vr.familyName != null) villager.setFamilyName(vr.familyName);
        villager.setGender(vr.gender);
        if (building.cultureKey != null) villager.setCultureKey(building.cultureKey);
        if (vr.type != null) villager.setVillagerTypeKey(vr.type);
        villager.housePoint = vr.getHousePos();
        villager.townHallPoint = building.getTownHallPos();

        level.addFreshEntity(villager);
        MillLog.minor("VillagerSpawner", "Spawned villager: " + vr.firstName + " " + vr.familyName);
    }
}
