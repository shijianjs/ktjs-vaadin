package demo.pages.layout

import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.DemoPage
import web.html.HTMLElement
import web.html.HtmlTagName
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import io.github.shijianjs.ktjs.demo.page.Blocks.bulletList
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.EventLog
import demo.ui.PageFrame
import demo.ui.PageMeta
import demo.ui.Readout
import demo.ui.listen
import io.github.shijianjs.ktjs.dom.css.cssSheet
import io.github.shijianjs.ktjs.dom.css.dsl.Pseudos
import io.github.shijianjs.ktjs.dom.css.dsl.className
import io.github.shijianjs.ktjs.dom.css.dsl.compound
import io.github.shijianjs.ktjs.dom.css.dsl.invoke
import io.github.shijianjs.ktjs.dom.css.dsl.child
import io.github.shijianjs.ktjs.dom.css.dsl.pseudo
import io.github.shijianjs.ktjs.dom.css.dsl.pseudoElement
import io.github.shijianjs.ktjs.dom.css.dsl.tag
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.VaadinTagName
import web.cssom.CSSStyleSheet
import web.cssom.ident
import web.cssom.ClassName
import web.cssom.Color
import io.github.shijianjs.ktjs.vaadin.badge.createBadge
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import io.github.shijianjs.ktjs.vaadin.scroller.ScrollerScrollDirections
import io.github.shijianjs.ktjs.vaadin.scroller.createScroller
import io.github.shijianjs.ktjs.vaadin.sidenav.SideNavElement
import io.github.shijianjs.ktjs.vaadin.sidenav.SideNavEvents
import io.github.shijianjs.ktjs.vaadin.sidenav.SideNavItemElement
import io.github.shijianjs.ktjs.vaadin.sidenav.createSideNav
import io.github.shijianjs.ktjs.vaadin.sidenav.createSideNavItem
import io.github.shijianjs.ktjs.vaadin.verticallayout.createVerticalLayout
import web.location.location

val sideNavEntry: CatalogEntry =
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
                sourcePath = "pages/layout/SideNavPage.kt",
            ),
        kitPage = { SideNavPage.NAME() },
    )

internal class SideNavPage : DemoPage() {
    private val texts = Binder(sideNavTexts)

    private val frame = PageFrame(sideNavEntry)

    private val log = EventLog(4)

    private val adminNav = createSideNav()

    private val collapsed = Readout("")

    private val keyboardList = HtmlTagName.div()

