package vaadin.combobox

import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/combo-box/test/items.test.js`.
 *
 * The upstream cases read the dropdown through the `_scroller` protected member; here the item
 * elements are queried from the combo box light DOM instead (the scroller is appended with
 * `slot="overlay"`), so every case below stays inside the public API.
 *
 * Dropped upstream cases:
 * - `should change the 'model.focused' property`: it drives the focus through `_focusedIndex`.
 * - `should not run renderers for invisible items`: it asserts on the identity of recycled item
 *   children while filtering, which depends on the virtualizer's internal item pool.
 *
 * One deliberate divergence: the upstream object items use numeric ids and set `value = 2`. The
 * wrapper types `value` as `String`, and the component compares `_getItemValue(item) === value`
 * strictly, so the fixtures use string ids — the code path under test is the same.
 */
class ItemsTest {
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

    private fun newComboBox(): ComboBoxElement = attach(createComboBox())

    private fun stringComboBox(): ComboBoxElement = newComboBox().apply { items = stringItems("foo", "bar", "baz") }

    private fun personItems(): Array<JsAny> = js(
        "[{ id: '1', name: 'John', surname: 'Doe', age: 30 }," +
            " { id: '2', name: 'Jane', surname: 'Smith', age: 25 }," +
            " { id: '3', name: 'Bob', surname: 'Johnson', age: 35 }]",
    ).unsafeCast<Array<JsAny>>()

    private class RenderCall(
        val root: HTMLElement,
        val owner: ComboBoxElement,
        val model: ComboBoxItemModel,
    )

    // renderer

    @Test
    fun passesRootOwnerAndModelToTheRenderer(): Promise<Unit> {
        val comboBox = stringComboBox()
        val calls = mutableListOf<RenderCall>()
        comboBox.renderer = { root, owner, model -> calls += RenderCall(root, owner, model) }

        comboBox.opened = true

        return nextRender(comboBox).then {
            assertTrue(calls.isNotEmpty(), "expected the renderer to run, got ${calls.size} calls")
            val call = calls[0]

            assertEquals("vaadin-combo-box-item", call.root.tagName.lowercase())
            assertSame(comboBox, call.owner)
            assertEquals("foo", call.model.item.unsafeCast<String>())
            assertEquals(0.0, call.model.index)
            assertFalse(call.model.selected)
            assertFalse(call.model.focused)
        }
    }

    @Test
    fun updatesModelSelectedWhenTheValueChanges(): Promise<Unit> {
        val comboBox = stringComboBox()
        val models = mutableListOf<ComboBoxItemModel>()
        comboBox.renderer = { _, _, model -> models += model }

        comboBox.opened = true

        return nextRender(comboBox)
            .then {
                comboBox.value = "foo"
                nextUpdate(comboBox)
            }
            .then {
                assertTrue(models.isNotEmpty())
                assertTrue(models.last().selected, "expected the last rendered model to be selected")
            }
    }

    @Test
    fun usesTheRendererForTheItemContent(): Promise<Unit> {
        val comboBox = stringComboBox()
        comboBox.renderer = { root, _, model ->
            root.appendChild(document.createTextNode("${model.item} ${model.index}"))
        }

        comboBox.opened = true

        return nextRender(comboBox).then {
            assertEquals("foo 0", comboBoxFirstItem(comboBox)?.textContent?.trim())
        }
    }

    @Test
    fun runsRenderersAgainWhenRequestingAContentUpdate(): Promise<Unit> {
        val comboBox = stringComboBox()
        var calls = 0
        comboBox.renderer = { _, _, _ -> calls++ }

        comboBox.opened = true

        return nextRender(comboBox).then {
            assertEquals(3, calls, "expected one renderer call per item after opening")

            comboBox.requestContentUpdate()

            assertEquals(6, calls, "expected the renderers to run again for every item")
        }
    }

    @Test
    fun doesNotThrowWhenRequestingAContentUpdateBeforeOpening() {
        val comboBox = stringComboBox()

        comboBox.requestContentUpdate()
    }

    @Test
    fun rendersTheItemLabelWhenRemovingTheRenderer(): Promise<Unit> {
        val comboBox = stringComboBox()
        var firstRoot: HTMLElement? = null
        comboBox.renderer = { root, _, _ ->
            if (firstRoot == null) {
                firstRoot = root
            }
            root.textContent = "bar"
        }

        comboBox.opened = true

        return nextRender(comboBox)
            .then {
                assertEquals("bar", firstRoot?.textContent)
                comboBox.renderer = null
                nextRender(comboBox)
            }
            .then { assertEquals("foo", firstRoot?.textContent) }
    }

    @Test
    fun doesNotKeepTheRendererContentWhenRestoringTheRenderer(): Promise<Unit> {
        val comboBox = stringComboBox()
        var firstRoot: HTMLElement? = null
        val renderer: ComboBoxRenderer = { root, _, model ->
            if (firstRoot == null) {
                firstRoot = root
            }
            val span = (root.firstElementChild as HTMLElement?) ?: document.createElement("span").also { root.appendChild(it) }
            span.textContent = model.item.unsafeCast<String>()
        }
        comboBox.renderer = renderer

        comboBox.opened = true

        return nextRender(comboBox)
            .then {
                comboBox.renderer = null
                nextRender(comboBox)
            }
            .then {
                assertEquals("foo", firstRoot?.textContent)
                comboBox.renderer = renderer
                nextRender(comboBox)
            }
            .then {
                val root = assertNotNull(firstRoot)
                assertEquals("foo", root.textContent)
                assertEquals(1, root.querySelectorAll("span").length)
            }
    }

    @Test
    fun clearsThePreviousContentWhenAssigningANewRenderer(): Promise<Unit> {
        val comboBox = stringComboBox()
        var firstRoot: HTMLElement? = null

        comboBox.opened = true

        return nextRender(comboBox)
            .then {
                comboBox.renderer = { root, _, _ ->
                    if (firstRoot == null) {
                        firstRoot = root
                    }
                }
                nextRender(comboBox)
            }
            .then { assertEquals("", firstRoot?.textContent) }
    }

    // itemClassNameGenerator

    @Test
    fun setsTheClassNameOnDropdownItems(): Promise<Unit> {
        val comboBox = stringComboBox()
        comboBox.itemClassNameGenerator = { item -> "item-" + item.unsafeCast<String>() }

        comboBox.`open`()

        return nextRender(comboBox).then {
            val items = comboBoxItems(comboBox)

            assertEquals(3, items.size)
            assertEquals("item-foo", items[0].getAttribute("class"))
            assertEquals("item-bar", items[1].getAttribute("class"))
            assertEquals("item-baz", items[2].getAttribute("class"))
        }
    }

    @Test
    fun removesTheClassNameWhenTheGeneratorReturnsAnEmptyString(): Promise<Unit> {
        val comboBox = stringComboBox()
        comboBox.itemClassNameGenerator = { item -> "item-" + item.unsafeCast<String>() }

        comboBox.`open`()

        return nextRender(comboBox)
            .then {
                comboBox.close()
                comboBox.itemClassNameGenerator = { "" }
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                val items = comboBoxItems(comboBox)

                assertEquals(3, items.size)
                assertEquals("", items[0].getAttribute("class"))
                assertEquals("", items[1].getAttribute("class"))
                assertEquals("", items[2].getAttribute("class"))
            }
    }

    @Test
    fun removesTheClassNameWhenTheGeneratorIsSetToNull(): Promise<Unit> {
        val comboBox = stringComboBox()
        comboBox.itemClassNameGenerator = { item -> "item-" + item.unsafeCast<String>() }

        comboBox.`open`()

        return nextRender(comboBox)
            .then {
                comboBox.close()
                comboBox.itemClassNameGenerator = null
                comboBox.`open`()
                nextRender(comboBox)
            }
            .then {
                val items = comboBoxItems(comboBox)

                assertEquals(3, items.size)
                assertEquals("", items[0].getAttribute("class"))
                assertEquals("", items[1].getAttribute("class"))
                assertEquals("", items[2].getAttribute("class"))
            }
    }

    // itemLabelGenerator

    @Test
    fun generatesTheItemTextContentWithTheLabelGenerator(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.items = personItems()
        comboBox.itemLabelGenerator = { item -> item.unsafeCast<PersonItem>().let { "${it.name} ${it.surname}" } }

        comboBox.`open`()

        return nextRender(comboBox).then {
            val items = comboBoxItems(comboBox)

            assertEquals(3, items.size)
            assertEquals("John Doe", items[0].textContent)
            assertEquals("Jane Smith", items[1].textContent)
            assertEquals("Bob Johnson", items[2].textContent)
        }
    }

    @Test
    fun usesTheGeneratedLabelAsInputValueForTheSelectedItem(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.items = personItems()
        comboBox.itemLabelGenerator = { item -> item.unsafeCast<PersonItem>().let { "${it.name} ${it.surname}" } }
        comboBox.itemValuePath = "id"

        comboBox.value = "2"

        return nextRender(comboBox).then { assertEquals("Jane Smith", comboBoxInputValue(comboBox)) }
    }

    @Test
    fun filtersItemsUsingTheGeneratedLabel() {
        val comboBox = newComboBox()
        comboBox.items = personItems()
        comboBox.itemLabelGenerator = { item -> item.unsafeCast<PersonItem>().let { "${it.name} ${it.surname}" } }

        setComboBoxInput(comboBox, "john")

        val filtered = assertNotNull(comboBox.filteredItems)
        assertEquals(2, filtered.size)
        assertSame(comboBox.items!![0], filtered[0])
        assertSame(comboBox.items!![2], filtered[1])
    }

    @Test
    fun prefersTheLabelGeneratorOverTheLabelPath(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.items = personItems()
        comboBox.itemLabelPath = "surname"
        comboBox.itemLabelGenerator = { item -> item.unsafeCast<PersonItem>().name }

        comboBox.`open`()

        return nextRender(comboBox).then {
            val items = comboBoxItems(comboBox)

            assertEquals("John", items[0].textContent)
            assertEquals("Jane", items[1].textContent)
        }
    }

    @Test
    fun acceptsAnEmptyStringFromTheLabelGenerator(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.items = personItems()
        comboBox.itemLabelGenerator = { item ->
            val person = item.unsafeCast<PersonItem>()
            if (person.id == "2") "" else "${person.name} ${person.surname}"
        }

        comboBox.`open`()

        return nextRender(comboBox).then {
            val items = comboBoxItems(comboBox)

            assertEquals("John Doe", items[0].textContent)
            assertEquals("", items[1].textContent)
            assertEquals("Bob Johnson", items[2].textContent)
        }
    }

    @Test
    fun updatesTheDropdownWhenTheLabelGeneratorChanges(): Promise<Unit> {
        val comboBox = newComboBox()
        comboBox.items = personItems()
        comboBox.itemLabelGenerator = { item -> item.unsafeCast<PersonItem>().let { "${it.name} ${it.surname}" } }

        comboBox.`open`()

        return nextRender(comboBox)
            .then {
                assertEquals("John Doe", comboBoxFirstItem(comboBox)?.textContent)

                comboBox.itemLabelGenerator = { item ->
                    item.unsafeCast<PersonItem>().let { "${it.name} (${it.age})" }
                }
                nextRender(comboBox)
            }
            .then { assertEquals("John (30)", comboBoxFirstItem(comboBox)?.textContent) }
    }
}

/** The object shape used by the item generator cases of `items.test.js`. */
private external interface PersonItem {
    var id: String
    var name: String
    var surname: String
    var age: Double
}
