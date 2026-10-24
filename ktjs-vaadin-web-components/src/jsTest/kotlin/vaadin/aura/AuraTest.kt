package vaadin.aura

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers the hand-written `@vaadin/aura` wrapper.
 *
 * Aura is a CSS-only design system, so the wrapper exposes nothing but the two entry points of the
 * stylesheet. The assertion is that they match the published paths.
 */
class AuraTest {
    @Test
    fun exposesTheStylesheetEntryPoint() {
        assertEquals("@vaadin/aura/aura.css", Aura.CSS)
    }

    @Test
    fun exposesTheDistEntryPoint() {
        assertEquals("@vaadin/aura/dist/aura.css", Aura.DIST_CSS)
    }
}
