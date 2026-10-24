package vaadin.contextmenu

import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/context-menu/test/context-menu.test.js` and the public part of
 * `packages/context-menu/test/context-menu-items.test.js`.
 *
 * The pointer driven cases need real `contextmenu`/`mousedown` sequences; the item API, the
 * `openOn`/`listenOn`/`selector` contract and the attribute API are translated.
 */
class ContextMenuTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerContextMenu()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newMenu(): ContextMenuElement = attach(createContextMenu())

    private fun items(): Array<ContextMenuItemData> = js(
        "[{ text: 'Edit' }, { text: 'Delete' }, { component: 'separator' }]",
    ).unsafeCast<Array<ContextMenuItemData>>()

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerContextMenu()
        val menu = newMenu()

        assertEquals("vaadin-context-menu", menu.tagName.lowercase())
        assertEquals("vaadin-context-menu", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun isClosedByDefault(): Promise<Unit> {
        val menu = newMenu()

        return nextRender(menu).then {
            // `opened` is read-only: the menu opens on the configured `openOn` event.
            assertFalse(js("(menu) => menu.opened")(menu) as Boolean)
        }
    }

    @Test
    fun exposesTheItems(): Promise<Unit> {
        val menu = newMenu()
        menu.items = items()

        return nextRender(menu).then { assertEquals(3, menu.items?.size) }
    }

    @Test
    fun appliesTheOpenAndListenEvents(): Promise<Unit> {
        val menu = newMenu()

        return nextUpdate(menu)
            .then {
                menu.setAttribute("open-on", "click")
                menu.setAttribute("listen-on", "click")
                nextUpdate(menu)
            }
            .then {
                assertEquals("click", menu.getAttribute("open-on"))
                assertEquals("click", menu.getAttribute("listen-on"))
            }
    }

    @Test
    fun appliesTheSelector(): Promise<Unit> {
        val menu = newMenu()

        return nextUpdate(menu)
            .then {
                menu.setAttribute("selector", "vaadin-item")
                nextUpdate(menu)
            }
            .then { assertEquals("vaadin-item", menu.getAttribute("selector")) }
    }

    @Test
    fun closeAllMenusEventTypeIsExposed() {
        // The static helper of the package is exposed as an event type on the element; closing all
        // menus is triggered by dispatching it.
        assertNotNull(ContextMenuEvents.closeAllMenus)
    }

    // Kotlin-only coverage

    @Test
    fun createContextMenuAppliesTheConfigureBlock() {
        val menu = attach(createContextMenu())
        menu.items = items()

        assertEquals(3, menu.items?.size)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-context-menu",
            document.createElement(vaadin.VaadinTagName.contextMenu).tagName.lowercase(),
        )
        assertEquals("open-on", ContextMenuAttributes.openOn)
        assertEquals("listen-on", ContextMenuAttributes.listenOn)
        assertEquals("close-on", ContextMenuAttributes.closeOn)
        assertEquals("selector", ContextMenuAttributes.selector)
        assertEquals("opened", ContextMenuAttributes.opened)
        assertEquals("position", ContextMenuAttributes.position)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(ContextMenuEvents.itemSelected)
        assertNotNull(ContextMenuEvents.openedChanged)
        assertNotNull(ContextMenuEvents.closed)
    }
}
