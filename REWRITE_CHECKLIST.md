# Millénaire 2 — Comprehensive Rewrite Checklist

**Target:** 1:1 feature parity with legacy behavior via ground-up, modern, high-quality 1.21.1 NeoForge architecture.
**MC Version:** 1.21.1 | **NeoForge:** 21.1.209 | **Mappings:** Parchment 2024.12.07

---

## Phase 0: Scaffolding & Cleanup

### 0.1 Dead Code Removal
- [ ] Delete `Config.java` (empty legacy shell, 9 lines)

### 0.2 Package Restructure
- [ ] Move block entities from `entity/blockentity/` → `block/entity/` (standard NeoForge layout)
  - `MillFirePitBlockEntity.java`
  - `MillImportTableBlockEntity.java`
  - `MillLockedChestBlockEntity.java`
  - `MillMockBannerBlockEntity.java`
  - `MillPanelBlockEntity.java`
- [ ] Create `init/` package for registry classes
- [ ] Move `MillEntities.java` → `init/ModEntityTypes.java` + `init/ModBlockEntityTypes.java` (split entity and block-entity registration)
- [ ] Move `MillItems.java` → `init/ModItems.java`
- [ ] Move `MillBlocks.java` → `init/ModBlocks.java`
- [ ] Move `MillMenuTypes.java` → `init/ModMenuTypes.java`
- [ ] Update all import references across the codebase

### 0.3 Registry Extraction from Main Class
- [ ] Extract `DeferredRegister` declarations from `Millenaire2.java` into their respective `init/` classes
  - Currently `BLOCKS`, `ITEMS`, `CREATIVE_MODE_TABS`, `ENTITY_TYPES`, `BLOCK_ENTITY_TYPES` are all in the main mod class
  - Each `init/` class owns its own `DeferredRegister` and calls `.register(modEventBus)` 
- [ ] Slim `Millenaire2.java` to only: mod constructor, `commonSetup`, `onServerStarting`, static accessors

---

## Phase 1: Foundation Hardening

### 1.1 Point → BlockPos Migration
- [ ] Audit all usages of `Point` (currently 127 lines, mutable public fields `x`, `y`, `z`)
- [ ] Either:
  - (A) Make `Point` immutable (private final fields, builder for mutations), OR
  - (B) Replace `Point` with `BlockPos` throughout and remove `Point.java`
- [ ] Replace `Point.writeToNBT`/`readFromNBT` with `NbtUtils.writeBlockPos`/`readBlockPos` (vanilla)
- [ ] Replace `Point.writeToBuf`/`readFromBuf` with `FriendlyByteBuf.writeBlockPos`/`readBlockPos`

### 1.2 Static World Data Access
- [ ] Replace `Millenaire2.worldData` static nullable field with proper `SavedData` access via `MillWorldData.get(ServerLevel)`
- [ ] Remove `Millenaire2.getWorldData()` — callers should use `MillWorldData.get(level)` directly
- [ ] Audit all call sites: `MillEventController`, `ServerTickHandler`, `MillVillager.die()`, `ServerPacketHandler` (20+ references to `Millenaire2.getWorldData()`)
- [ ] Ensure no `Level` references are stored in long-lived objects (currently `MillWorldData.world`, `Building.world` are problematic)

### 1.3 Configuration Cleanup
- [ ] Replace static mutable runtime fields in `MillConfig` with a proper accessor pattern
  - Current: `public static boolean generateVillages;` set in `onLoad()`
  - Target: `MillConfig.get().generateVillages()` or keep statics but mark final-ish via spec `.get()` calls
- [ ] Add missing config options observed in code but not in config (e.g., `constructionBlocksPerTick` is in config but some tick intervals are hardcoded)

### 1.4 Logging Overhaul
- [ ] Replace `MillLog` static methods with per-class `Logger` instances (standard SLF4J pattern)
  - Current: `MillLog.major(this, "message")` — `source` param is `Object` but ignored in output
  - Target: Each class gets `private static final Logger LOGGER = LogUtils.getLogger();`
- [ ] OR keep `MillLog` but make it useful: include source class name in output, respect config log levels
- [ ] Replace all `MillLog.minor()` calls with `LOGGER.debug()`, `MillLog.major()` with `LOGGER.info()`

### 1.5 NBT Key Constants
- [ ] Define `static final String` constants for all NBT keys in every class that does NBT serialization:
  - `Building.java` (~40 NBT keys as raw strings)
  - `VillagerRecord.java` (~25 NBT keys)
  - `UserProfile.java` (~20 NBT keys)
  - `ConstructionIP.java` (~10 NBT keys)
  - `BuildingResManager.java` (~6 NBT keys)
  - `MillVillager.java` (~15 NBT keys)
  - `BuildingProject.java` (~6 NBT keys)
  - `BuildingLocation.java`

