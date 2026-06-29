# AGENTS.md — Bytesweep

This project uses [kmm-agent-skills](https://github.com/ronjunevaldoz/kmm-agent-skills).
Skills are installed in `.claude/skills/`.

Bytesweep is a cross-platform **storage/app cleaner** (Android, iOS, Desktop/JVM, Web
JS+WasmJs, Ktor server) built on the Kotlin/kmp-wizard `all-targets` baseline.

## Architecture note

This project uses **clean-architecture packages inside `:app:shared`** rather than separate
Gradle feature modules. The wizard baseline ships without `build-logic` convention plugins,
so layering is package-enforced under `io.github.ronjunevaldoz.bytesweep`:

```
model/      pure types (JunkItem, JunkCategory, ScanResult)
domain/     StorageScanner interface + use cases
data/       platform StorageScanner impls (expect platformModule via Koin)
presenter/  ScannerViewModel + ScannerContract (MVI, no Compose)
ui/         ScannerScreen / ScannerContent + theme (Compose Multiplatform)
di/         Koin appModule + expect platformModule
core/mvi/   MviViewModel base (StateFlow + Channel)
```

## Skill routing

| Topic | Skill |
|---|---|
| ViewModel / screen state | `kotlin-multiplatform-mvi` |
| Dependency injection (Koin) | `kotlin-multiplatform-dependency-injection` |
| REST API / network (Ktor) | `kotlin-multiplatform-network-layer` |
| Auth service (Ktor) | `kotlin-multiplatform-ktor-auth-service` |
| Unit tests (Turbine) | `kotlin-multiplatform-unit-testing` |
| Design system | `kotlin-multiplatform-design-system` |
| Screenshot tests | `kotlin-multiplatform-roborazzi` |
| expect/actual platform code | `kotlin-multiplatform-expect-actual` |
| Architecture audit | `kotlin-multiplatform-audit` |
| New feature scaffold | `kotlin-multiplatform-feature-scaffold` |

Detected from `gradle/libs.versions.toml`: **koin**, **ktor**, **turbine**.

## Modules

| Module | Role |
|---|---|
| `:app:shared` | shared Compose UI + business logic (all targets) |
| `:app:androidApp` | Android entry point (`BytesweepApplication` starts Koin) |
| `:app:desktopApp` | Desktop (JVM) entry point |
| `:app:webApp` | Web (JS + WasmJs) entry point |
| `:app:iosApp` | iOS entry point (Xcode) |
| `:core` | shared utility module |
| `:server` | Ktor server module |

## Build note

Run Gradle with **JDK 17** (`export JAVA_HOME=$(/usr/libexec/java_home -v 17)`); the
system default JDK 25 is rejected by Gradle 9.1.

## Commands installed

See `.claude/commands/kmm-*.md`. Key commands:
- `/kmm-implement-feature <name>` — plan → implement → validate → review a new feature
- `/kmm-run-audit` — architecture audit with per-finding remediation
- `/kmm-verify` — full validation pipeline (tests, audit, design, screenshots)
- `/kmm-fix-design` — scan and fix design system violations
- `/kmm-record-design-baselines` — record Roborazzi golden PNGs
- `/kmm-update-skills` — pull latest skills and re-deploy
