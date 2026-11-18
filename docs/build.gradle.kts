plugins {
    kotlin("multiplatform")
    id("io.github.shijianjs.ktjs.docusaurus")
}

/**
 * `docs` is this project's own documentation site: Markdown lives in `src/jsMain/resources`,
 * configuration is Kotlin, and `./gradlew :docs:docusaurusBuild` turns both into the static site
 * GitHub Pages serves at `<pages root>/`.
 *
 * The module is loaded by Node (Docusaurus imports `docusaurus.config.mjs` from its bundle), so it
 * must not define custom elements: those read `HTMLElement` at module load.
 */
kotlin {
    js {
        useEsModules()
        nodejs()
        binaries.library()
    }

    compilerOptions {
        // `kotlin.js.JsAny` is the type of every upstream member declared as `unknown`.
        optIn.add("kotlin.js.ExperimentalWasmJsInterop")
    }

    sourceSets {
        jsMain.dependencies {
            // The wrapper brings its own `@docusaurus/*` npm dependencies: the klib records the
            // versions it was built against and Kotlin/JS installs them in the npm workspace.
            implementation(
                "io.github.shijianjs:ktjs-docusaurus:${providers.gradleProperty("ktjs-docusaurus.version").get()}",
            )

            // The mermaid theme is resolved *by name* from the generated configuration, so npm has
            // to install it next to the site: no Kotlin code imports it, and the wrapper does not
            // bring it along. Version is the upstream `@docusaurus/*` lockstep anchor (3.10.2).
            implementation(npm("@docusaurus/theme-mermaid", "3.10.2"))
        }
    }
}

docusaurus {
    // Generates `sidebars.mjs` next to the site configuration from the exported `sidebars` function.
    sidebarsFunction = "sidebars"

    // Dev 默认中文启动：`docusaurusStart` 不带参数也能起中文站点；构建不受影响。单次运行仍可用
    // `--locale en` / `-Pdocusaurus.locale=en` 覆盖。
    startArguments = listOf("--locale", "zh-Hans")
}
