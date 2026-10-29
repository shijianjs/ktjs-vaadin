package demo

import demo.pages.buttons.buttonsEntries
import demo.pages.data.dataEntries
import demo.pages.display.displayEntries
import demo.pages.fields.fieldsEntries
import demo.pages.layout.layoutEntries
import demo.pages.overlays.overlaysEntries
import demo.pages.overview.overviewEntries
import demo.pages.pickers.pickersEntries
import demo.pages.selection.selectionEntries
import demo.pages.theming.themingEntries
import demo.pages.upload.uploadEntries
import demo.ui.PageMeta
import demo.ui.PageView
import demo.ui.anchor

/** Base URL of the Vaadin component documentation. Every page links to its own page below it. */
const val DOCS_BASE = "https://vaadin.com/docs/latest/zh/components"

/** Base URL of the Vaadin styling documentation (themes, style properties, utility classes). */
const val STYLING_BASE = "https://vaadin.com/docs/latest/zh/styling"

/** Dokka output assembled by `pagesSite` next to the demo. */
const val API_ROOT = "api/ktjs-vaadin-web-components"

/** Top-bar entry point of the API reference. */
const val API_DOCS_URL = "$API_ROOT/index.html"

/** A sidebar group. [id] is stable across languages; [label] is resolved for display. */
class Group(val id: String, private val zh: String, private val en: String) {
    val label: String
        get() = tr(zh, en)
}

/** Sidebar groups, in navigation order. */
object Groups {
    val OVERVIEW = Group("overview", "概览", "Overview")
    val BUTTONS = Group("buttons", "按钮与操作", "Buttons & actions")
    val FIELDS = Group("fields", "输入字段", "Input fields")
    val SELECTION = Group("selection", "选择控件", "Selection")
    val PICKERS = Group("pickers", "日期与时间", "Date & time")
    val OVERLAYS = Group("overlays", "浮层与反馈", "Overlays & feedback")
    val DATA = Group("data", "数据展示", "Data display")
    val LAYOUT = Group("layout", "布局与导航", "Layout & navigation")
    val DISPLAY = Group("display", "基础展示", "Display")
    val THEMING = Group("theming", "主题与样式", "Theming & styling")
}

/**
 * One entry of the showcase: the route id, the sidebar group it belongs to, the page metadata and
 * the builder that produces the page DOM.
 */
class CatalogEntry(
    val id: String,
    val group: Group,
    val meta: PageMeta,
    private val builder: (PageMeta) -> PageView,
) {
    /** Builds a fresh instance of the page. The shell caches one per route. */
    fun create(): web.html.HTMLElement {
        val view = builder(meta)
        view.links.append(
            anchor(
                tr("Kotlin API 文档", "Kotlin API reference"),
                componentApiUrl(id),
                "page__link",
                external = true,
            ),
        )
        return view.root
    }
}

/**
 * Dokka package page of the component behind [id].
 *
 * Wrapper packages mirror the npm package names with the hyphens removed (`text-field` →
 * `vaadin.textfield`), so the route id is enough to locate the package. `overview` and `theming`
 * cover several packages and link to the module index / Lumo package instead.
 */
fun componentApiUrl(id: String): String =
    when (id) {
        "overview" -> API_DOCS_URL
        "theming" -> "$API_ROOT/vaadin.lumostyles/index.html"
        else -> "$API_ROOT/vaadin.${id.replace("-", "")}/index.html"
    }

/**
 * Every wrapped component, one page each.
 *
 * The order of the lists is the order of the sidebar groups; within a list the declaration order
 * is the order of the group. `pages/Overview.kt` indexes the whole list, so a new entry shows up
 * there without a second registration.
 */
val catalog: List<CatalogEntry> =
    listOf(
        overviewEntries,
        buttonsEntries,
        fieldsEntries,
        selectionEntries,
        pickersEntries,
        overlaysEntries,
        dataEntries,
        uploadEntries,
        layoutEntries,
        displayEntries,
        themingEntries,
    ).flatten()

private val catalogById: Map<String, CatalogEntry> = catalog.associateBy { it.id }

/** Sidebar groups that actually have entries, in navigation order. */
val catalogGroups: List<Group> = catalog.map { it.group }.distinct()

/** Looks up an entry by route id. */
fun catalogEntry(id: String): CatalogEntry? = catalogById[id]

/** Entries of one sidebar group, in declaration order. */
fun catalogEntriesOf(group: Group): List<CatalogEntry> = catalog.filter { it.group == group }