---

## Phase 2: Data System Modernization

### 2.1 Replace ParametersManager Reflection System
- [ ] Audit `ParametersManager.java` (342 lines of reflection-based annotation-driven file parsing)
- [ ] Replace with `Codec`-based JSON deserialization for each data type
- [ ] Remove `ConfigAnnotations.java` and `@ConfigField` / `@FieldDocumentation` annotations
- [ ] Convert all `.txt` data files to `.json` format

### 2.2 Culture Data → JSON Codecs
- [ ] Rewrite `Culture.java` to load from JSON via `Codec<Culture>` instead of `VirtualDir` + custom parsing
- [ ] Rewrite `VillagerType.java` to use `Codec<VillagerType>` instead of `@ConfigField` annotations
  - Currently 217 lines with ~30 annotated fields parsed via reflection
- [ ] Convert culture data files from `.txt` → `.json` (norman, indian, mayan, japanese, byzantine, etc.)

### 2.3 Building Plans → JSON Codecs
- [ ] Rewrite `BuildingPlan.java` / `BuildingPlanSet.java` to use Codec-based loading
- [ ] Rewrite `BuildingCustomPlan.java` to use Codec
- [ ] Keep PNG plan loader (`PngPlanLoader.java`) but modernize its resource access

### 2.4 Economy/Trade Data → Codecs
- [ ] Rewrite `TradeGoodLoader.java` (158 lines): replace manual Gson parsing with `Codec`
- [ ] Rewrite `VillageEconomyLoader.java` (198 lines): replace manual Gson with `Codec`
- [ ] Rewrite `DiplomacyManager.loadFromServer()` (116 lines): replace manual Gson with `Codec`
- [ ] Rewrite `BiomeCultureMapper.java` (156 lines): replace manual Gson with `Codec`

### 2.5 Quest System → JSON
- [ ] Rewrite `Quest.java` to load from JSON instead of custom `.txt` parser
- [ ] Replace `QuestStep` manual parsing with `Codec<QuestStep>`
- [ ] Replace `QuestVillager` manual parsing with `Codec<QuestVillager>`
- [ ] Convert quest `.txt` files to `.json`

### 2.6 Content Deployment
- [ ] Evaluate removing `ContentDeployer.java` entirely — use proper data pack / resource pack loading
- [ ] All culture, quest, and config data should live under `data/millenaire2/` and be loaded via `ResourceManager`
- [ ] Remove filesystem-based loading (`new File(...)`, `BufferedReader`, etc.)

---

## Phase 3: Networking Overhaul

### 3.1 Dedicated Payload Records
- [ ] Replace `MillGenericS2CPayload` with dedicated payload `record` per packet type:
  - `VillagerSyncPayload` (replaces PACKET_VILLAGER_SYNC)
  - `VillagerSpeechPayload` (replaces PACKET_VILLAGER_SPEECH)
  - `VillageListPayload` (replaces PACKET_VILLAGELIST)
  - `PlayerProfilePayload` (replaces PACKET_PROFILE)
  - `OpenGuiPayload` (replaces PACKET_OPENGUI)
  - `TradeDataPayload` (replaces PACKET_TRADEDATA)
  - `BuildingSyncPayload` (replaces PACKET_BUILDING)
  - `MapInfoPayload` (replaces PACKET_MAPINFO)
  - `ChatTranslatedPayload` (replaces PACKET_CHATTRANSLATED)
  - `QuestPayload` (replaces PACKET_QUEST)
- [ ] Replace `MillGenericC2SPayload` with dedicated payload records:
  - `GuiActionPayload` (replaces PACKET_GUIACTION + subtypes)
  - `VillagerInteractPayload` (replaces PACKET_VILLAGERINTERACT_REQUEST)
  - `VillageListRequestPayload` (replaces PACKET_VILLAGELIST_REQUEST)
  - `MapInfoRequestPayload` (replaces PACKET_MAPINFO_REQUEST)
  - `DevCommandPayload` (replaces PACKET_DEVCOMMAND)
- [ ] Each payload gets its own `StreamCodec` using proper typed fields (no raw `byte[]`)

