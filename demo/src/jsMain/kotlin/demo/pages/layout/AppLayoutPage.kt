package demo.pages.layout

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.Blocks.row as kitRow
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.heading
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.pages.data.gridItems
import demo.pages.fixtures.people
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.listen
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.applayout.AppLayoutElement
import io.github.shijianjs.ktjs.vaadin.applayout.AppLayoutEvents
import io.github.shijianjs.ktjs.vaadin.applayout.AppLayoutPrimarySections
import io.github.shijianjs.ktjs.vaadin.applayout.createAppLayout
import io.github.shijianjs.ktjs.vaadin.applayout.createDrawerToggle
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.grid.GridElement
import io.github.shijianjs.ktjs.vaadin.grid.createGrid
import io.github.shijianjs.ktjs.vaadin.grid.createGridColumn
import io.github.shijianjs.ktjs.vaadin.horizontallayout.createHorizontalLayout
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.verticallayout.createVerticalLayout
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement
import web.html.HTMLParagraphElement
import web.html.HtmlTagName

private val english: AppLayoutTexts =
    AppLayoutTexts(
        basicHeading = "Basic usage",
        basicHint = "An application consists of a content area, a navbar and a drawer; the drawer is a " +
            "fixed column on wide screens and a toggleable overlay on narrow ones.",
        myApp = "MyApp",
        contentText = "The content area is filled through the default slot.",
        navbarHeading = "Navbar",
        navbarHint = "The navbar is the header above the content area; it can hold the application title, " +
            "primary actions or secondary navigation.",
        dashboard = "Dashboard",
        orders = "Orders",
        customers = "Customers",
        products = "Products",
        contentArea = "Content area",
        placementHeading = "Navbar placement",
        placementHint = "By default the navbar sits on top; with primarySection set to drawer the drawer " +
            "takes the full height and the navbar moves above the content on the right, where it usually " +
            "holds the title and actions of the current view.",
        dashboardContent = "Dashboard content",
        togglePrimary = "Toggle primarySection",
        scrollingHeading = "Scrolling behavior",
        scrollingHint = "The layout grows with its content by default and the page scrolls. Give the " +
            "layout a fixed height and let the content area fill it to scroll inside the content area " +
            "instead.",
        autoHeight = "Auto height",
        fixedHeight = "Full height",
        bottomHeading = "Bottom navbar on small touchscreens",
        bottomHint = "With --vaadin-app-layout-touch-optimized enabled, the navbar touch-optimized slot " +
            "is shown at the bottom of the screen, as fitted for a handful of primary destinations on " +
            "mobile.",
        viewTitleContent = "View title and content",
        secondaryHeading = "Secondary navigation",
        secondaryHint = "The drawer holds the main navigation while the navbar carries the title of the " +
            "current view and the secondary navigation that belongs to it.",
        all = "All",
        open = "Open",
        completed = "Completed",
        cancelled = "Cancelled",
        orderList = "Order list",
    )

private val chinese: AppLayoutTexts =
    AppLayoutTexts(
        basicHeading = "基本用法",
        basicHint = "应用由内容区、导航栏与抽屉组成：抽屉在宽屏上是固定栏，窄屏时变成由抽屉开关控制的浮层。",
        myApp = "我的应用",
        contentText = "内容区由默认插槽填充。",
        navbarHeading = "导航栏",
        navbarHint = "navbar 是内容区上方的头部，可以放应用名、主工具栏或次级导航。",
        dashboard = "仪表盘",
        orders = "订单",
        customers = "客户",
        products = "产品",
        contentArea = "内容区",
        placementHeading = "导航栏位置",
        placementHint = "默认导航栏在顶部；把 primarySection 设为 drawer 后抽屉占满整个高度，导航栏移到右侧内容区上方，" +
            "常用来放当前视图的标题与次级操作。",
        dashboardContent = "仪表盘内容",
        togglePrimary = "切换 primarySection",
        scrollingHeading = "滚动行为",
        scrollingHint = "布局默认随内容增高，页面自己滚动；给布局设固定高度并让内容区撑满，长内容就在内容区内部滚动。",
        autoHeight = "自动高度",
        fixedHeight = "固定高度",
        bottomHeading = "小屏底部导航栏",
        bottomHint = "打开 --vaadin-app-layout-touch-optimized 后，放在 navbar touch-optimized 插槽里的导航会显示在" +
            "屏幕底部，适合移动端的少数几个主目的地。",
        viewTitleContent = "视图标题与内容",
        secondaryHeading = "次级导航",
        secondaryHint = "抽屉放大类导航，navbar 里放当前视图的标题与只与该视图相关的次级导航。",
        all = "全部",
        open = "进行中",
        completed = "已完成",
        cancelled = "已取消",
        orderList = "订单列表",
    )

