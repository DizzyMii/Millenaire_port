package org.dizzymii.millenaire2.entity.ai;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.entity.ai.sensor.HostileMobSensor;
import org.dizzymii.millenaire2.entity.ai.sensor.VillageSensor;
import org.dizzymii.millenaire2.entity.ai.sensor.ThreatSensor;

/**
 * Custom SensorTypes for Millénaire villagers.
 */
public class MillSensorTypes {

    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES =
            DeferredRegister.create(Registries.SENSOR_TYPE, Millenaire2.MODID);

    public static final DeferredHolder<SensorType<?>, SensorType<VillageSensor>> VILLAGE_SENSOR =
            SENSOR_TYPES.register("village_sensor", () -> new SensorType<>(VillageSensor::new));

    public static final DeferredHolder<SensorType<?>, SensorType<ThreatSensor>> THREAT_SENSOR =
            SENSOR_TYPES.register("threat_sensor", () -> new SensorType<>(ThreatSensor::new));

    public static final DeferredHolder<SensorType<?>, SensorType<HostileMobSensor>> HOSTILE_MOB_SENSOR =
            SENSOR_TYPES.register("hostile_mob_sensor", () -> new SensorType<>(HostileMobSensor::new));

    public static void init() {
        // Class loading triggers registration
    }
}
