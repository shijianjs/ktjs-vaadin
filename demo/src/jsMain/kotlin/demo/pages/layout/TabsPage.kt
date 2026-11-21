package demo.pages.layout

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import web.html.HTMLElement
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.CatalogEntry
import demo.Catalog
import demo.Groups
import demo.bi
import demo.ui.EventLog
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.listen
import io.github.shijianjs.ktjs.vaadin.a11ybase.ListOrientations
import io.github.shijianjs.ktjs.vaadin.badge.createBadge
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.tabs.TabElement
import io.github.shijianjs.ktjs.vaadin.tabs.TabsElement
import io.github.shijianjs.ktjs.vaadin.tabs.TabsEvents
import io.github.shijianjs.ktjs.vaadin.tabs.createTab
import io.github.shijianjs.ktjs.vaadin.tabs.createTabs
import web.cssom.ClassName

internal class TabsPage : DemoPage() {
    private val texts = Binder(tabsTexts)

    private val frame = PageFrame(ENTRY)

    private val log = EventLog(4)

    private val contentTabs = createTabs()

    private val contentPages: MutableList<Pair<String, (TabsTexts) -> String>> = mutableListOf()

    private val content = HtmlTagName.p().apply { className = TABS_CONTENT_TEXT }

    init {
        frame.mount(this)
        registerVaadinIcons()

        fun tab(copy: (TabsTexts) -> String): TabElement =
            createTab().also { node -> texts.bind(node, copy) { node.textContent = it } }

        fun tabsOf(
            vararg copy: (TabsTexts) -> String,
            configure: TabsElement.() -> Unit = {},
        ): TabsElement =
            createTabs(configure).apply { copy.forEach { append(tab(it)) } }

        fun labelledTabs(
            vararg copy: (TabsTexts) -> String,
            configure: TabsElement.() -> Unit = {},
        ): TabsElement = tabsOf(*copy, configure = configure)

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val tabs = tabsOf({ it.details }, { it.payment }, { it.shipping })
        tabs.dataset["test"] = "tabs"
        log.placeholder("")
        tabs.listen(TabsEvents.selectedChanged) {
            log.add(texts.current.selectedChanged(tabs.selected.toInt()))
        }
        basic.body.append(exampleBox(null, kitCol(tabs, log.root)))
        body.append(basic.root)

        val states = SectionBlock.NAME { show() }
        texts.bind(states.heading) { it.statesHeading }
        texts.bind(states.hint) { it.statesHint }
        val stateTabs = tabsOf({ it.selected }, { it.unselected })
        stateTabs.dataset["test"] = "tabs-states"
        stateTabs.append(tab { it.disabled }.also { it.disabled = true })
        states.body.append(exampleBox(null, stateTabs))
        body.append(states.root)

        val orientationSection = SectionBlock.NAME { show() }
        texts.bind(orientationSection.heading) { it.orientationHeading }
        texts.bind(orientationSection.hint) { it.orientationHint }
        val horizontalTabs =
            tabsOf({ it.analytics }, { it.customers }, { it.dashboards }, { it.documents }, { it.orders }).apply {
                dataset["test"] = "tabs-horizontal"
                style.setProperty("max-width", "100%")
                style.setProperty("width", "400px")
            }
        val hiddenScrollTabs =
            labelledTabs(
                { it.analytics },
                { it.customers },
                { it.dashboards },
                { it.documents },
                { it.orders },
                { it.products },
                { it.tasks },
            ).apply {
                addTheme("hide-scroll-buttons")
                dataset["test"] = "tabs-hide-scroll-buttons"
                style.setProperty("max-width", "100%")
                style.setProperty("width", "400px")
            }
        val verticalTabs =
            labelledTabs(
                { it.analytics },
                { it.customers },
                { it.dashboards },
                { it.documents },
                { it.orders },
                { it.products },
                { it.tasks },
                configure = { this.orientation = ListOrientations.vertical },
            ).apply {
                dataset["test"] = "tabs-vertical"
                style.setProperty("height", "240px")
                style.setProperty("width", "240px")
            }
        orientationSection.body.append(
            exampleBox(
                null,
                newCaption { it.horizontalCaption },
                horizontalTabs,
            ),
            exampleBox(
                null,
                newCaption { it.hiddenCaptions },
                hiddenScrollTabs,
            ),
            exampleBox(
                null,
                newCaption { it.verticalCaption },
                verticalTabs,
            ),
        )
        body.append(orientationSection.root)

        val icons = SectionBlock.NAME { show() }
        texts.bind(icons.heading) { it.iconsHeading }
        texts.bind(icons.hint) { it.iconsHint }
        fun iconTab(icon: String, copy: (TabsTexts) -> String, onTop: Boolean): TabElement =
            createTab().apply {
                if (onTop) addTheme("icon-on-top")
                val span = HtmlTagName.span()
                texts.bind(span, copy) { span.textContent = it }
                append(createIcon { this.icon = icon }, span)
            }
        val iconEntries: List<Pair<String, (TabsTexts) -> String>> =
            listOf(
                "vaadin:user" to { copy: TabsTexts -> copy.profile },
                "vaadin:cog" to { copy: TabsTexts -> copy.settings },
                "vaadin:bell" to { copy: TabsTexts -> copy.notifications },
            )
        val iconTabs = createTabs()
        iconTabs.dataset["test"] = "tabs-icons"
        val verticalIconTabs = createTabs { orientation = ListOrientations.vertical }
        verticalIconTabs.dataset["test"] = "tabs-icons-vertical"
        iconEntries.forEach { (icon, copy) ->
            iconTabs.append(iconTab(icon, copy, onTop = true))
            verticalIconTabs.append(iconTab(icon, copy, onTop = false))
        }
        val badgeTabs = createTabs()
        badgeTabs.dataset["test"] = "tabs-badges"
        listOf(
            ({ copy: TabsTexts -> copy.open }) to 24.0,
            ({ copy: TabsTexts -> copy.completed }) to 49.0,
            ({ copy: TabsTexts -> copy.cancelled }) to 5.0,
        ).forEach { (copy, count) ->
            val span = HtmlTagName.span()
            texts.bind(span, copy) { span.textContent = it }
            badgeTabs.append(
                createTab {
                    append(span, createBadge { number = count }.addTheme("filled"))
                },
            )
        }
        icons.body.append(
            exampleBox(null, newCaption { it.iconsHorizontalCaption }, iconTabs),
            exampleBox(null, newCaption { it.iconsVerticalCaption }, verticalIconTabs),
            exampleBox(null, newCaption { it.badgesCaption }, badgeTabs),
        )
        body.append(icons.root)

        val themes = SectionBlock.NAME { show() }
        texts.bind(themes.heading) { it.themesHeading }
        texts.bind(themes.hint) { it.themesHint }
        listOf("centered", "equal-width-tabs", "minimal", "small").forEach { theme ->
            themes.body.append(
                exampleBox(
                    theme,
                    tabsOf({ it.details }, { it.payment }, { it.shipping }).apply {
                        addTheme(theme)
                        dataset["test"] = "tabs-theme-$theme"
                    },
                ),
            )
        }
        body.append(themes.root)

        val focus = SectionBlock.NAME { show() }
        texts.bind(focus.heading) { it.focusHeading }
        texts.bind(focus.hint) { it.focusHint }
        val focusTabs = tabsOf({ it.details }, { it.payment }, { it.shipping })
        focusTabs.dataset["test"] = "tabs-focus-ring"
        focusTabs.firstElementChild?.setAttribute("focus-ring", "")
        focus.body.append(exampleBox(null, focusTabs))
        body.append(focus.root)

        val switching = SectionBlock.NAME { show() }
        texts.bind(switching.heading) { it.switchingHeading }
        texts.bind(switching.hint) { it.switchingHint }
        contentTabs.dataset["test"] = "tabs-content"
        contentPages += "dashboard" to { copy: TabsTexts -> copy.dashboard }
        contentPages += "payment" to { copy: TabsTexts -> copy.payment }
        contentPages += "shipping" to { copy: TabsTexts -> copy.shipping }
        contentPages.forEach { (_, copy) -> contentTabs.append(tab(copy)) }
        contentTabs.listen(TabsEvents.selectedChanged) { showContent() }
        switching.body.append(exampleBox(null, kitCol(contentTabs, content)))
        body.append(switching.root)

        applyTranslation(Langs.EN)
    }

