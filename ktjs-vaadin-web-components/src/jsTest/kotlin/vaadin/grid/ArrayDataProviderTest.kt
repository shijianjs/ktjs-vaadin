package vaadin.grid

import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextRender
import vaadin.nextUpdate
import web.html.HTMLElement

/**
 * Translated from `packages/grid/test/array-data-provider.test.js`.
 *
 * The upstream cases render the body rows into `grid.$.items` and read the cell content; the
 * assertions below stay on the public `items` / `size` / `dataProvider` state, which is what those
 * cases are ultimately about.
 *
 * Dropped upstream cases: `should set array data provider` (it compares `dataProvider` to the
 * protected `_arrayDataProvider`) and the `invalid paths` group, which asserts on the rendered
 * header content of a column with a path that does not exist.
 */
class ArrayDataProviderTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerGrid()
        registerGridColumn()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newGrid(): GridElement = attach(createGrid())

    private fun nestedItems(): Array<JsAny> = js(
        "[{ name: { first: 'foo', last: 'bar' } }, { name: { first: 'baz', last: 'qux' } }]",
    ).unsafeCast<Array<JsAny>>()

    @Test
    fun keepsTheItemArray(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.items = nestedItems()
                nextUpdate(grid)
            }
            .then {
                assertEquals(2, grid.items?.size)
                assertEquals(2.0, grid.size)
            }
    }

    @Test
    fun handlesNull(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.items = nestedItems()
                nextUpdate(grid)
            }
            .then {
                grid.items = null
                nextUpdate(grid)
            }
            .then {
                assertEquals(0.0, grid.size)
                assertNull(grid.items)
            }
    }

    @Test
    fun unsetsTheDataProvider(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.items = nestedItems()
                nextUpdate(grid)
            }
            .then {
                grid.items = null
                nextUpdate(grid)
            }
            .then { assertTrue(js("(grid) => grid.dataProvider === undefined")(grid) as Boolean) }
    }

    @Test
    fun overridesACustomDataProvider(): Promise<Unit> {
        val grid = newGrid()
        val provider: GridDataProvider = { _, _ -> }

        return nextRender(grid)
            .then {
                grid.dataProvider = provider
                grid.items = js("[1, 2, 3]").unsafeCast<Array<JsAny>>()
                nextUpdate(grid)
            }
            .then {
                assertTrue(js("(grid, provider) => grid.dataProvider !== provider")(grid, provider) as Boolean)
            }
    }

    @Test
    fun acceptsANewArrayOfTheSameLength(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.items = nestedItems()
                nextUpdate(grid)
            }
            .then {
                grid.items = js("[{ name: { first: 'a' } }, { name: { first: 'b' } }]").unsafeCast<Array<JsAny>>()
                nextUpdate(grid)
            }
            .then {
                val items = grid.items
                assertEquals(2, items?.size)
                assertEquals("a", js("(item) => item.name.first")(items!![0]) as String)
            }
    }

    @Test
    fun worksAfterBeingInitializedWithEmptyItems(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.items = emptyArray<JsAny>()
                grid.items = js("[0]").unsafeCast<Array<JsAny>>()
                nextUpdate(grid)
            }
            .then { assertEquals(1.0, grid.size) }
    }
}
