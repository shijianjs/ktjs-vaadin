package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.element
import demo.ui.example
import demo.ui.heading
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.span
import demo.ui.withTheme
import js.objects.unsafeJso
import kotlin.js.unsafeCast
import vaadin.a11ybase.ListOrientations
import vaadin.accordion.AccordionEvents
import vaadin.accordion.createAccordion
import vaadin.accordion.createAccordionPanel
import vaadin.applayout.AppLayoutEvents
import vaadin.applayout.AppLayoutPrimarySections
import vaadin.applayout.createAppLayout
import vaadin.applayout.createDrawerToggle
import vaadin.breadcrumbs.createBreadcrumbs
import vaadin.breadcrumbs.createBreadcrumbsItem
import vaadin.button.createButton
import vaadin.card.createCard
import vaadin.details.DetailsEvents
import vaadin.details.createDetails
import vaadin.emailfield.createEmailField
import vaadin.formlayout.FormLayoutLabelsPositions
import vaadin.formlayout.FormLayoutResponsiveStep
import vaadin.formlayout.StringOrDouble
import vaadin.formlayout.createFormItem
import vaadin.formlayout.createFormLayout
import vaadin.formlayout.createFormRow
import vaadin.grid.GridElement
import vaadin.grid.createGrid
import vaadin.grid.createGridColumn
import vaadin.horizontallayout.createHorizontalLayout
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.markdown.createMarkdown
import vaadin.masterdetaillayout.MasterDetailLayoutOrientations
import vaadin.masterdetaillayout.createMasterDetailLayout
import vaadin.passwordfield.createPasswordField
import vaadin.scroller.ScrollerScrollDirections
import vaadin.scroller.createScroller
import vaadin.sidenav.SideNavElement
import vaadin.sidenav.SideNavItemElement
import vaadin.sidenav.createSideNav
import vaadin.sidenav.createSideNavItem
import vaadin.splitlayout.SplitLayoutOrientations
import vaadin.splitlayout.createSplitLayout
import vaadin.tabs.TabsEvents
import vaadin.tabs.createTab
import vaadin.tabs.createTabs
import vaadin.tabsheet.TabSheetEvents
import vaadin.tabsheet.createTabSheet
import vaadin.textfield.createTextField
import vaadin.verticallayout.createVerticalLayout
import web.dom.ElementId
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement
import web.html.HtmlTagName

val layoutEntries: List<CatalogEntry> =
    listOf(
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
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildAppLayoutPage,
        ),
        CatalogEntry(
            id = "horizontal-layout",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = bi("Horizontal Layout", "Horizontal Layout"),
                    summary =
                        bi(
                            "横向排列子元素的容器，通过 theme 控制间距、内边距与换行。",
                            "Container that arranges its children horizontally; theme controls spacing, padding " +
                                "and wrapping.",
                        ),
                    docsUrl = "$DOCS_BASE/horizontal-layout",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildHorizontalLayoutPage,
        ),
        CatalogEntry(
            id = "vertical-layout",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = bi("Vertical Layout", "Vertical Layout"),
                    summary = bi("纵向排列子元素的容器。", "Container that stacks its children vertically."),
                    docsUrl = "$DOCS_BASE/vertical-layout",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildVerticalLayoutPage,
        ),
        CatalogEntry(
            id = "form-layout",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = bi("Form Layout", "Form Layout"),
                    summary =
                        bi(
                            "响应式表单栅格：按可用宽度自动调整列数，标签可以放在字段旁边。",
                            "Responsive form grid: the column count follows the available width and labels can " +
                                "sit beside their fields.",
                        ),
                    docsUrl = "$DOCS_BASE/form-layout",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildFormLayoutPage,
        ),
        CatalogEntry(
            id = "split-layout",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = bi("Split Layout", "Split Layout"),
                    summary =
                        bi(
                            "可拖动的分栏容器，orientation 决定水平或垂直分割。",
                            "Resizable split container; orientation switches between a horizontal and a " +
                                "vertical split.",
                        ),
                    docsUrl = "$DOCS_BASE/split-layout",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildSplitLayoutPage,
        ),
        CatalogEntry(
            id = "scroller",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = bi("Scroller", "Scroller"),
                    summary =
                        bi(
                            "带样式的滚动容器，scrollDirection 控制滚动轴。",
                            "Styled scrolling container; scrollDirection picks the scroll axis.",
                        ),
                    docsUrl = "$DOCS_BASE/scroller",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildScrollerPage,
        ),
        CatalogEntry(
            id = "master-detail-layout",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = bi("Master Detail Layout", "Master Detail Layout"),
                    summary =
                        bi(
                            "主从布局：主区在默认插槽，详情在 detail 插槽，放不下时自动切换成浮层。",
                            "Master-detail layout: the master goes into the default slot and the detail into the " +
                                "detail slot; the detail switches to an overlay when it does not fit.",
                        ),
                    docsUrl = "$DOCS_BASE/master-detail-layout",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildMasterDetailPage,
        ),
        CatalogEntry(
            id = "card",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = bi("Card", "Card"),
                    summary =
                        bi(
                            "内容卡片：标题层级、elevated / outlined / stretch-media 变体。",
                            "Content card: title heading level plus the elevated / outlined / stretch-media " +
                                "variants.",
                        ),
                    docsUrl = "$DOCS_BASE/card",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildCardPage,
        ),
        CatalogEntry(
            id = "accordion",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = bi("Accordion", "Accordion"),
                    summary =
                        bi(
                            "可折叠面板组，支持多开与指定展开下标。",
                            "Group of collapsible panels; several can stay open and the open indexes can be set " +
                                "directly.",
                        ),
                    docsUrl = "$DOCS_BASE/accordion",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildAccordionPage,
        ),
        CatalogEntry(
            id = "details",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = bi("Details", "Details"),
                    summary =
                        bi(
                            "单个可折叠区块，summary 是标题，子节点是内容。",
                            "A single collapsible block: summary is the heading and the children are the content.",
                        ),
                    docsUrl = "$DOCS_BASE/details",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildDetailsPage,
        ),
        CatalogEntry(
            id = "tabs",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = bi("Tabs", "Tabs"),
                    summary =
                        bi(
                            "标签栏：selected 是下标，支持横向 / 纵向与 equal-width 变体。",
                            "Tab bar: selected is the index; horizontal / vertical and equal-width variants are " +
                                "supported.",
                        ),
                    docsUrl = "$DOCS_BASE/tabs",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildTabsPage,
        ),
        CatalogEntry(
            id = "tabsheet",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = bi("Tab Sheet", "Tab Sheet"),
                    summary =
                        bi(
                            "把 Tabs 与内容面板组合起来，按 selected 下标切换内容。",
                            "Combines Tabs with content panels and switches panels by the selected index.",
                        ),
                    docsUrl = "$DOCS_BASE/tabs",
                    docsNote = bi("Tab Sheet 与 Tabs 共用一页官方文档。", "Tab Sheet shares the Tabs documentation page."),
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildTabSheetPage,
        ),
        CatalogEntry(
            id = "side-nav",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = bi("Side Nav", "Side Nav"),
                    summary =
                        bi(
                            "导航菜单：path 生成链接，可折叠、可嵌套。",
                            "Navigation menu: path generates links; it can collapse and nest.",
                        ),
                    docsUrl = "$DOCS_BASE/side-nav",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildSideNavPage,
        ),
        CatalogEntry(
            id = "breadcrumbs",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = bi("Breadcrumbs", "Breadcrumbs"),
                    summary =
                        bi(
                            "面包屑导航：每项一个 path，当前项不可点击。",
                            "Breadcrumb navigation: each item takes a path and the current item is not clickable.",
                        ),
                    docsUrl = "$DOCS_BASE/breadcrumbs",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildBreadcrumbsPage,
        ),
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

