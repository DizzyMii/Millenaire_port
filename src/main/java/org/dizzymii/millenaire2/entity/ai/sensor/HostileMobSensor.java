package org.dizzymii.millenaire2.entity.ai.sensor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Monster;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.millenaire2.entity.MillGuardNpc;
import org.dizzymii.millenaire2.entity.ai.MillSensorTypes;

import java.util.List;

/**
 * Sensor that scans for hostile mobs (Zombies, Skeletons, Creepers, etc.)
 * within detection range and stores the nearest one in NEAREST_HOSTILE memory.
 */
public class HostileMobSensor extends ExtendedSensor<MillGuardNpc> {

    private static final double DETECTION_RANGE = 16.0;

    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            MemoryModuleType.NEAREST_HOSTILE
    );

    public HostileMobSensor() {
        setScanRate(entity -> 10); // every half second
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends HostileMobSensor> type() {
        return MillSensorTypes.HOSTILE_MOB_SENSOR.get();
    }

    @Override
    protected void doTick(ServerLevel level, MillGuardNpc guard) {
        LivingEntity nearest = null;
        double nearestDistSq = Double.MAX_VALUE;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class,
                guard.getBoundingBox().inflate(DETECTION_RANGE),
                e -> e != guard && e.isAlive())) {

            if (!(entity instanceof Monster)) continue;

            double distSq = guard.distanceToSqr(entity);
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = entity;
            }
        }

        if (nearest != null) {
            BrainUtils.setMemory(guard, MemoryModuleType.NEAREST_HOSTILE, nearest);
        } else {
            guard.getBrain().eraseMemory(MemoryModuleType.NEAREST_HOSTILE);
        }
    }
}
