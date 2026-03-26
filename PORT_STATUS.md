# Millénaire 2 — NeoForge Port Status Report

**Generated:** 2026-03-26  
**Source version:** Minecraft 1.12.2 Forge (original Millénaire)  
**Target version:** Minecraft 1.21.1 NeoForge 21.1.209  
**Java version:** 21  
**Mappings:** Parchment 2024.12.07 (MC 1.21.3)  
**CI status:** ✅ BUILD SUCCESSFUL — All 82 GameTests pass

---

## Build Environment

| Property | Value | Status |
|----------|-------|--------|
| NeoForge version | 21.1.209 | ✅ |
| Minecraft version | 1.21.1 | ✅ |
| Java toolchain | 21 | ✅ |
| Parchment mappings | 2024.12.07 / MC 1.21.3 | ✅ |
| Mod ID | `millenaire2` | ✅ |
| Mod loader declaration | `javafml` in neoforge.mods.toml | ✅ |
| Build system | NeoForge ModDev plugin 2.0.140 | ✅ |
| Source encoding | UTF-8 (enforced by Gradle) | ✅ |

---

## Section 1 — Completed

Systems successfully migrated to NeoForge 1.21.1 standards.

### 1.1 Mod Entry Point

- `Millenaire2.java` uses the modern `@Mod(Millenaire2.MODID)` annotation.
- Constructor signature follows the NeoForge 2.0 pattern:
  `Millenaire2(IEventBus modEventBus, ModContainer modContainer)`.
- Event bus routing is correct: mod-lifecycle events go to `modEventBus`;
  game events go to `NeoForge.EVENT_BUS`.

### 1.2 Registry System — DeferredRegister

All registrations use the modern `DeferredRegister` / `DeferredHolder` API.
No legacy `RegistryEvent` subscribers exist anywhere in the codebase.

| Registry | Class | Entries |
|----------|-------|---------|
| Blocks | `MillBlocks` | 200+ block variants across all cultures |
| Items | `MillItems` | 100+ items (coins, crops, food, tools, weapons) |
| Creative tab | `Millenaire2` | 1 tab with all mod items |
| Entity types | `MillEntities` | 8 entity types |
| Block entity types | `MillEntities` | 5 block entity types |
| Memory module types | `ModMemoryTypes` | 5 custom brain memory modules |
| Menu types | `MillMenuTypes` | 1 menu (FirePit) |

### 1.3 Entity System

All entities extend `PathfinderMob` (1.21.1 equivalent of 1.12.2 `EntityCreature`).
No legacy `EntityCreature` or `EntityLivingBase` references remain.

| Entity class | Base | Notes |
|---|---|---|
| `MillVillager` (abstract) | `PathfinderMob` | Main NPC base; synched data via `SynchedEntityData` |
| `MillVillager.GenericMale` | `MillVillager` | Male villager variant |
| `MillVillager.GenericSymmFemale` | `MillVillager` | Female; symmetric model |
| `MillVillager.GenericAsymmFemale` | `MillVillager` | Female; asymmetric model |
| `HumanoidNPC` | `PathfinderMob` | State-driven SmartBrainLib-based NPC |
| `EntityTargetedBlaze` | `Blaze` | Targeted combat mob |
| `EntityTargetedWitherSkeleton` | `WitherSkeleton` | Targeted combat mob |
| `EntityTargetedGhast` | `Ghast` | Targeted combat mob |
| `EntityWallDecoration` | `Entity` | Wall-mounted decoration entity |

Entity data sync uses `SynchedEntityData.defineId()` for first name, family name, gender,
culture, and goal key — replacing the 1.12.2 `IEntityAdditionalSpawnData` pattern.

### 1.4 Brain / AI Framework

`MillVillager` uses the modern vanilla `Brain` API.  Brain configuration lives in
`VillagerBrainConfig`, and goal selection/execution is delegated to `VillagerGoalController`.

`HumanoidNPC` implements the SmartBrainLib (SBL) architecture via a drop-in compatibility
layer in `entity/brain/smartbrain/`.

