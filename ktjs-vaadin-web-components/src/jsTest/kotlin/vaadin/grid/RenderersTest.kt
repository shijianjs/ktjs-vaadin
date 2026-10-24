package vaadin.grid

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
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextFrame
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/grid/test/renderers.test.js`.
 *
 * The cells are reached through `grid.$.items/header/footer` and write into the protected
 * `cell._content`, which has no typed equivalent; both are read through `js(...)` in the helpers
 * below. The typed API is used everywhere else (`renderer`, `headerRenderer`, `footerRenderer`,
 * `rowDetailsRenderer`, `detailsOpenedItems`, `hiddenProperty`).
 *
 * Dropped upstream case: `should pass column as owner and this to the renderer` — a Kotlin lambda
 * cannot observe the JS `this` binding, so only the `owner` argument is asserted.
 */
class RenderersTest {
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

    private lateinit var grid: GridElement
    private lateinit var column: GridColumnElement

    private fun newGrid(): Promise<GridElement> {
        grid = attach(createGrid())
        column = document.createElement(vaadin.VaadinTagName.gridColumn).unsafeCast<GridColumnElement>()
        grid.appendChild(column)
        grid.items = js("[{ foo: 'bar' }, { foo: 'baz' }]").unsafeCast<Array<JsAny>>()

        return vaadin.nextRender(grid).then { grid }
    }

    private fun bodyCell(index: Int): HTMLElement =
        js("(grid, index) => grid.${'$'}.items.querySelectorAll('[part~=\"cell\"]')[index]")(grid, index)
            .unsafeCast<HTMLElement>()

    private fun headerCell(index: Int = 0): HTMLElement =
        js("(grid, index) => grid.${'$'}.header.querySelectorAll('[part~=\"cell\"]')[index]")(grid, index)
            .unsafeCast<HTMLElement>()

    private fun footerCell(index: Int = 0): HTMLElement =
        js("(grid, index) => grid.${'$'}.footer.querySelectorAll('[part~=\"cell\"]')[index]")(grid, index)
            .unsafeCast<HTMLElement>()

    private fun contentOf(cell: HTMLElement): HTMLElement =
        js("(cell) => cell._content")(cell).unsafeCast<HTMLElement>()

    private fun innerHtmlOf(element: HTMLElement?): String? =
        js("(element) => (element ? element.innerHTML : null)")(element) as String?

    private fun containerCell(row: Double, col: Double): HTMLElement = js(
        """(grid, row, col) => {
             const rows = Array.from(grid.${'$'}.items.querySelectorAll('[part~="row"]'));
             return rows[row].querySelectorAll('[part~="cell"]')[col];
           }""",
    )(grid, row, col).unsafeCast<HTMLElement>()

    private fun renderIndexAndFoo(root: HTMLElement, model: GridItemModel) {
        val item = js("(item) => item.foo")(model.item) as String
        val text = "${model.index.toInt()} $item"
        js("(root, text) => { root.innerHTML = ''; root.appendChild(document.createTextNode(text)); }")(root, text)
    }

    // region column cells

    private fun gridWithBodyRenderer(): Promise<GridElement> = newGrid().then {
        column.renderer = { root, _, model -> renderIndexAndFoo(root, model) }
        flushGrid(grid)
        grid
    }

    @Test
    fun rendersTheBodyCells(): Promise<Unit> = gridWithBodyRenderer().then {
        assertEquals("0 bar", innerHtmlOf(contentOf(bodyCell(0))))
        assertEquals("1 baz", innerHtmlOf(contentOf(bodyCell(1))))
    }

    @Test
    fun exposesTheModelFromTheCellParent(): Promise<Unit> = gridWithBodyRenderer().then {
        val model = js("(grid, cell) => grid.__getRowModel(cell.parentElement)")(grid, bodyCell(0))
            .unsafeCast<GridItemModel>()
        assertEquals("bar", js("(item) => item.foo")(model.item) as String)
    }

    @Test
    fun passesTheColumnAsTheOwner(): Promise<Unit> = gridWithBodyRenderer().then {
        var ownerName = ""
        column.renderer = { _, owner, _ -> ownerName = owner.tagName.lowercase() }
        flushGrid(grid)

        assertEquals("vaadin-grid-column", ownerName)
    }

    @Test
    fun allowsToChangeTheRenderer(): Promise<Unit> = gridWithBodyRenderer().then {
        column.renderer = { root, _, model -> js("(root, text) => { root.innerHTML = text; }")(root, "${model.index.toInt()} test") }
        flushGrid(grid)

        assertEquals("0 test", innerHtmlOf(contentOf(bodyCell(0))))
        assertEquals("1 test", innerHtmlOf(contentOf(bodyCell(1))))
    }

    @Test
    fun clearsTheContentWhenChangingTheRenderer(): Promise<Unit> = gridWithBodyRenderer().then {
        column.renderer = { _, _, _ -> }
        flushGrid(grid)

        assertEquals("", contentOf(bodyCell(0)).textContent)
        assertEquals("", contentOf(bodyCell(1)).textContent)
    }

    @Test
    fun initializesTheModelInstanceProperties(): Promise<Unit> = newGrid().then {
        var checked = false
        column.renderer = { _, _, model ->
            assertFalse(model.selected == true)
            assertFalse(model.expanded == true)
            assertFalse(model.detailsOpened == true)
            assertEquals(0.0, model.level)
            checked = true
        }
        flushGrid(grid)

        assertTrue(checked, "expected the renderer to run")
    }

    // endregion

    // region row details

    private fun gridWithDetails(): Promise<GridElement> = gridWithBodyRenderer().then {
        val renderer: GridRowDetailsRenderer = { root, _, model -> renderIndexAndFoo(root, model) }
        grid.rowDetailsRenderer = renderer
        flushGrid(grid)
        grid
    }

    @Test
    fun rendersTheRowDetails(): Promise<Unit> = gridWithDetails().then {
        grid.detailsOpenedItems = assertNotNull(grid.items).copyOf()

        assertEquals("0 bar", innerHtmlOf(bodyCellContent(grid, 0.0, 1.0)))
        assertEquals("1 baz", innerHtmlOf(bodyCellContent(grid, 1.0, 1.0)))
    }

    @Test
    fun opensTheDetailsWhenTheRendererIsSet(): Promise<Unit> = gridWithDetails().then {
        grid.detailsOpenedItems = assertNotNull(grid.items).copyOf()

        assertFalse(js("(cell) => cell.hidden")(containerCell(1.0, 1.0)) as Boolean)
    }

    @Test
    fun passesTheArgumentsToTheRowDetailsRenderer(): Promise<Unit> = gridWithDetails().then {
        val detailsCell = bodyCellContent(grid, 0.0, 1.0)
        var calls = 0
        var root: JsAny? = null
        var owner: JsAny? = null
        var model: GridItemModel? = null

        grid.rowDetailsRenderer = { rendered, columnOwner, renderedModel ->
            calls++
            root = rendered
            owner = columnOwner
            model = renderedModel
        }
        grid.detailsOpenedItems = arrayOf(assertNotNull(grid.items)[0])

        assertEquals(1, calls)
        assertEquals(detailsCell, root.unsafeCast<HTMLElement>())
        assertEquals(grid, owner.unsafeCast<GridElement>())
        val itemModel = assertNotNull(model)
        assertEquals(0.0, itemModel.index)
        assertEquals(0.0, itemModel.level)
        assertFalse(itemModel.expanded == true)
        assertFalse(itemModel.selected == true)
        assertFalse(itemModel.hasChildren == true)
        assertTrue(itemModel.detailsOpened == true)
    }

    @Test
    fun allowsToChangeTheRowDetailsRenderer(): Promise<Unit> = gridWithDetails().then {
        grid.detailsOpenedItems = assertNotNull(grid.items).copyOf()
        grid.rowDetailsRenderer = { rendered, _, model ->
            js("(root, text) => { root.innerHTML = text; }")(rendered, "${model.index.toInt()} test")
        }
        flushGrid(grid)

        assertEquals("0 test", innerHtmlOf(bodyCellContent(grid, 0.0, 1.0)))
        assertEquals("1 test", innerHtmlOf(bodyCellContent(grid, 1.0, 1.0)))
    }

    @Test
    fun doesNotInvokeTheBodyRendererWhenAssigningTheDetailsRenderer(): Promise<Unit> = gridWithDetails().then {
        var bodyCalls = 0
        column.renderer = { _, _, _ -> bodyCalls++ }
        flushGrid(grid)
        bodyCalls = 0

        grid.rowDetailsRenderer = { _, _, _ -> }

        assertEquals(0, bodyCalls)
    }

    @Test
    fun invokesTheBodyRendererWhenAssigningTheDetailsRendererWithOpenDetails(): Promise<Unit> =
        gridWithDetails().then {
            grid.detailsOpenedItems = assertNotNull(grid.items).copyOf()

            var bodyCalls = 0
            column.renderer = { _, _, _ -> bodyCalls++ }
            flushGrid(grid)
            bodyCalls = 0

            grid.rowDetailsRenderer = { _, _, _ -> }
            flushGrid(grid)

            assertTrue(bodyCalls > 0, "expected the body renderer to run")
        }

    @Test
    fun invokesTheDetailsRendererOncePerOpenCell(): Promise<Unit> = gridWithDetails().then {
        var calls = 0
        grid.rowDetailsRenderer = { _, _, _ -> calls++ }
        calls = 0

        grid.detailsOpenedItems = arrayOf(assertNotNull(grid.items)[0])

        assertEquals(1, calls)
    }

    @Test
    fun doesNotInvokeTheBodyRenderersWhenAssigningDetailsOpenedItems(): Promise<Unit> = gridWithDetails().then {
        grid.rowDetailsRenderer = null

        var bodyCalls = 0
        column.renderer = { _, _, _ -> bodyCalls++ }
        flushGrid(grid)
        bodyCalls = 0

        grid.detailsOpenedItems = arrayOf(assertNotNull(grid.items)[0])

        assertEquals(0, bodyCalls)
    }

    // endregion

    // region header cell

    private fun gridWithHeaderRenderer(): Promise<GridElement> = newGrid().then {
        column.headerRenderer = { root, _ -> js("(root) => { root.textContent = 'header'; }")(root) }
        flushGrid(grid)
        grid
    }

    @Test
    fun rendersTheHeaderCell(): Promise<Unit> = gridWithHeaderRenderer().then {
        assertEquals("header", contentOf(headerCell()).textContent)
    }

    @Test
    fun keepsTheHeaderVisibleWhenRemovingAColumnWithAHeaderRenderer(): Promise<Unit> =
        gridWithHeaderRenderer().then {
            val newColumn = document.createElement(vaadin.VaadinTagName.gridColumn)
            newColumn.unsafeCast<GridColumnElement>().headerRenderer = { root, _ ->
                js("(root) => { root.textContent = 'bar'; }")(root)
            }
            grid.appendChild(newColumn)
            flushGrid(grid)
            grid.removeChild(newColumn)
            flushGrid(grid)

            val hidden = js("(grid) => grid.${'$'}.header.firstElementChild.hidden")(grid) as Boolean
            assertFalse(hidden)
        }

    @Test
    fun keepsTheHeaderVisibleWhenRemovingAColumnWithAHeader(): Promise<Unit> = newGrid().then {
        column.header = "foo"
        val newColumn = document.createElement(vaadin.VaadinTagName.gridColumn)
        newColumn.unsafeCast<GridColumnElement>().header = "bar"
        grid.appendChild(newColumn)
        flushGrid(grid)
        grid.removeChild(newColumn)
        flushGrid(grid)

        val hidden = js("(grid) => grid.${'$'}.header.firstElementChild.hidden")(grid) as Boolean
        assertFalse(hidden)
    }

    @Test
    fun clearsTheHeaderContentWhenChangingTheRenderer(): Promise<Unit> = gridWithHeaderRenderer().then {
        val cell = headerCell()
        column.headerRenderer = { _, _ -> }
        flushGrid(grid)

        assertEquals("", contentOf(cell).textContent)
    }

    @Test
    fun updatesTheHeaderOfAHiddenColumn(): Promise<Unit> = gridWithHeaderRenderer().then {
        val cell = headerCell()

        column.hiddenProperty = true
        column.headerRenderer = { root, _ -> js("(root) => { root.textContent = 'foo'; }")(root) }

        nextFrame()
            .then {
                column.hiddenProperty = false
                nextFrame()
            }
            .then { assertEquals("foo", contentOf(cell).textContent) }
    }

    @Test
    fun updatesTheHeadersOfHiddenColumns(): Promise<Unit> = gridWithHeaderRenderer().then {
        val cell = headerCell()

        val newColumn = document.createElement(vaadin.VaadinTagName.gridColumn).unsafeCast<GridColumnElement>()
        newColumn.headerRenderer = { root, _ -> js("(root) => { root.textContent = 'header2'; }")(root) }
        grid.appendChild(newColumn)

        column.hiddenProperty = true
        newColumn.hiddenProperty = true

        column.headerRenderer = { root, _ -> js("(root) => { root.textContent = 'foo'; }")(root) }
        newColumn.headerRenderer = { root, _ -> js("(root) => { root.textContent = 'bar'; }")(root) }

        nextFrame()
            .then {
                column.hiddenProperty = false
                newColumn.hiddenProperty = false
                nextFrame()
            }
            .then {
                assertEquals("foo", contentOf(cell).textContent)
                assertEquals("bar", contentOf(headerCell(1)).textContent)
            }
    }

    // endregion

    // region footer cell

    @Test
    fun rendersTheFooterCell(): Promise<Unit> = newGrid().then {
        column.footerRenderer = { root, _ -> js("(root) => { root.textContent = 'footer'; }")(root) }
        flushGrid(grid)

        assertEquals("footer", contentOf(footerCell()).textContent)
    }

    @Test
    fun clearsTheFooterContentWhenChangingTheRenderer(): Promise<Unit> = newGrid().then {
        column.footerRenderer = { root, _ -> js("(root) => { root.textContent = 'footer'; }")(root) }
        flushGrid(grid)
        val cell = footerCell()

        column.footerRenderer = { _, _ -> }
        flushGrid(grid)

        assertEquals("", contentOf(cell).textContent)
    }

    // endregion

    @Test
    fun runsTheRenderersWhenRequestingAContentUpdate(): Promise<Unit> = newGrid().then {
        var bodyCalls = 0
        var headerCalls = 0
        var footerCalls = 0
        var detailsCalls = 0

        column.renderer = { _, _, _ -> bodyCalls++ }
        column.headerRenderer = { _, _ -> headerCalls++ }
        column.footerRenderer = { _, _ -> footerCalls++ }
        grid.rowDetailsRenderer = { _, _, _ -> detailsCalls++ }
        grid.detailsOpenedItems = assertNotNull(grid.items).copyOf()
        flushGrid(grid)

        bodyCalls = 0
        headerCalls = 0
        footerCalls = 0
        detailsCalls = 0
        grid.requestContentUpdate()

        assertTrue(bodyCalls > 0, "expected the body renderer to run")
        assertTrue(headerCalls > 0, "expected the header renderer to run")
        assertTrue(footerCalls > 0, "expected the footer renderer to run")
        assertTrue(detailsCalls > 0, "expected the row details renderer to run")
    }
}
