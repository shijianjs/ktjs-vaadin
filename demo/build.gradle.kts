plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        useEsModules()

        // The demo is a standalone page, so the JS target needs an executable webpack bundle.
        binaries.executable()
        browser()
    }

    // The JVM target carries only the Playwright end-to-end suite in `src/jvmTest`; the demo itself
    // stays a JS page.
    jvm()

    sourceSets {
        jsMain.dependencies {
            implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.10.4")
            // Wrapper modules used by the demo.
            implementation(project(":ktjs-vaadin-web-components"))
        }

        jvmTest.dependencies {
            implementation("com.microsoft.playwright:playwright:1.61.0")
            implementation("cn.hutool:hutool-all:5.8.48")
            implementation("org.junit.jupiter:junit-jupiter:5.14.1")
            implementation("org.junit-pioneer:junit-pioneer:2.3.0")
            implementation(kotlin("test-junit5"))
            runtimeOnly("org.junit.platform:junit-platform-launcher:1.14.1")
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Lumo theme
//
// Vaadin 25 ships Lumo as a plain stylesheet: `lumo.css` declares the design tokens and the
// `@media lumo_<module>` blocks that `LumoInjectionMixin` copies into each component's Shadow DOM
// by reading `document.styleSheets`. It therefore has to be a real stylesheet of the page, not an
// inline string and not something webpack could bundle (the Kotlin/JS webpack config has no CSS
// loader).
//
// `dist/lumo.css` is the pre-built, self-contained build of that stylesheet (no `@import`s, the
// icon font is inlined), so it only has to be copied next to `index.html`.
// ---------------------------------------------------------------------------------------------
val lumoStyles = rootProject.layout.buildDirectory.dir("js/node_modules/@vaadin/vaadin-lumo-styles/dist")

val copyLumoStyles = tasks.register<Copy>("copyLumoStyles") {
    description = "Copies the pre-built Lumo stylesheet into the demo resources."
    // The file comes from the npm workspace, which the Kotlin/JS plugin installs at the root.
    dependsOn(":kotlinNpmInstall")
    from(lumoStyles) {
        include("lumo.css")
        include("utility.css")
    }
    into(layout.buildDirectory.dir("generated/lumo"))
}

kotlin.sourceSets.getByName("jsMain").resources.srcDir(copyLumoStyles)

// ---------------------------------------------------------------------------------------------
// End-to-end suite
//
// The suite lives in `src/jvmTest`, so the demo stays one KMP module instead of carrying a second
// Gradle project. It uses Playwright's official JUnit 5 integration plus Hutool's SimpleServer for
// the static bundle, and reuses the Chromium that `kotlinInstallPlaywrightBrowsers` installs for
// the Kotlin/JS browser tests (hence the pinned Playwright version in the dependencies).
// `jvmTest` itself is disabled to keep `build`/`check` browser-free; this dedicated task runs the
// same test compilation on demand.
// ---------------------------------------------------------------------------------------------
val jvmE2eCompilation = kotlin.targets.getByName("jvm").compilations.getByName("test")

tasks.register<Test>("e2eTest") {
    group = "verification"
    description = "Runs the Kotlin/JVM Playwright suite against the bundled demo page."
    useJUnitPlatform()
    dependsOn("jsBrowserDistribution", ":ktjs-vaadin-web-components:kotlinInstallPlaywrightBrowsers")

    testClassesDirs = jvmE2eCompilation.output.classesDirs
    classpath = files(jvmE2eCompilation.output.allOutputs, jvmE2eCompilation.runtimeDependencyFiles)
    systemProperty("demo.dist", layout.buildDirectory.dir("dist/js/productionExecutable").get().asFile.absolutePath)
    systemProperty("e2e.output", layout.buildDirectory.dir("e2e-results").get().asFile.absolutePath)
    systemProperty("junit.jupiter.execution.timeout.default", "30 s")

    outputs.upToDateWhen { false }
}

tasks.named<Test>("jvmTest") { enabled = false }
