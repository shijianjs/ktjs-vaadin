package demo.pages.layout

import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.tr
import demo.ui.EventLog
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.bulletList
import demo.ui.column
import demo.ui.example
import demo.ui.page
import demo.ui.section
import js.objects.unsafeJso
import vaadin.breadcrumbs.BreadcrumbsI18n
import vaadin.breadcrumbs.BreadcrumbsItemElement
import vaadin.breadcrumbs.createBreadcrumbs
import vaadin.breadcrumbs.createBreadcrumbsItem
import vaadin.icon.createIcon
import vaadin.icons.registerVaadinIcons
import web.dom.clickEvent
import web.events.addHandler

val breadcrumbsEntry: CatalogEntry =
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
                sourcePath = "pages/layout/BreadcrumbsPage.kt",
            ),
        builder = ::buildBreadcrumbsPage,
    )

private fun buildBreadcrumbsPage(meta: PageMeta): PageView {
    val view = page(meta)
    registerVaadinIcons()

    val log = EventLog(4)
    val basic =
        section(
            tr("基本用法", "Basic usage"),
            tr(
                "面包屑从顶层位置逐级指向当前页面：带 path 的项渲染成链接，最后一项不带 path，代表当前页且不可点击。" +
                    "示例里的链接不会真的跳转，点击只写进事件日志。（Flow 的 ROUTER / MANUAL 模式是 Java 侧 API，" +
                    "Web Component 始终由调用方添加项。）",
                "Breadcrumbs lead from the top-level location to the current page: items with a path render as " +
                    "links, and the last item has no path, represents the current page and is not clickable. The " +
                    "links here do not navigate — clicks are written to the event log. (The Flow ROUTER / MANUAL " +
                    "modes are Java-side APIs; with the Web Component the caller always adds the items.)",
            ),
        )
    val crumbs = createBreadcrumbs()
    crumbs.dataset["test"] = "breadcrumbs"
    crumbs.append(
        crumb(tr("首页", "Home"), "/", onPath = { log.add("clicked · $it") }),
        crumb(tr("组件", "Components"), "/components", onPath = { log.add("clicked · $it") }),
        crumb(tr("面包屑", "Breadcrumbs")),
    )
    basic.body.append(example(null, column(crumbs, log.root)))
    view.body.append(basic.root)

    val items =
        section(
            tr("项与图标", "Items and icons"),
            tr(
                "path 决定一项是链接还是当前页；prefix 插槽可以放图标，例如层级根节点的房子图标。",
                "path decides whether an item is a link or the current page; the prefix slot can hold an icon, " +
                    "such as a home icon for the root of the hierarchy.",
            ),
        )
    val iconCrumbs = createBreadcrumbs()
    iconCrumbs.dataset["test"] = "breadcrumbs-icons"
    iconCrumbs.append(
        crumb(tr("首页", "Home"), "/", iconName = "vaadin:home"),
        crumb(tr("订单", "Orders"), "/orders", iconName = "vaadin:package"),
        crumb(tr("订单详情", "Order Details")),
    )
    items.body.append(example(null, iconCrumbs))
    view.body.append(items.root)

    val overflow =
        section(
            tr("溢出", "Overflow"),
            tr(
                "宽度不足时，前面的项会折叠进开头的溢出菜单，最后一项（当前页）始终可见。",
                "When the trail does not fit, leading items collapse into an overflow menu at the start; the " +
                    "last item (the current page) never collapses.",
            ),
        )
    val many = createBreadcrumbs()
    many.dataset["test"] = "breadcrumbs-overflow"
    many.style.setProperty("width", "300px")
    many.append(
        crumb(tr("首页", "Home"), "/"),
        crumb(tr("目录", "Catalog"), "/catalog"),
        crumb(tr("电子产品", "Electronics"), "/catalog/electronics"),
        crumb(tr("电脑", "Computers"), "/catalog/electronics/computers"),
        crumb(tr("笔记本", "Laptops"), "/catalog/electronics/computers/laptops"),
        crumb(tr("Model X1", "Model X1")),
    )
    overflow.body.append(example(null, many))
    view.body.append(overflow.root)

    val i18nSection =
        section(
            tr("本地化", "Internationalization"),
            tr(
                "目前可以本地化的只有溢出菜单按钮的无障碍标签：moreItems，屏幕阅读器会朗读它。",
                "Only the accessible label of the overflow button is localizable: moreItems, which screen " +
                    "readers announce.",
            ),
        )
    val localized = createBreadcrumbs()
    localized.dataset["test"] = "breadcrumbs-i18n"
    localized.style.setProperty("width", "16rem")
    localized.i18nProperty = unsafeJso<BreadcrumbsI18n> {
        moreItems = tr("展开折叠的层级", "Show hidden trail")
    }
    localized.append(
        crumb(tr("首页", "Home"), "/"),
        crumb(tr("目录", "Catalog"), "/catalog"),
        crumb(tr("电子产品", "Electronics"), "/catalog/electronics"),
        crumb(tr("电脑", "Computers"), "/catalog/electronics/computers"),
        crumb(tr("笔记本", "Laptops"), "/catalog/electronics/computers/laptops"),
        crumb(tr("Model X1", "Model X1")),
    )
    i18nSection.body.append(example(null, localized))
    view.body.append(i18nSection.root)

    val accessibility =
        section(
            tr("无障碍", "Accessibility"),
            tr(
                "组件渲染为 navigation 地标，辅助技术会把整条路径读成一个导航区域。",
                "The component renders with the navigation landmark role, so assistive technologies announce " +
                    "the trail as a navigation region.",
            ),
        )
    accessibility.body.append(
        example(
            null,
            bulletList(
                listOf(
                    tr(
                        "role=navigation：整条路径是一个导航地标。",
                        "role=navigation: the whole trail is a navigation landmark.",
                    ),
                    tr(
                        "aria-current=page：最后一个没有 path 的项标记为当前页面。",
                        "aria-current=page: the last item without a path is marked as the current page.",
                    ),
                    tr(
                        "带 path 的项是可聚焦、可用键盘操作的链接。",
                        "Items with a path are focusable links that work from the keyboard.",
                    ),
                    tr(
                        "溢出按钮带有可本地化的 aria-label（i18n.moreItems）。",
                        "The overflow button carries a localizable aria-label (i18n.moreItems).",
                    ),
                ),
            ),
        ),
    )
    view.body.append(accessibility.root)

    return view
}

/** A breadcrumbs item; the ones with a path navigate nowhere and report their path to [onPath]. */
private fun crumb(
    text: String,
    path: String? = null,
    iconName: String? = null,
    onPath: ((String) -> Unit)? = null,
): BreadcrumbsItemElement =
    createBreadcrumbsItem { textContent = text }.apply {
        if (path != null) {
            this.path = path
            clickEvent.addHandler {
                it.preventDefault()
                onPath?.invoke(path)
            }
        }
        if (iconName != null) append(createIcon { icon = iconName }.apply { slot = "prefix" })
    }
