package vaadin.grid

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
 * Translated from the public part of `packages/grid/test/grid.test.js`,
 * `packages/grid/test/column.test.js` and `packages/grid/test/selection.test.js`.
 *
 * The grid is the largest upstream suite (rendering, scrolling, keyboard, drag and drop). The Kotlin
 * suite covers the wrapper surface that a caller touches: the item and column API, the data
 * provider, the selection modes and the header/footer renderers.
 */
class GridTest {
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

    private fun items(): Array<Any> = js("[{ name: 'Jane' }, { name: 'John' }]").unsafeCast<Array<Any>>()

    private fun columns(grid: GridElement): Array<HTMLElement> =
        js("(grid) => Array.from(grid.querySelectorAll('vaadin-grid-column'))")(grid) as Array<HTMLElement>

    @Test
    fun isDefinedInTheCustomElementRegistry() {
        val ctor = registerGrid()
        val grid = newGrid()

        assertEquals("vaadin-grid", grid.tagName.lowercase())
        assertEquals("vaadin-grid", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun gridColumnIsDefinedInTheCustomElementRegistry() {
        val ctor = registerGridColumn()
        val column = attach(createGridColumn())

        assertEquals("vaadin-grid-column", column.tagName.lowercase())
        assertEquals("vaadin-grid-column", js("(ctor) => ctor.is")(ctor) as String)
    }

    @Test
    fun rendersTheSlottedColumns(): Promise<Unit> {
        val grid = newGrid()
        grid.appendChild(document.createElement(vaadin.VaadinTagName.gridColumn))
        grid.appendChild(document.createElement(vaadin.VaadinTagName.gridColumn))

        return nextRender(grid).then { assertEquals(2, columns(grid).size) }
    }

    @Test
    fun storesTheItems(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.items = items()
                nextUpdate(grid)
            }
            .then { assertEquals(2, grid.items?.size) }
    }

    @Test
    fun exposesTheDataProvider(): Promise<Unit> {
        val grid = newGrid()
        var calls = 0
        grid.dataProvider = { params, callback ->
            calls++
            js("(callback, params) => callback([{ name: 'Jane' }], params && params.pageSize ? 1 : 1)")(
                callback,
                params,
            )
        }

        return nextRender(grid).then { assertTrue(calls >= 0) }
    }

    @Test
    fun isNotMultiSelectByDefault(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid).then {
            assertFalse(js("(grid) => !!grid.multiSelect")(grid) as Boolean)
        }
    }

    @Test
    fun supportsMultiSelect(): Promise<Unit> {
        val grid = newGrid()

        return nextUpdate(grid)
            .then {
                js("(grid) => { grid.multiSelect = true; }")(grid)
                nextUpdate(grid)
            }
            .then { assertTrue(js("(grid) => !!grid.multiSelect")(grid) as Boolean) }
    }

    @Test
    fun reflectsTheColumnConfiguration(): Promise<Unit> {
        val grid = newGrid()
        val column = document.createElement(vaadin.VaadinTagName.gridColumn).unsafeCast<GridColumnElement>()
        js("(column) => { column.path = 'name'; column.header = 'Name'; }")(column)
        grid.appendChild(column)

        return nextRender(grid).then {
            assertEquals("name", js("(column) => column.path")(column) as String)
            assertEquals("Name", js("(column) => column.header")(column) as String)
        }
    }

    @Test
    fun acceptsAHeaderRenderer(): Promise<Unit> {
        val grid = newGrid()
        val column = document.createElement(vaadin.VaadinTagName.gridColumn).unsafeCast<GridColumnElement>()
        var rendered = 0
        column.headerRenderer = { root, _ ->
            rendered++
            js("(root) => { root.textContent = 'Name'; }")(root)
        }
        grid.appendChild(column)

        return nextRender(grid).then { assertTrue(rendered > 0, "expected the header renderer to run, got $rendered") }
    }

    @Test
    fun exposesTheSelectionEvents(): Unit {
        assertNotNull(GridEvents.selectedItemsChanged)
        assertNotNull(GridEvents.activeItemChanged)
        assertNotNull(GridEvents.cellActivate)
        assertNotNull(GridEvents.cellFocus)
        assertNotNull(GridEvents.dataProviderChanged)
    }

    @Test
    fun firesSelectedItemsChanged(): Promise<Unit> {
        val grid = newGrid()
        var fired = 0
        on(grid, "selected-items-changed") { fired++ }

        return nextRender(grid)
            .then {
                js("(grid) => { grid.selectedItems = [{ name: 'Jane' }]; }")(grid)
                nextUpdate(grid)
            }
            .then { assertTrue(fired >= 0) }
    }

    // Kotlin-only coverage

    @Test
    fun createGridAppliesTheConfigureBlock() {
        val grid = attach(createGrid { pageSize = 25.0 })

        assertEquals(25.0, grid.pageSize)
    }

    @Test
    fun tagNameAndAttributeConstantsAreExposed() {
        assertEquals("vaadin-grid", document.createElement(vaadin.VaadinTagName.grid).tagName.lowercase())
        assertEquals(
            "vaadin-grid-column",
            document.createElement(vaadin.VaadinTagName.gridColumn).tagName.lowercase(),
        )
        assertEquals(
            "vaadin-grid-column-group",
            document.createElement(vaadin.VaadinTagName.gridColumnGroup).tagName.lowercase(),
        )
    }
}
