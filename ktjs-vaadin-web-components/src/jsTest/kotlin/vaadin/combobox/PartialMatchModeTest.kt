package vaadin.combobox

import kotlin.js.Promise
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import web.html.HTMLElement

/**
 * Translated from the value-commit parts of `packages/combo-box/test/partial-match-mode.test.js`.
 *
 * The upstream suite types with `sendKeys({ type: … })`; here the input value is written and an
 * `input` event dispatched ([setComboBoxInput]). `Enter` is a single dispatched `keydown`
 * ([pressEnterInComboBox]) and `Tab` / outside click are replaced by [ComboBoxElement.close], which
 * commits the input value the same way.
 *
 * Dropped upstream cases: every `getFocusedItemIndex` assertion (the highlight of the partial match
 * mirrors the internal `_focusedIndex`) and the `lazy loading` group, which needs the same focus
 * state after an async provider response.
 */
class PartialMatchModeTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerComboBox()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun comboBox(configure: ComboBoxElement.() -> Unit = {}): ComboBoxElement =
        attach(createComboBox(configure)).apply { items = stringItems("apple", "banana", "grapefruit", "grape") }

    // none (default)

    @Test
    fun isNoneByDefault() {
        assertEquals("none", comboBox().partialMatchMode.unsafeCast<String>())
    }

    @Test
    fun commitsTheExactMatchOnEnter(): Promise<Unit> {
        val comboBox = comboBox()

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "grape")
            pressEnterInComboBox(comboBox)

            assertEquals("grape", comboBox.value)
        }
    }

    @Test
    fun commitsTheExactMatchOnClose(): Promise<Unit> {
        val comboBox = comboBox()

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "grape")
            comboBox.close()

            assertEquals("grape", comboBox.value)
        }
    }

    @Test
    fun doesNotCommitThePartialMatchOnEnter(): Promise<Unit> {
        val comboBox = comboBox()

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "grap")
            pressEnterInComboBox(comboBox)

            assertEquals("", comboBox.value)
        }
    }

    // first-match

    @Test
    fun commitsTheFirstPartialMatchOnEnter(): Promise<Unit> {
        val comboBox = comboBox { partialMatchMode = ComboBoxPartialMatchModes.firstMatch }

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "grap")
            pressEnterInComboBox(comboBox)

            assertEquals("grapefruit", comboBox.value)
        }
    }

    @Test
    fun doesNotCommitOnEnterWhenNoItemsMatch(): Promise<Unit> {
        val comboBox = comboBox { partialMatchMode = ComboBoxPartialMatchModes.firstMatch }

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "xyz")
            pressEnterInComboBox(comboBox)

            assertEquals("", comboBox.value)
        }
    }

    @Test
    fun commitsTheFirstPartialMatchOnClose(): Promise<Unit> {
        val comboBox = comboBox { partialMatchMode = ComboBoxPartialMatchModes.firstMatch }

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "grap")
            comboBox.close()

            assertEquals("grapefruit", comboBox.value)
        }
    }

    // only-match

    @Test
    fun commitsTheOnlyPartialMatchOnEnter(): Promise<Unit> {
        val comboBox = comboBox { partialMatchMode = ComboBoxPartialMatchModes.onlyMatch }

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "grapef")
            pressEnterInComboBox(comboBox)

            assertEquals("grapefruit", comboBox.value)
        }
    }

    @Test
    fun doesNotCommitWhenMultipleItemsMatch(): Promise<Unit> {
        val comboBox = comboBox { partialMatchMode = ComboBoxPartialMatchModes.onlyMatch }

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "grap")
            pressEnterInComboBox(comboBox)

            assertEquals("", comboBox.value)
        }
    }

    // autoOpenDisabled

    @Test
    fun commitsTheExactMatchOnEnterWhileClosed(): Promise<Unit> {
        val comboBox = comboBox {
            autoOpenDisabled = true
            partialMatchMode = ComboBoxPartialMatchModes.firstMatch
        }

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "grape")
            pressEnterInComboBox(comboBox)

            assertEquals("grape", comboBox.value)
        }
    }

    @Test
    fun doesNotCommitTheFirstPartialMatchOnEnterWhileClosed(): Promise<Unit> {
        val comboBox = comboBox {
            autoOpenDisabled = true
            partialMatchMode = ComboBoxPartialMatchModes.firstMatch
        }

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "grap")
            pressEnterInComboBox(comboBox)

            assertEquals("", comboBox.value)
        }
    }

    @Test
    fun commitsTheFirstPartialMatchOnEnterAfterOpening(): Promise<Unit> {
        val comboBox = comboBox {
            autoOpenDisabled = true
            partialMatchMode = ComboBoxPartialMatchModes.firstMatch
        }

        return nextRender(comboBox).then {
            setComboBoxInput(comboBox, "grap")
            comboBox.`open`()
            pressEnterInComboBox(comboBox)

            assertEquals("grapefruit", comboBox.value)
        }
    }
}
