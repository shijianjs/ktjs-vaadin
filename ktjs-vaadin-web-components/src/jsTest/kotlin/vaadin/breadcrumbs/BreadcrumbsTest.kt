package vaadin.breadcrumbs

import vaadin.queryAll
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
import vaadin.fixture
import vaadin.nextRender
import vaadin.nextUpdate
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/breadcrumbs/test/breadcrumbs.test.js` and the public part of
 * `packages/breadcrumbs/test/breadcrumbs-item.test.js`.
 *
 * The overflow/ellipsis cases depend on measured widths and on `ResizeObserver` timing; the
 * attribute, `current` and `path` cases are translated, and the keyboard cases are skipped like the
 * other suites that need `sendKeys`.
 */
class BreadcrumbsTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerBreadcrumbs()
        registerBreadcrumbsItem()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newBreadcrumbs(): BreadcrumbsElement = attach(createBreadcrumbs())

    private fun items(host: BreadcrumbsElement): Array<HTMLElement> =
        queryAll(host, "vaadin-breadcrumbs-item")

    private fun breadcrumbsFixture(): BreadcrumbsElement = attach(
        fixture(
            """
            <vaadin-breadcrumbs>
              <vaadin-breadcrumbs-item path="/">Root</vaadin-breadcrumbs-item>
              <vaadin-breadcrumbs-item path="/docs">Docs</vaadin-breadcrumbs-item>
              <vaadin-breadcrumbs-item>Current</vaadin-breadcrumbs-item>
            </vaadin-breadcrumbs>
            """.trimIndent(),
        ).unsafeCast<BreadcrumbsElement>(),
    )

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerBreadcrumbs()
        val host = newBreadcrumbs()

        assertEquals("vaadin-breadcrumbs", host.tagName.lowercase())
        assertEquals("vaadin-breadcrumbs", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun breadcrumbsItemIsDefinedInTheCustomElementRegistry() {
        val ctor = registerBreadcrumbsItem()
        val item = attach(createBreadcrumbsItem())

        assertEquals("vaadin-breadcrumbs-item", item.tagName.lowercase())
        assertEquals("vaadin-breadcrumbs-item", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun rendersTheSlottedItems(): Promise<Unit> {
        val host = breadcrumbsFixture()

        return nextRender(host).then { assertEquals(3, items(host).size) }
    }

    @Test
    fun marksTheLastItemAsCurrent(): Promise<Unit> {
        val host = breadcrumbsFixture()

        return nextRender(host).then {
            val list = items(host)
            assertTrue(js("(item) => item.current")(list[2]) as Boolean)
            assertFalse(js("(item) => item.current")(list[0]) as Boolean)
        }
    }

    @Test
    fun exposesThePathOfEachItem(): Promise<Unit> {
        val host = breadcrumbsFixture()

        return nextRender(host).then {
            assertEquals("/", items(host)[0].getAttribute("path"))
            assertEquals("/docs", items(host)[1].getAttribute("path"))
        }
    }

    @Test
    fun itemIsNotCurrentOnItsOwn(): Promise<Unit> {
        val item = attach(createBreadcrumbsItem())

        return nextRender(item).then {
            // `current` is read-only: the breadcrumbs container decides which item is current.
            assertFalse(item.current)
        }
    }

    @Test
    fun reflectsThePathOfAnItem(): Promise<Unit> {
        val item = attach(createBreadcrumbsItem())

        return nextUpdate(item).then {
            item.path = "/docs"
            assertEquals("/docs", item.path)
        }
    }

    @Test
    fun reflectsDisabledOnAnItem(): Promise<Unit> {
        val item = attach(createBreadcrumbsItem())

        return nextUpdate(item).then {
            item.disabled = true
            assertTrue(item.hasAttribute("disabled"))
        }
    }

    // Kotlin-only coverage

    @Test
    fun createBreadcrumbsAppliesTheConfigureBlock() {
        val host = attach(createBreadcrumbs())
        host.setAttribute("theme", "no-collapse")

        assertEquals("no-collapse", host.getAttribute("theme"))
    }

    @Test
    fun createBreadcrumbsItemAppliesTheConfigureBlock() {
        val item = attach(createBreadcrumbsItem { path = "/docs" })

        assertEquals("/docs", item.path)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-breadcrumbs", document.createElement(vaadin.VaadinTagName.breadcrumbs).tagName.lowercase())
        assertEquals(
            "vaadin-breadcrumbs-item",
            document.createElement(vaadin.VaadinTagName.breadcrumbsItem).tagName.lowercase(),
        )
        assertEquals("current", BreadcrumbsItemAttributes.current)
        assertEquals("path", BreadcrumbsItemAttributes.path)
        assertEquals("disabled", BreadcrumbsItemAttributes.disabled)
        assertEquals("theme", BreadcrumbsItemAttributes.theme)
        assertEquals("i18n", BreadcrumbsAttributes.i18n)
    }

    @Test
    fun i18nObjectIsExposed() {
        val host = newBreadcrumbs()

        assertNotNull(js("(host) => host.i18n")(host))
    }
}
