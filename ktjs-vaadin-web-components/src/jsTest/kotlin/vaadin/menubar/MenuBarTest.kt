package vaadin.menubar

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
 * Translated from `packages/menu-bar/test/menu-bar.test.js` and the public part of
 * `packages/menu-bar/test/menu-bar-items.test.js`.
 *
 * The upstream suite builds `items` from plain objects, which is exactly the shape of the
 * `MenuBarItem` interface; the objects are created with `js(...)` so the nested children stay a
 * plain JS array. Keyboard and submenu-position cases are skipped (they need `sendKeys` and layout).
 */
class MenuBarTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerMenuBar()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newMenuBar(): MenuBarElement = attach(createMenuBar())

    private fun items(): Array<MenuBarItem> = js(
        """
        [
          { text: 'File', children: [{ text: 'New' }, { text: 'Open' }] },
          { text: 'Edit', children: [{ text: 'Undo' }] },
          { text: 'Help' }
        ]
        """,
    ).unsafeCast<Array<MenuBarItem>>()

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerMenuBar()
        val host = newMenuBar()

        assertEquals("vaadin-menu-bar", host.tagName.lowercase())
        assertEquals("vaadin-menu-bar", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun rendersAButtonPerItem(): Promise<Unit> {
        val host = newMenuBar()
        host.items = items()

        return nextRender(host).then {
            // The buttons are light-DOM children created by the slot controller; the overflow
            // button is slotted separately.
            val buttons = js(
                "(host) => Array.from(host.querySelectorAll('vaadin-menu-bar-button'))" +
                    ".filter((button) => button.getAttribute('slot') !== 'overflow')",
            )(host) as Array<HTMLElement>
            assertEquals(3, buttons.size)
        }
    }

    @Test
    fun rendersTheItemText(): Promise<Unit> {
        val host = newMenuBar()
        host.items = items()

        return nextRender(host).then {
            val labels = js("(host) => Array.from(host.querySelectorAll('vaadin-menu-bar-button')).map((b) => b.textContent.trim())")(
                host,
            ) as Array<String>
            assertTrue(labels.contains("File"), "expected a File button, got ${labels.toList()}")
        }
    }

    @Test
    fun reflectsDisabledToTheAttribute(): Promise<Unit> {
        val host = newMenuBar()

        return nextUpdate(host)
            .then {
                host.disabled = true
                nextUpdate(host)
            }
            .then { assertTrue(host.hasAttribute("disabled")) }
    }

    @Test
    fun isNotOpenOnHoverByDefault(): Promise<Unit> {
        val host = newMenuBar()

        return nextRender(host).then { assertFalse(host.openOnHover) }
    }

    @Test
    fun reflectsOpenOnHover(): Promise<Unit> {
        val host = newMenuBar()

        return nextUpdate(host)
            .then {
                host.openOnHover = true
                nextUpdate(host)
            }
            .then { assertTrue(host.openOnHover) }
    }

    @Test
    fun reflectsTabNavigation(): Promise<Unit> {
        val host = newMenuBar()

        return nextUpdate(host)
            .then {
                host.tabNavigation = true
                nextUpdate(host)
            }
            .then { assertTrue(host.tabNavigation) }
    }

    @Test
    fun firesItemSelectedWhenAnItemIsClicked(): Promise<Unit> {
        val host = newMenuBar()
        host.items = items()
        var fired = 0
        on(host, "item-selected") { fired++ }

        return nextRender(host)
            .then {
                // The last item has no children, so clicking it selects the item instead of
                // opening a submenu.
                val buttons = js("(host) => Array.from(host.querySelectorAll('vaadin-menu-bar-button'))")(
                    host,
                ) as Array<HTMLElement>
                js("(button) => button.click()")(buttons.last())
                nextUpdate(host)
            }
            .then { assertTrue(fired > 0, "expected an item-selected event, got $fired") }
    }

    @Test
    fun closesWithoutAnOpenSubmenu(): Promise<Unit> {
        val host = newMenuBar()
        host.items = items()

        return nextRender(host).then {
            // `close()` is idempotent; with no submenu open it must not throw.
            host.close()
        }
    }

    // Kotlin-only coverage

    @Test
    fun createMenuBarAppliesTheConfigureBlock() {
        val host = attach(createMenuBar { openOnHover = true })

        assertTrue(host.openOnHover)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-menu-bar", document.createElement(vaadin.VaadinTagName.menuBar).tagName.lowercase())
        assertEquals("disabled", MenuBarAttributes.disabled)
        assertEquals("open-on-hover", MenuBarAttributes.openOnHover)
        assertEquals("reverse-collapse", MenuBarAttributes.reverseCollapse)
        assertEquals("tab-navigation", MenuBarAttributes.tabNavigation)
        assertEquals("i18n", MenuBarAttributes.i18n)
        assertEquals("theme", MenuBarAttributes.theme)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(MenuBarEvents.itemSelected)
    }

    @Test
    fun itemUnionTypesWrapPlainValues() {
        // The generated union factories are the documented way to pass a component or a theme list.
        val text: HTMLElementOrString = HTMLElementOrString("File")
        val theme: StringArrayOrString = StringArrayOrString("primary")
        val themes: StringArrayOrString = StringArrayOrString(js("['primary']") as Array<String>)

        assertNotNull(text)
        assertNotNull(theme)
        assertNotNull(themes)
    }
}