/** The seven top-level destinations shared by the app layout examples. */
private fun appSideNav(): SideNavElement =
    createSideNav().apply {
        style.setProperty("margin", "var(--lumo-space-s)")
        append(
            appNavItem("vaadin:dashboard", tr("仪表盘", "Dashboard")),
            appNavItem("vaadin:cart", tr("订单", "Orders")),
            appNavItem("vaadin:user-heart", tr("客户", "Customers")),
            appNavItem("vaadin:package", tr("产品", "Products")),
            appNavItem("vaadin:records", tr("文档", "Documents")),
            appNavItem("vaadin:list", tr("任务", "Tasks")),
            appNavItem("vaadin:chart", tr("分析", "Analytics")),
        )
    }

/**
 * A side navigation item without `path`: the demo is a hash-routed single page, so items render as
 * plain text instead of links that would navigate away.
 */
private fun appNavItem(icon: String, label: String): SideNavItemElement =
    createSideNavItem().apply {
        append(createIcon { this.icon = icon }.apply { slot = "prefix" }, span(label))
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

private fun buildHorizontalLayoutPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "spacing 和 padding 是最常用的两个主题，可以叠加使用。",
                "spacing and padding are the most common theme variants and can be combined.",
            ),
        )
    basic.body.append(
        example(
            "theme=\"spacing padding\"",
            createHorizontalLayout().apply {
                withTheme("spacing", "padding")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
    )
    view.body.append(basic.root)

    val verticalAlignment =
        section(
            tr("纵向对齐", "Vertical alignment"),
            tr(
                "用 align-items 控制子项在纵向上的位置：stretch（默认）、start、center、end、baseline。",
                "align-items positions the children vertically: stretch (default), start, center, end or " +
                    "baseline.",
            ),
        )
    verticalAlignment.body.append(
        example(
            "align-items: center",
            createHorizontalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("height", "7rem")
                style.setProperty("align-items", "center")
                append(box(tr("项目 1", "Item 1"), "2.5rem"), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3"), "4rem"))
            },
        ),
        example(
            "align-items: end",
            createHorizontalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("height", "7rem")
                style.setProperty("align-items", "end")
                append(box(tr("项目 1", "Item 1"), "2.5rem"), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3"), "4rem"))
            },
        ),
    )
    view.body.append(verticalAlignment.root)

    val individual =
        section(
            tr("单项纵向对齐", "Aligning individual items"),
            tr(
                "子项的 align-self 覆盖容器的 align-items，只影响它自己。",
                "align-self on a child overrides the container's align-items for that item only.",
            ),
        )
    individual.body.append(
        example(
            null,
            createHorizontalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("height", "8rem")
                style.setProperty("align-items", "stretch")
                append(
                    box(tr("项目 1", "Item 1")).apply { style.setProperty("align-self", "start") },
                    box(tr("项目 2", "Item 2")),
                    box(tr("项目 3", "Item 3")).apply { style.setProperty("align-self", "end") },
                )
            },
        ),
    )
    view.body.append(individual.root)

    val horizontalAlignment =
        section(
            tr("水平对齐", "Horizontal alignment"),
            tr(
                "子项可以用 slot 分成 start / middle / end 三组，也可以用 justify-content 统一分布。两种方式" +
                    "不要同时使用。",
                "Children can be grouped into start / middle / end with slots, or distributed as a whole with " +
                    "justify-content. Do not use both at the same time.",
            ),
        )
    horizontalAlignment.body.append(
        example(
            "slot=\"middle\" / slot=\"end\"",
            createHorizontalLayout().apply {
                withTheme("spacing", "padding")
                append(
                    box(tr("开始", "Start")),
                    box(tr("中间", "Middle")).apply { slot = "middle" },
                    box(tr("末尾", "End")).apply { slot = "end" },
                )
            },
        ),
        example(
            "justify-content: center",
            createHorizontalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("justify-content", "center")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
    )
    view.body.append(horizontalAlignment.root)

    val spacing =
        section(
            tr("间距", "Spacing"),
            tr("spacing 在子项之间加入统一间隔。", "spacing adds a uniform gap between the children."),
        )
    spacing.body.append(
        example(
            tr("没有 spacing", "Without spacing"),
            createHorizontalLayout().apply {
                withTheme("padding")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
        example(
            tr("有 spacing", "With spacing"),
            createHorizontalLayout().apply {
                withTheme("spacing", "padding")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
    )
    view.body.append(spacing.root)

    val padding =
        section(
            tr("内边距", "Padding"),
            tr("padding 在容器内部四周留出空间。", "padding adds space inside the container."),
        )
    padding.body.append(
        example(
            tr("没有 padding", "Without padding"),
            createHorizontalLayout().apply {
                withTheme("spacing")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
        example(
            tr("有 padding", "With padding"),
            createHorizontalLayout().apply {
                withTheme("padding", "spacing")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
    )
    view.body.append(padding.root)

    val margin =
        section(
            tr("外边距", "Margin"),
            tr("margin 在容器外部留出空间，与相邻元素拉开距离。", "margin adds space around the container."),
        )
    margin.body.append(
        example(
            tr("没有 margin", "Without margin"),
            div().apply {
                style.setProperty("border", "1px dashed var(--lumo-contrast-30pct)")
                append(
                    createHorizontalLayout().apply {
                        withTheme("spacing", "padding")
                        append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
                    },
                )
            },
        ),
        example(
            tr("有 margin", "With margin"),
            div().apply {
                style.setProperty("border", "1px dashed var(--lumo-contrast-30pct)")
                append(
                    createHorizontalLayout().apply {
                        withTheme("margin", "spacing", "padding")
                        append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
                    },
                )
            },
        ),
    )
    view.body.append(margin.root)

    val wrapping =
        section(
            tr("换行", "Wrapping"),
            tr(
                "默认情况下空间不足时子项会收缩或溢出；wrap 让它们换到下一行。",
                "By default children shrink or overflow when space runs out; wrap lets them flow onto a new line.",
            ),
        )
    wrapping.body.append(
        example(
            tr("没有 wrap", "Without wrap"),
            createHorizontalLayout().apply {
                withTheme("spacing", "margin", "padding")
                style.setProperty("width", "22rem")
                append(*(1..5).map { box(tr("项目 $it", "Item $it")) }.toTypedArray())
            },
        ),
        example(
            tr("有 wrap", "With wrap"),
            createHorizontalLayout().apply {
                withTheme("wrap", "spacing", "margin", "padding")
                style.setProperty("width", "22rem")
                append(*(1..5).map { box(tr("项目 $it", "Item $it")) }.toTypedArray())
            },
        ),
    )
    view.body.append(wrapping.root)

    val wrappingSlots =
        section(
            tr("分组后换行", "Wrapping aligned items"),
            tr(
                "与水平对齐组合时，把应该一起换行的子项放进子布局，再给子布局加 slot。",
                "When combined with horizontal alignment, group the children that should wrap together into " +
                    "sub-layouts and give those the slot.",
            ),
        )
    wrappingSlots.body.append(
        example(
            null,
            createHorizontalLayout().apply {
                withTheme("spacing", "padding", "wrap")
                append(
                    createHorizontalLayout().apply {
                        withTheme("spacing", "padding")
                        append(box(tr("开始", "Start")), box(tr("开始", "Start")))
                    },
                    createHorizontalLayout().apply {
                        slot = "middle"
                        withTheme("spacing", "padding")
                        append(box(tr("中间", "Middle")), box(tr("中间", "Middle")), box(tr("中间", "Middle")))
                    },
                    createHorizontalLayout().apply {
                        slot = "end"
                        withTheme("spacing", "padding")
                        append(box(tr("末尾", "End")), box(tr("末尾", "End")))
                    },
                )
            },
        ),
    )
    view.body.append(wrappingSlots.root)

    val expanding =
        section(
            tr("扩展子项", "Expanding items"),
            tr(
                "给子项设置 flex-grow 让它占据多余空间；多个子项按比例分配。",
                "flex-grow makes a child take the excess space; several children share it proportionally.",
            ),
        )
    expanding.body.append(
        example(
            "flex-grow: 1",
            createHorizontalLayout().apply {
                withTheme("padding", "spacing")
                append(
                    box(tr("项目 1", "Item 1")).apply { style.setProperty("flex-grow", "1") },
                    box(tr("项目 2", "Item 2")),
                    box(tr("项目 3", "Item 3")),
                )
            },
        ),
    )
    view.body.append(expanding.root)

    return view
}

private fun buildVerticalLayoutPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "与 Horizontal Layout 相同的主题，方向改为纵向；spacing 变成行间距。",
                "Same theme variants as Horizontal Layout, laid out vertically; spacing becomes the row gap.",
            ),
        )
    basic.body.append(
        example(
            "theme=\"spacing padding\"",
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                append(
                    box(tr("项目 1", "Item 1")),
                    box(tr("项目 2", "Item 2")),
                    box(tr("项目 3", "Item 3")),
                )
            },
        ),
    )
    view.body.append(basic.root)

    val verticalAlignment =
        section(
            tr("纵向对齐", "Vertical alignment"),
            tr(
                "布局比内容高时，justify-content 控制子项在纵向上的分布。",
                "When the layout is taller than its content, justify-content distributes the children vertically.",
            ),
        )
    verticalAlignment.body.append(
        example(
            "justify-content: center",
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("height", "12rem")
                style.setProperty("justify-content", "center")
                append(
                    box(tr("项目 1", "Item 1")),
                    box(tr("项目 2", "Item 2")),
                    box(tr("项目 3", "Item 3")),
                )
            },
        ),
    )
    view.body.append(verticalAlignment.root)

    val horizontalAlignment =
        section(
            tr("水平对齐", "Horizontal alignment"),
            tr(
                "align-items 控制子项在横向上的位置，例如整体居中。",
                "align-items positions the children horizontally, for example to center them.",
            ),
        )
    horizontalAlignment.body.append(
        example(
            "align-items: center",
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("align-items", "center")
                append(
                    box(tr("项目 1", "Item 1")),
                    box(tr("项目 2", "Item 2")),
                    box(tr("项目 3", "Item 3")),
                )
            },
        ),
    )
    view.body.append(horizontalAlignment.root)

    val individual =
        section(
            tr("单项对齐", "Aligning individual items"),
            tr(
                "子项的 align-self 可以覆盖容器的 align-items，只影响它自己。",
                "align-self on a child overrides the container's align-items for that item only.",
            ),
        )
    individual.body.append(
        example(
            null,
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("align-items", "start")
                append(
                    box(tr("项目 1", "Item 1")).apply { style.setProperty("align-self", "end") },
                    box(tr("项目 2", "Item 2")).apply { style.setProperty("align-self", "center") },
                    box(tr("项目 3", "Item 3")),
                )
            },
        ),
    )
    view.body.append(individual.root)

    val spacing =
        section(
            tr("间距", "Spacing"),
            tr("spacing 在子项之间加入统一间隔。", "spacing adds a uniform gap between the children."),
        )
    spacing.body.append(
        example(
            tr("没有 spacing", "Without spacing"),
            createVerticalLayout().apply {
                withTheme("padding")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
        example(
            tr("有 spacing", "With spacing"),
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
    )
    view.body.append(spacing.root)

    val padding =
        section(
            tr("内边距", "Padding"),
            tr("padding 在容器内部四周留出空间。", "padding adds space inside the container."),
        )
    padding.body.append(
        example(
            tr("没有 padding", "Without padding"),
            createVerticalLayout().apply {
                withTheme("spacing")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
        example(
            tr("有 padding", "With padding"),
            createVerticalLayout().apply {
                withTheme("padding", "spacing")
                append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
            },
        ),
    )
    view.body.append(padding.root)

    val margin =
        section(
            tr("外边距", "Margin"),
            tr("margin 在容器外部留出空间，与相邻元素拉开距离。", "margin adds space around the container."),
        )
    margin.body.append(
        example(
            tr("没有 margin", "Without margin"),
            div().apply {
                style.setProperty("border", "1px dashed var(--lumo-contrast-30pct)")
                append(
                    createVerticalLayout().apply {
                        withTheme("spacing", "padding")
                        append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
                    },
                )
            },
        ),
        example(
            tr("有 margin", "With margin"),
            div().apply {
                style.setProperty("border", "1px dashed var(--lumo-contrast-30pct)")
                append(
                    createVerticalLayout().apply {
                        withTheme("margin", "spacing", "padding")
                        append(box(tr("项目 1", "Item 1")), box(tr("项目 2", "Item 2")), box(tr("项目 3", "Item 3")))
                    },
                )
            },
        ),
    )
    view.body.append(margin.root)

    val wrapping =
        section(
            tr("换行", "Wrapping"),
            tr(
                "默认空间不足时子项会收缩或溢出；wrap 让它们换到下一列。",
                "By default children shrink or overflow when space runs out; wrap lets them flow into a new column.",
            ),
        )
    wrapping.body.append(
        example(
            tr("没有 wrap", "Without wrap"),
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                style.setProperty("height", "10rem")
                style.setProperty("align-items", "stretch")
                append(*(1..4).map { box(tr("项目 $it", "Item $it")) }.toTypedArray())
            },
        ),
        example(
            tr("有 wrap", "With wrap"),
            createVerticalLayout().apply {
                withTheme("wrap", "spacing", "padding")
                style.setProperty("height", "10rem")
                style.setProperty("align-items", "stretch")
                append(*(1..4).map { box(tr("项目 $it", "Item $it")) }.toTypedArray())
            },
        ),
    )
    view.body.append(wrapping.root)

    return view
}

private fun buildFormLayoutPage(meta: PageMeta): PageView {
    val view = page(meta)

    val autoSection =
        section(
            tr("自动响应式模式", "Auto-responsive mode"),
            tr(
                "autoResponsive 按可用宽度自动创建列，列宽由 columnWidth 指定（默认 12em）。每个字段默认独占" +
                    "一行，用 form-row 可以把字段并到同一行。",
                "autoResponsive creates columns from the available width, each as wide as columnWidth (12em by " +
                    "default). Every field starts a new row unless fields are grouped with form-row.",
            ),
        )
    val basicForm = createFormLayout { autoResponsive = true }
    basicForm.dataset["test"] = "form-layout"
    basicForm.append(
        formRow(textField(tr("名", "First name")), textField(tr("姓", "Last name"))),
        formRow(emailField(tr("邮箱地址", "Email address"))),
        formRow(passwordField(tr("密码", "Password")), passwordField(tr("确认密码", "Confirm password"))),
    )
    autoSection.body.append(example(null, formFrame(basicForm)))
    view.body.append(autoSection.root)

    val labelsSection =
        section(
            tr("标签位置", "Label position"),
            tr(
                "labelsAside 让 form-item 优先把标签放在字段左侧；宽度不够时自动改回字段上方。",
                "labelsAside makes form-item prefer placing its label beside the field and fall back above it " +
                    "when there is not enough room.",
            ),
        )
    val asideForm = createFormLayout {
        autoResponsive = true
        labelsAside = true
    }
    asideForm.dataset["test"] = "form-labels-aside"
    asideForm.append(
        formItem(tr("名", "First name"), createTextField()),
        formItem(tr("姓", "Last name"), createTextField()),
        formItem(tr("邮箱地址", "Email address"), createEmailField()),
    )
    labelsSection.body.append(example(null, formFrame(asideForm)))
    view.body.append(labelsSection.root)

    val columnsSection =
        section(
            tr("列宽与列数", "Column width and count"),
            tr(
                "columnWidth 决定一列多宽，expandColumns 让列平分剩余宽度，expandFields 让字段撑满所在列" +
                    "（包括 form-item 里的字段）。",
                "columnWidth sets the width of a column, expandColumns lets columns share the remaining width " +
                    "and expandFields stretches the fields, including those inside form-item.",
            ),
        )
    val expandColumnsForm = createFormLayout {
        autoResponsive = true
        columnWidth = "8em"
        expandColumns = true
    }
    expandColumnsForm.append(
        formRow(textField(tr("名", "First name")), textField(tr("姓", "Last name"))),
        formRow(emailField(tr("邮箱地址", "Email address"))),
        formRow(passwordField(tr("密码", "Password")), passwordField(tr("确认密码", "Confirm password"))),
    )
    val expandFieldsForm = createFormLayout {
        autoResponsive = true
        columnWidth = "8em"
        expandColumns = true
        expandFields = true
    }
    expandFieldsForm.append(
        formRow(textField(tr("名", "First name")), textField(tr("姓", "Last name"))),
        formRow(emailField(tr("邮箱地址", "Email address"))),
        formRow(passwordField(tr("密码", "Password")), passwordField(tr("确认密码", "Confirm password"))),
    )
    columnsSection.body.append(
        example("expand-columns", formFrame(expandColumnsForm)),
        example("expand-columns expand-fields", formFrame(expandFieldsForm)),
    )
    view.body.append(columnsSection.root)

    val colspan =
        section(
            tr("跨列", "Column span"),
            tr(
                "字段上的 data-colspan 让它占据多列；只能用在 form-row 里，并且需要 autoResponsive。",
                "data-colspan on a field makes it span several columns; it only applies inside form-row with " +
                    "autoResponsive enabled.",
            ),
        )
    val spanForm = createFormLayout {
        autoResponsive = true
        columnWidth = "8em"
        expandFields = true
    }
    spanForm.dataset["test"] = "form-colspan"
    spanForm.append(
        formRow(textField(tr("街道地址", "Street address")).apply { setAttribute("data-colspan", "3") }),
        formRow(
            textField(tr("邮政编码", "Postal code")),
            textField(tr("城市", "City/Town")).apply { setAttribute("data-colspan", "2") },
        ),
        formRow(textField(tr("国家", "Country")).apply { setAttribute("data-colspan", "2") }),
    )
    colspan.body.append(example(null, formFrame(spanForm)))
    view.body.append(colspan.root)

    val stepsSection =
        section(
            tr("响应式断点", "Responsive steps"),
            tr(
                "不用 autoResponsive 时用 responsiveSteps 描述断点与列数；首项通常从 0 开始。",
                "Without autoResponsive, responsiveSteps describes the breakpoints and column counts; the " +
                    "first step usually starts at 0.",
            ),
        )
    val stepsForm = createFormLayout()
    stepsForm.responsiveSteps =
        arrayOf(
            unsafeJso<FormLayoutResponsiveStep> {
                minWidth = StringOrDouble(0.0)
                columns = 1.0
                labelsPosition = FormLayoutLabelsPositions.top
            },
            unsafeJso<FormLayoutResponsiveStep> {
                minWidth = StringOrDouble("320px")
                columns = 2.0
            },
            unsafeJso<FormLayoutResponsiveStep> {
                minWidth = StringOrDouble("500px")
                columns = 3.0
            },
        )
    stepsForm.append(
        textField(tr("名", "First name")),
        textField(tr("姓", "Last name")),
        emailField(tr("邮箱地址", "Email address")),
    )

    val stepsAsideForm = createFormLayout()
    stepsAsideForm.style.setProperty("--vaadin-form-layout-label-width", "92px")
    stepsAsideForm.append(
        formItem(tr("名", "First name"), createTextField()),
        formItem(tr("姓", "Last name"), createTextField()),
        formItem(tr("邮箱地址", "Email address"), createEmailField()),
    )
    stepsSection.body.append(
        example("responsiveSteps", formFrame(stepsForm)),
        example(tr("form-item 默认把标签放在左侧", "form-item puts the label aside by default"), formFrame(stepsAsideForm)),
    )
    view.body.append(stepsSection.root)

    return view
}

private fun buildSplitLayoutPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "两个子元素之间有一条可拖动的分隔条；不指定宽度时两者平分。",
                "A draggable divider sits between the two children; they share the space evenly unless a width " +
                    "is set.",
            ),
        )
    val horizontal = createSplitLayout()
    horizontal.style.setProperty("max-height", "17rem")
    horizontal.append(splitMaster(), splitDetail())
    basic.body.append(example(null, horizontal))
    view.body.append(basic.root)

    val position =
        section(
            tr("分隔条位置", "Splitter position"),
            tr(
                "给两个子元素设置宽度或 flex，就能决定初始的分隔位置。",
                "Set a width or flex on the children to control where the splitter starts.",
            ),
        )
    val positioned = createSplitLayout()
    positioned.style.setProperty("max-height", "17rem")
    positioned.append(
        splitMaster().apply { style.setProperty("width", "70%") },
        splitDetail().apply { style.setProperty("width", "30%") },
    )
    position.body.append(example(null, positioned))
    view.body.append(position.root)

    val minMax =
        section(
            tr("最小与最大尺寸", "Minimum and maximum size"),
            tr(
                "min-width / max-width 限制拖动能到达的范围。",
                "min-width / max-width limit how far the splitter can be dragged.",
            ),
        )
    val bounded = createSplitLayout()
    bounded.style.setProperty("max-height", "17rem")
    bounded.append(
        splitMaster().apply {
            style.setProperty("min-width", "12rem")
            style.setProperty("max-width", "24rem")
        },
        splitDetail(),
    )
    minMax.body.append(example(null, bounded))
    view.body.append(minMax.root)

    val orientation =
        section(
            tr("方向", "Orientation"),
            tr("orientation 设为 vertical 后分隔条水平放置，两侧变成上下两个区域。", "With orientation set to vertical the divider is horizontal and the two areas stack."),
        )
    val vertical = createSplitLayout { this.orientation = SplitLayoutOrientations.vertical }
    vertical.style.setProperty("max-height", "22rem")
    vertical.append(splitMaster(), splitDetail())
    orientation.body.append(example(null, vertical))
    view.body.append(orientation.root)

    val themes =
        section(
            tr("主题", "Theme variants"),
            tr(
                "minimal 隐藏分隔条的底色，small 缩窄分隔条。",
                "minimal hides the divider background and small narrows the divider.",
            ),
        )
    val minimal = createSplitLayout().apply {
        withTheme("minimal")
        style.setProperty("max-height", "17rem")
        append(splitMaster(), splitDetail())
    }
    val small = createSplitLayout().apply {
        withTheme("small")
        style.setProperty("max-height", "17rem")
        append(splitMaster(), splitDetail())
    }
    themes.body.append(example("minimal", minimal), example("small", small))
    view.body.append(themes.root)

    val toggle =
        section(
            tr("折叠侧栏", "Collapsing a sidebar"),
            tr(
                "把按钮放进侧栏，点击时只改侧栏与详情区的宽度，模拟侧栏的展开与折叠。",
                "Put a button into the sidebar and change the widths of the sidebar and the detail area when " +
                    "it is clicked, simulating an expanding and collapsing sidebar.",
            ),
        )
    val layout = createSplitLayout()
    layout.dataset["test"] = "split-collapse"
    layout.style.setProperty("max-height", "17rem")
    val sidebar = div().apply {
        dataset["test"] = "split-sidebar"
        style.setProperty("overflow", "hidden")
        style.setProperty("width", "40%")
    }
    val detail = splitDetail()
    val collapseButton = createButton().apply {
        dataset["test"] = "split-collapse-button"
        withTheme("icon", "tertiary")
        setAttribute("aria-label", tr("展开或折叠侧栏", "Expand/collapse sidebar"))
        style.setProperty("float", "right")
    }
    var collapsed = false
    fun applyCollapsed() {
        val sidebarWidth = if (collapsed) 13 else 40
        sidebar.style.setProperty("width", "$sidebarWidth%")
        detail.style.setProperty("width", "${100 - sidebarWidth}%")
        collapseButton.replaceChildren(createIcon { icon = if (collapsed) "vaadin:arrow-right" else "vaadin:arrow-left" })
    }
    collapseButton.apply {
        clickEvent.addHandler {
            collapsed = !collapsed
            applyCollapsed()
        }
    }
    applyCollapsed()
    sidebar.append(collapseButton, splitMaster().apply { style.setProperty("clear", "right") })
    layout.append(sidebar, detail)
    toggle.body.append(example(null, layout))
    view.body.append(toggle.root)

    return view
}

private fun buildScrollerPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "内容超出固定高度时出现滚动条；overflow-indicators 在顶部与底部显示渐隐指示。",
                "A scrollbar appears when the content exceeds the fixed height; overflow-indicators fades the " +
                    "top and bottom edges.",
            ),
        )
    val scroller = createScroller().withTheme("overflow-indicators")
    scroller.dataset["test"] = "scroller"
    scroller.style.setProperty("max-height", "18rem")
    val markdown = createMarkdown()
    markdown.content = tr(SCROLLER_EVENT_ZH, SCROLLER_EVENT_EN)
    scroller.append(markdown)
    val card = createCard { cardTitle = tr("2025 夏季技术大会", "Summer Tech Conference 2025") }
    card.style.setProperty("max-width", "25rem")
    card.append(
        scroller,
        createButton { textContent = tr("加入日历", "Add to calendar") }.apply { slot = "footer" },
    )
    basic.body.append(example(null, card))
    view.body.append(basic.root)

    val both =
        section(
            tr("双向滚动", "Scrolling in both directions"),
            tr(
                "内容比容器更宽更高时，两个方向都会出现滚动条。",
                "When the content is both wider and taller than the container, both scrollbars appear.",
            ),
        )
    val bothScroller = createScroller()
    bothScroller.dataset["test"] = "scroller-both"
    bothScroller.style.setProperty("height", "18rem")
    bothScroller.style.setProperty("width", "100%")
    val illustration = element(HtmlTagName.img)
    illustration.src = scrollerIllustration()
    illustration.alt = tr("示例插图", "Sample illustration")
    illustration.style.setProperty("display", "block")
    bothScroller.append(illustration)
    both.body.append(example(null, bothScroller))
    view.body.append(both.root)

    val mobile =
        section(
            tr("移动端横向滚动", "Horizontal scrolling on mobile"),
            tr(
                "scrollDirection 设为 horizontal 后只允许横向滚动，适合把一组按钮放在一行里横向滑动。",
                "With scrollDirection set to horizontal only horizontal scrolling is allowed, useful for a row " +
                    "of buttons that slides sideways.",
            ),
        )
    val phone = div().apply {
        style.setProperty("border", "1px solid var(--lumo-contrast-20pct)")
        style.setProperty("border-radius", "var(--lumo-border-radius-l)")
        style.setProperty("max-width", "100%")
        style.setProperty("width", "22.5rem")
    }
    phone.append(
        heading(3, tr("新建…", "Create new...")).apply {
            style.setProperty("padding", "1rem 1rem 0")
            style.setProperty("font-size", "1.375rem")
        },
    )
    val horizontalScroller = createScroller { scrollDirection = ScrollerScrollDirections.horizontal }
    horizontalScroller.append(
        createHorizontalLayout().apply {
            style.setProperty("display", "inline-flex")
            withTheme("padding", "spacing")
            append(
                scrollerAction(tr("审计", "Audit"), "vaadin:clipboard-check"),
                scrollerAction(tr("报表", "Report"), "vaadin:book-dollar"),
                scrollerAction(tr("仪表盘", "Dashboard"), "vaadin:line-chart"),
                scrollerAction(tr("发票", "Invoice"), "vaadin:invoice"),
            )
        },
    )
    phone.append(horizontalScroller)
    mobile.body.append(example(null, phone))
    view.body.append(mobile.root)

    return view
}

