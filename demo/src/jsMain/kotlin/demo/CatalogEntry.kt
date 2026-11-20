package demo

import demo.ui.PageMeta

class CatalogEntry(
    val id: String,
    val group: Group,
    val meta: PageMeta,
    /** Creates the page element; the router mounts [Page.node] and caches one per route. */
    val kitPage: (() -> io.github.shijianjs.ktjs.demo.nav.Page)? = null,
) {
    /** Builds a fresh instance of the page. The shell caches one per route. */
    fun create(): web.html.HTMLElement {
        val page = kitPage ?: error("page `$id` has no element factory")
        return page().node
    }
}