**Custom memory module types** (registered via `DeferredRegister` in `ModMemoryTypes`):

| Memory key | Java type | Purpose |
|---|---|---|
| `BASE_LOCATION` | `GlobalPos` | Home anchor for patrol / return-home |
| `MACRO_OBJECTIVE` | `String` | High-level task (e.g. `"gather_wood"`, `"defend"`) |
| `NEEDED_MATERIALS` | `List<String>` | Refresh each scan cycle by `InventoryStateSensor` |
| `NEEDS_HEALING` | `Boolean` | Health-priority flag |
| `LAST_KNOWN_DANGER` | `BlockPos` | Danger location for strategic retreat |

**Sensors:**

| Class | Scan rate | Function |
|---|---|---|
| `InventoryStateSensor` | 10 ticks | Updates `NEEDED_MATERIALS`; flags `NEEDS_HEALING` |
| `SelfPreservationSensor` | 1 tick | Detects incoming damage and nearby hostiles |

**Behaviors:**

| Class | Activity | Function |
|---|---|---|
| `ContextualToolSwapBehavior` | WORK | Selects the correct tool from inventory before acting |
| `ConsumeFoodBehavior` | SURVIVAL | Eats food when food level falls below threshold |
| `StrategicRetreatBehavior` | SURVIVAL | Moves away from danger when health is critical |
| `InventoryManagementBehavior` | LOGISTICS | Discards low-value items when inventory is full |
| `MillWorkBehaviour` | WORK | Goal execution dispatch (wires to `VillagerGoalController`) |
| `MillFightBehaviour` | FIGHT | Combat dispatch (structure present; logic pending) |
| `MillRestBehaviour` | REST | Sleep / relax dispatch (structure present; logic pending) |
| `MillIdleBehaviour` | IDLE | Wander / socialise fallback (structure present; logic pending) |

**48 goal classes** in the `goal/` package cover construction, farming, trading, fishing,
crafting, child rearing, defence, pilgrimage, and more.

### 1.5 Networking

Uses the modern NeoForge 1.21.1 payload-based system (`RegisterPayloadHandlersEvent` /
`PayloadRegistrar`).  No `SimpleChannel` or `SimpleImpl` legacy code exists.

- `MillNetworking.register()` is called on the mod event bus.
- Two generic payloads: `MillGenericC2SPayload` and `MillGenericS2CPayload`.
- Both use `StreamCodec.composite()` for serialization.
- Specialised packet handlers in `network/handlers/` subpackage (trade, quest, hire, military, etc.).
- Client-only receivers confined to `client/network/` to prevent sidedness crashes.

### 1.6 Block Entities

All block entities extend `BlockEntity` (not the removed 1.12.2 `TileEntity`).
`saveAdditional` / `loadAdditional` use `HolderLookup.Provider`, not the raw legacy
`NBTTagCompound`.

| Block entity | Base class | Notes |
|---|---|---|
| `MillFirePitBlockEntity` | `BaseContainerBlockEntity` | 3-slot smelting; `BlockEntityTicker` for server tick |
| `MillLockedChestBlockEntity` | `BaseContainerBlockEntity` | Access-controlled container |
| `MillImportTableBlockEntity` | `BaseContainerBlockEntity` | Trading table |
| `MillPanelBlockEntity` | `BlockEntity` | Display panel (no inventory) |
| `MillMockBannerBlockEntity` | `BlockEntity` | Culture banner display |

### 1.7 GUI / Menu System

The FirePit is fully ported to the modern NeoForge menu pipeline.

| Component | Class | Pattern |
|---|---|---|
| Menu | `FirePitMenu` | `AbstractContainerMenu` + `MenuType` via `IMenuTypeExtension` |
| Screen | `FirePitScreen` | `AbstractContainerScreen<FirePitMenu>` |
| Registration | `MillMenuTypes` | `DeferredRegister<MenuType<?>>` |
| Client binding | `Millenaire2` | `RegisterMenuScreensEvent` |