private const val SCROLLER_EVENT_ZH =
    """
    **日期与时间**

    2026 年 6 月 20 日（周六）
    上午 9:00 – 下午 5:00

    **关于这场活动**

    一整天的演讲、动手工作坊与交流环节：无论你是资深开发者还是刚入门，都能找到适合自己的内容。

    **日程**

    - **9:00** – 签到与早餐
    - **10:00** – 开场主题演讲
    - **11:30** – 分会场
    - **13:00** – 午餐与自由交流
    - **14:30** – 下午工作坊
    - **16:30** – 闭幕与抽奖

    **需要携带**

    - 签到时出示证件
    - 笔记本电脑（可选，工作坊用）
    - 名片，方便交流

    **停车**

    B 停车场免费，路边车位有限。

    **联系我们**

    有问题？发邮件到 events@techconf.example。
    """

private const val SCROLLER_EVENT_EN =
    """
    **Date & Time**

    Saturday, June 20, 2026
    9:00 AM – 5:00 PM

    **About This Event**

    Join us for a full day of inspiring talks, hands-on workshops and networking. Whether you are a seasoned
    developer or just starting out, there is something for everyone.

    **Schedule**

    - **9:00 AM** – Registration & Breakfast
    - **10:00 AM** – Opening Keynote
    - **11:30 AM** – Breakout Sessions
    - **1:00 PM** – Lunch & Networking
    - **2:30 PM** – Afternoon Workshops
    - **4:30 PM** – Closing Remarks & Raffle

    **What to Bring**

    - Photo ID for check-in
    - Laptop (optional, for workshops)
    - Business cards for networking

    **Parking**

    Free parking in Lot B. Street parking is limited.

    **Contact**

    Questions? Email us at events@techconf.example.
    """

