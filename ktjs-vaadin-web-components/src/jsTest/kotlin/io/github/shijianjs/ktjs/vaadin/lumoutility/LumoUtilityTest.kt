package io.github.shijianjs.ktjs.vaadin.lumoutility

import js.reflect.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import web.cssom.ClassName
import web.dom.document
import web.html.HtmlTagName

/**
 * Covers the generated Lumo utility class list.
 *
 * The values are copied from `LumoUtility.java` and `utility.css`; the test pins a sample per group
 * shape (flat, nested, breakpoint-prefixed) and checks that a `ClassName` constant really lands in
 * `classList`, so a wrong factory or an underscore instead of a dash fails here instead of in a
 * downstream layout.
 */
class LumoUtilityTest {
    @Test
    fun mirrorsFlatGroups() {
        assertEquals("flex", LumoUtility.Display.FLEX.unsafeCast<String>())
        assertEquals("items-center", LumoUtility.AlignItems.CENTER.unsafeCast<String>())
        assertEquals("justify-between", LumoUtility.JustifyContent.BETWEEN.unsafeCast<String>())
        assertEquals("gap-s", LumoUtility.Gap.SMALL.unsafeCast<String>())
        assertEquals("m-auto", LumoUtility.Margin.AUTO.unsafeCast<String>())
        assertEquals("p-s", LumoUtility.Padding.SMALL.unsafeCast<String>())
        assertEquals("overflow-auto", LumoUtility.Overflow.AUTO.unsafeCast<String>())
        assertEquals("text-secondary", LumoUtility.TextColor.SECONDARY.unsafeCast<String>())
        assertEquals("w-full", LumoUtility.Width.FULL.unsafeCast<String>())
        assertEquals("sr-only", LumoUtility.Accessibility.SCREEN_READER_ONLY.unsafeCast<String>())
    }

    @Test
    fun mirrorsBreakpointGroups() {
        assertEquals("sm:items-center", LumoUtility.AlignItems.Breakpoint.Small.CENTER.unsafeCast<String>())
        assertEquals("md:items-start", LumoUtility.AlignItems.Breakpoint.Medium.START.unsafeCast<String>())
        assertEquals("lg:flex-row", LumoUtility.FlexDirection.Breakpoint.Large.ROW.unsafeCast<String>())
        assertEquals("2xl:block", LumoUtility.Display.Breakpoint.XXLarge.BLOCK.unsafeCast<String>())
    }

    @Test
    fun constantsLandInTheClassList() {
        val element = document.createElement(HtmlTagName.div)
        element.classList.add(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER)

        assertTrue(element.classList.contains(ClassName("flex")))
        assertTrue(element.classList.contains(ClassName("items-center")))
    }
}
