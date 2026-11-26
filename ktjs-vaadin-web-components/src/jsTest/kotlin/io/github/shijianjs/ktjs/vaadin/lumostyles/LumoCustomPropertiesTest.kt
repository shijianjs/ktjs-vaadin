package io.github.shijianjs.ktjs.vaadin.lumostyles

import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import web.cssom.StandardProperties
import web.cssom.Color
import web.cssom.`var`
import web.dom.document

/**
 * Covers [LumoCustomProperties]: the names must be the ones Lumo actually declares, and the types
 * must be the ones the CSSOM properties accept.
 */
class LumoCustomPropertiesTest {
    @Test
    fun propertyNamesAreTheUpstreamNames() {
        assertEquals("--lumo-primary-text-color", LumoCustomProperties.PRIMARY_TEXT_COLOR.unsafeCast<String>())
        assertEquals("--lumo-space-l", LumoCustomProperties.SPACE_L.unsafeCast<String>())
        assertEquals("--lumo-icons-checkmark", LumoCustomProperties.Icons.CHECKMARK.unsafeCast<String>())
    }

    @Test
    fun varWrapsATypedName() {
        val color: Color = `var`(LumoCustomProperties.PRIMARY_TEXT_COLOR)

        assertEquals("var(--lumo-primary-text-color)", color.unsafeCast<String>())
    }

    @Test
    fun spacingFitsPaddingAndGap() {
        // `HTMLElement.inlineStyle` in ktjs-dom-kit is exactly this cast: the typed CSSOM view over
        // `CSSStyleDeclaration`, which is where the `var()` values are written to.
        val style = document.createElement("div").style.unsafeCast<StandardProperties>()

        style.padding = `var`(LumoCustomProperties.SPACE_L)
        style.gap = `var`(LumoCustomProperties.SPACE_S)

        assertEquals("var(--lumo-space-l)", style.padding.unsafeCast<String>())
        assertEquals("var(--lumo-space-s)", style.gap.unsafeCast<String>())
    }
}

