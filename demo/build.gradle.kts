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

// End-to-end checks for the bundled page. Playwright and Chromium come from the Kotlin/JS npm
// workspace under build/js, so the first wrapper module's browser install task provides them.
tasks.register<Exec>("e2eTest") {
    group = "verification"
    description = "Runs the Playwright checks against the bundled demo page."
    dependsOn("jsBrowserDistribution", ":ktjs-vaadin-web-components:kotlinInstallPlaywrightBrowsers")

    val dist = layout.buildDirectory.dir("dist/js/productionExecutable")
    inputs.dir(dist)
    inputs.file(layout.projectDirectory.file("e2e/demo-page.test.mjs"))
    environment("DEMO_DIST", dist.get().asFile.absolutePath)
    commandLine("node", "e2e/demo-page.test.mjs")
}
