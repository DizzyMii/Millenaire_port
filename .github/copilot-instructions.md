# Copilot Instructions for Millénaire 2 — NeoForge Port

## Project Overview

This is a community-driven port of the classic [Millénaire](https://millenaire.org/) Minecraft mod from **Minecraft 1.12.2 Forge** to **Minecraft 1.21.1 NeoForge**. The mod adds NPC villages inspired by real-world historical cultures (Norman, Indian, Mayan, Japanese, Byzantine, Inuit, Seljuk, etc.). Villagers build, trade, farm, and expand their settlements over time.

## Tech Stack

- **Language:** Java 21
- **Minecraft version:** 1.21.1
- **Mod loader:** NeoForge 21.1.209
- **Build tool:** Gradle with the NeoForge ModDev plugin (`net.neoforged.moddev`)
- **Mappings:** Parchment (2024.12.07, MC 1.21.3)
- **License:** MIT (applies only to new code in this port)

## Project Structure

```
src/main/java/org/dizzymii/millenaire2/
├── Millenaire2.java        # Mod entry point (@Mod annotation)
├── Config.java             # NeoForge config integration
├── MillConfig.java         # Millénaire-specific configuration
├── block/                  # Block registrations
├── culture/                # Cultures, village types, villager types, building plans
├── data/                   # Config annotations, content deployment, parameters
├── item/                   # Item registrations
└── util/                   # Logging, legacy block mapping, language, geometry, I/O

src/main/resources/         # Assets and data packs (textures, lang, recipes, etc.)
src/main/templates/         # Metadata templates (neoforge.mods.toml, etc.)
src/generated/resources/    # Auto-generated resources (do not edit manually)
```

## Build & Run Commands

```bash
# Build the mod JAR
./gradlew build

# Launch a Minecraft client with the mod loaded
./gradlew runClient

# Launch a headless dedicated server with the mod loaded
./gradlew runServer

# Run data generators (outputs to src/generated/resources/)
./gradlew runData

# Run game tests
./gradlew runGameTestServer
```

> `check` (and therefore `build`) automatically runs `runGameTestServer`, so all game tests execute on every build.

## Coding Conventions

- **Java 21** features are encouraged (records, sealed classes, pattern matching, etc.).
- Follow the **existing package and class naming patterns** in `org.dizzymii.millenaire2.*`.
- Use `UTF-8` encoding for all source files (enforced by Gradle).
- Keep classes small and focused; prefer composition over inheritance where it aligns with the existing architecture.
- Do **not** include decompiled Minecraft source, extracted JARs, or any copyrighted Millénaire assets.
- Do **not** commit IDE-specific files (`.idea/`, `.vscode/`) unless intentional and discussed.
- Generated resources in `src/generated/resources/` are produced by `./gradlew runData` — do not edit them manually.

## Commit Message Format

Use [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add Norman villager pathfinding
fix: resolve null pointer in village spawn logic
docs: update README with new build instructions
chore: bump NeoForge version to 21.1.x
```

## Branching Strategy

| Pattern | Purpose |
|---------|---------|
| `main` | Stable, all phases merged |
| `feat/<description>` | New features or porting phases |
| `fix/<description>` | Bug fixes |
| `chore/<description>` | Cleanup, dependency updates, tooling |
| `docs/<description>` | Documentation only |

- **Never push directly to `main`** — all changes go through pull requests.
- Create branches from `main` and keep them small and focused (one logical change per PR).

## Porting Phases

The port is organised into incremental numbered phases. Check the README for the current phase mapping. When working on porting tasks, align changes with the relevant phase branch (`feat/phase*`).

## Key Patterns & APIs

- **Mod entry point:** `Millenaire2.java` — annotated with `@Mod(Millenaire2.MODID)`.
- **Registry objects** (blocks, items, etc.) follow the NeoForge `DeferredRegister` pattern.
- **Configuration** is split between NeoForge config (`Config.java`) and Millénaire-specific config (`MillConfig.java`).
- **Culture/village data** lives under `culture/` and is data-driven where possible.
- **Utility helpers** (logging, I/O, geometry) live under `util/`.

## Testing

- Game tests use the NeoForge GameTest framework.
- Test classes go in `src/main/java/…` under a `test` sub-package or in a dedicated test source set if one is added.
- Run `./gradlew runGameTestServer` to execute all registered game tests.

## Contribution Notes for Copilot

- Prefer minimal, focused changes — avoid large refactors unless the task explicitly requires them.
- When adding new registry entries (blocks, items, entities), follow the existing `DeferredRegister` patterns already in the codebase.
- Do not include AI-generated code dumps; ensure every generated snippet is reviewed and understood.
- Check `CONTRIBUTING.md` for the full contribution workflow, including PR requirements and review process.
