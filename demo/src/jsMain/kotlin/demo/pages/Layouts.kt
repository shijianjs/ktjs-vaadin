package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.example
import demo.ui.heading
import demo.ui.jsObject
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.row
import demo.ui.section
import demo.ui.withTheme
import vaadin.accordion.AccordionEvents
import vaadin.accordion.createAccordion
import vaadin.accordion.createAccordionPanel
import vaadin.applayout.AppLayoutEvents
import vaadin.applayout.createAppLayout
import vaadin.applayout.createDrawerToggle
import vaadin.breadcrumbs.createBreadcrumbs
import vaadin.breadcrumbs.createBreadcrumbsItem
import vaadin.button.createButton
import vaadin.card.createCard
import vaadin.details.DetailsEvents
import vaadin.details.createDetails
import vaadin.formlayout.createFormItem
import vaadin.formlayout.createFormLayout
import vaadin.horizontallayout.createHorizontalLayout
import vaadin.masterdetaillayout.createMasterDetailLayout
import vaadin.scroller.createScroller
import vaadin.sidenav.createSideNav
import vaadin.sidenav.createSideNavItem
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
import kotlin.js.unsafeCast

val layoutEntries: List<CatalogEntry> =
    listOf(
        CatalogEntry(
            id = "app-layout",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = "App Layout",
                    summary = "应用级布局：navbar、drawer 与内容区，抽屉在窄屏上变成浮层。",
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
                    title = "Horizontal Layout",
                    summary = "横向排列子元素的容器，通过 theme 控制间距、内边距与换行。",
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
                    title = "Vertical Layout",
                    summary = "纵向排列子元素的容器。",
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
                    title = "Form Layout",
                    summary = "响应式表单栅格：按可用宽度自动调整列数，标签可以放在字段旁边。",
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
                    title = "Split Layout",
                    summary = "可拖动的分栏容器，orientation 决定水平或垂直分割。",
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
                    title = "Scroller",
                    summary = "带样式的滚动容器，scrollDirection 控制滚动轴。",
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
                    title = "Master Detail Layout",
                    summary = "主从布局：主区在默认插槽，详情在 detail 插槽，放不下时自动切换成浮层。",
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
                    title = "Card",
                    summary = "内容卡片：标题层级、elevated / outlined / stretch-media 变体。",
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
                    title = "Accordion",
                    summary = "可折叠面板组，支持多开与指定展开下标。",
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
                    title = "Details",
                    summary = "单个可折叠区块，summary 是标题，子节点是内容。",
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
                    title = "Tabs",
                    summary = "标签栏：selected 是下标，支持横向 / 纵向与 equal-width 变体。",
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
                    title = "Tab Sheet",
                    summary = "把 Tabs 与内容面板组合起来，按 selected 下标切换内容。",
                    docsUrl = "$DOCS_BASE/tabs",
                    docsNote = "Tab Sheet 与 Tabs 共用一页官方文档。",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildTabSheetPage,
        ),
        CatalogEntry(
            id = "side-nav",
            group = Groups.LAYOUT,
            meta =
                PageMeta(
                    title = "Side Nav",
                    summary = "导航菜单：path 生成链接，可折叠、可嵌套。",
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
                    title = "Breadcrumbs",
                    summary = "面包屑导航：每项一个 path，当前项不可点击。",
                    docsUrl = "$DOCS_BASE/breadcrumbs",
                    sourcePath = "pages/Layouts.kt",
                ),
            builder = ::buildBreadcrumbsPage,
        ),
    )

private fun buildAppLayoutPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic =
        section("基本用法", "navbar 与 drawer 通过 slot 指定；drawerOpened 控制抽屉，窄屏时抽屉变成浮层。")
    val layout = createAppLayout()
    layout.dataset["test"] = "app-layout"
    layout.style.setProperty("height", "18rem")

    val toggle = createDrawerToggle()
    toggle.style.setProperty("color", "var(--lumo-body-text-color)")
    val title = heading(3, "示例应用")
    title.style.setProperty("margin", "0")
    val navbar = div().apply { slot = "navbar" }
    navbar.append(toggle, title)
    val drawer = div().apply { slot = "drawer" }
    drawer.style.setProperty("padding", "var(--lumo-space-m)")
    drawer.append(
        column(
            paragraph("导航项 1"),
            paragraph("导航项 2"),
            paragraph("导航项 3"),
        ),
    )
    val content =
        div().apply {
            style.setProperty("padding", "var(--lumo-space-m)")
            textContent = "这是内容区，由默认插槽填充。"
        }
    layout.append(navbar, drawer, content)

    val readout = Readout("drawerOpened = true")
    layout.listen(AppLayoutEvents.drawerOpenedChanged) {
        readout.set("drawerOpened = ${layout.drawerOpened}")
    }
    basic.body.append(example(null, column(layout, readout.root)))
    view.body.append(basic.root)

    val primary = section("primarySection", "切到 drawer 时抽屉占满整个高度，导航栏移到右侧内容区上方。")
    val primaryReadout = Readout("primarySection = navbar")
    val primaryButton =
        createButton { textContent = "切换 primarySection" }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                val next = if (layout.primarySection == "drawer") "navbar" else "drawer"
                layout.primarySection = next
                primaryReadout.set("primarySection = $next")
            }
        }
    primary.body.append(example(null, row(primaryButton, primaryReadout.root)))
    view.body.append(primary.root)

    return view.root
}

