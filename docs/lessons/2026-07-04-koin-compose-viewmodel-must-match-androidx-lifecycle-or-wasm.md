---
skill: kotlin-multiplatform-dependency-injection
date: 2026-07-04
severity: high
type: gap
---

## Koin compose-viewmodel must match androidx.lifecycle or Wasm IR-linkage fails

## What we followed

Wired koinViewModel() from koin-compose-viewmodel per the DI/MVI skills, choosing koin=4.0.0 while using androidx-lifecycle 2.11.0-beta01. No skill warns the two must be version-compatible.

## What broke / what we discovered

JVM, Android, and iOS ran fine, but the Kotlin/Wasm build threw at runtime: 'IrLinkageError: Can not read value from backing field of property androidx_lifecycle_viewmodel_compose_LocalViewModelStoreOwner$stable ... can not be accessed in module io.insert-koin:koin-compose-viewmodel'. koin-compose-viewmodel 4.0.0 was compiled against a different lifecycle-viewmodel-compose than 2.11.0-beta01, and the Wasm IR linker is strict where the JVM/JS linkers are lenient.

## Correct pattern

Pin Koin to the release built against your androidx.lifecycle version. Bumping koin 4.0.0 -> 4.2.1 (matching lifecycle 2.11) removed all partial-linkage warnings from the wasmJs production build and fixed the runtime crash. General rule: koin-compose-viewmodel and androidx.lifecycle(-viewmodel-compose) versions must be compatible; validate on Kotlin/Wasm because a mismatch is silent on JVM/Android and only surfaces there.

## Evidence

gradle/libs.versions.toml (koin = "4.2.1"); commit 060ba6d; runtime console error on https://ronjunevaldoz.github.io/bytesweep/ before the bump; './gradlew :app:webApp:wasmJsBrowserDistribution' produced no linkage warnings after the bump.

## Proposed skill change

Add to kotlin-multiplatform-dependency-injection 'Common Anti-Patterns' (and cross-note in kotlin-multiplatform-mvi): a Koin/lifecycle version-compatibility warning. Emphasize that koin-compose-viewmodel must match the androidx.lifecycle version and that mismatches only fail on Kotlin/Wasm (IrLinkageError at runtime), so Wasm must be part of verification. Consider pinning a known-good pairing in the feature-scaffold version table.