    init {
        frame.mount(this)
        addSheet(SIDE_NAV_SHEET)
        registerVaadinIcons()

        fun item(
            copy: (SideNavTexts) -> String,
            iconName: String? = null,
            path: String? = null,
            configure: SideNavItemElement.() -> Unit = {},
        ): SideNavItemElement {
            val node =
                createSideNavItem {
                    if (path != null) this.path = path
                    configure()
                }
            texts.bind(node, copy) { node.textContent = it }
            if (iconName != null) node.append(createIcon { icon = iconName }.apply { slot = "prefix" })
            return node
        }

        fun navOf(vararg items: SideNavItemElement, onPath: ((String) -> Unit)? = null): SideNavElement =
            createSideNav().apply {
                onNavigate = { event ->
                    event.path?.let { onPath?.invoke(it) }
                    null
                }
                append(*items)
            }

        fun badgeSuffix(copy: (SideNavTexts) -> String): io.github.shijianjs.ktjs.vaadin.badge.BadgeElement {
            val badge = createBadge { number = 12.0 }.addTheme("filled", "number-only")
            badge.slot = "suffix"
            texts.bind(badge, copy) { badge.setAttribute("aria-label", it) }
            return badge
        }

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val basicNav =
            navOf(
                item({ it.dashboard }, "vaadin:dashboard", "/dashboard"),
                item({ it.inbox }, "vaadin:envelope", "/inbox"),
                item({ it.calendar }, "vaadin:calendar", "/calendar"),
                item({ it.settings }, "vaadin:cog", "/settings"),
                item({ it.website }, "vaadin:vaadin-h", "https://vaadin.com") { target = "_blank" },
                onPath = { log.add("onNavigate · $it") },
            )
        basicNav.dataset["test"] = "side-nav"
        basicNav.style.setProperty("width", "100%")
        basic.body.append(exampleBox(null, kitCol(basicNav, log.root)))
        body.append(basic.root)

        val current = SectionBlock.NAME { show() }
        texts.bind(current.heading) { it.currentHeading }
        texts.bind(current.hint) { it.currentHint }
        val currentNav =
            createSideNav {
                onNavigate = { _ -> null }
                style.setProperty("width", "100%")
            }
        currentNav.dataset["test"] = "side-nav-current"
        currentNav.append(
            item({ it.currentPage }, "vaadin:dashboard", location.pathname),
            item({ it.anotherPage }, "vaadin:envelope", "/inbox"),
            item({ it.nestedParent }, "vaadin:folder", "/parent") { matchNested = true },
        )
        current.body.append(exampleBox(null, currentNav))
        body.append(current.root)

        val suffix = SectionBlock.NAME { show() }
        texts.bind(suffix.heading) { it.suffixHeading }
        texts.bind(suffix.hint) { it.suffixHint }
        val suffixNav =
            navOf(
                item({ it.inbox }, "vaadin:envelope", "/inbox") { append(badgeSuffix { it.unreadMessages }) },
                item({ it.calendar }, "vaadin:calendar", "/calendar") {
                    val badge = createBadge().addTheme("error", "icon-only")
                    badge.slot = "suffix"
                    texts.bind(badge, { it.upcomingAppointment }) { badge.setAttribute("aria-label", it) }
                    badge.append(createIcon { icon = "vaadin:bell" }.apply { slot = "icon" })
                    append(badge)
                },
            )
        suffixNav.style.setProperty("width", "100%")
        suffix.body.append(exampleBox(null, suffixNav))
        body.append(suffix.root)

        val hierarchy = SectionBlock.NAME { show() }
        texts.bind(hierarchy.heading) { it.hierarchyHeading }
        texts.bind(hierarchy.hint) { it.hierarchyHint }
        val messages =
            item({ it.messages }, "vaadin:envelope", "/messages") {
                append(
                    item({ it.inbox }, "vaadin:inbox", "/inbox") { slot = "children" },
                    item({ it.sent }, "vaadin:paperplane", "/sent") { slot = "children" },
                    item({ it.trash }, "vaadin:trash", "/trash") { slot = "children" },
                )
            }
        val admin =
            item({ it.admin }, "vaadin:cog") {
                append(
                    item({ it.users }, "vaadin:group", "/users") { slot = "children" },
                    item({ it.permissions }, "vaadin:key", "/permissions") { slot = "children" },
                )
            }
        val hierarchyNav = navOf(messages, admin)
        hierarchyNav.style.setProperty("width", "100%")
        hierarchy.body.append(exampleBox(null, hierarchyNav))
        body.append(hierarchy.root)

        val labelled = SectionBlock.NAME { show() }
        texts.bind(labelled.heading) { it.labelledHeading }
        texts.bind(labelled.hint) { it.labelledHint }
        fun labelNode(copy: (SideNavTexts) -> String): HTMLElement =
            HtmlTagName.span().also { node ->
                node.slot = "label"
                texts.bind(node, copy) { node.textContent = it }
            }
        val messagesNav =
            createSideNav {
                onNavigate = { _ -> null }
                style.setProperty("width", "100%")
            }
        messagesNav.append(
            labelNode { it.messages },
            item({ it.inbox }, "vaadin:inbox", "/inbox"),
            item({ it.sent }, "vaadin:paperplane", "/sent"),
            item({ it.trash }, "vaadin:trash", "/trash"),
        )
        adminNav.collapsible = true
        adminNav.onNavigate = { _ -> null }
        adminNav.style.setProperty("width", "100%")
        adminNav.dataset["test"] = "side-nav-collapsible"
        adminNav.append(
            labelNode { it.admin },
            item({ it.users }, "vaadin:group", "/users"),
            item({ it.permissions }, "vaadin:key", "/permissions"),
        )
        adminNav.listen(SideNavEvents.collapsedChanged) { showCollapsed() }
        labelled.body.append(
            exampleBox(
                null,
                createVerticalLayout().apply {
                    addTheme("spacing")
                    style.setProperty("width", "100%")
                    append(messagesNav, adminNav, collapsed.root)
                },
            ),
        )
        body.append(labelled.root)

        val newTab = SectionBlock.NAME { show() }
        texts.bind(newTab.heading) { it.newTabHeading }
        texts.bind(newTab.hint) { it.newTabHint }
        val newTabNav =
            navOf(item({ it.exampleSite }, path = "https://example.com") { target = "_blank" })
        newTabNav.style.setProperty("width", "100%")
        newTab.body.append(exampleBox(null, newTabNav))
        body.append(newTab.root)

        val scrolling = SectionBlock.NAME { show() }
        texts.bind(scrolling.heading) { it.scrollingHeading }
        texts.bind(scrolling.hint) { it.scrollingHint }
        val longNav =
            navOf(*Array(12) { index -> item({ it.item(index + 1) }, "vaadin:folder", "/item-${index + 1}") })
        longNav.style.setProperty("width", "100%")
        val scroller = createScroller { scrollDirection = ScrollerScrollDirections.vertical }
        scroller.dataset["test"] = "side-nav-scroller"
        scroller.style.setProperty("height", "12rem")
        scroller.append(longNav)
        scrolling.body.append(exampleBox(null, scroller))
        body.append(scrolling.root)

        val styling = SectionBlock.NAME { show() }
        texts.bind(styling.heading) { it.stylingHeading }
        texts.bind(styling.hint) { it.stylingHint }
        val stylingNav =
            navOf(
                item({ it.dashboard }, "vaadin:dashboard", "/dashboard"),
                item({ it.inbox }, "vaadin:envelope", "/inbox"),
                item({ it.website }, "vaadin:vaadin-h", "https://vaadin.com") {
                    className = EXTERNAL
                    target = "_blank"
                },
            )
        stylingNav.style.setProperty("width", "100%")
        styling.body.append(exampleBox(null, stylingNav))
        body.append(styling.root)

        val keyboard = SectionBlock.NAME { show() }
        texts.bind(keyboard.heading) { it.keyboardHeading }
        texts.bind(keyboard.hint) { it.keyboardHint }
        keyboard.body.append(exampleBox(null, keyboardList))
        body.append(keyboard.root)

        applyTranslation(Langs.EN)
    }