/** A wide, tall illustration so the scroller needs both scrollbars. */
private fun scrollerIllustration(): String =
    svgDataUri(
        "<svg xmlns='http://www.w3.org/2000/svg' width='1200' height='720'>" +
            "<rect width='1200' height='720' fill='#e8f1fb'/>" +
            "<circle cx='940' cy='150' r='90' fill='#ffd166'/>" +
            "<path d='M0 500 L280 280 L540 500 L780 240 L1200 540 L1200 720 L0 720 Z' fill='#90b4d6'/>" +
            "<path d='M0 590 L320 420 L660 590 L960 400 L1200 580 L1200 720 L0 720 Z' fill='#5d8fb8'/>" +
            "<path d='M0 660 L380 520 L760 660 L1200 540 L1200 720 L0 720 Z' fill='#3c6d94'/>" +
            "</svg>",
    )

/** Action button of the horizontal mobile scroller example. */
private fun scrollerAction(label: String, icon: String): HTMLElement {
    val button = createButton { textContent = label }
    button.style.setProperty("height", "6.25rem")
    button.append(createIcon { this.icon = icon }.apply { slot = "prefix" })
    return button
}

private fun buildMasterDetailPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "主区在默认插槽，详情放进 slot=\"detail\"；空间不足时详情会变成浮层。",
                "The master uses the default slot and the detail goes into slot=\"detail\"; the detail " +
                    "becomes an overlay when space runs out.",
            ),
        )
    val layout = createMasterDetailLayout()
    layout.dataset["test"] = "master-detail-layout"
    layout.style.setProperty("height", "16rem")
    layout.masterSize = "16rem"
    layout.detailSize = "1fr"

    val master = column(paragraph(tr("主区内容", "Master content")))
    val detail = div().apply {
        slot = "detail"
        style.setProperty("padding", "var(--lumo-space-m)")
        textContent = tr("详情内容", "Detail content")
    }
    layout.append(master, detail)
    basic.body.append(example(null, layout))
    view.body.append(basic.root)

    val options =
        section(
            tr("方向与浮层", "Orientation and overlay"),
            tr(
                "orientation 切换 master 与 detail 的排列方向，forceOverlay 强制详情以浮层显示。",
                "orientation switches how master and detail are ordered, and forceOverlay always shows the " +
                    "detail as an overlay.",
            ),
        )
    val readout = Readout("orientation = horizontal")
    val overlaySwitch = vaadin.switch.createSwitch { label = "forceOverlay" }
    val orientationButton =
        createButton { textContent = tr("切换方向", "Toggle orientation") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                layout.orientation =
                    if (layout.orientation == MasterDetailLayoutOrientations.horizontal) {
                        MasterDetailLayoutOrientations.vertical
                    } else {
                        MasterDetailLayoutOrientations.horizontal
                    }
                readout.set("orientation = ${layout.orientation.unsafeCast<String>()}")
                layout.recalculateLayout()
            }
        }
    overlaySwitch.listen(vaadin.switch.SwitchEvents.checkedChanged) {
        layout.forceOverlay = overlaySwitch.checked
        layout.recalculateLayout()
    }
    options.body.append(example(null, row(orientationButton, overlaySwitch, readout.root)))
    view.body.append(options.root)

    return view
}

