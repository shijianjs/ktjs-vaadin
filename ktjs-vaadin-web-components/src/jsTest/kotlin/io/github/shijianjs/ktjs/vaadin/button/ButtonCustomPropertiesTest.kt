package io.github.shijianjs.ktjs.vaadin.button

import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import web.cssom.StandardProperties
import web.cssom.`var`
import web.dom.document

/**
 * Covers [ButtonCustomProperties]: generated names must stay the upstream ones, and their types must
 * be the ones the CSSOM properties the component feeds them into accept.
 */
class ButtonCustomPropertiesTest {
    @Test
    fun propertyNamesAreTheUpstreamNames() {
        assertEquals("--vaadin-button-background", ButtonCustomProperties.BACKGROUND.unsafeCast<String>())
        assertEquals("--vaadin-button-border-radius", ButtonCustomProperties.BORDER_RADIUS.unsafeCast<String>())
        assertEquals("--vaadin-button-font-weight", ButtonCustomProperties.FONT_WEIGHT.unsafeCast<String>())
    }

    @Test
    fun varWrapsATypedName() {
        assertEquals("var(--vaadin-button-background)", `var`(ButtonCustomProperties.BACKGROUND).unsafeCast<String>())
    }

    @Test
    fun lengthsFitThePropertiesTheButtonFeeds() {
        val style = document.createElement("div").style.unsafeCast<StandardProperties>()

        style.padding = `var`(ButtonCustomProperties.PADDING)
        style.height = `var`(ButtonCustomProperties.HEIGHT)

        assertEquals("var(--vaadin-button-padding)", style.padding.unsafeCast<String>())
        assertEquals("var(--vaadin-button-height)", style.height.unsafeCast<String>())
    }
}