private fun buildHorizontalLayoutPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("间距与内边距", "theme 支持 spacing / padding / margin，可以单独或组合使用。")
    basic.body.append(
        example(
            "无 theme",
            createHorizontalLayout().apply {
                append(box("A"), box("B"), box("C"))
            },
        ),
        example(
            "spacing",
            createHorizontalLayout { }.apply {
                withTheme("spacing")
                append(box("A"), box("B"), box("C"))
            },
        ),
        example(
            "spacing padding margin",
            createHorizontalLayout().apply {
                withTheme("spacing", "padding", "margin")
                append(box("A"), box("B"), box("C"))
            },
        ),
    )
    view.body.append(basic.root)

    val wrap = section("换行与对齐", "wrap 让子元素在空间不足时换行；子元素的 flex 由 wrapper 的样式控制。")
    wrap.body.append(
        example(
            null,
            createHorizontalLayout().apply {
                withTheme("spacing", "wrap")
                style.setProperty("max-width", "24rem")
                append(box("很长很长的第一项"), box("第二项"), box("第三项"), box("第四项"))
            },
        ),
    )
    view.body.append(wrap.root)

    return view.root
}

private fun buildVerticalLayoutPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("间距与内边距", "与 Horizontal Layout 相同的主题变体，方向改为纵向。")
    basic.body.append(
        example(
            null,
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                append(box("第一行"), box("第二行"), box("第三行"))
            },
        ),
    )
    view.body.append(basic.root)

    val nested = section("嵌套", "纵向容器里可以放横向容器，栅格由外层负责。")
    nested.body.append(
        example(
            null,
            createVerticalLayout().apply {
                withTheme("spacing")
                append(
                    createHorizontalLayout().apply {
                        withTheme("spacing")
                        append(box("左"), box("右"))
                    },
                    createHorizontalLayout().apply {
                        withTheme("spacing")
                        append(box("下左"), box("下右"))
                    },
                )
            },
        ),
    )
    view.body.append(nested.root)

    return view.root
}

private fun buildFormLayoutPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "form-item 提供标签，form-layout 依据可用宽度决定列数。")
    val form = createFormLayout()
    form.dataset["test"] = "form-layout"
    form.append(
        formItem("姓名", createTextField { placeholder = "Ada" }),
        formItem("邮箱", vaadin.emailfield.createEmailField { placeholder = "ada@example.com" }),
        formItem("城市", createTextField { placeholder = "London" }),
        formItem("备注", vaadin.textarea.createTextArea { minRows = 2.0 }),
    )
    basic.body.append(example(null, form))
    view.body.append(basic.root)

    val responsive =
        section("响应式列数", "responsiveSteps 指定断点与列数；labelsAside 把标签放在字段左侧。")
    val readout = Readout("maxColumns = 4（默认）")
    val controlled = createFormLayout()
    controlled.responsiveSteps =
        jsObject(
            """
            [
              {"minWidth": "0px", "columns": 1, "labelsPosition": "top"},
              {"minWidth": "30rem", "columns": 2},
              {"minWidth": "50rem", "columns": 3}
            ]
            """.trimIndent(),
        ).unsafeCast<Array<vaadin.formlayout.FormLayoutResponsiveStep>>()
    controlled.append(
        *(1..6).map { index -> formItem("字段 $index", createTextField()) }.toTypedArray(),
    )
    val labelsSwitch = vaadin.switch.createSwitch { label = "labelsAside（标签在左侧）" }
    labelsSwitch.listen(vaadin.switch.SwitchEvents.checkedChanged) {
        controlled.labelsAside = labelsSwitch.checked
        readout.set("labelsAside = ${controlled.labelsAside}")
    }
    responsive.body.append(example(null, column(controlled, labelsSwitch, readout.root)))
    view.body.append(responsive.root)

    return view.root
}

