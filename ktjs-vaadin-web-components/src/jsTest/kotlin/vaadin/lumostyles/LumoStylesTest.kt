package vaadin.lumostyles

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Covers the hand-written `@vaadin/vaadin-lumo-styles` wrapper.
 *
 * The package is CSS-only: the upstream tests are visual, so the Kotlin suite checks the contract
 * that matters to callers — every exported `CSSResult` carries CSS text, the array-valued entry
 * keeps its order, and the stylesheet entry points point at the published files.
 */
class LumoStylesTest {
    @Test
    fun exposesCssResults() {
        assertNotNull(LumoStyles.fieldButton)
        assertNotNull(LumoStyles.helper)
        assertNotNull(LumoStyles.inputField)
        assertNotNull(LumoStyles.inputFieldShared)
        assertNotNull(LumoStyles.loader)
        assertNotNull(LumoStyles.overlay)
        assertNotNull(LumoStyles.requiredField)
    }

    @Test
    fun cssResultsCarryCssText() {
        assertTrue(LumoStyles.fieldButton.cssText.isNotEmpty())
        assertTrue(LumoStyles.helper.cssText.isNotEmpty())
        assertTrue(LumoStyles.inputField.cssText.isNotEmpty())
        assertTrue(LumoStyles.overlay.cssText.isNotEmpty())
    }

    @Test
    fun menuOverlayIsAnArrayOfCssResults() {
        val styles = LumoStyles.menuOverlay

        assertTrue(styles.isNotEmpty())
        assertTrue(styles.all { it.cssText.isNotEmpty() })
        assertNotNull(LumoStyles.menuOverlayCore)
    }

    @Test
    fun registerLumoStylesImportsTheBundle() {
        assertNotNull(registerLumoStyles())
    }

    @Test
    fun registerLumoIconsImportsTheIconset() {
        assertNotNull(registerLumoIcons())
    }

    @Test
    fun themeEntryPointsPointAtThePublishedFiles() {
        assertEquals("@vaadin/vaadin-lumo-styles/lumo.css", LumoThemes.CSS)
        assertEquals("@vaadin/vaadin-lumo-styles/utility.css", LumoThemes.UTILITY_CSS)
        assertEquals("@vaadin/vaadin-lumo-styles/presets/compact.css", LumoThemes.COMPACT_CSS)
        assertEquals("@vaadin/vaadin-lumo-styles/dist/lumo.css", LumoThemes.DIST_CSS)
        assertEquals(
            "@vaadin/vaadin-lumo-styles/dist/presets/compact.css",
            LumoThemes.DIST_COMPACT_CSS,
        )
    }
}