private fun buildCardPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "cardTitle 生成标题，titleHeadingLevel 决定标题层级，子节点是正文。",
                "cardTitle generates the heading, titleHeadingLevel sets its level and the children are the body.",
            ),
        )
    val card = createCard { cardTitle = tr("季度总结", "Quarterly summary") }
    card.append(
        paragraph(
            tr(
                "卡片内容可以是任意节点：文本、按钮、甚至其他组件。",
                "The card body can hold any nodes: text, buttons, even other components.",
            ),
        ),
    )
    basic.body.append(example(null, card))
    view.body.append(basic.root)

    val variants =
        section(
            tr("主题变体", "Theme variants"),
            tr(
                "elevated 加阴影，outlined 加边框，两者可以对照使用。",
                "elevated adds a shadow and outlined adds a border; they are shown side by side.",
            ),
        )
    variants.body.append(
        example(
            null,
            row(
                createCard { }.apply {
                    withTheme("elevated")
                    append(heading(3, "elevated"), paragraph(tr("带阴影的卡片", "Card with a shadow")))
                },
                createCard { }.apply {
                    withTheme("outlined")
                    append(heading(3, "outlined"), paragraph(tr("带边框的卡片", "Card with a border")))
                },
            ),
        ),
    )
    view.body.append(variants.root)

    val levels = section(tr("标题层级", "Heading levels"), "")
    levels.body.append(
        example(
            null,
            row(
                createCard {
                    cardTitle = tr("二级标题", "Level 2 heading")
                    titleHeadingLevel = 2.0
                }.apply { append(paragraph("titleHeadingLevel = 2")) },
                createCard {
                    cardTitle = tr("四级标题", "Level 4 heading")
                    titleHeadingLevel = 4.0
                }.apply { append(paragraph("titleHeadingLevel = 4")) },
            ),
        ),
    )
    view.body.append(levels.root)

    return view
}

