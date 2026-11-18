pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
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

// One Gradle project per published wrapper module.
include(":ktjs-vaadin-web-components")