### 3.2 Handler Decomposition
- [ ] Split `ServerPacketHandler.java` (854 lines) into per-feature handlers:
  - `TradePacketHandler` (trade buy/sell)
  - `QuestPacketHandler` (quest accept/complete/refuse)
  - `HirePacketHandler` (hire/release/extend)
  - `BuildingProjectPacketHandler` (building projects, controlled buildings)
  - `MilitaryPacketHandler` (relations, raids)
  - `ImportTablePacketHandler` (import table actions)
  - `VillagerInteractPacketHandler`
- [ ] Split `ClientPacketHandler.java` (432 lines) similarly
- [ ] Delete `PacketDataHelper.java` (manual byte serialization) — replaced by StreamCodecs
- [ ] Delete `MillPacketIds.java` — replaced by payload type discrimination

### 3.3 Chat Formatting
- [ ] Replace all hardcoded `§6[Millénaire]§r` / `\u00a76` formatting with `Component.translatable()` keys
- [ ] Create translation keys in `en_us.json` for every user-facing message
- [ ] Audit: `ServerPacketHandler` (~30 instances), `MillCommands` (~20 instances), `MillVillager.mobInteract()`, `ServerPacketSender`

---

## Phase 4: Entity Rewrite

### 4.1 MillVillager Decomposition
- [ ] Extract AI logic from `MillVillager.java` (681 lines) into `entity/ai/VillagerAIController.java`
  - Move: `tickGoalSelection()`, `tickGoalExecution()`, `selectNewGoal()`, `tryGoal()`, `tryVtypeGoals()`, phase selection
- [ ] Extract inventory from inline `HashMap<InvItem, Integer>` into `entity/VillagerInventory.java`
  - Implement proper `Container` or `SimpleContainer` interface
- [ ] Extract combat logic into `entity/ai/VillagerCombatHandler.java`
- [ ] Extract lifecycle (stuck detection, day phase, dialogue) into `entity/VillagerLifecycle.java`
- [ ] Reduce `MillVillager.java` to: entity definition, data accessors, delegating `tick()`, NBT, rendering hooks

### 4.2 Encapsulation
- [ ] Make all public mutable fields on `MillVillager` private with getters/setters:
  - `vtype`, `goalKey`, `housePoint`, `townHallPoint`, `inventory`, `heldItem`, etc. (30+ fields)
- [ ] Same for `VillagerRecord` (20+ public mutable fields)
- [ ] Same for `Building` (50+ public mutable fields)

### 4.3 AI System Decision
- [ ] Evaluate migrating from custom goal-tick system to Brain/SmartBrainLib:
  - **Pro:** Standard, tested, memory-based, composable behaviors
  - **Con:** Large migration, all 40+ Goal classes need rewriting
  - **Decision point:** If staying with custom goals, at minimum wire into `registerGoals()` for vanilla compatibility
- [ ] If keeping custom system: clean up `Goal.java` static instances, use proper registry/enum instead
- [ ] Replace `HashMap<Goal, Long> lastGoalTime` with a proper cooldown manager

### 4.4 Entity Type Cleanup
- [ ] Evaluate if 3 separate entity types for male/symm-female/asymm-female is needed
  - Could be 1 entity type with a synched data field for body model variant
- [ ] Clean up empty subclasses (`GenericMale`, `GenericSymmFemale`, `GenericAsymmFemale`)
- [ ] Consider moving targeted entities (`EntityTargetedBlaze`, etc.) to use vanilla entities + custom AI via events

---

## Phase 5: Village System Rewrite

### 5.1 Building Decomposition
- [ ] Split `Building.java` (759 lines) into focused classes:
  - `Building.java` — core state, position, level, culture, NBT root
  - `BuildingVillagers.java` — villager records, spawning, management
  - `BuildingConstruction.java` — construction IP, upgrade logic, block placement
  - `BuildingTrade.java` — trade goods, merchant interaction
  - `BuildingDiplomacy.java` — relations, known villages, raid state
  - `BuildingTick.java` — tick dispatcher calling sub-systems
- [ ] Remove public `world` and `mw` fields — pass as method parameters or use accessor

### 5.2 ConstructionIP Modernization
- [ ] Replace manual NBT in `ConstructionIP.java` with Codec-based serialization
- [ ] Add protection checks for block placement (respawn protection, spawn protection, etc.)
- [ ] Improve two-pass construction with proper scheduling

### 5.3 Diplomacy System
- [ ] Move all static mutable state from `DiplomacyManager` into per-village data
- [ ] Replace `java.util.Random` with `RandomSource` (Minecraft standard)
- [ ] Decouple raid logic from Building — raids should be managed by a `RaidManager` per village

