package demo.pages.layout

import demo.tr
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.sidenav.SideNavElement
import io.github.shijianjs.ktjs.vaadin.sidenav.SideNavItemElement
import io.github.shijianjs.ktjs.vaadin.sidenav.createSideNav
import io.github.shijianjs.ktjs.vaadin.sidenav.createSideNavItem
import web.components.CustomElementReference
import web.dom.TagName
import web.html.HtmlTagName

/**
 * `<demo-app-side-nav>`: the seven top-level destinations the app layout examples share.
 *
 * A side navigation item carries no `path`: the demo is a hash-routed single page, so items render as
 * plain text instead of links that would navigate away. The items are the element's own, which keeps the
 * list of destinations in one place instead of in every layout example that draws a drawer.
 */
internal class AppSideNav : CustomElementAdapter() {
    private val nav: SideNavElement =
        createSideNav().apply {
            style.setProperty("margin", "var(--lumo-space-s)")
            append(
                item("vaadin:dashboard", tr("仪表盘", "Dashboard")),
                item("vaadin:cart", tr("订单", "Orders")),
                item("vaadin:user-heart", tr("客户", "Customers")),
                item("vaadin:package", tr("产品", "Products")),
                item("vaadin:records", tr("文档", "Documents")),
                item("vaadin:list", tr("任务", "Tasks")),
                item("vaadin:chart", tr("分析", "Analytics")),
            )
        }

    init {
        attachShadowRoot().append(nav)
    }

    private fun item(iconName: String, label: String): SideNavItemElement =
        createSideNavItem().apply {
            append(
                createIcon { icon = iconName }.apply { slot = "prefix" },
                HtmlTagName.span { textContent = label },
            )
        }

    companion object : CustomElementReference<AppSideNav>(AppSideNav::class.js) {
        val NAME: TagName<AppSideNav> = TagName("demo-app-side-nav")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<demo-app-side-nav>`. */
internal fun newAppSideNav(): AppSideNav {
    AppSideNav.define()
    return AppSideNav.NAME()
}