private fun buildAccordionPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "每个 panel 用 summary 定义标题，子节点是展开后的内容。",
                "Each panel defines its header through summary and the children are the expanded content.",
            ),
        )
    val log = EventLog(4)
    val accordion = createAccordion { headingLevel = 3.0 }
    accordion.dataset["test"] = "accordion"
    accordion.append(
        accordionPanel(tr("配送与安装", "Delivery and setup")).apply {
            append(paragraph(tr("面板一的内容。", "Content of the first panel.")))
        },
        accordionPanel(tr("付款方式", "Payment")).apply {
            append(paragraph(tr("面板二的内容。", "Content of the second panel.")))
        },
        accordionPanel(tr("售后服务", "After-sales support")).apply {
            append(paragraph(tr("面板三的内容。", "Content of the third panel.")))
        },
    )
    accordion.listen(AccordionEvents.openedChanged) {
        log.add("opened-changed · opened=${accordion.opened}")
    }
    basic.body.append(example(null, column(accordion, log.root)))
    view.body.append(basic.root)

    val programmatic =
        section(
            tr("程序化展开", "Opening panels programmatically"),
            tr(
                "opened 是要展开的面板下标（Number），可以一次打开多个。",
                "opened holds the index (Number) of the panel to expand; several can be open at once.",
            ),
        )
    val readout = Readout("opened = -")
    val controlled = createAccordion()
    controlled.append(
        accordionPanel(tr("第一项", "First item")).apply { append(paragraph(tr("内容一", "Content one"))) },
        accordionPanel(tr("第二项", "Second item")).apply { append(paragraph(tr("内容二", "Content two"))) },
    )
    controlled.opened = 1.0
    controlled.listen(AccordionEvents.openedChanged) {
        readout.set("opened = ${controlled.opened}")
    }
    val openFirst =
        createButton { textContent = tr("展开第一项", "Open the first panel") }.withTheme("tertiary").apply {
            clickEvent.addHandler { controlled.opened = 0.0 }
        }
    val openBoth =
        createButton { textContent = tr("展开第二项", "Open the second panel") }.withTheme("tertiary").apply {
            clickEvent.addHandler { controlled.opened = 1.0 }
        }
    programmatic.body.append(example(null, column(controlled, row(openFirst, openBoth, readout.root))))
    view.body.append(programmatic.root)

    return view
}

