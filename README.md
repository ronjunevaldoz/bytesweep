# Bytesweep

A cross-platform **storage / app cleaner** built with Kotlin Multiplatform and Compose
Multiplatform. Bytesweep scans for reclaimable junk (caches, temp files, logs, large
files) and lets you clean it — from a single shared codebase running on **Android, iOS,
Desktop (JVM) and Web (JS + WasmJs)**, with a Ktor server module included.

## What it does

- **Scan** local storage for junk, grouped by category (cache, temp, logs, large files).
- **Review** discovered items with per-item and select-all checkboxes and live size totals.
- **Clean** the selected items and see how much space was reclaimed.

Each platform provides its own scanner via dependency injection:

| Platform | Scanner | Scans |
|---|---|---|
| Android | `AndroidStorageScanner` | app internal/external/code cache dirs |
| Desktop (JVM) | `DesktopStorageScanner` | JVM temp dir + `~/Library/Caches` (macOS) |
| iOS | `IosStorageScanner` | `NSCachesDirectory` + `NSTemporaryDirectory` |
| Web (JS/WasmJs) | `DemoStorageScanner` | representative dataset (browser sandbox blocks FS access) |

## Architecture

Clean-architecture layering inside `:app:shared` (`io.github.ronjunevaldoz.bytesweep`):

```
model/        JunkItem, JunkCategory, ScanResult        (pure types)
domain/       StorageScanner interface + use cases       (business contract)
data/         platform StorageScanner implementations    (expect/actual via Koin)
presenter/    ScannerViewModel + ScannerContract         (MVI — no Compose)
ui/           ScannerScreen / ScannerContent + theme     (Compose Multiplatform)
di/           Koin appModule + expect platformModule     (DI wiring)
core/mvi/     MviViewModel base (StateFlow + Channel)
```

- **MVI**: unidirectional `State` / `Intent` / `Effect`. State via `StateFlow` with atomic
  `update {}`, one-shot effects via `Channel` (no replay on recomposition).
- **DI**: Koin 4 with manual modules. Common bindings in `appModule`; each platform binds
  its `StorageScanner` through an `expect fun platformModule()`.

> **Note on DI:** manual Koin modules are used (rather than annotated/KSP) for clean,
> codegen-free builds across all five targets including JS and WasmJs. To migrate to
> annotated DI later, add the Koin compiler plugin and replace `appModule` with
> `@Single`/`@KoinViewModel` annotations.

## Project layout

- `/app/shared` — shared Compose UI + business logic for all platforms.
- `/app/androidApp` — Android entry point (`BytesweepApplication` starts Koin).
- `/app/desktopApp` — Desktop (JVM) entry point.
- `/app/webApp` — Web (JS + WasmJs) entry point.
- `/app/iosApp` — iOS entry point (open in Xcode).
- `/core` — shared utility module.
- `/server` — Ktor server module.

## Running

> Requires JDK 17 to run Gradle (the build targets JVM 11). With multiple JDKs installed:
> `export JAVA_HOME=$(/usr/libexec/java_home -v 17)`

- Android: `./gradlew :app:androidApp:assembleDebug`
- Desktop: `./gradlew :app:desktopApp:run`
- Web (WasmJs): `./gradlew :app:webApp:wasmJsBrowserDevelopmentRun`
- Web (JS): `./gradlew :app:webApp:jsBrowserDevelopmentRun`
- Server: `./gradlew :server:run`
- iOS: open `/app/iosApp` in Xcode and run.

## Tests

- Desktop/JVM (incl. `ScannerViewModel` MVI tests): `./gradlew :app:shared:jvmTest`
- Common (`formatSize`): bundled in the common test source set.
- iOS: `./gradlew :app:shared:iosSimulatorArm64Test`
- Web: `./gradlew :app:shared:jsTest` / `:app:shared:wasmJsTest`

---

Built on the [Kotlin/kmp-wizard](https://github.com/Kotlin/kmp-wizard) `all-targets`
baseline. AGP 9 · Kotlin 2.4 · Compose Multiplatform 1.11 · Koin 4.
