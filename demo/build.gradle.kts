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

    sourceSets {
        jsMain.dependencies {
            implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.10.1")
            // Wrapper modules used by the demo.
            implementation(project(":ktjs-vaadin-web-components"))
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
    }
    into(layout.buildDirectory.dir("generated/lumo"))
}

kotlin.sourceSets.getByName("jsMain").resources.srcDir(copyLumoStyles)

// ---------------------------------------------------------------------------------------------
// End-to-end tests
//
// `e2e` is a standalone npm/TypeScript project using @playwright/test. Its Chromium comes from the
// same Playwright browser cache the Kotlin/JS browser tests use
// (`:ktjs-vaadin-web-components:kotlinInstallPlaywrightBrowsers`), which is why the Playwright
// version below is pinned to the one the Kotlin Gradle plugin installs.
// ---------------------------------------------------------------------------------------------
val npmExecutable = if (System.getProperty("os.name").lowercase().contains("win")) "npm.cmd" else "npm"

val e2eInstall = tasks.register<Exec>("e2eInstall") {
    group = "verification"
    description = "Installs the npm dependencies of the end-to-end test project."
    workingDir = layout.projectDirectory.dir("e2e").asFile
    commandLine(npmExecutable, "install", "--no-audit", "--no-fund")
    inputs.file(layout.projectDirectory.file("e2e/package.json"))
    outputs.dir(layout.projectDirectory.dir("e2e/node_modules"))
}

tasks.register<Exec>("e2eTest") {
    group = "verification"
    description = "Runs the Playwright suite against the bundled demo page."
    dependsOn("jsBrowserDistribution", e2eInstall, ":ktjs-vaadin-web-components:kotlinInstallPlaywrightBrowsers")

    val dist = layout.buildDirectory.dir("dist/js/productionExecutable")
    inputs.dir(dist)
    inputs.dir(layout.projectDirectory.dir("e2e/tests"))
    environment("DEMO_DIST", dist.get().asFile.absolutePath)

    workingDir = layout.projectDirectory.dir("e2e").asFile
    commandLine("node", "node_modules/@playwright/test/cli.js", "test")
}