private fun buildDetailsPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "summary 是标题文本，子节点是展开区内容；opened 可读写。",
                "summary is the header text and the children are the expanded content; opened is read/write.",
            ),
        )
    val log = EventLog(4)
    val details = createDetails { summary = tr("更多信息", "More information") }
    details.dataset["test"] = "details"
    details.append(paragraph(tr("这里是展开后显示的详细说明。", "This text appears once the block is expanded.")))
    details.listen(DetailsEvents.openedChanged) { log.add("opened-changed · ${details.opened}") }
    basic.body.append(example(null, column(details, log.root)))
    view.body.append(basic.root)

    val openedSection =
        section(
            tr("默认展开", "Open by default"),
            tr("opened 设为 true 时初始即展开。", "Setting opened to true expands the block from the start."),
        )
    val expanded = createDetails {
        summary = tr("默认展开的区块", "Block opened by default")
        opened = true
    }
    expanded.append(paragraph(tr("一开始就能看到的内容。", "This content is visible right away.")))
    val toggleButton =
        createButton { textContent = tr("切换", "Toggle") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                expanded.opened = !expanded.opened
            }
        }
    openedSection.body.append(example(null, column(expanded, toggleButton)))
    view.body.append(openedSection.root)

    return view
}

private fun buildTabsPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "tab 的 value 可选；selected 是选中下标，selected-changed 在切换时触发。",
                "tab takes an optional value; selected holds the active index and selected-changed fires on " +
                    "every switch.",
            ),
        )
    val log = EventLog(4)
    val tabs = createTabs()
    tabs.dataset["test"] = "tabs"
    tabs.append(
        createTab { textContent = tr("概览", "Overview") },
        createTab { textContent = tr("明细", "Details") },
        createTab { textContent = tr("设置", "Settings") },
    )
    tabs.listen(TabsEvents.selectedChanged) { log.add("selected-changed · ${tabs.selected}") }
    basic.body.append(example(null, column(tabs, log.root)))
    view.body.append(basic.root)

    val variants =
        section(
            tr("方向与变体", "Orientation and variants"),
            tr(
                "orientation 设为 vertical 变成侧边标签，equal-width-tabs 让标签等宽。",
                "orientation set to vertical turns the tabs sideways, and equal-width-tabs makes every tab " +
                    "the same width.",
            ),
        )
    val vertical = createTabs { orientation = ListOrientations.vertical }
    vertical.append(
        createTab { textContent = tr("第一项", "First item") },
        createTab { textContent = tr("第二项", "Second item") },
    )
    val equal = createTabs()
    equal.withTheme("equal-width-tabs")
    equal.append(
        createTab { textContent = tr("左", "Left") },
        createTab { textContent = tr("中", "Middle") },
        createTab { textContent = tr("右", "Right") },
    )
    equal.style.setProperty("width", "20rem")
    variants.body.append(example(null, row(vertical, equal)))
    view.body.append(variants.root)

    return view
}

private fun buildTabSheetPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "把 vaadin-tabs 放进 slot=\"tabs\"；每个面板用 tab 属性指向对应 tab 的 id，" +
                    "selected 决定显示哪个面板（找不到匹配面板时组件进入 loading 状态）。",
                "vaadin-tabs goes into slot=\"tabs\"; each panel points at a tab id through its tab " +
                    "attribute, and selected decides which panel is shown (the component enters a loading " +
                    "state when no panel matches).",
            ),
        )
    val sheet = createTabSheet()
    sheet.dataset["test"] = "tab-sheet"
    val tabs = createTabs().apply { slot = "tabs" }

    val tabIds = listOf("tabsheet-demo-basic", "tabsheet-demo-contact", "tabsheet-demo-access")
    tabs.append(
        createTab { textContent = tr("基本信息", "Basic info") }.apply { id = ElementId(tabIds[0]) },
        createTab { textContent = tr("联系方式", "Contact") }.apply { id = ElementId(tabIds[1]) },
        createTab { textContent = tr("权限", "Permissions") }.apply { id = ElementId(tabIds[2]) },
    )
    val panels =
        listOf(
            column(
                heading(3, tr("基本信息", "Basic info")),
                paragraph(tr("这一页的内容由第一个面板提供。", "The content of this tab comes from the first panel.")),
            ),
            column(
                heading(3, tr("联系方式", "Contact")),
                paragraph(tr("第二个面板。", "The second panel.")),
            ),
            column(
                heading(3, tr("权限", "Permissions")),
                paragraph(tr("第三个面板。", "The third panel.")),
            ),
        )
    sheet.append(tabs)
    // `tab` is a tab-sheet convention rather than a DOM property, so it stays an attribute.
    panels.forEachIndexed { index, panelView -> panelView.setAttribute("tab", tabIds[index]) }
    panels.forEach { sheet.append(it) }

    val readout = Readout("selected = 0")
    sheet.listen(TabSheetEvents.selectedChanged) { readout.set("selected = ${sheet.selected}") }
    basic.body.append(example(null, column(sheet, readout.root)))
    view.body.append(basic.root)

    val programmatic = section(tr("程序化切换", "Switching programmatically"), "")
    programmatic.body.append(
        example(
            null,
            row(
                createButton { textContent = tr("第一页", "First tab") }.withTheme("tertiary").apply {
                    clickEvent.addHandler { sheet.selected = 0.0 }
                },
                createButton { textContent = tr("第三页", "Third tab") }.withTheme("tertiary").apply {
                    clickEvent.addHandler { sheet.selected = 2.0 }
                },
            ),
        ),
    )
    view.body.append(programmatic.root)

    return view
}

