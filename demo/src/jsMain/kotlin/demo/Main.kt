package demo

import vaadin.lumostyles.registerLumoIcons
import web.dom.document

/**
 * Entry point of the component showcase.
 *
 * The page is a single-page application: the shell is built once, and the router swaps the page
 * inside it on every fragment change. Nothing is assembled from markup strings — every node is
 * created through the wrapper factories and configured with typed properties.
 */
fun main() {
    // Registers the `lumo:*` icon names used by the shell and by the icon examples.
    registerLumoIcons()
    Theme.applyStored()

    val shell = AppShell()
    document.body.appendChild(shell.root)

    Router(shell::show).start()
}
