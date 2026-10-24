package demo

import demo.ui.listen
import web.events.Event
import web.events.EventType
import web.location.location
import web.window.window

/**
 * Hash-based client-side router.
 *
 * The demo is published as a static site (GitHub Pages, and any plain file server for the end-to-end
 * tests), so routes live in the fragment: `#/button`. That keeps relative asset URLs working on a
 * project page served from `/<repo>/`, and it gives history navigation for free — the browser pushes
 * a history entry per hash change, so back and forward re-dispatch `hashchange`.
 */
class Router(private val onRoute: (String) -> Unit) {
    companion object {
        /** Route shown when the URL has no (or an unknown) fragment. */
        const val DEFAULT_ROUTE = "overview"

        private val HASH_CHANGE: EventType<Event> = EventType("hashchange")

        /** Parses the route out of a `location.hash` value such as `#/text-field`. */
        fun parse(hash: String): String {
            val route = hash.removePrefix("#").removePrefix("/").trim()
            return route.ifEmpty { DEFAULT_ROUTE }
        }

        /** Fragment of [route], shared by the router and the sidebar links. */
        fun hashOf(route: String): String = "#/$route"
    }

    /** The route currently in the URL. */
    fun current(): String = parse(location.hash)

    /** Navigates to [route]; re-renders directly when the fragment would not change. */
    fun navigate(route: String) {
        val target = hashOf(route)
        if (location.hash == target) {
            onRoute(route)
        } else {
            location.hash = target
        }
    }

    /** Renders the route already in the URL and subscribes to fragment changes. */
    fun start() {
        window.listen(HASH_CHANGE) { onRoute(current()) }
        onRoute(current())
    }
}