private fun buildSideNavPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "path 生成链接；当前路由与 pathname 匹配的项会自动标记为 current。",
                "path generates links; the item whose pathname matches the current route is marked current " +
                    "automatically.",
            ),
        )
    val nav = createSideNav()
    nav.dataset["test"] = "side-nav"
    nav.append(
        createSideNavItem {
            path = "#/side-nav"
            textContent = tr("当前页面", "Current page")
        },
        createSideNavItem {
            path = "#/tabs"
            textContent = tr("另一个页面", "Another page")
        },
        createSideNavItem {
            textContent = tr("禁用项", "Disabled item")
            disabled = true
        },
    )
    basic.body.append(example(null, nav))
    view.body.append(basic.root)

    val nested =
        section(
            tr("嵌套与折叠", "Nesting and collapsing"),
            tr(
                "子项放进 slot=\"children\"；collapsible 打开右上角的折叠按钮。",
                "Child items go into slot=\"children\"; collapsible adds the collapse button in the top corner.",
            ),
        )
    val parent =
        createSideNavItem {
            textContent = tr("分组标题", "Group title")
            expanded = true
        }
    parent.append(
        createSideNavItem {
            path = "#/button"
            textContent = tr("子项一", "Child one")
            slot = "children"
        },
        createSideNavItem {
            path = "#/text-field"
            textContent = tr("子项二", "Child two")
            slot = "children"
        },
    )
    val nestedNav = createSideNav { collapsible = true }
    nestedNav.append(parent)
    val readout = Readout("collapsed = false")
    nestedNav.listen(vaadin.sidenav.SideNavEvents.collapsedChanged) {
        readout.set("collapsed = ${nestedNav.collapsed}")
    }
    nested.body.append(example(null, column(nestedNav, readout.root)))
    view.body.append(nested.root)

    return view
}

private fun buildBreadcrumbsPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "最后一项自动标记为 current，点击前几项会导航到对应 hash 路由。",
                "The last item is marked current automatically; clicking an earlier item navigates to its " +
                    "hash route.",
            ),
        )
    val crumbs = createBreadcrumbs()
    crumbs.dataset["test"] = "breadcrumbs"
    crumbs.append(
        createBreadcrumbsItem {
            path = "#/overview"
            textContent = tr("首页", "Home")
        },
        createBreadcrumbsItem {
            path = "#/layouts"
            textContent = tr("布局", "Layouts")
        },
        createBreadcrumbsItem { textContent = tr("面包屑", "Breadcrumbs") },
    )
    basic.body.append(example(null, crumbs))
    view.body.append(basic.root)

    val overflow =
        section(
            tr("项较多时", "Many items"),
            tr(
                "超出宽度时可以横向滚动，组件会保持首尾可见。",
                "Overflowing items scroll horizontally while the first and last stay visible.",
            ),
        )
    val many = createBreadcrumbs()
    (1..8).forEach { index ->
        many.append(
            createBreadcrumbsItem {
                path = "#/breadcrumbs"
                textContent = tr("第 $index 层", "Level $index")
            },
        )
    }
    overflow.body.append(example(null, many))
    view.body.append(overflow.root)

    return view
}

/** A tinted placeholder box used by the layout examples. */
private fun box(text: String, height: String? = null): HTMLElement =
    div().apply {
        textContent = text
        style.setProperty("padding", "var(--lumo-space-s) var(--lumo-space-m)")
        style.setProperty("border-radius", "var(--lumo-border-radius-m)")
        style.setProperty("background", "var(--lumo-primary-color-10pct)")
        if (height != null) style.setProperty("height", height)
    }

/** A fake master list for the split layout examples: sixteen rows of six cells. */
private fun splitMaster(): HTMLElement =
    div("split-master").apply {
        repeat(16) {
            val row = div("split-master__row")
            repeat(6) { row.append(div("split-master__cell")) }
            append(row)
        }
    }

/** A fake detail form for the split layout examples: ten labelled inputs. */
private fun splitDetail(): HTMLElement =
    div("split-detail").apply {
        repeat(10) {
            val field = div("split-detail__field")
            field.append(div("split-detail__label"), div("split-detail__input"))
            append(field)
        }
    }

/**
 * Puts a form layout next to an empty pane in a split layout, as the docs examples do: dragging the
 * splitter changes the width of the form and the responsive column count follows it.
 */
private fun formFrame(form: HTMLElement): HTMLElement {
    form.style.setProperty("width", "100%")
    val split = createSplitLayout()
    split.style.setProperty("height", "18rem")
    split.append(form, div().apply { style.setProperty("background", "var(--lumo-contrast-5pct)") })
    return split
}

/** A `<vaadin-form-row>` grouping fields into one row of an auto-responsive form layout. */
private fun formRow(vararg fields: HTMLElement): HTMLElement {
    val row = createFormRow()
    row.append(*fields)
    return row
}

private fun textField(label: String): HTMLElement = createTextField { this.label = label }

private fun emailField(label: String): HTMLElement = createEmailField { this.label = label }

private fun passwordField(label: String): HTMLElement = createPasswordField { this.label = label }

/** An accordion panel with a summary and one content child. */
private fun accordionPanel(summary: String): vaadin.accordion.AccordionPanelElement =
    createAccordionPanel { this.summary = summary }

/** A `vaadin-form-item` whose label goes into the `label` slot, as upstream documents it. */
private fun formItem(label: String, field: HTMLElement): HTMLElement {
    val item = createFormItem()
    val labelNode = element(HtmlTagName.label).apply {
        slot = "label"
        textContent = label
    }
    item.append(labelNode, field)
    return item
}

/** Encodes [markup] as an SVG data URI, so demo images never touch the network. */
private fun svgDataUri(markup: String): String =
    "data:image/svg+xml," +
        markup
            .replace("\"", "'")
            .replace("%", "%25")
            .replace("<", "%3C")
            .replace(">", "%3E")
            .replace("#", "%23")
            .replace(" ", "%20")