private fun buildSplitLayoutPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "两个子元素之间会出现可拖动的分隔条。")
    val horizontal = createSplitLayout()
    horizontal.style.setProperty("height", "10rem")
    horizontal.append(panel("左侧"), panel("右侧"))

    val vertical = createSplitLayout { orientation = "vertical" }
    vertical.style.setProperty("height", "14rem")
    vertical.append(panel("上方"), panel("下方"))

    basic.body.append(example("horizontal", horizontal), example("vertical", vertical))
    view.body.append(basic.root)

    return view.root
}

private fun buildScrollerPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val vertical = section("纵向滚动", "内容超出固定高度时出现滚动条。")
    val scroller = createScroller()
    scroller.dataset["test"] = "scroller"
    scroller.style.setProperty("height", "10rem")
    scroller.append(
        column(*(1..20).map { paragraph("第 $it 行内容") }.toTypedArray()),
    )
    vertical.body.append(example(null, scroller))
    view.body.append(vertical.root)

    val horizontal = section("横向滚动", "scrollDirection 设为 horizontal 后只允许横向滚动。")
    val horizontalScroller = createScroller { scrollDirection = "horizontal" }
    horizontalScroller.style.setProperty("height", "6rem")
    val wideRow = row(*(1..15).map { box("列 $it") }.toTypedArray())
    wideRow.style.setProperty("flex-wrap", "nowrap")
    horizontalScroller.append(wideRow)
    horizontal.body.append(example(null, horizontalScroller))
    view.body.append(horizontal.root)

    return view.root
}

private fun buildMasterDetailPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "主区在默认插槽，详情放进 slot=\"detail\"；空间不足时详情会变成浮层。")
    val layout = createMasterDetailLayout()
    layout.dataset["test"] = "master-detail-layout"
    layout.style.setProperty("height", "16rem")
    layout.masterSize = "16rem"
    layout.detailSize = "1fr"

    val master = column(paragraph("主区内容"))
    val detail = div().apply {
        slot = "detail"
        style.setProperty("padding", "var(--lumo-space-m)")
        textContent = "详情内容"
    }
    layout.append(master, detail)
    basic.body.append(example(null, layout))
    view.body.append(basic.root)

    val options = section("方向与浮层", "orientation 切换 master 与 detail 的排列方向，forceOverlay 强制详情以浮层显示。")
    val readout = Readout("orientation = horizontal")
    val overlaySwitch = vaadin.switch.createSwitch { label = "forceOverlay" }
    val orientationButton =
        createButton { textContent = "切换方向" }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                layout.orientation = if (layout.orientation == "horizontal") "vertical" else "horizontal"
                readout.set("orientation = ${layout.orientation}")
                layout.recalculateLayout()
            }
        }
    overlaySwitch.listen(vaadin.switch.SwitchEvents.checkedChanged) {
        layout.forceOverlay = overlaySwitch.checked
        layout.recalculateLayout()
    }
    options.body.append(example(null, row(orientationButton, overlaySwitch, readout.root)))
    view.body.append(options.root)

    return view.root
}

private fun buildCardPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "cardTitle 生成标题，titleHeadingLevel 决定标题层级，子节点是正文。")
    val card = createCard { cardTitle = "季度总结" }
    card.append(paragraph("卡片内容可以是任意节点：文本、按钮、甚至其他组件。"))
    basic.body.append(example(null, card))
    view.body.append(basic.root)

    val variants = section("主题变体", "elevated 加阴影，outlined 加边框，两者可以对照使用。")
    variants.body.append(
        example(
            null,
            row(
                createCard { }.apply {
                    withTheme("elevated")
                    append(heading(3, "elevated"), paragraph("带阴影的卡片"))
                },
                createCard { }.apply {
                    withTheme("outlined")
                    append(heading(3, "outlined"), paragraph("带边框的卡片"))
                },
            ),
        ),
    )
    view.body.append(variants.root)

    val levels = section("标题层级", "")
    levels.body.append(
        example(
            null,
            row(
                createCard {
                    cardTitle = "二级标题"
                    titleHeadingLevel = 2.0
                }.apply { append(paragraph("titleHeadingLevel = 2")) },
                createCard {
                    cardTitle = "四级标题"
                    titleHeadingLevel = 4.0
                }.apply { append(paragraph("titleHeadingLevel = 4")) },
            ),
        ),
    )
    view.body.append(levels.root)

    return view.root
}

