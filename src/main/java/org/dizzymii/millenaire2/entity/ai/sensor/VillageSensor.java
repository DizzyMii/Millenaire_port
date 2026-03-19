package org.dizzymii.millenaire2.entity.ai.sensor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.entity.ai.MillMemoryTypes;
import org.dizzymii.millenaire2.entity.ai.MillSensorTypes;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.world.MillWorldData;

import java.util.List;

/**
 * Sensor that reads the villager's home/townhall state and pushes
 * it into Brain memories every ~40 ticks (2 seconds).
 */
public class VillageSensor extends ExtendedSensor<MillVillager> {

    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            MillMemoryTypes.HOME_BUILDING_POS.get(),
            MillMemoryTypes.TOWNHALL_POS.get(),
            MillMemoryTypes.VILLAGE_UNDER_ATTACK.get()
    );

    public VillageSensor() {
        setScanRate(entity -> 40); // every 2 seconds
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends VillageSensor> type() {
        return MillSensorTypes.VILLAGE_SENSOR.get();
    }

    @Override
    protected void doTick(ServerLevel level, MillVillager villager) {
        // Push home building position
        if (villager.housePoint != null) {
            villager.getBrain().setMemory(MillMemoryTypes.HOME_BUILDING_POS.get(),
                    villager.housePoint.toBlockPos());
        } else {
            villager.getBrain().eraseMemory(MillMemoryTypes.HOME_BUILDING_POS.get());
        }

        // Push townhall position
        if (villager.townHallPoint != null) {
            villager.getBrain().setMemory(MillMemoryTypes.TOWNHALL_POS.get(),
                    villager.townHallPoint.toBlockPos());
        } else {
            villager.getBrain().eraseMemory(MillMemoryTypes.TOWNHALL_POS.get());
        }

        // Push village under attack status
        boolean underAttack = false;
        if (villager.townHallPoint != null) {
            MillWorldData mw = MillWorldData.get(level);
            Building th = mw.getBuilding(villager.townHallPoint);
            if (th != null) {
                underAttack = th.underAttack;
            }
        }
        villager.getBrain().setMemory(MillMemoryTypes.VILLAGE_UNDER_ATTACK.get(), underAttack);
    }
}
