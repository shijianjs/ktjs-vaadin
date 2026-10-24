package vaadin.fieldbase

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.checkbox.CheckboxElement
import vaadin.checkbox.createCheckbox
import vaadin.checkbox.registerCheckbox
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.textfield.TextFieldElement
import vaadin.textfield.createTextField
import vaadin.textfield.registerTextField
import vaadin.typeInto
import web.html.HTMLElement

/**
 * Translated from `packages/field-base/test suites`.
 *
 * The upstream package tests each mixin against a minimal field. The wrapper exposes the mixins as
 * interfaces, so they are exercised through the wrapped fields: `FieldMixin`, `ValidateMixin`,
 * `LabelMixin` and `PatternMixin` through `<vaadin-text-field>`, `CheckedMixin` through
 * `<vaadin-checkbox>`, and `ClearButtonMixin` through both.
 */
class FieldBaseTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerTextField()
        registerCheckbox()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun textField(): TextFieldElement = attach(createTextField())

    private fun checkbox(): CheckboxElement = attach(createCheckbox())

    // FieldMixin

    @Test
    fun fieldMixinExposesTheFieldState(): Promise<Unit> {
        val field = textField()

        return nextRender(field).then {
            field.label = "Name"
            field.required = true
            nextUpdate(field)
        }
            .then {
                assertEquals("Name", field.label)
                assertTrue(field.required)
                assertFalse(field.invalid)
            }
    }

    @Test
    fun fieldMixinReportsHelperTextAndErrorMessage(): Promise<Unit> {
        val field = textField()

        return nextRender(field).then {
            field.helperText = "Helper"
            field.errorMessage = "Error"
            nextUpdate(field)
        }
            .then {
                assertEquals("Helper", field.helperText)
                assertEquals("Error", field.errorMessage)
            }
    }

    // ValidateMixin

    @Test
    fun validateMixinValidatesOnDemand(): Promise<Unit> {
        val field = attach(createTextField { required = true })

        return nextRender(field).then {
            assertFalse(field.validate())
            assertTrue(field.invalid)
        }
    }

    @Test
    fun validateMixinClearsInvalidWhenValid(): Promise<Unit> {
        val field = attach(createTextField { required = true })

        return nextRender(field)
            .then {
                field.validate()
                typeInto(field, "Hello")
                nextUpdate(field)
            }
            .then {
                assertTrue(field.validate())
                assertFalse(field.invalid)
            }
    }

    // LabelMixin / PatternMixin

    @Test
    fun labelMixinRendersTheLabel(): Promise<Unit> {
        val field = attach(createTextField { label = "Name" })

        return nextRender(field).then {
            val slot = js("(field) => field.querySelector('[slot=label]')")(field) as HTMLElement?
            assertEquals(true, slot != null)
        }
    }

    @Test
    fun patternMixinValidatesTheValue(): Promise<Unit> {
        val field = attach(createTextField { pattern = "[0-9]+" })

        return nextRender(field)
            .then {
                typeInto(field, "abc")
                nextUpdate(field)
            }
            .then { assertFalse(field.checkValidity()) }
    }

    // CheckedMixin

    @Test
    fun checkedMixinExposesTheCheckedState(): Promise<Unit> {
        val checkbox = checkbox()

        return nextUpdate(checkbox)
            .then {
                checkbox.checked = true
                nextUpdate(checkbox)
            }
            .then {
                assertTrue(checkbox.checked)
                assertTrue(checkbox.hasAttribute("checked"))
            }
    }

    // ClearButtonMixin

    @Test
    fun clearButtonMixinClearsTheValue(): Promise<Unit> {
        val field = attach(createTextField { value = "Hello" })

        return nextRender(field).then {
            field.clear()
            assertEquals("", field.value)
        }
    }

    @Test
    fun clearButtonMixinExposesTheVisibilityFlag(): Promise<Unit> {
        val field = textField()

        return nextRender(field).then {
            assertFalse(field.clearButtonVisible)
            field.clearButtonVisible = true
            nextUpdate(field)
            assertTrue(field.clearButtonVisible)
        }
    }

    // InputConstraintsMixin

    @Test
    fun inputConstraintsMixinForwardsTheConstraints(): Promise<Unit> {
        val field = attach(
            createTextField {
                pattern = "[a-z]+"
                minlength = 2.0
                maxlength = 10.0
            },
        )

        return nextRender(field).then {
            val input = js("(field) => field.querySelector('input')")(field) as HTMLElement
            assertEquals("[a-z]+", input.getAttribute("pattern"))
            assertEquals("2", input.getAttribute("minlength"))
            assertEquals("10", input.getAttribute("maxlength"))
        }
    }
}
