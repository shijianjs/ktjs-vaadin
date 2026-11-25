package demo.pages.overlays

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.vaadin.themablemixin.addTheme
import demo.pages.fixtures.Person
import demo.pages.fixtures.people
import demo.pages.fixtures.personItems
import io.github.shijianjs.ktjs.vaadin.contextmenu.ContextMenuItemData
import io.github.shijianjs.ktjs.vaadin.grid.GridElement
import io.github.shijianjs.ktjs.vaadin.grid.createGrid
import io.github.shijianjs.ktjs.vaadin.grid.GridColumnElement
import io.github.shijianjs.ktjs.vaadin.grid.createGridColumn
import io.github.shijianjs.ktjs.vaadin.grid.createGridSelectionColumn
import web.events.EventType
import web.html.HTMLElement
import web.keyboard.KeyboardEvent

/**
 * Grid of people with the columns the overlay examples filter and right-click on.
 *
 * The four data columns come back with the grid so the page can bind their headers to its own text
 * table: a header is a property, so it needs a setter rather than the text the page writes once.
 */
internal class PersonGrid(
    val grid: GridElement,
    val firstName: GridColumnElement,
    val lastName: GridColumnElement,
    val email: GridColumnElement,
    val profession: GridColumnElement,
)

/** Creates the grid of the overlay examples, with its four data columns left to the page to label. */
internal fun personGrid(list: List<Person> = people(5), withSelection: Boolean = false): PersonGrid {
    val table = createGrid()
    table.items = personItems(list, withDisplayName = true)
    table.allRowsVisible = true
    if (withSelection) table.append(createGridSelectionColumn())
    val firstName = createGridColumn { path = "firstName" }
    val lastName = createGridColumn { path = "lastName" }
    val email = createGridColumn { path = "email" }
    val profession = createGridColumn { path = "profession" }
    table.append(firstName, lastName, email, profession)
    return PersonGrid(table, firstName, lastName, email, profession)
}

/** Binds the four column headers of a person grid to the page text table. */
internal fun <T : Any> PersonGrid.bindHeaders(
    texts: Binder<T>,
    firstName: (T) -> String,
    lastName: (T) -> String,
    email: (T) -> String,
    profession: (T) -> String,
) {
    texts.bind(this.firstName, firstName) { this.firstName.header = it }
    texts.bind(this.lastName, lastName) { this.lastName.header = it }
    texts.bind(this.email, email) { this.email.header = it }
    texts.bind(this.profession, profession) { this.profession.header = it }
}

internal val KEY_DOWN: EventType<KeyboardEvent> = EventType("keydown")

/**
 * `<vaadin-demo-icon-button>`: the icon-only button the overlay examples put into their toolbars.
 *
 * The host wraps the button because the theme a caller adds (`tertiary` on the tooltip targets) belongs
 * on the button, and [addTheme] hands it there.
 */


internal fun HTMLElement.ariaLabel(name: String) {
    ariaLabel = name
}

/**
 * Upstream types the extra data as `ContextMenuItemData<{ value?: string }>`; Kotlin models the
 * generic intersection by extending the wrapper interface.
 */
internal external interface CopyMenuItem : ContextMenuItemData {
    var value: String?
}

internal fun pad2(value: Int): String = if (value < 10) "0$value" else value.toString()
