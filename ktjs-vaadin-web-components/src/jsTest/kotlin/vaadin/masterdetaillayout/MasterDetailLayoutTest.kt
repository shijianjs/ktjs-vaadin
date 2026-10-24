package vaadin.masterdetaillayout

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
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/master-detail-layout/test/master-detail-layout.test.js`.
 *
 * The resize/animation cases need real layout; the size API, the expand flags and the overlay
 * properties are translated.
 */
class MasterDetailLayoutTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerMasterDetailLayout()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newLayout(): MasterDetailLayoutElement = attach(createMasterDetailLayout())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerMasterDetailLayout()
        val layout = newLayout()

        assertEquals("vaadin-master-detail-layout", layout.tagName.lowercase())
        assertEquals("vaadin-master-detail-layout", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun rendersAShadowRoot(): Promise<Unit> {
        val layout = newLayout()

        return nextRender(layout).then {
            assertNotNull(js("(layout) => layout.shadowRoot")(layout))
        }
    }

    @Test
    fun appliesTheSizes(): Promise<Unit> {
        val layout = attach(createMasterDetailLayout { masterSize = "300px"; detailSize = "1fr" })

        return nextRender(layout).then {
            assertEquals("300px", layout.masterSize)
            assertEquals("1fr", layout.detailSize)
        }
    }

    @Test
    fun storesTheSizes(): Promise<Unit> {
        val layout = newLayout()

        return nextUpdate(layout)
            .then {
                layout.masterSize = "400px"
                layout.detailSize = "200px"
                nextUpdate(layout)
            }
            .then {
                assertEquals("400px", layout.masterSize)
                assertEquals("200px", layout.detailSize)
            }
    }

    @Test
    fun appliesTheOrientation(): Promise<Unit> {
        val layout = attach(createMasterDetailLayout { orientation = "vertical" })

        return nextRender(layout).then { assertEquals("vertical", layout.orientation) }
    }

    @Test
    fun supportsTheExpandFlags(): Promise<Unit> {
        val layout = newLayout()

        return nextRender(layout).then {
            assertFalse(layout.expandMaster)
            assertFalse(layout.expandDetail)
            layout.expandMaster = true
            layout.expandDetail = true
            nextUpdate(layout)
            assertTrue(layout.expandMaster)
            assertTrue(layout.expandDetail)
        }
    }

    @Test
    fun supportsTheOverlayConfiguration(): Promise<Unit> {
        val layout = attach(createMasterDetailLayout { forceOverlay = true; overlaySize = "100%" })

        return nextRender(layout).then {
            assertTrue(layout.forceOverlay)
            assertEquals("100%", layout.overlaySize)
        }
    }

    @Test
    fun supportsDisablingTheAnimation(): Promise<Unit> {
        val layout = attach(createMasterDetailLayout { noAnimation = true })

        return nextRender(layout).then { assertTrue(layout.noAnimation) }
    }

    // Kotlin-only coverage

    @Test
    fun createMasterDetailLayoutAppliesTheConfigureBlock() {
        val layout = attach(createMasterDetailLayout { masterSize = "250px" })

        assertEquals("250px", layout.masterSize)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-master-detail-layout",
            document.createElement(vaadin.VaadinTagName.masterDetailLayout).tagName.lowercase(),
        )
        assertEquals("master-size", MasterDetailLayoutAttributes.masterSize)
        assertEquals("detail-size", MasterDetailLayoutAttributes.detailSize)
        assertEquals("expand-master", MasterDetailLayoutAttributes.expandMaster)
        assertEquals("expand-detail", MasterDetailLayoutAttributes.expandDetail)
        assertEquals("force-overlay", MasterDetailLayoutAttributes.forceOverlay)
        assertEquals("overlay-size", MasterDetailLayoutAttributes.overlaySize)
    }
}