    /** A paragraph above an example, used where the caption is copy rather than a CSS snippet. */
    private fun newCaption(copy: (TabsTexts) -> String): HTMLElement =
        HtmlTagName.p().apply {
            style.setProperty("margin", "0 0 0.75rem")
            style.setProperty("font-size", "0.8125rem")
            style.setProperty("color", "var(--lumo-secondary-text-color)")
            texts.bind(this, copy) { textContent = it }
        }

    override fun applyTranslation(lang: Lang) {
        val meta = ENTRY.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showContent()
    }

    private fun showContent() {
        val copy = texts.current
        val index = contentTabs.selected.toInt()
        val name = contentPages.getOrNull(index)?.let { it.second(copy) }
        content.textContent = if (name == null) copy.dashboardContent else copy.contentFor(name)
    }

    companion object : TabsPageDefaultReference() {
        val ENTRY: CatalogEntry =
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
                        docsUrl = "${Catalog.DOCS_BASE}/tabs",
                        sourcePath = "pages/layout/TabsPage.kt",
                    ),
                kitPage = { TabsPage.NAME() },
            )

        private val english: TabsTexts =
            TabsTexts(
                basicHeading = "Basic usage",
                basicHint = "Tabs group content into sections the user can switch between in place, without " +
                    "loading another page. selected holds the active index and selected-changed fires on every " +
                    "switch.",
                details = "Details",
                payment = "Payment",
                shipping = "Shipping",
                selectedChanged = { "selected-changed · $it" },
                statesHeading = "States",
                statesHint = "A tab is selected, unselected or disabled. Disabled tabs cannot be focused and " +
                    "keyboard navigation skips them; disabling instead of hiding keeps the layout stable and still " +
                    "tells users that the item exists.",
                selected = "Selected",
                unselected = "Unselected",
                disabled = "Disabled",
                orientationHeading = "Orientation and overflow",
                orientationHint = "Horizontal tabs suit a small number of items; on overflow they scroll and show " +
                    "scroll buttons on both ends by default, while hide-scroll-buttons removes them (not recommended " +
                    "on non-touch devices). Vertical tabs have no scroll buttons and are easier to scan when there " +
                    "are many items, but Tab Sheets do not support them.",
                analytics = "Analytics",
                customers = "Customers",
                dashboards = "Dashboards",
                documents = "Documents",
                orders = "Orders",
                products = "Products",
                tasks = "Tasks",
                horizontalCaption = "Horizontal: scroll buttons appear on overflow",
                hiddenCaptions = "Scroll buttons hidden (hide-scroll-buttons)",
                verticalCaption = "Vertical: no scroll buttons",
                iconsHeading = "Icons and other content",
                iconsHint = "A tab can hold icons, badges and almost any other element. Horizontal tabs work best " +
                    "with the icon above the label (icon-on-top, Lumo only), vertical tabs with the icon beside it; " +
                    "icon-only labels need a tooltip or an aria-label.",
                profile = "Profile",
                settings = "Settings",
                notifications = "Notifications",
                iconsHorizontalCaption = "Horizontal tabs: icon above the label",
                iconsVerticalCaption = "Vertical tabs: icon beside the label",
                badgesCaption = "Other elements: badges",
                open = "Open",
                completed = "Completed",
                cancelled = "Cancelled",
                themesHeading = "Theme variants",
                themesHint = "centered arranges the tabs in the middle, equal-width-tabs gives every tab the same " +
                    "width, minimal drops the tab bar background and small reduces the size.",
                focusHeading = "Focus and keyboard",
                focusHint = "Keyboard focus draws the focus ring (focus-ring). Arrow keys move the focus between " +
                    "tabs and Enter or Space selects the focused tab; disabled tabs are skipped.",
                switchingHeading = "Content switching without Tab Sheet",
                switchingHint = "When the tabs must be structurally separated from their content, switch the " +
                    "content on selected-changed yourself; when they can stay together, a Tab Sheet owns that logic.",
                dashboard = "Dashboard",
                dashboardContent = "This is the Dashboard content.",
                contentFor = { "This is the $it content." },
            )
        private val chinese: TabsTexts =
            TabsTexts(
                basicHeading = "基本用法",
                basicHint = "标签页把内容分组到几个可切换的小节里，适合在同一块界面里就地切换、而不必加载新页面。" +
                    "selected 是当前下标，切换时触发 selected-changed。",
                details = "明细",
                payment = "支付",
                shipping = "配送",
                selectedChanged = { "selected-changed · $it" },
                statesHeading = "状态",
                statesHint = "tab 可以是选中、未选中或禁用。禁用的 tab 无法聚焦，键盘导航会跳过它；用禁用代替隐藏" +
                    "既能避免布局跳动，也让用户知道该项存在。",
                selected = "已选中",
                unselected = "未选中",
                disabled = "已禁用",
                orientationHeading = "方向与溢出",
                orientationHint = "横向标签适合少量条目，溢出时可以横向滚动，默认在两端显示滚动按钮；" +
                    "hide-scroll-buttons 去掉这些按钮（非触屏设备上不建议）。纵向标签没有滚动按钮，条目多时更容易" +
                    "扫视，但 Tab Sheet 不支持纵向。",
                analytics = "分析",
                customers = "客户",
                dashboards = "仪表盘",
                documents = "文档",
                orders = "订单",
                products = "产品",
                tasks = "任务",
                horizontalCaption = "横向：溢出时显示滚动按钮",
                hiddenCaptions = "隐藏滚动按钮（hide-scroll-buttons）",
                verticalCaption = "纵向：没有滚动按钮",
                iconsHeading = "图标与其他内容",
                iconsHint = "tab 里可以放图标、徽标等几乎任意元素。横向标签通常把图标放在文字上方（icon-on-top，" +
                    "仅 Lumo），纵向标签则图标与文字并排；只有图标时要用 Tooltip 或 aria-label 补上文字说明。",
                profile = "个人资料",
                settings = "设置",
                notifications = "通知",
                iconsHorizontalCaption = "横向标签：图标在文字上方",
                iconsVerticalCaption = "纵向标签：图标与文字并排",
                badgesCaption = "其他元素：徽标",
                open = "待处理",
                completed = "已完成",
                cancelled = "已取消",
                themesHeading = "主题变体",
                themesHint = "centered 让标签居中排列，equal-width-tabs 让各标签等宽，minimal 去掉标签栏背景，" +
                    "small 缩小尺寸。",
                focusHeading = "焦点与键盘",
                focusHint = "键盘聚焦时 tab 显示焦点环（focus-ring）。聚焦后左右方向键在 tab 之间移动焦点，" +
                    "Enter 或空格选中当前聚焦的 tab；禁用的 tab 会被跳过。",
                switchingHeading = "不使用 Tab Sheet 切换内容",
                switchingHint = "结构上必须把标签与内容分开时，用 selected-changed 自己切换内容；能放在一起时" +
                    "优先用 Tab Sheet，切换逻辑由组件负责。",
                dashboard = "仪表盘",
                dashboardContent = "这是仪表盘的内容。",
                contentFor = { "这是「$it」的内容。" },
            )
        private val TABS_CONTENT_TEXT: ClassName = ClassName("tabs-content__text")
        private val tabsTexts: Translations<TabsTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
