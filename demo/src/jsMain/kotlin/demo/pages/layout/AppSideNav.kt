package demo.pages.layout

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.lumostyles.LumoCustomProperties
import io.github.shijianjs.ktjs.vaadin.sidenav.SideNavElement
import io.github.shijianjs.ktjs.vaadin.sidenav.SideNavItemElement
import io.github.shijianjs.ktjs.vaadin.sidenav.createSideNav
import io.github.shijianjs.ktjs.vaadin.sidenav.createSideNavItem
import web.cssom.`var`
import web.html.HtmlTagName

/**
 * `<demo-app-side-nav>`: the seven top-level destinations the app layout examples share.
 *
 * A side navigation item carries no `path`: the demo is a hash-routed single page, so items render as
 * plain text instead of links that would navigate away. The items are the element's own, which keeps the
 * list of destinations — and their copy — in one place instead of in every layout example that draws a
 * drawer.
 */
internal class AppSideNav : CustomElementAdapter() {
    private val texts = Binder(appSideNavTexts)

    private val nav: SideNavElement =
        createSideNav().apply {
            inlineStyle.margin = `var`(LumoCustomProperties.SPACE_S)
            append(
                item("vaadin:dashboard") { it.dashboard },
                item("vaadin:cart") { it.orders },
                item("vaadin:user-heart") { it.customers },
                item("vaadin:package") { it.products },
                item("vaadin:records") { it.documents },
                item("vaadin:list") { it.tasks },
                item("vaadin:chart") { it.analytics },
            )
        }

    init {
        attachShadowRoot().append(nav)
        applyTranslation(Langs.EN)
    }

    private fun item(iconName: String, copy: (AppSideNavTexts) -> String): SideNavItemElement =
        createSideNavItem().apply {
            val label = HtmlTagName.span()
            texts.bind(label, copy) { label.textContent = it }
            append(createIcon { icon = iconName }.apply { slot = "prefix" }, label)
        }

    /** Writes the destinations in [lang]. */
    fun applyTranslation(lang: Lang) {
        texts.apply(lang)
    }

    companion object : AppSideNavDefaultReference(name = "demo-app-side-nav") {
        private val english: AppSideNavTexts =
            AppSideNavTexts(
                dashboard = "Dashboard",
                orders = "Orders",
                customers = "Customers",
                products = "Products",
                documents = "Documents",
                tasks = "Tasks",
                analytics = "Analytics",
            )
        private val chinese: AppSideNavTexts =
            AppSideNavTexts(
                dashboard = "仪表盘",
                orders = "订单",
                customers = "客户",
                products = "产品",
                documents = "文档",
                tasks = "任务",
                analytics = "分析",
            )
        private val appSideNavTexts: Translations<AppSideNavTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
