pluginManagement {
    repositories {
        // The docs site is built with the ktjs-docusaurus Gradle plugin. Released versions come from
        // Central; `mavenLocal` is what serves a SNAPSHOT one, which is how the site is developed
        // against an unreleased wrapper. An empty local repository falls through to the next
        // repository, so the order costs nothing in CI.
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
    // The plugin's version is a catalog entry (`libs.plugins.ktjs.docusaurus` in
    // `gradle/libs.versions.toml`); `docs` applies it with `alias(...)`, so no resolution strategy
    // is needed here.
}

dependencyResolutionManagement {
    repositories {
        // Maven Central is the source of truth for kotlin-wrappers: the Aliyun mirror lags behind
        // and sometimes serves metadata without the corresponding klibs. Gradle does not fall back
        // to the next repository once metadata is found, so pin these modules to Central even when
        // a local mirror is active.
        exclusiveContent {
            forRepository { mavenCentral() }
            filter {
                includeGroup("org.jetbrains.kotlin-wrappers")
                includeModule("org.jetbrains.kotlin", "kotlin-js-plain-objects")
                // the stdlib half of the same trap: its JS klib only exists on Central
                includeGroup("org.jetbrains.kotlin")
            }
        }
        mavenCentral()
        // The wrapper kit (typed `Attribute` API) is developed in the ktjs-dom-kit repository and is
        // published locally while this refactor is in flight. The group filter is load-bearing:
        // mavenLocal is searched first, so an unfiltered repository here would let a stale local POM
        // shadow every other dependency (a POM-only `kotlin-stdlib` once removed its JS variant and
        // produced hundreds of "unresolved reference" errors).
        mavenLocal {
            content {
                includeGroupByRegex("io\\.github\\.shijianjs(\\..*)?")
            }
        }
    }
}

rootProject.name = "ktjs-vaadin"

// The demo is the GitHub Pages site, not a published module.
include(":demo")

// The documentation site: Kotlin configuration plus Markdown, assembled by the ktjs-docusaurus
// Gradle plugin into a static site that `pagesSite` publishes at the Pages root.
include(":docs")

// One Gradle project per published wrapper module.
include(":ktjs-vaadin-web-components")
