package demo.pages.layout

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.column
import demo.ui.div
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.section
import demo.ui.withTheme
import vaadin.button.createButton
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.tabs.createTab
import vaadin.tabs.createTabs
import vaadin.tabsheet.TabSheetElement
import vaadin.tabsheet.TabSheetEvents
import vaadin.tabsheet.createTabSheet
import web.dom.ElementId

val tabsheetEntry: CatalogEntry =
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
                sourcePath = "pages/layout/TabSheetPage.kt",
            ),
        builder = ::buildTabSheetPage,
    )

private fun buildTabSheetPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "Tab Sheet 把标签与内容面板绑在一起：vaadin-tabs 放进 slot=\"tabs\"，每个面板用 tab 属性指向" +
                    "对应 tab 的 id，切换由组件完成。选中的 tab 找不到面板时组件进入 loading 状态。",
                "A Tab Sheet binds tabs to content panels: vaadin-tabs goes into slot=\"tabs\" and each panel " +
                    "points at a tab id through its tab attribute; the component handles the switch. When the " +
                    "selected tab has no matching panel the component enters a loading state.",
            ),
        )
    val sheet = demoTabSheet("tabsheet-basic")
    sheet.dataset["test"] = "tab-sheet"
    val selected = Readout("selected = 0")
    sheet.listen(TabSheetEvents.selectedChanged) { selected.set("selected = ${sheet.selected}") }
    basic.body.append(example(null, column(sheet, selected.root)))
    view.body.append(basic.root)

    val lazy =
        section(
            tr("内容懒初始化", "Lazy initialization of tab contents"),
            tr(
                "只为访问过的 tab 创建面板：先准备好 tab 的 id，第一次选中时才把对应面板加进 Tab Sheet。" +
                    "上游示例还演示了加载指示器，这里只演示按需创建。",
                "Create a panel only when its tab is visited: prepare the tab ids up front and append the " +
                    "matching panel the first time the tab is selected. The upstream example also shows a " +
                    "loading indicator; this demo focuses on the on-demand creation.",
            ),
        )
    val lazySheet = createTabSheet()
    lazySheet.dataset["test"] = "tab-sheet-lazy"
    val lazyIds = listOf("tabsheet-lazy-dashboard", "tabsheet-lazy-payment", "tabsheet-lazy-shipping")
    val lazyTabs = createTabs { slot = "tabs" }
    listOf(tr("仪表盘", "Dashboard"), tr("支付", "Payment"), tr("配送", "Shipping")).forEachIndexed { index, label ->
        lazyTabs.append(createTab { textContent = label }.apply { id = ElementId(lazyIds[index]) })
    }
    val lazyPanels =
        listOf(
            tr("这是仪表盘面板的内容。", "This is the Dashboard tab content"),
            tr("这是支付面板的内容。", "This is the Payment tab content"),
            tr("这是配送面板的内容。", "This is the Shipping tab content"),
        ).mapIndexed { index, text ->
            div().apply {
                setAttribute("tab", lazyIds[index])
                textContent = text
            }
        }
    lazySheet.append(lazyTabs, lazyPanels[0])
    // The initially selected tab counts as visited; the others materialize on first selection.
    val visited = mutableSetOf(0)
    val visitedReadout = Readout("visited = [0]")
    lazySheet.listen(TabSheetEvents.selectedChanged) {
        val index = lazySheet.selected.toInt()
        if (index in lazyPanels.indices && visited.add(index)) lazySheet.append(lazyPanels[index])
        visitedReadout.set("visited = ${visited.sorted().joinToString(", ", "[", "]")}")
    }
    lazy.body.append(example(null, column(lazySheet, visitedReadout.root)))
    view.body.append(lazy.root)

    val prefixSuffix =
        section(
            tr("前缀与后缀", "Prefix and suffix"),
            tr(
                "Tab Sheet 提供 prefix / suffix 插槽，把按钮等自定义内容放在标签栏两端。",
                "A Tab Sheet exposes prefix / suffix slots for custom content at both ends of the tab bar.",
            ),
        )
    val adorned = demoTabSheet("tabsheet-adorned")
    adorned.append(createButton { textContent = tr("全部关闭", "Close all") }.apply { slot = "prefix" })
    adorned.append(
        createButton().apply {
            slot = "suffix"
            withTheme("icon")
            setAttribute("aria-label", tr("新增标签", "Add tab"))
            append(createIcon { icon = "vaadin:plus" })
        },
    )
    prefixSuffix.body.append(example(null, adorned))
    view.body.append(prefixSuffix.root)

    val bordered =
        section(
            tr("主题", "Theme"),
            tr(
                "bordered 是 Tab Sheet 的主题变体，会在标签栏与内容区外画一圈边框。",
                "bordered is the Tab Sheet theme variant that draws a border around the tab bar and content.",
            ),
        )
    bordered.body.append(example(null, demoTabSheet("tabsheet-bordered", "bordered")))
    view.body.append(bordered.root)

    return view
}

/** A three-tab Tab Sheet with one panel per tab; [key] keeps the tab ids unique per example. */
private fun demoTabSheet(key: String, theme: String? = null): TabSheetElement {
    val sheet = createTabSheet()
    if (theme != null) sheet.withTheme(theme)
    val ids = listOf("$key-dashboard", "$key-payment", "$key-shipping")
    val labels = listOf(tr("仪表盘", "Dashboard"), tr("支付", "Payment"), tr("配送", "Shipping"))
    val contents =
        listOf(
            tr("这是仪表盘面板的内容。", "This is the Dashboard tab content"),
            tr("这是支付面板的内容。", "This is the Payment tab content"),
            tr("这是配送面板的内容。", "This is the Shipping tab content"),
        )
    val tabs = createTabs { slot = "tabs" }
    labels.forEachIndexed { index, label ->
        tabs.append(createTab { textContent = label }.apply { id = ElementId(ids[index]) })
    }
    sheet.append(tabs)
    contents.forEachIndexed { index, text ->
        sheet.append(
            div().apply {
                setAttribute("tab", ids[index])
                textContent = text
            },
        )
    }
    return sheet
}
