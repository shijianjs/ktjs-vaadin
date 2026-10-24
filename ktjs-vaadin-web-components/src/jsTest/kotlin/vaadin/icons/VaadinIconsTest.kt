package vaadin.icons

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import vaadin.attach
import vaadin.detachAll
import vaadin.icon.createIcon
import vaadin.icon.registerIcon
import vaadin.icon.registerIconset
import vaadin.nextRender
import web.html.HTMLElement

/**
 * Covers the hand-written `@vaadin/icons` wrapper.
 *
 * The package is data-only (an SVG template registered as the `vaadin` icon set), so the upstream
 * project has no behavioural suite for it. The Kotlin coverage is the contract that matters:
 * `registerVaadinIcons()` imports the module and the icons of the set resolve through
 * `<vaadin-icon icon="vaadin:...">`.
 */
class VaadinIconsTest {
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

    private fun hasSvgContent(icon: HTMLElement): Boolean =
        js("(icon) => icon.svg != null")(
            icon,
        ) as Boolean

    @Test
    fun registerVaadinIconsImportsTheModule() {
        assertNotNull(registerVaadinIcons())
    }

    @Test
    fun registersTheVaadinIconset(): Promise<Unit> {
        registerVaadinIcons()
        val icon = attach(createIcon { icon = "vaadin:home" })

        return nextRender(icon).then {
            assertEquals(true, hasSvgContent(icon), "expected vaadin:home to render SVG content")
        }
    }

    @Test
    fun resolvesSeveralIconsFromTheSet(): Promise<Unit> {
        registerVaadinIcons()
        val bell = attach(createIcon { icon = "vaadin:bell" })
        val user = attach(createIcon { icon = "vaadin:user" })

        return nextRender(bell)
            .then {
                assertEquals(true, hasSvgContent(bell))
                nextRender(user)
            }
            .then { assertEquals(true, hasSvgContent(user)) }
    }

    @Test
    fun iconsOfTheSetKeepTheDefaultSize(): Promise<Unit> {
        registerVaadinIcons()
        val icon = attach(createIcon { icon = "vaadin:home" })

        return nextRender(icon).then {
            // The set registers its icons at 16px, which is the default icon size.
            assertEquals(16.0, icon.size)
        }
    }

    @Test
    fun acceptsAnSvgLiteralDirectly(): Promise<Unit> {
        registerVaadinIcons()
        val icon = attach(createIcon())

        return nextRender(icon)
            .then {
                icon.svg = vaadin.icon.unsafeSvgLiteral(
                    "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 16 16\"><path d=\"M0 0h16v16H0z\"/></svg>",
                )
                nextRender(icon)
            }
            .then { assertEquals(true, hasSvgContent(icon), "expected the literal to render") }
    }
}
