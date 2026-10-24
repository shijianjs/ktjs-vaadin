package vaadin.checkbox

import kotlin.js.Promise
import kotlin.js.js
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
import vaadin.shadowQuery
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/checkbox/test/checkbox.test.js` and the public part of
 * `packages/checkbox/test/checkbox-validation.test.js`.
 *
 * The upstream "input styles" cases measure the shadow-DOM `<input>`; those are skipped (visual and
 * private-member assertions, see AGENTS.md). Everything reachable through the public API —
 * `checked`, `indeterminate`, `label`, `value`, the `change` event and validation — is translated.
 */
class CheckboxTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerCheckbox()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newCheckbox(): CheckboxElement = attach(createCheckbox())

    private fun toggle(checkbox: CheckboxElement) {
        js("(element) => element.click()")(checkbox)
    }

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerCheckbox()
        val checkbox = newCheckbox()

        assertEquals("vaadin-checkbox", checkbox.tagName.lowercase())
        assertEquals("vaadin-checkbox", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun isUncheckedByDefault(): Promise<Unit> {
        val checkbox = newCheckbox()

        return nextRender(checkbox).then { assertFalse(checkbox.checked) }
    }

    @Test
    fun togglesCheckedOnClick(): Promise<Unit> {
        val checkbox = newCheckbox()

        return nextRender(checkbox)
            .then {
                toggle(checkbox)
                nextUpdate(checkbox)
            }
            .then {
                assertTrue(checkbox.checked)
                toggle(checkbox)
                nextUpdate(checkbox)
            }
            .then { assertFalse(checkbox.checked) }
    }

    @Test
    fun reflectsCheckedToTheAttribute(): Promise<Unit> {
        val checkbox = newCheckbox()

        return nextUpdate(checkbox)
            .then {
                checkbox.checked = true
                nextUpdate(checkbox)
            }
            .then { assertTrue(checkbox.hasAttribute("checked")) }
    }

    @Test
    fun firesCheckedChangedAndChange(): Promise<Unit> {
        val checkbox = newCheckbox()
        var checkedChanged = 0
        var changes = 0
        on(checkbox, "checked-changed") { checkedChanged++ }
        on(checkbox, "change") { changes++ }

        return nextRender(checkbox)
            .then {
                toggle(checkbox)
                nextUpdate(checkbox)
            }
            .then {
                assertTrue(checkedChanged > 0, "expected checked-changed, got $checkedChanged")
                assertTrue(changes > 0, "expected change, got $changes")
            }
    }

    @Test
    fun hasTheOnValueByDefault(): Promise<Unit> {
        val checkbox = newCheckbox()

        return nextRender(checkbox).then { assertEquals("on", checkbox.value) }
    }

    @Test
    fun doesNotToggleWhenDisabled(): Promise<Unit> {
        val checkbox = newCheckbox()

        return nextRender(checkbox)
            .then {
                checkbox.disabled = true
                nextUpdate(checkbox)
            }
            .then {
                toggle(checkbox)
                nextUpdate(checkbox)
            }
            .then { assertFalse(checkbox.checked) }
    }

    @Test
    fun supportsTheIndeterminateState(): Promise<Unit> {
        val checkbox = newCheckbox()

        return nextUpdate(checkbox)
            .then {
                checkbox.indeterminate = true
                nextUpdate(checkbox)
            }
            .then {
                assertTrue(checkbox.indeterminate)
                assertTrue(checkbox.hasAttribute("indeterminate"))
            }
    }

    @Test
    fun rendersTheLabelProperty(): Promise<Unit> {
        val checkbox = newCheckbox()

        return nextRender(checkbox)
            .then {
                checkbox.label = "Accept terms"
                nextUpdate(checkbox)
            }
            .then { assertEquals("Accept terms", checkbox.label) }
    }

    @Test
    fun isInvalidWhenRequiredAndUnchecked(): Promise<Unit> {
        val checkbox = attach(createCheckbox { required = true })

        return nextRender(checkbox).then {
            assertFalse(checkbox.checkValidity())
            checkbox.validate()
            assertTrue(checkbox.invalid)
        }
    }

    @Test
    fun becomesValidWhenChecked(): Promise<Unit> {
        val checkbox = attach(createCheckbox { required = true })

        return nextRender(checkbox)
            .then {
                checkbox.checked = true
                nextUpdate(checkbox)
            }
            .then { assertTrue(checkbox.checkValidity()) }
    }

    @Test
    fun rendersTheSlottedInput(): Promise<Unit> {
        val checkbox = newCheckbox()

        return nextRender(checkbox).then {
            // The field pattern keeps the `<input>` in the light DOM and slots it into the shadow
            // root, so it is queried on the host rather than through the shadow root.
            assertNotNull(js("(element) => element.querySelector('input')")(checkbox) as HTMLElement?)
        }
    }

    // Kotlin-only coverage

    @Test
    fun createCheckboxAppliesTheConfigureBlock() {
        val checkbox = attach(createCheckbox { checked = true })

        assertTrue(checkbox.checked)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-checkbox", document.createElement(vaadin.VaadinTagName.checkbox).tagName.lowercase())
        assertEquals("checked", CheckboxAttributes.checked)
        assertEquals("indeterminate", CheckboxAttributes.indeterminate)
        assertEquals("label", CheckboxAttributes.label)
        assertEquals("name", CheckboxAttributes.name)
        assertEquals("readonly", CheckboxAttributes.readonly)
        assertEquals("required", CheckboxAttributes.required)
        assertEquals("theme", CheckboxAttributes.theme)
    }

    @Test
    fun eventTypesAreExposed() {
        assertNotNull(CheckboxEvents.checkedChanged)
        assertNotNull(CheckboxEvents.indeterminateChanged)
        assertNotNull(CheckboxEvents.change)
        assertNotNull(CheckboxEvents.validated)
        assertNotNull(CheckboxEvents.invalidChanged)
    }

    @Test
    fun validatesOnDemand(): Promise<Unit> {
        val checkbox = attach(createCheckbox { required = true })
        var validated = 0
        on(checkbox, "validated") { validated++ }

        return nextRender(checkbox).then {
            checkbox.validate()
            assertTrue(validated > 0, "expected a validated event, got $validated")
        }
    }
}
