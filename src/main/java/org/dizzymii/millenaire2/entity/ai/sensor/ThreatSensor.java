package org.dizzymii.millenaire2.entity.ai.sensor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Monster;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.ai.MillSensorTypes;

import java.util.List;

/**
 * Sensor that detects nearby hostile mobs and enemy raiders,
 * updating HURT_BY and NEAREST_HOSTILE memories.
 */
public class ThreatSensor extends ExtendedSensor<MillVillager> {

    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.NEAREST_HOSTILE
    );

    public ThreatSensor() {
        setScanRate(entity -> 20); // every second
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ThreatSensor> type() {
        return MillSensorTypes.THREAT_SENSOR.get();
    }

    @Override
    protected void doTick(ServerLevel level, MillVillager villager) {
        // Find nearest hostile mob within attack range
        double range = MillVillager.ATTACK_RANGE;
        LivingEntity nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class,
                villager.getBoundingBox().inflate(range), e -> e != villager && e.isAlive())) {

            boolean isHostile = false;

            // Vanilla hostile mobs
            if (entity instanceof Monster) {
                isHostile = true;
            }

            // Enemy raiders from other villages
            if (entity instanceof MillVillager other) {
                if (other.isRaider && villager.townHallPoint != null
                        && !villager.townHallPoint.equals(other.townHallPoint)) {
                    isHostile = true;
                }
            }

            if (isHostile) {
                double dist = villager.distanceToSqr(entity);
                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = entity;
                }
            }
        }

        if (nearest != null) {
            BrainUtils.setMemory(villager, MemoryModuleType.NEAREST_HOSTILE, nearest);
        } else {
            villager.getBrain().eraseMemory(MemoryModuleType.NEAREST_HOSTILE);
        }
    }
}
