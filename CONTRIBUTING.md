# Contributing to Millénaire 2

Thanks for your interest in contributing to the Millénaire 2 NeoForge port! This document outlines the guidelines and process for contributing.

## Code of Conduct

Be respectful, constructive, and collaborative. Harassment, trolling, or disruptive behavior will not be tolerated.

## Getting Started

1. **Fork** the repository on GitHub.
2. **Clone** your fork locally:

   ```bash
   git clone https://github.com/<your-username>/Millenaire_port.git
   cd Millenaire_port
   ```

3. **Set up the upstream remote:**

   ```bash
   git remote add upstream https://github.com/DizzyMii/Millenaire_port.git
   ```

4. **Build the project:**

   ```bash
   ./gradlew build
   ```

## Branching

- **Never** push directly to `main`. All changes go through pull requests.
- Create a feature branch from `main` using the following naming conventions:
  - **Features:** `feat/short-description`
  - **Bug fixes:** `fix/short-description`
  - **Chores/cleanup:** `chore/short-description`
  - **Documentation:** `docs/short-description`

Example:

```bash
git checkout main
git pull upstream main
git checkout -b feat/add-byzantine-villager-textures
```

## Making Changes

- Keep changes **small and focused**. One logical change per PR.
- Follow the existing code style and conventions.
- Do **not** include IDE-specific config changes (`.idea/`, `.vscode/`) unless they are intentional and discussed.
- Do **not** commit decompiled source, extracted JARs, or any files covered by `.gitignore`.
- If you add a new dependency, include it in the same commit.

## Commit Messages

Use [Conventional Commits](https://www.conventionalcommits.org/) format:

```
feat: add Norman villager pathfinding
fix: resolve null pointer in village spawn logic
docs: update README with new build instructions
chore: bump NeoForge version to 21.1.x
```

## Submitting a Pull Request

1. **Push** your branch to your fork:

   ```bash
   git push origin feat/your-feature-name
   ```

2. **Open a Pull Request** against `DizzyMii/Millenaire_port` → `main`.
3. Fill out the PR description:
   - **What** does this change do?
   - **Why** is it needed?
   - **How** was it tested?
4. All PRs **require review and approval** from a maintainer before merging.
5. Be responsive to feedback — maintainers may request changes.

## What We're Looking For

- Bug fixes and stability improvements
- Porting work aligned with the current phase (see README for phase list)
- New textures, models, or structures (PT.2 of the roadmap)
- Documentation improvements
- Test coverage

## What to Avoid

- Large, sweeping refactors without prior discussion — open an issue first.
- Changes unrelated to the current porting effort.
- Inclusion of any copyrighted assets from the original Millénaire mod without permission.
- AI-generated code dumps without review or understanding of the changes.

## Reporting Issues

- Use [GitHub Issues](https://github.com/DizzyMii/Millenaire_port/issues) to report bugs or request features.
- Include steps to reproduce, expected vs actual behavior, and your environment (MC version, Java version, OS).

## License

By contributing, you agree that your contributions will be licensed under the **MIT License** as described in the project's LICENSE file.

## Questions?

- **Discord:** DizzyMii
- **Email:** DizzyMii.Github@gmail.com

Open an issue if you're unsure about anything — we'd rather help than have you waste time going in the wrong direction.
