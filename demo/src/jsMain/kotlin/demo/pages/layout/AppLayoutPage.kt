package demo.pages.layout

import io.github.shijianjs.ktjs.demo.page.col as kitCol
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.row as kitRow
import io.github.shijianjs.ktjs.demo.page.sectionBlock

import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachShadowRoot

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.heading
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.data.gridItems
import demo.pages.fixtures.people
import demo.tr
import demo.ui.PageMeta
import demo.ui.configureFrame
import demo.ui.Readout
import demo.ui.listen
import kotlin.js.unsafeCast
import io.github.shijianjs.ktjs.vaadin.applayout.AppLayoutEvents
import io.github.shijianjs.ktjs.vaadin.applayout.AppLayoutPrimarySections
import io.github.shijianjs.ktjs.vaadin.applayout.createAppLayout
import io.github.shijianjs.ktjs.vaadin.applayout.createDrawerToggle
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.grid.GridElement
import io.github.shijianjs.ktjs.vaadin.grid.createGrid
import io.github.shijianjs.ktjs.vaadin.grid.createGridColumn
import io.github.shijianjs.ktjs.vaadin.horizontallayout.createHorizontalLayout
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.scroller.createScroller
import io.github.shijianjs.ktjs.vaadin.verticallayout.createVerticalLayout
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement
import web.html.HtmlTagName

val appLayoutEntry: CatalogEntry =
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
                docsUrl = "$DOCS_BASE/app-layout",
                sourcePath = "pages/layout/AppLayoutPage.kt",
            ),
        kitPage = { newAppLayoutPage() },
        rebuildsOnLanguage = true,
    )

internal class AppLayoutPage : DemoPage() {

