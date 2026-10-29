package demo.pages.layout

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.pages.data.gridItems
import demo.pages.fixtures.people
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.element
import demo.ui.example
import demo.ui.grid
import demo.ui.heading
import demo.ui.listen
import demo.ui.page
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import kotlin.js.unsafeCast
import vaadin.applayout.AppLayoutEvents
import vaadin.applayout.AppLayoutPrimarySections
import vaadin.applayout.createAppLayout
import vaadin.applayout.createDrawerToggle
import vaadin.button.createButton
import vaadin.grid.GridElement
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.horizontallayout.createHorizontalLayout
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.scroller.createScroller
import vaadin.verticallayout.createVerticalLayout
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
        builder = ::buildAppLayoutPage,
    )

private fun buildAppLayoutPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    val basic =
        section(
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
        appNavbar(tr("我的应用", "MyApp")),
        appDrawer(),
        appContent(tr("内容区由默认插槽填充。", "The content area is filled through the default slot.")),
    )
    val readout = Readout("drawerOpened = false")
    layout.listen(AppLayoutEvents.drawerOpenedChanged) {
        readout.set("drawerOpened = ${layout.drawerOpened}")
    }
    basic.body.append(example(null, column(layout, readout.root)))
    view.body.append(basic.root)

    val navbarSection =
        section(
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
            navLink(tr("仪表盘", "Dashboard")),
            navLink(tr("订单", "Orders")),
            navLink(tr("客户", "Customers")),
            navLink(tr("产品", "Products")),
        )
    }
    navbarLayout.append(navTitle, navLinks, appContent(tr("内容区", "Content area")))
    navbarSection.body.append(example(null, navbarLayout))
    view.body.append(navbarSection.root)

    val placement =
        section(
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
        appNavbar(tr("仪表盘", "Dashboard")),
        appDrawer(),
        appContent(tr("仪表盘内容", "Dashboard content")),
    )
    val placementReadout = Readout("primarySection = drawer")
    val placementButton =
        createButton { textContent = tr("切换 primarySection", "Toggle primarySection") }.withTheme("tertiary").apply {
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
    placement.body.append(example(null, column(sideLayout, row(placementButton, placementReadout.root))))
    view.body.append(placement.root)

    val scrolling =
        section(
            tr("滚动行为", "Scrolling behavior"),
            tr(
                "布局默认随内容增高，页面自己滚动；给布局设固定高度并让内容区撑满，长内容就在内容区内部滚动。",
                "The layout grows with its content by default and the page scrolls. Give the layout a fixed " +
                    "height and let the content area fill it to scroll inside the content area instead.",
            ),
        )
    val autoFrame =
        div().apply {
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
        div().apply {
            style.setProperty("border", "1px solid var(--lumo-contrast-20pct)")
            style.setProperty("border-radius", "var(--lumo-border-radius-l)")
            style.setProperty("height", "18rem")
            style.setProperty("overflow", "hidden")
        }
    val fullLayout = createAppLayout().apply { style.setProperty("height", "100%") }
    val fullGrid = appPeopleGrid(20, allRowsVisible = false).apply {
        style.setProperty("height", "100%")
        withTheme("no-border")
    }
    fullLayout.append(
        heading(3, tr("固定高度", "Full height")).apply {
            slot = "navbar"
            style.setProperty("margin", "0")
        },
        fullGrid,
    )
    fullFrame.append(fullLayout)
    scrolling.body.append(example(tr("自动高度", "Auto height"), autoFrame), example(tr("固定高度", "Full height"), fullFrame))
    view.body.append(scrolling.root)

    val bottomNavbar =
        section(
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
            navLink(tr("仪表盘", "Dashboard"), "vaadin:dashboard"),
            navLink(tr("订单", "Orders"), "vaadin:cart"),
            navLink(tr("客户", "Customers"), "vaadin:user-heart"),
            navLink(tr("产品", "Products"), "vaadin:package"),
        )
    }
    bottomLayout.append(
        heading(3, tr("我的应用", "MyApp")).apply {
            slot = "navbar"
            style.setProperty("margin", "0")
        },
        bottomNav,
        appContent(tr("视图标题与内容", "View title and content")),
    )
    bottomNavbar.body.append(example(null, bottomLayout))
    view.body.append(bottomNavbar.root)

    val secondary =
        section(
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
                    navLink(tr("全部", "All")),
                    navLink(tr("进行中", "Open")),
                    navLink(tr("已完成", "Completed")),
                    navLink(tr("已取消", "Cancelled")),
                )
            },
        )
    }
    secondaryLayout.append(
        drawerTitle,
        appDrawer(),
        secondaryNavbar,
        appContent(tr("订单列表", "Order list")),
    )
    secondary.body.append(example(null, secondaryLayout))
    view.body.append(secondary.root)

    return view
}

/** The navbar of the app layout examples: drawer toggle plus the application title. */
private fun appNavbar(title: String): HTMLElement =
    div().apply {
        slot = "navbar"
        append(
            createDrawerToggle().apply { style.setProperty("color", "var(--lumo-body-text-color)") },
            heading(3, title).apply { style.setProperty("margin", "0") },
        )
    }

/** The drawer of the app layout examples: a scroller holding the main side navigation. */
private fun appDrawer(): HTMLElement =
    createScroller().apply {
        slot = "drawer"
        append(appSideNav())
    }

/** Content area filler for the app layout examples. */
private fun appContent(text: String): HTMLElement =
    div().apply {
        style.setProperty("padding", "var(--lumo-space-m)")
        textContent = text
    }

/** A demo navigation link that stays on the current hash route. */
private fun navLink(label: String, icon: String? = null): HTMLElement =
    element(HtmlTagName.a).apply {
        href = "#/app-layout"
        style.setProperty("display", "flex")
        style.setProperty("align-items", "center")
        style.setProperty("padding", "0 1rem")
        style.setProperty("font-weight", "500")
        style.setProperty("text-decoration", "none")
        style.setProperty("color", "inherit")
        if (icon == null) {
            textContent = label
        } else {
            setAttribute("aria-label", label)
            append(createIcon { this.icon = icon })
        }
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
