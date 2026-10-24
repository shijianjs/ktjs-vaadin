package vaadin.grid

import kotlin.js.Promise
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import vaadin.attach
import vaadin.detachAll
import vaadin.nextFrame
import vaadin.nextRender
import web.dom.document
import web.html.HTMLElement

/**
 * Translated from `packages/grid/test/disabled.test.js`.
 *
 * Dropped upstream cases: the two Tab / Shift+Tab cases of the `focus` group, which need a real key
 * sequence (`sendKeys`) between two input elements.
 */
class DisabledTest {
    private val attached = mutableListOf<HTMLElement>()

    private fun <T : HTMLElement> attach(element: T): T = vaadin.attach(element).also { attached += it }

    @BeforeTest
    fun register() {
        registerGrid()
        registerGridColumn()
        registerGridFilterColumn()
        registerGridSortColumn()
    }

    @AfterTest
    fun cleanup() {
        detachAll(attached)
        attached.clear()
    }

    private fun newGrid(): GridElement {
        val grid = attach(createGrid())
        grid.appendChild(document.createElement(vaadin.VaadinTagName.gridFilterColumn))
        grid.appendChild(document.createElement(vaadin.VaadinTagName.gridSortColumn))
        return grid
    }

    @Test
    fun isNotDisabledByDefault() {
        assertFalse(newGrid().disabled)
    }

    @Test
    fun reflectsTheDisabledProperty(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.disabled = true
                nextFrame()
            }
            .then {
                assertTrue(grid.hasAttribute("disabled"))

                grid.disabled = false
                nextFrame()
            }
            .then { assertFalse(grid.hasAttribute("disabled")) }
    }

    @Test
    fun setsTabindexToMinusOneWhenDisabled(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.disabled = true
                nextFrame()
            }
            .then { assertEquals("-1", grid.getAttribute("tabindex")) }
    }

    @Test
    fun removesTabindexWhenReEnabled(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.disabled = true
                nextFrame()
            }
            .then {
                grid.disabled = false
                nextFrame()
            }
            .then { assertFalse(grid.hasAttribute("tabindex")) }
    }

    @Test
    fun disablesPointerEvents(): Promise<Unit> {
        val grid = newGrid()

        return nextRender(grid)
            .then {
                grid.disabled = true
                nextFrame()
            }
            .then {
                assertEquals(
                    "none",
                    vaadin.computedStyle(grid, "pointerEvents"),
                )
            }
    }
}
