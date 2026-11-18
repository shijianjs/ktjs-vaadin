package demo

import demo.site.SITE_VERSION
import demo.site.API_URL
import demo.site.DOCS_URL
import demo.site.SOURCE_URL
import demo.site.siteMenu
import demo.site.siteTexts
import io.github.shijianjs.ktjs.demo.i18n.LanguageState as KitLanguageState
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.layout.DemoLayout
import io.github.shijianjs.ktjs.demo.layout.LayoutConfig
import io.github.shijianjs.ktjs.demo.style.Theme
import io.github.shijianjs.ktjs.vaadin.lumostyles.registerLumoIcons
import web.dom.ElementId
import web.dom.document

/**
 * Entry point of the component showcase.
 *
 * The chrome — app layout, drawer, breadcrumbs, the docs / language / theme menus — comes from
 * `ktjs-demo-kit`; this module only declares the catalog and the copy. The layout lives in `index.html`
 * as `<ktjs-demo-layout id="demo">`, so the browser creates the element and this function configures it.
 */
fun main() {
    // The page builders still resolve copy through `tr` while they build, so the language has to be
    // settled before the first page is created.
    LanguageState.init()
    // Registers the `lumo:*` icon names used by the docs menu and by the icon examples.
    registerLumoIcons()

    val language = KitLanguageState(
        languages = listOf(Langs.EN, Langs.ZH_HANS),
        storageKey = "ktjs-vaadin-language",
        initial = if (LanguageState.current == Language.EN) Langs.EN else Langs.ZH_HANS,
    )

    // Registered before the layout mounts: the layout notifies its own listener in `mount`, and the
    // legacy builders read `LanguageState`, so the demo's state has to move first.
    language.onChange { lang -> LanguageState.set(if (lang == Langs.EN) Language.EN else Language.ZH) }

    val theme = Theme(storageKey = "ktjs-vaadin-theme")
    theme.applyStored()

    DemoLayout.define()
    val layout = document.getElementById(ElementId("demo")) as? DemoLayout
        ?: error("index.html has no <ktjs-demo-layout id=\"demo\">")

    layout.mount(
        LayoutConfig(
            menu = siteMenu(),
            language = language,
            theme = theme,
            texts = siteTexts,
            siteVersion = SITE_VERSION,
            docsUrl = DOCS_URL,
            apiUrl = API_URL,
            sourceUrl = SOURCE_URL,
            logoSrc = "img/ktjs-vaadin-logo.png",
        ),
    )
}