All text-based GUI screens (`GuiTrade`, `GuiLockedChest`, `GuiPujas`, and all `GuiText`
subclasses) extend the modern `Screen` class and use `GuiGraphics` for rendering.

### 1.8 Configuration

- NeoForge config integration in `Config.java` using `ModConfigSpec`.
- Millénaire-specific in-game parameters in `MillConfig.java` / `MillConfigValues.java`.
- `ContentDeployer` copies default data files to the config directory on first launch.

### 1.9 Culture and Village Data

- 7 cultures (Norman, Indian, Mayan, Japanese, Byzantine, Inuit, Seljuk) loaded from
  config directory on `ServerStartingEvent`.
- Village types, building plans, villager types, and biome mappings are data-driven
  (plain-text and PNG plan files).
- `MillWorldData` extends `SavedData` for proper per-world persistence.
- `VillagerRecord` and `VillagerSpawner` correctly handle gender/handedness selection.
- `BuildingResManager` and related classes manage resource requirements for construction.

### 1.10 Advancement System

`MillAdvancements` registers vanilla-style advancements with a custom
`AlwaysTrueCriterionInstance` for milestone unlocks.  `PlayerListeners` fires
advancement triggers on the game event bus.

### 1.11 Test Coverage

82 GameTests passing (CI green).  Test classes live in `org.dizzymii.millenaire2.gametest`:

| Test class | Coverage |
|---|---|
| `VillagerSpawnTests` | 18 tests — VillagerRecord creation, gender/handedness, VillagerSpawner |
| `VillagerBrainTests` | 23 tests — brain activity selection, sensor firing, VillagerDebugger |
| `HumanoidNpcTests` | 32 tests — SBL behaviors, sensors, survival/logistics priority |
| `MillGameTests` | 9 tests — smoke tests, block/item registration integrity |

---

## Section 2 — Broken / In Progress

Systems that have been partially ported but contain stub implementations,
missing logic, or incomplete integration.

### 2.1 HumanoidNPC Brain — CORE and IDLE Activities

**File:** `entity/HumanoidNPC.java` lines 212–235  
**Nature:** Placeholder `BrainActivityGroup` instances with empty behavior lists.

`getCoreActivities()` returns an empty group.  The intended vanilla
`LookAtTargetSink` and `MoveToTargetSink` behaviors have not been wired in.
`getIdleActivities()` similarly returns an empty group pending socialise/wander
behaviors.

**Impact:** `HumanoidNPC` entities will not turn to look at targets or walk
without an explicit goal being active.

### 2.2 Targeted Mob AI

**Files:**
- `entity/EntityTargetedBlaze.java:11`
- `entity/EntityTargetedWitherSkeleton.java:11`
- `entity/EntityTargetedGhast.java:11`

**Nature:** TODO stubs.  Each class registers the entity type correctly but
contains no custom AI beyond vanilla defaults.

> `TODO: Implement target-tracking AI in a later phase.`

**Impact:** Targeted mobs (used in Millénaire raids and village defense) will
behave as plain vanilla mobs.

### 2.3 EntityWallDecoration

**File:** `entity/EntityWallDecoration.java`  
**Nature:** Registered as a plain `Entity`.  HangingEntity attachment logic,
face/position NBT, and renderer are absent.  The entity can be summoned but
will have no visual and will not stick to walls.

### 2.4 Item Specializations

**File:** `item/MillItems.java`  
**Nature:** The following item categories are registered as plain `Item` instances
instead of their proper subclasses:

| Category | Stub note |
|---|---|
| Wall decorations | Stubbed as plain items |
| Clothes (robes, tunics) | Stubbed as plain items |
| Banners | Stubbed as plain items |
| Bows | Stubbed as plain `Item` — custom `BowItem` behavior deferred |
| Armor — all cultures | Stubbed as plain `Item` — `ArmorMaterial` registration deferred |

**Impact:** These items have no wearable, projectile, or equip behavior.

### 2.5 Trade, Locked Chest, and Puja GUI — No MenuType Registration

