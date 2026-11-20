package demo.pages.layout

import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.col as kitCol
import io.github.shijianjs.ktjs.demo.page.Blocks.exampleBox
import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.demo.page.Blocks.bulletList
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import demo.CatalogEntry
import demo.DOCS_BASE
import demo.Groups
import demo.bi
import demo.ui.EventLog
import demo.ui.PageFrame
import demo.ui.PageMeta
import js.objects.unsafeJso
import io.github.shijianjs.ktjs.vaadin.breadcrumbs.BreadcrumbsElement
import io.github.shijianjs.ktjs.vaadin.breadcrumbs.BreadcrumbsI18n
import io.github.shijianjs.ktjs.vaadin.breadcrumbs.BreadcrumbsItemElement
import io.github.shijianjs.ktjs.vaadin.breadcrumbs.createBreadcrumbs
import io.github.shijianjs.ktjs.vaadin.breadcrumbs.createBreadcrumbsItem
import io.github.shijianjs.ktjs.vaadin.icon.createIcon
import io.github.shijianjs.ktjs.vaadin.icons.registerVaadinIcons
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLLIElement
import web.html.HTMLElement

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
        kitPage = { BreadcrumbsPage.NAME() },
    )

private val english: BreadcrumbsTexts = BreadcrumbsTexts(
    basicHeading = "Basic usage",
    basicHint = "Breadcrumbs lead from the top-level location to the current page: items with a path render as " +
        "links, and the last item has no path, represents the current page and is not clickable. The links here " +
        "do not navigate — clicks are written to the event log. (The Flow ROUTER / MANUAL modes are Java-side " +
        "APIs; with the Web Component the caller always adds the items.)",
    home = "Home",
    components = "Components",
    breadcrumbs = "Breadcrumbs",
    itemsHeading = "Items and icons",
    itemsHint = "path decides whether an item is a link or the current page; the prefix slot can hold an icon, " +
        "such as a home icon for the root of the hierarchy.",
    orders = "Orders",
    orderDetails = "Order Details",
    overflowHeading = "Overflow",
    overflowHint = "When the trail does not fit, leading items collapse into an overflow menu at the start; the " +
        "last item (the current page) never collapses.",
    catalog = "Catalog",
    electronics = "Electronics",
    computers = "Computers",
    laptops = "Laptops",
    i18nHeading = "Internationalization",
    i18nHint = "Only the accessible label of the overflow button is localizable: moreItems, which screen readers " +
        "announce.",
    moreItems = "Show hidden trail",
    a11yHeading = "Accessibility",
    a11yHint = "The component renders with the navigation landmark role, so assistive technologies announce the " +
        "trail as a navigation region.",
    a11yNavigation = "role=navigation: the whole trail is a navigation landmark.",
    a11yCurrent = "aria-current=page: the last item without a path is marked as the current page.",
    a11yFocusable = "Items with a path are focusable links that work from the keyboard.",
    a11yOverflow = "The overflow button carries a localizable aria-label (i18n.moreItems).",
)

private val chinese: BreadcrumbsTexts = BreadcrumbsTexts(
    basicHeading = "基本用法",
    basicHint = "面包屑从顶层位置逐级指向当前页面：带 path 的项渲染成链接，最后一项不带 path，代表当前页且不可点击。" +
        "示例里的链接不会真的跳转，点击只写进事件日志。（Flow 的 ROUTER / MANUAL 模式是 Java 侧 API，" +
        "Web Component 始终由调用方添加项。）",
    home = "首页",
    components = "组件",
    breadcrumbs = "面包屑",
    itemsHeading = "项与图标",
    itemsHint = "path 决定一项是链接还是当前页；prefix 插槽可以放图标，例如层级根节点的房子图标。",
    orders = "订单",
    orderDetails = "订单详情",
    overflowHeading = "溢出",
    overflowHint = "宽度不足时，前面的项会折叠进开头的溢出菜单，最后一项（当前页）始终可见。",
    catalog = "目录",
    electronics = "电子产品",
    computers = "电脑",
    laptops = "笔记本",
    i18nHeading = "本地化",
    i18nHint = "目前可以本地化的只有溢出菜单按钮的无障碍标签：moreItems，屏幕阅读器会朗读它。",
    moreItems = "展开折叠的层级",
    a11yHeading = "无障碍",
    a11yHint = "组件渲染为 navigation 地标，辅助技术会把整条路径读成一个导航区域。",
    a11yNavigation = "role=navigation：整条路径是一个导航地标。",
    a11yCurrent = "aria-current=page：最后一个没有 path 的项标记为当前页面。",
    a11yFocusable = "带 path 的项是可聚焦、可用键盘操作的链接。",
    a11yOverflow = "溢出按钮带有可本地化的 aria-label（i18n.moreItems）。",
)

/** The product every long trail ends on; a name, so it needs no translation. */
private const val MODEL_X1 = "Model X1"

private val breadcrumbsTexts: Translations<BreadcrumbsTexts> = texts(
    Langs.EN to english,
    Langs.ZH_HANS to chinese,
)

internal class BreadcrumbsPage : DemoPage() {
    private val texts = Binder(breadcrumbsTexts)

    private val frame = PageFrame(breadcrumbsEntry)

    private val log = EventLog(4)

    /** The basic trail: two links that only log their path, then the current page. */
    private val basicTrail: List<BreadcrumbsItemElement> =
        listOf(
            crumb("/", onPath = { log.add("clicked · $it") }),
            crumb("/components", onPath = { log.add("clicked · $it") }),
            crumb(null),
        )

