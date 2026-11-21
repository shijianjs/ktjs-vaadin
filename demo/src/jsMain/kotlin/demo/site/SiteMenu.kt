package demo.site

import demo.Catalog
import demo.page.overview.OverviewPage
import io.github.shijianjs.ktjs.demo.nav.DemoMenu
import io.github.shijianjs.ktjs.demo.nav.Entry
import io.github.shijianjs.ktjs.demo.nav.Group

/**
 * The site, derived from [Catalog.ENTRIES]: one drawer group per catalog group and one entry per page.
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
    val groups: Map<String, Group> = Catalog.ENTRIES.map { it.group.id }.distinct()
        .associateWith { id -> Group(id, Catalog.ENTRIES.first { it.group.id == id }.group.icon) }

    return DemoMenu(
        Catalog.ENTRIES.map { entry ->
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
