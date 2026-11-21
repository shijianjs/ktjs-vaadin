package demo

import demo.site.Site
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
import io.github.shijianjs.ktjs.dom.element.invoke

/**
 * Entry point of the component showcase.
 *
 * The chrome — app layout, drawer, breadcrumbs, the docs / language / theme menus — comes from
 * `ktjs-demo-kit`; this module only declares the catalog and the copy. The layout lives in `index.html`
 * as `<ktjs-demo-layout id="demo">`, so the browser creates the element and this function configures it.
 */
fun main() {
    // Registers the `lumo:*` icon names used by the docs menu and by the icon examples.
    registerLumoIcons()

    val language = KitLanguageState(
        languages = listOf(Langs.EN, Langs.ZH_HANS),
        storageKey = "ktjs-vaadin-language",
        initial = detectedLang(),
    )

    val theme = Theme(storageKey = "ktjs-vaadin-theme")
    theme.applyStored()

    // The companion registers the tag in its `init`; reading `NAME` upgrades the element the page already spells.
    DemoLayout.NAME
    val layout = document.getElementById(ElementId("demo")) as? DemoLayout
        ?: error("index.html has no <ktjs-demo-layout id=\"demo\">")

    layout.mount(
        LayoutConfig(
            menu = siteMenu(),
            language = language,
            theme = theme,
            texts = siteTexts,
            siteVersion = Site.VERSION,
            docsUrl = Site.DOCS_URL,
            apiUrl = Site.API_URL,
            sourceUrl = Site.SOURCE_URL,
            logoSrc = "img/ktjs-vaadin-logo.png",
        ),
    )
}