**Files:**
- `client/gui/GuiTrade.java` + `ui/ContainerTrade.java`
- `client/gui/GuiLockedChest.java` + `ui/ContainerLockedChest.java`
- `client/gui/GuiPujas.java` + `ui/ContainerPuja.java`

**Nature:** Container classes extend `AbstractContainerMenu` correctly, and screen
classes extend `Screen`, but none of these menus are registered in `MillMenuTypes`.
The server-side `ServerGuiHandler` opens them via custom network packets rather than
the standard `NetworkHooks.openScreen` / `IMenuFactory` path.

**Impact:** These GUIs work in the current packet-driven approach, but the pattern
is non-standard.  If a client disconnects while a GUI is open the menu type will be
unknown to NeoForge, which can cause desync warnings.

### 2.6 Block Entity Renderers — Stub Classes

**Files:**
- `client/render/TESRFirePit.java` — `render()` is an empty override.
- `client/render/TESRMockBanner.java` — class body is an empty comment block.
- `client/render/TESRPanel.java` — class body is an empty comment block.
- `client/render/TileEntityLockedChestRenderer.java` — class body is an empty comment block.
- `client/render/TileEntityMillBedRenderer.java` — class body is an empty comment block.

**Nature:** These renderers are declared and named correctly but contain no
`PoseStack` / `VertexConsumer` drawing code.

**Impact:** Fire pit shows no flame particles; banners, panels, locked chests, and
beds have no custom visual.

### 2.7 SmartBrainLib Compatibility Layer

**Package:** `entity/brain/smartbrain/`  
**Classes:** `SmartBrainOwner`, `BrainActivityGroup`, `ExtendedBehaviour`, `ExtendedSensor`

**Nature:** A four-class drop-in replacement for `net.tslat.smartbrainlib` because
`maven.tslat.net` is inaccessible in the build environment.  The layer mirrors the
SmartBrainLib public API but does not implement the full library.

**Impact:** Once `maven.tslat.net` is reachable the dependency can be added to
`build.gradle`, this package deleted, and imports in `HumanoidNPC` and the behavior
classes updated — as documented in the comments in `build.gradle`.

### 2.8 VillagerBrainConfig CORE Activity

**File:** `entity/brain/VillagerBrainConfig.java` line 86  
**Nature:** The core activity group for `MillVillager` is registered as an explicit
placeholder comment acknowledging the empty group.

---

## Section 3 — Untouched / Legacy

Systems that still rely on 1.12.2 architecture or have not been ported to 1.21.1
patterns.  These are functional in the current codebase but require a future rewrite
for full compliance.

### 3.1 World Generation — Not Using ConfiguredFeature / PlacedFeature / Jigsaw

**Package:** `world/`  
**Key classes:** `WorldGenVillage`, `WorldGenAppleTree`, `WorldGenCherry`,
`WorldGenOliveTree`, `WorldGenSakura`, `WorldGenPistachio`

**Current approach:** Village placement is triggered from server-tick chunk-exploration
hooks (`ServerStartingEvent` → chunk loaded event).  Tree generation classes call
`setBlock` directly rather than registering `ConfiguredFeature` instances.

**1.21.1 standard:** World features should be registered as `ConfiguredFeature` /
`PlacedFeature` data objects, then placed via biome modifiers
(`AddFeaturesBiomeModifier`) or jigsaw structures for village layouts.

**Why intentional:** The original Millénaire generation logic is highly customized
(distance constraints, culture-biome mapping, incremental hamlet expansion).  A
direct jigsaw port would lose this behavior.  The current approach is functional;
modernization is a separate long-term task.

### 3.2 ItemStack Data Storage — Raw NBT (Not Data Components)

**Affected classes:** All block entity `saveAdditional`/`loadAdditional` methods;
`MillVillager` NBT serialization; inventory serialization via `ContainerHelper`.

**Current approach:** `CompoundTag` fields written directly
(`tag.putInt("BurnTime", ...)`, `ContainerHelper.saveAllItems(...)`).

