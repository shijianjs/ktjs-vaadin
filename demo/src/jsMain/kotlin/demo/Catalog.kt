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
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs

/** Base URL of the Vaadin component documentation. Every page links to its own page below it. */
const val DOCS_BASE = "https://vaadin.com/docs/latest/zh/components"

/** Base URL of the Vaadin styling documentation (themes, style properties, utility classes). */
const val STYLING_BASE = "https://vaadin.com/docs/latest/zh/styling"

/** Dokka output assembled by `pagesSite` next to the demo. */
const val API_ROOT = "api/ktjs-vaadin-web-components"

/** Top-bar entry point of the API reference. */
const val API_DOCS_URL = "$API_ROOT/index.html"

/**
 * A sidebar group. [id] is stable across languages; [label] is resolved for display.
 *
 * [icon] is an Iconify name. It marks the section, not the row, so it appears twice: before the
 * group in the drawer, and before the group's heading on the component index. Keeping it here means
 * both places read it from one declaration instead of each carrying its own table.
 */
class Group(val id: String, private val zh: String, private val en: String, val icon: String) {
    val label: String
        get() = tr(zh, en)

    /** The group's name in [lang], for the site's translation table. */
    fun labelFor(lang: Lang): String = if (lang == Langs.EN) en else zh
}

/** Sidebar groups, in navigation order. */
object Groups {
    val OVERVIEW = Group("overview", "概览", "Overview", "lucide:layers")
    val BUTTONS = Group("buttons", "按钮与操作", "Buttons & actions", "lucide:mouse-pointer-click")
    val FIELDS = Group("fields", "输入字段", "Input fields", "lucide:text-cursor-input")
    val SELECTION = Group("selection", "选择控件", "Selection", "lucide:check-square")
    val PICKERS = Group("pickers", "日期与时间", "Date & time", "lucide:calendar")
    val OVERLAYS = Group("overlays", "浮层与反馈", "Overlays & feedback", "lucide:panels-top-left")
    val DATA = Group("data", "数据展示", "Data display", "lucide:table")
    val LAYOUT = Group("layout", "布局与导航", "Layout & navigation", "lucide:layout-dashboard")
    val DISPLAY = Group("display", "基础展示", "Display", "lucide:image")
    val THEMING = Group("theming", "主题与样式", "Theming & styling", "lucide:palette")
}

/**
 * One entry of the showcase: the route id, the sidebar group it belongs to, the page metadata and
 * the page element that renders it.
 *
 * A page is a custom element like every other component of this demo: the entry holds a factory for
 * its tag, not a function that assembles DOM. A `buildXxxPage(meta): PageView` was the shape this
 * architecture does not allow — DOM only ever grows inside the element that owns it.
 */
class CatalogEntry(
    val id: String,
    val group: Group,
    val meta: PageMeta,
    /** Creates the page element; the router mounts [Page.node] and caches one per route. */
    val kitPage: (() -> io.github.shijianjs.ktjs.demo.nav.Page)? = null,
    /**
     * `true` while the page resolves its copy with `tr()` as it builds.
     *
     * Such a page has no text of its own to rewrite, so a language switch replaces it instead of
     * calling `applyTranslation`; the slot stays and only the page inside it changes. Pages that own a
     * `Translations` table rewrite their copy in place and leave this `false`.
     */
    val rebuildsOnLanguage: Boolean = false,
) {
    /** Builds a fresh instance of the page. The shell caches one per route. */
    fun create(): web.html.HTMLElement {
        val page = kitPage ?: error("page `$id` has no element factory")
        return page().node
    }
}

/**
 * Dokka package page of the component behind [id].
 *
 * Wrapper packages mirror the npm package names with the hyphens removed (`text-field` →
 * `io.github.shijianjs.ktjs.vaadin.textfield`), so the route id is enough to locate the package. `overview` and `theming`
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

