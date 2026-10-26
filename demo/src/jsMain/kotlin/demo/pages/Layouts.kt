package demo.pages

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
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
import demo.tr
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

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "navbar 与 drawer 通过 slot 指定；drawerOpened 控制抽屉，窄屏时抽屉变成浮层。",
                "The navbar and drawer are set through slots; drawerOpened controls the drawer, which becomes " +
                    "an overlay on narrow screens.",
            ),
        )
    val layout = createAppLayout()
    layout.dataset["test"] = "app-layout"
    layout.style.setProperty("height", "18rem")

    val toggle = createDrawerToggle()
    toggle.style.setProperty("color", "var(--lumo-body-text-color)")
    val title = heading(3, tr("示例应用", "Sample app"))
    title.style.setProperty("margin", "0")
    val navbar = div().apply { slot = "navbar" }
    navbar.append(toggle, title)
    val drawer = div().apply { slot = "drawer" }
    drawer.style.setProperty("padding", "var(--lumo-space-m)")
    drawer.append(
        column(
            paragraph(tr("导航项 1", "Nav item 1")),
            paragraph(tr("导航项 2", "Nav item 2")),
            paragraph(tr("导航项 3", "Nav item 3")),
        ),
    )
    val content =
        div().apply {
            style.setProperty("padding", "var(--lumo-space-m)")
            textContent = tr("这是内容区，由默认插槽填充。", "This is the content area, filled through the default slot.")
        }
    layout.append(navbar, drawer, content)

    val readout = Readout("drawerOpened = true")
    layout.listen(AppLayoutEvents.drawerOpenedChanged) {
        readout.set("drawerOpened = ${layout.drawerOpened}")
    }
    basic.body.append(example(null, column(layout, readout.root)))
    view.body.append(basic.root)

    val primary =
        section(
            "primarySection",
            tr(
                "切到 drawer 时抽屉占满整个高度，导航栏移到右侧内容区上方。",
                "With drawer as the primary section the drawer takes the full height and the navbar moves " +
                    "above the content on the right.",
            ),
        )
    val primaryReadout = Readout("primarySection = navbar")
    val primaryButton =
        createButton { textContent = tr("切换 primarySection", "Toggle primarySection") }.withTheme("tertiary").apply {
            clickEvent.addHandler {
                val next = if (layout.primarySection == "drawer") "navbar" else "drawer"
                layout.primarySection = next
                primaryReadout.set("primarySection = $next")
            }
        }
    primary.body.append(example(null, row(primaryButton, primaryReadout.root)))
    view.body.append(primary.root)

    return view
}

private fun buildHorizontalLayoutPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("间距与内边距", "Spacing and padding"),
            tr(
                "theme 支持 spacing / padding / margin，可以单独或组合使用。",
                "theme accepts spacing / padding / margin, alone or combined.",
            ),
        )
    basic.body.append(
        example(
            tr("无 theme", "No theme"),
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

    val wrap =
        section(
            tr("换行与对齐", "Wrapping and alignment"),
            tr(
                "wrap 让子元素在空间不足时换行；子元素的 flex 由 wrapper 的样式控制。",
                "wrap lets children wrap when space runs out; the children's flex values come from the " +
                    "wrapper's styles.",
            ),
        )
    wrap.body.append(
        example(
            null,
            createHorizontalLayout().apply {
                withTheme("spacing", "wrap")
                style.setProperty("max-width", "24rem")
                append(
                    box(tr("很长很长的第一项", "A very long first item")),
                    box(tr("第二项", "Second item")),
                    box(tr("第三项", "Third item")),
                    box(tr("第四项", "Fourth item")),
                )
            },
        ),
    )
    view.body.append(wrap.root)

    return view
}

private fun buildVerticalLayoutPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("间距与内边距", "Spacing and padding"),
            tr("与 Horizontal Layout 相同的主题变体，方向改为纵向。", "Same theme variants as Horizontal Layout, laid out vertically."),
        )
    basic.body.append(
        example(
            null,
            createVerticalLayout().apply {
                withTheme("spacing", "padding")
                append(
                    box(tr("第一行", "First row")),
                    box(tr("第二行", "Second row")),
                    box(tr("第三行", "Third row")),
                )
            },
        ),
    )
    view.body.append(basic.root)

    val nested =
        section(
            tr("嵌套", "Nesting"),
            tr(
                "纵向容器里可以放横向容器，栅格由外层负责。",
                "A vertical container can hold horizontal ones; the outer container owns the grid.",
            ),
        )
    nested.body.append(
        example(
            null,
            createVerticalLayout().apply {
                withTheme("spacing")
                append(
                    createHorizontalLayout().apply {
                        withTheme("spacing")
                        append(box(tr("左", "Left")), box(tr("右", "Right")))
                    },
                    createHorizontalLayout().apply {
                        withTheme("spacing")
                        append(box(tr("下左", "Bottom left")), box(tr("下右", "Bottom right")))
                    },
                )
            },
        ),
    )
    view.body.append(nested.root)

    return view
}

