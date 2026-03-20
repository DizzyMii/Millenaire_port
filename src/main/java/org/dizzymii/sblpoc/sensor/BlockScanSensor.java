package org.dizzymii.sblpoc.sensor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;

import java.util.List;

/**
 * Periodically scans blocks around the NPC and updates the SpatialMemory.
 * Runs every 40 ticks (~2 seconds) to avoid performance impact.
 */
public class BlockScanSensor extends ExtendedSensor<PocNpc> {

    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of();

    public BlockScanSensor() {
        setScanRate(entity -> 40); // Every 2 seconds
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends BlockScanSensor> type() {
        return SblPocSetup.BLOCK_SCAN.get();
    }

    @Override
    protected void doTick(ServerLevel level, PocNpc npc) {
        npc.getSpatialMemory().scanAround(level, npc.blockPosition());
    }
}
