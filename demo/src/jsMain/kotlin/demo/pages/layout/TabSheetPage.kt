package demo.pages.layout

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.tabs.createTab
import io.github.shijianjs.ktjs.vaadin.tabs.createTabs
import io.github.shijianjs.ktjs.vaadin.tabsheet.TabSheetEvents
import io.github.shijianjs.ktjs.vaadin.tabsheet.createTabSheet
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
        kitPage = { TabSheetPage.NAME() },
    )

private val english: TabSheetTexts = TabSheetTexts(
    basicHeading = "Basic usage",
    basicHint = "A Tab Sheet binds tabs to content panels: vaadin-tabs goes into slot=\"tabs\" and each panel points " +
        "at a tab id through its tab attribute; the component handles the switch. When the selected tab has no " +
        "matching panel the component enters a loading state.",
    lazyHeading = "Lazy initialization of tab contents",
    lazyHint = "Create a panel only when its tab is visited: prepare the tab ids up front and append the matching " +
        "panel the first time the tab is selected. The upstream example also shows a loading indicator; this demo " +
        "focuses on the on-demand creation.",
    prefixHeading = "Prefix and suffix",
    prefixHint = "A Tab Sheet exposes prefix / suffix slots for custom content at both ends of the tab bar.",
    themeHeading = "Theme",
    themeHint = "bordered is the Tab Sheet theme variant that draws a border around the tab bar and content.",
    tabLabels = listOf("Dashboard", "Payment", "Shipping"),
    panelContents = listOf(
        "This is the Dashboard tab content",
        "This is the Payment tab content",
        "This is the Shipping tab content",
    ),
    closeAll = "Close all",
    addTab = "Add tab",
)

private val chinese: TabSheetTexts = TabSheetTexts(
    basicHeading = "基本用法",
    basicHint = "Tab Sheet 把标签与内容面板绑在一起：vaadin-tabs 放进 slot=\"tabs\"，每个面板用 tab 属性指向" +
        "对应 tab 的 id，切换由组件完成。选中的 tab 找不到面板时组件进入 loading 状态。",
    lazyHeading = "内容懒初始化",
    lazyHint = "只为访问过的 tab 创建面板：先准备好 tab 的 id，第一次选中时才把对应面板加进 Tab Sheet。" +
        "上游示例还演示了加载指示器，这里只演示按需创建。",
    prefixHeading = "前缀与后缀",
    prefixHint = "Tab Sheet 提供 prefix / suffix 插槽，把按钮等自定义内容放在标签栏两端。",
    themeHeading = "主题",
    themeHint = "bordered 是 Tab Sheet 的主题变体，会在标签栏与内容区外画一圈边框。",
    tabLabels = listOf("仪表盘", "支付", "配送"),
    panelContents = listOf("这是仪表盘面板的内容。", "这是支付面板的内容。", "这是配送面板的内容。"),
    closeAll = "全部关闭",
    addTab = "新增标签",
)

internal class TabSheetPage : DemoPage() {
    private val texts = Binder(tabSheetTexts)

    private val frame = PageFrame(tabsheetEntry)

    /** The basic, prefixed and bordered examples share the same three tabs and panels. */
    private val basicParts = demoTabSheet("tabsheet-basic")
    private val lazyParts = demoTabSheet("tabsheet-lazy")
    private val adornedParts = demoTabSheet("tabsheet-adorned")
    private val borderedParts = demoTabSheet("tabsheet-bordered", "bordered")

    private val closeAll: ButtonElement = createButton()

    /** The icon-only suffix button: its `aria-label` is an attribute, not text. */
    private val addTab: ButtonElement = createButton()

    init {
        frame.mount(this)
        registerVaadinIcons()

        basicParts.sheet.dataset["test"] = "tab-sheet"
        lazyParts.sheet.dataset["test"] = "tab-sheet-lazy"

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val selected = Readout("selected = 0")
        basicParts.sheet.listen(TabSheetEvents.selectedChanged) {
            selected.set("selected = ${basicParts.sheet.selected}")
        }
        basic.body.append(exampleBox(null, kitCol(basicParts.sheet, selected.root)))
        body.append(basic.root)

        val lazy = SectionBlock.NAME { show() }
        texts.bind(lazy.heading) { it.lazyHeading }
        texts.bind(lazy.hint) { it.lazyHint }
        val visited = mutableSetOf(0)
        val visitedReadout = Readout("visited = [0]")
        lazyParts.sheet.listen(TabSheetEvents.selectedChanged) {
            val index = lazyParts.sheet.selected.toInt()
            val panel = lazyParts.panels.getOrNull(index)
            if (panel != null && visited.add(index)) lazyParts.sheet.append(panel)
            visitedReadout.set("visited = ${visited.sorted().joinToString(", ", "[", "]")}")
        }
        // The initially selected tab counts as visited; the others materialize on first selection.
        lazyParts.sheet.append(lazyParts.panels[0])
        lazy.body.append(exampleBox(null, kitCol(lazyParts.sheet, visitedReadout.root)))
        body.append(lazy.root)

        val prefixSuffix = SectionBlock.NAME { show() }
        texts.bind(prefixSuffix.heading) { it.prefixHeading }
        texts.bind(prefixSuffix.hint) { it.prefixHint }
        closeAll.slot = "prefix"
        adornedParts.sheet.append(closeAll)
        addTab.slot = "suffix"
        addTab.addTheme("icon")
        addTab.append(createIcon { icon = "vaadin:plus" })
        adornedParts.sheet.append(addTab)
        prefixSuffix.body.append(exampleBox(null, adornedParts.sheet))
        body.append(prefixSuffix.root)

        val bordered = SectionBlock.NAME { show() }
        texts.bind(bordered.heading) { it.themeHeading }
        texts.bind(bordered.hint) { it.themeHint }
        bordered.body.append(exampleBox(null, borderedParts.sheet))
        body.append(bordered.root)

        listOf(basicParts, lazyParts, adornedParts, borderedParts).forEach { parts ->
            parts.tabs.forEachIndexed { index, tab ->
                texts.bind(tab) { copy -> copy.tabLabels[index] }
            }
            parts.panels.forEachIndexed { index, panel ->
                texts.bind(panel) { copy -> copy.panelContents[index] }
            }
        }

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = tabsheetEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        val copy = texts.current
        closeAll.textContent = copy.closeAll
        addTab.setAttribute("aria-label", copy.addTab)
    }

    companion object : CustomElementReferenceAdapter<TabSheetPage>(TabSheetPage::class.js, "vaadin-demo-tab-sheet-page") {
        private val tabSheetTexts: Translations<TabSheetTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
        /** A three-tab Tab Sheet with one panel per tab; [key] keeps the tab ids unique per example. */
        private fun demoTabSheet(key: String, theme: String? = null): SheetParts {
            val sheet = createTabSheet()
            if (theme != null) sheet.addTheme(theme)
            val ids = listOf("$key-dashboard", "$key-payment", "$key-shipping")
            val tabs = ids.map { id -> createTab().apply { this.id = ElementId(id) } }
            val tabsBar = createTabs { slot = "tabs" }
            tabs.forEach { tabsBar.append(it) }
            sheet.append(tabsBar)
            val panels =
                ids.map { id ->
                    HtmlTagName.div().apply { setAttribute("tab", id) }
                }
            panels.forEach { sheet.append(it) }
            return SheetParts(sheet, tabs, panels)
        }
    }
}
