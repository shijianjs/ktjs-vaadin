package vaadin.slider

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
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/slider/test/slider.test.js` and the public part of
 * `packages/slider/test/range-slider.test.js`.
 *
 * The pointer/keyboard interaction cases are skipped (they need real pointer sequences); the value,
 * bounds, step and attribute cases are translated.
 */
class SliderTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerSlider()
        registerRangeSlider()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newSlider(): SliderElement = attach(createSlider())

    private fun newRangeSlider(): RangeSliderElement = attach(createRangeSlider())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerSlider()
        val slider = newSlider()

        assertEquals("vaadin-slider", slider.tagName.lowercase())
        assertEquals("vaadin-slider", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun rangeSliderIsDefinedInTheCustomElementRegistry() {
        val ctor = registerRangeSlider()
        val slider = newRangeSlider()

        assertEquals("vaadin-range-slider", slider.tagName.lowercase())
        assertEquals("vaadin-range-slider", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun hasTheDefaultValueAndBounds(): Promise<Unit> {
        val slider = newSlider()

        return nextRender(slider).then {
            assertEquals(0.0, slider.value)
            // The bounds and the step stay `undefined` until they are assigned; the element then
            // falls back to 0 / 100 / 1 internally.
            assertEquals(null, js("(slider) => slider.min == null ? null : slider.min")(slider))
            assertEquals(null, js("(slider) => slider.max == null ? null : slider.max")(slider))
            assertEquals(null, js("(slider) => slider.step == null ? null : slider.step")(slider))
        }
    }

    @Test
    fun storesTheValue(): Promise<Unit> {
        val slider = newSlider()

        return nextRender(slider)
            .then {
                slider.value = 42.0
                nextUpdate(slider)
            }
            .then { assertEquals(42.0, slider.value) }
    }

    @Test
    fun firesValueChanged(): Promise<Unit> {
        val slider = newSlider()
        var fired = 0
        on(slider, "value-changed") { fired++ }

        return nextRender(slider)
            .then {
                slider.value = 10.0
                nextUpdate(slider)
            }
            .then { assertTrue(fired > 0, "expected a value-changed event, got $fired") }
    }

    @Test
    fun appliesTheBounds(): Promise<Unit> {
        val slider = attach(createSlider { min = 10.0; max = 20.0; step = 2.0; value = 14.0 })

        return nextRender(slider).then {
            assertEquals(10.0, slider.min)
            assertEquals(20.0, slider.max)
            assertEquals(2.0, slider.step)
            assertEquals(14.0, slider.value)
        }
    }

    @Test
    fun reflectsDisabled(): Promise<Unit> {
        val slider = newSlider()

        return nextUpdate(slider)
            .then {
                slider.disabled = true
                nextUpdate(slider)
            }
            .then { assertTrue(slider.hasAttribute("disabled")) }
    }

    @Test
    fun isInvalidWhenRequiredAndUnset(): Promise<Unit> {
        val slider = attach(createSlider { required = true })

        return nextRender(slider).then {
            slider.validate()
            assertTrue(slider.invalid)
        }
    }

    @Test
    fun rendersTheTrack(): Promise<Unit> {
        val slider = newSlider()

        return nextRender(slider).then {
            assertNotNull(js("(slider) => slider.shadowRoot.querySelector('[part~=track]')")(slider))
        }
    }

    // range slider

    @Test
    fun rangeSliderHasTwoValues(): Promise<Unit> {
        val slider = newRangeSlider()

        return nextRender(slider).then {
            assertEquals(2, slider.value.size)
        }
    }

    @Test
    fun rangeSliderStoresTheValues(): Promise<Unit> {
        val slider = newRangeSlider()

        return nextRender(slider)
            .then {
                slider.value = js("[20, 80]")
                nextUpdate(slider)
            }
            .then {
                assertEquals(2, slider.value.size)
                assertEquals(20.0, slider.value[0].toString().toDouble())
            }
    }

    @Test
    fun rangeSliderExposesTheAccessibleNames(): Promise<Unit> {
        val slider = attach(createRangeSlider { accessibleNameStart = "From"; accessibleNameEnd = "To" })

        return nextRender(slider).then {
            assertEquals("From", slider.accessibleNameStart)
            assertEquals("To", slider.accessibleNameEnd)
        }
    }

    // Kotlin-only coverage

    @Test
    fun createSliderAppliesTheConfigureBlock() {
        val slider = attach(createSlider { value = 25.0 })

        assertEquals(25.0, slider.value)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-slider", document.createElement(vaadin.VaadinTagName.slider).tagName.lowercase())
        assertEquals(
            "vaadin-range-slider",
            document.createElement(vaadin.VaadinTagName.rangeSlider).tagName.lowercase(),
        )
        assertEquals("min", SliderAttributes.min)
        assertEquals("max", SliderAttributes.max)
        assertEquals("step", SliderAttributes.step)
        assertEquals("value", SliderAttributes.value)
        assertEquals("accessible-name-start", RangeSliderAttributes.accessibleNameStart)
        assertEquals("accessible-name-end", RangeSliderAttributes.accessibleNameEnd)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(SliderEvents.valueChanged)
    }

    @Test
    fun sliderIsNotReadonlyByDefault(): Promise<Unit> {
        val slider = newSlider()

        return nextRender(slider).then { assertFalse(slider.readonly) }
    }
}
