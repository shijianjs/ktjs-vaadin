package vaadin.themablemixin

import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import vaadin.lumostyles.LumoStyles

/**
 * Covers the hand-written `@vaadin/vaadin-themable-mixin` wrapper.
 *
 * `registerStyles` is the only public entry point; the upstream package tests the mixin internals,
 * which are not part of the wrapper. The suite therefore checks that the registration path accepts a
 * single `CSSResult`, a list of them, and the `moduleId` / `include` options. The `CSSResult` values
 * come from the Lumo styles package, which is the supported way to obtain one.
 */
class ThemableMixinTest {
    @Test
    fun registersASingleStylesheet() {
        registerStyles("test-single-styles", LumoStyles.inputField)

        // Registering the same tag twice must stay a no-op instead of throwing.
        registerStyles("test-single-styles", LumoStyles.inputField)
    }

    @Test
    fun registersAStylesheetWithAModuleId() {
        val options: RegisterStylesOptions = js("({})").unsafeCast<RegisterStylesOptions>()
        options.moduleId = "test-module"

        registerStyles("test-module-styles", LumoStyles.helper, options)
    }

    @Test
    fun registersAnArrayOfStylesheets() {
        registerStyles("test-array-styles", arrayOf(LumoStyles.inputField, LumoStyles.inputFieldShared))
    }

    @Test
    fun registersGlobalStylesWithANullTagName() {
        registerStyles(null, LumoStyles.overlay)
    }

    @Test
    fun includesAnotherRegistration() {
        val base: RegisterStylesOptions = js("({})").unsafeCast<RegisterStylesOptions>()
        base.moduleId = "test-include-base"
        registerStyles("test-include-base-styles", LumoStyles.fieldButton, base)

        val derived: RegisterStylesOptions = js("({})").unsafeCast<RegisterStylesOptions>()
        derived.include = StringArrayOrString("test-include-base")
        registerStyles("test-include-styles", LumoStyles.loader, derived)
    }

    @Test
    fun cssResultExposesItsText() {
        val result = LumoStyles.inputField

        assertNotNull(result.cssText)
        assertTrue(result.cssText.isNotEmpty())
    }

    @Test
    fun stringArrayOrStringWrapsBothShapes() {
        val single: StringArrayOrString = StringArrayOrString("module")
        val multiple: StringArrayOrString = StringArrayOrString(arrayOf("one", "two"))

        assertNotNull(single)
        assertNotNull(multiple)
    }

    @Test
    fun themeAttributeIsTheThemingEntryPoint() {
        // The mixin interfaces carry no members: theming happens through the `theme` attribute and
        // the styles registered above.
        val element = js("() => document.createElement('div')")().unsafeCast<web.html.HTMLElement>()
        element.setAttribute("theme", "dark")

        assertEquals("dark", element.getAttribute("theme"))
    }
}
