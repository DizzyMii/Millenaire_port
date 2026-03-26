package org.dizzymii.millenaire2.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.dizzymii.millenaire2.entity.brain.ModMemoryTypes;
import org.dizzymii.millenaire2.entity.brain.behaviour.ConsumeFoodBehavior;
import org.dizzymii.millenaire2.entity.brain.behaviour.ContextualToolSwapBehavior;
import org.dizzymii.millenaire2.entity.brain.behaviour.InventoryManagementBehavior;
import org.dizzymii.millenaire2.entity.brain.behaviour.StrategicRetreatBehavior;
import org.dizzymii.millenaire2.entity.brain.sensor.InventoryStateSensor;
import org.dizzymii.millenaire2.entity.brain.sensor.SelfPreservationSensor;
import org.dizzymii.millenaire2.entity.brain.smartbrain.BrainActivityGroup;
import org.dizzymii.millenaire2.entity.brain.smartbrain.ExtendedSensor;
import org.dizzymii.millenaire2.entity.brain.smartbrain.SmartBrainOwner;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A state-driven, "human-like" autonomous NPC built on the SmartBrainLib (SBL)
 * architecture.
 *
 * <p>Unlike standard reactive mobs, {@code HumanoidNPC} is driven by custom brain
 * memories, sensors, and contextual behaviours:
 * <ul>
 *   <li>{@link ModMemoryTypes#BASE_LOCATION} — home position anchor for
 *       patrol and return-home behaviours.
 *   <li>{@link ModMemoryTypes#MACRO_OBJECTIVE} — high-level state such as
 *       {@code "gather_wood"} or {@code "defend"}.
 *   <li>{@link ModMemoryTypes#NEEDED_MATERIALS} — materials the NPC must acquire,
 *       refreshed each scan cycle by {@link InventoryStateSensor}.
 * </ul>
 *
 * <p>Brain activities:
 * <ol>
 *   <li><b>CORE</b> — runs every tick (look-at and walk stubs; full integration
 *       pending vanilla {@code LookAtTargetSink} / {@code MoveToTargetSink}).
 *   <li><b>WORK</b> — "acquisition" phase; {@link ContextualToolSwapBehavior}
 *       ensures the NPC holds the correct tool before any work action fires.
 *   <li><b>IDLE</b> — fallback when no higher-priority activity is active.
 * </ol>
 *
 * <p>Actual pathfinding and block-breaking logic are intentionally out of scope
 * for this pass; only the memory, sensor, and tool-swapping architecture is
 * implemented here.
 */
public class HumanoidNPC extends PathfinderMob implements SmartBrainOwner<HumanoidNPC> {

    // ========== Attribute defaults ==========

    private static final double DEFAULT_HEALTH        = 20.0;
    private static final double DEFAULT_MOVE_SPEED    = 0.5;
    private static final double DEFAULT_ATTACK_DAMAGE = 3.0;
    private static final double DEFAULT_FOLLOW_RANGE  = 32.0;
    private static final int CARRIED_INVENTORY_CAPACITY = 36;
    private static final int MAX_NPC_FOOD_LEVEL = 20;
    /**
     * NeoForge 1.21.1 activity compatibility:
     * use built-in activities instead of runtime custom Activity registration.
     *
     * <p>Semantic aliasing:
     * <ul>
     *   <li>Survival (retreat/defensive behavior) maps to {@link Activity#FIGHT}.
     *   <li>Logistics (inventory management / non-combat utility phase) maps to {@link Activity#REST}
     *       as an alternate non-work state used by this entity.
     * </ul>
     */
    public static final Activity SURVIVAL_ACTIVITY = Activity.FIGHT;
    public static final Activity LOGISTICS_ACTIVITY = Activity.REST;

    // ========== Instance state ==========

    /**
     * Logical carried inventory — items the NPC uses for tasks.
     * Tools are selected from this list by {@link ContextualToolSwapBehavior}.
     */
    private final List<ItemStack> carriedInventory = new ArrayList<>();
    private int npcFoodLevel = MAX_NPC_FOOD_LEVEL;

    /**
     * Sensors that are polled each server tick to populate brain memories.
     * Cached at construction time; re-creating on every tick would be wasteful.
     */
    private final List<ExtendedSensor<? super HumanoidNPC>> sensorCache;

    // ========== Constructor ==========

    /**
     * Standard {@link PathfinderMob} constructor.
     *
     * <p>The sensor cache is built immediately so sensors are ready on the first
     * tick without lazy initialisation overhead.
     *
     * @param type  the registered entity type
     * @param level the world the entity is being created in
     */
    public HumanoidNPC(EntityType<? extends HumanoidNPC> type, Level level) {
        super(type, level);
        this.sensorCache = getSensors();
    }

    // ========== Attribute factory ==========

    /**
     * Creates the {@link AttributeSupplier} used when the entity type is registered
     * in {@link MillEntities} and
     * {@link org.dizzymii.millenaire2.Millenaire2#registerEntityAttributes}.
     *
     * <p>Attributes represent a typical human-scale combatant at default difficulty.
     *
     * @return a builder pre-populated with health, movement speed, attack damage,
     *         and follow range
     */
    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH,        DEFAULT_HEALTH)
                .add(Attributes.MOVEMENT_SPEED,    DEFAULT_MOVE_SPEED)
                .add(Attributes.ATTACK_DAMAGE,     DEFAULT_ATTACK_DAMAGE)
                .add(Attributes.FOLLOW_RANGE,      DEFAULT_FOLLOW_RANGE);
    }

    // ========== Brain wiring ==========

    /**
     * Returns the {@link Brain.Provider} that declares all memory module types
     * used by this entity.
     *
     * <p>Sensor types are not registered here because our stub
     * {@link ExtendedSensor} API does not wrap vanilla {@code SensorType}; sensors
     * are instead polled manually in {@link #customServerAiStep}.
     *
     * @return a Brain provider configured with all custom memory types
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Brain.Provider<HumanoidNPC> brainProvider() {
        return Brain.provider(
                List.of(
                        ModMemoryTypes.BASE_LOCATION.get(),
                        ModMemoryTypes.MACRO_OBJECTIVE.get(),
                        ModMemoryTypes.NEEDED_MATERIALS.get(),
                        ModMemoryTypes.NEEDS_HEALING.get(),
                        ModMemoryTypes.LAST_KNOWN_DANGER.get()
                ),
                List.of() // SensorTypes not used with the stub ExtendedSensor API
        );
    }

    /**
     * Constructs the {@link Brain} and installs all activity groups via the
     * {@link SmartBrainOwner} helper methods.
     *
     * @param dynamic serialised brain data (may be empty on first spawn)
     * @return a fully configured Brain ready for use
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        Brain<HumanoidNPC> brain = (Brain<HumanoidNPC>) brainProvider().makeBrain(dynamic);
        configureBrainActivities(brain);
        return brain;
    }

    /**
     * Returns this entity's typed {@link Brain}, casting from the raw wildcard
     * returned by the super-class.
     *
     * @return the typed Brain
     */
    @Override
    @SuppressWarnings("unchecked")
    public Brain<HumanoidNPC> getBrain() {
        return (Brain<HumanoidNPC>) super.getBrain();
    }

    // ========== SmartBrainOwner implementation ==========

    /**
     * Returns the list of custom sensors that populate brain memories each tick.
     *
     * <p>The {@link InventoryStateSensor} is the primary awareness probe; it runs
     * at twice-per-second cadence and writes {@link ModMemoryTypes#NEEDED_MATERIALS}.
     *
     * @return sensors polled during each AI tick via {@link #customServerAiStep}
     */
    @Override
    public List<ExtendedSensor<? super HumanoidNPC>> getSensors() {
        return List.of(new InventoryStateSensor(), new SelfPreservationSensor());
    }

    /**
     * CORE tasks run every tick regardless of which activity is currently active.
     *
     * <p>Placeholder: vanilla {@code LookAtTargetSink} and {@code MoveToTargetSink}
     * should be wired here once full SBL integration is complete.
     *
     * @return an empty core group (placeholder)
     */
    @Override
    public BrainActivityGroup<HumanoidNPC> getCoreTasks() {
        // Placeholder — vanilla look/walk behaviours are wired here in the full
        // SBL integration pass.
        return BrainActivityGroup.coreTasks();
    }

    /**
     * IDLE tasks execute when no higher-priority activity is currently active.
     *
     * <p>Placeholder: socialise and wander behaviours are added in a future pass.
     *
     * @return an empty idle group (placeholder)
     */
    @Override
    public BrainActivityGroup<HumanoidNPC> getIdleTasks() {
        return BrainActivityGroup.idleTasks();
    }

    /**
     * WORK / acquisition tasks execute during the NPC's active task phase.
     *
     * <p>{@link ContextualToolSwapBehavior} fires here to ensure the NPC holds
     * the correct tool before any block-breaking or attacking action proceeds.
     *
     * @return the work/acquisition activity group containing the tool-swap behaviour
     */
    @Override
    public BrainActivityGroup<HumanoidNPC> getWorkTasks() {
        return BrainActivityGroup.workTasks(new ContextualToolSwapBehavior());
    }

    public BrainActivityGroup<HumanoidNPC> getSurvivalTasks() {
        return BrainActivityGroup.customTasks(
                SURVIVAL_ACTIVITY,
                new ConsumeFoodBehavior(),
                new StrategicRetreatBehavior()
        );
    }

    public BrainActivityGroup<HumanoidNPC> getLogisticsTasks() {
        return BrainActivityGroup.customTasks(
                LOGISTICS_ACTIVITY,
                new InventoryManagementBehavior()
        );
    }

    @Override
    public List<BrainActivityGroup<HumanoidNPC>> getAllBrainActivities() {
        List<BrainActivityGroup<HumanoidNPC>> list = new ArrayList<>();
        BrainActivityGroup<HumanoidNPC> survival = getSurvivalTasks();
        BrainActivityGroup<HumanoidNPC> core = getCoreTasks();
        BrainActivityGroup<HumanoidNPC> work = getWorkTasks();
        BrainActivityGroup<HumanoidNPC> logistics = getLogisticsTasks();
        BrainActivityGroup<HumanoidNPC> idle = getIdleTasks();
        if (!survival.isEmpty()) list.add(survival);
        if (!core.isEmpty()) list.add(core);
        if (!work.isEmpty()) list.add(work);
        if (!logistics.isEmpty()) list.add(logistics);
        if (!idle.isEmpty()) list.add(idle);
        return list;
    }

    // ========== Tick logic ==========

    /**
     * Server-side AI step: polls all sensors and then runs the vanilla Brain tick.
     *
     * <p>Sensors are polled <em>before</em> the brain tick so that memories written
     * by {@link InventoryStateSensor} are available to behaviours on the same tick.
     */
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (level() instanceof ServerLevel sl) {
            for (ExtendedSensor<? super HumanoidNPC> sensor : sensorCache) {
                sensor.tick(sl, this);
            }
            updateActivityState();
            level().getProfiler().push("humanoidNpcBrain");
            getBrain().tick(sl, this);
            level().getProfiler().pop();
        }
    }

    // ========== Inventory accessors ==========

    /**
     * Returns an unmodifiable view of the NPC's carried inventory.
     *
     * <p>Used by {@link ContextualToolSwapBehavior} and {@link InventoryStateSensor}
     * to inspect available tools without risking external mutation.
     *
     * @return read-only view of the carried item list
     */
    public List<ItemStack> getCarriedInventory() {
        return Collections.unmodifiableList(carriedInventory);
    }

    public int getCarriedInventoryCapacity() {
        return CARRIED_INVENTORY_CAPACITY;
    }

    public double getCarriedInventoryFillRatio() {
        return (double) carriedInventory.size() / (double) CARRIED_INVENTORY_CAPACITY;
    }

    /**
     * Adds an item to the NPC's carried inventory.
     *
     * <p>Empty stacks are silently ignored to keep the inventory clean.
     *
     * @param stack the item to add; must not be {@code null}
     */
    public void addToCarriedInventory(ItemStack stack) {
        if (!stack.isEmpty() && carriedInventory.size() < CARRIED_INVENTORY_CAPACITY) {
            carriedInventory.add(stack);
        }
    }

    public ItemStack getCarriedInventorySlot(int index) {
        if (index < 0 || index >= carriedInventory.size()) {
            return ItemStack.EMPTY;
        }
        return carriedInventory.get(index);
    }

    public ItemStack removeCarriedInventorySlot(int index) {
        if (index < 0 || index >= carriedInventory.size()) {
            return ItemStack.EMPTY;
        }
        return carriedInventory.remove(index);
    }

    public void pruneEmptyCarriedInventory() {
        carriedInventory.removeIf(ItemStack::isEmpty);
    }

    public int getNpcFoodLevel() {
        return npcFoodLevel;
    }

    public void setNpcFoodLevel(int foodLevel) {
        this.npcFoodLevel = Math.max(0, Math.min(MAX_NPC_FOOD_LEVEL, foodLevel));
    }

    // ========== Home location ==========

    /**
     * Stores or clears the NPC's home / base location in brain memory.
     *
     * <p>The stored {@link GlobalPos} is the anchor used by patrol and
     * return-home behaviours (implemented in a future pass).
     *
     * @param pos the global position to write, or {@code null} to erase the memory
     */
    public void setBaseLocation(@Nullable GlobalPos pos) {
        if (pos != null) {
            getBrain().setMemory(ModMemoryTypes.BASE_LOCATION.get(), pos);
        } else {
            getBrain().eraseMemory(ModMemoryTypes.BASE_LOCATION.get());
        }
    }

    // ========== Private brain-wiring helpers ==========

    /**
     * Iterates all non-empty {@link BrainActivityGroup}s from
     * {@link SmartBrainOwner#getAllBrainActivities()} and registers each one with
     * the vanilla {@link Brain} using sequential priority indices.
     *
     * <p>Uses the same priority-registration pattern as
     * {@link org.dizzymii.millenaire2.entity.brain.VillagerBrainConfig}.
     *
     * @param brain the brain to configure
     */
    private void configureBrainActivities(Brain<HumanoidNPC> brain) {
        for (BrainActivityGroup<HumanoidNPC> group : getAllBrainActivities()) {
            if (!group.isEmpty()) {
                registerGroup(brain, group);
            }
        }
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.WORK);
        brain.setActiveActivityIfPossible(Activity.WORK);
    }

    /**
     * Registers a single {@link BrainActivityGroup} with the given {@link Brain},
     * assigning sequential integer priorities (0, 1, 2 …) to each behaviour.
     *
     * @param brain the target brain
     * @param group the non-empty group to register
     */
    private static void registerGroup(Brain<HumanoidNPC> brain,
                                      BrainActivityGroup<HumanoidNPC> group) {
        ImmutableList<BehaviorControl<? super HumanoidNPC>> behaviours = group.behaviours();
        ImmutableList.Builder<Pair<Integer, BehaviorControl<? super HumanoidNPC>>> builder =
                ImmutableList.builder();
        for (int i = 0; i < behaviours.size(); i++) {
            builder.add(Pair.of(i, behaviours.get(i)));
        }
        brain.addActivityWithConditions(group.activity(), builder.build(), Set.of());
    }

    private void updateActivityState() {
        if (shouldPrioritizeSurvival()) {
            getBrain().setActiveActivityIfPossible(SURVIVAL_ACTIVITY);
            return;
        }
        if (shouldRunLogistics()) {
            getBrain().setActiveActivityIfPossible(LOGISTICS_ACTIVITY);
            return;
        }
        getBrain().setActiveActivityIfPossible(Activity.WORK);
    }

    private boolean shouldPrioritizeSurvival() {
        boolean needsHealing = getBrain().getMemory(ModMemoryTypes.NEEDS_HEALING.get()).orElse(false);
        boolean hasDanger = getBrain().getMemory(ModMemoryTypes.LAST_KNOWN_DANGER.get()).isPresent();
        boolean lowHunger = getNpcFoodLevel() <= 12;
        return needsHealing || hasDanger || lowHunger;
    }

    private boolean shouldRunLogistics() {
        return !shouldPrioritizeSurvival() && getCarriedInventoryFillRatio() > 0.80D;
    }
}
