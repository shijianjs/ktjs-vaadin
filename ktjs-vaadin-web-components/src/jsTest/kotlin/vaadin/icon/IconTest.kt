package vaadin.icon

import kotlin.js.Promise
import kotlin.js.js
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
import kotlin.js.unsafeCast
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/icon/test/icon.test.js` and the public part of
 * `packages/icon/test/icon-helpers.test.js`.
 *
 * The hand-written parts of the package — the SVG literal helpers (`unsafeSvgLiteral`,
 * `ensureSvgLiteral`, `isValidSvg`, `cloneSvgNode`) and the feature detection functions — have no
 * upstream Kotlin equivalent and are covered here as well.
 */
class IconTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerIcon()
        registerIconset()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newIcon(): IconElement = attach(createIcon())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerIcon()
        val icon = newIcon()

        assertEquals("vaadin-icon", icon.tagName.lowercase())
        assertEquals("vaadin-icon", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun iconsetIsDefinedInTheCustomElementRegistry() {
        val ctor = registerIconset()
        val iconset = attach(createIconset())

        assertEquals("vaadin-iconset", iconset.tagName.lowercase())
        assertEquals("vaadin-iconset", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun exposesTheIconName(): Promise<Unit> {
        val icon = newIcon()

        return nextUpdate(icon)
            .then {
                icon.icon = "vaadin:home"
                nextUpdate(icon)
            }
            .then { assertEquals("vaadin:home", icon.icon) }
    }

    @Test
    fun reflectsTheIconAttribute(): Promise<Unit> {
        val icon = attach(createIcon { icon = "vaadin:home" })

        return nextRender(icon).then { assertEquals("vaadin:home", icon.getAttribute("icon")) }
    }

    @Test
    fun exposesTheSvgLiteralOfARenderedIcon(): Promise<Unit> {
        val icon = attach(createIcon { icon = "vaadin:home" })

        return nextRender(icon).then {
            // The icon is resolved from the registered icon set into an SVG literal.
            assertNotNull(js("(icon) => icon.svg")(icon))
        }
    }

    @Test
    fun rendersAnSvgForAKnownIcon(): Promise<Unit> {
        val icon = attach(createIcon { icon = "vaadin:home" })

        return nextRender(icon).then {
            val svg = js("(icon) => icon.shadowRoot.querySelector('svg')")(icon)
            assertNotNull(svg, "expected the icon to render an <svg>")
        }
    }

    @Test
    fun appliesTheSize(): Promise<Unit> {
        val icon = attach(createIcon { icon = "vaadin:home" })

        return nextRender(icon)
            .then {
                icon.size = 32.0
                nextUpdate(icon)
            }
            .then {
                // `size` drives the CSS custom property of the icon rather than an SVG attribute.
                assertEquals(32.0, icon.size)
                assertNotNull(js("(icon) => icon.shadowRoot.querySelector('svg')")(icon))
            }
    }

    @Test
    fun appliesTheFontIconProperties(): Promise<Unit> {
        val icon = attach(createIcon { char = "\u0041"; fontFamily = "Material Icons" })

        return nextRender(icon).then {
            assertEquals("\u0041", icon.char)
            assertEquals("Material Icons", icon.fontFamily)
        }
    }

    @Test
    fun appliesTheSrcOfAnExternalIcon(): Promise<Unit> {
        val icon = attach(createIcon { src = "./test-icon.svg" })

        return nextRender(icon).then { assertEquals("./test-icon.svg", icon.src) }
    }

    // SVG literal helpers

    @Test
    fun unsafeSvgLiteralAcceptsAnSvgString() {
        val literal = unsafeSvgLiteral("<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>")

        assertNotNull(literal)
        assertEquals(true, isValidSvg(literal))
    }

    @Test
    fun isValidSvgRejectsAPlainString() {
        assertEquals(false, isValidSvg("not an svg"))
    }

    @Test
    fun ensureSvgLiteralWrapsAnSvgString() {
        val literal = ensureSvgLiteral("<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>")

        assertNotNull(literal)
    }

    @Test
    fun cloneSvgNodeCopiesAnElement() {
        val node = document.createElement("svg")
        val clone = cloneSvgNode(node)

        assertNotNull(clone)
    }

    // feature detection

    @Test
    fun containerQueryFeatureDetectionIsCallable() {
        // Both helpers are exported by the package and used internally by the icon; the wrapper
        // exposes them as-is (the manifest does not record their boolean return type).
        supportsCQUnitsForPseudoElements()
        needsFontIconSizingFallback()
    }

    // Kotlin-only coverage

    @Test
    fun createIconAppliesTheConfigureBlock() {
        val icon = attach(createIcon { symbol = "home" })

        assertEquals("home", icon.symbol)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-icon", document.createElement(vaadin.VaadinTagName.icon).tagName.lowercase())
        assertEquals(
            "vaadin-iconset",
            document.createElement(vaadin.VaadinTagName.iconset).tagName.lowercase(),
        )
        assertEquals("icon", IconAttributes.icon)
        assertEquals("src", IconAttributes.src)
        assertEquals("symbol", IconAttributes.symbol)
        assertEquals("char", IconAttributes.char)
        assertEquals("font-family", IconAttributes.fontFamily)
        assertEquals("ligature", IconAttributes.ligature)
        assertEquals("size", IconAttributes.size)
    }

    @Test
    fun iconWithoutANameHasNoSvgLiteral(): Promise<Unit> {
        val icon = newIcon()

        return nextRender(icon).then {
            assertEquals(null, js("(icon) => icon.svg == null ? null : icon.svg")(icon))
        }
    }
}
