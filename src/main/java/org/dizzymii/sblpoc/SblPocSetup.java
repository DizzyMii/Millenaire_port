package org.dizzymii.sblpoc;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.sblpoc.sensor.IncomingDamageSensor;
import org.dizzymii.sblpoc.sensor.NearbyThreatSensor;

import java.util.Optional;

/**
 * Self-contained registration for the PoC NPC.
 * Only dependency on Millenaire2 is the shared ENTITY_TYPES register and MODID.
 */
public class SblPocSetup {

    // ========== Sensor Types ==========
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES =
            DeferredRegister.create(Registries.SENSOR_TYPE, Millenaire2.MODID);

    public static final DeferredHolder<SensorType<?>, SensorType<NearbyThreatSensor>> NEARBY_THREAT =
            SENSOR_TYPES.register("poc_nearby_threat", () -> new SensorType<>(NearbyThreatSensor::new));

    public static final DeferredHolder<SensorType<?>, SensorType<IncomingDamageSensor>> INCOMING_DAMAGE =
            SENSOR_TYPES.register("poc_incoming_damage", () -> new SensorType<>(IncomingDamageSensor::new));

    // ========== Memory Types ==========
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_TYPES =
            DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, Millenaire2.MODID);

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Boolean>> SHOULD_SHIELD =
            MEMORY_TYPES.register("poc_should_shield",
                    () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Integer>> INCOMING_DAMAGE_TICKS =
            MEMORY_TYPES.register("poc_incoming_damage_ticks",
                    () -> new MemoryModuleType<>(Optional.of(Codec.INT)));

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Long>> FOOD_COOLDOWN =
            MEMORY_TYPES.register("poc_food_cooldown",
                    () -> new MemoryModuleType<>(Optional.empty()));

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Long>> SHIELD_COOLDOWN =
            MEMORY_TYPES.register("poc_shield_cooldown",
                    () -> new MemoryModuleType<>(Optional.empty()));

    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Integer>> NEARBY_HOSTILE_COUNT =
            MEMORY_TYPES.register("poc_nearby_hostile_count",
                    () -> new MemoryModuleType<>(Optional.of(Codec.INT)));

    // Combat state: "melee", "ranged", or "cover"
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<String>> COMBAT_STATE =
            MEMORY_TYPES.register("poc_combat_state",
                    () -> new MemoryModuleType<>(Optional.of(Codec.STRING)));

    // True = strafe right, False = strafe left. Flipped periodically.
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Boolean>> STRAFE_RIGHT =
            MEMORY_TYPES.register("poc_strafe_right",
                    () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));

    // Whether the NPC is currently being shot at (set by IncomingDamageSensor)
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Boolean>> UNDER_RANGED_FIRE =
            MEMORY_TYPES.register("poc_under_ranged_fire",
                    () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));

    // ========== Entity Type ==========
    public static final DeferredHolder<EntityType<?>, EntityType<PocNpc>> POC_NPC =
            Millenaire2.ENTITY_TYPES.register("poc_npc",
                    () -> EntityType.Builder.of(PocNpc::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .updateInterval(3)
                            .build("poc_npc"));

    public static void init() {
        // Force class loading
    }
}
