package org.dizzymii.millenaire2.entity.ai;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;
import java.util.Optional;

/**
 * Custom MemoryModuleTypes for Millénaire villagers.
 * These store villager-specific state inside the Brain system.
 */
public class MillMemoryTypes {

    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_TYPES =
            DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, Millenaire2.MODID);

    /** The BlockPos of this villager's home building */
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockPos>> HOME_BUILDING_POS =
            MEMORY_TYPES.register("home_building_pos", () -> new MemoryModuleType<>(Optional.of(BlockPos.CODEC)));

    /** The BlockPos of this villager's townhall */
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockPos>> TOWNHALL_POS =
            MEMORY_TYPES.register("townhall_pos", () -> new MemoryModuleType<>(Optional.of(BlockPos.CODEC)));

    /** Whether the village is currently under attack */
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Boolean>> VILLAGE_UNDER_ATTACK =
            MEMORY_TYPES.register("village_under_attack", () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));

    /** The current construction target block position */
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockPos>> CONSTRUCTION_TARGET =
            MEMORY_TYPES.register("construction_target", () -> new MemoryModuleType<>(Optional.empty()));

    /** The key of the active Millénaire goal (for display/sync purposes) */
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<String>> ACTIVE_GOAL_KEY =
            MEMORY_TYPES.register("active_goal_key", () -> new MemoryModuleType<>(Optional.of(Codec.STRING)));

    /** Whether this villager is a raider */
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Boolean>> IS_RAIDER =
            MEMORY_TYPES.register("is_raider", () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));

    /** Whether this villager is hired by a player */
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<String>> HIRED_BY =
            MEMORY_TYPES.register("hired_by", () -> new MemoryModuleType<>(Optional.of(Codec.STRING)));

    /** The BlockPos of a farming target crop */
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockPos>> FARM_TARGET =
            MEMORY_TYPES.register("farm_target", () -> new MemoryModuleType<>(Optional.empty()));

    /** The BlockPos of a tree to chop */
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockPos>> CHOP_TARGET =
            MEMORY_TYPES.register("chop_target", () -> new MemoryModuleType<>(Optional.empty()));

    public static void init() {
        // Class loading triggers registration
    }
}
