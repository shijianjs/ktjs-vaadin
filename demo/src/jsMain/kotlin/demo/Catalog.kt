package demo

import demo.pages.buttonEntries
import demo.pages.dataEntries
import demo.pages.displayEntries
import demo.pages.layoutEntries
import demo.pages.numberFieldEntries
import demo.pages.overlayEntries
import demo.pages.overviewEntries
import demo.pages.pickerEntries
import demo.pages.selectionEntries
import demo.pages.textFieldEntries
import demo.pages.themingEntries
import demo.ui.PageMeta

/** Base URL of the Vaadin component documentation. Every page links to its own page below it. */
const val DOCS_BASE = "https://vaadin.com/docs/latest/zh/components"

/** Base URL of the Vaadin styling documentation (themes, style properties, utility classes). */
const val STYLING_BASE = "https://vaadin.com/docs/latest/zh/styling"

/** Sidebar groups, in navigation order. */
object Groups {
    const val OVERVIEW = "概览"
    const val BUTTONS = "按钮与操作"
    const val FIELDS = "输入字段"
    const val SELECTION = "选择控件"
    const val PICKERS = "日期与时间"
    const val OVERLAYS = "浮层与反馈"
    const val DATA = "数据展示"
    const val LAYOUT = "布局与导航"
    const val DISPLAY = "基础展示"
    const val THEMING = "主题与样式"
}

/**
 * One entry of the showcase: the route id, the sidebar group it belongs to, the page metadata and
 * the builder that produces the page DOM.
 */
class CatalogEntry(
    val id: String,
    val group: String,
    val meta: PageMeta,
    private val builder: (PageMeta) -> web.html.HTMLElement,
) {
    /** Builds a fresh instance of the page. The shell caches one per route. */
    fun create(): web.html.HTMLElement = builder(meta)
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
        buttonEntries,
        textFieldEntries,
        numberFieldEntries,
        selectionEntries,
        pickerEntries,
        overlayEntries,
        dataEntries,
        layoutEntries,
        displayEntries,
        themingEntries,
    ).flatten()

private val catalogById: Map<String, CatalogEntry> = catalog.associateBy { it.id }

/** Sidebar groups that actually have entries, in navigation order. */
val catalogGroups: List<String> = catalog.map { it.group }.distinct()

/** Looks up an entry by route id. */
fun catalogEntry(id: String): CatalogEntry? = catalogById[id]

/** Entries of one sidebar group, in declaration order. */
fun catalogEntriesOf(group: String): List<CatalogEntry> = catalog.filter { it.group == group }
