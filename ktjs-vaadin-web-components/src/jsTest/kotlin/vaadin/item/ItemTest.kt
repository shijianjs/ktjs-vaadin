package vaadin.item

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
import vaadin.computedStyle
import vaadin.detachAll
import vaadin.fixture
import vaadin.nextRender
import vaadin.nextUpdate
import vaadin.shadowQuery
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/item/test/item.test.js` and the public half of
 * `packages/item/test/item-mixin.test.js`.
 *
 * The upstream files also assert private flags (`_hasVaadinItemMixin`); those are internal state and
 * are skipped (see AGENTS.md). The observable behaviour — `label`, `selected`, `disabled`, `value`
 * and the `content` part of the shadow root — is covered instead.
 */
class ItemTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerItem()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newItem(): ItemElement = attach(createItem())

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerItem()
        val item = newItem()

        assertEquals("vaadin-item", item.tagName.lowercase())
        assertEquals("vaadin-item", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun hasAnUnnamedSlotForTheLabel(): Promise<Unit> {
        val item = attach(fixture("<vaadin-item>label</vaadin-item>").unsafeCast<ItemElement>())

        return nextRender(item).then {
            val slot = assertNotNull(shadowQuery(item, "slot:not([name])"))
            assertEquals(
                "label",
                js("(slot) => slot.assignedNodes()[0].textContent.trim()")(slot) as String,
            )
        }
    }

    @Test
    fun hasAContentPartWrappingTheSlot(): Promise<Unit> {
        val item = newItem()

        return nextRender(item).then {
            val content = assertNotNull(shadowQuery(item, "[part=\"content\"]"))
            val slot = assertNotNull(shadowQuery(item, "slot"))
            assertEquals(
                true,
                js("(slot, content) => slot.parentElement === content")(slot, content) as Boolean,
            )
        }
    }

    @Test
    fun contentPartIsAFlexContainer(): Promise<Unit> {
        val item = newItem()

        return nextRender(item).then {
            val content = assertNotNull(shadowQuery(item, "[part=\"content\"]"))
            assertEquals("flex", computedStyle(content, "display"))
        }
    }

    // label

    @Test
    fun rendersTheLabelProperty(): Promise<Unit> {
        val item = newItem()

        return nextUpdate(item)
            .then {
                item.label = "Item label"
                nextUpdate(item)
            }
            .then { assertEquals("Item label", item.label) }
    }

    @Test
    fun reflectsSelectedToTheAttribute(): Promise<Unit> {
        val item = newItem()

        return nextUpdate(item)
            .then {
                item.selected = true
                nextUpdate(item)
            }
            .then { assertTrue(item.hasAttribute("selected")) }
    }

    @Test
    fun reflectsDisabledToTheAttribute(): Promise<Unit> {
        val item = newItem()

        return nextUpdate(item)
            .then {
                item.disabled = true
                nextUpdate(item)
            }
            .then { assertTrue(item.hasAttribute("disabled")) }
    }

    @Test
    fun storesTheValue(): Promise<Unit> {
        val item = newItem()

        return nextUpdate(item)
            .then {
                item.value = "foo"
                nextUpdate(item)
            }
            .then { assertEquals("foo", item.value) }
    }

    @Test
    fun isNotSelectedByDefault(): Promise<Unit> {
        val item = newItem()

        return nextRender(item).then { assertFalse(item.selected) }
    }

    // Kotlin-only coverage

    @Test
    fun createItemAppliesTheConfigureBlock() {
        val item = attach(createItem { value = "bar" })

        assertEquals("bar", item.value)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-item", document.createElement(vaadin.VaadinTagName.item).tagName.lowercase())
        assertEquals("disabled", ItemAttributes.disabled)
        assertEquals("selected", ItemAttributes.selected)
        assertEquals("theme", ItemAttributes.theme)
    }

    @Test
    fun labelIsNotReflectedToTheAttribute(): Promise<Unit> {
        val item = newItem()

        return nextUpdate(item)
            .then {
                item.label = "Item label"
                nextUpdate(item)
            }
            .then { assertEquals(null, item.getAttribute("label")) }
    }
}
