package vaadin.switch

import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextUpdate
import vaadin.on
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/switch/test/switch.test.ts`.
 *
 * The upstream "input styles" suite reaches into `element.inputElement` (a protected member that
 * the manifest does not expose) and measures computed styles of the shadow-DOM input; those cases
 * are not translated (see AGENTS.md). The public `checked` / `disabled` / `value` API inherited
 * from `CheckboxMixin` is covered instead.
 */
class SwitchTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun fixture(): SwitchElement = attach(createSwitch())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerSwitch()
        val switch = fixture()

        assertEquals("vaadin-switch", switch.tagName.lowercase())
        assertEquals("vaadin-switch", js("(ctor) => ctor.is")(ctor) as String)
    }

    // Kotlin-only coverage

    @Test
    fun checkedReflectsToTheAttribute(): Promise<Unit> {
        val switch = fixture()

        return nextUpdate(switch)
            .then {
                switch.checked = true
                nextUpdate(switch)
            }
            .then {
                assertTrue(switch.checked)
                assertTrue(switch.hasAttribute("checked"))
                switch.checked = false
                nextUpdate(switch)
            }
            .then {
                assertFalse(switch.checked)
                assertFalse(switch.hasAttribute("checked"))
            }
    }

    @Test
    fun checkedChangeEventIsDispatched(): Promise<Unit> {
        val switch = fixture()
        var fired = 0
        on(switch, "checked-changed") { fired++ }

        return nextUpdate(switch)
            .then {
                switch.checked = true
                nextUpdate(switch)
            }
            .then { assertTrue(fired > 0, "expected a checked-changed event, got $fired") }
    }

    @Test
    fun valueFollowsCheckedState(): Promise<Unit> {
        val switch = fixture()

        return nextUpdate(switch)
            .then {
                switch.checked = true
                nextUpdate(switch)
            }
            .then { assertEquals("on", switch.value) }
    }

    @Test
    fun disabledReflectsToTheAttribute(): Promise<Unit> {
        val switch = fixture()

        return nextUpdate(switch)
            .then {
                switch.disabled = true
                nextUpdate(switch)
            }
            .then { assertTrue(switch.hasAttribute("disabled")) }
    }

    @Test
    fun createSwitchAppliesTheConfigureBlock() {
        val switch = attach(createSwitch { label = "Notifications" })

        assertEquals("Notifications", switch.label)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        val switch = document.createElement(vaadin.VaadinTagName.switch)
        assertEquals("vaadin-switch", switch.tagName.lowercase())
        assertEquals("checked", SwitchAttributes.checked)
        assertEquals("label", SwitchAttributes.label)
        assertEquals("theme", SwitchAttributes.theme)
    }

    @Test
    fun eventConstantsMatchTheEventsTheElementFires(): Promise<Unit> {
        val switch = fixture()
        var changes = 0
        var checkedChanges = 0
        var validated = 0
        on(switch, "change") { changes++ }
        on(switch, "checked-changed") { checkedChanges++ }
        on(switch, "validated") { validated++ }

        return nextUpdate(switch)
            .then {
                // `change` is only fired for user interaction, so toggle the way a user would.
                js("(element) => element.click()")(switch)
                nextUpdate(switch)
            }
            .then {
                assertTrue(changes > 0, "expected a change event, got $changes")
                assertTrue(checkedChanges > 0, "expected a checked-changed event, got $checkedChanges")
                switch.validate()
                nextUpdate(switch)
            }
            .then { assertTrue(validated > 0, "expected a validated event, got $validated") }
    }
}