### 5.4 Economy System
- [ ] Integrate `VillageEconomyLoader` production/consumption into `Building` tick properly
- [ ] Resource capacity enforcement
- [ ] Clean separation between building-level and village-level economy

---

## Phase 6: World Data & Persistence

### 6.1 Thread Safety
- [ ] Replace `HashMap` in `MillWorldData` with `ConcurrentHashMap` for `buildings` and `villagerRecords`
- [ ] Or ensure all access is from server thread only (document and enforce)

### 6.2 Codec-Based Serialization
- [ ] Replace manual `save()`/`load()` NBT in `MillWorldData` with `Codec<MillWorldData>`
- [ ] Replace manual NBT in `Building` with `Codec<Building>`
- [ ] Replace manual NBT in `UserProfile` with `Codec<UserProfile>`
- [ ] Replace manual NBT in `VillagerRecord` with `Codec<VillagerRecord>`

### 6.3 Stale Data Cleanup
- [ ] Add periodic cleanup of dead villager records in `MillWorldData`
- [ ] Add building validation on load (remove buildings with invalid positions)
- [ ] Profile cleanup for players who haven't logged in for configurable duration

### 6.4 Efficient Ticking
- [ ] Replace blanket `for (Building b : buildings.values()) b.tick()` with spatial/activity-based tick scheduling
- [ ] Only tick buildings near players or with active construction/raids
- [ ] Use `ServerLevel.getChunkSource().hasChunk()` to skip unloaded buildings

---

## Phase 7: World Generation

### 7.1 Generation System
- [ ] Evaluate migrating village generation to Jigsaw structures (data-driven)
- [ ] If keeping procedural: clean up `WorldGenVillage.java` (385 lines)
  - Remove static `triedChunks` set — use `SavedData` or chunk capability
  - Replace hardcoded terrain evaluation with configurable parameters
- [ ] Move tree generators (`WorldGenAppleTree`, `WorldGenCherry`, etc.) to `ConfiguredFeature`/`PlacedFeature` JSON

### 7.2 Biome Integration
- [ ] `BiomeCultureMapper` is decent — upgrade JSON parsing to Codec
- [ ] Consider using NeoForge `BiomeModifiers` for feature injection

---

## Phase 8: Client & UI

### 8.1 Client-Side Caching
- [ ] Replace static `HashMap` caches in `ClientPacketHandler` with lifecycle-managed caches
- [ ] Clear caches on disconnect/dimension change
- [ ] Use `ClientLevel` events for cache invalidation

### 8.2 Screen Implementations
- [ ] Audit all GUI types referenced in `MillPacketIds.GUI_*` constants
- [ ] Implement missing screens (currently only `FirePitScreen` exists)
  - Trade screen
  - Village info screen
  - Chief/control screen
  - Quest screen
  - Hire screen
  - Building project screen
  - Map/minimap screen
  - Import table screen

### 8.3 Rendering
- [ ] Verify `ClientSetup.java` renderer registrations are complete
- [ ] Add villager name rendering above heads (config-gated, `MillConfig.displayVillagerNames`)
- [ ] Add construction progress rendering (particles, block highlights)

### 8.4 Localization
- [ ] Create comprehensive `en_us.json` with all translation keys
- [ ] Replace every `Component.literal()` with hardcoded English → `Component.translatable()`
- [ ] Add language file structure for mod-supported languages

---

## Phase 9: Commands & Events

### 9.1 Command Modernization
- [ ] Replace all `Component.literal("§6[Millénaire]§r ...")` with `Component.translatable()`
- [ ] Add proper argument types where applicable (culture names, village names)
- [ ] Add tab-completion for village names, culture names
- [ ] Proper permission levels for each command

### 9.2 Event Handlers
- [ ] Review `MillEventController.java` — clean up `findOwnerBuilding()` (O(n) building scan)
  - Consider spatial index or building position caching
- [ ] Review `ServerTickHandler.java`:
  - `attemptVillageGenerationNearPlayers()` does `getEntitiesOfClass()` with large radius — optimize
  - Chunk loading update — ensure proper ticket management

---

## Phase 10: Polish & Quality

### 10.1 Data Generation
- [ ] Create datagen providers for:
  - Block states and models (`BlockStateProvider`)
  - Item models (`ItemModelProvider`)
  - Loot tables (`LootTableProvider`)
  - Tags (`TagsProvider`)
  - Recipes (`RecipeProvider`)
  - Advancements

### 10.2 Testing
- [ ] Add GameTests for critical paths:
  - Village generation round-trip
  - Building construction progress
  - Trade flow
  - Quest acceptance/completion
  - Villager lifecycle (spawn, goal, death)
  - NBT persistence round-trips
  - Network payload encode/decode
