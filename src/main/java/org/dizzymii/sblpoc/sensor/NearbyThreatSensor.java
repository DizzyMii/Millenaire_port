package org.dizzymii.sblpoc.sensor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Monster;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;

import java.util.List;

/**
 * Scans for hostile mobs within 16 blocks every 10 ticks.
 * Writes NEAREST_HOSTILE and NEARBY_HOSTILE_COUNT memories.
 */
public class NearbyThreatSensor extends ExtendedSensor<PocNpc> {

    private static final double DETECTION_RANGE = 16.0;

    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            MemoryModuleType.NEAREST_HOSTILE,
            SblPocSetup.NEARBY_HOSTILE_COUNT.get()
    );

    public NearbyThreatSensor() {
        setScanRate(entity -> 10);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends NearbyThreatSensor> type() {
        return SblPocSetup.NEARBY_THREAT.get();
    }

    @Override
    protected void doTick(ServerLevel level, PocNpc npc) {
        LivingEntity nearest = null;
        double nearestDistSq = Double.MAX_VALUE;
        int hostileCount = 0;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class,
                npc.getBoundingBox().inflate(DETECTION_RANGE),
                e -> e != npc && e.isAlive() && e instanceof Monster)) {

            hostileCount++;
            double distSq = npc.distanceToSqr(entity);
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
                nearest = entity;
            }
        }

        if (nearest != null) {
            BrainUtils.setMemory(npc, MemoryModuleType.NEAREST_HOSTILE, nearest);
        } else {
            BrainUtils.clearMemory(npc, MemoryModuleType.NEAREST_HOSTILE);
        }
        BrainUtils.setMemory(npc, SblPocSetup.NEARBY_HOSTILE_COUNT.get(), hostileCount);
    }
}