private fun buildAccordionPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "每个 panel 用 summary 定义标题，子节点是展开后的内容。")
    val log = EventLog(4)
    val accordion = createAccordion { headingLevel = 3.0 }
    accordion.dataset["test"] = "accordion"
    accordion.append(
        accordionPanel("配送与安装").apply {
            append(paragraph("面板一的内容。"))
        },
        accordionPanel("付款方式").apply {
            append(paragraph("面板二的内容。"))
        },
        accordionPanel("售后服务").apply {
            append(paragraph("面板三的内容。"))
        },
    )
    accordion.listen(AccordionEvents.openedChanged) {
        log.add("opened-changed · opened=${accordion.opened}")
    }
    basic.body.append(example(null, column(accordion, log.root)))
    view.body.append(basic.root)

    val programmatic = section("程序化展开", "opened 是要展开的面板下标（Number），可以一次打开多个。")
    val readout = Readout("opened = -")
    val controlled = createAccordion()
    controlled.append(
        accordionPanel("第一项").apply { append(paragraph("内容一")) },
        accordionPanel("第二项").apply { append(paragraph("内容二")) },
    )
    controlled.opened = 1.0
    controlled.listen(AccordionEvents.openedChanged) {
        readout.set("opened = ${controlled.opened}")
    }
    val openFirst =
        createButton { textContent = "展开第一项" }.withTheme("tertiary").apply {
            clickEvent.addHandler { controlled.opened = 0.0 }
        }
    val openBoth =
        createButton { textContent = "展开第二项" }.withTheme("tertiary").apply {
            clickEvent.addHandler { controlled.opened = 1.0 }
        }
    programmatic.body.append(example(null, column(controlled, row(openFirst, openBoth, readout.root))))
    view.body.append(programmatic.root)

    return view.root
}

private fun buildDetailsPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "summary 是标题文本，子节点是展开区内容；opened 可读写。")
    val log = EventLog(4)
    val details = createDetails { summary = "更多信息" }
    details.dataset["test"] = "details"
    details.append(paragraph("这里是展开后显示的详细说明。"))
    details.listen(DetailsEvents.openedChanged) { log.add("opened-changed · ${details.opened}") }
    basic.body.append(example(null, column(details, log.root)))
    view.body.append(basic.root)

    val openedSection = section("默认展开", "opened 设为 true 时初始即展开。")
    val expanded = createDetails {
        summary = "默认展开的区块"
        opened = true
    }
    expanded.append(paragraph("一开始就能看到的内容。"))
    val toggleButton =
        createButton { textContent = "切换" }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                expanded.opened = !expanded.opened
            }
        }
    openedSection.body.append(example(null, column(expanded, toggleButton)))
    view.body.append(openedSection.root)

    return view.root
}

private fun buildTabsPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "tab 的 value 可选；selected 是选中下标，selected-changed 在切换时触发。")
    val log = EventLog(4)
    val tabs = createTabs()
    tabs.dataset["test"] = "tabs"
    tabs.append(
        createTab { textContent = "概览" },
        createTab { textContent = "明细" },
        createTab { textContent = "设置" },
    )
    tabs.listen(TabsEvents.selectedChanged) { log.add("selected-changed · ${tabs.selected}") }
    basic.body.append(example(null, column(tabs, log.root)))
    view.body.append(basic.root)

    val variants = section("方向与变体", "orientation 设为 vertical 变成侧边标签，equal-width-tabs 让标签等宽。")
    val vertical = createTabs { orientation = "vertical" }
    vertical.append(
        createTab { textContent = "第一项" },
        createTab { textContent = "第二项" },
    )
    val equal = createTabs()
    equal.withTheme("equal-width-tabs")
    equal.append(
        createTab { textContent = "左" },
        createTab { textContent = "中" },
        createTab { textContent = "右" },
    )
    equal.style.setProperty("width", "20rem")
    variants.body.append(example(null, row(vertical, equal)))
    view.body.append(variants.root)

    return view.root
}

