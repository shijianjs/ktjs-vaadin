package vaadin.progressbar

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.computedStyleProperty
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/progress-bar/test/progress-bar.test.js`.
 *
 * The upstream assertions read the `--vaadin-progress-value` custom property and the width of the
 * `value` part; both are reachable through the computed style of the host, so they are translated
 * as-is. The flexbox cases only assert layout, which works without the shadow DOM.
 */
class ProgressBarTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newProgressBar(): ProgressBarElement = attach(createProgressBar())

    private fun progressValue(element: HTMLElement): String = computedStyleProperty(element, "--vaadin-progress-value")

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerProgressBar()
        val progress = newProgressBar()

        assertEquals("vaadin-progress-bar", progress.tagName.lowercase())
        assertEquals("vaadin-progress-bar", js("(ctor) => ctor.is")(ctor) as String)
    }

    // basic

    @Test
    fun hasTheExpectedDefaults(): Promise<Unit> {
        val progress = newProgressBar()

        return nextRender(progress).then {
            // `value` stays `undefined` until it is assigned, which is how the upstream suite
            // asserts the default as well.
            assertEquals(null, js("(element) => element.value")(progress))
            assertEquals(0.0, progress.min)
            assertEquals(1.0, progress.max)
            assertTrue(!progress.indeterminate)
        }
    }

    @Test
    fun setsTheProgressValueCustomProperty(): Promise<Unit> {
        val progress = newProgressBar()

        return nextRender(progress)
            .then {
                progress.value = 0.1
                nextUpdate(progress)
            }
            .then { assertEquals("0.1", progressValue(progress)) }
    }

    @Test
    fun scalesWithCustomMinAndMax(): Promise<Unit> {
        val progress = newProgressBar()

        return nextRender(progress)
            .then {
                progress.max = 20.0
                progress.min = 10.0
                progress.value = 15.0
                nextUpdate(progress)
            }
            .then { assertEquals("0.5", progressValue(progress)) }
    }

    @Test
    fun normalizesTo1ForWrongBounds(): Promise<Unit> {
        val progress = newProgressBar()

        return nextRender(progress)
            .then {
                progress.value = 10.0
                progress.max = 12.0
                progress.min = 13.0
                nextUpdate(progress)
            }
            .then { assertEquals("1", progressValue(progress)) }
    }

    @Test
    fun normalizesTo1ForEqualBounds(): Promise<Unit> {
        val progress = newProgressBar()

        return nextRender(progress)
            .then {
                progress.value = 10.0
                progress.max = 10.0
                progress.min = 10.0
                nextUpdate(progress)
            }
            .then { assertEquals("1", progressValue(progress)) }
    }

    @Test
    fun normalizesTo0WhenTheValueIsUndefined(): Promise<Unit> {
        val progress = newProgressBar()

        return nextRender(progress)
            .then {
                js("(element) => { element.value = undefined; }")(progress)
                nextUpdate(progress)
            }
            .then { assertEquals("0", progressValue(progress)) }
    }

    @Test
    fun normalizesTo05WhenTheValueIs0AndMinIsMinus1(): Promise<Unit> {
        val progress = newProgressBar()

        return nextRender(progress)
            .then {
                progress.min = -1.0
                progress.value = 0.0
                nextUpdate(progress)
            }
            .then { assertEquals("0.5", progressValue(progress)) }
    }

    @Test
    fun clampsTheNormalizedValueBetween0And1(): Promise<Unit> {
        val progress = newProgressBar()

        return nextRender(progress)
            .then {
                progress.value = -1.0
                nextUpdate(progress)
            }
            .then {
                assertEquals("0", progressValue(progress))
                progress.value = 2.0
                nextUpdate(progress)
            }
            .then { assertEquals("1", progressValue(progress)) }
    }

    @Test
    fun setsAriaValueNow(): Promise<Unit> {
        val progress = newProgressBar()

        return nextRender(progress)
            .then {
                progress.max = 100.0
                progress.value = 50.0
                nextUpdate(progress)
            }
            .then { assertEquals("50", progress.getAttribute("aria-valuenow")) }
    }

    @Test
    fun setsAriaValueMinAndMax(): Promise<Unit> {
        val progress = newProgressBar()

        return nextRender(progress)
            .then {
                progress.max = 100.0
                progress.min = 10.0
                nextUpdate(progress)
            }
            .then {
                assertEquals("10", progress.getAttribute("aria-valuemin"))
                assertEquals("100", progress.getAttribute("aria-valuemax"))
            }
    }

    @Test
    fun setsTheIndeterminateAttribute(): Promise<Unit> {
        val progress = newProgressBar()

        return nextRender(progress)
            .then {
                progress.indeterminate = true
                nextUpdate(progress)
            }
            .then { assertTrue(progress.hasAttribute("indeterminate")) }
    }

    // inside column flexbox

    @Test
    fun hasTheWidthOfTheParentInAColumnFlexbox(): Promise<Unit> {
        val layout = attach(
            vaadin.fixture(
                "<div style=\"display: flex; flex-direction: column; align-items: flex-start;\">" +
                    "<vaadin-progress-bar></vaadin-progress-bar></div>",
            ),
        )
        val progress = js("(layout) => layout.firstElementChild")(layout) as HTMLElement

        return nextRender(progress).then {
            assertEquals(
                js("(element) => element.offsetWidth")(layout) as Int,
                js("(element) => element.offsetWidth")(progress) as Int,
            )
            assertTrue((js("(element) => element.offsetWidth")(progress) as Int) > 100)
        }
    }

    // Kotlin-only coverage

    @Test
    fun createProgressBarAppliesTheConfigureBlock() {
        val progress = attach(createProgressBar { value = 0.25 })

        assertEquals(0.25, progress.value)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        val progress = document.createElement(vaadin.VaadinTagName.progressBar)
        assertEquals("vaadin-progress-bar", progress.tagName.lowercase())
        assertEquals("value", ProgressBarAttributes.value)
        assertEquals("min", ProgressBarAttributes.min)
        assertEquals("max", ProgressBarAttributes.max)
        assertEquals("indeterminate", ProgressBarAttributes.indeterminate)
        assertEquals("theme", ProgressBarAttributes.theme)
    }

    @Test
    fun valueRoundTripsThroughTheProperty(): Promise<Unit> {
        val progress = newProgressBar()

        return nextRender(progress)
            .then {
                progress.value = 0.5
                nextUpdate(progress)
            }
            .then { assertEquals(0.5, progress.value) }
    }
}