**1.21.1 standard:** Custom item data should use `DataComponentType` and
`DataComponentMap` for items.  Block entity persistence via `CompoundTag` is still
valid in 1.21.1 and is not deprecated.

**Impact for items:** Mod items that store custom per-stack data (e.g., coins with
denomination, quest books with progress) should eventually use Data Components to
benefit from automatic `equals`/`hashCode`, SNBT display, and recipe condition
support.

### 3.3 Armor and Clothing Rendering

**File:** `client/render/LayerVillagerClothes.java`

**Current state:** Class exists but contains no actual render calls.
`ArmorMaterial` entries for each culture's armor sets have not been registered.

**Required rewrite:** Define `ArmorMaterial` records; register them; implement
`HumanoidArmorLayer`-style rendering in `LayerVillagerClothes`; bind culture-
specific textures.

### 3.4 Custom A\* Pathfinding

**Package:** `pathing/`, `pathing/atomicstryker/`  
**Key classes:** `PathNavigateSimple`, `AStarPathPlannerJPS`, `AStarWorker`,
`AStarWorkerJPS`, `RegionMapper`

**Current state:** The A\* planner classes are present and compile, but
`PathNavigateSimple` is not connected to any entity's navigator.  Villagers
currently rely on vanilla `PathNavigation`.

**Required work:** Wire `PathNavigateSimple` into `MillVillager.createNavigation()`.
The JPS planner needs testing against 1.21.1's chunk access patterns.

### 3.5 Diplomacy Actions (Data Loaded, Logic Absent)

**File:** `village/DiplomacyManager.java`

**Current state:** Culture affinity data is loaded (10 entries at startup) but no
code acts on these values.  Alliance/rivalry modifiers, diplomatic trade bonuses,
and hostility mechanics are not implemented.

### 3.6 Gameplay Systems Not Ported

The following Millénaire 1.12.2 features have no counterpart in the current codebase:

| Feature | Status |
|---|---|
| Dynasty / family tree system | Not started |
| Prayer / religion / puja scheduling | Data structures present; execution absent |
| Village raids and warfare | Entities registered; AI absent |
| Travel Book rendering | `BookManager` classes present; `TravelBookExporter` produces data but no in-game render |
| Quest delivery and completion | Quest loading works; delivery/reward flow incomplete |
| Villager age progression | `GoalChildBecomeAdult` class exists; life-cycle timer absent |
| Import table logistics | Block entity and GUI exist; economic loop not wired |

---

## Summary

| Area | Status |
|---|---|
| Build environment | ✅ Completed |
| Mod entry point | ✅ Completed |
| Registry system | ✅ Completed |
| Networking | ✅ Completed |
| Block entities | ✅ Completed |
| FirePit menu pipeline | ✅ Completed |
| Entity hierarchy | ✅ Completed |
| Brain / AI framework | ✅ Completed (core sensors and behaviors) |
| 48 goal classes | ✅ Completed |
| Culture & village data | ✅ Completed |
| Advancement system | ✅ Completed |
| Configuration | ✅ Completed |
| GameTests (82) | ✅ Completed |
| HumanoidNPC CORE/IDLE brain | ⚠️ In Progress |
| Targeted mob AI | ⚠️ In Progress |
| EntityWallDecoration | ⚠️ In Progress |
| Item specializations | ⚠️ In Progress |
| Trade/Puja/LockedChest menus | ⚠️ In Progress |
| Block entity renderers | ⚠️ In Progress |
| SmartBrainLib compatibility layer | ⚠️ Temporary — remove when library accessible |
| World generation | 🟡 Legacy (functional, rewrite deferred) |
| ItemStack Data Components | 🟡 Legacy (NBT valid, upgrade deferred) |
| Armor/clothing rendering | 🟡 Legacy — requires `ArmorMaterial` registration |
| Custom A\* pathfinding | 🟡 Legacy — present but not wired to entities |
| Diplomacy system | 🟡 Legacy — data loaded, logic absent |
| Dynasty, raids, quests, age | 🔴 Not started |
