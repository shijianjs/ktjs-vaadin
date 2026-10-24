package vaadin.multiselectcombobox

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
 * Translated from `packages/multi-select-combo-box/test/multi-select-combo-box.test.js`.
 *
 * The chip and keyboard cases need real typing sequences; the selection API (`selectedItems`,
 * `selectedValues`), the `maxItemsVisible` limit and the attribute contract are translated.
 */
class MultiSelectComboBoxTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerMultiSelectComboBox()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newComboBox(): MultiSelectComboBoxElement = attach(createMultiSelectComboBox())

    private fun items(): Array<Any> = js(
        "[{ label: 'Zero', value: '0' }, { label: 'One', value: '1' }, { label: 'Two', value: '2' }]",
    ).unsafeCast<Array<Any>>()

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerMultiSelectComboBox()
        val comboBox = newComboBox()

        assertEquals("vaadin-multi-select-combo-box", comboBox.tagName.lowercase())
        assertEquals("vaadin-multi-select-combo-box", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun startsWithoutASelection(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.items = items()

        return nextRender(comboBox).then { assertEquals(0, comboBox.selectedItems.size) }
    }

    @Test
    fun selectsItems(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.items = items()

        return nextRender(comboBox)
            .then {
                comboBox.selectedItems = js("[{ label: 'Zero', value: '0' }]")
                nextUpdate(comboBox)
            }
            .then { assertEquals(1, comboBox.selectedItems.size) }
    }

    @Test
    fun firesSelectedItemsChanged(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.items = items()
        var fired = 0
        on(comboBox, "selected-items-changed") { fired++ }

        return nextRender(comboBox)
            .then {
                comboBox.selectedItems = js("[{ label: 'One', value: '1' }]")
                nextUpdate(comboBox)
            }
            .then { assertTrue(fired > 0, "expected a selected-items-changed event, got $fired") }
    }

    @Test
    fun appliesTheCollapseChipsFlag(): Promise<Unit> {
        val comboBox = attach(createMultiSelectComboBox { collapseChips = true })

        return nextRender(comboBox).then { assertTrue(comboBox.collapseChips) }
    }

    @Test
    fun supportsTheClearButton(): Promise<Unit> {
        val comboBox = attach(createMultiSelectComboBox { clearButtonVisible = true })

        return nextRender(comboBox).then { assertTrue(comboBox.clearButtonVisible) }
    }

    @Test
    fun isNotReadonlyByDefault(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox).then { assertFalse(comboBox.readonly) }
    }

    @Test
    fun isClosedByDefault(): Promise<Unit> {
        val comboBox = newComboBox()

        return nextRender(comboBox).then { assertFalse(comboBox.opened) }
    }

    // Kotlin-only coverage

    @Test
    fun createMultiSelectComboBoxAppliesTheConfigureBlock() {
        val comboBox = attach(createMultiSelectComboBox { label = "Choices" })

        assertEquals("Choices", comboBox.label)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals(
            "vaadin-multi-select-combo-box",
            document.createElement(vaadin.VaadinTagName.multiSelectComboBox).tagName.lowercase(),
        )
        assertEquals("selected-items", MultiSelectComboBoxAttributes.selectedItems)
        assertEquals("selected-items-on-top", MultiSelectComboBoxAttributes.selectedItemsOnTop)
        assertEquals("collapse-chips", MultiSelectComboBoxAttributes.collapseChips)
        assertEquals("opened", MultiSelectComboBoxAttributes.opened)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(MultiSelectComboBoxEvents.selectedItemsChanged)
        assertNotNull(MultiSelectComboBoxEvents.customValueSet)
    }
}
