# ktjs-vaadin

Kotlin/JS wrappers for the free [Vaadin Web Components](https://github.com/vaadin/web-components).

[English](README.md) | [简体中文](README.zh-CN.md)

**[Live demo](https://shijianjs.github.io/ktjs-vaadin/)** ·
**[API reference](https://shijianjs.github.io/ktjs-vaadin/api/)**

All free `@vaadin/*` packages release in lockstep and share one upstream version, so they are
grouped into a single `ktjs-vaadin-web-components` module: 65 npm packages and 92 custom elements.
Every npm package keeps its own Kotlin package, and upstream mixins are modelled as
`external interface` declarations so a component can be used through the capability it provides.
Tree shaking removes unused declarations from the final application bundle, and publishing one
module keeps Maven Central deployments and version noise low. Commercial Vaadin components (Board,
Charts, CRUD, Dashboard, Grid Pro, Map, Rich Text Editor), the React bindings and the `vaadin` /
`vaadin-core` meta packages are not included.

## Modules

| Module | npm packages | Wrapper version | Maven Central |
| --- | --- | --- | --- |
| [ktjs-vaadin-web-components](ktjs-vaadin-web-components/README.md) | 65 free `@vaadin/*` packages | `25.3.4-kt.1` | [![Maven Central](https://img.shields.io/maven-central/v/io.github.shijianjs/ktjs-vaadin-web-components?label=Maven%20Central&logo=apachemaven&logoColor=white)](https://central.sonatype.com/artifact/io.github.shijianjs/ktjs-vaadin-web-components) |

## Versioning

Each module is versioned as `{upstream npm version}-kt.{revision}`. The upstream npm versions are
declared once in the root [`package.json`](package.json); the revision is read per module from
[`wrapper-revisions.properties`](wrapper-revisions.properties). Gradle derives the base version and
the `npm(...)` dependency from these two files:

- Only `-kt.N` versions are published; a version without the suffix never exists.
- Upstream upgrade: change the version in `package.json`; the base version follows automatically
  while the revision stays, even when the wrapper code itself did not change.
- Wrapper-only fix: the revision increments (`kt.2`, `kt.3`, ...) while the base version stays.
- Revisions are tracked per module and only ever increase; they never reset to `kt.1`. A release
  bumps and publishes only the modules that changed.

## Requirements

- JDK 17 or newer (the build uses Gradle 9.4.1).
- Node.js is downloaded automatically by the Kotlin Gradle plugin. npm is used because Yarn is
  disabled in `gradle.properties`.
- Chromium is downloaded by Playwright the first time browser tests run.
- The demo's end-to-end suite runs on the JVM with Playwright's JUnit 5 integration, so no Node.js
  installation on `PATH` is needed.

## Build and test

```bash
./gradlew build

# Per-module tests
./gradlew :ktjs-vaadin-web-components:jsBrowserTest

# End-to-end suite for the bundled demo (Playwright + JUnit 5 on the JVM)
./gradlew :demo:e2eTest

# GitHub Pages site (demo at the root, Dokka output under /api/)
./gradlew pagesSite
```

After adding or upgrading an npm dependency in a module, refresh the shared lock file:

```bash
./gradlew kotlinUpgradePackageLock
```

## Installation

Releases are published to Maven Central under the `io.github.shijianjs` group. Replace the version
with the one you need (`{upstream version}-kt.{revision}`):

```kotlin
dependencies {
    implementation("io.github.shijianjs:ktjs-vaadin-web-components:25.3.4-kt.1")
}
```

The POMs point at this GitHub repository (`url` and `scm.url`), and every module is a Kotlin
Multiplatform project publishing `kotlin-tooling-metadata.json`. Those are the requirements for
[klibs.io](https://klibs.io/) to discover the libraries automatically.

## Publishing

Publishing is configured once in the root [`build.gradle.kts`](build.gradle.kts) for all modules.
Releases run through `.github/workflows/release.yml`: it signs the artifacts, packages one archive
per Maven coordinate and registers them with the shared Maven Central hub. The hub later merges the
pending registrations into a single Central deployment. See the publishing section in
[AGENTS.md](AGENTS.md) for the one-time setup.

## License

Released under the [MIT License](LICENSE).