private fun buildTabSheetPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic =
        section(
            "基本用法",
            "把 vaadin-tabs 放进 slot=\"tabs\"；每个面板用 tab 属性指向对应 tab 的 id，" +
                "selected 决定显示哪个面板（找不到匹配面板时组件进入 loading 状态）。",
        )
    val sheet = createTabSheet()
    sheet.dataset["test"] = "tab-sheet"
    val tabs = createTabs().apply { slot = "tabs" }

    val tabIds = listOf("tabsheet-demo-basic", "tabsheet-demo-contact", "tabsheet-demo-access")
    tabs.append(
        createTab { textContent = "基本信息" }.apply { id = ElementId(tabIds[0]) },
        createTab { textContent = "联系方式" }.apply { id = ElementId(tabIds[1]) },
        createTab { textContent = "权限" }.apply { id = ElementId(tabIds[2]) },
    )
    val panels =
        listOf(
            column(heading(3, "基本信息"), paragraph("这一页的内容由第一个面板提供。")),
            column(heading(3, "联系方式"), paragraph("第二个面板。")),
            column(heading(3, "权限"), paragraph("第三个面板。")),
        )
    sheet.append(tabs)
    // `tab` is a tab-sheet convention rather than a DOM property, so it stays an attribute.
    panels.forEachIndexed { index, panelView -> panelView.setAttribute("tab", tabIds[index]) }
    panels.forEach { sheet.append(it) }

    val readout = Readout("selected = 0")
    sheet.listen(TabSheetEvents.selectedChanged) { readout.set("selected = ${sheet.selected}") }
    basic.body.append(example(null, column(sheet, readout.root)))
    view.body.append(basic.root)

    val programmatic = section("程序化切换", "")
    programmatic.body.append(
        example(
            null,
            row(
                createButton { textContent = "第一页" }.withTheme("tertiary").apply {
                    clickEvent.addHandler { sheet.selected = 0.0 }
                },
                createButton { textContent = "第三页" }.withTheme("tertiary").apply {
                    clickEvent.addHandler { sheet.selected = 2.0 }
                },
            ),
        ),
    )
    view.body.append(programmatic.root)

    return view.root
}

private fun buildSideNavPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "path 生成链接；当前路由与 pathname 匹配的项会自动标记为 current。")
    val nav = createSideNav()
    nav.dataset["test"] = "side-nav"
    nav.append(
        createSideNavItem {
            path = "#/side-nav"
            textContent = "当前页面"
        },
        createSideNavItem {
            path = "#/tabs"
            textContent = "另一个页面"
        },
        createSideNavItem {
            textContent = "禁用项"
            disabled = true
        },
    )
    basic.body.append(example(null, nav))
    view.body.append(basic.root)

    val nested = section("嵌套与折叠", "子项放进 slot=\"children\"；collapsible 打开右上角的折叠按钮。")
    val parent =
        createSideNavItem {
            textContent = "分组标题"
            expanded = true
        }
    parent.append(
        createSideNavItem {
            path = "#/button"
            textContent = "子项一"
            slot = "children"
        },
        createSideNavItem {
            path = "#/text-field"
            textContent = "子项二"
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

    return view.root
}

private fun buildBreadcrumbsPage(meta: PageMeta): HTMLElement {
    val view = page(meta)

    val basic = section("基本用法", "最后一项自动标记为 current，点击前几项会导航到对应 hash 路由。")
    val crumbs = createBreadcrumbs()
    crumbs.dataset["test"] = "breadcrumbs"
    crumbs.append(
        createBreadcrumbsItem {
            path = "#/overview"
            textContent = "首页"
        },
        createBreadcrumbsItem {
            path = "#/layouts"
            textContent = "布局"
        },
        createBreadcrumbsItem { textContent = "面包屑" },
    )
    basic.body.append(example(null, crumbs))
    view.body.append(basic.root)

    val overflow = section("项较多时", "超出宽度时可以横向滚动，组件会保持首尾可见。")
    val many = createBreadcrumbs()
    (1..8).forEach { index ->
        many.append(
            createBreadcrumbsItem {
                path = "#/breadcrumbs"
                textContent = "第 $index 层"
            },
        )
    }
    overflow.body.append(example(null, many))
    view.body.append(overflow.root)

    return view.root
}

/** A tinted placeholder box used by the layout examples. */
private fun box(text: String): HTMLElement {
    val element = div().apply {
        textContent = text
        style.setProperty("padding", "var(--lumo-space-s) var(--lumo-space-m)")
        style.setProperty("border-radius", "var(--lumo-border-radius-m)")
        style.setProperty("background", "var(--lumo-primary-color-10pct)")
    }
    return element
}

/** A bordered panel used by the split layout example. */
private fun panel(text: String): HTMLElement =
    div().apply {
        textContent = text
        style.setProperty("padding", "var(--lumo-space-m)")
        style.setProperty("height", "100%")
        style.setProperty("background", "var(--lumo-contrast-5pct)")
    }

/** An accordion panel with a summary and one content child. */
private fun accordionPanel(summary: String): vaadin.accordion.AccordionPanelElement =
    createAccordionPanel { this.summary = summary }

/** A `vaadin-form-item` whose label goes into the `label` slot, as upstream documents it. */
private fun formItem(label: String, field: HTMLElement): HTMLElement {
    val item = createFormItem()
    val labelNode = div().apply {
        slot = "label"
        textContent = label
    }
    item.append(labelNode, field)
    return item
}