- [ ] Test on dedicated server for sided safety

### 10.3 Performance Audit
- [ ] Profile tick performance with 10+ villages loaded
- [ ] Identify and fix any per-tick allocations
- [ ] Review entity scan patterns (`getEntitiesOfClass` calls)
- [ ] Memory leak audit: check for retained Level/Entity references

### 10.4 Compatibility
- [ ] Test with common mods (JEI, Jade/WAILA, Minimap mods)
- [ ] Ensure no mixin conflicts
- [ ] Verify resource pack compatibility

---

## File-by-File Issue Index

| File | Lines | Key Issues |
|------|-------|-----------|
| `Millenaire2.java` | 153 | Static worldData, registries in main class, heavy onServerStarting |
| `Config.java` | 9 | Dead code — delete |
| `MillConfig.java` | 126 | Static mutable runtime fields |
| `MillVillager.java` | 681 | Monolith, 30+ public mutable fields, custom AI bypass, no encapsulation |
| `Building.java` | 759 | Monolith, 50+ public mutable fields, raw Level/WorldData refs |
| `MillWorldData.java` | 250 | HashMap not thread-safe, Level ref stored, no stale cleanup |
| `ServerPacketHandler.java` | 854 | Massive switch dispatch, raw byte deserialization |
| `ClientPacketHandler.java` | 432 | Massive switch dispatch, static mutable caches |
| `ServerPacketSender.java` | 237 | Manual byte serialization, empty reputation/language writes |
| `WorldGenVillage.java` | 385 | Static triedChunks, hardcoded terrain params |
| `Culture.java` | 305 | Filesystem loading, custom parser |
| `VillagerType.java` | 217 | Reflection-based annotation loading |
| `VillagerRecord.java` | 215 | nanoTime-based IDs, Cloneable, manual NBT |
| `UserProfile.java` | 269 | Manual NBT/network serialization |
| `ConstructionIP.java` | 234 | Manual NBT, no protection checks |
| `DiplomacyManager.java` | 350 | All static mutable state, java.util.Random |
| `VillageEconomyLoader.java` | 198 | Manual Gson parsing |
| `TradeGoodLoader.java` | 158 | Manual Gson parsing |
| `BiomeCultureMapper.java` | 156 | Manual Gson parsing |
| `Quest.java` | 220 | File-based .txt loading |
| `MillCommands.java` | 315 | Hardcoded English, no tab-completion |
| `MillEventController.java` | 158 | O(n) building scan |
| `ServerTickHandler.java` | 77 | Large-radius entity scan |
| `Goal.java` | 185 | Static mutable instances |
| `Point.java` | 127 | Mutable, reinvents BlockPos |
| `MillLog.java` | 50 | Source param unused, no log levels |
| `ParametersManager.java` | 342 | Reflection-based parsing — replace with Codecs |
| `ContentDeployer.java` | 125 | JAR extraction — non-standard |
| `MillNetworking.java` | 45 | Generic payload registration |
| `MillEntities.java` | 100 | Mixed entity + block entity registration |
| `MillMenuTypes.java` | 28 | Only FirePit registered |
| `PacketDataHelper.java` | ~200 | Manual byte array serialization |
| `MillPacketIds.java` | ~100 | Magic number constants |
| `BuildingResManager.java` | 122 | ConcurrentHashMap fine, basic functionality |
| `BuildingProject.java` | 94 | Reasonably clean, needs Codec |

---

## Recommended Execution Order

**Sprint 1:** Phase 0 (scaffolding) + Phase 1.1-1.2 (Point, static access)
**Sprint 2:** Phase 1.3-1.5 (config, logging, NBT keys)
**Sprint 3:** Phase 3.1-3.2 (networking payloads + handler decomposition)
**Sprint 4:** Phase 4.1-4.2 (entity decomposition + encapsulation)
**Sprint 5:** Phase 5.1-5.2 (building decomposition + construction)
**Sprint 6:** Phase 2.1-2.3 (data system: ParametersManager → Codecs, Culture/VillagerType)
**Sprint 7:** Phase 2.4-2.6 (economy/trade/quest data + content deployment)
**Sprint 8:** Phase 6 (world data, persistence, efficient ticking)
**Sprint 9:** Phase 5.3-5.4 + Phase 7 (diplomacy, economy, worldgen)
**Sprint 10:** Phase 8 + Phase 9 (client/UI, commands, events)
**Sprint 11:** Phase 10 (datagen, testing, performance, compatibility)