private val appLayoutTexts: Translations<AppLayoutTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class AppLayoutPage : DemoPage() {
    private val texts = Binder(appLayoutTexts)

    private val frame = PageFrame(ENTRY)

    /** Every drawer built on this page, so a switch can rewrite their shared navigation. */
    private val drawers = mutableListOf<AppDrawer>()

    init {
        frame.mount(this)
        registerVaadinIcons()

        fun drawer(): AppDrawer = AppDrawer.NAME().also { drawers += it }

        fun content(copy: (AppLayoutTexts) -> String): AppContent =
            AppContent.NAME().also { node -> texts.bind(node, copy) { node.show(it) } }

        fun link(icon: String? = null, copy: (AppLayoutTexts) -> String): NavLink =
            NavLink.NAME().also { node -> texts.bind(node, copy) { node.show(it, icon) } }

        fun navTitle(copy: (AppLayoutTexts) -> String): HTMLElement {
            val node = heading(3, "")
            texts.bind(node, copy) { node.textContent = it }
            node.slot = "navbar"
            node.style.setProperty("margin", "0")
            return node
        }

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val layout = boxedAppLayout("22rem")
        layout.dataset["test"] = "app-layout"
        val navbar = AppNavbar.NAME()
        texts.bind(navbar.captionNode) { it.myApp }
        layout.append(navbar, drawer(), content { it.contentText })
        val readout = Readout("drawerOpened = false")
        layout.listen(AppLayoutEvents.drawerOpenedChanged) {
            readout.set("drawerOpened = ${layout.drawerOpened}")
        }
        basic.body.append(exampleBox(null, kitCol(layout, readout.root)))
        body.append(basic.root)

        val navbarSection = SectionBlock.NAME { show() }
        texts.bind(navbarSection.heading) { it.navbarHeading }
        texts.bind(navbarSection.hint) { it.navbarHint }
        val navbarLayout = boxedAppLayout("12rem")
        navbarLayout.dataset["test"] = "app-layout-navbar"
        val navLinks = createHorizontalLayout().apply {
            slot = "navbar"
            style.setProperty("flex", "1")
            style.setProperty("height", "2.25rem")
            style.setProperty("justify-content", "center")
            style.setProperty("gap", "0.5rem")
            append(
                link { it.dashboard },
                link { it.orders },
                link { it.customers },
                link { it.products },
            )
        }
        navbarLayout.append(navTitle { it.myApp }, navLinks, content { it.contentArea })
        navbarSection.body.append(exampleBox(null, navbarLayout))
        body.append(navbarSection.root)

        val placement = SectionBlock.NAME { show() }
        texts.bind(placement.heading) { it.placementHeading }
        texts.bind(placement.hint) { it.placementHint }
        val sideLayout = boxedAppLayout("20rem") { primarySection = AppLayoutPrimarySections.drawer }
        sideLayout.dataset["test"] = "app-layout-placement"
        val sideNavbar = AppNavbar.NAME()
        texts.bind(sideNavbar.captionNode) { it.dashboard }
        sideLayout.append(sideNavbar, drawer(), content { it.dashboardContent })
        val placementReadout = Readout("primarySection = drawer")
        val placementButton = createButton().addTheme("tertiary").apply {
            clickEvent.addHandler {
                val next =
                    if (sideLayout.primarySection == AppLayoutPrimarySections.drawer) {
                        AppLayoutPrimarySections.navbar
                    } else {
                        AppLayoutPrimarySections.drawer
                    }
                sideLayout.primarySection = next
                placementReadout.set("primarySection = ${next.unsafeCast<String>()}")
            }
        }
        texts.bind(placementButton) { it.togglePrimary }
        placement.body.append(exampleBox(null, kitCol(sideLayout, kitRow(placementButton, placementReadout.root))))
        body.append(placement.root)

        val scrolling = SectionBlock.NAME { show() }
        texts.bind(scrolling.heading) { it.scrollingHeading }
        texts.bind(scrolling.hint) { it.scrollingHint }
        val autoFrame = HtmlTagName.div().apply {
            style.setProperty("border", "1px solid var(--lumo-contrast-20pct)")
            style.setProperty("border-radius", "var(--lumo-border-radius-l)")
            style.setProperty("overflow", "hidden")
        }
        val autoLayout = boxedAppLayout()
        autoLayout.append(
            navTitle { it.autoHeight },
            appPeopleGrid(5, allRowsVisible = true),
        )
        autoFrame.append(autoLayout)

        val fullFrame = HtmlTagName.div().apply {
            style.setProperty("border", "1px solid var(--lumo-contrast-20pct)")
            style.setProperty("border-radius", "var(--lumo-border-radius-l)")
            style.setProperty("height", "18rem")
            style.setProperty("overflow", "hidden")
        }
        val fullLayout = boxedAppLayout("100%")
        val fullGrid = appPeopleGrid(20, allRowsVisible = false).apply {
            style.setProperty("height", "100%")
            addTheme("no-border")
        }
        fullLayout.append(
            navTitle { it.fixedHeight },
            fullGrid,
        )
        fullFrame.append(fullLayout)
        val autoCard = exampleBox("", autoFrame)
        texts.bind(autoCard.firstElementChild as HTMLParagraphElement) { it.autoHeight }
        val fullCard = exampleBox("", fullFrame)
        texts.bind(fullCard.firstElementChild as HTMLParagraphElement) { it.fixedHeight }
        scrolling.body.append(autoCard, fullCard)
        body.append(scrolling.root)

        val bottomNavbar = SectionBlock.NAME { show() }
        texts.bind(bottomNavbar.heading) { it.bottomHeading }
        texts.bind(bottomNavbar.hint) { it.bottomHint }
        val bottomLayout = boxedAppLayout("18rem")
        bottomLayout.dataset["test"] = "app-layout-bottom"
        bottomLayout.style.setProperty("--vaadin-app-layout-touch-optimized", "true")
        val bottomNav = createHorizontalLayout().apply {
            slot = "navbar touch-optimized"
            style.setProperty("width", "100%")
            style.setProperty("justify-content", "space-evenly")
            append(
                link("vaadin:dashboard") { it.dashboard },
                link("vaadin:cart") { it.orders },
                link("vaadin:user-heart") { it.customers },
                link("vaadin:package") { it.products },
            )
        }
        bottomLayout.append(
            navTitle { it.myApp },
            bottomNav,
            content { it.viewTitleContent },
        )
        bottomNavbar.body.append(exampleBox(null, bottomLayout))
        body.append(bottomNavbar.root)

        val secondary = SectionBlock.NAME { show() }
        texts.bind(secondary.heading) { it.secondaryHeading }
        texts.bind(secondary.hint) { it.secondaryHint }
        val secondaryLayout = boxedAppLayout("22rem") { primarySection = AppLayoutPrimarySections.drawer }
        secondaryLayout.dataset["test"] = "app-layout-secondary"
        val drawerTitle = heading(3, "")
        texts.bind(drawerTitle) { it.myApp }
        drawerTitle.slot = "drawer"
        drawerTitle.style.setProperty("margin", "var(--lumo-space-m)")
        val secondaryNavbar = createVerticalLayout().apply {
            slot = "navbar"
            style.setProperty("padding", "var(--lumo-space-s) var(--lumo-space-m)")
        }
        val orderHeading = heading(4, "")
        texts.bind(orderHeading) { it.orders }
        orderHeading.style.setProperty("margin", "0")
        secondaryNavbar.append(
            createHorizontalLayout().apply {
                style.setProperty("align-items", "center")
                append(
                    createDrawerToggle().apply { style.setProperty("margin-inline-end", "var(--lumo-space-s)") },
                    orderHeading,
                )
            },
            createHorizontalLayout().apply {
                style.setProperty("height", "2.25rem")
                style.setProperty("justify-content", "center")
                style.setProperty("gap", "0.5rem")
                append(
                    link { it.all },
                    link { it.open },
                    link { it.completed },
                    link { it.cancelled },
                )
            },
        )
        secondaryLayout.append(
            drawerTitle,
            drawer(),
            secondaryNavbar,
            content { it.orderList },
        )
        secondary.body.append(exampleBox(null, secondaryLayout))
        body.append(secondary.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        drawers.forEach { it.applyTranslation(lang) }
    }

    companion object : AppLayoutPageDefaultReference() {        val ENTRY: CatalogEntry =
            CatalogEntry(
                id = "app-layout",
                group = Groups.LAYOUT,
                meta =
                    PageMeta(
                        title = bi("App Layout", "App Layout"),
                        summary =
                            bi(
                                "应用级布局：navbar、drawer 与内容区，抽屉在窄屏上变成浮层。",
                                "Application-level layout: navbar, drawer and content area; the drawer becomes an " +
                                    "overlay on narrow screens.",
                            ),
                        docsUrl = "${Catalog.DOCS_BASE}/app-layout",
                        sourcePath = "pages/layout/AppLayoutPage.kt",
                    ),
                kitPage = { AppLayoutPage.NAME() },
            )


}

/**
 * `<vaadin-demo-app-navbar>`: the navbar of the app layout examples — drawer toggle plus the
 * application title. The host carries the slot, the nodes sit in its shadow root.
 */

/**
 * `<vaadin-demo-app-drawer>`: the drawer of the app layout examples, a scroller holding the main
 * side navigation.
 */

/**
 * `<vaadin-demo-nav-link>`: a demo navigation link that stays on the current hash route. The host is
 * a wrapper around the anchor, because an element cannot take over the `a` tag itself.
 */

/**
 * An app layout example that stays inside its card. The layout fixes its drawer to the viewport,
 * so paint containment is what keeps the drawer and the navbar inside the example instead of over
 * the page chrome.
 */
private fun boxedAppLayout(
    height: String? = null,
    configure: AppLayoutElement.() -> Unit = {},
): AppLayoutElement {
    val layout = createAppLayout(configure)
    if (height != null) layout.style.setProperty("height", height)
    layout.style.setProperty("contain", "paint")
    return layout
}

/** A grid of demo people used by the scrolling examples of the app layout page. */private fun appPeopleGrid(rows: Int, allRowsVisible: Boolean): GridElement {
    val grid = createGrid()
    grid.items = gridItems(people(rows))
    grid.allRowsVisible = allRowsVisible
    grid.append(
        createGridColumn { path = "firstName" },
        createGridColumn { path = "lastName" },
        createGridColumn { path = "email" },
        createGridColumn { path = "profession" },
    )
    return grid

}
}
