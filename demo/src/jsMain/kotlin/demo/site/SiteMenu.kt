package demo.site

import demo.catalog
import demo.page.overview.OverviewPage
import io.github.shijianjs.ktjs.demo.nav.DemoMenu
import io.github.shijianjs.ktjs.demo.nav.Entry
import io.github.shijianjs.ktjs.demo.nav.Group

/** The wrapper's version, shown under the site name: the npm packages it wraps. */
const val SITE_VERSION: String = "Vaadin 25"

/** The documentation site the demo is published under; the link menu opens it in a new tab. */
const val DOCS_URL: String = "https://shijianjs.github.io/ktjs-vaadin/"

/** Dokka reference assembled by `pagesSite` next to the demo. */
const val API_URL: String = "https://shijianjs.github.io/ktjs-vaadin/api/index.html"

/** Source repository of the wrappers and of this demo. */
const val SOURCE_URL: String = "https://github.com/shijianjs/ktjs-vaadin"

/**
 * The site, derived from [catalog]: one drawer group per catalog group and one entry per page.
 *
 * The catalog already carries the declarations (route id, group, bilingual title, builder), so the
 * menu is a projection of it rather than a second list that can drift. Icons belong to the group
 * row only: a page has no icon of its own, and repeating the group's mark on every page under it
 * would say nothing about the page while making the column of identical glyphs the loudest thing
 * in the drawer.
 *
 * The same [Group] instance has to be reused across its entries: groups are derived from the entry
 * list by identity, so a fresh instance per entry would split one group into sixty.
 */
fun siteMenu(): DemoMenu {
    val groups: Map<String, Group> = catalog.map { it.group.id }.distinct()
        .associateWith { id -> Group(id, catalog.first { it.group.id == id }.group.icon) }

    return DemoMenu(
        catalog.map { entry ->
            val group = groups.getValue(entry.group.id)
            Entry(entry.id, group) {
                // A page is an element and owns its copy, so a language switch rewrites the text in
                // place; the menu only picks the element for the route.
                val page = entry.kitPage ?: error("entry `${entry.id}` has no page element")
                page()
            }
        },
    )
}
