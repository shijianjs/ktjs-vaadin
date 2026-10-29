package demo.pages.layout

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.column
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.paragraph
import demo.ui.section
import demo.ui.span
import demo.ui.withTheme
import vaadin.a11ybase.ListOrientations
import vaadin.badge.createBadge
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.tabs.TabsElement
import vaadin.tabs.TabsEvents
import vaadin.tabs.createTab
import vaadin.tabs.createTabs

val tabsEntry: CatalogEntry =
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
                sourcePath = "pages/layout/TabsPage.kt",
            ),
        builder = ::buildTabsPage,
    )

private fun buildTabsPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "标签页把内容分组到几个可切换的小节里，适合在同一块界面里就地切换、而不必加载新页面。" +
                    "selected 是当前下标，切换时触发 selected-changed。",
                "Tabs group content into sections the user can switch between in place, without loading another " +
                    "page. selected holds the active index and selected-changed fires on every switch.",
            ),
        )
    val log = EventLog(4)
    val tabs = tabsOf(tr("明细", "Details"), tr("支付", "Payment"), tr("配送", "Shipping"))
    tabs.dataset["test"] = "tabs"
    tabs.listen(TabsEvents.selectedChanged) { log.add("selected-changed · ${tabs.selected}") }
    basic.body.append(example(null, column(tabs, log.root)))
    view.body.append(basic.root)

    val states =
        section(
            tr("状态", "States"),
            tr(
                "tab 可以是选中、未选中或禁用。禁用的 tab 无法聚焦，键盘导航会跳过它；用禁用代替隐藏既能避免" +
                    "布局跳动，也让用户知道该项存在。",
                "A tab is selected, unselected or disabled. Disabled tabs cannot be focused and keyboard " +
                    "navigation skips them; disabling instead of hiding keeps the layout stable and still " +
                    "tells users that the item exists.",
            ),
        )
    val stateTabs = tabsOf(tr("已选中", "Selected"), tr("未选中", "Unselected"))
    stateTabs.dataset["test"] = "tabs-states"
    stateTabs.append(
        createTab {
            textContent = tr("已禁用", "Disabled")
            disabled = true
        },
    )
    states.body.append(example(null, stateTabs))
    view.body.append(states.root)

    val orientation =
        section(
            tr("方向与溢出", "Orientation and overflow"),
            tr(
                "横向标签适合少量条目，溢出时可以横向滚动，默认在两端显示滚动按钮；hide-scroll-buttons 去掉这些" +
                    "按钮（非触屏设备上不建议）。纵向标签没有滚动按钮，条目多时更容易扫视，但 Tab Sheet 不支持纵向。",
                "Horizontal tabs suit a small number of items; on overflow they scroll and show scroll buttons " +
                    "on both ends by default, while hide-scroll-buttons removes them (not recommended on " +
                    "non-touch devices). Vertical tabs have no scroll buttons and are easier to scan when there " +
                    "are many items, but Tab Sheets do not support them.",
            ),
        )
    val horizontalTabs =
        tabsOf(
            tr("分析", "Analytics"),
            tr("客户", "Customers"),
            tr("仪表盘", "Dashboards"),
            tr("文档", "Documents"),
            tr("订单", "Orders"),
        ).apply {
            dataset["test"] = "tabs-horizontal"
            style.setProperty("max-width", "100%")
            style.setProperty("width", "400px")
        }
    val hiddenScrollTabs =
        tabsOf(
            tr("分析", "Analytics"),
            tr("客户", "Customers"),
            tr("仪表盘", "Dashboards"),
            tr("文档", "Documents"),
            tr("订单", "Orders"),
            tr("产品", "Products"),
            tr("任务", "Tasks"),
        ).apply {
            withTheme("hide-scroll-buttons")
            dataset["test"] = "tabs-hide-scroll-buttons"
            style.setProperty("max-width", "100%")
            style.setProperty("width", "400px")
        }
    val verticalTabs =
        tabsOf(
            tr("分析", "Analytics"),
            tr("客户", "Customers"),
            tr("仪表盘", "Dashboards"),
            tr("文档", "Documents"),
            tr("订单", "Orders"),
            tr("产品", "Products"),
            tr("任务", "Tasks"),
            configure = { this.orientation = ListOrientations.vertical },
        ).apply {
            dataset["test"] = "tabs-vertical"
            style.setProperty("height", "240px")
            style.setProperty("width", "240px")
        }
    orientation.body.append(
        example(tr("横向：溢出时显示滚动按钮", "Horizontal: scroll buttons appear on overflow"), horizontalTabs),
        example(tr("隐藏滚动按钮（hide-scroll-buttons）", "Scroll buttons hidden (hide-scroll-buttons)"), hiddenScrollTabs),
        example(tr("纵向：没有滚动按钮", "Vertical: no scroll buttons"), verticalTabs),
    )
    view.body.append(orientation.root)

    val icons =
        section(
            tr("图标与其他内容", "Icons and other content"),
            tr(
                "tab 里可以放图标、徽标等几乎任意元素。横向标签通常把图标放在文字上方（icon-on-top，仅 Lumo），" +
                    "纵向标签则图标与文字并排；只有图标时要用 Tooltip 或 aria-label 补上文字说明。",
                "A tab can hold icons, badges and almost any other element. Horizontal tabs work best with the " +
                    "icon above the label (icon-on-top, Lumo only), vertical tabs with the icon beside it; " +
                    "icon-only labels need a tooltip or an aria-label.",
            ),
        )
    val iconTabs = createTabs()
    iconTabs.dataset["test"] = "tabs-icons"
    iconTabEntries().forEach { (iconName, label) ->
        iconTabs.append(
            createTab {
                withTheme("icon-on-top")
                append(createIcon { icon = iconName }, span(label))
            },
        )
    }
    val verticalIconTabs = createTabs { this.orientation = ListOrientations.vertical }
    verticalIconTabs.dataset["test"] = "tabs-icons-vertical"
    iconTabEntries().forEach { (iconName, label) ->
        verticalIconTabs.append(createTab { append(createIcon { icon = iconName }, span(label)) })
    }
    val badgeTabs = createTabs()
    badgeTabs.dataset["test"] = "tabs-badges"
    listOf(
        tr("待处理", "Open") to 24.0,
        tr("已完成", "Completed") to 49.0,
        tr("已取消", "Cancelled") to 5.0,
    ).forEach { (label, count) ->
        badgeTabs.append(
            createTab { append(span(label), createBadge { number = count }.withTheme("filled")) },
        )
    }
    icons.body.append(
        example(tr("横向标签：图标在文字上方", "Horizontal tabs: icon above the label"), iconTabs),
        example(tr("纵向标签：图标与文字并排", "Vertical tabs: icon beside the label"), verticalIconTabs),
        example(tr("其他元素：徽标", "Other elements: badges"), badgeTabs),
    )
    view.body.append(icons.root)

    val themes =
        section(
            tr("主题变体", "Theme variants"),
            tr(
                "centered 让标签居中排列，equal-width-tabs 让各标签等宽，minimal 去掉标签栏背景，small 缩小尺寸。",
                "centered arranges the tabs in the middle, equal-width-tabs gives every tab the same width, " +
                    "minimal drops the tab bar background and small reduces the size.",
            ),
        )
    val themeVariants =
        listOf(
            "centered" to "centered",
            "equal-width-tabs" to "equal-width-tabs",
            "minimal" to "minimal",
            "small" to "small",
        )
    themes.body.append(
        *themeVariants
            .map { (theme, label) ->
                example(
                    label,
                    tabsOf(tr("明细", "Details"), tr("支付", "Payment"), tr("配送", "Shipping")).apply {
                        withTheme(theme)
                        dataset["test"] = "tabs-theme-$theme"
                    },
                )
            }
            .toTypedArray(),
    )
    view.body.append(themes.root)

    val focus =
        section(
            tr("焦点与键盘", "Focus and keyboard"),
            tr(
                "键盘聚焦时 tab 显示焦点环（focus-ring）。聚焦后左右方向键在 tab 之间移动焦点，Enter 或空格选中" +
                    "当前聚焦的 tab；禁用的 tab 会被跳过。",
                "Keyboard focus draws the focus ring (focus-ring). Arrow keys move the focus between tabs and " +
                    "Enter or Space selects the focused tab; disabled tabs are skipped.",
            ),
        )
    val focusTabs = tabsOf(tr("明细", "Details"), tr("支付", "Payment"), tr("配送", "Shipping"))
    focusTabs.dataset["test"] = "tabs-focus-ring"
    focusTabs.firstElementChild?.setAttribute("focus-ring", "")
    focus.body.append(example(null, focusTabs))
    view.body.append(focus.root)

    val switching =
        section(
            tr("不使用 Tab Sheet 切换内容", "Content switching without Tab Sheet"),
            tr(
                "结构上必须把标签与内容分开时，用 selected-changed 自己切换内容；能放在一起时优先用 Tab Sheet，" +
                    "切换逻辑由组件负责。",
                "When the tabs must be structurally separated from their content, switch the content on " +
                    "selected-changed yourself; when they can stay together, a Tab Sheet owns that logic.",
            ),
        )
    val pageNames = listOf(tr("仪表盘", "Dashboard"), tr("支付", "Payment"), tr("配送", "Shipping"))
    val contentTabs = tabsOf(*pageNames.toTypedArray())
    contentTabs.dataset["test"] = "tabs-content"
    val content = paragraph(tr("这是仪表盘的内容。", "This is the Dashboard content."), "tabs-content__text")
    contentTabs.listen(TabsEvents.selectedChanged) {
        val name = pageNames[contentTabs.selected.toInt()]
        content.textContent = tr("这是“$name”的内容。", "This is the $name content.")
    }
    switching.body.append(example(null, column(contentTabs, content)))
    view.body.append(switching.root)

    return view
}

/** One plain tab per label. */
private fun tabsOf(vararg labels: String, configure: TabsElement.() -> Unit = {}): TabsElement =
    createTabs(configure).apply {
        labels.forEach { label -> append(createTab { textContent = label }) }
    }

/** The icon/label pairs shared by the two icon examples. */
private fun iconTabEntries(): List<Pair<String, String>> =
    listOf(
        "vaadin:user" to tr("个人资料", "Profile"),
        "vaadin:cog" to tr("设置", "Settings"),
        "vaadin:bell" to tr("通知", "Notifications"),
    )