    init {
        configureFrame(appLayoutEntry)
        registerVaadinIcons()

        val basic =
            sectionBlock(
                tr("基本用法", "Basic usage"),
                tr(
                    "应用由内容区、导航栏与抽屉组成：抽屉在宽屏上是固定栏，窄屏时变成由抽屉开关控制的浮层。",
                    "An application consists of a content area, a navbar and a drawer; the drawer is a fixed column " +
                        "on wide screens and a toggleable overlay on narrow ones.",
                ),
            )
        val layout = createAppLayout()
        layout.dataset["test"] = "app-layout"
        layout.style.setProperty("height", "22rem")
        layout.append(
            newAppNavbar(tr("我的应用", "MyApp")),
            newAppDrawer(),
            newAppContent(tr("内容区由默认插槽填充。", "The content area is filled through the default slot.")),
        )
        val readout = Readout("drawerOpened = false")
        layout.listen(AppLayoutEvents.drawerOpenedChanged) {
            readout.set("drawerOpened = ${layout.drawerOpened}")
        }
        basic.body.append(exampleBox(null, kitCol(layout, readout.root)))
        body.append(basic.root)

        val navbarSection =
            sectionBlock(
                tr("导航栏", "Navbar"),
                tr(
                    "navbar 是内容区上方的头部，可以放应用名、主工具栏或次级导航。",
                    "The navbar is the header above the content area; it can hold the application title, primary " +
                        "actions or secondary navigation.",
                ),
            )
        val navbarLayout = createAppLayout()
        navbarLayout.dataset["test"] = "app-layout-navbar"
        navbarLayout.style.setProperty("height", "12rem")
        val navTitle = heading(3, tr("我的应用", "MyApp")).apply {
            slot = "navbar"
            style.setProperty("margin", "0")
        }
        val navLinks = createHorizontalLayout().apply {
            slot = "navbar"
            style.setProperty("width", "100%")
            style.setProperty("height", "2.25rem")
            style.setProperty("justify-content", "center")
            style.setProperty("gap", "0.5rem")
            append(
                newNavLink(tr("仪表盘", "Dashboard")),
                newNavLink(tr("订单", "Orders")),
                newNavLink(tr("客户", "Customers")),
                newNavLink(tr("产品", "Products")),
            )
        }
        navbarLayout.append(navTitle, navLinks, newAppContent(tr("内容区", "Content area")))
        navbarSection.body.append(exampleBox(null, navbarLayout))
        body.append(navbarSection.root)

        val placement =
            sectionBlock(
                tr("导航栏位置", "Navbar placement"),
                tr(
                    "默认导航栏在顶部；把 primarySection 设为 drawer 后抽屉占满整个高度，导航栏移到右侧内容区" +
                        "上方，常用来放当前视图的标题与次级操作。",
                    "By default the navbar sits on top; with primarySection set to drawer the drawer takes the full " +
                        "height and the navbar moves above the content on the right, where it usually holds the " +
                        "title and actions of the current view.",
                ),
            )
        val sideLayout = createAppLayout { primarySection = AppLayoutPrimarySections.drawer }
        sideLayout.dataset["test"] = "app-layout-placement"
        sideLayout.style.setProperty("height", "20rem")
        sideLayout.append(
            newAppNavbar(tr("仪表盘", "Dashboard")),
            newAppDrawer(),
            newAppContent(tr("仪表盘内容", "Dashboard content")),
        )
        val placementReadout = Readout("primarySection = drawer")
        val placementButton =
            createButton { textContent = tr("切换 primarySection", "Toggle primarySection") }.addTheme("tertiary").apply {
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
        placement.body.append(exampleBox(null, kitCol(sideLayout, kitRow(placementButton, placementReadout.root))))
        body.append(placement.root)

        val scrolling =
            sectionBlock(
                tr("滚动行为", "Scrolling behavior"),
                tr(
                    "布局默认随内容增高，页面自己滚动；给布局设固定高度并让内容区撑满，长内容就在内容区内部滚动。",
                    "The layout grows with its content by default and the page scrolls. Give the layout a fixed " +
                        "height and let the content area fill it to scroll inside the content area instead.",
                ),
            )
        val autoFrame =
            HtmlTagName.div().apply {
                style.setProperty("border", "1px solid var(--lumo-contrast-20pct)")
                style.setProperty("border-radius", "var(--lumo-border-radius-l)")
                style.setProperty("overflow", "hidden")
            }
        val autoLayout = createAppLayout()
        autoLayout.append(
            heading(3, tr("自动高度", "Auto height")).apply {
                slot = "navbar"
                style.setProperty("margin", "0")
            },
            appPeopleGrid(5, allRowsVisible = true),
        )
        autoFrame.append(autoLayout)

        val fullFrame =
            HtmlTagName.div().apply {
                style.setProperty("border", "1px solid var(--lumo-contrast-20pct)")
                style.setProperty("border-radius", "var(--lumo-border-radius-l)")
                style.setProperty("height", "18rem")
                style.setProperty("overflow", "hidden")
            }
        val fullLayout = createAppLayout().apply { style.setProperty("height", "100%") }
        val fullGrid = appPeopleGrid(20, allRowsVisible = false).apply {
            style.setProperty("height", "100%")
            addTheme("no-border")
        }
        fullLayout.append(
            heading(3, tr("固定高度", "Full height")).apply {
                slot = "navbar"
                style.setProperty("margin", "0")
            },
            fullGrid,
        )
        fullFrame.append(fullLayout)
        scrolling.body.append(exampleBox(tr("自动高度", "Auto height"), autoFrame), exampleBox(tr("固定高度", "Full height"), fullFrame))
        body.append(scrolling.root)

        val bottomNavbar =
            sectionBlock(
                tr("小屏底部导航栏", "Bottom navbar on small touchscreens"),
                tr(
                    "打开 --vaadin-app-layout-touch-optimized 后，放在 navbar touch-optimized 插槽里的导航会显示在" +
                        "屏幕底部，适合移动端的少数几个主目的地。",
                    "With --vaadin-app-layout-touch-optimized enabled, the navbar touch-optimized slot is shown at " +
                        "the bottom of the screen, as fitted for a handful of primary destinations on mobile.",
                ),
            )
        val bottomLayout = createAppLayout()
        bottomLayout.dataset["test"] = "app-layout-bottom"
        bottomLayout.style.setProperty("height", "18rem")
        bottomLayout.style.setProperty("--vaadin-app-layout-touch-optimized", "true")
        val bottomNav = createHorizontalLayout().apply {
            slot = "navbar touch-optimized"
            style.setProperty("width", "100%")
            style.setProperty("justify-content", "space-evenly")
            append(
                newNavLink(tr("仪表盘", "Dashboard"), "vaadin:dashboard"),
                newNavLink(tr("订单", "Orders"), "vaadin:cart"),
                newNavLink(tr("客户", "Customers"), "vaadin:user-heart"),
                newNavLink(tr("产品", "Products"), "vaadin:package"),
            )
        }
        bottomLayout.append(
            heading(3, tr("我的应用", "MyApp")).apply {
                slot = "navbar"
                style.setProperty("margin", "0")
            },
            bottomNav,
            newAppContent(tr("视图标题与内容", "View title and content")),
        )
        bottomNavbar.body.append(exampleBox(null, bottomLayout))
        body.append(bottomNavbar.root)

        val secondary =
            sectionBlock(
                tr("次级导航", "Secondary navigation"),
                tr(
                    "抽屉放大类导航，navbar 里放当前视图的标题与只与该视图相关的次级导航。",
                    "The drawer holds the main navigation while the navbar carries the title of the current view " +
                        "and the secondary navigation that belongs to it.",
                ),
            )
        val secondaryLayout = createAppLayout { primarySection = AppLayoutPrimarySections.drawer }
        secondaryLayout.dataset["test"] = "app-layout-secondary"
        secondaryLayout.style.setProperty("height", "22rem")
        val drawerTitle = heading(3, tr("我的应用", "MyApp")).apply {
            slot = "drawer"
            style.setProperty("margin", "var(--lumo-space-m)")
        }
        val secondaryNavbar = createVerticalLayout().apply {
            slot = "navbar"
            style.setProperty("padding", "var(--lumo-space-s) var(--lumo-space-m)")
            append(
                createHorizontalLayout().apply {
                    style.setProperty("align-items", "center")
                    append(
                        createDrawerToggle().apply { style.setProperty("margin-inline-end", "var(--lumo-space-s)") },
                        heading(4, tr("订单", "Orders")).apply { style.setProperty("margin", "0") },
                    )
                },
                createHorizontalLayout().apply {
                    style.setProperty("height", "2.25rem")
                    style.setProperty("justify-content", "center")
                    style.setProperty("gap", "0.5rem")
                    append(
                        newNavLink(tr("全部", "All")),
                        newNavLink(tr("进行中", "Open")),
                        newNavLink(tr("已完成", "Completed")),
                        newNavLink(tr("已取消", "Cancelled")),
                    )
                },
            )
        }
        secondaryLayout.append(
            drawerTitle,
            newAppDrawer(),
            secondaryNavbar,
            newAppContent(tr("订单列表", "Order list")),
        )
        secondary.body.append(exampleBox(null, secondaryLayout))
        body.append(secondary.root)

    }

    companion object : CustomElementReference<AppLayoutPage>(AppLayoutPage::class.js) {
        val NAME: TagName<AppLayoutPage> = TagName("vaadin-demo-app-layout-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-app-layout-page>`. */
internal fun newAppLayoutPage(): AppLayoutPage {
    AppLayoutPage.define()
    return AppLayoutPage.NAME()
}

/**
 * `<vaadin-demo-app-navbar>`: the navbar of the app layout examples — drawer toggle plus the
 * application title. The host carries the slot, the nodes sit in its shadow root.
 */
internal class AppNavbar : CustomElementAdapter() {
    private val toggle = createDrawerToggle().apply { style.setProperty("color", "var(--lumo-body-text-color)") }
    private val caption = heading(3, "").apply { style.setProperty("margin", "0") }

    init {
        attachShadowRoot().append(toggle, caption)
    }

    /** Sets the application title next to the drawer toggle. */
    fun show(title: String) {
        caption.textContent = title
    }

    override fun connectedCallback() {
        slot = "navbar"
    }

    companion object : CustomElementReference<AppNavbar>(AppNavbar::class.js) {
        val NAME: TagName<AppNavbar> = TagName("vaadin-demo-app-navbar")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-app-navbar>`. */
internal fun newAppNavbar(title: String): AppNavbar {
    AppNavbar.define()
    return AppNavbar.NAME().also { it.show(title) }
}

/**
 * `<vaadin-demo-app-drawer>`: the drawer of the app layout examples, a scroller holding the main
 * side navigation.
 */
internal class AppDrawer : CustomElementAdapter() {
    init {
        attachShadowRoot().append(createScroller().apply { append(newAppSideNav()) })
    }

    override fun connectedCallback() {
        slot = "drawer"
    }

    companion object : CustomElementReference<AppDrawer>(AppDrawer::class.js) {
        val NAME: TagName<AppDrawer> = TagName("vaadin-demo-app-drawer")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-app-drawer>`. */
internal fun newAppDrawer(): AppDrawer {
    AppDrawer.define()
    return AppDrawer.NAME()
}

/** `<vaadin-demo-app-content>`: padding filler for the app layout examples' content area. */
internal class AppContent : CustomElementAdapter() {
    /** Sets the text the content area shows. */
    fun show(text: String) {
        textContent = text
    }

    override fun connectedCallback() {
        style.setProperty("display", "block")
        style.setProperty("padding", "var(--lumo-space-m)")
    }

    companion object : CustomElementReference<AppContent>(AppContent::class.js) {
        val NAME: TagName<AppContent> = TagName("vaadin-demo-app-content")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-app-content>`. */
internal fun newAppContent(text: String): AppContent {
    AppContent.define()
    return AppContent.NAME().also { it.show(text) }
}

/**
 * `<vaadin-demo-nav-link>`: a demo navigation link that stays on the current hash route. The host is
 * a wrapper around the anchor, because an element cannot take over the `a` tag itself.
 */
internal class NavLink : CustomElementAdapter() {
    private val anchor = HtmlTagName.a().apply {
        href = "#/app-layout"
        style.setProperty("display", "flex")
        style.setProperty("align-items", "center")
        style.setProperty("padding", "0 1rem")
        style.setProperty("font-weight", "500")
        style.setProperty("text-decoration", "none")
        style.setProperty("color", "inherit")
    }

    init {
        attachShadowRoot().append(anchor)
    }

    /** Sets the label; with an [icon] the label moves to `aria-label` and the icon stands in for it. */
    fun show(label: String, icon: String? = null) {
        anchor.textContent = ""
        if (icon == null) {
            anchor.textContent = label
        } else {
            anchor.setAttribute("aria-label", label)
            anchor.append(createIcon { this.icon = icon })
        }
    }

    companion object : CustomElementReference<NavLink>(NavLink::class.js) {
        val NAME: TagName<NavLink> = TagName("vaadin-demo-nav-link")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** Creates a defined `<vaadin-demo-nav-link>`. */
internal fun newNavLink(label: String, icon: String? = null): NavLink {
    NavLink.define()
    return NavLink.NAME().also { it.show(label, icon) }
}

/** A grid of demo people used by the scrolling examples of the app layout page. */
private fun appPeopleGrid(rows: Int, allRowsVisible: Boolean): GridElement {
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

