# SmartBrainLib Reference — NeoForge 1.21.1

> **Library Version**: SBL 1.16.11  
> **Platform**: NeoForge 1.21.1  
> **Maven**: `net.tslat.smartbrainlib:SmartBrainLib-neoforge-1.21.1:1.16.11`  
> **Wiki**: https://github.com/Tslat/SmartBrainLib/wiki  
> **Discord**: https://discord.gg/Wk37XXgJN3

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Setup Boilerplate](#2-setup-boilerplate)
3. [Sensors](#3-sensors)
4. [Memories](#4-memories)
5. [Behaviours](#5-behaviours)
6. [Group Behaviours](#6-group-behaviours)
7. [Activity Groups](#7-activity-groups)
8. [Utility Classes](#8-utility-classes)
9. [Registration (NeoForge)](#9-registration-neoforge)
10. [Complete Examples](#10-complete-examples)
11. [Common Pitfalls & Best Practices](#11-common-pitfalls--best-practices)

---

## 1. Architecture Overview

### Brain vs Goals

The vanilla **Goal system** is self-contained: each Goal handles its own sensing, state, and actions. Goals communicate minimally via flags (`MOVE`, `LOOK`, `TARGET`). This makes them simple but rigid and wasteful — every goal re-scans for entities independently.

The **Brain system** (used by Villagers, Piglins, Wardens in vanilla) operates as a unified whole:

```
┌─────────────────────────────────────────────┐
│                   BRAIN                      │
│                                              │
│  ┌──────────┐   ┌──────────┐   ┌─────────┐  │
│  │ SENSORS  │──▶│ MEMORIES │◀──│BEHAVIOURS│  │
│  │          │   │          │──▶│          │  │
│  └──────────┘   └──────────┘   └─────────┘  │
│                                              │
│  ┌──────────────────────────────────────┐    │
│  │           ACTIVITIES                  │    │
│  │  CORE | IDLE | FIGHT | custom...     │    │
│  └──────────────────────────────────────┘    │
└─────────────────────────────────────────────┘
```

- **Sensors** gather data from the world on a slow timer (typically every 10-40 ticks) and store it in **Memories**.
- **Memories** are the shared data store. All behaviours and sensors read/write from the same memory pool.
- **Behaviours** read memories and perform actions. Each behaviour does ONE thing (look, move, attack — not all three).
- **Activities** group behaviours into categories (CORE, IDLE, FIGHT). The brain switches between activities based on memory state.

### Why SmartBrainLib?

Vanilla's brain implementation is verbose, uses immutable builders, and is poorly optimized. SBL fixes this by:

- Making behaviours and sensors configurable via fluent API (`.runFor()`, `.startCondition()`, `.stopIf()`)
- Replacing immutable lists with mutable `ObjectArrayList` for efficiency
- Providing pre-built behaviours for common patterns (melee attack, ranged attack, flee, strafe, etc.)
- Auto-registering required memories from sensors and behaviours
- Simplifying activity management to 3 method overrides

---

## 2. Setup Boilerplate

### Step 1: Implement SmartBrainOwner

Your entity class must extend `LivingEntity` (or any subclass like `PathfinderMob`, `Monster`) and implement `SmartBrainOwner<YourEntity>`:

```java
public class MyMob extends PathfinderMob implements SmartBrainOwner<MyMob> {

    public MyMob(EntityType<? extends MyMob> type, Level level) {
        super(type, level);
    }
```

### Step 2: Return SmartBrainProvider

Override `brainProvider()` to replace the vanilla brain system:

```java
    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }
```

`SmartBrainProvider` automatically collects all required `MemoryModuleType`s and `SensorType`s from your `getSensors()`, `getCoreTasks()`, `getIdleTasks()`, and `getFightTasks()` methods. You never need to manually list them.

### Step 3: Tick the Brain

Override `customServerAiStep()` (if extending `Mob`/`PathfinderMob`) or `serverAiStep()` (if extending `LivingEntity` directly):

```java
    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }
```

`tickBrain()` is a default method on `SmartBrainOwner` that handles the entire brain tick cycle.

### Step 4: Define Sensors, Core, Idle, and Fight Tasks

Override the 4 key methods (detailed in sections below):

```java
    @Override
    public List<? extends ExtendedSensor<? extends MyMob>> getSensors() { ... }

    @Override
    public BrainActivityGroup<? extends MyMob> getCoreTasks() { ... }

    @Override
    public BrainActivityGroup<? extends MyMob> getIdleTasks() { ... }

    @Override
    public BrainActivityGroup<? extends MyMob> getFightTasks() { ... }
```

### Minimal Complete Entity

```java
public class MyMob extends PathfinderMob implements SmartBrainOwner<MyMob> {

    public MyMob(EntityType<? extends MyMob> type, Level level) {
        super(type, level);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends MyMob>> getSensors() {
        return ObjectArrayList.of(
            new NearbyLivingEntitySensor<>(),
            new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends MyMob> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
            new LookAtTarget<>(),
            new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends MyMob> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
            new FirstApplicableBehaviour<MyMob>(
                new TargetOrRetaliate<>(),
                new SetPlayerLookTarget<>(),
                new SetRandomLookTarget<>()
            ),
            new OneRandomBehaviour<>(
                new SetRandomWalkTarget<>(),
                new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
            )
        );
    }

    @Override
    public BrainActivityGroup<? extends MyMob> getFightTasks() {
        return BrainActivityGroup.fightTasks(
            new InvalidateAttackTarget<>(),
            new SetWalkTargetToAttackTarget<>(),
            new AnimatableMeleeAttack<>(0)
        );
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }
}
```

---

## 3. Sensors

Sensors are the "eyes and ears" of the brain. They tick on a slow timer, scan the world, and write results into memories.

### ExtendedSensor API

All SBL sensors extend `ExtendedSensor<E extends LivingEntity>`. Key methods:

| Method | Purpose |
|---|---|
| `memoriesUsed()` | Return list of `MemoryModuleType<?>` this sensor reads/writes. **Required override.** |
| `type()` | Return the registered `SensorType`. **Required override for custom sensors.** |
| `doTick(ServerLevel, E)` | The actual sensing logic. Called on the sensor's scan timer. **Required override.** |
| `setScanRate(ToIntFunction<E>)` | Set how often (in ticks) the sensor runs. Default ~20 ticks. |
| `scanRate()` | Get the current scan rate function. |

### Writing a Custom Sensor (from SBL wiki)

Official example — detecting if the entity is in lava:

```java
public class InLavaSensor<E extends LivingEntity> extends PredicateSensor<E, E> {

    private static final List<MemoryModuleType<?>> MEMORIES =
            ObjectArrayList.of(MyMemoryTypes.IS_IN_LAVA);

    public InLavaSensor() {
        super((entity2, entity) -> entity.isInLava()); // Set predicate
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return MySensorTypes.IN_LAVA.get(); // Your registered SensorType
    }

    @Override
    protected void doTick(ServerLevel level, E entity) {
        if (predicate().test(entity, entity)) {
            BrainUtils.setMemory(entity, MyMemoryTypes.IS_IN_LAVA, true);
        } else {
            BrainUtils.clearMemory(entity, MyMemoryTypes.IS_IN_LAVA);
        }
    }
}
```

**Design rules** (from SBL wiki):
1. A sensor should fill **one function only**.
2. A sensor should **not perform any actions** on the entity besides setting/clearing/checking memories.

### Built-in Sensors — Full Catalog

#### Vanilla Wrappers (`api.core.sensor.vanilla.*`)

These are SBL-enhanced wrappers around vanilla sensors, gaining `ExtendedSensor` configurability:

| Sensor | Memories Written | Default Scan Rate |
|---|---|---|
| `NearbyLivingEntitySensor<E>` | `NEAREST_LIVING_ENTITIES`, `NEAREST_VISIBLE_LIVING_ENTITIES` | ~20 ticks |
| `NearbyPlayersSensor<E>` | `NEAREST_PLAYERS`, `NEAREST_VISIBLE_PLAYER`, `NEAREST_VISIBLE_ATTACKABLE_PLAYER` | ~20 ticks |
| `HurtBySensor<E>` | `HURT_BY`, `HURT_BY_ENTITY` | ~20 ticks |
| `NearbyHostileSensor<E>` | `NEAREST_HOSTILE` | ~20 ticks |
| `NearestItemSensor<E>` | `NEAREST_VISIBLE_WANTED_ITEM` | ~20 ticks |
| `ItemTemptingSensor<E>` | `TEMPTING_PLAYER`, `TEMPTATION_COOLDOWN_TICKS` | ~20 ticks |
| `NearbyAdultSensor<E>` | `NEAREST_VISIBLE_ADULT` | ~20 ticks |
| `NearbyBabySensor<E>` | `NEAREST_VISIBLE_BABY` | ~20 ticks |
| `NearbyGolemSensor<E>` | `NEAREST_VISIBLE_GOLEM` | ~20 ticks |
| `NearestHomeSensor<E>` | `NEAREST_BED` | ~20 ticks |
| `SecondaryPoiSensor<E>` | `SECONDARY_JOB_SITE` | ~20 ticks |
| `InWaterSensor<E>` | `IS_IN_WATER` | ~20 ticks |
| `AxolotlSpecificSensor<E>` | Axolotl-specific memories | ~20 ticks |
| `FrogSpecificSensor<E>` | Frog-specific memories | ~20 ticks |
| `HoglinSpecificSensor<E>` | Hoglin-specific memories | ~20 ticks |
| `PiglinSpecificSensor<E>` | Piglin-specific memories | ~20 ticks |
| `PiglinBruteSpecificSensor<E>` | Piglin Brute-specific memories | ~20 ticks |
| `WardenSpecificSensor<E>` | Warden-specific memories | ~20 ticks |

#### Custom SBL Sensors (`api.core.sensor.custom.*`)

| Sensor | Purpose | Memories Written |
|---|---|---|
| `GenericAttackTargetSensor<E>` | Detects valid attack targets using configurable predicate | `NEAREST_ATTACKABLE` |
| `IncomingProjectilesSensor<E>` | Detects projectiles heading toward the entity | `INCOMING_PROJECTILES` (SBL memory) |
| `NearbyBlocksSensor<E>` | Scans for specific block types nearby | `NEAREST_VISIBLE_BLOCKS` (SBL memory) |
| `NearbyItemsSensor<E>` | Scans for item entities nearby | `NEAREST_VISIBLE_ITEMS` (SBL memory) |
| `UnreachableTargetSensor<E>` | Detects when the current target is unreachable via pathfinding | `UNREACHABLE_TARGET` (SBL memory) |

#### Abstract Base Sensors

| Sensor | Purpose |
|---|---|
| `EntityFilteringSensor<E>` | Base for sensors that filter a list of entities by a predicate |
| `PredicateSensor<T, E>` | Base for sensors that find the first entity matching a predicate |

---

## 4. Memories

Memories are the shared data store of the brain. Any sensor or behaviour can read/write any memory.

### MemoryModuleType

A `MemoryModuleType<T>` is the registry key for a memory of type `T`. The brain can hold **at most one value** per `MemoryModuleType` at any time.

### MemoryStatus (Behaviour Prerequisites)

When defining behaviour prerequisites, you use `MemoryStatus`:

| Status | Meaning |
|---|---|
| `VALUE_PRESENT` | Memory **must** have a value for this behaviour to run |
| `VALUE_ABSENT` | Memory **must NOT** have a value for this behaviour to run |
| `REGISTERED` | Memory just needs to be registered (may or may not have a value) |

Example: A fight behaviour requires `ATTACK_TARGET` to be present:
```java
Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)
```

A targeting behaviour requires `ATTACK_TARGET` to be absent (don't re-target if already fighting):
```java
Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT)
```

### Key Vanilla Memories

| Memory | Type | Set By | Used By |
|---|---|---|---|
| `NEAREST_LIVING_ENTITIES` | `List<LivingEntity>` | `NearbyLivingEntitySensor` | Targeting behaviours |
| `NEAREST_VISIBLE_LIVING_ENTITIES` | `NearestVisibleLivingEntities` | `NearbyLivingEntitySensor` | Targeting behaviours |
| `NEAREST_HOSTILE` | `LivingEntity` | `NearbyHostileSensor` / custom | `TargetOrRetaliate` |
| `ATTACK_TARGET` | `LivingEntity` | Targeting behaviours | Fight behaviours, activity switching |
| `HURT_BY` | `DamageSource` | `HurtBySensor` | Retaliation behaviours |
| `HURT_BY_ENTITY` | `LivingEntity` | `HurtBySensor` | Retaliation behaviours |
| `WALK_TARGET` | `WalkTarget` | Movement behaviours | `MoveToWalkTarget` |
| `LOOK_TARGET` | `PositionTracker` | Look behaviours | `LookAtTarget` |
| `NEAREST_VISIBLE_PLAYER` | `Player` | `NearbyPlayersSensor` | `SetPlayerLookTarget` |
| `CANT_REACH_WALK_TARGET_SINCE` | `Long` | `MoveToWalkTarget` | Stuck detection |

### BrainUtils Helper Methods

`BrainUtils` (from `net.tslat.smartbrainlib.util`) provides static helpers:

```java
// Set a memory value
BrainUtils.setMemory(entity, MemoryModuleType.ATTACK_TARGET, targetEntity);

// Set a memory with expiry (auto-erased after N ticks)
BrainUtils.setMemory(entity, MemoryModuleType.ATTACK_TARGET, targetEntity, 200);

// Get a memory value (nullable)
LivingEntity target = BrainUtils.getMemory(entity, MemoryModuleType.ATTACK_TARGET);

// Check if a memory has a value
boolean hasTarget = BrainUtils.hasMemory(entity, MemoryModuleType.ATTACK_TARGET);

// Erase (clear) a memory
BrainUtils.clearMemory(entity, MemoryModuleType.ATTACK_TARGET);

// Set the attack target (convenience — also handles LOOK_TARGET)
BrainUtils.setTargetOfEntity(entity, target);

// Clear the attack target
BrainUtils.clearTargetOfEntity(entity);
```

### Registering Custom Memories (NeoForge)

```java
public class MyMemoryTypes {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_TYPES =
            DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, "mymod");

    // With codec (persists across save/load):
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockPos>> HOME_POS =
            MEMORY_TYPES.register("home_pos",
                () -> new MemoryModuleType<>(Optional.of(BlockPos.CODEC)));

    // Without codec (transient, lost on save/load):
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<BlockPos>> TEMP_TARGET =
            MEMORY_TYPES.register("temp_target",
                () -> new MemoryModuleType<>(Optional.empty()));

    // Boolean with codec:
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Boolean>> IS_IN_LAVA =
            MEMORY_TYPES.register("is_in_lava",
                () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));

    // String with codec:
    public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<String>> CURRENT_TASK =
            MEMORY_TYPES.register("current_task",
                () -> new MemoryModuleType<>(Optional.of(Codec.STRING)));
}
```

Register in your mod constructor:
```java
MyMemoryTypes.MEMORY_TYPES.register(modEventBus);
```

---

## 5. Behaviours

Behaviours are the action-taking components of the brain. Each behaviour does **one thing** and is designed to compose with others.

### ExtendedBehaviour Lifecycle

`ExtendedBehaviour<E extends LivingEntity>` is the base class for all SBL behaviours. The lifecycle is:

```
1. getMemoryRequirements()     — Are the required memories in the right state?
         │ YES
         ▼
2. checkExtraStartConditions() — Any additional checks? (default: true)
         │ YES
         ▼
3. start()                     — Begin the behaviour (set memories, init state)
         │
         ▼
4. tick()                      — Called every tick while running
         │
         ▼
5. shouldKeepRunning()         — Continue running? (default: true)
         │ NO (or runFor() expired)
         ▼
6. stop()                      — Clean up (erase memories, reset state)
```

### Key Override Methods

```java
public class MyBehaviour extends ExtendedBehaviour<MyMob> {

    // REQUIRED: Define memory prerequisites
    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return ObjectArrayList.of(
            Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)
        );
    }

    // OPTIONAL: Additional start checks beyond memory requirements
    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, MyMob entity) {
        return true; // default
    }

    // OPTIONAL: Called once when the behaviour starts
    @Override
    protected void start(ServerLevel level, MyMob entity, long gameTime) {}

    // OPTIONAL: Called every tick while running
    @Override
    protected void tick(ServerLevel level, MyMob entity, long gameTime) {}

    // OPTIONAL: Should the behaviour continue running?
    @Override
    protected boolean shouldKeepRunning(MyMob entity) {
        return true; // default
    }

    // OPTIONAL: Called once when the behaviour stops
    @Override
    protected void stop(ServerLevel level, MyMob entity, long gameTime) {}
}
```

### Fluent Configuration Methods

Every `ExtendedBehaviour` supports inline configuration:

```java
new AnimatableMeleeAttack<>(0)
    .runFor(entity -> 200)                                   // Max duration in ticks
    .startCondition(entity -> entity.getHealth() > 5)        // Extra start predicate
    .stopIf(entity -> !entity.isAlive())                     // Stop predicate (checked each tick)
    .whenStarting(entity -> entity.swing(InteractionHand.MAIN_HAND))  // Callback on start
    .whenStopping(entity -> entity.setAggressive(false))     // Callback on stop
    .cooldownFor(entity -> 40)                               // Cooldown before can run again
```

| Method | Purpose |
|---|---|
| `.runFor(ToIntFunction<E>)` | Maximum ticks this behaviour can run before auto-stopping |
| `.startCondition(Predicate<E>)` | Additional predicate checked alongside `checkExtraStartConditions` |
| `.stopIf(Predicate<E>)` | Predicate checked each tick; if true, behaviour stops |
| `.whenStarting(Consumer<E>)` | Callback fired when `start()` is called |
| `.whenStopping(Consumer<E>)` | Callback fired when `stop()` is called |
| `.cooldownFor(ToIntFunction<E>)` | Ticks before the behaviour can start again after stopping |
| `.noTimeout()` | Remove the default timeout (behaviour runs until explicitly stopped) |

### Built-in Behaviours — Full Catalog

#### Attack Behaviours (`api.core.behaviour.custom.attack.*`)

| Behaviour | Description | Key Memories |
|---|---|---|
| `AnimatableMeleeAttack<E>` | Swing at target when in melee range. Constructor takes `int attackIntervalTicks`. | Requires `ATTACK_TARGET` |
| `AnimatableRangedAttack<E>` | Ranged attack with configurable wind-up. | Requires `ATTACK_TARGET` |
| `BowAttack<E>` | Bow-specific ranged attack (charge + release). | Requires `ATTACK_TARGET` |
| `ConditionlessAttack<E>` | Instant one-shot attack with no conditions beyond memory. | Requires `ATTACK_TARGET` |
| `ConditionlessHeldAttack<E>` | Held (channeled) attack with no extra conditions. | Requires `ATTACK_TARGET` |
| `LeapAtTarget<E>` | Jump toward the attack target. | Requires `ATTACK_TARGET` |

#### Look Behaviours (`api.core.behaviour.custom.look.*`)

| Behaviour | Description | Key Memories |
|---|---|---|
| `LookAtTarget<E>` | Turn head to face whatever is in `LOOK_TARGET` memory. **Core task.** | Reads `LOOK_TARGET` |
| `LookAtAttackTarget<E>` | Set `LOOK_TARGET` to the current `ATTACK_TARGET`. | Reads `ATTACK_TARGET`, writes `LOOK_TARGET` |

#### Movement Behaviours (`api.core.behaviour.custom.move.*`)

| Behaviour | Description | Key Memories |
|---|---|---|
| `MoveToWalkTarget<E>` | Walk toward `WALK_TARGET` using vanilla pathfinding. **Core task.** | Reads `WALK_TARGET` |
| `WalkOrRunToWalkTarget<E>` | Like `MoveToWalkTarget` but can sprint when target is far. | Reads `WALK_TARGET` |
| `FloatToSurfaceOfFluid<E>` | Swim up when in water. **Core task.** | — |
| `AvoidEntity<E>` | Flee from a specific entity type. | Writes `WALK_TARGET` |
| `FleeTarget<E>` | Run away from the current `ATTACK_TARGET`. | Reads `ATTACK_TARGET`, writes `WALK_TARGET` |
| `EscapeSun<E>` | Move to shade (undead behaviour). | Writes `WALK_TARGET` |
| `FollowEntity<E>` | Follow a specific entity. | Writes `WALK_TARGET` |
| `FollowOwner<E>` | Follow the entity's owner (tameable). | Writes `WALK_TARGET` |
| `FollowParent<E>` | Baby follows nearest adult. | Writes `WALK_TARGET` |
| `FollowTemptation<E>` | Follow a player holding a tempting item. | Reads `TEMPTING_PLAYER` |
| `InteractWithDoor<E>` | Open/close doors when pathing through them. | — |
| `StayWithinDistanceOfAttackTarget<E>` | Keep distance from target (for ranged mobs). | Reads `ATTACK_TARGET` |
| `StrafeTarget<E>` | Circle-strafe around the attack target. | Reads `ATTACK_TARGET` |

#### Path/Walk-Target Behaviours (`api.core.behaviour.custom.path.*`)

These behaviours **set** `WALK_TARGET` but don't move the entity — movement is handled by `MoveToWalkTarget` in core tasks.

| Behaviour | Description | Writes |
|---|---|---|
| `SetRandomWalkTarget<E>` | Pick a random nearby position to walk to. | `WALK_TARGET` |
| `SetRandomFlyingTarget<E>` | Pick a random flying destination. | `WALK_TARGET` |
| `SetRandomHoverTarget<E>` | Pick a random hovering position. | `WALK_TARGET` |
| `SetRandomSwimTarget<E>` | Pick a random swimming destination. | `WALK_TARGET` |
| `SeekRandomNearbyPosition<E>` | Seek a random position near the entity. | `WALK_TARGET` |
| `SetWalkTargetToAttackTarget<E>` | Set walk target to the current attack target's position. | `WALK_TARGET` |
| `SetWalkTargetToBlock<E>` | Set walk target to a specific block position. | `WALK_TARGET` |

#### Targeting Behaviours (`api.core.behaviour.custom.target.*`)

These behaviours determine **what** to fight or look at.

| Behaviour | Description | Key Memories |
|---|---|---|
| `TargetOrRetaliate<E>` | Set `ATTACK_TARGET` to nearest hostile or last attacker. The primary targeting behaviour. | Writes `ATTACK_TARGET` |
| `InvalidateAttackTarget<E>` | Clear `ATTACK_TARGET` if the target is dead, too far, or otherwise invalid. | Reads/erases `ATTACK_TARGET` |
| `SetAttackTarget<E>` | Manually set an attack target based on a configurable predicate. | Writes `ATTACK_TARGET` |
| `SetRetaliateTarget<E>` | Set attack target specifically to the entity that last hurt us. | Reads `HURT_BY_ENTITY`, writes `ATTACK_TARGET` |
| `SetAdditionalAttackTargets<E>` | Add secondary targets beyond the primary one. | Writes SBL target memories |
| `SetPlayerLookTarget<E>` | Set `LOOK_TARGET` to nearest visible player. | Writes `LOOK_TARGET` |
| `SetRandomLookTarget<E>` | Set `LOOK_TARGET` to a random direction. | Writes `LOOK_TARGET` |

#### Miscellaneous Behaviours (`api.core.behaviour.custom.misc.*`)

| Behaviour | Description |
|---|---|
| `Idle<E>` | Do nothing for a configurable duration. Use `.runFor()` to set duration. |
| `Panic<E>` | Flee in a random direction (like `PanicGoal`). |
| `AvoidSun<E>` | Move to shade if exposed to sky. |
| `BlockWithShield<E>` | Raise shield to block incoming attacks. |
| `BreakBlock<E>` | Break a block at a target position. |
| `BreedWithPartner<E>` | Breed with a nearby partner of the same type. |
| `HoldItem<E>` | Pick up and hold an item. |
| `InvalidateMemory<E>` | Clear a specific memory based on a condition. |
| `ReactToUnreachableTarget<E>` | React when the attack target can't be pathed to. |
| `CustomBehaviour<E>` | Run a custom `Consumer<E>` — instant one-tick behaviour. |
| `CustomDelayedBehaviour<E>` | Run a custom action after a delay. |
| `CustomHeldBehaviour<E>` | Run a custom action that persists over multiple ticks. |

### The "Custom" Behaviours — Quick One-Offs

For simple actions that don't warrant a full class:

```java
// Instant one-tick action
new CustomBehaviour<>(entity -> {
    entity.heal(2.0f);
    entity.level().playSound(null, entity.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL);
})

// Delayed action (runs after N ticks)
new CustomDelayedBehaviour<MyMob>(entity -> {
    entity.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
}).delayFor(entity -> 60) // 3-second delay

// Held action (runs every tick for a duration)
new CustomHeldBehaviour<MyMob>(entity -> {
    // Called every tick
    entity.getNavigation().moveTo(somePos.getX(), somePos.getY(), somePos.getZ(), 1.0);
}).runFor(entity -> 100) // 5 seconds
```

---

## 6. Group Behaviours

Group behaviours wrap multiple child behaviours and control which ones run. They are critical for structuring AI.

### FirstApplicableBehaviour

Tries each child in order. The **first** child whose conditions are met runs. Others are skipped.

```java
new FirstApplicableBehaviour<MyMob>(
    new GuardFleeWhenLow(),   // Checked first — highest priority
    new GuardMeleeAttack()    // Only runs if flee didn't start
)
```

**Use for**: Priority-ordered behaviour selection. "If low health, flee; else, attack."

### OneRandomBehaviour

Picks **one random** child to run each time.

```java
new OneRandomBehaviour<>(
    new SetPlayerLookTarget<>(),    // 33% chance
    new SetRandomLookTarget<>(),    // 33% chance
    new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))  // 33% chance
)
```

**Use for**: Idle variety. Makes the entity feel less robotic.

### AllApplicableBehaviours

Runs **all** children whose conditions are met simultaneously.

```java
new AllApplicableBehaviours<>(
    new LookAtAttackTarget<>(),
    new SetWalkTargetToAttackTarget<>()
)
```

**Use for**: Parallel actions. "Look at target AND walk toward target at the same time."

### SequentialBehaviour

Runs children **one after another** in sequence. The next child starts after the previous one finishes.

```java
new SequentialBehaviour<>(
    new CustomDelayedBehaviour<>(entity -> entity.setAggressive(true)).delayFor(e -> 20),
    new AnimatableMeleeAttack<>(0).runFor(e -> 40),
    new CustomBehaviour<>(entity -> entity.setAggressive(false))
)
```

**Use for**: Multi-step sequences. "Wind up → Attack → Cool down."

### GroupBehaviour

Abstract base for custom group behaviours. Extend this to create your own grouping logic.

### RepeatingBehaviour

Wraps a single behaviour and repeats it N times or until a condition is met.

---

## 7. Activity Groups

Activities are the top-level containers that determine which behaviours are active. SBL provides 3 built-in activity groups.

### BrainActivityGroup.coreTasks(behaviours...)

**Always active.** Core tasks run regardless of which activity the brain is in. Typical core tasks:
- `LookAtTarget<>()` — face the look target
- `MoveToWalkTarget<>()` — walk toward the walk target
- `FloatToSurfaceOfFluid<>()` — swim when in water

```java
@Override
public BrainActivityGroup<? extends MyMob> getCoreTasks() {
    return BrainActivityGroup.coreTasks(
        new LookAtTarget<>(),
        new MoveToWalkTarget<>()
    );
}
```

### BrainActivityGroup.idleTasks(behaviours...)

**Default activity.** Runs when no higher-priority activity is active (i.e., `ATTACK_TARGET` is absent). Typical idle tasks:
- Targeting (sets `ATTACK_TARGET` which triggers fight)
- Random walking
- Looking at players/randomly
- Doing nothing (Idle)

```java
@Override
public BrainActivityGroup<? extends MyMob> getIdleTasks() {
    return BrainActivityGroup.idleTasks(
        new FirstApplicableBehaviour<MyMob>(
            new TargetOrRetaliate<>(),
            new SetPlayerLookTarget<>(),
            new SetRandomLookTarget<>()
        ),
        new OneRandomBehaviour<>(
            new SetRandomWalkTarget<>(),
            new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
        )
    );
}
```

### BrainActivityGroup.fightTasks(behaviours...)

**Activates automatically when `ATTACK_TARGET` memory is present.** When `ATTACK_TARGET` is erased, the brain falls back to idle.

```java
@Override
public BrainActivityGroup<? extends MyMob> getFightTasks() {
    return BrainActivityGroup.fightTasks(
        new InvalidateAttackTarget<>(),         // Clear dead/invalid targets
        new SetWalkTargetToAttackTarget<>(),     // Walk toward target
        new AnimatableMeleeAttack<>(0)           // Hit when in range
    );
}
```

### Activity Transition Rules

```
IDLE ──[ATTACK_TARGET set]──▶ FIGHT
FIGHT ─[ATTACK_TARGET erased]─▶ IDLE
CORE always runs in parallel with IDLE or FIGHT
```

The transition is **automatic**. You don't need to manually switch activities — just set/erase `ATTACK_TARGET`.

### Custom Activities

For advanced use cases (schedules, custom phases), use `handleAdditionalBrainSetup`:

```java
@Override
public void handleAdditionalBrainSetup(SmartBrain<MyMob> brain) {
    // Add a custom activity with its own memory requirements
    brain.activityRequirements.put(MY_CUSTOM_ACTIVITY, ImmutableSet.of(
        Pair.of(MY_CUSTOM_MEMORY, MemoryStatus.VALUE_PRESENT)
    ));
}
```

---

## 8. Utility Classes

### BrainUtils (`net.tslat.smartbrainlib.util.BrainUtils`)

The primary utility for memory manipulation. See [Section 4](#brainutils-helper-methods) for full API.

Key methods:
- `setMemory()`, `getMemory()`, `hasMemory()`, `clearMemory()`
- `setTargetOfEntity()`, `clearTargetOfEntity()`
- `getTargetOfEntity()`

### EntityRetrievalUtil (`net.tslat.smartbrainlib.util.EntityRetrievalUtil`)

Optimized entity scanning utilities:

```java
// Find nearest entity matching a predicate
LivingEntity nearest = EntityRetrievalUtil.getNearestEntity(
    entity, 16.0,   // search radius
    e -> e instanceof Monster && e.isAlive()
);

// Get all entities in range matching predicate
List<LivingEntity> targets = EntityRetrievalUtil.getEntities(
    entity, 16.0,
    e -> e instanceof Monster
);
```

### SensoryUtils (`net.tslat.smartbrainlib.util.SensoryUtils`)

Sensing and visibility helpers:

```java
// Check if entity can see target (line of sight)
boolean canSee = SensoryUtils.hasLineOfSight(entity, target);

// Check if entity is within melee range
boolean inRange = SensoryUtils.isInMeleeAttackRange(entity, target);
```

### RandomUtil (`net.tslat.smartbrainlib.util.RandomUtil`)

Random number utilities for AI:

```java
// Random int in range
int value = RandomUtil.randomNumberBetween(entity.getRandom(), 10, 30);

// Percentage chance check
boolean success = RandomUtil.percentChance(entity.getRandom(), 25); // 25% chance
```

### Object Helpers

| Class | Purpose |
|---|---|
| `SquareRadius` | Defines a square search radius (horizontal, vertical) |
| `MemoryTest` | Predefined memory presence/absence checks |
| `DynamicPositionTracker` | A `PositionTracker` that follows an entity |
| `FreePositionTracker` | A `PositionTracker` for a fixed position |
| `ExtendedTargetingConditions` | Enhanced `TargetingConditions` with extra predicates |

---

## 9. Registration (NeoForge)

### Registering Custom SensorTypes

```java
public class MySensorTypes {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPES =
            DeferredRegister.create(Registries.SENSOR_TYPE, "mymod");

    public static final DeferredHolder<SensorType<?>, SensorType<InLavaSensor<?>>> IN_LAVA =
            SENSOR_TYPES.register("in_lava", () -> new SensorType<>(InLavaSensor::new));
}
```

> See the [SBLSensors registry](https://github.com/Tslat/SmartBrainLib/blob/1.19.3/Common/src/main/java/net/tslat/smartbrainlib/registry/SBLSensors.java) for more examples.

### Registering Custom MemoryModuleTypes

See [Section 4 — Registering Custom Memories](#registering-custom-memories-neoforge).

### Wiring Into Mod Constructor

```java
@Mod(MODID)
public class MyMod {
    public MyMod(IEventBus modEventBus) {
        MySensorTypes.SENSOR_TYPES.register(modEventBus);
        MyMemoryTypes.MEMORY_TYPES.register(modEventBus);
        // ... other registries
    }
}
```

### Registering Entity Attributes

Custom brain entities still need attributes registered normally:

```java
@SubscribeEvent
private void registerEntityAttributes(EntityAttributeCreationEvent event) {
    event.put(MyEntities.MY_MOB.get(), MyMob.createAttributes().build());
}
```

### Installation (build.gradle)

```groovy
repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = 'SmartBrainLib'
                url = 'https://dl.cloudsmith.io/public/tslat/sbl/maven/'
            }
        }
        filter {
            includeGroup('net.tslat.smartbrainlib')
        }
    }
}

dependencies {
    implementation "net.tslat.smartbrainlib:SmartBrainLib-neoforge-${minecraft_version}:${sbl_version}"
}
```

In `gradle.properties`:
```properties
sbl_version=1.16.11
```

---

## 10. Complete Examples

### Example 1: Basic Hostile Mob (from SBL wiki)

The standard SBL entity pattern. Walks around, targets players/retaliates, fights in melee:

```java
public class MyMob extends Monster implements SmartBrainOwner<MyMob> {

    public MyMob(EntityType<? extends MyMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends MyMob>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(), // Tracks nearby entities
                new HurtBySensor<>()              // Tracks who hurt us
        );
    }

    @Override
    public BrainActivityGroup<? extends MyMob> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends MyMob> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<MyMob>(
                        new TargetOrRetaliate<>(),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()
                ),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<>(),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
                )
        );
    }

    @Override
    public BrainActivityGroup<? extends MyMob> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>(),
                new AnimatableMeleeAttack<>(0)
        );
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }
}
```

**AI Flow:**
1. `NearbyLivingEntitySensor` scans every ~20 ticks → stores all nearby entities
2. `HurtBySensor` tracks any damage → stores `HURT_BY` and `HURT_BY_ENTITY`
3. `TargetOrRetaliate` (idle) checks for nearby hostile or attacker → sets `ATTACK_TARGET`
4. Brain auto-transitions IDLE → FIGHT (because `ATTACK_TARGET` is now present)
5. `InvalidateAttackTarget` checks target is still valid each tick
6. `SetWalkTargetToAttackTarget` drives pathfinding toward the target
7. `AnimatableMeleeAttack` swings when in range
8. When target dies → `InvalidateAttackTarget` clears `ATTACK_TARGET` → brain returns to IDLE

### Example 2: Custom Behaviour (from SBL wiki)

A behaviour that picks a random nearby entity as the attack target, using configurable predicates (official SBL wiki example):

```java
public class SetRandomAttackTarget<E extends LivingEntity> extends ExtendedBehaviour<E> {

    private static final List<Pair<MemoryModuleType<?>, MemoryStatus>> MEMORY_REQUIREMENTS =
            ObjectArrayList.of(
                    Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT),
                    Pair.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT)
            );

    // Configurable predicates — SBL design philosophy: use stored variables, not hardcoded logic
    protected Predicate<LivingEntity> targetPredicate = entity -> true;
    protected Predicate<E> canTargetPredicate = entity -> true;

    // Fluent setters for dynamic configuration
    public SetRandomAttackTarget<E> targetPredicate(Predicate<LivingEntity> predicate) {
        this.targetPredicate = predicate;
        return this;
    }

    public SetRandomAttackTarget<E> canTargetPredicate(Predicate<E> predicate) {
        this.canTargetPredicate = predicate;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        return this.canTargetPredicate.test(entity);
    }

    @Override
    protected void start(ServerLevel level, E entity, long gameTime) {
        NearestVisibleLivingEntities nearbyEntities =
                BrainUtils.getMemory(entity, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);

        LivingEntity target = nearbyEntities.findClosest(this.targetPredicate).orElse(null);

        if (target == null) {
            BrainUtils.clearMemory(entity, MemoryModuleType.ATTACK_TARGET);
        } else {
            BrainUtils.setMemory(entity, MemoryModuleType.ATTACK_TARGET, target);
            BrainUtils.clearMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        }
    }
}
```

Usage in entity:
```java
new SetRandomAttackTarget<MyMob>()
        .targetPredicate(target -> target instanceof Player)
        .canTargetPredicate(entity -> entity.getHealth() > entity.getMaxHealth() * 0.5f)
```

**Key design points** (from SBL wiki):
1. A behaviour fills **one function only**.
2. A behaviour only uses **existing memory data**, not world scanning (that's the sensor's job).
3. Configurable options use **stored variables with fluent setters**, not hardcoded values.

### Example 3: Ranged Skeleton (SBLSkeleton pattern)

The canonical SBL example — a skeleton that targets players, strafes, and uses ranged or melee depending on held item. Based on the official [SBLSkeleton](https://github.com/Tslat/SmartBrainLib/blob/1.19.3/Common/src/main/java/net/tslat/smartbrainlib/example/SBLSkeleton.java):

```java
public class SmartSkeleton extends AbstractSkeleton implements SmartBrainOwner<SmartSkeleton> {

    public SmartSkeleton(EntityType<? extends SmartSkeleton> type, Level level) {
        super(type, level);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends SmartSkeleton>> getSensors() {
        return ObjectArrayList.of(
                new NearbyLivingEntitySensor<>(),
                new HurtBySensor<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends SmartSkeleton> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new MoveToWalkTarget<>(),
                new FloatToSurfaceOfFluid<>()  // Skeletons can get stuck in water
        );
    }

    @Override
    public BrainActivityGroup<? extends SmartSkeleton> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<SmartSkeleton>(
                        new TargetOrRetaliate<>(),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()
                ),
                new OneRandomBehaviour<>(
                        new SetRandomWalkTarget<>().speedModifier(0.8f),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
                )
        );
    }

    @Override
    public BrainActivityGroup<? extends SmartSkeleton> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new SetWalkTargetToAttackTarget<>().speedModifier(1.1f),
                new StrafeTarget<>()               // Circle-strafe around the target
                        .speedModifier(0.8f)
                        .stopStrafingWhen(entity -> !entity.isHolding(Items.BOW)),
                new FirstApplicableBehaviour<SmartSkeleton>(
                        new AnimatableRangedAttack<>(20)   // Bow attack with 20-tick windup
                                .startCondition(entity -> entity.isHolding(Items.BOW)),
                        new AnimatableMeleeAttack<>(0)     // Fallback to melee
                )
        );
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }
}
```

### Example 4: Boss Entity with Phases (AoA pattern)

A boss that switches between melee and ranged phases based on health, using `FirstApplicableBehaviour` for phase selection. Inspired by [AoA EliteSmash](https://github.com/Tslat/Advent-Of-Ascension/blob/1.19/source/content/entity/boss/smash/EliteSmashEntity.java):

```java
@Override
public BrainActivityGroup<? extends MyBoss> getFightTasks() {
    return BrainActivityGroup.fightTasks(
            new InvalidateAttackTarget<>(),
            new FirstApplicableBehaviour<MyBoss>(
                    // Phase 2: Below 50% HP → ranged attack + strafe
                    new SequentialBehaviour<>(
                            new StrafeTarget<MyBoss>().speedModifier(0.9f),
                            new AnimatableRangedAttack<MyBoss>(30)
                    ).startCondition(boss -> boss.getHealth() < boss.getMaxHealth() * 0.5f),

                    // Phase 1: Above 50% HP → charge + melee
                    new SequentialBehaviour<>(
                            new SetWalkTargetToAttackTarget<MyBoss>().speedModifier(1.5f),
                            new LeapAtTarget<MyBoss>(),
                            new AnimatableMeleeAttack<MyBoss>(10)
                    )
            )
    );
}
```

### Example 5: Passive Mob (Non-Combat)

A peaceful mob that wanders, follows temptation, breeds, and panics when hurt:

```java
@Override
public List<? extends ExtendedSensor<? extends MyAnimal>> getSensors() {
    return ObjectArrayList.of(
            new NearbyLivingEntitySensor<>(),
            new NearbyPlayersSensor<>(),
            new HurtBySensor<>(),
            new ItemTemptingSensor<MyAnimal>()
    );
}

@Override
public BrainActivityGroup<? extends MyAnimal> getCoreTasks() {
    return BrainActivityGroup.coreTasks(
            new LookAtTarget<>(),
            new MoveToWalkTarget<>(),
            new FloatToSurfaceOfFluid<>(),
            new Panic<>()  // Run when hurt (core so it always takes effect)
    );
}

@Override
public BrainActivityGroup<? extends MyAnimal> getIdleTasks() {
    return BrainActivityGroup.idleTasks(
            new FirstApplicableBehaviour<MyAnimal>(
                    new FollowTemptation<>(),       // Follow player with wheat
                    new BreedWithPartner<>(),        // Breed if in love mode
                    new FollowParent<>(),            // Babies follow adults
                    new SetPlayerLookTarget<>(),
                    new SetRandomLookTarget<>()
            ),
            new OneRandomBehaviour<>(
                    new SetRandomWalkTarget<>().speedModifier(0.6f),
                    new Idle<>().runFor(entity -> entity.getRandom().nextInt(40, 80))
            )
    );
}

// No getFightTasks() override needed — passive mob doesn't fight
```

---

## 11. Common Pitfalls & Best Practices

### Pitfalls to Avoid

| Pitfall | Why It's Bad | Fix |
|---|---|---|
| Forgetting `tickBrain(this)` in `customServerAiStep()` | Brain never runs — entity stands still | Always call `tickBrain(this)` |
| Missing `memoriesUsed()` on custom sensor | SBL can't auto-register the memory → crash | Return all memories the sensor writes to |
| Missing `type()` on custom sensor | Registration fails | Register a `SensorType` and return it |
| Using `MemoryStatus.VALUE_PRESENT` for memories set by the same activity | The memory might not be set yet | Use `REGISTERED` if the memory is optional |
| Storing entity references in behaviour fields across ticks | Entity might be removed/invalid between ticks | Re-fetch from memory each tick |
| Running expensive logic every tick in `tick()` | Unnecessary CPU load | Use counters like `if (gameTime % 20 == 0)` |
| Forgetting to erase `ATTACK_TARGET` when target dies | Entity stuck in FIGHT activity forever | Erase in `stop()` or use `InvalidateAttackTarget` |
| Not specifying generic type on group behaviours | Java compiler errors: `FirstApplicableBehaviour<>()` | Write `new FirstApplicableBehaviour<MyMob>(...)` |
| Setting `WALK_TARGET` every tick | Restarts pathfinding every tick, entity stutters | Only set when target position changes significantly |
| Overlapping memory writes from multiple sensors | Last writer wins, data is inconsistent | One sensor per memory, or coordinate carefully |

### Best Practices

1. **Keep behaviours single-purpose.** One behaviour = one action. Don't make a "FindTargetAndWalkAndAttack" behaviour. Make three: `FindTarget`, `WalkToTarget`, `Attack`.

2. **Use `runFor()` on all non-instant behaviours.** Prevents stuck behaviours from running forever.

3. **Use `FirstApplicableBehaviour` for priority.** Put higher-priority behaviours first. The first one whose conditions pass wins.

4. **Use `OneRandomBehaviour` for idle variety.** Prevents the entity from doing the same thing every tick.

5. **Sensors should be cheap.** They run on a timer but still scan entities. Use `setScanRate()` to control frequency. 10-40 ticks is typical.

6. **Don't re-scan in behaviours.** Behaviours should read from memories, not scan the world directly. That's the sensor's job.

7. **Use `BrainUtils` instead of raw brain access.** `BrainUtils.setMemory()` is safer than `brain.setMemory()` — it handles null checks and generics.

8. **Clean up in `stop()`.** Erase memories that shouldn't persist after the behaviour ends.

9. **Test with GameTests.** Spawn entity, set memories manually, assert behaviour outcomes.

10. **Order matters in `getSensors()`.** Sensors tick in the order they're listed. If sensor B depends on data from sensor A, put A first.

### Performance Tips

- **Sensor scan rates**: Set to 10-40 ticks depending on urgency. Combat sensors can be 10 ticks; village state sensors can be 40+.
- **Don't use `getEntitiesOfClass()` in behaviours.** That's the sensor's job. Behaviours should only read memories.
- **Pre-allocate memory requirement lists.** Use `private static final List<Pair<...>>` — don't create new lists per tick.
- **Use `ObjectArrayList`** (from fastutil) instead of `ArrayList` for sensor/behaviour lists. SBL uses this internally.

---

## Navigation Helpers

SBL also provides smooth navigation classes for entities that need better pathfinding:

| Class | Purpose |
|---|---|
| `SmoothGroundNavigation` | Smoother ground navigation than vanilla |
| `SmoothFlyingPathNavigation` | Smooth flying navigation |
| `SmoothWaterBoundPathNavigation` | Smooth swimming navigation |
| `SmoothAmphibiousPathNavigation` | Land + water navigation |
| `SmoothWallClimberNavigation` | Wall climbing (like spiders) |
| `MultiFluidSmoothGroundNavigation` | Ground navigation through multiple fluid types |

### SmartBrainSchedule

For time-of-day scheduling (like villager daily routines):

```java
SmartBrainSchedule schedule = new SmartBrainSchedule();
// Configure time-based activity switching
```

---

## Quick Reference Card

### Minimum Viable Entity Checklist

- [ ] Entity class extends `PathfinderMob` (or `Monster`, `LivingEntity`)
- [ ] Entity implements `SmartBrainOwner<YourEntity>`
- [ ] `brainProvider()` returns `new SmartBrainProvider<>(this)`
- [ ] `customServerAiStep()` calls `tickBrain(this)`
- [ ] `getSensors()` returns at least one sensor
- [ ] `getCoreTasks()` returns `LookAtTarget` + `MoveToWalkTarget`
- [ ] `getIdleTasks()` returns at least one idle behaviour
- [ ] Entity attributes registered in `EntityAttributeCreationEvent`
- [ ] Custom sensor types registered via `DeferredRegister<SensorType<?>>`
- [ ] Custom memory types registered via `DeferredRegister<MemoryModuleType<?>>`
- [ ] All `DeferredRegister`s wired to mod event bus in mod constructor

### Import Cheat Sheet

```java
// Core SBL
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;

// Behaviours
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.AllApplicableBehaviours;
import net.tslat.smartbrainlib.api.core.behaviour.SequentialBehaviour;

// Built-in behaviours
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.*;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.*;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.*;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.*;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.*;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.*;

// Sensors
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.*;
import net.tslat.smartbrainlib.api.core.sensor.custom.*;

// Utilities
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import net.tslat.smartbrainlib.util.SensoryUtils;
import net.tslat.smartbrainlib.util.RandomUtil;

// Helpers
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
```
