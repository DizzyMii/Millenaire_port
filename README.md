# Millénaire 2 — NeoForge Port

A community-driven (Currently only me :D Contributions are welcome) port of the classic [Millénaire](https://millenaire.org/) Minecraft mod from **1.12.2 Forge** to **1.21.1 NeoForge**.

## About

Millénaire is a Minecraft mod that adds NPC villages inspired by real-world historical cultures — Norman, Indian, Mayan, Japanese, Byzantine, Inuit, Seljuk, and more. Villagers build, trade, farm, and expand their settlements over time, creating a living world around the player.

The mod had been stuck on an outdated, archaic version for many years, unable to evolve with the game's updates. It played a significant role in shaping my early Minecraft experiences, serving as a cornerstone of my gameplay. Driven by nostalgia and a desire to revitalize this cherished part of my gaming history, I aimed to breathe new life into it and adapt it for modern times.
## Supporters

### Usernames
Prechecked


---
If you want to support me and keep this preservation rolling, consider supporting me on Ko-Fi!
https://ko-fi.com/dizzymii

## Remaining Port Features

> Full details for each item are in [PORT_STATUS.md](PORT_STATUS.md).

**In Progress / Broken**
- HumanoidNPC CORE and IDLE brain activities (LookAtTargetSink / MoveToTargetSink / wander not wired)
- Targeted mob AI — EntityTargetedBlaze, EntityTargetedWitherSkeleton, EntityTargetedGhast (vanilla behavior only)
- EntityWallDecoration — wall-attachment logic, face/position NBT, and renderer absent
- Item specializations — wall decorations, clothes, bows, and armor registered as plain Items instead of proper subclasses
- Trade, LockedChest, and Puja menus not registered in MillMenuTypes (non-standard packet-driven path)
- Block entity renderers — FirePit, MockBanner, Panel, LockedChest, and MillBed renderers are empty stubs
- SmartBrainLib compatibility layer — temporary shim; replace with real library when maven.tslat.net is reachable
- VillagerBrainConfig CORE activity group is an acknowledged empty placeholder

**Legacy / Deferred**
- World generation — village placement and tree gen not ported to ConfiguredFeature / PlacedFeature / Jigsaw
- ItemStack data storage — raw NBT still used; should migrate to DataComponentType / DataComponentMap for custom per-stack data
- Armor and clothing rendering — ArmorMaterial entries not registered; LayerVillagerClothes contains no render calls
- Custom A* pathfinding — PathNavigateSimple present but not wired into any entity navigator
- Diplomacy system — culture affinity data loaded but alliance/rivalry/hostility logic absent

**Not Started**
- Dynasty / family tree system
- Prayer, religion, and puja scheduling execution
- Village raids and warfare AI
- Travel Book in-game rendering
- Quest delivery and completion flow
- Villager age-progression life-cycle timer
- Import table economic loop

---

## Current Plan

PT.1: Bring the mod up to modern versions, maintaining as much of the original functionality and charm as possible.
PT.2: Redo Textures, Structures and Models with modern tools and techniques.
PT.3: Add new features and improvements to enhance the gameplay experience once the base is stable.

This project aims to bring Millénaire into the modern Minecraft ecosystem using **NeoForge** and **Minecraft 1.21.1**.

## Branch Structure

| Branch | Purpose |
|--------|---------|
| `main` | Default branch — all phases merged (1.21.1 NeoForge) |
| `feat/phase0-foundation` | Phase 0 foundation work |
| `feat/phase*` | Feature branches for individual porting phases |
| `feat/data-generation` | Data generation utilities |

> The legacy 1.12.2 Forge source is **not** maintained in this repository.

## Tech Stack

- **Minecraft** 1.21.1
- **NeoForge** 21.1.209
- **Java** 21
- **Gradle** with NeoForge ModDev plugin
- **Parchment** mappings (2024.12.07, MC 1.21.3)

## Project Structure

```
src/main/java/org/dizzymii/millenaire2/
├── Millenaire2.java        # Mod entry point
├── Config.java             # NeoForge config integration
├── MillConfig.java         # Millénaire-specific config
├── client/                 # Client-only systems (GUI, rendering hooks, client networking)
├── block/                  # Block registrations
├── culture/                # Cultures, village types, villager types, building plans
├── data/                   # Config annotations, content deployment, parameters
├── item/                   # Item registrations
├── network/                # Common/network protocol registration and payload contracts
└── util/                   # Logging, legacy block mapping, language, geometry, I/O
```

## Getting Started

### Prerequisites

- **JDK 21** (e.g. Eclipse Temurin, GraalVM)
- **Git**

### Setup

```bash
git clone https://github.com/DizzyMii/Millenaire_port.git
cd Millenaire_port
./gradlew build
```

### Running the Client

```bash
./gradlew runClient
```

### Running the Server

```bash
./gradlew runServer
```

### Data Generation

```bash
./gradlew runData
```

## Porting Phases

The port is organized into incremental phases:

| Phase | Area |
|-------|------|
| 0 | Foundation & project scaffolding |
| 4 | Entities |
| 5 | Networking |
| 6 | Village core |
| 7 | Quests |
| 8 | Client |
| 9 | Integration |
| 10 | Utilities |
| 11 | Commands |
| 12 | Building plans |
| 13 | Pathfinding |
| 14 | Goals |
| 15 | Village managers |
| 16 | Block classes |
| 17 | Book GUI |
| 18 | Client / networking remaining |
| 19 | Item classes |
| 20 | UI containers |
| 21 | Render stubs |
| 22 | Params infrastructure |
| 23 | Gap fill |

## Disclaimer

This is an **independent, fan-made project** and is **not affiliated with, endorsed by, or associated with** the original Millénaire mod author (Kinniken), Mojang Studios, Microsoft Corporation, NeoForged, or any of their subsidiaries or affiliates.

- **Minecraft** is a registered trademark of Mojang Studios / Microsoft Corporation.
- **Millénaire** is the original work of Kinniken. All credit for the original mod concept, gameplay design, and creative vision belongs to them.
- **NeoForge** is a community project and is not affiliated with Mojang Studios or Microsoft.

This project is a **clean-room port** — it exists solely to update the mod for modern Minecraft versions for the community's benefit. No decompiled Minecraft source code is distributed in this repository. If the original author of Millénaire has any concerns or wishes for this project to be taken down, please [open an issue](https://github.com/DizzyMii/Millenaire_port/issues) and it will be addressed promptly.

**This mod is provided "as-is" without warranty of any kind.** Use at your own risk. The authors of this port are not responsible for any damage to your Minecraft worlds, saves, or systems.

## Contributing

Contributions are welcome! Please open an issue or pull request on the [GitHub repository](https://github.com/DizzyMii/Millenaire_port).

## License

This project is licensed under the **MIT License**. This license applies **only to the new code written for this port**. It does not grant any rights to the original Millénaire mod's assets, branding, or intellectual property.

## Credits

- **Original Millénaire mod** by Kinniken — all original creative vision and game design credit belongs to them
- **NeoForge port** by [DizzyMii](https://github.com/DizzyMii)