private fun buildFormLayoutPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "form-item 提供标签，form-layout 依据可用宽度决定列数。",
                "form-item supplies the label and form-layout picks the column count from the available width.",
            ),
        )
    val form = createFormLayout()
    form.dataset["test"] = "form-layout"
    form.append(
        formItem(tr("姓名", "Name"), createTextField { placeholder = "Ada" }),
        formItem(tr("邮箱", "Email"), vaadin.emailfield.createEmailField { placeholder = "ada@example.com" }),
        formItem(tr("城市", "City"), createTextField { placeholder = "London" }),
        formItem(tr("备注", "Notes"), vaadin.textarea.createTextArea { minRows = 2.0 }),
    )
    basic.body.append(example(null, form))
    view.body.append(basic.root)

    val responsive =
        section(
            tr("响应式列数", "Responsive columns"),
            tr(
                "responsiveSteps 指定断点与列数；labelsAside 把标签放在字段左侧。",
                "responsiveSteps defines the breakpoints and column counts; labelsAside moves the labels to " +
                    "the left of the fields.",
            ),
        )
    val readout = Readout(tr("maxColumns = 4（默认）", "maxColumns = 4 (default)"))
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
        *(1..6).map { index -> formItem(tr("字段 $index", "Field $index"), createTextField()) }.toTypedArray(),
    )
    val labelsSwitch =
        vaadin.switch.createSwitch { label = tr("labelsAside（标签在左侧）", "labelsAside (labels on the left)") }
    labelsSwitch.listen(vaadin.switch.SwitchEvents.checkedChanged) {
        controlled.labelsAside = labelsSwitch.checked
        readout.set("labelsAside = ${controlled.labelsAside}")
    }
    responsive.body.append(example(null, column(controlled, labelsSwitch, readout.root)))
    view.body.append(responsive.root)

    return view
}

private fun buildSplitLayoutPage(meta: PageMeta): PageView {
    val view = page(meta)

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr("两个子元素之间会出现可拖动的分隔条。", "A draggable divider appears between the two children."),
        )
    val horizontal = createSplitLayout()
    horizontal.style.setProperty("height", "10rem")
    horizontal.append(panel(tr("左侧", "Left side")), panel(tr("右侧", "Right side")))

    val vertical = createSplitLayout { orientation = "vertical" }
    vertical.style.setProperty("height", "14rem")
    vertical.append(panel(tr("上方", "Top")), panel(tr("下方", "Bottom")))

    basic.body.append(example("horizontal", horizontal), example("vertical", vertical))
    view.body.append(basic.root)

    return view
}

private fun buildScrollerPage(meta: PageMeta): PageView {
    val view = page(meta)

    val vertical =
        section(
            tr("纵向滚动", "Vertical scrolling"),
            tr("内容超出固定高度时出现滚动条。", "A scrollbar appears when the content exceeds the fixed height."),
        )
    val scroller = createScroller()
    scroller.dataset["test"] = "scroller"
    scroller.style.setProperty("height", "10rem")
    scroller.append(
        column(*(1..20).map { paragraph(tr("第 $it 行内容", "Row $it content")) }.toTypedArray()),
    )
    vertical.body.append(example(null, scroller))
    view.body.append(vertical.root)

    val horizontal =
        section(
            tr("横向滚动", "Horizontal scrolling"),
            tr(
                "scrollDirection 设为 horizontal 后只允许横向滚动。",
                "With scrollDirection set to horizontal only horizontal scrolling is allowed.",
            ),
        )
    val horizontalScroller = createScroller { scrollDirection = "horizontal" }
    horizontalScroller.style.setProperty("height", "6rem")
    val wideRow = row(*(1..15).map { box(tr("列 $it", "Column $it")) }.toTypedArray())
    wideRow.style.setProperty("flex-wrap", "nowrap")
    horizontalScroller.append(wideRow)
    horizontal.body.append(example(null, horizontalScroller))
    view.body.append(horizontal.root)

    return view
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
    val vertical = createTabs { orientation = "vertical" }
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
