package vaadin.virtuallist

import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.js
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextFrame
import vaadin.nextResize
import vaadin.nextUpdate
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/virtual-list/test/virtual-list.test.js`.
 *
 * Cases that assert `getBoundingClientRect()` geometry or `elementFromPoint()` (visible index,
 * full-width items, scroll-to-index position) are skipped; everything observable through the
 * public API is covered. The Lit directive entry points are not wrapped (see AGENTS.md).
 */
class VirtualListTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun fixture(): VirtualListElement = attach(createVirtualList())

    private fun items(count: Int, prefix: String = "value"): Array<JsAny> =
        Array(count) { index -> item("$prefix-$index") }

    private fun item(value: String): JsAny {
        val item = js("({})").unsafeCast<TestItem>()
        item.value = value
        return item
    }

    private fun renderItem(root: HTMLElement, item: JsAny?) {
        root.textContent = js("(item) => item.value")(item) as String
    }

    private fun renderValue(list: VirtualListElement) {
        list.renderer = { root, _, model -> renderItem(root, model.item) }
    }

    @Test
    fun hasADefaultHeight() {
        assertEquals(400, js("(element) => element.offsetHeight")(fixture()) as Int)
    }

    @Test
    fun defaultHeightCanBeOverridden() {
        val list = fixture()
        list.style.height = "100px"
        assertEquals(100, js("(element) => element.offsetHeight")(list) as Int)
    }

    @Test
    fun hiddenListHasNoHeight() {
        val list = fixture()
        list.setAttribute("hidden", "")
        assertEquals(0, js("(element) => element.offsetHeight")(list) as Int)
    }

    @Test
    fun noElementsAreCreatedWithoutARenderer(): Promise<Unit> {
        val list = fixture()
        list.items = items(1)

        return nextUpdate(list).then { assertEquals(0, list.children.length) }
    }

    @Test
    fun hasListRole() {
        assertEquals("list", fixture().getAttribute("role"))
    }

    @Test
    fun itemsAreRenderedAsDivs(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list).then {
            assertTrue(list.childElementCount > 0)
            assertEquals("div", list.children.item(0)!!.localName)
        }
    }

    @Test
    fun clearingItemsHidesTheRenderedElements(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list)
            .then {
                list.items = null
                nextUpdate(list)
            }
            .then {
                js("(element) => [...element.children].every((child) => child.hidden)")(list) as Boolean
            }
            .then { assertTrue(it) }
    }

    @Test
    fun scrollToIndexWithoutRendererDoesNotThrow(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list)
            .then {
                list.renderer = null
                list.scrollToIndex(50.0)
            }
            .then { assertTrue(true) }
    }

    @Test
    fun replacingItemsWithTheSameSizeUpdatesTheContent(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list)
            .then {
                list.items = items(100, "text")
                nextUpdate(list)
            }
            .then { assertEquals("text-0", list.children.item(0)!!.textContent!!.trim()) }
    }

    @Test
    fun replacingItemsWithAShorterArrayUpdatesTheContent(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list)
            .then {
                list.items = items(99, "text")
                nextUpdate(list)
            }
            .then { assertEquals("text-0", list.children.item(0)!!.textContent!!.trim()) }
    }

    @Test
    fun assigningANewRendererClearsTheOldContent(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list)
            .then {
                list.renderer = { _, _, _ -> }
                nextUpdate(list)
            }
            .then { assertEquals("", list.children.item(0)!!.textContent!!.trim()) }
    }

    @Test
    fun removingTheRendererClearsTheOldContent(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list)
            .then {
                list.renderer = null
                nextUpdate(list)
            }
            .then { assertEquals("", list.children.item(0)!!.textContent!!.trim()) }
    }

    @Test
    fun requestContentUpdateRerunsTheRenderer(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        var name = "foo"
        list.renderer = { root, _, _ -> root.textContent = name }

        return nextUpdate(list)
            .then {
                assertEquals("foo", list.children.item(0)!!.textContent!!.trim())
                name = "bar"
                list.requestContentUpdate()
                nextUpdate(list)
            }
            .then { assertEquals("bar", list.children.item(0)!!.textContent!!.trim()) }
    }

    @Test
    fun itemsHaveListitemRole(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list).then { assertEquals("listitem", list.children.item(0)!!.getAttribute("role")) }
    }

    @Test
    fun itemsGetAriaSetSizeAndPosInSet(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list)
            .then {
                list.scrollToIndex(99.0)
                nextFrame()
            }
            .then {
                val lastIndex = js("(element) => element.lastVisibleIndex")(list) as Int
                val item = js(
                    "(element, index) => [...element.children].find((child) => child.textContent === 'value-' + index)",
                )(list, lastIndex) as HTMLElement
                assertEquals("100", item.getAttribute("aria-setsize"))
                assertEquals("100", item.getAttribute("aria-posinset"))
            }
    }

    @Test
    fun accessibleNameGeneratorAddsAriaLabels(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)
        list.itemAccessibleNameGenerator = { item ->
            "Accessible " + (js("(item) => item.value")(item) as String)
        }

        return nextUpdate(list).then {
            assertEquals("Accessible value-0", list.children.item(0)!!.getAttribute("aria-label"))
        }
    }

    @Test
    fun removingTheAccessibleNameGeneratorRemovesAriaLabels(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)
        list.itemAccessibleNameGenerator = { item ->
            "Accessible " + (js("(item) => item.value")(item) as String)
        }

        return nextUpdate(list)
            .then {
                list.itemAccessibleNameGenerator = null
                nextUpdate(list)
            }
            .then { assertNull(list.children.item(0)!!.getAttribute("aria-label")) }
    }

    @Test
    fun overflowIsBottomAtTheBeginning(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list)
            .then { nextResize(list) }
            .then { nextFrame() }
            .then { assertEquals("bottom", list.getAttribute("overflow")) }
    }

    @Test
    fun overflowIsTopBottomInTheMiddle(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list)
            .then {
                list.scrollToIndex(50.0)
                nextFrame()
            }
            .then { assertEquals("top bottom", list.getAttribute("overflow")) }
    }

    @Test
    fun overflowIsTopAtTheEnd(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list)
            .then {
                list.scrollToIndex(100.0)
                nextFrame()
            }
            .then { assertEquals("top", list.getAttribute("overflow")) }
    }

    @Test
    fun scrollPositionIsRestoredWhenMovingWithinTheDom(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list)
            .then {
                list.scrollToIndex(50.0)
                nextFrame().then { js("(element) => element.scrollTop")(list) as Int }
            }
            .then { top ->
                val wrapper = attach(document.createElement("div"))
                wrapper.appendChild(list)
                assertEquals(top, js("(element) => element.scrollTop")(list) as Int)
            }
    }

    @Test
    fun itemsAreHiddenWhenTheyAreRemoved(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list)
            .then {
                list.items = null
                nextUpdate(list)
            }
            .then {
                val children = js("(element) => [...element.children]")(list) as Array<HTMLElement>
                children.forEach { assertTrue(it.hasAttribute("hidden")) }
            }
    }

    @Test
    fun themeConstantIsExposed() {
        assertEquals("theme", VirtualListAttributes.theme)
    }

    @Test
    fun itemAccessibleNameGeneratorConstantIsExposed() {
        assertEquals("item-accessible-name-generator", VirtualListAttributes.itemAccessibleNameGenerator)
    }

    @Test
    fun firstVisibleIndexIsDefined(): Promise<Unit> {
        val list = fixture()
        list.items = items(100)
        renderValue(list)

        return nextUpdate(list).then {
            val index = js("(element) => element.firstVisibleIndex")(list)
            assertFalse(index == null)
        }
    }
}

/** A minimal item shape used by the renderer tests. */
private external interface TestItem {
    var value: String
}