    private fun showCollapsed() {
        collapsed.set(texts.current.collapsed(adminNav.collapsed))
    }

    override fun applyTranslation(lang: Lang) {
        val meta = sideNavEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        showCollapsed()
        showKeyboard()
    }

    private fun showKeyboard() {
        val copy = texts.current
        keyboardList.replaceChildren(bulletList(listOf(copy.tabLine, copy.toggleLine, copy.enterLine)))
    }

    companion object : CustomElementReferenceAdapter<SideNavPage>(SideNavPage::class.js, "vaadin-demo-side-nav-page") {
        /** The emphasis colour this page gives an external link; the sheet and the item share it. */
        private val EXTERNAL: ClassName = ClassName("external")
        /** The accent colour of that link. */
        private val ACCENT: Color = Color("deepskyblue")
        private val english: SideNavTexts =
            SideNavTexts(
                basicHeading = "Basic usage",
                basicHint = "Side Navigation is a vertical list of navigation links, usually placed in an App " +
                    "Layout drawer. Same-origin links here do not navigate: onNavigate intercepts the click and " +
                    "writes the path to the event log; external links and _blank targets are left alone.",
                dashboard = "Dashboard",
                inbox = "Inbox",
                calendar = "Calendar",
                settings = "Settings",
                website = "Vaadin website",
                currentHeading = "Automatic highlighting of the current item",
                currentHint = "The item whose path matches the current URL is highlighted and gets " +
                    "aria-current=\"page\". Only the exact path matches by default; matchNested makes /parent also " +
                    "match /parent/child, and query parameters in the path must all be present in the URL. When a " +
                    "nested item matches, its parents expand automatically; noAutoExpand disables that. The first " +
                    "item below points at the current page's path.",
                currentPage = "Current page",
                anotherPage = "Another page",
                nestedParent = "Nested route parent",
                suffixHeading = "Prefix and suffix elements",
                suffixHint = "Items expose prefix / suffix slots: the prefix is meant for icons and the suffix " +
                    "can hold notification badges. The whole row acts as a link, so interactive prefix or suffix " +
                    "elements are not recommended.",
                unreadMessages = "unread messages",
                upcomingAppointment = "Upcoming appointment",
                hierarchyHeading = "Hierarchy",
                hierarchyHint = "Child items go into slot=\"children\" and start collapsed. A parent may also " +
                    "have a path: clicking it navigates and expands its children; a parent without a path only " +
                    "groups them. There is no technical limit on nesting depth, but three levels are recommended " +
                    "at most.",
                messages = "Messages",
                sent = "Sent",
                trash = "Trash",
                admin = "Admin",
                users = "Users",
                permissions = "Permissions",
                labelledHeading = "Labeled collapsible list",
                labelledHint = "The label slot puts a heading on top of the list, which helps when several " +
                    "lists sit next to each other; a labeled list can also be collapsible, leaving only its " +
                    "heading when collapsed.",
                newTabHeading = "Open in another browser tab",
                newTabHint = "Setting target (for example _blank) on an item opens the link in a new tab or " +
                    "window, depending on the browser's settings.",
                exampleSite = "Example",
                scrollingHeading = "Scrolling",
                scrollingHint = "Side Navigation has no scroll area of its own; when the list gets long, wrap " +
                    "it in a Scroller and give the Scroller a height.",
                item = { "Item $it" },
                stylingHeading = "Styling",
                stylingHint = "Style the link itself through ::part(link); an external link can be tagged with " +
                    "an extra class (this page's stylesheet highlights .external).",
                keyboardHeading = "Keyboard usage",
                keyboardHint = "Tab moves between list items and, for parents, also to the expand/collapse " +
                    "button; Enter or Space toggles the expansion and Enter follows the link.",
                tabLine = "Tab: moves the focus between items; a parent's toggle button is a separate stop.",
                toggleLine = "Enter / Space: expands or collapses the child items.",
                enterLine = "Enter: follows the link.",
                collapsed = { "collapsed = $it" },
            )
        private val chinese: SideNavTexts =
            SideNavTexts(
                basicHeading = "基本用法",
                basicHint = "Side Navigation 是带链接的纵向导航列表，通常放在 App Layout 的抽屉里。示例里的" +
                    "同源链接不会真的跳转：onNavigate 拦截点击并把路径写进事件日志；外部链接与 _blank 目标不受影响。",
                dashboard = "仪表盘",
                inbox = "收件箱",
                calendar = "日历",
                settings = "设置",
                website = "Vaadin 官网",
                currentHeading = "自动高亮当前项",
                currentHint = "path 与当前 URL 匹配的项会自动高亮，并把 aria-current 设为 page。默认只匹配完整" +
                    "路径，matchNested 让 /parent 同时匹配 /parent/child；path 里的查询参数必须全部出现在 URL 中。" +
                    "匹配到嵌套项时，父项会自动展开，可用 noAutoExpand 关掉。下面的第一项指向当前页面的路径。",
                currentPage = "当前页面",
                anotherPage = "另一个页面",
                nestedParent = "嵌套路由父项",
                suffixHeading = "前缀与后缀",
                suffixHint = "项上有 prefix / suffix 插槽：prefix 主要放图标，suffix 可以放通知徽标。整行都是" +
                    "链接，所以不建议在前缀或后缀里放可交互元素。",
                unreadMessages = "未读消息",
                upcomingAppointment = "即将到来的预约",
                hierarchyHeading = "层级",
                hierarchyHint = "子项放进 slot=\"children\"，默认折叠。父项也可以带 path：点击时既导航也展开子项；" +
                    "省略 path 的父项只负责分组。层级深度没有技术限制，但建议不超过三层。",
                messages = "消息",
                sent = "已发送",
                trash = "回收站",
                admin = "管理",
                users = "用户",
                permissions = "权限",
                labelledHeading = "带标签的可折叠列表",
                labelledHint = "label 插槽在列表顶部加标题，便于并排放多个列表；标签列表可以设为可折叠，" +
                    "折叠后只保留标题栏。",
                newTabHeading = "在新标签页打开",
                newTabHint = "给项设置 target（例如 _blank）可以让链接在新的标签页或窗口中打开，行为取决于浏览器设置。",
                exampleSite = "示例站点",
                scrollingHeading = "滚动",
                scrollingHint = "Side Navigation 自身没有滚动区；条目较多时把它包进 Scroller，并给 Scroller 限定高度。",
                item = { "条目 $it" },
                stylingHeading = "样式",
                stylingHint = "用 ::part(link) 定制链接本身，外部链接可以额外加类名区分（本页样式表把 .external " +
                    "做成强调色）。",
                keyboardHeading = "键盘操作",
                keyboardHint = "列表项之间用 Tab 移动；有子项时 Tab 还会落到展开/折叠按钮上；Enter 或空格切换" +
                    "展开状态，Enter 触发链接。",
                tabLine = "Tab：在列表项之间移动焦点，父项的展开按钮也是单独的焦点停靠点。",
                toggleLine = "Enter / 空格：展开或折叠子项。",
                enterLine = "Enter：触发链接。",
                collapsed = { "collapsed = $it" },
            )
        /**
         * The styling example: the link of an item tagged `external` and its icon are drawn in an accent
         * colour. `::part(link)` is the only entry into the link's own shadow tree.
         */
        private val SIDE_NAV_SHEET: CSSStyleSheet = cssSheet {
            // `::part(link)` is the only entry into the link's own shadow tree. A part is a pseudo-element
            // of the host, so it joins the compound instead of following a combinator; the icon is a
            // light-DOM child, so it is a plain child selector.
            val externalItem = compound(tag(VaadinTagName.sideNavItem), className(EXTERNAL))
            val linkPart = pseudo(pseudoElement("part"), ident("link"))

            compound(externalItem, linkPart).invoke { color = ACCENT }
            compound(externalItem, pseudo(Pseudos.hover), linkPart).invoke { color = ACCENT }
            (externalItem child tag(VaadinTagName.icon)).invoke { color = ACCENT }
        }
        private val sideNavTexts: Translations<SideNavTexts> = texts(
            Langs.EN to english,
            Langs.ZH_HANS to chinese,
        )
    }
}
