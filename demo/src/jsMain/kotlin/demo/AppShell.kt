package demo

import demo.ui.div
import demo.ui.element
import demo.ui.listen
import demo.ui.setHidden
import demo.ui.span
import demo.ui.withClass
import demo.ui.withTheme
import vaadin.button.ButtonElement
import vaadin.button.createButton
import vaadin.icon.createIcon
import vaadin.sidenav.SideNavItemElement
import vaadin.sidenav.createSideNav
import vaadin.sidenav.createSideNavItem
import web.cssom.ClassName
import web.dom.clickEvent
import web.dom.document
import web.events.EventType
import web.events.addHandler
import web.html.ButtonType
import web.html.HTMLElement
import web.html.HTMLButtonElement
import web.html.HtmlTagName
import web.html.button
import web.keyboard.KeyboardEvent
import web.location.location
import web.window.window

/** `keydown` as a typed event so the listener receives a [KeyboardEvent]. */
private val KEY_DOWN: EventType<KeyboardEvent> = EventType("keydown")

/** One collapsible sidebar group: the header button, the list it controls and the current state. */
private class NavGroup(val toggle: HTMLButtonElement, val list: HTMLElement, var expanded: Boolean)

/**
 * Application shell: a fixed sidebar built once from [catalog], a top bar with the breadcrumb and
 * the Lumo light/dark switch, and the content area that hosts the current page.
 *
 * The shell never rebuilds. Navigation swaps the single child of [contentHost] and keeps the built
 * pages cached, so component state (typed input, selected rows, opened panels) survives a trip to
 * another page and back — the same way a desktop app behaves.
 */
class AppShell {
    private val contentHost = div("content__host")

    private val breadcrumb = span("", "topbar__breadcrumb")

    private val navItems = mutableMapOf<String, SideNavItemElement>()

    private val navGroups = mutableMapOf<String, NavGroup>()

    private val pages = mutableMapOf<String, HTMLElement>()

    private val lightButton: ButtonElement = createButton { textContent = "亮色" }

    private val darkButton: ButtonElement = createButton { textContent = "暗色" }

    private val nav: HTMLElement = buildNav()

    private val main: HTMLElement = buildMain()

    /** Root element; the shell owns it for the whole lifetime of the page. */
    val root: HTMLElement = div("shell", nav, main)

    private var shownRoute: String? = null

    init {
        lightButton.clickEvent.addHandler { switchTheme(false) }
        darkButton.clickEvent.addHandler { switchTheme(true) }
        syncThemeButtons()
    }

    /** Renders [route], falling back to the overview when the fragment names an unknown page. */
    fun show(route: String) {
        val entry = catalogEntry(route) ?: catalogEntry(Router.DEFAULT_ROUTE) ?: return

        if (shownRoute != entry.id) {
            shownRoute = entry.id
            navItems.forEach { (id, item) ->
                val active = id == entry.id
                item.classList.toggle(ClassName("nav__current"), active)
                item.ariaCurrent = if (active) "page" else null
            }
            // Reveal the active route even when the user left its group collapsed.
            setGroupExpanded(entry.group, true)
            breadcrumb.textContent = "${entry.group} / ${entry.meta.title}"
            document.title = "${entry.meta.title} · ktjs-vaadin"
        }

        val pageRoot = pages.getOrPut(entry.id) { entry.create() }
        if (contentHost.firstElementChild !== pageRoot) {
            contentHost.replaceChildren(pageRoot)
            window.scrollTo(0.0, 0.0)
        }
        nav.classList.remove(ClassName("shell__nav--open"))
    }

    /** Navigates to [route]; the router reacts to the resulting `hashchange`. */
    private fun open(route: String) {
        location.hash = Router.hashOf(route)
    }

    private fun switchTheme(dark: Boolean) {
        Theme.apply(dark)
        syncThemeButtons()
    }

    private fun syncThemeButtons() {
        val dark = Theme.isDark
        lightButton.withTheme(if (dark) "tertiary" else "primary")
        darkButton.withTheme(if (dark) "primary" else "tertiary")
        lightButton.ariaPressed = if (dark) "false" else "true"
        darkButton.ariaPressed = if (dark) "true" else "false"
    }

    private fun setGroupExpanded(group: String, expanded: Boolean) {
        val navGroup = navGroups[group] ?: return
        navGroup.expanded = expanded
        navGroup.toggle.ariaExpanded = if (expanded) "true" else "false"
        navGroup.list.setHidden(!expanded)
    }

    private fun buildNav(): HTMLElement {
        val side = element(HtmlTagName.nav, "shell__nav")
        side.append(
            div(
                "nav__brand",
                span("ktjs-vaadin", "nav__brand-name"),
                span("Kotlin/JS", "nav__brand-version"),
            ),
        )

        catalogGroups.forEach { group ->
            // One `vaadin-side-nav` per group: the element is a plain list container, and grouping
            // keeps the 60 entries scannable without nesting every component one level deeper.
            val groupNav = createSideNav()
            catalogEntriesOf(group).forEach { entry ->
                val item = createSideNavItem { textContent = entry.meta.title }
                item.dataset["route"] = entry.id
                // `vaadin-side-nav-item` derives its `current` state from `path`, comparing
                // `document.location.pathname` with the resolved path. A hash route always resolves
                // to the same pathname, so every item would be "current". The shell therefore keeps
                // navigation in its own hands and marks the active item with `nav__current`.
                item.role = "link"
                item.tabIndex = 0
                item.clickEvent.addHandler { open(entry.id) }
                item.listen(KEY_DOWN) { event ->
                    if (event.key == "Enter" || event.key == " ") {
                        event.preventDefault()
                        open(entry.id)
                    }
                }
                navItems[entry.id] = item
                groupNav.append(item)
            }

            val chevron = createIcon { icon = "lumo:angle-down" }.withClass("nav__group-chevron")
            val toggle = element(HtmlTagName.button, "nav__group-title", span(group), chevron)
            toggle.type = ButtonType.Companion.button
            toggle.dataset["group"] = group
            toggle.clickEvent.addHandler {
                val current = navGroups.getValue(group)
                setGroupExpanded(group, !current.expanded)
            }

            navGroups[group] = NavGroup(toggle, groupNav, expanded = false)
            // `show()` expands the group of the initial route, so only that one starts open.
            setGroupExpanded(group, false)

            side.append(div("nav__group", toggle, groupNav))
        }

        return side
    }

    private fun buildMain(): HTMLElement {
        val menuButton = createButton().withTheme("icon").withClass("topbar__menu")
        menuButton.append(createIcon { icon = "lumo:menu" })
        menuButton.ariaLabel = "显示导航"
        menuButton.clickEvent.addHandler { nav.classList.toggle(ClassName("shell__nav--open")) }

        val topbar =
            element(
                HtmlTagName.header,
                "topbar",
                menuButton,
                breadcrumb,
                div("theme-switch", lightButton, darkButton),
            )

        val content = element(HtmlTagName.main, "content", contentHost)
        return div("shell__main", topbar, content)
    }
}
