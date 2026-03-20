package org.dizzymii.sblpoc.sensor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;

import java.util.List;

/**
 * Updates time-related memories: whether it's night, the current day number,
 * and whether a thunderstorm is active.
 *
 * Runs every 100 ticks (~5 seconds) since time changes slowly.
 */
public class TimeSensor extends ExtendedSensor<PocNpc> {

    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            SblPocSetup.IS_NIGHT.get(),
            SblPocSetup.IS_THUNDERING.get(),
            SblPocSetup.DAY_NUMBER.get()
    );

    public TimeSensor() {
        setScanRate(entity -> 100);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends TimeSensor> type() {
        return SblPocSetup.TIME_SENSOR.get();
    }

    @Override
    protected void doTick(ServerLevel level, PocNpc npc) {
        long dayTime = level.getDayTime() % 24000;
        boolean isNight = dayTime >= 13000 && dayTime < 23000;

        if (isNight) {
            BrainUtils.setMemory(npc, SblPocSetup.IS_NIGHT.get(), true);
        } else {
            BrainUtils.clearMemory(npc, SblPocSetup.IS_NIGHT.get());
        }

        if (level.isThundering()) {
            BrainUtils.setMemory(npc, SblPocSetup.IS_THUNDERING.get(), true);
        } else {
            BrainUtils.clearMemory(npc, SblPocSetup.IS_THUNDERING.get());
        }

        int dayNumber = (int) (level.getDayTime() / 24000);
        BrainUtils.setMemory(npc, SblPocSetup.DAY_NUMBER.get(), dayNumber);
    }
}
