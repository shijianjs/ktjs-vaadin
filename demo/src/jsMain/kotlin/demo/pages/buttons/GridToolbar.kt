package demo.pages.buttons

import demo.ui.jsArray
import demo.ui.listen
import demo.ui.Readout
import demo.ui.SMALL
import io.github.shijianjs.ktjs.demo.page.PAGE_SHEET
import io.github.shijianjs.ktjs.demo.page.Blocks.col
import io.github.shijianjs.ktjs.demo.page.Blocks.row
import io.github.shijianjs.ktjs.demo.style.RESET
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementAdapter
import io.github.shijianjs.ktjs.dom.webcomponent.attachStyledShadow
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import io.github.shijianjs.ktjs.vaadin.button.createButton
import io.github.shijianjs.ktjs.vaadin.grid.GridEvents
import io.github.shijianjs.ktjs.vaadin.grid.createGrid
import io.github.shijianjs.ktjs.vaadin.grid.createGridColumn
import io.github.shijianjs.ktjs.vaadin.grid.createGridSelectionColumn
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import kotlin.js.JsAny
import web.dom.clickEvent
import web.events.addHandler
import web.html.HtmlTagName

private const val GRID_USERS =
    """
    [
      {"firstName": "Ada", "lastName": "Lovelace", "email": "ada@example.com"},
      {"firstName": "Grace", "lastName": "Hopper", "email": "grace@example.com"},
      {"firstName": "Margaret", "lastName": "Hamilton", "email": "margaret@example.com"},
      {"firstName": "Katherine", "lastName": "Johnson", "email": "katherine@example.com"}
    ]
    """

/**
 * `<vaadin-demo-grid-toolbar>`: a grid with a selection column, and the toolbar that depends on it.
 *
 * The element owns the grid, the four actions and the count. Edit-style actions need exactly one
 * selected row, delete needs at least one; the rule lives in one `render`, which the selection event
 * and the two translated suffixes call.
 */
class GridToolbar : CustomElementAdapter() {
    private val heading = HtmlTagName.h3 { className = SMALL }

    private val addUser = createButton()

    private val grid = createGrid()

    private val editProfile = createButton()

    private val managePermissions = createButton()

    private val resetPassword = createButton()

    private val deleteUser = createButton().addTheme("error")

    private val readout = Readout("")

    /** Column headers, keyed by the property the column binds; a header is written through this. */
    private val headers = mutableMapOf<String, (String) -> Unit>()

    /** Heading above the grid. */
    var gridTitle: String = ""
        set(value) {
            field = value
            heading.textContent = value
        }

    /** Label of the button that adds a row. */
    var addLabel: String = ""
        set(value) {
            field = value
            addUser.textContent = value
        }

    /** Label of the first single-row action. */
    var editLabel: String = ""
        set(value) {
            field = value
            editProfile.textContent = value
        }

    /** Label of the second single-row action. */
    var manageLabel: String = ""
        set(value) {
            field = value
            managePermissions.textContent = value
        }

    /** Label of the third single-row action. */
    var resetLabel: String = ""
        set(value) {
            field = value
            resetPassword.textContent = value
        }

    /** Label of the delete action. */
    var deleteLabel: String = ""
        set(value) {
            field = value
            deleteUser.textContent = value
        }

    /** Prefix of the "n rows selected" line. */
    var rowsPrefix: String = ""
        set(value) {
            field = value
            render()
        }

    /** Suffix of the "n rows selected" line. */
    var rowsSuffix: String = ""
        set(value) {
            field = value
            render()
        }

    init {
        grid.dataset["test"] = "button-grid"
        grid.style.setProperty("height", "14rem")
        grid.items = jsArray<JsAny>(GRID_USERS)
        grid.append(createGridSelectionColumn { autoSelect = true })
        for (path in listOf("firstName", "lastName", "email")) {
            val column = createGridColumn { this.path = path }
            headers[path] = { text -> column.header = text }
            grid.append(column)
        }
        grid.listen(GridEvents.selectedItemsChanged) { render() }
        attachStyledShadow(RESET, PAGE_SHEET).append(
            col(
                row(heading, addUser),
                grid,
                row(editProfile, managePermissions, resetPassword, deleteUser),
                readout.root,
            ),
        )
    }

    /** Writes a column header; `path` is the grid property the column binds. */
    fun setHeader(path: String, text: String) {
        headers[path]?.invoke(text)
    }

    private fun render() {
        val size = grid.selectedItems.size
        listOf(editProfile, managePermissions, resetPassword).forEach { it.disabled = size != 1 }
        deleteUser.disabled = size == 0
        readout.set(rowsPrefix + size + rowsSuffix)
    }

    companion object : CustomElementReferenceAdapter<GridToolbar>(GridToolbar::class.js, "vaadin-demo-grid-toolbar")
}
