package org.dizzymii.millenaire2.entity.brain;

import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.dizzymii.millenaire2.Millenaire2;

import java.util.List;
import java.util.Optional;

/**
 * Central registry for all custom {@link MemoryModuleType} instances used by
 * the HumanoidNPC brain.
 *
 * <p>Register is held here as a public constant so it can be handed to the
 * NeoForge mod event bus in {@link org.dizzymii.millenaire2.Millenaire2}.
 *
 * <p>Memory lifecycle in the SBL tick loop:
 * <ol>
 *   <li>{@link org.dizzymii.millenaire2.entity.brain.sensor.InventoryStateSensor}
 *       writes {@link #NEEDED_MATERIALS} every 10 ticks.
 *   <li>{@link org.dizzymii.millenaire2.entity.brain.behaviour.ContextualToolSwapBehavior}
 *       reads {@link #MACRO_OBJECTIVE} to decide which tool to equip.
 *   <li>{@link #BASE_LOCATION} is written once when the NPC spawns and is used
 *       by patrol / return-home behaviours.
 * </ol>
 */
public final class ModMemoryTypes {

    /**
     * DeferredRegister for all custom memory module types.
     * Must be registered with the mod event bus in the mod constructor.
     */
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES =
            DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, Millenaire2.MODID);

    // ========== Memory registrations ==========

    /**
     * Stores the NPC's home / spawn coordinates as a {@link GlobalPos} (dimension + block pos).
     * Written once on spawn via
     * {@link org.dizzymii.millenaire2.entity.HumanoidNPC#setBaseLocation}.
     */
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<GlobalPos>>
            BASE_LOCATION = MEMORY_MODULE_TYPES.register("base_location",
                    () -> new MemoryModuleType<>(Optional.of(GlobalPos.CODEC)));

    /**
     * Stores the NPC's current high-level objective as a plain string, e.g.
     * {@code "gather_wood"}, {@code "mine_stone"}, or {@code "defend"}.
     * Read by {@link org.dizzymii.millenaire2.entity.brain.behaviour.ContextualToolSwapBehavior}
     * to select the correct tool category.
     */
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<String>>
            MACRO_OBJECTIVE = MEMORY_MODULE_TYPES.register("macro_objective",
                    () -> new MemoryModuleType<>(Optional.of(Codec.STRING)));

    /**
     * Stores a dynamic list of item identifiers that the NPC is missing or needs
     * to replace (e.g. {@code "main_hand_tool:damaged:item.minecraft.iron_pickaxe"}).
     * Written each scan cycle by
     * {@link org.dizzymii.millenaire2.entity.brain.sensor.InventoryStateSensor}.
     */
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<List<String>>>
            NEEDED_MATERIALS = MEMORY_MODULE_TYPES.register("needed_materials",
                    () -> new MemoryModuleType<>(Optional.of(Codec.STRING.listOf())));

    // ========== Class-loading trigger ==========

    /**
     * Called from {@link org.dizzymii.millenaire2.Millenaire2} to force class loading
     * and ensure all static registration fields are initialised before the event bus
     * fires.
     */
    public static void init() {
        // Intentionally empty — static fields are initialised on first class access.
    }

    private ModMemoryTypes() {}
}
