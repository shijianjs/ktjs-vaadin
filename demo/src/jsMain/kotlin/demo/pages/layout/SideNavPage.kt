package demo.pages.layout

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.Readout
import demo.ui.bulletList
import demo.ui.column
import demo.ui.example
import demo.ui.listen
import demo.ui.page
import demo.ui.section
import demo.ui.span
import demo.ui.withClass
import demo.ui.withTheme
import vaadin.badge.createBadge
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import vaadin.scroller.ScrollerScrollDirections
import vaadin.scroller.createScroller
import vaadin.sidenav.SideNavElement
import vaadin.sidenav.SideNavEvents
import vaadin.sidenav.SideNavItemElement
import vaadin.sidenav.createSideNav
import vaadin.sidenav.createSideNavItem
import vaadin.verticallayout.createVerticalLayout
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
        builder = ::buildSideNavPage,
    )

private fun buildSideNavPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    val log = EventLog(4)
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "Side Navigation 是带链接的纵向导航列表，通常放在 App Layout 的抽屉里。示例里的同源链接不会真的" +
                    "跳转：onNavigate 拦截点击并把路径写进事件日志；外部链接与 _blank 目标不受影响。",
                "Side Navigation is a vertical list of navigation links, usually placed in an App Layout " +
                    "drawer. Same-origin links here do not navigate: onNavigate intercepts the click and writes " +
                    "the path to the event log; external links and _blank targets are left alone.",
            ),
        )
    val basicNav =
        navOf(
            navItem(tr("仪表盘", "Dashboard"), "vaadin:dashboard", "/dashboard"),
            navItem(tr("收件箱", "Inbox"), "vaadin:envelope", "/inbox"),
            navItem(tr("日历", "Calendar"), "vaadin:calendar", "/calendar"),
            navItem(tr("设置", "Settings"), "vaadin:cog", "/settings"),
            navItem(tr("Vaadin 官网", "Vaadin website"), "vaadin:vaadin-h", "https://vaadin.com").apply {
                target = "_blank"
            },
            onPath = { log.add("onNavigate · $it") },
        )
    basicNav.dataset["test"] = "side-nav"
    basicNav.style.setProperty("width", "100%")
    basic.body.append(example(null, column(basicNav, log.root)))
    view.body.append(basic.root)

    val current =
        section(
            tr("自动高亮当前项", "Automatic highlighting of the current item"),
            tr(
                "path 与当前 URL 匹配的项会自动高亮，并把 aria-current 设为 page。默认只匹配完整路径，" +
                    "matchNested 让 /parent 同时匹配 /parent/child；path 里的查询参数必须全部出现在 URL 中。" +
                    "匹配到嵌套项时，父项会自动展开，可用 noAutoExpand 关掉。下面的第一项指向当前页面的路径。",
                "The item whose path matches the current URL is highlighted and gets aria-current=\"page\". Only " +
                    "the exact path matches by default; matchNested makes /parent also match /parent/child, and " +
                    "query parameters in the path must all be present in the URL. When a nested item matches, its " +
                    "parents expand automatically; noAutoExpand disables that. The first item below points at the " +
                    "current page's path.",
            ),
        )
    val currentNav =
        createSideNav {
            onNavigate = { _ -> null }
            style.setProperty("width", "100%")
        }
    currentNav.dataset["test"] = "side-nav-current"
    currentNav.append(
        navItem(tr("当前页面", "Current page"), "vaadin:dashboard", location.pathname),
        navItem(tr("另一个页面", "Another page"), "vaadin:envelope", "/inbox"),
        navItem(tr("嵌套路由父项", "Nested route parent"), "vaadin:folder", "/parent").apply { matchNested = true },
    )
    current.body.append(example(null, currentNav))
    view.body.append(current.root)

    val suffix =
        section(
            tr("前缀与后缀", "Prefix and suffix elements"),
            tr(
                "项上有 prefix / suffix 插槽：prefix 主要放图标，suffix 可以放通知徽标。整行都是链接，" +
                    "所以不建议在前缀或后缀里放可交互元素。",
                "Items expose prefix / suffix slots: the prefix is meant for icons and the suffix can hold " +
                    "notification badges. The whole row acts as a link, so interactive prefix or suffix " +
                    "elements are not recommended.",
            ),
        )
    val suffixNav =
        navOf(
            navItem(tr("收件箱", "Inbox"), "vaadin:envelope", "/inbox").apply {
                append(
                    createBadge { number = 12.0 }.withTheme("filled", "number-only").apply {
                        slot = "suffix"
                        setAttribute("aria-label", tr("未读消息", "unread messages"))
                    },
                )
            },
            navItem(tr("日历", "Calendar"), "vaadin:calendar", "/calendar").apply {
                append(
                    createBadge().withTheme("error", "icon-only").apply {
                        slot = "suffix"
                        setAttribute("aria-label", tr("即将到来的预约", "Upcoming appointment"))
                        append(createIcon { icon = "vaadin:bell" }.apply { slot = "icon" })
                    },
                )
            },
        )
    suffixNav.style.setProperty("width", "100%")
    suffix.body.append(example(null, suffixNav))
    view.body.append(suffix.root)

    val hierarchy =
        section(
            tr("层级", "Hierarchy"),
            tr(
                "子项放进 slot=\"children\"，默认折叠。父项也可以带 path：点击时既导航也展开子项；" +
                    "省略 path 的父项只负责分组。层级深度没有技术限制，但建议不超过三层。",
                "Child items go into slot=\"children\" and start collapsed. A parent may also have a path: " +
                    "clicking it navigates and expands its children; a parent without a path only groups them. " +
                    "There is no technical limit on nesting depth, but three levels are recommended at most.",
            ),
        )
    val messages =
        navItem(tr("消息", "Messages"), "vaadin:envelope", "/messages").apply {
            append(
                navItem(tr("收件箱", "Inbox"), "vaadin:inbox", "/inbox").apply { slot = "children" },
                navItem(tr("已发送", "Sent"), "vaadin:paperplane", "/sent").apply { slot = "children" },
                navItem(tr("回收站", "Trash"), "vaadin:trash", "/trash").apply { slot = "children" },
            )
        }
    val admin =
        navItem(tr("管理", "Admin"), "vaadin:cog").apply {
            append(
                navItem(tr("用户", "Users"), "vaadin:group", "/users").apply { slot = "children" },
                navItem(tr("权限", "Permissions"), "vaadin:key", "/permissions").apply { slot = "children" },
            )
        }
    val hierarchyNav = navOf(messages, admin)
    hierarchyNav.style.setProperty("width", "100%")
    hierarchy.body.append(example(null, hierarchyNav))
    view.body.append(hierarchy.root)

    val labelled =
        section(
            tr("带标签的可折叠列表", "Labeled collapsible list"),
            tr(
                "label 插槽在列表顶部加标题，便于并排放多个列表；标签列表可以设为可折叠，" +
                    "折叠后只保留标题栏。",
                "The label slot puts a heading on top of the list, which helps when several lists sit next " +
                    "to each other; a labeled list can also be collapsible, leaving only its heading when " +
                    "collapsed.",
            ),
        )
    val messagesNav =
        createSideNav {
            onNavigate = { _ -> null }
            style.setProperty("width", "100%")
        }
    messagesNav.append(
        span(tr("消息", "Messages")).apply { slot = "label" },
        navItem(tr("收件箱", "Inbox"), "vaadin:inbox", "/inbox"),
        navItem(tr("已发送", "Sent"), "vaadin:paperplane", "/sent"),
        navItem(tr("回收站", "Trash"), "vaadin:trash", "/trash"),
    )
    val adminNav =
        createSideNav {
            collapsible = true
            onNavigate = { _ -> null }
            style.setProperty("width", "100%")
        }
    adminNav.dataset["test"] = "side-nav-collapsible"
    adminNav.append(
        span(tr("管理", "Admin")).apply { slot = "label" },
        navItem(tr("用户", "Users"), "vaadin:group", "/users"),
        navItem(tr("权限", "Permissions"), "vaadin:key", "/permissions"),
    )
    val collapsed = Readout("collapsed = false")
    adminNav.listen(SideNavEvents.collapsedChanged) { collapsed.set("collapsed = ${adminNav.collapsed}") }
    labelled.body.append(
        example(null, createVerticalLayout().apply {
            withTheme("spacing")
            style.setProperty("width", "100%")
            append(messagesNav, adminNav, collapsed.root)
        }),
    )
    view.body.append(labelled.root)

    val newTab =
        section(
            tr("在新标签页打开", "Open in another browser tab"),
            tr(
                "给项设置 target（例如 _blank）可以让链接在新的标签页或窗口中打开，行为取决于浏览器设置。",
                "Setting target (for example _blank) on an item opens the link in a new tab or window, " +
                    "depending on the browser's settings.",
            ),
        )
    val newTabNav = navOf(navItem(tr("示例站点", "Example"), path = "https://example.com").apply { target = "_blank" })
    newTabNav.style.setProperty("width", "100%")
    newTab.body.append(example(null, newTabNav))
    view.body.append(newTab.root)

    val scrolling =
        section(
            tr("滚动", "Scrolling"),
            tr(
                "Side Navigation 自身没有滚动区；条目较多时把它包进 Scroller，并给 Scroller 限定高度。",
                "Side Navigation has no scroll area of its own; when the list gets long, wrap it in a " +
                    "Scroller and give the Scroller a height.",
            ),
        )
    val longNav =
        navOf(
            *Array(12) { index ->
                navItem(tr("条目 ${index + 1}", "Item ${index + 1}"), "vaadin:folder", "/item-${index + 1}")
            },
        )
    longNav.style.setProperty("width", "100%")
    val scroller = createScroller { scrollDirection = ScrollerScrollDirections.vertical }
    scroller.dataset["test"] = "side-nav-scroller"
    scroller.style.setProperty("height", "12rem")
    scroller.append(longNav)
    scrolling.body.append(example(null, scroller))
    view.body.append(scrolling.root)

    val styling =
        section(
            tr("样式", "Styling"),
            tr(
                "用 ::part(link) 定制链接本身，外部链接可以额外加类名区分（本页样式表把 .external 做成强调色）。",
                "Style the link itself through ::part(link); an external link can be tagged with an extra class " +
                    "(this page's stylesheet highlights .external).",
            ),
        )
    val stylingNav =
        navOf(
            navItem(tr("仪表盘", "Dashboard"), "vaadin:dashboard", "/dashboard"),
            navItem(tr("收件箱", "Inbox"), "vaadin:envelope", "/inbox"),
            navItem(tr("Vaadin 官网", "Vaadin website"), "vaadin:vaadin-h", "https://vaadin.com").apply {
                withClass("external")
                target = "_blank"
            },
        )
    stylingNav.style.setProperty("width", "100%")
    styling.body.append(example(null, stylingNav))
    view.body.append(styling.root)

    val keyboard =
        section(
            tr("键盘操作", "Keyboard usage"),
            tr(
                "列表项之间用 Tab 移动；有子项时 Tab 还会落到展开/折叠按钮上；Enter 或空格切换展开状态，" +
                    "Enter 触发链接。",
                "Tab moves between list items and, for parents, also to the expand/collapse button; Enter or " +
                    "Space toggles the expansion and Enter follows the link.",
            ),
        )
    keyboard.body.append(
        example(
            null,
            bulletList(
                listOf(
                    tr("Tab：在列表项之间移动焦点，父项的展开按钮也是单独的焦点停靠点。", "Tab: moves the focus between items; a parent's toggle button is a separate stop."),
                    tr("Enter / 空格：展开或折叠子项。", "Enter / Space: expands or collapses the child items."),
                    tr("Enter：触发链接。", "Enter: follows the link."),
                ),
            ),
        ),
    )
    view.body.append(keyboard.root)

    return view
}

/** A side nav item with an optional icon in the prefix slot. */
private fun navItem(
    label: String,
    iconName: String? = null,
    path: String? = null,
    configure: SideNavItemElement.() -> Unit = {},
): SideNavItemElement =
    createSideNavItem {
        textContent = label
        if (path != null) this.path = path
        configure()
    }.apply {
        if (iconName != null) append(createIcon { icon = iconName }.apply { slot = "prefix" })
    }

/** A side nav whose links never navigate; [onPath] receives the path of every intercepted click. */
private fun navOf(vararg items: SideNavItemElement, onPath: ((String) -> Unit)? = null): SideNavElement =
    createSideNav().apply {
        onNavigate = { event ->
            event.path?.let { onPath?.invoke(it) }
            null
        }
        append(*items)
    }
