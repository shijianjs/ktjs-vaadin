import org.jetbrains.kotlin.gradle.ExperimentalJsTestDsl

plugins {
    kotlin("multiplatform")
}

// Wrapper version = {upstream npm version}-kt.{revision}. See the versioning section in AGENTS.md.
@Suppress("UNCHECKED_CAST")
val npmVersions = rootProject.extra["npmVersions"] as Map<String, String>
@Suppress("UNCHECKED_CAST")
val wrapperRevisions = rootProject.extra["wrapperRevisions"] as Map<String, Int>
val upstreamVersion = npmVersions.getValue("@vaadin/button")
val wrapperRevision = wrapperRevisions.getValue(project.name)
version = "$upstreamVersion-kt.$wrapperRevision"
description = "Kotlin/JS wrappers for the free Vaadin web components."

kotlin {
    js {
        useEsModules()

        browser {
            @OptIn(ExperimentalJsTestDsl::class)
            test {
                chromium()
            }
        }
    }

    sourceSets {
        jsMain.dependencies {
            api("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.10.5")
            // Every @vaadin/* entry in the root package.json is installed; package.json is the
            // single source of truth for upstream versions, so a Dependabot bump reaches all
            // packages that release together.
            npmVersions.keys
                .filter { it.startsWith("@vaadin/") }
                .forEach { implementation(npm(it, npmVersions.getValue(it))) }
        }
        jsTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