    private val iconTrail: List<BreadcrumbsItemElement> =
        listOf(
            crumb("/", "vaadin:home"),
            crumb("/orders", "vaadin:package"),
            crumb(null),
        )

    /** The long trail of the overflow example and of the localised one: six items, five of them links. */
    private val longPaths = listOf("/", "/catalog", "/catalog/electronics", "/catalog/electronics/computers",
        "/catalog/electronics/computers/laptops", null)

    private val overflowTrail: List<BreadcrumbsItemElement> = longPaths.map { crumb(it) }

    private val localisedTrail: List<BreadcrumbsItemElement> = longPaths.map { crumb(it) }

    private val localised: BreadcrumbsElement = createBreadcrumbs()

    /** The four accessibility statements: a bullet list built empty, then bound item by item. */
    private val a11yList: HTMLElement = bulletList(List(4) { "" })

    init {
        frame.mount(this)
        registerVaadinIcons()

        val basic = SectionBlock.NAME { show() }
        texts.bind(basic.heading) { it.basicHeading }
        texts.bind(basic.hint) { it.basicHint }
        val crumbs = createBreadcrumbs()
        crumbs.dataset["test"] = "breadcrumbs"
        crumbs.append(*basicTrail.toTypedArray())
        basic.body.append(exampleBox(null, kitCol(crumbs, log.root)))
        body.append(basic.root)

        val items = SectionBlock.NAME { show() }
        texts.bind(items.heading) { it.itemsHeading }
        texts.bind(items.hint) { it.itemsHint }
        val iconCrumbs = createBreadcrumbs()
        iconCrumbs.dataset["test"] = "breadcrumbs-icons"
        iconCrumbs.append(*iconTrail.toTypedArray())
        items.body.append(exampleBox(null, iconCrumbs))
        body.append(items.root)

        val overflow = SectionBlock.NAME { show() }
        texts.bind(overflow.heading) { it.overflowHeading }
        texts.bind(overflow.hint) { it.overflowHint }
        val many = createBreadcrumbs()
        many.dataset["test"] = "breadcrumbs-overflow"
        many.style.setProperty("width", "300px")
        many.append(*overflowTrail.toTypedArray())
        overflow.body.append(exampleBox(null, many))
        body.append(overflow.root)

        val i18nSection = SectionBlock.NAME { show() }
        texts.bind(i18nSection.heading) { it.i18nHeading }
        texts.bind(i18nSection.hint) { it.i18nHint }
        localised.dataset["test"] = "breadcrumbs-i18n"
        localised.style.setProperty("width", "16rem")
        localised.append(*localisedTrail.toTypedArray())
        i18nSection.body.append(exampleBox(null, localised))
        body.append(i18nSection.root)

        val accessibility = SectionBlock.NAME { show() }
        texts.bind(accessibility.heading) { it.a11yHeading }
        texts.bind(accessibility.hint) { it.a11yHint }
        val statements = listOf<(BreadcrumbsTexts) -> String>(
            { it.a11yNavigation },
            { it.a11yCurrent },
            { it.a11yFocusable },
            { it.a11yOverflow },
        )
        statements.forEachIndexed { index, field ->
            texts.bind(a11yList.children[index] as HTMLLIElement) { copy -> field(copy) }
        }
        accessibility.body.append(exampleBox(null, a11yList))
        body.append(accessibility.root)

        applyTranslation(Langs.EN)
    }

    override fun applyTranslation(lang: Lang) {
        val meta = breadcrumbsEntry.meta
        setTitle(meta.titleFor(lang))
        setSummary(meta.summaryFor(lang))
        texts.apply(lang)
        frame.show(lang)
        val copy = texts.current
        bindTrail(basicTrail, copy.home, copy.components, copy.breadcrumbs)
        bindTrail(iconTrail, copy.home, copy.orders, copy.orderDetails)
        // The last item is the product name, which reads the same in both languages.
        val longTrail = listOf(copy.home, copy.catalog, copy.electronics, copy.computers, copy.laptops, MODEL_X1)
        longTrail.forEachIndexed { index, label ->
            overflowTrail[index].textContent = label
            localisedTrail[index].textContent = label
        }
        localised.i18nProperty =
            unsafeJso<BreadcrumbsI18n> {
                moreItems = copy.moreItems
            }
    }

    /** Writes one trail's labels; the last item of every trail is the current page, hence no path. */
    private fun bindTrail(trail: List<BreadcrumbsItemElement>, vararg labels: String) {
        trail.forEachIndexed { index, item -> item.textContent = labels[index] }
    }

    companion object : CustomElementReferenceAdapter<BreadcrumbsPage>(BreadcrumbsPage::class.js, "vaadin-demo-breadcrumbs-page")
}

/** A breadcrumbs item; the ones with a path navigate nowhere and report their path to [onPath]. */
private fun crumb(
    path: String? = null,
    iconName: String? = null,
    onPath: ((String) -> Unit)? = null,
): BreadcrumbsItemElement =
    createBreadcrumbsItem().apply {
        if (path != null) {
            this.path = path
            clickEvent.addHandler {
                it.preventDefault()
                onPath?.invoke(path)
            }
        }
        if (iconName != null) append(createIcon { icon = iconName }.apply { slot = "prefix" })
    }
